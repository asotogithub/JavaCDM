package trueffect.truconnect.api.crud.dao.impl;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trueffect.truconnect.api.crud.EntityFactory.createCreativeGroupTarget;

import trueffect.truconnect.api.commons.model.CreativeGroupTarget;
import trueffect.truconnect.api.commons.model.GeoTarget;
import trueffect.truconnect.api.commons.model.enums.LocationType;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreativeGroupDaoImplTest {

    private PersistenceContext persistenceContextMock;
    private AccessControl accessControlMock;
    private SqlSession sqlSessionMock;
    private CreativeGroupDaoImpl creativeGroupDao;

    @Before
    public void init() {
        persistenceContextMock = mock(PersistenceContext.class);
        accessControlMock = mock(AccessControl.class);
        sqlSessionMock = mock(SqlSession.class);
        creativeGroupDao = new CreativeGroupDaoImpl(persistenceContextMock);
    }

    @Test
    public void buildLocationMapFromEmptyArray() {
        Map<LocationType, GeoTarget> locationMap = creativeGroupDao.locationMap(new ArrayList<CreativeGroupTarget>(0));
        assertThat(locationMap.isEmpty(), is(true));
    }

    @Test
    public void buildLocationMapFromNullArray() {
        Map<LocationType, GeoTarget> locationMap = creativeGroupDao.locationMap(null);
        assertThat(locationMap.isEmpty(), is(true));
    }

    @Test
    public void buildLocationMapFromPopulatedArray() {
        List<CreativeGroupTarget> creativeGroupTargets = new ArrayList<>(1);
        CreativeGroupTarget creativeGroupTarget1 = createCreativeGroupTarget();
        creativeGroupTarget1.setTypeCode(LocationType.COUNTRY.getCode());
        creativeGroupTargets.add(creativeGroupTarget1);
        CreativeGroupTarget creativeGroupTarget2 = createCreativeGroupTarget();
        creativeGroupTarget2.setTypeCode(LocationType.STATE.getCode());
        creativeGroupTargets.add(creativeGroupTarget2);
        CreativeGroupTarget creativeGroupTarget3 = createCreativeGroupTarget();
        creativeGroupTarget3.setTypeCode(LocationType.STATE.getCode());
        creativeGroupTargets.add(creativeGroupTarget3);
        Map<LocationType, GeoTarget> locationMap = creativeGroupDao.locationMap(creativeGroupTargets);
        assertThat(locationMap.keySet(), hasSize(2));
        assertThat(locationMap.get(LocationType.STATE).getTargets(), hasSize(2));
    }

    @Test
    public void getGeoTargetsWithEmptyArray() throws Exception {
        when(persistenceContextMock.selectList(eq("CreativeGroup.getCreativeGroupTargets"), anyLong(), eq(sqlSessionMock))).thenAnswer(new Answer<List<CreativeGroupTarget>>() {
            @Override
            public List<CreativeGroupTarget> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new ArrayList<>(0);
            }
        });
        List<GeoTarget> geoTargets = creativeGroupDao.getGeoTargets(123L, sqlSessionMock);
        assertThat(geoTargets, is(empty()));
    }

    @Test
    public void getGeoTargetsWithNonEmptyArray() throws Exception {
        when(persistenceContextMock.selectList(eq("CreativeGroup.getCreativeGroupTargets"), anyLong(), eq(sqlSessionMock))).thenAnswer(new Answer<List<CreativeGroupTarget>>() {
            @Override
            public List<CreativeGroupTarget> answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<CreativeGroupTarget> creativeGroupTargets = new ArrayList<>(1);
                CreativeGroupTarget creativeGroupTarget1 = createCreativeGroupTarget();
                creativeGroupTarget1.setTypeCode(LocationType.COUNTRY.getCode());
                creativeGroupTargets.add(creativeGroupTarget1);
                CreativeGroupTarget creativeGroupTarget2 = createCreativeGroupTarget();
                creativeGroupTarget2.setTypeCode(LocationType.STATE.getCode());
                creativeGroupTargets.add(creativeGroupTarget2);
                CreativeGroupTarget creativeGroupTarget3 = createCreativeGroupTarget();
                creativeGroupTarget3.setTypeCode(LocationType.STATE.getCode());
                creativeGroupTargets.add(creativeGroupTarget3);
                return creativeGroupTargets;
            }
        });
        List<GeoTarget> geoTargets = creativeGroupDao.getGeoTargets(123L, sqlSessionMock);
        assertThat(geoTargets, hasSize(2));
    }
}

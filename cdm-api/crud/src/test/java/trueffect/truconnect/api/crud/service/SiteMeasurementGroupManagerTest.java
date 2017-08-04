package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;
import static trueffect.truconnect.api.crud.EntityFactory.faker;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessControlMock;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementGroupDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementEventDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementGroupDaoImpl;
import trueffect.truconnect.api.crud.dao.mock.SiteMeasurementGroupContextMock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;

public class SiteMeasurementGroupManagerTest extends AbstractManagerTest{

    private SiteMeasurementManager siteMeasurementManager;
    private SiteMeasurementGroupManager groupManager;
    private User user;

    //DAOs
    private SiteMeasurementEventDao siteMeasurementEventDao;
    private SiteMeasurementDao siteMeasurementDao;
    private SiteMeasurementGroupDao siteMeasurementGroupDao;
    private UserDao userDao;

    @Before
    public void setUp() throws Exception {
        // Mock daos
        accessControlMockito = mock(AccessControl.class);
        siteMeasurementEventDao = mock(SiteMeasurementEventDao.class);
        siteMeasurementDao = mock (SiteMeasurementDao.class);
        siteMeasurementGroupDao = mock (SiteMeasurementGroupDao.class);
        userDao = mock(UserDao.class);

        // Variables
        user = EntityFactory.createUser();
        user.setUserName(key.getUserId());

        when(siteMeasurementGroupDao.openSession()).thenReturn(sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.SITE_MEASUREMENT, sqlSessionMock);

        groupManager = new SiteMeasurementGroupManager(siteMeasurementGroupDao, userDao, siteMeasurementEventDao,
                siteMeasurementDao, accessControlMockito);
        siteMeasurementManager = new SiteMeasurementManager(siteMeasurementDao, userDao, accessControlMockito);
    }

    @Test
    public void testCreateSmGroup() throws Exception {

        SmGroup expectedSmGroup;
        Long smId = EntityFactory.random.nextLong();
        expectedSmGroup = EntityFactory.createSmGroup();
        Long id = EntityFactory.random.nextLong();
        expectedSmGroup.setMeasurementId(smId);
        doNothing().when(siteMeasurementGroupDao).create(Matchers.any(SmGroup.class), eq(sqlSessionMock));
        when(siteMeasurementGroupDao.getNextId(eq(sqlSessionMock))).thenReturn(id);
        when(siteMeasurementGroupDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(expectedSmGroup);
        Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmGroup> actualSmGroup =
                groupManager.createSmGroup(expectedSmGroup, key);
        assertNotNull(actualSmGroup);
        assertThat(actualSmGroup.success(), is(expectedSmGroup));
    }



    @Test
    public void testIsSmGroupNameDoesNotExist() throws Exception {
        when(siteMeasurementGroupDao.isSmGroupNametExist(anyLong(), Matchers.any(String.class), eq(sqlSessionMock))).
                thenReturn(false);
        String name = faker.letterify("?????");
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> result =
                groupManager.isSmGroupNameExist(0L, name, key);
        assertThat(result.success(), is(false));
    }

    @Test
    public void testIsSmGroupNameAlreadyExist() throws Exception {
        when(siteMeasurementGroupDao.isSmGroupNametExist(anyLong(), Matchers.any(String.class), eq(sqlSessionMock))).
                thenReturn(true);
        String name = faker.letterify("?????");
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> result =
                groupManager.isSmGroupNameExist(0L, name, key);
        assertThat(result.success(), is(true));
    }

    @Test
    public void testIsSmGroupNameDoesNotExistInvalidSmId() throws Exception {
        when(siteMeasurementGroupDao.isSmGroupNametExist(anyLong(), Matchers.any(String.class), eq(sqlSessionMock))).
                thenReturn(false);
        String name = faker.letterify("?????");
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> result =
                groupManager.isSmGroupNameExist(null, name, key);
        assertThat(result.isError(), is(true));
        assertThat(result.error().getErrors().get(0).getMessage(), is("Invalid Sm Id, it cannot be empty."));
    }

    @Test
    public void getSMGroups ()  throws Exception {
        RecordSet<SmGroup> records = new RecordSet<SmGroup>();
        List<SmGroup> list = new ArrayList<>();
        Long smId = 7426250L;
        for (int i = 0; i < 5; i++) {
            SmGroup grp = EntityFactory.createSmGroup();
            grp.setMeasurementId(smId);
            list.add(grp);
        }
        records.setStartIndex(0);
        records.setPageSize(1);
        records.setTotalNumberOfRecords(5);
        records.setRecords(list);

        when(siteMeasurementGroupDao.getSmGroupsBySiteMeasurement(anyLong(), eq(sqlSessionMock))).thenReturn(records);
        Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<SmGroup>> grpResultSet =
                groupManager.getSiteMeasurementGroups(smId, key);
        assertNotNull(grpResultSet);
        assertNotNull(String.valueOf(grpResultSet.success().getRecords().size()), is(5));
    }

}
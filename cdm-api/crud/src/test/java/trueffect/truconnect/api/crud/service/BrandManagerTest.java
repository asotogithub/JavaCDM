package trueffect.truconnect.api.crud.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.BrandDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BrandManagerTest {

    private static final int NUMBER_OF_BRANDS = 5;
    private AccessControl accessControlMockito;
    private OauthKey key;
    private BrandManager manager;
    private BrandDao dao;
    private Long advertiserId;

    @Before
    public void setUp() throws Exception {
        accessControlMockito = mock(AccessControl.class);
        advertiserId = EntityFactory.random.nextLong();
        dao = mock(BrandDao.class);
        key = new OauthKey(EncryptUtil.encryptAES("dummy@user"), "00000");
        manager = new BrandManager(dao, accessControlMockito);
        List<Brand> list = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_BRANDS; i++){
            list.add(EntityFactory.createBrand());
        }

        SqlSession sqlSessionMock = mock(SqlSession.class);
        doReturn(sqlSessionMock).when(dao).openSession();
        RecordSet<Brand> recordSet = new RecordSet<>(0, 0, list.size(), list);
        doReturn(recordSet).when(dao).getBrandsByAdvertiserId(eq(advertiserId),
                                                              eq(key.getUserId()),
                                                              eq(sqlSessionMock));
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISERS), 
                anyList(), anyString(), any(SqlSession.class))).thenReturn(true);
    }

    @Test
    public void testSuccessfullyGetBrandsByAdvertiserId() throws Exception {
        RecordSet<Brand> actual = manager.getBrandsByAdvertiserId(advertiserId, key);
        assertNotNull(actual);
        assertNotNull(actual.getRecords());
        assertEquals(actual.getRecords().size(), NUMBER_OF_BRANDS);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedGetBrandsByAdvertiserIdDueToNullAdvertiserIdArgument(){
        manager.getBrandsByAdvertiserId(null, key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedGetBrandsByAdvertiserIdDueToNullKeyArgument(){
        manager.getBrandsByAdvertiserId(EntityFactory.random.nextLong(), null);
    }
}
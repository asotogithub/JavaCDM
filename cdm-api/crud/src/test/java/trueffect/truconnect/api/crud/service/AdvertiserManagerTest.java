package trueffect.truconnect.api.crud.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public class AdvertiserManagerTest extends AbstractManagerTest {

    private AdvertiserManager manager;
    private AdvertiserDao advertiserDao;
    private UserDao userDao;
    private Advertiser advertiser;
    private User user;
    private Long agencyId = 1L;

    @Before
    public void setUp() throws Exception {
        AccessControl accessControlMockito = mock(AccessControl.class);
        userDao = mock(UserDao.class);
        advertiserDao = mock(AdvertiserDao.class);
        manager = new AdvertiserManager(advertiserDao, userDao, accessControlMockito);
        sqlSessionMock = mock(SqlSession.class);
        when(advertiserDao.openSession()).thenReturn(sqlSessionMock);

        user = EntityFactory.createUser();
        user.setAgencyId(agencyId);
        user.setUserName(key.getUserId());
        when(userDao.get(anyString(), eq(sqlSessionMock))).thenReturn(user);
        sqlSessionMock = mock(SqlSession.class);

        key = new OauthKey(EncryptUtil.encryptAES("dummy@user"), "00000");
        manager = new AdvertiserManager(advertiserDao, userDao, this.accessControlMockito);
        when(advertiserDao.openSession()).thenReturn(sqlSessionMock);
        advertiser = EntityFactory.createAdvertiser();
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(advertiser);
        when(this.accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
    }

    @Test
    public void testSuccessfulCreate() throws Exception {
        when(advertiserDao.advertiserNameExists(any(Advertiser.class), eq(sqlSessionMock))).thenReturn(false);
        advertiser.setAgencyId(agencyId);
        advertiser.setId(null);
        Advertiser result = manager.create(advertiser, key);
        assertNotNull(result);
    }

    @Test(expected = ValidationException.class)
    public void testArgumentsExceptionsOnCreate() throws Exception {
        advertiser = new Advertiser();
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(advertiser);
        Advertiser result = manager.create(advertiser, key);
    }

    @Test
    public void testGetAdvertisersByUser() throws Exception {
        when(userDao.get(anyString(), eq(sqlSessionMock))).thenReturn(user);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
        RecordSet<Advertiser> result = new RecordSet<>();
        List<Advertiser> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Advertiser adv = EntityFactory.createAdvertiser();
            adv.setAgencyId(agencyId);
            list.add(adv);
        }
        result.setRecords(list);
        when(advertiserDao.getAdvertisersByUserId(anyString(), eq(sqlSessionMock))).thenReturn(result);
        RecordSet<Advertiser> advertisers = manager.getAdvertisersByUserId(key.getUserId(), key);
        assertNotNull(advertisers);
    }
}

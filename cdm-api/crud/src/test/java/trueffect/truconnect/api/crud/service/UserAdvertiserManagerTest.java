package trueffect.truconnect.api.crud.service;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.UserAdvertiser;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.UserAdvertiserDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserAdvertiserManagerTest{

    private AccessControl accessControl;
    private UserAdvertiserManager manager;
    private OauthKey key;
    private UserAdvertiserDao dao;
    private UserDao userDao;
    private User user;

    @Before
    public void setUp() throws Exception {
        accessControl = mock(AccessControl.class);
        dao = mock(UserAdvertiserDao.class);
        key = new OauthKey(EncryptUtil.encryptAES("dummy@user"), "00000");
        userDao = mock(UserDao.class);
        user = EntityFactory.createUser();
        manager = new UserAdvertiserManager(dao, accessControl);
        SqlSession session = mock(SqlSession.class);
        doReturn(session).when(dao).openSession();
        doReturn(user).when(userDao).get(eq(user.getUserName()),
                                         eq(key.getUserId()),
                                         eq(session));
        when(accessControl.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), anyString(), eq(session))).thenReturn(true);
        when(accessControl.isAdmin(anyString(), eq(session))).thenReturn(false);
    }

    @Test
    public void testUpdateAdvertiserRef() throws Exception {
        List<UserAdvertiser> userAdvertisers = new ArrayList<>();
        UserAdvertiser ua1 = new UserAdvertiser();
        ua1.setAdvertiserId(EntityFactory.random.nextLong());
        String userId = user.getUserName();
        ua1.setUserId(userId);
        userAdvertisers.add(ua1);
        UserAdvertiser ua2 = new UserAdvertiser();
        ua2.setAdvertiserId(EntityFactory.random.nextLong());
        ua2.setUserId(userId);
        userAdvertisers.add(ua2);
        RecordSet<UserAdvertiser>
            recordSet = new RecordSet<>(0, 0, userAdvertisers.size(), userAdvertisers);
        Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<UserAdvertiser>> actual = manager.updateUserAdvertisers(
            userId,
            recordSet,
            key);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.success(), is(notNullValue()));
        assertThat(actual.success().getRecords(), is(notNullValue()));
        assertThat(actual.success().getRecords().size(), is(2));
    }
}
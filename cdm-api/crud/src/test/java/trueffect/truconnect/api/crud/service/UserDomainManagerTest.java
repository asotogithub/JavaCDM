package trueffect.truconnect.api.crud.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.UserDomain;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.UserDomainDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNull;

public class UserDomainManagerTest extends AbstractManagerTest {

    private UserDomainManager manager;
    private UserDomainDao userDomainDao;
    private UserDao userDao;
    private User user;

    @Before
    public void setUp() throws Exception {
        userDomainDao = mock(UserDomainDao.class);
        SqlSession session = mock(SqlSession.class);
        doReturn(session).when(userDomainDao).openSession();
        userDao = mock(UserDao.class);
        user = EntityFactory.createUser();
        doReturn(user).when(userDao).get(eq(user.getUserName()),
                                         eq(key.getUserId()),
                                         eq(session));
        when(accessControlMockito.isAdmin(anyString(), eq(session))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.COOKIE_DOMAIN), 
                anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(userDao.get(anyString(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(user);
        manager = new UserDomainManager(userDomainDao, accessControlMockito);
    }

    @Test
    public void testSuccessfulUpdate() throws Exception {
        RecordSet<UserDomain> recordSet = new RecordSet<>(0, 0, 10, EntityFactory.createUserDomains(key.getUserId()));
        Either<Error, RecordSet<UserDomain>> result = manager.updateUserDomains(userId, recordSet, key);
        RecordSet<UserDomain> actual = result.success();
        assertNotNull(actual);
        assertNull(result.error());
    }
}
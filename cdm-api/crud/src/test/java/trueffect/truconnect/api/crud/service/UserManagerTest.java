package trueffect.truconnect.api.crud.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static trueffect.truconnect.api.crud.EntityFactory.createTraffickingUsers;
import static trueffect.truconnect.api.crud.EntityFactory.createUser;

import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.CookieDomainDTO;
import trueffect.truconnect.api.commons.model.dto.UserView;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.CookieDomainDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rambert Rioja
 */
public class UserManagerTest {

    private AccessControl accessControlMock;
    private PersistenceContext persistenceContextMock;
    private CookieDomainDaoImpl cookieDomainDaoMock;
    private UserDaoImpl userDaoMock;
    private final String userId = "email.test22@contact.com";
    private final String Yes = "Y";
    private final String No = "N";
    private Long agencyId = new Long(1061024);
    private Long contactId = new Long(13749);
    private final String pwd = "123";
    private final String createdTpwsKey = "000000";
    private final String pwdEncry = "PJkJr+wlNU1VHa4hWQuybjjVPyFzuNP"
            + "cPu5MBH56scHri4UQPjvnumE7MbtcnDYhTcnxSkL9ei/bhIVrylxEwg==";
    private UserManager usersService;
    private CookieDomainManager domainManager;
    private final UnitTestCleanerManager cleaner = new UnitTestCleanerManager();
    private UserDao userDaoMockTrafficking;
    private SqlSession sqlSessionMock;
    private UserManager usersManager;
    private OauthKey key;

    @Before
    public void init() throws Exception {
        accessControlMock = mock(AccessControl.class);
        persistenceContextMock = mock(PersistenceContext.class);
        cookieDomainDaoMock = mock(CookieDomainDaoImpl.class);
        usersService = new UserManager(new UserDaoImpl(persistenceContextMock, accessControlMock), accessControlMock);
        domainManager = mock(CookieDomainManager.class);
        sqlSessionMock = mock(SqlSession.class);
        userDaoMockTrafficking = mock(UserDao.class);
        usersManager = new UserManager(userDaoMockTrafficking, accessControlMock);
        String userId = "foo@bar.com";
        String tpws = "00000";
        key = new OauthKey(EncryptUtil.encryptAES(userId), tpws);
        when(accessControlMock.isAdmin(anyString(), any(SqlSession.class))).thenReturn(true);
        MockAccessControlUtil.allowAccessForUser(accessControlMock, AccessStatement.AGENCY, sqlSessionMock);
    }

    @Test
    public void testGettingDomains() throws Exception {
        String userId = "foo@bar.com";
        String tpws = "00000";
        int expectedLength = 3;
        OauthKey key = new OauthKey(EncryptUtil.encryptAES(userId), tpws);
        when(accessControlMock.isAdmin(eq(userId), any(SqlSession.class))).thenReturn(true);
        when(domainManager.getDomains(eq(userId), eq(key))).thenAnswer(
                listOfDomains(expectedLength));
        RecordSet<CookieDomainDTO> domains = domainManager.getDomains(userId, key);
        assertThat(domains.getPageSize(), is(equalTo(expectedLength)));
    }

    @Test
    public void testGetTraffickingUsers() throws Exception {
        List<UserView> list = createTraffickingUsers();
        when(userDaoMockTrafficking.openSession()).thenReturn(sqlSessionMock);
        when(userDaoMockTrafficking.getUserView(anyLong(), any(SqlSession.class))).thenReturn(list);
        RecordSet<UserView> result = usersManager.getUserView(agencyId, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getRecords().size(), is(10));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetTraffickingUsersFailNullAgencyId() throws Exception {
        List<UserView> list = createTraffickingUsers();
        when(userDaoMockTrafficking.openSession()).thenReturn(sqlSessionMock);
        when(userDaoMockTrafficking.getUserView(anyLong(), any(SqlSession.class))).thenReturn(list);
        RecordSet<UserView> result = usersManager.getUserView(null, key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTraffickingUsersFailNullKey() throws Exception {
        List<UserView> list = createTraffickingUsers();
        when(userDaoMockTrafficking.openSession()).thenReturn(sqlSessionMock);
        when(userDaoMockTrafficking.getUserView(anyLong(), any(SqlSession.class))).thenReturn(list);
        RecordSet<UserView> result = usersManager.getUserView(agencyId, null);
    }

    @Test
    public void testSetLimitDomain() {
        when(userDaoMockTrafficking.openSession()).thenReturn(sqlSessionMock);
        User user = createUser();
        when(userDaoMockTrafficking.get(anyString(), any(SqlSession.class))).thenReturn(user);
        doNothing().when(userDaoMockTrafficking).setUserLimits(any(User.class), any(OauthKey.class), any(SqlSession.class));
        Either<Error, Boolean> result = usersManager.setUserLimits(user.getUserName(), user, key);

        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }
    
    @Test
    public void testSetLimitDomainFail() {
        User user = createUser();
        when(accessControlMock.isAdmin(anyString(), any(SqlSession.class))).thenReturn(false);
        Either<Error, Boolean> result = usersManager.setUserLimits(user.getUserName(), user, key);

        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
    }

    @Test
    public void testSetLimitDomainFailWrongLimitDomainsValue() {
        User user = createUser();
        user.setLimitDomains("A");
        user.setLimitAdvertisers(null);
        Either<Error, Boolean> result = usersManager.setUserLimits(user.getUserName(), user, key);

        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
    }

    @Test
    public void testSetLimitAdvertiserFailWrongLimitAdvertisersValue() {
        User user = createUser();
        user.setLimitDomains(null);
        user.setLimitAdvertisers("A");
        Either<Error, Boolean> result = usersManager.setUserLimits(user.getUserName(), user, key);

        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetLimitDomainFailNullId() {
        User user = createUser();
        usersManager.setUserLimits(null, user, key);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetLimitDomainFailNullUser() {
        User user = createUser();
        usersManager.setUserLimits(user.getUserName(), null, key);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetLimitDomainFailNullKey() {
        User user = createUser();
        usersManager.setUserLimits(user.getUserName(), user, null);
    }
    
    private static Answer<RecordSet<CookieDomainDTO>> listOfDomains(final int num) {
        return new Answer<RecordSet<CookieDomainDTO>>() {
            @Override
            public RecordSet<CookieDomainDTO> answer(InvocationOnMock invocationOnMock) throws Throwable {
                RecordSet<CookieDomainDTO> result = new RecordSet<>();
                result.setStartIndex(0);
                result.setPageSize(num);
                List<CookieDomainDTO> expected = new ArrayList<>();
                for(int i = 0; i < num; i++) {
                    expected.add(new CookieDomainDTO());
                }
                result.setRecords(expected);
                return result;
            }
        };
    }
}
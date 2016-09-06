package trueffect.truconnect.api.crud.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CookieDomainDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.mock.CookieDomainContextMock;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;

public class CookieDomainManagerTest {

    private AccessControl accessControlMockito;
    private CookieDomainManager manager;
    private CookieDomainDao cdDao;
    private OauthKey key;
    private UserDao userDao;
    private SqlSession mockSession;
    private CookieDomain cookieDomain = null;

    @Before
    public void setUp() throws Exception {
        CookieDomainContextMock persistenceContext = new CookieDomainContextMock();
        cdDao = mock(CookieDomainDao.class);
        accessControlMockito = mock(AccessControl.class);
        key = new OauthKey(EncryptUtil.encryptAES("dummy@user"), "00000");
        manager = new CookieDomainManager(cdDao, userDao, accessControlMockito);
        userDao = mock(UserDao.class);
        cookieDomain = EntityFactory.createCookieDomain();
        cookieDomain.setAgencyId(EntityFactory.random.nextLong());
        mockSession = mock(SqlSession.class);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), anyString(), eq(mockSession))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS), anyListOf(Long.class), anyString(), eq(mockSession))).thenReturn(true);
        when(cdDao.openSession()).thenReturn(mockSession);

    }

    @Test
    public void testSuccessfulCreate() throws Exception {
        when(cdDao.existCookieDomain(
                any(CookieDomain.class),
                eq(mockSession))).thenReturn(false);
        when(cdDao.getNextId(
                eq(mockSession))).thenReturn(EntityFactory.random.nextLong());
        when(cdDao.get(
                anyLong(),
                eq(mockSession))).thenReturn(EntityFactory.createCookieDomain());
        CookieDomain actual = manager.create(cookieDomain, key);
        assertNotNull(actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArgumentsExceptionsOnCreate() throws Exception {
        cookieDomain = null;
        manager.create(cookieDomain, key);
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void testCreateWithWrongAccess() throws Exception {
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), anyString(), eq(mockSession))).thenReturn(false);
        manager.create(cookieDomain, key);
    }

    @Test(expected = ValidationException.class)
    public void testDuplicateNameOnCreate() throws Exception {
        when(cdDao.existCookieDomain(
                any(CookieDomain.class),
                eq(mockSession))).thenReturn(true);
        manager.create(cookieDomain, key);
    }

    @Test
    public void testGetAfterCreate() throws Exception {
        when(cdDao.existCookieDomain(
                any(CookieDomain.class),
                eq(mockSession))).thenReturn(false);
        when(cdDao.getNextId(
                eq(mockSession))).thenReturn(EntityFactory.random.nextLong());
        when(cdDao.get(
                anyLong(),
                eq(mockSession))).thenReturn(EntityFactory.createCookieDomain());
        CookieDomain actual1 = manager.create(cookieDomain, key);
        CookieDomain actual2 = manager.get(actual1.getId(), key);
        assertNotNull(actual2);
    }

    @Test(expected = NotFoundException.class)
    public void testDelete() throws Exception {

        CookieDomain cd = manager.create(cookieDomain, key);
        SuccessResponse response = manager.delete(cd.getId(), key);
        assertNotNull(response);
        assertEquals("CookieDomain " + cd.getId() + " successfully deleted", response.getMessage());
        manager.get(cd.getId(), key);
    }
}

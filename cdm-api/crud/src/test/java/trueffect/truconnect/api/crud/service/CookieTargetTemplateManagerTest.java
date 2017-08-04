package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.CookieTargetTemplate;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CookieTargetTemplateDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rambert Rioja
 */
public class CookieTargetTemplateManagerTest {
    
    private AccessControl accessControlMockito;
    private OauthKey key;
    private SqlSession mockSession;
    private CookieTargetTemplate cookieTarget;
    private List<CookieTargetTemplate> cookieTargetList;
    private Long cookieDomainId;
    
    private CookieTargetTemplateDao cookieTargetTemplateDao;
    private CookieTargetTemplateManager cookieTargetTemplateManager;

    @Before
    public void setUp() throws Exception {
        // Mocks
        accessControlMockito = mock(AccessControl.class);
        mockSession = mock(SqlSession.class);
        cookieTargetTemplateDao = mock(CookieTargetTemplateDao.class);
        
        //variables
        key = new OauthKey(EncryptUtil.encryptAES("foo@bar.com"), "0000");
        cookieTarget = EntityFactory.createCookieTargetTemplate();
        cookieDomainId = cookieTarget.getCookieDomainId();
        cookieTargetList = createCookieTargetsList(cookieDomainId, 10);

        //manager
        cookieTargetTemplateManager = new CookieTargetTemplateManager(cookieTargetTemplateDao, accessControlMockito);
        
        // Mock behaviors
        when(cookieTargetTemplateDao.openSession()).thenReturn(mockSession);
        when(accessControlMockito.isUserValidFor(
            eq(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS),
            anyList(),
            eq(key.getUserId()),
            eq(mockSession))).thenReturn(true);
        when(cookieTargetTemplateDao.save(any(CookieTargetTemplate.class), 
                any(SqlSession.class))).thenReturn(cookieTarget);

        when(cookieTargetTemplateDao.getByCookieDomainId(eq(cookieTarget.getCookieDomainId()), 
                any(SqlSession.class))).thenReturn(cookieTargetList);
    }
    
    @Test
    public void testGetByDomain() throws Exception {
        
        //call method to test
        RecordSet<CookieTargetTemplate> result = cookieTargetTemplateManager.getByDomain(cookieDomainId, key);
        assertNotNull(result);
        assertThat(result.getRecords(), hasSize(10));
    }

    @Test
    public void testGetByDomainEmptyResult() throws Exception {
        
        //call method to test
        RecordSet<CookieTargetTemplate> result = cookieTargetTemplateManager.getByDomain(cookieDomainId+1, key);
        assertNotNull(result);
        assertThat(result.getRecords(), hasSize(0));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetByDomainWithoutPayload() throws Exception {
        //set values
        cookieDomainId = null;

        //call method to test
        cookieTargetTemplateManager.getByDomain(cookieDomainId, key);
    }    

    @Test(expected = IllegalArgumentException.class)
    public void testGetByDomainWithoutKey() throws Exception {
        //set values
        key = null;

        //call method to test
        cookieTargetTemplateManager.getByDomain(cookieDomainId, key);
    }
    
    @Test(expected = DataNotFoundForUserException.class)
    public void testGetByDomainWithWrongAccess() throws Exception {
        
        //customize mock behavior 
        when(accessControlMockito.isUserValidFor(
            eq(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS),
            anyList(),
            eq(key.getUserId()),
            eq(mockSession))).thenReturn(false);
        
        //call method to test
        cookieTargetTemplateManager.getByDomain(cookieDomainId, key);
    }

    @Test
    public void testSaveCookieTargetTemplate() throws Exception {
        
        //call method to test
        CookieTargetTemplate result = cookieTargetTemplateManager.saveCookieTargetTemplate(cookieDomainId, cookieTarget, key);
        assertNotNull(result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSaveCookieTargetTemplateWithoutPayload() throws Exception {
        //set values
        cookieTarget = null;

        //call method to test
        cookieTargetTemplateManager.saveCookieTargetTemplate(cookieDomainId, cookieTarget, key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveCookieTargetTemplateWithoutKey() throws Exception {
        //set values
        key = null;

        //call method to test
        cookieTargetTemplateManager.saveCookieTargetTemplate(cookieDomainId, cookieTarget, key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveCookieTargetTemplateWithoutCookieDomainId() throws Exception {
        //set values
        cookieDomainId = null;

        //call method to test
        cookieTargetTemplateManager.saveCookieTargetTemplate(cookieDomainId, cookieTarget, key);
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void testSaveCookieTargetTemplateWithWrongAccess() throws Exception {
        
        //customize mock behavior 
        when(accessControlMockito.isUserValidFor(
            eq(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS),
            anyList(),
            eq(key.getUserId()),
            eq(mockSession))).thenReturn(false);
        
        //call method to test
        cookieTargetTemplateManager.saveCookieTargetTemplate(cookieDomainId, cookieTarget, key);
    }
    
    @Test(expected = ValidationException.class)
    public void testSaveCookieTargetTemplateWithoutName() throws Exception {
        //set Values
        cookieTarget.setCookieName(null);
        
        //call method to test
        cookieTargetTemplateManager.saveCookieTargetTemplate(cookieDomainId, cookieTarget, key);
    }

    @Test(expected = ValidationException.class)
    public void testSaveCookieTargetTemplateWithLongName() throws Exception {
        //set Values
        cookieTarget.setCookieName(cookieTarget.getCookieName()+"XXX");
        
        //call method to test
        cookieTargetTemplateManager.saveCookieTargetTemplate(cookieDomainId, cookieTarget, key);
    }
    
    private List<CookieTargetTemplate> createCookieTargetsList(Long cookieDomainId, int cont) {
        List<CookieTargetTemplate> result =  new ArrayList<>();
        
        for (int i = 0; i < cont; i++) {
            CookieTargetTemplate cookie = EntityFactory.createCookieTargetTemplate();
            cookie.setCookieDomainId(cookieDomainId);
            result.add(cookie);
        }
        return result;
    }
}

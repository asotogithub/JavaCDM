package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.SiteContactView;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.dao.SiteDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Abel Soto
 */
public class SiteManagerTest extends AbstractManagerTest {

    //Test Mockito variables
    private Site site;
    
    private SiteDao siteDao;
    private ExtendedPropertiesDao extendedPropertiesDao;

    private SiteManager siteManager;
    
    @Before
    public void init() throws Exception {

        siteDao = mock(SiteDao.class);
        extendedPropertiesDao = mock(ExtendedPropertiesDao.class);
        siteManager = new SiteManager(siteDao, extendedPropertiesDao, accessControlMockito);

        //prepare data
        site = EntityFactory.createSite();
        List<Site> siteList = new ArrayList<>();
        siteList.add(site);
        RecordSet<Site> sites = new RecordSet<>(siteList);

        //mock behaviors - session
        when(siteDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(siteDao).commit(any(SqlSession.class));
        doNothing().when(siteDao).close(any(SqlSession.class));
        
        //mock behaviors - DAC
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE), 
                anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);      
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.PUBLISHER), 
                anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);      

        //mock behaviors
        when(siteDao.get(anyLong(), any(SqlSession.class))).thenReturn(site);
        when(siteDao.getSites(any(SearchCriteria.class), eq(key), any(SqlSession.class))).thenReturn(sites);
        
        when(siteDao.exists(any(Site.class), any(SqlSession.class))).thenReturn(null);
        when(siteDao.getNextId(any(SqlSession.class))).thenReturn(site.getId());
        doNothing().when(siteDao).create(any(Site.class), any(SqlSession.class));
        when(extendedPropertiesDao.updateExternalId(anyString(), anyString(), eq(site.getId()), 
                eq(site.getExternalId()), any(SqlSession.class))).thenReturn(site.getExternalId());
        
        doNothing().when(siteDao).update(any(Site.class), any(SqlSession.class));
        
        doNothing().when(siteDao).delete(eq(site.getId()), eq(key), any(SqlSession.class));
    }
    
    @Test
    public void testGetById() throws Exception {
        //call method to test
        Site result = siteManager.getByid(site.getId(), key);
        assertNotNull(result);
    }

    @Test
    public void testGetCriteria() throws Exception {
        //prepare data
        SearchCriteria criteria = new SearchCriteria();
        criteria.setQuery("id in ["+site.getId()+"] ordering name asc");
        criteria.setStartIndex(1);
        criteria.setPageSize(5);
        
        //call method to test
        RecordSet<Site> result = siteManager.getSites(criteria, key);
        assertNotNull(result);
    }

    @Test
    public void testSaveSite() throws Exception {
        //prepare data
        Site siteToSave = site;
        siteToSave.setId(null);
        
        //call method to test
        Site result = siteManager.create(siteToSave, key);
        assertNotNull(result);
    }

    @Test
    public void testUpdateSite() throws Exception {
        //prepare data
        Site siteToUpdate = site;
        siteToUpdate.setName(EntityFactory.faker.name().name());
        
        //customize mock behaviors
        when(siteDao.get(anyLong(), any(SqlSession.class))).thenReturn(siteToUpdate);
        
        //call method to test
        Site result = siteManager.update(site.getId(), siteToUpdate, key);
        assertNotNull(result);
        assertEquals(result.getName(), siteToUpdate.getName());
    }

    @Test
    public void testDeleteSite() throws Exception {
        
        //call method to test
        SuccessResponse resp = siteManager.remove(site.getId(), key);
        assertNotNull(resp);
    }
    
    @Test(expected = DataNotFoundForUserException.class)
    public void testGetByIdWithWrongAccessSite() throws Exception {
        //customize mock behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE),
                anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(false);

        //call method to test
        siteManager.getByid(site.getId(), key);
    }

    @Test(expected = ValidationException.class)
    public void testSaveSiteWithWrongData() throws Exception {
        //prepare data
        site.setPublisherId(null);
        site.setName(EntityFactory.faker.lorem().fixedString(260));
        
        //customize mock behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE),
                anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(false);

        //call method to test
        siteManager.create(site, key);
    }
    
    @Test(expected = ConflictException.class)
    public void testSaveSiteWithNameDuplicated() throws Exception {
        //prepare data
        Site siteToSave = site;
        siteToSave.setId(null);
        
        //customize mock behavior
        when(siteDao.exists(any(Site.class), any(SqlSession.class))).thenReturn(1L);
        
        //call method to test
        siteManager.create(siteToSave, key);
    }
    
    @Test(expected = ValidationException.class)
    public void testUpdateSiteWithWrongData() throws Exception {
        //prepare data
        Site siteToUpdate = site; 
        siteToUpdate.setPublisherId(null);
        siteToUpdate.setName(EntityFactory.faker.lorem().fixedString(260));
        
        //call method to test
        siteManager.update(site.getId(), siteToUpdate, key);
    }
    
    @Test(expected = NotFoundException.class)
    public void testUpdateSiteNonExistent() throws Exception {
        
        //customize mock behavior
        when(siteDao.get(anyLong(), any(SqlSession.class))).thenReturn(null);
        
        //call method to test
        siteManager.update(site.getId(), site, key);
    }
    
    @Test
    public void testGeSiteContactsViewOk() {
        // set data
        Long campaignId = Math.abs(EntityFactory.random.nextLong());
        List<SiteContactView> siteContacts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SiteContactView view = EntityFactory.createSiteContactView();
            siteContacts.add(view);
        }
        
        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        
        when(siteDao.getTraffickingSiteContacts(anyLong(), eq(sqlSessionMock))).
                thenReturn(siteContacts);
        
        // perform test
        Either<Error,  RecordSet<SiteContactView>> result = siteManager.getTraffickingSiteContacts(campaignId, key);
        assertNotNull(result);
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGeSiteContactsViewWithNullParameters() {
        // set data
        Long campaignId = null;
        key = null;
       
        // perform test
        siteManager.getTraffickingSiteContacts(campaignId, key);
    }
    
    @Test
    public void testGeSiteContactsViewWithWrongAccess() {
        // set data
        Long campaignId = Math.abs(EntityFactory.random.nextLong());
        
        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);
        
        // perform test
        Either<Error,  RecordSet<SiteContactView>> result = siteManager.getTraffickingSiteContacts(campaignId, key);
        assertNotNull(result);
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
    }
}
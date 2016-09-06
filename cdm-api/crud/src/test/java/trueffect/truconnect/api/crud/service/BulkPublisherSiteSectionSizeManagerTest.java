package trueffect.truconnect.api.crud.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.dto.BulkPublisherSiteSectionSize;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.dao.SiteDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by richard.jaldin on 10/22/2015.
 */
public class BulkPublisherSiteSectionSizeManagerTest {

    //Test Mockito variables
    private OauthKey key;
    private BulkPublisherSiteSectionSize bulkPublisherSiteSectionSize;

    private UserDao userDao;
    private PublisherDao publisherDao;
    private SiteDao siteDao;
    private ExtendedPropertiesDao extendedPropertiesDao;
    private SiteSectionDao siteSectionDao;
    private SizeDao sizeDao;

    private BulkPublisherSiteSectionSizeManager bulkPublisherSiteSectionSizeManager;
    private Long agencyId;
    private AccessControl accessControlMockito;
    private SqlSession mockSession;

    @Before
    public void init() throws Exception {
        // Set key value for oauth
        key = new OauthKey(EncryptUtil.encryptAES("dummy@user"), "00000");

        //mock objects
        accessControlMockito = mock(AccessControl.class);
        mockSession = mock(SqlSession.class);

        userDao = mock(UserDao.class);
        publisherDao = mock(PublisherDao.class);
        siteDao = mock(SiteDao.class);
        extendedPropertiesDao = mock(ExtendedPropertiesDao.class);
        siteSectionDao = mock(SiteSectionDao.class);
        sizeDao = mock(SizeDao.class);
        bulkPublisherSiteSectionSizeManager = new BulkPublisherSiteSectionSizeManager(userDao, publisherDao, siteDao, extendedPropertiesDao, siteSectionDao, sizeDao, accessControlMockito);

        //prepare data
        bulkPublisherSiteSectionSize = EntityFactory.createBulkPublisherSiteSectionSize();
        agencyId = Math.abs(EntityFactory.random.nextLong());

        //mock behaviors - session
        when(publisherDao.openSession()).thenReturn(mockSession);
        doNothing().when(publisherDao).commit(any(SqlSession.class));
        doNothing().when(publisherDao).close(any(SqlSession.class));

        //mock behaviors - DAC
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.PUBLISHER), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);

        when(userDao.getAgencyIdByUser(eq(key.getUserId()), eq(mockSession))).thenReturn(agencyId);
    }

    @Test
    public void testSaveBulk() throws Exception {
        //prepare data
        BulkPublisherSiteSectionSize data = bulkPublisherSiteSectionSize;

        when(publisherDao.exists(eq(bulkPublisherSiteSectionSize.getPublisher()), eq(mockSession))).thenReturn(null);
        when(publisherDao.create(eq(bulkPublisherSiteSectionSize.getPublisher()), eq(mockSession))).thenAnswer(createdPublisher());

        RecordSet<Site> emptySet = new RecordSet<>(0, 100, 0, new ArrayList<Site>());
        when(siteDao.getSites(any(SearchCriteria.class), eq(key), eq(mockSession))).thenReturn(emptySet);

        when(siteDao.exists(eq(bulkPublisherSiteSectionSize.getSite()), eq(mockSession))).thenReturn(null);
        doNothing().when(siteDao).create(eq(bulkPublisherSiteSectionSize.getSite()), eq(mockSession));
        when(siteDao.get(anyLong(), eq(mockSession))).thenAnswer(createdSite());

        when(siteSectionDao.exists(eq(bulkPublisherSiteSectionSize.getSection()), eq(mockSession))).thenReturn(0L);
        when(siteSectionDao.create(eq(bulkPublisherSiteSectionSize.getSection()), eq(mockSession))).thenAnswer(createdSection());

        when(sizeDao.create(eq(bulkPublisherSiteSectionSize.getSize()), eq(mockSession))).thenAnswer(createdSize());

        //call method to test
        BulkPublisherSiteSectionSize result = bulkPublisherSiteSectionSizeManager.createBulk(data, false, key);
        assertNotNull(result);
        assertNotNull(result.getPublisher());
        assertNotNull(result.getPublisher().getId());
        assertNotNull(result.getSite());
        assertNotNull(result.getSite().getId());
        assertEquals(result.getPublisher().getId(), result.getSite().getPublisherId());
        assertNotNull(result.getSection());
        assertNotNull(result.getSection().getId());
        assertEquals(result.getSite().getId(), result.getSection().getSiteId());
        assertNotNull(result.getSize());
        assertNotNull(result.getSize().getId());
    }

    @Test(expected = ValidationException.class)
    public void testSaveBulkDuplicatedSiteUnderOtherPublisher() throws Exception {
        //prepare data
        BulkPublisherSiteSectionSize data = bulkPublisherSiteSectionSize;

        when(publisherDao.exists(
                eq(bulkPublisherSiteSectionSize.getPublisher()), 
                eq(mockSession))).thenReturn(null);
        when(publisherDao.create(
                eq(bulkPublisherSiteSectionSize.getPublisher()), 
                eq(mockSession))).thenAnswer(createdPublisher());
        when(siteDao.checkSiteByName(
                any(String.class),
                eq(key.getUserId()), 
                eq(mockSession))).thenReturn(new ArrayList<Site>(){{add(EntityFactory.createSite());
        }});

        //call method to test
        bulkPublisherSiteSectionSizeManager.createBulk(data, false, key);
    }

    @Test
    public void testSaveBulkWithIgnoreDuplicatedSitesFlagEnabled() throws Exception {
        //prepare data
        BulkPublisherSiteSectionSize data = bulkPublisherSiteSectionSize;

        when(publisherDao.exists(eq(bulkPublisherSiteSectionSize.getPublisher()), eq(mockSession))).thenReturn(null);
        when(publisherDao.create(eq(bulkPublisherSiteSectionSize.getPublisher()), eq(mockSession))).thenAnswer(createdPublisher());

        RecordSet<Site> sitesRecordSet = new RecordSet<>(0, 100, 1, new ArrayList<Site>(){{
            add(EntityFactory.createSite());
        }});
        when(siteDao.getSites(any(SearchCriteria.class), eq(key), eq(mockSession))).thenReturn(sitesRecordSet);

        when(siteDao.exists(eq(bulkPublisherSiteSectionSize.getSite()), eq(mockSession))).thenReturn(null);
        doNothing().when(siteDao).create(eq(bulkPublisherSiteSectionSize.getSite()), eq(mockSession));
        when(siteDao.get(anyLong(), eq(mockSession))).thenAnswer(createdSite());

        //call method to test
        BulkPublisherSiteSectionSize result = bulkPublisherSiteSectionSizeManager.createBulk(data, true, key);
        assertNotNull(result);
    }

    private Answer<Publisher> createdPublisher() {
        return new Answer<Publisher>() {
            @Override
            public Publisher answer(InvocationOnMock invocationOnMock) throws Throwable {
                Publisher pub = (Publisher) invocationOnMock.getArguments()[0];
                pub.setId(pub.getId() == null ? Math.abs(EntityFactory.random.nextLong()) : pub.getId());
                pub.setCreatedTpwsKey(pub.getId() == null ? key.getTpws() : pub.getCreatedTpwsKey());
                pub.setAgencyId(agencyId);
                pub.setModifiedTpwsKey(key.getTpws());
                pub.setCreatedDate(new Date());
                pub.setModifiedDate(new Date());
                return pub;
            }
        };
    }

    private Answer<Site> createdSite() {
        return new Answer<Site>() {
            @Override
            public Site answer(InvocationOnMock invocationOnMock) throws Throwable {
                Site site = bulkPublisherSiteSectionSize.getSite();
                site.setId(site.getId() == null ? Math.abs(EntityFactory.random.nextLong()) : site.getId());
                site.setCreatedTpwsKey(site.getId() == null ? key.getTpws() : site.getCreatedTpwsKey());
                site.setModifiedTpwsKey(key.getTpws());
                site.setCreatedDate(new Date());
                site.setModifiedDate(new Date());
                return site;
            }
        };
    }

    private Answer<SiteSection> createdSection() {
        return new Answer<SiteSection>() {
            @Override
            public SiteSection answer(InvocationOnMock invocationOnMock) throws Throwable {
                SiteSection section = bulkPublisherSiteSectionSize.getSection();
                section.setId(section.getId() == null ? Math.abs(EntityFactory.random.nextLong()) : section.getId());
                section.setCreatedTpwsKey(section.getId() == null ? key.getTpws() : section.getCreatedTpwsKey());
                section.setModifiedTpwsKey(key.getTpws());
                section.setCreatedDate(new Date());
                section.setModifiedDate(new Date());
                return section;
            }
        };
    }

    private Answer<Size> createdSize() {
        return new Answer<Size>() {
            @Override
            public Size answer(InvocationOnMock invocationOnMock) throws Throwable {
                Size size = bulkPublisherSiteSectionSize.getSize();
                size.setId(size.getId() == null ? Math.abs(EntityFactory.random.nextLong()) : size.getId());
                size.setCreatedTpwsKey(size.getId() == null ? key.getTpws() : size.getCreatedTpwsKey());
                size.setModifiedTpwsKey(key.getTpws());
                size.setCreatedDate(new Date());
                size.setModifiedDate(new Date());
                return size;
            }
        };
    }
}

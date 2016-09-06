package trueffect.truconnect.api.crud.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

/**
 * Unit Tests for {@code SiteSectionManager}
 * @author Marcelo Heredia
 */
public class SiteSectionManagerTest {

    private SiteSectionDao siteSectionDao;
    private SiteSectionManager siteSectionManager;
    private PersistenceContext context;
    private OauthKey key;
    private SqlSession mockSession;

    @Before
    public void init() throws Exception {
        // Create mocks
        AccessControl accessControlMockito = mock(AccessControl.class);
        context = mock(PersistenceContext.class);
        mockSession = mock(SqlSession.class);
        siteSectionDao = mock(SiteSectionDao.class);
        key = new OauthKey(EncryptUtil.encryptAES("foo@bar.com"), "0000");
        // Classes under tests
        siteSectionManager = new SiteSectionManager(siteSectionDao, accessControlMockito);
        // Define mocks behavior
        when(siteSectionDao.openSession()).thenReturn(mockSession);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE), 
                anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE_SECTION), 
                anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        
    }

    @Test
    public void testSaveAndGet() throws Exception {
        SiteSection siteSection = EntityFactory.createSiteSection();
        siteSection.setId(null);
        String name = siteSection.getName();

        when(siteSectionDao.exists(
            eq(siteSection),
            eq(mockSession))).thenAnswer(existingSiteSection());
        when(siteSectionDao.create(
            eq(siteSection),
            eq(mockSession))).thenAnswer(savedSiteSection(siteSection));

        siteSection = siteSectionManager.create(siteSection, key);

        assertNotNull(siteSection);
        assertEquals(name, siteSection.getName());
        assertEquals(key.getTpws(), siteSection.getCreatedTpwsKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveSiteSectionWithoutPayload() throws Exception {
        SiteSection siteSection = null;
        siteSectionManager.create(siteSection, key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveSiteSectionWithoutKey() throws Exception {
        SiteSection siteSection = EntityFactory.createSiteSection();
        siteSectionManager.create(siteSection, null);
    }

    @Test
    public void testUpdate() throws Exception {
        SiteSection siteSection = EntityFactory.createSiteSection();
        siteSection.setId(null);
        siteSection.setName("testName");
        String name = siteSection.getName();

        when(siteSectionDao.exists(
            eq(siteSection),
            eq(mockSession))).thenAnswer(existingSiteSection());
        when(siteSectionDao.create(
            eq(siteSection),
            eq(mockSession))).thenAnswer(savedSiteSection(siteSection));

        siteSection = siteSectionManager.create(siteSection, key);

        assertNotNull(siteSection);
        assertEquals(name, siteSection.getName());
        assertEquals(key.getTpws(), siteSection.getCreatedTpwsKey());


        when(siteSectionDao.get(
            eq(siteSection.getId()),
            eq(mockSession))).thenAnswer(savedSiteSection(siteSection));
        when(siteSectionDao.update(
            eq(siteSection),
            eq(mockSession))).thenAnswer(savedSiteSection(siteSection));

        String updatedName = "Updated SiteSection name";
        siteSection.setName(updatedName);
        SiteSection updatedSiteSection = siteSectionManager.update(siteSection.getId(), siteSection, key);

        assertNotNull(updatedSiteSection);
        assertEquals(updatedName, siteSection.getName());
        assertEquals(key.getTpws(), siteSection.getModifiedTpwsKey());
    }

    @Test
    public void testRemove() throws Exception {
        SiteSection siteSection = EntityFactory.createSiteSection();
        siteSection.setId(null);
        String name = siteSection.getName();

        when(siteSectionDao.exists(
            eq(siteSection),
            eq(mockSession))).thenAnswer(existingSiteSection());
        when(siteSectionDao.create(
            eq(siteSection),
            eq(mockSession))).thenAnswer(savedSiteSection(siteSection));

        siteSection = siteSectionManager.create(siteSection, key);

        assertNotNull(siteSection);
        assertEquals(name, siteSection.getName());
        assertEquals(key.getTpws(), siteSection.getCreatedTpwsKey());

        SuccessResponse response = siteSectionManager.remove(siteSection.getId(), key);

        assertNotNull(response);
        assertEquals("Site Section " + siteSection.getId() + " successfully deleted", response.getMessage());
    }

    private Answer<Long> existingSiteSection() {
        return new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocationOnMock) throws Throwable {
                return 0L;
            }
        };
    }

    private Answer<SiteSection> savedSiteSection(final SiteSection siteSection) {
        return new Answer<SiteSection>() {
            @Override
            public SiteSection answer(InvocationOnMock invocationOnMock) throws Throwable {
                siteSection.setId(siteSection.getId() == null ? EntityFactory.random.nextLong() : siteSection.getId());
                siteSection.setCreatedTpwsKey(key.getTpws());
                siteSection.setModifiedTpwsKey(key.getTpws());
                siteSection.setCreatedDate(new Date());
                siteSection.setModifiedDate(new Date());
                return siteSection;
            }
        };
    }
}
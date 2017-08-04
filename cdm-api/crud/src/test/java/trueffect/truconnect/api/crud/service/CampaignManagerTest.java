package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.CampaignDetailsDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupDtoForCampaigns;
import trueffect.truconnect.api.commons.model.enums.CampaignStatusEnum;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.BrandDao;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CookieDomainDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Richard Jaldin
 */
public class CampaignManagerTest extends AbstractManagerTest {

    private CampaignManager campaignManager;
    private CampaignDao campaignDaoMock;
    private AdvertiserDao advertiserDaoMock;
    private BrandDao brandDaoMock;
    private CookieDomainDao cookieDomainDaoMock;
    private CreativeDao creativeDao;

    private Campaign testCampaign;

    @Before
    public void init() throws Exception {
        campaignDaoMock = mock(CampaignDao.class);
        advertiserDaoMock = mock(AdvertiserDao.class);
        brandDaoMock = mock(BrandDao.class);
        cookieDomainDaoMock = mock(CookieDomainDao.class);
        creativeDao = mock(CreativeDao.class);
        campaignManager = new CampaignManager(campaignDaoMock, advertiserDaoMock,
                brandDaoMock, cookieDomainDaoMock, creativeDao, accessControlMockito);

        testCampaign = EntityFactory.createCampaign();

        //mock sessions
        when(campaignDaoMock.openSession()).thenReturn(sqlSessionMock);

        //mock access control
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER),
                anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.BRAND),
                anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS),
                anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);

        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY),
                anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyList(), anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.PLACEMENT),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE_GROUP),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);

        //mock behaviors
        when(campaignDaoMock.get(eq(testCampaign.getId()),
                eq(sqlSessionMock))).thenReturn(testCampaign);
    }

    @Test
    public void testGettingCampaignDetails() throws Exception {
        //prepare data
        Advertiser testAdvertiser = EntityFactory.createAdvertiser();
        Brand testBrand = EntityFactory.createBrand();
        CookieDomain testCookieDomain = EntityFactory.createCookieDomain();
        testCampaign.setAdvertiserId(testAdvertiser.getId());
        testCampaign.setBrandId(testBrand.getId());
        testCampaign.setCookieDomainId(testCookieDomain.getId());

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.BRAND), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.COOKIE_DOMAIN), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);

        when(advertiserDaoMock.get(testCampaign.getAdvertiserId(), sqlSessionMock)).thenReturn(testAdvertiser);
        when(brandDaoMock.get(testCampaign.getBrandId(), sqlSessionMock)).thenReturn(testBrand);
        when(cookieDomainDaoMock.get(testCampaign.getCookieDomainId(), sqlSessionMock)).thenReturn(testCookieDomain);

        // Perform test
        CampaignDetailsDTO details = campaignManager.getDetails(testCampaign.getId(), key);
        assertThat(details, is(notNullValue()));
        assertThat(details.getCampaign().getAdvertiserName(), is(equalTo(testAdvertiser.getName())));
        assertThat(details.getCampaign().getBrandName(), is(equalTo(testBrand.getName())));
        assertThat(details.getCampaign().getDomain(), is(equalTo(testCookieDomain.getDomain())));
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void testGettingCampaignDetailsWhenNotAuthorizedForAdvertiser() throws Exception {
        //Prepare data
        Advertiser testAdvertiser = EntityFactory.createAdvertiser();
        Brand testBrand = EntityFactory.createBrand();
        CookieDomain testCookieDomain = EntityFactory.createCookieDomain();
        testCampaign.setAdvertiserId(testAdvertiser.getId());
        testCampaign.setBrandId(testBrand.getId());
        testCampaign.setCookieDomainId(testCookieDomain.getId());

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.BRAND), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);

        // Perform test
        campaignManager.getDetails(testCampaign.getId(), key);
    }

    @Test
    public void testValidatingWhenChangingCookieDomainAndCampaignIsTrafficked() throws Exception {
        //Prepare data
        CookieDomain testCookieDomain = EntityFactory.createCookieDomain();
        CookieDomain newCookieDomain = EntityFactory.createCookieDomain();

        Campaign existingCampaign = EntityFactory.createCampaign();
        existingCampaign.setCookieDomainId(testCookieDomain.getId());
        existingCampaign.setStatusId(2L);

        testCampaign.setId(existingCampaign.getId());
        testCampaign.setCookieDomainId(newCookieDomain.getId());
        testCampaign.setStatusId(2L);

        //Initialize mocks
        when(campaignDaoMock.get(testCampaign.getId(), sqlSessionMock)).thenReturn(existingCampaign);

        // Perform test
        String className = testCampaign.getClass().getSimpleName();
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(testCampaign, className);
        campaignManager.validate(testCampaign, errors);
        assertThat(errors.getErrorCount(), is(equalTo(1)));
        assertThat(errors.getFieldError("cookieDomainId"), is(notNullValue()));
    }

    @Test
    public void testValidatingWhenNameIsDuplicated() throws Exception {
        //Prepare data
        testCampaign.setStatusId(1L);

        // Initialize mocks
        when(campaignDaoMock.isDuplicate(testCampaign, sqlSessionMock)).thenReturn(true);

        // Perform test
        String className = testCampaign.getClass().getSimpleName();
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(testCampaign, className);
        campaignManager.validate(testCampaign, errors);
        assertThat(errors.getErrorCount(), is(equalTo(1)));
        assertThat(errors.getFieldError("name"), is(notNullValue()));
    }

    @Test
    public void testWithNoValidationErrors() throws Exception {
        //Prepare data
        testCampaign.setStatusId(1L);

        // Initialize mocks
        when(campaignDaoMock.isDuplicate(testCampaign, sqlSessionMock)).thenReturn(false);

        // Perform test
        String className = testCampaign.getClass().getSimpleName();
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(testCampaign, className);
        campaignManager.validate(testCampaign, errors);
        assertThat(errors.getErrorCount(), is(equalTo(0)));
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void testUpdatingWhenDontHaveAllPermissions() throws Exception {
        //Prepare data
        Advertiser testAdvertiser = EntityFactory.createAdvertiser();
        Brand testBrand = EntityFactory.createBrand();
        CookieDomain testCookieDomain = EntityFactory.createCookieDomain();
        testCampaign.setAdvertiserId(testAdvertiser.getId());
        testCampaign.setBrandId(testBrand.getId());
        testCampaign.setCookieDomainId(testCookieDomain.getId());
        testCampaign.setStatusId(1L);

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.BRAND), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(campaignDaoMock.get(any(Long.class), any(SqlSession.class))).thenReturn(testCampaign);

        // Perform test
        campaignManager.update(testCampaign, key);
    }

    @Test
    public void testUpdatingCorrectly() throws Exception {
        //Prepare data
        Advertiser testAdvertiser = EntityFactory.createAdvertiser();
        Brand testBrand = EntityFactory.createBrand();
        CookieDomain testCookieDomain = EntityFactory.createCookieDomain();
        testCampaign.setAdvertiserId(testAdvertiser.getId());
        testCampaign.setBrandId(testBrand.getId());
        testCampaign.setCookieDomainId(testCookieDomain.getId());
        testCampaign.setStatusId(1L);

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.BRAND), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(advertiserDaoMock.get(testCampaign.getAdvertiserId(), sqlSessionMock)).thenReturn(testAdvertiser);
        when(brandDaoMock.get(testCampaign.getBrandId(), sqlSessionMock)).thenReturn(testBrand);
        when(cookieDomainDaoMock.get(testCampaign.getCookieDomainId(), sqlSessionMock)).thenReturn(testCookieDomain);
        when(campaignDaoMock.update(testCampaign, sqlSessionMock)).thenReturn(1);

        // Perform test
        Campaign updatedCampaign = campaignManager.update(testCampaign, key);
        assertThat(updatedCampaign, is(testCampaign));
    }

    @Test
    public void testUpdatingWithDomainThatHasNotChangedButHaveNoAccessTo() throws Exception {
        //Prepare data
        Advertiser testAdvertiser = EntityFactory.createAdvertiser();
        Brand testBrand = EntityFactory.createBrand();
        CookieDomain testCookieDomain = EntityFactory.createCookieDomain();
        testCampaign.setAdvertiserId(testAdvertiser.getId());
        testCampaign.setBrandId(testBrand.getId());
        testCampaign.setCookieDomainId(testCookieDomain.getId());
        testCampaign.setStatusId(1L);

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.BRAND), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(advertiserDaoMock.get(testCampaign.getAdvertiserId(), sqlSessionMock)).thenReturn(testAdvertiser);
        when(brandDaoMock.get(testCampaign.getBrandId(), sqlSessionMock)).thenReturn(testBrand);
        when(cookieDomainDaoMock.get(testCampaign.getCookieDomainId(), sqlSessionMock)).thenReturn(testCookieDomain);
        when(campaignDaoMock.update(testCampaign, sqlSessionMock)).thenReturn(1);

        // Perform test
        Campaign updatedCampaign = campaignManager.update(testCampaign, key);
        assertThat(updatedCampaign, is(equalTo(testCampaign)));
    }

    @Test
    public void testSaveCampaign() throws Exception {
        //Prepare data
        testCampaign.setId(null);
        Long id = EntityFactory.random.nextLong();

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(campaignDaoMock.getNextId(eq(sqlSessionMock))).thenReturn(id);
        when(campaignDaoMock.getLimitAdvertisers(eq(key.getUserId()), eq(sqlSessionMock))).thenReturn("N");
        when(campaignDaoMock.getAdvertiserId(eq(key.getUserId()), eq(testCampaign.getBrandId()),
                eq(sqlSessionMock))).thenReturn(testCampaign.getAdvertiserId());
        when(campaignDaoMock.getCampaignCount(eq(testCampaign.getName()),
                eq(testCampaign.getBrandId()), eq(sqlSessionMock))).thenReturn(0L);
        doNothing().when(campaignDaoMock).saveCampaign(anyLong(), any(Campaign.class),
                anyLong(), eq(key), eq(sqlSessionMock));
        when(campaignDaoMock.getMediaBuyId(eq(sqlSessionMock))).thenReturn(testCampaign.getMediaBuyId());
        doNothing().when(campaignDaoMock).saveMediaBuy(anyLong(), anyLong(), any(Campaign.class),
                eq(key), eq(sqlSessionMock));
        doNothing().when(campaignDaoMock).saveMediaBuyCampaign(anyLong(), anyLong(),
                eq(key), eq(sqlSessionMock));
        doNothing().when(campaignDaoMock).saveCreativeGroup(anyLong(), eq(key), eq(sqlSessionMock));

        when(campaignDaoMock.get(eq(id),
                eq(sqlSessionMock))).thenAnswer(getCampaign());

        // Perform test
        Campaign result = campaignManager.create(testCampaign, key);
        assertThat(result.getName(), is(equalTo(testCampaign.getName())));
        assertThat(result.getAdvertiserId(), is(equalTo(testCampaign.getAdvertiserId())));
        assertThat(result.getBrandId(), is(equalTo(testCampaign.getBrandId())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveCampaignWithWrongPayload() throws Exception {
        //Prepare data
        testCampaign = null;

        // Perform test
        campaignManager.create(testCampaign, key);
    }

    @Test(expected = ValidationException.class)
    public void testSaveCampaignWithWrongDataPayload() throws Exception {
        //Prepare data
        testCampaign.setStartDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(testCampaign.getStartDate());
        cal.add(Calendar.DAY_OF_YEAR, -30);
        testCampaign.setEndDate(cal.getTime());

        // Perform test
        campaignManager.create(testCampaign, key);
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void testSaveCampaignWithWrongAccessAdvertiser() throws Exception {
        //Prepare data
        testCampaign.setId(null);

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER),
                anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(false);

        // Perform test
        campaignManager.create(testCampaign, key);
    }

    @Test(expected = ConflictException.class)
    public void testSaveCampaignWithDuplicateName() throws Exception {
        //Prepare data
        testCampaign.setId(null);
        Long id = EntityFactory.random.nextLong();

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(campaignDaoMock.getNextId(eq(sqlSessionMock))).thenReturn(id);
        when(campaignDaoMock.getLimitAdvertisers(eq(key.getUserId()), eq(sqlSessionMock))).thenReturn("N");
        when(campaignDaoMock.getAdvertiserId(eq(key.getUserId()), eq(testCampaign.getBrandId()),
                eq(sqlSessionMock))).thenReturn(testCampaign.getAdvertiserId());
        when(campaignDaoMock.getCampaignCount(eq(testCampaign.getName()),
                eq(testCampaign.getBrandId()), eq(sqlSessionMock))).thenReturn(1L);

        // Perform test
        campaignManager.create(testCampaign, key);
    }

    @Test
    public void testSetCampaignStatus() {
        //Prepare data
        Long newStatus = 1L;

        // Initialize mocks        
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        doNothing().when(campaignDaoMock).setCampaignStatus(eq(testCampaign.getId()), eq(newStatus),
                eq(sqlSessionMock));
        when(campaignDaoMock.getCampaignStatus(eq(testCampaign.getId()),
                eq(sqlSessionMock))).thenReturn(newStatus);

        // Perform test
        Either<Error, Boolean> result = campaignManager.setCampaignStatus(testCampaign.getId(), CampaignStatusEnum.NEW, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(equalTo(Boolean.TRUE)));
    }

    @Test
    public void testGettingCreativeGroupsForCampaign() throws Exception {
        //Prepare data
        List<CreativeGroupDtoForCampaigns> list = EntityFactory.createListForCreativeGroupDtoForCampaigns(testCampaign.getId(), 2);

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), anyList(), eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(campaignDaoMock.getCreativeGroupList(testCampaign.getId(), 0L, 1000L, sqlSessionMock)).thenReturn(list);
        when(campaignDaoMock.getCountCreativeGroupList(testCampaign.getId(), sqlSessionMock)).thenReturn(new Long(list.size()));

        // Perform test
        RecordSet<CreativeGroupDtoForCampaigns> creativeGroups = campaignManager.getCreativeGroupsForCampaign(testCampaign.getId(), 0L, 1000L, key);
        assertThat(creativeGroups.getTotalNumberOfRecords(), is(equalTo(2)));
    }

    private Answer<Campaign> getCampaign() {
        return new Answer<Campaign>() {
            public Campaign answer(InvocationOnMock invocation) {
                Long id = (Long) invocation.getArguments()[0];
                testCampaign.setId(id);
                testCampaign.setCreatedTpwsKey(key.getTpws());
                testCampaign.setModifiedTpwsKey(key.getTpws());
                return testCampaign;
            }
        };
    }
}

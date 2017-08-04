package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.AgencyUser;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.CampaignCreatorContact;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Trafficking;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.enums.CampaignStatusEnum;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.TraffickingDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public class TraffickingManagerTest {
    
    private AccessControl accessControl;
    private OauthKey key;
    private Trafficking trafficking;
    private SqlSession mockSession;
    private User user;
    private Campaign campaign;

    private TraffickingDao traffickingDao;
    private UserDao userDao;
    private CampaignDao campaignDao;
    private TraffickingManager traffickingManager;
    private TraffickingManager.Trafficker trafficker;

    @Before
    public void setUp() throws Exception {
        // Mocks
        accessControl = mock(AccessControl.class);
        mockSession = mock(SqlSession.class);
        traffickingDao = mock(TraffickingDao.class);
        userDao = mock(UserDao.class);
        campaignDao = mock(CampaignDao.class);
        trafficker = mock(TraffickingManager.Trafficker.class);
        
        //variables
        key = new OauthKey(EncryptUtil.encryptAES("foo@bar.com"), "0000");
        trafficking = EntityFactory.createTrafficking();
        
        user = EntityFactory.createUser();
        user.setUserName(key.getUserId());
        
        campaign = EntityFactory.createCampaign();
        campaign.setId(trafficking.getCampaignId());
        campaign.setAgencyId(user.getAgencyId());
        campaign.setStatusId(CampaignStatusEnum.NEW.getStatusCode());
        
        AgencyUser agencyUser = EntityFactory.createAgencyUser();
        
        //manager
        traffickingManager = new TraffickingManager(traffickingDao, userDao, campaignDao,
                 accessControl);
        
        // Mock behaviors
        when(traffickingDao.openSession()).thenReturn(mockSession);
        MockAccessControlUtil.allowAccessForUser(accessControl, AccessStatement.CAMPAIGN, mockSession);
        MockAccessControlUtil.allowAccessForUser(accessControl, AccessStatement.COOKIE_DOMAIN, mockSession);
        when(userDao.get(eq(key.getUserId()),
                any(SqlSession.class))).thenReturn(user);
        when(campaignDao.get(eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(campaign);
        when(campaignDao.getCampaignStatus(eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(campaign.getStatusId());
        when(userDao.getByTPWSKey(eq(key.getTpws()),
                any(SqlSession.class))).thenReturn(agencyUser);
        doNothing().when(campaignDao).setCampaignStatus(
                any(Long.class), any(Long.class), any(SqlSession.class));
        //mock validations
        when(traffickingDao.getCreativePlacementMatchHeightAndWidth(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(0L);
        when(traffickingDao.getCreativeSchedule(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(1L);
        when(traffickingDao.getSchedulesClickthroughCount(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(0L);
        when(traffickingDao.getDatesValidationCount(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(0L);
        when(traffickingDao.checkContactsBelongsAgencyByUser(
                anyList(),
                eq(key.getUserId()),
                any(SqlSession.class))).thenReturn(true);
        when(traffickingDao.getCreativeGroupCreativesByCampaign(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(EntityFactory.createCampaignCreatorContact());
        //mock trafficking
        when(trafficker.pushHtmlTagToTruQ(
                any(Integer.class), any(Integer.class),
                any(String.class))).thenReturn(Boolean.TRUE);
    }
    
    @Test
    public void testTrafficCampaign() throws Exception {
        //set values
        CampaignCreatorContact campaignCreatorContact = new CampaignCreatorContact();
        campaignCreatorContact.setContactId(1L);
        
        //customize mock behavior 
        when(traffickingDao.getCreativeGroupCreativesByCampaign(
                eq(trafficking.getCampaignId()), 
                any(SqlSession.class))).thenReturn(campaignCreatorContact);
        when(userDao.getTraffickingContacts(anyList(),
                any(SqlSession.class))).thenReturn(trafficking.getAgencyContacts());

        //call method to test
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result =
                traffickingManager.trafficCampaign(trafficking, key, trafficker);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(nullValue()));
    }
    
    @Test
    public void testNotTraffickingContactOk() throws Exception {
        //set values
        CampaignCreatorContact campaignCreatorContact = new CampaignCreatorContact();
        campaignCreatorContact.setContactId(1L);
        //customize mock behavior 
        when(traffickingDao.getCreativeGroupCreativesByCampaign(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(campaignCreatorContact);
        when(userDao.getTraffickingContacts(anyList(),
                any(SqlSession.class))).thenReturn(trafficking.getAgencyContacts());
        //call method to test
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result =
                traffickingManager.trafficCampaign(trafficking, key, trafficker);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(nullValue()));
    }

    @Test
    public void testNotTraffickingContactFail() throws Exception {
        //set values
        CampaignCreatorContact campaignCreatorContact = new CampaignCreatorContact();
        campaignCreatorContact.setContactId(1L);
        List<Integer> notTraffickingContact = new ArrayList<>(trafficking.getAgencyContacts());
        notTraffickingContact.remove(0);
        //customize mock behavior 
        when(traffickingDao.getCreativeGroupCreativesByCampaign(
                eq(trafficking.getCampaignId()), 
                any(SqlSession.class))).thenReturn(campaignCreatorContact);
        when(userDao.getTraffickingContacts(anyList(),
                any(SqlSession.class))).thenReturn(notTraffickingContact);
        //call method to test
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result =
                traffickingManager.trafficCampaign(trafficking, key, trafficker);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        ValidationError error =
                (ValidationError) result.error().getErrors().iterator().next();
        assertThat((ValidationCode)error.getCode(),
                is(ValidationCode.INVALID));
        assertThat(error.getMessage(),
                is(equalTo("Failed to traffic. Not all Contacts are related to Trafficking Contact Users.")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrafficCampaignWithoutPayload() throws Exception {
        //set values
        trafficking = null;

        //call method to test
        traffickingManager.trafficCampaign(trafficking, key, trafficker);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrafficCampaignWithoutKey() throws Exception {
        //set values
        key = null;

        //call method to test
        traffickingManager.trafficCampaign(trafficking, key, trafficker);
    }

    @Test
    public void testTrafficCampaignWithWrongAccess() throws Exception {
        
        //customize mock behavior 
        when(accessControl.isUserValidFor(
                eq(AccessStatement.CAMPAIGN),
                eq(Collections.singletonList(campaign.getId())),
                eq(key.getUserId()),
                eq(mockSession))).thenReturn(false);
        //call method to test
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result =
                traffickingManager.trafficCampaign(trafficking, key, trafficker);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        AccessError error =
                (AccessError) result.error().getErrors().iterator().next();
        assertThat((SecurityCode) error.getCode(),
                is(SecurityCode.NOT_FOUND_FOR_USER));
        String message = String.format("%s: [%s]Not found for User: %s", "CampaignId", String.valueOf(campaign.getId()),
                key.getUserId());
        assertThat(error.getMessage(),
                is(equalTo(
                        message)));
    }
    
    @Test
    public void testTrafficCampaignThatMyUserIsNotAllowedTo() throws Exception {
        Campaign campaign = EntityFactory.createCampaign();
        //customize mock behavior 
        when(campaignDao.get(eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(campaign);
        //call method to test
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result =
                traffickingManager.trafficCampaign(trafficking, key, trafficker);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        BusinessError error =
                (BusinessError) result.error().getErrors().iterator().next();
        assertThat((BusinessCode)error.getCode(),
                is(BusinessCode.NOT_FOUND));
        assertThat(error.getMessage(),
                is(equalTo(
                        "Campaign's agencyId mismatch User's agencyId.")));
    }

    @Test
    public void testTrafficCampaignWithWrongAgencyId() throws Exception {
        //set values
        campaign.setAgencyId(EntityFactory.random.nextLong());
        //customize mock behavior 
        when(campaignDao.get(eq(trafficking.getCampaignId()), 
                any(SqlSession.class))).thenReturn(campaign);
        when(userDao.get(eq(key.getUserId()), 
                eq(key.getUserId()), 
                any(SqlSession.class))).thenReturn(user);
        
        //call method to test
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result =
                traffickingManager.trafficCampaign(trafficking, key, trafficker);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        BusinessError error =
                (BusinessError) result.error().getErrors().iterator().next();
        assertThat((BusinessCode) error.getCode(),
                is(BusinessCode.NOT_FOUND));
        assertThat(error.getMessage(),
                is(equalTo(
                        "Campaign's agencyId mismatch User's agencyId.")));
    }


    @Test
    public void testTrafficCampaignWithWrongSettings() throws Exception {
        //set values
        CampaignCreatorContact campaignCreatorContact = new CampaignCreatorContact();
        campaignCreatorContact.setContactId(1L);

        //customize mock behavior 
        when(traffickingDao.getCreativeGroupCreativesByCampaign(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(campaignCreatorContact);
        when(traffickingDao.getCreativePlacementMatchHeightAndWidth(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(1L);
        when(traffickingDao.getCreativeSchedule(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(0L);
        when(traffickingDao.getSchedulesClickthroughCount(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(1L);

        //call method to test
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result =
                traffickingManager.trafficCampaign(trafficking, key, trafficker);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(3)));
        Iterator<trueffect.truconnect.api.commons.exceptions.business.Error> iterator =
                result.error().getErrors().iterator();
        ValidationError error =
                (ValidationError) iterator.next();
        assertThat((ValidationCode) error.getCode(),
                is(ValidationCode.INVALID));
        assertThat(error.getMessage(),
                is(equalTo(
                        "This Campaign has no Schedules to traffic")));
        error =
                (ValidationError) iterator.next();
        assertThat((ValidationCode) error.getCode(),
                is(ValidationCode.INVALID));
        assertThat(error.getMessage(),
                is(equalTo(
                        "Placement's dimensions do not match Creative's dimensions")));
        error =
                (ValidationError) iterator.next();
        assertThat((ValidationCode) error.getCode(),
                is(ValidationCode.INVALID));
        assertThat(error.getMessage(),
                is(equalTo(
                        "Missing Clickthrough for some Schedules")));
    }
    
    @Test
    public void testValidationsCheck() throws Exception {
        Errors result = traffickingManager.checkCampaign(campaign.getId(), key);
        assertNotNull(result);
        Assert.assertEquals(0, result.getErrors().size());
    }

    @Test
    public void testValidationsCheckFailDimensions() throws Exception {
        when(traffickingDao.getCreativePlacementMatchHeightAndWidth(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(1L);
        Errors result = traffickingManager.checkCampaign(campaign.getId(), key);
        assertNotNull(result);
        Assert.assertEquals(1, result.getErrors().size());
    }

    @Test
    public void testValidationsCheckFailSchedule() throws Exception {
        when(traffickingDao.getCreativeSchedule(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(0L);
        Errors result = traffickingManager.checkCampaign(campaign.getId(), key);
        assertNotNull(result);
        Assert.assertEquals(1, result.getErrors().size());
    }

    @Test
    public void testValidationsCheckFailClickThrough() throws Exception {
        when(traffickingDao.getSchedulesClickthroughCount(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(1L);
        when(traffickingDao.getDatesValidationCount(
                eq(trafficking.getCampaignId()), 
                any(SqlSession.class))).thenReturn(0L);
        Errors result = traffickingManager.checkCampaign(campaign.getId(), key);
        assertNotNull(result);
        Assert.assertEquals(1, result.getErrors().size());
    }

    @Test
    public void testValidationsCheckFailClickThroughAndDimensions() throws Exception {
        when(traffickingDao.getCreativePlacementMatchHeightAndWidth(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(1L);
        when(traffickingDao.getSchedulesClickthroughCount(
                eq(trafficking.getCampaignId()),
                any(SqlSession.class))).thenReturn(1L);
        Errors result = traffickingManager.checkCampaign(campaign.getId(), key);
        assertNotNull(result);
        Assert.assertEquals(2, result.getErrors().size());
    }
}

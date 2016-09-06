package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.AgencyUser;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.CampaignCreatorContact;
import trueffect.truconnect.api.commons.model.Error;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Trafficking;
import trueffect.truconnect.api.commons.model.TraffickingArrayOfint;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.enums.CampaignStatusEnum;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.TraffickingDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.consumer.wsdl.TraffickingService;
import trueffect.truconnect.consumer.wsdl.TraffickingService_Service;
import com.trueffect.tags.IMSMQService;
import com.trueffect.tags.MSMQService;

import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfint;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Class that defines Business Logic for Trafficking operations
 *
 * @author Abel Soto
 * @author Marcelo Heredia
 */
public class TraffickingManager extends AbstractGenericManager {
    
    private TraffickingDao traffickingDao;
    private UserDao userDao;
    private CampaignDao campaignDao;

    public TraffickingManager(TraffickingDao traffickingDao,
                              UserDao userDao,
                              CampaignDao campaignDao,
                              AccessControl accessControl) {
        super(accessControl);
        this.traffickingDao = traffickingDao;
        this.userDao = userDao;
        this.campaignDao = campaignDao;
    }

    /**
     * Trafficks a given {@code Campaign}
     *
     * @param trafficking
     * @param key User Identifier
     * @return
     */    
    public Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void>
    trafficCampaign(Trafficking trafficking, OauthKey key, Trafficker trafficker){
        // Nullability checks
        if (trafficking == null) {
            throw new IllegalArgumentException("trafficking cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        trueffect.truconnect.api.commons.exceptions.business.Errors errors = new trueffect.truconnect.api.commons.exceptions.business.Errors();
        List<Integer> agencyContacts = trafficking.getAgencyContacts();
        List<Integer> siteContacts = trafficking.getSiteContacts();
        if ((agencyContacts == null || agencyContacts.isEmpty())
                && trafficking.getCurrentContactId() == null) {
            errors.addError(new ValidationError("Agency Contacts Id is a required field.",
                    ValidationCode.INVALID));
            return Either.error(errors);
        }
        if ((siteContacts == null || siteContacts.isEmpty())
                && trafficking.getCurrentContactId() == null) {
            errors.addError(new ValidationError("Site Contacts Id is a required field.",
                    ValidationCode.INVALID));
            return Either.error(errors);
        }

        SqlSession session = traffickingDao.openSession();

        try {
            // DAC
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(trafficking.getCampaignId()), key.getUserId(), session)) {
                String message = ResourceBundleUtil.getString("dataAccessControl.notFoundForUser",
                        "CampaignId", Long.toString(trafficking.getCampaignId()), key.getUserId());
                errors.addError(new AccessError(message,
                        SecurityCode.NOT_FOUND_FOR_USER));
                return Either.error(errors);
            }
            if (trafficking.getCookieDomainId() != null && !userValidFor(AccessStatement.COOKIE_DOMAIN, Collections.singletonList(trafficking.getCookieDomainId()), key.getUserId(), session)) {
                String message = ResourceBundleUtil.getString("dataAccessControl.notFoundForUser",
                        "domainId", Long.toString(trafficking.getCookieDomainId()), key.getUserId());
                errors.addError(new AccessError(message,
                        SecurityCode.NOT_FOUND_FOR_USER));
                return Either.error(errors);
            }

            // Validate contacts
            if(trafficking.getCurrentContactId() != null) {
                List<Integer> contacts = agencyContacts != null ? agencyContacts : new ArrayList<Integer>();
                contacts.add(trafficking.getCurrentContactId());
                trafficking.setAgencyContacts(contacts);
                agencyContacts = trafficking.getAgencyContacts();

                if (!traffickingDao.checkContactsBelongsAgencyByUser(agencyContacts, key.getUserId(), session)) {
                    errors.addError(new ValidationError(ResourceBundleUtil.getString(
                            "global.error.dataAccessControlEntity", "Contact"),
                            ValidationCode.INVALID));
                    return Either.error(errors);
                }

                if(trafficking.getSiteContacts() != null && !trafficking.getSiteContacts().isEmpty()) {
                    if (!traffickingDao.checkSiteContacts(trafficking.getSiteContacts(), key.getUserId(), session)) {
                        errors.addError(new ValidationError(ResourceBundleUtil.getString(
                                "global.error.dataAccessControlEntity", "Site Contact"),
                                ValidationCode.INVALID));
                        return Either.error(errors);
                    }
                }

                List<Integer> sites = siteContacts != null ? siteContacts : new ArrayList<Integer>();
                sites.add(trafficking.getCurrentContactId());
                trafficking.setSiteContacts(sites);

            } else {
                if (!traffickingDao.checkContactsBelongsAgencyByUser(agencyContacts, key.getUserId(), session)) {
                    errors.addError(new ValidationError(ResourceBundleUtil.getString(
                            "global.error.dataAccessControlEntity", "Contact"),
                            ValidationCode.INVALID));
                    return Either.error(errors);
                }

                if (!traffickingDao.checkContactsBelongsAgencyByUser(siteContacts, key.getUserId(), session)) {
                    errors.addError(new ValidationError(ResourceBundleUtil.getString(
                            "global.error.dataAccessControlEntity", "Contact"),
                            ValidationCode.INVALID));
                    return Either.error(errors);
                }
            }

            // Campaign validations
            List<String> stringErrors = checkCampaign(trafficking.getCampaignId(), key, session);
            if (!stringErrors.isEmpty()) {
                for (String message : stringErrors) {
                    errors.addError(new ValidationError(message, ValidationCode.INVALID));
                }
            }
            if (errors.isEmpty()) {
                // Validates that Agency from User and Campaign's Agency are the same
                Campaign campaign =  campaignDao.get(trafficking.getCampaignId(), session);
                // Obtain status from campaign
                Long campaignId = campaign.getId();
                log.debug("Status for Campaign {} is {} ", campaignId, campaign.getStatusId());
                Long previousStatus = campaign.getStatusId();

                User user = userDao.get(key.getUserId(), session);
                log.debug("User  -> {} \nCampaign  ->: {} ", user, campaign);
                if (!user.getAgencyId().equals(campaign.getAgencyId())) {
                    log.warn("Campaign's agencyId : {} mismatch User's agencyId: {}." , campaign.getAgencyId(), user.getAgencyId());
                    errors.addError(new BusinessError(ResourceBundleUtil.getString("trafficking.error.campaignAndUserMismatchAgencyId"),
                            BusinessCode.NOT_FOUND, null));
                    return Either.error(errors);
                }

                List<Integer> allowedTraffickingContacts = userDao.getTraffickingContacts(agencyContacts, session);
                if(allowedTraffickingContacts.size() != agencyContacts.size()){
                    errors.addError(new ValidationError(ResourceBundleUtil.getString("trafficking.error.notTraffickingContact"),
                            ValidationCode.INVALID));
                    return Either.error(errors);
                }
                
                // Change Campaign's domain ID if requester requires so, and it is possible according the status of the campaign
                if(trafficking.getCookieDomainId() != null) {
                    if(CampaignStatusEnum.typeOf(campaign.getStatusId()) == CampaignStatusEnum.NEW) {
                        campaign.setCookieDomainId(trafficking.getCookieDomainId());
                        campaign.setModifiedTpwsKey(key.getTpws());
                        int affected = campaignDao.updateCookieDomainId(campaign, session);
                        log.debug("Campaign {} has been updated: {}", campaign.getId(), affected);
                    } else {
                        log.warn("Trying to update the Campaign Domain ID when the Campaign ({}) status is {}", campaign.getId(), campaign.getStatusId());
                    }
                }
                // Change campaign status --> trafficking
                campaignDao.setCampaignStatus(trafficking.getCampaignId(), CampaignStatusEnum.TRFKG.getStatusCode(), session);

                AgencyUser agencyUser = userDao.getByTPWSKey(key.getTpws(), session);
                Long responseCampaign = getCampaignCreatorContactId(trafficking.getCampaignId(), key, session);
                if(responseCampaign == null) {
                    String message = ResourceBundleUtil.getString("trafficking.error.locatorNotFound", String.valueOf(trafficking.getCampaignId()));
                    errors.addError(new BusinessError(
                            message, BusinessCode.NOT_FOUND, null));
                    return Either.error(errors);
                }
                Integer creatorContactId = responseCampaign.intValue();
                if (creatorContactId <= 0) {
                    errors.addError(new BusinessError(ResourceBundleUtil.getString("trafficking.error.failedTraffic",
                            String.valueOf(trafficking.getCampaignId())), BusinessCode.NOT_FOUND, null));
                    return Either.error(errors);
                }
                // Prepare data to traffic
                TraffickingArrayOfint traffickingContacts = new TraffickingArrayOfint();
                traffickingContacts.setAgencyContacts(agencyContacts);
                traffickingContacts.setSiteContacts(siteContacts);
                // Call service to traffic
                trafficker.setSqlSession(session);
                trafficker.callTrafficking(Integer.valueOf(campaignId.intValue()),
                        Integer.valueOf(previousStatus.intValue()), traffickingContacts,
                        creatorContactId,
                        Integer.valueOf(agencyUser.getId().intValue()));
                trafficker.pushHtmlTagToTruQ(Integer.valueOf(campaignId.intValue()),
                        Integer.valueOf(agencyUser.getId().intValue()), key.getTpws());
                log.info("The campaignId {} is being trafficked: {} ", campaignId, trafficking);
                traffickingDao.commit(session);
            }
            else {
                traffickingDao.rollback(session);
                return Either.error(errors);
            }
        } catch (Exception e) {
            log.error("Unexpected exception: ", e);
            traffickingDao.rollback(session);
            errors.addError(new BusinessError(ResourceBundleUtil.getString(
                    "global.error.dataAccessControlEntity", "Contact"),
                    BusinessCode.INTERNAL_ERROR, null));
            return Either.error(errors);
        } finally {
            traffickingDao.close(session);
        }
        return Either.success(null);
    }  
    
    /**
     * Validate campaign to traffic
     *
     * @param campaignId Campaign Id.
     * @param key User Identifier
     * @return
     */
    public Errors checkCampaign(Long campaignId, OauthKey key) throws Exception {
        //validations
        // Nullability checks
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign Id cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        //Session
        SqlSession session = traffickingDao.openSession();
        Errors result = new Errors();
        try {
            //check access control
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(
                        ResourceBundleUtil.getString("dataAccessControl.dataNotFoundForUser", key.getUserId()));
            }
            errors = checkCampaign(campaignId, key, session);
            List<Error> allErrors = new ArrayList<>();
            if (!errors.isEmpty()) {
                for (String message : errors) {
                    Error error = new Error(message, ApiValidationUtils.TYPE_INVALID);
                    allErrors.add(error);
                }
            }
            result.setErrors(allErrors);
        } finally {
            traffickingDao.close(session);
        }
        return result;
    }

    /**
     * check Campaign Validation Before Trafficking
     *
     * @param campaignId Campaign Id.
     * @param key User Identifier
     * @param session 
     * @return
     */
    private List<String> checkCampaign(Long campaignId, OauthKey key, SqlSession session) {
        List<String> errorList = new ArrayList<>();
        
        MultivaluedMap<String, String> mapErrors = validateCampaign(campaignId, key, session);
        Long creativeSchedules = new Long(mapErrors.getFirst("creativeSchedules"));
        if (creativeSchedules == 0) {
            errorList.add("This Campaign has no Schedules to traffic");
        }
        Long nonMatchHeightWidth = new Long(mapErrors.getFirst("nonMatchHeightWidth"));
        if (nonMatchHeightWidth > 0) {
            errorList.add("Placement's dimensions do not match Creative's dimensions");
        }
        Long schedulesClickthrough = new Long(mapErrors.getFirst("schedulesClickthrough"));
        if (schedulesClickthrough > 0) {
            errorList.add("Missing Clickthrough for some Schedules");
        }        
        Long creativesClickthrough = new Long(mapErrors.getFirst("creativesClickthrough"));
        if (creativesClickthrough > 0) {
            errorList.add("Missing Clickthrough for some Creatives");
        }        
        Long datesValidation = new Long(mapErrors.getFirst("datesValidation"));
        if (datesValidation > 0) {
            errorList.add("End date cannot be less than Start date");
        }
        return errorList;
    }

    /**
     * Returns Quantity math width and Height between creative and placement
     *
     * @param campaignId Campaign Id.
     * @param key User Identifier
     * @param session
     * @return
     */
    private MultivaluedMap<String, String> validateCampaign(Long campaignId, OauthKey key, SqlSession session) {
        //validations
        // Nullability checks
        if (campaignId == null) {
            throw new IllegalArgumentException("IO's id cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }
        
        MultivaluedMap<String, String> resultMap = new MultivaluedMapImpl();
        Long nonMatchHeightWidth = traffickingDao.getCreativePlacementMatchHeightAndWidth(campaignId, session);
        Long creativeSchedules = traffickingDao.getCreativeSchedule(campaignId, session);
        Long schedulesClickthrough = traffickingDao.getSchedulesClickthroughCount(campaignId, session);
        Long creativesClickthrough = traffickingDao.getCreativesClickthroughCount(campaignId, session);
        Long datesValidation = traffickingDao.getDatesValidationCount(campaignId, session);
        //add result validations into map
        resultMap.add("nonMatchHeightWidth", Long.toString(nonMatchHeightWidth));
        resultMap.add("creativeSchedules", Long.toString(creativeSchedules));
        resultMap.add("schedulesClickthrough", Long.toString(schedulesClickthrough));
        resultMap.add("creativesClickthrough", Long.toString(creativesClickthrough));
        resultMap.add("datesValidation", Long.toString(datesValidation));
        return resultMap;
    }

    /**
     * Returns Campaign Contact
     *
     * @param id Campaign Id.
     * @param key User Identifier
     * @return
     */
    private Long getCampaignCreatorContactId(Long id, OauthKey key, SqlSession session) {
        //validations
        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException("IO's id cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }
        
        //Session
        CampaignCreatorContact result;
        //call dao
        result = traffickingDao.getCreativeGroupCreativesByCampaign(id, session);
        if (result != null) {
            return result.getContactId();

        } else {
            return null;
        }
    }

    /**
     * Interface to make this class testable
     * */
    public interface Trafficker {
        Either<trueffect.truconnect.api.commons.exceptions.business.Error, Boolean>
        callTrafficking(Integer entityId, Integer priorStatusId,
                             TraffickingArrayOfint selectContacts,
                             Integer campaignOwner, Integer traffickingUserId);
        Boolean pushHtmlTagToTruQ(Integer campaignId, Integer agencyId, String sessionKey);
        void setSqlSession(SqlSession session);
    }

    /**
     * Default implementation for {@code Trafficker} that know to talk to .Net Services
     */
    public static class ExternalTrafficker implements Trafficker {
        private Logger log = LoggerFactory.getLogger(this.getClass());
        @Override
        public Either<trueffect.truconnect.api.commons.exceptions.business.Error, Boolean>
        callTrafficking(Integer entityId, Integer priorStatusId,
                                    TraffickingArrayOfint selectContacts,
                                    Integer campaignOwner, Integer traffickingUserId) {
            ArrayOfint agencyContacts = new ArrayOfint();
            agencyContacts.setInt(selectContacts.getAgencyContacts());
            ArrayOfint siteContacts = new ArrayOfint();
            siteContacts.setInt(selectContacts.getSiteContacts());
            TraffickingService_Service service = new TraffickingService_Service();
            TraffickingService port = service.getBasicHttpBindingTraffickingService();
            port.trafficCampaign(entityId, priorStatusId, agencyContacts, siteContacts, campaignOwner, traffickingUserId);
            return Either.success(Boolean.TRUE);
        }

        @Override
        public Boolean pushHtmlTagToTruQ(Integer campaignId, Integer agencyId, String sessionKey){
            Boolean result = false;
            try {
                MSMQService service = new MSMQService();
                IMSMQService port = service.getBasicHttpBindingIMSMQService();
                result = port.insertMessageToQueue(campaignId, agencyId, sessionKey);
            } catch (Exception e) {
                log.warn("Could not push HTML tags to TruQ. Reason: ", e);
                throw e;
            }
            return result;
        }

        @Override
        public void setSqlSession(SqlSession session) {
            // Do nothing in this implementation
        }
    }

    /**
     * Mock Trafficking Implementation for testing purposes
     *
     */
    public static class TFAMockTrafficker implements Trafficker {

        private CampaignManager campaignManager;
        private OauthKey key;
        private Logger log = LoggerFactory.getLogger(this.getClass());
        private SqlSession session;

        public TFAMockTrafficker(CampaignManager campaignManager, OauthKey key) {
            this.campaignManager = campaignManager;
            this.key = key;
        }
        @Override
        public Either<trueffect.truconnect.api.commons.exceptions.business.Error, Boolean>
        callTrafficking(Integer entityId, Integer priorStatusId,
                                    TraffickingArrayOfint selectContacts,
                                    Integer campaignOwner, Integer traffickingUserId){
            return campaignManager.setCampaignStatus((long) entityId, CampaignStatusEnum.AACT,
                    key,  session);
        }

        @Override
        public Boolean pushHtmlTagToTruQ(Integer campaignId, Integer agencyId, String sessionKey){
            // This implementation does nothing
            return Boolean.TRUE;
        }

        @Override
        public void setSqlSession(SqlSession session) {
            this.session = session;
        }
    }
}

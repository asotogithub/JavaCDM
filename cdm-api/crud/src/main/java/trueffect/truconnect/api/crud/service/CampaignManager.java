package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.model.dto.CampaignDetailsDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupDtoForCampaigns;
import trueffect.truconnect.api.commons.model.enums.CampaignStatusEnum;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.BrandDao;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CookieDomainDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.validation.CampaignValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Manager class to abstract away the persistence layers. Also allows for an
 * easy place to integrate several DAOs or Managers.
 */
public class CampaignManager extends AbstractGenericManager {

    private final CampaignDao campaignDao;
    private final AdvertiserDao advertiserDao;
    private final BrandDao brandDao;
    private final CookieDomainDao cookieDomainDao;
    private CampaignValidator validator;
    private CreativeDao creativeDao;

    public CampaignManager(CampaignDao campaignDao,
                           AdvertiserDao advertiserDao,
                           BrandDao brandDao,
                           CookieDomainDao cookieDomainDao,
                           CreativeDao creativeDao,
                           AccessControl accessControl) {
        super(accessControl);
        this.campaignDao = campaignDao;
        this.advertiserDao = advertiserDao;
        this.brandDao = brandDao;
        this.cookieDomainDao = cookieDomainDao;
        this.creativeDao = creativeDao;
        validator = new CampaignValidator();
    }

    public Campaign get(Long id, OauthKey key) throws Exception {
        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException("Campaign's id cannot be null");
        }

        Campaign result = null;
        SqlSession session = campaignDao.openSession();
        try {
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CampaignId", Long.toString(id), key.getUserId()));
            }
            result = campaignDao.get(id, session);
        } finally {
            campaignDao.close(session);
        }
        return result;
    }

    public Campaign create(Campaign campaign, OauthKey key) throws Exception {
        return create(campaign, key, false);
    }

    /**
     * Creates a new Campaign
     * @param campaign The Campaign to create
     * @param key The authorization key of the user creating the Campaign.
     * @param testingMode Determines if the user is creating a Campaign in testing mode.
     * @return A newly created Campaign
     * @throws Exception
     */
    public Campaign create(Campaign campaign, OauthKey key, boolean testingMode) throws Exception {

        // Nullability checks
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }

        String className = campaign.getClass().getSimpleName();
        BeanPropertyBindingResult bpbResult = new BeanPropertyBindingResult(campaign, className);
        validator.validate(campaign, bpbResult);
        if (bpbResult.hasErrors()) {
            throw new ValidationException(bpbResult);
        }

        Campaign result = null;
        SqlSession session = campaignDao.openSession();
        try {
            if (!userValidFor(AccessStatement.AGENCY, campaign.getAgencyId(), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AgencyId", Long.toString(campaign.getAgencyId()), key.getUserId()));
            }
            if (!userValidFor(AccessStatement.ADVERTISER, campaign.getAdvertiserId(), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AdvertiserId", Long.toString(campaign.getAdvertiserId()), key.getUserId()));
            }
            if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(campaign.getBrandId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "BrandId", Long.toString(campaign.getBrandId()), key.getUserId()));
            }
            if (!userValidFor(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS, Collections.singletonList(campaign.getCookieDomainId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
            }

            Long id = campaignDao.getNextId(session);
            String limitAdvertisers = campaignDao.getLimitAdvertisers(key.getUserId(), session);

            Long advertiserId;
            Long errorCode = 0L;
            if (limitAdvertisers.equals(Constants.NO_FLAG)) {
                advertiserId = campaignDao.getAdvertiserId(key.getUserId(), campaign.getBrandId(), session);
            } else {
                advertiserId = campaignDao.getAdvertiserIdElse(key.getUserId(), campaign.getBrandId(), session);
            }

            if (advertiserId != 0) {
                Long campaignCount = campaignDao.getCampaignCount(campaign.getName(), campaign.getBrandId(), session);
                if (campaignCount == 0) {
                    if (id > 0) {
                        campaign.setStartDate(DateConverter.startDate(campaign.getStartDate()));
                        campaign.setEndDate(DateConverter.endDate(campaign.getEndDate()));
                        // Enabling testing mode to generate extra parameters
                        if(testingMode) {
                            campaign.setResourcePathId(campaignDao.getNextId(session));
                        }
                        campaignDao.saveCampaign(id, campaign, advertiserId, key, session);
                    }
                } else {
                    errorCode = -2L;
                }
            } else {
                errorCode = -3L;
            }

            if (errorCode == -2) {
                throw new ConflictException("Campaign name already exists for brand " + campaign.getBrandId());
            } else if (errorCode == -3) {
                throw new SQLException("Security constraint violation - User access "
                        + "rights don't allow campaign creation under selected advertiser.");
            } else {
                //Add default Media Buy Campaign
                Long mediaBuyId = campaignDao.getMediaBuyId(session);
                campaignDao.saveMediaBuy(id, mediaBuyId, campaign, key, session);
                campaignDao.saveMediaBuyCampaign(id, mediaBuyId, key, session);

                //Add Default Creative Group
                campaignDao.saveCreativeGroup(id, key, session);

                result = campaignDao.get(id, session);
                campaignDao.commit(session);
            }
        } catch (Exception e) {
            campaignDao.rollback(session);
            throw e;
        } finally {
            campaignDao.close(session);
        }
        return result;
    }

    public Long getCampaignStatus(Long id, OauthKey key) throws Exception {

        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException("Campaign's id cannot be null");
        }

        Long result = null;
        SqlSession session = campaignDao.openSession();
        try {
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CampaignId", Long.toString(id), key.getUserId()));
            }
            Campaign campaign = campaignDao.get(id, session);
            if (campaign == null) {
                throw new ValidationException("Campaign not found. Id: " + id);
            }
            result = campaignDao.getCampaignStatus(id, session);
        } finally {
            campaignDao.close(session);
        }
        return result;
    }

    /**
     * Updates a {@code Campaign} to a given {@code status}
     * @param id The {@code Campaign} id
     * @param status The {@code status} to update the given {@code Campaign}
     * @param key The user who is updating the status
     * @return true if the status was successfully updated; false otherwise.
     */
    public Either<Error, Boolean> setCampaignStatus(Long id, CampaignStatusEnum status, OauthKey key){
        SqlSession session = campaignDao.openSession();
        Either<Error, Boolean> result = null;
        try {
            result = this.setCampaignStatus(id, status, key, session);
            campaignDao.commit(session);
            return result;
        }  catch (Exception e) {
            log.warn(String.format("Failure to change status of Campaign %s to %s", status, id), e);
            campaignDao.rollback(session);
            Error error= new BusinessError(e.getMessage(), BusinessCode.INTERNAL_ERROR, null);
            return Either.error(error);
        } finally {
            campaignDao.close(session);
        }
    }

    public Either<Error, Boolean> setCampaignStatus(Long id, CampaignStatusEnum status, OauthKey key, SqlSession session) {

        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException("Campaign's id cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status' cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("Oauth key cannot be null");
        }
        if (session == null) {
            throw new IllegalArgumentException("session cannot be null");
        }

        Long statId = null;
        Boolean result = Boolean.FALSE;
        
        //check access control and validations
        if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(id), key.getUserId(), session)) {
            String message = ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CampaignId", Long.toString(id), key.getUserId());
            Error error =
                    new AccessError(message, SecurityCode.ILLEGAL_USER_CONTEXT, key.getUserId());
            return Either.error(
                    error);
        }

        Long statusCode = status.getStatusCode();
        campaignDao.setCampaignStatus(id, statusCode, session);
        if (status == CampaignStatusEnum.AACT) {
            campaignDao.setCampaignStatusIfTrafficked(id, statusCode, session);
            creativeDao.updateCreativeVersionsToTrafficked(id, session);
        }
        log.debug("User {} updated Campaign status to: {} ({}) ", key.getUserId(), status, statusCode);
        return Either.success(Boolean.TRUE);
    }

    public CampaignDetailsDTO getDetails(Long id, OauthKey key) throws Exception {

        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException("Campaign's id cannot be null");
        }

        // Start the session
        SqlSession session = campaignDao.openSession();
        CampaignDetailsDTO result = null;
        try {

            // check access control
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CampaignId", Long.toString(id), key.getUserId()));
            }
            Campaign campaign = campaignDao.get(id, session);

            if (campaign != null) {
                // Get the advertiser object
                if (!userValidFor(AccessStatement.ADVERTISER, campaign.getAdvertiserId(), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AdvertiserId", Long.toString(campaign.getAdvertiserId()), key.getUserId()));
                }
                Advertiser advertiser = advertiserDao.get(campaign.getAdvertiserId(), session);

                // Get the brand object
                if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(campaign.getBrandId()), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "BrandId", Long.toString(campaign.getBrandId()), key.getUserId()));
                }
                Brand brand = brandDao.get(campaign.getBrandId(), session);

                // Get the domain object
                CookieDomain cookieDomain = null;
                if (campaign.getCookieDomainId() != null) {
                    if (!userValidFor(AccessStatement.COOKIE_DOMAIN, Collections.singletonList(campaign.getCookieDomainId()), key.getUserId(), session)) {
                        throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CookieDomainId", Long.toString(campaign.getCookieDomainId()), key.getUserId()));
                    }
                    cookieDomain = cookieDomainDao.get(campaign.getCookieDomainId(), session);
                }

                // Assemble the result object
                CampaignDTO campaignDTO
                        = new CampaignDTO(campaign, advertiser, brand, cookieDomain);
                result = new CampaignDetailsDTO(campaignDTO, brand, advertiser);
            }
        } finally {
            // Close out the session
            campaignDao.close(session);
        }
        return result;
    }

    private boolean isCookieDomainBeingChanged(Campaign existingCampaign, Campaign newCampaign) {
        return !existingCampaign.getCookieDomainId().equals(newCampaign.getCookieDomainId());
    }

    /**
     * Perform validations needed for saving/updating a campaign.
     *
     * TODO: the validators in the public module should be moved to commons so
     * they could be handled here as well
     *
     * @param campaign Campaign to validate.
     * @param errors Errors object that has already been initialized.
     *
     * @throws Exception
     */
    public void validate(Campaign campaign, Errors errors) throws Exception {

        // Nullability checks
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }

        // Start the session
        SqlSession session = campaignDao.openSession();
        try {
            Campaign existingCampaign = campaignDao.get(campaign.getId(), session);
            // Validate
            if (campaignDao.isDuplicate(campaign, session)) {
                errors.rejectValue("name", ApiValidationUtils.TYPE_INVALID, "Name must be unique within a brand");
            }
            // Check for fields that are not supposed to change after trafficking
            if (existingCampaign.getStatusId() != 1L) {
                if (isCookieDomainBeingChanged(existingCampaign, campaign)) {
                    errors.rejectValue("cookieDomainId", ApiValidationUtils.TYPE_INVALID, "Can not change the cookie domain after the campaign has been trafficked");
                }
            }
        } finally {
            // Close out the session
            campaignDao.close(session);
        }
    }

    /**
     * Update a campaign. Checks are performed to ensure that the user has
     * access to update the campaign with the selections they have chosen.
     *
     * @param campaign Campaign to be updated. It is assumed that the campaign
     * has already been validated.
     * @param key authorization key of the user updating the Campaign.
     * @return the updated Campaign.
     *
     * @throws Exception
     */
    public Campaign update(Campaign campaign, OauthKey key) throws Exception {

        // Nullability checks
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }

        // Validate
        String className = campaign.getClass().getSimpleName();
        BeanPropertyBindingResult bpbResult = new BeanPropertyBindingResult(campaign, className);
        validator.validate(campaign.getId(), campaign, bpbResult);
        validate(campaign, bpbResult);
        if (bpbResult.hasErrors()) {
            throw new ValidationException(bpbResult);
        }
        // Clean up the campaign object
        campaign.setDupName(campaign.getName().toLowerCase());
        campaign.setModifiedTpwsKey(key.getTpws());
        // Start the session
        SqlSession session = campaignDao.openSession();
        Campaign result = null;
        try {
            // Check access
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaign.getId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CampaignId", Long.toString(campaign.getId()), key.getUserId()));
            }
            // Get existing campaign
            Campaign existingCampaign = campaignDao.get(campaign.getId(), session);
            if (existingCampaign == null) {
                throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.NOT_FOUND, "id");
            }

            // Check access
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaign.getId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CampaignId", Long.toString(campaign.getId()), key.getUserId()));
            }
            // check the advertiser object
            campaign.setAdvertiserId(campaign.getAdvertiserId() != null ? campaign.getAdvertiserId() : existingCampaign.getAdvertiserId());
            if (!userValidFor(AccessStatement.ADVERTISER, campaign.getAdvertiserId(), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AdvertiserId", Long.toString(campaign.getAdvertiserId()), key.getUserId()));
            }
            // check the brand object
            campaign.setBrandId(campaign.getBrandId() != null ? campaign.getBrandId() : existingCampaign.getBrandId());
            if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(campaign.getBrandId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "BrandId", Long.toString(campaign.getBrandId()), key.getUserId()));
            }

            // Bypass cookie domain security if there is no change to the cookie domain
            campaign.setCookieDomainId(campaign.getCookieDomainId() != null ? campaign.getCookieDomainId() : existingCampaign.getCookieDomainId());
            if (isCookieDomainBeingChanged(existingCampaign, campaign)) {
                    if (CampaignStatusEnum.typeOf(existingCampaign.getStatusId()) != CampaignStatusEnum.NEW) {
                        // Campaing is already in traffic, changing the cookie domain is not allowed
                        throw new ValidationException(ResourceBundleUtil.getString("campaign.error.alreadyInTrafficUpdateCookie"));
                    } else {
                        if (!userValidFor(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS, Collections.singletonList(campaign.getCookieDomainId()), key.getUserId(), session)) {
                            // User does not have access to the new cookie domain id
                            throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CookieDomainId", Long.toString(campaign.getCookieDomainId()), key.getUserId()));
                    }
                }
            }

            campaign.setStartDate(DateConverter.startDate(campaign.getStartDate()));
            campaign.setEndDate(DateConverter.endDate(campaign.getEndDate()));
            int affected = campaignDao.update(campaign, session);
            if (affected <= 0) {
                campaignDao.rollback(session);
                throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.NOT_FOUND, "id");
            }

            // update overall budget
            // TODO this should go on MediaBuy's DAO
            campaignDao.updateOverallBudget(campaign, session);

            // Retrieve recently updated Campaign
            result = campaignDao.get(campaign.getId(), session);
            log.info(key.toString()+" Updated "+ campaign);
            campaignDao.commit(session);
        } catch (PersistenceException e) {
            log.warn(e.getMessage(),e);
            campaignDao.rollback(session);
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.DUPLICATE, "name");
        } finally {
            campaignDao.close(session);
        }
        return result;
    }

    public RecordSet<CreativeGroupDtoForCampaigns> getCreativeGroupsForCampaign(Long campaignId, Long startIndex,
                                                                                Long pageSize,OauthKey key) throws Exception {
        // Nullability checks
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign's id cannot be null");
        }

        RecordSet<CreativeGroupDtoForCampaigns> result = null;
        SqlSession session = campaignDao.openSession();

        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex",startIndex);
        paginator.put("pageSize",pageSize);
        validatePaginator(paginator);
        try {
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CampaignId", Long.toString(campaignId), key.getUserId()));
            }
            Campaign campaign = campaignDao.get(campaignId, session);
            if (campaign == null) {
                throw new ValidationException("Campaign not found. Id: " + campaignId);
            }
            List<CreativeGroupDtoForCampaigns> creativeGroupList = campaignDao.getCreativeGroupList(campaignId,
                    paginator.get("startIndex"), paginator.get("pageSize"), session);
            Long numberOfRecords = campaignDao.getCountCreativeGroupList (campaignId, session);
            if (creativeGroupList != null) {
                result = new RecordSet<>( paginator.get("startIndex").intValue(),
                        paginator.get("pageSize").intValue(),numberOfRecords.intValue(), creativeGroupList);
            }
        } finally {
            campaignDao.close(session);
        }
        return result;
    }
}
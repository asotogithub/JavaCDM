package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.EmailError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.delivery.TagEmail;
import trueffect.truconnect.api.commons.model.delivery.TagEmailRecipientsWrapper;
import trueffect.truconnect.api.commons.model.delivery.TagEmailResponse;
import trueffect.truconnect.api.commons.model.delivery.TagEmailSite;
import trueffect.truconnect.api.commons.model.delivery.TagEmailSiteResponse;
import trueffect.truconnect.api.commons.model.delivery.TagEmailWrapper;
import trueffect.truconnect.api.commons.model.delivery.TagPlacement;
import trueffect.truconnect.api.commons.model.delivery.TagType;
import trueffect.truconnect.api.commons.model.delivery.enums.TagTypeEnum;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;
import trueffect.truconnect.api.crud.validation.CreativeInsertionFilterParamValidator;
import trueffect.truconnect.api.crud.validation.PackagePlacementValidator;
import trueffect.truconnect.api.crud.validation.PlacementFilterParamValidator;
import trueffect.truconnect.api.external.proxy.TagEmailProxy;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.external.proxy.TagProxy;
import trueffect.truconnect.api.external.proxy.TagTypeProxy;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Abel Soto
 */
public class PlacementManager extends AbstractGenericManager {
    
    private static final String HTML_NO_SCRIPT_COMMENT = "<!-- href/image tag -->";

    private PackagePlacementDaoExtended packagePlacementDao;
    private PlacementDao placementDao;
    private CostDetailDaoExtended costDetailDao;
    private CampaignDao campaignDao;
    private SiteSectionDao siteSectionDao;
    private SizeDao sizeDao;
    private InsertionOrderDao ioDao;
    private ExtendedPropertiesDao extendedPropertiesDao;
    private PackageDaoExtended packageDao;
    private CostDetailDaoBase dimCostDetailDao;
    private UserDao userDao;

    private IOPlacementStatusManager ioPlacementStatusManager;
    private CostDetailManager placementCostDetailManager;

    private PackagePlacementValidator validator;
    private CreativeInsertionFilterParamValidator filterParamValidator;
    private PlacementFilterParamValidator placementFilterParamValidator;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public PlacementManager(PlacementDao placementDao, CostDetailDaoExtended costDetailDao,
                            CampaignDao campaignDao, SiteSectionDao siteSectionDao, SizeDao sizeDao,
                            PlacementStatusDao placementStatusDao, UserDao userDao,
                            ExtendedPropertiesDao extendedPropertiesDao, InsertionOrderDao ioDao,
                            InsertionOrderStatusDao ioStatusDao, PackageDaoExtended packageDao,
                            PackagePlacementDaoExtended packagePlacementDao,
                            CostDetailDaoBase dimCostDetailDao,
                            CreativeInsertionDao creativeInsertionDao,
                            AccessControl accessControl) {
        super(accessControl);
        this.validator = new PackagePlacementValidator();
        this.placementDao = placementDao;
        this.costDetailDao = costDetailDao;
        this.packagePlacementDao = packagePlacementDao;
        this.campaignDao = campaignDao;
        this.siteSectionDao = siteSectionDao;
        this.ioDao = ioDao;
        this.extendedPropertiesDao = extendedPropertiesDao;
        this.sizeDao = sizeDao;
        this.packageDao = packageDao;
        this.dimCostDetailDao = dimCostDetailDao;
        this.userDao = userDao;
        this.ioPlacementStatusManager = new IOPlacementStatusManager(placementStatusDao, ioStatusDao, userDao,
                creativeInsertionDao,
                accessControl);
        this.placementFilterParamValidator = new PlacementFilterParamValidator();
        this.placementCostDetailManager = new CostDetailManager(costDetailDao, dimCostDetailDao,
                CostDetailType.PLACEMENT, accessControl);
        this.filterParamValidator = new CreativeInsertionFilterParamValidator();
    }

    /**
     * Gets an Placement based on its ID
     *
     * @param id the ID of the Placement
     * @param key The OAuthKey that contains the user id executing this method
     * @return the Advertiser
     * @throws java.lang.Exception
     */
    public Placement get(Long id, OauthKey key) throws Exception {
        SqlSession session = placementDao.openSession();
        Placement result;
        try {
            if (!userValidFor(AccessStatement.PLACEMENT,
                    id,
                    key.getUserId(), session)) {
                String message = ResourceBundleUtil.getString("dataAccessControl.notFoundForUserSingleElement",
                        "PlacementId", String.valueOf(id), key.getUserId());
                throw new DataNotFoundForUserException(
                        message);
            }
            result = get(id, session);
        } finally {
            placementDao.close(session);
        }
        return result;
    }

    private Placement get(Long id,SqlSession session) {
        Placement result = placementDao.get(id, session);
        result.setCostDetails(costDetailDao.getAll(id, session));
        return result;
    }

    /**
     * Gets a list of all Placements
     *
     * @param searchCriteria The SearchCriteria to apply
     * @param key The OAuthKey that contains the user id executing this method
     * @return The list of all Placements.
     * @throws java.lang.Exception
     */
    public RecordSet<Placement> getByCriteria(SearchCriteria searchCriteria, OauthKey key) throws Exception {
        //validations
        if (searchCriteria == null) {
            throw new IllegalArgumentException("Search Criteria cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        if (searchCriteria.getPageSize() > 1000) {
            throw new Exception("The page size allows up to 1000 records.");
        }
        if (searchCriteria.getStartIndex() < 0) {
            throw new Exception("Cannot retrieve records for start index: " + searchCriteria.getStartIndex() + ". The minimum start index is: 0");
        }

        SqlSession session = placementDao.openSession();
        RecordSet<Placement> result;
        try {
            result = placementDao.getPlacements(searchCriteria, key, session);
            for (Placement placement : result.getRecords()) {
                placement.setCostDetails(costDetailDao.getAll(placement.getId(), session));
            }
        } finally {
            placementDao.close(session);
        }
        return result;
    }

    /**
     * Creates a standalone Placement
     *
     * @param placement Placement object to be saved
     * @param key The OAuthKey that contains the user id executing this method
     * @return The Placement already created on the database.
     * @throws java.lang.Exception
     */
    public Placement create(Placement placement, OauthKey key) throws Exception {
        // Nullability checks
        if (placement == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.placement")));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        // model validation
        String className = placement.getClass().getSimpleName();
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(placement, className);
        validator.validateStandalonePlacementForCreate(placement, validationResult);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        // Calculate inventory
        Long inventory = PackagePlacementUtil.calculateInventory(placement.getRate(),
                placement.getAdSpend(), placement.getRateType());
        placement.setInventory(inventory);

        Placement result = null;
        SqlSession session = placementDao.openSession();
        SqlSession dimSession = dimCostDetailDao.openSession();
        try {
            // get Contact User
            User user = userDao.get(key.getUserId(), session);
            placement.setStartDate(DateConverter.startDate(placement.getStartDate()));
            placement.setEndDate(DateConverter.endDate(placement.getEndDate()));
            result = create(placement, true, user.getContactId(), key, session, dimSession);
            placementDao.commit(session);
            dimCostDetailDao.commit(dimSession);
        } catch (Exception e) {
            placementDao.rollback(session);
            dimCostDetailDao.rollback(dimSession);
            throw e;
        } finally {
            placementDao.close(session);
            dimCostDetailDao.close(dimSession);
        }
        log.info("{} Saved {}", key.toString(), placement);
        return result;
    }

    /**
     * Performs access validations and creates a new Placement.
     *
     * @param placement Placement object to be created
     * @param createCostDetails For placementStandalone = TRUE for PackagePlacement = FALSE
     * @param userContactId
     * @param key
     * @param session
     * @param dimSession
     * @return the new Placement created on the data base
     * @throws java.lang.Exception
     */
    public Placement create(Placement placement, boolean createCostDetails, Long userContactId, OauthKey key,
                            SqlSession session, SqlSession dimSession) throws Exception {
        // Nullability checks
        if (placement == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.placement")));
        }
        if (userContactId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "User Contact Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "DIM Session"));
        }

        //Check Access Control
        if (!userValidFor(AccessStatement.INSERTION_ORDER, Collections.singletonList(placement.getIoId()), key.getUserId(), session)) {
            throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
        }
        if (!userValidFor(AccessStatement.SITE, Collections.singletonList(placement.getSiteId()), key.getUserId(), session)) {
            throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteId", 
                    Long.toString(placement.getSiteId()), key.getUserId()));
        }

        // find the AdSize.
        log.debug("Looking for Size with dimensions: width -> {}, height -> {}",
                placement.getWidth(), placement.getHeight());
        Size size = sizeDao.getByUserAndDimensions(placement.getHeight(), placement.getWidth(), key,
                session);
        if (size == null) {
            // We need the agencyId that the placement belongs to
            log.debug("Looking for Campaign's agency");
            Campaign campaign = campaignDao.get(placement.getCampaignId(), session);

            log.debug("Creating a new Size");
            Size sizeAux = new Size();
            sizeAux.setHeight(placement.getHeight());
            sizeAux.setWidth(placement.getWidth());
            sizeAux.setAgencyId(campaign.getAgencyId());
            sizeAux.setLabel(placement.getWidth() + "x" + placement.getHeight());
            sizeAux.setCreatedTpwsKey(key.getTpws());
            size = sizeDao.create(sizeAux, session);
        }
        placement.setSizeId(size.getId());

        // Before the placement gets created, check "Site Section" and create it if it does not exist.
        SiteSection siteSection = null;
        if (placement.getSiteSectionId() != null) {
            siteSection = siteSectionDao.get(placement.getSiteSectionId(), session);
        }
        if (siteSection == null) {
            siteSection = new SiteSection();
            String name = placement.getName().length() > Constants.MAX_SITE_SECTION_NAME_LENGTH ? placement.getName().substring(0, 150) : placement.getName();
            name = name + (new Date()).getTime();
            siteSection.setName(name);
            siteSection.setSiteId(placement.getSiteId());
            siteSection.setCreatedTpwsKey(key.getTpws());
            log.debug("Saving SiteSection: {} for placement.", siteSection);
            siteSection = siteSectionDao.create(siteSection, session);
        }
        placement.setSiteSectionId(siteSection.getId());

        // set default values and persist Placement + status + costDetails
        validator.applyDefaults(placement);
        Placement result = createPlacement(placement, createCostDetails, userContactId, key, session, dimSession);
        return result;
    }

    /**
     * Set Default values and creates a new Placement with data already validated.
     *
     * @param placement Placement object to be created
     * @param createCostDetails For placementStandalone = TRUE for PackagePlacement = FALSE
     * @param userContactId
     * @param key
     * @param session
     * @param dimSession
     * @return the new Placement created on the data base
     */
    public Placement createPlacement(Placement placement, boolean createCostDetails, Long userContactId,
                                     OauthKey key, SqlSession session, SqlSession dimSession) {
        // Nullability checks
        if (placement == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.placement")));
        }
        if (userContactId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", " User Contact Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "DIM Session"));
        }

        // Set default values
        placement.setStartDate(DateConverter.startDate(placement.getStartDate()));
        placement.setEndDate(DateConverter.endDate(placement.getEndDate()));
        placement.setCreatedTpwsKey(key.getTpws());
        // Call dao to create placement
        Placement result = this.placementDao.create(placement, session);

        // Save placement's status
        ioPlacementStatusManager.createPlacementStatus(result.getId(), placement.getStatus(),
                userContactId, key, session);

        // Saving the ExternalPlacementId
        if (placement.getExternalId() != null) {
            extendedPropertiesDao.updateExternalId("Placement", "MediaID", result.getId(), placement.getExternalId(), session);
        }

        placement.setId(result.getId());
        result = placementDao.get(placement.getId(), session);

        // Save Placement Cost Detail (Only create)
        if (createCostDetails) {
            List<CostDetail> costDetails = placementCostDetailManager
                    .updateCostDetails(placement.getCostDetails(), result.getId(), key, session,
                            dimSession);
            result.setCostDetails(costDetails);
        }
        return result;
    }

    /**
     * Updates a given Placement
     *
     * @param id The ID of the Placement
     * @param placement Placement object to be updated
     * @param key The OAuthKey that contains the user id executing this method
     * @return The Updated Placement.
     * @throws java.lang.Exception
     */
    public Placement update(Long id, Placement placement, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (placement == null) {
            throw new IllegalArgumentException("Placement cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        String className = placement.getClass().getSimpleName();
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(placement, className);
        validator.validatePlacementForUpdate(id, placement, validationResult);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        SqlSession session = placementDao.openSession();
        SqlSession dimSession = dimCostDetailDao.openSession();
        Placement result = null;
        try {
            if (!userValidFor(AccessStatement.PLACEMENT, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException("PlacementId: " + id + " Not found for User: " + key.getUserId());
            }

            Placement existentPlacement = get(placement.getId(), session);

            if (Constants.YES_FLAG.equalsIgnoreCase(existentPlacement.getIsScheduled())) {
                // If Scheduled; then, copy all Placement properties from DB except the status.
                String userStatus = placement.getStatus();
                copyPlacement(placement, existentPlacement);
                placement.setStatus(userStatus);
            }
            // Data check to validate if give Package Id (if provided) matches the one
            // in the Placement just read from DB
            boolean isStandalone = existentPlacement.getPackageId() == null;
            if(!isStandalone && !existentPlacement.getPackageId().equals(placement.getPackageId())){
                String message = ResourceBundleUtil.getString(
                        "packagePlacement.error.placementCannotUpdatePackageAssociation",
                        String.valueOf(placement.getId()));
                throw new ValidationException(message);
            }

            if (placement.getStartDate() == null || placement.getEndDate() == null) {
                placement.setStartDate(placement.getStartDate() == null ? existentPlacement.getStartDate() : placement.getStartDate());
                placement.setEndDate(placement.getEndDate() == null ? existentPlacement.getEndDate() : placement.getEndDate());
                if (placement.getStartDate().compareTo(placement.getEndDate()) > 0) {
                    throw new ValidationException("End Date is less than Start Date; Start Date:"
                            + placement.getStartDate() + ", End Date:" + placement.getEndDate());
                }
            }

            //validates only if the status is changed 
            if (!existentPlacement.getStatus().equals(placement.getStatus())) {
                validator.validateChangeStatus(placement, existentPlacement, validationResult);
                if (validationResult.hasErrors()) {
                    throw new ValidationException(validationResult);
                }
            }

            placement.setSizeId(
                    placement.getSizeId() == null ? existentPlacement.getSizeId() : placement
                            .getSizeId());
            placement.setUtcOffset(existentPlacement.getUtcOffset());
            placement.setSmEventId(existentPlacement.getSmEventId());
            placement.setCountryCurrencyId(existentPlacement.getCountryCurrencyId());
            placement.setIsSecure(existentPlacement.getIsSecure());
            placement.setModifiedTpwsKey(key.getTpws());
            placement.setStartDate(DateConverter.startDate(placement.getStartDate()));
            placement.setEndDate(DateConverter.endDate(placement.getEndDate()));
            if(isStandalone) {
                placement.setInventory(PackagePlacementUtil.calculateInventory(placement.getCostDetails().get(0).getPlannedGrossRate(),
                        placement.getCostDetails().get(0).getPlannedGrossAdSpend(), RateTypeEnum.typeOf(placement.getCostDetails().get(0).getRateType()).name()));
                placement.setRateType(RateTypeEnum.typeOf(placement.getCostDetails().get(0).getRateType()).name());
                placement.setRate(placement.getCostDetails().get(0).getPlannedGrossRate());
            } else {
                placement.setInventory(null);
                placement.setRateType(null);
                placement.setRate(null);
            }
            // Update placement data
            result = placementDao.update(placement, session);

            // Update Status if it was provided by the user
            if (placement.getStatus() != null) {
                InsertionOrder io = ioDao.get(result.getIoId(), session);
                User user = userDao.get(key.getUserId(), session);
                Map<Long, Placement> map = new HashMap<>();
                map.put(placement.getId(), placement);
                this.updatePlacementsStatus(io,
                        Collections.singletonList(existentPlacement),
                        map,
                        user.getContactId(),
                        key,
                        session);
            }

            // Updating external Id
            extendedPropertiesDao.updateExternalId("Placement", "MediaID", result.getId(), placement.getExternalId(), session);

            // update Cost Details
            if(isStandalone) {
                placementCostDetailManager.updateCostDetails(placement.getCostDetails(), placement.getId(), key, session, dimSession);
            }

            result = get(id, session);
            placementDao.commit(session);
            dimCostDetailDao.commit(dimSession);
        } catch (Exception e) {
            log.warn(String.format("Exception while updating Placement %s", id), e);
            placementDao.rollback(session);
            dimCostDetailDao.rollback(dimSession);
            throw e;
        } finally {
            placementDao.close(session);
            dimCostDetailDao.close(dimSession);
        }
        log.debug("Placement with id {} has been updated ", id);
        return result;
    }

    private void copyPlacement(Placement to, Placement from) {
        to.setId(from.getId());
        to.setIoId(from.getIoId());
        to.setSiteId(from.getSiteId());
        to.setSiteSectionId(from.getSiteSectionId());
        to.setSizeId(from.getSizeId());
        to.setStartDate(from.getStartDate());
        to.setEndDate(from.getEndDate());
        to.setInventory(from.getInventory());
        to.setRate(from.getRate());
        to.setRateType(from.getRateType());
        to.setMaxFileSize(from.getMaxFileSize());
        to.setIsSecure(from.getIsSecure());
        to.setLogicalDelete(from.getLogicalDelete());
        to.setCreatedTpwsKey(from.getCreatedTpwsKey());
        to.setModifiedTpwsKey(from.getModifiedTpwsKey());
        to.setCreatedDate(from.getCreatedDate());
        to.setModifiedDate(from.getModifiedDate());
        to.setIsTrafficked(from.getIsTrafficked());
        to.setResendTags(from.getResendTags());
        to.setUtcOffset(from.getUtcOffset());
        to.setSmEventId(from.getSmEventId());
        to.setCountryCurrencyId(from.getCountryCurrencyId());
        to.setName(from.getName());
        to.setExtProp1(from.getExtProp1());
        to.setExtProp2(from.getExtProp2());
        to.setExtProp3(from.getExtProp3());
        to.setExtProp4(from.getExtProp4());
        to.setExtProp5(from.getExtProp5());
        to.setWidth(from.getWidth());
        to.setHeight(from.getHeight());
        to.setCampaignId(from.getCampaignId());
        to.setStatus(from.getStatus());
        to.setExternalId(from.getExternalId());
        to.setPackageId(from.getPackageId());
        to.setSiteName(from.getSiteName());
        to.setSizeName(from.getSizeName());
        to.setCostDetails(from.getCostDetails());
        to.setAdSpend(from.getAdSpend());
        to.setIsScheduled(from.getIsScheduled());
        to.setSectionName(from.getSectionName());
    }

    public Placement updateOnImport(Placement placement, boolean updateCostDetails, OauthKey key, SqlSession session, SqlSession dimSession) {
        // Nullability checks
        if (placement == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.placement")));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "DIM Session"));
        }

        // Set default values
        placement.setModifiedTpwsKey(key.getTpws());
        // Call dao to create placement
        Placement result = this.placementDao.updateOnImport(placement, session);

        // Saving the ExternalPlacementId
        if (placement.getExternalId() != null) {
            extendedPropertiesDao.updateExternalId("Placement", "MediaID", result.getId(), placement.getExternalId(), session);
        }

        //placement.setId(result.getId());
        result = placementDao.get(placement.getId(), session);

        // Update the Placement Cost Detail
        if (updateCostDetails) {
            List<CostDetail> costDetails = placementCostDetailManager
                    .updateCostDetailsOnImport(placement.getCostDetails(), result.getId(), key, session,
                            dimSession);
            result.setCostDetails(costDetails);
        }
        return result;
    }

    /**
     * Deletes a Placement.
     *
     * @param id The ID of the Placement
     * @param key Session ID of the user who updates the Placement.
     * @throws java.lang.Exception
     */
    public void remove(Long id, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        SqlSession session = placementDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.PLACEMENT, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException("PlacementId: " + id + DataNotFoundForUserException.NOT_FOUND_MESSAGE + key.getUserId());
            }
            this.placementDao.delete(id, key, session);
            placementDao.commit(session);
        } catch (PersistenceException e) {
            placementDao.rollback(session);
            throw e;
        } finally {
            placementDao.close(session);
        }
        log.info(key.toString() + " Deleted " + id);
    }

    /**
     * Is valid placement for agency.
     *
     * @param id The ID of the Placement
     * @param key Session ID of the user who updates the Placement.
     * @throws java.lang.Exception
     */
    public void isValidForAgency(Long id, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        SqlSession session = placementDao.openSession();
        try {
            if (!userValidFor(AccessStatement.PLACEMENT, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException("PlacementId: " + Collections.singletonList(id).toString() + DataNotFoundForUserException.NOT_FOUND_MESSAGE + key.getUserId());
            }
        } finally {
            placementDao.close(session);
        }
    }

    public List<Placement> createStandalonePlacements(Long ioId, List<Placement> placements, Long userContactId, OauthKey key,
                                                      SqlSession session, SqlSession dimSession) throws Exception {
        // Nullability checks
        if (ioId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "InsertionOrder Id"));
        }
        if (placements == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Placements"));
        }
        if (userContactId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "User Contact Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "DIM Session"));
        }

        List<Placement> result = new ArrayList<>();
        CostDetail costDetail;
        Long inventory;

        //only create new placements
        for (Placement placement : placements) {
            //validations
            if (!ioId.equals(placement.getIoId())) {
                throw new ValidationException("IO's id in request body does not match the requested id.");
            }
            BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(placement, "placement");
            validator.validateStandalonePlacementForCreate(placement, validationResult);
            if (validationResult.hasErrors()) {
                throw new ValidationException(validationResult);
            }
            costDetail = placement.getCostDetails().iterator().next();
            inventory = PackagePlacementUtil.calculateInventory(costDetail.getPlannedGrossRate(),
                    costDetail.getPlannedGrossAdSpend(), RateTypeEnum.typeOf(costDetail.getRateType()).name());
            placement.setInventory(inventory);
            placement.setStatus(InsertionOrderStatusEnum.NEW.getName());

            Placement newPlacement = this.create(placement, true, userContactId, key, session, dimSession);
            // relationship package-placement
            result.add(newPlacement);
        }
        return result;
    }

    /**
     * Updates a set of packages and its placements
     *
     * @param packageId The Package ID
     * @param ioId The io ID
     * @param placements The {@code RecordSet} of {@code Placement}s
     * @param key Session ID of the user who updates the Placement.
     * @return a recordSet of PackageDTOs persisted
     */
    public RecordSet<Placement> addNewPlacementsToPackage(Long packageId, Long ioId,
                                                          RecordSet<Placement> placements,
                                                          OauthKey key) {
        // Nullability checks 
        if (packageId == null) {
            throw new IllegalArgumentException("Package ID cannot be null");
        }
        if (ioId == null) {
            throw new IllegalArgumentException("IO ID cannot be null");
        }
        if (placements == null) {
            throw new IllegalArgumentException("PlacementDTO RecordSet cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }
        // Validate payload
        validator.validatePlacementsToCreateBelowExistingPackage(placements.getRecords());

        //Session
        SqlSession session = placementDao.openSession();
        SqlSession dimSession = dimCostDetailDao.openSession();
        List<Placement> result;
        try {
            //check access control
            if (!userValidFor(AccessStatement.PACKAGE, Collections.singletonList(packageId),
                    key.getUserId(), session)) {
                throw BusinessValidationUtil
                        .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            }
            if (!userValidFor(AccessStatement.INSERTION_ORDER, Collections.singletonList(ioId),
                    key.getUserId(), session)) {
                throw BusinessValidationUtil
                        .buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }

            //Obtain Placement's package
            Package existentPackage = packageDao.get(packageId, session);
            if (existentPackage == null) {
                throw BusinessValidationUtil
                        .buildBusinessSystemException(BusinessCode.NOT_FOUND, "packageId");
            }
            //validate IoId and CampaignId from payload
            for (Placement placementToCreate : placements.getRecords()) {
                if (!placementToCreate.getIoId().equals(ioId)) {
                    throw BusinessValidationUtil
                            .buildBusinessSystemException(BusinessCode.INVALID, "ioId");
                }
                if (!placementToCreate.getCampaignId().equals(existentPackage.getCampaignId())) {
                    throw BusinessValidationUtil
                            .buildBusinessSystemException(BusinessCode.INVALID, "campaignId");
                }
            }

            // get Contact User
            User user = userDao.get(key.getUserId(), session);
            CostDetail pkgCostDetail = existentPackage.getCostDetails().get(0);

            // create new placements
            addNewPlacementsToPackage(existentPackage.getId(), existentPackage.getPlacementCount(),
                    pkgCostDetail, ioId, placements.getRecords(), user.getContactId(), key, session,
                    dimSession);

            // get all placements
            result = getPlacementsByPackage(packageId, key, session);
            placementDao.commit(session);
            dimCostDetailDao.commit(session);
        } catch (SystemException e) { // Check for validation exceptions part of Package
            placementDao.rollback(session);
            dimCostDetailDao.rollback(session);
            throw e;
        } finally {
            placementDao.close(session);
            dimCostDetailDao.close(session);
        }
        return new RecordSet<>(0, 0, result.size(), result);
    }

    /**
     * Creates new placements under an existent Package
     *
     * @param packageId
     * @param existingPlacements
     * @param key
     * @param session
     * @param pkgCostDetail
     * @param contactId
     * @param ioId
     * @param placements
     * @param dimSession
     */
    public void addNewPlacementsToPackage(Long packageId, Long existingPlacements,
                                          CostDetail pkgCostDetail, Long ioId,
                                          List<Placement> placements, Long contactId, OauthKey key,
                                          SqlSession session, SqlSession dimSession) {
        // Nullability checks
        if (packageId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Package id"));
        }
        if (existingPlacements == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "ExistingPlacements"));
        }
        if (pkgCostDetail == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "CostDetail"));
        }
        if (ioId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "InsertionOrder Id"));
        }
        if (placements == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Placements"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Sql Session"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Sql Dim Session"));
        }

        //inventory  --> divided by the number of placements in the package
        //rateType, rate and flightDates --> copied to all placements from package
        Long inventory = PackagePlacementUtil.inventoryPlacement(pkgCostDetail.getInventory(),
                placements.size() + existingPlacements.intValue());
        String pkgRateType = RateTypeEnum.typeOf(pkgCostDetail.getRateType()).toString();
        Date pkgCostDetailStartDate = DateConverter.startDate(pkgCostDetail.getStartDate());
        Date pkgCostDetailEndDate = DateConverter.startDate(pkgCostDetail.getEndDate());

        List<PackagePlacement> pkgPlacements = new ArrayList<>();
        for (Placement toCreate : placements) {
            //Set CostDetail Values from packageCostDetails
            toCreate.setRateType(pkgRateType);
            toCreate.setRate(pkgCostDetail.getPlannedGrossRate());
            toCreate.setStartDate(pkgCostDetailStartDate);
            toCreate.setEndDate(pkgCostDetailEndDate);
            toCreate.setInventory(inventory);

            toCreate.setStatus(InsertionOrderStatusEnum.NEW.getName());
            //create a placement
            Placement placementCreated;
            try {
                placementCreated = create(toCreate, false, contactId, key, session, dimSession);
            } catch (DataNotFoundForUserException e) {
                throw BusinessValidationUtil
                        .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            } catch (Exception e) {
                throw BusinessValidationUtil
                        .buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, "");
            }

            //structure to create associations
            pkgPlacements.add(new PackagePlacement(packageId, placementCreated.getId()));
        }

        //create relationship package-placement
        packagePlacementDao.create(pkgPlacements, key, session);

        //update costDetails for old existent Placements
        if (existingPlacements != 0) {
            Placement dataToUpdate = new Placement();
            dataToUpdate.setInventory(inventory);
            dataToUpdate.setModifiedTpwsKey(key.getTpws());
            int rowsUpdated = placementDao
                    .updateDataCostDetailPlacementsByPackageId(dataToUpdate, packageId, session);
            if (rowsUpdated <= 0) {
                if (rowsUpdated != BatchExecutor.BATCH_UPDATE_RETURN_VALUE) {
                    throw BusinessValidationUtil
                            .buildBusinessSystemException(BusinessCode.NOT_FOUND, "packageId");
                }
            } else if (rowsUpdated != (placements.size() + existingPlacements)) {
                throw BusinessValidationUtil
                        .buildBusinessSystemException(BusinessCode.INTERNAL_ERROR, "");
            }
        }
    }

    public RecordSet<Placement> updatePlacementStatus(Long ioId, RecordSet<Placement> placementsRecordSet, OauthKey key) {
        //validations
        // Nullability checks 
        if (ioId == null) {
            throw new IllegalArgumentException("IO's id cannot be null");
        }
        if (placementsRecordSet == null) {
            throw new IllegalArgumentException("Placement RecordSet cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }
        //Session
        SqlSession session = placementDao.openSession();
        RecordSet<Placement> result = new RecordSet<>();
        try {
            result = updatePlacementStatus(ioId, placementsRecordSet, key, session);
            placementDao.commit(session);
        } catch (SystemException e) {
            placementDao.rollback(session);
            throw e;
        } catch (Exception e) {
            log.warn("Unexpected exception happened", e);
            placementDao.rollback(session);
        } finally {
            placementDao.close(session);
        }
        return result;
    }

    /**
     * Get a RecocrdSet of PlacementView in order to create new Creative
     * Insertions
     *
     * @param campaignId
     * @param filterParam
     * @param key
     * @return
     */
    public Either<Errors, RecordSet<PlacementView>> getPlacementsByCreativeInsertionFilterParam(Long campaignId, CreativeInsertionFilterParam filterParam, OauthKey key) {

        if (campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Campaign Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();
        // Validate payload
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(filterParam, "creativeInsertionFilterParam");
        filterParamValidator.validateToGetPlacements(filterParam, validationResult);
        if (validationResult.hasErrors()) {
            errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(validationResult));
            return Either.error(errors);
        }

        RecordSet<PlacementView> result = null;
        SqlSession session = placementDao.openSession();
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // check access control into payload
            if (filterParam != null
                    && filterParam.getPivotType() != null && !filterParam.getPivotType().isEmpty()
                    && filterParam.getType() != null && !filterParam.getType().isEmpty()) {
                List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelTypesToValidate = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.
                        get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, filterParam.getPivotType().toUpperCase())).
                        get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, filterParam.getType().toUpperCase()));
                AccessStatement accessStatement;
                Long id;
                for (CreativeInsertionFilterParamTypeEnum levelType : hierarchyLevelTypesToValidate) {
                    accessStatement = null;
                    id = null;
                    switch (levelType) {
                        case CREATIVE:
                            accessStatement = AccessStatement.CREATIVE;
                            id = filterParam.getCreativeId();
                            break;
                        case GROUP:
                            accessStatement = AccessStatement.CREATIVE_GROUP;
                            id = filterParam.getGroupId();
                            break;
                        case PLACEMENT:
                            accessStatement = AccessStatement.PLACEMENT;
                            id = filterParam.getPlacementId();
                            break;
                        case SECTION:
                            accessStatement = AccessStatement.SITE_SECTION;
                            id = filterParam.getSectionId();
                            break;
                        case SITE:
                            accessStatement = AccessStatement.SITE;
                            id = filterParam.getSiteId();
                            break;
                    }
                    if (!userValidFor(accessStatement, Collections.singletonList(id), key.getUserId(), session)) {
                        AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                                SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                        errors.addError(error);
                        return Either.error(errors);
                    }
                }
            }

            // Business Logic
            List<PlacementView> placements = placementDao.getPlacementsByFilterParam(campaignId,
                    filterParam, key.getUserId(), session);
            Long counterResult = placementDao.getCountPlacementsByFilterParam(campaignId,
                    filterParam, session);
            result = new RecordSet<>(SearchCriteria.SEARCH_CRITERIA_START_INDEX, SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE, counterResult.intValue(), placements);

        } finally {
            placementDao.close(session);
        }
        return Either.success(result);
    }

    /**
     * Get a RecocrdSet of PlacementView in order to create new Creative
     * Insertions
     *
     * @param agencyId
     * @param advertiserId
     * @param brandId
     * @param filterParam
     * @param startIndex
     * @param pageSize
     * @param key
     * @return
     */
    public Either<Errors, RecordSet<PlacementView>> getPlacementsByPlacementFilterParam(
            Long agencyId, Long advertiserId, Long brandId, PlacementFilterParam filterParam,
            Long startIndex, Long pageSize, OauthKey key) {

        // Nullability checks
        if (agencyId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Agency Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();
        if (advertiserId == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "AdvertiserId"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);

        }
        if (brandId == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "BrandId"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);

        }
        if (filterParam == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "Filter Param"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);

        }
        // Validate payload
        BeanPropertyBindingResult validationResult =
                new BeanPropertyBindingResult(filterParam, "placementFilterParam");
        ValidationUtils
                .invokeValidator(placementFilterParamValidator, filterParam, validationResult);
        if (validationResult.hasErrors()) {
            errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(validationResult));
            return Either.error(errors);
        }

        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex", startIndex);
        paginator.put("pageSize", pageSize);
        Either<Error, HashMap<String, Long>> validPaginator = validatePaginator(paginator);
        if (validPaginator.isError()) {
            errors.addError(validPaginator.error());
            return Either.error(errors);
        } else {
            paginator = validPaginator.success();
        }

        RecordSet<PlacementView> result = null;
        SqlSession session = placementDao.openSession();
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            if (!userValidFor(AccessStatement.ADVERTISER, Collections.singletonList(advertiserId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(brandId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            // check access control into payload
            Either<AccessError, Void> accessControlResult =
                    checkAccessControlPlacementFilterParam(filterParam,
                            key.getUserId(), session);
            if (accessControlResult != null && accessControlResult.isError()) {
                errors.addError(accessControlResult.error());
                return Either.error(errors);
            }

            // Business Logic
            PlacementFilterParamLevelTypeEnum levelType = PlacementFilterParamLevelTypeEnum
                    .valueOf(filterParam.getLevelType().toUpperCase());
            List<PlacementView> placements = placementDao.getPlacementsViewByLevelType(agencyId,
                    advertiserId, brandId, filterParam, levelType, paginator.get("startIndex"),
                    paginator.get("pageSize"), session);
            int numberOfRecords = placementDao.getCountPlacementsViewByLevelType(agencyId,
                    advertiserId, brandId, filterParam, levelType, session).intValue();
            result = new RecordSet<>(paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(), numberOfRecords, placements);

        } finally {
            placementDao.close(session);
        }
        return Either.success(result);
    }

    /**
     * Get a RecocrdSet of PlacementView result of a search
     *
     * @param agencyId
     * @param advertiserId
     * @param brandId
     * @param searchOptions
     * @param pattern
     * @param startIndex
     * @param pageSize
     * @param key
     * @return
     */
    public Either<Errors, RecordSet<PlacementView>> searchPlacementsByPattern(Long agencyId,
                                                                              Long advertiserId,
                                                                              Long brandId,
                                                                              String pattern,
                                                                              PlacementSearchOptions searchOptions,
                                                                              Long startIndex,
                                                                              Long pageSize,
                                                                              OauthKey key) {

        if (agencyId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Agency Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        Errors errors = new Errors();
        if (advertiserId == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "Advertiser Id"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }
        if (brandId == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "Brand Id"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }
        if (StringUtils.isBlank(pattern)) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "Pattern"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }
        if (searchOptions == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "Search Options"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }
        if (pattern.length() > Constants.SEARCH_PATTERN_MAX_LENGTH) {
            Error error = new Error(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Pattern", Constants.SEARCH_PATTERN_MAX_LENGTH),
                    ValidationCode.INVALID);
            errors.addError(error);
            return Either.error(errors);
        }

        // Validate paginator
        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex", startIndex);
        paginator.put("pageSize", pageSize);
        Either<Error, HashMap<String, Long>> validPaginator = validatePaginator(paginator);
        if (validPaginator.isError()) {
            errors.addError(validPaginator.error());
            return Either.error(errors);
        } else {
            paginator = validPaginator.success();
        }

        RecordSet<PlacementView> result = null;
        SqlSession session = placementDao.openSession();
        try {
            //  Check DAC
            if (!userValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            if (!userValidFor(AccessStatement.ADVERTISER, Collections.singletonList(advertiserId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(brandId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // Business Logic
            pattern = "%" + pattern + "%";
            List<PlacementView> placements = placementDao
                    .searchPlacementsViewByPattern(agencyId, advertiserId, brandId, pattern,
                            searchOptions, paginator.get("startIndex"), paginator.get("pageSize"),
                            session);
            Long counterResult = placementDao
                    .searchCountPlacementsViewByPattern(agencyId, advertiserId, brandId, pattern,
                            searchOptions, session);

            result = new RecordSet<>(
                    paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(),
                    counterResult.intValue(),
                    placements);
        } finally {
            placementDao.close(session);
        }
        return Either.success(result);
    }

    public Either<Errors, RecordSet<PlacementView>> getPlacementsAssociatedInjectionTag(Long tagId,
                                                                                        Long startIndex,
                                                                                        Long pageSize,
                                                                                        OauthKey key) {
        // Nullability checks
        if (tagId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Html Injection Tag Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();
        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex", startIndex);
        paginator.put("pageSize", pageSize);
        Either<Error, HashMap<String, Long>> validPaginator = validatePaginator(paginator);

        if (validPaginator.isError()) {
            errors.addError(validPaginator.error());
            return Either.error(errors);
        } else {
            paginator = validPaginator.success();
        }

        //Obtain session
        List<PlacementView> result = null;
        SqlSession session = placementDao.openSession();
        int numberOfRecords = 0;
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.HTML_INJECTION, Collections.singletonList(tagId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            result = placementDao.getPlacementsByTagId(tagId, paginator.get("startIndex"),
                    paginator.get("pageSize"), session);
            numberOfRecords = placementDao.getCountPlacementsByTagId(tagId,
                    session).intValue();
        } finally {
            placementDao.close(session);
        }
        RecordSet<PlacementView> res = new RecordSet<>(paginator.get("startIndex").intValue(),
                paginator.get("pageSize").intValue(), numberOfRecords, result);
        return Either.success(res);
    }

    public Either<Errors, RecordSet<PlacementView>> searchPlacementsAssociatedInjectionTagByPattern(
            Long tagId, String pattern, PlacementSearchOptions searchOptions, Long startIndex,
            Long pageSize, OauthKey key) {

        // Nullability checks
        if (tagId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Html Injection Tag Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();
        if (StringUtils.isBlank(pattern)) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "Pattern"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }
        if (searchOptions == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "Search Options"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }
        if (searchOptions.isSection()) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.invalid", "soSection"),
                    ValidationCode.INVALID);
            errors.addError(error);
            return Either.error(errors);
        }
        if (pattern.length() > Constants.SEARCH_PATTERN_MAX_LENGTH) {
            ValidationError error = new ValidationError(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Pattern", Constants.SEARCH_PATTERN_MAX_LENGTH),
                    ValidationCode.INVALID);
            errors.addError(error);
            return Either.error(errors);
        }

        // Validate paginator
        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex", startIndex);
        paginator.put("pageSize", pageSize);
        Either<Error, HashMap<String, Long>> validPaginator = validatePaginator(paginator);
        if (validPaginator.isError()) {
            errors.addError(validPaginator.error());
            return Either.error(errors);
        } else {
            paginator = validPaginator.success();
        }

        //Obtain session
        RecordSet<PlacementView> result = null;
        SqlSession session = placementDao.openSession();
        int numberOfRecords = 0;
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.HTML_INJECTION, Collections.singletonList(tagId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // Business Logic
            pattern = "%" + pattern + "%";
            List<PlacementView> view = placementDao.searchPlacementsAssociatedTagByPattern(tagId,
                    pattern, searchOptions, paginator.get("startIndex"), paginator.get("pageSize"),
                    session);
            numberOfRecords = placementDao.getCountSearchPlacementsAssociatedTagByPattern(tagId,
                    pattern, searchOptions, session).intValue();
            result = new RecordSet<>(paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(), numberOfRecords, view);
        } finally {
            placementDao.close(session);
        }
        return Either.success(result);
    }

    private boolean arePlacementsRejected(Long ioId, SqlSession session) {
        List<Placement> placements = placementDao.getPlacementsByIoId(ioId, null, session);
        for (Placement placement : placements) {
            if (!placement.getStatus().equals(InsertionOrderStatusEnum.REJECTED.getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the Status of a list of {@code Placement}s
     * @param io The IO id t
     * @param placementsOnDb
     * @param uniquePlacements
     * @param contactId
     * @param key
     * @param session
     * @return
     */
    private List<Placement> updatePlacementsStatus(InsertionOrder io,
                                                        List<Placement> placementsOnDb,
                                                        Map<Long, Placement> uniquePlacements,
                                                        Long contactId,
                                                        OauthKey key,
                                                        SqlSession session) {
        for (Placement placementOnDb : placementsOnDb) {

            if (io.getId().longValue() != placementOnDb.getIoId().longValue()) {
                throw new IllegalArgumentException("Placement's ioId does not match ioId sent.");
            }
            Placement placement = uniquePlacements.get(placementOnDb.getId());
            ioPlacementStatusManager.updatePlacementStatus(io.getCampaignId(),
                    placementOnDb.getId(),
                    placement.getStatus(),
                    placementOnDb.getStatus(),
                    contactId,
                    key,
                    session);
        }
        List<Placement> result = placementDao
                .getPlacementsByIoId(io.getId(), InsertionOrderStatusEnum.ACCEPTED, session);
        if (result.size() > 0 && !io.getStatus().equals(InsertionOrderStatusEnum.ACCEPTED.getName())) {
            ioPlacementStatusManager.updateIOStatus(io.getId(), InsertionOrderStatusEnum.ACCEPTED.getName(), io.getStatus(), key, session);
        } else if (arePlacementsRejected(io.getId(), session) && !io.getStatus().equals(InsertionOrderStatusEnum.REJECTED.getName())) {
            ioPlacementStatusManager.updateIOStatus(io.getId(), InsertionOrderStatusEnum.REJECTED.getName(), io.getStatus(), key, session);
        }
        return placementDao.getPlacementsByIoId(io.getId(), null, session);
    }


    private RecordSet<Placement> updatePlacementStatus(Long ioId, RecordSet<Placement> placementsRecordSet, OauthKey key, SqlSession session) throws Exception {
        Map<Long, Placement> uniquePlacements = new HashMap<>();
        for (Placement placement : placementsRecordSet.getRecords()) {
            uniquePlacements.put(placement.getId(), placement);
        }
        // Check that user is valid to access the requested data
        if (!userValidFor(AccessStatement.PLACEMENT, uniquePlacements.keySet(), key.getUserId(), session)) {
            throw BusinessValidationUtil
                    .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
        }

        if(!userValidFor(AccessStatement.INSERTION_ORDER, ioId, key.getUserId(), session)) {
            throw BusinessValidationUtil
                    .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
        }

        // get user Contact
        List<Placement> placementsOnDb = placementDao.getPlacementsByIds(uniquePlacements.keySet(),
                session);
        for (Placement placementOnDb : placementsOnDb) {
            if (ioId.longValue() != placementOnDb.getIoId().longValue()) {
                throw new IllegalArgumentException("Placement's ioId does not match ioId sent.");
            }
            Placement placement = uniquePlacements.get(placementOnDb.getId());
            BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(placement, Placement.class.getSimpleName());
            validator.validateChangeStatus(placement, placementOnDb, validationResult);
            if (validationResult.hasErrors()) {
                throw BusinessValidationUtil.buildSpringValidationSystemException(ValidationCode.INVALID, validationResult);
            }
        }
        User user = userDao.get(key.getUserId(), session);
        InsertionOrder io = ioDao.get(ioId, session);
        return new RecordSet<>(updatePlacementsStatus(io, placementsOnDb, uniquePlacements, user.getContactId(), key, session));
    }

    private List<Placement> getPlacementsByPackage(Long packageId, OauthKey key, SqlSession session) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setQuery("packageId equals to " + packageId);
        RecordSet<Placement> placements;
        List<Placement> result;
        try {
            placements = placementDao.getPlacements(criteria, key, session);
        } catch (SearchApiException e) {
            log.warn("Error while creating Searcher", e);
            throw BusinessValidationUtil
                    .buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, "");
        } catch (Exception e) {
            throw BusinessValidationUtil
                    .buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, "");
        }
        result = placements.getRecords();
        return result;
    }

    private Either<AccessError, Void> checkAccessControlPlacementFilterParam(PlacementFilterParam filterParam,
            String userId, SqlSession session) {
        /*
         1. DAC for Advertiser
         2. DAC for Brand
         3. DAC for filterParamIds
         */
        List<PlacementFilterParamLevelTypeEnum> hierarchyLevelTypes = PlacementFilterParamLevelTypeEnum.DEFAULT_HIERARCHY_TO_LEVEL.
                get(PlacementFilterParamLevelTypeEnum.valueOf(PlacementFilterParamLevelTypeEnum.class, filterParam.getLevelType().toUpperCase()));
        List<PlacementFilterParamLevelTypeEnum> hierarchyLevelToValidate = new ArrayList<>(hierarchyLevelTypes);
        hierarchyLevelToValidate.remove(hierarchyLevelToValidate.size() - 1);
        AccessStatement accessStatement;
        Long id;
        for (PlacementFilterParamLevelTypeEnum levelType : hierarchyLevelToValidate) {
            accessStatement = null;
            id = null;
            switch (levelType) {
                case PLACEMENT:
                    accessStatement = AccessStatement.PLACEMENT;
                    id = filterParam.getPlacementId();
                    break;
                case SECTION:
                    accessStatement = AccessStatement.SITE_SECTION;
                    id = filterParam.getSectionId();
                    break;
                case SITE:
                    accessStatement = AccessStatement.SITE;
                    id = filterParam.getSiteId();
                    break;
                case CAMPAIGN:
                    accessStatement = AccessStatement.CAMPAIGN;
                    id = filterParam.getCampaignId();
                    break;
            }
            if (!userValidFor(accessStatement, Collections.singletonList(id), userId, session)) {
                AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, userId);
                return Either.error(error);
            }
        }
        return null;
    }

    public Either<Errors, RecordSet<PlacementView>> getPlacementsByBrand(Long brandId,
                                                                         OauthKey key) {
        // Nullability checks
        if (brandId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "brand Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        //Obtain session
        List<PlacementView> result = null;
        Errors errors = new Errors();
        SqlSession session = placementDao.openSession();

        try {
            //check access control
            if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(brandId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            result = placementDao.getPlacementsByBrand(brandId, session);
        } finally {
            placementDao.close(session);
        }
        RecordSet<PlacementView> res = new RecordSet<>();
        res.setRecords(result);
        return Either.success(res);
    }
    
    public Either<Errors, TagEmailResponse> sendTagEmails(TagEmail tagEmail, TagEmailProxy proxy,
                                                          OauthKey key) {
        
        if (tagEmail == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Tag email"));
        }

        if (tagEmail.getTagEmailSites() == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Tag email data"));
        }

        if (proxy == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "TagEmail proxy"));
        }

        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        List<String> userMail = new ArrayList<String>();
        userMail.add(tagEmail.getUserEmail());
        Errors errors = new Errors();
        Either<Errors, Void> emailValidation = validateEmailsFormat(userMail);
        if (emailValidation != null && emailValidation.isError()) {
            return Either.error(emailValidation.error());
        }

        if (tagEmail.getToEmails() != null && tagEmail.getToEmails().size() > 0) {
            emailValidation = validateEmailsFormat(tagEmail.getToEmails());
            if (emailValidation != null && emailValidation.isError()) {
                return Either.error(emailValidation.error());
            }
        }

        for (TagEmailSite siteEmail:tagEmail.getTagEmailSites()) {
            Either<Error, Void> result = checkAccessControlEmailTag(siteEmail.getPlacementIds(),key.getUserId(), siteEmail.getSiteId(),
                    siteEmail.getFileType());
            if (result != null && result.isError()) {
                errors.addError(result.error());
                return Either.error(errors);
            }
            if (siteEmail.getRecipients() != null) {
                emailValidation = validateEmailsFormat(siteEmail.getRecipients());
                if (emailValidation != null && emailValidation.isError()) {
                    return Either.error(emailValidation.error());
                }
            }
        }

        TagEmailResponse tagResponse = new TagEmailResponse();
        tagResponse.setTagEmailSiteResponses(new ArrayList<TagEmailSiteResponse>());

        for (TagEmailSite site : tagEmail.getTagEmailSites()) {
            List<String> allEmails = new ArrayList<>();

            if (tagEmail.getToEmails() != null && tagEmail.getToEmails().size() > 0) {
                allEmails.addAll(tagEmail.getToEmails());
            }

            if (site.getRecipients() != null && site.getRecipients().size() > 0) {
                allEmails.addAll(site.getRecipients());
            }

            if (allEmails.size() > 0) {
                TagEmailWrapper wrap = new TagEmailWrapper();

                wrap.setFileType(site.getFileType());
                wrap.setUserEmail(tagEmail.getUserEmail());
                wrap.setPlacementIds(site.getPlacementIds());
                wrap.setToEmails(new TagEmailRecipientsWrapper(site.getSiteId(), allEmails));

                try {
                    TagEmailWrapper result = proxy.post(wrap);

                    if (result.isIsSuccess()) {
                        tagResponse.getTagEmailSiteResponses().add(new TagEmailSiteResponse(
                                site.getSiteId(), result.isIsSuccess(), result.getMessage()));
                    } else {
                        EmailError error = new EmailError(result.getMessage(),
                                BusinessCode.INVALID, StringUtils.join(allEmails.toArray(), ","));
                        errors.addError(error);
                    }

                } catch (Exception e) {
                    EmailError error = new EmailError(e.getMessage(),
                            BusinessCode.INTERNAL_ERROR,
                            StringUtils.join(allEmails.toArray(), ","));
                    errors.addError(error);
                }
            }
        }

        if (!errors.getErrors().isEmpty()) {
            return Either.error(errors);
        }

        return Either.success(tagResponse);
    }
    
    public Either<Errors, TagPlacement> getAdTagPlacement(Long placementId, TagProxy tagProxy, 
            TagTypeProxy tagTypeProxy, OauthKey key) {
        
        if (placementId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Placement Id"));
        }
        
        if (tagProxy == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Tag Proxy"));
        }
        
        if (tagTypeProxy == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Tag Type Proxy"));
        }
        
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        
        Errors errors = new Errors();
        TagPlacement tagResult = new TagPlacement();
        SqlSession session = placementDao.openSession();
        
        try {
            if (!userValidFor(AccessStatement.PLACEMENT, Collections.singletonList(placementId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            
            Placement placement = placementDao.get(placementId, session);
            InsertionOrder io = ioDao.get(placement.getIoId(), session);
            Campaign campaign = campaignDao.get(placement.getCampaignId(), session);
            
            tagResult.setIoNumber(io.getId().toString());
            tagResult.setIoDescription(io.getName());
            tagResult.setCampaignName(campaign.getName());
            tagResult.setStartDate(placement.getStartDate());
            tagResult.setEndDate(placement.getEndDate());
            tagResult.setImpressions(placement.getInventory().intValue());

            RecordSet<TagType> tagTypes = tagTypeProxy.getTagTypes(
                    campaign.getAdvertiserId().intValue(), placement.getSiteId().intValue());

            List<TagTypeEnum> requiredTypes = Arrays.asList(
                    TagTypeEnum.IFRAME, TagTypeEnum.SCRIPT, TagTypeEnum.CLICK_URL);

            for (TagType tagType:tagTypes.getRecords()){
                TagTypeEnum tagTypeEnum = TagTypeEnum.parse(tagType.getName());
                
                if (tagTypeEnum != null && requiredTypes.contains(tagTypeEnum)) {
                    
                    String tagString = tagProxy.getTagString(tagType.getTagId().intValue(), 
                            placementId.intValue());
                    
                    switch(tagTypeEnum) {
                        case IFRAME:
                            tagResult.setFullAdTag(tagString);
                            break;
                        case SCRIPT:
                            String noScript = HTML_NO_SCRIPT_COMMENT
                                + tagString.substring(tagString.indexOf("<NOSCRIPT"));
                        
                            tagResult.setScriptVersion(tagString);
                            tagResult.setNoScriptVersion(noScript);
                            break;
                        case CLICK_URL:
                            tagResult.setClickRedirect(tagString);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            BusinessError error = new BusinessError(e.getMessage(), 
                    BusinessCode.INTERNAL_ERROR, StringUtils.EMPTY);
            errors.addError(error);
        } finally {
            placementDao.close(session);
        }
                
        if (!errors.getErrors().isEmpty()){
            return Either.error(errors);
        } 
        
        return Either.success(tagResult);
    }
    
    private Either<Error, Void> checkAccessControlEmailTag(List<Integer> placementIds, String userId, Integer siteId,
                                            String fileType) {
        SqlSession session = placementDao.openSession();
        Placement placement;
        try {

            if (siteId == null) {
                Error error = new Error(ResourceBundleUtil.getString("global.error.null", "SiteId"),
                        ValidationCode.REQUIRED);
                return Either.error(error);
            }

            // Check access control
            List<Long> placement2LongList = new ArrayList();
            List<Placement> placementsList;
            for (int i = 0; i < placementIds.size(); i++) {
                placement2LongList.add(placementIds.get(i).longValue());
            }

            if (!userValidFor(AccessStatement.PLACEMENT, placement2LongList, userId,
                    session)) {
                Error error = new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER",
                        userId),
                        ValidationCode.INVALID);
                return Either.error(error);
            }

            placementsList = placementDao.getPlacementsByIds(placement2LongList, session);

            for (int i = 0; i < placementsList.size(); i++) {
                if (!placementsList.get(i).getStatus().equals(InsertionOrderStatusEnum.ACCEPTED.getName())) {
                    Error error = new Error(ResourceBundleUtil.getString("placement.error.InvalidStatus",
                            placementsList.get(i).getStatus()),
                            ValidationCode.INVALID);
                    return Either.error(error);
                }

                if (placementsList.get(i).getIsTrafficked() == 0) {
                    Error error = new Error(ResourceBundleUtil.getString("trafficking.error.PlacementNotTrafficked",
                            String.valueOf(placementsList.get(i).getId())),
                            ValidationCode.INVALID);
                    return Either.error(error);
                }
            }

            if (!userValidFor(AccessStatement.SITE, Collections.singletonList(siteId.longValue()),
                    userId, session)) {
                Error error = new Error(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser",
                        "SiteId" , String.valueOf(Long.toString(siteId)), userId),
                        ValidationCode.INVALID);
                return Either.error(error);

            }

            if (fileType.toUpperCase().compareTo(Constants.PDF_FILE_TYPE) != 0 &&
                    fileType.toUpperCase().compareTo(Constants.HTML_FILE_TYPE) != 0 &&
                    fileType.toUpperCase().compareTo(Constants.XLSX_FILE_TYPE) != 0 &&
                    fileType.toUpperCase().compareTo(Constants.TXT_FILE_TYPE) != 0 &&
                    fileType.toUpperCase().compareTo(Constants.XLSX_1_X_1_FILE_TYPE) != 0) {
                Error error = new Error(ResourceBundleUtil.getString("dataAccessControl.InvalidFileType",
                         fileType),
                        ValidationCode.INVALID);
                return Either.error(error);
            }
        } finally {
            placementDao.close(session);
        }
        return null;
    }

    private Either<Errors, Void> validateEmailsFormat(List<String> emailList) {
        Errors errors = new Errors();

        for (int i = 0; i < emailList.size(); i++) {
            if (!emailList.get(i).matches(ValidationConstants.REGEXP_EMAIL_ADDRESS))  {
                Error error = new Error(ResourceBundleUtil.getString("email.error.InvalidEmail",
                        emailList.get(i)), ValidationCode.INVALID);
                errors.addError(error);
                }
        }
        if (!errors.getErrors().isEmpty()){
            return Either.error(errors);
        }
        return null;
    }
}

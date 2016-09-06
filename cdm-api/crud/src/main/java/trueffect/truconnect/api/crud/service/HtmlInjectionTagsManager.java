package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.HtmlInjectionTagAssociation;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.delivery.TruQTagList;
import trueffect.truconnect.api.commons.model.delivery.TruQTagMessage;
import trueffect.truconnect.api.commons.model.dto.HtmlInjectionTagAssociationDTO;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.enums.HtmlInjectionTypeEnum;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.commons.model.htmlinjection.FacebookCustomTrackingInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.HtmlInjectionType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.HtmlInjectionTagsDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.factory.HtmlInjectionTagFactory;
import trueffect.truconnect.api.crud.validation.HtmlInjectionTagValidator;
import trueffect.truconnect.api.crud.validation.PlacementActionTagAssocParamValidator;
import trueffect.truconnect.api.crud.validation.PlacementFilterParamValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.external.proxy.TruQProxy;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Saulo Lopez
 */
public class HtmlInjectionTagsManager extends AbstractGenericManager {

    private static final String SEPARATOR = "_";

    private final HtmlInjectionTagsDao htmlInjectionTagsDao;
    private PlacementDao placementDao;
    private SiteSectionDao sectionDao;
    private UserDao userDao;
    private CampaignDao campaignDao;
    private PlacementFilterParamValidator placementFilterParamValidator;
    private HtmlInjectionTagValidator htmlInjectionTagValidator;
    private PlacementActionTagAssocParamValidator placementActionTagAssocParamValidator;
    private TruQProxy truQProxy;

    public HtmlInjectionTagsManager(HtmlInjectionTagsDao htmlInjectionTagsDao,
                                    PlacementDao placementDao, SiteSectionDao sectionDao,
                                    CampaignDao campaignDao, UserDao userDao, AccessControl accessControl,
                                    TruQProxy truQProxy) {
        super(accessControl);
        this.htmlInjectionTagsDao = htmlInjectionTagsDao;
        this.placementDao = placementDao;
        this.sectionDao = sectionDao;
        this.campaignDao = campaignDao;
        this.userDao = userDao;
        this.placementFilterParamValidator = new PlacementFilterParamValidator();
        this.htmlInjectionTagValidator = new HtmlInjectionTagValidator();
        this.placementActionTagAssocParamValidator = new PlacementActionTagAssocParamValidator();
        this.truQProxy = truQProxy;
    }

    public Either<Error, RecordSet<HtmlInjectionTags>> getHtmlInjectionTagsForAgency(Long agencyId,
                                                                                     OauthKey key) {
        if (agencyId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Agency Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        RecordSet<HtmlInjectionTags> result = null;
        SqlSession session = htmlInjectionTagsDao.openSession();
        try {
            if (!userValidFor(AccessStatement.AGENCY, agencyId, key.getUserId(), session)) {
                return Either.error(new Error(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
            result = htmlInjectionTagsDao.getHtmlInjectionTagsForAgency(agencyId, key, session);
        } finally {
            htmlInjectionTagsDao.close(session);
        }
        return Either.success(result);
    }

    public Either<Error, HtmlInjectionTags> getHtmlInjectionTag(Long htmlInjectionTagId,
                                                                OauthKey key) {
        if (htmlInjectionTagId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "HtmlInjectionTag Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        HtmlInjectionTags result = null;
        SqlSession session = htmlInjectionTagsDao.openSession();
        try {
            if (!userValidFor(AccessStatement.HTML_INJECTION, htmlInjectionTagId, key.getUserId(),
                    session)) {
                return Either.error(new Error(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
            result = htmlInjectionTagsDao.getHtmlInjectionById(htmlInjectionTagId, session);
        } finally {
            htmlInjectionTagsDao.close(session);
        }
        return Either.success(result);
    }

    public Either<Errors, HtmlInjectionTags> updateHtmlInjectionTag(Long id,
                                                                    HtmlInjectionTags htmlInjectionTag,
                                                                    OauthKey key) {
        if (id == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Id"));
        }
        if (htmlInjectionTag == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "HtmlInjectionTag"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();
        if ((htmlInjectionTag.getId() != null) && !id.equals(htmlInjectionTag.getId())) {
            errors.addError(
                    new Error(ResourceBundleUtil.getString("global.error.putWithDifferentId"),
                            BusinessCode.INVALID));
            return Either.error(errors);
        }

        htmlInjectionTag.setModifiedTpwsKey(key.getTpws());
        htmlInjectionTag.setHtmlContent(StringUtils.trim(htmlInjectionTag.getHtmlContent()));
        htmlInjectionTag.setSecureHtmlContent(
                StringUtils.trim(htmlInjectionTag.getSecureHtmlContent()));
        htmlInjectionTag.setName(HtmlInjectionTagFactory.createName(htmlInjectionTag.getName()));

        // All model validations performed below
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(htmlInjectionTag,
                HtmlInjectionTags.class.getSimpleName());
        ValidationUtils.invokeValidator(htmlInjectionTagValidator, htmlInjectionTag,
                validationResult);
        if (validationResult.hasErrors()) {
            errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(validationResult));
            return Either.error(errors);
        }

        SqlSession session = htmlInjectionTagsDao.openSession();
        HtmlInjectionTags result = null;
        try {
            if (!userValidFor(AccessStatement.HTML_INJECTION, htmlInjectionTag.getId(),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            result = htmlInjectionTagsDao.updateHtmlInjection(htmlInjectionTag, session);
            htmlInjectionTagsDao.commit(session);

            //call TruQ service
            TruQTagList truQTagList = new TruQTagList(new ArrayList<TruQTagMessage>());

            TruQTagMessage tagMessage = new TruQTagMessage();
            tagMessage.setAgencyId(htmlInjectionTag.getAgencyId().intValue());
            tagMessage.setCampaignId(0);
            tagMessage.setChangedTagIds(Integer.toString(htmlInjectionTag.getId().intValue()));
            tagMessage.setSessionKey(key.getTpws());

            truQTagList.getTagMessages().add(tagMessage);

            truQProxy.post(truQTagList);
        } catch (Exception e) {
            htmlInjectionTagsDao.rollback(session);
            errors.addError(
                    new Error(ResourceBundleUtil.getString("BusinessCode.INTERNAL_ERROR"),
                            BusinessCode.INTERNAL_ERROR));
            return Either.error(errors);
        } finally {
            htmlInjectionTagsDao.close(session);
        }
        return Either.success(result);
    }

    /**
     * Get a RecocrdSet of PlacementView in order to create new Creative Insertions
     *
     * @param agencyId
     * @param filterParam
     * @param startIndex
     * @param pageSize
     * @param key
     * @return Either
     */
    public Either<Errors, HtmlInjectionTagAssociationDTO> getAssociationsByPlacementFilterParam(
            Long agencyId, PlacementFilterParam filterParam, Long startIndex, Long pageSize,
            OauthKey key) {

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
        if (filterParam == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "Filter Param"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);

        }
        // Validate payload
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(
                filterParam, "placementFilterParam");
        placementFilterParamValidator.validateGetAssociations(filterParam, validationResult);
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

        HtmlInjectionTagAssociationDTO result = new HtmlInjectionTagAssociationDTO();
        SqlSession session = htmlInjectionTagsDao.openSession();
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

            // check access control into payload
            Either<AccessError, Void> accessControlResult = checkAccessControlPlacementFilterParam(
                    filterParam, key.getUserId(), session);
            if (accessControlResult != null && accessControlResult.isError()) {
                errors.addError(accessControlResult.error());
                return Either.error(errors);
            }

            // Business Logic
            PlacementFilterParamLevelTypeEnum levelType = PlacementFilterParamLevelTypeEnum
                    .valueOf(filterParam.getLevelType().toUpperCase());
            Long entityTypeId = levelType.getCode();
            Long entityId = null;
            switch (levelType) {
                case PLACEMENT:
                    if (!placementDao.checkPlacementsBelongsCampaignId(
                            Collections.singletonList(filterParam.getPlacementId()),
                            filterParam.getCampaignId(), session)) {
                        BusinessError error = new BusinessError(ResourceBundleUtil
                                .getString("global.error.entityHasNoRelationship", "placementId",
                                        "campaignId"), BusinessCode.INVALID, "placementId");
                        errors.addError(error);
                        return Either.error(errors);
                    }
                    Placement placement = placementDao.get(filterParam.getPlacementId(), session);
                    if (!placement.getSiteId().equals(filterParam.getSiteId())) {
                        BusinessError error = new BusinessError(ResourceBundleUtil
                                .getString("global.error.entityHasNoRelationship", "placementId",
                                        "siteId"), BusinessCode.INVALID, "siteId");
                        errors.addError(error);
                        return Either.error(errors);
                    }
                    if (!placement.getSiteSectionId().equals(filterParam.getSectionId())) {
                        BusinessError error = new BusinessError(ResourceBundleUtil
                                .getString("global.error.entityHasNoRelationship", "placementId",
                                        "sectionId"), BusinessCode.INVALID, "siteId");
                        errors.addError(error);
                        return Either.error(errors);
                    }
                    entityId = filterParam.getPlacementId();
                    break;
                case SECTION:
                    SiteSection section = sectionDao.get(filterParam.getSectionId(), session);
                    if (!section.getSiteId().equals(filterParam.getSiteId())) {
                        BusinessError error = new BusinessError(ResourceBundleUtil
                                .getString("global.error.entityHasNoRelationship", "siteId",
                                        "sectionId"), BusinessCode.INVALID, "siteId");
                        errors.addError(error);
                        return Either.error(errors);
                    }
                    entityId = filterParam.getSectionId();
                    break;
                case SITE:
                    entityId = filterParam.getSiteId();
                    break;
                case CAMPAIGN:
                    entityId = filterParam.getCampaignId();
                    break;
            }

            List<HtmlInjectionTagAssociation> associations = htmlInjectionTagsDao
                    .getAssociations(agencyId, filterParam, entityTypeId, entityId,
                            paginator.get("startIndex"), paginator.get("pageSize"), session);

            int numberOfRecords = htmlInjectionTagsDao
                    .getCountAssociations(agencyId, filterParam, entityTypeId, entityId, session)
                    .intValue();

            List<HtmlInjectionTagAssociation> inherited, direct;
            inherited = new ArrayList<>();
            direct = new ArrayList<>();

            for (HtmlInjectionTagAssociation assoc : associations) {
                if (assoc.getIsInherited() == Constants.ENABLED) {
                    inherited.add(assoc);
                } else {
                    direct.add(assoc);
                }
            }

            result.setDirectAssociations(direct);
            result.setInheritedAssociations(inherited);
            result.setTotalNumberOfRecords(numberOfRecords);
            result.setStartIndex(paginator.get("startIndex").intValue());
            result.setPageSize(paginator.get("pageSize").intValue());
        } finally {
            htmlInjectionTagsDao.close(session);
        }
        return Either.success(result);
    }

    /**
     * Get a RecocrdSet of PlacementView in order to create new Creative Insertions
     *
     * @param bulkDelete
     * @param key
     * @return Either
     */
    public Either<Errors, String> deleteBulk(RecordSet<Long> bulkDelete, OauthKey key) {

        // Nullability checks
        if (bulkDelete == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "BulkDelete"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();
        // Validate payload
        if (bulkDelete.getRecords().isEmpty()) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "bulkCreate"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);

        }

        List<Long> toDelete = bulkDelete.getRecords();
        for (Long id : toDelete) {
            if (id == null) {
                ValidationError error = new ValidationError(
                        ResourceBundleUtil.getString("global.error.empty", "id"),
                        ValidationCode.REQUIRED);
                errors.addError(error);
                return Either.error(errors);
            }
        }

        SqlSession session = htmlInjectionTagsDao.openSession();
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.HTML_INJECTION, toDelete,
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // get user AgencyId
            User user = userDao.get(key.getUserId(), session);

            // Business Logic
            Integer affected = htmlInjectionTagsDao
                    .bulkDeleteByIds(user.getAgencyId(), toDelete, key.getTpws(), session);
            if (affected <= 0) {
                htmlInjectionTagsDao.rollback(session);
                BusinessError error = new BusinessError(
                        ResourceBundleUtil.getString("global.error.empty", "ids"),
                        BusinessCode.NOT_FOUND,
                        "ids");
                errors.addError(error);
                return Either.error(errors);
            }
            htmlInjectionTagsDao.commit(session);

            //call TruQ service
            TruQTagList truQTagList = new TruQTagList(new ArrayList<TruQTagMessage>());

            TruQTagMessage tagMessage = new TruQTagMessage();
            tagMessage.setAgencyId(user.getAgencyId().intValue());
            tagMessage.setCampaignId(0);
            tagMessage.setChangedTagIds(StringUtils.join(toDelete, ","));
            tagMessage.setSessionKey(key.getTpws());

            truQTagList.getTagMessages().add(tagMessage);

            truQProxy.post(truQTagList);
        } catch (Exception e) {
            htmlInjectionTagsDao.rollback(session);
            BusinessError error = new BusinessError(e.getMessage(),
                    BusinessCode.INTERNAL_ERROR,
                    "ids");
            errors.addError(error);
            return Either.error(errors);
        } finally {
            htmlInjectionTagsDao.close(session);
        }
        String message = ResourceBundleUtil.getString("global.info.bulkOperationSuccess",
                ResourceBundleUtil.getString("global.label.delete"),
                ResourceBundleUtil.getString("global.label.htmlInjectionTags"));
        return Either.success(message);
    }

    public Either<Errors, HtmlInjectionTags> createTagFromType(Long agencyId,
                                                               HtmlInjectionType htmlInjectionType,
                                                               OauthKey key) {
        HtmlInjectionTags result = null;
        if (agencyId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Agency Id"));
        }

        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        if (htmlInjectionType == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Html Injection Type"));
        }

        Errors validationErrors = null;
        validationErrors = HtmlInjectionTagValidator.validateHtmlInjectionTag(htmlInjectionType);
        if (validationErrors != null && !validationErrors.getErrors().isEmpty()) {
            return Either.error(validationErrors);
        }

        Errors errors = new Errors();
        result = HtmlInjectionTagFactory.createHtmlInjectionTag(htmlInjectionType);
        if (result == null) {
            errors.addError(new Error(ResourceBundleUtil.getString("BusinessCode.INVALID"),
                    ValidationCode.INVALID));
            return Either.error(errors);
        }

        SqlSession session = htmlInjectionTagsDao.openSession();
        try {
            if (!userValidFor(AccessStatement.AGENCY, agencyId, key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            if (htmlInjectionType.getType() == HtmlInjectionTypeEnum.FACEBOOK_CUSTOM_TRACKING) {
                Long firstPartyDomainId = ((FacebookCustomTrackingInjectionType) htmlInjectionType).getFirstPartyDomainId();
                if (!userValidFor(AccessStatement.COOKIE_DOMAIN, firstPartyDomainId, key.getUserId(), session)) {
                    AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"), SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                    errors.addError(error);
                    return Either.error(errors);
                }
            }

            result.setAgencyId(agencyId);
            result.setCreatedTpwsKey(key.getTpws());
            result = htmlInjectionTagsDao.insertHtmlInjection(result, session);
            if (result == null) {
                errors.addError(new Error(
                        ResourceBundleUtil.getString("global.error.noDBConnection")));
                return Either.error(errors);
            }
            htmlInjectionTagsDao.commit(session);
        } catch (Exception e) {
            htmlInjectionTagsDao.rollback(session);
            errors.addError(
                    new Error(ResourceBundleUtil.getString("BusinessCode.INTERNAL_ERROR"),
                            BusinessCode.INTERNAL_ERROR));
            return Either.error(errors);
        } finally {
            htmlInjectionTagsDao.close(session);
        }
        return Either.success(result);
    }

    public Either<Errors, RecordSet<HtmlInjectionTags>> getTagsByPlacementId(Long placementId,
                                                                             Long startIndex,
                                                                             Long pageSize,
                                                                             OauthKey key) {
        // Nullability checks
        if (placementId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Placement Id"));
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
        List<HtmlInjectionTags> tagsList = null;
        SqlSession session = htmlInjectionTagsDao.openSession();
        int numberOfRecords = 0;
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.PLACEMENT, Collections.singletonList(placementId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            Placement placement = placementDao.get(placementId, session);

            tagsList = htmlInjectionTagsDao.getHtmlInjectionTagsByPlacementId(
                    placement.getCampaignId(), placementId, paginator.get("startIndex"),
                    paginator.get("pageSize"), session);
            numberOfRecords = htmlInjectionTagsDao.getCountHtmlInjectionTagsByPlacementId(
                    placement.getCampaignId(), placementId, session).intValue();
        } finally {
            htmlInjectionTagsDao.close(session);
        }
        RecordSet<HtmlInjectionTags> result =
                new RecordSet<>(paginator.get("startIndex").intValue(),
                        paginator.get("pageSize").intValue(), numberOfRecords, tagsList);
        return Either.success(result);
    }

    /**
     * Get a RecocrdSet of PlacementView in order to create new Creative Insertions
     * @param agencyId
     * @param advertiserId
     * @param brandId
     * @param bulkActionParam
     * @param key
     * @return Either
     */
    public Either<Errors, String> placementActionAssociationsBulk(Long agencyId, Long advertiserId,
                                                                  Long brandId,
                                                                  PlacementActionTagAssocParam bulkActionParam,
                                                                  OauthKey key) {
        // Nullability checks
        if (agencyId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Agency Id"));
        }
        if (bulkActionParam == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "BulkActionParam"));
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

        // Validate payload and clean data
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(bulkActionParam,
                "placementActionTagAssocParam");
        ValidationUtils.invokeValidator(placementActionTagAssocParamValidator, bulkActionParam,
                validationResult);
        if (validationResult.hasErrors()) {
            errors.getErrors()
                  .addAll(ApiValidationUtils.parseToValidationError(validationResult));
            return Either.error(errors);
        }

        SqlSession session = htmlInjectionTagsDao.openSession();
        long millis = System.currentTimeMillis();
        log.info("MEASURE: TagAssociations - Begin process for {}", key.getUserId());
        try {
            // check access control
            Either<AccessError, Void> accessControlResult =
                    checkAccessControlPlacementActionParam(agencyId, advertiserId, brandId,
                            bulkActionParam, key.getUserId(), session);
            if (accessControlResult != null && accessControlResult.isError()) {
                errors.addError(accessControlResult.error());
                return Either.error(errors);
            }

            // Check data consistency
            Either<ValidationError, Void> dataConsistencyResult =
                    checkDataConsistencyForActionParam(advertiserId, brandId, bulkActionParam,
                            session);
            if (dataConsistencyResult != null && dataConsistencyResult.isError()) {
                errors.addError(dataConsistencyResult.error());
                return Either.error(errors);
            }

            // Create Associations
            PlacementActionTagAssocParam.PlacementAction actionEnum =
                    PlacementActionTagAssocParam.PlacementAction
                            .valueOf(PlacementActionTagAssocParam.PlacementAction.class,
                                    bulkActionParam.getAction().toUpperCase());
            // Call DAO to persist associations
            millis = System.currentTimeMillis();
            log.info(
                    "MEASURE: TagAssociations - Execute Action. Start measure for Action = {}, PlacementActionTagAssocParams = {}",
                    actionEnum.toString(), bulkActionParam.toString());
            switch (actionEnum) {
                case C:
                    htmlInjectionTagsDao
                            .createTagAssociations(agencyId, advertiserId, brandId, bulkActionParam,
                                    key.getTpws(), session);
                    break;
                case D:
                    htmlInjectionTagsDao.deleteTagAssociations(agencyId, advertiserId, brandId,
                            bulkActionParam,
                            key.getTpws(), session);
                    break;
            }
            millis = System.currentTimeMillis() - millis;
            log.info("MEASURE: TagAssociations - Execute Action. Stop measure. Took {}",
                    toSeconds(millis));

            htmlInjectionTagsDao.commit(session);

            //call TruQ service
            log.info("MEASURE: TagAssociations - Send TruQTagMessage. Start measure for {} campaign",
                    bulkActionParam.getCampaignId());
            sendTruQTagMessagesByCampaignId(agencyId, bulkActionParam.getCampaignId(),
                    key.getTpws());
            millis = System.currentTimeMillis() - millis;
            log.info("MEASURE: TagAssociations - Send TruQTagMessage. Stop measure. Took {}",
                    toSeconds(millis));
        } catch (Exception e) {
            htmlInjectionTagsDao.rollback(session);
            BusinessError error = new BusinessError(
                    e.getMessage(),
                    BusinessCode.INTERNAL_ERROR, null);
            errors.addError(error);
            return Either.error(errors);
        } finally {
            htmlInjectionTagsDao.close(session);
        }

        String message = ResourceBundleUtil.getString("global.info.bulkOperationSuccess",
                ResourceBundleUtil.getString("global.label.actions"),
                ResourceBundleUtil.getString("global.label.htmlInjectionTagAssociations"));
        millis = System.currentTimeMillis() - millis;
        log.info("MEASURE: TagAssociations - End process {}. Took {}", key.getUserId(),
                toSeconds(millis));
        return Either.success(message);
    }

    /**
     * De associate a RecocrdSet of Html Tag Injection Ids from a Placement Id
     *
     * @param placementId
     * @param tagIds
     * @param key
     * @return Either
     */
    public Either<Errors, String> deletePlacementHtmlTagInjectionAssocBulk(Long placementId,
                                                                           RecordSet<Long> tagIds,
                                                                           OauthKey key) {

        // Nullability checks
        if (placementId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Placement Id"));
        }
        if (tagIds == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Tag Ids"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        // Validate payload
        Set<Long> toDelete = new HashSet<>();
        for (Long id : tagIds.getRecords()) {
            if (id != null) {
                toDelete.add(id);
            }
        }

        Errors errors = new Errors();
        if (toDelete.isEmpty()) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.empty", "tagIds List"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }

        SqlSession session = htmlInjectionTagsDao.openSession();
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.PLACEMENT, placementId,
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            if (!userValidFor(AccessStatement.HTML_INJECTION, toDelete,
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // Business Logic
            Placement plac = placementDao.get(placementId, session);
            Campaign campaign = campaignDao.get(plac.getCampaignId(), session);
            PlacementActionTagAssocParam bulkActionParam = new PlacementActionTagAssocParam();
            bulkActionParam.setLevelType(
                    PlacementFilterParamLevelTypeEnum.PLACEMENT.toString().toLowerCase());
            bulkActionParam.setCampaignId(campaign.getId());
            bulkActionParam.setSiteId(plac.getSiteId());
            bulkActionParam.setSectionId(plac.getSiteSectionId());
            bulkActionParam.setPlacementId(placementId);

            for (Long tagId : toDelete) {
                bulkActionParam.setHtmlInjectionId(tagId);
                htmlInjectionTagsDao
                        .deleteTagAssociations(campaign.getAgencyId(), campaign.getAdvertiserId(),
                                campaign.getBrandId(), bulkActionParam, key.getTpws(), session);
            }
            htmlInjectionTagsDao.commit(session);

            //call TruQ service
            sendTruQTagMessagesByCampaignId(campaign.getAgencyId(), campaign.getId(),
                    key.getTpws());
        } catch (Exception e) {
            htmlInjectionTagsDao.rollback(session);
            BusinessError error = new BusinessError(
                    e.getMessage(),
                    BusinessCode.INTERNAL_ERROR, null);
            errors.addError(error);
            return Either.error(errors);
        } finally {
            htmlInjectionTagsDao.close(session);
        }

        String message = ResourceBundleUtil.getString("global.info.bulkOperationSuccess",
                ResourceBundleUtil.getString("global.label.delete"),
                ResourceBundleUtil.getString("global.label.htmlInjectionTagAssociations"));
        return Either.success(message);
    }

    private void sendTruQTagMessagesByCampaignId(Long agencyId, Long campaignId, String tpws)
            throws ProxyException {
        TruQTagList truQTagList = new TruQTagList(new ArrayList<TruQTagMessage>());
        TruQTagMessage tagMessage = new TruQTagMessage();
        tagMessage.setAgencyId(agencyId.intValue());
        tagMessage.setCampaignId(campaignId.intValue());
        tagMessage.setSessionKey(tpws);
        truQTagList.getTagMessages().add(tagMessage);
        truQProxy.post(truQTagList);
    }

    private Either<AccessError, Void> checkAccessControlPlacementFilterParam(
            PlacementFilterParam filterParam, String userId, SqlSession session) {
        List<PlacementFilterParamLevelTypeEnum> hierarchyLevelTypes =
                PlacementFilterParamLevelTypeEnum.DEFAULT_HIERARCHY_TO_LEVEL.get(
                        PlacementFilterParamLevelTypeEnum.valueOf(
                                PlacementFilterParamLevelTypeEnum.class,
                                filterParam.getLevelType().toUpperCase()));
        AccessStatement accessStatement;
        Long id;
        for (PlacementFilterParamLevelTypeEnum levelType : hierarchyLevelTypes) {
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
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, userId);
                return Either.error(error);
            }
        }
        return null;
    }

    private Either<AccessError, Void> checkAccessControlPlacementActionParam(Long agencyId,
                                                                             Long advertiserId,
                                                                             Long brandId,
                                                                             PlacementActionTagAssocParam actionParams,
                                                                             String userId,
                                                                             SqlSession session) {
        if (!userValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyId),
                userId, session)) {
            AccessError error = new AccessError(
                    ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                    SecurityCode.NOT_FOUND_FOR_USER, userId);
            return Either.error(error);
        }
        if (!userValidFor(AccessStatement.ADVERTISER, Collections.singletonList(advertiserId),
                userId, session)) {
            AccessError error = new AccessError(
                    ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                    SecurityCode.NOT_FOUND_FOR_USER, userId);
            return Either.error(error);
        }
        if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(brandId),
                userId, session)) {
            AccessError error = new AccessError(
                    ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                    SecurityCode.NOT_FOUND_FOR_USER, userId);
            return Either.error(error);
        }

        if (actionParams.getCampaignId() != null) {
            if (!userValidFor(AccessStatement.CAMPAIGN,
                    Collections.singletonList(actionParams.getCampaignId()), userId, session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, userId);
                return Either.error(error);
            }
        }
        if (actionParams.getSiteId() != null) {
            if (!userValidFor(AccessStatement.SITE,
                    Collections.singletonList(actionParams.getSiteId()), userId, session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, userId);
                return Either.error(error);
            }
        }
        if (actionParams.getSectionId() != null) {
            if (!userValidFor(AccessStatement.SITE_SECTION,
                    Collections.singletonList(actionParams.getSectionId()), userId, session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, userId);
                return Either.error(error);
            }
        }
        if (actionParams.getPlacementId() != null) {
            if (!userValidFor(AccessStatement.PLACEMENT,
                    Collections.singletonList(actionParams.getPlacementId()), userId, session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, userId);
                return Either.error(error);
            }
        }
        if (!userValidFor(AccessStatement.HTML_INJECTION,
                Collections.singletonList(actionParams.getHtmlInjectionId()), userId, session)) {
            AccessError error = new AccessError(
                    ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                    SecurityCode.NOT_FOUND_FOR_USER, userId);
            return Either.error(error);
        }
        return null;
    }

    private Either<ValidationError, Void> checkDataConsistencyForActionParam(
            Long advertiserId, Long brandId, PlacementActionTagAssocParam bulkActionParams,
            SqlSession session) {

        Long campaignsCount = campaignDao.getCountCampaignsByAdvertiserAndBrandIds(advertiserId,
                brandId, Collections.singletonList(bulkActionParams.getCampaignId()), session);
        if (campaignsCount != 1) {
            ValidationError error = new ValidationError(ResourceBundleUtil
                    .getString("global.error.entityHasNoRelationships", "campaignId",
                            "advertiserId", "brandId"),
                    ValidationCode.INVALID);
            error.setObjectName("campaignId");
            return Either.error(error);
        }
        PlacementFilterParamLevelTypeEnum levelTypeEnum = PlacementFilterParamLevelTypeEnum
                .valueOf(PlacementFilterParamLevelTypeEnum.class,
                        bulkActionParams.getLevelType().toUpperCase());
        switch (levelTypeEnum) {
            case CAMPAIGN:
                break;
            case SITE:
                String paramSite =
                        bulkActionParams.getCampaignId() + SEPARATOR + bulkActionParams.getSiteId();
                Long sitesCount = placementDao
                        .getCountCampaignSiteOfPlacementsByIds(Collections.singletonList(paramSite),
                                session);
                if (sitesCount != 1) {
                    ValidationError error = new ValidationError(ResourceBundleUtil
                            .getString("htmlTagInjection.error.entityHasNoRelationship", "siteId",
                                    "campaignId",
                                    PlacementFilterParamLevelTypeEnum.SITE.toString().toLowerCase()),
                            ValidationCode.INVALID);
                    error.setObjectName("siteId");
                    return Either.error(error);
                }
                break;
            case SECTION:
                String paramSection = bulkActionParams.getCampaignId() + SEPARATOR + bulkActionParams
                        .getSiteId() + SEPARATOR + bulkActionParams.getSectionId();
                Long sectionsCount = placementDao.getCountCampaignSiteSectionOfPlacementsByIds(
                        Collections.singletonList(paramSection), session);
                if (sectionsCount != 1) {
                    ValidationError error = new ValidationError(ResourceBundleUtil
                            .getString("htmlTagInjection.error.entityHasNoRelationship",
                                    "siteId, sectionId", "campaignId",
                                    PlacementFilterParamLevelTypeEnum.SECTION.toString()
                                                                             .toLowerCase()),
                            ValidationCode.INVALID);
                    error.setObjectName("sectionId");
                    return Either.error(error);
                }
                break;
            case PLACEMENT:
                String paramPlacement = bulkActionParams.getCampaignId() + SEPARATOR + bulkActionParams
                        .getSiteId() + SEPARATOR + bulkActionParams
                        .getSectionId() + SEPARATOR + bulkActionParams.getPlacementId();
                Long placementsCount = placementDao
                        .getCountCampaignSiteSectionPlacementOfPlacementsByIds(
                                Collections.singletonList(paramPlacement), session);
                if (placementsCount != 1) {
                    ValidationError error = new ValidationError(ResourceBundleUtil
                            .getString("htmlTagInjection.error.entityHasNoRelationship",
                                    "siteId, sectionId, placementId", "campaignId",
                                    PlacementFilterParamLevelTypeEnum.PLACEMENT.toString()
                                                                               .toLowerCase()),
                            ValidationCode.INVALID);
                    error.setObjectName("placementId");
                    return Either.error(error);
                }
                break;
        }
        return null;
    }

    private static String toSeconds(long millis) {
        return String.format("%02d sec", TimeUnit.MILLISECONDS.toSeconds(millis));
    }
}

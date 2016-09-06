package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.CreativeVersion;
import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.GeoTarget;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.ScheduleSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeView;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.util.GenericUtils;
import trueffect.truconnect.api.crud.validation.CreativeGroupValidator;
import trueffect.truconnect.api.crud.validation.CreativeInsertionFilterParamValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Richard Jaldin
 */
public class CreativeGroupManager extends AbstractGenericManager {

    protected CreativeGroupDao creativeGroupDao;
    protected CreativeGroupCreativeDao creativeGroupCreativeDao;
    protected CreativeInsertionDao creativeInsertionDao;
    protected CreativeDao creativeDao;
    protected ExtendedPropertiesDao extendedPropertiesDao;
    protected CreativeGroupValidator validator;
    protected CreativeManager creativeManager;
    protected UtilityWrapper utilityWrapper;
    private CreativeInsertionFilterParamValidator filterParamValidator;
    public static final boolean ALLOW_VERSIONING = true;
    public static final boolean DISALLOW_VERSIONING = false;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CreativeGroupManager(CreativeGroupDao creativeGroupDao,
                                CreativeGroupCreativeDao creativeGroupCreativeDao,
                                CreativeInsertionDao creativeInsertionDao, CreativeDao creativeDao,
                                CampaignDao campaignDao,
                                UserDao userDao,
                                ExtendedPropertiesDao extendedPropertiesDao,
                                AccessControl accessControl) {
        super(accessControl);
        this.creativeGroupDao = creativeGroupDao;
        this.creativeGroupCreativeDao = creativeGroupCreativeDao;
        this.creativeInsertionDao = creativeInsertionDao;
        this.creativeDao = creativeDao;
        this.extendedPropertiesDao = extendedPropertiesDao;
        creativeManager = new CreativeManager(creativeDao, creativeGroupDao, creativeGroupCreativeDao, creativeInsertionDao,
                campaignDao, userDao, extendedPropertiesDao, accessControl);
        utilityWrapper = new UtilityWrapperImpl();
        this.validator = new CreativeGroupValidator();
        this.filterParamValidator = new CreativeInsertionFilterParamValidator();
    }

    //Constructor to make this class testable
    public CreativeGroupManager(CreativeGroupDao creativeGroupDao,
                                CreativeGroupCreativeDao creativeGroupCreativeDao,
                                CreativeInsertionDao creativeInsertionDao, CreativeDao creativeDao,
                                CampaignDao campaignDao,
                                UserDao userDao,
                                ExtendedPropertiesDao extendedPropertiesDao,
                                AccessControl accessControl,
                                CreativeGroupManager.UtilityWrapper utilityWrapperCGM,
                                CreativeManager.UtilityWrapper utilityWrapperCM ) {
        this(creativeGroupDao, creativeGroupCreativeDao, creativeInsertionDao,
                creativeDao, campaignDao, userDao, extendedPropertiesDao, accessControl);
        this.utilityWrapper = utilityWrapperCGM;
        creativeManager.setUtilityWrapper(utilityWrapperCM);
    }

    /**
     * Gets a CreativeGroup record.
     *
     * @param id The ID of CreativeGroup
     * @param key
     * @return The CreativeGroup.
     * @throws Exception
     */
    public CreativeGroup get(Long id, OauthKey key) throws Exception {
        SqlSession session = creativeGroupDao.openSession();
        CreativeGroup result;
        try {
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeGroupId", Long.toString(id), key.getUserId()));
            }
            result = creativeGroupDao.get(id, key, session);
        } finally {
            creativeGroupDao.close(session);
        }
        return result;
    }

    /**
     * Get records by a Search Criteria
     *
     * @param searchCriteria The search criteria to filter.
     * @param key
     * @return RecordSet with the matching SearchCriteria
     * @throws Exception
     */
    public RecordSet<CreativeGroup> get(SearchCriteria searchCriteria, OauthKey key) throws Exception {
        SqlSession session = creativeGroupDao.openSession();
        RecordSet<CreativeGroup> result;
        try {
            result = creativeGroupDao.get(searchCriteria, key, session);
        } finally {
            creativeGroupDao.close(session);
        }
        return result;
    }

    /**
     * Saves a Creative Group.
     *
     * @param creativeGroup The Creative Group object to create.
     * @return The Creative Group already saved on the database.
     * @throws Exception
     */
    public CreativeGroup save(CreativeGroup creativeGroup, OauthKey key) throws Exception {
        if (creativeGroup == null) {
            throw new IllegalArgumentException("Creative Group cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        String className = creativeGroup.getClass().getSimpleName();
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(creativeGroup, className);

        setDefaults(creativeGroup);

        validator.validate(creativeGroup, validationResult);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        SqlSession session = creativeGroupDao.openSession();
        CreativeGroup result;
        try {
            //check access control
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(creativeGroup.getCampaignId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CampaignId", Long.toString(creativeGroup.getCampaignId()), key.getUserId()));
            }

            // Checking duplicated names.
            checkDuplicatedNameAndGeoTargeting(creativeGroup, session);

            result = this.creativeGroupDao.save(creativeGroup, key, session);

            //save externalID
            String externalId = extendedPropertiesDao.updateExternalId("CreativeGroup", "MediaID",
                    result.getId(), creativeGroup.getExternalId(), session);
            result.setExternalId(externalId);
            creativeGroupDao.commit(session);
        } catch (Exception e) {
            creativeGroupDao.rollback(session);
            throw e;
        } finally {
            creativeGroupDao.close(session);
        }
        log.info(key.toString() + " Saved " + creativeGroup);
        return result;
    }

    protected void setDefaults(CreativeGroup creativeGroup) {
        if (StringUtils.isBlank(creativeGroup.getRotationType())) {
            creativeGroup.setRotationType("Weighted");
        }
        if (creativeGroup.getIsDefault() == null) {
            creativeGroup.setIsDefault(0L);
        }
        if (creativeGroup.getWeight() == null) {
            creativeGroup.setWeight(100L);
        }
        if (creativeGroup.getImpressionCap() == null) {
            creativeGroup.setImpressionCap(0L);
        }
        if (creativeGroup.getClickthroughCap() == null) {
            creativeGroup.setClickthroughCap(0L);
        }
        if (creativeGroup.getIsReleased() == null) {
            creativeGroup.setIsReleased(0L);
        }
        if (creativeGroup.getDoOptimization() == null) {
            creativeGroup.setDoOptimization(0L);
        }
        if (creativeGroup.getDoGeoTargeting() == null) {
            creativeGroup.setDoGeoTargeting(0L);
        }
        if (creativeGroup.getFrequencyCapWindow() == null) {
            creativeGroup.setFrequencyCapWindow(24L);
        }
        if (creativeGroup.getDoStoryboarding() == null) {
            creativeGroup.setDoStoryboarding(0L);
        }
        if (creativeGroup.getDoCookieTargeting() == null) {
            creativeGroup.setDoCookieTargeting(0L);
        }
        if (creativeGroup.getDoDaypartTargeting() == null) {
            creativeGroup.setDoDaypartTargeting(0L);
        }
        if (creativeGroup.getEnableGroupWeight() == null) {
            creativeGroup.setEnableGroupWeight(0L);
        }
        if (creativeGroup.getEnableFrequencyCap() == null) {
            creativeGroup.setEnableFrequencyCap(0L);
        }
        if (creativeGroup.getPriority() == null) {
            creativeGroup.setPriority(0L);
        }
        if (creativeGroup.getMinOptimizationWeight() == null) {
            creativeGroup.setMinOptimizationWeight(0L);
        }
    }

    /**
     * Saves an edited Creative Group
     * @param id The Creative Group ID to update.
     * @param creativeGroup The Creative Group
     * @param key The Oauth key to track who is executing this operation
     * @return a CreativeGroup with the updates applied
     * @throws Exception
     */
    public CreativeGroup update(Long id, CreativeGroup creativeGroup, OauthKey key) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("The id cannot be null");
        }
        if (creativeGroup == null) {
            throw new IllegalArgumentException("Creative Group cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        String className = creativeGroup.getClass().getSimpleName();
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(creativeGroup, className);
        validator.validatePUT(creativeGroup, id, validationResult);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        SqlSession session = creativeGroupDao.openSession();
        CreativeGroup result;
        try {
            //check access control
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(creativeGroup.getId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeGroupId", Long.toString(creativeGroup.getId()), key.getUserId()));
            }
            CreativeGroup target = creativeGroupDao.get(creativeGroup.getId(), key, session);
            if (target == null) {
                throw new ValidationException("Can't update record if it doesn't exist");
            }
            if (StringUtils.isBlank(creativeGroup.getName())) {
                creativeGroup.setName(target.getName());
            }
            checkDuplicatedNameAndGeoTargeting(creativeGroup, session);
            result = this.creativeGroupDao.update(creativeGroup, key, session);
            //update externalID
            String externalId = extendedPropertiesDao.updateExternalId("CreativeGroup", "MediaID",
                    creativeGroup.getId(), creativeGroup.getExternalId(), session);
            result.setExternalId(externalId);

            creativeGroupDao.commit(session);
        } catch (Exception e) {
            creativeGroupDao.rollback(session);
            throw e;
        } finally {
            creativeGroupDao.close(session);
        }
        log.info(key.toString() + " Updated " + id);
        return result;
    }

    private void checkDuplicatedNameAndGeoTargeting(CreativeGroup creativeGroup, SqlSession session)
            throws Exception {
        Boolean exists = creativeGroupDao.creativeGroupExists(creativeGroup, session);
        if (exists) {
            throw new ConflictException("Creative Group name already exists.");
        }

        validateGeoTargeting(creativeGroup, session);
    }

    protected void validateGeoTargeting(CreativeGroup creativeGroup, SqlSession session) throws Exception {
        try {
            if (creativeGroup.getDoGeoTargeting() == null) {
                throw new ValidationException(ResourceBundleUtil.getString("global.error.empty", "DoGeoTargeting"));
            } else if (creativeGroup.getDoGeoTargeting().equals(1L)) {
                if (creativeGroup.getGeoTargets() == null || creativeGroup.getGeoTargets().isEmpty()) {
                    throw new ValidationException("A geo target must be provided if geo targeting is enabled.");
                } else {
                    List<Long> targetIds = new ArrayList<>();
                    for (GeoTarget geoTarget : creativeGroup.getGeoTargets()) {
                        for (GeoLocation geoLocation : geoTarget.getTargets()) {
                            targetIds.add(geoLocation.getId());
                        }
                    }
                    List<Long> invalidTargetValues = creativeGroupDao.getInvalidTargetValues(targetIds, session);
                    if (!invalidTargetValues.isEmpty()) {
                        throw new ValidationException("Invalid Target Value Ids:" + invalidTargetValues.toString());
                    }
                }
            }
        } catch (Exception e) {
            throw new ValidationException("Unable to parse GEO target values");
        }
    }

    /**
     * Mark an Creative Group as deleted, Note that is a logical deletion.
     *
     * @param id The ID of the Creative Group
     * @param key Session ID of the user who updates the CreativeGroup.
     * @param recursiveDelete
     * @return the Creative Group that has been marked as deleted
     * @throws Exception
     */
    public SuccessResponse remove(Long id, OauthKey key, Boolean recursiveDelete) throws Exception {
        //null validations
        if(id == null){
            throw new IllegalArgumentException("Creative Id cannot be null");
        }
        if(key == null){
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        //Obtain session
        SqlSession session = creativeGroupDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("SecurityCode.ILLEGAL_USER_CONTEXT"));
            }

            CreativeGroup creativeGroup = creativeGroupDao.get(id, key, session);
            boolean isSystemDefault = Constants.SYSTEM_DEFAULT_CREATIVE_GROUP_NAME.equals(creativeGroup.getName());

            if (isSystemDefault) {
                throw new ValidationException("Cannot delete the system default Creative Group.");
            }

            List<CreativeInsertion> schedules = creativeInsertionDao.getCreativeInsertionsByGroupId(
                    id, key, session);
            if (schedules != null && schedules.size() > 0) {
                if (recursiveDelete != null && recursiveDelete) {
                    for (CreativeInsertion crInsertion : schedules) {
                        creativeInsertionDao.delete(crInsertion.getId(), key, session);
                    }
                } else {
                    throw new ValidationException("Cannot delete Creative Group with schedules.");
                }
            }

            creativeGroupDao.deleteCreativeFromCreative(id, key.getTpws(), session);
            creativeGroupDao.remove(id, key, session);
            creativeGroupDao.commit(session);
        } catch (PersistenceException e) {
            creativeGroupDao.rollback(session);
            throw e;
        } finally {
            creativeGroupDao.close(session);
        }
        log.info("Creative Group Id = {} Deleted by User = {} ", id, key.toString());
        return new SuccessResponse("Creative Group " + id + " successfully deleted.");
    }

    /**
     * Saves the association between Creative and Creative Groups
     *
     * @param id The Id of the Creative Group
     * @param creativeGroupCreative The CreativeGroupCreativeObject
     * @param key The Oauth key to track who is executing this operation
     * @return An association between Creative and Creative Group.
     * @throws Exception
     */
    public CreativeGroupCreative saveCreativeGroupCreative(Long id, CreativeGroupCreative creativeGroupCreative, OauthKey key) throws Exception {

        //validations
        if (id == null || creativeGroupCreative.getCreativeGroupId() == null
                || id.compareTo(creativeGroupCreative.getCreativeGroupId()) != 0) {
            throw new ValidationException("Identifier in URL does not match resource in request body.");
        }

        //obtain session
        SqlSession session = creativeGroupCreativeDao.openSession();
        CreativeGroupCreative result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(id), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(creativeGroupCreative.getCreativeId()), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            CreativeGroupCreative groupRelationship = this.creativeGroupCreativeDao.get(id,
                    creativeGroupCreative.getCreativeId(), session);
            if (groupRelationship == null) {
                creativeGroupCreative.setCreatedTpwsKey(key.getTpws());
                result = this.creativeGroupCreativeDao.save(creativeGroupCreative, session);
            } else {
                result = this.creativeGroupCreativeDao.update(creativeGroupCreative, key, session);
            }
            this.creativeGroupCreativeDao.commit(session);
        } catch (Exception e) {
            this.creativeGroupCreativeDao.rollback(session);
            throw e;
        } finally {
            this.creativeGroupCreativeDao.close(session);
        }
        log.info(key.toString() + " Saved " + id);
        return result;
    }

    /**
     * Gets a RecordSet of CreativeGroupCreatives.
     *
     * @param creativeGroupId The ID of CreativeGroup
     * @param key
     * @return The CreativeGroupCreative's RecordSet.
     * @throws Exception
     */
    public RecordSet<CreativeGroupCreative> getCreativeGroupCreativesByCreativeGroup(Long creativeGroupId, OauthKey key) throws Exception {
        //validations

        //session
        SqlSession session = creativeGroupDao.openSession();
        RecordSet<CreativeGroupCreative> records = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(creativeGroupId), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeGroupId", Long.toString(creativeGroupId), key.getUserId()));
            }
            CreativeGroup group = creativeGroupDao.get(creativeGroupId, key, session);
            if (group == null) {
                throw new NotFoundException(ResourceBundleUtil.getString("global.error.recordNotFound", creativeGroupId));
            }

            SearchCriteria criteria = new SearchCriteria();
            criteria.setQuery("creativeGroupId equals to " + creativeGroupId);
            records = this.creativeGroupCreativeDao.getByCreativeGroup(criteria, key, session);
        } catch (PersistenceException e) {
            creativeGroupDao.rollback(session);
            throw e;
        } finally {
            creativeGroupDao.close(session);
        }
        return records;
    }

    /**
     * Update associations CreativeGroup - Creatives. Method for use in
     * Services.
     *
     * @param id CreativeGroup ID
     * @param creativeGroupCreative CreativeGroupCreative with the new list of
     * CreativeGroupCreatives
     * @param key
     * @return CreativeGroupCreative with the updated list of Creatives.
     * @throws Exception
     */
    public CreativeGroupCreative updateCreativeGroupCreatives(Long id,
                                                              CreativeGroupCreative creativeGroupCreative,
                                                              OauthKey key) throws Exception {
        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        if (creativeGroupCreative == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "CreativeGroupCreative"));
        }

        //session
        SqlSession session = creativeGroupCreativeDao.openSession();
        CreativeGroupCreative result = null;
        try {
            result = this.updateCreativeGroupCreatives(id, creativeGroupCreative, key, session);
            creativeGroupCreativeDao.commit(session);
        } catch (Exception e) {
            creativeGroupCreativeDao.rollback(session);
            throw e;
        } finally {
            creativeGroupCreativeDao.close(session);
        }
        return result;
    }

    /**
     * Update associations CreativeGroup - Creatives. Method for use into
     * Managers
     *
     * @param id CreativeGroup ID
     * @param creativeGroupCreative CreativeGroupCreative with the new list of
     * CreativeGroupCreatives
     * @param key
     * @param session
     * @return CreativeGroupCreative with the updated list of Creatives.
     * @throws Exception
     */
    private CreativeGroupCreative updateCreativeGroupCreatives(Long id,
                                                               CreativeGroupCreative creativeGroupCreative,
                                                               OauthKey key, SqlSession session) throws Exception {
        //Do validation and find the creatives we need to save off and removes duplicates
        List<Long> creativesToSave = validationAndCreativesToSave(creativeGroupCreative, key,
                session, id);

        try {
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(id), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(
                        SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            List<CreativeGroupCreative> existentCGC = creativeGroupCreativeDao.getCreativeGroupCreativesByCG(
                    id, key, session);
            List<Long> existentCreatives = new ArrayList<>();
            for (int i = 0; i < existentCGC.size(); i++) {
                existentCreatives.add(i, existentCGC.get(i).getCreativeId());
            }
            List<Long> addRecord = new ArrayList<>(creativesToSave.size());
            for (Long temp : creativesToSave) {
                addRecord.add(temp.longValue());
            }
            List<Long> removeRecord = new ArrayList<>(existentCreatives.size());
            for (Long temp : existentCreatives) {
                removeRecord.add(temp.longValue());
            }
            addRecord.removeAll(existentCreatives);
            removeRecord.removeAll(creativesToSave);

            //takes records and adds them to the creativeGroupCreative dao
            addRecordsToDb(addRecord, key, session, id);
            //removes records from the creativeGroupCreative dao
            removeRecordsFromDb(removeRecord, key, session, id);

            List<CreativeGroupCreative> resultObject = creativeGroupCreativeDao.getCreativeGroupCreativesByCG(id, key, session);
            List<Creative> result = new ArrayList<>();
            for (int i = 0; i < resultObject.size(); i++) {
                Long creativeId = resultObject.get(i).getCreativeId();
                if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(creativeId), key.getUserId(), session)) {
                    throw BusinessValidationUtil.buildAccessSystemException(
                            SecurityCode.ILLEGAL_USER_CONTEXT);
                }
                Creative temp = creativeDao.get(creativeId, session);
                result.add(temp);
            }
            creativeGroupCreative.setCreatives(result);
        } catch (PersistenceException e) {
            throw e;
        }
        log.info(key.toString() + " Updated " + id);
        return creativeGroupCreative;
    }

    /**
     * Do validation and find the creatives we need to save off and removes duplicates
     * @param creativeGroupCreative
     * @param key
     * @param session
     * @param id
     * @return
     * @throws Exception
     */
    private List<Long> validationAndCreativesToSave(CreativeGroupCreative creativeGroupCreative, OauthKey key, SqlSession session, Long id) throws Exception{
        if (id != null && creativeGroupCreative.getCreativeGroupId() != null
                && !id.equals(creativeGroupCreative.getCreativeGroupId())) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.mismatchingId"));
        }

        List<Creative> creatives = creativeGroupCreative.getCreatives();
        List<Long> creativesToSave = new ArrayList<>();
        List<Long> duplicatedCreatives = new ArrayList<>();
        for (int i = 0; i < creatives.size(); i++) {
            duplicatedCreatives.add(i, creatives.get(i).getId());
        }
        Iterator<Long> iterator = duplicatedCreatives.listIterator();
        while (iterator.hasNext()) {
            Long creativeId = (Long) iterator.next();
            if (!creativesToSave.contains(creativeId)) {
                creativesToSave.add(creativeId);
            }
        }
        //Obtain campaignId of CreativeGroup
        CreativeGroup group = creativeGroupDao.get(id, key, session);
        //validate campaigns of creatives = campaign of creativeGroup
        Long creativesCount = creativeDao.getCountCreativesByCampaignIdAndIds(creativesToSave,
                group.getCampaignId(), session);
        if (creativesCount != creativesToSave.size()) {
            throw new ValidationException("Creatives does not belong to the same Creative Group campaign.");
        }

        return creativesToSave;
    }

    /**
     * removes records from the creativeGroupCreative dao
     * @param removeRecord
     * @param key
     * @param session
     * @param id
     * @throws Exception
     */
    private void removeRecordsFromDb(List<Long> removeRecord, OauthKey key, SqlSession session, Long id) throws Exception{
        if (removeRecord.size() > 0) {
            for (int i = 0; i < removeRecord.size(); i++) {
                if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(removeRecord.get(i)), key.getUserId(), session)) {
                    throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
                }
                creativeGroupCreativeDao.remove(id, removeRecord.get(i), session);
            }
        }
    }

    private void addRecordsToDb(List<Long> addRecord, OauthKey key, SqlSession session, Long id) throws Exception{
        if (addRecord.size() > 0) {
            for (int i = 0; i < addRecord.size(); i++) {
                if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(addRecord.get(i)), key.getUserId(), session)) {
                    throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
                }
                CreativeGroupCreative temp = new CreativeGroupCreative();
                temp.setCreativeGroupId(id);
                temp.setCreativeId(addRecord.get(i));
                temp.setDisplayOrder(0L);
                temp.setDisplayQuantity(0L);
                temp.setCreatedTpwsKey(key.getTpws());
                creativeGroupCreativeDao.save(temp, session);
            }
        }
    }

    /**
     * Returns the Publisher based on the ID
     *
     * @param id ID of the Publisher to return
     * @param key
     * @return the Publisher of the id
     * @throws java.lang.Exception
     */
    public ScheduleSet getSchedule(Long id, OauthKey key) throws Exception {
        SqlSession session = creativeGroupDao.openSession();
        ScheduleSet result;
        try {
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(id), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            result = this.creativeInsertionDao.getScheduleSet(id, session);
            creativeGroupDao.commit(session);
        } finally {
            creativeGroupDao.close(session);
        }
        return result;
    }

    /**
     * Create Creative Group - Creative associations.
     *
     * @param associations The {@code CreativeGroup} List IDs and creatives List
     * data
     * @param key The Oauth key to track who is executing this operation
     * @return an updated {@code SiteMeasurement} DTO
     */
    public Either<Errors, String> createAssociations(CreativeGroupCreativeDTO associations,
                                                     OauthKey key) {
        // Nullability checks
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        if (associations == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "CreativeGroupCreativeDTO"));
        }

        // payload validation
        if (associations.getCampaignId() == null) {
            throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INVALID,
                    "CreativeGroupCreativeDTO.campaignId");
        }
        if (associations.getCreatives() == null || associations.getCreatives().isEmpty()) {
            throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INVALID,
                    "CreativeGroupCreativeDTO.creatives");
        }

        Set<String> filenames = new HashSet<>();
        for (Creative creative : associations.getCreatives()) {
            if (creative != null && !StringUtils.isBlank(creative.getFilename())) {
                filenames.add(creative.getFilename());
            }
        }
        if (filenames.isEmpty()) {
            throw BusinessValidationUtil.buildBusinessSystemException(ValidationCode.REQUIRED,
                    "Creative.filename");
        }

        //Remove duplicates from filenames and Creative ids
        List<Creative> creativesToSave = cleanDuplicateCreatives(associations);

        //session
        SqlSession session = creativeGroupCreativeDao.openSession();
        try {
            // Check access control - throws exceptions on failure.
            if (!userValidFor(AccessStatement.CAMPAIGN,
                    Collections.singletonList(associations.getCampaignId()), key.getUserId(),
                    session)) {
                throw BusinessValidationUtil
                        .buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            // Validate groupIds:
            // 1. get creativeData for determinate if the creative is new or existing
            // 2. validate groupIds for new creatives
            // 3. Validate duplicate alias for existing creatives

            // 1.
            List<Creative> existingCreatives = creativeDao
                    .getByCampaignIdAndFileNames(associations.getCampaignId(), filenames, session);
            Set<Long> groupIds = new HashSet<>();
            if (existingCreatives.size() < filenames.size()) {
                // 2.
                //Remove duplicates from creativeGroupsIds
                for (Long id : associations.getCreativeGroupIds()) {
                    if (id != null) {
                        groupIds.add(id);
                    }
                }
                if (groupIds.isEmpty()) {
                    throw BusinessValidationUtil
                            .buildBusinessSystemException(ValidationCode.REQUIRED,
                                    "CreativeGroupCreativeDTO.creativeGroupIds");
                }
                // DAC
                if (!userValidFor(AccessStatement.CREATIVE_GROUP, groupIds, key.getUserId(),
                        session)) {
                    throw BusinessValidationUtil
                            .buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
                }
                //Check the creativeGroups belongs to a campaignId
                Long groups = creativeGroupDao.getCountCreativeGroupsByCampaignId(
                        associations.getCampaignId(), groupIds, session);
                if (groups != groupIds.size()) {
                    throw BusinessValidationUtil.buildBusinessSystemException(
                            BusinessCode.NOT_FOUND, "creativeGroup");
                }
            }

            // 3.
            Either<Errors, Void> validateAlias = aliasValidations(associations.getCampaignId(), existingCreatives,
                            creativesToSave, session);
            if (validateAlias != null && validateAlias.isError()) {
                return Either.error(validateAlias.error());
            }
            // 4. process creatives
            HashMap<String, Creative> existingMap = new HashMap<>();
            for (Creative creative : existingCreatives) {
                existingMap.put(creative.getFilename(), creative);
            }
            for (Creative creativeToProcess : creativesToSave) {
                //check: is a creative existent or new
                if (existingMap.containsKey(creativeToProcess.getFilename())) {
                    Creative creative = existingMap.get(creativeToProcess.getFilename());
                    // if the Id in the Creative is not set, set it
                    if (creativeToProcess.getId() == null) {
                        creativeToProcess.setId(creative.getId());
                    }

                    //should not be able to set the width and height after a creative has been associated once.
                    creativeToProcess.setWidth(null);
                    creativeToProcess.setHeight(null);

                    GenericUtils.copyOnlyPopulatedFields(creative, creativeToProcess);

                    updateExistingCreativeForVersioning(creative, key, session);
                } else { // new creative
                    // if either height a/o width is specified, use them; else indicate not-specified
                    Long width = creativeToProcess.getWidth() != null ? creativeToProcess
                            .getWidth() : Constants.UNSET_WIDTH_OR_HEIGHT;
                    Long height = creativeToProcess.getHeight() != null ? creativeToProcess
                            .getHeight() : Constants.UNSET_WIDTH_OR_HEIGHT;

                    // TODO: Review this validations, the sizes are only providen on 3rd files
                    if (width > Constants.MAX_SIZE_WIDTH || height > Constants.MAX_SIZE_HEIGHT) {
                        throw new ValidationException(
                                ResourceBundleUtil.getString("FileUploadCode.CREATIVE_TOO_BIG",
                                        width, height, Constants.MAX_SIZE_WIDTH));
                    }

                    String filePath = CreativeManager.buildTempPathFor(associations.getCampaignId(),
                            creativeToProcess.getFilename());

                    //persist creative
                    Creative creative = null;
                    try (InputStream inputStream = utilityWrapper.createFile(filePath)) {
                        // TODO: Review this validation
                        boolean isExpandable = (creativeToProcess.getIsExpandable() != null && creativeToProcess.getIsExpandable() != 0L);
                        creative = creativeManager.saveCreativeFile(inputStream, creativeToProcess,
                                associations.getCampaignId(), height, width,
                                isExpandable, ALLOW_VERSIONING, key, session);
                    }
                    //Create group associations for new creatives and persist in db.
                    updateCreativeGroupCreatives(session, key, creative, new ArrayList<>(groupIds));
                }
            }
            creativeGroupCreativeDao.commit(session);

            //remove file(s) from temp
            removeCreativesFromTempDir(associations, creativesToSave);
        } catch (ConflictException e) {
            creativeGroupCreativeDao.rollback(session);
            throw BusinessValidationUtil
                    .buildBusinessSystemException(e, BusinessCode.DUPLICATE, "filename");
        } catch (FileNotFoundException e) {
            creativeGroupCreativeDao.rollback(session);
            throw BusinessValidationUtil
                    .buildBusinessSystemException(e, BusinessCode.NOT_FOUND, "filename");
        } catch (SystemException e) {
            creativeGroupCreativeDao.rollback(session);
            throw e;
        } catch (Exception e) {
            creativeGroupCreativeDao.rollback(session);
            throw BusinessValidationUtil
                    .buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, e.getMessage());
        } finally {
            creativeGroupCreativeDao.close(session);
        }
        return Either.success(ResourceBundleUtil.getString("creative.info.successfullyAssociated"));
    }

    private List<Creative> cleanDuplicateCreatives(CreativeGroupCreativeDTO associations){
        TreeSet hs = new TreeSet<>(new Comparator<Creative>(){
            // filter out duplicate Creative filenames
            public int compare(Creative c1, Creative c2) {
                // have to allow for Creatives that hve no filename specified
                if (c1.getFilename() != null && c2.getFilename() != null &&
                        c1.getFilename().equalsIgnoreCase(c2.getFilename())) {
                    return 0;
                }
                return 1;
                // technically, this jumbles the order since never returns a -1
                // but this does serve to eliminate duplicate filenames
            }
        });
        hs.addAll(associations.getCreatives());     // arg must be of type Collection<Creative>

        List<Creative> creativesToSave = new ArrayList<>();
        creativesToSave.addAll(hs);

        return creativesToSave;
    }

    public Either<Errors, Void> aliasValidations(Long campaignId, List<Creative> existing,
                                                 List<Creative> toProcess,
                                                 SqlSession session) {
        // 1. Validate alias for existent creatives.
        Errors errors = new Errors();
        HashMap<String, Creative> creativeMap = new HashMap<>();
        for (Creative creative : toProcess) {
            if (StringUtils.isBlank(creative.getAlias())) {
                String message = ResourceBundleUtil.getString("global.error.empty", "Creative.alias");
                ValidationError error = new ValidationError(message, ValidationCode.REQUIRED);
                error.setField("alias");
                error.setObjectName(creative.getFilename());
                errors.getErrors().add(error);
                return Either.error(errors);
            }

            if (creative.getAlias().length() > Constants.DEFAULT_CHARS_LENGTH) {
                ValidationError error = new ValidationError();
                error.setMessage(ResourceBundleUtil
                    .getString("global.error.invalidStringLength", "Creative.alias",
                            Constants.DEFAULT_CHARS_LENGTH));
                error.setCode(ValidationCode.INVALID);

                error.setField("alias");
                error.setObjectName(creative.getFilename());
                errors.getErrors().add(error);
                return Either.error(errors);
            }

            creativeMap.put(creative.getFilename(), creative);
        }

        // 2. Validate dup alias for creativeId
        Set<String> creativeIdAlias = new HashSet<>();
        for (Creative creativeDB : existing) {
            Creative creativeToProcess = creativeMap.get(creativeDB.getFilename());
            String idAlias = creativeDB.getId() + Constants.VALUE_SEPARATOR + creativeToProcess.getAlias();
            creativeIdAlias.add(idAlias);
        }

        // check in db dup alias
        List<Creative> dupAlias = creativeDao
                .getDupVersionAliasByCampaignAndCreativeIdAlias(campaignId,
                        creativeIdAlias, session);
        if (!dupAlias.isEmpty()) {
            for (Creative dup : dupAlias) {
                String message = ResourceBundleUtil.getString(
                        "creative.error.versionAliasDuplicate", dup.getAlias(), dup.getFilename());
                ValidationError error = new ValidationError(message, ValidationCode.DUPLICATE);
                error.setField("alias");
                error.setRejectedValue(dup.getAlias());
                error.setObjectName(dup.getFilename());
                errors.getErrors().add(error);
            }
            return Either.error(errors);
        }
        return null;
    }

    private void updateExistingCreativeForVersioning(Creative creative, OauthKey key,
                                                     SqlSession session) {
        //get existing entries in the creative_version table for this creative_id.
        //This is ordered VERSION_NUMBER descending
        List<CreativeVersion> creativeVersionList =
                creativeDao.getCreativeVersionsById(creative.getId(), session);

        //existing list of creative_versions
        if (creativeVersionList != null && !creativeVersionList.isEmpty()) {
            //filter through and see if there are none in the pending state.
            boolean isVersionInPendingState = false;
            for (CreativeVersion creativeVersion : creativeVersionList) {
                if (creativeVersion.getIsDateSet() == 0L) {
                    isVersionInPendingState = true;
                    break;
                }
            }

            boolean isFirstEntry = isVersionInPendingState && creative.getCreativeVersion() == 1L;
            creative.setAlias(getAppropriateAlias(creative, isFirstEntry));

            //if there is one in the pending state already we just need to update the alias in the Creative_Version and Creative table.
            if (isVersionInPendingState == true) {
                creativeDao.updateCreativeAlias(creative.getId(), creative.getAlias(), key, session);
            } else { 
                //none in the pending state we need to increment to the latest version
                //index 0 should have the highest current version number
                creative.setCreativeVersion(creativeVersionList.get(0).getVersionNumber() + 1L);
                creativeDao.insertCreativeVersion(creative, session);
            }
        } else { //no existing creative_version entries associated with this creative.
            creative.setCreativeVersion(Constants.DEFAULT_CREATIVE_INITIAL_VERSION);
            creativeDao.insertCreativeVersion(creative, session);
        }
    }

    protected String getAppropriateAlias(Creative creative, boolean isFirstEntry) {
        String alias = "";
        //if the alias exists then use it.
        //If it doesn't exist or the alias is simply the filename without the extension
        //then we need to add the "_<version number>" at the end
        String filenameWithoutExtension = AdminFile.getFilenameWithoutExtension(
                creative.getFilename());

        if (StringUtils.isBlank(creative.getAlias())) {
            if (isFirstEntry) {
                alias = filenameWithoutExtension;
            } else {
                alias = filenameWithoutExtension + "_" + creative.getCreativeVersion();
            }
        } else {
            alias = creative.getAlias();
        }
        return alias;
    }

    private void removeCreativesFromTempDir(CreativeGroupCreativeDTO associations, List<Creative> creativesToSave){
        for (Creative fileToDelete : creativesToSave) {
            if (fileToDelete.getId() == null){
                String filePath = CreativeManager.buildTempPathFor(associations.getCampaignId(), fileToDelete.getFilename());
                AdminFile.deleteFile(filePath);
            }
        }
    }

    private void updateCreativeGroupCreatives(SqlSession session, OauthKey key, Creative creative, List<Long> groupsToSave) throws Exception{
        for (Long creativeGroupId : groupsToSave) {
            List<Creative> creatives = new ArrayList<>();
            creatives.add(creative);

            CreativeGroupCreative cgc = new CreativeGroupCreative();
            cgc.setCreativeGroupId(creativeGroupId);

            //persisted associations --> new associations
            List<CreativeGroupCreative> listAssociations = creativeGroupCreativeDao.getCreativeGroupCreativesByCG(creativeGroupId, key, session);
            for (CreativeGroupCreative listAssociation : listAssociations) {
                creatives.add(listAssociation.getCreative());
            }
            cgc.setCreatives(creatives);
            this.updateCreativeGroupCreatives(creativeGroupId, cgc, key, session);
        }
    }

    /**
     * Get a RecocrdSet of CreativeGroupCreativeView in order to create new
     * Creative Insertions
     *
     * @param campaignId
     * @param filterParam
     * @param key
     * @return
     */
    public Either<Errors, RecordSet<CreativeGroupCreativeView>> getGroupCreativesByCreativeInsertionFilterParam(Long campaignId, CreativeInsertionFilterParam filterParam, OauthKey key) {
        if (campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Campaign Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();
        // Validate payload
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(filterParam, "creativeInsertionFilterParam");
        filterParamValidator.validateToGetGroupCreatives(filterParam, validationResult);
        if (validationResult.hasErrors()) {
            errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(validationResult));
            return Either.error(errors);
        }

        RecordSet<CreativeGroupCreativeView> result = null;
        SqlSession session = creativeGroupCreativeDao.openSession();
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
            List<CreativeGroupCreativeView> groupCreatives = creativeGroupCreativeDao.getGroupCreativesByFilterParam(campaignId, filterParam, key.getUserId(), session);
            Long counterResult = creativeGroupCreativeDao.getCountGroupCreativesByFilterParam(campaignId, filterParam, session);
            result = new RecordSet<>(SearchCriteria.SEARCH_CRITERIA_START_INDEX, SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE, counterResult.intValue(), groupCreatives);

        } finally {
            creativeGroupCreativeDao.close(session);
        }
        return Either.success(result);
    }

    /**
     * Interface to make this class testable
     * */
    interface UtilityWrapper {
        InputStream createFile(String path) throws FileNotFoundException;
    }

    /**
     * Default implementation for {@code FileWrapper}
     */
    class UtilityWrapperImpl implements UtilityWrapper {
        @Override
        public InputStream createFile(String path) throws FileNotFoundException{
            return new FileInputStream(path);
        }
    }
}

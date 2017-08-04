package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.APIException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.DuplicateCIError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.BulkCreativeInsertion;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionBulkUpdate;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionExtendedView;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionSearchOptions;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.validation.CreativeGroupValidator;
import trueffect.truconnect.api.crud.validation.CreativeInsertionFilterParamValidator;
import trueffect.truconnect.api.crud.validation.CreativeInsertionRawDataViewValidator;
import trueffect.truconnect.api.crud.validation.CreativeInsertionSpringValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 *
 * @author Abel Soto, Richard Jaldin, Jason Geraheart, Marcelo Heredia
 */
public class CreativeInsertionManager extends AbstractGenericManager {

    private CreativeInsertionDao creativeInsertionDao;
    private CampaignDao campaignDao;
    private PlacementDao placementDao;
    private CreativeDao creativeDao;
    private CreativeGroupDao creativeGroupDao;
    private CreativeGroupCreativeDao creativeGroupCreativeDao;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private CreativeInsertionRawDataViewValidator creativeIsertionRawDataViewValidator;
    private CreativeInsertionFilterParamValidator creativeInsertionFilterParamValidator;
    private CreativeInsertionSpringValidator springValidator;
    private CreativeGroupValidator groupValidator;

    public CreativeInsertionManager(CreativeInsertionDao creativeInsertionDao, CampaignDao campaignDao, PlacementDao placementDao,
            CreativeDao creativeDao, CreativeGroupDao creativeGroupDao, CreativeGroupCreativeDao creativeGroupCreativeDao, 
            AccessControl accessControl) {
        super(accessControl);
        this.creativeInsertionDao = creativeInsertionDao;
        this.campaignDao = campaignDao;
        this.placementDao = placementDao;
        this.creativeDao = creativeDao;
        this.creativeGroupDao = creativeGroupDao;
        this.creativeGroupCreativeDao = creativeGroupCreativeDao;
        creativeIsertionRawDataViewValidator = new CreativeInsertionRawDataViewValidator();
        creativeInsertionFilterParamValidator = new CreativeInsertionFilterParamValidator();
        springValidator = new CreativeInsertionSpringValidator();
        groupValidator = new CreativeGroupValidator();
    }

    /**
     * Returns the CreativeInsertion based on the ID
     *
     * @param id CreativeInsertion order ID number and primary key
     * @param key
     * @return the CreativeInsertion of the id
     * @throws Exception
     */
    public CreativeInsertion get(Long id, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        //Obtain session
        SqlSession session = this.creativeInsertionDao.openSession();
        CreativeInsertion result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.CREATIVE_INSERTION, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeInsertionId", Long.toString(id), key.getUserId()));
            }
            result = this.creativeInsertionDao.get(id, session);
            if(result == null ){
                throw new NotFoundException("Creative Insertion not found");
            }
        } finally {
            creativeInsertionDao.close(session);
        }
        return result;
    }

    /**
     * Get records by a Search Criteria
     *
     * @param searchCriteria The SearchCriteria to search for.
     * @param key the OAuthKey that contains the userId that executes this method
     * @return Returns a RecordSet of CreativeInsertions
     * @throws Exception
     */
    public RecordSet<CreativeInsertion> get(SearchCriteria searchCriteria, OauthKey key) throws Exception {

        //null validations
        if(key == null){
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        //Obtain session
        SqlSession session = this.creativeInsertionDao.openSession();
        RecordSet<CreativeInsertion> result = null;
        try {
            // SearchCriteria validations are performed in DAO
            result = creativeInsertionDao.get(searchCriteria, key, session);
        } finally {
            creativeInsertionDao.close(session);
        }
        return result;
    }

    /**
     * Get Creative Insertion by Creative Group Id
     *
     * @param creativeGroupId The Id of the Creative group.
     * @param key
     * @return List Creative Group Creative.
     * @throws Exception
     */
    public RecordSet<CreativeInsertion> getByCreativeGroupId(Long creativeGroupId, OauthKey key) throws Exception {
        //null validations
        if (creativeGroupId == null) {
            throw new IllegalArgumentException("CreativeGroupId Id cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        //Obtain session
        SqlSession session = this.creativeInsertionDao.openSession();
        List<CreativeInsertion> result = null;
        try {
            //check accces control
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(creativeGroupId), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeGroupId", Long.toString(creativeGroupId), key.getUserId()));
            }

            result = creativeInsertionDao.getCreativeInsertionsByGroupId(creativeGroupId, key,
                    session);
        } finally {
            creativeInsertionDao.close(session);
        }

        RecordSet<CreativeInsertion> resultRecordSet = new RecordSet<>(0, result.size(), result.size(), result);
        return resultRecordSet;
    }

    /**
     * Get creative insertions related a Creative Id
     *
     * @param creativeId
     * @param startIndex
     * @param pageSize
     * @param key The {@code OauthKey} for the requester user
     * @return CreativeInsertion saved
     */
    public Either<Errors, RecordSet<CreativeInsertionView>> getByCreativeId(Long creativeId,
                                                                            Long startIndex,
                                                                            Long pageSize,
                                                                            OauthKey key) {
        //null validations
        if (creativeId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Creative Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();

        //validations
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
        SqlSession session = creativeInsertionDao.openSession();
        RecordSet<CreativeInsertionView> result;
        try {
            // DAC
            if (!userValidFor(AccessStatement.CREATIVE, creativeId, key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // obtain creative campaignId
            Creative creative = creativeDao.get(creativeId, session);
            List<CreativeInsertionView> list = creativeInsertionDao
                    .getSchedulesByCreativeId(creative.getCampaignId(), creativeId, key.getUserId(),
                            paginator.get("startIndex"), paginator.get("pageSize"), session);
            int numberOfRecords = creativeInsertionDao.getCountSchedulesByCreativeId(
                    creative.getCampaignId(), creativeId, key.getUserId(), session).intValue();

            result = new RecordSet<>(paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(), numberOfRecords, list);
        } finally {
            creativeInsertionDao.close(session);
        }
        return Either.success(result);
    }

    public CreativeInsertion createFromSchedulesContext(CreativeInsertion creativeInsertion, OauthKey key) throws Exception {
        return createCreativeInsertion(creativeInsertion, key, false);
    }
    
    public CreativeInsertion create(CreativeInsertion creativeInsertion, OauthKey key) throws Exception {
        return createCreativeInsertion(creativeInsertion, key, true);
    }
    
    /**
     * Saves a CreativeInsertion
     *
     * @param creativeInsertion The CreativeInsertion object to create.
     * @param key
     * @return CreativeInsertion saved
     * @throws Exception
     */
    private CreativeInsertion createCreativeInsertion(CreativeInsertion creativeInsertion, OauthKey key, boolean toCreate) throws Exception {

        // null validations
        if (creativeInsertion == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "CreativeInsertion"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        // Perform Model Validations
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertion,
                creativeInsertion.getClass().getSimpleName());
        ApiValidationUtils.rejectIfPostWithId(errors, "id", ResourceBundleUtil.getString("global.label.creativeInsertion"));
        ValidationUtils.invokeValidator(springValidator, creativeInsertion, errors);
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        SqlSession session = creativeInsertionDao.openSession();
        CreativeInsertion result = new CreativeInsertion();
        try {
            List<CreativeInsertion> insertions = new ArrayList<>(Collections.singletonList(creativeInsertion));
            Either<Errors, BulkCreativeInsertion> response = createInsertionBusinessLogic(insertions, 
                    creativeInsertion.getCampaignId(), 
                    Collections.singletonList(creativeInsertion.getPlacementId()),
                    Collections.singletonList(creativeInsertion.getCreativeGroupId()),
                    Collections.singletonList(creativeInsertion.getCreativeId()),
                    key, session, toCreate);
            
            if(response.isError()) {
                Error error = response.error().getErrors().get(0);
                if (error instanceof ValidationError) {
                    throw new ValidationException(error.getMessage());
                } else if (error instanceof AccessError) {
                    throw new DataNotFoundForUserException(error.getMessage());
                } else if (error instanceof DuplicateCIError) {
                    throw new ConflictException(error.getMessage());
                } else {
                    // This should never happen
                    throw new APIException(error.getMessage());
                }
            } else {
                result = response.success().getCreativeInsertions().get(0);
                creativeInsertionDao.commit(session);
            }

        } finally {
            creativeInsertionDao.close(session);
        }

        log.debug("For user: {} Saved the creative insertion: {}", key.getUserId(), creativeInsertion);
        return result;
    }

    /**
     * Updates a CreativeInsertion
     *
     * @param id the Primary key
     * @param creativeInsertion The CreativeInsertion to update.
     * @return CreativeInsertion updated
     * @throws Exception
     */
    public CreativeInsertion update(Long id, CreativeInsertion creativeInsertion, OauthKey key) throws Exception {

        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Creative Insertion ID"));
        }

        if (creativeInsertion == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Creative Insertion"));
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));

        }
        // Perform Model Validations
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertion,
                creativeInsertion.getClass().getSimpleName());
        ApiValidationUtils.rejectIfPutWithoutId(errors,
                "id",
                ResourceBundleUtil.getString("global.error.putMissingId",
                        ResourceBundleUtil.getString("global.label.creativeInsertion")));
        ValidationUtils.invokeValidator(springValidator, creativeInsertion, errors);
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        //Obtain session
        CreativeInsertion updated = null;
        SqlSession session = creativeDao.openSession();
        try {
            //check access control
            if (!userValidFor(AccessStatement.CREATIVE_INSERTION, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeInsertionId", Long.toString(id), key.getUserId()));
            }
            CreativeInsertion ci = creativeInsertionDao.get(id, session);
            if (creativeInsertion.getStartDate() == null || creativeInsertion.getEndDate() == null) {
                if (!userValidFor(AccessStatement.PLACEMENT, Collections.singletonList(creativeInsertion.getPlacementId()), key.getUserId(), session)) {
                    // FIXME We should not throw exception here, but we don't have yet a way to
                    // return expected error situations as errors and not as exceptions (current approach)
                    throw new DataNotFoundForUserException("PlacementId: "+ creativeInsertion.getPlacementId() +" Not found for User: " + key.getUserId());
                }
                Placement placement = placementDao.get(ci.getPlacementId(), session);
                creativeInsertion.setStartDate(placement.getStartDate());
                creativeInsertion.setEndDate(placement.getEndDate());
            }
            if (creativeInsertion.getWeight() == null) {
                creativeInsertion.setWeight(ci.getWeight());
            }
            if (creativeInsertion.getClickthrough() == null) {
                if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(creativeInsertion.getCreativeId()), key.getUserId(), session)) {
                    throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
                }
                Creative creative = creativeDao.get(ci.getCreativeId(), session);
                creativeInsertion.setClickthrough(creative.getClickthrough());
            }
            creativeInsertion.setCreativeType(ci.getCreativeType());
            springValidator.validateClickthroughUpdate(creativeInsertion, errors);
            if (errors.hasErrors()) {
                throw new ValidationException(errors);
            }
            updated = this.creativeInsertionDao.update(creativeInsertion, session, key);
            creativeDao.commit(session);
        } catch (Exception e) {
            creativeDao.rollback(session);
            throw e;
        } finally {
            creativeDao.close(session);
        }
        return updated;
    }

    public CreativeInsertionBulkUpdate bulkUpdate(CreativeInsertionBulkUpdate toUupdate, OauthKey key) {
        //null validations
        if (toUupdate == null) {
            return new CreativeInsertionBulkUpdate();
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        
        // Perform model validations for Insertions List
        List<Long> creativeInsertionIds = toUupdate.getCreativeInsertionIds();
        boolean hasInsertionsData = creativeInsertionIds != null && !creativeInsertionIds.isEmpty();
        if(hasInsertionsData) {
            BeanPropertyBindingResult resultErrors;
            Errors errors = new Errors();
            for (CreativeInsertion ci : toUupdate.getCreativeInsertions()) {
                resultErrors = new BeanPropertyBindingResult(ci, ci.getClass().getSimpleName());
                ApiValidationUtils.rejectIfPutWithoutId(resultErrors, "id",
                        ResourceBundleUtil.getString("global.error.putMissingId",
                                ResourceBundleUtil.getString("global.label.creativeInsertion")));
                ValidationUtils.invokeValidator(springValidator, ci, resultErrors);
                if (resultErrors.hasErrors()) {
                    errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(resultErrors));
                }
            }
            if(!errors.getErrors().isEmpty()) {
                SystemException systemException = new SystemException(ValidationCode.INVALID);
                systemException.set(ValidationConstants.KEY_VALIDATION_RESULT, errors);
                throw systemException;
            }
        }

        // Perform model validations for CreativeGroup List
        List<Long> creativeGroupIds = toUupdate.getCreativeGroupIds();
        boolean hasGroupsData = creativeGroupIds != null && !creativeGroupIds.isEmpty();
        if(hasGroupsData) {
            // Valid ID and weight values
            BeanPropertyBindingResult resultErrors;
            Errors errors = new Errors();
            for (CreativeGroup group : toUupdate.getCreativeGroups()) {
                
                resultErrors = new BeanPropertyBindingResult(group, group.getClass().getSimpleName());
                ApiValidationUtils.rejectIfPutWithoutId(resultErrors, "id",
                        ResourceBundleUtil.getString("global.error.putMissingId",
                                ResourceBundleUtil.getString("global.label.creativeGroup")));
                groupValidator.validateUpdateWeight(group, resultErrors);
                if (resultErrors.hasErrors()) {
                    errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(resultErrors));
                }
                //add insertion into map
                //ciMap.put(group.getId(), group);
            }
            if(!errors.getErrors().isEmpty()) {
                SystemException systemException = new SystemException(ValidationCode.INVALID);
                systemException.set(ValidationConstants.KEY_VALIDATION_RESULT, errors);
                throw systemException;
            }
        }
        
        //Obtain session
        SqlSession session = creativeInsertionDao.openSession();
        CreativeInsertionBulkUpdate result = new CreativeInsertionBulkUpdate();
        try {
            // DAC validations
            if (hasInsertionsData) {
                if(!userValidFor(AccessStatement.CREATIVE_INSERTION, creativeInsertionIds,
                        key.getUserId(), session)) {
                    // FIXME We should not throw exception here, but we don't have yet a way to
                    // return expected error situations as errors and not as exceptions (current approach)
                    throw BusinessValidationUtil
                            .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
                }
            }
            if (hasGroupsData) {
                // DAC 
                if(!userValidFor(AccessStatement.CREATIVE_GROUP, creativeGroupIds,
                        key.getUserId(), session)) {
                    // FIXME We should not throw exception here, but we don't have yet a way to
                    // return expected error situations as errors and not as exceptions (current approach)
                    throw BusinessValidationUtil
                            .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
                }
            }
            
            // Check that user is valid to access the requested data
            if(hasInsertionsData) {
                // *** Update the Creative Insertions ***
                // Create a map to get easy access to the deltas
                Map<Long, CreativeInsertion> ciMap = new HashMap<>();
                for (CreativeInsertion ci : toUupdate.getCreativeInsertions()) {
                    ciMap.put(ci.getId(), ci);
                }

                // UPDATE PROCESS
                int fromIndex = 0; 
                int itera = (int) Math.ceil((double)creativeInsertionIds.size()/(double)Constants.MAX_NUMBER_VALUES_IN_CLAUSE);
                List<CreativeInsertion> creativeInsertionsToUpdate = new ArrayList<>();
                for (int i = 0; i < itera; i++) {
                    int toIndex = (fromIndex + Constants.MAX_NUMBER_VALUES_IN_CLAUSE) > creativeInsertionIds.size() ? 
                            creativeInsertionIds.size(): (fromIndex + Constants.MAX_NUMBER_VALUES_IN_CLAUSE);

                    // Get all of the affected creative insertions from the database
                    SearchCriteria ciCriteria = new SearchCriteria();
                    ciCriteria.setQuery(String.format("id in [%s]", StringUtils.join(creativeInsertionIds.subList(fromIndex, toIndex), ", ")));
                    List<CreativeInsertion> resultInsertionsToUpdate = creativeInsertionDao.get(ciCriteria, key, session).getRecords();

                    BeanPropertyBindingResult resultErrors;
                    Errors errors = new Errors();
                    // Apply updates to the existing objects
                    for (CreativeInsertion existing : resultInsertionsToUpdate) {
                        // Removing from the map so that I can check if there are any that did not line up with what exists in the database.
                        CreativeInsertion toUpdate = ciMap.remove(existing.getId());
                        // Apply user updates only if they exist
                        // NOTE. All these checks should be done in the mapper, but doing that would break tpasapi
                        if (toUpdate.getStartDate() != null && toUpdate.getEndDate() != null) {
                            existing.setEndDate(toUpdate.getEndDate());
                            existing.setStartDate(toUpdate.getStartDate());
                        }
                        if (toUpdate.getClickthrough() != null) {
                            existing.setClickthrough(toUpdate.getClickthrough());
                        }
                        if (toUpdate.getClickthroughs() != null) {
                            existing.setClickthroughs(toUpdate.getClickthroughs());
                        }
                        if (toUpdate.getWeight() != null) {
                            existing.setWeight(toUpdate.getWeight());
                        }
                        resultErrors = new BeanPropertyBindingResult(existing, existing.getClass().getSimpleName());
                        springValidator.validateClickthroughUpdate(existing, resultErrors);
                        if (resultErrors.hasErrors()) {
                            errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(resultErrors));
                        }
                    }
                    if (!errors.getErrors().isEmpty()) {
                        SystemException systemException = new SystemException(ValidationCode.INVALID);
                        systemException.set(ValidationConstants.KEY_VALIDATION_RESULT, errors);
                        throw systemException;
                    }
                    creativeInsertionsToUpdate.addAll(resultInsertionsToUpdate);
                    fromIndex += Constants.MAX_NUMBER_VALUES_IN_CLAUSE;
                }
                // Check if all entries have been processed
                if (!ciMap.isEmpty()) {
                    throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.NOT_FOUND, "creative_insertion_id");
                }

                // Perform the update
                List<CreativeInsertion> updatedCreativeInsertions = creativeInsertionDao.bulkUpdate(creativeInsertionsToUpdate, session, key);

                // Set the updated creative insertions on the result object
                result.setCreativeInsertions(updatedCreativeInsertions);
            }

            // CREATIVE GROUP UPDATES
            if(hasGroupsData) {
                // *** Update the Creative Groups ***
                // Create a map to get easy access to the deltas
                Map<Long, CreativeGroup> cgMap = new HashMap<>();
                for (CreativeGroup cg : toUupdate.getCreativeGroups()) {
                    cgMap.put(cg.getId(), cg);
                }

                int fromIndex = 0;
                int itera = (int) Math.ceil((double)creativeGroupIds.size()/(double)Constants.MAX_NUMBER_VALUES_IN_CLAUSE);
                List<CreativeGroup> creativeGroupsToUpdate = new ArrayList<>();
                for (int i = 0; i < itera; i++) {
                    int toIndex = (fromIndex + Constants.MAX_NUMBER_VALUES_IN_CLAUSE) > creativeGroupIds.size() ? 
                            creativeGroupIds.size(): (fromIndex + Constants.MAX_NUMBER_VALUES_IN_CLAUSE); 
                
                    // Get all of the affected creative groups from the database
                    SearchCriteria cgCriteria = new SearchCriteria();
                    cgCriteria.setQuery(String.format("id in [%s]", StringUtils.join(creativeGroupIds.subList(fromIndex, toIndex), ", ")));
                    creativeGroupsToUpdate = creativeGroupDao.get(cgCriteria, key, session).getRecords();
                    
                    // Apply updates to the existing objects
                    for (CreativeGroup existing : creativeGroupsToUpdate) {
                        // Removing from the map so that I can check if there are any that did not line up with what exists in the database.
                        CreativeGroup toUpdate = cgMap.remove(existing.getId());
                        existing.setWeight(toUpdate.getWeight());
                    }
                    creativeGroupsToUpdate.addAll(creativeGroupsToUpdate);
                    fromIndex += Constants.MAX_NUMBER_VALUES_IN_CLAUSE;
                }

                // Check if all entries have been processed
                if (!cgMap.isEmpty()) {
                    throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.NOT_FOUND, "creative_group_id");
                }

                // Perform the update
                List<CreativeGroup> updatedCreativeGroups = creativeGroupDao.bulkUpdate(creativeGroupsToUpdate, session, key);

                // Set the updated creative groups on the result object
                result.setCreativeGroups(updatedCreativeGroups);                
            }
            creativeInsertionDao.commit(session);
        } catch (SystemException e) {
            log.warn("Bulk update of creative insertions failed.", e);
            creativeInsertionDao.rollback(session);
            throw e;
        } catch (Exception e) {
            log.warn("Bulk update of creative insertions failed.", e);
            creativeInsertionDao.rollback(session);
            throw new SystemException(e.getMessage(), e, BusinessCode.INVALID);
        } finally {
            creativeInsertionDao.close(session);
        }
        return result;
    }
    
    /**
     * Removes a CreativeInsertion
     *
     * @param id the ID of the CreativeInsertion
     * @param key The {@code OauthKey} for the requester user
     * @return The status of the remove operation.
     * @throws Exception
     */
    public SuccessResponse remove(Long id, OauthKey key) throws Exception {
        //null validations
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Creative Insertion ID"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        //Obtain session
        SqlSession session = creativeInsertionDao.openSession();
        try {
            //check access control
            if (!userValidFor(AccessStatement.CREATIVE_INSERTION, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeInsertionId", Long.toString(id), key.getUserId()));
            }
            creativeInsertionDao.delete(id, key, session);
            creativeInsertionDao.commit(session);
        } catch (Exception e) {
            creativeInsertionDao.rollback(session);
            throw e;
        } finally {
            creativeInsertionDao.close(session);
        }
        log.info("Creative Insertion = {} was deleted", id);
        return new SuccessResponse("Creative Insertion " + id + " successfully deleted");
    }

    /**
     * Get a RecocrdSet of CreativeInsertionVies by CampaignId
     *
     * @param campaignId
     * @param startIndex
     * @param pageSize
     * @param key
     * @return 
     */
    public RecordSet<CreativeInsertionView> getAllCreativeInsertions(Long campaignId,
                                                                     Long startIndex,
                                                                     Long pageSize,
                                                                     OauthKey key) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        RecordSet<CreativeInsertionView> result = null;
        SqlSession session = creativeInsertionDao.openSession();
        try {
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            }
            if (campaignDao.get(campaignId, session) == null) {
                throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.NOT_FOUND, "id");
            }
            HashMap<String, Long> paginator = new HashMap<>();
            paginator.put("startIndex",startIndex);
            paginator.put("pageSize",pageSize);
            validatePaginator(paginator);
            List<CreativeInsertionView> creativeInsertions = creativeInsertionDao.getAllCreativeInsertions(
                    campaignId, paginator.get("startIndex"), paginator.get("pageSize"), session);
            result = new RecordSet<>(
                    paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(),
                    creativeInsertionDao.getAllCreativeInsertionsCount(campaignId, session).intValue(),
                    creativeInsertions);
        } finally {
            creativeInsertionDao.close(session);
        }
        return result;
    }
    
    public Either<Error, RecordSet<CreativeInsertionView>> getCreativeInsertions(Long campaignId,
                                                                                 CreativeInsertionFilterParam filterParamIds,
                                                                                 Long startIndex,
                                                                                 Long pageSize,
                                                                                 OauthKey key) {
        if (campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Campaign Id"));
        }
        if (filterParamIds == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "filterParamIds"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        // Validate payload
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(filterParamIds, "creativeInsertionFilterParam");
        creativeInsertionFilterParamValidator.validateToGetData(filterParamIds, errors);
        if (errors.hasErrors()) {
            return Either.error(new Error(ApiValidationUtils.asCSVString(errors),
                ValidationCode.INVALID));
        }
        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex", startIndex);
        paginator.put("pageSize", pageSize);
        Either<Error, HashMap<String, Long>> validPaginator = validatePaginator(paginator);
        if(validPaginator.isError()) {
            return Either.error(validPaginator.error());
        } else {
            paginator = validPaginator.success();
        }
        
        RecordSet<CreativeInsertionView> result = null;
        SqlSession session = creativeInsertionDao.openSession();
        try {
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"), 
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
            
            // check access control into payload
            List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelTypesToValidate = new ArrayList<>(CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.
                    get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, filterParamIds.getPivotType().toUpperCase())).
                    get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, filterParamIds.getType().toUpperCase())));
            hierarchyLevelTypesToValidate.remove(hierarchyLevelTypesToValidate.size()-1);
            AccessStatement accessStatement;
            Long id;
            for (CreativeInsertionFilterParamTypeEnum levelType : hierarchyLevelTypesToValidate) {
                accessStatement = null;
                id = null;
                switch (levelType) {
                    case CREATIVE:
                        accessStatement = AccessStatement.CREATIVE;
                        id = filterParamIds.getCreativeId();
                        break;
                    case GROUP:
                        accessStatement = AccessStatement.CREATIVE_GROUP;
                        id = filterParamIds.getGroupId();
                        break;
                    case PLACEMENT:
                        accessStatement = AccessStatement.PLACEMENT;
                        id = filterParamIds.getPlacementId();
                        break;
                    case SECTION:
                        accessStatement = AccessStatement.SITE_SECTION;
                        id = filterParamIds.getSectionId();
                        break;
                    case SITE:
                        accessStatement = AccessStatement.SITE;
                        id = filterParamIds.getSiteId();
                        break;
                }
                if(!userValidFor(accessStatement, Collections.singletonList(id), key.getUserId(), session)) {
                    return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"), 
                            SecurityCode.NOT_FOUND_FOR_USER));
                }
            }

            // Business Logic
            List<CreativeInsertionView> creativeInsertions = null;
            switch (CreativeInsertionFilterParamTypeEnum.valueOf(filterParamIds.getType().toUpperCase())){
                case SITE:
                    creativeInsertions = creativeInsertionDao.getSchedulesSiteLevel(
                            campaignId, filterParamIds, paginator.get("startIndex"), paginator.get("pageSize"), session);
                    break;
                case SECTION:
                    creativeInsertions = creativeInsertionDao.getSchedulesSectionLevel(
                            campaignId, filterParamIds, paginator.get("startIndex"), paginator.get("pageSize"), session);
                    break;
                case PLACEMENT:
                    creativeInsertions = creativeInsertionDao.getSchedulesPlacementLevel(
                            campaignId, filterParamIds, paginator.get("startIndex"), paginator.get("pageSize"), session);
                    break;
                case GROUP:
                    creativeInsertions = creativeInsertionDao.getSchedulesGroupLevel(
                            campaignId, filterParamIds, paginator.get("startIndex"), paginator.get("pageSize"), session);
                    break;
                case CREATIVE:
                    creativeInsertions = creativeInsertionDao.getSchedulesCreativeLevel(
                            campaignId, filterParamIds, paginator.get("startIndex"), paginator.get("pageSize"), session);
                    break;
                case SCHEDULE:
                    creativeInsertions = creativeInsertionDao.getSchedulesScheduleLevel(
                            campaignId, filterParamIds, paginator.get("startIndex"), paginator.get("pageSize"), session);
                    break;
            }

            int numberOfRecords =
                    creativeInsertions.size() < paginator.get("pageSize").intValue() &&
                            paginator.get("startIndex").intValue() == 0 ?
                            creativeInsertions.size() :
                            creativeInsertionDao.getCountSchedulesByLevel(campaignId, filterParamIds, session).intValue();
            result = new RecordSet<>(
                    paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(),
                    numberOfRecords,
                    creativeInsertions);
        } finally {
            creativeInsertionDao.close(session);
        }
        return Either.success(result);
    }

    /**
     * Get a RecocrdSet of CreativeInsertionView result of a search
     *
     * @param campaignId
     * @param filterParamIds
     * @param searchOptions
     * @param pattern
     * @param startIndex
     * @param pageSize
     * @param key
     * @return 
     */
    public Either<Error, RecordSet<CreativeInsertionView>> searchCreativeInsertions(Long campaignId,
                                                                                    CreativeInsertionFilterParam filterParamIds,
                                                                                    CreativeInsertionSearchOptions searchOptions,
                                                                                    String pattern,
                                                                                    Long startIndex,
                                                                                    Long pageSize,
                                                                                    OauthKey key) {
        if (campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Campaign Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        if (pattern == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Pattern"));
        }
        if (filterParamIds == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "FilterParamIds"));
        }
        if (searchOptions == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Search Options"));
        }

        if (pattern.length() > Constants.SEARCH_PATTERN_MAX_LENGTH) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Pattern", Constants.SEARCH_PATTERN_MAX_LENGTH),
                    ValidationCode.INVALID));
        }
        // Validate payload
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(filterParamIds, "creativeInsertionFilterParam");
        creativeInsertionFilterParamValidator.validateToSearchData(filterParamIds, validationResult);
        if (validationResult.hasErrors()) {
            return Either.error(new Error(ApiValidationUtils.asCSVString(validationResult), 
                ValidationCode.INVALID));
        }

        // Validate paginator
        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex",startIndex);
        paginator.put("pageSize", pageSize);
        Either<Error, HashMap<String, Long>> validPaginator = validatePaginator(paginator);
        if(validPaginator.isError()) {
            return Either.error(validPaginator.error());
        } else {
            paginator = validPaginator.success();
        }
        
        RecordSet<CreativeInsertionView> result = null;
        SqlSession session = creativeInsertionDao.openSession();
        try {
            //  Check DAC
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
            
            // check access control into payload --> only if TYPE != null
            if (filterParamIds.getType() != null && !filterParamIds.getType().isEmpty()) {
                Either<Error, Void> accessControlResult = checkAccessControlForCreativeInsertionFilterParamIds(
                        new ArrayList<>(Collections.singletonList(filterParamIds)), key.getUserId(), session);
                if (accessControlResult != null && accessControlResult.isError()) {
                    return Either.error(accessControlResult.error());
                }
            }

            // Business Logic
            // Review SearchOptions
            List<CreativeInsertionView> creativeInsertions = null;
            Long counterResult = 0L;
            
            boolean isOptionsOk = verifyCleanSearchOptions(searchOptions, filterParamIds.getPivotType(), filterParamIds.getType());
            pattern = "%" + pattern + "%";
            if (isOptionsOk) {
                creativeInsertions = creativeInsertionDao.searchSchedulesLevel(
                        campaignId, filterParamIds, searchOptions, pattern, paginator.get("startIndex"), paginator.get("pageSize"), session);
                counterResult = creativeInsertionDao.searchSchedulesLevelCount(campaignId, filterParamIds, searchOptions, pattern, 
                        paginator.get("startIndex"), paginator.get("pageSize"), session);
            }
            
            result = new RecordSet<>(
                    paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(),
                    counterResult.intValue(),
                    creativeInsertions);
        } finally {
            creativeInsertionDao.close(session);
        }
        return Either.success(result);
    }

    /**
     * Performs a bulk delete of a set of CreativeInsertionFilterParam
     *
     * @param campaignId
     * @param bulkDelete The container of a list of Creative Insertion IDs
     * @param key
     * @return 
     */
    public Either<Error, String> creativeInsertionsBulkDelete(Long campaignId, RecordSet<CreativeInsertionFilterParam> bulkDelete, OauthKey key ) {

        //null validations
        if (campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Campaign ID"));
        }
        if (bulkDelete == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "bulkDelete"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        
        if (bulkDelete.getRecords() == null || bulkDelete.getRecords().isEmpty()) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.requiredField","creativeInsertionFilterParam"), 
                        ValidationCode.REQUIRED));
        }
        // Validate payload
        for (CreativeInsertionFilterParam ciBulk : bulkDelete.getRecords()) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(ciBulk, "creativeInsertionFilterParam");
            ValidationUtils.invokeValidator(creativeInsertionFilterParamValidator, ciBulk, errors);
            if (errors.hasErrors()) {
                return Either.error(new Error(ApiValidationUtils.asCSVString(errors),
                    ValidationCode.INVALID));
            }
        }        
        
        SqlSession session = creativeInsertionDao.openSession();
        try {
            //check access control
            if(!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"), 
                        SecurityCode.NOT_FOUND_FOR_USER));
            }

            // check access control into payload
            Either<Error, Void> accessControlResult = checkAccessControlForCreativeInsertionFilterParamIds(
                    bulkDelete.getRecords(), key.getUserId(), session);
            if (accessControlResult != null && accessControlResult.isError()) {
                return Either.error(accessControlResult.error());
            }

            // business logic
            for (CreativeInsertionFilterParam ciBulk : bulkDelete.getRecords()) {
                ciBulk = cleanupParams(ciBulk);
                int affected = creativeInsertionDao.bulkDeleteByFilterParam(campaignId, ciBulk,
                        key.getTpws(), session);
                if(affected <= 0) {
                    creativeInsertionDao.rollback(session);
                    return Either.error(new Error(ResourceBundleUtil.getString("BusinessCode.NOT_FOUND", "creativeInsertionId"),
                        BusinessCode.NOT_FOUND));
                }
            }
            creativeInsertionDao.commit(session);
        } finally {
            creativeInsertionDao.close(session);
        }
        String message = ResourceBundleUtil.getString("global.info.bulkOperationSuccess",
                ResourceBundleUtil.getString("global.label.delete"),
                ResourceBundleUtil.getString("global.label.creativeInsertions"));
        return Either.success(message);
    }

    private CreativeInsertionFilterParam cleanupParams(CreativeInsertionFilterParam ciBulk) {
        CreativeInsertionFilterParam result = new CreativeInsertionFilterParam();
        List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelTypes =
                CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.get(
                        CreativeInsertionFilterParamTypeEnum
                                .valueOf(CreativeInsertionFilterParamTypeEnum.class,
                                        ciBulk.getPivotType().toUpperCase())).get(
                        CreativeInsertionFilterParamTypeEnum
                                .valueOf(CreativeInsertionFilterParamTypeEnum.class,
                                        ciBulk.getType().toUpperCase()));
        for (CreativeInsertionFilterParamTypeEnum hierarchyLevelType : hierarchyLevelTypes) {
            switch (hierarchyLevelType) {
                case CREATIVE:
                case SCHEDULE:
                    result.setCreativeId(ciBulk.getCreativeId());
                    break;
                case GROUP:
                    result.setGroupId(ciBulk.getGroupId());
                    break;
                case PLACEMENT:
                    result.setPlacementId(ciBulk.getPlacementId());
                    break;
                case SECTION:
                    result.setSectionId(ciBulk.getSectionId());
                    break;
                case SITE:
                    result.setSiteId(ciBulk.getSiteId());
                    break;
            }
        }
        return result;
    }

    /**
     * Removes a CreativeInsertion
     *
     * @param id the ID of the CreativeGroup
     * @param key
     * @return The status of the remove operation.
     * @throws Exception
     */
    public SuccessResponse removeByCreativeGroupId(Long id, OauthKey key) throws Exception {
        //null validations
        if (id == null) {
            throw new IllegalArgumentException("CreativeGroup Id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        //Obtain session
        SqlSession session = creativeInsertionDao.openSession();
        try {
            //check accces control
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeGroupId", Long.toString(id), key.getUserId()));
            }
            this.creativeInsertionDao.deleteByCreativeGroupId(id, key, session);
            creativeInsertionDao.commit(session);
        } catch (Exception e) {
            creativeInsertionDao.rollback(session);
            throw e;
        } finally {
            creativeInsertionDao.close(session);
        }
        log.info("Creative Insertions belongs a creativeGroup Id = {} was deleted" ,id );
        return new SuccessResponse("Creatives Insertion by Creative group " + id + " successfully deleted");
    }

    /**
     * Creates a CreativeInsertion in bulk
     *
     * @param bulkCreativeInsertion The CreativeInsertion object to create.
     * @param key The {@code OauthKey} for the requester user
     * @param campaignId The Campaign Id
     * @return CreativeInsertion saved
     */
    public Either<Errors, BulkCreativeInsertion> bulkCreate(BulkCreativeInsertion bulkCreativeInsertion, OauthKey key, Long campaignId) {
        //null validations
        if (bulkCreativeInsertion == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "BulkCreativeInsertion"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();

        //validations
        List<CreativeInsertion> creativeInsertions = bulkCreativeInsertion.getCreativeInsertions();
        if (creativeInsertions == null || creativeInsertions.isEmpty()) {
            ValidationError error = new ValidationError();
            error.setMessage(ResourceBundleUtil.getString("global.error.emptyLis", "BulkCreativeInsertion"));
            error.setCode(ValidationCode.INVALID);
            errors.addError(error);
            return Either.error(errors);
        }

        // Data Access Control bulk checks and model validation
        Set<Long> placementIds, groupIds, creativeIds;
        placementIds = new HashSet<>();
        creativeIds = new HashSet<>();
        groupIds = new HashSet<>();
        BeanPropertyBindingResult berrors;
        boolean campaignIdMatch = true;
        for(CreativeInsertion ci: creativeInsertions) {
            // Accumulate IDs
            placementIds.add(ci.getPlacementId());
            creativeIds.add(ci.getCreativeId());
            groupIds.add(ci.getCreativeGroupId());
            // Perform validation
            berrors = new BeanPropertyBindingResult(ci, CreativeInsertion.class.getSimpleName());
            // Validate Creative Insertion
            ValidationUtils.invokeValidator(springValidator, ci, berrors);

            if (berrors.hasErrors()) {
                errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(berrors));
            }
            campaignIdMatch = campaignIdMatch && campaignId.equals(ci.getCampaignId());
        }
        if(!errors.getErrors().isEmpty()) {
            return Either.error(errors);
        }
        // Validate if any of the Creative Insertion's campaign id does not match the one form the path
        if(!campaignIdMatch) {
            errors.addError(new ValidationError(
                    ResourceBundleUtil.getString("creativeInsertion.error.bulkCreateWithMixedCampaignIds"),
                    ValidationCode.INVALID));
            return Either.error(errors);
        }

        //Obtain session
        SqlSession session = creativeInsertionDao.openSession();
        Either<Errors, BulkCreativeInsertion> result;
        try {
            result = createInsertionBusinessLogic(creativeInsertions, campaignId, placementIds, 
                    groupIds, creativeIds, key, session, true);
            if(result.isError()) {
                return Either.error(result.error());
            } else {
                creativeInsertionDao.commit(session);
            }
        } finally {
            creativeInsertionDao.close(session);
        }
        return result;
    }
    
    /**
     * Creates a CreativeInsertions doing validations of DAC, and applying business Logic
     *
     * @param creativeInsertions The CreativeInsertion object to create.
     * @param key The {@code OauthKey} for the requester user
     * @param campaignId The Campaign Id
     * @return Either with Creative insertions created or Errors
     */
    private Either<Errors, BulkCreativeInsertion> createInsertionBusinessLogic(List<CreativeInsertion> creativeInsertions,
            Long campaignId, Collection<Long> placementIds, Collection<Long> groupIds, Collection<Long> creativeIds, OauthKey key, SqlSession session, boolean toCreate){
        // result variables
        CreativeInsertion result = null;
        HashSet<String> cgcs = new HashSet<>();
        BulkCreativeInsertion bulkResponse = new BulkCreativeInsertion();
        Errors errors = new Errors();
        
        // Check DAC
        Either<AccessError, Void> accessControlResult = checkAccessControl(campaignId,
                key.getUserId(),
                session,
                placementIds,
                creativeIds,
                groupIds);
        if(accessControlResult != null && accessControlResult.isError()) {
            errors.addError(accessControlResult.error());
            return Either.error(errors);
        }

        // Obtain the Creative Grups that participate in the list of Creative Insertions
        List<CreativeGroup> groupList = creativeGroupDao.getCreativeGroupsByIds(groupIds, session);
        Map<Long, CreativeGroup> groupsMap = new HashMap<>();
        for (CreativeGroup group: groupList) {
            groupsMap.put(group.getId(), group);
        }
        
        // Obtain the Creatives that participate in the list of Creative Insertions
        List<Creative> creativeList = creativeDao.getCreativesByIds(creativeIds, session);
        Map<Long, Creative> creativesMap = new HashMap<>();
        for (Creative creative: creativeList) {
            creativesMap.put(creative.getId(), creative);
        }

        // Obtain the Placements that participate in the list of Creative Insertions
        List<Placement> placementsList = placementDao.getPlacementsByIds(placementIds, session);
        Map<Long, Placement> placementsMap = new HashMap<>();
        for (Placement placement : placementsList) {
            placementsMap.put(placement.getId(), placement);
        }

        // Check for duplicates and remove them
        Long numberOfRecords = creativeInsertionDao.getAllCreativeInsertionsCount(campaignId, session);
        List<CreativeInsertionView> onDBCIs = creativeInsertionDao.getAllCreativeInsertions(campaignId,
                new Long(SearchCriteria.SEARCH_CRITERIA_START_INDEX), numberOfRecords, session);

        List<CreativeInsertionExtendedView> ciToCreate = new ArrayList<>();
        for (CreativeInsertion ci : creativeInsertions) {
            CreativeInsertionExtendedView ciev = new CreativeInsertionExtendedView();
            ciev.setCampaignId(ci.getCampaignId());
            ciev.setPlacementId(ci.getPlacementId());
            ciev.setCreativeGroupId(ci.getCreativeGroupId());
            ciev.setCreativeId(ci.getCreativeId());
            if(onDBCIs.contains(ciev)) {
                DuplicateCIError error = new DuplicateCIError(
                        ResourceBundleUtil.getString("bulkCreativeInsertion.error.onlyOneAssociation"),
                        BusinessCode.DUPLICATE,
                        ciev.getSiteId(),
                        ciev.getSiteSectionId(),
                        ciev.getPlacementId(),
                        ciev.getCreativeGroupId(),
                        ciev.getCreativeId());
                errors.addError(error);
            } else {
                ciev.setWeight(ci.getWeight());
                ciev.setPrimaryClickthrough(ci.getClickthrough());
                ciev.setAdditionalClickthroughs(ci.getClickthroughs());
                ciev.setSequence(ci.getSequence());
                ciev.setEndDate(ci.getEndDate());
                ciev.setStartDate(ci.getStartDate());
                ciev.setReleased(ci.getReleased());
                ciev.setCampaignId(campaignId);
                ciev.setPlacementStatus(ci.getTimeZone());
                ciev.setTimeZone(ci.getTimeZone());
                Creative creative = creativesMap.get(ci.getCreativeId());
                ciev.setCreativeType(creative.getCreativeType());
                ciev.setCreativeSize(creative.getWidth() + "x" + creative.getHeight());
                ciev.setCreativeCampaignId(creative.getCampaignId());
                Placement placement = placementsMap.get(ci.getPlacementId());
                ciev.setPlacementStatus(placement.getStatus());
                ciev.setPlacementStartDate(placement.getStartDate());
                ciev.setPlacementEndDate(placement.getEndDate());
                ciev.setSizeName(placement.getSizeName());
                ciev.setPlacementCampaignId(placement.getCampaignId());
                CreativeGroup group = groupsMap.get(ci.getCreativeGroupId());
                ciev.setGroupCampaignId(group.getCampaignId());
                // It's a new CI, add it to the list of those to be created
                ciToCreate.add(ciev);
                onDBCIs.add(ciev);
            }
        }

        if(errors.getErrors() != null && !errors.getErrors().isEmpty()) {
            return Either.error(errors);
        }

        // Obtain classification of Creatives by Placement
        Map<Long, CreativeManager.CreativeGlobalClassification> creativeClassificationByPlacement =
                creativeInsertionDao.getCreativeClassificationByPlacementId(campaignId, placementIds, session);

        for (CreativeInsertionExtendedView ciev : ciToCreate) {
            // Check if Placements contain consistent Creative types
            CreativeManager.CreativeGlobalClassification classification =
                    creativeClassificationByPlacement.get(ciev.getPlacementId());
            if(classification == null) {
                creativeClassificationByPlacement.put(ciev.getPlacementId(), getCreativeClassificationByCreativeType(ciev.getCreativeType()));
            } else {
                if(classification != getCreativeClassificationByCreativeType(ciev.getCreativeType())) {
                    ValidationError error = new ValidationError(
                            ResourceBundleUtil.getString("creativeInsertion.error.placementWithMixedCreativeTypes"),
                            ValidationCode.INVALID);
                    error.setObjectName("placementId");
                    error.setRejectedValue(String.valueOf(ciev.getPlacementId()));
                    errors.addError(error);
                }
            }
            // Various validations
            // Check placement status
            if (InsertionOrderStatusEnum.fromName(ciev.getPlacementStatus()) != InsertionOrderStatusEnum.ACCEPTED) {
                errors.addError(new ValidationError(
                        ResourceBundleUtil.getString(
                                "creativeInsertion.error.placementStatus",
                                ciev.getPlacementId().toString(),
                                InsertionOrderStatusEnum.ACCEPTED.getName()),
                        ValidationCode.INVALID));
            } // Check all Campaign IDs match
            // Check Creative's Campaign ID vs Campaign ID
            else if (!ciev.getCampaignId().equals(ciev.getCreativeCampaignId())) {
                ValidationError error = new ValidationError(
                        ResourceBundleUtil.getString("global.error.campaignContextViolation",
                                String.valueOf(ciev.getCreativeCampaignId()),
                                String.valueOf(ciev.getCampaignId())),
                        ValidationCode.INVALID);
                error.setField("creativeId");
                error.setRejectedValue(String.valueOf(ciev.getCreativeId()));
                errors.addError(error);
            }
            // Check Placement's Campaign ID vs Campaign ID
            else if (!ciev.getCampaignId().equals(ciev.getPlacementCampaignId())) {
                ValidationError error = new ValidationError(
                        ResourceBundleUtil.getString("global.error.campaignContextViolation",
                                String.valueOf(ciev.getPlacementCampaignId()),
                                String.valueOf(ciev.getCampaignId())),
                        ValidationCode.INVALID);
                error.setField("placementId");
                error.setRejectedValue(String.valueOf(ciev.getPlacementId()));
                errors.addError(error);
            }
            // Check Group's Campaign ID vs Campaign ID
            else if (!ciev.getCampaignId().equals(ciev.getGroupCampaignId())) {
                ValidationError error = new ValidationError(
                        ResourceBundleUtil.getString("global.error.campaignContextViolation",
                                String.valueOf(ciev.getGroupCampaignId()),
                                String.valueOf(ciev.getCampaignId())),
                        ValidationCode.INVALID);
                error.setField("groupId");
                error.setRejectedValue(String.valueOf(ciev.getCreativeGroupId()));
                errors.addError(error);
            }
            // Check if Creative and Placement Sizes match
            else if (!ciev.getCreativeSize().equalsIgnoreCase(ciev.getSizeName())) {
                errors.addError(new ValidationError(
                        ResourceBundleUtil.getString("creativeInsertion.error.placementAndCreativeDimensions"),
                        ValidationCode.INVALID));
            }

            // Transform DTO to execute CT validations
            CreativeInsertion ci = new CreativeInsertion();
            ci.setClickthrough(ciev.getPrimaryClickthrough());
            ci.setClickthroughs(ciev.getAdditionalClickthroughs());
            ci.setCreativeType(ciev.getCreativeType());
            //Check clickthroughs
            BeanPropertyBindingResult ctValidationErrors = new BeanPropertyBindingResult(ci,
                        ciev.getClass().getSimpleName());
            if (toCreate) {
                springValidator.validateClickthroughCreate(ci, creativesMap.get(ciev.getCreativeId()), ctValidationErrors);
            } else {
                springValidator.validateClickthroughUpdate(ci, ctValidationErrors);
            }
            
            if (ctValidationErrors.hasErrors()) {
                errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(ctValidationErrors));
            } else{
                ciev.setPrimaryClickthrough(ci.getClickthrough());
                ciev.setAdditionalClickthroughs(ci.getClickthroughs());
            }
            cgcs.add(ciev.getCreativeGroupId() + "_" + ciev.getCreativeId());
        }
       
        // Are there validation errors
        if(errors.getErrors() != null && !errors.getErrors().isEmpty()) {
            return Either.error(errors);
        }

        List<String> existingCGC = creativeGroupCreativeDao.getExistingCGC(new ArrayList<String> (cgcs), campaignId, session);
        cgcs.removeAll(existingCGC);
        SqlSession sessionBatch = creativeInsertionDao.openSession(ExecutorType.BATCH);
        List<CreativeInsertion> resultList = new ArrayList<>();
        try {
            if (!cgcs.isEmpty()) {
                for (String cgc : cgcs) {
                    CreativeGroupCreative creativeGroupCreative = new CreativeGroupCreative();
                    int index = cgc.indexOf('_');
                    creativeGroupCreative.setCreativeGroupId(Long.parseLong(cgc.substring(0, index)));
                    creativeGroupCreative.setCreativeId(Long.parseLong(cgc.substring(index + 1, cgc.length())));
                    creativeGroupCreative.setDisplayOrder(0L);
                    creativeGroupCreative.setDisplayQuantity(0L);
                    creativeGroupCreative.setCreatedTpwsKey(key.getTpws());
                    creativeGroupCreativeDao.save(creativeGroupCreative, sessionBatch);
                }
            }

            for (CreativeInsertionExtendedView civ : ciToCreate) {
                result = createCreativeInsertion(civ, key, sessionBatch);
                resultList.add(result);
            }
            creativeInsertionDao.commit(sessionBatch);
        } catch (Exception e) {
            creativeInsertionDao.rollback(sessionBatch);
            errors.addError(new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR));
        } finally{
            creativeInsertionDao.close(sessionBatch);
        }
        //call dao
        bulkResponse.setCreativeInsertions(resultList);
        return Either.success(bulkResponse);
    }

    private CreativeManager.CreativeGlobalClassification getCreativeClassificationByCreativeType(String creativeType) {
        CreativeManager.CreativeType type = CreativeManager.CreativeType.typeOf(creativeType);
        if (type == CreativeManager.CreativeType.XML ||
            type == CreativeManager.CreativeType.VMAP ||
            type == CreativeManager.CreativeType.VAST) {
            return CreativeManager.CreativeGlobalClassification.XML;
        } else {
            return CreativeManager.CreativeGlobalClassification.NON_XML;
        }
    }

    private Either<AccessError, Void> checkAccessControl(Long campaignId, String userId, SqlSession session,
            Collection<Long> placements, Collection<Long> creatives, Collection<Long> groups) {
        /*
        1. DAC for Campaign
        2. DAC for Cretive Group
        3. DAC for Placements
        4. DAC for Creatives
         */
        if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), userId, session)) {
            AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.ILLEGAL_USER_CONTEXT"),
                    SecurityCode.NOT_FOUND_FOR_USER, userId);
            return Either.error(error);
        }
        
        if (!userValidFor(AccessStatement.CREATIVE_GROUP, groups, userId, session)) {
            AccessError error = new AccessError(ResourceBundleUtil.getString("dataAccessControl.dataNotFoundForUser", userId),
                    SecurityCode.NOT_FOUND_FOR_USER, userId);
            return Either.error(error);
        }
        if (!userValidFor(AccessStatement.PLACEMENT, placements, userId, session)) {
            String placementIds = placements.size() > 1 ? placements.toString() : placements.iterator().next().toString();
            AccessError error = new AccessError(ResourceBundleUtil.getString("dataAccessControl.notFoundForUserSingleElement", 
                    "PlacementId", placementIds, userId),
                    SecurityCode.NOT_FOUND_FOR_USER,
                    userId);
            return Either.error(error);
        }
        if (!userValidFor(AccessStatement.CREATIVE, creatives, userId, session)) {
            AccessError error = new AccessError(ResourceBundleUtil.getString("dataAccessControl.dataNotFoundForUser", userId), 
                    SecurityCode.NOT_FOUND_FOR_USER,
                    userId);
            return Either.error(error);            
        }        
        return null;
    }
    
    private CreativeInsertion createCreativeInsertion(CreativeInsertionExtendedView ciev, OauthKey key, SqlSession session) {
        //validations
        CreativeInsertion result;

        // Set defaults if needed
        // Default Start and End Dates
        if (ciev.getStartDate() == null || ciev.getEndDate() == null) {
            //set values for creativeInsertion from placemet
            ciev.setStartDate(ciev.getPlacementStartDate());
            ciev.setEndDate(ciev.getPlacementEndDate());
        }
        // Clickthrough
        if (ciev.getPrimaryClickthrough() == null) {
            //set values for creativeInsertion from creative
            ciev.setPrimaryClickthrough(ciev.getCreativeClickthrough());
        }
        // Clickthroughs
        if (ciev.getWeight() == null) {
            ciev.setWeight(Constants.CREATIVE_INSERTION_DEFAULT_WEIGHT);
        }

        // Persist
        CreativeInsertion ci = new CreativeInsertion();
        ci.setClickthrough(ciev.getPrimaryClickthrough());
        ci.setClickthroughs(ciev.getAdditionalClickthroughs());
        ci.setEndDate(ciev.getEndDate());
        ci.setStartDate(ciev.getStartDate());
        ci.setCampaignId(ciev.getCampaignId());
        ci.setCreativeId(ciev.getCreativeId());
        ci.setCreativeGroupId(ciev.getCreativeGroupId());
        ci.setPlacementId(ciev.getPlacementId());
        ci.setWeight(ciev.getWeight());
        ci.setCreatedTpwsKey(key.getTpws());
        // Unusual parameters copied just in case they are provided
        ci.setTimeZone(ciev.getTimeZone());
        ci.setReleased(ciev.getReleased());
        ci.setSequence(ciev.getSequence());
        if(StringUtils.isBlank(ci.getTimeZone())) {
            ci.setTimeZone(TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
        }
        if(ci.getReleased() == null) {
            ci.setReleased(0L);
        }
        if(ci.getSequence() == null) {
            ci.setSequence(0L);
        }

        result = this.creativeInsertionDao.create(ci, session);
        log.debug("For user: {} Creative Insertion created: {}", key.getUserId(), result);
        return result;
    }
    
    private boolean verifyCleanSearchOptions(CreativeInsertionSearchOptions searchOptions, String pivotType, String typeLevel) {

        //1. Obtain hierarchy: 
        //      a. without type (level of context) --> obtain all hierarchy
        //      b. with type (level of context) --> obtain hierarchy from this type
        CreativeInsertionFilterParamTypeEnum pivotTypeEnum = CreativeInsertionFilterParamTypeEnum.
                valueOf(CreativeInsertionFilterParamTypeEnum.class, pivotType.toUpperCase());

        CreativeInsertionFilterParamTypeEnum levelTypeEnum = (typeLevel != null && !typeLevel.isEmpty()) ? CreativeInsertionFilterParamTypeEnum.
                valueOf(CreativeInsertionFilterParamTypeEnum.class, typeLevel.toUpperCase()) : pivotTypeEnum;

        List<CreativeInsertionFilterParamTypeEnum> allHierarchy = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_FROM_LEVEL.get(pivotTypeEnum).get(pivotTypeEnum);
        List<CreativeInsertionFilterParamTypeEnum> hierarchyFromLevel = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_FROM_LEVEL.get(pivotTypeEnum).get(levelTypeEnum);

        //2. Clean search options
        List<CreativeInsertionFilterParamTypeEnum> outsiteLevels = new ArrayList<>();
        outsiteLevels.addAll(allHierarchy);
        outsiteLevels.removeAll(hierarchyFromLevel);
        for (CreativeInsertionFilterParamTypeEnum type : outsiteLevels) {
            switch (type) {
                case SITE:
                    searchOptions.setSite(false);
                    break;
                case SECTION:
                    searchOptions.setSection(false);
                    break;
                case PLACEMENT:
                    searchOptions.setPlacement(false);
                    break;
                case GROUP:
                    searchOptions.setGroup(false);
                    break;
                case CREATIVE:
                case SCHEDULE:
                    if(!pivotTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.CREATIVE)) {
                        searchOptions.setCreative(false);
                    }
                    break;
            }
        }
                
        //3. Review SearchOptions for hierarchy from level type
        int optionsCounter = 0;
        for (CreativeInsertionFilterParamTypeEnum type : hierarchyFromLevel) {
            switch (type) {
                case SITE:
                    optionsCounter += searchOptions.isSite() ? 1 : 0;
                    break;
                case SECTION:
                    optionsCounter += searchOptions.isSection() ? 1 : 0;
                    break;
                case PLACEMENT:
                    optionsCounter += searchOptions.isPlacement() ? 1 : 0;
                    break;
                case GROUP:
                    optionsCounter += searchOptions.isGroup() ? 1 : 0;
                    break;
                case CREATIVE:
                case SCHEDULE:
                    optionsCounter += searchOptions.isCreative() ? 1 : 0;
                    break;
            }
        }
        return optionsCounter > 0;
    }

    private Either<Error, Void> checkAccessControlForCreativeInsertionFilterParamIds(List<CreativeInsertionFilterParam> ciFilterParams, String userId, SqlSession session) {

        Set<Long> hsSiteIds, hsSectionIds, hsPlacementIds, hsGroupIds, hsCreativeIds;
        hsSiteIds = new HashSet();
        hsSectionIds = new HashSet();
        hsPlacementIds = new HashSet();
        hsGroupIds = new HashSet();
        hsCreativeIds = new HashSet();

        List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelTypesToValidate = null;
        for (CreativeInsertionFilterParam ciFilterParam : ciFilterParams) {
            hierarchyLevelTypesToValidate = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.
                    get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, ciFilterParam.getPivotType().toUpperCase())).
                    get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, ciFilterParam.getType().toUpperCase()));

            for (CreativeInsertionFilterParamTypeEnum hierarchyLevelToValidate : hierarchyLevelTypesToValidate) {
                switch (hierarchyLevelToValidate) {
                    case CREATIVE:
                    case SCHEDULE:
                        hsCreativeIds.add(ciFilterParam.getCreativeId());
                        break;
                    case GROUP:
                        hsGroupIds.add(ciFilterParam.getGroupId());
                        break;
                    case PLACEMENT:
                        hsPlacementIds.add(ciFilterParam.getPlacementId());
                        break;
                    case SECTION:
                        hsSectionIds.add(ciFilterParam.getSectionId());
                        break;
                    case SITE:
                        hsSiteIds.add(ciFilterParam.getSiteId());
                        break;
                }
            }
        }
        if (!hsSiteIds.isEmpty()) {
            if (!userValidFor(AccessStatement.SITE, hsSiteIds, userId, session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
        }
        if (!hsSectionIds.isEmpty()) {
            if (!userValidFor(AccessStatement.SITE_SECTION, hsSectionIds, userId, session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
        }
        if (!hsPlacementIds.isEmpty()) {
            if (!userValidFor(AccessStatement.PLACEMENT, hsPlacementIds, userId, session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
        }
        if (!hsGroupIds.isEmpty()) {
            if (!userValidFor(AccessStatement.CREATIVE_GROUP, hsGroupIds, userId, session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
        }
        if (!hsCreativeIds.isEmpty()) {
            if (!userValidFor(AccessStatement.CREATIVE, hsCreativeIds, userId, session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
        }
        return null;
    }

}

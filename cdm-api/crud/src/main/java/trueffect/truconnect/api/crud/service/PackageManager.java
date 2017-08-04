package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoBase;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;
import trueffect.truconnect.api.crud.validation.PackageValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Business logic operations for Packages
 *
 * @author Gustavo Claure, Marcelo Heredia, Richard Jaldin
 */
public class PackageManager extends AbstractGenericManager {

    private PackageDaoExtended packageDao;
    private PlacementDao placementDao;
    private PackagePlacementDaoExtended packagePlacementDao;
    private CostDetailManager packageCostDetailManager;
    private CostDetailManager placementCostDetailManager;
    private PackageDaoBase dimPackageDao;
    private PackagePlacementDaoBase dimPackagePlacementDao;
    private InsertionOrderDao insertionOrderDao;
    private PackageValidator packageValidator;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public PackageManager(PackageDaoExtended packageDao, CostDetailDaoExtended packageCostDetailDao,
                          PlacementDao placementDao, CostDetailDaoExtended placementCostDetailDao,
                          PackagePlacementDaoExtended packagePlacementDao,
                          CostDetailDaoBase dimPlacementCostDetailDao,
                          PackageDaoBase dimPackageDao, CostDetailDaoBase dimPackageCostDetailDao,
                          PackagePlacementDaoBase dimPackagePlacementDao,
                          InsertionOrderDao insertionOrderDao, AccessControl accessControl) {
        super(accessControl);
        this.packageDao = packageDao;
        this.placementDao = placementDao;
        this.packagePlacementDao = packagePlacementDao;
        this.dimPackageDao = dimPackageDao;
        this.dimPackagePlacementDao = dimPackagePlacementDao;
        this.insertionOrderDao = insertionOrderDao;
        this.packageCostDetailManager = new CostDetailManager(packageCostDetailDao,
                dimPackageCostDetailDao, CostDetailType.PACKAGE, accessControl);
        this.placementCostDetailManager = new CostDetailManager(placementCostDetailDao,
                dimPlacementCostDetailDao, CostDetailType.PLACEMENT, accessControl);
        packageValidator = new PackageValidator();
    }

    /**
     * Gets a Package given an {@code id}
     *
     * @param id The Package ID
     * @param session The The SqlSession where to execute the query
     * @return the obtained Package
     */
    public Package getPackage(Long id, SqlSession session) {
        Package result = packageDao.get(id, session);
        result.setPlacements(placementDao.getPlacementsByPackageId(result.getId(), session));
        return result;
    }

    public Package getPackage(Long id, OauthKey key) {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        SqlSession session = packageDao.openSession();
        try {
            if(!userValidFor(AccessStatement.PACKAGE, Collections.singletonList(id), key.getUserId(), session)){
                // TODO change for Either soon
                throw BusinessValidationUtil
                        .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            }
            return getPackage(id, session);
        } finally {
            packageDao.close(session);
        }
    }

    /**
     * Gets a list of all Packages with its Placements and CostDetails
     *
     * @param criteria The Search Criteria
     * @param key The Oauth key to track who is executing this operation
     * @return The list of all Packages.
     */
    public RecordSet<Package> get(SearchCriteria criteria, OauthKey key) {
        //validations
        if (criteria == null) {
            throw new IllegalArgumentException("Search Criteria cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        if (criteria.getPageSize() > SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE) {
            throw new SystemException("The page size allows up to 1000 records.", BusinessCode.INVALID);
        }
        if (criteria.getStartIndex() < SearchCriteria.SEARCH_CRITERIA_START_INDEX) {
            throw new SystemException("Cannot retrieve records for start index: " + criteria.getStartIndex() + ". The minimum start index is: 0", BusinessCode.INVALID);
        }

        SqlSession session = packageDao.openSession();
        RecordSet<Placement> placements;
        RecordSet<Package> packages;
        try {
            try {
                placements = placementDao.getPlacements(criteria, key, session);
            } catch (Exception e) {
                throw new SystemException(e, BusinessCode.INVALID);
            }

            //Get packages
            packages = packageDao.get(criteria, key, session);
        } finally {
            packageDao.close(session);
        }

        // Add non standalone placements
        List<Package> records = new ArrayList<>();
        for (Package pkg : packages.getRecords()) {
            Package pkgResult = getPackage(pkg, placements.getRecords());
            if(pkgResult.getPlacements().size() > 0) {
                records.add(pkgResult);
            }
        }
        return new RecordSet<>(0, SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE, records.size(), records);
    }

    public Either<Errors, RecordSet<Package>> getPackagesByCampaignAndIoId(Long campaignId,
                                                                           Long ioId,
                                                                           Long startIndex,
                                                                           Long pageSize,
                                                                           OauthKey key) {

        // Nullability checks
        if (campaignId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Campaign Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        Errors errors = new Errors();
        if (ioId == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.null", "ioId"),
                    ValidationCode.REQUIRED);
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
        RecordSet<Package> result = null;
        SqlSession session = packageDao.openSession();
        int numberOfRecords = 0;
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            if (!userValidFor(AccessStatement.INSERTION_ORDER, Collections.singletonList(ioId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            InsertionOrder io = insertionOrderDao.get(ioId, session);
            if (!io.getCampaignId().equals(campaignId)) {
                ValidationError error = new ValidationError(
                        ResourceBundleUtil.getString("global.error.entityHasNoRelationship", "ioId",
                                "campaignId"),
                        ValidationCode.REQUIRED);
                errors.addError(error);
                return Either.error(errors);
            }

            // Business Logic
            List<Package> packages = packageDao.getPackageByCampaignAndIoId(campaignId, ioId,
                    paginator.get("startIndex"), paginator.get("pageSize"), session);
            numberOfRecords = packageDao.getCountPackageByCampaignAndIoId(campaignId, ioId, session)
                                        .intValue();
            result = new RecordSet<>(paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(), numberOfRecords, packages);
        } finally {
            packageDao.close(session);
        }
        return Either.success(result);
    }

    private Package getPackage(Package pkg, List<Placement> placements) {
        pkg.setPlacements(new ArrayList<Placement>());
        for (Placement placement : placements) {
            if(placement.getPackageId() != null &&  
                    (placement.getPackageId().longValue() == pkg.getId().longValue())){
                pkg.getPlacements().add(placement);
            } 
        }
        return pkg;
    }

    /**
     * Creates a new Package
     *
     * @param pkg The Package to create
     * @param key The Oauth key to track who is executing this operation
     * @param session The SqlSession where to execute the persistence query
     * @param dimSession The SqlSession where to execute the persistence query on dim DB
     * @return The newly created Package
     *
     */
    public Package create(Package pkg, OauthKey key, SqlSession session, SqlSession dimSession) throws SystemException {
        //null validations
        if (pkg == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.package")));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "SqlSession"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Dim SqlSession"));
        }

        // Model Validations
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(pkg, "package");
        packageValidator.validatePackageForCreate(pkg, validationResult);
        if (validationResult.hasErrors()) {
            throw BusinessValidationUtil.buildSpringValidationSystemException(ValidationCode.INVALID, validationResult);
        }

        // Check access control
        if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(pkg.getCampaignId()), key.getUserId(), session)) {
            throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
        }
        checkExistingName(pkg, session);

        // create Package + costDetails
        Package result = this.createPackage(pkg, key, session, dimSession);

        return result;
    }

    /**
     * Set Default values and creates a new Package with data already validated.
     *
     * @param pkg
     * @param key
     * @param session
     * @param dimSession
     * @return the new Package created on the data base
     */
    public Package createPackage(Package pkg, OauthKey key, SqlSession session, SqlSession dimSession) {
        //null validations
        if (pkg == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.package")));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "SqlSession"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Dim SqlSession"));
        }

        // Overriding the package description as in XLS both are the same.
        pkg.setDescription(pkg.getName());
        // Set default values
        pkg.setCreatedTpwsKey(key.getTpws());
        pkg.setCreatedDate(new Date());
        pkg.setModifiedTpwsKey(key.getTpws());
        pkg.setModifiedDate(pkg.getCreatedDate());
        log.debug("Package '{}' created by {} ", pkg.getName(), key.getUserId());

        // create Package
        Package result = this.packageDao.create(pkg, session);

        // Create dim package
        dimPackageDao.create(result, dimSession);
        
        // CreateCostDetails
        List<CostDetail> costDetails = packageCostDetailManager.updateCostDetails(
                pkg.getCostDetails(), result.getId(), key, session, dimSession);
        result.setCostDetails(costDetails);

        return result;
    }

    /**
     * Updates a Package
     *
     * @param id The Package ID
     * @param pkg The Package to update
     * @param key The Oauth key to track who is executing this operation
     * @return the updated Package
     * @throws trueffect.truconnect.api.commons.exceptions.SystemException When
     * a validation error occurs
     */
    public Package update(Long id, Package pkg, OauthKey key) {
        //null validations
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Id"));
        }
        if (pkg == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.package")));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        //validate package data
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(pkg, "package");
        packageValidator.validatePackageForUpdate(pkg, validationResult);
        if(validationResult.hasErrors()){
            throw BusinessValidationUtil.buildSpringValidationSystemException(ValidationCode.INVALID,
                    validationResult);
        }

        // Checking duplicated placements on Package
        if(checkDuplicatedPlacementsOnPayload(pkg.getPlacements())){
            //throw new ValidationException("The Package with Id: " + pkg.getPackageId() + ", have duplicated placements");
            throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.DUPLICATE,
                    "placements");
        }

        //session
        SqlSession session = packageDao.openSession();
        SqlSession dimSession = dimPackageDao.openSession();
        Package result = null;
        try {
            //check access control
            if(!userValidFor(AccessStatement.PACKAGE, Collections.singletonList(pkg.getId()), 
                    key.getUserId(), session)){
                throw BusinessValidationUtil
                    .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            }
            
            //check the placements belongs the same campaignId
            List<Long> placementsIdProcess = new ArrayList<>();
            for (Placement placement : pkg.getPlacements()) {
                placementsIdProcess.add(placement.getId());
            }
            HashSet hs = new HashSet();
            hs.addAll(placementsIdProcess);
            placementsIdProcess = new ArrayList<>();
            placementsIdProcess.addAll(hs);
            boolean userValid = userValidFor(AccessStatement.PLACEMENT,
                    placementsIdProcess,
                    key.getUserId(), session);
            if (!userValid) {
                // FIXME We should not throw exception here, but we don't have yet a way to
                // return expected error situations as errors and not as exceptions (current approach)
                throw BusinessValidationUtil
                        .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            }
            
            //check the placements belong to the same campaignId
            if (!placementDao.checkPlacementsBelongsCampaignId(placementsIdProcess, pkg.getCampaignId(), session)) {
                //throw new ValidationException("A placement Campaign id does not belong to the same Package Campaign Id");
                throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INVALID,
                        "campaignId");
            }
            
            //check placements standalone
            Long isStandAlone = placementDao.checkPlacementsStandAlone(placementsIdProcess, 
                    pkg.getId(), session);
            if (isStandAlone > 0) {
                //throw new ValidationException("A placement Campaign id does not belong to the same Package Campaign Id");
                throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INVALID,
                    "placement");
            }

            checkExistingName(pkg, session);
            
            //set new package data
            Package existentPackage = getPackage(pkg.getId(), session);
            existentPackage.setName(StringUtils.isBlank(pkg.getName())
                    ? existentPackage.getName() : pkg.getName());
            existentPackage.setDescription(pkg.getDescription());
            existentPackage.setExternalId(pkg.getExternalId());
            existentPackage.setModifiedTpwsKey(key.getTpws());
            existentPackage.setModifiedDate(new Date());
            
            //call dao to update package
            packageDao.update(existentPackage, session);

            // Update dim package
            dimPackageDao.update(existentPackage, dimSession);

            //update packageCostDetails
            packageCostDetailManager.updateCostDetails(pkg.getCostDetails(), pkg.getId(), key, session, dimSession);
            
            //update package-placement associations
            //remove existent
            //add news
            //Handle Package-Placement associations + Cost Details
            handlePackagePlacementAssociations(pkg, key, session, dimSession);
            
            result = this.getPackage(pkg.getId(), session);
            packageDao.commit(session);
            dimPackageDao.commit(dimSession);
        } catch (SystemException e) {
            packageDao.rollback(session);
            dimPackageDao.rollback(dimSession);
            throw e;
        } finally {
            packageDao.close(session);
            dimPackageDao.close(dimSession);
        }
        log.debug("Package '{}' updated by {} ", pkg.getId(), key.getUserId());
        return result;
    }

    /**
     * Disassociates a given {@code placementId} from its related Package.
     * <p>
     *     In case the last Placement for the Package is {@code placementId}, there will be a
     *     validation error
     * </p>
     * @param placementId The Placement ID to disassociate its Package from
     * @param key The Oauth key to track who is executing this operation
     * @return Either an {@code Errors} object containing the Validation or Business erros; or,
     * null (void) in case the operation completed successfully.
     */
    public Either<Errors, Void> disassociateFromPackage(Long placementId, OauthKey key) {
        //null validations
        if (placementId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "placementId"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        Errors errors = new Errors();
        packageValidator.validatePlacementId(placementId, errors);
        if(!errors.isEmpty()) {
            return Either.error(errors);
        }

        // Create Sessions
        SqlSession session = packageDao.openSession();
        SqlSession dimSession = dimPackageDao.openSession();
        Placement placement = null;
        try {
            // Check DAC to Placement
            if(!userValidFor(AccessStatement.PLACEMENT, placementId,
                    key.getUserId(), session)){
                errors.addError(new ValidationError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER
                ));
                return Either.error(errors);
            }

            // Get Package associated.
            placement = placementDao.get(placementId, session);
            // If Package is null; then, return error. Otherwise, continue.
            packageValidator.validatePlacementCanBeDisassociated(placement, errors);
            if(!errors.isEmpty()) {
                return Either.error(errors);
            }

            Package pkg = getPackage(placement.getPackageId(), session);
            packageValidator.validatePackageHasPlacementsToDisassociate(pkg, placementId, errors);
            if(!errors.isEmpty()) {
                return Either.error(errors);
            }
            // Update Package by removing the given Placement
            for(Placement p : pkg.getPlacements()) {
                if(p.getId().equals(placementId)) {
                    pkg.getPlacements().remove(p);
                    break;
                }
            }

            // Update Package-Placement associations
            handlePackagePlacementAssociations(pkg, key, session, dimSession);
            packageDao.commit(session);
            dimPackageDao.commit(dimSession);
        } catch (Exception e) {
            log.error("Error while removing Placement from Package", e);
            packageDao.rollback(session);
            dimPackageDao.rollback(dimSession);
            Error error = new BusinessError(
                    e.getMessage(),
                    BusinessCode.INTERNAL_ERROR,
                    "placementId");
            errors.addError(error);
            return Either.error(errors);
        } finally {
            packageDao.close(session);
            dimPackageDao.close(dimSession);
        }
        return Either.success(null);
    }

    public Package updateOnImport(Package pkg, OauthKey key, SqlSession session, SqlSession dimSession) {
        //null validations
        if (pkg == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.package")));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "SqlSession"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Dim SqlSession"));
        }
        pkg.setModifiedTpwsKey(key.getTpws());

        // create Package
        Package result = this.packageDao.updateOnImport(pkg, session);

        // Create dim package
        dimPackageDao.update(result, dimSession);

        // Update Cost Details
        List<CostDetail> costDetails = packageCostDetailManager.updateCostDetailsOnImport(
                pkg.getCostDetails(), pkg.getId(), key, session, dimSession);
        result.setCostDetails(costDetails);

        return result;
    }

    /**
     * Remove a packages in bulk
     *
     * @param bulkDelete
     * @param key
     * @return Either
     */
    public Either<Errors, String> bulkDelete(RecordSet<Long> bulkDelete, OauthKey key) {

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
                    ResourceBundleUtil.getString("global.error.null", "BulkDelete"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }
        Set<Long> packageIds = new HashSet<>();
        for (Long packageId : bulkDelete.getRecords()) {
            if (packageId != null) {
                packageIds.add(packageId);
            }
        }
        if (packageIds.isEmpty()) {
            ValidationError error =
                    new ValidationError(ResourceBundleUtil.getString("global.error.empty", "id"),
                            ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }

        SqlSession session = packageDao.openSession();
        SqlSession dimSession = dimPackageDao.openSession();
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.PACKAGE, packageIds, key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            //1. Disassociate placements from packages
            for (Long packageId : packageIds) {
                Package pkg = new Package();
                pkg.setId(packageId);
                handlePackagePlacementAssociations(pkg, key, session, dimSession);
            }

            //2. Delete package (te_xls and dim databases)
            packageDao.delete(new ArrayList<>(packageIds), key.getTpws(), session);
            dimPackageDao.delete(new ArrayList<>(packageIds), key.getTpws(), dimSession);

            packageDao.commit(session);
            dimPackageDao.commit(dimSession);
        } catch (Exception e) {
            packageDao.rollback(session);
            BusinessError error =
                    new BusinessError(e.getMessage(), BusinessCode.INTERNAL_ERROR, "BulkDelete");
            errors.addError(error);
            return Either.error(errors);
        } finally {
            packageDao.close(session);
        }
        String message = ResourceBundleUtil.getString("global.info.bulkOperationSuccess",
                ResourceBundleUtil.getString("global.label.delete"),
                ResourceBundleUtil.getString("global.label.package"));
        return Either.success(message);
    }

    private void checkExistingName(Package pkg, SqlSession session) {
        Boolean exists = packageDao.packageNameExists(pkg, session);
        if (exists) {
            throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.DUPLICATE,
                    "packageName");
        }
    }

    private boolean checkDuplicatedPlacementsOnPayload(List<Placement> placements) {
        boolean result = false;
        List<Long> placementIds = new ArrayList<>();
        for (Placement placement : placements) {
            if (placementIds.contains(placement.getId())) {
                result = true;
                break;
            }
            placementIds.add(placement.getId());
        }
        return result;
    }

    /**
     * Adding delta processing to save/delete records
     *
     * @param pkg Package containing information from user's request
     * @param key Current user's OauthKey
     * @param session Current user's session
     */
    private void handlePackagePlacementAssociations(Package pkg, OauthKey key, SqlSession session, SqlSession dimSession) {

        //obtain package + placements data
        Package existingPackage = this.getPackage(pkg.getId(), session);
        
        List<PackagePlacement> news = associationsFor(pkg, session);
        List<PackagePlacement> existing = associationsFor(existingPackage, session);
        
        //obtain packageCostDetail data
        CostDetail firstPkgCost = existingPackage.getCostDetails().get(0);
        String rateType = RateTypeEnum.typeOf(firstPkgCost.getRateType()).name();
        Date startDateCost = DateConverter.startDate(firstPkgCost.getStartDate());
        Date endDateCost = DateConverter.endDate(firstPkgCost.getEndDate());

        // 1. To delete (existing - news)
        if(existing != null && existing.size() > 0) {
            List<PackagePlacement> toDelete = new ArrayList<>();
            toDelete.addAll(existing);
            toDelete.removeAll(news);
            //remove associations
            if (!toDelete.isEmpty()) {
                packagePlacementDao.delete(toDelete, key, session);

                // DELETE dim packagePlacements
                dimPackagePlacementDao.delete(toDelete, key, dimSession);

                // Copy costdetail from package to placements
                for (PackagePlacement pkgPl : toDelete) {
                    //removing default XLS placement cost details
                    placementCostDetailManager.removeCostDetailsByForeignId(pkgPl.getPlacementId(), key, session, dimSession);
                    Placement placement = null;
                    // Copy costdetail from package to placement
                    placement = placementDao.get(pkgPl.getPlacementId(), session);
                    //Set CostDetail Values from packageCostDetails
                    placement.setRateType(rateType);
                    placement.setRate(firstPkgCost.getPlannedGrossRate());
                    placement.setStartDate(startDateCost);
                    placement.setEndDate(endDateCost);
                    placement.setInventory(firstPkgCost.getInventory());
                    placementDao.update(placement, session);

                    for(CostDetail cd : existingPackage.getCostDetails()) {
                        cd.setId(null);
                        cd.setForeignId(placement.getId());
                        cd.setCreatedTpwsKey(key.getTpws());
                        cd.setModifiedTpwsKey(key.getTpws());
                    }

                    placementCostDetailManager.updateCostDetails(existingPackage.getCostDetails(), placement.getId(), key, session, dimSession);
                }
            }
        }

        // 2. To Add (news - existing)
        List<PackagePlacement> toAdd = new ArrayList<>();
        toAdd.addAll(news);
        toAdd.removeAll(existing);
        //add new relationships
        if(toAdd.size() > 0) {
            List<PackagePlacement> created = packagePlacementDao.create(toAdd, key, session);

            // CREATE dim packagePlacements
            dimPackagePlacementDao.create(created, key, dimSession);

            // remove costdetail info
            for (PackagePlacement pkgPl : toAdd) {
                Placement placement = placementDao.get(pkgPl.getPlacementId(), session);

                if (placement.getPackageId() != null && placement.getPackageId() != pkg.getId().longValue()) {
                    //throw new ValidationException("Placement already belongs to a different Package and cannot be associated to this Package");
                    throw BusinessValidationUtil.buildValidationSystemException(ValidationCode.INVALID,
                            "packageId",
                            "placement",
                            placement.getPackageId().toString(),
                            null,
                            null,
                            null,
                            null);
                }
                //remove costDetails from placement
                placementCostDetailManager.removeCostDetailsByForeignId(pkgPl.getPlacementId(), key, session, dimSession);
            }
        }

        // 3. Adding and Updating have the same behavior
        // Update cost info for the new relationships
        Long inventory = PackagePlacementUtil.inventoryPlacement(firstPkgCost.getInventory(), news.size());
        List<Placement> placements = new ArrayList<>();
        for (PackagePlacement pkgPl : news) {
            Placement placement = placementDao.get(pkgPl.getPlacementId(), session);
            placement.setRateType(rateType);
            placement.setRate(firstPkgCost.getPlannedGrossRate());
            placement.setStartDate(startDateCost);
            placement.setEndDate(endDateCost);
            placement.setInventory(inventory);
            //update placement data
            placement = placementDao.update(placement, session);
            placements.add(placement);
        }
        pkg.setPlacements(placements);
    }
    
    /**
     * Returns a list of PackagePlacement objects based on Package and Placement objects
     * @param pkg the Package containing the Package and Placement information
     * @return List of PackagePlacement
     */
    private List<PackagePlacement> associationsFor(Package pkg, SqlSession session){
        List<PackagePlacement> result = new ArrayList<>();
        if(pkg.getPlacements() != null){
            for (Placement placement : pkg.getPlacements()) {
                PackagePlacement pkgPlacement = new PackagePlacement();
                //obtain a pkpl from database.
                List<PackagePlacement> pkgPl =  packagePlacementDao.getPackagePlacements(null, 
                        pkg.getId(), Collections.singletonList(placement.getId()), session);
                if(pkgPl != null && pkgPl.size() > 0){
                    pkgPlacement = pkgPl.get(0);
                } else {
                    pkgPlacement.setPackageId(pkg.getId());
                    pkgPlacement.setPlacementId(placement.getId());
                }
                result.add(pkgPlacement);
            }
        }
        return result;
    }

}

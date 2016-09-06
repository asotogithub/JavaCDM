package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.BulkPackagePlacement;
import trueffect.truconnect.api.commons.model.dto.PackagePlacementView;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoBase;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by richard.jaldin on 10/8/2015.
 */
public class PackagePlacementManager  extends AbstractGenericManager {

    private static final String CAMPAIGN = "campaignId";
    private static final String INSERTION_ORDER = "ioId";

    private PlacementDao placementDao;
    private InsertionOrderDao ioDao;
    private PackagePlacementDaoExtended packagePlacementDao;
    private UserDao userDao;
    private PlacementManager placementManager;
    private PackageManager packageManager;
    private PackagePlacementDaoBase dimPackagePlacementDao;

    public PackagePlacementManager(PlacementDao placementDao, CostDetailDaoExtended placementCostDetailDao,
                            CampaignDao campaignDao, SiteSectionDao siteSectionDao, SizeDao sizeDao,
                            PlacementStatusDao placementStatusDao, UserDao userDao,
                            ExtendedPropertiesDao extendedPropertiesDao, InsertionOrderDao ioDao,
                            InsertionOrderStatusDao ioStatusDao, PackageDaoExtended packageDao, CostDetailDaoExtended packageCostDetailDao,
                            PackagePlacementDaoExtended packagePlacementDao, CostDetailDaoBase dimPackageCostDetailDao,
                            CostDetailDaoBase dimPlacementCostDetailDao, PackageDaoBase dimPackageDao,
                            PackagePlacementDaoBase dimPackagePlacementDao, CreativeInsertionDao creativeInsertionDao,
                                   AccessControl accessControl) {
        super(accessControl);
        this.placementDao = placementDao;
        this.ioDao = ioDao;
        this.packagePlacementDao = packagePlacementDao;
        this.dimPackagePlacementDao = dimPackagePlacementDao;
        this.userDao = userDao;
        this.placementManager = new PlacementManager(placementDao, placementCostDetailDao,
                campaignDao, siteSectionDao, sizeDao, placementStatusDao, userDao, extendedPropertiesDao,
                ioDao, ioStatusDao, packageDao, packagePlacementDao, dimPlacementCostDetailDao,
                creativeInsertionDao,
                accessControl);

        this.packageManager = new PackageManager(packageDao, packageCostDetailDao, placementDao,
                placementCostDetailDao, packagePlacementDao, dimPlacementCostDetailDao,
                dimPackageDao, dimPackageCostDetailDao, dimPackagePlacementDao, ioDao,
                accessControl);
    }

    /**
     * Create a set of packages and its placements
     *
     * @param ioId The ID of Insertion Order
     * @param bulkPackagePlacement Object containing Packages with Placements and standalone Placements.
     * @param key Session ID of the user who updates the Placement.
     * @return a recordSet of PackageDTOs persisted
     * @throws java.lang.Exception
     */
    public BulkPackagePlacement createBulkPackagePlacements(Long ioId, BulkPackagePlacement bulkPackagePlacement, OauthKey key) throws Exception {
        // * Creates Package
        // * Creates Placements with individual cost detail
        // * Creates relationships Package - Placement
        // * Create Package Cost Detail
        // * Creates Standalone Placements with its cost detail

        // Validations
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        if (ioId == null) {
            ValidationError error = new ValidationError();
            error.setField("ioId");
            throw BusinessValidationUtil.buildSystemException(error, ValidationCode.REQUIRED);
        }
        if (bulkPackagePlacement == null) {
            ValidationError error = new ValidationError();
            error.setField("bulkPackagePlacement");
            throw BusinessValidationUtil.buildSystemException(error, ValidationCode.REQUIRED);
        }
        boolean placementsEmpty = bulkPackagePlacement.getPlacements() == null || bulkPackagePlacement.getPlacements().isEmpty();
        boolean packagesEmpty = bulkPackagePlacement.getPackages() == null || bulkPackagePlacement.getPackages().isEmpty();
        if (placementsEmpty && packagesEmpty) {
            ValidationError error = new ValidationError();
            error.setField("bulkPackagePlacement");
            throw BusinessValidationUtil.buildSystemException(error, ValidationCode.REQUIRED);
        }

        // Get a Session
        SqlSession session = placementDao.openSession();
        SqlSession dimSession = dimPackagePlacementDao.openSession();
        BulkPackagePlacement result = new BulkPackagePlacement();
        result.setPackages(new ArrayList<Package>());
        result.setPlacements(new ArrayList<Placement>());
        try {
            // Check access control for Insertion Order
            if (!userValidFor(AccessStatement.INSERTION_ORDER, Collections.singletonList(ioId), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            // get Contact User
            User user = userDao.get(key.getUserId(), session);
            if (!packagesEmpty) {
                for (Package pkg : bulkPackagePlacement.getPackages()) {
                    if (pkg.getPlacements() == null || pkg.getPlacements().isEmpty()) {
                        ValidationError error = new ValidationError();
                        error.setField("placements");
                        error.setObjectName("package");
                        throw BusinessValidationUtil.buildSystemException(error, ValidationCode.REQUIRED);
                    }
                    for (Placement placement : pkg.getPlacements()) {
                        //validations
                        if (!ioId.equals(placement.getIoId())) {
                            throw new ValidationException("IO's id in request body does not match the requested id.");
                        }
                    }
                    // Data consistency
                    InsertionOrder io = ioDao.get(ioId, session);
                    if (io.getCampaignId().longValue() != pkg.getCampaignId().longValue()) {
                        throw new IllegalArgumentException("The Package Campaign id does not match the IO Campaign id.");
                    }

                    //create package and package costDetails
                    Package packageSaved = packageManager.create(pkg, key, session, dimSession);
                    packageSaved.setPlacements(pkg.getPlacements());
                    List<Placement> placementsResult = createPlacementsForPackage(ioId, user.getContactId(),
                            key, session, dimSession, packageSaved);
                    packageSaved.setPlacements(placementsResult);
                    result.getPackages().add(packageSaved);
                }
            }
            if (!placementsEmpty) {
                List<Placement> placementsResult = placementManager.createStandalonePlacements(ioId,
                        bulkPackagePlacement.getPlacements(), user.getContactId(), key, session, dimSession);
                result.setPlacements(placementsResult);
            }

            placementDao.commit(session);
            dimPackagePlacementDao.commit(dimSession);
        } catch (Exception e) {
            placementDao.rollback(session);
            dimPackagePlacementDao.rollback(dimSession);
            log.warn("Caught a SystemException. Parse to Legacy Exception", e);
            throw e;
        } finally {
            placementDao.close(session);
            dimPackagePlacementDao.close(dimSession);
        }
        return result;
    }

    private List<Placement> createPlacementsForPackage(Long ioId, Long contactId, OauthKey key, SqlSession session,
                                                       SqlSession dimSession, Package pkg) throws Exception {
        List<Placement> result = new ArrayList<>();
        CostDetail pkgCostDetail = null;
        Long inventory = null;
        String rateType = null;
        Date costStartDate = null;
        Date costEndDate = null;

        pkgCostDetail = pkg.getCostDetails().get(0);

        //inventory , adSpend --> divided by the number of placements in the package
        //rateType, rate and flightDates --> copied to all placements from package
        inventory = PackagePlacementUtil.inventoryPlacement(pkgCostDetail.getInventory(), pkg.getPlacements().size());
        rateType = RateTypeEnum.typeOf(pkgCostDetail.getRateType()).name();
        costStartDate = DateConverter.startDate(pkgCostDetail.getStartDate());
        costEndDate = DateConverter.endDate(pkgCostDetail.getEndDate());

        List<PackagePlacement> pkgPlacements = new ArrayList<>();
        //only create new placements
        for (Placement placement : pkg.getPlacements()) {

            //Set CostDetail Values from packageCostDetails
            placement.setRateType(rateType);
            placement.setRate(pkgCostDetail.getPlannedGrossRate());
            placement.setStartDate(costStartDate);
            placement.setEndDate(costEndDate);
            placement.setInventory(inventory);
            placement.setStatus(InsertionOrderStatusEnum.NEW.getName());

            Placement newPlacement = placementManager.create(placement, false, contactId, key, session, dimSession);

            // relationship package-placement
            pkgPlacements.add(new PackagePlacement(pkg.getId(), newPlacement.getId()));
            result.add(newPlacement);
        }

        //create relationship package-placement
        List<PackagePlacement> packagePlacements = packagePlacementDao.create(pkgPlacements, key, session);

        // create dim packagePlacement
        dimPackagePlacementDao.create(packagePlacements, key, dimSession);

        return result;
    }

    public RecordSet<PackagePlacementView> getPackagePlacementViewByInsertionOrder(Long id, OauthKey key) {
        SqlSession session = placementDao.openSession();
        RecordSet<PackagePlacementView> result;
        try {
            result = getPackagePlacementView(AccessStatement.INSERTION_ORDER, id, INSERTION_ORDER, key, session);
        } finally {
            placementDao.close(session);
        }
        return result;
    }

    public RecordSet<PackagePlacementView> getPackagePlacementViewByCampaign(Long id, OauthKey key) {
        SqlSession session = placementDao.openSession();
        RecordSet<PackagePlacementView> result;
        try {
            result = getPackagePlacementView(AccessStatement.CAMPAIGN, id, CAMPAIGN, key, session);
        } finally {
            placementDao.close(session);
        }
        return result;
    }

    private RecordSet<PackagePlacementView> getPackagePlacementView(AccessStatement acessStatement, Long id, String paramName, OauthKey key, SqlSession session) {
        // Validations 
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        if (!userValidFor(acessStatement, Collections.singletonList(id), key.getUserId(), session)) {
            throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
        }
        RecordSet<PackagePlacementView> result = new RecordSet<>();
        List<PackagePlacementView> placements = new ArrayList<>();
        List<PackagePlacementView> packages = new ArrayList<>();
        //call dao
        packages = placementDao.getPackagesForPackagePlacementView(id, paramName, session);
        placements = placementDao.getPlacementsForPackagePlacementView(id, paramName, session);
        for (PackagePlacementView placement : placements) {
            if (placement.getPackageId() != null) {
                for (PackagePlacementView package1 : packages) {
                    if (placement.getPackageId().longValue() == package1.getPackageId().longValue()) {
                        placement.setAdSpend(package1.getPlacementAdSpend());
                        placement.setPackageName(package1.getPackageName());
                    }
                }
            }
        }
        result.setRecords(placements);
        result.setTotalNumberOfRecords(placements.size());
        return result;
    }
}

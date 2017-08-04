package trueffect.truconnect.api.crud.service.importexport;

import trueffect.truconnect.api.commons.exceptions.business.ImportExportCellError;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.importexport.Action;
import trueffect.truconnect.api.commons.model.importexport.CostDetailRawDataView;
import trueffect.truconnect.api.commons.model.importexport.PackageMapId;
import trueffect.truconnect.api.commons.model.importexport.enums.ImportIssueType;
import trueffect.truconnect.api.commons.model.importexport.enums.InAppOption;
import trueffect.truconnect.api.commons.model.importexport.enums.InAppType;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.dao.SiteDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.service.InsertionOrderManager;
import trueffect.truconnect.api.crud.service.PackageManager;
import trueffect.truconnect.api.crud.service.PackagePlacementManager;
import trueffect.truconnect.api.crud.service.PlacementManager;
import trueffect.truconnect.api.crud.service.PublisherManager;
import trueffect.truconnect.api.crud.service.SiteManager;
import trueffect.truconnect.api.crud.service.SiteSectionManager;
import trueffect.truconnect.api.crud.service.SizeManager;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;
import trueffect.truconnect.api.crud.validation.CostDetailImportValidator;
import trueffect.truconnect.api.crud.validation.InsertionOrderValidator;
import trueffect.truconnect.api.crud.validation.MediaRawDataViewValidator;
import trueffect.truconnect.api.crud.validation.PackageValidator;
import trueffect.truconnect.api.crud.validation.PlacementValidator;
import trueffect.truconnect.api.crud.validation.PublisherValidator;
import trueffect.truconnect.api.crud.validation.SiteSectionValidator;
import trueffect.truconnect.api.crud.validation.SiteValidator;
import trueffect.truconnect.api.crud.validation.SizeValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by richard.jaldin on 4/27/2016.
 */
public class MediaImportExportHelper {

    private static final String SEPARATOR = "_@<-*->@_";

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SiteValidator siteValidator;
    private SizeValidator sizeValidator;
    private PlacementValidator placementValidator;
    private PublisherValidator publisherValidator;
    private CostDetailImportValidator costDetailValidator;
    private MediaRawDataViewValidator mediaRawDataViewValidator;

    private InsertionOrderDao insertionOrderDao;
    private PublisherDao publisherDao;
    private PlacementDao placementDao;
    private SiteDao siteDao;
    private SiteSectionDao sectionDao;
    private SizeDao sizeDao;
    private PackageDaoExtended packageDao;

    private InsertionOrderManager ioManager;
    private PublisherManager publisherManager;
    private SiteManager siteManager;
    private SiteSectionManager sectionManager;
    private SizeManager sizeManager;
    private PlacementManager placementManager;
    private PackageManager packageManager;
    private PackagePlacementManager packagePlacementManager;

    private OauthKey key;

    public MediaImportExportHelper(InsertionOrderDao insertionOrderDao, PublisherDao publisherDao, PlacementDao placementDao,
                                   SiteDao siteDao, SiteSectionDao sectionDao, SizeDao sizeDao, PackageDaoExtended packageDao,
                                   InsertionOrderManager ioManager,
                                   PublisherManager publisherManager, SiteManager siteManager,
                                   SiteSectionManager sectionManager, SizeManager sizeManager,
                                   PlacementManager placementManager, PackageManager packageManager,
                                   PackagePlacementManager packagePlacementManager, OauthKey key) {
        this.insertionOrderDao = insertionOrderDao;
        this.publisherDao = publisherDao;
        this.placementDao = placementDao;
        this.siteDao = siteDao;
        this.sectionDao = sectionDao;
        this.sizeDao = sizeDao;
        this.packageDao = packageDao;

        this.ioManager = ioManager;
        this.publisherManager = publisherManager;
        this.siteManager = siteManager;
        this.sectionManager = sectionManager;
        this.sizeManager = sizeManager;
        this.placementManager = placementManager;
        this.packageManager = packageManager;
        this.packagePlacementManager = packagePlacementManager;
        this.key = key;

        this.siteValidator = new SiteValidator();
        this.sizeValidator = new SizeValidator();
        this.placementValidator = new PlacementValidator();
        this.publisherValidator= new PublisherValidator();
        this.costDetailValidator = new CostDetailImportValidator();
        this.mediaRawDataViewValidator = new MediaRawDataViewValidator(publisherValidator,
                new InsertionOrderValidator(), siteValidator, new SiteSectionValidator(),
                sizeValidator, new PackageValidator(), placementValidator, costDetailValidator);
    }

    public List<MediaRawDataView> doCleanup(List<MediaRawDataView> records) {
        List<MediaRawDataView> result = new ArrayList<>();
        for (MediaRawDataView origin : records) {
            MediaRawDataView destination = new MediaRawDataView();

            destination.setOrderNumber(origin.getOrderNumber());
            destination.setOrderName(origin.getOrderName());
            destination.setMediaPackageId(origin.getMediaPackageId());
            destination.setMediaPackageName(origin.getMediaPackageName());
            destination.setPlacementId(origin.getPlacementId());
            destination.setPlacementName(origin.getPlacementName());
            destination.setExtPlacementId(origin.getExtPlacementId());
            destination.setPublisher(origin.getPublisher());
            destination.setSite(origin.getSite());
            destination.setSection(origin.getSection());
            destination.setAdWidth(origin.getAdWidth());
            destination.setAdHeight(origin.getAdHeight());
            destination.setPlannedAdSpend(origin.getPlannedAdSpend());
            destination.setInventory(origin.getInventory());
            destination.setRate(origin.getRate());
            destination.setRateType(origin.getRateType());
            destination.setStartDate(origin.getStartDate());
            destination.setEndDate(origin.getEndDate());
            destination.setPlacementProp1(origin.getPlacementProp1());
            destination.setPlacementProp2(origin.getPlacementProp2());
            destination.setPlacementProp3(origin.getPlacementProp3());
            destination.setPlacementProp4(origin.getPlacementProp4());
            destination.setPlacementProp5(origin.getPlacementProp5());
            destination.setSectionProp1(origin.getSectionProp1());
            destination.setSectionProp2(origin.getSectionProp2());
            destination.setSectionProp3(origin.getSectionProp3());
            destination.setSectionProp4(origin.getSectionProp4());
            destination.setSectionProp5(origin.getSectionProp5());
            destination.setSiteId(origin.getSiteId());
            destination.setIssues(origin.getIssues());
            destination.setFieldsWithFormulaError(origin.getFieldsWithFormulaError());
            destination.setRowError(origin.getRowError());
            result.add(destination);
        }
        return result;
    }

    public List<MediaRawDataView> doModelValidation(List<MediaRawDataView> records, Map<PackageMapId, List<MediaRawDataView>> packageMap) {
        if(packageMap == null) {
            return null;
        }
        BeanPropertyBindingResult errors;
        // Separate out Packages and Standalone Placements
        List<MediaRawDataView> standalonePlacements = new ArrayList<>(records);
        for(Map.Entry<PackageMapId, List<MediaRawDataView>> entry : packageMap.entrySet())
        {
            standalonePlacements.removeAll(entry.getValue());
            // TODO Check for empty costs details
            Iterator<MediaRawDataView> iterator = entry.getValue().iterator();
            Iterator<MediaRawDataView> rejectIterator = entry.getValue().iterator();
            MediaRawDataView primaryPackageRow = iterator.next();
            errors = new BeanPropertyBindingResult(primaryPackageRow, "MediaRawDataView");
            ValidationUtils
                    .invokeValidator(mediaRawDataViewValidator, primaryPackageRow, errors);
            MediaRawDataViewValidator.copyErrors(primaryPackageRow, errors);
            boolean rejectAll = false;
            if(!primaryPackageRow.hasValidCostDetails()) {
                continue;
            }
            List<CostDetailRawDataView> oldCostDetails = null;
            while (iterator.hasNext()) {
                MediaRawDataView currentPackageRow = iterator.next();
                errors = new BeanPropertyBindingResult(currentPackageRow, "MediaRawDataView");
                // Saving old Cost Details to make sure we don't lose original CD for this Placement
                oldCostDetails = currentPackageRow.getCostDetails();
                if(primaryPackageRow.hasValidCostDetails()) {
                    boolean costsEquals =  primaryPackageRow.costDetailsEquals(currentPackageRow);
                    if(!costsEquals) {
                        rejectAll = true;
                    }
                    // Clear up remaining costs to avoid to re-validate
                    currentPackageRow.setCostDetails(new ArrayList<CostDetailRawDataView>());
                } else {
                    // Update status to error for all rows on this Package
                    rejectAll = true;
                }
                ValidationUtils
                        .invokeValidator(mediaRawDataViewValidator, currentPackageRow, errors);
                MediaRawDataViewValidator.copyErrors(currentPackageRow, errors);
                // After validation, re-set Cost Details
                currentPackageRow.setCostDetails(oldCostDetails);
            }

            // Reject all cost only if there are some error
            if(rejectAll) {
                while (rejectIterator.hasNext()) {
                    MediaRawDataView packageRow = rejectIterator.next();
                    errors = new BeanPropertyBindingResult(packageRow, "MediaRawDataView");
                    rejectCosts(packageRow, errors);
                }
            }
        }

        // Now validate Standalone Placements only
        for(MediaRawDataView row : standalonePlacements) {
            errors = new BeanPropertyBindingResult(row, "MediaRawDataView");
            ValidationUtils
                    .invokeValidator(mediaRawDataViewValidator, row, errors);
            MediaRawDataViewValidator.copyErrors(row, errors);
        }
        standalonePlacements.clear();
        return records;
    }

    private void rejectCosts(MediaRawDataView row, BeanPropertyBindingResult errors) {
        String key =
                "packagePlacementImport.error.inconsistentCostsAmongstPackages";
        errors.rejectValue("costDetails",
                key,
                ResourceBundleUtil.getString(key));
        // Do not validate costs individually for this media row
        MediaRawDataViewValidator.copyErrors(row, errors);
    }

    /**
     * Check Cost Details data validation
     *
     * @param campaignId
     * @param packagesMap Media rows coming from the XLSX file to be validated
     * @param userId
     * @param session
     */
    public void doPackageDataValidation(Long campaignId,
                                        Map<PackageMapId, List<MediaRawDataView>> packagesMap,
                                        String userId, SqlSession session) {

        // 1. get a list of valid packages: names or packageIds.
        //    a. if Id == null and all rows and costDetails are valid --> set packageName.
        //    b. if Id != null and all rows and costDetails are valid --> set Ids
        //       - set packageIds
        //       - set packageId+placementId --> Map
        // 2. For packageNames --> get data from DB + costDetails
        //    a. Compare costDetails
        //    b. If costDetail are valids:
        //       - set packageId + quantity of existing placements
        //       - set error
        // 3. For packageIds --> DAC
        //    a. get valid PackageIds + name from DB
        //    b. compare if package needs to be updated
        //    c. Obtain a list of invalid Ids
        //    d. Add the error message for invalid ids
        // 4. For package+Placement Ids --> validation of placement related to this package
        //    a. validate IDs and obtain placementData from Db
        //    b. Obtain the list of invalid Ids
        //    c. Add the error message for the list from invalid ids

        List<String> packageNames = new ArrayList<>();
        List<Long> packageIds = new ArrayList<>();
        Map<String, List<MediaRawDataView>> pkgPlacIdsMap = new HashMap<>();
        //1.
        for (Map.Entry<PackageMapId, List<MediaRawDataView>> entry : packagesMap.entrySet()) {
            PackageMapId packageMapId = entry.getKey();
            List<MediaRawDataView> rows = entry.getValue();

            if (packageMapId.getId() == null) {
                if (!rows.get(0).hasValidPackage() || !rows.get(0).hasValidCostDetails()) {
                    continue;
                }
                packageNames.add(packageMapId.getName());
            } else {
                Long packageId;
                try {
                    packageId = new Long(packageMapId.getId());
                } catch (NumberFormatException e) {
                    continue;
                }
                packageIds.add(packageId);

                for (MediaRawDataView row : rows) {
                    if (StringUtils.isNotBlank(row.getPlacementId())) {
                        Long placementId;
                        try {
                            placementId = new Long(row.getPlacementId());
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        String ids = packageId + "_" + placementId;
                        if (pkgPlacIdsMap.get(ids) == null) {
                            pkgPlacIdsMap.put(ids, new ArrayList<MediaRawDataView>());
                        }
                        pkgPlacIdsMap.get(ids).add(row);
                    }
                }
            }
        }

        List<ImportExportCellError> errors;
        //2. Get data
        if (!packageNames.isEmpty()) {
            List<MediaRawDataView> pkgOnDb =
                    packageDao.getMediaPackageByPackageNames(packageNames, campaignId, session);

            for (MediaRawDataView packageOnDb : pkgOnDb) {
                List<MediaRawDataView> mediasRaw = packagesMap.get(new PackageMapId(null,
                        packageOnDb.getMediaPackageName().toLowerCase()));

                //a.
                boolean hasValidCosts = packageOnDb.costDetailsEquals(mediasRaw.get(0));

                // b.
                if (hasValidCosts) {
                    for (MediaRawDataView row : mediasRaw) {
                        row.setMediaPackageIdDb(Long.valueOf(packageOnDb.getMediaPackageId()));
                        row.setExistingPlacements(packageOnDb.getExistingPlacements());
                    }
                } else {
                    ImportExportCellError error = new ImportExportCellError(
                            ResourceBundleUtil
                                    .getString("packagePlacementImport.error.mismatchingCostDetail",
                                            packageOnDb.getMediaPackageName()),
                            ValidationCode.INVALID);
                    error.setType(ImportIssueType.ERROR);
                    //error.setField("costDetails");
                    errors = new ArrayList<>();
                    errors.add(error);
                    for (MediaRawDataView row : mediasRaw) {
                        row.getIssues().put("rowError", errors);
                    }
                }
            }
        }

        // 3. PackageIds --> DAC
        if (!packageIds.isEmpty()) {
            List<MediaRawDataView> pkgOnDb = packageDao.getMediaPackagesByUserAndIds(packageIds,
                    campaignId, userId, session);

            List<Long> validIds = new ArrayList<>();
            for (MediaRawDataView validPackage : pkgOnDb) {
                Long packageId = new Long(validPackage.getMediaPackageId());
                List<MediaRawDataView> rows =
                        packagesMap.get(new PackageMapId(validPackage.getMediaPackageId(), null));
                for (MediaRawDataView row : rows) {
                    // If equals, it means the row doesn't need to get updated
                    row.setPackageChanged(!row.packagePropertiesEqual(validPackage));
                    row.setCostDetailsChanged(!row.costDetailsEquals(validPackage));
                }
                validIds.add(packageId);
            }

            List<Long> invalidIds = new ArrayList<>();
            invalidIds.addAll(packageIds);
            invalidIds.removeAll(validIds);

            if (!invalidIds.isEmpty()) {
                ImportExportCellError error = new ImportExportCellError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER);
                error.setType(ImportIssueType.ERROR);
                error.setField("mediaPackageId");

                errors = new ArrayList<>();
                errors.add(error);
                for (Long invalidPkgId : invalidIds) {
                    List<MediaRawDataView> rows =
                            packagesMap.get(new PackageMapId(invalidPkgId.toString(), null));
                    for (MediaRawDataView row : rows) {
                        row.getIssues().put("rowError", errors);
                    }
                }
            }
        }

        // 4.
        Set<String> setIds = new HashSet<>(pkgPlacIdsMap.keySet());
        if (!setIds.isEmpty()) {
            List<MediaRawDataView> pkgOnDb = placementDao.getMediaPlacementByPkgPlacIds(
                    new ArrayList<>(setIds), campaignId, session);

            List<String> validIds = new ArrayList<>();
            for (MediaRawDataView validPlacement : pkgOnDb) {
                String pkgPlacId = validPlacement.getMediaPackageId() + "_" 
                        + validPlacement.getPlacementId();
                List<MediaRawDataView> rows = pkgPlacIdsMap.get(pkgPlacId);
                for (MediaRawDataView row : rows) {
                    // If equals, it means the row doesn't need to get updated
                    row.setPlacementChanged(!row.placementPropertiesEqual(validPlacement));
                }
                validIds.add(pkgPlacId);
            }

            List<String> invalidIds = new ArrayList<>();
            invalidIds.addAll(setIds);
            invalidIds.removeAll(validIds);

            if (!invalidIds.isEmpty()) {
                ImportExportCellError error = new ImportExportCellError(
                        ResourceBundleUtil.getString(
                                "packagePlacementImport.error.placementHasNoRelationshipWithPackage"),
                        ValidationCode.INVALID);
                error.setType(ImportIssueType.ERROR);
                error.setField("placementId");

                errors = new ArrayList<>();
                errors.add(error);
                for (String invalidPkgId : invalidIds) {
                    List<MediaRawDataView> rows = pkgPlacIdsMap.get(invalidPkgId);
                    for (MediaRawDataView row : rows) {
                        row.getIssues().put("rowError", errors);
                    }
                }
            }
        }
    }

    /**
     * Check existence of placement elements
     *
     * @param campaignId
     * @param records Media rows coming from the XLSX file to be validated
     * @param session
     * @param userId The {@code OauthKey} for the requester user
     * @return List CreativeInsertionRawDataView
     */
    public List<MediaRawDataView> doPlacementDataValidation(Long campaignId,
                                                            List<MediaRawDataView> records,
                                                            SqlSession session,
                                                            String userId) {

        // 1. set the IoNumber + ioName and MediaRawDataView object into a mapIoData Map.
        //    set the publisherName and MediaRawDataView object into a mapPublisherData Map.
        // 2. Check DAC for placements (placementId != null)
        // 3. validate IDs and obtain existend IDs
        // 4. set the Ids obtained for the existent Data
        Map<String, List<MediaRawDataView>> mapIoData = new HashMap<>();
        Map<String, List<MediaRawDataView>> mapPublisherData = new HashMap<>();
        Map<String, List<MediaRawDataView>> mapSiteData = new HashMap<>();
        Map<String, List<MediaRawDataView>> mapSectionData = new HashMap<>();
        Map<String, List<MediaRawDataView>> mapSizeData = new HashMap<>();
        Map<Long, List<MediaRawDataView>> mapPlacements = new HashMap<>();

        //1.
        for (MediaRawDataView row : records) {
            if (row.getPlacementId() == null) {
                if (row.hasValidInsertionOrder()) {
                    String ioNumberName =
                            row.getOrderNumber() + SEPARATOR + row.getOrderName().toUpperCase();

                    List<MediaRawDataView> positions = mapIoData.get(ioNumberName);
                    if (positions == null) {
                        positions = new ArrayList<>();
                    }
                    positions.add(row);
                    mapIoData.put(ioNumberName, positions);
                }

                if (row.hasValidPublisher()) {
                    List<MediaRawDataView> positions =
                            mapPublisherData.get(row.getPublisher().toUpperCase());
                    if (positions == null) {
                        positions = new ArrayList<>();
                    }
                    positions.add(row);
                    mapPublisherData.put(row.getPublisher().toUpperCase(), positions);
                }

                if (row.hasValidSize()) {
                    String widthHeight = row.getAdWidth() + "x" + row.getAdHeight();

                    List<MediaRawDataView> positions = mapSizeData.get(widthHeight);
                    if (positions == null) {
                        positions = new ArrayList<>();
                    }
                    positions.add(row);
                    mapSizeData.put(widthHeight, positions);
                }
            }

            // Build a map by placementIds for DAC validation (StandAlone)
            if (StringUtils.isBlank(row.getMediaPackageId()) && StringUtils
                    .isNotBlank(row.getPlacementId())) {
                Long placementId;
                try {
                    placementId = new Long(row.getPlacementId());
                } catch (NumberFormatException e) {
                    continue;
                }
                if (mapPlacements.get(placementId) == null) {
                    mapPlacements.put(placementId, new ArrayList<MediaRawDataView>());
                }
                mapPlacements.get(placementId).add(row);
            }
        }

        // 2.
        List<ImportExportCellError> errors;
        Set<Long> setIds = new HashSet<>(mapPlacements.keySet());
        if (!setIds.isEmpty()) {
            List<MediaRawDataView> placementsOnDb = placementDao
                    .getMediaPlacementByUserAndIds(new ArrayList<>(setIds), campaignId,
                            key.getUserId(), session);
            List<Long> validIds = new ArrayList<>();

            ImportExportCellError error = new ImportExportCellError(ResourceBundleUtil
                    .getString("packagePlacementImport.error.nonStandAlonePlacement"),
                    ValidationCode.INVALID);
            error.setType(ImportIssueType.ERROR);
            error.setField("mediaPackageId");
            errors = new ArrayList<>();
            errors.add(error);

            for (MediaRawDataView validPlacement : placementsOnDb) {
                Long placementId = Long.valueOf(validPlacement.getPlacementId());
                List<MediaRawDataView> rows = mapPlacements.get(placementId);
                for (MediaRawDataView row : rows) {
                    // If equals, it means the row doesn't need to get updated
                    row.setPlacementChanged(!row.placementPropertiesEqual(validPlacement));
                    row.setCostDetailsChanged(!row.costDetailsEquals(validPlacement));

                    // non standAlone placement
                    if (StringUtils.isNotBlank(validPlacement.getMediaPackageId())) {
                        row.getIssues().put("rowError", errors);
                    }
                }
                validIds.add(placementId);
            }

            List<Long> invalidIds = new ArrayList<>();
            invalidIds.addAll(setIds);
            invalidIds.removeAll(validIds);

            if (!invalidIds.isEmpty()) {
                ImportExportCellError dacError = new ImportExportCellError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER);
                dacError.setType(ImportIssueType.ERROR);
                dacError.setField("placementId");

                errors = new ArrayList<>();
                errors.add(dacError);
                for (Long invalidId : invalidIds) {
                    List<MediaRawDataView> rows = mapPlacements.get(invalidId);
                    for (MediaRawDataView row : rows) {
                        row.getIssues().put("rowError", errors);
                    }
                }
            }
        }

        // 3. 4.
        if (mapIoData.keySet().size() > 0) {
            List<InsertionOrder> existentIos = insertionOrderDao.getIosByCampaignIdAndIoNumberName(
                    mapIoData.keySet(), userId, campaignId, session);

            for (InsertionOrder io : existentIos) {
                List<MediaRawDataView> positions = mapIoData.get(io.getName());
                for (MediaRawDataView position : positions) {
                    position.setIoId(io.getId());
                }
            }
        }

        if (mapPublisherData.keySet().size() > 0) {
            List<Publisher> existentPublishers = publisherDao
                    .getByCampaignIdAndName(mapPublisherData.keySet(), userId, campaignId, session);

            for (Publisher publisher : existentPublishers) {
                List<MediaRawDataView> positions = mapPublisherData.get(publisher.getName());
                for (MediaRawDataView record : positions) {
                    record.setPublisherId(publisher.getId());

                    if (record.hasValidPublisher() && record.hasValidSite()) {
                        String publisherSiteName =
                                publisher.getId() + SEPARATOR + record.getSite().toUpperCase();

                        if (mapSiteData.get(publisherSiteName) == null) {
                            mapSiteData
                                    .put(publisherSiteName, new ArrayList<MediaRawDataView>());
                        }
                        mapSiteData.get(publisherSiteName).add(record);
                    }
                }
            }
        }

        if (mapSiteData.keySet().size() > 0) {
            List<Site> existentSites = siteDao.getByCampaignIdAndPublisherNameSiteName(
                    mapSiteData.keySet(), userId, campaignId, session);

            for (Site site : existentSites) {
                List<MediaRawDataView> positions = mapSiteData.get(site.getName());
                for (MediaRawDataView record : positions) {
                    record.setSiteIdDb(site.getId());

                    if (record.hasValidPublisher() && record.hasValidSite() &&
                            record.hasValidSection()) {
                        String publisherSiteSectionName = record.getPublisherId() +
                                SEPARATOR + record.getSiteIdDb() + SEPARATOR +
                                record.getSection().toUpperCase();

                        if (mapSectionData.get(publisherSiteSectionName) == null) {
                            mapSectionData.put(publisherSiteSectionName,
                                    new ArrayList<MediaRawDataView>());
                        }
                        mapSectionData.get(publisherSiteSectionName).add(record);
                    }
                }
            }
        }

        if (mapSectionData.keySet().size() > 0) {
            List<SiteSection> existentSections = sectionDao.getByCampaignIdAndPublisherNameSiteName(
                    mapSectionData.keySet(), userId, campaignId, session);

            for (SiteSection section : existentSections) {
                List<MediaRawDataView> positions = mapSectionData.get(section.getName());
                for (MediaRawDataView position : positions) {
                    position.setSectionId(section.getId());
                }
            }
        }

        if (mapSizeData.keySet().size() > 0) {
            List<Size> existentSizes = sizeDao.getByCampaignIdAndWidthHeight(mapSizeData.keySet(),
                    userId, campaignId, session);

            for (Size size : existentSizes) {
                List<MediaRawDataView> positions = mapSizeData.get(size.getLabel());
                for (MediaRawDataView position : positions) {
                    position.setSizeId(size.getId());
                }
            }
        }
        return records;
    }

    /**
     * Check placement duplication based on Site, Section and Size.
     *
     * @param campaignId
     * @param rows Media rows coming from the XLSX file to be validated
     * @param session
     * @param userId The {@code OauthKey} for the requester user
     * @return List CreativeInsertionRawDataView
     */
    public List<MediaRawDataView> doDuplicatedValidation(Long campaignId,
                                                         List<MediaRawDataView> rows,
                                                         SqlSession session,
                                                         String userId) {

        Map<String, MediaRawDataView> uniquePlacements = new HashMap<>();
        List<ImportExportCellError> errors;
        for (MediaRawDataView row : rows) {
            if (row.getErrors().isEmpty() &&
                    !StringUtils.isEmpty(row.getSite()) &&
                    !StringUtils.isEmpty(row.getSection()) &&
                    !StringUtils.isEmpty(row.getAdWidth()) &&
                    !StringUtils.isEmpty(row.getAdHeight()) &&
                    StringUtils.isEmpty(row.getPlacementId())) {
                String keyString = row.getSite() + SEPARATOR + row.getSection() + SEPARATOR + row
                        .getAdWidth() + SEPARATOR + row.getAdHeight();
                keyString = keyString.toUpperCase();

                if (uniquePlacements.containsKey(keyString)) {
                    ImportExportCellError error = new ImportExportCellError(
                            ResourceBundleUtil
                                    .getString("global.error.duplicatedPlacement", row.getSite(),
                                            row.getSection(),
                                            row.getAdWidth() + "x" + row.getAdHeight()),
                            ValidationCode.INVALID);
                    error.setType(ImportIssueType.WARNING);
                    error.setInAppType(InAppType.DUPLICATED_PLACEMENT);
                    error.setOptions(getOptionsByType(InAppType.DUPLICATED_PLACEMENT));
                    error.setDefaultOption(getDefaultOptionByType(InAppType.DUPLICATED_PLACEMENT));

                    errors = new ArrayList<>();
                    errors.add(error);
                    row.getIssues().put("rowError", errors);
                } else {
                    uniquePlacements.put(keyString, row);
                }
            }
        }

        if(uniquePlacements.size() > 0) {
            List<Placement> existingPlacements = placementDao
                    .getBySiteSectionAndSize(uniquePlacements.keySet(), userId, campaignId,
                            session);
            for (Placement placement : existingPlacements) {
                String keyString = placement.getSiteName() + SEPARATOR + placement
                        .getSectionName() + SEPARATOR + placement.getWidth() + SEPARATOR + placement
                        .getHeight();
                MediaRawDataView mrdv = uniquePlacements.get(keyString.toUpperCase());

                if (mrdv != null) {
                    ImportExportCellError error = new ImportExportCellError(
                            ResourceBundleUtil.getString("global.error.duplicatedPlacement",
                                    mrdv.getSite(), mrdv.getSection(),
                                    mrdv.getAdWidth() + "x" + mrdv.getAdHeight()),
                            ValidationCode.INVALID);
                    error.setType(ImportIssueType.WARNING);
                    error.setInAppType(InAppType.DUPLICATED_PLACEMENT);
                    error.setOptions(getOptionsByType(InAppType.DUPLICATED_PLACEMENT));
                    error.setDefaultOption(getDefaultOptionByType(InAppType.DUPLICATED_PLACEMENT));

                    errors = new ArrayList<>();
                    errors.add(error);
                    mrdv.getIssues().put("rowError", errors);
                }
            }
        }

        return rows;
    }

    public int saveMediaHierarchy(Long campaignId, List<MediaRawDataView> records, User user,
                                  MediaBuy mediaBuy, SqlSession session, SqlSession dimSession) {

        HashMap<String, Long> iosMap = new HashMap<>();
        HashMap<String, Long> publishersMap = new HashMap<>();
        HashMap<String, Long> sitesMap = new HashMap<>();
        HashMap<String, Long> sectionsMap = new HashMap<>();
        HashMap<String, Long> sizesMap = new HashMap<>();
        HashMap<String, Package> packagesMap = new HashMap<>();
        List<Placement> placements = new ArrayList<>();

        int result = 0;
        for (MediaRawDataView record : records) {
            String publisherIdForMap = null;
            String ioIdForMap = null;
            String siteIdForMap = null;
            String sectionIdForMap = null;
            String sizeIdForMap = null;
            if (record.getPlacementId() == null) {
                // 1. Create Publisher
                publisherIdForMap = record.getPublisher();
                if (publishersMap.get(publisherIdForMap) == null) {
                    if (record.getPublisherId() != null) { // No need to create a new Publisher
                        publishersMap.put(publisherIdForMap, record.getPublisherId());
                    } else { // Need to create a new Publisher
                        // 1. Create new Publisher
                        Publisher publisher = new Publisher();
                        publisher.setName(record.getPublisher());
                        publisher.setAgencyId(user.getAgencyId());
                        publisherValidator.applyDefaults(publisher);
                        // 2. Persist
                        publisher = publisherManager.createPublisher(publisher, key, session);
                        publishersMap.put(publisherIdForMap, publisher.getId());
                    }
                }

                // 2. Create IO
                ioIdForMap = record.getOrderNumber() + SEPARATOR + record.getOrderName();
                if (iosMap.get(ioIdForMap) == null) {
                    if (record.getIoId() != null) { // No need to create a new IO
                        iosMap.put(ioIdForMap, record.getIoId());
                    } else { // Need to create a new IO
                        //1. Create new IO
                        InsertionOrder io = new InsertionOrder();
                        io.setName(record.getOrderName());
                        io.setIoNumber(Integer.valueOf(record.getOrderNumber()));
                        io.setMediaBuyId(mediaBuy.getId());
                        io.setPublisherId(publishersMap.get(publisherIdForMap));
                        //2. Persist
                        io = ioManager.create(io, session, key);
                        iosMap.put(ioIdForMap, io.getId());
                    }
                }

                // 3. Create Site
                siteIdForMap = publishersMap.get(publisherIdForMap) + SEPARATOR + record.getSite();
                if (sitesMap.get(siteIdForMap) == null) {
                    if (record.getSiteIdDb() != null) { // No need to create a new Site
                        sitesMap.put(siteIdForMap, record.getSiteIdDb());
                    } else { // Need to create a new Site
                        // 1. Create new Site
                        Site site = new Site();
                        site.setName(record.getSite());
                        site.setPublisherId(publishersMap.get(publisherIdForMap));
                        // 2. Apply defaults
                        site = siteValidator.applyDefaults(site);
                        // 3. Persist
                        site = siteManager.create(site, key, session);
                        sitesMap.put(siteIdForMap, site.getId());
                    }
                }

                // 4. Create Section
                sectionIdForMap = publishersMap.get(publisherIdForMap) + SEPARATOR + sitesMap
                        .get(siteIdForMap) + SEPARATOR + record.getSection();
                if (sectionsMap.get(sectionIdForMap) == null) {
                    if (record.getSectionId() != null) { // No need to create a new Section
                        sectionsMap.put(sectionIdForMap, record.getSectionId());
                    } else { // Need to create a new Section
                        // 1. Create new Section
                        SiteSection section = new SiteSection();
                        section.setName(record.getSection());
                        section.setSiteId(sitesMap.get(siteIdForMap));
                        section.setExtProp1(record.getSectionProp1());
                        section.setExtProp2(record.getSectionProp2());
                        section.setExtProp3(record.getSectionProp3());
                        section.setExtProp4(record.getSectionProp4());
                        section.setExtProp5(record.getSectionProp5());
                        // 2. Persist
                        section = sectionManager.createSection(section, key, session);
                        sectionsMap.put(sectionIdForMap, section.getId());
                    }
                }

                // 5. Create Size
                sizeIdForMap = record.getAdWidth() + "x" + record.getAdHeight();
                if (sizesMap.get(sizeIdForMap) == null) {
                    if (record.getSizeId() != null) { // No need to create a new Size
                        sizesMap.put(sizeIdForMap, record.getSizeId());
                    } else { // Need to create a new Size
                        // 1. Create new Size
                        Size size = new Size();
                        size.setWidth(Long.valueOf(record.getAdWidth()));
                        size.setHeight(Long.valueOf(record.getAdHeight()));
                        size.setAgencyId(user.getAgencyId());
                        // 2. Apply defaults
                        size = sizeValidator.applyDefaults(size);
                        // 3. Persist
                        size = sizeManager.createSize(size, key, session);
                        sizesMap.put(sizeIdForMap, size.getId());
                    }
                }
            }

            if (StringUtils.isEmpty(record.getMediaPackageName())) {
                // 6a. StandAlone Placement: Create instance of Placement with its costs
                Placement placement = getPlacement(record, campaignId,
                        ioIdForMap != null ? iosMap.get(ioIdForMap) : null,
                        siteIdForMap != null ? sitesMap.get(siteIdForMap) : null,
                        sectionIdForMap != null ? sectionsMap.get(sectionIdForMap) : null,
                        sizeIdForMap != null ? sizesMap.get(sizeIdForMap) : null, true);
                if ((record.getPlacementId() != null && (record.isPlacementChanged() ||
                        record.isCostDetailsChanged())) || record.getPlacementId() == null) {
                    placements.add(placement);
                }
            } else {
                // 6b. Package-Placements: Group the placements by package name
                if (packagesMap.get(record.getMediaPackageName()) == null) {
                    packagesMap.put(record.getMediaPackageName(), getPackage(campaignId, record));
                }
                Placement placement = getPlacement(record, campaignId,
                        ioIdForMap != null ? iosMap.get(ioIdForMap) : null,
                        siteIdForMap != null ? sitesMap.get(siteIdForMap) : null,
                        sectionIdForMap != null ? sectionsMap.get(sectionIdForMap) : null,
                        sizeIdForMap != null ? sizesMap.get(sizeIdForMap) : null, false);
                if ((record.getPlacementId() != null && (record.isPlacementChanged() ||
                        record.isCostDetailsChanged())) || record.getPlacementId() == null) {
                    // Copy cost information to placement
                    CostDetail costDetail =
                            packagesMap.get(record.getMediaPackageName()).getCostDetails().get(0);
                    placement.setStartDate(costDetail.getStartDate());
                    placement.setEndDate(costDetail.getEndDate());

                    packagesMap.get(record.getMediaPackageName()).getPlacements().add(placement);
                }
            }
        }
        // persist placements standAlone + package placements
        result = persistMediaData(user.getContactId(), placements, packagesMap, session, dimSession);
        return result;
    }

    private int persistMediaData(Long contactId, List<Placement> placements,
                                 HashMap<String, Package> packages, SqlSession session,
                                 SqlSession dimSession) {
        Integer result = 0;

        // Persist standAlone Placements
        for (Placement placement : placements) {
            if(placement.getId() == null) {
                placementManager
                        .createPlacement(placement, true, contactId, key, session, dimSession);
            } else {
                placementManager.updateOnImport(placement, true, key, session, dimSession);
            }
            result++;
        }

        // Persist Package-Placements
        for (Map.Entry<String, Package> entry : packages.entrySet()) {
            Package pkg = entry.getValue();
            Package savedPackage;
            Long pkgId = pkg.getId();
            boolean hasPackageBeenUpdated = false;
            if (pkg.getId() == null) {
                // create new package
                savedPackage = packageManager.createPackage(pkg, key, session, dimSession);
                pkgId = savedPackage.getId();
                pkg.setPlacementCount(0L);
            } else {
                if(pkg.isChanged()) {
                    savedPackage = packageManager.updateOnImport(pkg, key, session, dimSession);
                    hasPackageBeenUpdated = true;
                } else {
                    savedPackage = packageManager.getPackage(pkg.getId(), session);
                }
            }
            if(pkg.getId() == null) {
                // add placements into package
                Long ioId = pkg.getPlacements().get(0).getIoId();
                placementManager.addNewPlacementsToPackage(pkgId, pkg.getPlacementCount(),
                        pkg.getCostDetails().get(0), ioId, pkg.getPlacements(), contactId, key,
                        session, dimSession);
            } else {
                if(pkg.getPlacements() != null && !pkg.getPlacements().isEmpty()) {
                    // Need to know how many placements will be in total
                    long totalPlacementsForPackage = savedPackage.getPlacementCount();
                    for (Placement placement : pkg.getPlacements()) {
                        if(placement.getId() != null) {
                            placementManager
                                    .updateOnImport(placement, false, key, session, dimSession);
                        } else {
                            placementManager.addNewPlacementsToPackage(pkgId,
                                    totalPlacementsForPackage,
                                    pkg.getCostDetails().get(0), placement.getIoId(),
                                    Arrays.asList(placement), contactId, key,
                                    session, dimSession);
                            totalPlacementsForPackage++;
                        }
                    }
                }
            }
            result = result + (hasPackageBeenUpdated && pkg.getPlacements().size() == 0
                    ? 1 // count of the package only
                    : pkg.getPlacements().size()); // count of placements updated without duplicate for the package update
        }
        return result;
    }

    private Placement getPlacement(MediaRawDataView record, Long campaignId, Long ioId, Long siteId,
                                   Long sectionId, Long sizeId, boolean isStandalonePlacement) {
        // 1. Create Placement
        Placement placement = new Placement();

        // 2. Set needed data
        placement.setCampaignId(campaignId);
        placement.setWidth(record.getAdWidth() != null ? Long.valueOf(record.getAdWidth()) : null);
        placement.setHeight(record.getAdHeight() != null ? Long.valueOf(record.getAdHeight()) : null);

        if(isStandalonePlacement) {
            // 3. Create Cost Details
            List<CostDetail> costDetails = new ArrayList<>();
            for (CostDetailRawDataView cost : record.getCostDetails()) {
                CostDetail costDetail = new CostDetail();
                try {
                    costDetail.setStartDate(DateConverter.parseForImport(cost.getStartDate()));
                    costDetail.setEndDate(DateConverter.parseForImport(cost.getEndDate()));
                } catch (ParseException e) {
                    log.error(ResourceBundleUtil.getString("global.error.dateFormat",
                            String.format("%s or %s ",
                                    ResourceBundleUtil.getString("global.label.startDate"),
                                    ResourceBundleUtil.getString("global.label.endDate"))));
                }
                String plannedAdSpend = cost.getPlannedAdSpend() != null ? cost.getPlannedAdSpend()
                        : cost.getPlannedAdSpendAutoGenerated();
                String rate = cost.getRate() != null ? cost.getRate() : cost.getRateAutoGenerated();
                String rateType = cost.getRateType() != null ? cost.getRateType()
                        : cost.getRateTypeAutoGenerated();
                costDetail.setPlannedGrossAdSpend(Double.valueOf(plannedAdSpend));
                costDetail.setPlannedGrossRate(Double.valueOf(rate));
                costDetail.setRateType(RateTypeEnum.valueOf(rateType).getCode());

                // 3.1 Add inventory value
                Long inventory = PackagePlacementUtil.calculateInventory(
                        costDetail.getPlannedGrossRate(), costDetail.getPlannedGrossAdSpend(),
                        RateTypeEnum.typeOf(costDetail.getRateType()).name());
                costDetail.setInventory(inventory);

                costDetails.add(costDetail);
            }

            // 3.2. Set provided properties
            CostDetail costDetail = costDetails.get(0);
            placement.setAdSpend(costDetail.getPlannedGrossAdSpend());
            placement.setRate(costDetail.getPlannedGrossRate());
            placement.setRateType(RateTypeEnum.typeOf(costDetail.getRateType()).name());
            placement.setStartDate(costDetail.getStartDate());
            placement.setEndDate(costDetail.getEndDate());
            placement.setInventory(costDetail.getInventory());

            // 3.3. Add Cost Details
            placement.setCostDetails(costDetails);
        }

        // 4. Set provided properties
        placement.setId(record.getPlacementId() != null ? Long
                .valueOf(record.getPlacementId()) : null);
        placement.setName(record.getPlacementNameAutoGenerated());
        placement.setExternalId(record.getExtPlacementId());
        placement.setExtProp1(record.getPlacementProp1());
        placement.setExtProp2(record.getPlacementProp2());
        placement.setExtProp3(record.getPlacementProp3());
        placement.setExtProp4(record.getPlacementProp4());
        placement.setExtProp5(record.getPlacementProp5());

        // 5. Set needed Ids
        placement.setIoId(ioId);
        placement.setSiteId(siteId);
        placement.setSiteSectionId(sectionId);
        placement.setSizeId(sizeId);

        // 6. Apply defaults
        placement = placementValidator.applyDefaults(placement);

        return placement;
    }

    private Package getPackage(Long campaignId, MediaRawDataView record) {
        // 1. Create Package
        Package pkg = new Package();
        pkg.setId(record.getMediaPackageIdDb() == null ? null : record.getMediaPackageIdDb());
        pkg.setPlacementCount(record.getExistingPlacements());
        pkg.setId(record.getMediaPackageId() != null ? Long
                .valueOf(record.getMediaPackageId()) : null);
        pkg.setName(record.getMediaPackageName());
        pkg.setCampaignId(campaignId);
        pkg.setChanged(record.isPackageChanged() || record.isCostDetailsChanged());
        pkg.setPlacements(new ArrayList<Placement>());

        // 2. Create Cost Details
        List<CostDetail> costDetails = new ArrayList<>();
        for (CostDetailRawDataView cost : record.getCostDetails()) {
            CostDetail costDetail = new CostDetail();
            try {
                costDetail.setStartDate(DateConverter.parseForImport(cost.getStartDate()));
                costDetail.setEndDate(DateConverter.parseForImport(cost.getEndDate()));
            } catch (ParseException e) {
                log.error(ResourceBundleUtil.getString("global.error.dateFormat",
                        String.format("%s or %s ",
                                ResourceBundleUtil.getString("global.label.startDate"),
                                ResourceBundleUtil.getString("global.label.endDate"))));
            }
            String plannedAdSpend = cost.getPlannedAdSpend() != null ? cost.getPlannedAdSpend()
                    : cost.getPlannedAdSpendAutoGenerated();
            String rate = cost.getRate() != null ? cost.getRate() : cost.getRateAutoGenerated();
            String rateType = cost.getRateType() != null ? cost.getRateType()
                    : cost.getRateTypeAutoGenerated();
            costDetail.setPlannedGrossAdSpend(Double.valueOf(plannedAdSpend));
            costDetail.setPlannedGrossRate(Double.valueOf(rate));
            costDetail.setRateType(RateTypeEnum.valueOf(rateType).getCode());

            // 2.1 Add inventory value
            Long inventory = PackagePlacementUtil.calculateInventory(
                    costDetail.getPlannedGrossRate(), costDetail.getPlannedGrossAdSpend(),
                    RateTypeEnum.typeOf(costDetail.getRateType()).name());
            costDetail.setInventory(inventory);

            costDetails.add(costDetail);
        }

        // 2.3. Add Cost Details
        pkg.setCostDetails(costDetails);

        return pkg;
    }

    public List<MediaRawDataView> doInAppCorrections(List<MediaRawDataView> records,
                                                     List<Action> actions) {
        List<MediaRawDataView> result = new ArrayList<>();
        for (MediaRawDataView record : records) {
            // 1. Check for in-app actions
            if (record.getActions() != null && !record.getActions().isEmpty()) {
                for (Action action : actions) {
                    if (record.getRowError().equals(String.valueOf(action.getRownum()))) {
                        for (ImportExportCellError error : record.getActions()) {
                            // 2. Fix error
                            if (error.getInAppType().equals(InAppType.DUPLICATED_PLACEMENT) &&
                                    action.getInAppType().equals(InAppType.DUPLICATED_PLACEMENT)) {
                                if (action.getAction() != null) {
                                    switch (action.getAction()) {
                                        case DUPLICATE:
                                            // 3. Remove error from the record
                                            for (Map.Entry<String, List<ImportExportCellError>> entry : record
                                                    .getIssues().entrySet()) {
                                                if (!entry.getValue().isEmpty() &&
                                                        entry.getValue().contains(error)) {
                                                    entry.getValue().remove(
                                                            entry.getValue().indexOf(error));
                                                    if(entry.getValue().isEmpty()) {
                                                        record.getIssues().remove(entry.getKey());
                                                    }
                                                }
                                            }
                                            result.add(record);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else { // 4. Add the ones with no in-app actions
                result.add(record);
            }
        }
        return result;
    }

    /**
     * Moves all individual properties for Costs information in a {@code MediaRawDataView} into
     * an structure that holds one or multiple Costs per a single {@code MediaRawDataView}.
     * <p>
     * The resulting list has no rows for Costs
     * </p>
     *
     * @param rows The input {@code MediaRawDataView} list
     * @return a new list of {@code MediaRawDataView} that includes one or multiple
     * Costs information per each {@code MediaRawDataView} instance
     */
    public List<MediaRawDataView> doUnflatten(List<MediaRawDataView> rows) {
        List<MediaRawDataView> output = new ArrayList<>();
        if( rows == null || rows.isEmpty()){
            return null;
        }
        for( MediaRawDataView row : rows ) {
            if(row.isEmpty()) {
                continue;
            }

            CostDetailRawDataView costDetail = new CostDetailRawDataView(
                    row.getPlannedAdSpend(),
                    row.getInventory(),
                    row.getRate(),
                    row.getRateType(),
                    row.getStartDate(),
                    row.getEndDate());

            costDetail.setRowNumber(row.getRowError());

            if (row.getFieldsWithFormulaError().contains("plannedAdSpend")) {
                costDetail.getFieldsWithFormulaError().add("plannedAdSpend");
            }
            if (row.getFieldsWithFormulaError().contains("inventory")) {
                costDetail.getFieldsWithFormulaError().add("inventory");
            }
            if (row.getFieldsWithFormulaError().contains("rate")) {
                costDetail.getFieldsWithFormulaError().add("rate");
            }
            if (row.getFieldsWithFormulaError().contains("rateType")) {
                costDetail.getFieldsWithFormulaError().add("rateType");
            }
            if (row.getFieldsWithFormulaError().contains("startDate")) {
                costDetail.getFieldsWithFormulaError().add("startDate");
            }
            if (row.getFieldsWithFormulaError().contains("endDate")) {
                costDetail.getFieldsWithFormulaError().add("endDate");
            }

            if(!row.isCostDetailRow()) {
                // Add as a CostDetailRawDataView
                row.getCostDetails().add(costDetail);
                // Clean up Cost data for this Row
                row.setPlannedAdSpend("");
                row.setInventory("");
                row.setRate("");
                row.setRateType("");
                row.setStartDate("");
                row.setEndDate("");
                // Add this row to the final list
                output.add(row);
            } else {
                // Row is a Cost Detail only
                output.get(output.size() - 1).getCostDetails().add(costDetail);
            }
        }
        return output;
    }

    public List<InAppOption> getOptionsByType(InAppType type){
        List<InAppOption> options = new ArrayList<>();

        switch(type) {
            case DUPLICATED_PLACEMENT:
                options.add(InAppOption.DUPLICATE);
                options.add(InAppOption.DO_NOT_IMPORT);
                break;
        }

        return options;
    }

    public InAppOption getDefaultOptionByType(InAppType type){
        InAppOption result = null;

        switch(type) {
            case DUPLICATED_PLACEMENT:
                result = InAppOption.DUPLICATE;
                break;
        }

        return result;
    }

    /**
     * Builds a {@code Map} that contains as key the package name in lower case, and as value
     * a {@code java.util.List} of {@code MediaRawDataView} that holds all Placements below such
     * package name
     * @param records The plain model where to extract packages from
     * @return The resulting {@code Map}
     */
    public Map<PackageMapId, List<MediaRawDataView>> doPackageMap(List<MediaRawDataView> records) {
        if(records == null) {
            return null;
        }

        Map<PackageMapId, List<MediaRawDataView>> mapPackages = new HashMap<>();
        //1. mapPackages --> packageName or packageId
        for (MediaRawDataView row : records) {
            if (StringUtils.isBlank(row.getMediaPackageName()) && StringUtils.isBlank(row.getMediaPackageId())) {
                //standalone placement
                continue;
            }
            String name = row.getMediaPackageName() == null ? null : row.getMediaPackageName().toLowerCase();
            PackageMapId pkgId = new PackageMapId(row.getMediaPackageId(), name);
            List<MediaRawDataView> positions = mapPackages.get(pkgId);
            if (positions == null) {
                positions = new ArrayList<>();
            }
            positions.add(row);
            mapPackages.put(pkgId, positions);
        }
        return mapPackages;
    }
}

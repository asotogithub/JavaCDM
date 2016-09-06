package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.business.ImportExportCellError;
import trueffect.truconnect.api.commons.exceptions.business.ImportExportRowError;
import trueffect.truconnect.api.commons.exceptions.business.enums.FileUploadCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.importexport.Action;
import trueffect.truconnect.api.commons.model.importexport.CostDetailRawDataView;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawErrorDataView;
import trueffect.truconnect.api.commons.model.importexport.MediaRawErrorDataView;
import trueffect.truconnect.api.commons.model.importexport.PackageMapId;
import trueffect.truconnect.api.commons.model.importexport.enums.ImportIssueType;
import trueffect.truconnect.api.commons.model.importexport.XlsErrorTemplateDescriptor;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.commons.exceptions.InvalidTemplateException;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.ImportExportErrors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.util.ExportImportUtil;
import trueffect.truconnect.api.commons.util.SerializationUtil;
import trueffect.truconnect.api.commons.util.TempFileUtil;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.MediaBuyDao;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoBase;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.dao.SiteDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.service.importexport.CreativeInsertionImportExportHelper;
import trueffect.truconnect.api.crud.service.importexport.Exporter;
import trueffect.truconnect.api.crud.service.importexport.IssuesExporter;
import trueffect.truconnect.api.crud.service.importexport.MediaExporter;
import trueffect.truconnect.api.crud.service.importexport.MediaImportExportHelper;
import trueffect.truconnect.api.crud.service.importexport.CreativeInsertionExporter;
import trueffect.truconnect.api.crud.validation.CreativeInsertionRawDataViewValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import com.sun.jersey.core.header.FormDataContentDisposition;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.ExecutorType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author marleny.patsi
 */
public class ImportExportManager extends AbstractGenericManager{

    public static final String SCHEDULE_EXPORT_PATH =
            TempFileUtil.OS_TMP_PATH + File.separator + Constants.BULK_CREATIVE_INSERTIONS_EXPORT_PATH + File.separator;
    public static final String SCHEDULES_IMPORT_PATH =
            TempFileUtil.OS_TMP_PATH + File.separator + Constants.BULK_CREATIVE_INSERTIONS_IMPORT_PATH + File.separator;

    public static final String MEDIA_EXPORT_PATH =
            TempFileUtil.OS_TMP_PATH + File.separator + Constants.BULK_MEDIA_EXPORT_PATH + File.separator;
    public static final String MEDIA_IMPORT_PATH =
            TempFileUtil.OS_TMP_PATH + File.separator + Constants.BULK_MEDIA_IMPORT_PATH + File.separator;

    private static final String EXPORT_FILE_TIMESTAMP_FORMAT = "yyyyMMdd_hhmmss";
    private static final String ERROR_MESSAGE_CREATIVE_INSERTION_NOT_EXIST =
            ResourceBundleUtil.getString("creativeInsertion.error.creativeInsertionId");
    private static final String XLSX_TEMPLATES_PATH =
            Constants.TEMPLATES_PATH + "/" + AdminFile.FileType.XLSX.getFileType();
    private static final String XLSX_TEMPLATE_SCHEDULE_PATH =
            "/" + XLSX_TEMPLATES_PATH + "/" + "Schedule Edit Template.xlsx";
    public static final String XLSX_TEMPLATE_SCHEDULE_ERRORS_PATH =
            "/" + XLSX_TEMPLATES_PATH + "/" + "Schedule Edit Template - Error Export Version.xlsx";
    public static final String XLSX_TEMPLATE_MEDIA_PATH =
            "/" + XLSX_TEMPLATES_PATH + "/" + "media-template-export-version.xlsx";
    public static final String XLSX_TEMPLATE_MEDIA_ERRORS_PATH =
            "/" + XLSX_TEMPLATES_PATH + "/" + "media-template-error-export-version.xlsx";

    public static final String EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW = "CreativeInsertion";
    public static final String EXPORT_IMPORT_TYPE_MEDIA_VIEW = "Media";

    private InsertionOrderManager ioManager;
    private PublisherManager publisherManager;
    private SiteManager siteManager;
    private SiteSectionManager sectionManager;
    private SizeManager sizeManager;
    private PlacementManager placementManager;
    private PackageManager packageManager;
    private PackagePlacementManager packagePlacementManager;

    private CampaignDao campaignDao;
    private CreativeInsertionDao creativeInsertionDao;
    private CreativeGroupDao creativeGroupDao;
    private InsertionOrderDao insertionOrderDao;
    private PublisherDao publisherDao;
    private SiteDao siteDao;
    private SiteSectionDao sectionDao;
    private SizeDao sizeDao;
    private PlacementDao placementDao;
    private CostDetailDaoExtended placementCostDetailDao;
    private CostDetailDaoExtended packageCostDetailDao;

    private MediaBuyDao mediaBuyDao;
    private UserDao userDao;
    private PackagePlacementDaoBase dimPackagePlacementDao;
    private PackageDaoExtended packageDao;

    private UtilityWrapper utilityWrapper;

    private CreativeInsertionRawDataViewValidator creativeIsertionRawDataViewValidator;

    /**
     *
     * @param campaignDao
     * @param creativeInsertionDao
     * @param creativeGroupDao
     * @param insertionOrderDao
     * @param publisherDao
     * @param siteDao
     * @param sectionDao
     * @param accessControl
     * @param placementDao
     * @param placementCostDetailDao
     * @param packageCostDetailDao
     * @param mediaBuyDao
     * @param userDao
     * @param dimPackagePlacementDao
     * @param packageDao
     * @param sizeDao
     * @param ioManager
     * @param publisherManager
     * @param sectionManager
     * @param sizeManager
     * @param siteManager
     * @param packagePlacementManager
     * @param packageManager
     * @param placementManager
     */
    public ImportExportManager(CampaignDao campaignDao, CreativeInsertionDao creativeInsertionDao,
                               CreativeGroupDao creativeGroupDao,
                               InsertionOrderDao insertionOrderDao, PublisherDao publisherDao,
                               SiteDao siteDao, SiteSectionDao sectionDao, SizeDao sizeDao,
                               PlacementDao placementDao,
                               CostDetailDaoExtended placementCostDetailDao,
                               CostDetailDaoExtended packageCostDetailDao, MediaBuyDao mediaBuyDao,
                               UserDao userDao, PackagePlacementDaoBase dimPackagePlacementDao,
                               PackageDaoExtended packageDao,
                               InsertionOrderManager ioManager, PublisherManager publisherManager,
                               SiteManager siteManager, SiteSectionManager sectionManager,
                               SizeManager sizeManager, PlacementManager placementManager,
                               PackageManager packageManager,
                               PackagePlacementManager packagePlacementManager,
                               AccessControl accessControl) {
        super(accessControl);
        this.campaignDao = campaignDao;
        this.creativeInsertionDao = creativeInsertionDao;
        this.creativeGroupDao = creativeGroupDao;
        this.insertionOrderDao = insertionOrderDao;
        this.publisherDao = publisherDao;
        this.siteDao = siteDao;
        this.sectionDao = sectionDao;
        this.sizeDao = sizeDao;
        this.placementDao = placementDao;
        this.placementCostDetailDao = placementCostDetailDao;
        this.packageCostDetailDao = packageCostDetailDao;
        this.mediaBuyDao = mediaBuyDao;
        this.userDao = userDao;
        this.dimPackagePlacementDao = dimPackagePlacementDao;
        this.packageDao = packageDao;
        this.ioManager = ioManager;
        this.publisherManager = publisherManager;
        this.siteManager = siteManager;
        this.sectionManager = sectionManager;
        this.sizeManager = sizeManager;
        this.placementManager = placementManager;
        this.packageManager = packageManager;
        this.packagePlacementManager = packagePlacementManager;

        this.utilityWrapper = new UtilityWrapperImpl();
        this.creativeIsertionRawDataViewValidator = new CreativeInsertionRawDataViewValidator();
    }

    /**
     * Upload a excel file into a tmp path for a campaignId Note. This method
     * does not close the {@code InputStream} received. The developer must
     * ensure to properly close it
     *
     * @param campaignId
     * @param objectType
     * @param inputStream
     * @param contDisposition
     * @param key The {@code OauthKey} for the requester user
     * @return String
     */
    // TODO: We need to update this manager method --> EITHER
    public String uploadXLSXFileInTmp(Long campaignId, String objectType, InputStream inputStream, FormDataContentDisposition contDisposition, OauthKey key) {

        // null validations
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign id cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        if (objectType == null) {
            throw new IllegalArgumentException("ObjectType cannot be null");
        }
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        if (contDisposition == null) {
            throw new IllegalArgumentException("ContentDisposition cannot be null");
        }
        String filename = contDisposition.getFileName();
        if (StringUtils.isBlank(filename)) {
            throw BusinessValidationUtil.buildFileUploadSystemException(
                    FileUploadCode.FILENAME_REQUIRED, filename);
        }

        // Create SqlSession
        SqlSession session = campaignDao.openSession();
        String uuid = "";
        String pathFileName = "";
        try {

            //validate extension
            String fileExtension = AdminFile.getExtension(filename);
            if (!fileExtension.equals(AdminFile.FileType.XLSX.getFileType())) {
                throw BusinessValidationUtil.buildFileUploadSystemException(
                        FileUploadCode.NOT_ALLOWED_FILE_TYPE, filename, fileExtension);
            }

            // Check access control for campaign
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            }
            String tempPathExcelByCampaign = buildImportPath(campaignId, objectType);
            File folder = new File(tempPathExcelByCampaign);
            // Create create directory if needed
            if (!folder.exists()) {
                folder.mkdirs();
            }

            //set a filename with UUID
            uuid = UUID.randomUUID().toString();
            String filenameUuid = uuid + "." + fileExtension;
            //upload file
            AdminFile adminFile = new AdminFile();
            if (adminFile.saveFile(inputStream, tempPathExcelByCampaign, filenameUuid) == 0) {
                throw BusinessValidationUtil.buildFileUploadSystemException(
                        FileUploadCode.EMPTY_FILE, filename);
            }

            pathFileName = tempPathExcelByCampaign + filenameUuid;
            //validate size file
            Long lengthFile = utilityWrapper.getLength(tempPathExcelByCampaign, filenameUuid);
            Double lengthFileKb = ((double) lengthFile) / AdminFile.STD_BUFFER_IMAGE_SIZE;
            if (lengthFileKb > AdminFile.MAX_EXCEL_FILE_SIZE_KB) {
                throw BusinessValidationUtil.buildFileUploadSystemException(
                        FileUploadCode.MAX_FILE_SIZE, filename, AdminFile.MAX_EXCEL_FILE_SIZE_KB);
            }
            //validation to file is a valid excel and rows number validation
            File file = new File(pathFileName);
            AdminFile.FileCheckXLSXResult checkResult = utilityWrapper.validateFileXLSX(file);
            if (checkResult != AdminFile.FileCheckXLSXResult.VALID_FILE_TYPE) {
                throw BusinessValidationUtil.buildFileUploadSystemException(
                        FileUploadCode.NOT_ALLOWED_IMPORT_FILE_TYPE, filename);
            }
        } catch (IOException e) {
            if (!pathFileName.isEmpty()) {
                AdminFile.deleteFile(pathFileName);
            }
            // This groups whatever kind of IOException we are not supporting directly
            throw BusinessValidationUtil.buildFileUploadSystemException(
                    FileUploadCode.INTERNAL_ERROR, filename, e);
        } catch (SystemException e) {
            if (!pathFileName.isEmpty()) {
                AdminFile.deleteFile(pathFileName);
            }
            throw e;
        } finally {
            campaignDao.close(session);
        }
        log.info("File {} was uploaded successfully by {}", filename, key.getUserId());
        return uuid;
    }

    public Either<ImportExportErrors, SuccessResponse> importToUpdateSchedulesFromXLSXFile(Long campaignId, String uuid, Boolean ignoreErrors, OauthKey key) {
        // null validations
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign id cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (ignoreErrors == null) {
            throw new IllegalArgumentException("Ignore Errors cannot be null");
        }

        CreativeInsertionImportExportHelper scheduleHelper = new CreativeInsertionImportExportHelper();

        long millis = System.currentTimeMillis();
        log.info("MEASURE: Begin import process for {}", key.getUserId());
        // Create SqlSession
        SqlSession sessionAccessControl = creativeInsertionDao.openSession();
        SqlSession sessionUpdate = null;
        String filename = uuid + "." + AdminFile.FileType.XLSX.getFileType();
        int affectedRecords = 0;
        List<CreativeInsertionRawDataView> records = null;
        try {
            // Check access control for campaign
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), sessionAccessControl)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            }
            String tempPathExcelByCampaign = buildImportPath(campaignId, EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW);
            String tempPathObjFile = tempPathExcelByCampaign + uuid + ".obj";
            records = SerializationUtil.deserialize(tempPathObjFile);

            // 1. Check if there is a serialized file, if not use the XLS file
            if (records == null) {
                // 2. Call import utility to open the file and recover the List of records
                String pathFileName = tempPathExcelByCampaign + filename;
                try {
                    records = ExportImportUtil
                            .parseTo(CreativeInsertionRawDataView.class, new File(pathFileName),
                                    ExportImportUtil.VALIDATE_HEADERS);
                    if (records.size() >= ExportImportUtil.DEFAULT_MAX_NUM_ROWS) {
                        throw BusinessValidationUtil.buildFileUploadSystemException(
                                FileUploadCode.FILE_XLSX_MAX_ROWS, filename, ExportImportUtil.DEFAULT_MAX_NUM_ROWS);
                    }
                } catch (InvalidTemplateException e) {
                    throw BusinessValidationUtil.buildFileUploadSystemException(
                            FileUploadCode.FILE_INVALID_FORMAT, filename, e);
                }

                if (records != null && !records.isEmpty()) {
                    // 2.1. Cleanup possible Errors may come up during the reading
                    records = scheduleHelper.doCleanup(records);

                    // 3. Model validation Without ClickThroughs
                    records = importInsertionsDataModelValidatorWithoutClickThroughs(records);

                    // 4. Validation of Data Access Control
                    records = importInsertionsDataAccessValidator(campaignId, records, sessionAccessControl, key);

                    // 5. Model validation
                    records = importInsertionsDataModelClickThroughsValidator(records);

                    // 6. Persist temporal OBJ file to avoid future validations
                    millis = System.currentTimeMillis();
                    log.info("MEASURE: Serialization. Start measure for {} Creative Insertion", records.size());
                    SerializationUtil.serialize(records, tempPathObjFile);
                    millis = System.currentTimeMillis() - millis;
                    log.info("MEASURE: Serialization. Stop measure. Took {}", toSeconds(millis));
                }
            }

            // 7. Separate records to update and to be returned as error
            List<CreativeInsertionRawDataView> toUpdate = new ArrayList<>();
            List<CreativeInsertionRawDataView> withErrors = new ArrayList<>();
            for (CreativeInsertionRawDataView record : records) {
                if (StringUtils.isBlank(record.getReason())) {
                    toUpdate.add(record);
                } else {
                    withErrors.add(record);
                }
            }

            // 8. Save the records
            if ((!ignoreErrors && withErrors.isEmpty()) || ignoreErrors) {
                sessionUpdate = creativeInsertionDao.openSession(ExecutorType.BATCH);

                HashMap<Long, List<String>> mapCI = new HashMap<>();
                HashMap<Long, CreativeInsertionRawDataView> mapCG = new HashMap<>();
                millis = System.currentTimeMillis();
                log.info("MEASURE: Update of Creative Insertions. Start measure for {} Creative Insertion", records.size());
                for (CreativeInsertionRawDataView record : toUpdate) {
                    Long cgId = record.getCreativeGroupId();
                    if (record.isCiPropsChanged()) {
                        record.setModifiedTpwsKey(key.getTpws());
                        creativeInsertionDao.updateOnImport(record, sessionUpdate);
                        if (!mapCI.containsKey(cgId)) {
                            mapCI.put(cgId, new ArrayList<String>());
                        }
                        mapCI.get(cgId).add(record.getCreativeInsertionId());
                    }
                    mapCG.put(cgId, record);
                }
                millis = System.currentTimeMillis() - millis;
                log.info("MEASURE: Update of Creative Insertions. Stop measure. Took {}", toSeconds(millis));

                millis = System.currentTimeMillis();
                log.info("MEASURE: Update of Creative Groups. Start measure for {} Creative Insertion", records.size());
                List<Long> cgUpdated = new ArrayList<>();
                int countNonUpdatedByCG = 0;
                for (Map.Entry<Long, CreativeInsertionRawDataView> entry : mapCG.entrySet()) {
                    if (entry.getValue().isGroupPropsChanged()) {
                        entry.getValue().setModifiedTpwsKey(key.getTpws());
                        creativeGroupDao.updateOnImport(entry.getValue(), sessionUpdate);
                        cgUpdated.add(entry.getValue().getCreativeGroupId());
                    } else {
                        countNonUpdatedByCG += mapCI.containsKey(entry.getKey()) ? mapCI.get(entry.getKey()).size() : 0;
                    }
                }
                millis = System.currentTimeMillis() - millis;
                log.info("MEASURE: Update of Creative Groups. Stop measure. Took {}", toSeconds(millis));

                millis = System.currentTimeMillis();
                log.info("MEASURE: Checking how many Creative Insertions were updated. Start measure for {} Creative Insertion", records.size());
                int startIndex = SearchCriteria.SEARCH_CRITERIA_START_INDEX;
                int pageSize = SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE;
                int numPages = (cgUpdated.size() / pageSize) + (cgUpdated.size() % pageSize > 0 ? 1 : 0);
                while (numPages > 0 && numPages - 1 > 0) {
                    affectedRecords += creativeInsertionDao.getCountAffectedSchedulesDueToImport(
                            cgUpdated.subList(startIndex, startIndex + pageSize), campaignId, sessionUpdate);
                    startIndex += pageSize;
                    numPages--;
                }
                if (numPages - 1 == 0) {
                    affectedRecords += creativeInsertionDao.getCountAffectedSchedulesDueToImport(
                            cgUpdated.subList(startIndex, startIndex + (cgUpdated.size() % pageSize)), campaignId, sessionUpdate);
                }
                affectedRecords += countNonUpdatedByCG;
                millis = System.currentTimeMillis() - millis;
                log.info("MEASURE: Checking how many Creative Insertions were updated. Stop measure. Took {}", toSeconds(millis));

                creativeInsertionDao.commit(sessionUpdate);
            } else {
                // 9. Return the Errors
                ImportExportErrors errors = new ImportExportErrors();
                errors.setTotal(records.size());
                errors.setInvalidCount(withErrors.size());
                errors.setValidCount(toUpdate.size());
                for (CreativeInsertionRawDataView record : withErrors) {
                    ValidationError error = new ValidationError();
                    error.setMessage(record.getReason());
                    error.setCode(ValidationCode.INVALID);
                    error.setField(record.getRowError());
                    errors.addError(error);
                }
                return Either.error(errors);
            }
        } catch (SystemException e) {
            creativeInsertionDao.rollback(sessionAccessControl);
            if (sessionUpdate != null) {
                creativeInsertionDao.rollback(sessionUpdate);
            }
            throw e;
        } catch (Exception e) {
            creativeInsertionDao.rollback(sessionAccessControl);
            if (sessionUpdate != null) {
                creativeInsertionDao.rollback(sessionUpdate);
            }
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, "");
        } finally {
            creativeInsertionDao.close(sessionAccessControl);
            if (sessionUpdate != null) {
                creativeInsertionDao.close(sessionUpdate);
            }
            if (records != null) {
                records.clear();
            }
        }
        millis = System.currentTimeMillis() - millis;
        log.info("MEASURE: End import process File {} for {}. Took {} for {} records", filename, key.getUserId(), toSeconds(millis), affectedRecords);
        return Either.success(new SuccessResponse(
                affectedRecords + " scheduling insertions were successfully updated!"));
    }

    public File exportIssuesOfImportByCampaign(Long campaignId, String objectType,
                                               String formatType, String uuid, OauthKey key) {
        //null validations
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign id cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        if (objectType == null) {
            throw new IllegalArgumentException("ObjectType cannot be null");
        }
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (formatType == null) {
            formatType = AdminFile.FileType.XLSX.getFileType();
        }

        Exporter exporter = null;
        switch(objectType) {
            case ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW:
                exporter = new IssuesExporter(campaignDao, campaignId, uuid, key,
                        objectType, ImportExportManager.XLSX_TEMPLATE_SCHEDULE_ERRORS_PATH,
                        CreativeInsertionRawErrorDataView.class, accessControl);
                break;
            case ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW:
                exporter = new IssuesExporter(campaignDao, campaignId, uuid, key, objectType,
                        ImportExportManager.XLSX_TEMPLATE_MEDIA_ERRORS_PATH,
                        MediaRawErrorDataView.class, accessControl) {

                    @Override
                    public List<? extends XlsErrorTemplateDescriptor> getData(
                            String tempPathObjFile) {
                        List<MediaRawDataView> records = SerializationUtil.deserialize(tempPathObjFile);
                        List<MediaRawDataView> result = new ArrayList<>();
                        for (MediaRawDataView view : records) {
                            List<MediaRawDataView> rowResult = new ArrayList<>();
                            boolean isTheFirstRow = true;
                            boolean hasErrors = view.getIssues().size() > 0;
                            MediaRawDataView record = view;
                            for (CostDetailRawDataView cost : view.getCostDetails()) {
                                if (!isTheFirstRow) {
                                    record = new MediaRawDataView();
                                }
                                copyCosts(cost, record);
                                rowResult.add(record);
                                isTheFirstRow = false;
                                hasErrors = hasErrors || cost.getIssues().size() > 0;
                            }
                            if(hasErrors) {
                                result.addAll(rowResult);
                            }
                        }
                        return result;
                    }

                    private void copyCosts(CostDetailRawDataView src, MediaRawDataView dst) {
                        dst.setPlannedAdSpend(src.getPlannedAdSpend());
                        dst.setInventory(src.getInventory());
                        dst.setRate(src.getRate());
                        dst.setRateType(src.getRateType());
                        dst.setStartDate(src.getStartDate());
                        dst.setEndDate(src.getEndDate());
                        dst.setRowError(src.getRowNumber());
                        dst.getFieldsWithFormulaError().addAll(src.getFieldsWithFormulaError());
                        dst.getIssues().putAll(src.getIssues());
                    }
                };
                break;
        }
        return exporter.export();
    }

    /**
     * Update a campaign. Checks are performed to ensure that the user has
     * access to update the campaign with the selections they have chosen.
     *
     * @param campaignId Campaign ID
     * @param objectType
     * @param formatType
     * @param key authorization key of the user updating the Campaign.
     * @return the updated Campaign.
     */
    public File exportByCampaign(Long campaignId, String objectType, String formatType,
                                 OauthKey key) {
        //null validations
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }
        if (formatType == null) {
            formatType = AdminFile.FileType.XLSX.getFileType();
        }

        Exporter exporter = null;
        switch(objectType) {
            case ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW:
                exporter =
                        new CreativeInsertionExporter(campaignDao, creativeInsertionDao, campaignId,
                                key, objectType, XLSX_TEMPLATE_SCHEDULE_PATH,
                                CreativeInsertionRawDataView.class, accessControl);
                break;
            case ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW:
                exporter = new MediaExporter(campaignDao, placementDao, placementCostDetailDao,
                        packageCostDetailDao, campaignId, key, objectType, XLSX_TEMPLATE_MEDIA_PATH,
                        MediaRawDataView.class, accessControl);
                break;
        }
        return exporter.export();
    }

    public Either<ImportExportErrors, SuccessResponse> importToCreatePlacementsFromXLSXFile(
            Long campaignId, String uuid, Boolean ignoreErrors, List<Action> actions, OauthKey key) {
        // null validations
        if (campaignId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Campaign id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        ImportExportErrors errors = new ImportExportErrors();
        if (uuid == null) {
            errors.addError(new Error(ResourceBundleUtil.getString("global.error.null", "UUID"),
                    ValidationCode.INVALID));
            return Either.error(errors);
        }
        if (ignoreErrors == null) {
            errors.addError(
                    new Error(ResourceBundleUtil.getString("global.error.null", "Ignore Errors"),
                            ValidationCode.INVALID));
            return Either.error(errors);
        }

        long millis = System.currentTimeMillis();
        log.info("MEASURE: Begin import Media process for {}", key.getUserId());

        MediaImportExportHelper mediaHelper =
                new MediaImportExportHelper(insertionOrderDao, publisherDao, placementDao, siteDao, sectionDao,
                        sizeDao, packageDao, ioManager, publisherManager, siteManager, sectionManager,
                        sizeManager, placementManager, packageManager, packagePlacementManager, key);

        // Create SqlSession
        SqlSession sessionAccessControl = campaignDao.openSession();
        SqlSession sessionBulk = null;
        SqlSession sessionDim = null;
        String filename = uuid + "." + AdminFile.FileType.XLSX.getFileType();
        int affectedRecords = 0;
        List<MediaRawDataView> records = null;
        try {
            // Check access control for campaign
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId),
                    key.getUserId(), sessionAccessControl)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // 1. Obtain the temporal path of xlsx file
            String tempPathExcelByCampaign = buildImportPath(campaignId, EXPORT_IMPORT_TYPE_MEDIA_VIEW);
            String tempPathObjFile = tempPathExcelByCampaign + uuid + ".obj";
            records = SerializationUtil.deserialize(tempPathObjFile);

            // 2. Check if there is a serialized file, if not use the XLS file
            if (records == null) {
                String pathFileName = tempPathExcelByCampaign + filename;
                try {
                    // 3. Call import utility to open the file and recover the List of records
                    records = ExportImportUtil.parseTo(MediaRawDataView.class,
                            new File(pathFileName), ExportImportUtil.VALIDATE_HEADERS);
                    if (records.size() > ExportImportUtil.MEDIA_MAX_NUM_ROWS) {
                        BusinessError error = new BusinessError(ResourceBundleUtil.getString(
                                "FileUploadCode.FILE_XLSX_MAX_ROWS",
                                ExportImportUtil.MEDIA_MAX_NUM_ROWS),
                                FileUploadCode.FILE_XLSX_MAX_ROWS, filename);
                        errors.addError(error);
                        return Either.error(errors);
                    }
                } catch (InvalidTemplateException e) {
                    BusinessError error = new BusinessError(e.getMessage(),
                            FileUploadCode.FILE_INVALID_FORMAT, filename);
                    errors.addError(error);
                    return Either.error(errors);
                }

                if (records != null && !records.isEmpty()) {
                    // 3.1. Cleanup possible Errors may come up during the reading
                    millis = System.currentTimeMillis();
                    log.info("MEASURE: Cleanup. Start measure for {} Media", records.size());
                    records = mediaHelper.doCleanup(records);
                    millis = System.currentTimeMillis() - millis;
                    log.info("MEASURE: Cleanup. Stop measure. Took {}", toSeconds(millis));

                    // 4. Unflatten media
                    millis = System.currentTimeMillis();
                    log.info("MEASURE: Unflatten. Start measure for {} Media", records.size());
                    records = mediaHelper.doUnflatten(records);
                    millis = System.currentTimeMillis() - millis;
                    log.info("MEASURE: Unflatten. Stop measure. Took {}", toSeconds(millis));

                    // 5. Model Validation
                    millis = System.currentTimeMillis();
                    log.info("MEASURE: Model Validation. Start measure for {} Media", records.size());
                    Map<PackageMapId, List<MediaRawDataView>> packageMap = mediaHelper.doPackageMap(records);
                    records = mediaHelper.doModelValidation(records, packageMap);
                    millis = System.currentTimeMillis() - millis;
                    log.info("MEASURE: Model Validation. Stop measure. Took {}", toSeconds(millis));

                    // 6. Validation of Data against DB
                    // 6.1 Validation Package CostDetails
                    millis = System.currentTimeMillis();
                    log.info("MEASURE: Data PackageCostDetails Validation. Start measure for {} Media", records.size());
                    mediaHelper.doPackageDataValidation(campaignId, packageMap, key.getUserId(), sessionAccessControl);
                    millis = System.currentTimeMillis() - millis;
                    log.info("MEASURE: Data PackageCostDetails Validation. Stop measure. Took {}", toSeconds(millis));

                    // 6.2. Access Control and get Ids from existent data
                    millis = System.currentTimeMillis();
                    log.info("MEASURE: Data Validation. Start measure for {} Media", records.size());
                    records = mediaHelper.doPlacementDataValidation(campaignId, records,
                            sessionAccessControl, key.getUserId());
                    millis = System.currentTimeMillis() - millis;
                    log.info("MEASURE: Data Validation. Stop measure. Took {}", toSeconds(millis));
                    
                    // 7. Validation of Placement duplication based on Site, Section and Size.
                    millis = System.currentTimeMillis();
                    log.info("MEASURE: Duplicated Validation. Start measure for {} Media", records.size());
                    records = mediaHelper.doDuplicatedValidation(campaignId, records,
                            sessionAccessControl, key.getUserId());
                    millis = System.currentTimeMillis() - millis;
                    log.info("MEASURE: Duplicated Validation. Stop measure. Took {}", toSeconds(millis));

                    // 8. Persist temporal OBJ file to avoid future validations
                    SerializationUtil.serialize(records, tempPathObjFile);
                }
            }

            // 9. Clean all Actions
            if(actions != null && !actions.isEmpty()){
                records = mediaHelper.doInAppCorrections(records, actions);
            }

            // 10. Separate records to create and to be returned as error
            List<MediaRawDataView> toCreate = new ArrayList<>();
            List<MediaRawDataView> withErrors = new ArrayList<>();
            for (MediaRawDataView record : records) {
                if (record.getIssues().isEmpty() && !record.costDetailsHaveIssues()) {
                    toCreate.add(record);
                } else if(!record.getWarnings().isEmpty() || record.costDetailsHaveWarnings() ) {
                    if(record.getErrors().isEmpty() ) {
                    // The placement will be able to be created only if there nor errors
                        toCreate.add(record);
                    }
                    withErrors.add(record);
                } else {
                    withErrors.add(record);
                }
            }

            // 11. Create new data
            if ((!ignoreErrors && withErrors.isEmpty()) || ignoreErrors) {
                if(!toCreate.isEmpty()) {
                    // 12. Common elements from DB
                    MediaBuy mediaBuy = mediaBuyDao.getByCampaign(campaignId, sessionAccessControl);
                    User user = userDao.get(key.getUserId(), sessionAccessControl);

                    // 13. Persist all the records
                    millis = System.currentTimeMillis();
                    log.info("MEASURE: Persistence. Start measure for {} Media", records.size());
                    sessionBulk = placementDao.openSession(ExecutorType.BATCH);
                    sessionDim = dimPackagePlacementDao.openSession();
                    affectedRecords = mediaHelper.saveMediaHierarchy(campaignId, toCreate, user,
                            mediaBuy, sessionBulk, sessionDim);
                    placementDao.commit(sessionBulk);
                    dimPackagePlacementDao.commit(sessionDim);
                    millis = System.currentTimeMillis() - millis;
                    log.info("MEASURE: Persistence. Stop measure. Took {}", toSeconds(millis));
                }
            } else {
                // 14. Return the Errors
                errors.setTotal(records.size());
                errors.setInvalidCount(withErrors.size());
                errors.setValidCount(toCreate.size());
                ImportExportRowError error;
                for (MediaRawDataView record : withErrors) {
                    Iterator<CostDetailRawDataView> iterator = record.getCostDetails().iterator();
                    if (iterator.hasNext()) {
                        record.getIssues().putAll(iterator.next().getIssues());
                    }

                    List<String> errorsList = record.getErrors();
                    Collections.sort(errorsList);
                    if(!errorsList.isEmpty()) {
                        error = new ImportExportRowError(StringUtils.join(errorsList, ", "),
                                ValidationCode.INVALID);
                        error.setRownum(record.getRowError());
                        error.setType(ImportIssueType.ERROR);
                        errors.addError(error);
                    }
                    List<String> warningsList = record.getWarnings();
                    Collections.sort(warningsList);
                    if(!warningsList.isEmpty()) {
                        error = new ImportExportRowError(
                                StringUtils.join(warningsList, ", "),
                                ValidationCode.INVALID);
                        error.setRownum(record.getRowError());
                        error.setType(ImportIssueType.WARNING);
                        errors.addError(error);
                    }
                    if(!record.getActions().isEmpty()) {
                        for(ImportExportCellError cellError: record.getActions()){
                            error = new ImportExportRowError(
                                cellError.getMessage(),
                                ValidationCode.INVALID);
                            error.setRownum(record.getRowError());
                            error.setType(ImportIssueType.WARNING);
                            error.setInAppType(cellError.getInAppType());
                            error.setOptions(cellError.getOptions());
                            error.setDefaultOption(cellError.getDefaultOption());
                            errors.addError(error);
                        }
                    }
                    // Fetch all issues from Cost Details from 2nd position onwards
                    while(iterator.hasNext()) {
                        CostDetailRawDataView costDetail = iterator.next();
                        errorsList = costDetail.getErrors();
                        Collections.sort(errorsList);
                        if(!errorsList.isEmpty()) {
                            error = new ImportExportRowError(StringUtils.join(
                                    errorsList, ", "),
                                    ValidationCode.INVALID);
                            error.setRownum(costDetail.getRowNumber());
                            error.setType(ImportIssueType.ERROR);
                            errors.addError(error);
                        }
                        warningsList = costDetail.getWarnings();
                        Collections.sort(warningsList);
                        if(!warningsList.isEmpty()) {
                            error = new ImportExportRowError(
                                    StringUtils.join(warningsList, ", "),
                                    ValidationCode.INVALID);
                            error.setRownum(costDetail.getRowNumber());
                            error.setType(ImportIssueType.WARNING);
                            errors.addError(error);
                        }
                        List<ImportExportCellError> costActions = costDetail.getActions();
                        if(! costActions.isEmpty()) {
                            for(ImportExportCellError cellError:  costActions){
                                error = new ImportExportRowError(
                                        cellError.getMessage(),
                                        ValidationCode.INVALID);
                                error.setRownum(costDetail.getRowNumber());
                                error.setType(ImportIssueType.WARNING);
                                error.setInAppType(cellError.getInAppType());
                                error.setOptions(cellError.getOptions());
                                error.setDefaultOption(cellError.getDefaultOption());
                                errors.addError(error);
                            }
                        }
                    }
                }
                return Either.error(errors);
            }
        } catch (SystemException e) {
            campaignDao.rollback(sessionAccessControl);
            if (sessionBulk != null) {
                placementDao.rollback(sessionBulk);
            }
            if (sessionDim != null) {
                dimPackagePlacementDao.rollback(sessionDim);
            }
            throw e;
        } catch (Exception e) {
            campaignDao.rollback(sessionAccessControl);
            if (sessionBulk != null) {
                placementDao.rollback(sessionBulk);
            }
            if (sessionDim != null) {
                dimPackagePlacementDao.rollback(sessionDim);
            }
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, "");
        } finally {
            campaignDao.close(sessionAccessControl);
            if (sessionBulk != null) {
                placementDao.close(sessionBulk);
            }
            if (sessionDim != null) {
                dimPackagePlacementDao.close(sessionDim);
            }
            if (records != null) {
                records.clear();
            }
        }
        millis = System.currentTimeMillis() - millis;
        log.info("MEASURE: End import process File {} for {}. Took {} for {} records", filename, key.getUserId(), toSeconds(millis), affectedRecords);
        return Either.success(new SuccessResponse(ResourceBundleUtil.getString("packagePlacementImport.info.success", affectedRecords)));
    }

    /**
     * Model validations for data
     *
     * @param listCImport
     * @return a Recordset of CreativeInsertionRawDataView
     */
    private List<CreativeInsertionRawDataView> importInsertionsDataModelValidatorWithoutClickThroughs(List<CreativeInsertionRawDataView> listCImport) {
        long millis = System.currentTimeMillis();
        log.info("MEASURE: Model Validation. Start measure for {} Creative Insertions", listCImport.size());
        List<CreativeInsertionRawDataView> result = new ArrayList<>();
        LinkedHashMap<String, CreativeInsertionRawDataView> duplicated = new LinkedHashMap<>();
        int nullCounter = 0;
        for (CreativeInsertionRawDataView cInsertImport : listCImport) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(cInsertImport, "creativeInsertionImportView");
            ValidationUtils.invokeValidator(creativeIsertionRawDataViewValidator, cInsertImport, errors);
            String errorMessage = ApiValidationUtils.asCSVString(errors);
            cInsertImport.setReason(errorMessage);
            String scheduleId = cInsertImport.getCreativeInsertionId() == null ? "null" + ++nullCounter : cInsertImport.getCreativeInsertionId();
            if (duplicated.containsKey(scheduleId)) {
                duplicated.remove(scheduleId);
            }
            duplicated.put(scheduleId, cInsertImport);
        }
        result.addAll(duplicated.values());
        duplicated.clear();
        listCImport.clear();
        millis = System.currentTimeMillis() - millis;
        log.info("MEASURE: Model Validation. Stop measure. Took {}", toSeconds(millis));
        return result;
    }

    /**
     * Model validations, only the click throughs
     *
     * @param listCImport
     * @return a Recordset of CreativeInsertionRawDataView
     */
    private List<CreativeInsertionRawDataView> importInsertionsDataModelClickThroughsValidator(List<CreativeInsertionRawDataView> listCImport) {
        long millis = System.currentTimeMillis();
        log.info(
                "MEASURE: Model Validation for Click Throughs. Start measure for {} Creative Insertions",
                listCImport.size());
        for (CreativeInsertionRawDataView cInsertImport : listCImport) {
            long millisForCT = System.currentTimeMillis();
            log.info("MEASURE: Model Validation for CT. Start measure for {} Creative Insertion Id",
                    cInsertImport.getCreativeInsertionId());
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(cInsertImport, "creativeInsertionImportView");
            creativeIsertionRawDataViewValidator.validateClickthrough(cInsertImport, errors);
            String errorMessage = (cInsertImport.getReason().length() <= 0 ? "" : cInsertImport.getReason()) + ApiValidationUtils.asCSVString(errors);
            cInsertImport.setReason(errorMessage);
            millisForCT = System.currentTimeMillis() - millisForCT;
            log.info("MEASURE: Model Validation for CT. Stop measure. Took {} ms", millisForCT);
        }
        millis = System.currentTimeMillis() - millis;
        log.info("MEASURE: Model Validation for Click Throughs. Stop measure. Took {}", toSeconds(millis));
        return listCImport;
    }

    /**
     * Check Data Access for CreativeInsertion IDs
     *
     * @param insertionsToValidate Creative Insertions to validate
     * @param key The {@code OauthKey} for the requester user
     * @return List CreativeInsertionRawDataView
     */
    private List<CreativeInsertionRawDataView> importInsertionsDataAccessValidator(Long campaignId,
            List<CreativeInsertionRawDataView> insertionsToValidate, SqlSession session, OauthKey key) {

        int rowsProcessed = 0;

        do {
            // 1. get 1000 creativeInsertionIds to validate
            // 2. set this ids and CreativeInsertionRawDataView object into a mapIds Map.
            // 3. validate IDs and obtain groupIds to update
            // 4. set the groupId for a valid insertionID
            // 5. Obtain the list of invalid Ids
            // 6. Update the error message for the list from step 5 

            Map<Long, List<CreativeInsertionRawDataView>> mapIds = new HashMap<>();

            // 1.
            for (int i = rowsProcessed; (i < insertionsToValidate.size() && i < (rowsProcessed + Constants.MAX_NUMBER_VALUES_IN_CLAUSE)); i++) {
                String creativeInsertionId = insertionsToValidate.get(i).getCreativeInsertionId();
                if (creativeInsertionId != null && !creativeInsertionId.isEmpty()) {
                    Long insertionId;
                    try {
                        insertionId = new Long(creativeInsertionId);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    //2.
                    List<CreativeInsertionRawDataView> positions = mapIds.get(insertionId);
                    if (positions == null) {
                        positions = new ArrayList<>();
                    }
                    positions.add(insertionsToValidate.get(i));
                    mapIds.put(insertionId, positions);
                }
            }
            // Ids to validate
            Set<Long> setIds = new HashSet<>(mapIds.keySet());

            if (setIds.size() > 0) {
                //3.
                List<CreativeInsertionRawDataView> validInsertionIds = null;
                try {
                    validInsertionIds = creativeInsertionDao.getCreativeInsertionsByUserId(setIds, key.getUserId(), campaignId, session);
                } catch (Exception e) {
                    throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INTERNAL_ERROR, "CreativeInsertionId");
                }

                // 4.
                List<Long> validIds = new ArrayList<>();
                for (CreativeInsertionRawDataView validInsertion : validInsertionIds) {
                    Long insertionId = new Long(validInsertion.getCreativeInsertionId());
                    List<CreativeInsertionRawDataView> positions = mapIds.get(insertionId);
                    for (CreativeInsertionRawDataView position : positions) {
                        // If equals, it means the row doesn't need to get updated
                        boolean ciPropsChanged = !position.creativeInsertionPropsEqual(validInsertion);
                        position.setCiPropsChanged(ciPropsChanged);
                        boolean groupPropsChanged = !position.groupPropsEqual(validInsertion);
                        position.setGroupPropsChanged(groupPropsChanged);
                        position.setCreativeGroupId(validInsertion.getCreativeGroupId());
                        position.setCreativeType(validInsertion.getCreativeType());
                    }
                    validIds.add(insertionId);
                }

                // 5.
                List<Long> invalidInsertionIds = new ArrayList<>();
                invalidInsertionIds.addAll(setIds);
                invalidInsertionIds.removeAll(validIds);
                // 6.
                if (!invalidInsertionIds.isEmpty()) {
                    for (Long invalidId : invalidInsertionIds) {
                        List<CreativeInsertionRawDataView> positions = mapIds.get(invalidId);
                        for (CreativeInsertionRawDataView position : positions) {
                            String error = position.getReason();
                            error = (error == null || error.isEmpty() ? ERROR_MESSAGE_CREATIVE_INSERTION_NOT_EXIST
                                    : error + ", " + ERROR_MESSAGE_CREATIVE_INSERTION_NOT_EXIST);
                            position.setReason(error);
                        }
                    }
                }
            }
            rowsProcessed = Constants.MAX_NUMBER_VALUES_IN_CLAUSE + rowsProcessed;
        } while (rowsProcessed < insertionsToValidate.size());

        return insertionsToValidate;
    }

    /**
     * Method to make this class testable by seeting a custom implementation of a {@code UtilityWrapper}
     * @param utilityWrapper The custom {@code UtilityWrapper} to set
     */
    public void setUtilityWrapper(UtilityWrapper utilityWrapper) {
        this.utilityWrapper = utilityWrapper;
    }

    /**
     * Interface to make this class testable
     *
     */
    interface UtilityWrapper {

        Long getLength(String pathFile, String fileName);

        AdminFile.FileCheckXLSXResult validateFileXLSX(File file);
    }

    class UtilityWrapperImpl implements UtilityWrapper {

        @Override
        public Long getLength(String pathFile, String fileName) {
            return AdminFile.getLength(pathFile, fileName);
        }

        @Override
        public AdminFile.FileCheckXLSXResult validateFileXLSX(File file) {
            return AdminFile.validateFileXLSX(file);
        }
    }

    /**
     * Builds the temporary path of an xlsx import file given a
     * {@code campaignId}
     *
     * @param campaignId The Campaign Id to use in the path
     * @param objectType
     * @return The Path where the temporary CreativeInsertion excel file has
     * been imported to
     */
    public static String buildImportPath(Long campaignId, String objectType) {
        String path = "";
        switch (objectType) {
            case EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW:
                path = SCHEDULES_IMPORT_PATH;
                break;
            case EXPORT_IMPORT_TYPE_MEDIA_VIEW:
                path = MEDIA_IMPORT_PATH;
                break;
        }
        return path + campaignId + File.separator;
    }

    /**
     * Builds the temporary path of an xlsx export file given a
     * {@code campaignId}
     *
     * @param campaignId The Campaign Id to use in the path
     * @return The Path where the temporary CreativeInsertion excel file has
     * been exported to
     */
    public static String buildExportPath(Long campaignId, String objectType) {
        String path = "";
        switch (objectType) {
            case EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW:
                path = SCHEDULE_EXPORT_PATH;
                break;
            case EXPORT_IMPORT_TYPE_MEDIA_VIEW:
                path = MEDIA_EXPORT_PATH;
                break;
        }

        return path + campaignId + File.separator;
    }

    private static String toSeconds(long millis) {
        return (millis / 1000) + " sec";
    }

    public static String getTempFilename(String campaignName, Long campaignId) {
        String name = campaignName.replaceAll(ValidationConstants.REGEXP_FILENAME_UNALLOWED_CHARACTERS, "") + " (" + campaignId + ") "
                + DateFormatUtils.format(new Date(), EXPORT_FILE_TIMESTAMP_FORMAT);
        return name;
    }
}

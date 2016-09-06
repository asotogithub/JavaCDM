package trueffect.truconnect.api.crud.service;

import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.business.ImportExportErrors;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.MediaBuyDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoBase;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.dao.SiteDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackagePlacementDaoImpl;

import com.sun.jersey.core.header.FormDataContentDisposition;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import org.junit.Ignore;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@code CreativeInsertionManager}
 *
 * @author Marcelo Heredia
 */
public class ImportExportManagerTest extends AbstractManagerTest {

    private ImportExportManager importExportManager;

    private CampaignDao campaignDao;
    private CreativeInsertionDao creativeInsertionDao;
    private CreativeGroupDao creativeGroupDao;
    private InsertionOrderDao insertionOrderDao;

    private ImportExportManager.UtilityWrapper utilityWrapperMock;

    private InputStream inp;
    private Campaign campaign;
    private String filePathTestImportFile;
    private Long fileSize;
    private SqlSession sessionBatch;
    private String objectTypeToImport;
    private String objectTypeToExport;
    private Map<String, CreativeInsertionRawDataView> insertionsSourceDataExportMap;

    private String exportFilePath;
    private Long campaignIdTestImportFile;
    private String uuidTestUploadFile;

    private static final String FILENAME = "Schedule Edit Template.xlsx";
    private static final String FILE_PATH = "/template/xlsx/" + FILENAME;

    private static final int ALL_INSERTIONS_UNIQUE_ACCEPTED = 0;
    private static final int INSERTIONS_DUPLICATED_ACCEPTED = 1;
    private static final int INSERTIONS_UNIQUE_REJECTED = 2;
    private static final int INSERTIONS_DUPLICATED_REJECTED = 3;

    private static enum TypeImportDataToTest {

        INSERTIONS_UNIQUE_GROUP_UNIQUE,
        INSERTIONS_UNIQUE_GROUP_SHARED,
        INSERTIONS_DUPLICATED_GROUP_UNIQUE,
        INSERTIONS_DUPLICATED_GROUP_SHARED
    };

    private static enum TypeChangesOnImportDataToTest {

        DATA_WITH_NO_CHANGES,
        DATA_WITH_INSERTION_CHANGES,
        DATA_WITH_GROUP_CHANGES,
        DATA_WITH_BOTH_CHANGES
    };

    private static enum TypeAccessOnImportDataToTest {

        ALL_INSERTIONS_ACCEPTED,
        INSERTION_REJECTED
    };

    @Before
    public void setupImportFiles() {
        //mock Daos
        campaignDao = mock(CampaignDao.class);
        creativeInsertionDao = mock(CreativeInsertionDao.class);
        creativeGroupDao = mock(CreativeGroupDao.class);
        insertionOrderDao = mock(InsertionOrderDao.class);

        sessionBatch = mock(SqlSession.class);

        PublisherDao publisherDao = mock(PublisherDao.class);
        SiteDao siteDao = mock(SiteDao.class);
        SiteSectionDao sectionDao = mock(SiteSectionDao.class);
        SizeDao sizeDao = mock(SizeDao.class);
        PlacementDao placementDao = mock(PlacementDao.class);
        PlacementStatusDao placementStatusDao = mock(PlacementStatusDao.class);
        InsertionOrderStatusDao insertionOrderStatusDao = mock(InsertionOrderStatusDao.class);
        ExtendedPropertiesDao extendedPropertiesDao = mock(ExtendedPropertiesDao.class);
        CostDetailDaoExtended placementCostDetailDao = mock(CostDetailDaoExtended.class); //PLACEMENT
        CostDetailDaoExtended packageCostDetailDao = mock(CostDetailDaoExtended.class); //PACKAGE
        PackageDaoExtended packageDao = mock(PackageDaoExtended.class);
        PackagePlacementDaoExtended packagePlacementDao = mock(PackagePlacementDaoExtended.class);
        MediaBuyDao mediaBuyDao = mock(MediaBuyDao.class);
        UserDao userDao = mock(UserDao.class);
        CostDetailDaoBase dimPackageCostDetailDao = mock(DimCostDetailDaoImpl.class); //CostDetailType.PACKAGE
        CostDetailDaoBase dimPlacementCostDetailDao = mock(CostDetailDaoBase.class); //CostDetailType.PLACEMENT
        PackageDaoBase dimPackageDao = mock(PackageDaoBase.class);
        PackagePlacementDaoBase dimPackagePlacementDao = mock(DimPackagePlacementDaoImpl.class);

        InsertionOrderManager ioManager =
                new InsertionOrderManager(insertionOrderDao, insertionOrderStatusDao, userDao,
                        placementDao, placementStatusDao, creativeInsertionDao, accessControlMockito);
        PublisherManager publisherManager =
                new PublisherManager(publisherDao, accessControlMockito);
        SiteManager siteManager =
                new SiteManager(siteDao, extendedPropertiesDao, accessControlMockito);
        SiteSectionManager sectionManager =
                new SiteSectionManager(sectionDao, accessControlMockito);
        SizeManager sizeManager = new SizeManager(sizeDao, accessControlMockito);
        PlacementManager placementManager =
                new PlacementManager(placementDao, placementCostDetailDao, campaignDao, sectionDao, sizeDao,
                        placementStatusDao, userDao, extendedPropertiesDao, insertionOrderDao,
                        insertionOrderStatusDao, packageDao, packagePlacementDao,
                        dimPackageCostDetailDao,
                        creativeInsertionDao,
                        accessControlMockito);
        PackageManager packageManager =
                new PackageManager(packageDao, packageCostDetailDao, placementDao,
                        placementCostDetailDao, packagePlacementDao, dimPlacementCostDetailDao,
                        dimPackageDao, dimPackageCostDetailDao, dimPackagePlacementDao,
                        insertionOrderDao, accessControlMockito);

        PackagePlacementManager packagePlacementManager =
                new PackagePlacementManager(placementDao, placementCostDetailDao, campaignDao,
                        sectionDao, sizeDao, placementStatusDao, userDao, extendedPropertiesDao,
                        insertionOrderDao, insertionOrderStatusDao, packageDao,
                        packageCostDetailDao, packagePlacementDao, dimPackageCostDetailDao,
                        dimPlacementCostDetailDao, dimPackageDao, dimPackagePlacementDao,
                        creativeInsertionDao,
                        accessControlMockito);
        importExportManager =
                new ImportExportManager(campaignDao, creativeInsertionDao, creativeGroupDao,
                        insertionOrderDao, publisherDao, siteDao, sectionDao, sizeDao, placementDao,
                        placementCostDetailDao, packageCostDetailDao, mediaBuyDao, userDao,
                        dimPackagePlacementDao, packageDao, ioManager, publisherManager, siteManager,
                        sectionManager, sizeManager, placementManager, packageManager,
                        packagePlacementManager, accessControlMockito);
        utilityWrapperMock = mock(ImportExportManager.UtilityWrapper.class);
        importExportManager.setUtilityWrapper(utilityWrapperMock);

        //variables
        campaign = EntityFactory.createCampaign();

        inp = getClass().getResourceAsStream(FILE_PATH);
        objectTypeToImport = null;
        filePathTestImportFile = null;
        insertionsSourceDataExportMap = null;
        fileSize = 1000L;

        objectTypeToExport = null;
        exportFilePath = null;
        campaignIdTestImportFile = null;
        uuidTestUploadFile = null;

        // Mock Session
        when(campaignDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(campaignDao).close(sqlSessionMock);

        when(creativeInsertionDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(creativeInsertionDao).close(sqlSessionMock);

        // Mocks updateSession
        when(creativeInsertionDao.openSession(any(ExecutorType.class))).thenReturn(sessionBatch);
        doNothing().when(creativeInsertionDao).close(sessionBatch);

        // Mocks common behavior
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, sqlSessionMock);

        when(utilityWrapperMock.getLength(anyString(), anyString())).
                thenReturn(fileSize);
        when(utilityWrapperMock.validateFileXLSX(any(File.class))).
                thenReturn(AdminFile.FileCheckXLSXResult.VALID_FILE_TYPE);

        when(campaignDao.get(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(campaign);
    }

    @After
    public void teardown() throws IOException {
        if (inp != null) {
            inp.close();
        }
        //remove files temp
        if (campaign.getId() != null && objectTypeToImport != null) {
            String path = ImportExportManager.buildImportPath(campaign.getId(), objectTypeToImport);
            deleteAllFiles(path);
        }
        if (filePathTestImportFile != null) {
            deleteAllFiles(filePathTestImportFile);
        }

        //remove temp files
        if (exportFilePath != null) {
            deleteAllFiles(exportFilePath);
        }
        //remove files temp
        if (campaignIdTestImportFile != null && objectTypeToExport != null) {
            String path = ImportExportManager.buildImportPath(campaignIdTestImportFile, objectTypeToExport);
            deleteAllFiles(path);
        }
    }

    @Test
    public void uploadCreativeInsertionXLSXFileInTmpOk() {
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(FILENAME).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(fileSize).build();

        //call method to test
        String uuidTestUploadFile = importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inp, contentDisposition, key);

        assertThat(uuidTestUploadFile, is(notNullValue()));
        File file = new File(ImportExportManager.buildImportPath(campaign.getId(), objectTypeToImport) + uuidTestUploadFile + "." + AdminFile.FileType.XLSX.getFileType());
        assertThat(file.exists(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void uploadCreativeInsertionXLSXFileInTmpWrongParameters() {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(FILENAME).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(fileSize).build();

        //call method to test
        importExportManager.uploadXLSXFileInTmp(null, objectTypeToImport, inp, contentDisposition,
                key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void uploadCreativeInsertionXLSXFileInTmpWithoutFile() {
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;

        //call method to test
        importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, null, null, key);
    }

    @Test(expected = SystemException.class)
    public void uploadCreativeInsertionXLSXFileInTmpWrongContentDisposition() {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(fileSize).build();
        //call method to test
        importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inp, contentDisposition, key);
    }

    @Test(expected = SystemException.class)
    public void uploadCreativeInsertionXLSXFileInTmpWrongExtension() {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName("Schedule Edit Template.jpg").creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(fileSize).build();

        //call method to test
        importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inp, contentDisposition, key);
    }

    @Test(expected = SystemException.class)
    public void uploadCreativeInsertionXLSXFileInTmpWrongAccess() throws Exception {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(FILENAME).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(fileSize).build();

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, Collections.singletonList(campaign.getId()),
                sqlSessionMock);

        //call method to test
        importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inp, contentDisposition, key);
    }

    @Test(expected = SystemException.class)
    public void uploadCreativeInsertionXLSXFileInTmpWrongSize() {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(FILENAME).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(fileSize).build();

        //customize mock's behavior
        when(utilityWrapperMock.getLength(anyString(), anyString())).
                thenReturn(65000000L);

        //call method to test
        importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inp, contentDisposition, key);
    }

    @Test(expected = SystemException.class)
    public void uploadCreativeInsertionXLSXFileInTmpInvalidFile() {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(FILENAME).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(fileSize).build();

        //customize mock's behavior
        when(utilityWrapperMock.validateFileXLSX(any(File.class))).
                thenReturn(AdminFile.FileCheckXLSXResult.INVALID_FILE_TYPE);

        //call method to test
        importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inp, contentDisposition, key);
    }

    @Test(expected = SystemException.class)
    public void uploadCreativeInsertionXLSXFileInTmpMaxNumRowsError() {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(FILENAME).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(fileSize).build();

        //customize mock's behavior
        when(utilityWrapperMock.validateFileXLSX(any(File.class))).
                thenReturn(AdminFile.FileCheckXLSXResult.MAX_NUM_ROWS);

        //call method to test
        importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inp,
                contentDisposition, key);
    }

    @Test
    public void importSchedulesWithIgnoreErrorsTrueOk() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_UNIQUE);

        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_BOTH_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.TRUE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                is(countDataToImport + " scheduling insertions were successfully updated!"));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsTrueWithDuplicatedIdsOk() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_DUPLICATED_GROUP_UNIQUE);

        //customize mock's behavior
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_BOTH_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.TRUE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                containsString("scheduling insertions were successfully updated!"));
        String delimit = " ";
        String[] idsString = result.success().getMessage().split(delimit);
        int affectedRows = Integer.valueOf(idsString[0]);
        assertThat(affectedRows, lessThan(countDataToImport));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsTrueWithUniqueIdsRejectedOk() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_UNIQUE);

        //customize mock's behavior
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.INSERTION_REJECTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_BOTH_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.TRUE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                containsString("scheduling insertions were successfully updated!"));
        String delimit = " ";
        String[] idsString = result.success().getMessage().split(delimit);
        int affectedRows = Integer.valueOf(idsString[0]);
        assertThat(affectedRows, lessThan(countDataToImport));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsTrueWithDuplicatedIdsRejectedOk() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_DUPLICATED_GROUP_UNIQUE);

        //customize mock's behavior
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.INSERTION_REJECTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_BOTH_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.TRUE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                containsString("scheduling insertions were successfully updated!"));
        String delimit = " ";
        String[] idsString = result.success().getMessage().split(delimit);
        int affectedRows = Integer.valueOf(idsString[0]);
        assertThat(affectedRows, lessThan(countDataToImport));
    }

    @Test(expected = IllegalArgumentException.class)
    public void importSchedulesWithIgnoreErrorsTrueWithoutInputParameterError() {
        // set data
        campaign.setId(null);

        // Perform test
        importExportManager.importToUpdateSchedulesFromXLSXFile(campaign.getId(), null, Boolean.TRUE, key);
    }

    @Test(expected = SystemException.class)
    public void importSchedulesWithIgnoreErrorsTrueWrongAccessOnCampaignError() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_UNIQUE);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, Collections.singletonList(campaign.getId()),
                sqlSessionMock);

        // Perform test
        importExportManager.importToUpdateSchedulesFromXLSXFile(campaign.getId(), uuidTestUploadFile, Boolean.TRUE, key);
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseWithDuplicatedIdsOk() throws Exception {
        // set data
        int countDataToImport = 10;
        uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_DUPLICATED_GROUP_UNIQUE);

        //customize mock's behavior
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_BOTH_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                containsString("scheduling insertions were successfully updated!"));
        String delimit = " ";
        String[] idsString = result.success().getMessage().split(delimit);
        int affectedRows = Integer.valueOf(idsString[0]);
        assertThat(affectedRows, lessThan(countDataToImport));
    }

    @Test(expected = IllegalArgumentException.class)
    public void importSchedulesWithIgnoreErrorsFalseWithNullParameters() {
        // set data
        campaign.setId(null);

        // Perform test
        importExportManager.importToUpdateSchedulesFromXLSXFile(campaign.getId(), null, Boolean.FALSE, key);
    }

    @Test(expected = SystemException.class)
    public void importSchedulesWithIgnoreErrorsFalseWrongAccessOnCampaign() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport,
                TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_UNIQUE);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, sqlSessionMock);

        // Perform test
        importExportManager.importToUpdateSchedulesFromXLSXFile(campaign.getId(),
                uuidTestUploadFile, Boolean.FALSE, key);
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseWrongCampaignOnInsertionsError() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_UNIQUE);

        //customize mock's behavior
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.INSERTION_REJECTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_BOTH_CHANGES, insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseWithUniqueIdsRejectedError() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_UNIQUE);

        //customize mock's behavior
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.INSERTION_REJECTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_BOTH_CHANGES, insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseWithDuplicatedIdsRejectedError() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_DUPLICATED_GROUP_UNIQUE);

        //customize mock's behavior
        when(creativeInsertionDao
                .getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(),
                        eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(
                        TypeAccessOnImportDataToTest.INSERTION_REJECTED,
                        TypeChangesOnImportDataToTest.DATA_WITH_BOTH_CHANGES,
                        insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseWithDuplicatedIdsAndFormulasOk() throws Exception {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        campaign.setId(Math.abs(EntityFactory.random.nextLong()));
        File file = new File(getClass().getResource("/xlsx/template.with.formulas.xlsx").toURI());
        assertThat(file, is(notNullValue()));
        assertThat(file.getTotalSpace(), is(greaterThan(0L)));

        // upload file
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(file.getName()).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(file.length()).build();

        //customize mock's behavior
        when(utilityWrapperMock.getLength(anyString(), anyString())).
                thenReturn(file.length());

        String uuidTestUploadFile;
        try (InputStream inp = new FileInputStream(file)) {
            uuidTestUploadFile = importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inp, contentDisposition, key);
        }
        assertThat(uuidTestUploadFile, is(notNullValue()));

        //customize mock's behavior
        doNothing().when(creativeInsertionDao).updateOnImport(
                any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()),
                anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(
                        TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                        TypeChangesOnImportDataToTest.DATA_WITH_BOTH_CHANGES, null));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseFileWithoutChangesOk() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_UNIQUE);

        //customize mock's behavior
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        when(creativeInsertionDao
                .getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(),
                        eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(
                        TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                        TypeChangesOnImportDataToTest.DATA_WITH_NO_CHANGES,
                        insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(),
                eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                is("0 scheduling insertions were successfully updated!"));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseFileWithInsertionChangesOk() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_UNIQUE);

        //customize mock's behavior
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sessionBatch));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sessionBatch));
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()),
                anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_INSERTION_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                is(countDataToImport + " scheduling insertions were successfully updated!"));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseFileWithUniqueGroupsChangesOk() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_UNIQUE);

        //customize mock's behavior
        when(creativeInsertionDao.openSession(any(ExecutorType.class))).thenReturn(sessionBatch);
        doNothing().when(creativeInsertionDao).close(sessionBatch);
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sessionBatch));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sessionBatch));

        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_GROUP_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                is(countDataToImport + " scheduling insertions were successfully updated!"));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseFileWithSharedGroupsAndInsertiongChangesOk() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_SHARED);

        //customize mock's behavior
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_INSERTION_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                is(countDataToImport + " scheduling insertions were successfully updated!"));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseFileWithSharedGroupsAndGroupsChangesOk() throws Exception {
        // set data
        int countDataToImport = 10;
        String uuidTestUploadFile = prepareDataToImportTest(countDataToImport, TypeImportDataToTest.INSERTIONS_UNIQUE_GROUP_SHARED);

        //customize mock's behavior
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_GROUP_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(),
                is(countDataToImport + " scheduling insertions were successfully updated!"));
    }

    @Test
    public void importSchedulesWithIgnoreErrorsFalseValidatesClickthroughIsUnallowedFor3rdError() throws Exception {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        int countDataToImport = 10;

        // prepare data to export
        insertionsSourceDataExportMap = EntityFactory.createMapCreativeInsertionImportViewList(countDataToImport);

        for (Map.Entry<String, CreativeInsertionRawDataView> entrySet : insertionsSourceDataExportMap.entrySet()) {
            entrySet.getValue().setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
            entrySet.getValue().setCreativeClickThroughUrl(EntityFactory.createFakeURL());
        }
        List<CreativeInsertionRawDataView> insertionsToExport = new ArrayList<>(insertionsSourceDataExportMap.values());

        // mock methods to export data and Export.
        when(creativeInsertionDao.getCreativeInsertionsToExport(anyLong(), any(SqlSession.class))).thenReturn(insertionsToExport);
        File file = importExportManager.exportByCampaign(campaign.getId(), objectTypeToImport, null,
                key);
        assertThat(file, is(notNullValue()));
        assertThat(file.getTotalSpace(), is(greaterThan(0L)));
        filePathTestImportFile = file.getPath();

        // Prepare Data to Upload File to Import Test
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(file.getName()).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(file.length()).build();

        // mocks methods to upload file
        when(utilityWrapperMock.getLength(anyString(), anyString())).
                thenReturn(file.length());

        String uuidTestUploadFile;
        try (InputStream inputStream = new FileInputStream(file)) {
            uuidTestUploadFile = importExportManager.uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inputStream, contentDisposition, key);
        }
        assertThat(uuidTestUploadFile, is(notNullValue()));

        // Import Process
        //customize mock's behavior
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(), eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getDataFromInsertionsHasAccessByUser(TypeAccessOnImportDataToTest.ALL_INSERTIONS_ACCEPTED,
                                TypeChangesOnImportDataToTest.DATA_WITH_INSERTION_CHANGES, insertionsSourceDataExportMap));
        when(creativeInsertionDao.getCountAffectedSchedulesDueToImport(anyList(), eq(campaign.getId()), eq(sessionBatch))).
                thenAnswer(getCountAffectedSchedulesDueToImport(insertionsSourceDataExportMap));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                campaign.getId(), uuidTestUploadFile, Boolean.FALSE, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(),
                is("3rd Creatives cannot have either primary or additional Clickthroughs."));
    }

    @Test
    public void importMediaWithAllErrors() throws Exception {
        // set data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW;
        campaign.setId(Math.abs(EntityFactory.random.nextLong()));
        File file = new File(getClass().getResource(
                "/xlsx/media/media.with.all.validation.issues.xlsx").toURI());
        assertThat(file, is(notNullValue()));
        assertThat(file.getTotalSpace(), is(greaterThan(0L)));

        // upload file
        FormDataContentDisposition contentDisposition =
                FormDataContentDisposition.name("testData").fileName(
                        file.getName()).creationDate(new Date()).modificationDate(new Date())
                                          .readDate(new Date()).size(file.length()).build();

        //customize mock's behavior
        when(utilityWrapperMock.getLength(anyString(), anyString())).thenReturn(file.length());

        String uuidTestUploadFile;
        try (InputStream inp = new FileInputStream(file)) {
            uuidTestUploadFile = importExportManager
                    .uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inp,
                            contentDisposition, key);
        }
        assertThat(uuidTestUploadFile, is(notNullValue()));

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager
                .importToCreatePlacementsFromXLSXFile(campaign.getId(), uuidTestUploadFile,
                        Boolean.FALSE, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
    }

    @Test
    public void testExport() throws Exception {
        // set data
        objectTypeToExport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        List<CreativeInsertionRawDataView> list = EntityFactory.createCreativeInsertionImportViewList(100);

        // Initialize mocks
        when(creativeInsertionDao.openSession()).thenReturn(sqlSessionMock);
        when(campaignDao.get(anyLong(), any(SqlSession.class))).thenReturn(campaign);
        when(creativeInsertionDao.getCreativeInsertionsToExport(anyLong(),
                any(SqlSession.class))).thenReturn(list);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyList(), anyString(), any(SqlSession.class))).thenReturn(true);

        // perform test
        File file = importExportManager.exportByCampaign(campaign.getId(), objectTypeToExport, null,
                key);
        exportFilePath = file.getPath();
        assertNotNull(file);
        assertTrue(file.getTotalSpace() > 0);
    }

    @Test(expected = SystemException.class)
    public void testExportFail() throws Exception {
        // set data
        objectTypeToExport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        List<CreativeInsertionRawDataView> list = EntityFactory.createCreativeInsertionImportViewList(100);
        // Initialize mocks
        when(creativeInsertionDao.openSession()).thenReturn(sqlSessionMock);
        when(campaignDao.get(anyLong(), any(SqlSession.class))).thenReturn(campaign);
        when(creativeInsertionDao.getCreativeInsertionsToExport(anyLong(),
                any(SqlSession.class))).thenReturn(list);
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN,
                Collections.singletonList(campaign.getId()), sqlSessionMock);

        // perform test
        importExportManager.exportByCampaign(campaign.getId(), objectTypeToExport, null, key);
    }

    @Ignore
    @Test
    public void testExportIssuesOk() throws Exception {
        // set data
        objectTypeToExport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        prepareDataToExportIssuesTest(10, this.INSERTIONS_UNIQUE_REJECTED);

        // Initialize mocks
        when(creativeInsertionDao.openSession()).thenReturn(sqlSessionMock);
        when(campaignDao.get(anyLong(), any(SqlSession.class))).thenReturn(campaign);

        // perform test
        File file = importExportManager
                .exportIssuesOfImportByCampaign(campaignIdTestImportFile, objectTypeToExport, null,
                        uuidTestUploadFile, key);
        exportFilePath = file.getPath();
        assertNotNull(file);
        assertTrue(file.getTotalSpace() > 0);
    }

    @Ignore
    @Test(expected = SystemException.class)
    public void testExportIssuesWithWrongAcceess() throws Exception {
        // set data
        objectTypeToExport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        prepareDataToExportIssuesTest(10, this.INSERTIONS_UNIQUE_REJECTED);

        // Initialize mocks
        when(creativeInsertionDao.openSession()).thenReturn(sqlSessionMock);
        when(campaignDao.get(anyLong(), any(SqlSession.class))).thenReturn(campaign);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyListOf(Long.class), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        importExportManager
                .exportIssuesOfImportByCampaign(campaignIdTestImportFile, objectTypeToExport, null,
                        uuidTestUploadFile, key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportIssuesWithoutCampaignId() throws Exception {
        // set data
        objectTypeToExport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        prepareDataToExportIssuesTest(10, this.INSERTIONS_UNIQUE_REJECTED);

        // perform test
        importExportManager.exportIssuesOfImportByCampaign(null, objectTypeToExport, null,
                uuidTestUploadFile, key);
    }

    private void deleteAllFiles(String path) {
        File f = new File(path);
        if (f.exists()) {
            if (f.isDirectory()) {
                for (File c : f.listFiles()) {
                    deleteFile(c);
                }
            }

            boolean deleted = f.delete();
            if (!deleted) {
                fail("Could not clean up test directory: " + f.getAbsolutePath());
            }
        }
    }

    private void deleteFile(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteFile(c);
            }
        }
        if (!f.delete()) {
            fail("Could not clean up test directory");
        }
    }

    private String prepareDataToImportTest(int counterDataImport, TypeImportDataToTest typeImportData) throws Exception {

        // prepare data to export
        objectTypeToExport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        insertionsSourceDataExportMap = EntityFactory.createMapCreativeInsertionImportViewList(counterDataImport);

        List<CreativeInsertionRawDataView> insertionsToExport = new ArrayList<>(insertionsSourceDataExportMap.values());
        List<String> insertionIds = null;
        int counterDupGroups;
        int counterDupIns;
        switch (typeImportData) {
            case INSERTIONS_UNIQUE_GROUP_UNIQUE:
                break;
            case INSERTIONS_UNIQUE_GROUP_SHARED:
                insertionIds = new ArrayList<>(insertionsSourceDataExportMap.keySet());
                counterDupGroups = 1;
                while (counterDupGroups < insertionsSourceDataExportMap.size()) {
                    insertionsSourceDataExportMap.get(insertionIds.get(counterDupGroups)).setCreativeGroupId(
                            insertionsSourceDataExportMap.get(insertionIds.get(counterDupGroups - 1)).getCreativeGroupId());
                    counterDupGroups += 2;
                }
                break;
            case INSERTIONS_DUPLICATED_GROUP_UNIQUE:
                counterDupIns = 3;
                while (counterDupIns < insertionsToExport.size()) {
                    insertionsToExport.set(counterDupIns, insertionsToExport.get(counterDupIns - 1));
                    counterDupIns += 5;
                }
                break;
            case INSERTIONS_DUPLICATED_GROUP_SHARED:
                insertionIds = new ArrayList<>(insertionsSourceDataExportMap.keySet());
                counterDupGroups = 1;
                while (counterDupGroups < insertionsSourceDataExportMap.size()) {
                    insertionsSourceDataExportMap.get(insertionIds.get(counterDupGroups)).setCreativeGroupId(
                            insertionsSourceDataExportMap.get(insertionIds.get(counterDupGroups - 1)).getCreativeGroupId());
                    counterDupGroups += 2;
                }
                counterDupIns = 3;
                while (counterDupIns < insertionsToExport.size()) {
                    insertionsToExport.set(counterDupIns, insertionsToExport.get(counterDupIns - 1));
                    counterDupIns += 5;
                }
                break;
        }

        // mock methods to export data and Export.
        when(creativeInsertionDao.getCreativeInsertionsToExport(anyLong(), any(SqlSession.class))).thenReturn(insertionsToExport);
        File file = importExportManager.exportByCampaign(campaign.getId(), objectTypeToExport, null,
                key);
        assertThat(file, is(notNullValue()));
        assertThat(file.getTotalSpace(), is(greaterThan(0L)));
        filePathTestImportFile = file.getPath();

        // Prepare Data to Upload File to Import Test
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(file.getName()).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(file.length()).build();

        // mocks methods to upload file
        when(utilityWrapperMock.getLength(anyString(), anyString())).
                thenReturn(file.length());

        try (InputStream inputStream = new FileInputStream(file)) {
            uuidTestUploadFile = importExportManager.uploadXLSXFileInTmp(campaign.getId(), ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW, inputStream, contentDisposition, key);
        }
        assertThat(uuidTestUploadFile, is(notNullValue()));

        return uuidTestUploadFile;
    }

    private void prepareDataToExportIssuesTest(int recordsImport, int testType) throws Exception {
        // set data
        objectTypeToExport = ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW;
        List<CreativeInsertionRawDataView> insertionsToExport = EntityFactory.createCreativeInsertionImportViewList(recordsImport);
        if (testType == INSERTIONS_DUPLICATED_ACCEPTED || testType == INSERTIONS_DUPLICATED_REJECTED) {
            int counter = 3;
            while (counter < insertionsToExport.size()) {
                insertionsToExport.set(counter, insertionsToExport.get(counter - 1));
                counter += 5;
            }
        }
        //customize mock's behavior
        when(campaignDao.get(anyLong(), any(SqlSession.class))).thenReturn(campaign);
        when(creativeInsertionDao.getCreativeInsertionsToExport(anyLong(),
                any(SqlSession.class))).thenReturn(insertionsToExport);

        // export creative insertions
        File file = importExportManager.exportByCampaign(campaign.getId(), objectTypeToExport, null,
                key);
        assertNotNull(file);
        assertTrue(file.getTotalSpace() > 0);
        exportFilePath = file.getPath();

        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("testData").
                fileName(file.getName()).creationDate(new Date()).modificationDate(new Date()).
                readDate(new Date()).size(file.length()).build();

        //customize mock's behavior
        when(utilityWrapperMock.getLength(anyString(), anyString())).
                thenReturn(file.length());
        when(utilityWrapperMock.validateFileXLSX(any(File.class))).
                thenReturn(AdminFile.FileCheckXLSXResult.VALID_FILE_TYPE);

        try (InputStream inp = new FileInputStream(file)) {
            // upload file
            uuidTestUploadFile = importExportManager.uploadXLSXFileInTmp(
                    campaign.getId(), ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW, inp, contentDisposition, key);
        }
        assertThat(uuidTestUploadFile, is(notNullValue()));

        //remove export file
        teardown();
        campaignIdTestImportFile = campaign.getId();

        // import process
        //customize mock's behavior
        when(creativeInsertionDao.getCreativeInsertionsByUserId(anyList(),
                eq(key.getUserId()), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getCreativeInsertionsByUserId(INSERTIONS_UNIQUE_REJECTED));
        doNothing().when(creativeInsertionDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));
        doNothing().when(creativeGroupDao).updateOnImport(any(CreativeInsertionRawDataView.class),
                eq(sqlSessionMock));

        // call method to test import process
        Either<ImportExportErrors, SuccessResponse> response = importExportManager.importToUpdateSchedulesFromXLSXFile(campaignIdTestImportFile, uuidTestUploadFile, Boolean.FALSE, key);
        assertNotNull(response);
        assertThat(response.error(), is(notNullValue()));
    }

    private static Answer<List<CreativeInsertionRawDataView>> getDataFromInsertionsHasAccessByUser(final TypeAccessOnImportDataToTest accessTestType,
            final TypeChangesOnImportDataToTest dataTestType,
            final Map<String, CreativeInsertionRawDataView> sourceDataMap) {
        return new Answer<List<CreativeInsertionRawDataView>>() {
            @Override
            public List<CreativeInsertionRawDataView> answer(InvocationOnMock invocation) {
                Set<Long> ids = (Set<Long>) invocation.getArguments()[0];
                List<Long> listIds = new ArrayList<>(ids);

                List<CreativeInsertionRawDataView> result = new ArrayList<>();
                switch (accessTestType) {
                    case ALL_INSERTIONS_ACCEPTED:
                        for (Long id : listIds) {
                            CreativeInsertionRawDataView ci = new CreativeInsertionRawDataView();
                            ci.setCreativeInsertionId(id.toString());
                            ci.setCreativeGroupId(Math.abs(EntityFactory.random.nextLong()));
                            if (sourceDataMap != null) {
                                CreativeInsertionRawDataView oldDataToExport = sourceDataMap.get(id.toString());
                                ci.setCreativeGroupId(oldDataToExport.getCreativeGroupId());
                                setDataToResult(ci, oldDataToExport);
                            }
                            result.add(ci);
                        }
                        break;
                    case INSERTION_REJECTED:
                        for (int i = 0; i < listIds.size(); i++) {
                            if ((i % 3) != 0) {
                                CreativeInsertionRawDataView ci = new CreativeInsertionRawDataView();
                                ci.setCreativeInsertionId(listIds.get(i).toString());

                                CreativeInsertionRawDataView oldDataToExport = sourceDataMap.get(ci.getCreativeInsertionId());
                                ci.setCreativeGroupId(oldDataToExport.getCreativeGroupId());
                                setDataToResult(ci, oldDataToExport);
                                result.add(ci);
                            }
                        }
                        break;
                }
                return result;
            }

            private void setDataToResult(CreativeInsertionRawDataView ci, CreativeInsertionRawDataView oldDataToExport) {
                long newInsertionWeight;
                long newGroupWeight;
                switch (dataTestType) {
                    case DATA_WITH_NO_CHANGES:
                        //set old values for insertion and grup data
                        ci.setCreativeWeight(oldDataToExport.getCreativeWeight());
                        ci.setCreativeStartDate(oldDataToExport.getCreativeStartDate());
                        ci.setCreativeEndDate(oldDataToExport.getCreativeEndDate());
                        ci.setCreativeType(oldDataToExport.getCreativeType());
                        ci.setCreativeClickThroughUrl(oldDataToExport.getCreativeClickThroughUrl());
                        ci.setGroupWeight(oldDataToExport.getGroupWeight());
                        break;
                    case DATA_WITH_INSERTION_CHANGES:
                        //set new values for insertion data
                        newInsertionWeight = Long.parseLong(oldDataToExport.getCreativeWeight()) + 1 > Constants.CREATIVE_INSERTION_MAX_WEIGHT
                                ? Long.parseLong(oldDataToExport.getCreativeWeight()) - 2 : new Long(oldDataToExport.getCreativeWeight()) + 1L;
                        ci.setCreativeWeight("" + newInsertionWeight);
                        ci.setCreativeStartDate(oldDataToExport.getCreativeStartDate());
                        ci.setCreativeEndDate(oldDataToExport.getCreativeEndDate());
                        ci.setCreativeType(oldDataToExport.getCreativeType());
                        if (oldDataToExport.getCreativeType().equalsIgnoreCase(CreativeManager.CreativeType.TRD.getCreativeType())) {
                            ci.setCreativeClickThroughUrl(null);
                        } else {
                            ci.setCreativeClickThroughUrl(oldDataToExport.getCreativeClickThroughUrl());
                        }
                        //set old value for group data
                        ci.setGroupWeight(oldDataToExport.getGroupWeight());
                        break;
                    case DATA_WITH_GROUP_CHANGES:
                        //set old values for insertion data
                        ci.setCreativeWeight(oldDataToExport.getCreativeWeight());
                        ci.setCreativeStartDate(oldDataToExport.getCreativeStartDate());
                        ci.setCreativeEndDate(oldDataToExport.getCreativeEndDate());
                        ci.setCreativeType(oldDataToExport.getCreativeType());
                        ci.setCreativeClickThroughUrl(oldDataToExport.getCreativeClickThroughUrl());
                        //set new value for group data
                        newGroupWeight = Long.parseLong(oldDataToExport.getGroupWeight()) + 1 > Constants.CREATIVE_GROUP_MAX_WEIGHT
                                ? Long.parseLong(oldDataToExport.getGroupWeight()) - 2 : new Long(oldDataToExport.getGroupWeight()) + 1L;
                        ci.setGroupWeight("" + newGroupWeight);
                        break;
                    case DATA_WITH_BOTH_CHANGES:
                        //set new values for insertion data
                        newInsertionWeight = Long.parseLong(oldDataToExport.getCreativeWeight()) + 1 > Constants.CREATIVE_INSERTION_MAX_WEIGHT
                                ? Long.parseLong(oldDataToExport.getCreativeWeight()) - 2 : new Long(oldDataToExport.getCreativeWeight()) + 1L;
                        ci.setCreativeWeight("" + newInsertionWeight);
                        ci.setCreativeStartDate(oldDataToExport.getCreativeStartDate());
                        ci.setCreativeEndDate(oldDataToExport.getCreativeEndDate());
                        ci.setCreativeType(oldDataToExport.getCreativeType());
                        if (oldDataToExport.getCreativeType().equalsIgnoreCase(CreativeManager.CreativeType.TRD.getCreativeType())) {
                            ci.setCreativeClickThroughUrl(null);
                        } else {
                            ci.setCreativeClickThroughUrl(oldDataToExport.getCreativeClickThroughUrl());
                        }
                        //set new value for group data
                        newGroupWeight = Long.parseLong(oldDataToExport.getGroupWeight()) + 1 > Constants.CREATIVE_GROUP_MAX_WEIGHT
                                ? Long.parseLong(oldDataToExport.getGroupWeight()) - 2 : new Long(oldDataToExport.getGroupWeight()) + 1L;
                        ci.setGroupWeight("" + newGroupWeight);
                        break;
                }
            }
        };
    }

    private static Answer<Integer> getCountAffectedSchedulesDueToImport(final Map<String, CreativeInsertionRawDataView> sourceDataMap) {
        return new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) {
                List<Long> listGroupIds = (List<Long>) invocation.getArguments()[0];
                Integer result = 0;
                Map<Long, List<CreativeInsertionRawDataView>> validGroupInsertionsMap = new HashMap<>();
                List<CreativeInsertionRawDataView> validInsertions = new ArrayList<>(sourceDataMap.values());
                for (CreativeInsertionRawDataView validInsertion : validInsertions) {
                    if (!validGroupInsertionsMap.containsKey(validInsertion.getCreativeGroupId())) {
                        validGroupInsertionsMap.put(validInsertion.getCreativeGroupId(), new ArrayList<CreativeInsertionRawDataView>());
                    }
                    validGroupInsertionsMap.get(validInsertion.getCreativeGroupId()).add(validInsertion);
                }

                for (Long groupId : listGroupIds) {
                    result += validGroupInsertionsMap.get(groupId).size();
                }
                return result;
            }
        };
    }

    private static Answer<List<CreativeInsertionRawDataView>> getCreativeInsertionsByUserId(final int testType) {
        return new Answer<List<CreativeInsertionRawDataView>>() {
            @Override
            public List<CreativeInsertionRawDataView> answer(InvocationOnMock invocation) {
                Set<Long> ids = (Set<Long>) invocation.getArguments()[0];
                List<Long> listIds = new ArrayList<>(ids);
                //String userId = (String) invocation.getArguments()[1];
                List<CreativeInsertionRawDataView> valids = new ArrayList<>();
                if (testType == ALL_INSERTIONS_UNIQUE_ACCEPTED || testType == INSERTIONS_DUPLICATED_ACCEPTED) {
                    for (Long id : listIds) {
                        CreativeInsertionRawDataView ci = new CreativeInsertionRawDataView();
                        ci.setCreativeInsertionId(id.toString());
                        ci.setCreativeGroupId(Math.abs(EntityFactory.random.nextLong()));
                        valids.add(ci);
                    }
                } else if (testType == INSERTIONS_UNIQUE_REJECTED || testType == INSERTIONS_DUPLICATED_REJECTED) {
                    for (int i = 0; i < listIds.size(); i++) {
                        if ((i % 3) != 0) {
                            CreativeInsertionRawDataView ci = new CreativeInsertionRawDataView();
                            ci.setCreativeInsertionId(listIds.get(i).toString());
                            ci.setCreativeGroupId(Math.abs(EntityFactory.random.nextLong()));
                            valids.add(ci);
                        }
                    }
                }
                return valids;
            }
        };
    }
}

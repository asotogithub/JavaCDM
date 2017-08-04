package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.exceptions.business.ImportExportErrors;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
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
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackagePlacementDaoImpl;

import com.sun.jersey.core.header.FormDataContentDisposition;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Unit tests for {@code Media} Import/Export
 *
 * @author Marcelo Heredia
 */
public class MediaImportExportManagerTest extends AbstractManagerTest {

    private ImportExportManager importExportManager;

    private ImportExportManager.UtilityWrapper utilityWrapperMock;

    private InputStream inp;
    private Campaign campaign;
    private String filePathTestImportFile;
    private String objectTypeToImport;
    private String objectTypeToExport;

    private String exportFilePath;
    private Long campaignIdTestImportFile;

    private static final String BLANK_TEMPLATE_FILE = "Schedule Edit Template.xlsx";
    private static final String BLANK_FILE_PATH = "/template/xlsx/" + BLANK_TEMPLATE_FILE;


    @Before
    public void setupImportFiles() {
        // Create mock DAOs
        CampaignDao campaignDao = mock(CampaignDao.class);
        InsertionOrderDao insertionOrderDao = mock(InsertionOrderDao.class);

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
        CreativeInsertionDao creativeInsertionDao = mock(CreativeInsertionDaoImpl.class);


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
                new ImportExportManager(campaignDao, null, null, insertionOrderDao, publisherDao,
                        siteDao, sectionDao, sizeDao, placementDao, placementCostDetailDao,
                        packageCostDetailDao, mediaBuyDao, userDao, dimPackagePlacementDao, packageDao,
                        ioManager, publisherManager, siteManager, sectionManager, sizeManager,
                        placementManager, packageManager, packagePlacementManager, accessControlMockito);
        utilityWrapperMock = mock(ImportExportManager.UtilityWrapper.class);
        importExportManager.setUtilityWrapper(utilityWrapperMock);

        //variables
        campaign = EntityFactory.createCampaign();

        inp = getClass().getResourceAsStream(BLANK_FILE_PATH);
        objectTypeToImport = null;
        filePathTestImportFile = null;

        objectTypeToExport = null;
        exportFilePath = null;
        campaignIdTestImportFile = null;

        // Mock Campaign DAO Methods Session
        when(campaignDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(campaignDao).close(sqlSessionMock);
        when(campaignDao.get(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(campaign);

        // Mocks DAC
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, sqlSessionMock);

        // Mocks File Management utilities
        when(utilityWrapperMock.getLength(anyString(), anyString())).
                thenReturn(1000L);
        when(utilityWrapperMock.validateFileXLSX(any(File.class))).
                thenReturn(AdminFile.FileCheckXLSXResult.VALID_FILE_TYPE);

    }

    @After
    public void teardown() throws IOException {
        if (inp != null) {
            inp.close();
        }
        //remove files temp
        if (campaign.getId() != null && objectTypeToImport != null) {
            String path = ImportExportManager.buildImportPath(campaign.getId(), objectTypeToImport);
            AdminFile.deleteRecursively(path);
        }
        if (filePathTestImportFile != null) {
            AdminFile.deleteRecursively(filePathTestImportFile);
        }

        //remove temp files
        if (exportFilePath != null) {
            AdminFile.deleteRecursively(exportFilePath);
        }
        //remove files temp
        if (campaignIdTestImportFile != null && objectTypeToExport != null) {
            String path = ImportExportManager.buildImportPath(campaignIdTestImportFile,
                    objectTypeToExport);
            AdminFile.deleteRecursively(path);
        }
    }

    @Test
    public void testImportMediaWorksheetWithAllColumnsInError() throws Exception {
        //// Set up data
        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW;

        // Prepare import file
        File file = buildImportFile("/xlsx/media/media.with.all.validation.issues.xlsx");

        // Upload file
        FormDataContentDisposition contentDisposition = buildFormData(file);

        // Customize Mocks' behavior
        when(utilityWrapperMock.getLength(anyString(), anyString())).thenReturn(file.length());

        String uuidTestUploadFile;
        try (InputStream inputStream = new FileInputStream(file)) {
            uuidTestUploadFile = importExportManager
                    .uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inputStream,
                            contentDisposition, key);
        }

        // Perform test
        Either<ImportExportErrors, SuccessResponse> result = importExportManager
                .importToCreatePlacementsFromXLSXFile(campaign.getId(), uuidTestUploadFile,
                        Boolean.FALSE, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        // TODO add more assertions about errors
        System.out.println("Success : " + result);
    }

    @Test
    public void testMediaWorksheetWithCostsCanGenerateModelCollection()
            throws Exception {
//        //// Set up data
//        objectTypeToImport = ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW;
//
//        // Prepare import file
//        File file = buildImportFile("/xlsx/media/media.with.all.validation.issues.xlsx");
//
//        // Upload file
//        FormDataContentDisposition contentDisposition = buildFormData(file);
//
//        // Customize Mocks' behavior
//        when(utilityWrapperMock.getLength(anyString(), anyString())).thenReturn(file.length());
//
//        String uuidTestUploadFile;
//        try (InputStream inputStream = new FileInputStream(file)) {
//            uuidTestUploadFile = importExportManager
//                    .uploadXLSXFileInTmp(campaign.getId(), objectTypeToImport, inputStream,
//                            contentDisposition, key);
//        }
//
//        // Perform test
//        Either<ImportExportErrors, SuccessResponse> result = importExportManager
//                .importToCreatePlacementsFromXLSXFile(campaign.getId(), uuidTestUploadFile,
//                        Boolean.FALSE, null, key);
//
//        assertThat(result, is(notNullValue()));
//        assertThat(result.success(), is(nullValue()));
//        assertThat(result.error(), is(notNullValue()));
//        // TODO add more assertions about errors
//        System.out.println("Success : " + result);
    }

    @Test
    public void testMediaRawModelWithCostsCanGenerateDomainModel() {

    }

    private FormDataContentDisposition buildFormData(File file) {
        return FormDataContentDisposition.name("testData").fileName(
                file.getName()).creationDate(new Date()).modificationDate(new Date())
                                  .readDate(new Date()).size(file.length()).build();
    }

    private File buildImportFile(String path) throws URISyntaxException {
        return new File(getClass().getResource(
                path).toURI());
    }

}

package trueffect.truconnect.api.crud.service.importexport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.business.ImportExportCellError;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.importexport.Action;
import trueffect.truconnect.api.commons.model.importexport.CostDetailRawDataView;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.model.importexport.PackageMapId;
import trueffect.truconnect.api.commons.model.importexport.enums.InAppOption;
import trueffect.truconnect.api.commons.model.importexport.enums.InAppType;
import trueffect.truconnect.api.commons.model.importexport.enums.ImportIssueType;
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
import trueffect.truconnect.api.crud.service.AbstractManagerTest;
import trueffect.truconnect.api.crud.service.InsertionOrderManager;
import trueffect.truconnect.api.crud.service.PackageManager;
import trueffect.truconnect.api.crud.service.PackagePlacementManager;
import trueffect.truconnect.api.crud.service.PlacementManager;
import trueffect.truconnect.api.crud.service.PublisherManager;
import trueffect.truconnect.api.crud.service.SiteManager;
import trueffect.truconnect.api.crud.service.SiteSectionManager;
import trueffect.truconnect.api.crud.service.SizeManager;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by richard.jaldin on 5/11/2016.
 */
public class MediaImportExportHelperTest extends AbstractManagerTest {

    private static final int ALL_DATA = 0;
    private static final int PARTIAL_DATA = 1;
    private Long campaignId;
    private User user;
    private Map<Long, Placement> placements;
    private List<MediaRawDataView> records;
    private MediaImportExportHelper mediaHelper;

    private InsertionOrderDao insertionOrderDao;
    private PublisherDao publisherDao;
    private SiteDao siteDao;
    private SiteSectionDao sectionDao;
    private SizeDao sizeDao;
    private PlacementDao placementDao;
    private CostDetailDaoExtended placementCostDetailDao;
    private PackageDaoExtended packageDao;

    @Before
    public void setUp() {
        insertionOrderDao = mock(InsertionOrderDao.class);
        publisherDao = mock(PublisherDao.class);
        siteDao = mock(SiteDao.class);
        sectionDao = mock(SiteSectionDao.class);
        sizeDao = mock(SizeDao.class);
        placementDao = mock(PlacementDao.class);
        packageDao = mock(PackageDaoExtended.class);

        placementCostDetailDao = mock(CostDetailDaoExtended.class); //CostDetailType.PLACEMENT
        CostDetailDaoExtended packageCostDetailDao = mock(CostDetailDaoExtended.class); //CostDetailType.PACKAGE
        InsertionOrderStatusDao insertionOrderStatusDao = mock(InsertionOrderStatusDao.class);
        PlacementStatusDao placementStatusDao = mock(PlacementStatusDao.class);
        PackagePlacementDaoExtended packagePlacementDao = mock(PackagePlacementDaoExtended.class);
        CostDetailDaoBase dimPackageCostDetailDao = mock(CostDetailDaoBase.class); //CostDetailType.PACKAGE
        CostDetailDaoBase dimPlacementCostDetailDao = mock(CostDetailDaoBase.class); //CostDetailType.PLACEMENT
        PackageDaoBase dimPackageDao = mock(PackageDaoBase.class);
        PackagePlacementDaoBase dimPackagePlacementDao = mock(PackagePlacementDaoBase.class);
        UserDao userDao = mock(UserDao.class);
        ExtendedPropertiesDao extendedPropertiesDao = mock(ExtendedPropertiesDao.class);
        CampaignDao campaignDao = mock(CampaignDao.class);
        CreativeInsertionDao creativeInsertionDao = mock(CreativeInsertionDaoImpl.class);

        InsertionOrderManager ioManager = new InsertionOrderManager(insertionOrderDao,
                insertionOrderStatusDao, userDao, placementDao, placementStatusDao,
                creativeInsertionDao, accessControlMockito);
        PublisherManager publisherManager = new PublisherManager(publisherDao, accessControlMockito);
        SiteManager siteManager = new SiteManager(siteDao, extendedPropertiesDao,
                accessControlMockito);
        SiteSectionManager sectionManager = new SiteSectionManager(sectionDao, accessControlMockito);
        SizeManager sizeManager = new SizeManager(sizeDao, accessControlMockito);
        PlacementManager placementManager = new PlacementManager(placementDao, placementCostDetailDao,
                campaignDao, sectionDao, sizeDao, placementStatusDao, userDao, extendedPropertiesDao,
                insertionOrderDao, insertionOrderStatusDao, packageDao, packagePlacementDao,
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

        sqlSessionMock = mock(SqlSession.class);

        mediaHelper = new MediaImportExportHelper(insertionOrderDao, publisherDao, placementDao, siteDao, sectionDao,
                sizeDao, packageDao, ioManager, publisherManager, siteManager, sectionManager,
                sizeManager, placementManager, packageManager, packagePlacementManager, key);

        campaignId = Math.abs(EntityFactory.random.nextLong());
        user = EntityFactory.createUser();
        user.setUserName(key.getUserId());
        placements = new HashMap<>();
        records = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            MediaRawDataView raw = EntityFactory.createMediaRawDataView();
            raw.setRateType(EntityFactory.faker.options().option(new String[]{
                    "CPA", "CPC", "CPL", "CPM", "FLT"}));
            records.add(raw);
        }

        when(userDao.get(anyString(), eq(sqlSessionMock))).thenReturn(user);
        when(sizeDao.create(
                any(Size.class),
                eq(sqlSessionMock))).thenAnswer(new Answer<Size>() {
            @Override
            public Size answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Size) invocationOnMock.getArguments()[0];
            }
        });
        //mock behaviors - DAC
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.PLACEMENT,
                sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.PACKAGE,
                sqlSessionMock);
    }

    @Test
    public void testDoModelValidationAllOk() throws Exception {
        records = mediaHelper.doUnflatten(records);
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(records);
        List<MediaRawDataView> rows = mediaHelper.doModelValidation(records, packagesMap);
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            assertThat(row.getReason(), is(""));
            assertThat(row.getIssues().size(), is(0));
        }
    }

    @Test
    public void testDoModelValidationAllWrong() throws Exception {
        //mess with all data
        messWithData(ALL_DATA);
        // Execute the test
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(records);
        List<MediaRawDataView> rows = mediaHelper.doModelValidation(records, packagesMap);
        for (MediaRawDataView row : rows) {
            executeModelValidationAssertions(row);
        }
    }

    @Test
    public void testDoModelValidationPartiallyWrong() throws Exception {
        //mess with half of the data
        messWithData(PARTIAL_DATA);
        records = mediaHelper.doUnflatten(records);
        // Execute the test
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(records);
        List<MediaRawDataView> rows = mediaHelper.doModelValidation(records, packagesMap);
        for (int i = 0; i < rows.size(); i++) {
            MediaRawDataView row = rows.get(i);
            if(i % 2 == 0){
                executeModelValidationAssertions(row);
            } else {
                assertThat(row, is(notNullValue()));
                assertThat(row.getReason(), is(""));
                assertThat(row.getIssues().size(), is(0));
            }
        }
    }

    @Test
    public void testDoModelValidationForAutogeneratedPlacementName() throws Exception {
        //mess with all data
        for (int i = 0; i < records.size(); i++) {
            MediaRawDataView record = records.get(i);
            record.setPlacementName(null);
            record.setPublisher(EntityFactory.faker.lorem().fixedString(150));
            record.setSite(EntityFactory.faker.lorem().fixedString(150));
            record.setSection(EntityFactory.faker.lorem().fixedString(150));
        }

        records = mediaHelper.doUnflatten(records);
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(records);
        // Execute the test
        List<MediaRawDataView> rows = mediaHelper.doModelValidation(records, packagesMap);
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            assertThat(row.getIssues().size(), is(1));
            assertThat(row.getIssues().containsKey("placementName"), is(true));
            assertThat(row.getReason(), is("The auto-generated concatenated placement " +
                    "name exceeds the character limit (256) and will be truncated for you. " +
                    "Confirm this placement name in TruAdvertiser after Import"));
            List<ImportExportCellError> warnings = row.getIssues().get("placementName");
            assertThat(warnings, is(notNullValue()));
            for (ImportExportCellError warning : warnings) {
                assertThat(warning, is(notNullValue()));
                assertThat(warning.getMessage(), is("The auto-generated concatenated placement " +
                        "name exceeds the character limit (256) and will be truncated for you. " +
                        "Confirm this placement name in TruAdvertiser after Import"));
                assertThat(warning.getType(), is(ImportIssueType.WARNING));
            }
        }
    }

    @Test
    public void testDoModelValidationAllWithAllCellsWithFormulasWrong() throws Exception {
        //mess with all data
        for (int i = 0; i < records.size(); i++) {
            MediaRawDataView record = records.get(i);
            List<String> properties =
                    Arrays.asList("orderNumber", "orderName", "placementName", "extPlacementId",
                            "publisher", "site", "section", "adWidth", "adHeight", "plannedAdSpend",
                            "inventory", "rate", "rateType", "startDate", "endDate",
                            "placementProp1", "placementProp2", "placementProp3", "placementProp4",
                            "placementProp5", "sectionProp1", "sectionProp2", "sectionProp3",
                            "sectionProp4", "sectionProp5");
            record.setFieldsWithFormulaError(properties);
        }
        records = mediaHelper.doUnflatten(records);
        // Execute the test
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(records);
        List<MediaRawDataView> rows = mediaHelper.doModelValidation(records, packagesMap);
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            assertThat(row.getIssues().size(), is(20));
            assertThat(row.getIssues().containsKey("orderNumber"), is(true));
            assertThat(row.getIssues().containsKey("orderName"), is(true));
            assertThat(row.getIssues().containsKey("placementName"), is(true));
            assertThat(row.getIssues().containsKey("extPlacementId"), is(true));
            assertThat(row.getIssues().containsKey("publisher"), is(true));
            assertThat(row.getIssues().containsKey("site"), is(true));
            assertThat(row.getIssues().containsKey("section"), is(true));
            assertThat(row.getIssues().containsKey("adWidth"), is(true));
            assertThat(row.getIssues().containsKey("adHeight"), is(true));
            assertThat(row.getIssues().containsKey("costDetails[0].plannedAdSpend"), is(false));
            assertThat(row.getIssues().containsKey("costDetails[0].rate"), is(false));
            assertThat(row.getIssues().containsKey("costDetails[0].rateType"), is(false));
            assertThat(row.getIssues().containsKey("costDetails[0].startDate"), is(false));
            assertThat(row.getIssues().containsKey("costDetails[0].endDate"), is(false));
            assertThat(row.getIssues().containsKey("placementProp1"), is(true));
            assertThat(row.getIssues().containsKey("placementProp2"), is(true));
            assertThat(row.getIssues().containsKey("placementProp3"), is(true));
            assertThat(row.getIssues().containsKey("placementProp4"), is(true));
            assertThat(row.getIssues().containsKey("placementProp5"), is(true));
            assertThat(row.getIssues().containsKey("sectionProp1"), is(true));
            assertThat(row.getIssues().containsKey("sectionProp2"), is(true));
            assertThat(row.getIssues().containsKey("sectionProp3"), is(true));
            assertThat(row.getIssues().containsKey("sectionProp4"), is(true));
            assertThat(row.getIssues().containsKey("sectionProp5"), is(true));
            assertThat(row.getReason(), is("" +
                    "This row contains a cell with an error in the formula under the " +
                    "PlacementProp1 column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "Site column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "PlacementProp3 column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "PlacementProp2 column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "PlacementProp5 column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "PlacementProp4 column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "Order Name column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "Ad Width column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "Ad Height column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "Section column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "Publisher column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "SectionProp1 column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "SectionProp3 column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "Ext Placement ID column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "SectionProp2 column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "SectionProp5 column and will NOT be imported., " +
                    "One or more Cost Details have errors, " +
                    "This row contains a cell with an error in the formula under the " +
                    "SectionProp4 column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "Order Number column and will NOT be imported., " +
                    "This row contains a cell with an error in the formula under the " +
                    "Placement Name column and will NOT be imported."));
        }
        // Check error in formulas for Cost Details

    }

    @Test
    public void testDoDataValidationAllOkWithOnlyExistingElements() throws Exception {
        prepareMocksForDataValidation();
        records = mediaHelper.doUnflatten(records);
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(records);
        List <MediaRawDataView> rows = mediaHelper.doModelValidation(records, packagesMap);
        rows = mediaHelper.doPlacementDataValidation(campaignId, rows, sqlSessionMock,
                key.getUserId());
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            assertThat(row.getReason(), is(""));
            assertThat(row.getIssues().size(), is(0));
            assertThat(row.getIoId(), is(notNullValue()));
            assertThat(row.getPublisherId(), is(notNullValue()));
            assertThat(row.getSiteId(), is(nullValue()));
            assertThat(row.getSiteIdDb(), is(notNullValue()));
            assertThat(row.getSectionId(), is(notNullValue()));
            assertThat(row.getSizeId(), is(notNullValue()));
        }
    }

    @Test
    public void testDoDataValidationAllOkWithOnlyNewElements() throws Exception {
        // Prepare DAOs
        when(insertionOrderDao.getIosByCampaignIdAndIoNumberName(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenReturn(null);

        when(publisherDao.getByCampaignIdAndName(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenReturn(null);

        when(siteDao.getByCampaignIdAndPublisherNameSiteName(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenReturn(null);

        when(sectionDao.getByCampaignIdAndPublisherNameSiteName(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenReturn(null);

        when(sizeDao.getByCampaignIdAndWidthHeight(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenReturn(null);

        //mess with all data
        messWithData(ALL_DATA);

        // Execute the test
        records = mediaHelper.doUnflatten(records);
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(records);
        List <MediaRawDataView> rows = mediaHelper.doModelValidation(records, packagesMap);
        rows = mediaHelper.doPlacementDataValidation(campaignId, rows, sqlSessionMock,
                key.getUserId());
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            assertThat(row.getReason(), is(not("")));
            assertThat(row.getIssues().size(), is(not(0)));
            assertThat(row.getIoId(), is(nullValue()));
            assertThat(row.getPublisherId(), is(nullValue()));
            assertThat(row.getSiteId(), is(nullValue()));
            assertThat(row.getSectionId(), is(nullValue()));
            assertThat(row.getSizeId(), is(nullValue()));
        }
    }

    @Test
    public void testCreateAllMediaHierarchyAllOkWithOnlyExistingElements() throws Exception {
        prepareMocksForDataValidation();

        when(placementDao.create(
                any(Placement.class),
                eq(sqlSessionMock))).thenAnswer(new Answer<Placement>() {
            @Override
            public Placement answer(InvocationOnMock invocationOnMock) throws Throwable {
                Placement placement = (Placement) invocationOnMock.getArguments()[0];
                placement.setId(Math.abs(EntityFactory.random.nextLong()));
                placements.put(placement.getId(), placement);
                return placement;
            }
        });

        when(placementDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(new Answer<Placement>() {
            @Override
            public Placement answer(InvocationOnMock invocationOnMock) throws Throwable {
                Long placementId = (Long) invocationOnMock.getArguments()[0];
                return placements.get(placementId);
            }
        });

        when(placementCostDetailDao.create(
                any(CostDetail.class),
                eq(sqlSessionMock))).thenAnswer(new Answer<CostDetail>() {
            @Override
            public CostDetail answer(InvocationOnMock invocationOnMock) throws Throwable {
                CostDetail costDetail = (CostDetail) invocationOnMock.getArguments()[0];
                costDetail.setId(Math.abs(EntityFactory.random.nextLong()));
                return costDetail;
            }
        });

        MediaBuy mediaBuy = EntityFactory.createMediaBuy();
        SqlSession dimSession = mock(SqlSession.class);
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(records);
        List <MediaRawDataView> rows = mediaHelper.doModelValidation(
                mediaHelper.doUnflatten(records), packagesMap);
        rows = mediaHelper.doPlacementDataValidation(campaignId, rows, sqlSessionMock,
                key.getUserId());
        int numberOfCreatedPlacements = mediaHelper.saveMediaHierarchy(campaignId, rows, user,
                mediaBuy,
                sqlSessionMock, dimSession);
        assertThat(numberOfCreatedPlacements, is(100));
    }
    
    @Test
    public void testDoDuplicatedValidation() {
        int quantity = 3;
        List<MediaRawDataView> rows = getMediaRawDataView(quantity);
        List<Placement> placements = new ArrayList<>();
        when(placementDao.getBySiteSectionAndSize(anyList(), anyString(), anyLong(),
                any(SqlSession.class))).thenReturn(
                placements);
        List<MediaRawDataView> result = mediaHelper.doDuplicatedValidation(campaignId, rows,
                sqlSessionMock, userId);
        assertThat(rows.size(), is(result.size()));
        for (MediaRawDataView mrdv : result) {
            assertThat(mrdv.getIssues().size(), is(0));
        }
    }
    
    @Test
    public void testDoDuplicatedValidationDuplicatedFail() {
        MediaRawDataView first = EntityFactory.createMediaRawDataView();
        MediaRawDataView second = first;
        List<MediaRawDataView> rows = new ArrayList<>();
        rows.add(first);
        rows.add(second);
        List<Placement> placements = new ArrayList<>();
        when(placementDao
                .getBySiteSectionAndSize(anyList(), anyString(), anyLong(), any(SqlSession.class)))
                .thenReturn(placements);
        List<MediaRawDataView> result = mediaHelper.doDuplicatedValidation(campaignId, rows,
                sqlSessionMock, userId);
        for (MediaRawDataView mrdv : result) {
            assertThat(mrdv.getIssues().size(), is(1));
            for (Map.Entry<String, List<ImportExportCellError>> entrySet : mrdv.getIssues().entrySet()) {
                assertThat(entrySet.getKey(), is("rowError"));
                List<ImportExportCellError> errors = entrySet.getValue();
                for (ImportExportCellError error : errors) {
                    assertThat(error.getType().name(), is("WARNING"));
                    assertThat(error.getDefaultOption(), is(notNullValue()));
                    assertThat(error.getDefaultOption(), is(InAppOption.DUPLICATE));
                    assertThat(error.getOptions().size(), is(2));
                    assertThat(error.getOptions().get(0).name(), is("DUPLICATE"));
                    assertThat(error.getOptions().get(1).name(), is("DO_NOT_IMPORT"));
                    assertThat(error.getInAppType().name(), is("DUPLICATED_PLACEMENT"));
                    assertThat(error.getCode().getNumber(), is(101));
                    assertThat(error.getMessage(), is("The placement with Site, Section and Size" +
                            " combination: " + mrdv.getSite() + " - " + mrdv.getSection() + " - " +
                            mrdv.getAdWidth() + "x" + mrdv.getAdHeight() + ", already exists. " +
                            "What would you like to do?"));
                }
            }
        }
    }

    @Test
    public void testDoDuplicatedValidationDBDuplicatedFail() {
        MediaRawDataView first = EntityFactory.createMediaRawDataView();
        List<MediaRawDataView> rows = new ArrayList<>();
        rows.add(first);
        Placement placement = new Placement();
        placement.setSiteName(first.getSite());
        placement.setSectionName(first.getSection());
        placement.setWidth(new Long(first.getAdWidth()));
        placement.setHeight(new Long(first.getAdHeight()));
        List<Placement> placements = new ArrayList<>();
        placements.add(placement);
        when(placementDao.getBySiteSectionAndSize(anyList(), anyString(), anyLong(),
                any(SqlSession.class))).thenReturn(
                placements);
        List<MediaRawDataView> result = mediaHelper.doDuplicatedValidation(campaignId, rows,
                sqlSessionMock, userId);
        for (MediaRawDataView mrdv : result) {
            assertThat(mrdv.getIssues().size(), is(1));
            for (Map.Entry<String, List<ImportExportCellError>> entrySet : mrdv.getIssues().entrySet()) {
                assertThat(entrySet.getKey(), is("rowError"));
                List<ImportExportCellError> errors = entrySet.getValue();
                for (ImportExportCellError error : errors) {
                    assertThat(error.getType().name(), is("WARNING"));
                    assertThat(error.getDefaultOption(), is(notNullValue()));
                    assertThat(error.getDefaultOption(), is(InAppOption.DUPLICATE));
                    assertThat(error.getOptions().size(), is(2));
                    assertThat(error.getOptions().get(0).name(), is("DUPLICATE"));
                    assertThat(error.getOptions().get(1).name(), is("DO_NOT_IMPORT"));
                    assertThat(error.getInAppType().name(), is("DUPLICATED_PLACEMENT"));
                    assertThat(error.getCode().getNumber(), is(101));
                    assertThat(error.getMessage(), is("The placement with Site, Section and Size" +
                            " combination: " + mrdv.getSite() + " - " + mrdv.getSection() + " - " +
                            mrdv.getAdWidth() + "x" + mrdv.getAdHeight() + ", already exists. " +
                            "What would you like to do?"));
                }
            }
        }
    }
    
    @Test
    public void testDoInAppCorrectionsDuplicate(){
        MediaRawDataView first = EntityFactory.createMediaRawDataView();
        MediaRawDataView second = EntityFactory.createMediaRawDataView();
        
        final ImportExportCellError error = new ImportExportCellError();
        error.setType(ImportIssueType.WARNING);
        error.setInAppType(InAppType.DUPLICATED_PLACEMENT);
        
        Map<String, List<ImportExportCellError>> issues = new HashMap<>();
        issues.put("rowError", new ArrayList<ImportExportCellError>(){{ add(error); }});

        first.setIssues(issues);
        
        first.setRowError("1");
        second.setRowError("2");

        List<MediaRawDataView> rows = new ArrayList<>();
        rows.add(first);
        rows.add(second);

        Action action = new Action();
        action.setInAppType(InAppType.DUPLICATED_PLACEMENT);
        action.setAction(InAppOption.DUPLICATE);
        action.setRownum(new Integer(first.getRowError()));
        
        List<Action> actions = new ArrayList<>();
        actions.add(action);

        List<MediaRawDataView> result = mediaHelper.doInAppCorrections(rows, actions);
        
        assertThat(result.size(), is(rows.size()));
        assertThat(result.get(0).getActions().size(), is(0));
    }

    @Test
    public void testDoInAppCorrectionsDoNotImport(){
        MediaRawDataView first = EntityFactory.createMediaRawDataView();
        MediaRawDataView second = EntityFactory.createMediaRawDataView();
        
        final ImportExportCellError error = new ImportExportCellError();
        error.setType(ImportIssueType.WARNING);
        error.setInAppType(InAppType.DUPLICATED_PLACEMENT);
        
        Map<String, List<ImportExportCellError>> issues = new HashMap<>();
        issues.put("rowError", new ArrayList<ImportExportCellError>() {{
            add(error);
        }});

        first.setIssues(issues);

        first.setRowError("1");
        second.setRowError("2");
        
        List<MediaRawDataView> rows = new ArrayList<>();
        rows.add(first);
        rows.add(second);
        
        Action action = new Action();
        action.setInAppType(InAppType.DUPLICATED_PLACEMENT);
        action.setAction(InAppOption.DO_NOT_IMPORT);
        action.setRownum(new Integer(first.getRowError()));
        
        List<Action> actions = new ArrayList<>();
        actions.add(action);
        
        List<MediaRawDataView> result = mediaHelper.doInAppCorrections(rows, actions);

        assertThat(result.size(), is(rows.size() - 1));
    }

    @Test
    public void doPackageDataValidationForUpdateAllPass() {
        // Prepare data
        int numberOfPackages = 2;
        int numberOfPlacementsPerPackage = 2;
        int numberOfStandalonePlacements = 0;
        int numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails,
                true);
        rows = mediaHelper.doUnflatten(rows);
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(rows);

        // Customize mock's behavior
        when(packageDao.getMediaPackagesByUserAndIds(anyList(), anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getMediaPackagesByUserAndIds(false, rows));
        when(placementDao.getMediaPlacementByPkgPlacIds(anyList(), anyLong(), eq(sqlSessionMock)))
                .thenAnswer(getMediaPlacementByPkgPlacIds(false, rows));

        // Perform test
        mediaHelper.doPackageDataValidation(1L, packagesMap, key.getUserId(), sqlSessionMock);
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            if (row.getMediaPackageId() != null && row.getPlacementId() != null) {
                assertThat(row.getIssues(), is(notNullValue()));
                assertThat(row.getIssues().size(), is(equalTo(0)));
                assertThat(row.isPackageChanged(), is(Boolean.TRUE));
                assertThat(row.isCostDetailsChanged(), is(Boolean.TRUE));
                assertThat(row.isPackageChanged(), is(Boolean.TRUE));
            }
        }
    }

    @Test
    public void doPackageDataValidationForUpdatePass() {
        // Prepare data
        int numberOfPackages = 2;
        int numberOfPlacementsPerPackage = 2;
        int numberOfStandalonePlacements = 0;
        int numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails,
                true);
        rows = mediaHelper.doUnflatten(rows);
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(rows);

        // Customize mock's behavior
        when(packageDao.getMediaPackagesByUserAndIds(anyList(), anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getMediaPackagesByUserAndIds(true, rows));
        when(placementDao.getMediaPlacementByPkgPlacIds(anyList(), anyLong(), eq(sqlSessionMock)))
                .thenAnswer(getMediaPlacementByPkgPlacIds(true, rows));

        // Perform test
        mediaHelper.doPackageDataValidation(1L, packagesMap, key.getUserId(), sqlSessionMock);
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            if (row.getMediaPackageId() != null && row.getPlacementId() != null) {
                assertThat(row.getIssues(), is(notNullValue()));
                assertThat(row.getIssues().size(), is(equalTo(0)));
                assertThat(row.isPackageChanged(), is(Boolean.FALSE));
                assertThat(row.isCostDetailsChanged(), is(Boolean.FALSE));
                assertThat(row.isPackageChanged(), is(Boolean.FALSE));
            }
        }
    }

    @Test
    public void doPackageDataValidationForUpdateWithAccessControlErrors() {
        // Prepare data
        int numberOfPackages = 2;
        int numberOfPlacementsPerPackage = 2;
        int numberOfStandalonePlacements = 0;
        int numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails,
                true);
        rows = mediaHelper.doUnflatten(rows);
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(rows);

        // Customize mock's behavior
        when(packageDao.getMediaPackagesByUserAndIds(anyList(), anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenReturn(new ArrayList());
        when(placementDao.getMediaPlacementByPkgPlacIds(anyList(), anyLong(), eq(sqlSessionMock)))
                .thenAnswer(getMediaPlacementByPkgPlacIds(false, rows));

        // Perform test
        mediaHelper.doPackageDataValidation(1L, packagesMap, key.getUserId(), sqlSessionMock);
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            if (row.getMediaPackageId() != null && row.getPlacementId() != null) {
                assertThat(row.getIssues(), is(notNullValue()));
                assertThat(row.getIssues().size(), is(equalTo(1)));
                assertThat(row.getIssues().get("rowError").size(), is(equalTo(1)));
                ImportExportCellError error = row.getIssues().get("rowError").get(0);
                assertThat(error.getField(), is(equalTo("mediaPackageId")));
                assertThat(error.getType().toString(), is(ImportIssueType.ERROR.toString()));
                assertThat(error.getMessage(), is(equalTo(
                        "The user is not allowed in this context or the requested entity does not exist")));
            }
        }
    }

    @Test
    public void doPackageDataValidationForUpdateWithPlacementIdErrors() {
        // Prepare data
        int numberOfPackages = 2;
        int numberOfPlacementsPerPackage = 2;
        int numberOfStandalonePlacements = 0;
        int numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails,
                true);
        rows = mediaHelper.doUnflatten(rows);
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(rows);

        // Customize mock's behavior
        when(packageDao.getMediaPackagesByUserAndIds(anyList(), anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getMediaPackagesByUserAndIds(false, rows));
        when(placementDao.getMediaPlacementByPkgPlacIds(anyList(), anyLong(), eq(sqlSessionMock)))
                .thenReturn(new ArrayList());

        // Perform test
        mediaHelper.doPackageDataValidation(1L, packagesMap, key.getUserId(), sqlSessionMock);
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            if (row.getMediaPackageId() != null && row.getPlacementId() != null) {
                assertThat(row.getIssues(), is(notNullValue()));
                assertThat(row.getIssues().size(), is(equalTo(1)));
                assertThat(row.getIssues().get("rowError").size(), is(equalTo(1)));
                ImportExportCellError error = row.getIssues().get("rowError").get(0);
                assertThat(error.getField(), is(equalTo("placementId")));
                assertThat(error.getType().toString(), is(ImportIssueType.ERROR.toString()));
                assertThat(error.getMessage(), is(equalTo(
                        "The placement has no relationship with the provided package.")));
            }
        }
    }

    @Test
    public void doPlacementDataValidationForUpdateAllPass() {
        // Prepare data
        int numberOfPackages = 0;
        int numberOfPlacementsPerPackage = 0;
        int numberOfStandalonePlacements = 3;
        int numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails,
                true);
        rows = mediaHelper.doUnflatten(rows);
        // Customize mock's behavior
        when(placementDao.getMediaPlacementByUserAndIds(anyList(), anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getMediaPlacementByUserAndIds(false, true, rows));

        // Perform test
        mediaHelper.doPlacementDataValidation(1L, rows, sqlSessionMock, key.getUserId());
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            assertThat(row.getIssues(), is(notNullValue()));
            assertThat(row.getIssues().size(), is(equalTo(0)));
            assertThat(row.isPlacementChanged(), is(Boolean.TRUE));
            assertThat(row.isCostDetailsChanged(), is(Boolean.TRUE));
        }
    }

    @Test
    public void doPlacementDataValidationForUpdatePass() {
        // Prepare data
        int numberOfPackages = 0;
        int numberOfPlacementsPerPackage = 0;
        int numberOfStandalonePlacements = 3;
        int numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails,
                true);
        rows = mediaHelper.doUnflatten(rows);
        // Customize mock's behavior
        when(placementDao.getMediaPlacementByUserAndIds(anyList(), anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getMediaPlacementByUserAndIds(true, true, rows));

        // Perform test
        mediaHelper.doPlacementDataValidation(1L, rows, sqlSessionMock, key.getUserId());
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            assertThat(row.getIssues(), is(notNullValue()));
            assertThat(row.getIssues().size(), is(equalTo(0)));
            assertThat(row.isPlacementChanged(), is(Boolean.FALSE));
            assertThat(row.isCostDetailsChanged(), is(Boolean.FALSE));
        }
    }

    @Test
    public void doPlacementDataValidationForUpdateWithErrorNonSttandAlonePlacement() {
        // Prepare data
        int numberOfPackages = 0;
        int numberOfPlacementsPerPackage = 0;
        int numberOfStandalonePlacements = 3;
        int numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails,
                true);
        rows = mediaHelper.doUnflatten(rows);
        // Customize mock's behavior
        when(placementDao.getMediaPlacementByUserAndIds(anyList(), anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getMediaPlacementByUserAndIds(true, false, rows));

        // Perform test
        mediaHelper.doPlacementDataValidation(1L, rows, sqlSessionMock, key.getUserId());
        for (MediaRawDataView row : rows) {
            assertThat(row.getIssues(), is(notNullValue()));
            assertThat(row.getIssues().size(), is(equalTo(1)));
            assertThat(row.getIssues().get("rowError").size(), is(equalTo(1)));
            ImportExportCellError error = row.getIssues().get("rowError").get(0);
            assertThat(error.getField(), is(equalTo("mediaPackageId")));
            assertThat(error.getType().toString(), is(ImportIssueType.ERROR.toString()));
            assertThat(error.getMessage(), is(equalTo(
                    "The provided placement belongs to a package, the package id is needed.")));
        }
    }
    
    @Test
    public void doPlacementDataValidationForUpdateWithAccessError() {
        // Prepare data
        int numberOfPackages = 0;
        int numberOfPlacementsPerPackage = 0;
        int numberOfStandalonePlacements = 3;
        int numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails,
                true);
        rows = mediaHelper.doUnflatten(rows);
        // Customize mock's behavior
        when(placementDao.getMediaPlacementByUserAndIds(anyList(), anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenReturn(new ArrayList());

        // Perform test
        mediaHelper.doPlacementDataValidation(1L, rows, sqlSessionMock, key.getUserId());
        for (MediaRawDataView row : rows) {
            assertThat(row.getIssues(), is(notNullValue()));
            assertThat(row.getIssues().size(), is(equalTo(1)));
            assertThat(row.getIssues().get("rowError").size(), is(equalTo(1)));
            ImportExportCellError error = row.getIssues().get("rowError").get(0);
            assertThat(error.getField(), is(equalTo("placementId")));
            assertThat(error.getType().toString(), is(ImportIssueType.ERROR.toString()));
            assertThat(error.getMessage(), is(equalTo(
                    "The user is not allowed in this context or the requested entity does not exist")));
        }
    }
    
    private List<MediaRawDataView> getMediaRawDataView(int quantity){
        List<MediaRawDataView> mediaRawDataViews = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            MediaRawDataView mrdv = EntityFactory.createMediaRawDataView();
            mediaRawDataViews.add(mrdv);
        }
        return mediaRawDataViews;
    }

    private void messWithData(int messCondition) {
        for (int i = 0; i < records.size(); i++) {
            if(messCondition == ALL_DATA || (messCondition == PARTIAL_DATA && i % 2 == 0)) {
                MediaRawDataView record = records.get(i);
                record.setPlacementName(null);
                record.setAdHeight(null);
                record.setAdWidth("-1");
                record.setPublisher(null);
                record.setSite(null);
                record.setSection(null);
                record.setPlannedAdSpend(null);
                record.setRate(null);
                record.setOrderName(null);
                record.setOrderNumber(null);
            }
        }
    }

    private void executeModelValidationAssertions(MediaRawDataView row) {
        assertThat(row, is(notNullValue()));
        assertThat(row.getIssues().size(), is(7));
        assertThat(row.getIssues().containsKey("site"), is(true));
        assertThat(row.getIssues().containsKey("orderNumber"), is(true));
        assertThat(row.getIssues().containsKey("orderName"), is(true));
        assertThat(row.getIssues().containsKey("adWidth"), is(true));
        assertThat(row.getIssues().containsKey("adHeight"), is(true));
        assertThat(row.getIssues().containsKey("section"), is(true));
        assertThat(row.getIssues().containsKey("publisher"), is(true));
        assertThat(row.getReason(), is("Site field is required, " +
                "Order Number field is required, Order Name field is required, " +
                "Invalid Ad Width. Values must be greater than 0, Ad Height field is required, " +
                "Site Section field is required, Publisher field is required"));
    }

    private void prepareMocksForDataValidation() {
        when(insertionOrderDao.getIosByCampaignIdAndIoNumberName(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenAnswer(new Answer<List<InsertionOrder>>() {
            @Override
            public List<InsertionOrder> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Collection<String> names = (Collection<String>) invocationOnMock.getArguments()[0];
                List<InsertionOrder> result = new ArrayList<>();
                InsertionOrder io;
                for (String name : names) {
                    io = new InsertionOrder();
                    io.setId(Math.abs(EntityFactory.random.nextLong()));
                    String[] ioParts = name.split("_@<\\-\\*\\->@_");
                    io.setIoNumber(Integer.valueOf(ioParts[0]));
                    io.setName(name);
                    result.add(io);
                }
                return result;
            }
        });

        when(publisherDao.getByCampaignIdAndName(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenAnswer(new Answer<List<Publisher>>() {
            @Override
            public List<Publisher> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Collection<String> names = (Collection<String>) invocationOnMock.getArguments()[0];
                List<Publisher> result = new ArrayList<>();
                Publisher publisher;
                for (String name : names) {
                    publisher = new Publisher();
                    publisher.setId(Math.abs(EntityFactory.random.nextLong()));
                    publisher.setName(name);
                    result.add(publisher);
                }
                return result;
            }
        });

        when(siteDao.getByCampaignIdAndPublisherNameSiteName(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenAnswer(new Answer<List<Site>>() {
            @Override
            public List<Site> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Collection<String> names = (Collection<String>) invocationOnMock.getArguments()[0];
                List<Site> result = new ArrayList<>();
                Site site;
                for (String name : names) {
                    site = new Site();
                    site.setId(Math.abs(EntityFactory.random.nextLong()));
                    site.setName(name);
                    result.add(site);
                }
                return result;
            }
        });

        when(sectionDao.getByCampaignIdAndPublisherNameSiteName(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenAnswer(new Answer<List<SiteSection>>() {
            @Override
            public List<SiteSection> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Collection<String> names = (Collection<String>) invocationOnMock.getArguments()[0];
                List<SiteSection> result = new ArrayList<>();
                SiteSection section;
                for (String name : names) {
                    section = new SiteSection();
                    section.setId(Math.abs(EntityFactory.random.nextLong()));
                    section.setName(name);
                    result.add(section);
                }
                return result;
            }
        });

        when(sizeDao.getByCampaignIdAndWidthHeight(
                anyList(),
                eq(key.getUserId()),
                eq(campaignId),
                eq(sqlSessionMock))).thenAnswer(new Answer<List<Size>>() {
            @Override
            public List<Size> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Collection<String> names = (Collection<String>) invocationOnMock.getArguments()[0];
                List<Size> result = new ArrayList<>();
                Size size;
                for (String name : names) {
                    size = new Size();
                    size.setId(Math.abs(EntityFactory.random.nextLong()));
                    size.setLabel(name);
                    result.add(size);
                }
                return result;
            }
        });
    }

    private Answer<List<MediaRawDataView>> getMediaPackagesByUserAndIds(final boolean isEqual,
                                                                        final List<MediaRawDataView> mediaRaws) {
        return new Answer<List<MediaRawDataView>>() {
            @Override
            public List<MediaRawDataView> answer(InvocationOnMock invocation) {
                List<Long> packageIds = (List<Long>) invocation.getArguments()[0];
                Long campaignId = (Long) invocation.getArguments()[1];

                List<MediaRawDataView> result = new ArrayList<>();
                for (Long pkgId : packageIds) {
                    for (MediaRawDataView row : mediaRaws) {
                        if (Long.valueOf(row.getMediaPackageId()).equals(pkgId)) {
                            MediaRawDataView view = new MediaRawDataView();
                            if (isEqual) {
                                view = row;
                            } else {
                                view.setMediaPackageId(row.getMediaPackageId());
                                view.setMediaPackageName(row.getMediaPackageName() + "-db");
                                List<CostDetailRawDataView> costs = new ArrayList<>();
                                for (CostDetailRawDataView cost : row.getCostDetails()) {
                                    Long newRate = Long.valueOf(cost.getRate()) + 5L;
                                    CostDetailRawDataView newCost =
                                            new CostDetailRawDataView(cost.getPlannedAdSpend(),
                                                    cost.getInventory(), String.valueOf(newRate),
                                                    cost.getRateType(), cost.getStartDate(),
                                                    cost.getEndDate());
                                    costs.add(newCost);
                                }
                                view.setCostDetails(costs);
                            }
                            result.add(view);
                            break;
                        }
                    }
                }
                return result;
            }
        };
    }

    private Answer<List<MediaRawDataView>> getMediaPlacementByPkgPlacIds(final boolean isEqual,
                                                                         final List<MediaRawDataView> mediaRaws) {
        return new Answer<List<MediaRawDataView>>() {
            @Override
            public List<MediaRawDataView> answer(InvocationOnMock invocation) {
                List<String> pkgPlacIds = (List<String>) invocation.getArguments()[0];
                Long campaignId = (Long) invocation.getArguments()[1];

                List<MediaRawDataView> result = new ArrayList<>();
                for (String pkgPlacId : pkgPlacIds) {
                    for (MediaRawDataView row : mediaRaws) {
                        String ids = row.getMediaPackageId() + "_" + row.getPlacementId();
                        if (pkgPlacId.equals(ids)) {
                            MediaRawDataView view = new MediaRawDataView();
                            if (isEqual) {
                                view = row;
                            } else {
                                view.setMediaPackageId(row.getMediaPackageId());
                                view.setMediaPackageName(row.getMediaPackageName());
                                view.setPlacementId(row.getPlacementId());
                                view.setPlacementName(row.getPlacementName() + "-db");
                                view.setPlacementProp1(view.getPlacementName() + "prop1-db");
                            }
                            result.add(view);
                            break;
                        }
                    }
                }
                return result;
            }
        };
    }

    private Answer<List<MediaRawDataView>> getMediaPlacementByUserAndIds(final boolean isEqual,
                                                                         final boolean isStandAlone,
                                                                         final List<MediaRawDataView> mediaRaws) {
        return new Answer<List<MediaRawDataView>>() {
            @Override
            public List<MediaRawDataView> answer(InvocationOnMock invocation) {
                List<Long> placIds = (List<Long>) invocation.getArguments()[0];
                Long campaignId = (Long) invocation.getArguments()[1];

                List<MediaRawDataView> result = new ArrayList<>();
                for (Long placId : placIds) {
                    for (MediaRawDataView row : mediaRaws) {
                        if (Long.valueOf(row.getPlacementId()).equals(placId)) {
                            MediaRawDataView view = new MediaRawDataView();
                            if (isEqual) {
                                view = row;
                            } else {
                                view.setPlacementId(row.getPlacementId());
                                view.setPlacementName(row.getPlacementName() + "-db");
                                view.setPlacementProp1(view.getPlacementName() + "prop1-db");
                                List<CostDetailRawDataView> costs = new ArrayList<>();
                                for (CostDetailRawDataView cost : row.getCostDetails()) {
                                    Long newRate = Long.valueOf(cost.getRate()) + 5L;
                                    CostDetailRawDataView newCost =
                                            new CostDetailRawDataView(cost.getPlannedAdSpend(),
                                                    cost.getInventory(), String.valueOf(newRate),
                                                    cost.getRateType(), cost.getStartDate(),
                                                    cost.getEndDate());
                                    costs.add(newCost);
                                }
                                view.setCostDetails(costs);
                            }
                            if (!isStandAlone) {
                                view.setMediaPackageId(String.valueOf(Math.abs(EntityFactory.random.nextLong())));
                            }
                            result.add(view);
                            break;
                        }
                    }
                }
                return result;
            }
        };
    }
}

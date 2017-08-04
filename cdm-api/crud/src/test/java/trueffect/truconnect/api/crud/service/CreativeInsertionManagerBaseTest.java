package trueffect.truconnect.api.crud.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.dto.BulkCreativeInsertion;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.BrandDao;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CookieDomainDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
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
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackagePlacementDaoImpl;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Superclass for {@code CreativeInsertionManager} Unit Tests
 * @author Marcelo Heredia
 */
public class CreativeInsertionManagerBaseTest extends AbstractManagerTest {

    protected static final int ALL_INSERTIONS_UNIQUE_ACCEPTED = 0;
    protected static final int INSERTIONS_DUPLICATED_REJECTED = 3;

    protected CampaignManager campaignManager;
    protected CreativeInsertionDao creativeInsertionDao;
    protected CreativeInsertionManager manager;
    protected ImportExportManager importExportManager;
    protected CreativeGroupDao creativeGroupDao;
    protected CreativeGroupCreativeDao creativeGroupCreativeDao;

    protected Campaign campaign;
    protected PlacementDao placementDao;
    protected CreativeDao creativeDao;
    protected CampaignDao campaignDao;
    protected InsertionOrderDao insertionOrderDao;
    protected CreativeInsertion creativeInsertion;
    protected Placement placement;
    protected Creative creative;

    protected SqlSession sessionBatch;

    protected static enum TypeClassPlacementOnDBToTest {
        EMPTY,
        PLACEMENT_XML,
        PLACEMENT_NON_XML
    };

    protected static enum TypeCreativesOnDBToTest {
        CREATIVES_XML,
        CREATIVES_NON_XML,
        CREATIVES_3RD,
        CREATIVES_MIXED
    };

    @Before
    public void parentSetUp() throws Exception {
        //mocks
        creativeInsertionDao = mock(CreativeInsertionDao.class);
        placementDao = mock(PlacementDao.class);
        creativeDao = mock(CreativeDao.class);
        campaignDao = mock(CampaignDao.class);
        creativeGroupDao = mock(CreativeGroupDao.class);
        creativeGroupCreativeDao = mock(CreativeGroupCreativeDao.class);
        insertionOrderDao = mock(InsertionOrderDao.class);
        sessionBatch = mock(SqlSession.class);

        //variables
        campaign = EntityFactory.createCampaign();
        creativeInsertion = EntityFactory.createCreativeInsertion();
        creativeInsertion.setCampaignId(campaign.getId());
        creativeInsertion.setId(null);

        placement = EntityFactory.createPlacement();
        placement.setId(creativeInsertion.getPlacementId());
        placement.setCampaignId(creativeInsertion.getCampaignId());
        placement.setStatus(InsertionOrderStatusEnum.ACCEPTED.getName());

        creative = EntityFactory.createCreative();
        creative.setId(creativeInsertion.getCreativeId());
        creative.setCampaignId(creativeInsertion.getCampaignId());
        creative.setWidth(placement.getWidth());
        creative.setHeight(placement.getHeight());

        manager = new CreativeInsertionManager(creativeInsertionDao, campaignDao, placementDao,
                creativeDao, creativeGroupDao, creativeGroupCreativeDao, accessControlMockito);

        PublisherDao publisherDao = mock(PublisherDao.class);
        SiteDao siteDao = mock(SiteDao.class);
        SiteSectionDao sectionDao = mock(SiteSectionDao.class);
        SizeDao sizeDao = mock(SizeDao.class);
        PlacementStatusDao placementStatusDao = mock(PlacementStatusDao.class);
        InsertionOrderStatusDao insertionOrderStatusDao = mock(InsertionOrderStatusDao.class);
        ExtendedPropertiesDao extendedPropertiesDao = mock(ExtendedPropertiesDao.class);
        CostDetailDaoExtended placementCostDetailDao = mock(CostDetailDaoExtended.class); //CostDetailType.PLACEMENT
        CostDetailDaoExtended packageCostDetailDao = mock(CostDetailDaoExtended.class); //CostDetailType.PACKAGE
        PackageDaoExtended packageDao = mock(PackageDaoExtended.class);
        PackagePlacementDaoExtended packagePlacementDao = mock(PackagePlacementDaoExtended.class);
        PackageDaoBase dimPackageDao = mock(PackageDaoBase.class);
        CostDetailDaoBase dimPackageCostDetailDao = mock(DimCostDetailDaoImpl.class); //CostDetailType.PACKAGE
        CostDetailDaoBase dimPlacementCostDetailDao = mock(CostDetailDaoBase.class); //CostDetailType.PLACEMENT
        PackagePlacementDaoBase dimPackagePlacementDao = mock(DimPackagePlacementDaoImpl.class);
        MediaBuyDao mediaBuyDao = mock(MediaBuyDao.class);
        UserDao userDao = mock(UserDao.class);

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
                        placementCostDetailDao, placementCostDetailDao, mediaBuyDao, userDao,
                        dimPackagePlacementDao, packageDao, ioManager, publisherManager,
                        siteManager, sectionManager, sizeManager, placementManager, packageManager,
                        packagePlacementManager, accessControlMockito);

        AdvertiserDao advertiserDao = mock(AdvertiserDao.class);
        BrandDao brandDao = mock(BrandDao.class);
        CookieDomainDao cookieDomainDao = mock(CookieDomainDao.class);
        
        campaignManager = new CampaignManager(campaignDao, advertiserDao, brandDao, 
                cookieDomainDao, creativeDao, accessControlMockito);
        
        // Mocks sessionBatch
        when(creativeInsertionDao.openSession(any(ExecutorType.class))).thenReturn(sessionBatch);
        doNothing().when(creativeInsertionDao).close(sessionBatch);
        
        // Mocks common behavior
        when(creativeInsertionDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(creativeInsertionDao).close(sqlSessionMock);

        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.CREATIVE_GROUP, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.CREATIVE, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.PLACEMENT, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.SITE, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.SITE_SECTION, sqlSessionMock);

        when(placementDao.get(
                eq(creativeInsertion.getPlacementId()),
                eq(sqlSessionMock))).thenReturn(placement);
        when(creativeDao.get(
                eq(creativeInsertion.getCreativeId()),
                eq(sqlSessionMock))).thenReturn(creative);
        when(creativeInsertionDao.create(
                any(CreativeInsertion.class),
                eq(sessionBatch))).thenAnswer(saveCreativeInsertion());

        when(campaignDao.get(anyLong(), any(SqlSession.class))).
                                                                       thenReturn(campaign);
    }

    private static Answer<CreativeInsertion> saveCreativeInsertion() {
        return new Answer<CreativeInsertion>() {
            public CreativeInsertion answer(InvocationOnMock invocation) {
                CreativeInsertion ci = (CreativeInsertion) invocation.getArguments()[0];
                ci.setId(Math.abs(EntityFactory.random.nextLong()));
                return ci;
            }
        };
    }

    protected List<CreativeInsertionView> prepareListCreativeInsertionView (BulkCreativeInsertion bulkCI, int counter, Long campaignId, int testType){

        List<CreativeInsertionView> result = new ArrayList<>();
        CreativeInsertionView insertion;
        for (int i = 0; i < counter; i++) {
            insertion = EntityFactory.createCreativeInsertionView();
            insertion.setCampaignId(campaignId);
            result.add(insertion);
        }

        if (testType == INSERTIONS_DUPLICATED_REJECTED && (bulkCI.getCreativeInsertions() != null && !bulkCI.getCreativeInsertions().isEmpty())) {
            List<CreativeInsertion> insertionsPayload = bulkCI.getCreativeInsertions();
            int counterD = 3;
            int counterPayload = 0;
            CreativeInsertion ci;
            CreativeInsertionView view;
            while (counterD < result.size() && counterPayload < insertionsPayload.size()) {
                ci = insertionsPayload.get(counterPayload);
                view = result.get(counterD);
                view.setCampaignId(ci.getCampaignId());
                view.setId(ci.getId());
                view.setCreativeGroupId(ci.getCreativeGroupId());
                view.setCreativeId(ci.getCreativeId());
                view.setPlacementId(ci.getPlacementId());
                view.setStartDate(ci.getStartDate());
                view.setEndDate(ci.getEndDate());
                view.setTimeZone(ci.getTimeZone());
                view.setWeight(ci.getWeight());
                view.setSequence(ci.getSequence());
                view.setPrimaryClickthrough(ci.getClickthrough());
                view.setReleased(ci.getReleased());
                view.setLogicalDelete(ci.getLogicalDelete());
                view.setCreatedTpwsKey(ci.getCreatedTpwsKey());
                view.setModifiedTpwsKey(ci.getModifiedTpwsKey());
                view.setCreatedDate(ci.getCreatedDate());
                view.setModifiedDate(ci.getModifiedDate());
                view.setCreativeInsertionRootId(ci.getCreativeInsertionRootId());
                view.setAdditionalClickthroughs(ci.getClickthroughs());
                counterD +=5;
                counterPayload+=2;
            }
        }
        return result;
    }
}

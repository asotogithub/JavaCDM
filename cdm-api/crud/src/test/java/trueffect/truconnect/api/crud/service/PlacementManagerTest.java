package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.EmailError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.PlacementStatus;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.delivery.TagEmail;
import trueffect.truconnect.api.commons.model.delivery.TagEmailResponse;
import trueffect.truconnect.api.commons.model.delivery.TagEmailSite;
import trueffect.truconnect.api.commons.model.delivery.TagEmailWrapper;
import trueffect.truconnect.api.commons.model.delivery.TagPlacement;
import trueffect.truconnect.api.commons.model.delivery.TagType;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;
import trueffect.truconnect.api.external.proxy.TagEmailProxy;
import trueffect.truconnect.api.external.proxy.TagProxy;
import trueffect.truconnect.api.external.proxy.TagTypeProxy;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.validation.FieldError;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Richard Jaldin
 */
public class PlacementManagerTest extends AbstractManagerTest {

    private static final int ONE_PLACEMENT_ACCEPTED = 0;
    private static final int ALL_PLACEMENTS_ACCEPTED = 1;
    private static final int ONE_PLACEMENT_REJECTED = 2;
    private static final int ALL_PLACEMENTS_REJECTED = 3;

    private static final int STANDALONE_PLACEMENTS = 0;
    private static final int PLACEMENTS_WITH_PACKAGES = 1;
    private static final int PLACEMENTS_MIXED = 2;

    private PlacementManager placementManager;
    private InsertionOrderManager ioManager;
    private HashMap<Long, Placement> placementsMap;
    private Placement placement;
    private RecordSet<Package> pkgs;
    private User user;

    private Map<Long, CostDetail> costDeleted;
    private Map<Long, CostDetail> costCreated;
    private Map<Long, CostDetail> costUpdated;
    private Map<Long, CostDetail> dimDeleted;
    private Map<Long, CostDetail> dimCreated;
    private Map<Long, CostDetail> dimUpdated;

    //DAOs
    private PackageDaoExtended packageDao;
    private PlacementDao placementDao;
    private CostDetailDaoExtended placementCostDetailDao;
    private CampaignDao campaignDao;
    private SiteSectionDao siteSectionDao;
    private SizeDao sizeDao;
    private PlacementStatusDao placementStatusDao;
    private UserDao userDao;
    private CreativeInsertionDao creativeInsertionDao;
    private ExtendedPropertiesDao extendedPropertiesDao;
    private InsertionOrderDao insertionOrderDao;
    private InsertionOrderStatusDao ioStatusDao;
    private CostDetailDaoExtended packageCostDetailDao;
    private Long ioId;
    private Long campaignId;
    private InsertionOrder io;
    private PackagePlacementDaoExtended packagePlacementDao;
    private List<Placement> allPlacements;

    private CostDetailDaoBase dimCostDetailDao;
    private SqlSession sqlSessionMockDim;

    @Before
    public void setUp() throws Exception {
        // Mock daos
        accessControlMockito = mock(AccessControl.class);
        placementDao = mock(PlacementDao.class);
        placementCostDetailDao = mock(CostDetailDaoExtended.class);
        campaignDao = mock(CampaignDao.class);
        siteSectionDao = mock(SiteSectionDao.class);
        sizeDao = mock(SizeDao.class);
        placementStatusDao = mock(PlacementStatusDao.class);
        userDao = mock(UserDao.class);
        extendedPropertiesDao = mock(ExtendedPropertiesDao.class);
        insertionOrderDao = mock(InsertionOrderDao.class);
        ioStatusDao = mock(InsertionOrderStatusDao.class);
        packageDao = mock(PackageDaoExtended.class);
        packageCostDetailDao = mock(CostDetailDaoExtended.class);
        packagePlacementDao = mock(PackagePlacementDaoExtended.class);
        creativeInsertionDao = mock(CreativeInsertionDaoImpl.class);
        dimCostDetailDao = mock(CostDetailDaoBase.class);
        sqlSessionMockDim = mock(SqlSession.class);

        // Variables
        placementsMap = new HashMap<>();
        placement = EntityFactory.createPlacement();
        allPlacements = new ArrayList<>();
        ioId = Math.abs(EntityFactory.random.nextLong());
        campaignId = EntityFactory.random.nextLong();
        io = EntityFactory.createInsertionOrder();
        io.setId(ioId);
        io.setCampaignId(campaignId);
        pkgs = new RecordSet<>();
        user = EntityFactory.createUser();
        user.setUserName(key.getUserId());
        costCreated = new HashMap<>();
        costUpdated = new HashMap<>();
        costDeleted = new HashMap<>();
        dimDeleted = new HashMap<>();
        dimCreated = new HashMap<>();
        dimUpdated = new HashMap<>();

        placementManager = new PlacementManager(placementDao, placementCostDetailDao,
                campaignDao, siteSectionDao, sizeDao, placementStatusDao, userDao,
                extendedPropertiesDao, insertionOrderDao, ioStatusDao, packageDao,
                packagePlacementDao, dimCostDetailDao,
                creativeInsertionDao,
                accessControlMockito);

        ioManager = new InsertionOrderManager(insertionOrderDao, ioStatusDao, userDao,
                placementDao, placementStatusDao, creativeInsertionDao, accessControlMockito);

        // mock sessions
        when(placementDao.openSession()).thenReturn(sqlSessionMock);
        when(packageDao.openSession()).thenReturn(sqlSessionMock);
        when(insertionOrderDao.openSession()).thenReturn(sqlSessionMock);

        when(dimCostDetailDao.openSession()).thenReturn(sqlSessionMockDim);

        // Mocks access control
        MockAccessControlUtil.allowAllForUser(accessControlMockito, sqlSessionMock);

        // Mocks common behavior
        when(userDao.get(eq(key.getUserId()), eq(key.getUserId()),
                eq(sqlSessionMock))).thenReturn(user);
        when(userDao.get(eq(key.getUserId()), eq(sqlSessionMock))).
                thenReturn(user);
        when(placementDao.create(any(Placement.class),
                eq(sqlSessionMock))).thenAnswer(create());
        when(placementDao.update(any(Placement.class),
                eq(sqlSessionMock))).thenAnswer(create());
        when(placementDao.get(anyLong(),
                eq(sqlSessionMock))).thenAnswer(savedPlacement());
        when(siteSectionDao.get(anyLong(),
                eq(sqlSessionMock))).thenAnswer(savedSiteSection(placement));
        when(sizeDao.getByUserAndDimensions(anyLong(), anyLong(), eq(key),
                eq(sqlSessionMock))).thenAnswer(savedSize(placement));
        when(placementStatusDao.create(any(PlacementStatus.class),
                eq(sqlSessionMock))).thenAnswer(savedPlacementStatus(placement));
        when(placementCostDetailDao.create(any(CostDetail.class),
                eq(sqlSessionMock))).thenAnswer(savedCostDetail(costCreated));

        // Mocks dim
        doAnswer(removeCostDetails(dimDeleted)).when(dimCostDetailDao).remove(any(CostDetail.class), anyString(), eq(sqlSessionMockDim));
        when(dimCostDetailDao.update(any(CostDetail.class), eq(sqlSessionMockDim))).thenAnswer(updateCostDetails(dimUpdated));
        when(dimCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMockDim))).thenAnswer(createCostDetails(dimCreated));
    }

    @Test
    public void testSaveAndGet() throws Exception {
        // set data
        String name = placement.getName();
        String sizeName = placement.getSizeName();

        // perform test
        Placement result = placementManager.create(placement, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getName(), is(name));
        assertThat(result.getSizeName(), is(sizeName));
        assertThat(result.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));
    }

    @Test
    public void testSavePlacementWithoutSiteSection() throws Exception {
        // set data
        placement.setSiteSectionId(null);
        String name = placement.getName();
        String sizeName = placement.getSizeName();

        //customize mock's behavior
        when(siteSectionDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenReturn(null);
        when(siteSectionDao.create(
                any(SiteSection.class),
                eq(sqlSessionMock))).thenAnswer(savedSiteSection(placement));

        // perform test
        Placement result = placementManager.create(placement, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getName(), is(name));
        assertThat(result.getSizeName(), is(sizeName));
        assertThat(result.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));
        assertThat(result.getSiteSectionId(), is(notNullValue()));
    }

    @Test
    public void testSavePlacementWithoutPayload() throws Exception {
        // set data
        placement = null;

        // perform test
        try {
            placementManager.create(placement, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Placement cannot be null."));
        }
    }

    @Test
    public void testSavePlacementWithoutKey() throws Exception {
        // set data
        key = null;

        // perform test
        try {
            placementManager.create(placement, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("OauthKey cannot be null."));
        }
    }

    @Test
    public void testSaveWithAllErrors() throws Exception {
        // set data
        placement.setCampaignId(null);
        placement.setSiteId(null);
        placement.setName(EntityFactory.faker.lorem().fixedString(257));
        placement.setMaxFileSize(-1L);
        placement.setInventory(-1L);
        placement.setRate(-1.0);
        placement.setAdSpend(-1.0);
        placement.setRateType("MY_OWN_RATE_TYPE");
        placement.setStatus("MY_OWN_STATUS");
        placement.setHeight(null);
        placement.setWidth(null);

        // perform test
        try {
            placementManager.create(placement, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getErrors().getFieldErrors().size(), is(8));
            FieldError error = e.getErrors().getFieldErrors().get(0);
            assertThat(error.getField(), is("siteId"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid siteId, it cannot be empty."));
            error = e.getErrors().getFieldErrors().get(1);
            assertThat(error.getField(), is("name"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid name, it supports characters up to 256."));
            error = e.getErrors().getFieldErrors().get(2);
            assertThat(error.getField(), is("maxFileSize"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid maxFileSize, it must be greater than or equals to 0."));
            error = e.getErrors().getFieldErrors().get(3);
            assertThat(error.getField(), is("rate"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid rate, it must be greater than or equals to 0.0."));
            error = e.getErrors().getFieldErrors().get(4);
            assertThat(error.getField(), is("rateType"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid rateType, it should be one of [CPM, CPC, CPA, CPL, FLT]."));
            error = e.getErrors().getFieldErrors().get(5);
            assertThat(error.getField(), is("status"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid status, it should be one of [New, Accepted, Rejected]."));
            error = e.getErrors().getFieldErrors().get(6);
            assertThat(error.getField(), is("height"));
            assertThat(error.getDefaultMessage(),
                    is("The Placement  , is missing or requires corrections to be made to the Height. Height: null . Please correct the details for the information provided."));
            error = e.getErrors().getFieldErrors().get(7);
            assertThat(error.getField(), is("width"));
            assertThat(error.getDefaultMessage(),
                    is("The Placement  , is missing or requires corrections to be made to the Width. Width: null . Please correct the details for the information provided."));
        }
    }

    @Test
    public void testSaveWithInventoryCalculationErrors() throws Exception {
        // set data
        placement.setRate(Constants.MAX_SAFE_INTEGER + 1.0);
        placement.setAdSpend(Constants.MAX_SAFE_INTEGER + 1.0);

        // perform test
        try {
            placementManager.create(placement, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getErrors().getFieldErrors().size(), is(1));
            FieldError error = e.getErrors().getFieldErrors().get(0);
            assertThat(error.getField(), is("rate"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid rate, it must be less than or equals to 9007199254740991."));
        }
    }

    @Test
    public void testSaveAdSpendInformation() throws Exception {
        // set data
        String name = placement.getName();
        String sizeName = placement.getSizeName();
        Double adSpend = 100.0;
        placement.setAdSpend(adSpend);

        // perform test
        Placement result = placementManager.create(placement, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getName(), is(name));
        assertThat(result.getSizeName(), is(sizeName));
        assertThat(result.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));
        assertThat(result.getSiteSectionId(), is(notNullValue()));
        assertThat(result.getInventory(), is(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY));
        assertThat(result.getAdSpend(), is(adSpend));
    }

    @Test
    public void testSaveInventoryForCPM() throws Exception {
        // set data
        Double adSpend = 100.0;
        placement.setAdSpend(adSpend);
        Double rate = 0.0;
        placement.setRate(rate);

        // perform test
        Placement result = placementManager.create(placement, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));
        assertThat(result.getInventory(), is(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY));
        assertThat(result.getAdSpend(), is(adSpend));
        assertThat(result.getRate(), is(rate));
        assertThat(result.getRateType(), is(RateTypeEnum.CPM.name()));
    }

    @Test
    public void testSaveInventoryForFLT() throws Exception {
        // set data
        placement.setRateType(RateTypeEnum.FLT.name());
        Double adSpend = 100.0;
        placement.setAdSpend(adSpend);
        Double rate = 0.0;
        placement.setRate(rate);

        // perform test
        Placement result = placementManager.create(placement, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));
        assertThat(result.getInventory(), is(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY));
        assertThat(result.getAdSpend(), is(adSpend));
        assertThat(result.getRate(), is(rate));
        assertThat(result.getRateType(), is(RateTypeEnum.CPM.name()));
    }

    @Test
    public void testSaveInventoryForOtherCases() throws Exception {
        // set data
        placement.setRateType(RateTypeEnum.CPC.name());
        Double adSpend = 100.0;
        placement.setAdSpend(adSpend);
        Double rate = 0.0;
        placement.setRate(rate);

        Placement result = placementManager.create(placement, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));
        assertThat(result.getInventory(), is(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY));
        assertThat(result.getAdSpend(), is(adSpend));
        assertThat(result.getRate(), is(rate));
        assertThat(result.getRateType(), is(RateTypeEnum.CPM.name()));
    }

    @Test
      public void testUpdate() throws Exception {
        // set data
        InsertionOrder io = EntityFactory.createInsertionOrder();
        io.setId(Math.abs(EntityFactory.random.nextLong()));
        String name = placement.getName();
        placement.setIoId(io.getId());

        when(insertionOrderDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(io);

        Placement placementSaved = placementManager.create(placement, key);
        assertThat(placementSaved, is(notNullValue()));
        assertThat(placementSaved.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));

        String newName = "Placement updated name";
        String newStatus = InsertionOrderStatusEnum.ACCEPTED.getName();
        placementSaved.setName(newName);
        placementSaved.setStatus(newStatus);

        //customize mock's behavior
        when(insertionOrderDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(io);
        when(placementDao.getPlacements(
                any(SearchCriteria.class),
                eq(key),
                eq(sqlSessionMock))).thenReturn(new RecordSet<>(Arrays.asList(placementSaved)));
        when(placementCostDetailDao.getAll(
                anyLong(),
                eq(sqlSessionMock))).thenReturn(placementSaved.getCostDetails());
        when(placementCostDetailDao.update(
                any(CostDetail.class),
                eq(sqlSessionMock))).thenAnswer(updateCostDetails(costUpdated));

        // perform test
        Placement result = placementManager.update(placementSaved.getId(), placementSaved, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getName(), is(newName));
        assertThat(result.getStatus(), is(newStatus));
    }

    @Test
    public void testUpdateWithAllErrors() throws Exception {
        // new data
        String name = placement.getName();

        Placement result = placementManager.create(placement, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getName(), is(name));
        assertThat(result.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));

        result.setName(EntityFactory.faker.lorem().fixedString(257));
        result.setMaxFileSize(-1L);
        result.setInventory(-1L);
        result.setRate(-1.0);
        result.setRateType("MY_OWN_RATE_TYPE");
        result.setStatus("MY_OWN_STATUS");

        // perform test
        try {
            placementManager.update(result.getId(), result, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getErrors().getFieldErrors().size(), is(5));
            FieldError error = e.getErrors().getFieldErrors().get(0);
            assertThat(error.getField(), is("name"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid name, it supports characters up to 256."));
            error = e.getErrors().getFieldErrors().get(1);
            assertThat(error.getField(), is("maxFileSize"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid maxFileSize, it must be greater than or equals to 0."));
            error = e.getErrors().getFieldErrors().get(2);
            assertThat(error.getField(), is("rate"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid rate, it must be greater than or equals to 0.0."));
            error = e.getErrors().getFieldErrors().get(3);
            assertThat(error.getField(), is("rateType"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid rateType, it should be one of [CPM, CPC, CPA, CPL, FLT]."));
            error = e.getErrors().getFieldErrors().get(4);
            assertThat(error.getField(), is("status"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid status, it should be one of [New, Accepted, Rejected]."));
        }
    }


    @Test
    public void testUpdateScheduledPlacementWithPropertyChangesShouldIgnoreChanges()
            throws Exception {
        Placement p = prepareScheduledPlacementToUpdate();
        // Perform modifications on Placement. All of them should not be applied as
        // Such placement has schedules
        String suffix = String.valueOf(System.currentTimeMillis());
        p.setName(p.getName() + suffix);
        p.setWidth(p.getWidth() + 1 );
        p.setHeight(p.getHeight() + 1 );
        // Assert results
        Placement updatedPlacement= placementManager.update(p.getId(),
                p, key);
        assertThat(updatedPlacement, is(notNullValue()));
        assertThat(updatedPlacement.getName(), is(placement.getName()));
        assertThat(updatedPlacement.getIsScheduled(), is(equalTo("Y")));
        assertThat(updatedPlacement.getWidth(), is(equalTo(placement.getWidth())));
        assertThat(updatedPlacement.getHeight(), is(equalTo(placement.getHeight())));
    }
    @Test
    public void testUpdateScheduledPlacementToInactiveShouldRemoveSchedules() throws Exception {
        Placement p = prepareScheduledPlacementToUpdate();
        // Setting to Rejected as we want Schedules to be deleted.
        p.setStatus("Rejected");

        // Assert results
        Placement updatedPlacement= placementManager.update(p.getId(),
                p, key);
        assertThat(updatedPlacement, is(notNullValue()));
        assertThat(updatedPlacement.getIsScheduled(), is(equalTo("N")));

    }

    private Placement prepareScheduledPlacementToUpdate()
            throws InvocationTargetException, IllegalAccessException {
        // Define initial Placement state
        this.placement.setId(Math.abs(EntityFactory.random.nextLong()));
        this.placement.setPackageId(null);
        InsertionOrder io = EntityFactory.createInsertionOrder();
        io.setId(Math.abs(EntityFactory.random.nextLong()));
        io.setCampaignId(this.placement.getCampaignId());
        this.placement.setIoId(io.getId());
        this.placement.setStatus("Accepted");
        // By default we define this placement HAS Schedules
        this.placement.setIsScheduled("Y");
        // Create a copy of 'placement'
        Placement p = new Placement();
        BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
        BeanUtils.copyProperties(p, this.placement);

        p.setId(Math.abs(EntityFactory.random.nextLong()));
        List<CostDetail> costDetails = getCostDetails(5, false);
        p.setCostDetails(costDetails);
        p.setPackageId(null);

        // Mock DAOs to mimic expected behavior
        when(creativeInsertionDao.bulkDeleteByFilterParam(eq(this.placement.getCampaignId()),
                any(CreativeInsertionFilterParam.class),
                eq(key.getTpws()),
                eq(sqlSessionMock))).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                CreativeInsertionFilterParam params =
                        (CreativeInsertionFilterParam) invocationOnMock.getArguments()[1];
                Placement _p = PlacementManagerTest.this.placement;
                if (params.getPlacementId().equals(_p.getId())) {
                    _p.setIsScheduled("N");
                }
                return 1;
            }
        });

        when(placementCostDetailDao.getAll(anyLong(), eq(sqlSessionMock))).thenReturn(costDetails);
        when(insertionOrderDao.get(eq(io.getId()), eq(sqlSessionMock))).thenReturn(io);
        when(placementCostDetailDao.update(
                any(CostDetail.class),
                eq(sqlSessionMock))).thenAnswer(updateCostDetails(costUpdated));
        when(placementDao.get(
                anyLong(),
                eq(sqlSessionMock))).then(new Answer<Placement>() {
            @Override
            public Placement answer(InvocationOnMock invocationOnMock) throws Throwable {
                return PlacementManagerTest.this.placement;
            }
        });
        return p;
    }

    @Test
    public void testUpdateMultipleWithAnActivePlacement() throws Exception {
        // set data
        ioId = Math.abs(EntityFactory.random.nextLong());
        RecordSet<Placement> records = createPlacements(ioId, 1);

        //customize mock's behavior
        when(placementDao.getPlacements(
                any(SearchCriteria.class),
                eq(key),
                eq(sqlSessionMock))).
                thenAnswer(savedPlacements(records, ONE_PLACEMENT_ACCEPTED));
        when(placementDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(getPlacement());
        when(insertionOrderDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(savedIO(ioId, InsertionOrderStatusEnum.ACCEPTED.getName()));
        when(packageDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(getPackageById());
        when(placementDao.checkPlacementsBelongsCampaignId(
                anyList(),
                eq(campaignId),
                eq(sqlSessionMock))).thenReturn(Boolean.TRUE);

        // perform test
        RecordSet<Placement> result = placementManager.updatePlacementStatus(ioId, records, key);

        for (Placement plac : result.getRecords()) {
            assertThat(plac.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));
        }

        InsertionOrder io = ioManager.get(ioId, key);
        assertThat(io, is(notNullValue()));
        assertThat(io.getStatus(), is(InsertionOrderStatusEnum.ACCEPTED.getName()));
    }

    @Test
    public void testUpdateMultipleWithARejectedPlacement() throws Exception {
        // set data
        ioId = Math.abs(EntityFactory.random.nextLong());
        RecordSet<Placement> records = createPlacements(ioId, 10);
        records.getRecords().get(0).setStatus(InsertionOrderStatusEnum.REJECTED.getName());

        //customize mock's behavior
        when(placementDao.getPlacements(any(SearchCriteria.class),
                eq(key), eq(sqlSessionMock))).thenAnswer(savedPlacements(records, ONE_PLACEMENT_REJECTED));
        when(placementDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(getPlacement());
        when(insertionOrderDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(savedIO(ioId, InsertionOrderStatusEnum.NEW.getName()));
        when(packageDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(getPackageById());
        when(placementDao.checkPlacementsBelongsCampaignId(anyList(), eq(campaignId),
                eq(sqlSessionMock))).thenReturn(Boolean.TRUE);
        when(userDao.get(eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(user);

        // perform test
        RecordSet<Placement> result = placementManager.updatePlacementStatus(ioId, records, key);
        for (int i = 0; i < result.getRecords().size(); i++) {
            Placement plac = result.getRecords().get(i);
            if (i == 0) {
                assertThat(plac.getStatus(), is(InsertionOrderStatusEnum.REJECTED.getName()));
            } else {
                assertThat(plac.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));
            }
        }

        InsertionOrder io = ioManager.get(ioId, key);
        assertThat(io, is(notNullValue()));
        assertThat(io.getStatus(), is(InsertionOrderStatusEnum.NEW.getName()));
    }

    @Test
    public void testUpdateMultipleWithAllActivePlacement() throws Exception {
        // set data
        ioId = Math.abs(EntityFactory.random.nextLong());
        RecordSet<Placement> records = createPlacements(ioId, 10);
        for (int i = 0; i < records.getRecords().size(); i++) {
            records.getRecords().get(i).setStatus(InsertionOrderStatusEnum.ACCEPTED.getName());
        }

        //customize mock's behavior
        when(placementDao.getPlacements(
                any(SearchCriteria.class),
                eq(key),
                eq(sqlSessionMock))).
                thenAnswer(savedPlacements(records, ALL_PLACEMENTS_ACCEPTED));
        when(placementDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(getPlacement());
        when(insertionOrderDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(savedIO(ioId, "Accepted"));
        when(packageDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(getPackageById());
        when(placementDao.checkPlacementsBelongsCampaignId(
                anyList(),
                eq(campaignId),
                eq(sqlSessionMock))).thenReturn(Boolean.TRUE);

        // perform test
        RecordSet<Placement> result = placementManager.updatePlacementStatus(ioId, records, key);
        for (int i = 0; i < result.getRecords().size(); i++) {
            Placement plac = result.getRecords().get(i);
            assertThat(plac.getStatus(), is(InsertionOrderStatusEnum.ACCEPTED.getName()));
        }

        InsertionOrder io = ioManager.get(ioId, key);
        assertThat(io, is(notNullValue()));
        assertThat(io.getStatus(), is(InsertionOrderStatusEnum.ACCEPTED.getName()));
    }

    @Test
    public void testUpdateMultipleWithAllRejectedPlacement() throws Exception {
        // set data
        ioId = Math.abs(EntityFactory.random.nextLong());
        RecordSet<Placement> records = createPlacements(ioId, 10);
        for (int i = 0; i < records.getRecords().size(); i++) {
            records.getRecords().get(i).setStatus("Rejected");
        }

        //customize mock's behavior
        when(placementDao.getPlacements(
                any(SearchCriteria.class),
                eq(key),
                eq(sqlSessionMock))).
                thenAnswer(savedPlacements(records, ALL_PLACEMENTS_REJECTED));
        when(placementDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(getPlacement());
        when(insertionOrderDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(savedIO(ioId, "Rejected"));
        when(packageDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(getPackageById());
        when(placementDao.checkPlacementsBelongsCampaignId(
                anyList(),
                eq(campaignId),
                eq(sqlSessionMock))).thenReturn(Boolean.TRUE);
        when(userDao.get(
                eq(key.getUserId()),
                eq(sqlSessionMock))).thenReturn(user);

        // perform test
        RecordSet<Placement> result = placementManager.updatePlacementStatus(ioId, records, key);
        for (int i = 0; i < result.getRecords().size(); i++) {
            Placement plac = result.getRecords().get(i);
            assertThat(plac.getStatus(), is(InsertionOrderStatusEnum.REJECTED.getName()));
        }

        InsertionOrder io = ioManager.get(ioId, key);
        assertThat(io, is(notNullValue()));
        assertThat(io.getStatus(), is(InsertionOrderStatusEnum.REJECTED.getName()));
    }

    @Test
    public void testAddPlacementsWithoutIoId() throws Exception {
        // set data
        Package pkg = EntityFactory.createPackage();
        pkg.setId(Math.abs(EntityFactory.random.nextLong()));
        RecordSet<Placement> placements = new RecordSet<>();
        placements.setRecords(createOnlyPlacements(ioId, 5, pkg));
        placements.setTotalNumberOfRecords(5);
        ioId = null;

        // perform test
        try {
            placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, placements, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("IO ID cannot be null"));
        }
    }

    @Test
    public void testAddPlacementsWithWrongAccess() throws Exception {
        // set data
        Package pkg = EntityFactory.createPackage();
        pkg.setId(Math.abs(EntityFactory.random.nextLong()));
        RecordSet<Placement> placements = new RecordSet<>();
        placements.setRecords(createOnlyPlacements(ioId, 5, pkg));
        placements.setTotalNumberOfRecords(5);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.PACKAGE,
                sqlSessionMock);
        // perform test
        try {
            placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, placements, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
            assertThat(e.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
        }
    }

    @Test
    public void testAddPlacementsWithWrongIoId() throws Exception {
        // set data
        Package pkg = EntityFactory.createPackage();
        pkg.setId(Math.abs(EntityFactory.random.nextLong()));
        RecordSet<Placement> placements = new RecordSet<>();
        placements.setRecords(createOnlyPlacements(ioId, 5, pkg));
        placements.setTotalNumberOfRecords(5);
        for (int i = 0; i < placements.getRecords().size(); i++) {
            placements.getRecords().get(i).setIoId(Math.abs(EntityFactory.random.nextLong()));
        }

        //customize mock's behavior

        when(packageDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(pkg);

        // perform test
        try {
            placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, placements, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(BusinessCode.INVALID.toString()));
            assertThat(e.getMessage(), is("Invalid entity or payload"));

        }
    }

    // Added happy path tests for
    //  1. Standalone placements
    //  2. placements with packages (non standalone)
    //  3. placements mixed (standalone/non standalone)
    @Test
    public void testAddStandalonePlacements() throws Exception {
        // set data
        Package pkg = EntityFactory.createPackage();
        pkg.setId(Math.abs(EntityFactory.random.nextLong()));
        RecordSet<Placement> placements = new RecordSet<>();
        placements.setRecords(createOnlyPlacements(ioId, 5, pkg));
        placements.setTotalNumberOfRecords(5);
        pkg.setPlacementCount(5L);

        //customize mock's behavior
        when(packageDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenReturn(pkg);
        when(placementDao.updateDataCostDetailPlacementsByPackageId(
                any(Placement.class),
                anyLong(),
                eq(sqlSessionMock))).thenReturn(10);
        when(placementDao.getPlacements(
                any(SearchCriteria.class),
                eq(key),
                eq(sqlSessionMock))).thenReturn(placements);

        // perform test
        placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, placements, key);
        for (Placement plac : pkg.getPlacements()) {
            assertThat(plac, is(notNullValue()));
        }
    }

    @Test
    public void testAddNonStandalonePlacements() throws Exception {
        //set data
        Package pkg = EntityFactory.createPackage();
        pkg.setId(Math.abs(EntityFactory.random.nextLong()));
        RecordSet<Placement> placements = new RecordSet<>();
        placements.setRecords(createOnlyPlacements(ioId, 5, pkg));
        placements.setTotalNumberOfRecords(5);
        pkg.setPlacementCount(5L);

        //customize mock's behavior
        when(packageDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenReturn(pkg);
        when(packageDao.create(
                any(Package.class),
                eq(sqlSessionMock))).thenAnswer(savePackage());
        when(packageCostDetailDao.create(
                any(CostDetail.class),
                eq(sqlSessionMock))).thenAnswer(savePackageCostDetail());
        when(insertionOrderDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenReturn(io);
        when(placementDao.updateDataCostDetailPlacementsByPackageId(
                any(Placement.class),
                anyLong(),
                eq(sqlSessionMock))).thenReturn(10);
        when(placementDao.getPlacements(
                any(SearchCriteria.class),
                eq(key),
                eq(sqlSessionMock))).thenReturn(placements);

        // perform test
        placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, placements, key);
        for (Placement plac : pkg.getPlacements()) {
            assertThat(plac, is(notNullValue()));
            assertThat(plac.getName(), is(notNullValue()));
        }
    }

    @Test
    public void testAddPlacementsIntoExistentPackage() throws Exception {
        // set data
        //create a package with placements
        RecordSet<Package> packageDTORecordSet = createPackagePlacements(5);

        //Prepare data
        //add new placements into the same package
        List<Placement> newPlacements = createOnlyPlacements(ioId, 5, packageDTORecordSet.getRecords().get(0));
        allPlacements = packageDTORecordSet.getRecords().get(0).getPlacements();
        for (Placement newPlacement : newPlacements) {
            allPlacements.add(newPlacement);
        }
        packageDTORecordSet.getRecords().get(0).setPlacements(allPlacements);

        Package existentPkg = packageDTORecordSet.getRecords().get(0);

        //customize mock's behavior
        when(packageDao.get(
                eq(existentPkg.getId()),
                eq(sqlSessionMock))).thenReturn(existentPkg);
        when(placementDao.getPlacements(
                any(SearchCriteria.class),
                eq(key),
                eq(sqlSessionMock))).thenAnswer(getExistentPlacements());
        when(placementDao.updateDataCostDetailPlacementsByPackageId(
                any(Placement.class),
                eq(existentPkg.getId()),
                eq(sqlSessionMock))).thenReturn(allPlacements.size());

        // perform test
        for (Package pkg : packageDTORecordSet.getRecords()) {
            when(placementDao.updateDataCostDetailPlacementsByPackageId(
                    any(Placement.class),
                    anyLong(),
                    eq(sqlSessionMock))).thenReturn(15);
            RecordSet<Placement> placements = new RecordSet<>();
            placements.setRecords(packageDTORecordSet.getRecords().iterator().next().getPlacements());
            placements.setTotalNumberOfRecords(packageDTORecordSet.getRecords().iterator().next().getPlacements().size());
            placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, placements, key);
            assertThat(pkg, is(notNullValue()));
            assertThat(pkg.getId(), is(notNullValue()));
            assertThat(pkg.getPlacements(), is(notNullValue()));
            assertThat(pkg.getPlacements().size(), is(equalTo(allPlacements.size())));
            Long inventory = PackagePlacementUtil.inventoryPlacement(pkg.getCostDetails().get(0).getInventory(), allPlacements.size());
            for (Placement pl : pkg.getPlacements()) {
                assertThat(pl.getInventory(), is(equalTo(inventory)));
            }
        }
    }

    @Test
    public void testAddPlacementsIntoExistentPackageWithWrongPackageAccess() throws Exception {
        // set data
        //create a package with placements
        RecordSet<Package> packageDTORecordSet = createPackagePlacements(5);
        List<Placement> newPlacements = createOnlyPlacements(ioId, 5, packageDTORecordSet.getRecords().get(0));
        allPlacements = packageDTORecordSet.getRecords().get(0).getPlacements();
        for (Placement newPlacement : newPlacements) {
            allPlacements.add(newPlacement);
        }
        packageDTORecordSet.getRecords().get(0).setPlacements(allPlacements);

        Package existentPkg = packageDTORecordSet.getRecords().get(0);
        existentPkg.getCostDetails().get(0).setRateType(RateTypeEnum.CPM.getCode());
        RecordSet<Placement> placements = new RecordSet<>();
        placements.setRecords(createOnlyPlacements(ioId, 5, existentPkg));
        placements.setTotalNumberOfRecords(5);
        existentPkg.setPlacementCount(5L);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.PACKAGE,
                sqlSessionMock);

        // perform test
        try {
            placementManager.addNewPlacementsToPackage(existentPkg.getId() + 1L, ioId, placements, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
            assertThat(e.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
        }
    }

    @Test
    public void testAddPlacementsIntoExistentPackageWithoutNewPlacements() throws Exception {
        // set data
        //create a package with placements
        Package pkg = EntityFactory.createPackage();
        pkg.setId(Math.abs(EntityFactory.random.nextLong()));
        pkg.setIoId(ioId);

        // perform test
        try {
            placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("PlacementDTO RecordSet cannot be null"));
        }
    }

    @Test
    public void testUpdateWithNewAssociations() throws Exception {
        // set data
        Package pkg = EntityFactory.createPackage();
        pkg.setId(Math.abs(EntityFactory.random.nextLong()));
        RecordSet<Placement> placements = new RecordSet<>();
        placements.setRecords(createOnlyPlacements(ioId, 5, pkg));
        placements.setTotalNumberOfRecords(5);
        pkg.setPlacementCount(5L);

        //customize mock's behavior
        when(packageDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenReturn(pkg);
        when(packageDao.create(
                any(Package.class),
                eq(sqlSessionMock))).thenAnswer(savePackage());
        when(packageCostDetailDao.create(
                any(CostDetail.class),
                eq(sqlSessionMock))).thenAnswer(savePackageCostDetail());
        when(insertionOrderDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenReturn(io);
        when(placementDao.getPlacements(
                any(SearchCriteria.class),
                eq(key),
                eq(sqlSessionMock))).thenReturn(placements);
        when(placementDao.updateDataCostDetailPlacementsByPackageId(
                any(Placement.class),
                anyLong(),
                eq(sqlSessionMock))).thenReturn(10);
        placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, placements, key);
        placements.getRecords().addAll(createOnlyPlacements(ioId, 5, pkg));
        placements.setTotalNumberOfRecords(10);
        pkg.setPlacementCount(10L);
        for (Placement plac : placements.getRecords()) {
            plac.setId(null);
        }
        when(placementDao.updateDataCostDetailPlacementsByPackageId(
                any(Placement.class),
                anyLong(),
                eq(sqlSessionMock))).thenReturn(20);
        // perform test
        placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, placements, key);
    }

    @Test
    public void testGetPlacementsByParamsWithFilterParamsOk() throws Exception {
        // set data
        int countReult = 10;
        CreativeInsertionFilterParam filterParam = prepareDataToGetPlacementsTest(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SITE);

        //customize mock's behavior
        when(placementDao.getPlacementsByFilterParam(eq(campaignId), eq(filterParam), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getPlacementsByFilterParam(countReult));
        when(placementDao.getCountPlacementsByFilterParam(eq(campaignId), eq(filterParam),
                eq(sqlSessionMock))).thenReturn(new Long(countReult));

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager.
                getPlacementsByCreativeInsertionFilterParam(campaignId, filterParam, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords(), is(notNullValue()));
    }

    @Test
    public void testGetPlacementsByParamsWithNullFilterParamsOk() throws Exception {
        // set data
        int countReult = 10;
        CreativeInsertionFilterParam filterParam = null;

        //customize mock's behavior
        when(placementDao.getPlacementsByFilterParam(eq(campaignId), eq(filterParam), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getPlacementsByFilterParam(countReult));
        when(placementDao.getCountPlacementsByFilterParam(eq(campaignId), eq(filterParam),
                eq(sqlSessionMock))).thenReturn(new Long(countReult));

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager.
                getPlacementsByCreativeInsertionFilterParam(campaignId, filterParam, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords(), is(notNullValue()));
    }

    @Test
    public void testGetPlacementsByParamsWithNullCampaignId() throws Exception {
        // set data
        campaignId = null;
        CreativeInsertionFilterParam filterParam = prepareDataToGetPlacementsTest(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SITE);

        // Perform test
        try {
            placementManager.getPlacementsByCreativeInsertionFilterParam(campaignId, filterParam, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Campaign Id cannot be null."));
        }
    }

    @Test
    public void testGetPlacementsByParamsWithWrongPayload() throws Exception {
        // set data
        CreativeInsertionFilterParam filterParam = prepareDataToGetPlacementsTest(
                CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        filterParam.setSiteId(null);

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager.
                getPlacementsByCreativeInsertionFilterParam(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is("Invalid siteId, it cannot be empty."));
    }

    @Test
    public void testGetPlacementsByParamsWithWrongPayloadIds() throws Exception {
        // set data
        CreativeInsertionFilterParam filterParam = prepareDataToGetPlacementsTest(
                CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        filterParam.setSiteId(null);
        filterParam.setGroupId(null);

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager.
                getPlacementsByCreativeInsertionFilterParam(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(2));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is("Invalid groupId, it cannot be empty."));
        error = result.error().getErrors().get(1);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is("Invalid siteId, it cannot be empty."));
    }

    @Test
    public void testGetPlacementsByParamsWithWrongAccessToCampaign() throws Exception {
        // set data
        CreativeInsertionFilterParam filterParam = prepareDataToGetPlacementsTest(
                CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN,
                Collections.singletonList(campaignId), sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager.
                getPlacementsByCreativeInsertionFilterParam(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void testGetPlacementsByParamsWithWrongAccessToPayloadIds() throws Exception {
        // set data
        CreativeInsertionFilterParam filterParam = prepareDataToGetPlacementsTest(
                CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.SITE,
                Collections.singletonList(filterParam.getSiteId()), sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager.
                getPlacementsByCreativeInsertionFilterParam(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void createPlacementOk() throws Exception {
        // set data
        int totalCostDetails = 5;
        placement = EntityFactory.createPlacement();
        List<CostDetail> costDetails = getCostDetails(totalCostDetails, false);
        placement.setCostDetails(costDetails);
        List<CostDetail> existentCosts = new ArrayList<>();

        //customize mock's behavior
        when(placementCostDetailDao.getAll(anyLong(), eq(sqlSessionMock))).thenReturn(existentCosts);
        when(placementCostDetailDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(getCostDetailById());
        doAnswer(removeCostDetails(costDeleted)).when(placementCostDetailDao).remove(any(CostDetail.class), anyString(), eq(sqlSessionMock));

        // Perform test
        Placement result = placementManager.create(placement, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getCostDetails(), is(notNullValue()));
        assertThat(result.getCostDetails().size(), is(equalTo(2)));
        assertThat(costCreated, is(notNullValue()));
        assertThat(costCreated.size(), is(equalTo(2)));
        assertThat(costUpdated, is(notNullValue()));
        assertThat(costUpdated.size(), is(equalTo(0)));
        assertThat(costDeleted, is(notNullValue()));
        assertThat(costDeleted.size(), is(equalTo(0)));

        assertThat(dimCreated, is(notNullValue()));
        assertThat(dimCreated.size(), is(equalTo(2)));
        assertThat(dimUpdated, is(notNullValue()));
        assertThat(dimUpdated.size(), is(equalTo(0)));
        assertThat(dimDeleted, is(notNullValue()));
        assertThat(dimDeleted.size(), is(equalTo(0)));
    }

    @Test
    public void updatePlacementOk() throws Exception {
        // set data
        int totalCostDetails = 5;
        List<CostDetail> costDetails = getCostDetails(totalCostDetails, true);
        placement.setCostDetails(costDetails);
        placement.setIoId(io.getId());
        List<CostDetail> existentCosts = costDetails;

        placement.setId(Math.abs(EntityFactory.random.nextLong()));
        placement.setName(placement.getName() + "updated");

        //customize mock's behavior
        when(insertionOrderDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(io);
        when(placementDao.getPlacementsByIoId(anyLong(), any(InsertionOrderStatusEnum.class), eq(sqlSessionMock))).
                                                                                                                          thenReturn(
                                                                                                                                  Arrays.asList(placement));
        when(placementCostDetailDao.getAll(anyLong(), eq(sqlSessionMock))).thenReturn(existentCosts);
        when(placementCostDetailDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(getCostDetailById());
        when(placementCostDetailDao.update(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(updateCostDetails(costUpdated));
        doAnswer(removeCostDetails(costDeleted)).when(placementCostDetailDao).remove(any(CostDetail.class), anyString(), eq(sqlSessionMock));

        // Perform test
        Placement result = placementManager.update(placement.getId(), placement, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getCostDetails(), is(notNullValue()));
        assertThat(result.getCostDetails().size(), is(equalTo(totalCostDetails)));
        assertThat(costUpdated, is(notNullValue()));
        assertThat(costUpdated.size(), is(equalTo(totalCostDetails)));
        assertThat(costCreated, is(notNullValue()));
        assertThat(costCreated.size(), is(equalTo(0)));
        assertThat(costDeleted, is(notNullValue()));
        assertThat(costDeleted.size(), is(equalTo(0)));

        assertThat(dimUpdated, is(notNullValue()));
        assertThat(dimUpdated.size(), is(equalTo(totalCostDetails)));
        assertThat(dimCreated, is(notNullValue()));
        assertThat(dimCreated.size(), is(equalTo(0)));
        assertThat(dimDeleted, is(notNullValue()));
        assertThat(dimDeleted.size(), is(equalTo(0)));
    }

    @Test
    public void getPlacementsByPlacementFilterParamOk() {
        // prepare data
        int totalRecords = 5;
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = EntityFactory.createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);

        // customize mock's behavior
        when(placementDao.getPlacementsViewByLevelType(eq(agencyId), eq(advertiserId), eq(brandId),
                eq(filterParam), any(PlacementFilterParamLevelTypeEnum.class), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(
                getPlacementViewByPlacementFilterParam(totalRecords));
        when(placementDao.getCountPlacementsViewByLevelType(eq(agencyId), eq(advertiserId),
                eq(brandId), eq(filterParam), any(PlacementFilterParamLevelTypeEnum.class),
                eq(sqlSessionMock))).thenReturn(Long.valueOf(totalRecords));

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .getPlacementsByPlacementFilterParam(agencyId, advertiserId, brandId, filterParam,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(totalRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(totalRecords)));
    }

    @Test
    public void getPlacementsByPlacementFilterParamWithNullParameters() {
        // prepare data
        Long agencyId = null;
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = EntityFactory.createPlacementFilterParam(
                PlacementFilterParamLevelTypeEnum.PLACEMENT);

        // Perform test
        try {
            placementManager.getPlacementsByPlacementFilterParam(agencyId, advertiserId, brandId,
                    filterParam, null, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(String.format("%s cannot be null.", "Agency Id")));
        }
    }

    @Test
    public void getPlacementsByPlacementFilterParamWithNullFilterParam() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = null;

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .getPlacementsByPlacementFilterParam(agencyId, advertiserId, brandId, filterParam,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error, instanceOf(ValidationError.class));
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(String.format("%s cannot be null.", "Filter Param")));
    }

    @Test
    public void getPlacementsByPlacementFilterParamWithWrongPayload() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = EntityFactory
                .createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);
        filterParam.setCampaignId(null);
        filterParam.setSectionId(null);

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .getPlacementsByPlacementFilterParam(agencyId, advertiserId, brandId, filterParam,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(2)));
        Error error = result.error().getErrors().get(0);
        assertThat(error, instanceOf(ValidationError.class));
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(),
                is(String.format("Invalid %s, it cannot be empty.", "campaignId")));
        error = result.error().getErrors().get(1);
        assertThat(error, instanceOf(ValidationError.class));
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(),
                is(String.format("Invalid %s, it cannot be empty.", "sectionId")));
    }

    @Test
    public void getPlacementsByPlacementFilterParamWithWrongAccessForAgency() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = EntityFactory
                .createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.AGENCY,
                sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .getPlacementsByPlacementFilterParam(agencyId, advertiserId, brandId, filterParam,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error, instanceOf(AccessError.class));
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void getPlacementsByPlacementFilterParamWithWrongAccessForFilterParamIds() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = EntityFactory
                .createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);

        // customize mock's behavior
        MockAccessControlUtil
                .disallowAccessForUser(accessControlMockito, AccessStatement.ADVERTISER,
                        sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .getPlacementsByPlacementFilterParam(agencyId, advertiserId, brandId, filterParam,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error, instanceOf(AccessError.class));
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void searchPlacementsByPatternPass() {
        // prepare data
        int totalRecords = 5;
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        String pattern = EntityFactory.faker.letterify("?????");
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();
        searchOptions.setCampaign(true);
        searchOptions.setSite(true);
        searchOptions.setSection(true);
        searchOptions.setPlacement(true);
        List<PlacementView> list =
                EntityFactory.createPlacementViewListForCampaign(campaignId, totalRecords);

        // customize mock's behavior
        when(placementDao.searchPlacementsViewByPattern(eq(agencyId), eq(advertiserId), eq(brandId),
                anyString(), any(PlacementSearchOptions.class), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenReturn(list);
        when(placementDao
                .searchCountPlacementsViewByPattern(eq(agencyId), eq(advertiserId), eq(brandId),
                        anyString(), any(PlacementSearchOptions.class), eq(sqlSessionMock)))
                .thenReturn(Long.valueOf(list.size()));

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .searchPlacementsByPattern(agencyId, advertiserId, brandId, pattern, searchOptions,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(totalRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(totalRecords)));
    }

    @Test
    public void searchPlacementsFailedDueNullPathParam() {
        // prepare data
        Long agencyId = null;
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        String pattern = EntityFactory.faker.letterify("?????");
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();

        // Perform test
        try {
            placementManager.searchPlacementsByPattern(agencyId, advertiserId, brandId, pattern,
                    searchOptions, null, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(String.format("%s cannot be null.", "Agency Id")));
        }
    }

    @Test
    public void searchPlacementsFailedDueNullQueryParams() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = null;
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        String pattern = EntityFactory.faker.letterify("?????");
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .searchPlacementsByPattern(agencyId, advertiserId, brandId, pattern, searchOptions,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("%s cannot be null.", "Advertiser Id"))));
    }

    @Test
    public void searchPlacementsFailedDueNullPattern() {
        // prepare data
        // pattern --> null
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        String pattern = null;
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .searchPlacementsByPattern(agencyId, advertiserId, brandId, pattern, searchOptions,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("%s cannot be null.", "Pattern"))));

        // pattern --> empty
        pattern = "";

        // Perform test
        result = placementManager
                .searchPlacementsByPattern(agencyId, advertiserId, brandId, pattern, searchOptions,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("%s cannot be null.", "Pattern"))));

        // pattern --> blank spaces
        pattern = "     ";

        // Perform test
        result = placementManager
                .searchPlacementsByPattern(agencyId, advertiserId, brandId, pattern, searchOptions,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("%s cannot be null.", "Pattern"))));
    }

    @Test
    public void searchPlacementsFailedDuePatternLenght() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        String pattern =
                EntityFactory.faker.lorem().fixedString(Constants.SEARCH_PATTERN_MAX_LENGTH + 1);
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .searchPlacementsByPattern(agencyId, advertiserId, brandId, pattern, searchOptions,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %s, it supports characters up to %s.", "Pattern",
                        Constants.SEARCH_PATTERN_MAX_LENGTH))));
    }

    @Test
    public void searchPlacementsFailedDueWrongAccessForAgency() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        Long advertiserId = Math.abs(EntityFactory.random.nextLong());
        Long brandId = Math.abs(EntityFactory.random.nextLong());
        String pattern = EntityFactory.faker.letterify("?????");
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.AGENCY,
                sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .searchPlacementsByPattern(agencyId, advertiserId, brandId, pattern, searchOptions,
                        null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat(error, instanceOf(AccessError.class));
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void testGetPlacementByAdvertiserAndBrandNullBrand() {
        try {
            placementManager.getPlacementsByBrand(null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "brand Id"))));
        }
    }

    @Test
    public void testGetPlacementByAdvertiserAndBrand() {
        PlacementView plctByAdvAndBrand = new PlacementView();
        plctByAdvAndBrand.setCampaignId(new Long(6395485));
        plctByAdvAndBrand.setCampaignName(new String("Frank campaign 2"));
        plctByAdvAndBrand.setIsTrafficked(0L);
        plctByAdvAndBrand.setId(new Long("7249593"));
        plctByAdvAndBrand.setName(new String("Danny P 7"));
        plctByAdvAndBrand.setSiteSectionName(new String("ffff"));
        plctByAdvAndBrand.setSiteId(new Long("6281267"));
        plctByAdvAndBrand.setSiteName(new String("aaaaa"));
        plctByAdvAndBrand.setSiteSectionId(new Long("6288725"));
        plctByAdvAndBrand.setSizeName(new String("100x100"));
        plctByAdvAndBrand.setStatus(new String("New"));
        List<PlacementView> plcmtList = new ArrayList<PlacementView>();
        plcmtList.add(plctByAdvAndBrand);
        // customize mock's behavior
        when(placementDao.getPlacementsByBrand(any(Long.class),any(SqlSession.class))).thenReturn(plcmtList);
        // perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager.getPlacementsByBrand(4564231L , key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(1));
        assertThat(result.success().getRecords().get(0), is(equalTo(plctByAdvAndBrand)));
    }

    @Test
    public void testGetPlacementByAdvertiserAndBrandAccessValidationBrand() {
        // customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.BRAND), anyList(),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);
        // perform test
        Either<Errors, RecordSet<PlacementView>> result =
                placementManager.getPlacementsByBrand(4564231L , key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error, instanceOf(AccessError.class));
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }
    
    @Test
    public void testSendTagEmailsSuccessful() throws ProxyException {
        List<Placement> plcmList = new ArrayList();
        Integer plcInt = EntityFactory.random.nextInt();
        Placement plcm = new Placement();
        plcm.setId(plcInt.longValue());
        plcm.setIsTrafficked(1L);
        plcm.setStatus("Accepted");
        plcmList.add(plcm);

        TagEmailProxy proxy = mock(TagEmailProxy.class);
        TagEmailWrapper response = mock(TagEmailWrapper.class);

        when(response.isIsSuccess()).thenReturn(Boolean.TRUE);
        when(response.getMessage()).thenReturn(StringUtils.EMPTY);
        when(proxy.post(any(TagEmailWrapper.class))).thenReturn(response);
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock)))
                .thenReturn(plcmList);

        List<String> emailList = new ArrayList<>();
        emailList.add(EntityFactory.createEmailAddress());
        emailList.add(EntityFactory.createEmailAddress());

        List<Integer> integerList = new ArrayList<>();
        integerList.add(EntityFactory.random.nextInt());
        integerList.add(EntityFactory.random.nextInt());

        TagEmailSite tagEmailSite = new TagEmailSite();
        tagEmailSite.setFileType("txt");
        tagEmailSite.setSiteId(EntityFactory.random.nextInt());
        tagEmailSite.setPlacementIds(integerList);
        tagEmailSite.setRecipients(emailList);

        TagEmail tagEmail = new TagEmail();
        tagEmail.setUserEmail(EntityFactory.createEmailAddress());
        tagEmail.setToEmails(emailList);
        tagEmail.setTagEmailSites(Arrays.asList(tagEmailSite));

        Either<Errors, TagEmailResponse> result =
                placementManager.sendTagEmails(tagEmail, proxy, key);

        assertThat(result, is(notNullValue()));
        assertThat("message: "+ result.error(), result.success(), is(notNullValue()));
        assertThat(result.success().getTagEmailSiteResponses(), is(notNullValue()));
        assertThat(result.success().getTagEmailSiteResponses().size(), is(1));
        assertThat(result.success().getTagEmailSiteResponses().get(0), is(notNullValue()));
        assertThat(result.success().getTagEmailSiteResponses().get(0).getSuccess(),
                is(Boolean.TRUE));
    }
    
   public void testSendTagEmailsError() throws ProxyException {
       List<Placement> plcmList = new ArrayList();
       Integer plcInt = EntityFactory.random.nextInt();
       Placement plcm = new Placement();
       plcm.setId(plcInt.longValue());
       plcm.setIsTrafficked(1L);
       plcmList.add(plcm);
       when(placementDao.getPlacementsByIds(anyCollection(),eq(sqlSessionMock))).thenReturn(plcmList);
        TagEmailProxy proxy = mock(TagEmailProxy.class);
        TagEmailWrapper response = mock(TagEmailWrapper.class);
        
        when(response.isIsSuccess()).thenReturn(Boolean.FALSE);
        when(response.getMessage()).thenReturn(EntityFactory.faker.letterify("?????"));
        when(proxy.post(any(TagEmailWrapper.class))).thenReturn(response);
        
        List<String> emailList = new ArrayList<>();
        emailList.add(EntityFactory.faker.internet().emailAddress());
        emailList.add(EntityFactory.faker.internet().emailAddress());
        
        List<Integer> integerList = new ArrayList<>();
        integerList.add(EntityFactory.random.nextInt());
        integerList.add(EntityFactory.random.nextInt());
        
        TagEmailSite tagEmailSite = new TagEmailSite();
        tagEmailSite.setFileType("txt");
        tagEmailSite.setSiteId(EntityFactory.random.nextInt());
        tagEmailSite.setPlacementIds(integerList);
        tagEmailSite.setRecipients(emailList);
        
        TagEmail tagEmail = new TagEmail();
        tagEmail.setUserEmail(EntityFactory.faker.internet().emailAddress());
        tagEmail.setToEmails(emailList);
        tagEmail.setTagEmailSites(Arrays.asList(tagEmailSite));
        
        Either<Errors, TagEmailResponse> result = 
                placementManager.sendTagEmails(tagEmail, proxy, key);
        
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getMessage(), is(response.getMessage()));
        
        List<String> allEmails = new ArrayList<>();
        allEmails.addAll(tagEmail.getToEmails());
        allEmails.addAll(tagEmailSite.getRecipients());
        
        assertThat(((EmailError)error).getEmailList(), is(StringUtils.join(allEmails.toArray(), ",")));
    }
    
    @Test
    public void testGetAdTagPlacement() throws Exception {
        TagProxy tagProxy = mock(TagProxy.class);
        TagTypeProxy tagTypeProxy = mock(TagTypeProxy.class);
        
        Placement placementOne = EntityFactory.createPlacement();
        InsertionOrder ioOne = EntityFactory.createInsertionOrder();
        Campaign campaignOne = EntityFactory.createCampaign();
        
        ioOne.setId(EntityFactory.random.nextLong());
        placementOne.setId(EntityFactory.random.nextLong());
        placementOne.setIoId(ioOne.getId());
        placementOne.setCampaignId(campaignOne.getId());
        placementOne.setInventory(EntityFactory.random.nextLong());
        
        TagType tagTypeOne = new TagType();
        tagTypeOne.setTagId(EntityFactory.random.nextLong());
        tagTypeOne.setName("IFRAME");
        
        TagType tagTypeTwo = new TagType();
        tagTypeTwo.setTagId(EntityFactory.random.nextLong());
        tagTypeTwo.setName("SCRIPT");
        
        TagType tagTypeThree = new TagType();
        tagTypeThree.setTagId(EntityFactory.random.nextLong());
        tagTypeThree.setName("CLICK_URL");
        
        RecordSet<TagType> tagTypes = new RecordSet<>(
                Arrays.asList(tagTypeOne, tagTypeTwo, tagTypeThree));
        
        String generatedTag = "<SCRIPT SRC=\"SOME_URL\"></SCRIPT>\n" +
                    "<NOSCRIPT>\n" +
                    "  <A HREF=\"SOME_URL\" TARGET=\"_blank\">\n" +
                    "  <IMG SRC=\"SOME_URL\" WIDTH=88 HEIGHT=31 ALT=\"Click Here\" BORDER=0>\n" +
                    "  </A>\n" +
                    "</NOSCRIPT>";
        
        when(tagProxy.getTagString(anyInt(), anyInt())).thenReturn(generatedTag);
        when(tagTypeProxy.getTagTypes(anyInt(), anyInt())).thenReturn(tagTypes);
        when(placementDao.get(eq(placementOne.getId()), eq(sqlSessionMock))).thenReturn(placementOne);
        when(insertionOrderDao.get(eq(ioOne.getId()), eq(sqlSessionMock))).thenReturn(ioOne);
        when(campaignDao.get(eq(campaignOne.getId()), eq(sqlSessionMock))).thenReturn(campaignOne);
        
        Either<Errors, TagPlacement> result = 
                placementManager.getAdTagPlacement(placementOne.getId(), tagProxy, tagTypeProxy, key);
        
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        TagPlacement tagPlacement = result.success();
        assertThat(tagPlacement.getIoNumber(), is(ioOne.getId().toString()));
        assertThat(tagPlacement.getIoDescription(), is(ioOne.getName()));
        assertThat(tagPlacement.getCampaignName(), is(campaignOne.getName()));
        assertThat(tagPlacement.getStartDate(), is(placementOne.getStartDate()));
        assertThat(tagPlacement.getEndDate(), is(placementOne.getEndDate()));
        assertThat(tagPlacement.getImpressions(), is(placementOne.getInventory().intValue()));
        assertThat(tagPlacement.getFullAdTag(), is(notNullValue()));
        assertThat(tagPlacement.getScriptVersion(), is(notNullValue()));
        assertThat(tagPlacement.getNoScriptVersion(), is(notNullValue()));
        assertThat(tagPlacement.getClickRedirect(), is(notNullValue()));
    }
    
    @Test
    public void testGetAdTagPlacementFail() throws Exception {
        TagProxy tagProxy = mock(TagProxy.class);
        TagTypeProxy tagTypeProxy = mock(TagTypeProxy.class);
        
        Either<Errors, TagPlacement> result =
                placementManager.getAdTagPlacement(EntityFactory.random.nextLong(), tagProxy, tagTypeProxy, key);
        
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
    }

    /**
     * Tests for getPlacementsByTagId HtmlInjectionTags
     */
    @Test
    public void getPlacementsByTagIdShouldSucceed() {
        when(placementDao
                .getPlacementsByTagId(anyLong(), anyLong(), anyLong(), any(SqlSession.class)))
                .thenReturn(
                        EntityFactory.createPlacementViewListForCampaign(1L, 2));
        when(placementDao.getCountPlacementsByTagId(anyLong(), any(SqlSession.class))).thenReturn(
                2L);

        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .getPlacementsAssociatedInjectionTag(1L, 1L, 100L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void getPlacementsByTagIdShouldFailNullId() {
        try {
            placementManager.getPlacementsAssociatedInjectionTag(null, 1L, 100L, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Html Injection Tag Id"))));
        }
    }

    @Test
    public void getPlacementsByTagIdShouldFailDueAccessPermission() {
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.HTML_INJECTION, sqlSessionMock);

        Either<Errors, RecordSet<PlacementView>> result =
                placementManager.getPlacementsAssociatedInjectionTag(1L, 1L, 100L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void getPlacementsByTagIdShouldFailDueWrongPaginatorValues() {
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .getPlacementsAssociatedInjectionTag(1L, null,
                        new Long(SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE + 1), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    /**
     * Tests for searchPlacementsAssociatedTagByPattern
     */
    @Test
    public void searchPlacementsAssociatedInjectionTagByPatternPass() {
        // prepare data
        int totalRecords = 5;
        Long htmlInjectionTagId = Math.abs(EntityFactory.random.nextLong());
        String pattern = EntityFactory.faker.letterify("?????");
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();
        searchOptions.setCampaign(true);
        searchOptions.setSite(true);
        searchOptions.setPlacement(true);
        List<PlacementView> list = EntityFactory.createPlacementViewListForCampaign(campaignId,
                totalRecords);

        // customize mock's behavior
        when(placementDao.searchPlacementsAssociatedTagByPattern(eq(htmlInjectionTagId),
                anyString(), any(PlacementSearchOptions.class), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenReturn(list);
        when(placementDao.getCountSearchPlacementsAssociatedTagByPattern(eq(htmlInjectionTagId),
                anyString(), any(PlacementSearchOptions.class), eq(sqlSessionMock))).thenReturn(
                Long.valueOf(list.size()));

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .searchPlacementsAssociatedInjectionTagByPattern(htmlInjectionTagId, pattern,
                        searchOptions, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(totalRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(totalRecords)));
    }

    @Test
    public void searchPlacementsAssociatedInjectionTagByPatternFailedDueNullPathParam() {
        // prepare data
        Long htmlInjectionTagId = null;
        String pattern = EntityFactory.faker.letterify("?????");
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();

        // Perform test
        try {
            placementManager.searchPlacementsAssociatedInjectionTagByPattern(htmlInjectionTagId,
                    pattern, searchOptions, null, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(String.format("%s cannot be null.", "Html Injection Tag Id")));
        }
    }

    @Test
    public void searchPlacementsAssociatedInjectionTagByPatternFailedDueNullPattern() {
        // prepare data
        // pattern --> null
        Long htmlInjectionTagId = Math.abs(EntityFactory.random.nextLong());
        String pattern = null;
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .searchPlacementsAssociatedInjectionTagByPattern(htmlInjectionTagId, pattern,
                        searchOptions, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("%s cannot be null.", "Pattern"))));

        // pattern --> empty
        pattern = "";

        // Perform test
        result = placementManager.searchPlacementsAssociatedInjectionTagByPattern(
                htmlInjectionTagId, pattern, searchOptions, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("%s cannot be null.", "Pattern"))));

        // pattern --> blank spaces
        pattern = "     ";

        // Perform test
        result = placementManager.searchPlacementsAssociatedInjectionTagByPattern(
                htmlInjectionTagId, pattern, searchOptions, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("%s cannot be null.", "Pattern"))));

        // pattern --> blank spaces
        pattern = EntityFactory.faker.lorem().fixedString(Constants.SEARCH_PATTERN_MAX_LENGTH + 1);

        // Perform test
        result = placementManager.searchPlacementsAssociatedInjectionTagByPattern(
                htmlInjectionTagId, pattern, searchOptions, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %s, it supports characters up to %s.", "Pattern",
                        Constants.SEARCH_PATTERN_MAX_LENGTH))));
    }

    @Test
    public void searchPlacementsAssociatedInjectionTagByPatternFailedDueSearchOptions() {
        // prepare data
        // searchOptions --> null
        Long htmlInjectionTagId = Math.abs(EntityFactory.random.nextLong());
        String pattern = EntityFactory.faker.letterify("?????");
        PlacementSearchOptions searchOptions = null;

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .searchPlacementsAssociatedInjectionTagByPattern(htmlInjectionTagId, pattern,
                        searchOptions, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("%s cannot be null.", "Search Options"))));

        // searchOptions --> soSection true
        searchOptions = new PlacementSearchOptions();
        searchOptions.setSection(true);

        // Perform test
        result = placementManager.searchPlacementsAssociatedInjectionTagByPattern(
                htmlInjectionTagId, pattern, searchOptions, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %s value.", "soSection"))));
    }

    @Test
    public void searchPlacementsAssociatedInjectionTagByPatternFailedDueWrongAccessForId() {
        // prepare data
        Long htmlInjectionTagId = Math.abs(EntityFactory.random.nextLong());
        String pattern = EntityFactory.faker.letterify("?????");
        PlacementSearchOptions searchOptions = new PlacementSearchOptions();
        searchOptions.setCampaign(true);
        searchOptions.setSite(true);
        searchOptions.setPlacement(true);

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.HTML_INJECTION, sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<PlacementView>> result = placementManager
                .searchPlacementsAssociatedInjectionTagByPattern(htmlInjectionTagId, pattern,
                        searchOptions, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat(error, instanceOf(AccessError.class));
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void testPlacementNotTrafficked() throws ProxyException {
        List<Placement> plcmList = new ArrayList();
        Integer plcInt = EntityFactory.random.nextInt();
        Placement plcm = new Placement();
        plcm.setId(plcInt.longValue());
        plcm.setIsTrafficked(0L);
        plcm.setStatus("Accepted");
        plcmList.add(plcm);
        when(placementDao.getPlacementsByIds(anyCollection(),eq(sqlSessionMock))).thenReturn(plcmList);

        TagEmailProxy proxy = mock(TagEmailProxy.class);
        TagEmailWrapper response = mock(TagEmailWrapper.class);

        List<String> emailList = new ArrayList<>();
        emailList.add("test@trueforce.com");

        List<Integer> integerList = new ArrayList<>();
        integerList.add(plcInt);

        TagEmailSite tagEmailSite = new TagEmailSite();
        tagEmailSite.setFileType("txt");
        tagEmailSite.setSiteId(EntityFactory.random.nextInt());
        tagEmailSite.setPlacementIds(integerList);
        tagEmailSite.setRecipients(emailList);

        TagEmail tagEmail = new TagEmail();
        tagEmail.setUserEmail("user@email.com");
        tagEmail.setToEmails(emailList);
        tagEmail.setTagEmailSites(Arrays.asList(tagEmailSite));

        Either<Errors, TagEmailResponse> result =
                placementManager.sendTagEmails(tagEmail, proxy, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getMessage(),
                is(String.format("Placement %s is not trafficked", String.valueOf(plcInt))));
    }

    @Test
    public void testInvalidSite() throws ProxyException {
        List<Placement> plcmList = new ArrayList();
        Integer plcInt = EntityFactory.random.nextInt();
        Placement plcm = new Placement();
        plcm.setId(plcInt.longValue());
        plcm.setIsTrafficked(1L);
        plcmList.add(plcm);
        when(placementDao.getPlacementsByIds(anyCollection(),eq(sqlSessionMock))).thenReturn(plcmList);

        TagEmailProxy proxy = mock(TagEmailProxy.class);
        TagEmailWrapper response = mock(TagEmailWrapper.class);

        List<String> emailList = new ArrayList<>();
        emailList.add("test@trueforce.com");

        List<Integer> integerList = new ArrayList<>();
        integerList.add(plcInt);

        TagEmailSite tagEmailSite = new TagEmailSite();
        tagEmailSite.setFileType("txt");
        tagEmailSite.setSiteId(null);
        tagEmailSite.setPlacementIds(integerList);
        tagEmailSite.setRecipients(emailList);

        TagEmail tagEmail = new TagEmail();
        tagEmail.setUserEmail("user@email.com");
        tagEmail.setToEmails(emailList);
        tagEmail.setTagEmailSites(Arrays.asList(tagEmailSite));

        Either<Errors, TagEmailResponse> result =
                placementManager.sendTagEmails(tagEmail, proxy, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getMessage(),
                is("SiteId cannot be null."));
    }

    @Test
    public void testInvalidPlacementStatus() throws ProxyException {
        List<Placement> plcmList = new ArrayList();
        Integer plcInt = EntityFactory.random.nextInt();
        Placement plcm = new Placement();
        plcm.setIsTrafficked(1L);
        plcm.setStatus("Planning");
        plcmList.add(plcm);
        when(placementDao.getPlacementsByIds(anyCollection(),eq(sqlSessionMock))).thenReturn(plcmList);

        TagEmailProxy proxy = mock(TagEmailProxy.class);
        TagEmailWrapper response = mock(TagEmailWrapper.class);

        List<String> emailList = new ArrayList<>();
        emailList.add("test@trueforce.com");

        List<Integer> integerList = new ArrayList<>();
        integerList.add(plcInt);

        TagEmailSite tagEmailSite = new TagEmailSite();
        tagEmailSite.setFileType("txt");
        tagEmailSite.setSiteId(6064873);
        tagEmailSite.setPlacementIds(integerList);
        tagEmailSite.setRecipients(emailList);

        TagEmail tagEmail = new TagEmail();
        tagEmail.setUserEmail("user@email.com");
        tagEmail.setToEmails(emailList);
        tagEmail.setTagEmailSites(Arrays.asList(tagEmailSite));

        Either<Errors, TagEmailResponse> result =
                placementManager.sendTagEmails(tagEmail, proxy, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getMessage(),
                is(String.format("Invalid Placement [%s] status", plcm.getStatus())));
    }

    private Answer<List<PlacementView>> getPlacementViewByPlacementFilterParam(final int counter) {
        return new Answer<List<PlacementView>>() {
            public List<PlacementView> answer(InvocationOnMock invocation) {
                Long agencyId = (Long) invocation.getArguments()[0];
                PlacementFilterParam filterParam = (PlacementFilterParam) invocation.getArguments()[3];
                PlacementFilterParamLevelTypeEnum levelType =
                        (PlacementFilterParamLevelTypeEnum) invocation.getArguments()[4];
                List<PlacementView> result = new ArrayList<>();
                PlacementView placementView;
                switch (levelType) {
                    case CAMPAIGN:
                        for (int i = 0; i < counter; i++) {
                            placementView = new PlacementView();
                            placementView.setCampaignId(Math.abs(EntityFactory.random.nextLong()));
                            placementView.setCampaignName(
                                    "CAMPAIGN-" + EntityFactory.faker.name().name());
                            result.add(placementView);
                        }
                        break;
                    case SITE:
                        for (int i = 0; i < counter; i++) {
                            placementView = new PlacementView();
                            placementView.setCampaignId(filterParam.getCampaignId());
                            placementView.setSiteId(Math.abs(EntityFactory.random.nextLong()));
                            placementView.setSiteName("SITE-" + EntityFactory.faker.name().name());
                            result.add(placementView);
                        }
                        break;
                    case SECTION:
                        for (int i = 0; i < counter; i++) {
                            placementView = new PlacementView();
                            placementView.setCampaignId(filterParam.getCampaignId());
                            placementView.setSiteId(filterParam.getSiteId());
                            placementView.setSiteSectionId(Math.abs(
                                    EntityFactory.random.nextLong()));
                            placementView.setSiteSectionName("SECTION-"
                                    + EntityFactory.faker.name().name());
                            result.add(placementView);
                        }
                        break;
                    case PLACEMENT:
                        for (int i = 0; i < counter; i++) {
                            placementView = new PlacementView();
                            PlacementView pla = EntityFactory.createPlacementView();
                            placementView.setCampaignId(filterParam.getCampaignId());
                            placementView.setSiteId(filterParam.getSiteId());
                            placementView.setSiteSectionId(filterParam.getSectionId());
                            placementView.setId(pla.getId());
                            placementView.setName(pla.getName());
                            placementView.setPlacementAlias(
                                    placementView.getName() + " - " + pla.getSizeName());
                            result.add(placementView);
                        }
                        break;
                }
                return result;
            }
        };
    }

    private CreativeInsertionFilterParam prepareDataToGetPlacementsTest(CreativeInsertionFilterParamTypeEnum pivotType, CreativeInsertionFilterParamTypeEnum levelType) throws Exception {
        CreativeInsertionFilterParam filterParam = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                pivotType, levelType);
        filterParam.setPivotType(filterParam.getPivotType().toLowerCase());
        filterParam.setType(filterParam.getType().toLowerCase());

        return filterParam;
    }

    private RecordSet<Placement> createPlacements(Long ioId, int totalRecords) throws Exception {
        RecordSet<Placement> result = new RecordSet<>();
        result.setRecords(new ArrayList<Placement>());
        result.setPageSize(1000);
        result.setStartIndex(0);
        result.setTotalNumberOfRecords(totalRecords);

        for (int i = 0; i < totalRecords; i++) {
            Long placementId = Math.abs(EntityFactory.random.nextLong());
            Placement placement = EntityFactory.createPlacement();
            placement.setIoId(ioId);
            placement.setId(placementId);
            placement.setCreatedTpwsKey(key.getTpws());
            placement.setModifiedTpwsKey(key.getTpws());
            placement.setCreatedDate(new Date());
            placement.setModifiedDate(new Date());
            placement.setStatus("New");
            placementsMap.put(placementId, placement);
            result.getRecords().add(placement);
        }
        return result;
    }

    private RecordSet<Package> dataForMultiplePlacements(int totalRecords, int type) throws Exception {
        RecordSet<Package> result = new RecordSet<>();
        result.setRecords(new ArrayList<Package>());
        result.setPageSize(1000);
        result.setStartIndex(0);
        result.setTotalNumberOfRecords(totalRecords);
        if (type == STANDALONE_PLACEMENTS || type == PLACEMENTS_MIXED) {
            Package pkgDTO = EntityFactory.createPackage();
            pkgDTO.setCampaignId(campaignId);
            pkgDTO.setName(null);
            List<Placement> placementList = createOnlyPlacements(ioId, totalRecords, pkgDTO);
            pkgDTO.setPlacements(placementList);
            pkgDTO.setPlacementCount((long) placementList.size());
            result.getRecords().add(pkgDTO);
        }
        if (type == PLACEMENTS_WITH_PACKAGES || type == PLACEMENTS_MIXED) {
            Package pkgDTO = EntityFactory.createPackage();
            pkgDTO.setCampaignId(campaignId);
            List<Placement> placementList = createOnlyPlacements(ioId, totalRecords, pkgDTO);
            pkgDTO.setPlacements(placementList);
            pkgDTO.setPlacementCount((long) placementList.size());
            result.getRecords().add(pkgDTO);
        }
        return result;
    }

    private List<Placement> createOnlyPlacements(Long ioId, int totalRecords, Package pkgDTO) {
        List<Placement> placementList = new ArrayList<>();
        for (int i = 0; i < totalRecords; i++) {
            Placement placement = EntityFactory.createPlacement();
            placement.setIoId(ioId);
            placement.setCampaignId(pkgDTO.getCampaignId());
            placement.setCreatedTpwsKey(key.getTpws());
            placement.setModifiedTpwsKey(key.getTpws());
            placement.setCreatedDate(new Date());
            placement.setModifiedDate(new Date());
            placement.setStatus("New");
            placementList.add(placement);
        }
        return placementList;
    }

    private Answer<Placement> getPlacement() {
        return new Answer<Placement>() {
            public Placement answer(InvocationOnMock invocation) {
                Long id = (Long) invocation.getArguments()[0];
                return placementsMap.get(id);
            }
        };
    }

    private Answer<RecordSet<Placement>> savedPlacements(final RecordSet<Placement> placements, final int type) {
        return new Answer<RecordSet<Placement>>() {
            @Override
            public RecordSet<Placement> answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (type == ONE_PLACEMENT_ACCEPTED) {
                    for (Placement placement : placements.getRecords()) {
                        if (placement.getStatus().equals("Accepted")) {
                            return new RecordSet<>(0, 1, 1, Collections.singletonList(placement));
                        }
                    }
                } else if (type == ONE_PLACEMENT_REJECTED) {
                    for (Placement placement : placements.getRecords()) {
                        if (placement.getStatus().equals("Rejected")) {
                            return new RecordSet<>(0, 1, 1, Collections.singletonList(placement));
                        }
                    }
                }
                return placements;
            }
        };
    }

    private Answer<Placement> create() {
        return new Answer<Placement>() {
            @Override
            public Placement answer(InvocationOnMock invocationOnMock) throws Throwable {
                placement = (Placement) invocationOnMock.getArguments()[0];
                placement.setId(placement.getId() == null ? Math.abs(EntityFactory.random.nextLong()) : placement.getId());
                placement.setCreatedTpwsKey(key.getTpws());
                placement.setModifiedTpwsKey(key.getTpws());
                placement.setCreatedDate(new Date());
                placement.setModifiedDate(new Date());
                if (placement.getStatus() == null) {
                    placement.setStatus("New");
                }
                return placement;
            }
        };
    }

    private Answer<Placement> savedPlacement() {
        return new Answer<Placement>() {
            @Override
            public Placement answer(InvocationOnMock invocationOnMock) throws Throwable {
                Long id = (Long) invocationOnMock.getArguments()[0];
                return placement.getId().longValue() == id.longValue() ? placement : null;
            }
        };
    }

    private Answer<Package> getPackageById() {
        return new Answer<Package>() {
            @Override
            public Package answer(InvocationOnMock invocationOnMock) throws Throwable {
                Long id = (Long) invocationOnMock.getArguments()[0];
                Package pkg = null;
                for (Package pkgDTO : pkgs.getRecords()) {
                    if (pkgDTO.getId().equals(id)) {
                        pkg = pkgDTO;
                        break;
                    }
                }
                if (pkg == null) {
                    return null;
                }
                // Adding additional information for Package Cost
                CostDetail costDetail = new CostDetail();
                costDetail.setPlannedNetAdSpend(Math.random() * 10000);
                costDetail.setInventory(EntityFactory.random.nextLong());
                costDetail.setPlannedNetRate(Math.random() * 10000);
                costDetail.setRateType(RateTypeEnum.CPA.getCode());
                List<CostDetail> costDetails = new ArrayList<>();
                costDetails.add(costDetail);
                pkg.setCostDetails(costDetails);
                return pkg;
            }
        };
    }

    private Answer<InsertionOrder> savedIO(final Long ioId, final String status) {
        return new Answer<InsertionOrder>() {
            @Override
            public InsertionOrder answer(InvocationOnMock invocationOnMock) throws Throwable {
                InsertionOrder io = EntityFactory.createInsertionOrder();
                io.setId(Math.abs(EntityFactory.random.nextLong()));
                io.setCreatedTpwsKey(key.getTpws());
                io.setModifiedTpwsKey(key.getTpws());
                io.setCampaignId(campaignId);
                io.setCreatedDate(new Date());
                io.setModifiedDate(new Date());
                io.setStatus(status);
                return io;
            }
        };
    }

    private Answer<Size> savedSize(final Placement placement) {
        return new Answer<Size>() {
            @Override
            public Size answer(InvocationOnMock invocationOnMock) throws Throwable {
                Size result = new Size();
                result.setWidth(placement.getWidth());
                result.setHeight(placement.getHeight());
                result.setLabel(placement.getSizeName());
                result.setId(placement.getSizeId() == null ? EntityFactory.random.nextLong() : placement.getSizeId());
                result.setCreatedDate(new Date());
                result.setModifiedDate(new Date());
                return result;
            }
        };
    }

    private Answer<SiteSection> savedSiteSection(final Placement placement) {
        return new Answer<SiteSection>() {
            @Override
            public SiteSection answer(InvocationOnMock invocationOnMock) throws Throwable {
                SiteSection result = new SiteSection();
                result.setName("Site Section for " + placement.getName());
                result.setSiteId(placement.getSiteId());
                result.setId(EntityFactory.random.nextLong());
                result.setCreatedDate(new Date());
                result.setModifiedDate(new Date());
                return result;
            }
        };
    }

    private Answer<PlacementStatus> savedPlacementStatus(final Placement placement) {
        return new Answer<PlacementStatus>() {
            @Override
            public PlacementStatus answer(InvocationOnMock invocationOnMock) throws Throwable {
                PlacementStatus status = (PlacementStatus) invocationOnMock.getArguments()[0];
                placement.setStatus("New");
                return status;
            }
        };
    }

    private Answer<CostDetail> savedCostDetail(final Map<Long, CostDetail> costCreated) {
        return new Answer<CostDetail>() {
            @Override
            public CostDetail answer(InvocationOnMock invocationOnMock) throws Throwable {
                CostDetail result = (CostDetail) invocationOnMock.getArguments()[0];
                result.setId(Math.abs(EntityFactory.random.nextLong()));
                costCreated.put(result.getId(), result);
                return result;
            }
        };
    }

    private Answer<Package> savePackage() {
        return new Answer<Package>() {
            @Override
            public Package answer(InvocationOnMock invocationOnMock) throws Throwable {
                Package pkg = (Package) invocationOnMock.getArguments()[0];
                pkg.setId(pkg.getId() == null ? Math.abs(EntityFactory.random.nextLong()) : pkg.getId());
                pkg.setCreatedTpwsKey(key.getTpws());
                pkg.setModifiedTpwsKey(key.getTpws());
                pkg.setCreatedDate(new Date());
                pkg.setModifiedDate(new Date());
                return pkg;
            }
        };
    }

    private Answer<CostDetail> savePackageCostDetail() {
        return new Answer<CostDetail>() {
            @Override
            public CostDetail answer(InvocationOnMock invocationOnMock) throws Throwable {
                CostDetail costDetail = (CostDetail) invocationOnMock.getArguments()[0];
                costDetail.setId(costDetail.getId() == null
                        ? Math.abs(EntityFactory.random.nextLong()) : costDetail.getId());
                costDetail.setCreatedTpwsKey(key.getTpws());
                costDetail.setModifiedTpwsKey(key.getTpws());
                costDetail.setCreatedDate(new Date());
                costDetail.setModifiedDate(new Date());
                return costDetail;
            }
        };
    }

    private Answer<RecordSet<Placement>> getExistentPlacements() {
        return new Answer<RecordSet<Placement>>() {
            @Override
            public RecordSet<Placement> answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<Placement> result = new ArrayList<>();
                for (Placement pl : allPlacements) {
                    if (pl.getId() != null) {
                        result.add(pl);
                    }
                }
                return new RecordSet<>(result);
            }
        };
    }

    private RecordSet<Package> createPackagePlacements(int totalPlacements) throws Exception {
        RecordSet<Package> packageDTORecordSet = dataForMultiplePlacements(totalPlacements, PLACEMENTS_WITH_PACKAGES);
        RecordSet<Placement> placements = new RecordSet<>();
        when(packageDao.create(
                any(Package.class),
                eq(sqlSessionMock))).thenAnswer(savePackage());
        when(packageCostDetailDao.create(
                any(CostDetail.class),
                eq(sqlSessionMock))).thenAnswer(savePackageCostDetail());
        when(insertionOrderDao.get(
                anyLong(),
                eq(sqlSessionMock))).thenReturn(io);
        for (int i = 0; i < packageDTORecordSet.getRecords().size(); i++) {
            when(placementDao.updateDataCostDetailPlacementsByPackageId(
                    any(Placement.class),
                    anyLong(),
                    eq(sqlSessionMock))).thenReturn(5 + ((i + 1) * 5));
            when(placementDao.getPlacements(
                    any(SearchCriteria.class),
                    eq(key),
                    eq(sqlSessionMock))).thenReturn(placements);
            Package pkg = packageDTORecordSet.getRecords().get(i);
            pkg.setId(Math.abs(EntityFactory.random.nextLong()));
            placements.setRecords(createOnlyPlacements(ioId, 5, pkg));
            placements.setTotalNumberOfRecords(5);
            when(packageDao.get(
                    anyLong(),
                    eq(sqlSessionMock))).thenReturn(pkg);
            placementManager.addNewPlacementsToPackage(pkg.getId(), ioId, placements, key);
        }
        return packageDTORecordSet;
    }

    private static Answer<List<PlacementView>> getPlacementsByFilterParam(final int counter) {
        return new Answer<List<PlacementView>>() {
            @Override
            public List<PlacementView> answer(InvocationOnMock invocation) {
                List<PlacementView> result = new ArrayList<>();
                PlacementView view;
                for (int i = 0; i < counter; i++) {
                    view = EntityFactory.createPlacementView();
                    view.setAdSpend(null);
                    view.setIoId(null);
                    result.add(view);
                }
                return result;
            }
        };
    }

    private List<CostDetail> getCostDetails(int numberOfCostDetails, boolean fromDB) {
        List<CostDetail> result = new ArrayList<>();
        RateTypeEnum rateType;
        for (int i = 0; i < numberOfCostDetails; i++) {
            CostDetail costDetail = EntityFactory.createCostDetail();
            costDetail.setForeignId(placement.getId());
            if (i > 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(result.get(i - 1).getEndDate());
                cal.add(Calendar.DAY_OF_YEAR, 1);
                costDetail.setStartDate(cal.getTime());
                cal = Calendar.getInstance();
                cal.setTime(costDetail.getStartDate());
                cal.add(Calendar.DAY_OF_YEAR, 30);
                costDetail.setEndDate(cal.getTime());
            }
            costDetail.setStartDate(DateConverter.startDate(costDetail.getStartDate()));
            costDetail.setEndDate(DateConverter.endDate(costDetail.getEndDate()));
            if (fromDB) {
                costDetail.setId(Math.abs(EntityFactory.random.nextLong()));
                rateType = RateTypeEnum.typeOf(costDetail.getRateType());
                costDetail.setInventory(PackagePlacementUtil.calculateInventory(costDetail.getPlannedGrossRate(),
                        costDetail.getPlannedGrossAdSpend(), rateType.toString()));
                costDetail.setPlannedNetAdSpend(PackagePlacementUtil.calculatePlannedNetAdSpend(costDetail.getMargin(),
                        costDetail.getPlannedGrossAdSpend()));
                costDetail.setPlannedNetRate(PackagePlacementUtil.calculatePlannedNetRate(costDetail.getMargin(),
                        costDetail.getPlannedGrossRate()));
            } else {
                costDetail.setId(null);
            }
            result.add(costDetail);
        }
        if (fromDB) {
            CostDetail placeholder = result.get(result.size() - 1);
            placeholder.setInventory(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY);
            placeholder.setRateType(RateTypeEnum.CPM.getCode());
            placeholder.setPlannedGrossAdSpend(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE);
            placeholder.setPlannedGrossRate(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE);
            placeholder.setPlannedNetAdSpend(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE);
            placeholder.setPlannedNetRate(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE);
            placeholder.setEndDate(null);

        }
        return result;
    }

    private Answer<Void> removeCostDetails(final Map<Long, CostDetail> deleted) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                CostDetail result = (CostDetail) invocation.getArguments()[0];
                deleted.put(result.getId(), result);
                return null;
            }
        };
    }

    private static Answer<CostDetail> createCostDetails(final Map<Long, CostDetail> created) {
        return new Answer<CostDetail>() {
            @Override
            public CostDetail answer(InvocationOnMock invocation) {
                CostDetail result = (CostDetail) invocation.getArguments()[0];
                result.setId(Math.abs(EntityFactory.random.nextLong()));
                created.put(result.getId(), result);
                return result;
            }
        };
    }

    private static Answer<CostDetail> updateCostDetails(final Map<Long, CostDetail> updated) {
        return new Answer<CostDetail>() {
            @Override
            public CostDetail answer(InvocationOnMock invocation) {
                CostDetail result = (CostDetail) invocation.getArguments()[0];
                updated.put(result.getId(), result);
                return result;
            }
        };
    }

    private static Answer<CostDetail> getCostDetailById() {
        return new Answer<CostDetail>() {
            @Override
            public CostDetail answer(InvocationOnMock invocation) {
                Long id = (Long) invocation.getArguments()[0];
                CostDetail result = EntityFactory.createCostDetail();
                result.setId(id);
                return result;
            }
        };
    }
}

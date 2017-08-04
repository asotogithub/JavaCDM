package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;

import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.commons.model.dim.DimPackageCostDetail;
import trueffect.truconnect.api.commons.model.dim.DimPlacementCost;
import trueffect.truconnect.api.commons.model.dim.DimPlacementCostDetail;
import trueffect.truconnect.api.commons.model.dim.DimProductBuyCost;
import trueffect.truconnect.api.commons.model.dto.dim.DimPackageDTO;
import trueffect.truconnect.api.commons.model.dto.dim.DimPlacementCostDetailDTO;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AgencyDao;
import trueffect.truconnect.api.crud.dao.DimCostDetailDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public class DimCostDetailManagerTest extends AbstractManagerTest {

    private static DimCostDetailManager dimCostDetailManager;
    private SqlSession dimSession;
    private AgencyDao agencyDao;
    private DimCostDetailDao dimCostDetailDao;
    private PackageDaoBase dimPackageDao;

    @Before
    public void setUp() throws Exception {
        agencyDao = mock(AgencyDao.class);
        dimPackageDao = mock(PackageDaoBase.class);
        dimCostDetailDao = mock(DimCostDetailDao.class);
        dimCostDetailManager = new DimCostDetailManager(dimPackageDao, dimCostDetailDao, agencyDao, accessControlMockito);
        when(agencyDao.openSession()).thenReturn(sqlSessionMock);
        dimSession = mock(SqlSession.class);
        when(dimCostDetailDao.openSession()).thenReturn(dimSession);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, sqlSessionMock);
    }

    @Test
    public void getPlacementOk() {
        when(dimCostDetailDao.getDimPlacementCost(anyLong(), any(SqlSession.class))).thenReturn(createDimPlacementCost(2));
        when(dimCostDetailDao.getDimPlacementCostDetail(anyLong(), any(SqlSession.class))).thenReturn(createDimPlacementCostDetail(2));
        when(dimCostDetailDao.getDimProductBuyCost(anyLong(), any(SqlSession.class))).thenReturn(createDimProductBuyCost(2));
        Either<Error, DimPlacementCostDetailDTO> result = dimCostDetailManager.getDimPlacementCostDetails(1L);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void getPlacementFailNullParam() {
        try {
            dimCostDetailManager.getDimPlacementCostDetails(null);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Placement Id"))));
        }
    }

    @Test
    public void getPackageOk() {
        when(dimCostDetailDao.getDimPackageCostDetail(anyLong(), any(SqlSession.class))).thenReturn(createDimPackageCostDetail(2));
        when(dimCostDetailDao.getPackagePlacement(anyLong(), any(SqlSession.class))).thenReturn(createPackagePlacement(2));
        when(dimPackageDao.get(anyLong(), any(SqlSession.class))).thenReturn(createPackage());
        Either<Error, DimPackageDTO> result = dimCostDetailManager.getPackage(1L);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void getPackageFailNullParam() {
        try {
            dimCostDetailManager.getPackage(null);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Package Id"))));
        }
    }

    @Test
    public void getPackageNotFoundFail() {
        when(dimCostDetailDao.getDimPackageCostDetail(anyLong(), any(SqlSession.class))).thenReturn(createDimPackageCostDetail(2));
        when(dimCostDetailDao.getPackagePlacement(anyLong(), any(SqlSession.class))).thenReturn(createPackagePlacement(2));
        when(dimPackageDao.get(anyLong(), any(SqlSession.class))).thenReturn(null);
        Either<Error, DimPackageDTO> result = dimCostDetailManager.getPackage(1L);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error();
        assertThat(error.getCode(), instanceOf(BusinessCode.class));
        assertThat(error.getCode().toString(), is(BusinessCode.NOT_FOUND.toString()));
        assertThat(error.getMessage(), is("Record with id 1 not found."));
    }

    @Test
    public void dimHardRemoveCostDetailsOk() {
        when(agencyDao.getAllPackageIds(anyLong(), any(SqlSession.class))).thenReturn(EntityFactory.getLongList(10));
        when(agencyDao.getAllPlacementIds(anyLong(), any(SqlSession.class))).thenReturn(EntityFactory.getLongList(10));
        Either<Error, Void> result = dimCostDetailManager.dimHardRemoveCostDetails(1L, key, sqlSessionMock);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void dimHardRemoveCostDetailsDACFail() {
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, sqlSessionMock);
        // perform test
        Either<Error, Void> result = dimCostDetailManager.dimHardRemoveCostDetails(1L, key, sqlSessionMock);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error();
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void dimHardRemoveCostDetailsNullIdFail() {
        try {
            dimCostDetailManager.dimHardRemoveCostDetails(null, key, sqlSessionMock);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Agency Id"))));
        }
    }

    @Test
    public void dimHardRemoveCostDetailsNullKeyFail() {
        try {
            dimCostDetailManager.dimHardRemoveCostDetails(1L, null, sqlSessionMock);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "OauthKey"))));
        }
    }

    @Test
    public void dimHardRemoveCostDetailsNullSessionFail() {
        try {
            dimCostDetailManager.dimHardRemoveCostDetails(1L, key, null);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "TFA Session"))));
        }
    }

    private List<DimPlacementCost> createDimPlacementCost(int quantity) {
        List<DimPlacementCost> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            DimPlacementCost dpc = new DimPlacementCost();
            dpc.setStartDate(new Date());
            dpc.setEndDate(new Date());
            dpc.setInventory(Math.abs(EntityFactory.random.nextLong()));
            dpc.setCostType("P");
            dpc.setCostRateType("CPC");
            dpc.setPlacementCostId(Math.abs(EntityFactory.random.nextLong()));
            dpc.setPlacementId(Math.abs(EntityFactory.random.nextLong()));
            dpc.setRate(Math.abs(EntityFactory.random.nextDouble()));
            result.add(dpc);
        }
        return result;
    }

    private List<DimPlacementCostDetail> createDimPlacementCostDetail(int quantity) {
        List<DimPlacementCostDetail> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            DimPlacementCostDetail dpcd = new DimPlacementCostDetail(createCostDetail());
            dpcd.setPlacementCostId(Math.abs(EntityFactory.random.nextLong()));
            dpcd.setPlacementId(Math.abs(EntityFactory.random.nextLong()));
            dpcd.setDimPlacementCostDetailRateType(2L);
            result.add(dpcd);
        }
        return result;
    }

    private List<DimProductBuyCost> createDimProductBuyCost(int quantity) {
        List<DimProductBuyCost> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            DimProductBuyCost dpbc = new DimProductBuyCost();
            dpbc.setStartDate(new Date());
            dpbc.setEndDate(new Date());
            dpbc.setInventory(Math.abs(EntityFactory.random.nextLong()));
            dpbc.setCostType("P");
            dpbc.setCostRateType("CPC");
            dpbc.setProductBuyCostId(Math.abs(EntityFactory.random.nextLong()));
            dpbc.setProductBuyId(Math.abs(EntityFactory.random.nextLong()));
            dpbc.setRate(Math.abs(EntityFactory.random.nextDouble()));
            result.add(dpbc);
        }
        return result;
    }

    private DimPackageDTO createPackage() {
        DimPackageDTO result = new DimPackageDTO();
        String text = "DimPackageDTO " + EntityFactory.faker.name().name() + (new Date().getTime());
        result.setName(text);
        result.setCountryCurrencyId(1L);
        result.setCampaignId(EntityFactory.random.nextLong());
        result.setExternalId(String.valueOf(Math.abs(EntityFactory.random.nextLong())));
        result.setDescription(text);
        return result;
    }

    private List<PackagePlacement> createPackagePlacement(int quantity) {
        List<PackagePlacement> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            PackagePlacement pp = new PackagePlacement();
            pp.setCreated(new Date());
            pp.setModified(new Date());
            pp.setId(Math.abs(EntityFactory.random.nextLong()));
            pp.setPackageId(Math.abs(EntityFactory.random.nextLong()));
            pp.setPlacementId(Math.abs(EntityFactory.random.nextLong()));
            pp.setLogicalDelete("N");
            result.add(pp);
        }
        return result;
    }

    private List<DimPackageCostDetail> createDimPackageCostDetail(int quantity) {
        List<DimPackageCostDetail> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            DimPackageCostDetail dpcd = new DimPackageCostDetail(createCostDetail());
            dpcd.setPackageCostId(Math.abs(EntityFactory.random.nextLong()));
            dpcd.setPackageId(Math.abs(EntityFactory.random.nextLong()));
            dpcd.setRateType(2L);
            result.add(dpcd);
        }
        return result;
    }

    private CostDetail createCostDetail() {
        CostDetail cd = new CostDetail();
        cd.setActualGrossAdSpend(Math.abs(EntityFactory.random.nextDouble()));
        cd.setActualGrossRate(Math.abs(EntityFactory.random.nextDouble()));
        cd.setActualNetAdSpend(Math.abs(EntityFactory.random.nextDouble()));
        cd.setActualNetRate(Math.abs(EntityFactory.random.nextDouble()));
        cd.setCreatedDate(new Date());
        cd.setModifiedDate(new Date());
        cd.setEndDate(new Date());
        cd.setInventory(Math.abs(EntityFactory.random.nextLong()));
        cd.setLogicalDelete("N");
        cd.setPlannedGrossAdSpend(Math.abs(EntityFactory.random.nextDouble()));
        cd.setPlannedGrossRate(Math.abs(EntityFactory.random.nextDouble()));
        cd.setPlannedNetAdSpend(Math.abs(EntityFactory.random.nextDouble()));
        cd.setPlannedNetRate(Math.abs(EntityFactory.random.nextDouble()));
        return cd;
    }
}

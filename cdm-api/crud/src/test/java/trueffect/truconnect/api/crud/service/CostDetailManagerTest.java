package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gustavo Claure
 */
public class CostDetailManagerTest extends AbstractManagerTest {

    private CostDetailDaoExtended costDetailDao;
    private CostDetailManager costDetailManager;
    private CostDetailDaoBase dimCostDetailDao;
    private SqlSession sqlSessionMockDim;

    private CostDetailType packageType;
    private Long packageId;
    private List<CostDetail> costDetails;
    private Map<Long, CostDetail> deleted;
    private Map<Long, CostDetail> created;
    private Map<Long, CostDetail> updated;

    private Map<Long, CostDetail> dimDeleted;
    private Map<Long, CostDetail> dimCreated;
    private Map<Long, CostDetail> dimUpdated;

    @Before
    public void setUp() throws Exception {
        // Mock daos
        costDetailDao = mock(CostDetailDaoExtended.class);
        packageId = Math.abs(EntityFactory.random.nextLong());
        dimCostDetailDao = mock(CostDetailDaoBase.class);
        sqlSessionMockDim = mock(SqlSession.class);

        // variables
        packageType = CostDetailType.PACKAGE;
        costDetails = this.getCostDetails(1, false);
        deleted = new HashMap<>();
        created = new HashMap<>();
        updated = new HashMap<>();
        dimDeleted = new HashMap<>();
        dimCreated = new HashMap<>();
        dimUpdated = new HashMap<>();

        costDetailManager = new CostDetailManager(costDetailDao, dimCostDetailDao,
                packageType, accessControlMockito);

        // Mocks access control
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.PACKAGE, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.PACKAGE_COST_DETAIL, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.PLACEMENT, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.PLACEMENT_COST_DETAIL, sqlSessionMock);

        // Mocks dim
        doAnswer(remove(dimDeleted)).when(dimCostDetailDao).remove(any(CostDetail.class), anyString(), eq(sqlSessionMockDim));
        when(dimCostDetailDao.update(any(CostDetail.class), eq(sqlSessionMockDim))).thenAnswer(update(dimUpdated));
        when(dimCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMockDim))).thenAnswer(create(dimCreated));
    }

    @Test
    public void setDefaultCostDetailsWithSingleCostOk() {
        // prepare data
        costDetails = this.getCostDetails(10, false);

        // perform test
        List<CostDetail> result = costDetailManager.setDefaultCostDetails(packageId, costDetails, tpws);

        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(equalTo(2)));

        CostDetail first = result.get(0);
        assertThat(first.getForeignId(), is(equalTo(packageId)));
        assertThat(first.getInventory(), is(notNullValue()));
        assertThat(first.getInventory(), is(greaterThanOrEqualTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
        assertThat(first.getPlannedNetAdSpend(), is(notNullValue()));
        assertThat(first.getPlannedNetRate(), is(notNullValue()));
        assertThat(first.getStartDate(), is(notNullValue()));
        assertThat(first.getEndDate(), is(notNullValue()));
        assertThat(first.getCreatedTpwsKey(), is(equalTo(tpws)));

        CostDetail placeholder = result.get(1);
        assertThat(placeholder.getForeignId(), is(equalTo(packageId)));
        assertThat(placeholder.getInventory(), is(notNullValue()));
        assertThat(placeholder.getInventory(), is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
        assertThat(placeholder.getPlannedGrossAdSpend(), is(notNullValue()));
        assertThat(placeholder.getPlannedGrossAdSpend(), is(equalTo(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE)));
        assertThat(placeholder.getPlannedGrossRate(), is(notNullValue()));
        assertThat(placeholder.getPlannedGrossRate(), is(equalTo(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE)));
        assertThat(placeholder.getPlannedNetAdSpend(), is(notNullValue()));
        assertThat(placeholder.getPlannedNetAdSpend(), is(equalTo(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE)));
        assertThat(placeholder.getPlannedNetRate(), is(notNullValue()));
        assertThat(placeholder.getPlannedNetRate(), is(equalTo(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE)));
        assertThat(placeholder.getStartDate(), is(notNullValue()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(first.getEndDate());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date newStartDate = DateConverter.startDate(calendar.getTime());
        assertThat(placeholder.getStartDate(), is(equalTo(newStartDate)));
        assertThat(placeholder.getEndDate(), is(nullValue()));
    }

    @Test
    public void setDefaultCostDetailsWithMultipleCostsOk() {
        // prepare data

        // perform test
        List<CostDetail> result = costDetailManager.setDefaultCostDetails(packageId, costDetails, tpws);

        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(equalTo(2)));

        CostDetail first = result.get(0);
        assertThat(first.getForeignId(), is(equalTo(packageId)));
        assertThat(first.getInventory(), is(notNullValue()));
        assertThat(first.getInventory(), is(greaterThanOrEqualTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
        assertThat(first.getPlannedNetAdSpend(), is(notNullValue()));
        assertThat(first.getPlannedNetRate(), is(notNullValue()));
        assertThat(first.getStartDate(), is(notNullValue()));
        assertThat(first.getEndDate(), is(notNullValue()));
        assertThat(first.getCreatedTpwsKey(), is(equalTo(tpws)));

        CostDetail placeholder = result.get(1);
        assertThat(placeholder.getForeignId(), is(equalTo(packageId)));
        assertThat(placeholder.getInventory(), is(notNullValue()));
        assertThat(placeholder.getInventory(), is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
        assertThat(placeholder.getPlannedGrossAdSpend(), is(notNullValue()));
        assertThat(placeholder.getPlannedGrossAdSpend(), is(equalTo(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE)));
        assertThat(placeholder.getPlannedGrossRate(), is(notNullValue()));
        assertThat(placeholder.getPlannedGrossRate(), is(equalTo(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE)));
        assertThat(placeholder.getPlannedNetAdSpend(), is(notNullValue()));
        assertThat(placeholder.getPlannedNetAdSpend(), is(equalTo(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE)));
        assertThat(placeholder.getPlannedNetRate(), is(notNullValue()));
        assertThat(placeholder.getPlannedNetRate(), is(equalTo(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE)));
        assertThat(placeholder.getStartDate(), is(notNullValue()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(first.getEndDate());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date newStartDate = DateConverter.startDate(calendar.getTime());
        assertThat(placeholder.getStartDate(), is(equalTo(newStartDate)));
        assertThat(placeholder.getEndDate(), is(nullValue()));
    }

    @Test
    public void setDefaultCostDetailsWithNullParameters() {
        // prepare data
        costDetails = null;

        // perform test
        try {
            costDetailManager.setDefaultCostDetails(packageId, costDetails, tpws);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "CostDetails"))));
        }
    }

    @Test
    public void removeCostDetailsByForeignIdOk() {
        // prepare data
        int recordsToDelete = 5;

        //customize mock's behavior
        when(costDetailDao.getAll(anyLong(), eq(sqlSessionMock))).thenReturn(getCostDetails(recordsToDelete, true));
        doAnswer(remove(deleted)).when(costDetailDao).remove(any(CostDetail.class), anyString(), eq(sqlSessionMock));

        // perform test
        costDetailManager.removeCostDetailsByForeignId(packageId, key, sqlSessionMock, sqlSessionMockDim);
        assertThat(deleted.size(), is(equalTo(recordsToDelete)));

        // Dim assertions
        assertThat(dimDeleted.size(), is(equalTo(recordsToDelete)));
    }

    @Test
    public void removeCostDetailsByForeignIdWithNullParameters() {
        // prepare data
        packageId = null;

        // perform test
        try {
            costDetailManager.removeCostDetailsByForeignId(packageId, key, sqlSessionMock, sqlSessionMockDim);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Foreign id"))));
        }
    }

    @Test
    public void updateCostDetailsDeleteOk() {
        // prepare data
        int totalRecords = 5;
        int recordsToDelete = 2;
        List<CostDetail> existent = getCostDetails(totalRecords, true);
        costDetails = new ArrayList<>(existent.subList(0, existent.size() - recordsToDelete));

        //customize mock's behavior
        when(costDetailDao.getAll(eq(packageId), eq(sqlSessionMock))).thenReturn(existent);
        when(costDetailDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(getById());
        doAnswer(remove(deleted)).when(costDetailDao).remove(any(CostDetail.class), anyString(), eq(sqlSessionMock));

        when(costDetailDao.update(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(update(updated));
        when(costDetailDao.create(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(create(created));

        // perform test
        List<CostDetail> result = costDetailManager.updateCostDetails(costDetails, packageId, key, sqlSessionMock, sqlSessionMockDim);
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(equalTo(totalRecords - recordsToDelete + 1)));
        assertThat(deleted.size(), is(equalTo(recordsToDelete)));
        assertThat(updated.size(), is(equalTo(totalRecords - recordsToDelete)));
        assertThat(created.size(), is(equalTo(1)));

        // Dim assertions
        assertThat(dimDeleted.size(), is(equalTo(recordsToDelete)));
        assertThat(dimUpdated.size(), is(equalTo(totalRecords - recordsToDelete)));
        assertThat(dimCreated.size(), is(equalTo(1)));
    }

    @Test
    public void updateCostDetailsDeleteAll() {
        // prepare data
        int totalRecords = 5;
        int recordsToDelete = totalRecords;
        List<CostDetail> existent = getCostDetails(totalRecords, true);
        costDetails = new ArrayList<>(existent.subList(0, existent.size() - recordsToDelete));

        //customize mock's behavior
        when(costDetailDao.getAll(eq(packageId), eq(sqlSessionMock))).thenReturn(existent);

        // perform test
        try {
            costDetailManager.updateCostDetails(costDetails, packageId, key, sqlSessionMock, sqlSessionMockDim);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getMessage(),
                    is("You cannot delete all Cost Details. At least the first one should remain"));
        }
    }

    @Test
    public void updateCostDetailsDeleteNonConsecutive() {
        // prepare data
        int totalRecords = 5;
        int recordsToDelete = 2;
        List<CostDetail> existent = getCostDetails(totalRecords, true);
        costDetails = new ArrayList<>(existent.subList(0, recordsToDelete - 1));
        costDetails.add(existent.get(existent.size() - 2));

        //customize mock's behavior
        when(costDetailDao.getAll(eq(packageId), eq(sqlSessionMock))).thenReturn(existent);

        // perform test
        try {
            costDetailManager.updateCostDetails(costDetails, packageId, key, sqlSessionMock, sqlSessionMockDim);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getMessage(),
                    is("Cost Details to delete are not in consecutive order"));
        }
    }

    @Test
    public void updateCostDetailsDeleteNotLastCostDetail() {
        // prepare data
        int totalRecords = 5;
        int recordsToDelete = 2;
        List<CostDetail> existent = getCostDetails(totalRecords, true);
        costDetails = new ArrayList<>(existent.subList(existent.size() - recordsToDelete - 1, existent.size()));

        //customize mock's behavior
        when(costDetailDao.getAll(eq(packageId), eq(sqlSessionMock))).thenReturn(existent);

        // perform test
        try {
            costDetailManager.updateCostDetails(costDetails, packageId, key, sqlSessionMock, sqlSessionMockDim);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getMessage(),
                    is("Cost Details to delete are not last in the list"));
        }
    }

    @Test
    public void updateCostDetailsUpdateOk() {
        // prepare data
        int totalRecords = 5;
        int recordsToUpdate = 3;
        List<CostDetail> existent = getCostDetails(totalRecords, true);
        costDetails = new ArrayList<>(existent.subList(0, existent.size()));
        for (int i = 0; i < recordsToUpdate; i++) {
            costDetails.get(i).setInventory(Math.abs(EntityFactory.random.nextLong()));
            costDetails.get(i).setRateType(RateTypeEnum.FLT.getCode());
        }

        //customize mock's behavior
        when(costDetailDao.getAll(eq(packageId), eq(sqlSessionMock))).thenReturn(existent);
        when(costDetailDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(getById());
        doAnswer(remove(deleted)).when(costDetailDao).remove(any(CostDetail.class), anyString(), eq(sqlSessionMock));

        when(costDetailDao.update(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(update(updated));
        when(costDetailDao.create(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(create(created));

        // perform test
        List<CostDetail> result = costDetailManager.updateCostDetails(costDetails, packageId, key, sqlSessionMock, sqlSessionMockDim);
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(equalTo(totalRecords)));
        assertThat(updated.size(), is(equalTo(totalRecords)));
        assertThat(deleted.size(), is(equalTo(0)));
        assertThat(created.size(), is(equalTo(0)));

        // DIM assertions
        assertThat(dimUpdated.size(), is(equalTo(totalRecords)));
        assertThat(dimDeleted.size(), is(equalTo(0)));
        assertThat(dimCreated.size(), is(equalTo(0)));
    }

    @Test
    public void updateCostDetailsAddOk() {
        // prepare data
        int totalRecords = 5;
        List<CostDetail> existent = new ArrayList<>();
        costDetails = getCostDetails(totalRecords, false);

        //customize mock's behavior
        when(costDetailDao.getAll(eq(packageId), eq(sqlSessionMock))).thenReturn(existent);
        when(costDetailDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(getById());
        doAnswer(remove(deleted)).when(costDetailDao).remove(any(CostDetail.class), anyString(), eq(sqlSessionMock));

        when(costDetailDao.update(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(update(updated));
        when(costDetailDao.create(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(create(created));

        // perform test
        List<CostDetail> result = costDetailManager.updateCostDetails(costDetails, packageId, key, sqlSessionMock, sqlSessionMockDim);
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(equalTo(totalRecords + 1)));
        assertThat(created.size(), is(equalTo(totalRecords + 1)));
        assertThat(updated.size(), is(equalTo(0)));
        assertThat(deleted.size(), is(equalTo(0)));

        // DIM assertions
        assertThat(dimCreated.size(), is(equalTo(totalRecords + 1)));
        assertThat(dimUpdated.size(), is(equalTo(0)));
        assertThat(dimDeleted.size(), is(equalTo(0)));
    }

    @Test
    public void updateCostDetailsWithNullParameters() {
        // prepare data
        packageId = null;

        // perform test
        try {
            costDetailManager.updateCostDetails(costDetails, packageId, key, sqlSessionMock, sqlSessionMockDim);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Foreign id"))));
        }
    }

    @Test
    public void updateCostDetailsWithWrongAccess() {

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.PACKAGE, sqlSessionMock);
        // perform test
        try {
            costDetailManager.updateCostDetails(costDetails, packageId, key, sqlSessionMock, sqlSessionMockDim);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getErrorCode().toString(),
                    is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
            assertThat(e.getMessage(),
                    is("The user is not allowed in this context or the requested entity does not exist"));
        }
    }

    private List<CostDetail> getCostDetails(int numberOfCostDetails, boolean fromDB) {
        List<CostDetail> result = new ArrayList<>();
        RateTypeEnum rateType;
        for (int i = 0; i < numberOfCostDetails; i++) {
            CostDetail costDetail = EntityFactory.createCostDetail();
            costDetail.setForeignId(packageId);
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

    private static Answer<CostDetail> getById() {
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

    private Answer<Void> remove(final Map<Long, CostDetail> deleted) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                CostDetail result = (CostDetail) invocation.getArguments()[0];
                deleted.put(result.getId(), result);
                return null;
            }
        };
    }

    private static Answer<CostDetail> create(final Map<Long, CostDetail> created) {
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

    private static Answer<CostDetail> update(final Map<Long, CostDetail> updated) {
        return new Answer<CostDetail>() {
            @Override
            public CostDetail answer(InvocationOnMock invocation) {
                CostDetail result = (CostDetail) invocation.getArguments()[0];
                updated.put(result.getId(), result);
                return result;
            }
        };
    }
}

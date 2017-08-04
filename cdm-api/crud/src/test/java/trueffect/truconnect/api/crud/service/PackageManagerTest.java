package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoBase;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit Tests for Package
 *
 * @author Gustavo Claure, Marcelo Heredia
 */
public class PackageManagerTest extends AbstractManagerTest {

    private PackageDaoExtended packageDao;
    private CostDetailDaoExtended packageCostDetailDao;
    private CostDetailDaoExtended placementCostDetailDao;
    private PlacementDao placementDao;
    private InsertionOrderDao insertionOrderDao;
    private PackagePlacementDaoExtended packagePlacementDao;

    private CostDetailDaoBase dimPackageCostDetailDao;
    private PackageDaoBase dimPackageDao;
    private PackagePlacementDaoBase dimPackagePlacementDao;
    private CostDetailDaoBase dimPlacementCostDetailDao;

    private PackageManager packageManager;
    private Package pkg;
    private Placement placement;
    private SqlSession sqlSessionMockDim;
    private Package dimPackage;
    private Map<Long, CostDetail> costDetailsCreated;
    private Map<Long, CostDetail> dimCostDetailsCreated;
    private Map<Long, CostDetail> dimCostDetailsUpdated;
    private Map<Long, CostDetail> dimCostDetailsDeleted;
    private Map<Long, CostDetail> costDetailsDeleted;
    private Map<Long, Placement> placementsCreated;
    private Map<Long, Placement> placementsUpdated;
    private Map<Long, PackagePlacement> pkgPlacementsDeleted;
    private Map<Long, PackagePlacement> dimPkgPlacementsDeleted;

    @Before
    public void setUp() throws Exception {
        // Mocks daos
        packageDao = mock(PackageDaoExtended.class);
        packageCostDetailDao = mock(CostDetailDaoExtended.class);
        placementCostDetailDao = mock(CostDetailDaoExtended.class);
        packagePlacementDao = mock(PackagePlacementDaoExtended.class);
        placementDao = mock(PlacementDao.class);
        insertionOrderDao = mock(InsertionOrderDao.class);

        dimPackageCostDetailDao = mock(CostDetailDaoBase.class);
        dimPackageDao = mock(PackageDaoBase.class);
        dimPackagePlacementDao = mock(PackagePlacementDaoBase.class);
        dimPlacementCostDetailDao = mock(CostDetailDaoBase.class);
        sqlSessionMockDim = mock(SqlSession.class);

        //variables
        pkg = EntityFactory.createPackage();
        placement = EntityFactory.createPlacement();
        dimPackage = new Package();
        placementsCreated = new HashMap<>();
        costDetailsCreated = new HashMap<>();
        costDetailsDeleted = new HashMap<>();
        placementsCreated = new HashMap<>();
        placementsUpdated = new HashMap<>();
        pkgPlacementsDeleted = new HashMap<>();
        dimPkgPlacementsDeleted = new HashMap<>();

        dimCostDetailsCreated = new HashMap<>();
        dimCostDetailsUpdated = new HashMap<>();
        dimCostDetailsDeleted = new HashMap<>();

        //manager
        packageManager = new PackageManager(packageDao, packageCostDetailDao, placementDao,
                placementCostDetailDao, packagePlacementDao, dimPlacementCostDetailDao,
                dimPackageDao, dimPackageCostDetailDao, dimPackagePlacementDao, insertionOrderDao,
                accessControlMockito);

        // Mock sessions
        when(packageDao.openSession()).thenReturn(sqlSessionMock);
        when(dimPackageDao.openSession()).thenReturn(sqlSessionMockDim);

        // Mocks access control
        MockAccessControlUtil.allowAllForUser(accessControlMockito, sqlSessionMock);

        // Mock common behavios
        when(packageDao.create(any(Package.class), eq(sqlSessionMock))).
                thenAnswer(createPackage());
        when(packageCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock))).
                thenAnswer(createCostDetail());
        when(packageDao.packageNameExists(any(Package.class), eq(sqlSessionMock))).
                thenReturn(false);
        when(packageCostDetailDao.getAll(anyLong(), eq(sqlSessionMock))).
                thenReturn(new ArrayList<CostDetail>());
        doAnswer(removePackagePlacement(pkgPlacementsDeleted)).when(packagePlacementDao).
                delete(anyList(), eq(key), eq(sqlSessionMock));
        doAnswer(removeCostDetail(costDetailsDeleted)).when(packageCostDetailDao).
                remove(any(CostDetail.class), anyString(), eq(sqlSessionMock));
        when(placementDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(placement);
        when(placementDao.update(any(Placement.class), eq(sqlSessionMock))).thenReturn(placement);

        // Mocks dim
        doAnswer(createPackageDim()).when(dimPackageDao).create(any(Package.class), eq(sqlSessionMockDim));
        when(dimPackageCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMockDim))).thenAnswer(createCostDetail(dimCostDetailsCreated));
        doAnswer(updatePackage(dimPackage)).when(dimPackageDao).update(any(Package.class), eq(sqlSessionMockDim));
        when(dimPackageCostDetailDao.update(any(CostDetail.class), eq(sqlSessionMockDim))).thenAnswer(updateCostDetail(dimCostDetailsUpdated));
        doAnswer(removeCostDetail(dimCostDetailsDeleted)).when(dimPackageCostDetailDao).remove(any(CostDetail.class), anyString(), eq(sqlSessionMockDim));
        doAnswer(removePackagePlacement(dimPkgPlacementsDeleted)).when(packagePlacementDao).delete(anyList(), eq(key), eq(sqlSessionMockDim));
    }

    @Test
    public void testGetPackage() throws Exception {
        Long packageId = EntityFactory.random.nextLong();
        when(packageDao.get(eq(packageId), any(SqlSession.class))).thenReturn(pkg);
        when(placementDao.getPlacementsByPackageId(eq(packageId), eq(sqlSessionMock))).thenReturn(
                new ArrayList<Placement>());
        Package pkg = packageManager.getPackage(packageId, key);
        assertThat(pkg, is(notNullValue()));
        verify(packageDao, times(1)).close(sqlSessionMock);
    }

    @Test(expected = SystemException.class)
    public void testGetPackageNotFound() throws Exception {
        Long packageId = EntityFactory.random.nextLong();
        when(packageDao.get(eq(packageId), any(SqlSession.class))).thenReturn(pkg);
        MockAccessControlUtil.disallowAccessForUser(
                accessControlMockito,
                AccessStatement.PACKAGE,
                Collections.singletonList(packageId),
                key.getUserId(),
                sqlSessionMock
        );
        Package pkg = packageManager.getPackage(packageId, key);
    }

    @Test
    public void createPackageOk() {

        //customize mock's behavior
        when(packageCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(createCostDetail(costDetailsCreated));
        pkg.setId(null);
        // perform test
        Package result = packageManager.create(pkg, key, sqlSessionMock, sqlSessionMockDim);
        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getCostDetails(), is(notNullValue()));
        assertThat(result.getCostDetails().size(), is(2));
        assertThat(costDetailsCreated, is(notNullValue()));
        assertThat(costDetailsCreated.size(), is(2));

        // DIM assertions
        assertThat(dimPackage, is(notNullValue()));
        assertThat(dimCostDetailsCreated, is(notNullValue()));
        assertThat(dimCostDetailsCreated.size(), is(2));
    }

    @Test
    public void createPackageWithNullParameters() {
        // prepare data
        pkg = null;

        // perform test
        try {
            packageManager.create(pkg, key, sqlSessionMock, sqlSessionMockDim);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Package"))));
        }
    }

    @Test
    public void createPackageWithWrongPayload() {
        // prepare data
        pkg.setId(null);
        pkg.setName(null);
        pkg.setPlacements(null);

        // perform test
        try {
            packageManager.create(pkg, key, sqlSessionMock, sqlSessionMockDim);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            BeanPropertyBindingResult errors = (BeanPropertyBindingResult) e.getProperties().get("VALIDATION_RESULT");
            assertThat(errors.getAllErrors().size(), is(3));
            FieldError fieldError = (FieldError) errors.getAllErrors().get(0);
            assertThat(fieldError, is(notNullValue()));
            assertThat(fieldError.getField(), is(equalTo("name")));
            assertThat(fieldError.getRejectedValue(), is(equalTo(null)));
            assertThat(fieldError.getDefaultMessage(), is(equalTo(
                    String.format("Invalid %s, it cannot be empty.", "name"))));
            fieldError = (FieldError) errors.getAllErrors().get(1);
            assertThat(fieldError.getField(), is(equalTo("placements")));
            assertThat(fieldError.getRejectedValue(), is(equalTo(null)));
            assertThat(fieldError.getDefaultMessage(), is(equalTo(
                    String.format("Invalid %s, it cannot be empty.", "placements"))));
        }
    }

    @Test
    public void createPackageWithWrongAccess() {
        // prepare data

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, sqlSessionMock);
        pkg.setId(null);
        // perform test
        try {
            packageManager.create(pkg, key, sqlSessionMock, sqlSessionMockDim);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getErrorCode().toString(),
                    is(SecurityCode.ILLEGAL_USER_CONTEXT.toString()));
            assertThat(e.getMessage(),
                    is(equalTo("User not allowed in this context")));
        }
    }

    @Test
    public void updateOk() throws Exception {
        // prepare data
        pkg = prepareDataToUpdate(pkg);

        //customize mock's behavior
        when(packageCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(createCostDetail(costDetailsCreated));
        when(placementDao.checkPlacementsBelongsCampaignId(anyList(), eq(pkg.getCampaignId()), eq(sqlSessionMock))).thenReturn(Boolean.TRUE);
        when(placementDao.checkPlacementsStandAlone(anyList(), eq(pkg.getCampaignId()), eq(sqlSessionMock))).thenReturn(0L);
        when(placementDao.getPlacements(any(SearchCriteria.class), eq(key), eq(sqlSessionMock))).thenAnswer(getPlacements(pkg));
        when(packageDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(pkg);

        // perform test
        Package result = packageManager.update(pkg.getId(), pkg, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getCostDetails(), is(notNullValue()));
        assertThat(result.getCostDetails().size(), is(3));
        assertThat(costDetailsCreated, is(notNullValue()));
        assertThat(costDetailsCreated.size(), is(3));

        // DIM assertions
        assertThat(dimPackage, is(notNullValue()));
        assertThat(dimCostDetailsCreated, is(notNullValue()));
        assertThat(dimCostDetailsCreated.size(), is(3));
    }

    @Test
    public void updateWithNullParameters() {
        // prepare data
        pkg.setId(null);

        // perform test
        try {
            packageManager.update(pkg.getId(), pkg, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Id"))));
        }
    }

    @Test
    public void updateWithWrongPayload() {
        // prepare data
        pkg = prepareDataToUpdate(pkg);
        pkg.setName(null);

        // perform test
        try {
            packageManager.update(pkg.getId(), pkg, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            BeanPropertyBindingResult errors = (BeanPropertyBindingResult) e.getProperties().get("VALIDATION_RESULT");
            assertThat(errors.getAllErrors().size(), is(1));
            FieldError fieldError = (FieldError) errors.getAllErrors().get(0);
            assertThat(fieldError, is(notNullValue()));
            assertThat(fieldError.getField(), is(equalTo("name")));
            assertThat(fieldError.getRejectedValue(), is(equalTo(null)));
            assertThat(fieldError.getDefaultMessage(), is(equalTo(
                    String.format("Invalid %s, it cannot be empty.", "name"))));
        }
    }

    @Test
    public void updateWithDuplicatePlacements() {
        // prepare data
        pkg = prepareDataToUpdate(pkg);
        pkg.getPlacements().set(0, pkg.getPlacements().get(pkg.getPlacements().size() - 1));

        // perform test
        try {
            packageManager.update(pkg.getId(), pkg, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getErrorCode().toString(),
                    is(BusinessCode.DUPLICATE.toString()));
            assertThat(e.getMessage(),
                    is(equalTo("The provided entity already exists")));
        }
    }

    @Test
    public void updateWithWrongPackageAccess() {
        // prepare data
        pkg = prepareDataToUpdate(pkg);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.PACKAGE, sqlSessionMock);

        // perform test
        try {
            packageManager.update(pkg.getId(), pkg, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getErrorCode().toString(),
                    is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
            assertThat(e.getMessage(),
                    is(equalTo("The user is not allowed in this context or the requested entity does not exist")));
        }
    }

    @Test
    public void updateWithWrongPlacementsAccess() {
        // prepare data
        pkg = prepareDataToUpdate(pkg);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.PLACEMENT, sqlSessionMock);

        // perform test
        try {
            packageManager.update(pkg.getId(), pkg, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getErrorCode().toString(),
                    is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
            assertThat(e.getMessage(),
                    is(equalTo("The user is not allowed in this context or the requested entity does not exist")));
        }
    }

    @Test
    public void updateWithWrongPlacementsCampaign() {
        // prepare data
        pkg = prepareDataToUpdate(pkg);

        //customize mock's behavior
        when(placementDao.checkPlacementsBelongsCampaignId(anyList(), anyLong(), eq(sqlSessionMock))).thenReturn(
                false);

        // perform test
        try {
            packageManager.update(pkg.getId(), pkg, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getErrorCode().toString(),
                    is(BusinessCode.INVALID.toString()));
            assertThat(e.getMessage(),
                    is(equalTo("Invalid entity or payload")));
        }
    }

    @Test
    public void updateWithPlacementNotStandAlone() {
        // prepare data
        pkg = prepareDataToUpdate(pkg);

        //customize mock's behavior
        when(placementDao.checkPlacementsStandAlone(anyList(), anyLong(), eq(sqlSessionMock))).thenReturn(
                1L);

        // perform test
        try {
            packageManager.update(pkg.getId(), pkg, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            assertThat(e.getErrorCode().toString(),
                    is(BusinessCode.INVALID.toString()));
            assertThat(e.getMessage(),
                    is(equalTo("Invalid entity or payload")));
        }
    }


    /**
     * Bulk Delete Test
     */
    @Test
    public void bulkDeletePackagesWithPlacementsPass() {
        // prepare data
        int totalRecords = 3;
        int placementsForPackage = 2;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // customize mock's behavior
        when(packageDao.get(anyLong(), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createPackage());
        when(placementDao.getPlacementsByPackageId(anyLong(), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createPlacements(placementsForPackage));
        doNothing().when(packagePlacementDao).delete(anyList(), eq(key), eq(sqlSessionMock));
        doNothing().when(dimPackagePlacementDao).delete(anyList(), eq(key), eq(sqlSessionMockDim));

        when(packageCostDetailDao.getAll(anyLong(), eq(sqlSessionMock)))
                .thenReturn(new ArrayList<CostDetail>());
        when(placementDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(new Answer<Placement>() {
            @Override
            public Placement answer(InvocationOnMock invocationOnMock) throws Throwable {
                Long id = (Long) invocationOnMock.getArguments()[0];
                Placement placement = EntityFactory.createPlacement();
                placement.setId(id);
                return placement;
            }
        });
        when(placementDao.update(any(Placement.class), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createPlacement());

        doNothing().when(placementCostDetailDao)
                   .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMock));
        doNothing().when(dimPlacementCostDetailDao)
                   .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMockDim));
        when(placementCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createCostDetail());
        when(dimPlacementCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createCostDetail());
        when(packageDao.delete(anyList(), eq(key.getTpws()), eq(sqlSessionMock))).thenReturn(1);
        when(dimPackageDao.delete(anyList(), eq(key.getTpws()), eq(sqlSessionMock))).thenReturn(1);

        // Perform test
        Either<Errors, String> result = packageManager.bulkDelete(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is("Bulk delete for Package completed successfully."));
        verify(packagePlacementDao, times(totalRecords))
                .delete(anyList(), eq(key), eq(sqlSessionMock));
        verify(dimPackagePlacementDao, times(totalRecords))
                .delete(anyList(), eq(key), eq(sqlSessionMockDim));
        verify(placementCostDetailDao, times(0))
                .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMock));
        verify(dimPlacementCostDetailDao, times(0))
                .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMockDim));
        verify(packagePlacementDao, times(0)).create(anyList(), eq(key), eq(sqlSessionMock));
        verify(dimPackagePlacementDao, times(0)).create(anyList(), eq(key), eq(sqlSessionMockDim));
    }

    @Test
    public void bulkDeletePackagesStandAlonePass() {
        // prepare data
        int totalRecords = 3;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // customize mock's behavior
        when(packageDao.get(anyLong(), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createPackage());
        when(placementDao.getPlacementsByPackageId(anyLong(), eq(sqlSessionMock)))
                .thenReturn(new ArrayList<Placement>());
        doNothing().when(packagePlacementDao).delete(anyList(), eq(key), eq(sqlSessionMock));
        doNothing().when(dimPackagePlacementDao).delete(anyList(), eq(key), eq(sqlSessionMockDim));

        when(packageCostDetailDao.getAll(anyLong(), eq(sqlSessionMock)))
                .thenReturn(new ArrayList<CostDetail>());
        when(placementDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(new Answer<Placement>() {
            @Override
            public Placement answer(InvocationOnMock invocationOnMock) throws Throwable {
                Long id = (Long) invocationOnMock.getArguments()[0];
                Placement placement = EntityFactory.createPlacement();
                placement.setId(id);
                return placement;
            }
        });
        when(placementDao.update(any(Placement.class), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createPlacement());

        doNothing().when(placementCostDetailDao)
                   .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMock));
        doNothing().when(dimPlacementCostDetailDao)
                   .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMockDim));
        when(placementCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createCostDetail());
        when(dimPlacementCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createCostDetail());
        when(packageDao.delete(anyList(), eq(key.getTpws()), eq(sqlSessionMock))).thenReturn(1);
        when(dimPackageDao.delete(anyList(), eq(key.getTpws()), eq(sqlSessionMock))).thenReturn(1);

        // Perform test
        Either<Errors, String> result = packageManager.bulkDelete(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is("Bulk delete for Package completed successfully."));
        verify(packagePlacementDao, times(0))
                .delete(anyList(), eq(key), eq(sqlSessionMock));
        verify(dimPackagePlacementDao, times(0))
                .delete(anyList(), eq(key), eq(sqlSessionMockDim));
        verify(placementCostDetailDao, times(0))
                .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMock));
        verify(dimPlacementCostDetailDao, times(0))
                .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMockDim));
        verify(packagePlacementDao, times(0)).create(anyList(), eq(key), eq(sqlSessionMock));
        verify(dimPackagePlacementDao, times(0)).create(anyList(), eq(key), eq(sqlSessionMockDim));
    }

    @Test
    public void bulkDeletePackageFailedDueNullPayload() {
        // prepare data
        RecordSet<Long> toDelete = null;

        // Perform test
        try {
            packageManager.bulkDelete(toDelete, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "BulkDelete"))));
        }
    }

    @Test
    public void bulkDeletePackageFailedDueEmptyPayload() {
        // prepare data
        List<Long> ids = new ArrayList<>();
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // Perform test
        Either<Errors, String> result = packageManager.bulkDelete(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format("%s cannot be null.", "BulkDelete"))));
    }

    @Test
    public void bulkDeletePackageFailedDueEmptyIdsPayload() {
        // prepare data
        List<Long> ids = new ArrayList<>();
        ids.add(null);
        ids.add(null);
        ids.add(null);
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // Perform test
        Either<Errors, String> result = packageManager.bulkDelete(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %s, it cannot be empty.", "id"))));
    }

    @Test
    public void bulkDeletePackageFailedDueWrongAccess() {
        // prepare data
        int totalRecords = 10;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.PACKAGE, sqlSessionMock);

        // Perform test
        Either<Errors, String> result = packageManager.bulkDelete(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format(
                        "The user is not allowed in this context or the requested entity does not exist"))));
    }

    @Test
    public void bulkDeletePackageFailedDueDataBaseException() {
        // prepare data
        int totalRecords = 10;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // customize mock's behavior
        // customize mock's behavior
        when(packageDao.get(anyLong(), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createPackage());
        when(placementDao.getPlacementsByPackageId(anyLong(), eq(sqlSessionMock)))
                .thenReturn(new ArrayList<Placement>());
        doNothing().when(packagePlacementDao).delete(anyList(), eq(key), eq(sqlSessionMock));
        doNothing().when(dimPackagePlacementDao).delete(anyList(), eq(key), eq(sqlSessionMockDim));

        when(packageCostDetailDao.getAll(anyLong(), eq(sqlSessionMock)))
                .thenReturn(new ArrayList<CostDetail>());
        when(placementDao.get(anyLong(), eq(sqlSessionMock))).thenAnswer(new Answer<Placement>() {
            @Override
            public Placement answer(InvocationOnMock invocationOnMock) throws Throwable {
                Long id = (Long) invocationOnMock.getArguments()[0];
                Placement placement = EntityFactory.createPlacement();
                placement.setId(id);
                return placement;
            }
        });
        when(placementDao.update(any(Placement.class), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createPlacement());

        doNothing().when(placementCostDetailDao)
                   .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMock));
        doNothing().when(dimPlacementCostDetailDao)
                   .remove(any(CostDetail.class), eq(key.getTpws()), eq(sqlSessionMockDim));
        when(placementCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createCostDetail());
        when(dimPlacementCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createCostDetail());
        when(packageDao.delete(anyList(), eq(key.getTpws()), eq(sqlSessionMock))).thenReturn(1);
        doThrow(new PersistenceException("Error querying database.  Cause: java.sql.SQLRecoverableException: No more data to read from socket.")).when(packageDao).delete(anyList(), eq(key.getTpws()), eq(sqlSessionMock));

        // Perform test
        Either<Errors, String> result = packageManager.bulkDelete(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        BusinessError error = (BusinessError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(BusinessCode.class));
        assertThat(error.getCode().toString(), is(BusinessCode.INTERNAL_ERROR.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format(
                "Error querying database.  Cause: java.sql.SQLRecoverableException: No more data to read from socket."))));
    }

    private Package prepareDataToUpdate(Package pkg) {
        pkg.setId(Math.abs(EntityFactory.random.nextLong()));
        pkg.setCostDetails(getCostDetails(3, true));
        for (Placement pl : pkg.getPlacements()) {
            pl.setId(Math.abs(EntityFactory.random.nextLong()));
        }
        return pkg;
    }

    private Answer<RecordSet<Placement>> getPlacements(final Package pack) {
        return new Answer<RecordSet<Placement>>() {
            @Override
            public RecordSet<Placement> answer(InvocationOnMock invocationOnMock) {
                RecordSet<Placement> result = new RecordSet<>();
                result.setRecords(pack.getPlacements());
                return result;
            }
        };
    }

    private Answer<Package> createPackage() {
        return new Answer<Package>() {
            @Override
            public Package answer(InvocationOnMock invocationOnMock) {
                Package result = (Package) invocationOnMock.getArguments()[0];
                result.setId(result.getId() == null ? Math.abs(EntityFactory.random.nextLong()) : result.getId());
                result.setCreatedTpwsKey(key.getTpws());
                result.setModifiedTpwsKey(key.getTpws());
                result.setCreatedDate(new Date());
                result.setModifiedDate(new Date());
                return result;
            }
        };
    }

    private Answer<Package> updatePackage(final Package packageUpdated) {
        return new Answer<Package>() {
            @Override
            public Package answer(InvocationOnMock invocationOnMock) {
                Package result = (Package) invocationOnMock.getArguments()[0];
                result.setId(result.getId() == null ? Math.abs(EntityFactory.random.nextLong()) : result.getId());
                result.setCreatedTpwsKey(key.getTpws());
                result.setModifiedTpwsKey(key.getTpws());
                result.setCreatedDate(new Date());
                result.setModifiedDate(new Date());
                packageUpdated.setCampaignId(result.getCampaignId());
                packageUpdated.setCostDetails(result.getCostDetails());
                packageUpdated.setCountryCurrencyId(result.getCountryCurrencyId());
                packageUpdated.setCreatedDate(result.getCreatedDate());
                packageUpdated.setCreatedTpwsKey(result.getCreatedTpwsKey());
                packageUpdated.setDescription(result.getDescription());
                packageUpdated.setExternalId(result.getExternalId());
                packageUpdated.setId(result.getId());
                packageUpdated.setIoId(result.getIoId());
                packageUpdated.setLogicalDelete(result.getLogicalDelete());
                packageUpdated.setName(result.getName());
                packageUpdated.setPlacements(result.getPlacements());
                return result;
            }
        };
    }

    private Answer<CostDetail> createCostDetail() {
        return new Answer<CostDetail>() {
            @Override
            public CostDetail answer(InvocationOnMock invocationOnMock) throws Throwable {
                CostDetail costDetail = (CostDetail) invocationOnMock.getArguments()[0];
                costDetail.setId(costDetail.getId() == null ? EntityFactory.random.nextLong() : costDetail.getId());
                costDetail.setCreatedTpwsKey(key.getTpws());
                costDetail.setModifiedTpwsKey(key.getTpws());
                costDetail.setCreatedDate(new Date());
                costDetail.setModifiedDate(new Date());
                return costDetail;
            }
        };
    }

    private Answer<Void> createPackageDim() {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Package result = (Package) invocation.getArguments()[0];
                result.setLogicalDelete("N");
                dimPackage = result;
                return null;
            }
        };
    }

    private static Answer<CostDetail> createCostDetail(final Map<Long, CostDetail> created) {
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

    private static Answer<CostDetail> updateCostDetail(final Map<Long, CostDetail> updated) {
        return new Answer<CostDetail>() {
            @Override
            public CostDetail answer(InvocationOnMock invocation) {
                CostDetail result = (CostDetail) invocation.getArguments()[0];
                updated.put(result.getId(), result);
                return result;
            }
        };
    }

    private Answer<Void> removeCostDetail(final Map<Long, CostDetail> deleted) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                CostDetail result = (CostDetail) invocation.getArguments()[0];
                deleted.put(result.getId(), result);
                return null;
            }
        };
    }

    private Answer<Void> removePackagePlacement(final Map<Long, PackagePlacement> placementsDeleted) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                List<PackagePlacement> result = (List<PackagePlacement>) invocation.getArguments()[0];
                for (PackagePlacement packPlac : result) {
                    placementsDeleted.put(packPlac.getId(), packPlac);
                }
                return null;
            }
        };
    }

    private List<CostDetail> getCostDetails(int numberOfCostDetails, boolean fromDB) {
        List<CostDetail> result = new ArrayList<>();
        RateTypeEnum rateType;
        for (int i = 0; i < numberOfCostDetails; i++) {
            CostDetail costDetail = EntityFactory.createCostDetail();
            costDetail.setForeignId(pkg.getId());
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

    @Test
    public void testDisassociatePlacementFromPackageShouldSucceed() {
        // Mock Placement with Package
        pkg.setId(Math.abs(EntityFactory.random.nextLong()));
        pkg.setCostDetails(EntityFactory.createCostDetailList(5));
        final List<Placement> placements = EntityFactory.createPlacements(10);
        // Assign Package Id to all Placements
        for(Placement p : placements) {
            p.setPackageId(pkg.getId());
        }
        Placement placementToRemove = placements.get(
                Math.abs(EntityFactory.random.nextInt(placements.size() - 1)));
        when(placementDao.get(eq(placementToRemove.getId()),
                eq(sqlSessionMock))).thenReturn(placementToRemove);
        when(placementDao.getPlacementsByPackageId(eq(pkg.getId()),
                eq(sqlSessionMock))).thenAnswer(
                new Answer<ArrayList<Placement>>() {
                    @Override
                    public ArrayList<Placement> answer(InvocationOnMock invocationOnMock)
                            throws Throwable {
                        ArrayList<Placement> result = new ArrayList<>(placements.size());
                        for (Placement p : placements) {
                            result.add(new Placement(p.getId(),
                                    p.getSiteId(),
                                    p.getSiteSectionId(),
                                    p.getIoId(),
                                    p.getSizeId(),
                                    p.getStartDate(),
                                    p.getEndDate(),
                                    p.getInventory(),
                                    p.getRate(),
                                    p.getRateType(),
                                    p.getMaxFileSize(),
                                    p.getIsSecure(),
                                    p.getCreatedTpwsKey(),
                                    p.getIsTrafficked(),
                                    p.getResendTags(),
                                    p.getUtcOffset(),
                                    p.getSmEventId(),
                                    p.getCountryCurrencyId(),
                                    p.getName()));
                        }
                        return result;
                    }
                });
        when(packageDao.get(eq(pkg.getId()),
                eq(sqlSessionMock))).thenAnswer(new Answer<Package>() {
            @Override
            public Package answer(InvocationOnMock invocationOnMock) throws Throwable {
                Package _pkg = new Package();
                _pkg.setId(pkg.getId());
                _pkg.setName(pkg.getName());
                _pkg.setCostDetails(pkg.getCostDetails());

                return _pkg;
            }
        });
        when(placementCostDetailDao.create(any(CostDetail.class), eq(sqlSessionMock))).thenAnswer(
                new Answer<CostDetail>() {
                    @Override
                    public CostDetail answer(InvocationOnMock invocationOnMock) throws Throwable {
                        CostDetail cd = (CostDetail) invocationOnMock.getArguments()[0];
                        cd.setId(Math.abs(EntityFactory.random.nextLong()));
                        return cd;
                    }
                });
        // Pick Random Placement to disassociate

        Either<Errors, Void> result = packageManager.disassociateFromPackage(placementToRemove.getId(), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(nullValue()));
        // Assert package-placement relationships were affected correctly

    }

    @Test
    public void testDisassociatePlacementWithNoPackageShouldFail() {
        // Mock Placement with no Package
        long placementId = Math.abs(EntityFactory.random.nextLong());
        placement = EntityFactory.createPlacement();
        placement.setId(placementId);
        placement.setPackageId(null);
        when(placementDao.get(eq(placement.getId()), eq(sqlSessionMock))).thenReturn(placement);

        Either<Errors, Void> result = packageManager.disassociateFromPackage(placementId, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        String expected = String.format(
                "The placement with id '%s' does not have any Package associated",
                placement.getId());
        assertThat(result.error().getErrors().iterator().next().getMessage(), is(expected));
    }

    @Test
    public void testDisassociatePlacementWithPackageUserWithNoAccessShouldFail() {
        long placementId = Math.abs(EntityFactory.random.nextLong());
        placement.setId(placementId);
        MockAccessControlUtil.disallowAccessForUser(
                accessControlMockito,
                AccessStatement.PLACEMENT,
                Collections.singletonList(placement.getId()),
                sqlSessionMock);

        Either<Errors, Void> result = packageManager.disassociateFromPackage(placementId, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        String expected = "The user is not allowed in this context or the requested entity does not exist";
        assertThat(result.error().getErrors().iterator().next().getMessage(), is(expected));
    }

    @Test
    public void testDisassociatePackageWithSinglePlacementShouldFail() {
        // Mock Package with single Placement
        long placementId = Math.abs(EntityFactory.random.nextLong());
        placement = EntityFactory.createPlacement();
        placement.setId(placementId);
        placement.setPackageId(pkg.getId());
        when(packageDao.get(
                eq(placement.getPackageId()),
                eq(sqlSessionMock))).thenReturn(pkg);

        when(placementDao.get(eq(placement.getId()), eq(sqlSessionMock))).thenReturn(placement);

        List<Placement> placements = new ArrayList<>();
        placements.add(placement);
        when(placementDao.getPlacementsByPackageId(eq(pkg.getId()),
                eq(sqlSessionMock))).thenReturn(placements);

        Either<Errors, Void> result = packageManager.disassociateFromPackage(placement.getId(), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        String expected = String.format(
                "Placement with id '%s' cannot be disassociated from Package " +
                        "'%s' as it is its only Placement",
                placement.getId(),
                pkg.getId());
        assertThat(result.error().getErrors().iterator().next().getMessage(), is(expected));
    }

    @Test
    public void testGetPackagesByCampaignAndIoIdInvalidCampaign() {
        long ioId = Math.abs(EntityFactory.random.nextLong());
        Long startIndex = 1L;
        Long pageSize = 1L;
        try {
            Either<Errors, RecordSet<Package>> result = packageManager.getPackagesByCampaignAndIoId
                    (null, ioId ,startIndex, pageSize, key);
            fail("Campaign Id cannot be null.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(equalTo("Campaign Id cannot be null.")));
        }
    }

    @Test
    public void testGetPackagesByCampaignAndIoIdInvalidIoId() {
        Long campaignId = EntityFactory.random.nextLong();
        Long startIndex = 1L;
        Long pageSize = 1L;
        Either<Errors, RecordSet<Package>> result = packageManager.getPackagesByCampaignAndIoId
                (campaignId, null ,startIndex, pageSize, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getMessage(), is(equalTo(
                "ioId cannot be null.")));
    }

    @Test
    public void testGetPackagesByCampaignAndIodNoRelationships() {
        InsertionOrder io = EntityFactory.createInsertionOrder();
        io.setId(EntityFactory.random.nextLong());
        io.setCampaignId(EntityFactory.random.nextLong());
        Long campaignId = EntityFactory.random.nextLong();
        Long startIndex = 1L;
        Long pageSize = 1L;

        when(insertionOrderDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(io);
        Either<Errors, RecordSet<Package>> result = packageManager.getPackagesByCampaignAndIoId
                (campaignId, io.getId() ,startIndex, pageSize, key);
        assertThat(result, is(notNullValue()));

        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getMessage(), is(equalTo("The ioId has no relationship with the provided campaignId.")));
    }

    @Test
    public void testGetPackagesByCampaignAndIod() {
        Long campaignId = EntityFactory.random.nextLong();
        InsertionOrder io = EntityFactory.createInsertionOrder();
        io.setId(EntityFactory.random.nextLong());
        io.setCampaignId(campaignId);

        Long startIndex = 1L;
        Long pageSize = 1L;
        Package pack = EntityFactory.createPackage();
        List<Package> packages = new ArrayList<>();
        packages.add(pack);

        when(insertionOrderDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(io);
        when(packageDao.getCountPackageByCampaignAndIoId(anyLong(), anyLong(), eq(sqlSessionMock))).thenReturn(1L);
        when(packageDao.getPackageByCampaignAndIoId(anyLong(), anyLong(), anyLong(), anyLong(), eq(sqlSessionMock))).thenReturn(packages);

        Either<Errors, RecordSet<Package>> result = packageManager.getPackagesByCampaignAndIoId
                (campaignId, io.getId() ,startIndex, pageSize, key);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(notNullValue()));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(1)));
        assertThat(result.success().getRecords().size(), is(equalTo(1)));

    }
}

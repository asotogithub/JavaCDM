package trueffect.truconnect.api.crud.service.importexport;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.model.importexport.CostDetailRawDataView;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.model.importexport.PackageMapId;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.service.AbstractManagerTest;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Unit test for Cost Detail validation on Media Import
 * @author Marcelo Heredia
 */
public class MediaImportExportHelperCostValidationTest extends AbstractManagerTest{

    private List<MediaRawDataView> rows;
    private MediaImportExportHelper mediaHelper;
    private PackageDaoExtended packageDao;
    private int numberOfCostDetails;
    private int numberOfPackages;
    private int numberOfPlacementsPerPackage;
    private int numberOfStandalonePlacements;
    private Map<PackageMapId, List<MediaRawDataView>> packagesMap;

    @Before
    public void before() {
        packageDao = mock(PackageDaoExtended.class);
        mediaHelper = new MediaImportExportHelper(null, null, null,
                null, null, null,
                packageDao, null, null,
                null, null, null,
                null, null, null, key);
        // Prepare data
        numberOfPackages = 2;
        numberOfPlacementsPerPackage = 2;
        numberOfStandalonePlacements = 1;
        numberOfCostDetails = 4;
        rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails, false);
        rows = mediaHelper.doUnflatten(rows);
        packagesMap = mediaHelper.doPackageMap(rows);
    }

    @Test
    public void testUnflattenMediaRawDataViewListSuccess(){
        List<MediaRawDataView> list = EntityFactory.createMediaRawDataViewList(2, 4, 3, 4, false);
        List<MediaRawDataView> result = mediaHelper.doUnflatten(list);
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(11));
        for(MediaRawDataView row : result) {
            assertThat(row.getCostDetails(), is(notNullValue()));
            assertThat(row.getCostDetails().size(), is(4));
        }
    }

    @Test
    public void testValidateModelPackageAndStandalonePlacementWithAllValidCostDetailsShouldSuccess() {
        // Perform test
        List<MediaRawDataView> result = mediaHelper.doModelValidation(rows, packagesMap);
        assertThat(result, is(notNullValue()));
        for(MediaRawDataView row : result) {
            assertThat(row.getIssues(), is(notNullValue()));
            assertThat(row.getIssues().size(), is(0));
        }
    }

    @Test
    public void testValidateModelPackageAndStandalonePlacementWithNoLastCostDetailShouldSuccess() {
        // Remove last cost detail for all Packages and Standalone Placement
        int totalRows = numberOfPackages * numberOfPlacementsPerPackage + numberOfStandalonePlacements;
        for(int i = 0; i < totalRows; i++) {
            rows.get(i).getCostDetails().remove(3);
        }
        // Perform test
        List<MediaRawDataView> result =
                mediaHelper.doModelValidation(rows, packagesMap);
        assertThat(result, is(notNullValue()));
        for(int i = 0; i < totalRows; i++) {
            if(i % 2 == 0) { // Only the rows 0, 2, and 4 are in error
                assertThat(result.get(i).getIssues(), is(notNullValue()));
                assertThat(result.get(i).getIssues().size(), is(0));
            }
        }
    }

    @Test
    public void testValidateModelPackageWithFirstCostsOKButOthersInErrorShouldFail() {
        // Prepare data with no standalone Placements
        numberOfPackages = 3;
        numberOfPlacementsPerPackage = 4;
        numberOfStandalonePlacements = 0;
        List<MediaRawDataView> dataViews =
                EntityFactory.createMediaRawDataViewList(numberOfPackages,
                        numberOfPlacementsPerPackage, numberOfStandalonePlacements,
                        numberOfCostDetails, false);

        rows = mediaHelper.doUnflatten(dataViews);
        // First Package. Remove last cost detail in fourth Placement for first Package
        rows.get(3).getCostDetails().remove(3);
        // Third Package. Modify the last cost detail's start date to not be consecutive (thus, not the
        // same as the first group of costs). This applies to last Placement of Third Package
        Date d = rows.get(11).getCostDetails().get(3).getStartDateDt();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_YEAR, 1);
        rows.get(11).getCostDetails().get(3).setStartDate(
                DateConverter.importExportFormat(c.getTime()));
        System.out.println("Input list:\n" + EntityFactory.renderMediaRawDataView(rows));
        // Perform test
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(dataViews);
        List<MediaRawDataView> result =
                mediaHelper.doModelValidation(rows, packagesMap);

        System.out.println("-----------------------------------------\n" +
                "Output list:\n" + EntityFactory.renderMediaRawDataView(result));
        // Perform assertions
        // First Package, all Placements should be in error
        assertGroupOfCosts(result, 0, 3, 1);
        // Second Package, none of the Placements should be in error
        assertGroupOfCosts(result, 4, 7, 0);
        // Third Package, all Placements should be in error
        assertGroupOfCosts(result, 8, 11, 1);
    }

    private void assertGroupOfCosts(List<MediaRawDataView> rows,
                                    int from,
                                    int to,
                                    int expectedIssuesNumber) {
        for(int i = from; i < to; i++) {
            MediaRawDataView row = rows.get(i);
            assertThat(row.getIssues(), is(notNullValue()));
            assertThat("Issues are: " + row.getIssues(), row.getIssues().size(), is(equalTo(expectedIssuesNumber)));
            if(expectedIssuesNumber > 0) {
                assertThat(row.getIssues().values().iterator().next().iterator().next().getMessage(),
                        is(equalTo(
                                "The cost & flight details for this placement do not match with " +
                                        "the other placements within this package")));

            }
        }
    }

    @Test
    public void testValidateModelPackageWithMixedUpCostDetailsShouldFail() {
        // Perform test that it doesn't get errors with the original Cost Details
        List<MediaRawDataView> result = mediaHelper.doModelValidation(rows, packagesMap);
        assertThat(result, is(notNullValue()));
        assertThat(result.get(0).getIssues().size(), is(0));

        // Mix up Cost Details for First group of costs
        List<CostDetailRawDataView> costDetails = rows.get(0).getCostDetails();
        CostDetailRawDataView first = costDetails.get(0);
        int lastPosition = costDetails.size() - 1;
        CostDetailRawDataView last = costDetails.get(lastPosition);
        costDetails.remove(lastPosition);
        costDetails.remove(0);
        Collections.shuffle(costDetails);
        costDetails.add(last);
        costDetails.add(first);
        // Execute validations
        result = mediaHelper.doModelValidation(rows, packagesMap);
        assertThat(result.get(0).getIssues().size(), is(1));
        assertThat(result.get(0).getIssues().values().iterator().next().iterator().next().getMessage(), containsString(
                "One or more Cost Details have errors"));
    }

    @Test
    public void testValidateModelPackageWithWrongDateIntervalsShouldFail() {
        // Make sure to mess up a date moving it back to 1 year
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        // Mess up date ranges of 2nd cost from first Package
        List<CostDetailRawDataView> costDetails = rows.get(0).getCostDetails();
        // Modifying the second position
        costDetails.get(2).setStartDate(DateConverter.importExportFormat(calendar.getTime()));
        Collections.shuffle(costDetails);
        // Perform test
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(rows);
        List<MediaRawDataView> result = mediaHelper.doModelValidation(rows, packagesMap);
        assertThat(result, is(notNullValue()));
        assertThat(result.get(0).getIssues(), is(notNullValue()));
        assertThat(result.get(0).getIssues().size(), is(equalTo(1)));
        assertThat(result.get(0).getIssues().values().iterator().next().iterator().next().getMessage(), containsString(
                "One or more Cost Details have errors"));
        assertThat(result.get(1).getIssues(), is(notNullValue()));
        assertThat(result.get(1).getIssues().size(), is(equalTo(0)));
    }

    @Test
    public void doPkgCostDetailsDataValidationEqualsCostsPass() {
        // Prepare data
        numberOfPackages = 2;
        numberOfPlacementsPerPackage = 1;
        numberOfStandalonePlacements = 1;
        numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails, false);
        rows = mediaHelper.doUnflatten(rows);

        // Customize mock's behavior
        when(packageDao.getMediaPackageByPackageNames(anyList(), anyLong(), eq(sqlSessionMock)))
                .thenAnswer(getCostDetailByPackageNames(true, rows));

        // Perform test
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(rows);
        mediaHelper.doPackageDataValidation(1L, packagesMap, key.getUserId(), sqlSessionMock);
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            if (row.getMediaPackageName() != null && !row.getMediaPackageName().isEmpty()) {
                assertThat(row.getMediaPackageId(), is(nullValue()));
                assertThat(row.getMediaPackageIdDb(), is(notNullValue()));
            }
            assertThat(row.getIssues().size(), is(0));
        }
    }

    @Test
    public void doPkgCostDetailsDataValidationMistmatchingCosts() {
        // Prepare data
        numberOfPackages = 2;
        numberOfPlacementsPerPackage = 1;
        numberOfStandalonePlacements = 1;
        numberOfCostDetails = 2;
        List<MediaRawDataView> rows = EntityFactory.createMediaRawDataViewList(numberOfPackages,
                numberOfPlacementsPerPackage, numberOfStandalonePlacements, numberOfCostDetails, false);
        rows = mediaHelper.doUnflatten(rows);

        // Customize mock's behavior
        when(packageDao.getMediaPackageByPackageNames(anyList(), anyLong(), eq(sqlSessionMock)))
                .thenAnswer(getCostDetailByPackageNames(false, rows));

        // Perform test
        Map<PackageMapId, List<MediaRawDataView>> packagesMap = mediaHelper.doPackageMap(rows);
        mediaHelper.doPackageDataValidation(1L, packagesMap, key.getUserId(), sqlSessionMock);
        for (MediaRawDataView row : rows) {
            assertThat(row, is(notNullValue()));
            if (StringUtils.isNotBlank(row.getMediaPackageName())) {
                assertThat(row.getMediaPackageId(), is(nullValue()));
                assertThat(row.getIssues(), is(notNullValue()));
                assertThat(row.getIssues().size(), is(equalTo(1)));
                assertThat(
                        row.getIssues().values().iterator().next().iterator().next().getMessage(),
                        containsString(
                                "The cost detail for placement that you are trying to add to a package needs to match the cost detail with that package"));
            }
        }
    }

    private Answer<List<MediaRawDataView>> getCostDetailByPackageNames(final boolean isEqual,
                                                                       final List<MediaRawDataView> mediaRaws) {
        return new Answer<List<MediaRawDataView>>() {
            @Override
            public List<MediaRawDataView> answer(InvocationOnMock invocation) {
                List<String> packageNames = (List<String>) invocation.getArguments()[0];
                Long campaignId = (Long) invocation.getArguments()[1];

                List<MediaRawDataView> result = new ArrayList<>();
                for (String packageName : packageNames) {
                    for (MediaRawDataView row : mediaRaws) {
                        if (packageName.equalsIgnoreCase(row.getMediaPackageName())) {
                            MediaRawDataView view = new MediaRawDataView();
                            view.setMediaPackageId(Math.abs(EntityFactory.random.nextLong()) + "");
                            view.setMediaPackageName(row.getMediaPackageName());
                            List<CostDetailRawDataView> costs = new ArrayList<>();
                            for (CostDetailRawDataView cost : row.getCostDetails()) {
                                CostDetailRawDataView newCost =
                                        new CostDetailRawDataView(cost.getPlannedAdSpend(),
                                                cost.getInventory(), cost.getRate(),
                                                cost.getRateType(), cost.getStartDate(),
                                                cost.getEndDate());
                                costs.add(newCost);
                            }
                            view.setCostDetails(costs);
                            result.add(view);
                            if (!isEqual) {
                                CostDetailRawDataView cost = row.getCostDetails().get(0);
                                Long newRate = Long.valueOf(cost.getRate()) + 5L;
                                cost.setRate(newRate + "");
                            }
                            break;
                        }
                    }
                }
                return result;
            }
        };
    }
}

package trueffect.truconnect.api.crud.service.importexport;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.model.importexport.XLSTemplateDescriptor;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.service.AbstractManagerTest;
import trueffect.truconnect.api.crud.service.ImportExportManager;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richard.jaldin on 6/22/2016.
 */
public class MediaExporterTest extends AbstractManagerTest {

    private Long campaignId;
    private User user;
    private List<MediaRawDataView> placements;
    private Map<Long, List<MediaRawDataView>> placementCostDetails;
    private Map<Long, List<MediaRawDataView>> packageCostDetails;

    private PlacementDao placementDao;
    private CostDetailDaoExtended placementCostDetailDao;
    private CostDetailDaoExtended packageCostDetailDao;

    private MediaExporter exporter;

    @Before
    public void setUp() {
        placementDao = mock(PlacementDao.class);
        placementCostDetailDao = mock(CostDetailDaoExtended.class); //CostDetailType.PLACEMENT
        packageCostDetailDao = mock(CostDetailDaoExtended.class); //CostDetailType.PACKAGE
        CampaignDao campaignDao = mock(CampaignDao.class);

        campaignId = Math.abs(EntityFactory.random.nextLong());
        user = EntityFactory.createUser();
        user.setUserName(key.getUserId());
        placements = new ArrayList<>();
        placementCostDetails = new HashMap<>();
        packageCostDetails = new HashMap<>();

        exporter = new MediaExporter(campaignDao, placementDao, placementCostDetailDao,
                packageCostDetailDao, campaignId, key,
                ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW,
                ImportExportManager.XLSX_TEMPLATE_MEDIA_PATH, MediaRawDataView.class,
                accessControlMockito);

        sqlSessionMock = mock(SqlSession.class);

        // Package + Costs
        String[] packageNames = new String[]{
                EntityFactory.faker.letterify("???????????"),
                EntityFactory.faker.letterify("???????????"),
                EntityFactory.faker.letterify("???????????"),
                EntityFactory.faker.letterify("???????????"),
                EntityFactory.faker.letterify("???????????")};
        for (String packageName : packageNames) {
            Long packageId = Math.abs(EntityFactory.random.nextLong());
            for (int i = 0; i < 10; i++) {
                MediaRawDataView raw = EntityFactory.createMediaRawDataView();
                raw.setPlacementId(String.valueOf(placements.size() + 1));
                raw.setMediaPackageId(String.valueOf(packageId));
                raw.setMediaPackageName(packageName);
                populateCosts(true, raw); // empty costs
                placements.add(raw);
                if(i == 0) {
                    for (int j = 0; j < 10; j++) {
                        if (packageCostDetails.get(packageId) == null) {
                            packageCostDetails.put(packageId, new ArrayList<MediaRawDataView>());
                        }
                        MediaRawDataView cost = new MediaRawDataView();
                        cost.setMediaPackageId(String.valueOf(packageId));
                        populateCosts(false, cost);
                        packageCostDetails.get(packageId).add(cost);
                    }
                }
            }
        }

        // Placements + costs
        for (int i = 0; i < 100; i++) {
            MediaRawDataView raw = EntityFactory.createMediaRawDataView();
            raw.setPlacementId(String.valueOf(i + 51));
            populateCosts(true, raw); // empty costs
            placements.add(raw);
            for (int j = 0; j < 5; j++) {
                if(placementCostDetails.get(Long.valueOf(raw.getPlacementId())) == null){
                    placementCostDetails.put(Long.valueOf(raw.getPlacementId()), new ArrayList<MediaRawDataView>());
                }
                MediaRawDataView cost = new MediaRawDataView();
                cost.setPlacementId(raw.getPlacementId());
                populateCosts(false, cost); // not empty costs
                placementCostDetails.get(Long.valueOf(raw.getPlacementId())).add(cost);
            }
        }

        //mock behaviors - DAC
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN,
                sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.PLACEMENT,
                sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.PACKAGE,
                sqlSessionMock);

        // Mock DAOs
        when(placementDao.getMediaForExport(
                eq(campaignId),
                eq(sqlSessionMock))).thenAnswer(new Answer<List<MediaRawDataView>>() {
            @Override
            public List<MediaRawDataView> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return placements;
            }
        });

        when(placementCostDetailDao.getCostsForExport(
                anyList(),
                eq(sqlSessionMock))).thenAnswer(getCostDetails(placementCostDetails));

        when(packageCostDetailDao.getCostsForExport(
                anyList(),
                eq(sqlSessionMock))).thenAnswer(getCostDetails(packageCostDetails));
    }

    @Test
    public void testGetDataOk() throws Exception {
        List<? extends XLSTemplateDescriptor> rows = exporter.getData(sqlSessionMock);
        assertThat(rows, is(notNullValue()));
        assertThat(rows.size(), is(1000));
    }

    private void populateCosts(boolean hasEmptyCosts, MediaRawDataView record) {
        if(hasEmptyCosts) {
            record.setPlannedAdSpend(null);
            record.setInventory(null);
            record.setRate(null);
            record.setRateType(null);
            record.setStartDate(null);
            record.setEndDate(null);
        } else {
            record.setPlannedAdSpend(EntityFactory.faker.numerify("######"));
            record.setRate(EntityFactory.faker.numerify("######"));
            record.setRateType(EntityFactory.faker.options().option(new String[]{
                    String.valueOf(RateTypeEnum.CPA.getCode()),
                    String.valueOf(RateTypeEnum.CPC.getCode()),
                    String.valueOf(RateTypeEnum.CPL.getCode()),
                    String.valueOf(RateTypeEnum.CPM.getCode()),
                    String.valueOf(RateTypeEnum.FLT.getCode())}));

            Calendar calendar = Calendar.getInstance();
            record.setStartDate(
                    DateConverter
                            .importExportFormat(
                                    DateConverter.startDate(calendar.getTime())));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            record.setEndDate(
                    DateConverter.importExportFormat(
                            DateConverter.endDate(calendar.getTime())));
        }
    }

    private Answer<List<MediaRawDataView>> getCostDetails(
            final Map<Long, List<MediaRawDataView>> costDetails) {
        return new Answer<List<MediaRawDataView>>() {
            @Override
            public List<MediaRawDataView> answer(InvocationOnMock invocationOnMock)
                    throws Throwable {
                Collection<Long> ids = (Collection<Long>) invocationOnMock.getArguments()[0];
                List<MediaRawDataView> result = new ArrayList<>();
                for (Long id : ids) {
                    result.addAll(costDetails.get(id));
                }
                return result;
            }
        };
    }
}

package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.Organization;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AdmTransactionDao;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.AgencyDao;
import trueffect.truconnect.api.crud.dao.DatasetConfigDao;
import trueffect.truconnect.api.crud.dao.DimCostDetailDao;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgencyManagerTest extends AbstractManagerTest {

    private AgencyManager manager;
    private AgencyDao agencyDao;
    private DatasetConfigDao datasetConfigDao;
    private AdmTransactionDao admTransactionDao;
    private Agency ag;
    private AdvertiserDao advertiserDao;
    private DimCostDetailDao dimCostDetailDao;
    private PackageDaoExtended dimPackageDao;

    @Before
    public void setUp() throws Exception {
        agencyDao = mock(AgencyDao.class);
        advertiserDao = mock(AdvertiserDao.class);
        datasetConfigDao = mock(DatasetConfigDao.class);
        admTransactionDao = mock(AdmTransactionDao.class);
        dimCostDetailDao = mock(DimCostDetailDao.class);
        dimPackageDao = mock(PackageDaoExtended.class);
        manager = new AgencyManager(agencyDao, datasetConfigDao, admTransactionDao, 
                advertiserDao, dimCostDetailDao, dimPackageDao, accessControlMockito);
        ag = EntityFactory.createAgency();

        when(agencyDao.openSession()).thenReturn(sqlSessionMock);
        when(agencyDao.getExistsAgency(anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(agencyDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(ag);
        when(agencyDao.saveOrganization(any(Organization.class), eq(sqlSessionMock))).
                thenReturn(EntityFactory.random.nextLong());
        when(agencyDao.getCampaigns(anyLong(), anyString(), eq(sqlSessionMock))).thenReturn(createCampaignDtos(5));
        doNothing().when(agencyDao).hardRemove(anyLong(), eq(sqlSessionMock));

    }

    private RecordSet<CampaignDTO> createCampaignDtos(int i) {
        RecordSet<CampaignDTO> result = new RecordSet<>();
        List<CampaignDTO> campaigns = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            CampaignDTO campaign = EntityFactory.createCampaignDto();
            campaigns.add(campaign);
        }
        result.setRecords(campaigns);
        return result;
    }

    @Test
    public void testSaveAndGet() throws Exception {
        ag.setId(null);
        when(agencyDao.save(
                any(Agency.class),
                anyLong(),
                eq(sqlSessionMock))).thenReturn(ag);
        ag = manager.save(ag, key);
        assertNotNull(ag);
    }

    @Test
    public void testUpdate() throws Exception {
        when(agencyDao.update(
                any(Agency.class),
                eq(sqlSessionMock))).thenReturn(ag);
        ag = manager.update(ag, ag.getId(), key);
        assertNotNull(ag);
    }

    @Test
    public void testremove() throws Exception {
        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);

        SuccessResponse sr = manager.hardRemove(ag.getId(), key);
        assertNotNull(sr);
    }
    @Test
    public void testgetCampaigns() throws Exception {
        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);

        RecordSet<CampaignDTO> campaigns = manager.getCampaigns(ag.getId(), key);
        assertNotNull(campaigns);
    }

    @Test
    public void gettingDatasetsShouldOnlyReturnConfigWhereUserHasCorrectAdvertiserPermissions() throws Exception {
        Long agencyId = EntityFactory.random.nextLong();
        when(datasetConfigDao.getDatasets(agencyId)).thenReturn(EntityFactory.createDatasetConfigList(2));
        when(accessControlMockito.isUserValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyId), key.getUserId(), sqlSessionMock)).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(EntityFactory.createAdvertiser());
        Either<Error, RecordSet<DatasetConfigView>> datasets = manager.getDatasets(agencyId, key);
        assertThat(datasets.isSuccess(), is(true));
        assertThat(datasets.success().getRecords(), is(empty()));
    }

    @Test
    public void gettingDatasetsWithoutCorrectAgencyPermissionsShouldReturnError() throws Exception {
        Long agencyId = EntityFactory.random.nextLong();
        when(datasetConfigDao.getDatasets(agencyId)).thenReturn(EntityFactory.createDatasetConfigList(2));
        when(accessControlMockito.isUserValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyId), key.getUserId(), sqlSessionMock)).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(EntityFactory.createAdvertiser());
        Either<Error, RecordSet<DatasetConfigView>> datasets = manager.getDatasets(agencyId, key);
        assertThat(datasets.isError(), is(true));
        assertThat(datasets.error().getCode().getNumber(), is(equalTo(SecurityCode.PERMISSION_DENIED.getNumber())));
    }

    @Test
    public void gettingDatasetsShouldReturnSuccessful() throws Exception {
        Long agencyId = EntityFactory.random.nextLong();
        when(datasetConfigDao.getDatasets(agencyId)).thenReturn(EntityFactory.createDatasetConfigList(2));
        when(accessControlMockito.isUserValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyId), key.getUserId(), sqlSessionMock)).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(EntityFactory.createAdvertiser());
        Either<Error, RecordSet<DatasetConfigView>> datasets = manager.getDatasets(agencyId, key);
        assertThat(datasets.isSuccess(), is(true));
        assertThat(datasets.success().getRecords(), hasSize(2));
    }
}

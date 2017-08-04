package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementCampaignDTO;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementCampaignType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteMeasurementCampaignDao;
import trueffect.truconnect.api.crud.EntityFactory;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Abel Soto
 */
public class SiteMeasurementCampaignManagerTest extends AbstractManagerTest {

    private SiteMeasurementCampaignManager smcManager;
    private SiteMeasurementCampaignDao smcDao;
    
    private SiteMeasurementCampaignDTO smcDto;
    private List<SiteMeasurementCampaignDTO> smcs;
    private Long measurementId;

    @Before
    public void init() throws Exception {
        smcDao = mock(SiteMeasurementCampaignDao.class);
        smcManager = new SiteMeasurementCampaignManager(smcDao , accessControlMockito);

        measurementId = Math.abs(EntityFactory.random.nextLong());
        smcs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SiteMeasurementCampaignDTO dto = EntityFactory.createSiteMeasurementCampaign();
            dto.setMeasurementId(measurementId);
            smcs.add(dto);
        }
        smcDto = EntityFactory.createSiteMeasurementCampaign();
        smcDto.setMeasurementId(measurementId);
        smcs.add(smcDto);


        //mock behaviors - session
        when(smcDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(smcDao).commit(any(SqlSession.class));
        doNothing().when(smcDao).close(any(SqlSession.class));

        //mock behaviors - DAC
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.SITE_MEASUREMENT, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN,
                sqlSessionMock);

        //mock behaviors
        when(smcDao.getAssociatedCampaignsForSiteMeasurement(anyLong(), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(new Answer<List<SiteMeasurementCampaignDTO>>() {
            @Override
            public List<SiteMeasurementCampaignDTO> answer(InvocationOnMock invocation) {
                Long id = (Long) invocation.getArguments()[0];
                List<SiteMeasurementCampaignDTO> result = new ArrayList<>();
                for (SiteMeasurementCampaignDTO smc : smcs) {
                    if (smc.getMeasurementId().longValue() == id.longValue()) {
                        result.add(smc);
                    }
                }
                return result;
            }
        });
        when(smcDao.getCountAssociatedCampaignsForSiteMeasurement(anyLong(),
                eq(sqlSessionMock))).thenReturn(new Long(smcs.size()));
        doNothing().when(smcDao).remove(eq(smcDto), eq(sqlSessionMock));
    }

    @Test
    public void testGetListSiteMeasurementCampaigns() {
        Either<Error, RecordSet<SiteMeasurementCampaignDTO>> result = smcManager
                .getCampaignsForSiteMeasurement(measurementId,
                        SiteMeasurementCampaignType.ASSOCIATED, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(smcs.size())));
    }

    @Test
    public void testRemoveSiteMeasurementCampaign() {
        when(smcDao.getAssociatedCampaignsForSiteMeasurement(anyLong(), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenReturn(smcs);
        when(smcDao.getCountAssociatedCampaignsForSiteMeasurement(anyLong(),
                eq(sqlSessionMock))).thenReturn(new Long(smcs.size()));

        final List<SiteMeasurementCampaignDTO> param = new ArrayList<>();
        param.addAll(smcs);
        param.remove(smcDto);
        Either<Error, RecordSet<SiteMeasurementCampaignDTO>> result = smcManager.save(measurementId, new RecordSet<>(param), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(5)));

        when(smcDao.getAssociatedCampaignsForSiteMeasurement(anyLong(), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenReturn(param);
        when(smcDao.getCountAssociatedCampaignsForSiteMeasurement(anyLong(),
                eq(sqlSessionMock))).thenReturn(new Long(param.size()));

        result = smcManager.getCampaignsForSiteMeasurement(measurementId,
                SiteMeasurementCampaignType.ASSOCIATED, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(5)));
    }

}
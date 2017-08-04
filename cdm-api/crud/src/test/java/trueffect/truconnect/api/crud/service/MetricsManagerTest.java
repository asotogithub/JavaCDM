package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.MetricsDao;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public class MetricsManagerTest extends AbstractManagerTest {

    private MetricsManager manager;
    private MetricsDao metricsDaoEDW;
    private MetricsDao metricsDaoForUserValidation;

    private static Long campaignId = EntityFactory.random.nextLong();
    private static final Long agency_id = EntityFactory.random.nextLong();

    @Before
    public void setUp() throws Exception {
        metricsDaoEDW = mock(MetricsDao.class);
        metricsDaoForUserValidation = mock(MetricsDao.class);
        manager = new MetricsManager(metricsDaoForUserValidation, metricsDaoEDW, accessControlMockito);
        when(metricsDaoEDW.openSession()).thenReturn(sqlSessionMock);
        when(metricsDaoForUserValidation.openSession()).thenReturn(sqlSessionMock);
        when(metricsDaoEDW.openSession(any(ExecutorType.class))).thenReturn(sqlSessionMock);

        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.ADVERTISER, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.BRAND, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, sqlSessionMock);
    }

    @Test
    public void testGettingCampaignMetricsByAgency() throws Exception {
        // Initialize mocks
        Metrics metrics1 = createMetrics();
        Metrics metrics2 = createMetrics();
        List<Metrics> listOfTestMetrics = Arrays.asList(metrics1, metrics2);

        RecordSet<Metrics> testRecordSetOfMetrics = new RecordSet<Metrics>(
                0, 0, listOfTestMetrics.size(), listOfTestMetrics);
        when(metricsDaoEDW.getCampaignListMetrics(anyLong(), any(LocalDate.class),
                any(LocalDate.class), any(SqlSession.class), any(RecordSet.class))).thenReturn(testRecordSetOfMetrics);

        RecordSet<Metrics> metricsRecordSet = manager.getAgencyMetrics(agency_id, null, null, key, null);
        assertThat(metricsRecordSet, is(notNullValue()));
        List<Metrics> metricsList = metricsRecordSet.getRecords();
        assertThat(metricsList.size(), is(equalTo(listOfTestMetrics.size())));
        assertThat(metricsList.get(0), is(equalTo(metrics1)));
        assertThat(metricsList.get(1), is(equalTo(metrics2)));
    }

    @Test
    public void testGettingCampaignMetricsByCampaign() throws Exception {
        // Initialize mocks
        Metrics metrics1 = createMetrics();
        Metrics metrics2 = createMetrics();
        List<Metrics> listOfTestMetrics = Arrays.asList(metrics1, metrics2);

        RecordSet<Metrics> testRecordSetOfMetrics = new RecordSet<Metrics>(
                0, 0, listOfTestMetrics.size(), listOfTestMetrics);

        when(metricsDaoEDW.getCampaignMetrics(anyLong(), any(LocalDate.class),
                any(LocalDate.class), any(SqlSession.class))).thenReturn(testRecordSetOfMetrics);

        // Perform test
        RecordSet<Metrics> metricsRecordSet = manager.getCampaignMetrics(campaignId, null, null, key);
        assertThat(metricsRecordSet, is(notNullValue()));
        List<Metrics> metricsList = metricsRecordSet.getRecords();
        assertThat(metricsList.size(), is(equalTo(listOfTestMetrics.size())));
        assertThat(metricsList.get(0), is(equalTo(metrics1)));
        assertThat(metricsList.get(1), is(equalTo(metrics2)));
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void testGettingMetricsWhenNotAuthorizedForAdvertiser() throws Exception {
        // Initialize mocks
        Metrics metrics1 = createMetrics();
        Metrics metrics2 = createMetrics();
        List<Metrics> listOfTestMetrics = Arrays.asList(metrics1, metrics2);

        RecordSet<Metrics> testRecordSetOfMetrics = new RecordSet<Metrics>(
                0, 0, listOfTestMetrics.size(), listOfTestMetrics);
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, sqlSessionMock);

        when(metricsDaoEDW.getCampaignListMetrics(anyLong(), any(LocalDate.class),
                any(LocalDate.class), any(SqlSession.class), any(RecordSet.class))).thenReturn(testRecordSetOfMetrics);
        // Perform test
        manager.getAgencyMetrics(agency_id, LocalDate.now().minusDays(29).toDate(), LocalDate.now().toDate(), key, null);
    }

    @Test
    public void insertMetricsOk() {
        RecordSet<Metrics> metrics = createRecordSetMetrics(5);
        when(metricsDaoEDW.getMetricsByCampaignIds(anyList(), any(SqlSession.class))).thenReturn(metrics);
        Either<Error, RecordSet<Metrics>> result = manager.insertMetrics(agency_id, metrics.getRecords(), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void insertMetricsNullAgencyFail() {
        RecordSet<Metrics> metrics = createRecordSetMetrics(5);
        try {
            manager.insertMetrics(null, metrics.getRecords(), key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Agency Id"))));
        }
    }

    @Test
    public void insertMetricsNullListFail() {
        try {
            manager.insertMetrics(agency_id, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Metrics List"))));
        }
    }

    @Test
    public void insertMetricsEmptyListFail() {
        RecordSet<Metrics> metrics = createRecordSetMetrics(0);
        try {
            manager.insertMetrics(agency_id, metrics.getRecords(), key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Metrics List"))));
        }
    }

    @Test
    public void insertMetricsInvalidAgencyFail() {
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, sqlSessionMock);
        RecordSet<Metrics> metrics = createRecordSetMetrics(5);
        when(metricsDaoEDW.getMetricsByCampaignIds(anyList(), any(SqlSession.class))).thenReturn(metrics);
        Either<Error, RecordSet<Metrics>> result = manager.insertMetrics(agency_id, metrics.getRecords(), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error();
        System.out.println(error.getMessage());
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void insertMetricsInvalidCampaignFail() {
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, sqlSessionMock);
        RecordSet<Metrics> metrics = createRecordSetMetrics(5);
        when(metricsDaoEDW.getMetricsByCampaignIds(anyList(), any(SqlSession.class))).thenReturn(metrics);
        Either<Error, RecordSet<Metrics>> result = manager.insertMetrics(agency_id, metrics.getRecords(), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error();
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
    }

    private RecordSet<Metrics> createRecordSetMetrics(int quantity) {
        List<Metrics> list = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            list.add(createMetrics());
        }
        return new RecordSet<>(list);
    }

    /**
     * helper to create metrics objects
     *
     * @return
     */
    private Metrics createMetrics() {
        Metrics metrics = new Metrics();
        metrics.setClicks((long) Math.random());
        metrics.setConversions((long) Math.random());
        metrics.setCost((float) Math.random());
        metrics.setCtr((float) Math.random());
        metrics.setDay(new Date());
        metrics.setId((int) Math.random());
        metrics.seteCPA((float) Math.random());
        metrics.setImpressions((long) Math.random());

        return metrics;
    }
}

package trueffect.truconnect.api.crud.dao.impl;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.crud.Constants;
import trueffect.truconnect.api.crud.cassandra.CassandraClient;
import trueffect.truconnect.api.crud.dao.MetricsDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import trueffect.truconnect.api.resources.ResourceUtil;

public class MetricsDaoImpl extends AbstractGenericDao implements MetricsDao {

    public static final String METRICS_BY_CAMPAIGN_IDS = "MetricsEDW.getMetricsByCampaignIds";
    public static final String METRICS_BY_AGENCY_AND_DATE = "MetricsEDW.getMetricsByAgencyAndDateRange";
    public static final String METRICS_BY_CAMPAIGN_AND_DATE = "MetricsEDW.getCampaignMetricsAndDateRange";
    public static final String METRICS_BY_CAMPAIGN_FOR_CREATIVE = "MetricsEDW.getCreativeMetricsByCampaign";
    public static final String METRICS_BY_CAMPAIGN_FOR_TOP_TEN_CREATIVE = "MetricsEDW.getTopTenCreativesByCampaign";
    public static final String GBL_CONTROL_REFRESH_AGG_CAMPAIGN = "MetricsEDW.getGblControlRefreshAggCampaign";

   // private final CassandraClient cassandraClient = new CassandraClient();
    private SizeDao creative;

    private static Logger LOGGER = LoggerFactory.getLogger(MetricsDaoImpl.class);

    public MetricsDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public RecordSet<Metrics> getMetricsByCampaignIds(List<String> ids, final SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        List<Metrics> result = new ArrayList<>();
        Accumulator<List<Metrics>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        List<Metrics> aux = new ResultSetAccumulatorImpl<List<Metrics>>(
                "ids",
                ids,
                resultAccumulator,
                parameter) {
            @Override
            public List<Metrics> execute(Object parameters) {
                return getPersistenceContext().selectMultiple(METRICS_BY_CAMPAIGN_IDS,
                        parameters, session);
            }
        }.getResults();
        return new RecordSet<>(0, aux.size(), aux.size(), aux);
    }

    @Override
    public RecordSet<Metrics> getCampaignListMetrics(Long agencyId, LocalDate startDate, LocalDate endDate, SqlSession session, RecordSet<CampaignDTO> campaigns) {
        RecordSet<Metrics> result = null;
        Long impressions = null;
        Long clicks = null;
        HashMap<String, Object> parameter = new HashMap<>();
        StringBuilder campaignIds = new StringBuilder("");
        for (CampaignDTO campaignDTO : campaigns.getRecords() ){
            campaignIds.append(campaignDTO.getId());
            campaignIds.append(",");
        }
        campaignIds.setLength(Math.max(campaignIds.length() - 1, 0));

        try {
            String dateTimeHour = (String) getPersistenceContext().selectOne(GBL_CONTROL_REFRESH_AGG_CAMPAIGN);
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHH");
            LocalDateTime lastProcessedDateTime = LocalDateTime.parse(dateTimeHour, dtf);
            parameter.put("startDate", startDate.toDate());
            parameter.put("endDate", endDate.toDate());
            parameter.put("agencyId", agencyId);
            List<Metrics> metrics = getPersistenceContext().selectMultiple(METRICS_BY_AGENCY_AND_DATE, parameter, session);
            if ( lastProcessedDateTime.toLocalDate().compareTo(endDate) <= 0 ) {
//                ResultSet resultSet = cassandraClient.executeQuery("select sum(clicks) as cli, sum(impressions) as imp " +
//                        "from ta_" + ResourceUtil.get(Constants.CASSANDRA_ENV) + ".counter_campaign" +
//                        " where campaign_id in (" + campaignIds + ")" +
//                        " and day_id in (" + dateTimeHour.substring(0, 8) + ")" +
//                        " and hour_id > " + dateTimeHour + ";");

//                if (resultSet != null) {
//                    Row row = resultSet.one();
//                    if (row != null) {
//                        impressions = row.getLong("imp");
//                        clicks = row.getLong("cli");
//                    }
//                }
//                if (metrics.size() > 0) {
//                    metrics.get(metrics.size() - 1).setClicks(metrics.get(metrics.size() - 1).getClicks() + clicks);
//                    metrics.get(metrics.size() - 1).setImpressions(metrics.get(metrics.size() - 1).getImpressions() + impressions);
//                } else {
//                    Metrics metric = new Metrics();
//                    metric.setImpressions(impressions);
//                    metric.setClicks(clicks);
//                    metric.setDay(lastProcessedDateTime.toDate());
//                    metrics.add(metric);
//                }
            }
            result = new RecordSet<>(0, metrics.size(), metrics.size(), metrics);
        } catch (Exception e) {
            LOGGER.error("Failed to get campaign metrics", e);
        }

        return result;
    }

    @Override
    public RecordSet<Metrics> getCampaignMetrics(Long campaignId, LocalDate startDate, LocalDate endDate, SqlSession session) {
        RecordSet<Metrics> result;
        HashMap<String, Object> parameter = new HashMap<>();

        parameter.put("startDate", startDate.toDate());
        parameter.put("endDate", endDate.toDate());
        parameter.put("campaignId", campaignId);

        try {
            List<Metrics> metrics = getPersistenceContext().selectMultiple(METRICS_BY_CAMPAIGN_AND_DATE, parameter, session);
            result = new RecordSet<>(0, metrics.size(), metrics.size(), metrics);
        } catch (Exception e) {
            LOGGER.error("Failed to get campaign metrics", e);
            throw e;
        }

        return result;
    }

    @Override
    public void insertMetrics(long agencyId, Metrics metrics, SqlSession session){
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("campaignId", metrics.getId());
        parameter.put("agencyId", agencyId);
        parameter.put("dayId", new DateTime(metrics.getDay()).toString("yyyyMMdd"));
        parameter.put("day", metrics.getDay());
        parameter.put("impressions", metrics.getImpressions());
        parameter.put("conversions", metrics.getConversions());
        parameter.put("clicks", metrics.getClicks());
        parameter.put("cost", metrics.getCost());
        parameter.put("eCPA", metrics.geteCPA());
        parameter.put("ctr", metrics.getCtr());

        getPersistenceContext().execute("MetricsEDW.saveMetrics", parameter, session);
    }

    @Override
    public RecordSet<Metrics> getCreativeMetricsByCampaign(Long campaignId, SqlSession session) {
        RecordSet<Metrics> result;
        HashMap<String, Object> parameter = new HashMap<>();

        parameter.put("campaignId", campaignId);
        List<Metrics> metrics = getPersistenceContext().selectMultiple(METRICS_BY_CAMPAIGN_FOR_CREATIVE, parameter, session);
        result = new RecordSet<>(0, metrics.size(), metrics.size(), metrics);

        return result;
    }

    @Override
    public RecordSet<Metrics> getTopTenCreativeMetricsByCampaign(Long campaignId,  SqlSession session) {
        RecordSet<Metrics> result;
        HashMap<String, Object> parameter = new HashMap<>();

        parameter.put("campaignId", campaignId);

        List<Metrics> metrics = getPersistenceContext().selectMultiple(METRICS_BY_CAMPAIGN_FOR_TOP_TEN_CREATIVE, parameter, session);
        result = new RecordSet<>(0, metrics.size(), metrics.size(), metrics);
        return result;
    }
}
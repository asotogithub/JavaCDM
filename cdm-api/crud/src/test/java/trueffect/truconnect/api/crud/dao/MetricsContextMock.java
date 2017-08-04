package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.crud.mybatis.MetricsPersistenceContextMock;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jeff Fryer
 */
public class MetricsContextMock extends MetricsPersistenceContextMock {

    private Map<Long, Metrics> metrics;
    private static Long globalSequence = 0L;

    public MetricsContextMock(Map<Long, Metrics> metrics) {
        this.metrics = metrics;
    }

    @Override
    public void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception {
        if ("MetricsEDW.getCampaignListMetrics".equalsIgnoreCase(xmlMethod)) {
            Metrics metric = new Metrics();
            Long id = (Long) parameterMap.get("id");
            metric.setId(id);
            metric.setConversions((Long) parameterMap.get("conversion"));
            metric.setClicks((Long) parameterMap.get("clicks"));
            metric.setCost((Float) parameterMap.get("cost"));
            metric.setCtr((Float) parameterMap.get("ctr"));
            metric.setDay((Date) parameterMap.get("day"));
            metric.seteCPA((Float) parameterMap.get("eCPA"));
            metric.setId(id);
            metric.setImpressions((Long) parameterMap.get("impressions"));
            metrics.put(id, metric);
        } else {
            super.callPlSqlStoredProcedure(xmlMethod, parameterMap, session);
        }
    }
}

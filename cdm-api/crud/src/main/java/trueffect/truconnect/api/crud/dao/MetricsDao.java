package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * @author Jeff Fryer
 */
public interface MetricsDao extends GenericDao {

    RecordSet<Metrics> getMetricsByCampaignIds(List<String> ids, SqlSession session);
    
    /**
     * Returns denormalized metrics for a campaign by agency and date range.
     * @param agencyId The agency id to load campaign metrics for.
     * @param startDate The start date for metrics (inclusive).
     * @param endDate The end date for metrics (inclusive).
     * @param session The sql session.
     * @param campaigns recordset of campaignDTO objects.
     * @return A record per campaign/day combination
     */
    RecordSet<Metrics> getCampaignListMetrics(Long agencyId, LocalDate startDate, LocalDate endDate, SqlSession session, RecordSet<CampaignDTO> campaigns);

    /**
     * Returns denormalized for a single campaign by date range.
     * @param campaignId The campaignId to filter for.
     * @param startDate The start date (inclusive).
     * @param endDate The end date (inclusive).
     * @param session The SQL session.
     * @return A record per day.
     */
    RecordSet<Metrics> getCampaignMetrics(Long campaignId, LocalDate startDate, LocalDate endDate, SqlSession session);

    /**
     * Utility function to insert test data into lower environments.  This should NOT be exposed via REST.  DB users
     * have been locked down to prevent accidental access outside of dev.
     * @param agencyId The agency id to insert for.
     * @param metrics The metrics to insert.
     * @param session The session to use when inserting.
     */
    void insertMetrics(long agencyId, Metrics metrics, SqlSession session);

    /**
     * Returns denormalized for a single campaign by date range.
     * @param campaignId The campaignId to filter for.
     * @param session The SQL session.
     * @return A record per day.
     */
    RecordSet<Metrics> getCreativeMetricsByCampaign(Long campaignId,  SqlSession session);

    /**
     * Returns denormalized for a single campaign by date range.
     * @param campaignId The campaignId to filter for.
     * @param session The SQL session.
     * @return A record per day.
     */
    RecordSet<Metrics> getTopTenCreativeMetricsByCampaign(Long campaignId, SqlSession session) ;
}

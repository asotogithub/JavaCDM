package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.dto.SiteMeasurementCampaignDTO;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Set;

/**
 *
 * @author marleny.patsi
 */
public interface SiteMeasurementCampaignDao extends GenericDao{
    
    /**
     * Get a SiteMeasurementCampaignDTO for a given {@code SiteMeasurementCampaign} 
     * @param siteMeasurementCampaign The given {@code SiteMeasurementCampaign} DTO
     * @param session The current persistence session
     * @return List of SiteMeasurementCampaignDTO
     */
    SiteMeasurementCampaignDTO getSiteMeasurementCampaign(
            SiteMeasurementCampaignDTO siteMeasurementCampaign, SqlSession session);
    
    /**
     * Get a List of DTOs for a given {@code SiteMeasurement} 
     * @param measurementId The given {@code SiteMeasurement} DTO
     * @param session The current persistence session
     * @return List of SiteMeasurementCampaignDTO
     */    
    List<SiteMeasurementCampaignDTO> getAssociatedCampaignsForSiteMeasurement(
            Long measurementId, Long startIndex, Long pageSize, SqlSession session);

    Long getCountAssociatedCampaignsForSiteMeasurement(
            Long measurementId, SqlSession session);

    List<SiteMeasurementCampaignDTO> getUnassociatedCampaignsForSiteMeasurement(
            Long measurementId, Long startIndex, Long pageSize, SqlSession session);

    Long getCountUnassociatedCampaignsForSiteMeasurement(
            Long measurementId, SqlSession session);
    /**
     * Create a SiteMeasurementCampaign for a given {@code SiteMeasurement} and {@code Campaign} 
     * @param siteMeasurementCampaign The given {@code SiteMeasurementCampaign} DTO
     * @param session The current persistence session
     */
    SiteMeasurementCampaignDTO create(SiteMeasurementCampaignDTO siteMeasurementCampaign, SqlSession session);

    /**
     * Remove (Logically) a given {@code SiteMeasurement} DTO
     *
     * @param siteMeasurementCampaign The given {@code SiteMeasurement} DTO
     * @param session The current persistence session
     */
    void remove(SiteMeasurementCampaignDTO siteMeasurementCampaign, SqlSession session);

    boolean checkDomainConsistency(Long id, Set<Long> campaignIds, SqlSession session);
}

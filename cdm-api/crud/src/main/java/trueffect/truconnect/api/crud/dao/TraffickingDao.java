package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.CampaignCreatorContact;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;

/**
 *
 * @author marleny.patsi
 */
public interface TraffickingDao extends GenericDao {

    CampaignCreatorContact getCreativeGroupCreativesByCampaign(Long id, SqlSession session);

    Long getCreativeSchedule(Long campaignId, SqlSession session);

    Long getCreativePlacementMatchHeightAndWidth(Long campaignId, SqlSession session);
    
    Long getSchedulesClickthroughCount(Long campaignId, SqlSession session);
    
    Long getCreativesClickthroughCount(Long campaignId, SqlSession session);
    
    Long getDatesValidationCount(Long campaignId, SqlSession session);
    
    boolean checkContactsBelongsAgencyByUser(Collection<Integer> ids, String userId, SqlSession session);

    boolean checkSiteContacts(Collection<Integer> ids, String userId, SqlSession session);
}

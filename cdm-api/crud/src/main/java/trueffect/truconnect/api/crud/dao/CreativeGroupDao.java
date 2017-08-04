package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public interface CreativeGroupDao extends GenericDao {
    
    CreativeGroup get(Long id, OauthKey key, SqlSession session) throws Exception;
    
    RecordSet<CreativeGroup> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception;
    
    List<CreativeGroup> getCreativeGroupsByIds(Collection<Long> groupIds, SqlSession session);
    
    List<Long> getInvalidTargetValues(List<Long> creativeGroupTargets, SqlSession session) throws Exception;
    
    CreativeGroup save(CreativeGroup group, OauthKey key, SqlSession session) throws Exception;
    
    CreativeGroup update(CreativeGroup group, OauthKey key, SqlSession session) throws Exception;
    
    void remove(Long id, OauthKey key, SqlSession session) throws Exception;

    List<CreativeGroup> bulkUpdate(List<CreativeGroup> creativeGroups, SqlSession session, OauthKey key) throws Exception;

    /**
     * Updates the creative group weight data for a {@code creativeGroupId} 
     * @param group The CreativeGroup to update
     * @param session The MyBatis {@code SqlSession} to execute this query
     */
    void updateOnImport(CreativeInsertionRawDataView group, SqlSession session);

    Long getCountCreativeGroupsByCampaignId(Long campaignId, Collection<Long> ids,
                                            SqlSession session);
    
    Boolean creativeGroupExists(CreativeGroup cg, SqlSession session);

    Integer deleteCreativeFromCreative(Long creativeGroupId, String tpwsKey, SqlSession session);
}

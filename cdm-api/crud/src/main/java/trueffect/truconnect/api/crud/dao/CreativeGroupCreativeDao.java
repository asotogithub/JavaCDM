package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeView;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public interface CreativeGroupCreativeDao extends GenericDao {

    CreativeGroupCreative get(Long creativeGroupId, Long creativeId, SqlSession session);

    List<CreativeGroupCreative> getByCreative(Long creativeId, SqlSession session);

    List<String> getExistingCGC(List<String> cgcs, Long campaignId, SqlSession session);
    
    CreativeGroupCreative save(CreativeGroupCreative creativeGroupCreative, SqlSession session);

    CreativeGroupCreative update(CreativeGroupCreative creativeGroupCreative, OauthKey key, SqlSession session) throws Exception;

    /**
     * This method makes a physical removal over TE_XLS.CREATIVE_GROUP_CREATIVE table
     *
     * @param creativeGroupId
     * @param creativeId
     * @param session
    */
    void remove(Long creativeGroupId, Long creativeId, SqlSession session);

    RecordSet<CreativeGroupCreative> getByCreativeGroup(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception;

    List<CreativeGroupCreative> getCreativeGroupCreativesByCG(Long creativeGroupId, OauthKey key, SqlSession session) throws Exception;
    
    List<CreativeGroupCreativeView> getGroupCreativesByFilterParam(Long campaignId, CreativeInsertionFilterParam filterParam, String userId, SqlSession session);

    Long getCountGroupCreativesByFilterParam(Long campaignId, CreativeInsertionFilterParam filterParam, SqlSession session);
}
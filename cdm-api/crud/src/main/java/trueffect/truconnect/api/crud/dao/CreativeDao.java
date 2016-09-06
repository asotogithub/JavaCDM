package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeVersion;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.CreativeAssociationsDTO;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public interface CreativeDao extends GenericDao {

    /**
     *
     * @param id
     * @param session
     * @return
     */
    Creative get(Long id, SqlSession session);

    Creative getCreativeByCampaignIdAndFileName(Long campaignId, String filename,
                                                SqlSession session);

    List<Creative> getByCampaignIdAndFileNames(Long campaignId, Collection<String> names,
                                               SqlSession session);

    @SuppressWarnings("unchecked")
    RecordSet<Creative> getCreatives(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception;

    Long getCountCreativesByCampaignIdAndIds(List<Long> ids, Long campaignId, SqlSession session);

    Creative create(Creative creative, OauthKey key, SqlSession session) throws Exception;

    /**
     * Updates a given {@code Creative}
     * @param creative The {@code Creative} to update
     * @param key The user which is updating the {@code Creative}
     * @param session The MyBatis {@code SqlSession} to execute this query
     */
    void update(Creative creative, OauthKey key, SqlSession session);

    void insertCreativeVersion(Creative creative, SqlSession session);

    void updateCreativeAlias(Long creativeId, String alias, OauthKey key, SqlSession session);

    void remove(Long id, OauthKey key, SqlSession session) throws Exception;

    void removeCreativeClickThrough(Long creativeId, OauthKey key, SqlSession session);

    Creative saveCreativeClickThrough(Creative creative, OauthKey key, SqlSession session);

    List<Creative> getCreativesByIds(Collection<Long> ids, SqlSession session);

    List<CreativeVersion> getCreativeVersionsById(Long creativeId, SqlSession session);

    Long getCreativeIdByCampaignIdAndFileName(Long campaignId, String filename, SqlSession session);

    List<Creative> getCreativesWithNoGroupAssociation(Long campaignId, Long groupId, Long startIndex, Long pageSize, SqlSession session);

    Long getCountForCreativesWithNoGroupAssociation(Long campaignId, Long groupId, Long startIndex, Long pageSize, SqlSession session);

    List<Creative> getCreativesByCampaign(Long campaignId, Long startIndex, Long pageSize, SqlSession session);

    Long getCountForCreativesByCampaign(Long campaignId, Long startIndex, Long pageSize, SqlSession session);

    /**
     * Get the quantity of group and schedules associations has a CreativeId
     *
     * @param creativeId
     * @param session The MyBatis {@code SqlSession} to execute this query
     * @return CreativeRelationsDTO
     */
    List<CreativeAssociationsDTO> getScheduleAssocPerGroupByCreativeId(Long creativeId,
                                                                       SqlSession session);

    /**
     * Updates a list of {@code Version}s for a given {@code creativeId}
     * @param versions The versions for the given {@code creativeId}
     * @param session The MyBatis {@code SqlSession} to execute this query
     */
    int updateVersions(List<CreativeVersion> versions, SqlSession session);

    List<Creative> getDupVersionAliasByCampaignAndCreativeIdAlias(Long campaignId,
                                                                  Collection<String> alias,
                                                                  SqlSession session);
    int updateCreativeVersionsToTrafficked(Long campaignId, SqlSession session);
}

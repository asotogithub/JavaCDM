package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.ScheduleSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionSearchOptions;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.crud.mybatis.GenericDao;
import trueffect.truconnect.api.crud.service.CreativeManager;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Gustavo Claure
 */
public interface CreativeInsertionDao extends GenericDao{

    /**
     *
     * @param id
     * @param session
     * @return
     * @deprecated Use {@link trueffect.truconnect.api.crud.dao.CreativeInsertionDao#getById(Long, SqlSession)}  method instead to avoid using checked exceptions
     */    
    CreativeInsertion get(Long id, SqlSession session);

    RecordSet<CreativeInsertion> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception;
    
    List<CreativeInsertion> getCreativeInsertionsByGroupId(Long creativeGroupId, OauthKey key, SqlSession session) throws Exception;

    /**
     * Creates a Creative Insertion.
     * @param creativeInsertion
     * @param session The MyBatis {@code SqlSession} to execute this query
     * @return the Creative Insertion created
     */
    CreativeInsertion create(CreativeInsertion creativeInsertion,SqlSession session);

    CreativeInsertion update(CreativeInsertion creativeInsertion, SqlSession session, OauthKey key) throws Exception;

    List<CreativeInsertion> bulkUpdate(List<CreativeInsertion> creativeInsertions, SqlSession session, OauthKey key) throws Exception;
    
    @Deprecated
    void delete(Long id, OauthKey key, SqlSession session) throws Exception;

    /**
     * Deletes a list of Creative Insertion IDs. Sets the {@code LOGICAL_DELETE} column to 'Y'
     * @param creativeInsertionIds The Creative Insertion IDs to delete
     * @param tpwsKey The user tpws key to save for every deleted Creative Insertion
     * @param session The MyBatis {@code SqlSession} to execute this query
     * @return The number of delete Creative Insertions
     */
    int bulkDeleteById(Set<Long> creativeInsertionIds, String tpwsKey, SqlSession session);
    
    /**
     * Deletes a list of Creative Insertion IDs. Sets the {@code LOGICAL_DELETE} column to 'Y'
     * @param campaignId The Campaign Id of Creative Insertions to delete
     * @param ciBulk The Creative Insertion Bulk structure with IDs of Site, Section, Placement, Group or Creative to delete
     * @param tpwsKey The user tpws key to save for every deleted Creative Insertion
     * @param session The MyBatis {@code SqlSession} to execute this query
     * @return The number of delete Creative Insertions
     */    
    int bulkDeleteByFilterParam(Long campaignId, CreativeInsertionFilterParam ciBulk,
                                String tpwsKey, SqlSession session);

    void deleteByCreativeGroupId(Long id, OauthKey key, SqlSession session) throws Exception;

    ScheduleSet getScheduleSet(Long creativeGroupId, SqlSession session) throws Exception;

    Long getCampaingIdByGreativeGroupId(Long creativeGroupId);


    /**
     * Counts the number of Scheduled Associations for these {@code creativeIds}
     * @param searchCriteria The SearchCriteria to use
     * @param userId The user id that executes the query
     * @param session The MyBatis {@code SqlSession} to execute this query
     * @return The number of Scheduled Associations for these {@code creativeIds}
     * @throws trueffect.truconnect.api.commons.exceptions.SearchApiException
     */
    Long getScheduleCountByCreativeIds(SearchCriteria searchCriteria, String userId, SqlSession session)
        throws SearchApiException;
    
    /**
     * Gets a list of CreativeInsertions for this {@code campaignId}
     * @param campaignId
     * @param session The MyBatis {@code SqlSession} to execute this query
     * @return A list of CreativeInsertions for this {@code campaignId}
     */ 
    List<CreativeInsertionRawDataView> getCreativeInsertionsToExport(Long campaignId, SqlSession session);

    /**
     * Gets a list of CreativeInsertions with {@code creativeInsertionIds} and {@code creativeGroupIds} 
     * for these {@code creativeInsertionIds} that belong to this {@code userId}
     * @param ids The {@code creativeInsertionIds} list, this list should have a maximum of 1000 values
     * @param userId The user id that executes the query
     * @param campaignId
     * @param session The MyBatis {@code SqlSession} to execute this query
     * @return A list of CreativeInsertions for these {@code creativeInsertionIds} that belong to this {@code userId}
     */    
    List<CreativeInsertionRawDataView> getCreativeInsertionsByUserId(Collection<Long> ids, String userId, Long campaignId, SqlSession session);
    
    /**
     * Updates the creativeInsertion and Click-through data to import process 
     * for a {@code creativeInsertionId} 
     * @param creativeInsertion
     * @param session The MyBatis {@code SqlSession} to execute this query
     */
    void updateOnImport(CreativeInsertionRawDataView creativeInsertion, SqlSession session);

    List<CreativeInsertionView> getAllCreativeInsertions(Long campaignId, Long startIndex, Long pageSize, SqlSession session);

    Long getAllCreativeInsertionsCount(Long campaignId, SqlSession session);

    List<CreativeInsertionView> getSchedulesSiteLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session);

    List<CreativeInsertionView> getSchedulesSectionLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session);

    List<CreativeInsertionView> getSchedulesPlacementLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session);

    List<CreativeInsertionView> getSchedulesGroupLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session);

    List<CreativeInsertionView> getSchedulesCreativeLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session);
    
    Long getCountSchedulesByLevel(Long campaignId, CreativeInsertionFilterParam parentIds, SqlSession session);

    List<CreativeInsertionView> getSchedulesScheduleLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session);

    int getCountAffectedSchedulesDueToImport(List<Long> cgIds, Long campaignId, SqlSession session);

    Map<Long,CreativeManager.CreativeGlobalClassification> getCreativeClassificationByPlacementId(Long campaignId, Collection<Long> ids, SqlSession session);

    List<CreativeInsertionView> searchSchedulesLevel(Long campaignId, CreativeInsertionFilterParam parentIds, CreativeInsertionSearchOptions searchOptions, String pattern, Long startIndex, Long pageSize, SqlSession session);

    Long searchSchedulesLevelCount(Long campaignId, CreativeInsertionFilterParam parentIds, CreativeInsertionSearchOptions searchOptions, String pattern, Long startIndex, Long pageSize, SqlSession session);

    /**
     * Get all schedules associated a CreativeId and campaignId
     *
     * @param campaignId
     * @param creativeId
     * @param userId
     * @param session The MyBatis {@code SqlSession} to execute this query
     * @param startIndex
     * @param pageSize
     * @return List<CreativeInsertionView>
     */
    List<CreativeInsertionView> getSchedulesByCreativeId(Long campaignId, Long creativeId,
                                                         String userId, Long startIndex,
                                                         Long pageSize, SqlSession session);

    /**
     * Get the quantity of schedules associated a CreativeId and campaignId
     *
     * @param campaignId
     * @param creativeId
     * @param userId
     * @param session The MyBatis {@code SqlSession} to execute this query
     * @return List<CreativeInsertionView>
     */
    Long getCountSchedulesByCreativeId(Long campaignId, Long creativeId, String userId,
                                       SqlSession session);

    Long getCountSchedulesByrCreativeAndGroupIds(Long creativeId, Collection<Long> groupIds,
                                                 String userId, SqlSession session);
}

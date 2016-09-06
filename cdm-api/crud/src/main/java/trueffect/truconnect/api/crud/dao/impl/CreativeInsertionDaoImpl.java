package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.Schedule;
import trueffect.truconnect.api.commons.model.ScheduleEntry;
import trueffect.truconnect.api.commons.model.ScheduleSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.TupleSchedule;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionSearchOptions;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.DataAccessControl;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.MapAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.SumAccumulatorImpl;
import trueffect.truconnect.api.crud.service.CreativeManager;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 *
 * @author Abel Soto
 * @author Richard Jaldin
 */
public class CreativeInsertionDaoImpl extends AbstractGenericDao implements CreativeInsertionDao {

    private static final String CREATIVE_INSERTION_CREATIVE_INSERTION_ROOT_CNT = "CreativeInsertion.creativeInsertionRootCnt";
    private static final String CREATIVE_INSERTION_CREATIVE_INSERTION_ROOT_ID = "CreativeInsertion.creativeInsertionRootId";
    private static final String CREATIVE_INSERTION_INSERT_CREATIVE_INSERTION_ROOT = "CreativeInsertion.insertCreativeInsertionRoot";
    private static final String CREATIVE_INSERTION_INSERT_CREATIVE_INSERTION = "CreativeInsertion.insertCreativeInsertion";
    private static final String CREATIVE_INSERTION_RICH_MEDIA_ID = "CreativeInsertion.richMediaId";
    private static final String CREATIVE_INSERTION_CREATIVE_INSERTION_ROOT_CNT_RMI = "CreativeInsertion.creativeInsertionRootCntRMI";
    private static final String CREATIVE_INSERTION_INSERT_CREATIVE_INSERTION_ROOT_RMI = "CreativeInsertion.insertCreativeInsertionRootRMI";
    private static final String CREATIVE_INSERTION_INSERT_CREATIVE_INSERTION_CLICK = "CreativeInsertion.insertCreativeInsertionclick";
    private static final String CREATIVE_INSERTION_DELETE_CREATIVE_INSERTION_CLICK = "CreativeInsertion.deleteCreativeInsertionclick";
    private static final String CREATIVE_INSERTION_UPDATE_CREATIVE_INSERTION = "CreativeInsertion.updateCreativeInsertion";
    private static final String CREATIVE_INSERTION_DELETE_CREATIVE_INSERTION = "CreativeInsertion.deleteCreativeInsertion";
    private static final String CREATIVE_INSERTION_DELETE_CREATIVE_INSERTION_BY_CG_ID = "CreativeInsertion.deleteCreativeInsertionByCgId";
    private static final String CREATIVE_INSERTION_GET_CREATIVE_INSERTION_BY_ID = "CreativeInsertion.getCreativeInsertionById";
    public static final String CREATIVE_INSERTION_GET_CREATIVE_INSERTION_BY_CREATIVE_GROUP_ID = "CreativeInsertion.getCreativeInsertionByCreativeGroupId";
    private static final String CREATIVE_INSERTION_GET_DISTINCT_CREATIVE_INSERTIONS_PLACEMENTS = "CreativeInsertion.getDistinctCreativeInsertionsPlacements";
    private static final String CREATIVE_INSERTION_GET_DISTINCT_CREATIVE_INSERTION_BY_CREATIVE_GROUP_ID_AND_PLACEMENT_ID = "CreativeInsertion.getDistinctCreativeInsertionByCreativeGroupIdAndPlacementId";
    private static final String CREATIVE_INSERTION_GET_CREATIVE_INSERTION_CLICKTHROUGHS = "CreativeInsertion.getCreativeInsertionClickthroughs";
    private static final String CREATIVE_INSERTION_GET_CREATIVE_INSERTION_ROOT_ID_BY_CREATIVE_GROUP_ID = "CreativeInsertion.getCreativeInsertionRootIdByCreativeGroupId";
    private static final String CREATIVE_INSERTION_GET_CREATIVE_INSERTIONS_BY_CRITERIA = "CreativeInsertion.getCreativeInsertionsByCriteria";
    public static final String CREATIVE_INSERTION_GET_CREATIVE_INSERTIONS_NUMBER_OF_RECORDS_BY_CRITERIA = "CreativeInsertion.getCreativeInsertionsNumberOfRecordsByCriteria";
    private static final String CREATIVE_INSERTION_GET_CAMPAING_ID_BY_GREATIVE_GROUP_ID = "CreativeInsertion.getCampaingIdByGreativeGroupId";
    private static final String STATEMENT_GET_CREATIVE_INSERTIONS_EXPORT = "CreativeInsertion.getCreativeInsertionsToExport";
    private static final String STATEMENT_CREATIVE_INSERTIONS_BY_USERID = "CreativeInsertion.getCreativeInsertionsByUserId";
    private static final String STATEMENT_BULK_DELETE_BY_ID = "CreativeInsertion.bulkDeleteById";
    private static final String STATEMENT_BULK_DELETE_BY_FILTER_PARAM = "CreativeInsertion.bulkDeleteByFilterParam";
    private static final String CREATIVE_INSERTION_UPDATE_ON_IMPORT = "CreativeInsertion.updateCreativeInsertionOnImport";
    private static final String GET_ALL_CREATIVE_INSERTIONS = "CreativeInsertion.getAllCreativeInsertions";
    private static final String GET_ALL_CREATIVE_INSERTIONS_COUNT = "CreativeInsertion.getAllCreativeInsertionsCount";
    private static final String GET_CREATIVE_INSERTIONS_SITE_LEVEL = "CreativeInsertion.getCreativeInsertionsSiteLevel";
    private static final String GET_CREATIVE_INSERTIONS_SECTION_LEVEL = "CreativeInsertion.getCreativeInsertionsSectionLevel";
    private static final String GET_CREATIVE_INSERTIONS_PLACEMENT_LEVEL = "CreativeInsertion.getCreativeInsertionsPlacementLevel";
    private static final String GET_CREATIVE_INSERTIONS_GROUP_LEVEL = "CreativeInsertion.getCreativeInsertionsGroupLevel";
    private static final String GET_CREATIVE_INSERTIONS_GROUP_LEVEL_ASSOCIATIONS = "CreativeInsertion.getCreativeInsertionsGroupLevelAssociations";
    private static final String GET_CREATIVE_INSERTIONS_CREATIVE_LEVEL = "CreativeInsertion.getCreativeInsertionsCreativeLevel";
    private static final String GET_CREATIVE_INSERTIONS_SCHEDULE_LEVEL = "CreativeInsertion.getCreativeInsertionsScheduleLevel";
    private static final String GET_COUNT_CREATIVE_INSERTIONS_SITE_LEVEL = "CreativeInsertion.getCountCreativeInsertionsSiteLevel";
    private static final String GET_COUNT_CREATIVE_INSERTIONS_SECTION_LEVEL = "CreativeInsertion.getCountCreativeInsertionsSectionLevel";
    private static final String GET_COUNT_CREATIVE_INSERTIONS_PLACEMENT_LEVEL = "CreativeInsertion.getCountCreativeInsertionsPlacementLevel";
    private static final String GET_COUNT_CREATIVE_INSERTIONS_GROUP_LEVEL = "CreativeInsertion.getCountCreativeInsertionsGroupLevel";
    private static final String GET_COUNT_CREATIVE_INSERTIONS_CREATIVE_LEVEL = "CreativeInsertion.getCountCreativeInsertionsCreativeLevel";
    private static final String GET_COUNT_CREATIVE_INSERTIONS_SCHEDULE_LEVEL = "CreativeInsertion.getCountCreativeInsertionsScheduleLevel";
    private static final String GET_COUNT_AFFECTED_SCHEDULES_DUE_TO_IMPORT = "CreativeInsertion.getCountAffectedSchedulesDueToImport";
    private static final String STATEMENT_GET_CREATIVE_CLASSIFICATION_BY_PLACEMENT = "CreativeInsertion.creativeClassificationByPlacement";
    private static final String STATEMENT_GET_BY_CREATIVE_ID = "CreativeInsertion.getCreativeInsertionsByCreativeId";
    private static final String STATEMENT_GET_COUNT_BY_CREATIVE_ID = "CreativeInsertion.getCountCreativeInsertionsByCreativeId";
    private static final String STATEMENT_GET_COUNT_BY_CREATIVE_AND_GROUP_ID = "CreativeInsertion.getCountCreativeInsertionsByCreativeAndGroupId";

    // Search queries
    private static final String SEARCH_CREATIVE_INSERTIONS = "CreativeInsertionSearch.searchCreativeInsertions";
    private static final String SEARCH_CREATIVE_INSERTIONS_COUNT = "CreativeInsertionSearch.searchCreativeInsertionsCount";
    private static final String SEARCH_CREATIVE_INSERTIONS_PLACEMENTS_ASSOCIATIONS = "CreativeInsertionSearch.searchCreativeInsertionsPlacementsAssociations";
    private static final String SEARCH_CREATIVE_INSERTIONS_CREATIVE_GROUPS_ASSOCIATIONS = "CreativeInsertionSearch.searchCreativeInsertionsCreativeGroupsAssociations";
    private static final String SEARCH_CREATIVE_INSERTIONS_CREATIVE_ASSOCIATIONS = "CreativeInsertionSearch.searchCreativeInsertionsCreativeAssociations";
    
    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this
     * DAO
     */
    public CreativeInsertionDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }
    
    @Override
    public CreativeInsertion get(Long id, SqlSession session){
        CreativeInsertion result = getPersistenceContext().selectSingle(
                CREATIVE_INSERTION_GET_CREATIVE_INSERTION_BY_ID,
                id,
                session,
                CreativeInsertion.class);
        if (result != null) {
            List<Clickthrough> resultClickthroughs = getClickthroughs(id, session);
            result.setClickthroughs(resultClickthroughs);
        }
        return result;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<CreativeInsertion> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception{
        RecordSet<CreativeInsertion> result;
        if(criteria == null){
            throw new IllegalArgumentException("SearchCriteria cannot be null");
        }

        if (criteria.getPageSize() > 1000) {
            throw new Exception("The page size allows up to 1000 records.");
        }
        if (criteria.getStartIndex() < 0) {
            throw new Exception("Cannot retrieve records for start index: " + criteria.getStartIndex() + ". The minimum start index is: 0");
        }

        Searcher<CreativeInsertion> searcher = new Searcher<CreativeInsertion>(criteria.getQuery(), CreativeInsertion.class);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        // Getting data only for the page
        List<CreativeInsertion> aux = getPersistenceContext().selectMultiple(CREATIVE_INSERTION_GET_CREATIVE_INSERTIONS_BY_CRITERIA,
                parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<CreativeInsertion>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        long totalRecords = getScheduleCountByCreativeIds(criteria, key.getUserId(), session);

        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        result = new RecordSet<CreativeInsertion>(criteria.getStartIndex(), criteria.getPageSize(), (int)totalRecords, aux);
        return result;
    }
    
    @Override
    public List<CreativeInsertion> getCreativeInsertionsByGroupId(Long creativeGroupId, OauthKey key, SqlSession session) throws Exception {
        List<CreativeInsertion> result = (List<CreativeInsertion>) getPersistenceContext().
                selectList(CREATIVE_INSERTION_GET_CREATIVE_INSERTION_BY_CREATIVE_GROUP_ID, creativeGroupId, session);
        return result;
    }    

    @Override
    public CreativeInsertion create(CreativeInsertion creativeInsertion, SqlSession session) {
        CreativeInsertion result = null;
        //for the nextval of cookie domain
        Long id = getPersistenceContext().selectSingle("GenericQueries.getNextId", "SEQ_CREATIVE_INSERTION", session, Long.class);
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("campaignId", creativeInsertion.getCampaignId());
        parameter.put("creativeGroupId", creativeInsertion.getCreativeGroupId());
        parameter.put("creativeId", creativeInsertion.getCreativeId());
        parameter.put("placementId", creativeInsertion.getPlacementId());
        parameter.put("startDate", creativeInsertion.getStartDate());
        parameter.put("endDate", creativeInsertion.getEndDate());
        parameter.put("timeZone", creativeInsertion.getTimeZone());
        parameter.put("weight", creativeInsertion.getWeight());
        parameter.put("clickthrough", creativeInsertion.getClickthrough());
        parameter.put("released", creativeInsertion.getReleased());
        parameter.put("sequence", creativeInsertion.getSequence());
        parameter.put("tpwsKey", creativeInsertion.getCreatedTpwsKey());

        Long creativeInsertionRootCnt = getPersistenceContext().selectSingle(CREATIVE_INSERTION_CREATIVE_INSERTION_ROOT_CNT,
                parameter, session, Long.class);
        Long creativeInsertionRootId;

        //set creativeInsertionRootId param
        if (creativeInsertionRootCnt > 0) {
            creativeInsertionRootId = getPersistenceContext().selectSingle(CREATIVE_INSERTION_CREATIVE_INSERTION_ROOT_ID,
                    parameter, session, Long.class);
            parameter.put("creativeInsertionRootId", creativeInsertionRootId);
        } else {
            creativeInsertionRootId = getNextId(session);
            parameter.put("creativeInsertionRootId", creativeInsertionRootId);
            getPersistenceContext().execute(CREATIVE_INSERTION_INSERT_CREATIVE_INSERTION_ROOT,
                    parameter, session);
        }

        //create creativeInsertion
        getPersistenceContext().execute(CREATIVE_INSERTION_INSERT_CREATIVE_INSERTION,
                parameter, session);

        //set richMediaId param
        Long richMediaId = getPersistenceContext().selectSingle(CREATIVE_INSERTION_RICH_MEDIA_ID,
                creativeInsertion.getCreativeId(), session, Long.class);
        parameter.put("richMediaId", richMediaId);
        if (richMediaId != null) {
            creativeInsertionRootCnt = getPersistenceContext().selectSingle(CREATIVE_INSERTION_CREATIVE_INSERTION_ROOT_CNT_RMI,
                    parameter, session, Long.class);
            if (creativeInsertionRootCnt <= 0) {
                creativeInsertionRootId = getNextId(session);
                parameter.put("creativeInsertionRootId", creativeInsertionRootId);
                getPersistenceContext().execute(CREATIVE_INSERTION_INSERT_CREATIVE_INSERTION_ROOT_RMI,
                        parameter, session);
            }
        }
        result = get(id, session);
        List<Clickthrough> clicks = saveClickthroughs(id, creativeInsertion.getClickthroughs(),
                creativeInsertion.getCreatedTpwsKey(), session);
        result.setClickthroughs(clicks);
        return result;
    }

    @Override
    public CreativeInsertion update(CreativeInsertion creativeInsertion, SqlSession session, OauthKey key) throws Exception {

        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", creativeInsertion.getId());
        parameter.put("startDate", creativeInsertion.getStartDate());
        parameter.put("endDate", creativeInsertion.getEndDate());
        parameter.put("weight", creativeInsertion.getWeight());
        parameter.put("clickthrough", creativeInsertion.getClickthrough());
        parameter.put("released", creativeInsertion.getReleased());
        parameter.put("sequence", creativeInsertion.getSequence());
        parameter.put("tpwsKey", key.getTpws());
        getPersistenceContext().callPlSqlStoredProcedure(CREATIVE_INSERTION_UPDATE_CREATIVE_INSERTION,
                parameter, session);
        CreativeInsertion result = get(creativeInsertion.getId(), session);
        removeClickthroughsOfCreativeInsertionId(creativeInsertion.getId(), session);
        List<Clickthrough> clicks = saveClickthroughs(creativeInsertion.getId(),
                creativeInsertion.getClickthroughs(), key.getTpws(), session);
        result.setClickthroughs(clicks);
        return result;
    }

    @Override
    public List<CreativeInsertion> bulkUpdate(List<CreativeInsertion> creativeInsertions, SqlSession session, OauthKey key) throws Exception {
        List<CreativeInsertion> updatedList = new ArrayList<>();
        if(creativeInsertions == null) {
            return updatedList;
        }

        for(CreativeInsertion creativeInsertion : creativeInsertions) {
            updatedList.add(this.update(creativeInsertion, session, key));
        }
        return updatedList;
    }    
    
    /**
     *
     * @param id
     * @param key
     * @param session
     * @deprecated This method performs a DataAccessControl check at a DAO level. Use {@link trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl#bulkDeleteById(Set, String, SqlSession)}  instead
     * @throws Exception
     */
    @Override
    public void delete(Long id, OauthKey key, SqlSession session) throws Exception {
        List<Long> ids = Arrays.asList(id);
        DataAccessControl.isUserValidForCreativeInsertion(ids, key.getUserId(), session);
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", id);
        parameter.put("tpwsKey", key.getTpws());
        getPersistenceContext().callPlSqlStoredProcedure(CREATIVE_INSERTION_DELETE_CREATIVE_INSERTION, parameter, session);
    }

    @Override
    public int bulkDeleteById(Set<Long> creativeInsertionIds, String tpwsKey, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("ids", creativeInsertionIds);
        parameter.put("tpwsKey", tpwsKey);
        return getPersistenceContext().update(STATEMENT_BULK_DELETE_BY_ID, parameter, session);
    }

    @Override
    public int bulkDeleteByFilterParam(Long campaignId, CreativeInsertionFilterParam ciBulk,
                                       String tpwsKey, SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("campaignId", campaignId);
        parameters.put("siteId", ciBulk.getSiteId());
        parameters.put("sectionId", ciBulk.getSectionId());
        parameters.put("groupId", ciBulk.getGroupId());
        parameters.put("placementId", ciBulk.getPlacementId());
        parameters.put("tpwsKey", tpwsKey);
        return getPersistenceContext().update(STATEMENT_BULK_DELETE_BY_FILTER_PARAM, parameters, session);
    }    

    @Override
    public void deleteByCreativeGroupId(Long id, OauthKey key, SqlSession session) throws Exception {
        List<Long> ids = Arrays.asList(id);
        DataAccessControl.isUserValidForCreativeGroup(ids, key.getUserId(), session);
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", id);
        parameter.put("tpwsKey", key.getTpws());
        getPersistenceContext().callPlSqlStoredProcedure(CREATIVE_INSERTION_DELETE_CREATIVE_INSERTION_BY_CG_ID,
                parameter, session);
    }

    @Override
    public ScheduleSet getScheduleSet(Long creativeGroupId, SqlSession session) throws Exception {
        List<TupleSchedule> tuples = null;
        List<CreativeInsertion> creativeInsertionIds = null;
        List<Schedule> result = new ArrayList<Schedule>();
        tuples = (List<TupleSchedule>) getPersistenceContext().selectList(CREATIVE_INSERTION_GET_DISTINCT_CREATIVE_INSERTIONS_PLACEMENTS, creativeGroupId, session);
        if (!tuples.isEmpty()) {
            for (TupleSchedule plId : tuples) {
                HashMap<String, Object> parameter = new HashMap<String, Object>();
                parameter.put("creativeGroupId", creativeGroupId);
                parameter.put("placementId", plId.getPlacementId());
                parameter.put("creativeId", plId.getCreativeId());
                creativeInsertionIds = (List<CreativeInsertion>) getPersistenceContext().selectList(CREATIVE_INSERTION_GET_DISTINCT_CREATIVE_INSERTION_BY_CREATIVE_GROUP_ID_AND_PLACEMENT_ID, parameter, session);
                Schedule tempSchedule = new Schedule();
                List<ScheduleEntry> scheduleEntrys = new ArrayList<ScheduleEntry>();
                for (CreativeInsertion temp : creativeInsertionIds) {
                    ScheduleEntry scheduleEntry = new ScheduleEntry();
                    scheduleEntry.setId(temp.getCreativeId());
                    List<Clickthrough> clickthroughs = (List<Clickthrough>) getPersistenceContext().selectList(CREATIVE_INSERTION_GET_CREATIVE_INSERTION_CLICKTHROUGHS, temp.getId(), session);
                    Clickthrough tempCT = new Clickthrough();
                    tempCT.setSequence(Constants.ENABLED);
                    tempCT.setUrl(temp.getClickthrough());
                    clickthroughs.add(tempCT);
                    scheduleEntry.setClickthroughs(clickthroughs);
                    scheduleEntry.setCreatedDate(temp.getCreatedDate());
                    scheduleEntry.setEndDate(temp.getEndDate());
                    Boolean released = (temp.getReleased() == null || temp.getReleased().equals(Constants.DISABLED)) ? false : true;
                    scheduleEntry.setIsReleased(released);
                    scheduleEntry.setModifiedDate(temp.getModifiedDate());
                    scheduleEntry.setSequence(temp.getSequence());
                    scheduleEntry.setStartDate(temp.getStartDate());
                    scheduleEntry.setTimeZone(temp.getTimeZone());
                    scheduleEntry.setWeight(temp.getWeight());
                    scheduleEntrys.add(scheduleEntry);
                    tempSchedule.setEntries(scheduleEntrys);
                    result.add(tempSchedule);
                }
                tempSchedule.setCreativeGroupId(creativeGroupId);
                tempSchedule.setCreativeId(plId.getCreativeId());
                HashMap<String, Object> parameterCIR = new HashMap<String, Object>();
                parameterCIR.put("creativeGroupId", creativeGroupId);
                parameterCIR.put("placementId", plId.getPlacementId());
                parameterCIR.put("creativeId", plId.getCreativeId());
                tempSchedule.setId((Long) getPersistenceContext().selectOne(CREATIVE_INSERTION_GET_CREATIVE_INSERTION_ROOT_ID_BY_CREATIVE_GROUP_ID, parameterCIR, session));
                tempSchedule.setPlacementId(plId.getPlacementId());
            }
            ScheduleSet scheduleSet = new ScheduleSet();
            scheduleSet.setSchedules(result);
            return scheduleSet;
        } else {
            throw new NotFoundException("Schedule not found");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Clickthrough> getClickthroughs(Long id, SqlSession session) {
        return getPersistenceContext().selectMultiple(CREATIVE_INSERTION_GET_CREATIVE_INSERTION_CLICKTHROUGHS, id, session);
    }

    @Override
    public Long getCampaingIdByGreativeGroupId(Long creativeGroupId) {
        try {
            List<Long> list = (List<Long>) MyBatisUtil.selectList(CREATIVE_INSERTION_GET_CAMPAING_ID_BY_GREATIVE_GROUP_ID, creativeGroupId);
            return list.size() > 0 ? list.get(0) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Long getScheduleCountByCreativeIds(SearchCriteria searchCriteria, String userId, SqlSession session)
        throws SearchApiException {
        HashMap<String, String> parameter = new HashMap<String, String>();
        Searcher<CreativeInsertion> searcher = new Searcher<>(searchCriteria.getQuery(), CreativeInsertion.class);
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("userId", userId);
        Integer result = getPersistenceContext().selectSingle(
            CREATIVE_INSERTION_GET_CREATIVE_INSERTIONS_NUMBER_OF_RECORDS_BY_CRITERIA,
            parameter, session, Integer.class);
        return Long.valueOf(result);
    }

    @Override
    public List<CreativeInsertionRawDataView> getCreativeInsertionsToExport(Long campaignId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_CREATIVE_INSERTIONS_EXPORT, parameter, session);
    }

    @Override
    public List<CreativeInsertionRawDataView> getCreativeInsertionsByUserId(Collection<Long> ids, String userId, Long campaignId, SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", ids);
        parameters.put("userId", userId);
        parameters.put("campaignId", campaignId);
        parameters.put("timezone", TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
        return getPersistenceContext().selectMultiple(STATEMENT_CREATIVE_INSERTIONS_BY_USERID, parameters, session);
    }    
    
    private List<Clickthrough> saveClickthroughs(Long creativeInsertionId, List<Clickthrough> clicks, String keyTpws, SqlSession session) {
        if (clicks != null) {
            for (Clickthrough click : clicks) {
                click.setCreativeInsertionId(creativeInsertionId);
                click.setModifiedTpwsKey(keyTpws);
                getPersistenceContext().execute(CREATIVE_INSERTION_INSERT_CREATIVE_INSERTION_CLICK,
                        click, session);
            }
            return getClickthroughs(creativeInsertionId, session);
        }
        return null;
    }
    
    private void createClickthroughs(List<Clickthrough> clickthroughs, SqlSession session) {
        if (clickthroughs != null) {
            for (Clickthrough click : clickthroughs) {
                getPersistenceContext().execute(CREATIVE_INSERTION_INSERT_CREATIVE_INSERTION_CLICK, click, session);
            }
        }
    }

    private void removeClickthroughsOfCreativeInsertionId(Long id, SqlSession session) {
        getPersistenceContext().execute(CREATIVE_INSERTION_DELETE_CREATIVE_INSERTION_CLICK,
                id, session);
    }    
    
    @Override
    public void updateOnImport(CreativeInsertionRawDataView creativeInsertion, SqlSession session) {
        //1. get Clickthroughs
        List<Clickthrough> clickthroughs = null;
        if(creativeInsertion.getCreativeClickThroughUrl() != null){
            String[] ctURLs = creativeInsertion.getCreativeClickThroughUrl().split(",");
            creativeInsertion.setCreativeClickThroughUrl(ctURLs[0].trim());
            if (ctURLs.length > 1) {
                clickthroughs = new ArrayList<>(ctURLs.length - 1);
                for (int i = 1; i < ctURLs.length; i++) {
                    Clickthrough ct = new Clickthrough(
                            i + 1L,
                            ctURLs[i].trim(),
                            creativeInsertion.getModifiedTpwsKey(),
                            Long.valueOf(creativeInsertion.getCreativeInsertionId()));
                    clickthroughs.add(ct);
                }
            }
        }
        
        // 2. update creativeInsertionData
        getPersistenceContext().execute(CREATIVE_INSERTION_UPDATE_ON_IMPORT, creativeInsertion, session);
        
        // 3. update clickthroughs
        if(clickthroughs != null && !clickthroughs.isEmpty()) {
            removeClickthroughsOfCreativeInsertionId(Long.valueOf(creativeInsertion.getCreativeInsertionId()), session);
            createClickthroughs(clickthroughs, session);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<CreativeInsertionView> getAllCreativeInsertions(Long campaignId, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("superiorLimit", startIndex + pageSize);
        parameter.put("inferiorLimit", startIndex);
        return getPersistenceContext().selectMultiple(GET_ALL_CREATIVE_INSERTIONS, parameter, session);
    }
    @Override
    public Long getAllCreativeInsertionsCount(Long campaignId, SqlSession session) {
        return getPersistenceContext().selectSingle(GET_ALL_CREATIVE_INSERTIONS_COUNT, campaignId, session, Long.class);
    }

    @Override
    public List<CreativeInsertionView> getSchedulesSiteLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pivotType", parentIds.getPivotType());
        parameter.put("campaignId", campaignId);
        parameter.put("groupId", parentIds.getGroupId());
        parameter.put("creativeId", parentIds.getCreativeId());
        return getPersistenceContext().
                selectMultiple(GET_CREATIVE_INSERTIONS_SITE_LEVEL, parameter,
                        new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
    }

    @Override
    public List<CreativeInsertionView> getSchedulesSectionLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("siteId", parentIds.getSiteId());
        return getPersistenceContext().
                selectMultiple(GET_CREATIVE_INSERTIONS_SECTION_LEVEL, parameter,
                        new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
    }

    @Override
    public List<CreativeInsertionView> getSchedulesPlacementLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pivotType", parentIds.getPivotType());
        parameter.put("campaignId", campaignId);
        parameter.put("siteId", parentIds.getSiteId());
        parameter.put("sectionId", parentIds.getSectionId());
        parameter.put("groupId", parentIds.getGroupId());
        parameter.put("creativeId", parentIds.getCreativeId());
        return getPersistenceContext().
                selectMultiple(GET_CREATIVE_INSERTIONS_PLACEMENT_LEVEL, parameter,
                        new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
    }

    @Override
    public List<CreativeInsertionView> getSchedulesGroupLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pivotType", parentIds.getPivotType());
        parameter.put("campaignId", campaignId);
        parameter.put("siteId", parentIds.getSiteId());
        parameter.put("sectionId", parentIds.getSectionId());
        parameter.put("placementId", parentIds.getPlacementId());
        parameter.put("creativeId", parentIds.getCreativeId());
        List<CreativeInsertionView> result = getPersistenceContext().
                selectMultiple(GET_CREATIVE_INSERTIONS_GROUP_LEVEL, parameter,
                        new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
        List<Long> ids = new ArrayList<>();
        if(result != null && !result.isEmpty()) {
            for (CreativeInsertionView ciView : result) {
                ids.add(ciView.getCreativeGroupId());
            }
            parameter.put("ids", ids);
            List<CreativeInsertionView> associations = getPersistenceContext().selectMultiple(GET_CREATIVE_INSERTIONS_GROUP_LEVEL_ASSOCIATIONS, parameter, session);
            if(associations != null && !associations.isEmpty()) {
                for (CreativeInsertionView ciView : result) {
                    for (CreativeInsertionView ciViewAssoc : associations) {
                        if(ciView.getCreativeGroupId().equals(ciViewAssoc.getCreativeGroupId())) {
                            ciView.setCreativeGroupAssociationsWithPlacements(ciViewAssoc.getCreativeGroupAssociationsWithPlacements());
                            ciView.setCreativeGroupAssociationsWithCreatives(ciViewAssoc.getCreativeGroupAssociationsWithCreatives());
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<CreativeInsertionView> getSchedulesCreativeLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pivotType", parentIds.getPivotType());
        parameter.put("campaignId", campaignId);
        parameter.put("inferiorLimit", startIndex);
        parameter.put("pageSize", pageSize);
        return getPersistenceContext().selectMultiple(GET_CREATIVE_INSERTIONS_CREATIVE_LEVEL, parameter, session);
    }

    @Override
    public List<CreativeInsertionView> getSchedulesScheduleLevel(Long campaignId, CreativeInsertionFilterParam parentIds, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pivotType", parentIds.getPivotType());
        parameter.put("campaignId", campaignId);
        parameter.put("siteId", parentIds.getSiteId());
        parameter.put("sectionId", parentIds.getSectionId());
        parameter.put("placementId", parentIds.getPlacementId());
        parameter.put("groupId", parentIds.getGroupId());
        parameter.put("creativeId", parentIds.getCreativeId());
        parameter.put("inferiorLimit", startIndex);
        parameter.put("pageSize", pageSize);
        return getPersistenceContext().selectMultiple(GET_CREATIVE_INSERTIONS_SCHEDULE_LEVEL, parameter, session);
    }

    @Override
    public Long getCountSchedulesByLevel(Long campaignId, CreativeInsertionFilterParam parentIds, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pivotType", parentIds.getPivotType());
        parameter.put("campaignId", campaignId);
        parameter.put("siteId", parentIds.getSiteId());
        parameter.put("sectionId", parentIds.getSectionId());
        parameter.put("placementId", parentIds.getPlacementId());
        parameter.put("groupId", parentIds.getGroupId());
        parameter.put("creativeId", parentIds.getCreativeId());
        
        String statement = null;
        switch (CreativeInsertionFilterParamTypeEnum.valueOf(parentIds.getType().toUpperCase())){
                case SITE:
                    statement = GET_COUNT_CREATIVE_INSERTIONS_SITE_LEVEL;
                    break;
                case SECTION:
                    statement = GET_COUNT_CREATIVE_INSERTIONS_SECTION_LEVEL;
                    break;
                case PLACEMENT:
                    statement = GET_COUNT_CREATIVE_INSERTIONS_PLACEMENT_LEVEL;
                    break;
                case GROUP:
                    statement = GET_COUNT_CREATIVE_INSERTIONS_GROUP_LEVEL;
                    break;
                case CREATIVE:
                    statement = GET_COUNT_CREATIVE_INSERTIONS_CREATIVE_LEVEL;
                    break;
                case SCHEDULE:
                    statement = GET_COUNT_CREATIVE_INSERTIONS_SCHEDULE_LEVEL;
                    break;
            }
        return getPersistenceContext().selectSingle(statement, parameter, session, Long.class);
    }

    @Override
    public int getCountAffectedSchedulesDueToImport(List<Long> cgIds, Long campaignId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("ids", cgIds);
        return getPersistenceContext().selectSingle(GET_COUNT_AFFECTED_SCHEDULES_DUE_TO_IMPORT, parameter, session, Integer.class);
    }

    @Override
    public List<CreativeInsertionView> searchSchedulesLevel(Long campaignId, CreativeInsertionFilterParam parentIds, CreativeInsertionSearchOptions searchOptions, String pattern, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pivotType", parentIds.getPivotType());
        parameter.put("type", parentIds.getType());
        parameter.put("pattern", pattern);
        parameter.put("campaignId", campaignId);
        parameter.put("startIndex", startIndex);
        parameter.put("pageSize", pageSize);
        
        parameter.put("siteId", parentIds.getSiteId());
        parameter.put("sectionId", parentIds.getSectionId());
        parameter.put("placementId", parentIds.getPlacementId());
        parameter.put("groupId", parentIds.getGroupId());
        parameter.put("creativeId", parentIds.getCreativeId());

        List<CreativeInsertionView> result = null;
        if(searchOptions.isSite() || searchOptions.isSection() || searchOptions.isPlacement() ||
                searchOptions.isGroup() || searchOptions.isCreative()) {
            parameter.put("soSite", searchOptions.isSite());
            parameter.put("soSection", searchOptions.isSection());
            parameter.put("soPlacement", searchOptions.isPlacement());
            parameter.put("soGroup", searchOptions.isGroup());
            parameter.put("soCreative", searchOptions.isCreative());

            result = getPersistenceContext().selectMultiple(SEARCH_CREATIVE_INSERTIONS, parameter, session);
            
            List<Long> pIds = new ArrayList<>();
            List<Long> cgIds = new ArrayList<>();
            List<Long> cIds = new ArrayList<>();
            if(result != null && !result.isEmpty()) {
                for (CreativeInsertionView ciView : result) {
                    if(ciView.getPlacementId() != null) {
                        pIds.add(ciView.getPlacementId());
                    }
                    if(ciView.getCreativeGroupId() != null) {
                        cgIds.add(ciView.getCreativeGroupId());
                    }
                    if(ciView.getCreativeId() != null) {
                        cIds.add(ciView.getCreativeId());
                    }
                }
                List<CreativeInsertionView> associations;
                if(!pIds.isEmpty()) {
                    parameter.put("ids", pIds);
                    associations = getPersistenceContext().
                            selectMultiple(SEARCH_CREATIVE_INSERTIONS_PLACEMENTS_ASSOCIATIONS, parameter, session);
                    if (associations != null && !associations.isEmpty()) {
                        for (CreativeInsertionView ciView : result) {
                            for (CreativeInsertionView ciViewAssoc : associations) {
                                if (ciView.getPlacementId() != null && ciView.getPlacementId().equals(ciViewAssoc.getPlacementId())) {
                                    ciView.setPlacementAssociationsWithCreativeGroups(ciViewAssoc.getPlacementAssociationsWithCreativeGroups());
                                }
                            }
                        }
                    }
                }
                if(!cgIds.isEmpty()) {
                    parameter.put("ids", cgIds);
                    associations = getPersistenceContext().selectMultiple(SEARCH_CREATIVE_INSERTIONS_CREATIVE_GROUPS_ASSOCIATIONS, parameter, session);
                    if (associations != null && !associations.isEmpty()) {
                        for (CreativeInsertionView ciView : result) {
                            for (CreativeInsertionView ciViewAssoc : associations) {
                                if(ciView.getCreativeGroupId() != null && ciView.getCreativeGroupId().equals(ciViewAssoc.getCreativeGroupId())) {
                                    if ("group".equalsIgnoreCase(parentIds.getPivotType())) {
                                        ciView.setCreativeGroupAssociationsWithPlacements(ciViewAssoc.getCreativeGroupAssociationsWithPlacements());
                                        ciView.setCreativeGroupAssociationsWithCreatives(ciViewAssoc.getCreativeGroupAssociationsWithCreatives());
                                    } else if (ciView.getPlacementId() != null && ciView.getPlacementId().equals(ciViewAssoc.getPlacementId())) {
                                        ciView.setCreativeGroupAssociationsWithPlacements(ciViewAssoc.getCreativeGroupAssociationsWithPlacements());
                                        ciView.setCreativeGroupAssociationsWithCreatives(ciViewAssoc.getCreativeGroupAssociationsWithCreatives());
                                    }
                                }
                            }
                        }
                    }
                }
                if(!cIds.isEmpty()) {
                    parameter.put("ids", cIds);
                    associations = getPersistenceContext().selectMultiple(SEARCH_CREATIVE_INSERTIONS_CREATIVE_ASSOCIATIONS, parameter, session);
                    if (associations != null && !associations.isEmpty()) {
                        for (CreativeInsertionView ciView : result) {
                            for (CreativeInsertionView ciViewAssoc : associations) {
                                if (ciView.getCreativeId() != null && ciView.getCreativeId().equals(ciViewAssoc.getCreativeId())) {
                                    ciView.setCreativeAssociationsWithPlacements(ciViewAssoc.getCreativeAssociationsWithPlacements());
                                    ciView.setCreativeAssociationsWithCreativeGroups(ciViewAssoc.getCreativeAssociationsWithCreativeGroups());
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Long searchSchedulesLevelCount(Long campaignId, CreativeInsertionFilterParam parentIds, CreativeInsertionSearchOptions searchOptions, String pattern, Long startIndex, Long pageSize, SqlSession session) {
        if(searchOptions.isSite() || searchOptions.isSection() || searchOptions.isPlacement() ||
                searchOptions.isGroup() || searchOptions.isCreative()) {
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("pivotType", parentIds.getPivotType());
            parameter.put("type", parentIds.getType());
            parameter.put("pattern", pattern);
            parameter.put("campaignId", campaignId);
            parameter.put("startIndex", startIndex);
            parameter.put("pageSize", pageSize);
            parameter.put("soSite", searchOptions.isSite());
            parameter.put("soSection", searchOptions.isSection());
            parameter.put("soPlacement", searchOptions.isPlacement());
            parameter.put("soGroup", searchOptions.isGroup());
            parameter.put("soCreative", searchOptions.isCreative());
            
            parameter.put("siteId", parentIds.getSiteId());
            parameter.put("sectionId", parentIds.getSectionId());
            parameter.put("placementId", parentIds.getPlacementId());
            parameter.put("groupId", parentIds.getGroupId());
            parameter.put("creativeId", parentIds.getCreativeId());

            return getPersistenceContext().
                    selectSingle(SEARCH_CREATIVE_INSERTIONS_COUNT, parameter, session, Long.class);
        }
        return 0L;
    }

    @Override
    public Map<Long, CreativeManager.CreativeGlobalClassification> getCreativeClassificationByPlacementId(Long campaignId, Collection<Long> ids, final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("campaignId", campaignId);
        Map<Long, CreativeManager.CreativeGlobalClassification> results = new HashMap<>();
        Accumulator<Map<Long, CreativeManager.CreativeGlobalClassification>> resultAccumulator = new MapAccumulatorImpl<>(results);
        return new ResultSetAccumulatorImpl<Map<Long, CreativeManager.CreativeGlobalClassification>>(
                "ids",
                new ArrayList<Object>(ids),
                resultAccumulator,
                params) {
            @Override
            public Map<Long, CreativeManager.CreativeGlobalClassification> execute(Object parameters) {
                CreativeClassificationByPlacementHandler handler = new CreativeClassificationByPlacementHandler();
                getPersistenceContext().select(STATEMENT_GET_CREATIVE_CLASSIFICATION_BY_PLACEMENT, parameters , session, handler);
                return handler.getMap();
            }
        }.getResults();
    }

    @Override
    public List<CreativeInsertionView> getSchedulesByCreativeId(Long campaignId, Long creativeId,
                                                                String userId, Long startIndex,
                                                                Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("creativeId", creativeId);
        parameter.put("userId", userId);
        parameter.put("inferiorLimit", startIndex);
        parameter.put("pageSize", pageSize);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_BY_CREATIVE_ID, parameter,
                new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
    }

    @Override
    public Long getCountSchedulesByCreativeId(Long campaignId, Long creativeId, String userId,
                                              SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("creativeId", creativeId);
        parameter.put("userId", userId);
        return getPersistenceContext().selectSingle(STATEMENT_GET_COUNT_BY_CREATIVE_ID, parameter,
                session, Long.class);
    }

    @Override
    public Long getCountSchedulesByrCreativeAndGroupIds(Long creativeId, Collection<Long> groupIds,
                                                        String userId, final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("creativeId", creativeId);
        params.put("userId", userId);

        Long result = 0L;
        Accumulator<Long> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Long>("ids", new ArrayList<>(groupIds),
                resultAccumulator, params) {
            @Override
            public Long execute(Object parameters) {
                return getPersistenceContext()
                        .selectSingle(STATEMENT_GET_COUNT_BY_CREATIVE_AND_GROUP_ID,
                                parameters,
                                session, Long.class);
            }
        }.getResults();
    }
}

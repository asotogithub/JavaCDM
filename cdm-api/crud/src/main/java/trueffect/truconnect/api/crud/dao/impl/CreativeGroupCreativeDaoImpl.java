package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeView;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Richard Jaldin
 */
public class CreativeGroupCreativeDaoImpl extends AbstractGenericDao implements CreativeGroupCreativeDao {

    private static final String STATEMENT_GET_BY_CREATIVE_GROUP_CREATIVE_ID = "CreativeGroupCreative.getCreativeGroupCreative";
    private static final String STATEMENT_GET_BY_CREATIVE_ID = "CreativeGroupCreative.getCreativeGroupCreativesByCreative";
    private static final String STATEMENT_SAVE = "CreativeGroupCreative.saveCreativeGroupCreative";
    private static final String STATEMENT_UPDATE = "CreativeGroupCreative.updateCreativeGroupCreative";
    private static final String STATEMENT_DELETE = "CreativeGroupCreative.deleteCreativeGroupCreative";
    private static final String STATEMENT_GET_BY_CRITERIA = "CreativeGroupCreative.getCreativeGroupCreativesByCriteria";
    private static final String STATEMENT_GET_NUMBER_OF_RECORDS_BY_CRITERIA = "CreativeGroupCreative.getCreativeGroupCreativesNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_BY_CREATIVE_GROUP_ID = "CreativeGroupCreative.getCreativeGroupCreativesByCG";    
    private static final String STATEMENT_GET_EXISTING_CREATIVE_GROUP_CREATIVE = "CreativeGroupCreative.getExistingCGC";    
    private static final String STATEMENT_GET_VIEW_BY_FILTER_PARAM = "CreativeGroupCreative.getGroupCreativesByFilterParam";
    private static final String STATEMENT_GET_COUNT_COUNT_VIEW_BY_FILTER_PARAM = "CreativeGroupCreative.getCountGroupCreativesByFilterParam";

    /**
     * Creates a new {@code GenericDaoImpl} with a specific {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this DAO
     */
    public CreativeGroupCreativeDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public CreativeGroupCreative get(Long creativeGroupId, Long creativeId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("creativeGroupId", creativeGroupId);
        parameter.put("creativeId", creativeId);
        parameter.put("defaultCreativeVersion", Constants.DEFAULT_CREATIVE_INITIAL_VERSION);
        return getPersistenceContext().selectSingle(STATEMENT_GET_BY_CREATIVE_GROUP_CREATIVE_ID,
                parameter, session, CreativeGroupCreative.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CreativeGroupCreative> getByCreative(Long creativeId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("creativeId", creativeId);
        parameter.put("defaultCreativeVersion", Constants.DEFAULT_CREATIVE_INITIAL_VERSION);
        return getPersistenceContext().selectMultiple(
                STATEMENT_GET_BY_CREATIVE_ID, parameter, session);
    }

    @Override
    public CreativeGroupCreative save(CreativeGroupCreative creativeGroupCreative,
                                      SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("creativeGroupId", creativeGroupCreative.getCreativeGroupId());
        parameter.put("creativeId", creativeGroupCreative.getCreativeId());
        parameter.put("displayOrder", creativeGroupCreative.getDisplayOrder());
        parameter.put("displayQuantity", creativeGroupCreative.getDisplayQuantity());
        parameter.put("tpwsKey", creativeGroupCreative.getCreatedTpwsKey());
        getPersistenceContext().execute(STATEMENT_SAVE, parameter, session);
        return this.get(creativeGroupCreative.getCreativeGroupId(),
                creativeGroupCreative.getCreativeId(), session);
    }

    @Override
    public CreativeGroupCreative update(CreativeGroupCreative creativeGroupCreative, OauthKey key, SqlSession session) throws Exception {
        CreativeGroupCreative result = null;
        try {
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("creativeGroupId", creativeGroupCreative.getCreativeGroupId());
            parameter.put("creativeId", creativeGroupCreative.getCreativeId());
            parameter.put("displayOrder", creativeGroupCreative.getDisplayOrder());
            parameter.put("displayQuantity", creativeGroupCreative.getDisplayQuantity());
            parameter.put("tpwsKey", key.getTpws());
            getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_UPDATE, parameter, session);
            result = this.get(creativeGroupCreative.getCreativeGroupId(), creativeGroupCreative.getCreativeId(), session);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    @Override
    public void remove(Long creativeGroupId, Long creativeId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("creativeGroupId", creativeGroupId);
        parameter.put("creativeId", creativeId);
        getPersistenceContext().update(STATEMENT_DELETE, parameter, session);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<CreativeGroupCreative> getByCreativeGroup(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception {
        if (criteria.getPageSize() > 1000) {
            throw new Exception("The page size allows up to 1000 records.");
        }
        if (criteria.getStartIndex() < 0) {
            throw new Exception("Cannot retrieve records for start index: " + criteria.getStartIndex() + ". The minimum start index is: 0");
        }

        Searcher<CreativeGroupCreative> searcher = new Searcher<>(criteria.getQuery(), CreativeGroupCreative.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());
        parameter.put("defaultCreativeVersion", Long.toString(Constants.DEFAULT_CREATIVE_INITIAL_VERSION));

        // Getting data only for the page
        List<CreativeGroupCreative> aux = (List<CreativeGroupCreative>) getPersistenceContext().selectList(STATEMENT_GET_BY_CRITERIA,
                parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<CreativeGroupCreative>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = (Integer) getPersistenceContext().selectOne(STATEMENT_GET_NUMBER_OF_RECORDS_BY_CRITERIA, parameter, session);
        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        return new RecordSet<>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
    }

    @Override
    public List<CreativeGroupCreative> getCreativeGroupCreativesByCG(Long creativeGroupId, OauthKey key, SqlSession session) throws Exception {
        List<CreativeGroupCreative> result = null;
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("creativeGroupId", creativeGroupId);
        result = (List<CreativeGroupCreative>) getPersistenceContext().selectList(STATEMENT_GET_BY_CREATIVE_GROUP_ID, parameter, session);
        return result;
    }

    @Override
    public List<CreativeGroupCreativeView> getGroupCreativesByFilterParam(Long campaignId, CreativeInsertionFilterParam filterParam, String userId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("userId", userId);
        parameter.put("pivotType", filterParam.getPivotType());
        parameter.put("type", filterParam.getType());
        parameter.put("campaignId", campaignId);

        parameter.put("siteId", filterParam.getSiteId());
        parameter.put("sectionId", filterParam.getSectionId());
        parameter.put("placementId", filterParam.getPlacementId());
        parameter.put("groupId", filterParam.getGroupId());
        parameter.put("creativeId", filterParam.getCreativeId());
        List<CreativeGroupCreativeView> result = getPersistenceContext().selectMultiple(STATEMENT_GET_VIEW_BY_FILTER_PARAM, parameter, session);
        return result;
    }

    @Override
    public List<String> getExistingCGC(List<String> cgcs, Long campaignId, final SqlSession session){
        HashMap<String, Object> parameter = new HashMap<>();
        List<String> result = new ArrayList<>();
        parameter.put("campaignId", campaignId);
        Accumulator<List<String>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<String>>(
                "cgcs",
                cgcs,
                resultAccumulator,
                parameter) {
            @Override
            public List<String> execute(Object parameters) {
                return getPersistenceContext().selectMultiple(STATEMENT_GET_EXISTING_CREATIVE_GROUP_CREATIVE, 
                        parameters, session);
            }
        }.getResults();
    }
    
    public Long getCountGroupCreativesByFilterParam(Long campaignId, CreativeInsertionFilterParam filterParam, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pivotType", filterParam.getPivotType());
        parameter.put("type", filterParam.getType());
        parameter.put("campaignId", campaignId);
        parameter.put("siteId", filterParam.getSiteId());
        parameter.put("sectionId", filterParam.getSectionId());
        parameter.put("placementId", filterParam.getPlacementId());
        parameter.put("groupId", filterParam.getGroupId());
        parameter.put("creativeId", filterParam.getCreativeId());
        return getPersistenceContext().selectSingle(STATEMENT_GET_COUNT_COUNT_VIEW_BY_FILTER_PARAM, parameter, session, Long.class);
    }
}

package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupTarget;
import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.GeoTarget;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.commons.model.enums.LocationType;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.SumAccumulatorImpl;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Richard Jaldin
 */
public class CreativeGroupDaoImpl extends AbstractGenericDao implements CreativeGroupDao {

    private static final String STATEMENT_GET_BY_ID = "CreativeGroup.getCreativeGroup";
    private static final String STATEMENT_GET_GRUPS_BY_IDS = "CreativeGroup.getCreativeGroupByIds";
    private static final String STATEMENT_GET_TARGETS = "CreativeGroup.getCreativeGroupTargets";
    private static final String STATEMENT_GET_BY_CRITERIA = "CreativeGroup.getCreativeGroupsByCriteria";
    private static final String STATEMENT_GET_NUMBER_OF_RECORDS_BY_CRITERIA = "CreativeGroup.getCreativeGroupsNumberOfRecordsByCriteria";
    private static final String STATEMENT_COUNT_GROUPS_BY_CAMPAIGN_ID = "CreativeGroup.getCountCreativeGroupsBelongsCampaign";
    private static final String STATEMENT_SAVE = "CreativeGroup.saveCreativeGroup";
    private static final String STATEMENT_UPDATE = "CreativeGroup.updateCreativeGroup";
    private static final String STATEMENT_REMOVE = "CreativeGroup.removeCreativeGroup";
    private static final String STATEMENT_GET_ALREADY_EXISTS = "CreativeGroup.creativeGroupAlreadyExists";
    private static final String STATEMENT_VALID_TARGET_VALUES = "CreativeGroup.validTargetValues";
    private static final String STATEMENT_INSERT_TARGET = "CreativeGroup.insertCreativeGroupTarget";
    private static final String STATEMENT_DELETE_TARGET = "CreativeGroup.deleteCreativeGroupTarget";
    private static final String STATEMENT_UPDATE_ON_IMPORT = "CreativeGroup.updateCreativeGroupOnImport";
    private static final String STATEMENT_DELETE_CREATIVES_FROM_CREATIVE = "CreativeGroup.deleteCreatives";

    /**
     * Creates a new {@code GenericDaoImpl} with a specific {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this DAO
     */
    public CreativeGroupDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }
    
    @Override
    public CreativeGroup get(Long id, OauthKey key, SqlSession session) throws Exception {
        CreativeGroup creativeGroup = this.getById(id, session);
        creativeGroup.setDoDaypartTargeting(creativeGroup.getDoDaypartTargeting() == null ? 0L : creativeGroup.getDoDaypartTargeting());
        List<GeoTarget> geoTargets = getGeoTargets(id, session);
        creativeGroup.setGeoTargets(geoTargets);
        return creativeGroup;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<CreativeGroup> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception {
        if (criteria.getPageSize() > 1000) {
            throw new Exception("The page size allows up to 1000 records.");
        }
        if (criteria.getStartIndex() < 0) {
            throw new Exception("Cannot retrieve records for start index: " + criteria.getStartIndex() + ". The minimum start index is: 0");
        }

        Searcher<CreativeGroup> searcher = new Searcher<>(criteria.getQuery(), CreativeGroup.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        // Getting data only for the page
        List<CreativeGroup> aux = (List<CreativeGroup>) getPersistenceContext().selectList(STATEMENT_GET_BY_CRITERIA,
                parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.isEmpty()) { // empty result.
            return new RecordSet<>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = getPersistenceContext().selectOne(STATEMENT_GET_NUMBER_OF_RECORDS_BY_CRITERIA, parameter, session, Integer.class);
        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        if(aux != null){
            for (CreativeGroup cg : aux) {
                cg.setDoDaypartTargeting(cg.getDoDaypartTargeting() == null ? 0L : cg.getDoDaypartTargeting());
                List<GeoTarget> geoTargets = getGeoTargets(cg.getId(), session);
                cg.setGeoTargets(geoTargets);
            }
        }
        return new RecordSet<>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
    }
    
    @Override
    public List<CreativeGroup> getCreativeGroupsByIds(Collection<Long> groupIds, final SqlSession session){
        HashMap<String, Object> parameters = new HashMap<>();
        List<CreativeGroup> result = new ArrayList<>();
        Accumulator<List<CreativeGroup>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<CreativeGroup>>(
                "ids",
                new ArrayList<Long>(groupIds),
                resultAccumulator,
                parameters) {
            @Override
            public List<CreativeGroup> execute(Object parameters) {
                return getPersistenceContext().selectMultiple(STATEMENT_GET_GRUPS_BY_IDS,
                        parameters, session);
            }

        }.getResults();
    }

    @Override
    public List<Long> getInvalidTargetValues(List<Long> creativeGroupTargets, SqlSession session) throws Exception {
        List<Long> ids = new ArrayList<>(creativeGroupTargets);
        List<Long> results = (List<Long>) getPersistenceContext().selectList(STATEMENT_VALID_TARGET_VALUES, ids, session);
        ids.removeAll(results);
        return ids;
    }

    @Override
    public CreativeGroup save(CreativeGroup group, OauthKey key, SqlSession session) throws Exception {
        Long id = super.getNextId(session);
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("campaignId", group.getCampaignId());
        parameter.put("name", group.getName());
        parameter.put("rotationType", group.getRotationType());
        parameter.put("impressionCap", group.getImpressionCap());
        parameter.put("clickthroughCap", group.getClickthroughCap());
        parameter.put("weight", group.getWeight());
        parameter.put("isReleased", group.getIsReleased());
        parameter.put("isDefault", group.getIsDefault());
        parameter.put("cookieTarget", group.getCookieTarget());
        parameter.put("daypartTarget", group.getDaypartTarget());
        parameter.put("doOptimization", group.getDoOptimization());
        parameter.put("optimizationType", group.getOptimizationType());
        parameter.put("optimizationSpeed", group.getOptimizationSpeed());
        parameter.put("minOptimizationWeight", group.getMinOptimizationWeight());
        parameter.put("doGeoTargeting", group.getDoGeoTargeting());
        parameter.put("doCookieTarget", group.getDoCookieTargeting());
        parameter.put("doDaypartTarget", group.getDoDaypartTargeting());
        parameter.put("doStoryboarding", group.getDoStoryboarding());
        parameter.put("tpwsKey", key.getTpws());
        parameter.put("enableGroupWeight", group.getEnableGroupWeight());
        parameter.put("priority", group.getPriority());
        parameter.put("enableFrequencyCap", group.getEnableFrequencyCap());
        parameter.put("frequencyCap", group.getFrequencyCap());
        parameter.put("frequencyCapWindow", group.getFrequencyCapWindow());
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_SAVE, parameter, session);
        CreativeGroup result = getById(id, session);
        saveCreativeGroupTargets(id, group.getGeoTargets(), session, key);
        result.setGeoTargets(getGeoTargets(id, session));
        return result;
    }

    @Override
    public CreativeGroup update(CreativeGroup group, OauthKey key, SqlSession session) throws Exception {
        CreativeGroup result = null;

        CreativeGroup source = getById(group.getId(), session);
        group.setCampaignId(source.getCampaignId());

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", group.getId());
        parameter.put("name", group.getName());
        parameter.put("rotationType", group.getRotationType());
        parameter.put("impressionCap", group.getImpressionCap());
        parameter.put("clickthroughCap", group.getClickthroughCap());
        parameter.put("weight", group.getWeight());
        parameter.put("isReleased", source.getIsReleased());
        parameter.put("isDefault", group.getIsDefault());
        parameter.put("cookieTarget", group.getCookieTarget());
        parameter.put("daypartTarget", group.getDaypartTarget());
        parameter.put("doOptimization", group.getDoOptimization());
        parameter.put("optimizationType", group.getOptimizationType());
        parameter.put("optimizationSpeed", group.getOptimizationSpeed());
        parameter.put("minOptimizationWeight", group.getMinOptimizationWeight());
        parameter.put("doGeoTargeting", group.getDoGeoTargeting());
        parameter.put("doCookieTarget", group.getDoCookieTargeting());
        parameter.put("doDaypartTarget", group.getDoDaypartTargeting());
        parameter.put("doStoryboarding", group.getDoStoryboarding());
        parameter.put("tpwsKey", key.getTpws());
        parameter.put("enableGroupWeight", group.getEnableGroupWeight());
        parameter.put("priority", group.getPriority());
        parameter.put("enableFrequencyCap", group.getEnableFrequencyCap());
        parameter.put("frequencyCap", group.getFrequencyCap());
        parameter.put("frequencyCapWindow", group.getFrequencyCapWindow());
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_UPDATE, parameter, session);
        result = this.getById(group.getId(), session);

        deleteCreativeGroupTarget(group.getId(), session);
        saveCreativeGroupTargets(group.getId(), group.getGeoTargets(), session, key);
        result.setGeoTargets(getGeoTargets(group.getId(), session));
        return result;
    }

    @Override
    public void remove(Long id, OauthKey key, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", id);
        parameter.put("modifiedTpwsKey", key.getTpws());
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_REMOVE, parameter, session);
    }

    @Override
    public List<CreativeGroup> bulkUpdate(List<CreativeGroup> creativeGroups, SqlSession session, OauthKey key) throws Exception {
        List<CreativeGroup> updatedList = new ArrayList<>();
        if(creativeGroups == null) {
            return updatedList;
        }

        for(CreativeGroup creativeGroup : creativeGroups) {
            updatedList.add(this.update(creativeGroup, key, session));
        }
        return updatedList;
    }
    
    @Override
    public void updateOnImport(CreativeInsertionRawDataView group, SqlSession session) {
        getPersistenceContext().execute(STATEMENT_UPDATE_ON_IMPORT, group, session);
    }    

    @Override
    public Long getCountCreativeGroupsByCampaignId(Long campaignId, Collection<Long> ids,
                                                   final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("campaignId", campaignId);

        Long result = 0L;
        Accumulator<Long> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Long>("ids", new ArrayList<>(ids), resultAccumulator,
                parameters) {
            @Override
            public Long execute(Object parameters) {
                return getPersistenceContext()
                        .selectSingle(STATEMENT_COUNT_GROUPS_BY_CAMPAIGN_ID, parameters,
                                session, Long.class);
            }
        }.getResults();
    }

    @Override
    public Boolean creativeGroupExists(CreativeGroup cg, SqlSession session) {
        Long numberOfCreatives = getPersistenceContext().selectSingle(STATEMENT_GET_ALREADY_EXISTS, cg, session, Long.class);
        return numberOfCreatives.compareTo(0L) > 0 ;
    }
    
    protected CreativeGroup getById(Long creativeGroupId, SqlSession session) {
        CreativeGroup cg = getPersistenceContext().selectSingle(STATEMENT_GET_BY_ID, creativeGroupId, session, CreativeGroup.class);
        return cg;
    }    

    private void saveCreativeGroupTargets(Long creativeGroupId, List<GeoTarget> geoTargets, SqlSession session, OauthKey key) {
        if(geoTargets == null) {
            return;
        }

        Map<Long,CreativeGroupTarget> dupCheck = new HashMap<>();

        for(GeoTarget geoTarget : geoTargets) {
            List<CreativeGroupTarget> creativeGroupTargets = geoTarget.getCreativeGroupTargets();
            for(CreativeGroupTarget creativeGroupTarget : creativeGroupTargets) {
                if (!dupCheck.containsKey(creativeGroupTarget.getValueId())) {
                    dupCheck.put(creativeGroupTarget.getValueId(), creativeGroupTarget);
                    creativeGroupTarget.setCreativeGroupId(creativeGroupId);
                    creativeGroupTarget.setCreatedTpwsKey(key.getTpws());
                    creativeGroupTarget.setModifiedTpwsKey(key.getTpws());
                    getPersistenceContext().execute(STATEMENT_INSERT_TARGET, creativeGroupTarget, session);
                }
            }
        }
    }

    private void deleteCreativeGroupTarget(Long id, SqlSession session) throws Exception {
        HashMap<String, Object> parameter;
        parameter = new HashMap<String, Object>();
        parameter.put("creativeGroupId", id);
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_DELETE_TARGET, parameter, session);
    }

    protected List<GeoTarget> getGeoTargets(Long id, SqlSession session) throws Exception {
        @SuppressWarnings("unchecked")
        List<CreativeGroupTarget> creativeGroupTargets = (List<CreativeGroupTarget>)
                getPersistenceContext().selectList(STATEMENT_GET_TARGETS, id, session);
        List<GeoTarget> result = new ArrayList<>(0);
        if(!creativeGroupTargets.isEmpty()) {
            Map<LocationType, GeoTarget> locationMap = locationMap(creativeGroupTargets);
            result = new ArrayList<>(locationMap.values());
        }
        return result;
    }

    protected Map<LocationType, GeoTarget> locationMap(List<CreativeGroupTarget> creativeGroupTargets) {
        Map<LocationType, GeoTarget> targets = new HashMap<>();
        if(creativeGroupTargets != null) {
            for (CreativeGroupTarget creativeGroupTarget : creativeGroupTargets) {
                LocationType locationType = LocationType.typeOf(creativeGroupTarget.getTypeCode());
                GeoTarget existingTarget = targets.get(locationType);
                if (existingTarget == null) {
                    existingTarget = new GeoTarget();
                    existingTarget.setAntiTarget(creativeGroupTarget.getAntiTarget());
                    existingTarget.setTypeCode(creativeGroupTarget.getTypeCode());
                }
                GeoLocation geoLocation = new GeoLocation();
                geoLocation.setId(creativeGroupTarget.getValueId());
                geoLocation.setLabel(creativeGroupTarget.getTargetLabel());
                existingTarget.addTarget(geoLocation);
                targets.put(locationType, existingTarget);
            }
        }
        return targets;
    }

    @Override
    public Integer deleteCreativeFromCreative(Long creativeGroupId, String tpwsKey, SqlSession session){
        HashMap<String, Object> parameter;
        parameter = new HashMap<String, Object>();
        parameter.put("creativeGroupId", creativeGroupId);
        return getPersistenceContext().update(STATEMENT_DELETE_CREATIVES_FROM_CREATIVE, parameter, session);
    }


}

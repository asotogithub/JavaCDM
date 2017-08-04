package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gustavo Claure
 */
public class CreativeGroupContextMock extends PersistenceContextMock {

    private Map<Long, CreativeGroup> creativeGroups;

    public CreativeGroupContextMock(Map<Long, CreativeGroup> creativeGroups) {
        this.creativeGroups = creativeGroups;
    }

    public Map<Long, CreativeGroup> getCreativeGroups() {
        return creativeGroups;
    }

    @Override
    public void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception {
        super.callPlSqlStoredProcedure(xmlMethod, parameterMap, session);
        if ("CreativeGroup.saveCreativeGroup".equalsIgnoreCase(xmlMethod) ||
                "CreativeGroup.updateCreativeGroup".equalsIgnoreCase(xmlMethod)) {
            CreativeGroup cg = new CreativeGroup();
            cg.setId((Long) parameterMap.get("id"));
            cg.setName((String) parameterMap.get("name"));
            cg.setCampaignId((Long) parameterMap.get("campaignId"));
            cg.setRotationType((String) parameterMap.get("rotationType"));
            cg.setOptimizationType((String) parameterMap.get("optimizationType"));
            cg.setOptimizationSpeed((String) parameterMap.get("optimizationSpeed"));
            cg.setCreatedTpwsKey((String) parameterMap.get("tpwsKey"));
            cg.setImpressionCap((Long) parameterMap.get("impressionCap"));
            cg.setClickthroughCap((Long) parameterMap.get("clickthroughCap"));
            cg.setWeight((Long) parameterMap.get("weight"));
            cg.setIsDefault((Long) parameterMap.get("isDefault"));
            cg.setCookieTarget((String) parameterMap.get("cookieTarget"));
            cg.setDaypartTarget((String) parameterMap.get("daypartTarget"));
            cg.setDoOptimization((Long) parameterMap.get("doOptimization"));
            cg.setMinOptimizationWeight((Long) parameterMap.get("minOptimizationWeight"));
            cg.setDoGeoTargeting((Long) parameterMap.get("doGeoTargeting"));
            cg.setDoCookieTargeting((Long) parameterMap.get("doCookieTarget"));
            cg.setDoDaypartTargeting((Long) parameterMap.get("doDaypartTarget"));
            cg.setDoStoryboarding((Long) parameterMap.get("doStoryboarding"));
            cg.setEnableGroupWeight((Long) parameterMap.get("enablecgWeight"));
            cg.setPriority((Long) parameterMap.get("priority"));
            cg.setEnableFrequencyCap((Long) parameterMap.get("enableFrequencyCap"));
            cg.setFrequencyCap((Long) parameterMap.get("frequencyCap"));
            cg.setFrequencyCapWindow((Long) parameterMap.get("frequencyCapWindow"));
            cg.setIsReleased((Long) parameterMap.get("isReleased"));
            cg.setEnableGroupWeight((Long) parameterMap.get("enableGroupWeight"));
            cg.setModifiedTpwsKey((String) parameterMap.get("tpwsKey"));
            creativeGroups.put(cg.getId(), cg);
        }else if("CreativeGroup.removeCreativeGroup".equalsIgnoreCase(xmlMethod)){
            Long cgId = (Long) parameterMap.get("id");
            creativeGroups.remove(cgId);
        }
    }

    @Override
    public <T> T selectOne(String xmlMethod, Object parameter, SqlSession session, Class<T> type) throws Exception {
        if("CreativeGroup.getCreativeGroupsNumberOfRecordsByCriteria".equalsIgnoreCase(xmlMethod)){
            return type.cast(new Integer(creativeGroups.size()));
        } else if("CreativeGroup.creativeGroupAlreadyExists".equalsIgnoreCase(xmlMethod)){
            //query by cg.groupName and campaignId and id if this is provided
            CreativeGroup cg = (CreativeGroup) parameter;
            creativeGroups.containsKey(cg.getId());
            Long cont = 0L;
            for (Map.Entry<Long, CreativeGroup> entrySet : creativeGroups.entrySet()) {
                CreativeGroup group = entrySet.getValue();
                if (group.getName().equals(cg.getName()) && group.getCampaignId().equals(cg.getCampaignId())) {
                    if (cg.getId() != null) {
                        if (group.getId().equals(cg.getId())) {
                            cont ++;
                        }
                    }else{
                        cont ++;
                    }
                }
            }
            return type.cast(cont);
        } else {
            return super.selectOne(xmlMethod, parameter, session, type);
        }
    }

    @Override
    public <T> T selectSingle(String statement, Object parameter, SqlSession session,
                              Class<T> type) {
        if("CreativeGroup.creativeGroupAlreadyExists".equalsIgnoreCase(statement)){
            //query by cg.groupName and campaignId and id if this is provided
            CreativeGroup cg = (CreativeGroup) parameter;
            creativeGroups.containsKey(cg.getId());
            Long cont = 0L;
            for (Map.Entry<Long, CreativeGroup> entrySet : creativeGroups.entrySet()) {
                CreativeGroup group = entrySet.getValue();
                if (group.getName().equals(cg.getName()) && group.getCampaignId().equals(cg.getCampaignId())) {
                    if (cg.getId() != null) {
                        if (group.getId().equals(cg.getId())) {
                            cont ++;
                        }
                    }else{
                        cont ++;
                    }
                }
            }
            return type.cast(cont);
        } else {
            return super.selectSingle(statement, parameter, session, type);
        }
    }

    @Override
    public List<?> selectList(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) throws Exception {
        List<CreativeGroup> result = new ArrayList<CreativeGroup>();
        for( Map.Entry<Long, CreativeGroup> entry : creativeGroups.entrySet()){
            result.add(entry.getValue());
        }
        return result;
    }

}

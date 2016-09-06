package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gustavo Claure
 */
public class CreativeGroupCreativeContextMock extends PersistenceContextMock {

    private Map<String, CreativeGroupCreative> creativeGroupCreatives;

    public CreativeGroupCreativeContextMock(Map<String, CreativeGroupCreative> creativeGroupCreatives) {
        this.creativeGroupCreatives = creativeGroupCreatives;
    }

    public Map<String, CreativeGroupCreative> getCreativeGroupCreatives() {
        return creativeGroupCreatives;
    }

    @Override
    public void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception {
        super.callPlSqlStoredProcedure(xmlMethod, parameterMap, session);
        if ("CreativeGroupCreative.saveCreativeGroupCreative".equalsIgnoreCase(xmlMethod)) {
            CreativeGroupCreative cgc = new CreativeGroupCreative();
            cgc.setCreativeGroupId((Long)parameterMap.get("creativeGroupId"));
            cgc.setCreativeId((Long)parameterMap.get("creativeId"));
            cgc.setDisplayOrder((Long)parameterMap.get("displayOrder"));
            cgc.setDisplayQuantity((Long)parameterMap.get("displayQuantity"));
            cgc.setCreatedTpwsKey((String)parameterMap.get("tpwsKey"));
            creativeGroupCreatives.put(cgc.getCreativeGroupId()+","+cgc.getCreativeId(), cgc);
        } else if ("CreativeGroupCreative.updateCreativeGroupCreative".equalsIgnoreCase(xmlMethod)) {
            CreativeGroupCreative cgc = new CreativeGroupCreative();
            cgc.setCreativeGroupId((Long)parameterMap.get("creativeGroupId"));
            cgc.setCreativeId((Long)parameterMap.get("creativeId"));
            cgc.setDisplayOrder((Long)parameterMap.get("displayOrder"));
            cgc.setDisplayQuantity((Long)parameterMap.get("displayQuantity"));
            cgc.setModifiedTpwsKey((String)parameterMap.get("tpwsKey"));
            creativeGroupCreatives.put(cgc.getCreativeGroupId()+","+cgc.getCreativeId(), cgc);
        }
    }

}

package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gustavo Claure
 */
public class CreativePersistenceContextMock extends PersistenceContextMock {

    private static Long globalSequence = 0L;

    private Map<Long, Creative> creatives;

    public CreativePersistenceContextMock(Map<Long, Creative> creatives) {
        this.creatives = creatives;
    }

    public Map<Long, Creative> getCreatives() {
        return creatives;
    }

    @Override
    public <T> T selectOne(String xmlMethod, Object parameter, SqlSession session, Class<T> type) throws Exception {
        if ("CreativePkg.getCreativesNumberOfRecordsByCriteria".equalsIgnoreCase(xmlMethod)) {
            return type.cast(new Integer(creatives.size()));
        } else {
            return super.selectOne(xmlMethod, parameter, session, type);
        }
    }

    @Override
    public List<?> selectList(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) throws Exception {
        List<Creative> result = new ArrayList<Creative>();
        for (Map.Entry<Long, Creative> entry : creatives.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception {
        super.callPlSqlStoredProcedure(xmlMethod, parameterMap, session);
        if ("CreativePkg.insertCreative".equalsIgnoreCase(xmlMethod)
                || "CreativePkg.updateCreative".equalsIgnoreCase(xmlMethod)) {
            Creative crtv = new Creative();
            crtv.setId((Long) parameterMap.get("id"));
            crtv.setAgencyId((Long) parameterMap.get("agencyId"));
            crtv.setAlias((String) parameterMap.get("alias"));
            crtv.setCampaignId((Long) parameterMap.get("campaignId"));
            crtv.setClickthrough((String) parameterMap.get("clickthrough"));
            crtv.setCreatedDate((Date) parameterMap.get("createdDate"));
            crtv.setCreatedTpwsKey((String) parameterMap.get("tpwsKey"));
            crtv.setCreativeType((String) parameterMap.get("creativeType"));
            crtv.setExtProp1((String) parameterMap.get("extProp1"));
            crtv.setExtProp2((String) parameterMap.get("extProp2"));
            crtv.setExtProp3((String) parameterMap.get("extProp3"));
            crtv.setExtProp4((String) parameterMap.get("extProp4"));
            crtv.setExternalId((String) parameterMap.get("externalId"));
            crtv.setFileSize((Long) parameterMap.get("fileSize"));
            crtv.setFilename((String) parameterMap.get("filename"));
            crtv.setHeight((Long) parameterMap.get("height"));
            crtv.setIsExpandable((Long) parameterMap.get("isExpandable"));
            crtv.setModifiedDate((Date) parameterMap.get("modifiedDate"));
            crtv.setModifiedTpwsKey((String) parameterMap.get("tpwsKey"));
            crtv.setOwnerCampaignId((Long) parameterMap.get("ownerCampaignId"));
            crtv.setPurpose((String) parameterMap.get("purpose"));
            crtv.setReleased((Long) parameterMap.get("released"));
            crtv.setRichMediaId((Long) parameterMap.get("richMediaId"));
            crtv.setScheduled((Long) parameterMap.get("scheduled"));
            crtv.setSetCookieString((String) parameterMap.get("cookieString"));
            crtv.setSwfClickCount((Long) parameterMap.get("swfClickCount"));
            crtv.setWidth((Long) parameterMap.get("width"));
            creatives.put(crtv.getId(), crtv);
        } else if ("CreativePkg.deleteCreative".equalsIgnoreCase(xmlMethod)) {
            Long id = (Long) parameterMap.get("id");
            creatives.remove(id);
        }
    }
}

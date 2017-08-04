package trueffect.truconnect.api.crud.dao;

import java.util.HashMap;
import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.SmEventPing;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;

/**
 *
 * @author Rambert Rioja
 */
public class SmEventPingDao {

    public SmEventPing get(Long id) throws Exception {
        return (SmEventPing) MyBatisUtil.selectOne("getSmEventPing", id);
    }

    public SmEventPing get(Long id, SqlSession session) throws Exception {
        return (SmEventPing) MyBatisUtil.selectOne("getSmEventPing", id, session);
    }

    public SmEventPing save(Long smEventId, Long siteId,
            String pingContent, String description, Long pingType,
            Long tagType, OauthKey key) throws Exception {
        SqlSession session = null;
        SmEventPing result = null;
        try {
            session = MyBatisUtil.beginTransaction();
            Long smEventPingId = (Long) MyBatisUtil.selectOne("getNextId",
                    "SEQ_SM_EVENT_PING", session);
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("smEventPingId", smEventPingId);
            parameter.put("smEventId", smEventId);
            parameter.put("siteId", siteId);
            parameter.put("pingContent", pingContent);
            parameter.put("description", description);
            parameter.put("pingType", pingType);
            parameter.put("tagType", tagType);
            parameter.put("createdTpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql("insertSmEventPing", parameter, session);
            result = get(smEventPingId, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public SmEventPing update(Long smEventPingId, String pingContent,
            String description, Long pingType, Long tagType,
            OauthKey key) throws Exception {
        SqlSession session = null;
        SmEventPing result = null;
        try {
            session = MyBatisUtil.beginTransaction();
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("smEventPingId", smEventPingId);
            parameter.put("pingContent", pingContent);
            parameter.put("description", description);
            parameter.put("pingType", pingType);
            parameter.put("tagType", tagType);
            parameter.put("modifiedTpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql("updateSmEventPing", parameter, session);
            result = get(smEventPingId, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public void remove(Long smEventPingId, OauthKey key) throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtil.beginTransaction();
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("smEventPingId", smEventPingId);
            parameter.put("modifiedTpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql("deleteSmEventPing", parameter, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
    }
}

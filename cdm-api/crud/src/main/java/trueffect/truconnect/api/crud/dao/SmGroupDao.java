package trueffect.truconnect.api.crud.dao;

import java.util.HashMap;
import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;

/**
 *
 * @author Rambert Rioja
 */
public class SmGroupDao {

    public SmGroup get(Long id) throws Exception {
        return (SmGroup) MyBatisUtil.selectOne("getSmGroup", id);
    }

    protected SmGroup get(Long id, SqlSession session) throws Exception {
        return (SmGroup) MyBatisUtil.selectOne("getSmGroup", id, session);
    }

    public SmGroup save(String groupName, Long measurementId,
            OauthKey key) throws Exception {
        SqlSession session = null;
        SmGroup result = null;
        try {
            session = MyBatisUtil.beginTransaction();
            Long smGroupId = (Long) MyBatisUtil.selectOne("getNextId",
                    "SEQ_SM_GROUP", session);
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("smGroupId", smGroupId);
            parameter.put("measurementId", measurementId);
            parameter.put("groupName", groupName);
            parameter.put("createdTpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql("insertSmGroup", parameter, session);
            result = get(smGroupId, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public void remove(Long smGroupId, OauthKey key) throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtil.beginTransaction();
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("smGroupId", smGroupId);
            parameter.put("createdTpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql("deleteSmGroup", parameter, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
    }
}

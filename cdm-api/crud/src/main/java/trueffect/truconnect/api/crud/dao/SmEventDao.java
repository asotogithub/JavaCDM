package trueffect.truconnect.api.crud.dao;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;

/**
 *
 * @author Abel Soto
 */
public class SmEventDao {

    public SmEvent save(Long smGroupId,
            String eventName,
            String location,
            Long eventType,
            Long smEventType,
            OauthKey key) throws Exception {
        SqlSession session = null;
        SmEvent result = null;
        try {
            session = MyBatisUtil.beginTransaction();

            //for the nextval of cookie domain
            Long seqSmEventId = (Long) MyBatisUtil.selectOne("GenericQueries.getNextId", "SEQ_SM_EVENT", session);
            //Parsing the input parameters of the process
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("p_sm_event_id", seqSmEventId);
            parameter.put("p_sm_group_id", smGroupId);
            parameter.put("p_event_name", eventName);
            parameter.put("p_location", location);
            parameter.put("p_event_type", eventType);
            parameter.put("p_sm_event_type", smEventType);
            parameter.put("p_tpws_key", key.getTpws());
            MyBatisUtil.callProcedurePlSql("SiteMeasurementPkg.insertSMEvent", parameter, session);
            result = get(seqSmEventId, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public SmEvent update(Long smEventId,
            String location,
            Long smEventType,
            OauthKey key) throws Exception {
        SqlSession session = null;
        SmEvent result = null;
        try {
            session = MyBatisUtil.beginTransaction();

            //Parsing the input parameters of the process
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("p_sm_event_id", smEventId);
            parameter.put("p_location", location);
            parameter.put("p_sm_event_type", smEventType);
            parameter.put("p_tpws_key", key.getTpws());
            MyBatisUtil.callProcedurePlSql("SiteMeasurementPkg.upDateSMEvent", parameter, session);
            result = get(smEventId, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public SmEvent delete(Long smEventId, OauthKey key) throws Exception {
        SqlSession session = null;
        SmEvent result = null;
        try {
            session = MyBatisUtil.beginTransaction();

            //Parsing the input parameters of the process
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("p_sm_event_id", smEventId);
            parameter.put("p_tpws_key", key.getTpws());
            MyBatisUtil.callProcedurePlSql("SiteMeasurementPkg.deleteSMEvent", parameter, session);
            result = get(smEventId, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public SmEvent get(Long smEventId) throws Exception {
        return (SmEvent) MyBatisUtil.selectOne("SiteMeasurementPkg.getSmEventId", smEventId);
    }

    protected SmEvent get(Long smEventId, SqlSession session) throws Exception {
        return (SmEvent) MyBatisUtil.selectOne("SiteMeasurementPkg.getSmEventId", smEventId, session);
    }

    public List<SmEvent> getSmEventAll() throws Exception {
        return (List<SmEvent>) MyBatisUtil.selectList("SiteMeasurementPkg.getSmEventAll");
    }

    public List<SmEvent> getSmEventGroupId(Long groupId) throws Exception {
        return (List<SmEvent>) MyBatisUtil.selectList("SiteMeasurementPkg.getSmEventGroupId", groupId);
    }
}

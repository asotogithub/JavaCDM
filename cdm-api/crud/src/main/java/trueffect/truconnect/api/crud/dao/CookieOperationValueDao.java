package trueffect.truconnect.api.crud.dao;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.CookieOperationValue;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;

/**
 *
 * @author Gustavo Claure
 */
public class CookieOperationValueDao {

    public CookieOperationValue get(Long id) throws Exception {
        return (CookieOperationValue) MyBatisUtil.selectOne("CookiePkg.getCookieOperationValue",
                id);
    }

    protected CookieOperationValue get(Long id, SqlSession session) throws Exception {
        return (CookieOperationValue) MyBatisUtil.selectOne("CookiePkg.getCookieOperationValue",
                id, session);
    }

    public List<CookieOperationValue> getCookieOperationValuesByOperation(Long cookieOperationId)
            throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtil.beginTransaction();
            List<CookieOperationValue> list = null;
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("cookieOperationValueId", cookieOperationId);
            MyBatisUtil.callProcedurePlSql("CookiePkg.getCookieOpValBy_CookieOp",
                    parameter, session);
            if (parameter.get("refCursor") != null) {
                list = (List<CookieOperationValue>) parameter.get("refCursor");
            }
            MyBatisUtil.commitTransaction(session);
            return list;
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
    }

    public List<CookieOperationValue> getCookieOperationValuesByOperationCampaign(Long cookieOperationId, Long campaignId)
            throws Exception {
        SqlSession session = null;

        try {
            session = MyBatisUtil.beginTransaction();
            List<CookieOperationValue> list = null;
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("cookieOperationValueId", cookieOperationId);
            parameter.put("campaignId", campaignId);
            MyBatisUtil.callProcedurePlSql("CookiePkg.getCookieOpValBy_CookieOp_Cmpn",
                    parameter, session);
            if (parameter.get("refCursor") != null) {
                list = (List<CookieOperationValue>) parameter.get("refCursor");
            }
            MyBatisUtil.commitTransaction(session);
            return list;
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
    }

    public List<CookieOperationValue> getCookieOperationValuesByCampaign(Long campaignId)
            throws Exception {
        SqlSession session = null;

        try {
            session = MyBatisUtil.beginTransaction();
            List<CookieOperationValue> list = null;
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("campaignId", campaignId);
            MyBatisUtil.callProcedurePlSql("CookiePkg.getCookieOpValBy_Campaign",
                    parameter, session);
            if (parameter.get("refCursor") != null) {
                list = (List<CookieOperationValue>) parameter.get("refCursor");
            }
            MyBatisUtil.commitTransaction(session);
            return list;
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
    }

    public CookieOperationValue save(Long cookieOperationId,
            String cookieValue, OauthKey key)
            throws Exception {
        SqlSession session = null;
        CookieOperationValue result = null;
        try {
            session = MyBatisUtil.beginTransaction();
            Long cookieOperationValueId = (Long) MyBatisUtil.selectOne("GenericQueries.getNextId",
                    "SEQ_GBL_DIM", session);
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("cookieOperationValueId", cookieOperationValueId);
            parameter.put("cookieOperationId", cookieOperationId);
            parameter.put("cookieValue", cookieValue);
            parameter.put("createdTpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql("CookiePkg.insertCookieOperationValue",
                    parameter, session);
            result = get(cookieOperationValueId, session);
            MyBatisUtil.commitTransaction(session);

        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public CookieOperationValue delete(Long cookieOperationValueId, OauthKey key) throws Exception {
        SqlSession session = null;
        CookieOperationValue result = new CookieOperationValue();
        try {
            session = MyBatisUtil.beginTransaction();

            //Parsing the input parameters of the process
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("cookieOperationValueId", cookieOperationValueId);
            parameter.put("createdTpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql("CookiePkg.deleteCookieOperationValue",
                    parameter, session);
            result = get(cookieOperationValueId, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }
}

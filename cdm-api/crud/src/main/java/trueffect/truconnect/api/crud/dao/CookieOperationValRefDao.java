package trueffect.truconnect.api.crud.dao;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.CookieOperationValRef;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;

/**
 *
 * @author Gustavo Claure
 */
public class CookieOperationValRefDao {

    public CookieOperationValRef get(Long id) throws Exception {
        return (CookieOperationValRef) MyBatisUtil.selectOne("CookiePkg.getCookieOperationValRef",
                id);
    }

    protected CookieOperationValRef get(Long id, SqlSession session) throws Exception {
        return (CookieOperationValRef) MyBatisUtil.selectOne("CookiePkg.getCookieOperationValRef",
                id, session);
    }

    public List<CookieOperationValRef> getCookieOperationValRefsByOperationValue(Long cookieOperationValueId)
            throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtil.beginTransaction();
            List<CookieOperationValRef> list = null;
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("cookieOperationValueId", cookieOperationValueId);
            MyBatisUtil.callProcedurePlSql("CookiePkg.getCookieOpValRefBy_OpVal",
                    parameter, session);
            if (parameter.get("refCursor") != null) {
                list = (List<CookieOperationValRef>) parameter.get("refCursor");
            }
            MyBatisUtil.commitTransaction(session);
            return list;
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
    }

    public List<CookieOperationValRef> getCookieOperationValRefsByCampaign(Long campaignId)
            throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtil.beginTransaction();
            List<CookieOperationValRef> list = null;
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("campaignId", campaignId);
            MyBatisUtil.callProcedurePlSql("CookiePkg.getCookieOpValRefBy_Cmpn",
                    parameter, session);
            if (parameter.get("refCursor") != null) {
                list = (List<CookieOperationValRef>) parameter.get("refCursor");
            }
            MyBatisUtil.commitTransaction(session);
            return list;
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
    }

    public List<CookieOperationValRef> getCookieOperationValRefsByOperationCampaign(Long cookieOperationValueId, Long campaignId)
            throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtil.beginTransaction();
            List<CookieOperationValRef> list = null;
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("cookieOperationValueId", cookieOperationValueId);
            parameter.put("campaignId", campaignId);
            MyBatisUtil.callProcedurePlSql("CookiePkg.getCookieOpValRefBy_COV_Cmpn",
                    parameter, session);
            if (parameter.get("refCursor") != null) {
                list = (List<CookieOperationValRef>) parameter.get("refCursor");
            }
            MyBatisUtil.commitTransaction(session);
            return list;
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
    }

    public CookieOperationValRef save(Long cookieOperationValueId,
            Long cookieRefEntityType, Long cookieRefEntityId,
            Long CampaignId, OauthKey key)
            throws Exception {
        SqlSession session = null;
        CookieOperationValRef result = null;
        try {
            session = MyBatisUtil.beginTransaction();
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("cookieOperationValueId", cookieOperationValueId);
            parameter.put("cookieRefEntityType", cookieRefEntityType);
            parameter.put("cookieRefEntityId", cookieRefEntityId);
            parameter.put("campaignId", CampaignId);
            parameter.put("createdTpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql("CookiePkg.insertCookieOperationValRef",
                    parameter, session);
            result = get(cookieOperationValueId, session);
            MyBatisUtil.commitTransaction(session);

        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public CookieOperationValRef remove(Long cookieOperationValueId,
            Long cookieRefEntityType, Long cookieRefEntityId,
            Long campaignId, OauthKey key) throws Exception {
        SqlSession session = null;
        CookieOperationValRef result = new CookieOperationValRef();
        try {
            session = MyBatisUtil.beginTransaction();

            //Parsing the input parameters of the process
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("cookieOperationValueId", cookieOperationValueId);
            parameter.put("cookieRefEntityType", cookieRefEntityType);
            parameter.put("cookieRefEntityId", cookieRefEntityId);
            parameter.put("campaignId", campaignId);
            parameter.put("modifiedTpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql("CookiePkg.deleteCookieOperationValRef",
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

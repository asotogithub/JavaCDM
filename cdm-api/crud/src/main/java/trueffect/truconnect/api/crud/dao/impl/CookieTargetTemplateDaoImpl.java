package trueffect.truconnect.api.crud.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import trueffect.truconnect.api.commons.model.CookieTargetTemplate;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.crud.dao.CookieTargetTemplateDao;
import trueffect.truconnect.api.crud.dao.DataAccessControl;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

/**
 *
 * @author Richard Jaldin
 */
public class CookieTargetTemplateDaoImpl extends AbstractGenericDao implements CookieTargetTemplateDao {

    private static final String STATEMENT_GET_COOKIE_BY_DOMAIN = "CookiePkg.getCookieByDomain";
    private static final String STATEMENT_GET_COOKIE_TARGET_TEMPLATE = "getCookieTT";
    private static final String STATEMENT_INSERT_COOKIE_TARGET_TEMPLATE = "insertCookieTT";
    private static final String STATEMENT_UPDATE_COOKIE_TARGET_TEMPLATE = "updateCookieTT";
    private static final String STATEMENT_DELETE_COOKIE_TARGET_TEMPLATE = "deleteCookieTT";
    private static final String STATEMENT_EXIST_COOKIE_TARGET_TEMPLATE = "CookiePkg.existCookieTT";
    
    public CookieTargetTemplateDaoImpl(PersistenceContext context) {
         super(context);
    }


    @Override
    public CookieTargetTemplate get(Long id, SqlSession session) throws Exception {
        return getPersistenceContext().selectOne(STATEMENT_GET_COOKIE_TARGET_TEMPLATE, id, session, CookieTargetTemplate.class);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<CookieTargetTemplate> getByCookieDomainId(Long cookieDomainId, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("cookieDomainId", cookieDomainId);

        List<CookieTargetTemplate> result = (List<CookieTargetTemplate>) getPersistenceContext().selectList(
                STATEMENT_GET_COOKIE_BY_DOMAIN, parameter, session);
        return result;
    }

    @Override
    public CookieTargetTemplate save(CookieTargetTemplate cookie, SqlSession session) throws Exception {
        CookieTargetTemplate result = null;
        Long cookieTTId = getNextId(session);
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("cookieTargetTemplateId", cookieTTId);
        parameter.put("cookieName", cookie.getCookieName());
        parameter.put("cookieDomainId", cookie.getCookieDomainId());
        parameter.put("cookieContentType", cookie.getCookieContentType());
        parameter.put("contentPossibleValues", cookie.getContentPossibleValues());
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_INSERT_COOKIE_TARGET_TEMPLATE, parameter, session);
        result = get(cookieTTId, session);
        return result;
    }

    @Override
    public CookieTargetTemplate update(CookieTargetTemplate cookie, SqlSession session) throws Exception {
        CookieTargetTemplate result = null;
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("cookieTargetTemplateId", cookie.getCookieTargetTemplateId());
        parameter.put("cookieName", cookie.getCookieName());
        parameter.put("cookieDomainId", cookie.getCookieDomainId());
        parameter.put("cookieContentType", cookie.getCookieContentType());
        parameter.put("contentPossibleValues", cookie.getContentPossibleValues());

        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_UPDATE_COOKIE_TARGET_TEMPLATE, parameter, session);
        result = get(cookie.getCookieTargetTemplateId(), session);
        return result;
    }

    @Override
    public void remove(Long id, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("cookieTargetTemplateId", id);
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_DELETE_COOKIE_TARGET_TEMPLATE, parameter, session);
    }

    @Override
    public boolean exists(Long cookieDomainId, String cookieName, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("cookieName", cookieName);
        parameter.put("cookieDomainId", cookieDomainId);
        return getPersistenceContext()
                .selectOne(STATEMENT_EXIST_COOKIE_TARGET_TEMPLATE, parameter, session).equals("true");
    }
}

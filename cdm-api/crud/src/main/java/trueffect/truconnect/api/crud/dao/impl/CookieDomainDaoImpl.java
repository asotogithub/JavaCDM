package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.dto.CookieDomainDTO;
import trueffect.truconnect.api.crud.dao.CookieDomainDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 * @author Abel Soto
 */
public class CookieDomainDaoImpl extends AbstractGenericDao implements CookieDomainDao {

     public static final String CHECK_IF_COOKIE_DOMAIN_EXISTS = "CookieDomain.existsCookieDomain";
    
    public CookieDomainDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public void create(CookieDomain domain, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", domain.getId());
        parameter.put("agencyId", domain.getAgencyId());
        parameter.put("domain", domain.getDomain());
        parameter.put("tpwsKey", domain.getCreatedTpwsKey());
        getPersistenceContext().execute("CookieDomain.insertCookieDomain", parameter, session);
    }

    @Override
    public void delete(CookieDomain domain, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", domain.getId());
        parameter.put("tpwsKey", domain.getModifiedTpwsKey());
        getPersistenceContext().execute("CookieDomain.deleteCookieDomain", parameter, session);
    }

    public CookieDomain get(Long id, SqlSession session) throws Exception {
        return (CookieDomain) getPersistenceContext()
                .selectOne("CookieDomain.getCookieDomain", id, session);
    }

    @Override
    public boolean existCookieDomain(CookieDomain cookieDomain, SqlSession session)
            throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("domain", cookieDomain.getDomain());
        parameter.put("agencyId", cookieDomain.getAgencyId());
        return getPersistenceContext()
                .selectOne(CHECK_IF_COOKIE_DOMAIN_EXISTS, parameter, session).equals("true");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CookieDomainDTO> getDomains(String id, SqlSession session) throws Exception {
        return (List<CookieDomainDTO>) getPersistenceContext()
                            .selectList("CookieDomain.getUserDomains", id, session);
    }
}

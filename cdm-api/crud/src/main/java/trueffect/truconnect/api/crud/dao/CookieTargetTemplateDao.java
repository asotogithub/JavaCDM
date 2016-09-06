package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.CookieTargetTemplate;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 * @author Richard Jaldin
 */
public interface CookieTargetTemplateDao extends GenericDao  {

    CookieTargetTemplate get(Long id, SqlSession session) throws Exception;
    
    List<CookieTargetTemplate> getByCookieDomainId(Long cookieDomainId, SqlSession session) throws Exception;
    
    CookieTargetTemplate save(CookieTargetTemplate cookie, SqlSession session) throws Exception;

    CookieTargetTemplate update(CookieTargetTemplate cookie, SqlSession session) throws Exception;

    void remove(Long id, SqlSession session) throws Exception;
    
    boolean exists(Long cookieDomainId, String cookieName, SqlSession session) throws Exception;
}

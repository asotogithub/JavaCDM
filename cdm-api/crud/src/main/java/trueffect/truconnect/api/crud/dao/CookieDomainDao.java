package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.dto.CookieDomainDTO;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 * @author Abel Soto
 */
public interface CookieDomainDao extends GenericDao {

    /**
     * Returns the CookieDomain based on the identifier
     *
     * @param id ID of the CookieDomain to return
     * @return the CookieDomain of the ID identifier
     */
    CookieDomain get(Long id, SqlSession session) throws Exception;

    /**
     * Saves a CookieDomain
     *
     * Domain name should follow the patterns "XXXX.DDDDDD.COM",
     * "xxx.XXX.ddd.DDD.com"
     *
     * @param domain Domain to be saved.
     */
    void create(CookieDomain domain, SqlSession session) throws Exception;

    /**
     * Removes an CookieDomain
     *
     * @param domain CookieDomain to be removed
     * @return CookieDomain that has been deleted
     */
    void delete(CookieDomain domain, SqlSession session) throws Exception;

    boolean existCookieDomain(CookieDomain cookieDomain, SqlSession session) throws Exception;

    List<CookieDomainDTO> getDomains(String userId, SqlSession session) throws Exception;
}

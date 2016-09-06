package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public interface PackageDaoBase extends GenericDao {

    /**
     * Gets a Package given an ID
     * @param id The Package ID
     * @param session The SqlSession where to execute the query
     * @return The Package that matches the ID
     */
    Package get(Long id, SqlSession session);

    /**
     * Creates a new Package
     * @param pkg The Package to create
     * @param session The SqlSession where to execute the persistence query
     * @return The newly created Package
     */
    Package create(Package pkg, SqlSession session);

    /**
     * Updates a Package
     * @param pkg The Package to update
     * @param session
     */
    void update(Package pkg, SqlSession session);

    /**
     * Deletes logically a Package
     * @param packageId The Package ID to delete
     * @param key The Oauth key to track who is executing this operation
     * @param session The SqlSession where to execute the persistence query
     */
    void delete(Long packageId, OauthKey key, SqlSession session);

    /**
     * Deletes logically a Package
     * @param ids The Package ID to delete
     * @param tpwsKey The Oauth key to track who is executing this operation
     * @param session The SqlSession where to execute the persistence query
     * @return
     */
    Integer delete(List<Long> ids, String tpwsKey, SqlSession session);

}

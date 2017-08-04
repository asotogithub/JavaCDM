package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 * DAO for checking the status of the database connection.
 */
public interface StatusDao extends GenericDao {

    /**
     * Get the current schema of the user connected to the database.
     *
     * @param session
     * @return
     */
    String currentSchema(SqlSession session);

    /**
     * Check that the session can connect to the underlying database.
     *
     * @param session
     * @return
     */
    Long checkConnection(SqlSession session);
}

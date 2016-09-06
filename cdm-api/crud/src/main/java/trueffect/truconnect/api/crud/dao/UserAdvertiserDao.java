package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

/**
 *
 * @author abel.soto
 */
public interface UserAdvertiserDao extends GenericDao {

    void addUserAdvReference(String userId, Long advertiserId, SqlSession session);

    void cleanUserAdvReference(String userId, SqlSession session);
}

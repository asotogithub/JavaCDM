package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.SqlSession;

import trueffect.truconnect.api.commons.model.UserDomain;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

/**
 * Created by richard.jaldin on 6/19/2015.
 */
public interface UserDomainDao extends GenericDao {

    /**
     * Removes the Security override restrictions
     * @param userId The {@code User}'s Id from whom remove the restrictions
     * @param session The current persistence session
     */
    void removeUserDomains(String userId, SqlSession session);

    /**
     * Adds a Security override restriction
     * @param userDomain The restriction
     * @param session The current persistence session
     * @return The saved restriction
     */
    UserDomain createUserDomain(UserDomain userDomain, SqlSession session);
}

package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;

/**
 *
 * @author marleny.patsi
 */
public class AccessControlMock implements AccessControl {

    /**
     * Check if the given {@code userId} can access to the given context of {@code ids} using a
     * specific {@code statement}
     *
     * @param statement The Access Statement to use which references to the query to use for this
     *                  check
     * @param ids       The context of IDs to check
     * @param userId    The User ID which we want to check
     * @param session   The SqlSession where this query is going to be executed
     * @return true if the {@code userId} has access to <b>all</b> of the {@code ids} provided.
     * false otherwise
     */
    @Override
    public boolean isUserValidFor(AccessStatement statement, Collection<Long> ids, String userId,
                                  SqlSession session) {
        return true;
    }

    @Override
    public boolean isAdmin(String userId, SqlSession session) {
        return true;
    }

}

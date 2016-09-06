package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.AgencyUser;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RolePermission;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.UserView;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Data access object for users.
 */
public interface UserDao extends GenericDao {

    User get(String userIdTobeRetreived, String sessionUserId) throws Exception;

    boolean isValidUserToBeSaved(String userIdTobeRetreived, Long agencyId, String sessionUserId) throws Exception;

    Long getAgencyIdByUser(String userId, SqlSession session) throws Exception;

    /**
     * Gets the User information given a User {@code id}
     * @param id The id of the user to be retrieved
     * @param userId The {@code userId} that performs the request to execute this method
     * @param session
     * @return A {@code User} that contains all the information for {@code id}
     * @throws Exception when any exception occurs when executing the query
     * @deprecated Use {@link trueffect.truconnect.api.crud.dao.UserDao#get(String, org.apache.ibatis.session.SqlSession)} method instead to avoid using checked exceptions
     */
    @Deprecated
    User get(String id, String userId, SqlSession session) throws Exception;
    
    User get(String id, SqlSession session);
    
    User save(User user, OauthKey key) throws Exception;

    void createUserRole(Long userId, List<String> roles, SqlSession session) throws Exception;

    Long getRoleId(String role, SqlSession session) throws Exception;

    User update(User user, OauthKey key) throws Exception;

    User update(User user, OauthKey key, SqlSession session) throws Exception;
    
    void setUserLimits(User user, OauthKey key, SqlSession session);

    void remove(String id, OauthKey key) throws Exception;

    @SuppressWarnings("unchecked")
    AgencyUser getByTPWSKey(String tpws, SqlSession session);

    /**
     * Returns all the Permissions assigned to the User's assigned Roles
     * @param userId The User Id to obtain the Permissions from
     * @param session The MyBatis {@code SqlSession} to use
     * @return The {@code List} of {@code RolePermission} for the provided {@code userId}
     * @throws Exception When an Exception happens
     */
    List<String> getPermissionsByUser(String userId, SqlSession session) throws Exception;
    
    List<UserView> getUserView(Long agencyId, SqlSession session);
    
    List<Integer> getTraffickingContacts(List<Integer> agencyContacts, SqlSession session);
}

package trueffect.truconnect.api.oauth;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.oauth.model.User;
import trueffect.truconnect.api.oauth.mybatis.MyBatisConnector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Rambert Rioja
 */
public class UserProvider {
    private static final Logger logger = LoggerFactory.getLogger(UserProvider.class);
    private static final MyBatisConnector myBatisConnector = new MyBatisConnector();
    public static final String STATEMENT_GET_USER = "getUser";
    public static final String STATEMENT_GET_ROLES = "getRoles";

    /**
     * Gets a {@code User} given a {@code userId}
     * @param userId The User Id to look for
     * @return a {@code User} given a {@code userId}
     */
    public static User getUser(String userId) {
        SqlSession session = myBatisConnector.beginTransaction();
        User user = null;
        try {
            user = (User) myBatisConnector.selectOne(STATEMENT_GET_USER, userId, session);
            if (user != null) {
                List<String> roles = myBatisConnector.selectList(STATEMENT_GET_ROLES, userId, session);
                user.setRoles(roles);
            }
        } catch (Exception e) {
            logger.warn(String.format("Unexpected Exception while getting User '%s'", userId), e);
        }
        finally {
            myBatisConnector.endTransaction(session);
        }
        return user;
    }

    public static String generateTPWSKey(String userId, String userHostAddress, String userAgent) {
        try {
            String tpws = UUID.randomUUID().toString();
            HashMap<String, Object> params = new HashMap<>();
            User user = UserProvider.getUser(userId);
            params.put("tpwskey", tpws);
            params.put("agentType", "TPEI");
            params.put("userOrgType", "AGENCY");
            params.put("contactId", user.getContactId());
            params.put("registeredUserId", userId);
            params.put("userHostAddress", userHostAddress);
            params.put("userAgent", userAgent);
            
            logger.debug("Generating TpwsKey with {}", params);
            myBatisConnector.callProcedure("openTPWSKey", params);
            return tpws;
        } catch (Exception ex) {
            logger.error(String.format("Error generating the Tpws Key for userId=%s, userHostAddress=%s, userAgent=%s",
                    userId,
                    userHostAddress,
                    userAgent),
                    ex);
            return "000000";
        }

    }
}

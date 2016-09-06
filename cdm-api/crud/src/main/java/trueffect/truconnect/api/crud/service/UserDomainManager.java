package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.UserDomain;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.UserDomainDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard.jaldin on 6/19/2015.
 */
public class UserDomainManager extends AbstractGenericManager {

    private UserDomainDao userDomainDao;

    public UserDomainManager(UserDomainDao userDomainDao,
                             AccessControl accessControl) {
        super(accessControl);
        this.userDomainDao = userDomainDao;
    }

    /**
     * Updates the Domains Security Override based on the domains and userId
     *
     * @param userId  The User's Id for who the admin will change its security permissions
     * @param domains The RecordSet of domains to be restricted
     * @param key     The Oauth key to track who is executing this operation (AKA the admin)
     * @return The RecordSet of the Domains restricted
     */
    public Either<Error, RecordSet<UserDomain>> updateUserDomains(String userId, RecordSet<UserDomain> domains,
                                                   OauthKey key) {
        if (userId == null) {
            throw new IllegalArgumentException("User's id cannot be null");
        }
        if (domains == null) {
            throw new IllegalArgumentException("Domains cannot be null");
        }

        SqlSession session = userDomainDao.openSession();
        try {
            // Check access control
            List<Long> ids = new ArrayList<>();
            if (domains.getRecords() != null && domains.getRecords().size() > 0) {
                for (UserDomain userDomain : domains.getRecords()) {
                    ids.add(userDomain.getDomainId());
                    // check if userId matches with domain.userId
                    if (StringUtils.isNotEmpty(userId) && StringUtils
                            .isNotEmpty(userDomain.getUserId()) && !userId
                            .equals(userDomain.getUserId())) {
                        return Either.error(new Error(ResourceBundleUtil.getString("BusinessCode.ENTITY_ID_MISMATCH"), BusinessCode.ENTITY_ID_MISMATCH));
                    }
                }
            }
            isAdminUser(key.getUserId(), session);
            if (!userValidFor(AccessStatement.COOKIE_DOMAIN, ids, key.getUserId(), session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.ILLEGAL_USER_CONTEXT"), SecurityCode.ILLEGAL_USER_CONTEXT));
            }

            userDomainDao.removeUserDomains(userId, session);
            if (domains.getRecords() != null && domains.getRecords().size() > 0) {
                for (UserDomain userDomain : domains.getRecords()) {
                    userDomainDao.createUserDomain(userDomain, session);
                }
            }
            userDomainDao.commit(session);
        } catch (Exception e) {
            userDomainDao.rollback(session);
            return Either.error(new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR));
        } finally {
            userDomainDao.close(session);
        }
        return Either.success(domains);
    }

}

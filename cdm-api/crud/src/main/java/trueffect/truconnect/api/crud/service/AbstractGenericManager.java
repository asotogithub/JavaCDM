package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Super class that defines commons methods for all Manager classes
 * Created by marcelo.heredia on 6/10/2015.
 * @author Marcelo Heredia
 */
public abstract class AbstractGenericManager {
    protected AccessControl accessControl;
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    protected AbstractGenericManager(AccessControl accessControl) {
        if(accessControl == null){
            throw new IllegalArgumentException("AccessControl cannot be null");
        }
        this.accessControl = accessControl;
    }

    /**
     * Checks if a user has access to a given context ID
     *
     * @param statement The key of the Mapped SQL statement that contains the logic to validate the User's access
     * @param id             A single ID
     * @param userId         The User id to check
     * @param session        The {@code SqlSession} session
     * @return true, when the {@code userId} has access to the given {@code id}
     */
    public boolean userValidFor(AccessStatement statement, long id, String userId,
                                SqlSession session) {
        return userValidFor(statement, Collections.singletonList(id), userId, session);
    }

    /**
     * Checks if a user has access to a given context of IDs.
     *
     * Note. This method is similar to {@link trueffect.truconnect.api.crud.service.AbstractGenericManager#checkUserValidFor(trueffect.truconnect.api.crud.dao.AccessStatement, java.util.List, String, org.apache.ibatis.session.SqlSession)}
     * but this one doesn't user internally any @Deprecated method. Additionally, this doesn't throw
     * any unchecked exception purposely. The invoker needs to process the boolean return type
     * to determine what to do with that result.
     * @param statement The key of the Mapped SQL statement that contains the logic to validate the User's access
     * @param ids            A list of IDs
     * @param userId         The User id to check
     * @param session        The {@code SqlSession} session
     */
    public boolean userValidFor(AccessStatement statement, Collection<Long> ids, String userId,
                                SqlSession session) {
        if(ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "ids"));
        }
        checkCommonParams(statement, ids, userId, session);
        if(statement.equals(AccessStatement.IS_ADMIN)) {
            return accessControl.isAdmin(userId, session);
        } else {
            return accessControl.isUserValidFor(statement, ids, userId, session);
        }
    }

    private void checkCommonParams(AccessStatement statement, Collection<Long> ids, String userId, SqlSession session) {
        if(statement == null){
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "statement"));
        }
        if(userId == null){
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "userId"));
        }
        if(session == null){
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "session"));
        }
    }
    boolean isAdminUser(String userId, SqlSession session) {
        return accessControl.isAdmin(userId, session);
    }

    protected Either<Error, HashMap<String, Long>> validatePaginator(HashMap<String, Long> paginator) {
        paginator.put("startIndex", paginator.get("startIndex") != null ? paginator.get("startIndex") : SearchCriteria.SEARCH_CRITERIA_START_INDEX);
        paginator.put("pageSize", paginator.get("pageSize") != null ? paginator.get("pageSize") : SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE);
        if (paginator.get("startIndex").intValue() < SearchCriteria.SEARCH_CRITERIA_START_INDEX) {
            return Either.error(new Error(ResourceBundleUtil.getString("pagination.error.illegalStartIndex",
                    paginator.get("startIndex"), SearchCriteria.SEARCH_CRITERIA_START_INDEX.toString()),
                    ValidationCode.INVALID));
        }
        if (paginator.get("pageSize").intValue() > SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE) {
            return Either.error(new Error(ResourceBundleUtil.getString("pagination.error.tooLargePageSize",
                    SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE.toString()),
                    ValidationCode.INVALID));
        }
        return Either.success(paginator);
    }
}

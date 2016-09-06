package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public class AccessControlImpl implements AccessControl {

    private static final String STATEMENT_IS_ADMIN_USER = "DataAccessControlPkg.isAdminUser";

    private PersistenceContext persistenceContext;

    public AccessControlImpl(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    @Override
    public boolean isAdmin(String userId, SqlSession session) {
        String roleAdmin = persistenceContext.executeSelectOne(
            STATEMENT_IS_ADMIN_USER, userId, session, String.class);
        return roleAdmin != null && roleAdmin.equals("Y");
    }

    private static HashMap<String, Object> parameters(Collection<Long> ids, String userId) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ids", ids);
        parameters.put("userId", userId);
        return parameters;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isUserValidFor(AccessStatement statement, Collection<Long> ids, String userId, SqlSession session) {
        if(statement == null){
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "statement"));
        }
        if(ids == null){
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "ids"));
        }
        
        List<Long> idsList = new ArrayList<>(ids);
        int fromIndex = 0; 
        int iterations = (int) Math.ceil((double)ids.size()/(double)Constants.MAX_NUMBER_VALUES_IN_CLAUSE);
        boolean result = false;
        for (int i = 0; i < iterations; i++) {
            int toIndex = (fromIndex + Constants.MAX_NUMBER_VALUES_IN_CLAUSE) > ids.size() ? 
                            ids.size(): (fromIndex + Constants.MAX_NUMBER_VALUES_IN_CLAUSE);
            // Possible results are 'true' or 'false'
            String queryResult = persistenceContext.executeSelectOne(statement.getKey(),
                    parameters(idsList.subList(fromIndex, toIndex), userId), session, String.class);
            result = Boolean.valueOf(queryResult);
            if (!result) {
                return result;
            }
            fromIndex += Constants.MAX_NUMBER_VALUES_IN_CLAUSE;
        }
        return result;
    }
}

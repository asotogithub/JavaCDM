package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.UserDomain;
import trueffect.truconnect.api.crud.dao.UserDomainDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;

/**
 * Created by richard.jaldin on 6/19/2015.
 */
public class UserDomainDaoImpl extends AbstractGenericDao implements UserDomainDao {

    public UserDomainDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public void removeUserDomains(String userId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("userId", userId);
        getPersistenceContext().execute("UserDomain.removeUserDomains", parameter, session);
    }

    @Override
    public UserDomain createUserDomain(UserDomain userDomain, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("userId", userDomain.getUserId());
        parameter.put("domainId", String.valueOf(userDomain.getDomainId()));
        getPersistenceContext().execute("UserDomain.createUserDomain", parameter, session);
        return userDomain;
    }
}

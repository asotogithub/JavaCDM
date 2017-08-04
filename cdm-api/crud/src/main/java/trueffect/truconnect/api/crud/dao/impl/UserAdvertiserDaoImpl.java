package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.crud.dao.UserAdvertiserDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;

/**
 *
 * @author abel.soto
 */
public class UserAdvertiserDaoImpl extends AbstractGenericDao implements UserAdvertiserDao{

    public UserAdvertiserDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }
    
    @Override
     public void addUserAdvReference(String userId, Long advertiserId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("userId", userId);
        parameter.put("advertiserId", advertiserId.toString());
        getPersistenceContext().execute("UserAdvertiser.addUserAdvertiserRef", parameter, session);

    }

    @Override
    public void cleanUserAdvReference(String userId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("userId", userId);
        getPersistenceContext().execute("UserAdvertiser.cleanUserAdvertiserRef", parameter, session);
    }
}

package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.crud.dao.StatusDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

public class StatusDaoImpl extends AbstractGenericDao implements StatusDao {

    public static final String STATUS_CHECK_CONNECTION = "Status.checkConnection";
    public static final String STATUS_CURRENT_SCHEMA = "Status.currentSchema";

    public StatusDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public String currentSchema(SqlSession session) {
        return getPersistenceContext().selectSingle(STATUS_CURRENT_SCHEMA, null, session, String.class);
    }

    @Override
    public Long checkConnection(SqlSession session) {
        return getPersistenceContext().selectSingle(STATUS_CHECK_CONNECTION, null, session, Long.class);
    }
}

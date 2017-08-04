package trueffect.truconnect.api.crud.mybatis;

import trueffect.truconnect.api.crud.dao.AccessControl;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;


/**
 * Super class to capture common properties and methods amongst all DAOs
 * @author Abel Soto, Marcelo Heredia, Marleny Patsi
 */
public abstract class AbstractGenericDao {

    private PersistenceContext persistenceContext;
    private AccessControl accessControl;

    /**
     * Creates a new {@code GenericDaoImpl} with a specific {@code PersistentContext}
     * @param persistenceContext The specific {@code PersistentContext} for this DAO
     */
    protected AbstractGenericDao(PersistenceContext persistenceContext) {
        setPersistenceContext(persistenceContext);
    }

    /**
     * Creates a new {@code GenericDaoImpl} with a specific {@code PersistentContext} and a given {@code AccessControl}
     * @param persistenceContext The specific {@code PersistentContext} for this DAO
     * @param accessControl The specific {@code AccessControl} for this DAO
     * @deprecated This constructor should not be used from now on, as the {@code AccessControl} should be managed
     * at a Business layer level.
     */
    @Deprecated
    protected AbstractGenericDao(PersistenceContext persistenceContext, AccessControl accessControl) {
        setPersistenceContext(persistenceContext);
        setAccessControl(accessControl);
    }

    protected void setPersistenceContext(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    protected PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    @Deprecated
    protected void setAccessControl(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    @Deprecated
    public AccessControl getAccessControl() {
        return accessControl;
    }

    public SqlSession openSession(){
        return getPersistenceContext().openSession();
    }

    public SqlSession openSession(ExecutorType executorType){
        return getPersistenceContext().openSession(executorType);
    }

    public void commit(SqlSession session){
        getPersistenceContext().commit(session);
    }

    public void rollback(SqlSession session){
        getPersistenceContext().rollback(session);
    }

    public void close(SqlSession session){
        getPersistenceContext().close(session);
    }

    public Long getNextId(SqlSession session) {
        return getPersistenceContext().executeSelectOne("GenericQueries.getNextId" , "SEQ_GBL_DIM", session, Long.class);
    }

    public Long getNextId(String sequenceName, SqlSession session) {
        if (StringUtils.isBlank(sequenceName)) {
            sequenceName = "SEQ_GBL_DIM";
        }
        return getPersistenceContext().selectSingle("GenericQueries.getNextId", sequenceName, session, Long.class);
    }
}

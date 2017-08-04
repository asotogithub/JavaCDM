package trueffect.truconnect.api.crud.mybatis;

import trueffect.truconnect.api.crud.Constants;
import trueffect.truconnect.api.resources.ResourceUtil;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.io.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PersistenceContextMyBatis implements PersistenceContext {
    private final Logger logger = LoggerFactory.getLogger(PersistenceContextMyBatis.class);

    private String resource;
    private static Map<String,SqlSessionFactory> sqlSessionFactory = new HashMap<>();

    public PersistenceContextMyBatis() {
    	this(Constants.CM_MYBATIS_PATH);
    }

    public PersistenceContextMyBatis(String resource) {
        this.resource = resource;
        synchronized (sqlSessionFactory) {
            if (!sqlSessionFactory.containsKey(resource)) {
                initSessionFactory(resource);
            }
        }
    }

    private void initSessionFactory(String resource) {
        try {
            logger.info("Starting new session factory for resource '{}'", resource);
            Reader reader = Resources.getResourceAsReader(this.resource);
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            sqlSessionFactory.put(resource, sqlSessionFactoryBuilder.build(reader, connectionProperties()));
            logger.info("SqlSessionFactory created is {}", sqlSessionFactory);
        } catch (Exception e) {
            logger.error("Unable to create SQL session", e);
        }
    }

    protected Properties connectionProperties() {
        Properties props = new Properties();
        props.setProperty("url", ResourceUtil.get(Constants.CM_DATA_STORE_URL_RESOURCE));
        props.setProperty("username", ResourceUtil.get(Constants.CM_DATA_STORE_USERNAME_RESOURCE));
        props.setProperty("password", ResourceUtil.get(Constants.CM_DATA_STORE_PASSWORD_RESOURCE));
        props.setProperty("driver", ResourceUtil.get(Constants.CM_DATA_STORE_DRIVER_RESOURCE));
        return props;
    }

    @Override
    public List<?> selectList(String xmlMethod) throws Exception {
        List<?> result = null;
        SqlSession session = this.beginTransaction();
        result = session.selectList(xmlMethod);
        this.endTransaction(session);
        return result;
    }

    @Override
    public List<?> selectList(String xmlMethod, Object parameter) throws Exception {
        List<?> result = null;
        SqlSession session = this.beginTransaction();
        result = session.selectList(xmlMethod, parameter);
        this.endTransaction(session);
        return result;
    }

    @Override
    public Object selectOne(String xmlMethod) throws Exception {
        Object result = null;
        SqlSession session = this.beginTransaction();
        result = session.selectOne(xmlMethod);
        this.endTransaction(session);
        return result;
    }

    @Override
    public Object selectOne(String xmlMethod, Object parameter) throws Exception {
        Object result = null;
        SqlSession session = this.beginTransaction();
        result = session.selectOne(xmlMethod, parameter);
        this.endTransaction(session);
        return result;
    }

    @Override
    public void execute(String statement, Object parameter, SqlSession session) {
        session.selectOne(statement, parameter);
    }

    @Override
    public int delete(String statement, Object parameter, SqlSession session) { 
        return session.delete(statement, parameter);
    }

    @Override
    public <T> T executeSelectOne(String statement, Object parameter, SqlSession session, Class<T> type) {
        return type.cast(session.selectOne(statement, parameter));
    }

    @Override
    public List<?> selectList(String xmlMethod, SqlSession session) throws Exception {
        return session.selectList(xmlMethod);
    }

    @Override
    public List<?> selectList(String xmlMethod, Object parameter, SqlSession session) throws Exception {
        return session.selectList(xmlMethod, parameter);
    }
    
    public <T extends Serializable> List<?> selectList(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) throws Exception {
        return session.selectList(xmlMethod, parameter, limits);
    }

    public int update(String xmlMethod, Object parameter, SqlSession session) {
        return session.update(xmlMethod, parameter);
    }

    @Override
    public <T> List<T> selectMultiple(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) {
        return session.selectList(xmlMethod, parameter, limits);
    }

    @Override
    public <T> List<T> selectMultiple(String statement, Object parameter, SqlSession session) {
        return session.selectList(statement, parameter);
    }

    @Override
    public Object selectOne(String xmlMethod, SqlSession session) throws Exception {
        return session.selectOne(xmlMethod);
    }

    @Override
    public Object selectOne(String xmlMethod, Object parameter, SqlSession session) throws Exception {
        return session.selectOne(xmlMethod, parameter);
    }

    @Override
    public <T> T selectOne(String xmlMethod, Object parameter, SqlSession session, Class<T> type) throws Exception {
        return type.cast(session.selectOne(xmlMethod, parameter));
    }

    @Override
    public <T> T selectSingle(String statement, Object parameter, SqlSession session, Class<T> type) {
        return type.cast(session.selectOne(statement, parameter));
    }

    @Override
    public void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception {
        session.selectOne(xmlMethod, parameterMap);
    }

    @Override
    public void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameter) throws Exception {
        SqlSession session = this.beginTransaction();
        session.selectOne(xmlMethod, parameter);
        this.commitTransaction(session);
    }

    @Override
    public SqlSession openSession() {
        return sqlSessionFactory.get(this.resource).openSession();
    }

    @Override
    public SqlSession openSession(ExecutorType executorType) {
        return sqlSessionFactory.get(this.resource).openSession(executorType);
    }

    @Override
    public SqlSession beginTransaction() throws Exception {
        return sqlSessionFactory.get(this.resource).openSession();
    }

    @Override
    public void commitTransaction(SqlSession session) throws Exception {
        session.commit(true);
        session.close();
    }

    @Override
    public void rollbackTransaction(SqlSession session) throws Exception {
        session.rollback(true);
        session.close();
    }

    @Override
    public void commit(SqlSession session) {
        session.commit(true);
    }

    @Override
    public void rollback(SqlSession session) {
        session.rollback(true);
    }
    @Override
    public void close(SqlSession session) {
        if(session != null) {
            session.close();
        }
    }

    @Override
    public void endTransaction(SqlSession session) throws Exception {
        if(session != null) {
            session.close();
        }
    }

    @Override
    public void select(String statement, Object parameter, SqlSession session, ResultHandler handler) {
        session.select(statement, parameter, handler);
    }
}

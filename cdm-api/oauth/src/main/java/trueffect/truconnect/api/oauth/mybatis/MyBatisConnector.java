package trueffect.truconnect.api.oauth.mybatis;

import trueffect.truconnect.api.resources.ResourceUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Rambert Rioja
 */
public class MyBatisConnector {

    private String resource;
    private static SqlSessionFactory sqlSessionFactory;
    private static final Log LOGGER = LogFactory.getLog(MyBatisConnector.class);

    public MyBatisConnector() {
        this.resource = "mybatis-conf.xml";
        if(sqlSessionFactory == null) {
            initSessionFactory();
        }
    }

    private void initSessionFactory(){
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(inputStream, connectionProperties());
        } catch (Exception e) {
            LOGGER.error("Unable to create SQL session", e);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Could not close stream", e);
                }
            }
        }
    }

    private Properties connectionProperties() {
        Properties props = new Properties();
        props.setProperty("url", ResourceUtil.get("dataStore.url"));
        props.setProperty("username", ResourceUtil.get("dataStore.username"));
        props.setProperty("password", ResourceUtil.get("dataStore.password"));
        props.setProperty("driver", ResourceUtil.get("dataStore.driver"));
        return props;
    }

    public Object callProcedure(String xmlMethod, HashMap<String, Object> parameterMap) throws Exception {
        SqlSession session = null;
        try {
            Object result;
            session = this.beginTransaction();
            result = callProcedure(xmlMethod, parameterMap, session);
            commitTransaction(session);
            return result;
        } catch (Exception e) {
            rollbackTransaction(session);
            throw e;
        }
    }

    public Object selectOne(String xmlMethod, Object parameter, SqlSession session) {
        return session.selectOne(xmlMethod, parameter);
    }

    public <T> List<T> selectList(String xmlMethod, Object parameter, SqlSession session) {
        return session.<T>selectList(xmlMethod, parameter);
    }

    public SqlSession beginTransaction() {
        return sqlSessionFactory.openSession();
    }

    public Object callProcedure(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception {
        return session.selectOne(xmlMethod, parameterMap);
    }

    public void commitTransaction(SqlSession session) throws Exception {
        session.commit(true);
        session.close();
    }

    public void rollbackTransaction(SqlSession session) throws Exception {
        session.rollback();
        session.close();
    }

    public void endTransaction(SqlSession session) {
        session.close();
    }
}

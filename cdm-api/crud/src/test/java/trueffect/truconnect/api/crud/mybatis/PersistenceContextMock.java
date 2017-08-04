package trueffect.truconnect.api.crud.mybatis;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public class PersistenceContextMock implements PersistenceContext {

    private static Long globalSequence = 0L;

    private static final PersistenceContextMyBatis MY_BATIS_BUSSINESS_LOGIC = mock(PersistenceContextMyBatis.class);

    @Override
    public List<?> selectList(String xmlMethod) throws Exception {
        List<?> list = new ArrayList<Object>();
        doReturn(list).when(MY_BATIS_BUSSINESS_LOGIC).selectList(xmlMethod);
        return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod);
    }

    @Override
    public List<?> selectList(String xmlMethod, Object parameter) throws Exception {
        List<?> list = new ArrayList<Object>();
        doReturn(list).when(MY_BATIS_BUSSINESS_LOGIC).selectList(xmlMethod, parameter);
        return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod, parameter);
    }

    @Override
    public Object selectOne(String xmlMethod) throws Exception {
        List<?> list = new ArrayList<Object>();
        doReturn(list).when(MY_BATIS_BUSSINESS_LOGIC).selectOne(xmlMethod);
        return MY_BATIS_BUSSINESS_LOGIC.selectOne(xmlMethod);
    }

    @Override
    public Object selectOne(String xmlMethod, Object parameter) throws Exception {
        List<?> list = new ArrayList<Object>();
        doReturn(list).when(MY_BATIS_BUSSINESS_LOGIC).selectOne(xmlMethod, parameter);
        return MY_BATIS_BUSSINESS_LOGIC.selectOne(xmlMethod, parameter);
    }

    @Override
    public void execute(String statement, Object parameter, SqlSession session) {
        doNothing().when(MY_BATIS_BUSSINESS_LOGIC).execute(statement, parameter, session);
        MY_BATIS_BUSSINESS_LOGIC.execute(statement, parameter, session);
    }

    @Override
    public int delete(String statement, Object parameter, SqlSession session) {
        doReturn(1).when(MY_BATIS_BUSSINESS_LOGIC).delete(statement, parameter, session);
        return MY_BATIS_BUSSINESS_LOGIC.delete(statement, parameter, session);
    }

    @Override
    public <T> T executeSelectOne(String statement, Object parameter, SqlSession session, Class<T> type) {
        Object result;
        if (type == Long.class) {
            result = new Long(10L);
        } else {
            result = type;
        }

        if ("SEQ_GBL_DIM".equalsIgnoreCase((String) parameter)) {
            globalSequence++;
            result = globalSequence;
        }
        doReturn(result).when(MY_BATIS_BUSSINESS_LOGIC).executeSelectOne(statement, parameter, session, type);
        return MY_BATIS_BUSSINESS_LOGIC.executeSelectOne(statement, parameter, session, type);
    }

    @Override
    public List<?> selectList(String xmlMethod, SqlSession session) throws Exception {
        List<?> list = new ArrayList<Object>();
        doReturn(list).when(MY_BATIS_BUSSINESS_LOGIC).selectOne(xmlMethod, session);
        return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod, session);
    }

    @Override
    public List<?> selectList(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) throws Exception {
        List<?> list = new ArrayList<Object>();
        doReturn(list).when(MY_BATIS_BUSSINESS_LOGIC).selectList(xmlMethod, parameter, limits, session);
        return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod, parameter, limits, session);
    }

    @Override
    public List<?> selectMultiple(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) {
        List<?> list = new ArrayList<Object>();
        doReturn(list).when(MY_BATIS_BUSSINESS_LOGIC).selectMultiple(xmlMethod, parameter, limits, session);
        return MY_BATIS_BUSSINESS_LOGIC.selectMultiple(xmlMethod, parameter, limits, session);
    }

    @Override
    public List<?> selectMultiple(String statement, Object parameter, SqlSession session) {
        List<?> list = new ArrayList<Object>();
        doReturn(list).when(MY_BATIS_BUSSINESS_LOGIC).selectMultiple(statement, parameter, session);
        return MY_BATIS_BUSSINESS_LOGIC.selectMultiple(statement, parameter, session);
    }

    @Override
    public List<?> selectList(String xmlMethod, Object parameter, SqlSession session) throws Exception {
        List<?> list = new ArrayList<Object>(); //////
        doReturn(list).when(MY_BATIS_BUSSINESS_LOGIC).selectList(xmlMethod, parameter, session);
        return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod, parameter, session);
    }

    @Override
    public Object selectOne(String xmlMethod, SqlSession session) throws Exception {
        doReturn(new Object()).when(MY_BATIS_BUSSINESS_LOGIC).selectOne(xmlMethod, session);
        return MY_BATIS_BUSSINESS_LOGIC.selectOne(xmlMethod, session);
    }

    @Override
    public Object selectOne(String xmlMethod, Object parameter, SqlSession session) throws Exception {
        doReturn(new Object()).when(MY_BATIS_BUSSINESS_LOGIC).selectOne(xmlMethod, parameter, session);
        return MY_BATIS_BUSSINESS_LOGIC.selectOne(xmlMethod, parameter, session);
    }

    @Override
    public <T> T selectOne(String xmlMethod, Object parameter, SqlSession session, Class<T> type) throws Exception {
        Object result;
        if (type == Long.class) {
            result = 10L;
        } else {
            result = type;
        }

        //TODO: Check if parameter is to create a unique sequence
        if ("SEQ_GBL_DIM".equalsIgnoreCase((String) parameter)) {
            globalSequence++;
            result = globalSequence;
        }
        doReturn(result).when(MY_BATIS_BUSSINESS_LOGIC).selectOne(xmlMethod, parameter, session, type);
        return MY_BATIS_BUSSINESS_LOGIC.selectOne(xmlMethod, parameter, session, type);
    }

    @Override
    public <T> T selectSingle(String statement, Object parameter, SqlSession session, Class<T> type) {
        return null; // By default returning null as we want sub-classes to override this method according the scenario
    }

    @Override
    public void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception {
        doNothing().when(MY_BATIS_BUSSINESS_LOGIC).callPlSqlStoredProcedure(xmlMethod, parameterMap, session);
        MY_BATIS_BUSSINESS_LOGIC.callPlSqlStoredProcedure(xmlMethod, parameterMap, session);
    }

    @Override
    public void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap) throws Exception {
        doNothing().when(MY_BATIS_BUSSINESS_LOGIC).callPlSqlStoredProcedure(xmlMethod, parameterMap);
        MY_BATIS_BUSSINESS_LOGIC.callPlSqlStoredProcedure(xmlMethod, parameterMap);
    }

    @Override
    public int update(String xmlMethod, Object parameter, SqlSession session) {
        doReturn(1).when(MY_BATIS_BUSSINESS_LOGIC).update(xmlMethod, parameter, session);
        return MY_BATIS_BUSSINESS_LOGIC.update(xmlMethod, parameter, session);
    }

    @Override
    public SqlSession openSession() {
        SqlSession session = mock(SqlSession.class);
        doReturn(session).when(MY_BATIS_BUSSINESS_LOGIC).openSession();
        return MY_BATIS_BUSSINESS_LOGIC.openSession();
    }

    @Override
    public SqlSession openSession(ExecutorType executorType) {
        SqlSession session = mock(SqlSession.class);
        doReturn(session).when(MY_BATIS_BUSSINESS_LOGIC).openSession(executorType);
        return MY_BATIS_BUSSINESS_LOGIC.openSession(executorType);
    }

    @Override
    public SqlSession beginTransaction() throws Exception {
        SqlSession session = mock(SqlSession.class);
        doReturn(session).when(MY_BATIS_BUSSINESS_LOGIC).beginTransaction();
        return MY_BATIS_BUSSINESS_LOGIC.beginTransaction();
    }

    @Override
    public void commitTransaction(SqlSession session) throws Exception {
        doNothing().when(MY_BATIS_BUSSINESS_LOGIC).commitTransaction(session);
        MY_BATIS_BUSSINESS_LOGIC.commitTransaction(session);
    }

    @Override
    public void rollbackTransaction(SqlSession session) throws Exception {
        doNothing().when(MY_BATIS_BUSSINESS_LOGIC).rollbackTransaction(session);
        MY_BATIS_BUSSINESS_LOGIC.rollbackTransaction(session);
    }

    @Override
    public void commit(SqlSession session) {

    }

    @Override
    public void rollback(SqlSession session) {

    }

    @Override
    public void close(SqlSession session) {

    }

    @Override
    public void endTransaction(SqlSession session) throws Exception {
        doNothing().when(MY_BATIS_BUSSINESS_LOGIC).endTransaction(session);
        MY_BATIS_BUSSINESS_LOGIC.endTransaction(session);
    }

    @Override
    public void select(String statement, Object parameter, SqlSession session, ResultHandler handler) {

    }
}

package trueffect.truconnect.api.crud.mybatis;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Method definition for persistence operations with MyBatis
 * Created by marcelo.heredia on 5/19/2015.
 * @author Marcelo Heredia
 */
public interface PersistenceContext {

    List<?> selectList(String xmlMethod) throws Exception;

    List<?> selectList(String xmlMethod, Object parameter) throws Exception ;

    Object selectOne(String xmlMethod) throws Exception ;

    Object selectOne(String xmlMethod, Object parameter) throws Exception ;

    /**
     * Executes a Stored Procedure using the given {@code parameter} in the current {@code session}
     * @param statement The name of the Mapped SQL statement to execute
     * @param parameter The object {@code parameter} where to extract parameters from
     * @param session The {@code SqlSession} where the query will be executed
     */
    void execute(String statement, Object parameter, SqlSession session);

    /**
     * Executes a SQL update using the given {@code parameter} in the current {@code session}
     * @param statement The name of the Mapped SQL statement to execute
     * @param parameter The object {@code parameter} where to extract parameters from
     * @param session The {@code SqlSession} where the query will be executed
     * @return The number of affected records
     */
    int update(String statement, Object parameter, SqlSession session);

    /**
     * Executes a SQL remove using the given {@code parameter} in the current {@code session}
     * @param statement The name of the Mapped SQL statement to execute
     * @param parameter The object {@code parameter} where to extract parameters from
     * @param session The {@code SqlSession} where the query will be executed
     * @return The number of affected records
     */
    int delete(String statement, Object parameter, SqlSession session);

    /**
     * Executes a SQL create using the given {@code parameter} in the current {@code session}
     * @param statement The name of the Mapped SQL statement to execute
     * @param parameter The object {@code parameter} where to extract parameters from
     * @param session The {@code SqlSession} where the query will be executed
     * @param type
     * @return Type
     */
    <T> T  executeSelectOne(String statement, Object parameter, SqlSession session, Class<T> type);

    <T> List<T> selectMultiple(String statement, Object parameter, SqlSession session);

    <T> T selectSingle(String statement, Object parameter, SqlSession session, Class<T> type);

    List<?> selectList(String xmlMethod, SqlSession session) throws Exception ;

    /**
     *
     * @param xmlMethod
     * @param parameter
     * @param limits
     * @param session
     * @param <T>
     * @return
     * @throws Exception
     * @deprecated Use {@link PersistenceContext#selectMultiple(String, Object, RowBounds, SqlSession)} instead
     */
    @Deprecated
    <T extends Serializable> List<?> selectList(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) throws Exception;

    <T> List<T> selectMultiple(String xmlMethod, Object parameter, RowBounds limits, SqlSession session);

    List<?> selectList(String xmlMethod, Object parameter, SqlSession session) throws Exception ;

    /**
     *
     * @param xmlMethod
     * @param session
     * @return
     * @throws Exception
     * @deprecated Use {@link PersistenceContext#selectSingle(String, Object, SqlSession, Class)} instead
     */
    @Deprecated
    Object selectOne(String xmlMethod, SqlSession session) throws Exception;

    /**
     *
     * @param xmlMethod
     * @param parameter
     * @param session
     * @return
     * @deprecated Use {@link PersistenceContext#selectSingle(String, Object, SqlSession, Class)} instead
     * @throws Exception
     */
    @Deprecated
    Object selectOne(String xmlMethod, Object parameter, SqlSession session) throws Exception ;

    <T> T selectOne(String xmlMethod, Object parameter, SqlSession session, Class<T> type) throws Exception;

    /**
     * Executes a SQL statement
     * @param xmlMethod The name of the SQL mapper
     * @param parameterMap The map of parameters to use in the query
     * @param session The SqlSession where tu run the query
     * @throws Exception
     * @deprecated Use {@link trueffect.truconnect.api.crud.mybatis.PersistenceContext#execute(String, Object, org.apache.ibatis.session.SqlSession)} method instead to avoid using checked exceptions
     */
    @Deprecated
    void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception;

    /**
     * Executes a SQL statement
     * @param xmlMethod The name of the SQL mapper
     * @param parameterMap The map of parameters to use in the query
     * @throws Exception
     */
    @Deprecated
    void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap) throws Exception;

    SqlSession openSession();

    SqlSession openSession(ExecutorType executorType);

    SqlSession beginTransaction() throws Exception ;

    void commitTransaction(SqlSession session) throws Exception ;

    void rollbackTransaction(SqlSession session) throws Exception ;

    void commit(SqlSession session);

    void rollback(SqlSession session);

    void close(SqlSession session);

    void endTransaction(SqlSession session) throws Exception ;

    /**
     * Allows obtaining multiple results for a given {@code statement} and {@code session} using a custom {@ResultHandler}
     * @param statement The statement to execute
     * @param parameter The parameter(s) sent to the query
     * @param session The SqlSession where tu run the query
     * @param handler The custom result handler
     */
    void select(String statement, Object parameter, SqlSession session, ResultHandler handler);
}

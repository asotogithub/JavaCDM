package trueffect.truconnect.api.crud.mybatis;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

/**
 * Common methods amongst all DAO interfaces
 * Created by marcelo.heredia on 5/29/2015.
 * @author Marcelo Heredia
 */
public interface GenericDao {
    /**
     * Opens up a new MyBatis {@code SqlSession}
     *
     * @return a new {@code SqlSession}
     */
    SqlSession openSession();

    /**
     * Opens up a new MyBatis {@code SqlSession} with a given {@code ExecutorType}
     * @param executorType The type of executor
     * @return a new {@code SqlSession}
     */
    SqlSession openSession(ExecutorType executorType);

    /**
     * Commits a transaction for a given {@code SqlSession}
     *
     * @param session The {@code SqlSession} where the transaction will be committed
     */
    void commit(SqlSession session);

    /**
     * Commits a transaction for a given {@code SqlSession}
     *
     * @param session The {@code SqlSession} where the transaction will be committed
     */
    void rollback(SqlSession session);

    /**
     * Closes a {@code SqlSession}
     *
     * @param session The {@code SqlSession} to close
     */
    void close(SqlSession session);

    /**
     * Get a PK value
     * @param session The current persistence session
     * @return the value to PK siteMeasurement
     */
    Long getNextId(SqlSession session);
}

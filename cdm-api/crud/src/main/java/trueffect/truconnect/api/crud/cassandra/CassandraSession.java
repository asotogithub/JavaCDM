package trueffect.truconnect.api.crud.cassandra;

import com.datastax.driver.core.Session;

public final class CassandraSession {
    private static Session session = null;
    private CassandraSession(){}
    public static Session getSessionInstance(){
        if (session == null || session.isClosed()) {
            synchronized (CassandraSession.class) {
                if (session == null || session.isClosed()) {
                    session = CassandraCluster.getClusterInstance().connect();
                }
            }
        }
        return session;
    }
}

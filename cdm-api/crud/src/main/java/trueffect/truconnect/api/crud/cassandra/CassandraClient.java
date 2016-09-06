package trueffect.truconnect.api.crud.cassandra;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.ResultSet;

public class CassandraClient {

    private Session session = null;
    public CassandraClient(){
        session = CassandraSession.getSessionInstance();
    }

    public ResultSet executeQuery(String mappedQuery){
        isRunning();
        ResultSet resultSet = session.execute(mappedQuery);
        return resultSet;
    }

    public void isRunning(){
        if ( session.isClosed() ){
            session = CassandraSession.getSessionInstance();
        }
    }
}

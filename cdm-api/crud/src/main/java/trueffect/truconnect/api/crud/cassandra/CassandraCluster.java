package trueffect.truconnect.api.crud.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.policies.ConstantSpeculativeExecutionPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.crud.Constants;
import trueffect.truconnect.api.resources.ResourceUtil;

public final class CassandraCluster {
    private static Cluster cluster = null;
    private static Logger LOGGER = LoggerFactory.getLogger(CassandraCluster.class);

    private CassandraCluster(){}
    public static Cluster getClusterInstance(){
        if (cluster == null || cluster.isClosed()) {
            synchronized (CassandraCluster.class) {
                if (cluster == null  || cluster.isClosed()) {
                    /**
                     * ConstantSpeculativeExecutionPolicy
                     * If a response hasn't been received by a set number of MS it attempts
                     * another connection to a different node if that is successful it cancels the first request
                     * *Note. weird things can happen when you do insert/delete statements, timestamps should be used
                     * in those instances
                     *
                     * EC2MultiRegionAddressTranslator
                     * Its distinctive feature is that it translates addresses according to the location of the Cassandra host:
                     * addresses in different EC2 regions (than the client) are unchanged;
                     * addresses in the same EC2 region are translated to private IPs.
                     * This optimizes network costs, because Amazon charges more for communication over public IPs.
                     *
                     * LoadBalancing for the future when hosts are in multiple regions
                     * This will check local first then remote making it more effecient then standard roundrobin as used below
                     * DCAwareRoundRobinPolicy.builder()
                     * .withLocalDc("myLocalDC")
                     * .withUsedHostsPerRemoteDc(2)
                     * .allowRemoteDCsForLocalConsistencyLevel()
                     */
                    try {
                        String nodeAddresses = ResourceUtil.get(Constants.CASSANDRA_NODE_ADDRESSES);
                        cluster = Cluster.builder()
                                .addContactPoints(nodeAddresses.split(","))
                                //.withAddressTranslator(new EC2MultiRegionAddressTranslator())
                                .withLoadBalancingPolicy(new RoundRobinPolicy())
                                .withSpeculativeExecutionPolicy(
                                        new ConstantSpeculativeExecutionPolicy(
                                                Integer.parseInt(ResourceUtil.get(Constants.CASSANDRA_SPECULATIVE_DELAY)), // delay before a new execution is launched
                                                Integer.parseInt(ResourceUtil.get(Constants.CASSANDRA_SPECULATIVE_MAX_ATTEMPTS))    // maximum number of additional executions
                                        ))
                                .build();
                    } catch (Exception e){
                        LOGGER.error("Unable to connect to Cassandra Cluster. ",e);
                    }
                }
            }
        }

        return cluster;
    }
}
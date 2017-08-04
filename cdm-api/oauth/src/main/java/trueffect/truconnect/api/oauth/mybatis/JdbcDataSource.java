package trueffect.truconnect.api.oauth.mybatis;

import trueffect.truconnect.api.resources.ResourceUtil;

import com.mchange.v2.c3p0.AbstractComboPooledDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.beans.PropertyVetoException;
import java.io.Serializable;

import javax.naming.Referenceable;

/**
 * Created by richard.jaldin on 10/27/2015.
 */
@Component
public class JdbcDataSource extends AbstractComboPooledDataSource implements Serializable, Referenceable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(JdbcDataSource.class);

    public JdbcDataSource() {
        super();
        connectionProperties();
    }

    private void connectionProperties() {
        setDataSourceName("OAUTH_TOKEN_STORE");
        setJdbcUrl(ResourceUtil.get("dataStore.url"));
        setUser(ResourceUtil.get("dataStore.username"));
        setPassword(ResourceUtil.get("dataStore.password"));
        try {
            setDriverClass(ResourceUtil.get("dataStore.driver"));
            setContextClassLoaderSource("library");
        } catch (PropertyVetoException e) {
            LOG.error("Failed initializing the connection pool for Oauth2");
        }
        setPrivilegeSpawnedThreads(true);
        setAcquireIncrement(1);
        setInitialPoolSize(1);
        setMinPoolSize(1);
        setMaxPoolSize(30);
        setMaxIdleTime(3600);
        setIdleConnectionTestPeriod(3600);
    }
}

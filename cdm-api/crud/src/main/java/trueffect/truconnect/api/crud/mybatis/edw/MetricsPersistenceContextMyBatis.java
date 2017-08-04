package trueffect.truconnect.api.crud.mybatis.edw;

import trueffect.truconnect.api.crud.Constants;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.resources.ResourceUtil;

import java.util.Properties;

/**
 * @author Jeff Fryer
 */
public class MetricsPersistenceContextMyBatis extends PersistenceContextMyBatis {

    public MetricsPersistenceContextMyBatis() {
    	super(Constants.METRICS_MYBATIS_PATH);
    }

    protected Properties connectionProperties() {
        Properties props = new Properties();
        props.setProperty("url", ResourceUtil.get(Constants.METRICS_DATA_STORE_URL_RESOURCE));
        props.setProperty("username", ResourceUtil.get(Constants.METRICS_DATA_STORE_USERNAME_RESOURCE));
        props.setProperty("password", ResourceUtil.get(Constants.METRICS_DATA_STORE_PASSWORD_RESOURCE));
        props.setProperty("driver", ResourceUtil.get(Constants.METRICS_DATA_STORE_DRIVER_RESOURCE));
        return props;
    }
}

package trueffect.truconnect.api.crud.mybatis.dim;

import trueffect.truconnect.api.crud.Constants;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.resources.ResourceUtil;

import java.util.Properties;

/**
 *
 * @author Gustavo Claure
 */
public class DimPersistenceContextMyBatis extends PersistenceContextMyBatis {

    public DimPersistenceContextMyBatis() {
        super(Constants.DIM_MYBATIS_PATH);
    }

    @Override
    protected Properties connectionProperties() {
        Properties props = new Properties();
        props.setProperty("url", ResourceUtil.get(Constants.DIM_DATA_STORE_URL_RESOURCE));
        props.setProperty("username", ResourceUtil.get(Constants.DIM_DATA_STORE_USERNAME_RESOURCE));
        props.setProperty("password", ResourceUtil.get(Constants.DIM_DATA_STORE_PASSWORD_RESOURCE));
        props.setProperty("driver", ResourceUtil.get(Constants.DIM_DATA_STORE_DRIVER_RESOURCE));
        return props;
    }
}

package trueffect.truconnect.api.crud.dao.impl;

import com.trueffect.adm.data.DatasetMetrics;
import com.trueffect.adm.data.TransactionStore;
import com.trueffect.adm.data.impl.RedshiftTransactionStore;

import trueffect.truconnect.api.crud.Constants;
import trueffect.truconnect.api.crud.dao.AdmTransactionDao;
import trueffect.truconnect.api.resources.ResourceUtil;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

/**
 * Created by mnelson on 12/9/15.
 */
public class AdmTransactionImpl implements AdmTransactionDao {
    
    private static Logger LOGGER = LoggerFactory.getLogger(AdmTransactionImpl.class);
    private final TransactionStore store;
    
    public AdmTransactionImpl(String dbURL, Properties connectionProperties, int retentionInterval) {
        store = new RedshiftTransactionStore(dbURL, connectionProperties, retentionInterval);
    }
    
    @Override
    public DatasetMetrics datasetMetrics(UUID datasetId) {
        DatasetMetrics metrics = store.datasetMetrics(datasetId);
        return metrics;
    }

    @Override
    public DatasetMetrics datasetMetrics(UUID datasetId, DateTime startDate, DateTime endDate) {
        DatasetMetrics metrics = store.datasetMetrics(datasetId, startDate, endDate);
        return metrics;
    }
    
    public static AdmTransactionImpl instanceFromResources() {
        String dbURL = ResourceUtil.get(Constants.ADM_REDSHIFT_URL);
        Properties props = new Properties();
        props.put("user", ResourceUtil.get(Constants.ADM_REDSHIFT_USER));
        props.put("password", ResourceUtil.get(Constants.ADM_REDSHIFT_PASSWORD));
        int retentionInterval = Integer.parseInt(ResourceUtil.get(Constants.ADM_REDSHIFT_RETENTION_INTERVAL));
        return new AdmTransactionImpl(dbURL, props, retentionInterval);
    }
}

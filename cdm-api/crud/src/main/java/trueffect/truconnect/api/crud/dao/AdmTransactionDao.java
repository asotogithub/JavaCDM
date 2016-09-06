package trueffect.truconnect.api.crud.dao;

import com.trueffect.adm.data.DatasetMetrics;

import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by mnelson on 12/9/15.
 */
public interface AdmTransactionDao {
    DatasetMetrics datasetMetrics(UUID datasetId);

    DatasetMetrics datasetMetrics(UUID datasetId, DateTime startDate, DateTime endDate);
}

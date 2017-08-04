package trueffect.truconnect.api.crud.dao;

import com.trueffect.delivery.formats.adm.DatasetConfig;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.exceptions.business.Error;

import java.util.List;
import java.util.UUID;

/**
 * Created by jgearheart on 11/3/15.
 */
public interface DatasetConfigDao {
    List<DatasetConfig> getDatasets(Long agencyId);

    DatasetConfig getDataset(UUID id);

    Either<Error, Void> save(DatasetConfig config);
}

package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.crud.Constants;
import trueffect.truconnect.api.crud.dao.DatasetConfigDao;
import trueffect.truconnect.api.resources.ResourceUtil;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.trueffect.adm.data.DatasetConfigStore;
import com.trueffect.adm.data.impl.DynamoDbDatasetConfigStore;
import com.trueffect.delivery.formats.adm.DatasetConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.runtime.BoxedUnit;
import scala.util.Try;

/**
 * Wrapper around the scala implementation.
 */
public class DatasetConfigDaoImpl implements DatasetConfigDao {

    private static Logger LOGGER = LoggerFactory.getLogger(DatasetConfigDaoImpl.class);
    private final DatasetConfigStore store;

    public DatasetConfigDaoImpl(AWSCredentialsProvider credentials, String tableName, String region, String s3PathIndexName, String agencyIndexName, String domainIndexName) {
        store = new DynamoDbDatasetConfigStore(credentials, tableName, region, s3PathIndexName, agencyIndexName, domainIndexName);
    }

    @Override
    public List<DatasetConfig> getDatasets(Long agencyId) {
        Try<Seq<DatasetConfig>> datasets = store.getDatasets(agencyId);
        if(datasets.isSuccess()) {
            return JavaConversions.seqAsJavaList(datasets.get());
        } else {
            LOGGER.warn("Getting datasets was not successful. " + datasets.failed().get().getMessage());
            return new ArrayList<>(0);
        }
    }

    public DatasetConfig getDataset(UUID id) {
        Try<Option<DatasetConfig>> dataset = store.getDataset(id);
        if(dataset.isSuccess()) {
            Option<DatasetConfig> datasetConfigOption = dataset.get();
            if(datasetConfigOption.nonEmpty()) {
                return datasetConfigOption.get();
            } else {
                return null;
            }
        } else {
            LOGGER.warn("Getting datasets was not successful. " + dataset.failed().get().getMessage());
            return null;
        }
    }

    @Override
    public Either<Error, Void> save(DatasetConfig config) {
        Try<BoxedUnit> putTry = store.updateDatasetForUI(config);
        if(putTry.isSuccess()) {
            return Either.success(null);
        } else {
            Error error;
            Throwable throwable = putTry.failed().get();
            ErrorCode code;
            if(throwable instanceof IllegalArgumentException) {
                LOGGER.warn("Error from core: ", throwable);
                code = ValidationCode.INVALID;
                error = new ValidationError(throwable.getMessage(), code);
            } else {
                LOGGER.error("Unexpected exception from core: ", throwable);
                code = BusinessCode.EXTERNAL_COMPONENT_ERROR;
                error = new Error(throwable.getMessage(), code);
            }
            return Either.error(error);
        }
    }

    /**
     * Create a new instance of the Dataset Config DAO for ADM datasets.
     *
     * @return initialized DatasetConfigDao
     */
    public static DatasetConfigDaoImpl instanceFromResources() {
        String awsAccessKey = ResourceUtil.get(Constants.AWS_ACCESS_KEY);
        String awsSecretKey = ResourceUtil.get(Constants.AWS_SECRET_KEY);
        AWSCredentialsProvider credentials = new StaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
        String tableName = ResourceUtil.get(Constants.ADM_DATA_STORE_TABLE_NAME);
        String s3PathIndexName = ResourceUtil.get(Constants.ADM_DATA_STORE_S3_PATH_INDEX_NAME);
        String agencyIndexName = ResourceUtil.get(Constants.ADM_DATA_STORE_AGENCY_INDEX_NAME);
        String domainIndexName = ResourceUtil.get(Constants.ADM_DATA_STORE_DOMAIN_INDEX_NAME);
        String region = ResourceUtil.get(Constants.ADM_DATA_STORE_REGION);
        return new DatasetConfigDaoImpl(credentials, tableName, region, s3PathIndexName, agencyIndexName, domainIndexName);
    }
}

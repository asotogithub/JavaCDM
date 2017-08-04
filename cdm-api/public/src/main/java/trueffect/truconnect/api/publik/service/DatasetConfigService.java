package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.dto.DatasetMetrics;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AdmTransactionDao;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.DatasetConfigDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.AdmTransactionImpl;
import trueffect.truconnect.api.crud.dao.impl.AdvertiserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.DatasetConfigDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.DatasetManager;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Service for managing ADM datasets.
 */
@Path("/Datasets")
public class DatasetConfigService extends GenericService {

    private final DatasetManager datasetManager;

    public DatasetConfigService() {
        PersistenceContext context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        AdvertiserDao advertiserDao = new AdvertiserDaoImpl(context);
        DatasetConfigDao datasetConfigDao = DatasetConfigDaoImpl.instanceFromResources();
        AdmTransactionDao admTransactionDao = AdmTransactionImpl.instanceFromResources();
        datasetManager = new DatasetManager(datasetConfigDao, advertiserDao, admTransactionDao, accessControl);
    }

    public DatasetConfigService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Get an existing dataset config with additional information.
     *
     * @param id
     * @return
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getDatasetConfig(@PathParam("id") String id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_ADM_DATASET_CONFIG);
            Either<Error, DatasetConfigView> config = datasetManager.get(UUID.fromString(id), oauthKey());
            if (config.isError()) {
                return handleErrorCodes(config.error());
            } else {
                return APIResponse.ok(config.success()).build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(), e);
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Update an existing dataset config.  If a dataset id is supplied in the view, this call will
     * fail.
     *
     * @param id
     * @param configView
     * @return
     */
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateDatasetConfig(@PathParam("id") String id, DatasetConfigView configView) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_ADM_DATASET_CONFIG);
            Either<Errors, DatasetConfigView> config = datasetManager.update(configView, id, oauthKey());
            if (config.isError()) {
                return handleErrorCodes(config.error());
            } else {
                return APIResponse.ok(config.success(), uriInfo, "Datasets/" + config.success().getDatasetId().toString()).build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(), e);
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Create a new dataset config.
     *
     * @param configView view of the data to create the config from.
     * @return
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createDatasetConfig(DatasetConfigView configView) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_ADM_DATASET_CONFIG);
            Either<Error, DatasetConfigView> config = datasetManager.create(configView, oauthKey());
            if (config.isError()) {
                return handleErrorCodes(config.error());
            } else {
                return APIResponse.created(config.success(), uriInfo, "Datasets").build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(), e);
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Get an advertiser's transaction metrics
     * @param id    advertiser ID
     * @return
     */
    @GET
    @Path("/{id}/metrics")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getDatasetMetrics(@PathParam("id") String id,
                                      @QueryParam("startDate") String startDate,
                                      @QueryParam("endDate") String endDate) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_ADM_METRICS);
            Either<Error, DatasetMetrics> metrics =
                    datasetManager.datasetMetrics(id, startDate, endDate, oauthKey());
            if (metrics.isError()) {
                return handleErrorCodes(metrics.error());
            } else {
                return APIResponse.ok(metrics.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }
}

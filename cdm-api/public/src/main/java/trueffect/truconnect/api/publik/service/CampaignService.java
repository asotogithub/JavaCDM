package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotAcceptableException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.DuplicateCIError;
import trueffect.truconnect.api.commons.exceptions.business.ImportExportErrors;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.Error;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.BulkCreativeInsertion;
import trueffect.truconnect.api.commons.model.dto.CampaignDetailsDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeView;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupDtoForCampaigns;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionSearchOptions;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.dto.PackagePlacementView;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.dto.SiteContactView;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.model.importexport.Action;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.MediaBuyDao;
import trueffect.truconnect.api.crud.dao.MetricsDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoBase;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.dao.SiteDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.AdvertiserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.BrandDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CookieDomainDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupCreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ExtendedPropertiesDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderStatusDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.MediaBuyDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.MetricsDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackageDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackagePlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementStatusDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PublisherDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteSectionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SizeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackageDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackagePlacementDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.dim.DimPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.edw.MetricsPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.CampaignManager;
import trueffect.truconnect.api.crud.service.CreativeGroupManager;
import trueffect.truconnect.api.crud.service.CreativeInsertionManager;
import trueffect.truconnect.api.crud.service.CreativeManager;
import trueffect.truconnect.api.crud.service.ImportExportManager;
import trueffect.truconnect.api.crud.service.InsertionOrderManager;
import trueffect.truconnect.api.crud.service.MetricsManager;
import trueffect.truconnect.api.crud.service.PackageManager;
import trueffect.truconnect.api.crud.service.PackagePlacementManager;
import trueffect.truconnect.api.crud.service.PlacementManager;
import trueffect.truconnect.api.crud.service.PublisherManager;
import trueffect.truconnect.api.crud.service.SiteManager;
import trueffect.truconnect.api.crud.service.SiteSectionManager;
import trueffect.truconnect.api.crud.service.SizeManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Set of REST Services related to <code>Campaign</code> management.
 * 
 * @author Abel Soto
 */
@Api(value = "Campaigns", description = "Campaign service")
@Path("/Campaigns")
public class CampaignService extends GenericService {

    private CampaignManager campaignManager;
    private CreativeManager creativeManager;
    private CreativeGroupManager creativeGroupManager;
    private MetricsManager metricsManager;
    private PackagePlacementManager packagePlacementManager;
    private PlacementManager placementManager;
    private PackageManager packageManager;
    private CreativeInsertionManager creativeInsertionManager;
    private SiteManager siteManager;
    private ImportExportManager importExportManager;

    public CampaignService() {
        PersistenceContext context = new PersistenceContextMyBatis();
        PersistenceContext dimContext = new DimPersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        CampaignDaoImpl campaignDao = new CampaignDaoImpl(context);
        CreativeDao creativeDao = new CreativeDaoImpl(context);
        CreativeGroupCreativeDao creativeGroupCreativeDao = new CreativeGroupCreativeDaoImpl(context);
        CreativeGroupDao creativeGroupDao = new CreativeGroupDaoImpl(context);
        PlacementDao placementDao = new PlacementDaoImpl(context);
        AdvertiserDaoImpl advertiserDao = new AdvertiserDaoImpl(context);
        BrandDaoImpl brandDao = new BrandDaoImpl(context);
        CookieDomainDaoImpl cookieDomainDao = new CookieDomainDaoImpl(context);
        CreativeInsertionDao creativeInsertionDao = new CreativeInsertionDaoImpl(context);
        campaignManager = new CampaignManager(campaignDao, advertiserDao, brandDao,
                cookieDomainDao, creativeDao, accessControl);
        UserDaoImpl userDao = new UserDaoImpl(context, accessControl);
        ExtendedPropertiesDao extendedPropertiesDao = new ExtendedPropertiesDaoImpl(context);
        creativeManager = new CreativeManager(creativeDao, creativeGroupDao,
                creativeGroupCreativeDao, creativeInsertionDao, campaignDao, userDao, 
                extendedPropertiesDao, accessControl);
        PersistenceContextMyBatis metricsContext = new MetricsPersistenceContextMyBatis();
        MetricsDao metricsDaoForUserValidation = new MetricsDaoImpl(context);
        MetricsDao metricsDaoForEDW = new MetricsDaoImpl(metricsContext);
        metricsManager = new MetricsManager(metricsDaoForUserValidation,
                metricsDaoForEDW, accessControl);

        CostDetailDaoExtended placementCostDetailDao = new CostDetailDaoImpl(CostDetailType.PLACEMENT, context);
        SiteSectionDao siteSectionDao = new SiteSectionDaoImpl(context);
        SizeDao sizeDao = new SizeDaoImpl(context);
        InsertionOrderDao insertionOrderDao = new InsertionOrderDaoImpl(context);
        InsertionOrderStatusDao insertionOrderStatusDao = new InsertionOrderStatusDaoImpl(context);
        PlacementStatusDao placementStatusDao = new PlacementStatusDaoImpl(context);
        PackageDaoExtended packageDao = new PackageDaoImpl(context);
        CostDetailDaoExtended packageCostDetailDao = new CostDetailDaoImpl(CostDetailType.PACKAGE, context);
        PackagePlacementDaoExtended packagePlacementDao = new PackagePlacementDaoImpl(context);

        CostDetailDaoBase dimPlacementCostDetailDao = new DimCostDetailDaoImpl(CostDetailType.PLACEMENT, dimContext);
        CostDetailDaoBase dimPackageCostDetailDao = new DimCostDetailDaoImpl(CostDetailType.PACKAGE, dimContext);
        PackageDaoBase dimPackageDao = new DimPackageDaoImpl(dimContext);
        PackagePlacementDaoBase dimPackagePlacementDao = new DimPackagePlacementDaoImpl(dimContext);

        packagePlacementManager = new PackagePlacementManager(placementDao, placementCostDetailDao,
                campaignDao, siteSectionDao, sizeDao, placementStatusDao, userDao,
                extendedPropertiesDao, insertionOrderDao, insertionOrderStatusDao,
                packageDao, packageCostDetailDao, packagePlacementDao, dimPackageCostDetailDao,
                dimPlacementCostDetailDao, dimPackageDao, dimPackagePlacementDao,
                creativeInsertionDao, accessControl);

        packageManager = new PackageManager(packageDao, packageCostDetailDao, placementDao,
                placementCostDetailDao, packagePlacementDao, dimPlacementCostDetailDao,
                dimPackageDao, dimPackageCostDetailDao, dimPackagePlacementDao, insertionOrderDao,
                accessControl);

        placementManager = new PlacementManager(placementDao, placementCostDetailDao, campaignDao,
                siteSectionDao, sizeDao, placementStatusDao, userDao, extendedPropertiesDao, insertionOrderDao,
                insertionOrderStatusDao, packageDao, packagePlacementDao, dimPlacementCostDetailDao,
                creativeInsertionDao, accessControl);

        creativeGroupManager = new CreativeGroupManager(creativeGroupDao, creativeGroupCreativeDao,
                creativeInsertionDao, creativeDao, campaignDao, userDao, extendedPropertiesDao,
                accessControl);

        creativeInsertionManager = new CreativeInsertionManager(creativeInsertionDao, campaignDao, placementDao,
                        creativeDao, creativeGroupDao, creativeGroupCreativeDao, accessControl);

        SiteDao siteDao = new SiteDaoImpl(context);
        siteManager = new SiteManager(siteDao, extendedPropertiesDao, accessControl);

        MediaBuyDao mediaBuyDao = new MediaBuyDaoImpl(context);
        InsertionOrderManager ioManager = new InsertionOrderManager(insertionOrderDao, insertionOrderStatusDao,
                userDao,placementDao,placementStatusDao, creativeInsertionDao, accessControl);
        PublisherDao publisherDao = new PublisherDaoImpl(context);
        PublisherManager publisherManager = new PublisherManager(publisherDao, accessControl);
        SiteSectionManager sectionManager = new SiteSectionManager(siteSectionDao, accessControl);
        SizeManager sizeManager = new SizeManager(sizeDao,accessControl);
        importExportManager = new ImportExportManager(campaignDao, creativeInsertionDao,
                creativeGroupDao, insertionOrderDao, publisherDao, siteDao, siteSectionDao,
                sizeDao, placementDao, placementCostDetailDao, packageCostDetailDao, mediaBuyDao,
                userDao, dimPackagePlacementDao, packageDao, ioManager, publisherManager, siteManager,
                sectionManager, sizeManager, placementManager, packageManager,
                packagePlacementManager, accessControl);
    }

    public CampaignService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Returns a <code>Campaign</code> given its <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Campaign</code> that matches the given <code>id</code> exists
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     *     <li>No record was for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Campaign</code> id
     * @return a <code>Campaign</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCampaign(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Campaign campaign = campaignManager.get(id, oauthKey());
            return APIResponse.ok(campaign).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>Campaign</code> with the given <code>id</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_NEW_CAMPAIGN</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>Campaign</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaign</code>: Not  null</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @param campaign Campaign to be saved.
     * @return A new <code>Campaign</code> saved on the database.
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response create(Campaign campaign) {
        return create(campaign, false, AccessPermission.CREATE_NEW_CAMPAIGN);
    }

    /**
     * Creates a new <code>Creative</code> image with the given <code>Campaign</code> id and the File.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed: <code>ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>Creative</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>inputStream</code>: Is empty</li>
     *     <li><code>campaignId</code>: Is null</li>
     *     <li><code>filename</code>: File name with no extension length should be less than 122 characters</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @param id The ID of Campaign
     * @param inputStream File upload
     * @param filename File name
     * @param isExpandable Boolean indicating if creative is expandable; default is false
     * @return A new <code>Creative</code> created given the provided parameters
     */
    @POST
    @Path("/{id}/Creative")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveCreativeImage(@PathParam("id") Long id, InputStream inputStream, @QueryParam("filename") String filename,
            @DefaultValue("false") @QueryParam("isExpandable") Boolean isExpandable,
            @DefaultValue("0") @QueryParam("height") Long height,
            @DefaultValue("0") @QueryParam("width") Long width)
            // those default values need to agree with Constants.UNSET_WIDTH_OR_HEIGHT
    {
        log.debug("Save file for Creative: campaignId -> " + id + ", InputStream: " + inputStream);
        try {
            if (inputStream == null) {
                return APIResponse.bad("Image file cannot be empty.").build();
            }
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Creative responseSaveFile;
            try {
                responseSaveFile = creativeManager.saveCreativeFile(inputStream, filename, id, oauthKey(), isExpandable, height, width);
            } catch (SystemException e) {
                throw BusinessValidationUtil.parseToLegacyException(e);
            }
            return APIResponse.created(responseSaveFile, uriInfo, "Creative").build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (NotAcceptableException e) {
            log.warn("Not Acceptable Exception", e);
            return APIResponse.notAcceptable(e.getMessage());
        } catch (ConflictException e) {
            log.warn("Conflict Exception", e);
            if (e.getMessage().equalsIgnoreCase("File not uploaded.")) {
                Error error = new Error("File not uploaded.", ApiValidationUtils.TYPE_INVALID);
                Errors errors = new Errors(Arrays.asList(error));
                String fileType = AdminFile.fileType(filename);
                return Response.status(Response.Status.CONFLICT).header("type", fileType).entity(errors).build();
            }
            return APIResponse.conflict(e.getMessage());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("Error while closing stream", e);
                }
            }
        }
    }

    /**
     * Returns a list of <code>creative</code> given campaign <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CREATIVE_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a list of <code>Creative</code> is retrieved by given <code>campaign</code> id
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     *     <li>No record was for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param campaignId the <code>Campaign</code> id
     * @param startIndex the <code>start value</code> to retrieve data from collection
     * @param pageSize the<code>Size</code> of collection
     * @return a <code>List</code> of creative.
     */
    @GET
    @Path("/{id}/creatives")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreatives(@PathParam("id") Long campaignId,
                                 @QueryParam("startIndex") Long startIndex,
                                 @QueryParam("pageSize") Long pageSize) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CREATIVE_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<Creative>> result =
                    creativeManager.getCreativesByCampaign(campaignId, startIndex, pageSize, oauthKey());
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Saves a <code>Creative</code> image and stores it in a temporary path. This method doesn't
     * create any Creative on database.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>Creative File</code> was saved successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>inputStream</code>: Is empty</li>
     *     <li><code>campaignId</code>: Is null</li>
     *     <li><code>filename</code>: File name with no extension length should be less than 122 characters</li>
     * </ul>

     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @param id The ID of Campaign
     * @param inputStream Uploaded File
     * @param filename Filename of the uploaded file
     * @param height
     * @param width
     * @param isExpandable
     * @return a Creative representation of the uploaded Creative
     */
    @POST
    @Path("/{id}/creativeUpload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response creativeUpload(@PathParam("id") Long id,
                                   InputStream inputStream,
                                   @QueryParam("filename") String filename,
                                   @DefaultValue("false") @QueryParam("isExpandable") Boolean isExpandable,
                                   @DefaultValue("0") @QueryParam("height") Long height,
                                   @DefaultValue("0") @QueryParam("width") Long width) {
        log.debug("Save file for Creative: campaignId -> {}, filename: {}", id, filename);
        try {
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            log.debug("creativeUpload height: " + height + " width: " + width);
            Creative creative = creativeManager.saveCreativeFileInTmp(inputStream, filename, id,
                    isExpandable, height, width, oauthKey());
            return APIResponse.created(creative, uriInfo, "Creative").build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (SystemException e) {
            log.warn("Error while processing image", e);
            throw new WebApplicationSystemException(e);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("Error while closing out stream", e);
                }
            }
        }
    }

    /**
     * Returns a <code>Creative groups</code> given its <code>Campaign Id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>id</code> that matches the given <code>Campaign Id</code> exists
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     *     <li>No record was for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Campaign</code> id
     * @return a <code>List</code> of creative groups by <code>Campaign Id</code>.
     */
    @GET
    @Path("/{id}/creativeGroups")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}/creativeGroups", notes="Get set of Creative Groups given a CapmaignId.",
                  response=CreativeGroupDtoForCampaigns.class, responseContainer = "List")
    @ApiResponses(value={ @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Advertiser found OR Access Token belongs to a User that does not have access to the requested data") })
    public Response getCreativeGroups(@PathParam("id") Long id, @QueryParam("startIndex") Long startIndex,
                                      @QueryParam("pageSize") Long pageSize) {

        Response response = null;
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<CreativeGroupDtoForCampaigns> creativeGroups = campaignManager.getCreativeGroupsForCampaign(
                    id, startIndex, pageSize, oauthKey());
            response = APIResponse.ok(creativeGroups).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for Campaign Id = {} and User= {} ", id, oauthKey().getUserId());
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            response = APIResponse.error(e.getMessage());
        }

        return response;
    }

    /**
     * Returns a <code>Campaign</code> given its <code>Brand</code> and <code>Advertiser</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Campaign</code> that matches the given <code>id</code> exists
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaign</code>: Is bull</li>
     * </ul>

     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     *     <li>No record was for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The ID of Campaign
     * @return The Campaign details.
     */
    @GET
    @Path("/{id}/detail")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCampaignDetail(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            CampaignDetailsDTO details = campaignManager.getDetails(id, oauthKey());
            return APIResponse.ok(details).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates a <code>Campaign</code> with the given <code>id</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>Campaign</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>Campaign</code>: Is null</li>
     *     <li><code>Campaign</code>: Not exists</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 409 Conflict - When an attempt to create a <code>Size</code> with a given
     * <code>width</code> and <code>height</code> that already exists.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param id <code>campaign</code> to be updated
     * @return the updated <code>campaign</code>.
     */
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response update(@PathParam("id") Long id, Campaign campaign) {
        try {
            // Check roles
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            // Perform update
            Campaign updated = campaignManager.update(campaign, oauthKey());
            log.info(oauthKey().toString()+" Updated "+ id);
            log.debug("Finished the PUT update command successfully");
            return APIResponse.ok(updated).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a Campaign for Testing purposes. This method does the same as
     * {@link CampaignService#create(Campaign)}; except this:
     * <ul>
     *     <li>Auto-generates the {@code CAMPAIGN.RESOURCE_PATH_ID} to a unique ID</li>
     * </ul>
     * Permissions Allowed:
     * <ul>
     *     <li>{@code AUTOMATED_TEST}</li>
     * </ul>
     * @param campaign The campaign to create
     * @return The created campaign Entity
     */
    @POST
    @Path("/test")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createForTesting(Campaign campaign) {
        return create(campaign, true, AccessPermission.AUTOMATED_TEST);
    }

    private Response create(Campaign campaign, boolean testingMode, AccessPermission... permissions) {
        try {
            try {
                checkValidityOfToken();
                checkPermissions(permissions);
                campaign = campaignManager.create(campaign, oauthKey(), testingMode);
            } catch (SystemException e) {
                throw BusinessValidationUtil.parseToLegacyException(e);
            }
            log.info("Campaign created by user {} : {}", oauthKey().getUserId(), campaign);
            return APIResponse.created(campaign, uriInfo, "Campaigns").build();
        } catch (AccessDeniedException e) {
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (ConflictException e) {
            return APIResponse.conflict(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected Exception, reason: ", e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns <code>Creative Insertions</code> by <code>level</code> and <code>campaign</code> id.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CREATIVE_INSERTION_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a list of <code>Creative insertions</code> by <code>level</code> and
     * <code>campaign</code> id exists
     *
     * @HTTP 401 Unauthorized - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param campaignId The <code>Campaign</code> id.
     * @param parentIds the <code>parent</code> id.
     * @param startIndex the <code>start value</code> to retrieve data from collection.
     * @param pageSize the<code>Size</code> of collection
     * @return a list of<code>Creative Insertions</code> by <code>campaign</code> id and <code>level</code>
     */
    @GET
    @Path("/{id}/creativeInsertions")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreativeInsertions(@PathParam("id") Long campaignId,
                                          @InjectParam CreativeInsertionFilterParam parentIds,
                                          @QueryParam("startIndex") Long startIndex,
                                          @QueryParam("pageSize") Long pageSize) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CREATIVE_INSERTION_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<CreativeInsertionView>> result =
                    creativeInsertionManager.getCreativeInsertions(campaignId, parentIds, startIndex, pageSize, oauthKey());
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    @GET
    @Path("/{id}/creativeInsertions/test")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreativeInsertionsForTesting(@PathParam("id") Long campaignId,
                                                    @QueryParam("startIndex") Long startIndex,
                                                    @QueryParam("pageSize") Long pageSize) {
        try {
            try {
                checkValidityOfToken();
                checkPermissions(AccessPermission.AUTOMATED_TEST);
                RecordSet<CreativeInsertionView> creativeInsertions = creativeInsertionManager.getAllCreativeInsertions(campaignId, startIndex, pageSize, oauthKey());
                return APIResponse.ok(creativeInsertions).build();
            } catch (SystemException e) {
                throw BusinessValidationUtil.parseToLegacyException(e);
            }
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + campaignId, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns a list if <code>Creative insertions</code> that match <code>search options</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CREATIVE_INSERTION_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a list of  <code>Creative insertions</code> that matches the <code>search options</code>
     * is retrieved
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaignId</code>: Is null</li>
     *     <li><code>pattern</code>: Is null</li>
     *     <li><code>filterParamIds</code>: Is null</li>
     *     <li><code>searchOptions</code>: Is null</li>
     * </ul>
     *
     * @HTTP 401 Unauthorized - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param campaignId the <code>Campaign</code> id.
     * @param parentIds the <code>parent</code> id.
     * @param searchOptions the <code>search</code> options.
     * @param pattern the <code>pattern</code> to search for.
     * @param startIndex the <code>start value</code> to retrieve data from collection.
     * @param pageSize the<code>Size</code> of collection.
     * @return a list of<code>Creative Insertions</code> that match the <code>searchOptions</code>,
     * <code>pattern</code>, <code>parentIds</code>, and  <code>campaign</code> id.
     */
    @GET
    @Path("/{id}/searchCreativeInsertions")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response searchCreativeInsertions(@PathParam("id") Long campaignId,
                                             @InjectParam CreativeInsertionFilterParam parentIds,
                                             @InjectParam CreativeInsertionSearchOptions searchOptions,
                                             @QueryParam("pattern") String pattern,
                                             @QueryParam("startIndex") Long startIndex,
                                             @QueryParam("pageSize") Long pageSize) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CREATIVE_INSERTION_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<CreativeInsertionView>> result =
                    creativeInsertionManager.searchCreativeInsertions(campaignId, parentIds,
                            searchOptions, pattern, startIndex, pageSize, oauthKey());
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn("Unexpected Exception while searching for Creative Insertions.", e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Delete an set of existing <code>Creative insertions</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>DELETE_CREATIVE_INSERTION</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the set of existing <code>Creative insertions</code> was deleted successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaignId</code>: Is null</li>
     *     <li><code>bulkDelete</code>: Is null</li>
     *     <li><code>bulkDelete</code>: Is empty</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     * <ul>
     *     <li>Payload creative insertion filter parameter validator has errors</li>
     * </ul>
     *
     * @param campaignId The <code>campaign</code> id.
     * @param bulkDelete <code>bulkDelete</code>
     * @return success message.
     */
    @PUT
    @Path("/{id}/creativeInsertionsBulkDelete")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response creativeInsertionsBulkDelete(@PathParam("id") Long campaignId,
                                                 RecordSet<CreativeInsertionFilterParam> bulkDelete) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.DELETE_CREATIVE_INSERTION);
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, String> result = 
                    creativeInsertionManager.creativeInsertionsBulkDelete(campaignId, bulkDelete,
                            oauthKey());
            
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(new SuccessResponse(result.success())).build();
            }

        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns metrics for <code>campaign</code> given its <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a record per day is received by <code>Campaign</code> id.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaignId</code>: Is null</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Campaign</code> id
     * @param startDate The <code>Start</code> date
     * @param endDate The <code>End</code> date
     * @return a <code>metrics</code> for <code>campaign</code> id from <code>start</code> date to
     * <code>end</code> date.
     */
    @GET
    @Path("/{id}/metrics")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getMetricsForCampaign(@PathParam("id") Long id,
                                          @QueryParam("startDate")Date startDate,
                                          @QueryParam("endDate")Date endDate) {
        try {
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<Metrics> result = metricsManager.getCampaignMetrics(id, startDate, endDate,
                    oauthKey());
            return APIResponse.ok(result).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns a list of<code>Placement</code> package
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CAMPAIGN_PACKAGE_PLACEMENT_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a list of <code>placement</code> package by <code>campaign</code> exists.
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaignId</code>: Is null</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Campaign</code> id.
     * @return a RecordSet of <code>PlacementPackage</code>
     */
    @GET
    @Path("/{id}/packagePlacementView")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPackagePlacementView(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CAMPAIGN_PACKAGE_PLACEMENT_LIST);
            RecordSet<PackagePlacementView> result = packagePlacementManager.getPackagePlacementViewByCampaign(
                    id, oauthKey());
            return APIResponse.ok(result).build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns metrics for <code>Creative</code> by <code>Campaign</code> id.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CREATIVE_METRICS</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When <code>creative</code> metrics matches the given <code>campaign</code> id.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaignId</code>: Is null</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Campaign</code> id.
     * @return creative metrics by <code>campaign</code> id.
     */
    @GET
    @Path("/{id}/creative/metrics")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getMetricsForCreativesbyCampaign(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CREATIVE_METRICS);
            RecordSet<Metrics> result = metricsManager.getCreativeMetricsByCampaign(id, oauthKey());
            return APIResponse.ok(result).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns top ten metrics for <code>Creative</code> by <code>Campaign</code> id.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CREATIVE_METRICS</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When <code>creative</code> metrics matches the given <code>campaign</code> id.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaignId</code>: Is null</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Campaign</code> id.
     * @return creative top ten  metrics by <code>campaign</code> id.
     */
    @GET
    @Path("/{id}/creative/metrics/topten")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getTopTenMetricsForCreativesbyCampaign(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CREATIVE_METRICS);
            RecordSet<Metrics> result = metricsManager.getTopTenCreativeMetricsByCampaign(id, oauthKey());
            return APIResponse.ok(result).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Exports <code>Creative</code> insertion by <code>Campaign</code> id.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>EXPORT_CREATIVE_INSERTIONS</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Campaign</code> id has updated
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaignId</code>: Is null</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Campaign</code> id.
     * @return a updated <code>Campaign</code> id.
     */
    @GET
    @Path("/{id}/export")
    public Response export(@PathParam("id") Long id,
                           @QueryParam("format") String formatType,
                           @QueryParam("type") String objectType) {
        try {
            checkValidityOfToken();
            if (objectType != null) {
                File result;

                switch (objectType) {
                    case ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW:
                        checkPermissions(AccessPermission.EXPORT_CREATIVE_INSERTIONS);
                        break;
                    case ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW:
                        checkPermissions(AccessPermission.EXPORT_MEDIA);
                        break;
                    default:
                        throw BusinessValidationUtil
                                .buildBusinessSystemException(BusinessCode.INVALID, objectType);
                }
                result = importExportManager
                        .exportByCampaign(id, objectType, formatType, oauthKey());
                return Response.ok(result).header("Content-Disposition",
                        "attachment; filename=" + result.getName()).header("Content-Type",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").header(
                        "Access-Control-Expose-Headers", "Content-Disposition").build();
            } else {
                throw BusinessValidationUtil
                        .buildBusinessSystemException(BusinessCode.INVALID, objectType);
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Upload file for <code>Campaign</code> id.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>IMPORT_CREATIVE_INSERTIONS, IMPORT_MEDIA</code>
     *     </li>
     * </ul>
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Is null</li>
     *     <li><code>objectType</code>: Is null</li>
     *     <li><code>inputStream</code>: Is null</li>
     *     <li><code>cdh</code>: Is null</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id <code>Campaign</code> id
     * @param objectType <code>Object type</code> to upload creative insertion or media
     * @param inputStream <code>File</code> to read <code>Campaign</code> id data.
     * @param cdh <code>Content</code> disposition.
     * @return A  <code>FileName</code> with UUID.
     */
    @POST
    @Path("/{id}/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response uploadExcelToTmpPath(@PathParam("id") Long id,
            @QueryParam("type") String objectType,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition cdh) {
        log.debug("Upload file for Campaign: campaignId -> {} ", id);

        try {
            checkValidityOfToken();

            if (objectType != null) {
                switch(objectType) {
                    case ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW:
                        checkPermissions(AccessPermission.IMPORT_CREATIVE_INSERTIONS);
                        break;
                    case ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW:
                        checkPermissions(AccessPermission.IMPORT_MEDIA);
                        break;
                    default:
                        throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INVALID, objectType);
                }
                // TODO: We need to update this manager method --> EITHER
                String result = importExportManager.uploadXLSXFileInTmp(id, objectType, inputStream, cdh, oauthKey());
                return APIResponse.created(new SuccessResponse(result), uriInfo, "Campaigns").build();
            } else {
                throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INVALID, objectType);
            }
        } catch (SystemException e) {
            log.warn("Error while the upload process file", e);
            throw new WebApplicationSystemException(e);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("Unexpected IOExcepion", e);
                }
            }
        }
    }

    /**
     * Imports data to update schedules if <code>objectType</code> is
     * EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW by given <code>Campaign</code> id.
     * Imports data to create Placements if <code>objectType</code> is EXPORT_IMPORT_TYPE_MEDIA_VIEW
     * by given <code>Campaign</code> id.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>IMPORT_CREATIVE_INSERTIONS, IMPORT_MEDIA</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Size</code> that matches the given <code>id</code> exists
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaignId</code>: Is null</li>
     *     <li><code>uuid</code>: Is null</li>
     *     <li><code>ignoreErrors</code>: Is null</li>
     * </ul>
     *
     * @HTTP 200 OK - When a number of placements successfully messages is retrieved
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param campaignId <the <code>Campaign</code> id.
     * @param actions
     * @param objectType <code>Object type</code> to upload creative insertion or media
     * @param ignoreErrors
     * @param uuid
     * @return number <code>placements</code> were successfully imported.
     */
    @PUT
    @Path("/{id}/import")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response importExcelFromTmpPath(@PathParam("id") Long campaignId,
                                           RecordSet<Action> actions,
                                           @QueryParam("type") String objectType,
                                           @QueryParam("ignoreErrors") Boolean ignoreErrors,
                                           @QueryParam("uuid") String uuid) {
        log.debug("Finalize the import for Campaign: campaignId -> {} ", campaignId);

        try {
            checkValidityOfToken();

            if (objectType != null) {
                Either<ImportExportErrors, SuccessResponse> result;

                switch(objectType) {
                    case ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW:
                        checkPermissions(AccessPermission.IMPORT_CREATIVE_INSERTIONS);
                        result = importExportManager.importToUpdateSchedulesFromXLSXFile(
                                campaignId, uuid, ignoreErrors != null ? ignoreErrors : false,
                                oauthKey());
                        break;
                    case ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW:
                        checkPermissions(AccessPermission.IMPORT_MEDIA);
                        result = importExportManager.importToCreatePlacementsFromXLSXFile(
                                campaignId, uuid, ignoreErrors != null ? ignoreErrors : false,
                                actions != null ? actions.getRecords() : null, oauthKey());
                        break;
                    default:
                        throw BusinessValidationUtil.buildBusinessSystemException(
                                BusinessCode.INVALID, objectType);
                }
                if(result.isSuccess()){
                    return APIResponse.ok(result.success()).build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity(result.error()).build();
                }
            } else {
                throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INVALID, objectType);
            }
        } catch (SystemException e) {
            log.warn("Error while the upload process file", e);
            throw new WebApplicationSystemException(e);
        }
    }

    @GET
    @Path("/{id}/issues")
    public Response exportIssuesFromImport(@PathParam("id") Long campaignId,
                                           @QueryParam("format") String formatType,
                                           @QueryParam("type") String objectType,
                                           @QueryParam("uuid") String uuid) {
        try {
            checkValidityOfToken();
            if (objectType != null) {
                switch(objectType) {
                    case ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW:
                        checkPermissions(AccessPermission.EXPORT_CREATIVE_INSERTIONS);
                        break;
                    case ImportExportManager.EXPORT_IMPORT_TYPE_MEDIA_VIEW:
                        checkPermissions(AccessPermission.EXPORT_MEDIA_ISSUES);
                        break;
                    default:
                        throw BusinessValidationUtil
                                .buildBusinessSystemException(BusinessCode.INVALID, objectType);
                }
                File file = importExportManager.exportIssuesOfImportByCampaign(campaignId,
                        objectType, formatType, uuid, oauthKey());
                return Response.ok(file)
                        .header("Content-Disposition", "attachment; filename=" + file.getName())
                        .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .header("Access-Control-Expose-Headers", "Content-Disposition")
                        .build();
            } else {
                throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INVALID, objectType);
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns trafficking site contacts by <code>Campaign</code> id.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_SITE_CONTACTS_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a list of <code>Site Contacts</code> that matches the given <code>Campaign</code> id is retrieved
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaign</code>: Is null</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs.
     *
     * @param campaignId <<the <code>Campaign</code> id.
     * @return A list of <code>Site contacts</code>.
     */
    @GET
    @Path("/{id}/siteContacts")
    public Response getTraffickingSiteContacts(@PathParam("id") Long campaignId) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_SITE_CONTACTS_LIST);
            
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<SiteContactView>> either = 
                    siteManager.getTraffickingSiteContacts(campaignId, oauthKey());
            if(either.isError()) {
                return handleErrorCodes(either.error());
            } else {
                return Response.ok(either.success()).build();
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Creates one or more CreativeInsertions in bulk.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_CREATIVE_INSERTION</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 SAVED - the <code>Creative Insertion</code> was saved successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>creativeInsertions</code>: Is null</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs.
     *
     * @param campaignId the <code>Campaign</code> id.
     * @param creativeInsertions The List of <code>CreativeInsertion</code> object to create.
     * @return CreativeInsertion saved
     */
    @POST
    @Path("/{id}/bulkCreativeInsertion")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createCreativeInsertionInBulk(@PathParam("id") Long campaignId, BulkCreativeInsertion creativeInsertions) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_CREATIVE_INSERTION);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, BulkCreativeInsertion> result =
                    creativeInsertionManager.bulkCreate(creativeInsertions, oauthKey(), campaignId);

            if (result.isSuccess()) {
                return APIResponse.created(result.success(), uriInfo, "CreativeInsertions").build();
            } else {
                trueffect.truconnect.api.commons.exceptions.business.Error error = result.error().getErrors().get(0);
                if (error instanceof DuplicateCIError) {
                    return Response.status(Response.Status.CONFLICT).entity(result.error()).build();
                }
                return handleErrorCodes(error);
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     *  Get a Record Set of <code>Placements</code> in order to create new creative insertions
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_PLACEMENT_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a list of <code>placement</code> for given <code>campaign</code> id exists.
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param campaignId The <code>Size</code> id
     * @param filterParam The <code>Filter parameters</code> to retrieve the list.
     * @return a list of <code>placements</code> by <code>campaign</code> id that matches the
     * <code>filter parameters</code>.
     */
    @GET
    @Path("/{id}/creativeInsertions/placements")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPlacementsByFilterParam(@PathParam("id") Long campaignId, @InjectParam CreativeInsertionFilterParam filterParam) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_PLACEMENT_LIST);

            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, RecordSet<PlacementView>> result =
                    placementManager.getPlacementsByCreativeInsertionFilterParam(campaignId,
                            filterParam, oauthKey());
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn("Unexpected Exception while getting for Placements.", e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns a RecordSet of <code>CreativeGroupCreativeView</code> in order to create new
     * Creative Insertions
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CREATIVE_GROUP_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a recordSet of <code>CreativeGroupCreativeView</code> that matches the
     * given <code>Campaign</code> id is retrieved
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param campaignId The <code>Size</code> id
     * @return a <code>Size</code>
     */
    @GET
    @Path("/{id}/creativeInsertions/groupCreatives")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getGroupCreativesByFilterParam(@PathParam("id") Long campaignId, @InjectParam CreativeInsertionFilterParam filterParam) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CREATIVE_GROUP_LIST);

            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, RecordSet<CreativeGroupCreativeView>> result =
                    creativeGroupManager.getGroupCreativesByCreativeInsertionFilterParam(campaignId, filterParam, oauthKey());
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn("Unexpected Exception while getting for Creative Group Creatives.", e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns a list of <code>Packages</code> related with a <code>CampaignId</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_PACKAGE_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 When the <code>Placements</code> were got successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>ioId</code>: Not empty</li>
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an authentication error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Access Token Invalid</li>
     * </ul>

     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Permission Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @param campaignId the ID of the campaign.
     * @param ioId the ID of the InsertionOrder.
     * @param startIndex the starIndex from where to process
     * @param pageSize the page size
     * @return a RecordSet of <code>Package</code>
     */
    @GET
    @Path("/{id}/packages")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPackages(@PathParam("id") Long campaignId,
                                @QueryParam("ioId") Long ioId,
                                @QueryParam("startIndex") Long startIndex,
                                @QueryParam("pageSize") Long pageSize) {
        log.debug("Get Packages for Campaign = {}", campaignId);
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_PACKAGE_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, RecordSet<Package>>
                    result = packageManager.getPackagesByCampaignAndIoId(
                    campaignId, ioId, startIndex, pageSize, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }
}
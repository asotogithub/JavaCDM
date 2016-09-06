package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.BulkPublisherSiteSectionSize;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.model.dto.HtmlInjectionTagAssociationDTO;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.dto.UserView;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.model.htmlinjection.AdChoicesHtmlInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.CustomInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.FacebookCustomTrackingInjectionType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AdmTransactionDao;
import trueffect.truconnect.api.crud.dao.AgencyDao;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.DatasetConfigDao;
import trueffect.truconnect.api.crud.dao.DimCostDetailDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.HtmlInjectionTagsDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.MetricsDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.dao.SiteDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.AdmTransactionImpl;
import trueffect.truconnect.api.crud.dao.impl.AdvertiserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.AgencyDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.DatasetConfigDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ExtendedPropertiesDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.HtmlInjectionTagsDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderStatusDaoImpl;
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
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.dim.DimPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.edw.MetricsPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.AgencyManager;
import trueffect.truconnect.api.crud.service.BulkPublisherSiteSectionSizeManager;
import trueffect.truconnect.api.crud.service.HtmlInjectionTagsManager;
import trueffect.truconnect.api.crud.service.MetricsManager;
import trueffect.truconnect.api.crud.service.PlacementManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;
import trueffect.truconnect.api.external.proxy.TruQProxy;

import com.sun.jersey.api.core.InjectParam;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
 * Set of REST Services related to <code>Agency</code> management.
 *
 * @author Richard Jaldin
 */
@Api(value = "Agencies", description = "Agency service")
@Path("/Agencies")
public class AgencyService extends GenericService {

    private AgencyManager agencyManager;
    private BulkPublisherSiteSectionSizeManager bulkPublisherSiteSectionSizeManager;
    private MetricsManager metricsManager;
    private PlacementManager placementManager;
    private HtmlInjectionTagsManager htmlInjectionTagsManager;

    public AgencyService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        PersistenceContextMyBatis edwContext = new MetricsPersistenceContextMyBatis();
        PersistenceContextMyBatis dimContext = new DimPersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        DatasetConfigDao datasetConfigDao = DatasetConfigDaoImpl.instanceFromResources();
        AdmTransactionDao admTransactionDao = AdmTransactionImpl.instanceFromResources();
        AgencyDao agencyDao = new AgencyDaoImpl(context);
        DimCostDetailDao dimCostDetailDao = new DimCostDetailDaoImpl(dimContext);
        PackageDaoBase dimPackageDao = new DimPackageDaoImpl(dimContext);
        agencyManager = new AgencyManager(agencyDao, datasetConfigDao,
                admTransactionDao, new AdvertiserDaoImpl(context), dimCostDetailDao,
                dimPackageDao, accessControl);

        UserDao userDao = new UserDaoImpl(context);
        PublisherDao publisherDao = new PublisherDaoImpl(context);
        SiteDao siteDao = new SiteDaoImpl(context);
        ExtendedPropertiesDao extendedPropertiesDao = new ExtendedPropertiesDaoImpl(context);
        SiteSectionDao siteSectionDao = new SiteSectionDaoImpl(context);
        SizeDao sizeDao = new SizeDaoImpl(context);
        HtmlInjectionTagsDao HtmlInjectionTagsDao = new HtmlInjectionTagsDaoImpl(context);
        CreativeInsertionDao creativeInsertionDao = new CreativeInsertionDaoImpl(context);
        bulkPublisherSiteSectionSizeManager = new BulkPublisherSiteSectionSizeManager(userDao,
                publisherDao, siteDao, extendedPropertiesDao, siteSectionDao, sizeDao, accessControl);

        MetricsDao metricsDaoForUserValidation = new MetricsDaoImpl(context);
        MetricsDao metricsDaoForEDW = new MetricsDaoImpl(edwContext);
        metricsManager = new MetricsManager(metricsDaoForUserValidation, metricsDaoForEDW, accessControl);

        PlacementDao placementDao = new PlacementDaoImpl(context);
        CostDetailDaoExtended placementCostDetail = new CostDetailDaoImpl(CostDetailType.PLACEMENT, context);
        CampaignDao campaignDao = new CampaignDaoImpl(context);
        PlacementStatusDao placementStatusDao = new PlacementStatusDaoImpl(context);
        InsertionOrderDao ioDao = new InsertionOrderDaoImpl(context);
        InsertionOrderStatusDao ioStatusDao = new InsertionOrderStatusDaoImpl(context);
        PackageDaoExtended packageDao = new PackageDaoImpl(context);
        PackagePlacementDaoExtended packagePlacementDao = new PackagePlacementDaoImpl(context);
        CostDetailDaoBase dimPlacementCostDetail = new CostDetailDaoImpl(CostDetailType.PLACEMENT, dimContext);

        placementManager = new PlacementManager(placementDao, placementCostDetail, campaignDao,
                siteSectionDao, sizeDao, placementStatusDao, userDao, extendedPropertiesDao, ioDao,
                ioStatusDao, packageDao, packagePlacementDao, dimPlacementCostDetail,
                creativeInsertionDao, accessControl);

        htmlInjectionTagsManager = new HtmlInjectionTagsManager(HtmlInjectionTagsDao, placementDao,
                siteSectionDao, campaignDao, userDao, accessControl, new TruQProxy(headers));
    }

    public AgencyService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Returns an agency based on its ID
     *
     * @param id the ID of the agency.
     * @return the Agency
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getAgency(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Agency agency = agencyManager.get(id, oauthKey());
            return APIResponse.ok(agency).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Saves the Agency.
     *
     * @param agency The Agency to be saved.
     * @return The Agency saved on the database.
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveAgency(Agency agency) {
        try {
            rolesAllowed("ROLE_APP_ADMIN");
            agency = agencyManager.save(agency, oauthKey());
            log.info(oauthKey().toString() + " Saved " + agency);
            return APIResponse.created(agency, uriInfo, "Agencies").build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.conflict(CrudApiExceptionHandler.getMessage(e));
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + agency, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an Agency.
     *
     * @param id the ID of the agency
     * @param agency The Agency to be updated.
     *
     * @return The Agency updated on the database.
     */
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateAgency(@PathParam("id") Long id, Agency agency) {
        try {
            rolesAllowed("ROLE_APP_ADMIN");
            agency = agencyManager.update(agency, id, oauthKey());
            log.info(oauthKey().toString() + " Updated " + id);
            return APIResponse.ok(agency).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.conflict(CrudApiExceptionHandler.getMessage(e));
        } catch (ConflictException e) {
            log.warn("Conflict", e);
            return APIResponse.conflict(e.getMessage(), uriInfo, "Agencies").build();

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Delete physically all the data related to the Agency.
     *
     * @param id The ID of the agency
     * @return the Agency that has been deleted.
     */
    @DELETE
    @Path("/{id}/physical")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response hardRemoveAgency(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_API_TEST_ACCESS");
            SuccessResponse result = agencyManager.hardRemove(id, oauthKey());
            log.info(oauthKey().toString() + " Deleted " + id);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns the list of Campaigns for an Agency.
     *
     * @param id the ID of the Agency.
     * @return
     */
    @GET
    @Path("/{id}/campaigns")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}/campaigns", notes="Get set of Campaigns for an Agency.",
                  response=CampaignDTO.class, responseContainer="List")
    @ApiResponses(value={ @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Advertiser found OR Access Token belongs to a User that does not have access to the requested data") })
    public Response getCampaignsForAgency(@PathParam("id") Long id) {
        try {
            try {
                checkValidityOfToken();
                checkPermissions(AccessPermission.VIEW_CAMPAIGN_LIST);
            } catch (SystemException e) {
                throw BusinessValidationUtil.parseToLegacyException(e);
            }
            RecordSet<CampaignDTO> result = agencyManager.getCampaigns(id, oauthKey());
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Campaigns not found for {}", oauthKey().getUserId(), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.error("Unexpected Exception, reason: ", e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a list of  Metrics for an Agency in the format of a Metrics Objects.
     *
     * @param agencyId the ID of the Agency.
     * @return The metrics associated with the Agency
     */
    @GET
    @Path("/{agencyId}/metrics/{type}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getMetricsForAgency(@PathParam("agencyId") Long agencyId,
                                        @PathParam("type")String type,
                                        @QueryParam("startDate")Date startDate,
                                        @QueryParam("endDate")Date endDate) {
        try {
            if(!type.equalsIgnoreCase("campaigns")){
                throw new SystemException("type of[" + type +"] not valid", ValidationCode.INVALID);
            }

            checkValidityOfToken();
            if (type.equals("campaigns")) checkPermissions(AccessPermission.VIEW_CAMPAIGN_METRICS);

            RecordSet<Metrics> result = metricsManager.getAgencyMetrics(agencyId, startDate,
                    endDate, oauthKey(), agencyManager.getCampaigns(agencyId, oauthKey()));
            return APIResponse.ok(result).build();

        } catch (Exception e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    @POST
    @Path("/bulkPublisherSiteSectionSize")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveBulkPublisherSiteSectionSize(BulkPublisherSiteSectionSize bulkPublisherSiteSectionSize,
                                                     @QueryParam("ignoreDupSite") Boolean ignoreDupSite) {
        try {
            try {
                checkValidityOfToken();
                checkPermissions(AccessPermission.CREATE_BULK_PUBLISHER_SITE_SECTION_SIZE);
            } catch (SystemException e) {
                throw BusinessValidationUtil.parseToLegacyException(e);
            }
            // call to the right manager
            bulkPublisherSiteSectionSize = bulkPublisherSiteSectionSizeManager.createBulk(
                    bulkPublisherSiteSectionSize,
                    ignoreDupSite,
                    oauthKey());
            return APIResponse.created(bulkPublisherSiteSectionSize).build();
        }  catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString(), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (ConflictException e) {
            log.warn("Conflict Error", e);
            return APIResponse.conflict(e.getMessage());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns Users based on Agency's ID
     *
     * @param id the ID of the agency.
     * @return the Users
     */
    @GET
    @Path("/{id}/traffickingUsers")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getUsers(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_TRAFFICKING_USER_LIST);
            RecordSet<UserView> result = userManager.getUserView(id, oauthKey());
            return APIResponse.ok(result).build();
        } catch (SystemException e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    @GET
    @Path("/{id}/datasets")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getADMDatasetConfigs(@PathParam("id") Long agencyId) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_ADM_DATASET_CONFIG_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<DatasetConfigView>>
                    datasets = agencyManager.getDatasets(agencyId, oauthKey());
            if(datasets.isError()) {
                return handleErrorCodes(datasets.error());
            } else {
                return APIResponse.ok(datasets.success()).build();
            }
        } catch (Exception e) {
            log.warn("Error while retrieving datasets for agency.  AgencyId:" + agencyId, e);
            throw new WebApplicationSystemException(e);
        }
    }
    
    @POST
    @Path("/{id}/metrics")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response insertMetrics(@PathParam("id") Long id, RecordSet<Metrics> metricsList) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.AUTOMATED_TEST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<Metrics>>
                    result = metricsManager.insertMetrics(id, metricsList.getRecords(), oauthKey());
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

    @GET
    @Path("/{id}/placementView")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPlacementsByPlacementFilterParam(@PathParam("id") Long agencyId,
                                                        @QueryParam("advertiserId") Long advertiserId,
                                                        @QueryParam("brandId") Long brandId,
                                                        @InjectParam PlacementFilterParam filterParam,
                                                        @QueryParam("startIndex") Long startIndex,
                                                        @QueryParam("pageSize") Long pageSize) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_TAG_PLACEMENT_LIST);
            Either<Errors, RecordSet<PlacementView>> result = placementManager.
                    getPlacementsByPlacementFilterParam(agencyId, advertiserId, brandId,
                            filterParam, startIndex, pageSize, oauthKey());
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

    @GET
    @Path("/{id}/searchPlacementView")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response searchPlacementsByPlacementFilterParam(@PathParam("id") Long agencyId,
                                                           @QueryParam("advertiserId") Long advertiserId,
                                                           @QueryParam("brandId") Long brandId,
                                                           @QueryParam("pattern") String pattern,
                                                           @InjectParam PlacementSearchOptions searchOptions,
                                                           @QueryParam("startIndex") Long startIndex,
                                                           @QueryParam("pageSize") Long pageSize) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_TAG_PLACEMENT_LIST);
            Either<Errors, RecordSet<PlacementView>> result =
                    placementManager.searchPlacementsByPattern(
                            agencyId, advertiserId, brandId, pattern, searchOptions, startIndex,
                            pageSize, oauthKey());
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

    /**
     * Returns the list of Tags for an Agency.
     *
     * @param id the ID of the Agency.
     * @return
     */
    @GET
    @Path("/{id}/htmlInjectionTags")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getHtmlInjectionTagsForAgency(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_HTML_INJECTION_TAG_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<HtmlInjectionTags>> result
                    = htmlInjectionTagsManager.getHtmlInjectionTagsForAgency(id, oauthKey());
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

    /**
     * Returns the list of Tags for an Agency.
     *
     * @param agencyId the ID of the Agency.
     * @param filterParam
     * @param startIndex
     * @param pageSize
     * @return
     */
    @GET
    @Path("/{id}/htmlInjectionTagAssociation")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getAssociationsHtmlInjectionTags(@PathParam("id") Long agencyId,
                                                     @InjectParam PlacementFilterParam filterParam,
                                                     @QueryParam("startIndex") Long startIndex,
                                                     @QueryParam("pageSize") Long pageSize) {

        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_HTML_INJECTION_TAG_LIST);
            Either<Errors, HtmlInjectionTagAssociationDTO> result =
                    htmlInjectionTagsManager.getAssociationsByPlacementFilterParam(
                            agencyId, filterParam, startIndex, pageSize, oauthKey());
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

    @POST
    @Path("/{id}/htmlInjectionTypeAdChoices")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createHtmlInjectionFromType(@PathParam("id") Long id,
                                                AdChoicesHtmlInjectionType htmlInjectionType) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_HTML_INJECTION_TAG);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, HtmlInjectionTags>
                    result =
                    htmlInjectionTagsManager.createTagFromType(id, htmlInjectionType, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.status(Response.Status.CREATED).entity(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    @POST
    @Path("/{id}/htmlInjectionTypeFacebook")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createHtmlInjectionFromType(@PathParam("id") Long id,
                                                FacebookCustomTrackingInjectionType htmlInjectionType) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_HTML_INJECTION_TAG);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, HtmlInjectionTags>
                    result =
                    htmlInjectionTagsManager.createTagFromType(id, htmlInjectionType, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.status(Response.Status.CREATED).entity(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Creates a new <code>HtmlInjectionTags</code> under the given <code>id</code> of <code>Agency</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_HTML_INJECTION_TAG</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 Created - When the <code>HtmlInjectionTags</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>htmlInjectionType</code>: Not  null</li>
     *     <li><code>htmlInjectionType.name</code>: Not null</li>
     *     <li><code>htmlInjectionType.tagContent</code>: Not exceed limit defined in <code>Constants.HTML_INJECTION_HTML_CONTENT_LENGTH</code></li>
     *     <li><code>htmlInjectionType.tagContent</code>: Not proper HTML content</li>
     *     <li><code>htmlInjectionType.secureTagContent</code>: Not exceed limit defined in <code>Constants.HTML_INJECTION_HTML_CONTENT_LENGTH</code></li>
     *     <li><code>htmlInjectionType.secureTagContent</code>: Not proper HTML content</li>
     *     <li>Absence of <code>htmlInjectionType.tagContent</code> and <code>htmlInjectionType.secureTagContent</code></li>
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Permission Denied</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request, or:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     *     <li>No <code>Agency</code> was found for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param htmlInjectionType HTMLInjectionTag to be saved.
     * @return A new <code>HTMLInjectionTag</code> saved on the database.
     */
    @POST
    @Path("/{id}/htmlInjectionTypeCustom")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createHtmlInjectionFromType(@PathParam("id") Long id,
                                                CustomInjectionType htmlInjectionType) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_HTML_INJECTION_TAG);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, HtmlInjectionTags>
                    result =
                    htmlInjectionTagsManager.createTagFromType(id, htmlInjectionType, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.status(Response.Status.CREATED).entity(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Creates one or more <code>Htmi Injection Tag Association</code> in bulk.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_HTML_INJECTION_TAG_ASSOCIATION</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 Created - When the <code>Html Tag Association</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>advertiserId</code>: Not empty</li>
     *     <li><code>brandId</code>: Not empty</li>
     *     <li><code>bulkCreate</code>: Not null</li>
     *     <li><code>bulkCreate</code>: Not empty</li>
     *     <li><code>levelType</code>: Not empty</li>
     *     <li><code>htmlInjectionId</code>: Not empty</li>
     *     <li><code>all</code>: Can be 'true' for 'campaign' or 'site' levelType</li>
     *     <li><code>campaignId</code>: Campaign must be related to the 'advertiserId' and 'brandId'</li>
     *     <li><code>siteId</code>: Site must be related to a 'placement' from 'campaign'</li>
     *     <li><code>sectionId</code>: Section must be related to a 'placement' from 'campaign' and 'site'</li>
     *     <li><code>placementId</code>: Placement must be related to a from 'campaign', 'site' and 'section'</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the query parameters or payload data</li>
     * </ul>
     *
     * @param agencyId The <code>Agency</code> id.
     * @param advertiserId The <code>Advertiser</code> id.
     * @param brandId The <code>Brand</code> id.
     * @param bulkAction A RecordSet of<code>PlacementCreateTagAssocParam</code> to process.
     * @return success message.
     */
    @PUT
    @Path("/{id}/htmlInjectionTagAssociationsBulk")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response bulkAssociationsTagPlacementsActions(@PathParam("id") Long agencyId,
                                                        @QueryParam("advertiserId") Long advertiserId,
                                                        @QueryParam("brandId") Long brandId,
                                                        PlacementActionTagAssocParam bulkAction) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_HTML_INJECTION_TAG_ASSOCIATION);
            Either<Errors, String> result = htmlInjectionTagsManager.
                    placementActionAssociationsBulk(agencyId, advertiserId, brandId, bulkAction,
                            oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(new SuccessResponse(result.success())).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }
}

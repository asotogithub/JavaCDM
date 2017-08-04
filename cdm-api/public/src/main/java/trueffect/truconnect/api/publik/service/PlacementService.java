package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.model.Error;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.delivery.Tag;
import trueffect.truconnect.api.commons.model.delivery.TagEmail;
import trueffect.truconnect.api.commons.model.delivery.TagPlacement;
import trueffect.truconnect.api.commons.model.delivery.TagEmailResponse;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.HtmlInjectionTagsDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ExtendedPropertiesDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.HtmlInjectionTagsDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderStatusDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackageDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackagePlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementStatusDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteSectionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SizeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.dim.DimPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.HtmlInjectionTagsManager;
import trueffect.truconnect.api.crud.service.PackageManager;
import trueffect.truconnect.api.crud.service.PlacementManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;
import trueffect.truconnect.api.external.proxy.TagProxy;
import trueffect.truconnect.api.external.proxy.TagEmailProxy;
import trueffect.truconnect.api.external.proxy.TagTypeProxy;
import trueffect.truconnect.api.external.proxy.TruQProxy;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import com.sun.jersey.api.core.InjectParam;

import java.util.Arrays;

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

/**
 * Set of REST Services related to <code>Placement</code> management.
 *
 * @author Richard Jaldin
 * @author Gustavo Claure
 * @author Thomas Barjou
 */
@Path("/Placements")
public class PlacementService extends GenericService {

    private PlacementManager placementManager;
    private PackageManager packageManager;
    private HtmlInjectionTagsManager htmlInjectionTagsManager;

    public PlacementService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        PersistenceContextMyBatis dimContext = new DimPersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        PlacementDao placementDao = new PlacementDaoImpl(context);
        CostDetailDaoExtended placementCostDetailDao = new CostDetailDaoImpl(CostDetailType.PLACEMENT, context);
        CampaignDao campaignDao = new CampaignDaoImpl(context);
        SiteSectionDao siteSectionDao = new SiteSectionDaoImpl(context);
        SizeDao sizeDao = new SizeDaoImpl(context);
        PackageDaoExtended packageDao = new PackageDaoImpl(context);
        PackagePlacementDaoExtended packagePlacementDao = new PackagePlacementDaoImpl(context);
        PlacementStatusDao placementStatusDao = new PlacementStatusDaoImpl(context);
        UserDao userDao = new UserDaoImpl(context);
        ExtendedPropertiesDao extendedPropertiesDao = new ExtendedPropertiesDaoImpl(context);
        InsertionOrderDao insertionOrderDao = new InsertionOrderDaoImpl(context);
        InsertionOrderStatusDao insertionOrderStatusDao = new InsertionOrderStatusDaoImpl(context);
        CreativeInsertionDao creativeInsertionDao = new CreativeInsertionDaoImpl(context);

        CostDetailDaoBase dimPlacementCostDetailDao = new DimCostDetailDaoImpl(CostDetailType.PLACEMENT, dimContext);

        this.placementManager = new PlacementManager(placementDao, placementCostDetailDao,
                campaignDao, siteSectionDao, sizeDao, placementStatusDao, userDao,
                extendedPropertiesDao, insertionOrderDao, insertionOrderStatusDao,
                packageDao, packagePlacementDao, dimPlacementCostDetailDao, creativeInsertionDao,
                accessControl);

        this.packageManager = PackageService.getPackageManagerInstance();

        HtmlInjectionTagsDao htmlInjectionTagsDao = new HtmlInjectionTagsDaoImpl(context);
        this.htmlInjectionTagsManager =
                new HtmlInjectionTagsManager(htmlInjectionTagsDao, placementDao, siteSectionDao,
                        campaignDao, userDao, accessControl, new TruQProxy(headers));
    }

    /**
     * Returns a <code>Placement</code> given its <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Placement</code> that matches the given <code>id</code> exists
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
     * @param id The <code>Placement</code> id
     * @return a <code>Placement</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPlacement(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Placement placement = placementManager.get(id, oauthKey());
            return APIResponse.ok(placement).build();
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
     * Returns a <code>RecordSet</code> of one or multiple <code>Placement</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Placement</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>Search Criteria parameters are incorrect</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request, or:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     *     <li>No record was found for the given parameter(s)</li>
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param searchCriteria {@link SearchCriteria} that provides the filtering criteria
     * @return a {@link RecordSet<Placement>} with the Placements found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPlacements(@InjectParam SearchCriteria searchCriteria) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<Placement> records = placementManager.getByCriteria(searchCriteria,
                    oauthKey());
            return APIResponse.ok(records).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + searchCriteria, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (SearchApiException e) {
            log.warn("Error on the Search query.", e);
            return APIResponse.bad(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>Placement</code> with the given <code>Site</code> and <code>SiteSection</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>Placement</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>siteId</code>: Not empty</li>
     *     <li><code>siteSectionId</code>: Not empty</li>
     *     <li><code>ioId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 256.</li>
     *     <li><code>rateType</code>: Invalid value</li>
     *     <li><code>startDate</code>: Must be before <code>endDate</code></li>
     * </ul>
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
     * </ul>
     *
     * @HTTP 409 Conflict - When an attempt to create a <code>Site</code>
     * with same name, same publisher and same agency.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param placement <code>Placement</code> to create
     * @return A new <code>Placement</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createPlacement(Placement placement) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            placement = placementManager.create(placement, oauthKey());
            log.info(oauthKey().toString() + " Saved " + placement.toString());
            return APIResponse.created(placement, uriInfo, "Placements/" + placement.getId()).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ConflictException e) {
            log.warn("Conflict: ", e);
            return APIResponse.conflict(e.getMessage(), uriInfo, "Placements").build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + placement.toString(), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a <code>RecordSet</code> of one or multiple <code>Placement</code> with the given <code>Package</code> and <code>InsertionOrder</code>
     * 
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_BULK_PLACEMENT</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>Placement</code> were created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>siteId</code>: Not empty</li>
     *     <li><code>siteSectionId</code>: Not empty</li>
     *     <li><code>ioId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 256.</li>
     *     <li><code>rateType</code>: Invalid value</li>
     *     <li><code>startDate</code>: Must be before <code>endDate</code></li>
     * </ul>
     * 
     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Permission Denied</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param placements a {@link RecordSet<Placement>} to create
     * @param ioId The <code>InsertionOrder</code> id
     * @param packageId The <code>Package</code> id
     * @return A new <code>Placement</code> created given the provided parameters
     */
    @POST
    @Path("/bulk")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createPlacements(@QueryParam("ioId") Long ioId,
            @QueryParam("packageId") Long packageId,
            RecordSet<Placement> placements) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_BULK_PLACEMENT);
            //call manager to save data
            RecordSet<Placement> result = placementManager.addNewPlacementsToPackage(packageId, ioId, placements, oauthKey());
            log.info(oauthKey().toString() + " Updated " + placements);
            return APIResponse.ok(result, uriInfo, "Placements/").build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * <p>
     *   Updates an existing <code>Placement</code> based on its <code>id</code>. The Placement can be
     *   updated in different ways according to its relationship to a Package.
     * </p>
     *
     * <h4>Placement Properties to Update</h4>
     *
     * <h5>Placement below a Package</h5>
     *
     * <h6>Without Schedules</h6>
     *
     * <ul>
     *     <li>Association to Package</li>
     *     <li><code>name</code></li>
     *     <li><code>status</code></li>
     *     <li><code>width</code> and <code>height</code></li>
     * </ul>
     *
     * <h6>With Schedules</h6>
     *
     * <ul>
     *     <li>Association to Package</li>
     * </ul>

     * <h5>Standalone Placement</h5>
     *
     * <h6>Without Schedules</h6>
     *
     * <ul>
     *     <li><code>name</code></li>
     *     <li><code>status</code></li>
     *     <li><code>width</code> and <code>height</code></li>
     *     <li>Cost Details</li>
     * </ul>
     *
     * <h6>With Schedules</h6>
     *
     * <ul>
     *     <li><code>-</code></li>
     * </ul>
     *
     * <h4>Security</h4>
     *
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Placement</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>siteId</code>: Not empty</li>
     *     <li><code>siteSectionId</code>: Not empty</li>
     *     <li><code>ioId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 256.</li>
     *     <li><code>rateType</code>: Invalid value</li>
     *     <li><code>startDate</code>: Must be before <code>endDate</code></li>
     * </ul>
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
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param id The <code>Placement</code> id
     * @param placement <code>Placement</code> file
     * @return A <code>Placement</code> updated given the provided parameters
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updatePlacement(@PathParam("id") Long id, Placement placement) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            placement = placementManager.update(id, placement, oauthKey());
            log.info(oauthKey().toString() + " Updated " + placement);
            return APIResponse.ok(placement, uriInfo, "Placements/" + placement.getId()).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (NotFoundException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(e.getMessage()).build();
        } catch (ConflictException e) {
            log.warn("Conflict: ", e);
            return APIResponse.conflict(e.getMessage(), uriInfo, "Placements/" + id).build();
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
     * Disassociates a given <code>placementId</code> from its related <code>Package</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Permissions Allowed:  <code>UPDATE_PLACEMENT</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the given <code>placementId</code> was successfully disassociated from
     * its <code>Package</code>.
     *
     * @HTTP 400 Bad Request - When a data model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>placementId</code>: Not blank</li>
     *     <li><code>placementId</code>: Must be a positive number</li>
     *     <li><code>placementId</code>: Should have a Package associated.
     *     Standalone Placements will throw an error as they don't have Package relationship</li>
     *     <li><code>placementId</code>: Should not be the last Placement for its Package</li>
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Permission to this API endpoint is denied</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Placement</code> id
     * @return A <code>Void</code> when the given <code>placementId</code> can be
     * disassociated from its <code>Package</code>
     */
    @PUT
    @Path("/{id}/disassociateFromPackage")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response disassociateFromPackage(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_PLACEMENT);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result =
                    packageManager.disassociateFromPackage(id, oauthKey());
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                String message = ResourceBundleUtil.getString(
                        "packagePlacementImport.info.disassociateSuccess",
                        id);
                return Response.ok(new SuccessResponse(message)).build();
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Updates a <code>RecordSet</code> of one or multiple <code>Placement</code> status with the given <code>InsertionOrder</code>
     * 
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>UPDATE_PLACEMENTS_STATUS</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Placement</code> have been updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>status</code>: Not empty</li>
     *     <li><code>status</code>: Invalid value</li>
     * </ul>
     * 
     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Permission Denied</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param placements a {@link RecordSet<Placement>} to create
     * @param ioId The <code>InsertionOrder</code> id
     * @return A jersey <code>Response</code> 
     */
    @PUT
    @Path("/status")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updatePlacementStatus(@QueryParam("ioId") Long ioId, RecordSet<Placement> placements) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_PLACEMENTS_STATUS);
            placements = placementManager.updatePlacementStatus(ioId, placements, oauthKey());
            log.info("Placements status related to ioId = {} updated by User with key = {}", ioId, oauthKey().toString());
            return APIResponse.ok(placements, uriInfo, "Placements/").build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Deletes a <code>Placement</code> given its <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Placement</code> that matches the given <code>id</code> has been successfully deleted 
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
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Placement</code> id
     * @return a Jersey <code>Response</code>
     */
    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response removePlacement(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            placementManager.remove(id, oauthKey());
            log.info(oauthKey().toString() + " Deleted " + id);
            return APIResponse.ok().build();
        } catch (AccessDeniedException e) {
            log.info("Access Denied: ", e);
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
     * Returns a <code>RecordSet</code> of one or multiple <code>Tag</code> given the <code>Placement</code> and <code>TagType</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Tag</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>Search Criteria parameters are incorrect</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request, or:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     *     <li>No record was found for the given parameter(s)</li>
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Placement</code> id
     * @param tagTypeId The <code>TagType</code> id
     * @return a {@link RecordSet<Tag>} with the Tags found
     */
    @GET
    @Path("/{id}/Tag/{tagTypeId}")
    public Response getTagId(@PathParam("id") Long id, @PathParam("tagTypeId") Long tagTypeId) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");

            placementManager.isValidForAgency(id, oauthKey());
            TagProxy proxy = new TagProxy(headers);
            proxy.path("GetTag");
            proxy.query("tagId", tagTypeId.toString());
            proxy.query("placementId", id.toString());
            String aux = proxy.getTagString();
            Tag tag = new Tag(tagTypeId, aux);
            return APIResponse.ok(tag).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ProxyException ex) {
            log.info("Proxy: " + ex);
            Error error = new Error(ex.getMessage(),
                    ApiValidationUtils.EXTERNAL_SERVICE_ERROR);
            Errors errors = new Errors(Arrays.asList(error));
            return Response.status(ex.getStatus()).entity(errors).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>Response</code> by given <code>TagEmail</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>SEND_AD_TAGS_BY_EMAIL</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Reponse</code> by given <code>TagEmail</code> is retrieved successfully.
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
     *     <li>No record was found for the given parameter(s)</li>
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param te The <code>TagEmail</code> id
     * @return a <code>Response</code> with the status of the operation.
     */
    @POST
    @Path("/sendTagEmail")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response sendAdTagEmail(TagEmail te) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.SEND_AD_TAGS_BY_EMAIL);
            
            TagEmailProxy proxy = new TagEmailProxy(headers);
            
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, TagEmailResponse> result =
                    placementManager.sendTagEmails(te, proxy, oauthKey());
            
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn("Unexpected Exception while sending AdTag email.", e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>HtmlInjectionTags</code> given the <code>Placement</code> and <code>TagType</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_HTML_INJECTION_TAG_PLACEMENT_ASSOCIATED_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>HtmlInjectionTags</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>Search Criteria parameters are incorrect</li>
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
     *     <li>No record was found for the given parameter(s)</li>
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param placementId The <code>Placement</code> id
     * @param startIndex The starting index of the response recordSet
     * @param pageSize The page size of the response recordSet
     * @return a {@link RecordSet<HtmlInjectionTags>} with the HtmlInjectionTags found
     */
    @GET
    @Path("/{id}/htmlInjectionTags")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getTagsByPlacementId(@PathParam("id") Long placementId,
                                         @QueryParam("startIndex") Long startIndex,
                                         @QueryParam("pageSize") Long pageSize) {
        log.debug("Get HtmlInjectionTags by placementId {}", placementId);
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_HTML_INJECTION_TAG_PLACEMENT_ASSOCIATED_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, RecordSet<HtmlInjectionTags>>
                    result = htmlInjectionTagsManager.getTagsByPlacementId(placementId, startIndex,
                    pageSize, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }

        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Deletes one or multiple HTML Injection Tags ids contained in a <code>RecordSet</code> that
     * are related to a given <code>Placement</code> id
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>DELETE_HTML_TAG_INJECTION_ASSOCIATION</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>HtmlInjectionTags</code> ids could
     * be deleted successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple validation errors in the given query
     * parameters or body payload. Validations rules are:
     * <ul>
     *     <li><code>RecordSet</code> of ids should not be empty</li>
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
     *     <li>No record was found for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param placementId The <code>Placement</code> id
     * @param tagIds The <code>RecordSet</code> of HTML Tag Injection Ids
     * @return a <code>SuccessResponse</code> with a success message
     */
    @PUT
    @Path("/{id}/deleteHtmlInjectionTagsBulk")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response deletePlacementHtmlTagInjectionAssocBulk(@PathParam("id") Long placementId,
                                                             RecordSet<Long> tagIds) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.DELETE_HTML_INJECTION_TAG_ASSOCIATION);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, String> result =
                    htmlInjectionTagsManager.deletePlacementHtmlTagInjectionAssocBulk(
                            placementId, tagIds, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(new SuccessResponse(result.success())).build();
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }
    
    /**
     * Returns a <code>TagPlacement</code> with the Ad Tags given the <code>Placement</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_AD_TAG_DETAILS</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>TagPlacement</code> is retrieved successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>Placement is incorrect</li>
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
     *     <li>No record was found for the given parameter(s)</li>
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param placementId The <code>Placement</code> id
     * @return a {@link TagPlacement} with the Ad Tag data.
     */
    @GET
    @Path("/{id}/getAdTagsByPlacement")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getAdTagsByPlacement(@PathParam("id") Long placementId) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_AD_TAG_DETAILS);
            
            TagProxy tagProxy = new TagProxy(headers);
            TagTypeProxy tagTypeProxy = new TagTypeProxy(headers);
            
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, TagPlacement> result =
                    placementManager.getAdTagPlacement(placementId, tagProxy, tagTypeProxy, oauthKey());
            
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn("Unexpected Exception while sending AdTag email.", e);
            throw new WebApplicationSystemException(e);
        }
    }
}

package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeDTO;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupCreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ExtendedPropertiesDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.CreativeGroupManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;

import com.sun.jersey.api.core.InjectParam;

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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Set of REST Services related to <code>CreativeGroup</code> management.
 *
 * @author Richard Jaldin
 * @author Thomas Barjou
 */
@Api(value = "CreativeGroups", description = "Creative Group service")
@Path("/CreativeGroups")
public class CreativeGroupService extends GenericService {

    private CreativeGroupManager creativeGroupManager;

    public CreativeGroupService() {
        super();
        PersistenceContext context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        CreativeGroupDao creativeGroupDao = new CreativeGroupDaoImpl(context);
        CreativeGroupCreativeDao creativeGroupCreativeDao = new CreativeGroupCreativeDaoImpl(context);
        CreativeInsertionDao creativeInsertionDao = new CreativeInsertionDaoImpl(context);
        CreativeDao creativeDao = new CreativeDaoImpl(context);
        ExtendedPropertiesDao extendedPropertiesDao = new ExtendedPropertiesDaoImpl(context);
        creativeGroupManager = new CreativeGroupManager(creativeGroupDao,
                creativeGroupCreativeDao,
                creativeInsertionDao,
                creativeDao,
                new CampaignDaoImpl(context),
                new UserDaoImpl(context, accessControl),
                extendedPropertiesDao,
                accessControl);
    }

    public CreativeGroupService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Returns an <code>CreativeGroup</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>CreativeGroup</code> that matches the given <code>id</code> exists
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
     *     <li>No record was found for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>CreativeGroup</code> id
     * @return a <code>CreativeGroup</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}", notes="Get a Creative Group.", response=CreativeGroup.class)
    @ApiResponses({
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative Group found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getCreativeGroup(@PathParam("id") Long id) {
        log.info("Starting GET service, input data: id = " + id);
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            CreativeGroup creativeGroup = creativeGroupManager.get(id, oauthKey());
            log.info("Finishing GET service, data retrieved successfully");
            return APIResponse.ok(creativeGroup).build();
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
     * Returns a <code>RecordSet</code> of one or multiple <code>CreativeGroup</code>
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
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>CreativeGroup</code> is retrieved successfully.
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
     * @return a {@link RecordSet<CreativeGroup>} with the CreativeGroup found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/", notes="Return one or more Creative Groups based on search criteria.",
                  response=CreativeGroup.class, responseContainer="List")
    @ApiResponses({@ApiResponse(code=400, message="Bad request: one or more errors in search criteria"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative Group found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getCreativeGroups(@InjectParam SearchCriteria searchCriteria) {
        log.info("Starting GET CreativeGroups service, input data: " + searchCriteria);
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<CreativeGroup> records = creativeGroupManager.get(searchCriteria, oauthKey());
            ResponseBuilder resp;
            resp = APIResponse.ok(records);
            log.info("Finishing GET CreativeGroups service, data retrieved successfully");
            return resp.build();
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
     * Creates a new <code>CreativeGroup</code> with the given <code>Campaign</code>.
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
     * @HTTP 201 CREATED - When the <code>CreativeGroup</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>campaignId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 256.</li>
     *     <li><code>name</code>: Must be unique</li>
     *     <li><code>rotationType</code>: Not empty</li>
     *     <li><code>doDaypartTargeting</code>: Must have a <code>daypartTarget</code> if enabled</li>
     *     <li><code>doGeoTargeting</code>: Must have a <code>geoTarget</code> if enabled</li>
     *     <li><code>doCookieTargeting</code>: Must have a <code>cookieTarget</code> if enabled</li>
     *     <li><code>isDefault</code>: Does not accept any targeting if <code>isDefault</code> = true</li>
     *     <li><code>enableFrequencyCap</code>: Must have a <code>frequencyCap</code> and a <code>frequencyCapWindow</code> if enabled</li>
     *     <li><code>frequencyCap</code>: Must be a positive number</li>
     *     <li><code>frequencyCapWindow</code>: Must be valid value between [0-999]</li>
     *     <li><code>weight</code>: Must be valid value between [0-100]</li>
     * </ul>

     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request, or:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     * 
     * @HTTP 409 Conflict - When an attempt to create a <code>CreativeGroup</code>
     * with same name, and same Campaign.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param creativeGroup <code>CreativeGroup</code> to create
     * @return A new <code>CreativeGroup</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/", notes="Create a new Creative Group with the given Campaign.",
                  response=CreativeGroup.class)
    @ApiResponses({@ApiResponse(code=201, message="Created successfully"),
            @ApiResponse(code=400, message="Bad request: one or more errors such as empty payload, empty campaignId, empty name, etc."),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative Group found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response saveCreativeGroup(@ApiParam(required=true) CreativeGroup creativeGroup) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            creativeGroup = creativeGroupManager.save(creativeGroup, oauthKey());
            ResponseBuilder resp = APIResponse.created(creativeGroup, uriInfo, "CreativeGroups");
            log.info("Finishing POST service, data saved successfully");
            return resp.entity(creativeGroup).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (ConflictException e) {
            log.warn("Conflict Error: ", e);
            return APIResponse.conflict(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + creativeGroup, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>CreativeGroup</code>.
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
     * @HTTP 200 OK - When the <code>CreativeGroup</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>campaignId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 256.</li>
     *     <li><code>name</code>: Must be unique</li>
     *     <li><code>rotationType</code>: Not empty</li>
     *     <li><code>doDaypartTargeting</code>: Must provide a <code>daypartTarget</code> if enabled</li>
     *     <li><code>doGeoTargeting</code>: Must provide a <code>geoTarget</code> if enabled</li>
     *     <li><code>doCookieTargeting</code>: Must provide a <code>cookieTarget</code> if enabled</li>
     *     <li><code>isDefault</code>: Does not accept any targeting if <code>isDefault</code> = true</li>
     *     <li><code>enableFrequencyCap</code>: Must provide a <code>frequencyCap</code> and a <code>frequencyCapWindow</code> if enabled</li>
     *     <li><code>frequencyCap</code>: Must be a positive number</li>
     *     <li><code>frequencyCapWindow</code>: Must be valid value between [0-999]</li>
     *     <li><code>weight</code>: Must be valid value between [0-100]</li>
     * </ul>

     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request, or:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     * 
     * @HTTP 409 Conflict - When an attempt to update a <code>CreativeGroup</code>
     * with same name, and same Campaign.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param id The <code>CreativeGroup</code> id
     * @param creativeGroup <code>CreativeGroup</code> to update
     * @return A <code>CreativeGroup</code> updated given the provided parameters
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}", notes="Update an existing Creative Group.",
                  response=CreativeGroup.class)
    @ApiResponses({@ApiResponse(code=400, message="Bad request: one or more errors such as empty payload, empty id, empty campaignId, empty name, etc."),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative Group found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response updateCreativeGroup(@ApiParam(required=true) @PathParam("id") Long id,
                                        @ApiParam(required=true) CreativeGroup creativeGroup) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            creativeGroup = creativeGroupManager.update(id, creativeGroup, oauthKey());
            ResponseBuilder resp = APIResponse.ok(creativeGroup);
            log.info("Finishing PUT service, data saved successfully");
            return resp.build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (ConflictException e) {
            log.warn("Conflict: ", e);
            return APIResponse.conflict(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Deletes an <code>CreativeGroup</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>CreativeGroup</code> that matches the given <code>id</code> has been successfully deleted 
     *
     * @HTTP 401 Unauthorized - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
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
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>CreativeGroup</code> id
     * @param recursiveDelete The flag to delete also the CreativeGroup's Creatives 
     * @return a <code>Response</code>
     */
    @DELETE
    @Path("/{id}")
    @SuppressWarnings("unused")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}", notes="Delete an existing Creative Group.",
                  response=CreativeGroup.class)
    @ApiResponses({@ApiResponse(code=200, message="Successful"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative Group found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response removeCreativeGroup(@ApiParam(required=true) @PathParam("id") Long id, @QueryParam("recursiveDelete") Boolean recursiveDelete) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            SuccessResponse result = creativeGroupManager.remove(id, oauthKey(), recursiveDelete);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Bad Request", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>CreativeGroupCreative</code>
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
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>CreativeGroupCreative</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
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
     * @param id of the <code>CreativeGroup</code> containing the <code>CreativeGroupCreative</code>
     * @return a {@link RecordSet<CreativeGroupCreative>} for the CreativeGroup found
     */
    @GET
    @Path("/{id}/creatives")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}/creatives", notes="Return one or more Creative Groups Creatives that are associated with the CreativeGroup.",
                  response=CreativeGroupCreative.class, responseContainer="List")
    @ApiResponses({@ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative Group found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getCreativeGroupCreatives(@PathParam("id") Long id) {
        log.info("Starting GET CreativeGroupCreatives");
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<CreativeGroupCreative> records = creativeGroupManager.getCreativeGroupCreativesByCreativeGroup(id, oauthKey());
            ResponseBuilder resp = APIResponse.ok(records);
            log.info("Finishing GET CreativeGroupCreatives");
            return resp.build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (NotFoundException e) {
            log.warn("Not found: ", e);
            return APIResponse.notFound(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>CreativeGroup</code> by updating relations with <code>Creatives</code>.
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
     * @HTTP 200 OK - When the <code>CreativeGroup</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a <code>CreativeGroupCreative</code> validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>creativeGroupId</code>: Not empty</li>
     *     <li><code>creativeGroupId</code>: Must be the same as the url's id</li>
     *     <li><code>creativeId</code>: Not empty</li>
     *     <li><code>creativeId</code>: Must be from the same Campaign as CreativeGroup</li>
     * </ul>

     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request, or:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param id The <code>CreativeGroup</code> id
     * @param creativeGroupCreative <code>CreativeGroupCreative</code> with the new list of CreativeGroupCreatives
     * @return a <code>CreativeGroupCreative</code> with the updated list of Creatives.
     */
    @PUT
    @Path("/{id}/creatives")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}/creatives", notes="Update an existing Creative Group by updating relationships with Creatives.",
                  response=CreativeGroupCreative.class)
    @ApiResponses({@ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative Group found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response updateCreativeGroupCreativesList(@ApiParam(required=true) @PathParam("id") Long id,
                                                     @ApiParam(required=true) CreativeGroupCreative creativeGroupCreative) {
        log.info("Starting PUT CreativeGroups by Creative service, input data: id = " + id);
        CreativeGroupCreative cgc = null;
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            cgc = creativeGroupManager.updateCreativeGroupCreatives(id, creativeGroupCreative, oauthKey());
            log.info("Finishing PUT CreativeGroups by Creative service, data retrieved successfully");
            return APIResponse.ok(cgc).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Bad Request", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>CreativeGroup</code> by adding relations with <code>Creatives</code>.
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
     * @HTTP 200 OK - When the <code>CreativeGroup</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a <code>CreativeGroupCreativeDTO</code> validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>creativeGroupIds</code>: Not empty</li>
     *     <li><code>creativeGroupIds</code>: Must be from the same Campaign as Creatives</li>
     *     <li><code>Creative</code> id: Not empty</li>
     *     <li><code>Creative</code> id: Must be from the same Campaign as the CreativeGroups</li>
     * </ul>

     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request, or:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param associations <code>CreativeGroupCreativeDTO</code> with the list of <code>CreativeGroup</code> and <code>Creative</code>
     * @return a <code>CreativeGroupCreativeDTO</code> with the updated list of <code>CreativeGroup</code> and <code>Creative</code>
     */
    @PUT
    @Path("/createAssociations")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/createAssociations", notes="Update existing Creative Groups by adding relations with Creatives; each Creative is added to each Creative Group.")
    @ApiResponses({@ApiResponse(code=200, message="Successful"),
            @ApiResponse(code=400, message="Bad request: payload empty, no campaignId, no creativeGroupIds, no Creatives, invalid Creatives"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative Group found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response createAssociations(@ApiParam(required=true) CreativeGroupCreativeDTO associations) {
        try {
            // TODO: we need to change this in order to use permissions
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            //call manager to create associations
            Either<Errors, String> either =
                    creativeGroupManager.createAssociations(associations, oauthKey());
            if (either.isError()) {
                return handleErrorCodes(either.error());
            } else {
                return Response.ok(new SuccessResponse(either.success())).build();
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }
}

package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionBulkUpdate;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupCreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.CreativeInsertionManager;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * Set of REST Services related to <code>CreativeInsertion</code> management.
 *
 * @author Richard Jaldin, Thomas Barjou
 */
@Path("/CreativeInsertions")
public class CreativeInsertionService extends GenericService {

    private CreativeInsertionManager creativeInsertionManager;
    private PersistenceContextMyBatis context;
    private AccessControl accessControl;

    public CreativeInsertionService() {
        context = new PersistenceContextMyBatis();
        accessControl = new AccessControlImpl(context);
        CreativeInsertionDao creativeInsertionDao = new CreativeInsertionDaoImpl(context);
        PlacementDao placementDao = new PlacementDaoImpl(context);
        CampaignDao campaignDao = new CampaignDaoImpl(context);
        CreativeDao creativeDao = new CreativeDaoImpl(context);
        CreativeGroupDao creativeGroupDao = new CreativeGroupDaoImpl(context);
        CreativeGroupCreativeDao creativeGroupCreativeDao = new CreativeGroupCreativeDaoImpl(context);
        creativeInsertionManager = new CreativeInsertionManager(creativeInsertionDao,
                campaignDao, placementDao, creativeDao, creativeGroupDao, creativeGroupCreativeDao,
                accessControl);
    }

    public CreativeInsertionService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>CreativeInsertion</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>CreativeInsertion</code> is retrieved successfully.
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
     * @HTTP 404 Not Found - When no data is found for the request:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     *     <li>No record was found for the given parameter(s)</li>
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param searchCriteria {@link SearchCriteria} that provides the filtering criteria
     * @return a {@link RecordSet<CreativeInsertion>} with the CreativeInsertion found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreativeInsertions(@InjectParam SearchCriteria searchCriteria) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            RecordSet<CreativeInsertion> records = creativeInsertionManager.get(searchCriteria, oauthKey());
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
     * Returns a <code>CreativeInsertion</code> given its <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CreativeInsertion</code> that matches the given <code>id</code> exists
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
     * @param id The <code>CreativeInsertion</code> id
     * @return a <code>CreativeInsertion</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreativeInsertion(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            CreativeInsertion insertion = creativeInsertionManager.get(id, oauthKey());
            return APIResponse.ok(insertion).build();
        } catch (AccessDeniedException e) {
            log.warn("Access denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn(String.format("Data not found for %s : %s", oauthKey().toString(), id), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (NotFoundException e) {
            log.warn("Data not found", e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>CreativeInsertion</code> with the given <code>Advertiser</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed: <code>ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed: <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>CreativeInsertion</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>campaignId</code>: Not empty</li>
     *     <li><code>creativeGroupId</code>: Not empty</li>
     *     <li><code>creativeId</code>: Not empty</li>
     *     <li><code>placementId</code>: Not empty</li>
     *     <li><code>placementId</code>: Placement must have 'Accepted' status</li>
     *     <li><code>startDate</code>: Must be before <code>endDate</code></li>
     *     <li><code>weight</code>: Must be a whole number between [0,10.000]</li>
     *     <li><code>clickthrough</code>: Must start with either http:// or https:// and must be a well formed URL</li>
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
     * </ul>
     * 
     * @HTTP 409 Conflict - When an attempt to create a <code>CreativeInsertion</code>
     * with same Placement, Creative and CreativeGroup.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs.
     *
     * @param creativeInsertion <code>CreativeInsertion</code> to create
     * @return A new <code>CreativeInsertion</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveCreativeInsertion(CreativeInsertion creativeInsertion) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            creativeInsertion = creativeInsertionManager.create(creativeInsertion, oauthKey());
            ResponseBuilder response = Response.status(Status.CREATED);
            response = response.location(getLocation(Long.toString(creativeInsertion.getId())));
            log.info("The Creative Insertion = {} was successfully saved for key = {} ", creativeInsertion, oauthKey().toString());
            return APIResponse.created(creativeInsertion, uriInfo, "CreativeInsertions").build();
        } catch (AccessDeniedException e) {
            log.info("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn(String.format("Data not found for %s : %s", oauthKey().toString(), creativeInsertion), e);
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
     * Updates a bulk of existing <code>CreativeInsertion</code>
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>UPDATE_CREATIVE_INSERTION</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CreativeInsertion</code> are updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>campaignId</code>: Not empty</li>
     *     <li><code>creativeGroupId</code>: Not empty</li>
     *     <li><code>creativeId</code>: Not empty</li>
     *     <li><code>placementId</code>: Not empty</li>
     *     <li><code>startDate</code>: Must be before <code>endDate</code></li>
     *     <li><code>weight</code>: Must be a whole number between [0,10.000]</li>
     *     <li><code>clickthrough</code>: Must start with either http:// or https:// and must be a well formed URL</li>
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
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param creativeInsertionBulkUpdate <code>CreativeInsertionBulkUpdate</code> to process
     * @return a <code>Response</code>
     */
    @PUT
    @Path("/bulkUpdate")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response bulkUpdateCreativeInsertion(CreativeInsertionBulkUpdate creativeInsertionBulkUpdate) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_CREATIVE_INSERTION);
            CreativeInsertionBulkUpdate result = creativeInsertionManager.bulkUpdate(creativeInsertionBulkUpdate, oauthKey());
            return APIResponse.ok(result).build();
        } catch (Exception e) {
            log.warn("Exception, reason: ", e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Updates an existing <code>CreativeInsertion</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed: <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CreativeInsertion</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>campaignId</code>: Not empty</li>
     *     <li><code>creativeGroupId</code>: Not empty</li>
     *     <li><code>creativeId</code>: Not empty</li>
     *     <li><code>placementId</code>: Not empty</li>
     *     <li><code>startDate</code>: Must be before <code>endDate</code></li>
     *     <li><code>weight</code>: Must be a whole number between [0,10.000]</li>
     *     <li><code>clickthrough</code>: Must start with either http:// or https:// and must be a well formed URL</li>
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
     * </ul>
     * 
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param id The <code>CreativeInsertion</code> id
     * @param creativeInsertion <code>CreativeInsertion</code> to update
     * @return A <code>CreativeInsertion</code> updated given the provided parameters
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateCreativeInsertion(@PathParam("id") Long id, CreativeInsertion creativeInsertion) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            if (id.compareTo(creativeInsertion.getId()) != 0) {
                return APIResponse.bad("Identifier in URL does not match resource in request body.").build();
            }
            creativeInsertion = creativeInsertionManager.update(id, creativeInsertion, oauthKey());
            return APIResponse.ok(creativeInsertion).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn(String.format("Data not found for %s : %s", oauthKey().toString(), id), e);
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
     * Deletes a <code>CreativeInsertion</code> given its <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CreativeInsertion</code> that matches the given <code>id</code> has been successfully deleted 
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
     * @param id The <code>CreativeInsertion</code> id
     * @return a <code>Response</code>
     */
    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response removeCreativeInsertion(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            SuccessResponse result = creativeInsertionManager.remove(id, oauthKey());
            log.info(oauthKey().toString() + " Deleted " + id);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn(String.format("Data not found for %s : %s", oauthKey().toString(), id), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Deletes one or many <code>CreativeInsertion</code> given its <code>creativeGroupId</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CreativeInsertion</code> that matches the given <code>creativeGroupId</code> have been successfully deleted 
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
     * @return a <code>Response</code>
     */
    @DELETE
    @Path("/{id}/byCreativeGroupId")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response removeCreativeInsertionByCgId(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            SuccessResponse result = creativeInsertionManager.removeByCreativeGroupId(id, oauthKey());
            log.info(oauthKey().toString() + " Deleted " + id);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn(String.format("Data not found for %s : %s", oauthKey().toString(), id), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>CreativeInsertion</code> given its <code>creativeGroupId</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>CreativeInsertion</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the creativeGroupId
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
     * @param id of the <code>CreativeGroup</code> containing the <code>CreativeInsertion</code>
     * @return a {@link RecordSet<CreativeInsertion>} for the CreativeGroup found
     */
    @GET
    @Path("/{id}/byCreativeGroupId")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getByCreativeGroupId(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            RecordSet<CreativeInsertion> records = creativeInsertionManager.getByCreativeGroupId(id, oauthKey());
            return APIResponse.ok(records).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn(String.format("Data not found for %s : %s", oauthKey().toString(), id), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }
}

package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.PublisherDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.PublisherManager;
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

/**
 * Set of REST Services related to <code>Publisher</code> management.
 *
 * @author Richard Jaldin
 * @author Thomas Barjou
 */
@Path("/Publishers")
public class PublisherService extends GenericService {

    private PublisherManager publisherManager;

    public PublisherService() {
        PersistenceContext context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        PublisherDao publisherDao = new PublisherDaoImpl(context);
        publisherManager = new PublisherManager(publisherDao, accessControl);
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>Publisher</code>
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
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Publisher</code> is retrieved successfully.
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
     * @HTTP 500 Internal Server Error - When an unknown error occurs.
     *
     * @param searchCriteria {@link SearchCriteria} that provides the filtering criteria.
     * @return a {@link RecordSet<Publisher>} with the Publishers found.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPublishers(@InjectParam SearchCriteria searchCriteria) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<Publisher> records = publisherManager.getPublishers(searchCriteria, oauthKey());
            return APIResponse.ok(records).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ searchCriteria, e);
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
     * Returns a <code>Publisher</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Publisher</code> that matches the given <code>id</code> exists
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
     * @HTTP 500 Internal Server Error - When an unknown error occurs.
     *
     * @param id The <code>Publisher</code> id
     * @return a <code>Publisher</code>
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response get(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Publisher result = publisherManager.get(id, oauthKey());
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (NotFoundException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ id, e);
            return APIResponse.notFound(e.getMessage()).build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>Publisher</code> with the given <code>Agency</code>.
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
     * @HTTP 201 CREATED - When the <code>Publisher</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>agencyId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 256.</li>
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
     * @HTTP 409 Conflict - When an attempt to create a <code>Publisher</code>
     * with same name and same agency.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs.
     *
     * @param publisher <code>Publisher</code> to create
     * @return A new <code>Publisher</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response save(Publisher publisher) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            publisher = publisherManager.create(publisher, oauthKey());
            log.info(oauthKey().toString() + " Saved "+ publisher);
            return APIResponse.created(publisher, uriInfo, "Publishers/" + publisher.getId()).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ",e);
            return APIResponse.forbidden(e.getMessage());
        } catch (NotFoundException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ publisher, e);
            return APIResponse.notFound(e.getMessage()).build();
        } catch (ConflictException e) {
            log.warn("Conflict: ",e);
            return APIResponse.conflict(e.getMessage(), uriInfo, "Publishers").build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ publisher, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>Publisher</code>.
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
     * @HTTP 200 OK - When the <code>Publisher</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>agencyId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 256.</li>
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
     * @HTTP 409 Conflict - When an attempt to update a <code>Publisher</code>
     * with same name and same Agency.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs.
     *
     * @param id The <code>Publisher</code> id
     * @param publisher <code>Publisher</code> file
     * @return A <code>Publisher</code> updated given the provided parameters
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response update(@PathParam("id") Long id, Publisher publisher) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            publisher = publisherManager.update(id, publisher, oauthKey());
            log.info(oauthKey().toString()+ " Updated "+id);
            return APIResponse.ok(publisher).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (NotFoundException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ id, e);
            return APIResponse.notFound(e.getMessage()).build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ConflictException e) {
            log.warn("Conflict: ", e);
            return APIResponse.conflict(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Deletes a <code>Publisher</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Publisher</code> that matches the given <code>id</code> has been successfully deleted 
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
     * @param id The <code>Publisher</code> id
     * @param agencyId The <code>Agency</code> id 
     * @return a Jersey <code>Response</code>
     */
    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response remove(@PathParam("id") Long id,
            @QueryParam("agencyId") Long agencyId) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            SuccessResponse result = publisherManager.remove(id, agencyId, oauthKey());
            log.info(oauthKey().toString()+ " Deleted "+ id);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }
}

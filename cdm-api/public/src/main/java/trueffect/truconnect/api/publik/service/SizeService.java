package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.SizeDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.SizeManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Set of REST Services related to <code>Size</code> management.
 * 
 * @author Gustavo Claure
 * @author Marcelo Heredia
 */
@Path("/Sizes")
public class SizeService extends GenericService {

    private SizeManager sizeManager;

    public SizeService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        SizeDao sizeDao = new SizeDaoImpl(context);
        this.sizeManager = new SizeManager(sizeDao, accessControl);
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>Size</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed: <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Size</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>Search Criteria parameters are incorrect</li>
     * </ul>

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
     * @param searchCriteria {@link SearchCriteria} that provides the filtering criteria
     * @return a {@link RecordSet<Size>} with the Sizes found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSizes(@InjectParam SearchCriteria searchCriteria) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<Size> records = sizeManager.get(searchCriteria, oauthKey());
            return APIResponse.ok(records).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (SearchApiException e) {
            log.warn("Error on the Search query", e);
            return APIResponse.bad(CrudApiExceptionHandler.getMessage(e)).build();
        }
        catch (Exception e) {
            log.info(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>Size</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Size</code> that matches the given <code>id</code> exists
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
     *     <li>No record was found for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Size</code> id
     * @return a <code>Size</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response get(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Size size = sizeManager.get(id, oauthKey());
            return APIResponse.ok(size).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (NotFoundException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ id, e);
            return APIResponse.notFound(e.getMessage()).build();
        } catch (Exception e) {
            log.error("There was an internal error.", e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>Size</code> with the given <code>width</code> and <code>height</code>.
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
     * @HTTP 201 CREATED - When the <code>Size</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>agencyId</code>: Not empty</li>
     *     <li><code>width</code>: Valid number between 0 and 9999</li>
     *     <li><code>height</code>: Valid number between 0 and 9999</li>
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
     * @HTTP 409 Conflict - When an attempt to create a <code>Size</code> with a given
     * <code>width</code> and <code>height</code> that already exists.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param size <code>Size</code> to create
     * @return A new <code>Size</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response save(Size size) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            size = sizeManager.create(size, oauthKey());
            log.info(oauthKey().toString()+ " Saved "+size);
            return APIResponse.created(size, uriInfo, "Sizes").build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+size, e);
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
            return APIResponse.error(e.getMessage());
        }
    }
}

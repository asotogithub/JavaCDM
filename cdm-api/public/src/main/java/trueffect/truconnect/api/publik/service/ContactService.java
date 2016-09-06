package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.SiteContact;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.ContactDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.ContactDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.ContactManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;

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

/**
 * Set of REST Services related to <code>Contact</code> management.
 *
 * @author Richard Jaldin
 * @author Thomas Barjou
 */
@Path("/Contacts")
public class ContactService extends GenericService {

    private ContactManager contactManager;
    private PersistenceContextMyBatis context;
    private AccessControl accessControl;

    public ContactService() {
        context = new PersistenceContextMyBatis();
        accessControl = new AccessControlImpl(context);
        ContactDao contactDao = new ContactDaoImpl(context);

        this.contactManager = new ContactManager(contactDao, accessControl);
    }

    public ContactService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Returns a <code>Contact</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Contact</code> that matches the given <code>id</code> exists
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
     * @param id The <code>Contact</code> id
     * @return a <code>Contact</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getContact(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Contact contact = contactManager.getContact(id, oauthKey());
            return APIResponse.ok(contact).build();
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
     * Returns a <code>Contact</code> given its <code>User</code>
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
     * @HTTP 200 OK - When the <code>Contact</code> that matches the given <code>User</code> exists
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
     * @return a <code>Contact</code>
     */
    @GET
    @Path("/ByUser")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getContactByUser() {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Contact result = contactManager.getContactByUser(oauthKey());
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString(), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>Contact</code> with the given <code>Publisher</code>.
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
     * @HTTP 201 CREATED - When the <code>Contact</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>firstName</code>: Not empty</li>
     *     <li><code>firstName</code>: Supports characters up to 200.</li>
     *     <li><code>lastName</code>: Not empty</li>
     *     <li><code>lastName</code>: Supports characters up to 200.</li>
     *     <li><code>address1</code>: Supports characters up to 256.</li>
     *     <li><code>address2</code>: Supports characters up to 256.</li>
     *     <li><code>email</code>: Supports characters up to 256.</li>
     *     <li><code>notes</code>: Supports characters up to 2000.</li>
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
     * @param contact <code>Contact</code> to create
     * @return A new <code>Contact</code> created given the provided parameters
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveContact(Contact contact) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            contact = contactManager.create(contact, oauthKey());
            log.info(oauthKey().toString() + " Saved " + contact);
            return APIResponse.created(contact, uriInfo, "Contacts").build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + contact, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.conflict(CrudApiExceptionHandler.getMessage(e));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>Contact</code>.
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
     * @HTTP 200 OK - When the <code>Contact</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>firstName</code>: Not empty</li>
     *     <li><code>firstName</code>: Supports characters up to 200.</li>
     *     <li><code>lastName</code>: Not empty</li>
     *     <li><code>lastName</code>: Supports characters up to 200.</li>
     *     <li><code>address1</code>: Supports characters up to 256.</li>
     *     <li><code>address2</code>: Supports characters up to 256.</li>
     *     <li><code>email</code>: Supports characters up to 256.</li>
     *     <li><code>notes</code>: Supports characters up to 2000.</li>
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
     * @param id The <code>Contact</code> id
     * @param contact <code>Contact</code> to update
     * @return A <code>Contact</code> updated given the provided parameters
     */
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateContact(@PathParam("id") Long id, Contact contact) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");

            contact = contactManager.update(id, contact, oauthKey());
            log.info(oauthKey().toString() + " Updated " + id);
            return APIResponse.ok(contact).build();
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
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Deletes a <code>Contact</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Advertiser</code> that matches the given <code>id</code> has been successfully deleted 
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
     * @param id The <code>Contact</code> id
     * @return a <code>Response</code>
     */
    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response removeContact(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            SuccessResponse result = contactManager.delete(id, oauthKey());
            log.info(oauthKey().toString() + " Deleted " + id);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.conflict(CrudApiExceptionHandler.getMessage(e));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>SiteContact</code> with the given <code>SiteContact</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_SITE_CONTACT</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>SiteContact</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>siteid</code>: Not empty</li>
     *     <li><code>contactId</code>: Not empty</li>
     *     <li><code>typeId</code>: Not empty</li>
     * </ul>
     * 
     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Permission Denied</li>
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
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param agencyContact <code>AgencyContact</code> to create
     * @return A new <code>Response</code> given the provided parameters
     */
    @POST
    @Path("/AgencyContactRef")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response addAgencyContactRef(AgencyContact agencyContact) {
        log.info("Relationship Agency - Contact: " + agencyContact);
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            agencyContact = contactManager.addAgencyContactRef(agencyContact, oauthKey());
            log.info(oauthKey().toString() + " Saved " + agencyContact);
            return APIResponse.ok(agencyContact).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + agencyContact, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.conflict(CrudApiExceptionHandler.getMessage(e));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Removes the association between <code>Agency</code> and <code>Contact</code> 
     * given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Advertiser</code> that matches the given <code>id</code> exists
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
     * @param contactId The <code>Contact</code> id
     * @param agencyId The <code>Agency</code> id
     * @return a <code>Response</code>
     */
    @DELETE
    @Path("/AgencyContactRef")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response removeAgencyContactRefs(@QueryParam("contactId") Long contactId,
            @QueryParam("agencyId") Long agencyId) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            SuccessResponse result = contactManager.removeAgencyContactRefs(contactId, agencyId, oauthKey());
            log.info(oauthKey().toString() + " Deleted Association between" + contactId + " and " + agencyId);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + contactId + " or " + agencyId, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.conflict(CrudApiExceptionHandler.getMessage(e));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>SiteContact</code> with the given <code>SiteContact</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_SITE_CONTACT</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>SiteContact</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>siteId</code>: Not empty</li>
     *     <li><code>contactId</code>: Not empty</li>
     *     <li><code>typeId</code>: Not empty</li>
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
     * @param siteContact <code>SiteContact</code> to create
     * @return A new <code>Response</code> given the provided parameters
     */
    @POST
    @Path("/siteContactRef")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response addSiteContactRef(SiteContact siteContact) {
        log.info("Add relationship Site - Contact: {}", siteContact);
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_SITE_CONTACT);

            Either<trueffect.truconnect.api.commons.exceptions.business.Error, SiteContact> either
                    = contactManager.addSiteContactRef(siteContact, oauthKey());
            if (either.isError()) {
                return handleErrorCodes(either.error());
            } else {
                log.debug("The Site Contact = {} was successfully saved for User = {} ", siteContact, oauthKey().getUserId());
                return Response.ok(either.success()).build();
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }
}

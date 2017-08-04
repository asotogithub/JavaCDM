package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.CookieTargetTemplate;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CookieTargetTemplateDao;
import trueffect.truconnect.api.crud.dao.impl.CookieTargetTemplateDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.CookieDomainDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.CookieDomainManager;
import trueffect.truconnect.api.crud.service.CookieTargetTemplateManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Set of REST Services related to <code>CookieDomain</code> management.
 * 
 * @author Richard Jaldin
 * @author Thomas Barjou
 */
@Path("/CookieDomains")
public class CookieDomainService extends GenericService {

    private CookieDomainManager manager;
    private PersistenceContextMyBatis context;
    private AccessControl accessControl;

    private CookieTargetTemplateManager cookieTargetTemplateManager;
    
    public CookieDomainService() {
        context = new PersistenceContextMyBatis();
        accessControl = new AccessControlImpl(context);
        this.manager = new CookieDomainManager(new CookieDomainDaoImpl(context),
                                        new UserDaoImpl(context, accessControl),
                                        accessControl);
        CookieTargetTemplateDao cookieTargetTemplateDao = new CookieTargetTemplateDaoImpl(context);
        cookieTargetTemplateManager = new CookieTargetTemplateManager(cookieTargetTemplateDao, accessControl);
    }

    /**
     * Returns an <code>CookieDomain</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>CookieDomain</code> that matches the given <code>id</code> exists
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
     * @param id The <code>CookieDomain</code> id
     * @return an <code>CookieDomain</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCookieDomain(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            CookieDomain cookieDomain = manager.get(id, oauthKey());
            return APIResponse.ok(cookieDomain).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ",e);
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
     * Creates a new <code>CookieDomain</code> with the given <code>CookieDomain</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>ROLE_APP_ADMIN</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>CookieDomain</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>agencyId</code>: Not empty</li>
     *     <li><code>domain</code>: Not empty</li>
     *     <li><code>domain</code>: must be unique for the Agency</li>
     *     <li><code>domain</code>: should follow the patterns "XXXX.DDDDDD.COM", "xxx.XXX.ddd.DDD.com"</li>
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
     * @HTTP 409 Conflict - When an attempt to create a <code>CookieDomain</code>
     * with same url, and same agency.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
      @param domain <code>CookieDomain</code> to create
     * @return A new <code>CookieDomain</code> created given the provided parameters
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveCookieDomain(CookieDomain domain) {
        try {
            rolesAllowed("ROLE_APP_ADMIN");
            if (domain.getId() != null) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.postWithId", "Cookie Domain"))
                        .build();
            }
            if (StringUtils.isBlank(domain.getDomain())) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.empty",
                        "Domain name")).build();
            }

            Pattern p = Pattern.compile("([a-zA-Z_0-9])+(\\.\\w[a-zA-Z_0-9]+)+"
                                        + "(\\.\\w[a-zA-Z_0-9]+)"
                                        + "((\\.\\w[a-zA-Z_0-9]+)+(\\.\\w[a-zA-Z_0-9]+))?");
            Matcher matcher = p.matcher(domain.getDomain());
            if (!matcher.matches()) {
                return APIResponse.bad("Invalid Domain: " + domain.getDomain()).build();
            }

            domain = manager.create(domain, oauthKey());
            log.info(oauthKey().toString()+" Saved "+ domain);
            return APIResponse.created(domain, uriInfo, "CookieDomains").build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ",e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+domain, e);
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
     * Deletes a <code>CookieDomain</code> given its <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CookieDomain</code> that matches the given <code>id</code> has been successfully deleted 
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
     * @param id The <code>CookieDomain</code> id
     * @return a <code>Response</code>
     */
    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response removeCookieDomain(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN");
            SuccessResponse result = manager.delete(id, oauthKey());
            log.info(oauthKey().toString()+" Deleted "+ id);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ",e);
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
     * Returns a <code>RecordSet</code> of one or multiple <code>CookieTargetTemplate</code>
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
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>CookieTargetTemplate</code> is retrieved successfully.
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
     * @param id of the <code>CookieDomain</code> containing the <code>CookieTargetTemplate</code>
     * @return a {@link RecordSet<CookieTargetTemplate>} for the CookieDomain found
     */
    @GET
    @Path("/{id}/cookies")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCookies(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<CookieTargetTemplate> cookies = cookieTargetTemplateManager.getByDomain(id, oauthKey());
            return APIResponse.ok(cookies).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ",e);
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
     * Creates a new <code>CookieTargetTemplate</code> with the given <code>CookieDomain</code> id.
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
     * @HTTP 201 CREATED - When the <code>CookieTargetTemplate</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>cookieDomainId</code>: Not empty</li>
     *     <li><code>cookieName</code>: Not empty</li>
     *     <li><code>cookieName</code>: must be unique for the CookieDomain</li>
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
     * @HTTP 409 Conflict - When an attempt to create a <code>CookieTargetTemplate</code>
     * with same url, and same CookieDomain.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param id of the <code>CookieDomain</code> 
     * @param cookie <code>CookieTargetTemplate</code> to create
     * @return A new <code>CookieTargetTemplate</code> created given the provided parameters
     */
    @POST
    @Path("/{id}/cookie")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveCookie(@PathParam("id") Long id, CookieTargetTemplate cookie) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            cookie.setCookieDomainId(id);
            cookie = cookieTargetTemplateManager.saveCookieTargetTemplate(id, cookie, oauthKey());
            log.info("The Cookie Target Template ID = {}  saved for key = {}", id, oauthKey().toString());
            return APIResponse.created(cookie).build();
        } catch (AccessDeniedException e) {
            log.info("Access Denied: ",e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn(String.format("Data not found for %s : %s", oauthKey().toString(), id), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }
}

package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.AgencyUser;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.UserAdvertiser;
import trueffect.truconnect.api.commons.model.UserDomain;
import trueffect.truconnect.api.commons.model.dto.CookieDomainDTO;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.AdvertiserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CookieDomainDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserAdvertiserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDomainDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.AdvertiserManager;
import trueffect.truconnect.api.crud.service.CookieDomainManager;
import trueffect.truconnect.api.crud.service.UserAdvertiserManager;
import trueffect.truconnect.api.crud.service.UserDomainManager;
import trueffect.truconnect.api.crud.service.UserManager;
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
 *
 * @author Rambert Rioja
 */
@Path("/Users")
public class UserService extends GenericService {

    private UserManager userManager;
    private UserDomainManager userDomainManager;
    private CookieDomainManager cookieDomainManager;
    private UserAdvertiserManager userAdvertiserManager;
    private AdvertiserManager advertiserManager;

    public UserService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        UserDao userDao = new UserDaoImpl(context, accessControl);
        userManager = new UserManager(userDao, accessControl);
        userDomainManager =
                new UserDomainManager(new UserDomainDaoImpl(context), accessControl);
        cookieDomainManager =
                new CookieDomainManager(new CookieDomainDaoImpl(context), userDao, accessControl);
        userAdvertiserManager =
                new UserAdvertiserManager(new UserAdvertiserDaoImpl(context), accessControl);
        advertiserManager = new AdvertiserManager(new AdvertiserDaoImpl(context), userDao, accessControl);
    }

    public UserService(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    /**
     * Returns the User based on the user ID (user's email)
     *
     * @param id The user ID
     * @return the User that matches the give user ID
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getUser(@PathParam("id") String id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            User user = userManager.get(id, oauthKey().getUserId());
            return APIResponse.ok(user).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (NotFoundException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ id, e);
            return APIResponse.notFound(e.getMessage()).build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Saves a User
     *
     * @param user User to be saved.
     * @return the new User
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveUser(User user) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN");
            user = userManager.save(user, oauthKey());
            log.info(oauthKey().toString()+ " Saved "+user);
            return APIResponse.created(user, uriInfo, "Users").build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (ConflictException e) {
            log.warn("Conflict: ", e);
            return APIResponse.conflict(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+user, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates a User
     *
     * @param id User email and primary key
     * @param user User to be updated.
     * @return the User updated
     */
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateUser(@PathParam("id") String id, User user) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN");
            user = userManager.update(id, user, oauthKey());
            log.info(oauthKey().toString() + " Updated " + id);
            return APIResponse.ok(user).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Removes an User based on the email
     *
     * @param id email of the User to be removed
     * @return User that has been deleted
     */
    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response removeUser(@PathParam("id") String id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN");
            SuccessResponse result = userManager.remove(id, oauthKey());
            log.info(oauthKey().toString()+ " Deleted "+id);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getAgencyUserByTPWSKey(@QueryParam("tpws") String tpws) {
        try {
            rolesAllowed("ROLE_APP_ADMIN");
            AgencyUser agencyUser = userManager.getAgencyUserByTPWSKey(tpws);
            return APIResponse.ok(agencyUser).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+tpws, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns the list of the domains the user is authorized to see.
     *
     * @param id email of the User to return
     * @return A <code>RecordSet</code> of domains.
     */
    @GET
    @Path("/{id}/domains")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getUserDomains(@PathParam("id") String id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<CookieDomainDTO> domains = cookieDomainManager.getDomains(id, oauthKey());
            return APIResponse.ok(domains).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (NotFoundException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ id, e);
            return APIResponse.notFound(e.getMessage()).build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns the list of the advertisers the user is authorized to see.
     *
     * @param id email of the User to return
     * @return A <code>RecordSet</code> of advertisers.
     */
    @GET
    @Path("/{id}/advertisers")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getUserAdvertisers(@PathParam("id") String id) {

        if(log.isDebugEnabled()){
            log.debug("Entering get by userId: " + id);
        }
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_USER_ADVERTISER_LIST);
            RecordSet<Advertiser> records = advertiserManager.getAdvertisersByUserId(id, oauthKey());
            return APIResponse.ok(records).build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Updates a User - Advertisers relations
     *
     * @param id User email and primary key
     * @param advertisers RecordSet of Advertisers to be tied to the user.
     * @return the User - Advertisers relations updated
     */
    @PUT
    @Path("/{id}/userAdvertisers")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateUserAdvertisers(@PathParam("id") String id, RecordSet<UserAdvertiser> advertisers) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_USER_LIMIT_ADVERTISERS);
            Either<Error, RecordSet<UserAdvertiser>> result = userAdvertiserManager.updateUserAdvertisers(
                    id, advertisers, oauthKey());
            if (result.isSuccess()) {
                log.debug("The userAdvertisers = {} were successfully updated by the user = {}", id, oauthKey().toString());
                return APIResponse.ok(result.success()).build();
            } else {
                return handleErrorCodes(result.error());
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }
    /**
     * Updates a User - Domains relations
     *
     * @param id User email and primary key
     * @param domains RecordSet of Domains to be tied to the user.
     * @return the User - Domains relations updated
     */
    @PUT
    @Path("/{id}/userDomains")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateUserDomains(@PathParam("id") String id, RecordSet<UserDomain> domains) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_USER_LIMIT_DOMAINS);
            Either<Error, RecordSet<UserDomain>> result = userDomainManager.updateUserDomains(id, domains, oauthKey());
            if (result.isSuccess()) {
                log.debug("The user = {} was successfully updated by the user = {}", id, oauthKey().toString());
                return APIResponse.ok(result.success()).build();
            } else {
                return handleErrorCodes(result.error());
            }
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Updates User's limit domains and limit advertisers
     *
     * @param id User email and primary key
     * @param user The User to set limit domains/advertisers
     * @return the User - Domains/Advertisers relations updated
     */
    @PUT
    @Path("/{id}/limit")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response setUserLimits(@PathParam("id") String id, User user) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_USER_LIMIT_DOMAINS);
            Either<Error, Boolean> result = userManager.setUserLimits(id, user, oauthKey());
            if (result.isSuccess()) {
                return Response.status(Response.Status.SEE_OTHER)
                .location(getLocation("/Users/"+id)).build();
            } else {
                return handleErrorCodes(result.error());
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }
    
}

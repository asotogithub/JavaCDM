package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.delivery.TagType;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.AdvertiserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.BrandDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.AdvertiserManager;
import trueffect.truconnect.api.crud.service.BrandManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;
import trueffect.truconnect.api.external.proxy.TagTypeProxy;

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
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Set of REST Services related to <code>Advertiser</code> management.
 * 
 * @author Richard Jaldin
 * @author Marcelo Heredia
 * @author Thomas Barjou
 */
@Api(value = "Advertisers", description = "Advertiser service")
@Path("/Advertisers")
public class AdvertiserService extends GenericService {
    
    private AdvertiserManager advertiserManager;
    private BrandManager brandManager;
    
    public AdvertiserService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControlImpl accessControl = new AccessControlImpl(context);
        UserDao userDao = new UserDaoImpl(context);
        advertiserManager = new AdvertiserManager(new AdvertiserDaoImpl(context), userDao, accessControl);
        brandManager = new BrandManager(new BrandDaoImpl(context), accessControl);
    }
    
    public AdvertiserService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Returns an <code>Advertiser</code> given its <code>id</code>
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
     *     <li>No record was for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param id The <code>Advertiser</code> id
     * @return an <code>Advertiser</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}", notes="Get Advertiser.", response=Advertiser.class)
    @ApiResponses(value={ @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Advertiser found OR Access Token belongs to a User that does not have access to the requested data") })
    public Response get(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Advertiser advertiser = advertiserManager.get(id, oauthKey());
            return APIResponse.ok(advertiser).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.error("There was an internal error.", e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>Advertiser</code>
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
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Advertiser</code> is retrieved successfully.
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
     * @return a {@link RecordSet<Advertiser>} with the Advertisers found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getAdvertisers(@InjectParam SearchCriteria searchCriteria) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<Advertiser> records = advertiserManager.getAdvertisers(searchCriteria, oauthKey());
            return APIResponse.ok(records).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (SearchApiException e) {
            log.warn("Error on the Search query", e);
            return APIResponse.bad(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.error("There was an internal error.", e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>Brand</code> by Advertiser Id
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
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Brands</code> is retrieved successfully.
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
     * @param id The <code>Advertiser</code> id
     * @return a {@link RecordSet<Brand>} with the Brands found
     */
    @GET
    @Path("/{id}/brands")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}", notes="Get set of Brands associated with Advertiser.", response=Brand.class, responseContainer="List")
    @ApiResponses(value={ @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Advertiser found OR Access Token belongs to a User that does not have access to the requested data") })
    public Response getBrandsByAdvertiser(@PathParam("id") Long id) {

        if(log.isDebugEnabled()){
            log.debug("Entering get by advertiserId: " + id);
        }
        try {
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<Brand> records = brandManager.getBrandsByAdvertiserId(id, oauthKey());
            return APIResponse.ok(records).build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }    
    
     /**
     * Returns a <code>RecordSet</code> of one or multiple <code>TagType</code> by Advertiser and Site Id
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
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>TagType</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found
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
     * @param id The <code>Advertiser</code> id
     * @param siteId The <code>Site</code> id
     * @return a {@link RecordSet<Brand>} with the Brands found
     */
    @GET
    @Path("/{id}/site/{siteId}/tagTypes")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getTagTypes(@PathParam("id") Long id, @PathParam("siteId") Long siteId) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            //TODO: review if is necessary keep this validation here or move it into the manager layer
            advertiserManager.isValidForAgency(id, siteId, oauthKey());
            TagTypeProxy proxy = new TagTypeProxy(headers);
            proxy.path("GetTagTypes");
            proxy.query("AdvertiserId", Long.toString(id));
            proxy.query("SiteId", Long.toString(siteId));
            RecordSet<TagType> list = proxy.getTagTypes();
            return APIResponse.ok(list).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id +"-"+siteId, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>Advertiser</code> with the given <code>Agency</code>.
     *
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
     * @HTTP 201 CREATED - When the <code>Advertiser</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>agencyId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 100.</li>
     *     <li><code>name</code>: must be unique</li>
     *     <li><code>enableHtmlTag</code>: Not empty</li>
     *     <li><code>url</code>: Supports characters up to 256.</li>
     *     <li><code>address1</code>: Supports characters up to 256.</li>
     *     <li><code>address2</code>: Supports characters up to 256.</li>
     *     <li><code>notes</code>: Supports characters up to 2000.</li>
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
     * @HTTP 409 Conflict - When an attempt to create a <code>Advertiser</code>
     * with same name, and same agency.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param advertiser <code>Advertiser</code> to create
     * @return A new <code>Advertiser</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response save(Advertiser advertiser) {
        try {
            rolesAllowed("ROLE_APP_ADMIN");
            advertiser = advertiserManager.create(advertiser, oauthKey());
            log.info(oauthKey().toString()+" Saved "+ advertiser);
            return APIResponse.created(advertiser, uriInfo, "Advertisers").build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+advertiser, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (ConflictException e) {
            log.warn("Conflict: ", e);
            return APIResponse.conflict(e.getMessage(), uriInfo, "Advertisers").build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>Advertiser</code>.
     *
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
     * @HTTP 200 OK - When the <code>Advertiser</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>agencyId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 100.</li>
     *     <li><code>name</code>: must be unique</li>
     *     <li><code>enableHtmlTag</code>: Not empty</li>
     *     <li><code>url</code>: Supports characters up to 256.</li>
     *     <li><code>address1</code>: Supports characters up to 256.</li>
     *     <li><code>address2</code>: Supports characters up to 256.</li>
     *     <li><code>notes</code>: Supports characters up to 2000.</li>
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
     * @HTTP 409 Conflict - When an attempt to update a <code>Advertiser</code>
     * with same name, and same agency.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param id The <code>Advertiser</code> id
     * @param advertiser <code>Advertiser</code> to update
     * @return A <code>Advertiser</code> updated given the provided parameters
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response update(@PathParam("id") Long id, Advertiser advertiser) {
        try {
            rolesAllowed("ROLE_APP_ADMIN");
            advertiser = advertiserManager.update(id, advertiser, oauthKey());
            log.info(oauthKey().toString()+" Updated "+ id);
            return APIResponse.ok(advertiser).build();
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
            return APIResponse.conflict(CrudApiExceptionHandler.getMessage(e));
        } catch (ConflictException e) {
            log.warn("Conflict: ", e);
            return APIResponse.conflict(e.getMessage(), uriInfo, "Advertisers").build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Deletes an <code>Advertiser</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Advertiser</code> that matches the given <code>id</code> has been successfully deleted 
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
     * @param id The <code>Advertiser</code> id
     * @return a <code>Response</code>
     */
    @DELETE
    @Path("/{id}/physical")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response hardRemove(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN");
            SuccessResponse result = advertiserManager.hardRemove(id, oauthKey());
            log.info(oauthKey().toString()+" Deleted "+ id);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }
}

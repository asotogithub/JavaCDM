package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotAcceptableException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.Error;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CreativeAssociationsDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupCreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ExtendedPropertiesDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.CreativeInsertionManager;
import trueffect.truconnect.api.crud.service.CreativeManager;
import trueffect.truconnect.api.crud.service.UserManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;

import com.sun.jersey.api.core.InjectParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

/**
 * Set of REST Services related to <code>Creative</code> management.
 *
 * @author Richard Jaldin
 * @author Thomas Barjou
 */
@Api(value = "Creatives", description = "Creative service")
@Path("/Creatives")
public class CreativeService extends GenericService {

    private CreativeManager creativeManager;
    private CreativeInsertionManager creativeInsertionManager;

    public CreativeService() {
        PersistenceContext context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        CreativeDao creativeDao = new CreativeDaoImpl(context);
        CreativeGroupDao creativeGroupDao = new CreativeGroupDaoImpl(context);
        CreativeGroupCreativeDao creativeGroupCreativeDao = new CreativeGroupCreativeDaoImpl(context);
        CreativeInsertionDao creativeInsertionDao = new CreativeInsertionDaoImpl(context);
        CampaignDao campaignDao = new CampaignDaoImpl(context);

        UserDaoImpl userDao = new UserDaoImpl(context, accessControl);
        PlacementDao placementDao = new PlacementDaoImpl(context);
        ExtendedPropertiesDao extendedPropertiesDao = new ExtendedPropertiesDaoImpl(context);
        creativeInsertionManager = new CreativeInsertionManager(creativeInsertionDao, campaignDao,
                placementDao, creativeDao, creativeGroupDao, creativeGroupCreativeDao, accessControl);
        creativeManager = new CreativeManager(creativeDao, creativeGroupDao, creativeGroupCreativeDao,
                creativeInsertionDao, campaignDao, userDao, extendedPropertiesDao, accessControl);
        userManager = new UserManager(userDao, accessControl);
    }

    public CreativeService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Get the file of the <code>Creative</code> according to its id.
     * It can only be zip, jpg and gif files.
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
     * @HTTP 200 OK - When the <code>Creative</code> that matches the given <code>id</code> exists
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
     * @param id The <code>Creative</code> id
     * @return a file associated to the Creative
     */
    @GET
    @Path("/{id}/file")
    @ApiOperation(value="/{id}/file", notes="Get image file (zip, jpg, or gif) of a Creative.")
    @ApiResponses({@ApiResponse(code=200, message="Image file is returned"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getFile(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            File file = creativeManager.getFile(id, oauthKey());
            return APIResponse.ok(file).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (NotFoundException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Get the Preview of the <code>Creative</code> according to its id.
     * For files of type JPG and JPEG, the same creative image will be returned
     * with the appropriate MIME type "Content-Type: image/jpeg" For files of
     * type ZIP, the backup image will be extracted and returned as a GIF file.
     * For this type of file and GIF files, the content type is "Content-Type:
     * image/gif"
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
     * @HTTP 200 OK - When the <code>Creative</code> that matches the given <code>id</code> exists
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
     * @param id The <code>Creative</code> id
     * @return The Creative file associated to the Creative
     */
    @GET
    @Path("/{id}/preview")
    @ApiOperation(value="/{id}/preview", notes="Get preview of Creative.", responseHeaders=@ResponseHeader(name="Content-Type"))
    @ApiResponses({@ApiResponse(code=200, message="Image preview is returned"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getCreativePreview(@PathParam("id") Long id) {
        try {
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Map<String, Object> creativeMap = creativeManager.getCreativePreviewAndType(id,
                    oauthKey());
            // Not checking for null map as Access Control will make sure to throw another
            // exception if Creative ID is not found for user
            String mediaType;
            String fileType = (String) creativeMap.get(CreativeManager.CREATIVE_KEY_FILE_TYPE);
            CreativeManager.CreativeType creativeType = CreativeManager.CreativeType.typeOf(fileType);
            switch (creativeType) {
                case GIF:
                case HTML5:
                case ZIP:
                    mediaType = "image/gif";
                    break;
                case JPG:
                case JPEG:
                case TRD:
                case VMAP:
                case VAST:
                case XML:
                    mediaType = "image/jpeg";
                    break;
                case TXT:
                    mediaType = "text/plain";
                    break;
                default:
                    // This should be totally unexpected. Failsafe code
                    throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INTERNAL_ERROR, "mediaType");
            }
            Object entity = creativeMap.get(CreativeManager.CREATIVE_KEY_OBJECT);
            if (entity instanceof ByteArrayOutputStream) {
                ByteArrayOutputStream baos = null;
                ByteArrayInputStream bais = null;

                // uncomment line below to send streamed
                try {
                    baos = (ByteArrayOutputStream) entity;
                    byte[] imageData = baos.toByteArray();
                    bais = new ByteArrayInputStream(imageData);
                    return Response.ok(bais).
                            header("Content-Type", mediaType).build();
                } finally {
                    try {
                        if (baos != null) {
                            baos.close();
                        }
                        if (bais != null) {
                            bais.close();
                        }
                    } catch (IOException e) {
                        log.warn("Error while closing streams", e);
                    }
                }
            } else {
                return Response.ok(entity).
                        header("Content-Type", mediaType).build();
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Verifies the content of the zip files according to its <code>Creative</code> id.
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
     * @HTTP 200 OK - When the <code>Creative</code> that matches the given <code>id</code> exists
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
     * @param id The <code>Creative</code> id
     * @return a Jersey <code>Response</code>
     */
    @GET
    @Path("/verify/{id}")
    @ApiOperation(value="/verify/{id}", notes="Verifies content of zip files according to its Creative.")
    @ApiResponses({
            @ApiResponse(code=200, message="Verification successful"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response verification(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            boolean result = creativeManager.verification(id, oauthKey());
            if (result) {
                return APIResponse.ok().build();
            }
            return APIResponse.error("Something went wrong on verification.");
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
     * Returns a <code>Creative</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Creative</code> that matches the given <code>id</code> exists
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
     * @param id The <code>Creative</code> id
     * @return a <code>Creative</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}", notes="Get Creative.", response=Creative.class)
    @ApiResponses({@ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getCreative(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Creative creative = creativeManager.getCreative(id, oauthKey());
            return APIResponse.ok().entity(creative).build();
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
     * Returns a <code>RecordSet</code> of one or multiple <code>Creative</code>
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
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Creative</code> is retrieved successfully.
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
     * @param criteria {@link SearchCriteria} that provides the filtering criteria
     * @return a {@link RecordSet<Creative>} with the Creatives found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/", notes="Get Creatives by search criteria.", response=Creative.class,
                  responseContainer="List")
    @ApiResponses({@ApiResponse(code=200, message="Success; returns RecordSet of Creatives"),
            @ApiResponse(code=400, message="Bad request: one or more errors in search criteria"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getCreatives(@InjectParam SearchCriteria criteria) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<Creative> records = creativeManager.getCreatives(criteria, oauthKey());
            return APIResponse.ok(records).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + criteria, e);
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
     * Returns a <code>RecordSet</code> of one or multiple <code>CreativeAssociationsDTO</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CREATIVE_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Creative</code> that matches the given <code>id</code> exists
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
     * @param creativeId The <code>Creative</code> id
     * @return a Jersey <code>Response</code>
     */
    @GET
    @Path("/{id}/creativeAssociationsCount")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}/creativeAssociationsCount", notes="Get set of CreativeAssociationsDTOs for Creative.",
                  response=CreativeAssociationsDTO.class, responseContainer="List")
    @ApiResponses({@ApiResponse(code=401, message="Access Token Expired OR Permission Denied"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getCreativeAssociationsById(@PathParam("id") Long creativeId) {
        log.debug("Return groupIds and number of schedules associated for {} creative", creativeId);
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CREATIVE_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, RecordSet<CreativeAssociationsDTO>>
                    result = creativeManager.getScheduleAssocPerGroupByCreativeId(creativeId,
                    oauthKey());
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
     * Returns the <code>CreativeInsertionView</code> for the given <code>Creative</code> id
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CREATIVE_INSERTION_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Creative</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
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
     * @param creativeId The <code>Creative</code> id
     * @param startIndex
     * @param pageSize
     * @return a {@link RecordSet<CreativeInsertionView>} with the Creative found
     */
    @GET
    @Path("/{id}/schedules")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}/schedules", notes="Get set of CreativeInsertionViews for Creative.",
                  response=CreativeInsertionView.class, responseContainer="List")
    @ApiResponses({@ApiResponse(code=401, message="Access Token Expired OR Permission Denied"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getSchedulesByCreativeId(@PathParam("id") Long creativeId,
            @QueryParam("startIndex") Long startIndex,
            @QueryParam("pageSize") Long pageSize) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CREATIVE_INSERTION_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, RecordSet<CreativeInsertionView>> result = creativeInsertionManager.getByCreativeId(creativeId, startIndex,
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
     * Updates an existing <code>Creative</code> image.
     * Upload a creative image with either the same filename and campaignId, or with creativeId and campaignId.
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
     * @HTTP 200 OK - When the <code>Creative</code> image was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>inputStream</code>: Not empty</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>filename</code>: Not empty</li>
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
     * @param id The <code>Creative</code> id
     * @param inputStream The binary file
     * @param filename The filename of the image
     * @return a Jersey <code>Response</code>
     */
    @PUT
    @Path("/{id}/image")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}/image", notes="Updates an existing Creative's image.")
    @ApiResponses({@ApiResponse(code=200, message="Successful update"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="Access Token belongs to a User that does not have access to the requested data")})
    @SuppressWarnings({"rawtypes", "unused"})
    public Response replaceImage(@PathParam("id") Long id,
            InputStream inputStream,
            @ApiParam(required=true) @QueryParam("filename") String filename) throws Exception {
        log.debug("Replace Image {}", id);
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            creativeManager.replaceImage(id, inputStream, filename, oauthKey());
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ConflictException e) {
            log.warn("Conflict Exception", e);
            Error error = new Error(e.getMessage(), ApiValidationUtils.TYPE_INVALID);
            Errors errors = new Errors(Arrays.asList(error));
            String fileType = AdminFile.fileType(filename);
            return Response.status(Response.Status.CONFLICT).header("type", fileType).entity(errors).build();
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (NotAcceptableException e) {
            log.warn("Not Acceptable", e);
            return APIResponse.notAcceptable(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Deletes a <code>Creative</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Creative</code> that matches the given <code>id</code> has been successfully deleted 
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
     * @param creativeId The <code>Creative</code> id
     * @param recursiveDelete: Must be true if it is needed to delete also the CreativeInsertions of this Creative
     * @return a Jersey <code>Response</code>
     */
    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}", notes="Delete a Creative.")
    @ApiResponses({@ApiResponse(code=200, message="Delete successful"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response removeCreative(@PathParam("id") Long creativeId,
            @QueryParam("recursiveDelete") Boolean recursiveDelete) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            creativeManager.removeCreative(creativeId, oauthKey(), recursiveDelete);
            return Response.ok(new SuccessResponse("Creative " + creativeId + " successfully deleted")).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + creativeId, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (SearchApiException e) {
            log.warn("Error getting CreativeInsertions. Malformed Query.", e);
            return APIResponse.bad(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Error deleting Creative. Scheduled associations exist.", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * De-associate a <code>Creative</code> from the provided <code>RecordSet</code> of <code>CreativeGroup</code>s given its <code>id</code>
     * If after de-associating there is no associations remaining, the <code>Creative</code> gets deleted.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Permissions Allowed:  <code>DELETE_CREATIVE</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Creative</code> that matches the given <code>id</code> has been successfully deleted
     *
     * @HTTP 401 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param creativeId The <code>Creative</code> id
     * @param groupIds {@link RecordSet<Long>} with the Group ids to de-associate before deleting
     * @return a Jersey <code>Response</code>
     */
    @PUT
    @Path("/{id}/remove")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}/remove", notes="De-associate a Creative from set of CreativeGroups.")
    @ApiResponses({@ApiResponse(code=200, message="De-associate successful"),
            @ApiResponse(code=401, message="Access Token Expired OR Access Error"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response remove(@PathParam("id") Long creativeId,
                           @ApiParam(required=true) RecordSet<Long> groupIds) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.DELETE_CREATIVE);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, SuccessResponse>
                    result = creativeManager.removeCreative(creativeId, groupIds, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn("Unexpected Exception while getting unassociated Creative.", e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Deletes a <code>RecordSet</code> of <code>Creative</code> given its <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Creative</code> that matches the given <code>id</code> has been successfully deleted 
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
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param creatives {@link RecordSet<Creative>} with the Creatives to delete
     * @return a Jersey <code>Response</code>
     */
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/", notes="Delete a set of Creatives.")
    @ApiResponses({@ApiResponse(code=200, message="Delete successful"),
            @ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response removeCreatives(@ApiParam(required=true) RecordSet<Creative> creatives) {
        try {
            checkValidityOfToken();
            //TODO: This should have a checkPermissions call. To cover in US5196
            SuccessResponse result = creativeManager.removeCreatives(creatives, oauthKey());
            return APIResponse.ok(result).build();
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Updates an existing <code>Creative</code>.
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
     * @HTTP 200 OK - When the <code>Creative</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules on payload are:
     * <ul>
     *     <li>Should not be empty.</li>
     *     <li><code>id</code>: Required. Should not be blank</li>
     *     <li><code>id</code>: Should be the same as the <code>Creative</code> id provided
     *     in the path param</li>
     *     <li><code>alias</code>: Required. Should not be blank</li>
     *     <li><code>alias</code>: Should not be longer than 256 characters</li>
     *     <li><code>purpose</code>: Should not be longer than 256 characters</li>
     *     <li><code>extProp</code> from 1 to 5: Should not be longer than 256 characters</li>
     *     <li>If <code>creativeType</code> is <code>TRD</code>: <code>clickthrough</code> <b>should be empty</b></li>
     *     <li>If <code>creativeType</code> is any other supported type: <code>clickthrough</code> is required, and
     *     <b>should not be</b> empty</li>
     *     <li>If <code>creativeType</code> is <code>HTML5</code> or <code>ZIP</code>; then,
     *     <code>clickthroughs</code> are accepted.</li>
     *     <li><code>creativeGroups</code>: Can be null but not empty. Not providing the list of <code>Group</code>s
     *     implies that no changes should be done on the existing <code>Group</code>s associations for this <code>Creative</code>.</li>
     *     <li>When provided, <code>creativeGroups</code> validation rules are:
     *         <ul>
     *             <li><code>creativeGroups[i].id</code>: Required. Should not be blank</li>
     *         </ul>
     *     </li>
     *     <li>If <code>creativeType</code> is different from <code>HTML5</code> or <code>ZIP</code>; then,
     *     <code>clickthroughs</code> are rejected and should not be provided.</li>
     *     <li>When accepted, <code>clickthroughs</code> validation rules are:
     *         <ul>
     *             <li><code>clickthroughs[i].url</code>>: Required. Should not be blank</li>
     *             <li><code>clickthroughs[i].url</code>: Should be a well formed URL</li>
     *             <li><code>clickthroughs[i].url</code>: Should not be longer than 256 characters</li>
     *             <li><code>clickthroughs[i].sequence</code>: Required. Should be blank.</li>
     *             <li><code>clickthroughs[i].sequence</code>: Should be a positive integer number</li>
     *             <li><code>clickthroughs[i].sequence</code>: Should be unique in the list of <code>clickthroughs</code></li>
     *         </ul>
     *     </li>
     *     <li><code>versions</code>: Can be null or empty. Not providing the versions
     *     implies that no changes should be done on the existing versions.</li>
     *     <li>When provided, <code>versions</code> validation rules are:
     *         <ul>
     *             <li><code>versions[i].versionNumber</code>: Required. Should not be blank</li>
     *             <li><code>versions[i].versionNumber</code>: Should be a positive integer number</li>
     *             <li><code>versions[i].versionNumber</code>: Should be unique in the list of <code>versions</code>.
     *             <li><code>versions[N].versionNumber</code>: If this corresponds to the last known version (N), the <code>versions[N].alias</code> provided will be
     *             <b>ignored</b>, by taking the value provided in <code>alias</code>. <code>alias</code> overwrites the last know version <code>versions[N].alias</code></li>
     *             <li><code>versions[i].alias</code>: Required. Should not be blank</li>
     *             <li><code>versions[i].alias</code>: Should not be longer than 256 characters</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an authentication error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Access Token Invalid</li>
     * </ul>

     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Permission Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 409 Conflict - When an attempt to update a <code>Creative</code>
     * with same name and same Campaign.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param id The <code>Creative</code> id
     * @param creative <code>Creative</code> entity to update
     * @return The updated <code>Creative</code> with the provided parameters
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/{id}", notes="Updates an existing Creative.", response=Creative.class)
    @ApiResponses({@ApiResponse(code=403, message="Access Token Expired OR Role Not Allowed"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response updateCreative(@PathParam("id") Long id, @ApiParam(required=true) Creative creative) {
        try {
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> either;
            either = checkToken();
            if (either.isError()) {
                return handleErrorCodesAsLegacyErrors(either.error());
            }
            either = permissionsValid(AccessPermission.UPDATE_CREATIVE);
            if (either.isError()) {
                return handleErrorCodesAsLegacyErrors(either.error());
            }
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Creative> result =
                    creativeManager.updateCreative(id, creative, oauthKey());
            if (result.isError()) {
                return handleErrorCodesAsLegacyErrors(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(String.format("Unexpected exception while updating a Creative: %s",
                    e.getMessage()), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns the <code>Creative</code> that are not associated for the given <code>CreativeGroup</code> id
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_CREATIVE_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Creative</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
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
     * @param campaignId The <code>Campaign</code> id
     * @param groupId The <code>CreativeGroup</code> id
     * @param startIndex
     * @param pageSize
     * @return a {@link RecordSet<Creative>} with the Creative found
     */
    @GET
    @Path("/unassociated")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @ApiOperation(value="/unassociated", notes="Get Creatives that are not associated with a CreativeGroup.",
                  response=Creative.class, responseContainer="List")
    @ApiResponses({@ApiResponse(code=401, message="Access Token Expired OR Permission Denied"),
            @ApiResponse(code=404, message="No Creative found OR Access Token belongs to a User that does not have access to the requested data")})
    public Response getCreativesWithNoGroupAssociation(
            @ApiParam(required=true) @QueryParam("campaignId") Long campaignId,
            @ApiParam(required=true) @QueryParam("groupId") Long groupId,
            @QueryParam("startIndex") Long startIndex,
            @QueryParam("pageSize") Long pageSize) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_CREATIVE_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<Creative>> result
                    = creativeManager.getCreativesWithNoGroupAssociation(campaignId, groupId, startIndex, pageSize, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn("Unexpected Exception while getting unassociated Creatives.", e);
            throw new WebApplicationSystemException(e);
        }
    }
}

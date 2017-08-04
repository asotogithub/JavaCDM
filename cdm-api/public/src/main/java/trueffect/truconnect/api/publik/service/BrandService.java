package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
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
import trueffect.truconnect.api.crud.dao.impl.BrandDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ExtendedPropertiesDaoImpl;
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
import trueffect.truconnect.api.crud.service.BrandManager;
import trueffect.truconnect.api.crud.service.PlacementManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Set of REST Services related to <code>Brand</code> management.
 * 
 * @author Rambert Rioja
 * @author Thomas Barjou
 */
@Path("/Brands")
public class BrandService extends GenericService {

    private BrandManager brandManager;
    private PlacementManager placementManager;

    public BrandService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        PersistenceContextMyBatis dimContext = new DimPersistenceContextMyBatis();
        AccessControlImpl accessControl = new AccessControlImpl(context);
        this.brandManager = new BrandManager(new BrandDaoImpl(context), accessControl);

        PlacementDao placementDao = new PlacementDaoImpl(context);
        CostDetailDaoExtended
                placementCostDetailDao = new CostDetailDaoImpl(CostDetailType.PLACEMENT, context);
        CampaignDao campaignDao = new CampaignDaoImpl(context);
        SiteSectionDao siteSectionDao = new SiteSectionDaoImpl(context);
        SizeDao sizeDao = new SizeDaoImpl(context);
        PlacementStatusDao placementStatusDao = new PlacementStatusDaoImpl(context);
        UserDao userDao = new UserDaoImpl(context);
        ExtendedPropertiesDao extendedPropertiesDao = new ExtendedPropertiesDaoImpl(context);
        InsertionOrderDao insertionOrderDao = new InsertionOrderDaoImpl(context);
        InsertionOrderStatusDao insertionOrderStatusDao = new InsertionOrderStatusDaoImpl(context);
        PackageDaoExtended packageDao = new PackageDaoImpl(context);
        PackagePlacementDaoExtended packagePlacementDao = new PackagePlacementDaoImpl(context);
        CostDetailDaoBase dimPlacementCostDetailDao = new DimCostDetailDaoImpl(CostDetailType.PLACEMENT, dimContext);
        CreativeInsertionDao creativeInsertionDao = new CreativeInsertionDaoImpl(context);

        this.placementManager = new PlacementManager(placementDao, placementCostDetailDao,
                campaignDao, siteSectionDao, sizeDao, placementStatusDao, userDao,
                extendedPropertiesDao, insertionOrderDao, insertionOrderStatusDao,
                packageDao, packagePlacementDao, dimPlacementCostDetailDao,
                creativeInsertionDao, accessControl);
    }

    public BrandService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Returns a <code>Brand</code> given its <code>id</code>
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
     * @HTTP 200 OK - When the <code>Brand</code> that matches the given <code>id</code> exists
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
     * @param id The <code>Brand</code> id
     * @return a <code>Brand</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getBrand(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Brand brand = brandManager.get(id, oauthKey());
            return APIResponse.ok(brand).build();
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
     * Returns a <code>RecordSet</code> of one or multiple <code>Brand</code>
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
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Brand</code> is retrieved successfully.
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
     * @return a {@link RecordSet<Brand>} with the Brands found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getBrands(@InjectParam SearchCriteria searchCriteria) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<Brand> records = brandManager.getBrands(searchCriteria, oauthKey());
            Response.ResponseBuilder resp;
            resp = APIResponse.ok(records);
            return resp.build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + searchCriteria, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (SearchApiException e) {
            log.warn("Error on the Search query", e);
            return APIResponse.bad(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>Brand</code> with the given <code>Advertiser</code>.
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
     * @HTTP 201 CREATED - When the <code>Brand</code> was created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>advertiserId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 100.</li>
     *     <li><code>name</code>: must be unique</li>
     *     <li><code>description</code>: Supports characters up to 256.</li>
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
     * @HTTP 409 Conflict - When an attempt to create a <code>Brand</code>
     * with same name, and same agency.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param brand <code>Brand</code> to create
     * @return A new <code>Brand</code> created given the provided parameters
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveBrand(Brand brand) {
        try {
            rolesAllowed("ROLE_APP_ADMIN");
            brand = brandManager.create(brand, oauthKey());
            log.info(oauthKey().toString() + " Saved " + brand);
            return APIResponse.created(brand, uriInfo, "Brands").build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + brand, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (ConflictException e) {
            log.warn("Conflict: ", e);
            return APIResponse.conflict(e.getMessage());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>Brand</code>.
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
     * @HTTP 200 OK - When the <code>Brand</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>advertiserId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 100.</li>
     *     <li><code>name</code>: must be unique</li>
     *     <li><code>description</code>: Supports characters up to 256.</li>
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
     * @HTTP 409 Conflict - When an attempt to update a <code>Advertiser</code>
     * with same name, and same agency.
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs, or:
     *
     * @param id The <code>Brand</code> id
     * @param brand <code>Brand</code> to update
     * @return A <code>Brand</code> updated given the provided parameters
     */
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateBrand(@PathParam("id") Long id, Brand brand) {

        try {
            rolesAllowed("ROLE_APP_ADMIN");
            brand = brandManager.update(id, brand, oauthKey());
            log.info(oauthKey().toString() + " Updated " + id);
            return APIResponse.ok(brand).build();
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
     * Returns a <code>RecordSet</code> of one or multiple <code>placements</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>---</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_PLACEMENT_FOR_AD_TAGS_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>placement list</code>s is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given brand Id
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters

     * @HTTP 401 Unauthorized - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     * </ul>

     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request, or:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>

     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param brandId <code>brand</code> id
     * @return a {@link RecordSet<trueffect.truconnect.api.commons.model.dto.PlacementView>} with the Placements found
     */
    @GET
    @Path("/{id}/placements")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPlacementByAdvertiserAndBrand(@PathParam("id") Long brandId) {
        log.debug("Get Placements by brandId {}", brandId);
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_PLACEMENT_FOR_AD_TAGS_LIST);
            Either<Errors, RecordSet<PlacementView>>
                    result= placementManager.getPlacementsByBrand(brandId, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }

        }catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }

    }
}

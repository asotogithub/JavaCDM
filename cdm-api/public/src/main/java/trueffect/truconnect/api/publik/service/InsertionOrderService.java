package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.BulkPackagePlacement;
import trueffect.truconnect.api.commons.model.dto.PackagePlacementView;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoBase;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ExtendedPropertiesDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderStatusDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackagePlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackageDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementStatusDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteSectionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SizeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackageDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackagePlacementDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.dim.DimPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.PackagePlacementManager;
import trueffect.truconnect.api.crud.service.InsertionOrderManager;
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

/**
 *
 * @author Richard Jaldin
 */
@Path("/InsertionOrders")
public class InsertionOrderService extends GenericService {

    private InsertionOrderManager insertionOrderManager;
    private PackagePlacementManager packagePlacementManager;

    public InsertionOrderService() {
        super();
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        PersistenceContextMyBatis dimContext = new DimPersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);

        InsertionOrderDao insertionOrderDao = new InsertionOrderDaoImpl(context);
        UserDao userDao = new UserDaoImpl(context, accessControl);
        InsertionOrderStatusDao insertionOrderStatusDao = new InsertionOrderStatusDaoImpl(context);
        PlacementDao placementDao = new PlacementDaoImpl(context);
        CostDetailDaoExtended placementCostDetailDao = new CostDetailDaoImpl(CostDetailType.PLACEMENT, context);
        CreativeInsertionDao creativeInsertionDao= new CreativeInsertionDaoImpl(context);
        PlacementStatusDao placementStatusDao = new PlacementStatusDaoImpl(context);
        PackageDaoExtended packageDao = new PackageDaoImpl(context);
        CostDetailDaoExtended packageCostDetailDao = new CostDetailDaoImpl(CostDetailType.PACKAGE, context);
        PackagePlacementDaoExtended packagePlacementDao = new PackagePlacementDaoImpl(context);

        insertionOrderManager = new InsertionOrderManager(insertionOrderDao, insertionOrderStatusDao,
                userDao, placementDao, placementStatusDao, creativeInsertionDao, accessControl);
        CampaignDao campaignDao = new CampaignDaoImpl(context);
        SiteSectionDao siteSectionDao = new SiteSectionDaoImpl(context);
        SizeDao sizeDao = new SizeDaoImpl(context);
        ExtendedPropertiesDao extendedPropertiesDao = new ExtendedPropertiesDaoImpl(context);

        PackageDaoBase dimPackageDao = new DimPackageDaoImpl(dimContext);
        PackagePlacementDaoBase dimPackagePlacementDao = new DimPackagePlacementDaoImpl(dimContext);
        CostDetailDaoBase dimPlacementCostDetailDao = new DimCostDetailDaoImpl(CostDetailType.PLACEMENT, dimContext);
        CostDetailDaoBase dimPackageCostDetailDao = new DimCostDetailDaoImpl(CostDetailType.PACKAGE, dimContext);

        packagePlacementManager = new PackagePlacementManager(placementDao, placementCostDetailDao,
                campaignDao, siteSectionDao, sizeDao, placementStatusDao, userDao,
                extendedPropertiesDao, insertionOrderDao, insertionOrderStatusDao,
                packageDao, packageCostDetailDao, packagePlacementDao, dimPackageCostDetailDao,
                dimPlacementCostDetailDao, dimPackageDao, dimPackagePlacementDao,
                creativeInsertionDao, accessControl);
    }

    /**
     * Returns the InsertionOrder based on the ID
     *
     * @param id Insertion order ID number and primary key
     * @return the InsertionOrder of the id
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getInsertionOrder(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            InsertionOrder insertionOrder = insertionOrderManager.get(id, oauthKey());
            return APIResponse.ok(insertionOrder).build();
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
     * Returns the InsertionOrders that matches with search criteria
     *
     * @param searchCriteria search criteria object
     * @return RecordSet of InsertionOrders
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getInsertionOrders(@InjectParam SearchCriteria searchCriteria) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<InsertionOrder> records = insertionOrderManager.get(searchCriteria, oauthKey());
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
     * Saves an Insertion Order
     *
     * @param insertionOrder Insertion Order object to be saved
     * @return the new InsertionOrder saved on the data base
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveInsertionOrder(InsertionOrder insertionOrder) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            insertionOrder = insertionOrderManager.create(insertionOrder, oauthKey());
            log.info(oauthKey().toString() + " Saved " + insertionOrder);
            return APIResponse.created(insertionOrder, uriInfo, "InsertionOrders").build();
        } catch (ValidationException e) {
            log.warn("Validation Error", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + insertionOrder, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an Insertion Order
     *
     * @param id Insertion order ID number and primary key
     * @param insertionOrder Insertion Order object to be saved
     *
     * @return InsertionOrder updated
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateInsertionOrder(@PathParam("id") Long id, InsertionOrder insertionOrder) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            insertionOrder = insertionOrderManager.update(id, insertionOrder, oauthKey());
            log.info(oauthKey().toString() + " Updated " + insertionOrder);
            return APIResponse.ok(insertionOrder).build();
        } catch (ValidationException e) {
            log.warn("Validation Error", e);
            return APIResponse.bad(e.getMessage()).build();
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
     * Removes a InsertionOrder
     *
     * @param id Insertion order ID number and primary key
     * @return InsertionOrder that has been deleted
     */
    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response removeInsertionOrder(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            SuccessResponse result = insertionOrderManager.remove(id, oauthKey());
            log.info(oauthKey().toString() + " Deleted " + id);
            return APIResponse.ok(result).build();
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
     * Returns the InsertionOrder based on the MediaBuy ID
     *
     * @param mediaBuyId ID number of the MediaBuy
     * @return the InsertionOrder based on the ID
     */
    @GET
    @Path("/byMediaBuy/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getInsertionOrderByMediaBuy(@PathParam("id") Long mediaBuyId) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            InsertionOrder insertionOrder = insertionOrderManager.getByMediaBuy(mediaBuyId, oauthKey());
            return APIResponse.ok(insertionOrder).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for " + oauthKey().toString() + ":" + mediaBuyId, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Saves a set of Packages with their Placements and Standalone Placements
     *
     * @param ioId The Insertion Order ID
     * @param bulkPackagePlacement The {@code BulkPackagePlacement} object to process
     * @return the new InsertionOrder saved on the data base
     */
    @POST
    @Path("/{id}/bulkPackagePlacement")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createPackagePlacements(@PathParam("id") Long ioId, BulkPackagePlacement bulkPackagePlacement) {
        try {
            try {
                checkValidityOfToken();
                checkPermissions(AccessPermission.CREATE_BULK_PACKAGE_PLACEMENT);
                bulkPackagePlacement = packagePlacementManager.createBulkPackagePlacements(ioId, bulkPackagePlacement, oauthKey());
            } catch (SystemException se) {
                throw BusinessValidationUtil.parseToLegacyException(se);
            }
            return APIResponse.ok(bulkPackagePlacement).build();
        } catch (ValidationException e) {
            log.warn("Validation Error", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (ConflictException e) {
            log.warn("Conflict Error", e);
            return APIResponse.conflict(e.getMessage());
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for {} : {} ", oauthKey().toString(), ioId);
            log.warn("Data not found error: ", e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.error("Unexpected Exception, reason: ", e);
            return APIResponse.error(e.getMessage());
        }
    }
    
    @GET
    @Path("/{id}/packagePlacementView")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPackagePlacementView(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_IO_PACKAGE_PLACEMENT_LIST);
            RecordSet<PackagePlacementView> result = packagePlacementManager.getPackagePlacementViewByInsertionOrder(id, oauthKey());
            return APIResponse.ok(result).build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }
}
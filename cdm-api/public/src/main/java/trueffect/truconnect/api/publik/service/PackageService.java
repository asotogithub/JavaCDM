package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoBase;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackageDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackagePlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackageDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackagePlacementDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.dim.DimPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.PackageManager;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Services for Package operations
 * Created by marcelo.heredia on 8/17/2015.
 * @author Marcelo Heredia, Richard Jaldin
 */
@Path("/Packages")
public class PackageService extends GenericService {
    private PackageManager packageManager;

    public PackageService() {
       packageManager = getPackageManagerInstance();
    }

    public static PackageManager getPackageManagerInstance() {
        PersistenceContext persistenceContext = new PersistenceContextMyBatis();
        PersistenceContext dimContext = new DimPersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(persistenceContext);
        PackageDaoExtended packageDao = new PackageDaoImpl(persistenceContext);
        PlacementDao placementDao = new PlacementDaoImpl(persistenceContext);
        PackagePlacementDaoExtended packagePlacementDao = new PackagePlacementDaoImpl(persistenceContext);
        CostDetailDaoExtended packageCostDetailDao = new CostDetailDaoImpl(CostDetailType.PACKAGE, persistenceContext);
        CostDetailDaoExtended placementCostDetailDao = new CostDetailDaoImpl(CostDetailType.PLACEMENT, persistenceContext);

        PackageDaoBase dimPackageDao = new DimPackageDaoImpl(dimContext);
        PackagePlacementDaoBase dimPackagePlacementDao = new DimPackagePlacementDaoImpl(dimContext);
        CostDetailDaoBase dimPackageCostDetailDao = new DimCostDetailDaoImpl(CostDetailType.PACKAGE, dimContext);
        CostDetailDaoBase dimPlacementCostDetailDao = new DimCostDetailDaoImpl(CostDetailType.PLACEMENT, dimContext);
        InsertionOrderDao insertionOrderDao = new InsertionOrderDaoImpl(persistenceContext);

        return new PackageManager(packageDao, packageCostDetailDao,
                placementDao, placementCostDetailDao, packagePlacementDao,
                dimPlacementCostDetailDao, dimPackageDao, dimPackageCostDetailDao,
                dimPackagePlacementDao, insertionOrderDao, accessControl);
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPackage(@PathParam("id") Long packageId) {
        try{
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Package pkg = packageManager.getPackage(packageId, oauthKey());
            return Response.ok(pkg).build();
        } catch (SystemException e){
            throw new WebApplicationSystemException(e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPackages(@InjectParam SearchCriteria searchCriteria) {
        try {
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<Package> records = packageManager.get(searchCriteria, oauthKey());
            return Response.ok(records).build();
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }
    
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updatePackage(@PathParam("id") Long packageId, Package pkg) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_PACKAGE);
            Package result = packageManager.update(packageId, pkg, oauthKey());
            return APIResponse.ok(result).build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        } 
    }

    /**
     * Deletes one or more <code>Package</code>s in bulk.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>DELETE_PACKAGE</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Package</code> was deleted successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>Long</code>: Not empty</li>
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
     * @HTTP 404 Not Found - When Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the query parameters or payload data</li>
     * </ul>
     *
     * @param bulkDelete A RecordSet of <code>Long</code> with Package IDs to process.
     * @return success message.
     */
    @PUT
    @Path("/bulkDelete")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response packagesBulkDelete(RecordSet<Long> bulkDelete) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.DELETE_PACKAGE);
            Either<Errors, String> result =
                    packageManager.bulkDelete(bulkDelete, oauthKey());

            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(new SuccessResponse(result.success())).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }
}

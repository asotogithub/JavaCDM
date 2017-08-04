package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.dto.dim.DimPlacementCostDetailDTO;
import trueffect.truconnect.api.commons.model.dto.dim.DimPackageDTO;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AgencyDao;
import trueffect.truconnect.api.crud.dao.DimCostDetailDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.impl.AgencyDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackageDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.dim.DimPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.DimCostDetailManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Rodri A.
 */
@Path("/DIMTest")
public class DimTestService extends GenericService {

    private DimCostDetailManager dimCostDetailManager;

    public DimTestService() {
        PersistenceContextMyBatis context = new DimPersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        PackageDaoBase dimPackageDao = new DimPackageDaoImpl(context);
        DimCostDetailDao dimCostDetailDao = new DimCostDetailDaoImpl(context);
        AgencyDao agencyDao = new AgencyDaoImpl(context);
        dimCostDetailManager = new DimCostDetailManager(dimPackageDao, dimCostDetailDao, agencyDao, accessControl);
    }

    public DimTestService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Gets Package's hierarchy on dim DB based on its TFA DB ID
     *
     * @param id Package's Id
     * @return Package's hierarchy on dim DB based on its TFA DB ID
     */
    @GET
    @Path("/package/{id}/costDetails")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPackage(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.AUTOMATED_TEST);
            Either<Error, DimPackageDTO> result = dimCostDetailManager.getPackage(id);
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
     * Gets Placement's costs on dim DB based on its TFA DB ID
     *
     * @param id Placement's Id
     * @return Placement's costs on dim DB based on its TFA DB ID
     */
    @GET
    @Path("/placement/{id}/costDetails")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getDimPlacementCostDetail(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.AUTOMATED_TEST);
            Either<Error, DimPlacementCostDetailDTO> result = dimCostDetailManager.getDimPlacementCostDetails(id);
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
}

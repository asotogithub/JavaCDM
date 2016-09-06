package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.dto.Bootstrap;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AdmTransactionDao;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.BrandDao;
import trueffect.truconnect.api.crud.dao.CookieDomainDao;
import trueffect.truconnect.api.crud.dao.DatasetConfigDao;
import trueffect.truconnect.api.crud.dao.DimCostDetailDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.AdmTransactionImpl;
import trueffect.truconnect.api.crud.dao.impl.AdvertiserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.AgencyDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.BrandDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ContactDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CookieDomainDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.DatasetConfigDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.dim.DimPackageDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.dim.DimPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.AdvertiserManager;
import trueffect.truconnect.api.crud.service.AgencyManager;
import trueffect.truconnect.api.crud.service.BootstrapManager;
import trueffect.truconnect.api.crud.service.BrandManager;
import trueffect.truconnect.api.crud.service.ContactManager;
import trueffect.truconnect.api.crud.service.CookieDomainManager;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Service class for bootstrapping test data.
 */
@Path("/Bootstrap")
public class BootstrapService extends GenericService {

    private BootstrapManager bootstrapManager;

    public BootstrapService() {
        PersistenceContext context = new PersistenceContextMyBatis();
        PersistenceContextMyBatis dimContext = new DimPersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        AdvertiserDao advertiserDao = new AdvertiserDaoImpl(context);
        DatasetConfigDao datasetConfigDao = DatasetConfigDaoImpl.instanceFromResources();
        AdmTransactionDao admTransactionDao = AdmTransactionImpl.instanceFromResources();
        DimCostDetailDao dimCostDetailDao = new DimCostDetailDaoImpl(dimContext);
        PackageDaoBase dimPackageDao = new DimPackageDaoImpl(dimContext);
        AgencyManager agencyManager = new AgencyManager(new AgencyDaoImpl(context), datasetConfigDao,
                admTransactionDao, advertiserDao, dimCostDetailDao, dimPackageDao, accessControl);
        ContactManager contactManager = new ContactManager(new ContactDaoImpl(context), accessControl);
        UserDao userDao = new UserDaoImpl(context);
        AdvertiserManager advertiserManager = new AdvertiserManager(advertiserDao, userDao, accessControl);
        BrandDao brandDao = new BrandDaoImpl(context);
        BrandManager brandManager = new BrandManager(brandDao, accessControl);
        CookieDomainDao cookieDomainDao = new CookieDomainDaoImpl(context);
        CookieDomainManager cookieDomainManager = new CookieDomainManager(cookieDomainDao, userDao, accessControl);

        bootstrapManager = new BootstrapManager(agencyManager, contactManager, userManager, advertiserManager, brandManager, cookieDomainManager, accessControl);
    }

    /**
     * Get a bootstrapped agency.
     *
     * @return a DTO of the bootstrapped ids.
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response bootstrapAgencyWithUser() {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_BOOTSTRAP_DATA);
            Either<Errors, Bootstrap> bootstrap = bootstrapManager.bootstrap(oauthKey());
            if (bootstrap.isError()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(bootstrap.error()).build();
            } else {
                return Response.ok(bootstrap.success()).build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(), e);
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Get a bootstrapped user for an existing agency.
     *
     * @param agencyId
     */
    @POST
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response addUsersToExistingAgency(@PathParam("id") Long agencyId) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_BOOTSTRAP_DATA);
            Either<Errors, Bootstrap> bootstrap = bootstrapManager.bootstrap(agencyId, oauthKey());
            if (bootstrap.isError()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(bootstrap.error()).build();
            } else {
                return Response.ok(bootstrap.success()).build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(), e);
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Create some basic entities under an agency for testing.
     *
     * @param agencyId - id of existing agency
     * @return object containing the bootstrapped ids.
     */
    @POST
    @Path("/{id}/basic")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response bootstrapAgencyWithBasicElement(@PathParam("id") Long agencyId) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_BOOTSTRAP_DATA);
            Either<Errors, Bootstrap> bootstrap = bootstrapManager.basicSetup(agencyId, oauthKey());
            if (bootstrap.isError()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(bootstrap.error()).build();
            } else {
                return Response.ok(bootstrap.success()).build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(), e);
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            throw new WebApplicationSystemException(e);
        }
    }
}

package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementCampaignDTO;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.dto.SmEventDTO;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementCampaignType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementGroupDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementCampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementEventDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementGroupDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.SiteMeasurementCampaignManager;
import trueffect.truconnect.api.crud.service.SiteMeasurementEventManager;
import trueffect.truconnect.api.crud.service.SiteMeasurementGroupManager;
import trueffect.truconnect.api.crud.service.SiteMeasurementManager;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * Site Measurement related REST endpoints
 * @author Gustavo Claure
 */
@Path("/SiteMeasurements")
public class SiteMeasurementService extends GenericService {

    private SiteMeasurementManager smManager;
    private SiteMeasurementEventManager eventManager;
    private SiteMeasurementCampaignManager smcManager;
    private SiteMeasurementGroupManager smGroupManager;

    private SiteMeasurementGroupManager manager;


    public SiteMeasurementService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        UserDao userDao = new UserDaoImpl(context);
        SiteMeasurementGroupDao smGroupDao = new SiteMeasurementGroupDaoImpl(context, accessControl);
        SiteMeasurementDao smDao = new SiteMeasurementDaoImpl(context, accessControl);
        smManager = new SiteMeasurementManager(
                new SiteMeasurementDaoImpl(context, accessControl), userDao, accessControl);
        eventManager = new SiteMeasurementEventManager(
                new SiteMeasurementEventDaoImpl(context, accessControl), userDao, accessControl, smDao, smGroupDao);
        smcManager = new SiteMeasurementCampaignManager(new SiteMeasurementCampaignDaoImpl(context),accessControl);
        SiteMeasurementEventDao smEventDao = new SiteMeasurementEventDaoImpl(context, accessControl);
        smGroupManager = new SiteMeasurementGroupManager(new SiteMeasurementGroupDaoImpl(context, accessControl), userDao, smEventDao, smDao, accessControl);

    }

    /**
     * Gets a SiteMeasurement record.
     *
     * @param id The ID of SiteMeasurement
     * @return The SiteMeasurement.
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSiteMeasurement(@PathParam("id") Long id) {
        if(log.isDebugEnabled()){
            log.debug("Entering get by id");
        }
        try {
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            SiteMeasurementDTO result = smManager.get(id, oauthKey());
            return APIResponse.ok(result).build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Gets a RecordSet of SiteMeasurements
     *
     * @param searchCriteria search criteria
     * @return a RecordSet of SiteMeasurements
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSiteMeasurements(@InjectParam SearchCriteria searchCriteria) {
        if(log.isDebugEnabled()){
            log.debug("Entering get by Criteria: " + searchCriteria);
        }
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_SITE_MEASUREMENT_LIST);
            RecordSet<SiteMeasurementDTO> records = smManager.get(searchCriteria, oauthKey());
            return APIResponse.ok(records).build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Saves a <code>SiteMeasurementDTO</code> for the given <code>Site Measurement</code>'s data
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_SITE_MEASUREMENT</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 Created - When the <code>SiteMeasurementDTO</code> is created successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li><code>brandId</code>: Empty</li>
     *     <li><code>cookieDomainId</code>: Empty</li>
     *     <li><code>name</code>: More than 128 characters</li>
     *     <li><code>name</code>: Null or empty</li>
     *     <li><code>notes</code>: More than 200 characters</li>
     *     <li><code>expirationDate</code>: Empty</li>
     *     <li><code>assocMethod</code>: Is not either <code>CLICK</code>, <code>FIRST</code> or <code>CHNL</code></li>
     *     <li><code>clWindow</code>: Is not between 1 and 30 when <code>assocMethod</code> is either <code>CLICK</code> or <code>FIRST</code></li>
     *     <li><code>clWindow</code>: Is not null <code>assocMethod</code> is <code>CHNL</code></li>
     *     <li><code>vtWindow</code>: Is not between 1 and 30 when <code>assocMethod</code> is either <code>CLICK</code> or <code>FIRST</code></li>
     *     <li><code>vtWindow</code>: Is not null <code>assocMethod</code> is <code>CHNL</code></li>
     * </ul>
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
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param siteMeasurement The <code>SiteMeasurementDTO</code> with th Site Measurement data to be created.
     * @return a {@link SiteMeasurementDTO} with the Site Measurement data.
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveSiteMeasurement(SiteMeasurementDTO siteMeasurement) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_SITE_MEASUREMENT);
            Either<Errors, SiteMeasurementDTO> result = smManager.create(siteMeasurement, oauthKey());
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.status(Response.Status.CREATED).entity(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Updates an existing <code>SiteMeasurementDTO</code> for the given <code>Site Measurement</code>'s id
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>UPDATE_SITE_MEASUREMENT</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>SiteMeasurementDTO</code> is updated successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li><code>id</code>: Empty</li>
     *     <li><code>id</code>: Does not match the path id</li>
     *     <li><code>name</code>: More than 128 characters</li>
     *     <li><code>name</code>: Null or empty</li>
     *     <li><code>notes</code>: More than 200 characters</li>
     *     <li><code>assocMethod</code>: Is not either <code>CLICK</code>, <code>FIRST</code> or <code>CHNL</code></li>
     *     <li><code>clWindow</code>: Is not between 1 and 30 when <code>assocMethod</code> is either <code>CLICK</code> or <code>FIRST</code></li>
     *     <li><code>clWindow</code>: Is not null <code>assocMethod</code> is <code>CHNL</code></li>
     *     <li><code>vtWindow</code>: Is not between 1 and 30 when <code>assocMethod</code> is either <code>CLICK</code> or <code>FIRST</code></li>
     *     <li><code>vtWindow</code>: Is not null <code>assocMethod</code> is <code>CHNL</code></li>
     * </ul>
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
     * @param id The <code>SiteMeasurementDTO</code> id
     * @param siteMeasurement The <code>SiteMeasurementDTO</code> with th Site Measurement data to be updated.
     * @return a {@link SiteMeasurementDTO} with the Site Measurement data.
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response update(@PathParam("id") Long id, SiteMeasurementDTO siteMeasurement) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_SITE_MEASUREMENT);
            Either<Errors, SiteMeasurementDTO> result = smManager.update(id, siteMeasurement, oauthKey());
            if(result.isError()) {
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
     * Retrieves a recordSet of <code>SiteMeasurementCampaignDTO</code>
     * These can be associated ones or unassociated
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_SITE_MEASUREMENT_CAMPAIGNS_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a recordSet of <code>SiteMeasurementCampaignDTO</code> is returned successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>id</li> is null
     *     <li>type</li> is out of <code>ASSOCIATED</code> or <code>UNASSOCIATED</code> options
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Permission Denied</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Permission Not Allowed</li>
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
     * @param id The <code>Site Measurement</code> id
     * @return a recordSet of <code>SiteMeasurementCampaignDTO</code>
     */
    @GET
    @Path("/{id}/campaigns")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSmCampaigns(@PathParam("id") Long id,
                                             @QueryParam("type") @DefaultValue("ASSOCIATED")
                                             SiteMeasurementCampaignType type,
                                             @QueryParam("startIndex") Long startIndex,
                                             @QueryParam("pageSize") Long pageSize) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_SITE_MEASUREMENT_CAMPAIGNS_LIST);
            Either<Error, RecordSet<SiteMeasurementCampaignDTO>> result = smcManager
                    .getCampaignsForSiteMeasurement(id, type, startIndex, pageSize, oauthKey());

            if(result.isError()) {
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
     * Updates the relationship between <code>SiteMeasurement</code> and <code>Campaign</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>UPDATE_SITE_MEASUREMENT_CAMPAIGN_ASSOCS</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - All relationships of the <code>smCampaigns</code> have been updated successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>id</li> is null
     *     <li>smCampaigns</li> is null or empty
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Permission Denied</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Permission Not Allowed</li>
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
     * @param id The <code>Site Measurement</code> id
     * @param smCampaigns The <code>RecordSet</code> containing all relationships
     * @return a <code>RecordSet</code> of <code>SiteMeasurementCampaignDTO</code>s
     */
    @PUT
    @Path("/{id}/campaigns")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveSmCampaign(@PathParam("id") Long id, RecordSet<SiteMeasurementCampaignDTO> smCampaigns) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_SITE_MEASUREMENT_CAMPAIGN_ASSOCS);
            Either<Error, RecordSet<SiteMeasurementCampaignDTO>> result = smcManager.save(id,
                    smCampaigns, oauthKey());

            if(result.isError()) {
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
     * Gets a SiteMeasurement record.
     *
     * @param id The ID of SiteMeasurement
     * @return The SiteMeasurement.
     */
    @GET
    @Path("/{id}/events")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSiteMeasurementEvents(@PathParam("id") Long id) {
        if(log.isDebugEnabled()){
            log.debug("Entering get SmEvents by SiteMeasurement: " + id);
        }
        try {
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            RecordSet<SmEventDTO> records = eventManager.getSmEventsBySiteMeasurement(id, oauthKey());
            return APIResponse.ok(records).build();
        } catch (SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Retrieves a recordSet of <code>SmGroups</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_SITE_MEASUREMENT_GROUP_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a recordSet of <code>SmGroups</code> is returned successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>id</li> is null
     * </ul>
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
     * @param id The <code>Site Measurement </code> id
     * @return a recordSet of <code>SmGroup</code>
     */
    @GET
    @Path("/{id}/groups")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSiteMeasurementGroups(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_SITE_MEASUREMENT_GROUP_LIST);
            Either<Error, RecordSet<SmGroup>>
                    records = smGroupManager.getSiteMeasurementGroups(id, oauthKey());
            if(records.isError()) {
                return handleErrorCodes(records.error());
            } else {
                return APIResponse.ok(records.success()).build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }
}

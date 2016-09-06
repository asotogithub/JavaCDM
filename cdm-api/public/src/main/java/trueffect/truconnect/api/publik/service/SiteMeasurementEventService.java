package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.BooleanResponse;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementGroupDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementEventDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementGroupDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.SiteMeasurementEventManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.InjectParam;

/**
 * Created by richard.jaldin on 6/8/2015.
 */
@Path("/SiteMeasurementEvents")
public class SiteMeasurementEventService extends GenericService {

    private SiteMeasurementEventManager manager;

    public SiteMeasurementEventService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        UserDao userDao = new UserDaoImpl(context);
        SiteMeasurementGroupDao smGroupDao = new SiteMeasurementGroupDaoImpl(context, accessControl);
        SiteMeasurementDao smDao = new SiteMeasurementDaoImpl(context, accessControl);

        manager = new SiteMeasurementEventManager(new SiteMeasurementEventDaoImpl(context, accessControl), userDao,
                accessControl, smDao, smGroupDao);
    }

    /**
     * Saves a {@code SmEvent}.
     *
     * @param sme The {@code SmEvent} object to save.
     * @return The {@code SmEvent} already saved on the database.
     */

    /**
     * Returns the created <code>SmEvent</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_SITE_MEASUREMENT_EVENT</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>SmEvent</code> is retrieved successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>SmGroup Id</li> is null
     *     <li>smEvent</li> is null
     *     <li><Event Name/li> is blank
     *     <li><Event Name/li> is length is greater than 20
     *     <li><Event Name/li> has blank spaces
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
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param sme The <code>Site Measurement Event</code> .
     * @return a {@link SmEvent} with the SM Event data.
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveSmEvent(SmEvent sme) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_SITE_MEASUREMENT_EVENT);
            Either<Error, SmEvent> result = manager.create(sme, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return APIResponse.created(result.success()).build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns a <code>SmEvent</code> for a given <code>Site Measurement Id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_SITE_MEASUREMENT_PING_EVENT_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>SmEvent</code> is retrieved successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>smEvent Id is incorrect</li>
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
     * @param id The <code>Site Measurement Id</code> id
     * @return a {@link SmEvent} with the SM Event data.
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSmPingsByEvents(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_SITE_MEASUREMENT_PING_EVENT_LIST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmEvent>
                    records = manager.getSmPingsByEvents(id,oauthKey());
            if(records.isError()) {
                return handleErrorCodes(records.error());
            } else {
                return APIResponse.ok(records.success()).build();
            }
        } catch (Exception e) {
            log.warn("Error while retrieving ping events.  EventId:" + id, e);
            throw new WebApplicationSystemException(e);
        }
    }

    @POST
    @Path("/{id}/pingEvents")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response createPingsByEvent(@PathParam("id") Long id, RecordSet<SmPingEventDTO> records) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.AUTOMATED_TEST);
            Either<Error, RecordSet<SmPingEventDTO>> result = manager.createSmPingsByEvents(id, records, oauthKey());

            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return APIResponse.created(result.success()).build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Updates a <code>SmEvent</code> for a given <code>Site Measurement Event</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>UPDATE_SITE_MEASUREMENT_EVENT</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>SmEvent</code> is updated successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>smEvent Id is incorrect</li>
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
     * @param id The <code>Site Measurement Event Id</code> id
     * @return a {@link SmEvent} with the SM Event data.
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateSMEvent(@PathParam("id") Long id, SmEvent sme) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_SITE_MEASUREMENT_EVENT);

            Either<Errors, SmEvent> result = manager.update(id, sme, oauthKey());

            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return APIResponse.ok(result.success()).build();
            }
        } catch (SystemException e) {
            log.warn(e.getMessage(),e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns <code>BooleanResponse</code> given a <code>name</code> for the <code>Event</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CHECK_SITE_MEASUREMENT_EVENT</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When a <code>SiteMeasurementEvent</code> with the <code>name</code> does not exists
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>criteria</li> is null
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
     * @param groupId The <code>Sm Group</code> id
     * @param name The <code>SiteMeasurementEvent</code>'s name
     * @return a <code>BooleanResponse</code>
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response isEventNameExist(@QueryParam("groupId") Long groupId, @QueryParam("name") String name) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CHECK_SITE_MEASUREMENT_EVENT);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean>
                    result = manager.isEventNameExist(groupId, name, oauthKey());
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(new BooleanResponse(result.success())).build();
            }
        } catch (Exception e) {
            log.warn("Failed to get the existence of the event name", e);
            throw new WebApplicationSystemException(e);
        }
    }
}

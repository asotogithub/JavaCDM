package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventPingsDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementEventPingsDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.SiteMeasurementEventPingsManager;


import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by jesus.nunez on 8/12/2016.
 */

@Path("/SiteMeasurementEventPings")
public class SiteMeasurementEventPingsService extends GenericService {

    private SiteMeasurementEventPingsManager smPingManager;

    public SiteMeasurementEventPingsService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        UserDao userDao = new UserDaoImpl(context);
        SiteMeasurementEventPingsDao pingDao = new SiteMeasurementEventPingsDaoImpl(context);
        this.smPingManager = new SiteMeasurementEventPingsManager(userDao, pingDao, accessControl);

    }


    /**
     * Deletes one or multiple Site Measurement Pings ids contained in a <code>RecordSet</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>DELETE_HTML_INJECTION_TAG_ASSOCIATION</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>SmPing</code> ids could
     * be deleted successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple validation errors in the given query
     * parameters or body payload. Validations rules are:
     * <ul>
     *     <li><code>RecordSet</code> of ids should not be empty</li>
     *     <li><code>Records</code> of ids should not be null</li>
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
     * @param smPingIds The <code>RecordSet</code> of SiteMeasurement Pings Ids
     * @return a <code>SuccessResponse</code> with a success message
     */
    @PUT
    @Path("/deletePingEvents")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response deletePingEvent(RecordSet<Long> smPingIds) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.DELETE_SM_EVENT_PINGS);
            Either<Errors, String> result = smPingManager.deletePingEvent(smPingIds, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(new SuccessResponse(result.success())).build();
            }
        } catch (SystemException e) {
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Creates a bulk of <code>Site Measurement Event Pings</code> for a given <code>RecordSet</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_SM_EVENT_PINGS</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 Created - When the <code>SmPingEventDTO</code>'s are created successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>records are incorrect</li>
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an authentication error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Access Token Invalid</li>
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
     * @param records The <code>Site Measurement Event Pings</code> to create.
     * @return a RecordSet of {@link SmPingEventDTO} with the SM Event Ping data.
     */
    @POST
    @Path("/bulkCreate")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response bulkCreatePings(RecordSet<SmPingEventDTO> records) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_SM_EVENT_PINGS);

            Either<Errors, RecordSet<SmPingEventDTO>> result = smPingManager.createPingEvents(records, oauthKey());

            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return APIResponse.created(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Updates a bulk of <code>Site Measurement Event Pings</code> for a given <code>RecordSet</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>UPDATE_SM_EVENT_PINGS</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>SmPingEventDTO</code>'s are created successfully.
     *
     * @HTTP 400 Bad Request - When there are one or multiple errors in the given query
     * parameters
     * <ul>
     *     <li>records are incorrect</li>
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an authentication error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Access Token Invalid</li>
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
     * @param records The <code>Site Measurement Event Pings</code> to update.
     * @return a RecordSet of {@link SmPingEventDTO} with the SM Event Ping data.
     */
    @PUT
    @Path("/bulkUpdate")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response bulkUpdatePings(RecordSet<SmPingEventDTO> records) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_SM_EVENT_PINGS);

            Either<Errors, RecordSet<SmPingEventDTO>> result = smPingManager.updatePingEvents(records, oauthKey());

            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return APIResponse.ok(result.success()).build();

            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }
}

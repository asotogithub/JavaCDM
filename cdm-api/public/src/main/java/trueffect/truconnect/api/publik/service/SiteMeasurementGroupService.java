package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.BooleanResponse;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementGroupDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementEventDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementGroupDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.SiteMeasurementEventManager;
import trueffect.truconnect.api.crud.service.SiteMeasurementGroupManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.InjectParam;

/**
 * Created by richard.jaldin on 6/6/2015.
 */
@Path("/SiteMeasurementGroups")
public class SiteMeasurementGroupService extends GenericService {

    private SiteMeasurementGroupManager manager;
    private SiteMeasurementEventManager eventMgr;

    public SiteMeasurementGroupService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        UserDao userDao = new UserDaoImpl(context);
        SiteMeasurementEventDao smEventDao = new SiteMeasurementEventDaoImpl(context, accessControl);
        SiteMeasurementGroupDao smGroupDao = new SiteMeasurementGroupDaoImpl(context, accessControl);
        SiteMeasurementDao smDao = new SiteMeasurementDaoImpl(context, accessControl);

        manager = new SiteMeasurementGroupManager(new SiteMeasurementGroupDaoImpl(context, accessControl), userDao, smEventDao, smDao, accessControl);
        eventMgr = new SiteMeasurementEventManager(new SiteMeasurementEventDaoImpl(context, accessControl), userDao, accessControl,smDao, smGroupDao);
    }


    /**
     * Verifies if a <code>SmGorup Name</code> exists
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CHECK_SITE_MEASUREMENT_GROUP</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>SmGroup</code> name exists.
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
     * @param name The <code>Sm group name</code>
     * @param smId The <code>Sm group Id</code>
     * @return true if a Group name already exist
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response isGroupNameExist(@QueryParam("smId") Long smId, @QueryParam("name") String name) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CHECK_SITE_MEASUREMENT_GROUP);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean>
                    result = manager.isSmGroupNameExist(smId, name, oauthKey());
            if(result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(new BooleanResponse(result.success())).build();
            }
        } catch (Exception e) {
            log.warn("Failed to get the existence of the Sm Group Name", e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Creates a <code>SmGroup</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>CREATE_SITE_MEASUREMENT_GROUP</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>SmGroup</code> were created successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>siteMeasurementId</code>: Should not be empty or null</li>
     *     <li><code>groupName</code>: Should not be empty</li>
     *     <li><code>groupName</code>: Length is greater than 20 </li>
     *     <li><code>groupName</code>: Should not contains blank spaces</li>
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     *     <li>Permission Denied</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs.
     *
     * @param smg a {@link SmGroup} to create
     * @return A new <code>SmGroup</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveSmGroup(SmGroup smg) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.CREATE_SITE_MEASUREMENT_GROUP);
            Either<Error, SmGroup> result = manager.createSmGroup(smg, oauthKey());
            log.debug("Saving {} Site Measurement Group", smg);
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
}


package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.Trafficking;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.TraffickingDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.TraffickingDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.CampaignManager;
import trueffect.truconnect.api.crud.service.TraffickingManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.ws.WebServiceException;

/**
 * Set of REST Services related to <code>Trafficking</code> management.
 *
 * @author Abel Soto
 * @author Thomas Barjou
 */
@Path("/Trafficking")
public class TraffickingConsumerService extends GenericService {

    private TraffickingManager traffickingManager;
    private CampaignManager campaignManager;

    public TraffickingConsumerService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        TraffickingDao traffickingDao = new TraffickingDaoImpl(context);
        UserDao userDao = new UserDaoImpl(context, accessControl);
        CampaignDao campaignDao = new CampaignDaoImpl(context);
        CreativeDao creativeDao = new CreativeDaoImpl(context);
        this.traffickingManager = new TraffickingManager(traffickingDao,
                userDao,
                campaignDao,
                accessControl);
        this.campaignManager = new CampaignManager(campaignDao, null, null, null, creativeDao, accessControl);
    }

    public TraffickingConsumerService(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    /**
     * Trafficates a <code>Campaign</code> given its <code>Trafficking</code> parameters
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
     * @HTTP 200 OK - When the <code>Trafficking</code> has been sent successfully to AdServer  
     * <b>Important: the API Success does not guaranty the Campaign will be successfully trafficked by AdServer</b>
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>campaignId</code>: Not empty</li>
     *     <li><code>Campaign</code>: Does not have <code>Schedule</code> to traffick</li>
     *     <li><code>cookieDomainId</code>: Not empty</li>
     *     <li><code>cookieDomainId</code>: Can be changed to another cookieDomainId from the Agency and different from the Campaign one, But only when it's the first traffick. Once trafficked, the Campaign cannot change its CookieDomainId</li>
     *     <li><code>agencyContacts</code>: Not empty</li>
     *     <li><code>siteContacts</code>: Not empty</li>
     *     <li><code>currentContactId</code>: Not empty</li>
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
     *     <li>No record was for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param trafficking The <code>Trafficking</code> object with Campaign id and trafficking Contacts
     * @return a Jersey <code>Response</code>
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response trafficCampaign(Trafficking trafficking) throws Exception {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result = traffickingManager.trafficCampaign(
                    trafficking, oauthKey(), new TraffickingManager.ExternalTrafficker());

            if (result.isError()) {
                return handleErrorCodesAsLegacyErrors(result.error());
            } else {
                SuccessResponse response = new SuccessResponse("Campaign successfully trafficked.");
                return Response.ok(response).build();
            }
        } catch (WebServiceException e) {
            log.warn("Service Exception", e);
            return APIResponse.serviceUnavailable("Web Service Not reachable").build();
        } catch (Exception e) {
            log.warn("Unexpected exception", e);
            return APIResponse.error("Sorry, something went wrong.");
        }
    }


    @POST
    @Path("/test")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response trafficCampaignMock(Trafficking trafficking){
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.AUTOMATED_TEST);
            Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Void> result = traffickingManager.trafficCampaign(
                    trafficking, oauthKey(), new TraffickingManager.TFAMockTrafficker(campaignManager, oauthKey()));

            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                SuccessResponse response = new SuccessResponse(ResourceBundleUtil.getString("campaign.info.trafficSuccess"));
                return Response.ok(response).build();
            }
        } catch (Exception e) {
            log.warn("Unexpected exception", e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Validates if a <code>Campaign</code> is eligible for trafficking
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
     * @HTTP 200 OK - When the <code>Campaign</code> is valid for Trafficking 
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>campaignId</code>: Not empty</li>
     *     <li><code>Campaign</code>: Does not have <code>Schedule</code> to traffick</li>
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
     *     <li>No record was for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 500 Internal Server Error - When an unknown error occurs
     *
     * @param campaignId The <code>Campaign</code> id to validate
     * @return a Jersey <code>Response</code>
     */    @GET
    @Path("/validate")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response validateCampaign(@QueryParam("campaignId") Long campaignId) {

        log.info("Starting Campaign validation before trafficking, campaignId = {}", campaignId);
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            Errors result = traffickingManager.checkCampaign(campaignId, oauthKey());
            if (result.getErrors().isEmpty()) {
                log.info("Finishing Campaign validation before trafficking successfully");
                return APIResponse.ok(new SuccessResponse("Campaign " + campaignId + " successfully validated.")).build();
            } else {
                return APIResponse.bad(result).build();
            }
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Bad Request", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (DataNotFoundForUserException e) {
            log.warn(String.format("Data not found for %s : %s", oauthKey().toString(), campaignId), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn("Unexpected exception", e);
            return APIResponse.error(e.getMessage());
        }
    }
}

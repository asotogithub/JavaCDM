package trueffect.truconnect.api.tpasapi.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.model.Trafficking;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.tpasapi.proxy.TraffickingConsumerProxy;

/**
 * Set of REST Services related to <code>Trafficking</code> management.
 *
 * @author Abel Soto
 * @author Thomas Barjou
 */
@Path("/Trafficking")
public class TraffickingConsumerService extends GenericService {

    public TraffickingConsumerService() {
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
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>/Advertisers/{id}/site/{siteId}/tagTypes</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     * 
     * @HTTP 200 OK - When the <code>Trafficking</code> has been sent successfully to AdServer  
     * <b>Important: the API Success does not guaranty the Campaign will be successfully trafficked by AdServer</b>
     *
     * @param trafficking The <code>Trafficking</code> object with Campaign id and trafficking Contacts
     * @return a Jersey <code>Response</code>
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response trafficCampaignAdjustTimeZone(Trafficking trafficking) {

        if (StringUtils.isBlank(trafficking.getDomain())) {
            log.debug("Campaign Domain is a required field.");
        }
        TraffickingConsumerProxy proxy = new TraffickingConsumerProxy(headers);
        try {
            Response resProxy = proxy.saveTrafficking(trafficking);
            if (resProxy.getStatus() == Response.Status.OK.getStatusCode()) {
                log.info("Saved "+ trafficking);
                return APIResponse.ok().build();
            } else {
                return resProxy;
            }

        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }
}

package trueffect.truconnect.api.tpasapi.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.TagType;
import trueffect.truconnect.api.tpasapi.proxy.TagTypeProxy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Set of REST Services related to <code>Advertiser</code> management.
 * 
 * @author Rambert Rioja
 * @author Marcelo Heredia
 */
@Path("/Advertisers")
public class AdvertiserService extends GenericService {


    /**
     * Returns an <code>Advertiser</code> given its <code>id</code>
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
     *     <code>GET /Advertisers/{id}/site/{siteId}/tagTypes</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>TagType</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found
     *
     * @param id The <code>Advertiser</code> id
     * @param siteId The <code>Site</code> id
     * @return a <code>Tag Types</code>
     */
    @GET
    @Path("/{id}/site/{siteId}/tagTypes")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getTagTypes(@PathParam("id") Long id, @PathParam("siteId") Long siteId) {
        try {
            TagTypeProxy proxy = new TagTypeProxy(headers);
            proxy.path(Long.toString(id));
            proxy.path("site");
            proxy.path(Long.toString(siteId));
            proxy.path("tagTypes");
            RecordSet<TagType> list = proxy.getTagType();
            return APIResponse.ok(list).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: " , e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }
}

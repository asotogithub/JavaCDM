package trueffect.truconnect.api.tpasapi.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.tpasapi.model.Cookie;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.proxy.CookieProxy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Set of REST Services related to <code>Domain</code> management.
 * 
 * @author Richard Jaldin
 */
@Path("/Domains")
public class DomainService extends GenericService {

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>Cookie</code> by <code>Domain</code> id
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint: 
     *     <code>GET /CookieDomains/{id}/cookies</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     * </ul>
     * 
     * @param id The <code>Domain</code> id
     * @return a {@link RecordSet<Cookie>} with the Cookies found for the given <code>Domain</code>
     */
    @GET
    @Path("{id}/cookies")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCookiesByDomain(@PathParam("id") Long id) {
        CookieProxy proxy = new CookieProxy(headers);
        try {
            RecordSet<Cookie> cookies = proxy.getCookiesByDomain(id);
            return APIResponse.ok(cookies).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        }
    }
}

package trueffect.truconnect.api.tpasapi.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.proxy.CampaignProxy;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * Set of REST Services related to <code>Campaign</code> management.
 * 
 * @author Abel Soto
 */
@Path("/Campaigns")
public class CampaignService extends GenericService {

    @Context
    protected UriInfo uriInfo;

    public CampaignService() {
    }

    public CampaignService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Creates a Creative File and record
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
     *     <code>POST /Campaigns/{id}/Creative</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li><code>campaignId</code>: Is null</li>
     *     <li>No record was for the given parameter(s)</li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>Creative</code> was created successfully.
     *
      * @param id The <code>Campaign</code> id.
     * @param inputStream The <code>File</code> to be uploaded
     * @param filename The <code>File</code> name
     * @return The Creative.
     */
    @POST
    @Path("{id}/Creative")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response saveCreativeFile(@PathParam("id") Long id, InputStream inputStream,
            @QueryParam("filename") String filename) {
        CampaignProxy proxy = new CampaignProxy(headers);
        try {
            if (id != null) {
                proxy.path(id.toString());
                proxy.path("Creative");
                proxy.query("filename", filename);
                Creative creative = proxy.saveCreativeImage(inputStream, filename);
                ResponseBuilder responseSW = APIResponse.created(creative, uriInfo, "Creatives");
                log.info("Saved "+ id);
                return responseSW.build();
            } else {
                return APIResponse.bad("The Campaign id cannot be empty.").build();
            }
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return APIResponse.generic(e.getErrorResponse().getStatus(), e.getErrorResponse().getEntity()).build();
        } catch (Exception e) {
            log.warn("Service Exception: ", e);
            return APIResponse.error(e.getMessage());
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("Error while closing stream", e);
                }
            }
        }
    }
}

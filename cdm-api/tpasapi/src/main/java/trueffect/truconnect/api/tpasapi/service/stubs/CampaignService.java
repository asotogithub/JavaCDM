package trueffect.truconnect.api.tpasapi.service.stubs;

import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import trueffect.truconnect.api.tpasapi.service.GenericService;
import trueffect.truconnect.api.tpasapi.util.BeanFactory;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.commons.APIResponse;

/**
 *
 * @author Gustavo Claure
 */
@Path("/stubs/Campaigns")
public class CampaignService extends GenericService {

    /**
     * Save a Creative File and record.
     *
     * @param id The ID of Campaign
     * @param inputStream File upload
     * @param filename File name
     * @return The Creative.
     */
    @POST
    @Path("{id}/Creative")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveCreativeFile(@PathParam("id") Long id,
                                     InputStream inputStream,
                                     @QueryParam("filename") String filename) {
        try {
            Creative creative = BeanFactory.getCreative(id);

            if (filename.toLowerCase().contains(".png")) {
                return APIResponse.notAcceptable("File type not accepted.");
            }
            if (id.equals(creative.getCampaignId())) {
                creative.setAlias(creative.getAlias());
                creative.setClickthroughs(creative.getClickthroughs());
                creative.setExtendedProperty1(creative.getExtendedProperty1());
                creative.setExtendedProperty2(creative.getExtendedProperty2());
                creative.setExtendedProperty3(creative.getExtendedProperty3());
                creative.setExtendedProperty4(creative.getExtendedProperty4());
                creative.setExtendedProperty5(creative.getExtendedProperty5());
                creative.setFilename(creative.getFilename());
                creative.setIsExpandable(creative.getIsExpandable());
                creative.setPurpose(creative.getPurpose());
                log.info("Saved "+id);
                return APIResponse.created(creative).build();
            } else {
                return APIResponse.bad(
                        "Entity in request body does not match the requested id.").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        finally {
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

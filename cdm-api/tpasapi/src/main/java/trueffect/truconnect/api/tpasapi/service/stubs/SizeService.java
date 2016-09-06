package trueffect.truconnect.api.tpasapi.service.stubs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.tpasapi.service.GenericService;
import trueffect.truconnect.api.tpasapi.util.BeanFactory;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Size;

/**
 *
 * @author Rambert Rioja
 */
@Path("/stubs/Sizes")
public class SizeService extends GenericService {

    /**
     * Gets a Size record based on its id
     *
     * @param id Size ID
     * @return The Placement.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getSize(@PathParam("id") Long id) {
        try {
            Size size = BeanFactory.getSize();
            size.setId(id);
            return APIResponse.ok(size).build();
        } catch (Exception e) {
            log.warn("Error getting Size", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Gets Size records.
     *
     * @param query Encoded query
     * @return The RecordSet.
     */
    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getSizes(@QueryParam("query") String query) {
        try {
            RecordSet<Size> records = BeanFactory.getSizes();
            return APIResponse.ok(records).build();
        } catch (Exception e) {
            log.warn("Error getting Sizes", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Saves a Size object.
     *
     * @param size The Size object to save.
     * @return The Size already saved on the database.
     */
    @POST
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response saveSize(Size size) {
        try {
            if(size.getAgencyId() == null) {
                return APIResponse.bad("Invalid agencyId, it cannot be empty.").build();
            }
            if(size.getHeight() == null) {
                return APIResponse.bad("Invalid height, it cannot be empty.").build();
            }
            if(size.getWidth() == null) {
                return APIResponse.bad("Invalid width, it cannot be empty.").build();
            }
            if(StringUtils.isBlank(size.getLabel())) {
                size.setLabel(size.getHeight() + "x" + size.getWidth());
            }
            else if(size.getLabel().length() > 256) {
                return APIResponse.bad("Invalid name, it supports characters up to 256.").build();
            }

            Long id = (long) (Math.random() * 1000);
            size.setId(id);
            log.info("Saved " +id);
            return APIResponse.created(size).build();
        } catch (Exception e) {
            log.warn("Error saving Size", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }
}

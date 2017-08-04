package trueffect.truconnect.api.tpasapi.service.stubs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.tpasapi.service.GenericService;
import trueffect.truconnect.api.tpasapi.util.BeanFactory;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.TargetType;
import trueffect.truconnect.api.tpasapi.model.TargetValue;

/**
 *
 * @author Rambert Rioja
 */
@Path("/stubs/TargetTypes")
public class TargetTypeService extends GenericService {

    /**
     * Gets TargetType records.
     *
     * @param query Encoded query
     * @return The RecordSet.
     */
    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getTargetTypes() {
        try {
            RecordSet<TargetType> records = BeanFactory.getTargetTypes();
            return APIResponse.ok(records).build();
        } catch (Exception e) {
            log.warn("Error getting TargetTypes", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Gets TargetValue records.
     *
     * @param query Encoded query
     * @return The RecordSet.
     */
    @GET
    @Path("{code}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getTargetValues(@PathParam("code") String code) {
        try {
            RecordSet<TargetValue> records = BeanFactory.getTargetValues(code);
            return APIResponse.ok(records).build();
        } catch (Exception e) {
            log.warn("Error getting TargetValues", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }
}

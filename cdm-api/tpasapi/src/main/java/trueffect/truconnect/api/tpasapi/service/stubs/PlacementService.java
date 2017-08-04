package trueffect.truconnect.api.tpasapi.service.stubs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import trueffect.truconnect.api.tpasapi.model.Placement;
import trueffect.truconnect.api.tpasapi.model.RecordSet;

/**
 *
 * @author Rambert Rioja
 */
@Path("/stubs/Placements")
public class PlacementService extends GenericService {

    /**
     * Gets a Placement record based on its id
     *
     * @param id Placement ID
     * @return The Placement.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getPlacement(@PathParam("id") Long id) {
        try {
            Placement placement = BeanFactory.getPlacement();
            placement.setId(id);
            return Response.status(Response.Status.OK).entity(placement).build();
        } catch (Exception e) {
            log.warn("Error getting Placement", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Gets Placement records.
     *
     * @param query Encoded query
     * @return The RecordSet.
     */
    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getPlacements(@QueryParam("query") String query) {
        try {
            RecordSet<Placement> records = BeanFactory.getPlacements();
            return Response.status(Response.Status.OK).entity(records).build();
        } catch (Exception e) {
            log.warn("Error getting Placements", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Saves a Placement.
     *
     * @param placement The Placement object to save.
     * @return The Placement already saved on the database.
     */
    @POST
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response savePlacement(Placement placement) {
        try {
            if (placement.getId() != null) {
                return APIResponse.bad("Invalid campaignId, it cannot be empty.").build();
            }
            if (placement.getSiteId() == null) {
                return APIResponse.bad("Invalid siteId, it cannot be empty.").build();
            }
            if (placement.getCampaignId() == null) {
                return APIResponse.bad("Invalid campaignId, it cannot be empty.").build();
            }

            if (StringUtils.isBlank(placement.getName())) {
                return APIResponse.bad("Invalid name, it cannot be empty.").build();
            } else if (placement.getName().length() > 128) {
                return APIResponse.bad("Invalid name, it supports characters up to 128.").build();
            }

            Long id = (long) (Math.random() * 1000);
            placement.setId(id);
            log.info("Saved "+ id);
            return APIResponse.created(placement).build();
        } catch (Exception e) {
            log.warn("Error saving Placement", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }

    /**
     * Updates a Placement.
     *
     * @param id The ID of Placement
     * @param placement The Placement object to save.
     * @return The Placement already saved on the database.
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response updatePlacement(@PathParam("id") Long id, Placement placement) {
        try {
            if (!StringUtils.isBlank(placement.getName()) && placement.getName().length() > 128) {
                return APIResponse.bad("Invalid name, it supports characters up to 128.").build();
            }

            if (id == null || placement.getId() == null || id.compareTo(placement.getId()) != 0) {
                return APIResponse.notFound("Ids do not match.").build();
            }
            return APIResponse.ok(placement).build();
        } catch (Exception e) {
            log.warn("Error updating Placement", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }
}

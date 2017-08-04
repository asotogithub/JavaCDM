package trueffect.truconnect.api.tpasapi.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.tpasapi.model.Placement;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Tag;
import trueffect.truconnect.api.tpasapi.proxy.PlacementProxy;
import trueffect.truconnect.api.tpasapi.proxy.TagProxy;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * Set of REST Services related to <code>Placement</code> management.
 *
 * @author Rambert Rioja
 */
@Path("/Placements")
public class PlacementService extends GenericService {

    @Context
    protected UriInfo uriInfo;

    public PlacementService() {
    }

    public PlacementService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Returns a <code>Placement</code> given its <code>id</code>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /Placements/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation</li>
     * </ul>
     *
     * @param id of the <code>Placement</code> id
     * @return Placement that matches the given <code>id</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPlacement(@PathParam("id") Long id) {
        PlacementProxy proxy = new PlacementProxy(headers);
        try {
            proxy.path(Long.toString(id));
            Placement placement = proxy.getPlacement();
            return Response.ok(placement).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: " , e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>Placement</code>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /Placements</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation</li>
     * </ul>
     *
     * @param searchCriteria {@link SearchCriteria} that provides the filtering criteria
     * @return a {@link RecordSet<Placement>} with the Placements found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPlacements(@InjectParam SearchCriteria searchCriteria) {
        PlacementProxy proxy = new PlacementProxy(headers);
        try {
            RecordSet<Placement> placements = proxy.getPlacements(searchCriteria);
            return Response.ok(placements).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a Placement.
     *
     * @param placement The Placement object to save.
     * @return The Placement already saved on the database.
     */
    /**
     * Updates an existing <code>Placement</code>.
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>POST /Placements</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request, or:
     * <ul>
     *     <li>The <code>placement.campaign</code> Id is null</li>
     * </ul>
     *
     * @param placement <code>Placement</code> to be saved.
     * @return A message indicating that placement was created
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response savePlacement(Placement placement) {
        PlacementProxy proxy = new PlacementProxy(headers);
        try {
            if (placement.getCampaignId() == null) {
                return APIResponse.bad("Invalid campaignId, it cannot be empty.").build();
            }
            placement = proxy.savePlacement(placement);
            ResponseBuilder response = APIResponse.created(placement, uriInfo, "Placements");
            proxy.copyHeaders(response);
            log.info("Saved "+placement);
            return response.build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>Placement</code>.
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>PUT /Placements</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation</li>
     * </ul>
     *
     * @param id The <code>Placement</code> id
     * @param placement <code>Placement</code> file
     * @return A <code>Placement</code> updated given the provided parameters
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updatePlacement(@PathParam("id") Long id, Placement placement) {
        PlacementProxy proxy = new PlacementProxy(headers);
        try {
            proxy.path(id.toString());
            
            if (placement.getId() != null && id != null 
                    && !id.equals(placement.getId())) {
                return APIResponse.bad("Entity in request body does not match the requested id.").build();
            }
            placement = proxy.updatePlacement(placement);
            ResponseBuilder response = APIResponse.ok(placement);
            proxy.copyHeaders(response);
            log.info("Updated "+id);
            return response.build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: " , e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }


    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>Tag</code> given the
     * <code>Placement</code> and <code>TagType</code>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>/Placements/{id}/Tag/{tagTypeId}</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation</li>
     * </ul>
     *
     * @param id The <code>Placement</code> id
     * @param tagTypeId The <code>TagType</code> id
     * @return a {@link RecordSet<Tag>} with the Tags found
     */
    @GET
    @Path("{id}/Tag/{tagTypeId}")
    public Response getTagId(@PathParam("id") Long id, @PathParam("tagTypeId") Long tagTypeId) {
        try {
            TagProxy proxy = new TagProxy(headers);
            proxy.path(id.toString());
            proxy.path("Tag");
            proxy.path(tagTypeId.toString());
            Tag tag = proxy.getTagByTypeId();
            return APIResponse.ok(tag).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: " , e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }
}

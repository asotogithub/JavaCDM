package trueffect.truconnect.api.tpasapi.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.tpasapi.model.Publisher;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.proxy.PublisherProxy;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * Set of REST Services related to <code>Publisher</code> management.
 *
 * @author Abel Soto
 */
@Path("/Publishers")
public class PublisherService extends GenericService {

    public PublisherService() {
    }

    public PublisherService(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    /**
     * Returns a <code>Publisher</code> given its <code>id</code>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /Publishers/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation.</li>
     * </ul>
     *
     * @param id The <code>Publisher</code> id.
     * @return a <code>Publisher</code> of the id.
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response get(@PathParam("id") Long id) {
        PublisherProxy proxy = new PublisherProxy(headers);
        try {
            proxy.path(id.toString());
            Publisher publisher = proxy.getPublisher();
            return APIResponse.ok(publisher).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>Publisher</code>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /Publishers</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation.</li>
     * </ul>
     *
     * @param searchCriteria {@link SearchCriteria} that provides the filtering criteria
     * @return a {@link RecordSet<Publisher>} with the Publishers found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPublishers(@InjectParam SearchCriteria searchCriteria) {
        PublisherProxy proxy = new PublisherProxy(headers);
        try {
            RecordSet<Publisher> publishers = proxy.getPublishers(searchCriteria);
            return APIResponse.ok(publishers).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>Publisher</code> with the given <code>Agency</code>.
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>POST /Publishers</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation.</li>
     * </ul>
     *
     * @param publisher <code>Publisher</code> to create
     * @return A new <code>Publisher</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response save(trueffect.truconnect.api.tpasapi.model.Publisher publisher) {
        PublisherProxy proxy = new PublisherProxy(headers);
        try {
            publisher = proxy.savePublisher(publisher);
            ResponseBuilder response = APIResponse.created(publisher, uriInfo, "Publishers");
            proxy.copyHeaders(response);
            log.info("Saved "+publisher);
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
     * Updates an existing <code>Publisher</code>.
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>PUT /Publishers/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation.</li>
     * </ul>
     *
     * @param id The <code>Publisher</code> id
     * @param publisher <code>Publisher</code> file
     * @return A <code>Publisher</code> updated given the provided parameters
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response update(@PathParam("id") Long id, Publisher publisher) {
        PublisherProxy proxy = new PublisherProxy(headers);
        try {
            proxy.path(id.toString());
            Publisher result = proxy.putPublisher(publisher);
            Response.ResponseBuilder response = APIResponse.ok(result);
            proxy.copyHeaders(response);
            log.info("Updated "+ id);
            return response.build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: " , e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }
}

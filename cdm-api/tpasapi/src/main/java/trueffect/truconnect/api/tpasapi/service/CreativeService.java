package trueffect.truconnect.api.tpasapi.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.proxy.CreativeProxy;

import com.sun.jersey.api.core.InjectParam;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Set of REST Services related to <code>Creative</code> management.
 *
 * @author Richard Jaldin
 */
@Path("/Creatives")
public class CreativeService extends GenericService {

    /**
     * Returns a <code>Creative</code>  record according to its id.
     * For files of type JPG and JPEG, the same creative image will be returned
     * with the appropriate MIME type "Content-Type: image/jpeg" For files of
     * type ZIP, the backup image will be extracted and returned as a GIF file.
     * For this type of file and GIF files, the content type is "Content-Type:
     * image/gif".
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /Creatives/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @param id The <code>Creative</code> id.
     * @return The Creative file associated to the Creative.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreative(@PathParam("id") Long id) {
        CreativeProxy proxy = new CreativeProxy(headers);
        try {
            proxy.path(Long.toString(id));
            Creative creative = proxy.getCreative();
            return APIResponse.ok(creative).build();
        } catch (ProxyException e) {
            log.debug("Proxy Exception: ", e);
            return e.getErrorResponse();
        }
    }

    /**
     * Returns <code>Creative</code> by <code>Criteria</code>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /Creatives</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @param criteria The search <code>Criteria</code> id
     * @return The <code>CreativeGroup</code> that matches search criteria.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreativeGroups(@InjectParam SearchCriteria criteria) {
        CreativeProxy proxy = new CreativeProxy(headers);
        try {
            RecordSet<Creative> records = proxy.getCreatives(criteria);
            return APIResponse.ok(records).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a creative image by <code>Creative</code> id.
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /Creatives/{id}/file</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @param id The <code>Creative</code> id
     * @return The <code>Creative</code> id image.
     */
    @GET
    @Path("{id}/image")
    public Response getImage(@PathParam("id") Long id) {
        CreativeProxy proxy = new CreativeProxy(headers);
        try {
            proxy.path(Long.toString(id));
            proxy.path("file");
            File file = proxy.getImagen();
            Response.ResponseBuilder response = Response.ok((Object) file);
            proxy.copyHeaders(response);
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
     * Updates an existing <code>Creative</code> image.
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>PUT /Creatives/{id}/image</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>inputStream</code>: Not empty</li>
     * </ul>
     *
     * @param id The <code>Creative</code> id
     * @param inputStream The binary file
     * @param filename The filename of the image
     * @return a Jersey <code>Response</code>
     */
    @PUT
    @Path("{id}/image")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response replaceImage(@PathParam("id") Long id,
            InputStream inputStream,
            @QueryParam("filename") String filename) throws Exception {
        CreativeProxy proxy = new CreativeProxy(headers);
        try {
            proxy.path(Long.toString(id));
            proxy.path("image");
            proxy.query("filename", filename);
            proxy.updateImage(inputStream, filename);
            Response.ResponseBuilder response = APIResponse.ok();
            proxy.copyHeaders(response);
            log.info("Updated " + id);
            return response.build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("Error while closing stream", e);
                }
            }
        }
    }

    /**
     * Deletes a <code>Creative</code> given its <code>id</code>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>DEL /Creatives/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @param id The <code>Creative</code> id
     * @param recursiveDelete: Must be true if it is needed to delete also the CreativeInsertions of this Creative
     * @return a Jersey <code>Response</code>
     */
    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response removeCreative(@PathParam("id") Long id,
            @QueryParam("recursiveDelete") Boolean recursiveDelete) throws Exception {
        CreativeProxy proxy = new CreativeProxy(headers);
        try {
            proxy.path(Long.toString(id));
            Boolean recursive = recursiveDelete == null ? false : recursiveDelete;
            proxy.query("recursiveDelete", recursive.toString());
            SuccessResponse result = proxy.delete();
            log.info("Deleted " + id);
            return APIResponse.ok(result).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }

    }

    /**
     * Updates an existing <code>Creative</code>.

     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>PUT /Creatives/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li>when creative <code>id</code> is not equal as<code>creative.id</code></li>
     * </ul>
     *
     * @param id The <code>Creative</code> id
     * @param creative <code>Creative</code> file
     * @return A <code>Creative</code> updated given the provided parameters
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateCreative(@PathParam("id") Long id, Creative creative) {
        CreativeProxy proxy = new CreativeProxy(headers);
        try {
            if (id.equals(creative.getId())) {
                proxy.path(id.toString());
                Creative cr = proxy.updateCreative(creative);
                Response.ResponseBuilder response = APIResponse.ok(cr);
                proxy.copyHeaders(response);
                log.info("Updated: {}", id);
                return response.build();
            } else {
                return APIResponse.bad("Entity in request body does not match the requested id.").build();
            }
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            return APIResponse.bad(e.getMessage()).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }
}

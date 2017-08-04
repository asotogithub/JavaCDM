package trueffect.truconnect.api.tpasapi.service.stubs;

import com.sun.jersey.api.core.InjectParam;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Consumes;

import org.apache.commons.lang.StringUtils;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.resources.ResourceUtil;
import trueffect.truconnect.api.tpasapi.service.GenericService;
import trueffect.truconnect.api.tpasapi.util.BeanFactory;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.model.RecordSet;

/**
 *
 * @author Gustavo Claure
 */
@Path("/stubs/Creatives")
public class CreativeService extends GenericService {

    /**
     * Gets a Creative record.
     *
     * @param id The ID of Creative
     * @return The Creative.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreative(@PathParam("id") Long id) {
        try {
            Creative creative = BeanFactory.getCreative();
            creative.setId(id);
            return APIResponse.ok(creative).build();
        } catch (Exception e) {
            log.warn("Error getting Creative", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Get records by a Search Criteria
     *
     * @param criteria The search criteria to filter.
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreativeGroups(@InjectParam SearchCriteria criteria) {
        try {
            RecordSet<Creative> records = BeanFactory.getCreatives();
            return APIResponse.ok(records).build();
        } catch (Exception e) {
            log.warn("Error getting Creative", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Returns the Creative's image.
     *
     * @param id Creative's ID.
     * @return Creative's image.
     */
    @GET
    @Path("{id}/image")
    public Response getImage(@PathParam("id") Long id) {
        try {
            String FILE_PATH = ResourceUtil.get("stub.path");
            File file = new File(FILE_PATH);
            Response.ResponseBuilder response = Response.ok((Object) file);
            return response.build();
        } catch (Exception e) {
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     *
     * @param id Creative ID
     * @param inputStream Binary file
     * @param fileName Filename of the image
     * @return OK response if updated image
     */
    @PUT
    @Path("{id}/image")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response replaceImage(@PathParam("id") Long id,
            InputStream inputStream,
            @QueryParam("fileName") String fileName) {
        try {
            Creative creative = BeanFactory.getCreative();
            creative.setId(id);
            creative.setFilename(fileName);
            return APIResponse.ok().build();
        } catch (Exception e) {
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

    /**
     * Removes an Creative based on the ID
     *
     * @param id creative ID number and primary key
     * @return status OK if the Creative has been deleted successfully
     */
    @DELETE
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response removeCreative(@PathParam("id") Long id,
            @QueryParam("recursiveDelete") Boolean recursiveDelete) throws Exception {
        try {

            return APIResponse.ok(new SuccessResponse("Creative " + id + " successfully deleted")).build();
        } catch (Exception e) {
            log.warn("Error saving Creative", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }

    /**
     *
     * @param id Creative Id
     * @param creative Creative Object
     * @return
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateCreative(@PathParam("id") Long id, Creative creative) {
        try {
            if (id.equals(creative.getId())) {
                Creative result = BeanFactory.getCreative(creative.getCampaignId());
                if(!StringUtils.isBlank(creative.getAlias()) && creative.getAlias().length() > 256){
                    return APIResponse.bad("Invalid alias, it supports characters up to 256.").build();
                }
                if(!StringUtils.isBlank(creative.getPurpose()) && creative.getPurpose().length() > 256){
                    return APIResponse.bad("Invalid purpose, it supports characters up to 256.").build();
                }
                result.setAlias(creative.getAlias());
                result.setClickthroughs(creative.getClickthroughs());
                result.setExtendedProperty1(creative.getExtendedProperty1());
                result.setExtendedProperty2(creative.getExtendedProperty2());
                result.setExtendedProperty3(creative.getExtendedProperty3());
                result.setExtendedProperty4(creative.getExtendedProperty4());
                result.setExtendedProperty5(creative.getExtendedProperty5());
                result.setFilename(creative.getFilename());
                result.setIsExpandable(creative.getIsExpandable());
                result.setPurpose(creative.getPurpose());
                return APIResponse.ok(result).build();
            } else {
                return APIResponse.bad("Entity in request body does not match the requested id.").build();
            }
        } catch (Exception e) {
            return APIResponse.error(e.getMessage());
        }
    }
}

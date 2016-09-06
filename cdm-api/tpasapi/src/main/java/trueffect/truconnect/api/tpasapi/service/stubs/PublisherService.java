package trueffect.truconnect.api.tpasapi.service.stubs;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.util.AdminPhone;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.tpasapi.model.Publisher;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.service.GenericService;
import trueffect.truconnect.api.tpasapi.util.BeanFactory;

import org.apache.commons.lang.StringUtils;

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

/**
 *
 * @author Gustavo Claure
 */
@Path("/stubs/Publishers")
public class PublisherService extends GenericService {

    /**
     * Returns the Publisher based on the ID
     *
     * @param id ID of the Publisher to return
     * @return the Publisher of the id
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getPublisher(@PathParam("id") Long id) {
        try {
            Publisher publisher = BeanFactory.getPublisher();
            publisher.setId(id);
            return Response.status(Response.Status.OK).entity(publisher).build();
        } catch (Exception e) {
            log.warn("Error getting Publisher", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Gets Publishers by a Search Criteria
     *
     * @param query The search criteria to filter.
     * @return RecordSet.
     */
    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getPublishers(@QueryParam("query") String query) {
        try {
            RecordSet<Publisher> records = BeanFactory.getPublishers();
            return Response.status(Response.Status.OK).entity(records).build();
        } catch (Exception e) {
            log.warn("Error getting Publishers", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Saves a Publisher
     *
     * @param publisher The Publisher object to save.
     * @return The Publisher already saved on the database.
     */
    @POST
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response savePublisher(Publisher publisher) {
        try {
            if (publisher.getId() != null) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.postWithId", "Publisher")).build();
            }

            if (publisher.getAgencyId() == null) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.empty", "agencyId")).build();
            }
            if (StringUtils.isBlank(publisher.getName())) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.empty", "name")).build();
            } else if (publisher.getName().length() > 200) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "name", "200")).build();
            }
            if (publisher.getAddress1() != null && publisher.getAddress1().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "address1", "256")).build();
            }
            if (publisher.getAddress2() != null && publisher.getAddress2().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "address2", "256")).build();
            }
            if (publisher.getUrl() != null && publisher.getUrl().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "url", "256")).build();
            }
            if (publisher.getCity() != null && publisher.getCity().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "city", "256")).build();
            }
            if (publisher.getState() != null && publisher.getState().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "state", "256")).build();
            }
            if (publisher.getZipCode() != null && publisher.getZipCode().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "zipCode", "256")).build();
            }
            if (publisher.getCountry() != null && publisher.getCountry().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "country", "256")).build();
            }
            if (publisher.getPhoneNumber() != null && publisher.getPhoneNumber().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "phoneNumber", "256")).build();
            } else {
                if (!AdminPhone.validatePhone(publisher.getPhoneNumber())) {
                    return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidPhoneNumber", publisher.getPhoneNumber())).build();
                }
            }
            if (publisher.getAgencyNotes() != null && publisher.getAgencyNotes().length() > 2000) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "agencyNotes", "2000")).build();
            }
            Long id = (long) (Math.random() * 1000);
            publisher.setId(id);
            log.info("Saved "+ id);
            return APIResponse.created(publisher).build();
        } catch (Exception e) {
            log.warn("Error saving Publisher", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }

    /**
     * Updates a Publisher
     *
     * @param id ID of the Publisher
     * @param publisher is the Publisher Object is going to be updated
     * @return the updated Publisher
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response updatePublisher(@PathParam("id") Long id, Publisher publisher) {
        try {
            if (publisher.getId() == null) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.putWithoutId", "Publisher")).build();
            }

            if (id == null || !id.equals(publisher.getId())) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.mismatchingId")).build();
            }
            if (StringUtils.isBlank(publisher.getName())) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.empty", "name")).build();
            } else if (publisher.getName().length() > 200) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "name", "200")).build();
            }
            if (publisher.getAgencyNotes() != null && publisher.getAgencyNotes().length() > 2000) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "agencyNotes", "2000")).build();
            }
            if (publisher.getAddress1() != null && publisher.getAddress1().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "address1", "256")).build();
            }
            if (publisher.getAddress2() != null && publisher.getAddress2().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "address2", "256")).build();
            }
            if (publisher.getUrl() != null && publisher.getUrl().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "url", "256")).build();
            }
            if (publisher.getCity() != null && publisher.getCity().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "city", "256")).build();
            }
            if (publisher.getState() != null && publisher.getState().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "state", "256")).build();
            }
            if (publisher.getZipCode() != null && publisher.getZipCode().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "zipCode", "256")).build();
            }
            if (publisher.getCountry() != null && publisher.getCountry().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "country", "256")).build();
            }
            if (publisher.getPhoneNumber() != null && publisher.getPhoneNumber().length() > 256) {
                return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidStringLength", "phoneNumber", "256")).build();
            } else {
                if (!AdminPhone.validatePhone(publisher.getPhoneNumber())) {
                    return APIResponse.bad(ResourceBundleUtil.getString("global.error.invalidPhoneNumber", publisher.getPhoneNumber())).build();
                }
            }
            return APIResponse.ok(publisher).build();
        } catch (Exception e) {
            log.warn("Error updating Placement", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }
}

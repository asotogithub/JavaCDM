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
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Site;
import trueffect.truconnect.api.tpasapi.service.GenericService;
import trueffect.truconnect.api.tpasapi.util.BeanFactory;

/**
 *
 * @author Gustavo Claure
 */
@Path("/stubs/Sites")
public class SiteService extends GenericService {

    /**
     * Returns the Site of the ID.
     *
     * @param id Site's ID.
     * @return the Site.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getSite(@PathParam("id") Long id) {
        try {
            Site site = BeanFactory.getSite();
            site.setId(id);
            return APIResponse.ok(site).build();
        } catch (Exception e) {
            log.warn("Error getting Site", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Saves a Site.
     *
     * @param site the site to be saved.
     * @return the saved Site.
     */
    @POST
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response saveSite(Site site) {
        try {
            if (site.getPublisherId() == null) {
                return APIResponse.bad("Invalid publisherId, it cannot be empty.").build();
            }
            if (StringUtils.isBlank(site.getName())) {
                return APIResponse.bad("Invalid name, it cannot be empty.").build();
            } else if (site.getName().length() > 256) {
                return APIResponse.bad("Invalid name, it supports characters up to 256.").build();
            }
            if (!StringUtils.isBlank(site.getAgencyNotes()) && site.getAgencyNotes().length() > 256) {
                return APIResponse.bad("Invalid agencyNotes, it supports characters up to 256.").build();
            }
            if (!StringUtils.isBlank(site.getPublisherNotes()) && site.getPublisherNotes().length() > 256) {
                return APIResponse.bad("Invalid publisherNotes, it supports characters up to 256.").build();
            }
            if (!StringUtils.isBlank(site.getUrl()) && site.getUrl().length() > 256) {
                return APIResponse.bad("Invalid url, it supports characters up to 256.").build();
            }

            Long id = (long) (Math.random() * 1000);
            site.setId(id);
            log.info("Saved "+ id);
            return APIResponse.created(site).build();
        } catch (Exception e) {
            log.warn("Error saving Site", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }

    /**
     * Updates a Site.
     *
     * @param id Site's ID.
     * @param site the site to be updated.
     * @return the updated Site.
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response updateSite(@PathParam("id") Long id, Site site) {
        try {

            if (site.getId() == null) {
                return APIResponse.bad("Invalid id, it cannot be empty.").build();
            }

            if (id == null || id.compareTo(site.getId()) != 0) {
                return APIResponse.bad("Provided ID doesn't match required.").build();
            }
            if (StringUtils.isBlank(site.getName())) {
                return APIResponse.bad("Invalid name, it cannot be empty.").build();
            } else if (site.getName().length() > 256) {
                return APIResponse.bad("Invalid name, it supports characters up to 256.").build();
            }
            if (!StringUtils.isBlank(site.getAgencyNotes()) && site.getAgencyNotes().length() > 256) {
                return APIResponse.bad("Invalid agencyNotes, it supports characters up to 256.").build();
            }
            if (!StringUtils.isBlank(site.getPublisherNotes()) && site.getPublisherNotes().length() > 256) {
                return APIResponse.bad("Invalid publisherNotes, it supports characters up to 256.").build();
            }
            if (!StringUtils.isBlank(site.getUrl()) && site.getUrl().length() > 256) {
                return APIResponse.bad("Invalid url, it supports characters up to 256.").build();
            }

            Site s = (Site) getSite(id).getEntity();
            if (id.compareTo(site.getId()) != 0) {
                return APIResponse.bad("Provided ID doesn't match required").build();
            }
            s.setAcceptsFlash(site.getAcceptsFlash());
            s.setAgencyNotes(site.getAgencyNotes());
            s.setClickTrack(site.getClickTrack());
            s.setEncode(site.getEncode());
            s.setId(site.getId());
            s.setName(site.getName());
            s.setPreferredTag(site.getPreferredTag());
            s.setPublisherId(site.getPublisherId());
            s.setRichMedia(site.getRichMedia());
            s.setTargetWin(site.getTargetWin());
            s.setUrl(site.getUrl());

            return APIResponse.ok(s).build();
        } catch (Exception e) {
            log.warn("Error updating Site", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * returns Sites Recordset.
     *
     * @param query query.
     * @return the Site's Recordset.
     */
    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getSites(@QueryParam("query") String query) {
        try {
            RecordSet<Site> records = BeanFactory.getSites();
            return APIResponse.ok(records).build();
        } catch (Exception e) {
            log.warn("Error getting Sites", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }
}

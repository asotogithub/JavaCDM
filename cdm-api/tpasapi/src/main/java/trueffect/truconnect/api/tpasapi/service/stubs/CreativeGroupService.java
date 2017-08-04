package trueffect.truconnect.api.tpasapi.service.stubs;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.tpasapi.model.CreativeGroup;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Schedule;
import trueffect.truconnect.api.tpasapi.util.BeanFactory;
import trueffect.truconnect.api.tpasapi.service.GenericService;

/**
 *
 * @author Rambert Rioja
 */
@Path("/stubs/CreativeGroups")
public class CreativeGroupService extends GenericService {

    public CreativeGroupService() {
    }

    /**
     * Gets a CreativeGroup record.
     *
     * @param id The ID of CreativeGroup
     * @return The CreativeGroup.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getCreativeGroup(@PathParam("id") Long id) {
        try {
            CreativeGroup creativeGroup = BeanFactory.getCreativeGroup();
            creativeGroup.setId(id);
            return APIResponse.ok(creativeGroup).build();
        } catch (Exception e) {
            log.warn("Error getting Creative Group", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Gets CreativeGroup records.
     *
     * @param query Encoded query
     * @return The RecordSet.
     */
    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getCreativeGroupByCriteria(@QueryParam("query") String query) {
        try {
            RecordSet<CreativeGroup> records = BeanFactory.getCreativeGroups();
            return APIResponse.ok(records).build();
        } catch (Exception e) {
            log.warn("Error getting Creative Group", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Saves a Creative Group.
     *
     * @param creativeGroup The Creative Group object to save.
     * @return The Creative Group already saved on the database.
     */
    @POST
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response saveCreativeGroup(CreativeGroup creativeGroup) {
        try {
            if(creativeGroup.getCampaignId() == null){
                return APIResponse.bad("Invalid campaignId [null], it cannot be empty.").build();
            }
            if(StringUtils.isBlank(creativeGroup.getName())){
                return APIResponse.bad("Invalid name, it cannot be empty.").build();
            }
            if(creativeGroup.getDoCookieTargeting() && StringUtils.isBlank(creativeGroup.getCookieTarget())){
                return APIResponse.bad("A cookie target must be provided if cookie targeting is enabled.").build();
            }
            if(creativeGroup.getDoDaypartTargeting() && StringUtils.isBlank(creativeGroup.getDaypartTarget())){
                return APIResponse.bad("A daypart target must be provided if daypart targeting is enabled.").build();
            }
            if(creativeGroup.getDoGeoTargeting() && creativeGroup.getTargetValueIds() == null){
                return APIResponse.bad("A geo target must be provided if geo targeting is enabled.").build();
            }
            if (StringUtils.isBlank(creativeGroup.getRotationType())) {
                creativeGroup.setRotationType("Weighted");
            }
            if (creativeGroup.getWeight() == null) {
                creativeGroup.setWeight(100L);
            }

            if(creativeGroup.getEnableGroupWeight() == null){
                creativeGroup.setEnableGroupWeight(false);
            }
            if(creativeGroup.getPriority() == null){
                creativeGroup.setPriority(0L);
            } else if(creativeGroup.getPriority() <= 0L || creativeGroup.getPriority() >= 100L) {
                return APIResponse.bad("Invalid priority [" + creativeGroup.getPriority() + "], it supports values between 0 and 100.").build();
            }
            if(creativeGroup.getEnableFrequencyCap() == null){
                creativeGroup.setEnableFrequencyCap(false);
            }
            if(creativeGroup.getFrequencyCap() == null){
                creativeGroup.setFrequencyCap(0L);
            }
            if (creativeGroup.getEnableFrequencyCap()) {
                if (creativeGroup.getFrequencyCap() < 0L) {
                    return APIResponse.bad("The frequency cap should be more than zero.").build();
                }
                if (creativeGroup.getFrequencyCapWindow() < 0L) {
                    return APIResponse.bad("The frequence cap window should be more than zero.").build();
                }
            } else {
                creativeGroup.setFrequencyCap(0L);
                creativeGroup.setFrequencyCapWindow(0L);
            }

            Long id = (long) (Math.random() * 1000);
            creativeGroup.setId(id);
            log.info("Saved "+id);
            return APIResponse.created(creativeGroup).build();
        } catch (Exception e) {
            log.warn("Error saving Creative Group", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }

    /**
     * Updates a Creative Group.
     *
     * @param id The ID of CreativeGroup
     * @param creativeGroup The Creative Group object to save.
     * @return The Creative Group already saved on the database.
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response updateCreativeGroup(@PathParam("id") Long id, CreativeGroup creativeGroup) {
        try {
            if (id.compareTo(creativeGroup.getId()) != 0) {
                APIResponse.bad("Ids do not match").build();
            }
            if(creativeGroup.getEnableGroupWeight() == null){
                creativeGroup.setEnableGroupWeight(false);
            }
            if(creativeGroup.getPriority() == null){
                creativeGroup.setPriority(0L);
            } else if(creativeGroup.getPriority() <= 0L || creativeGroup.getPriority() >= 100L) {
                return APIResponse.bad("Invalid priority [" + creativeGroup.getPriority() + "], it supports values between 0 and 100.").build();
            }
            if(creativeGroup.getEnableFrequencyCap() == null){
                creativeGroup.setEnableFrequencyCap(false);
            }
            if(creativeGroup.getFrequencyCap() == null){
                creativeGroup.setFrequencyCap(0L);
            }
            if (creativeGroup.getEnableFrequencyCap()) {
                if (creativeGroup.getFrequencyCap() < 0L) {
                    return APIResponse.bad("The frequency cap should be more than zero.").build();
                }
                if (creativeGroup.getFrequencyCapWindow() < 0L) {
                    return APIResponse.bad("The frequence cap window should be more than zero.").build();
                }
            } else {
                creativeGroup.setFrequencyCap(0L);
                creativeGroup.setFrequencyCapWindow(0L);
            }

            return APIResponse.ok(creativeGroup).build();
        } catch (Exception e) {
            log.warn("Error updating Creative Group", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }

    /**
     * Removes a Creative Group based in the ID.
     *
     * @param id The ID of CreativeGroup
     * @return The Creative Group already saved on the database.
     */
    @DELETE
    @Path("{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response deleteCreativeGroup(@PathParam("id") Long id, @QueryParam("recursiveDelete") Boolean recursiveDelete) {
        try {
            if (id == null) {
                return APIResponse.bad("Invalid ID").build();
            }
            return APIResponse.ok(new SuccessResponse("Creative Group " + id + " successfully deleted.")).build();
        } catch (Exception e) {
            log.warn("Error deleting Creative Group", e);
            return APIResponse.bad(e.getMessage()).build();
        }
    }

    /**
     * Gets a Schedule record.
     *
     * @param id The ID of CreativeGroup
     * @return The Schedule.
     */
    @GET
    @Path("{id}/schedule")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getScheduleByCreativeId(@PathParam("id") Long id) {
        try {
            Schedule schedule = BeanFactory.getSchedule();
            schedule.setCreativeGroupId(id);
            return APIResponse.ok(schedule).build();
        } catch (Exception e) {
            log.warn("Error getting Schedule", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Updates a Schedule record.
     *
     * @param id The ID of CreativeGroup
     * @return The Schedule.
     */
    @PUT
    @Path("{id}/schedule")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateScheduleByCreativeId(@PathParam("id") Long id, Schedule schedule) {
        try {
            if (id.compareTo(schedule.getCreativeGroupId()) != 0) {
                return APIResponse.bad("Ids do not match.").build();
            }
            return APIResponse.ok(schedule).build();
        } catch (Exception e) {
            log.warn("Error updating Schedule", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
    *
    * @param id CreativeGroup ID
    * @param creativeGroupCreative CreativeGroupCreative with the new list of
    * CreativeGroupCreatives
    * @return CreativeGroupCreative with the updated list of Creatives.
    */
   @PUT
   @Path("{id}/creatives")
   @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
   @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
   public Response updateCreativeGroupCreativesList(@PathParam("id") Long creativeGroupId,
           CreativeGroupCreatives creativeGroupCreative) {
       try {
           return APIResponse.ok(creativeGroupCreative).build();
       } catch (Exception e) {
           return APIResponse.error(e.getMessage());
       }
   }

   /**
    * Gets all Creative Group Creatives associated to the CreativeGroup.
    *
    * @param id The Creative Group's Id.
    * @return The CreativeGroupCreatives containing all Creatives related to
    * CreativeGroup.
    */
   @GET
   @Path("{id}/creatives")
   @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
   public Response getCreativeGroupCreatives(@PathParam("id") Long id) {
       try {
           CreativeGroupCreatives records = BeanFactory.getCreativeGroupCreatives(id);
           return APIResponse.ok(records).build();
       } catch (Exception e) {
           return APIResponse.error(e.getMessage());
       }
   }
}

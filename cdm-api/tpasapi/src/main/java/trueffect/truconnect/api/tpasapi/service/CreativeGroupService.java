package trueffect.truconnect.api.tpasapi.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.tpasapi.model.Clickthrough;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Schedule;
import trueffect.truconnect.api.tpasapi.model.ScheduledCreative;
import trueffect.truconnect.api.tpasapi.model.ScheduledPlacement;
import trueffect.truconnect.api.tpasapi.proxy.CreativeGroupCreativeProxy;
import trueffect.truconnect.api.tpasapi.proxy.CreativeGroupProxy;
import trueffect.truconnect.api.tpasapi.proxy.CreativeGroupScheduleProxy;
import trueffect.truconnect.api.tpasapi.proxy.CreativeInsertionProxy;

import com.sun.jersey.api.core.InjectParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;


/**
 * Set of REST Services related to <code>CreativeGroup</code> management.
 *
 * @author Richard Jaldin
 */
@Path("/CreativeGroups")
public class CreativeGroupService extends GenericService {

    @Context
    protected UriInfo uriInfo;

    public CreativeGroupService() {
    }

    public CreativeGroupService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Returns a <code>CreativeGroup</code> given its <code>id</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /CreativeGroups/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CreativeGroup</code> that matches the given <code>id</code> exists
     *
     * @param id The <code>CreativeGroup</code> id
     * @return a <code>CreativeGroup</code>
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreativeGroup(@PathParam("id") Long id) {
        CreativeGroupProxy proxy = new CreativeGroupProxy(headers);
        try {
            proxy.path(Long.toString(id));
            trueffect.truconnect.api.tpasapi.model.CreativeGroup creativeGroup;
            creativeGroup = proxy.getCreativeGroup();
            ResponseBuilder resp = APIResponse.ok(creativeGroup);
            proxy.copyHeaders(resp);
            return resp.build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>CreativeGroup</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
      * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /CreativeGroups</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>CreativeGroup</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
     *
     * @param searchCriteria {@link SearchCriteria} that provides the filtering criteria
     * @return a {@link trueffect.truconnect.api.commons.model.RecordSet
     * < trueffect.truconnect.api.commons.model.CreativeGroup >} with the CreativeGroup found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreativeGroups(@InjectParam SearchCriteria searchCriteria) {
        CreativeGroupProxy proxy = new CreativeGroupProxy(headers);
        try {
            RecordSet<trueffect.truconnect.api.tpasapi.model.CreativeGroup> creativeGroups;
            creativeGroups = proxy.getCreativeGroups(searchCriteria);
            return APIResponse.ok(creativeGroups).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     *
     * Creates a new <code>CreativeGroup</code>.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>POST /CreativeGroups</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>CreativeGroup</code> was created successfully.
     *
     * @param creativeGroup <code>CreativeGroup</code> to create
     * @return A new <code>CreativeGroup</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveCreativeGroup(trueffect.truconnect.api.tpasapi.model.CreativeGroup creativeGroup) {
        CreativeGroupProxy proxy = new CreativeGroupProxy(headers);
        try {
            creativeGroup = proxy.saveCreativeGroup(creativeGroup);
            ResponseBuilder resp = APIResponse.created(creativeGroup, uriInfo, "CreativeGroups");
            proxy.copyHeaders(resp);
            return resp.build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn("Unexpected exception: ", e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>CreativeGroup</code>.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>PUT /CreativeGroups/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CreativeGroup</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>campaignId</code>: Not empty</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>name</code>: Supports characters up to 256.</li>
     *     <li><code>name</code>: Must be unique</li>
     *     <li><code>rotationType</code>: Not empty</li>
     *     <li><code>doDaypartTargeting</code>: Must provide a <code>daypartTarget</code> if enabled</li>
     *     <li><code>doGeoTargeting</code>: Must provide a <code>geoTarget</code> if enabled</li>
     *     <li><code>doCookieTargeting</code>: Must provide a <code>cookieTarget</code> if enabled</li>
     *     <li><code>isDefault</code>: Does not accept any targeting if <code>isDefault</code> = true</li>
     *     <li><code>enableFrequencyCap</code>: Must provide a <code>frequencyCap</code> and a <code>frequencyCapWindow</code> if enabled</li>
     *     <li><code>frequencyCap</code>: Must be a positive number</li>
     *     <li><code>frequencyCapWindow</code>: Must be valid value between [0-999]</li>
     *     <li><code>weight</code>: Must be valid value between [0-100]</li>
     * </ul>
     *
     * @param id The <code>CreativeGroup</code> id
     * @param creativeGroup <code>CreativeGroup</code> to update
     * @return A <code>CreativeGroup</code> updated given the provided parameters
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateCreativeGroup(@PathParam("id") Long id, trueffect.truconnect.api.tpasapi.model.CreativeGroup creativeGroup) {
        CreativeGroupProxy proxy = new CreativeGroupProxy(headers);
        try {
            creativeGroup = proxy.updateCreativeGroup(id, creativeGroup);
            ResponseBuilder resp = APIResponse.ok(creativeGroup);
            proxy.copyHeaders(resp);
            log.debug("Creative Group {} successfully updated ", id);
            return resp.build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn("Unexpected exception: ", e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates an existing <code>CreativeGroup</code> by updating relations with <code>Creative</code>.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>DEL /CreativeGroups/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CreativeGroup</code> that matches the given <code>id</code> has been successfully deleted 
     *
     * @HTTP 400 Bad Request - When a <code>CreativeGroupCreative</code> validation occurs. Validations rules are:
     * <ul>
     *     <li><code>id</code>: Not empty</li>
     * </ul>
     *
     * @param id The <code>CreativeGroup</code> id.
     * @return a <code>recursiveDelete</code> with the updated list of Creative.
     */
    @DELETE
    @Path("/{id}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response deleteCreativeGroup(@PathParam("id") Long id,
                                        @QueryParam("recursiveDelete") Boolean recursiveDelete) {
        try {
            if (id == null) {
                return APIResponse.notFound("Invalid id.").build();
            }
            CreativeGroupProxy proxy = new CreativeGroupProxy(headers);
            proxy.path(id.toString());
            if (recursiveDelete != null && recursiveDelete) {
                proxy.query("recursiveDelete", recursiveDelete.toString());
            }
            SuccessResponse result = proxy.delete();
            log.debug("Creative Group {} successfully deleted", id);
            return APIResponse.ok(result).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn("Unexpected exception: ", e);
            return APIResponse.notFound(e.getMessage()).build();
        }
    }

    /**
     * Updates an existing <code>CreativeGroup</code> by updating relations with <code>Creatives</code>.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>PUT /CreativeGroups/{id}/creatives</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>CreativeGroup</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a <code>CreativeGroupCreative</code> validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>creativeGroupId</code>: Not empty</li>
     *     <li><code>creativeGroupId</code>: Must be the same as the url's id</li>
     *     <li><code>creativeId</code>: Not empty</li>
     *     <li><code>creativeId</code>: Must be from the same Campaign as CreativeGroup</li>
     * </ul>
     *
     * @param creativeGroupId The <code>CreativeGroup</code> id
     * @param creativeGroupCreative <code>CreativeGroupCreative</code> with the new list of Creative Group Creative
     * @return a <code>CreativeGroupCreative</code> with the updated list of Creative.
     */
    @PUT
    @Path("/{id}/creatives")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateCreativeGroupCreativesList(@PathParam("id") Long creativeGroupId,
                                                     trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives creativeGroupCreative) {

        CreativeGroupCreatives cgc = null;
        CreativeGroupCreativeProxy proxy = new CreativeGroupCreativeProxy(headers);
        try {
            if (creativeGroupId != null) {
                proxy.path(creativeGroupId.toString());
                proxy.path("creatives");
                cgc = proxy.updateCreativeGroup(creativeGroupId, creativeGroupCreative);
                if (cgc == null) {
                    return APIResponse.notFound().build();
                }
            }
            ResponseBuilder resp = APIResponse.ok(cgc);
            proxy.copyHeaders(resp);
            log.debug("Creatives successfully updated for Group {}", creativeGroupId);
            return resp.build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn("Unexpected exception: ", e);
            return APIResponse.error(e.getMessage());
        }
    }


    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>CreativeGroupCreative</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /CreativeGroups/{id}/creatives</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>CreativeGroupCreative</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
     *
     * @param id of the <code>CreativeGroup</code> containing the <code>CreativeGroupCreative</code>
     * @return a {@link RecordSet<CreativeGroupCreatives>} for the CreativeGroup found
     */
    @GET
    @Path("/{id}/creatives")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCreativeGroupCreatives(@PathParam("id") Long id) {
        CreativeGroupProxy proxy = new CreativeGroupProxy(headers);
        try {
            CreativeGroupCreatives records = proxy.getCreativeGroupCreatives(id);
            ResponseBuilder resp = APIResponse.ok(records);
            proxy.copyHeaders(resp);
            return resp.build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: " , e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn("Unexpected exception: ", e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Updates one or many Schedule details given the <code>Schedule</code> and <code>Creative Group</code>
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>PUT /Schedules</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Schedule</code> were updated successfully.
     *
     * @HTTP 404 Not Found - When no data is found for the request. Possible scenarios are:
     * <ul>
     *     <li><code>id</code>: Is not null</li>
     *     <li><code>CreativeGroup</code> Id: Is not null</li>
     *     <li><code>id</code>: Is not equal to <code>CreativeGroup</code> Id</li>
     *     <li>Creative <code>Clickthrough</code>: Is not null or empty</li>
     * </ul>
     *
     * @param id The <code>Creative Group</code> id.
     * @param schedule The <code>Schedule</code>
     * @return The <code>Schedule</code> updated.
     */
    @PUT
    @Path("/{id}/schedule")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateSchedule(@PathParam("id") Long id, Schedule schedule) {
        CreativeGroupScheduleProxy proxy = new CreativeGroupScheduleProxy(headers);
        try {
            Long cgId = schedule.getCreativeGroupId();
            if (id != null && cgId != null && !id.equals(cgId)) {
                return APIResponse.notFound(ResourceBundleUtil.getString("global.error.mismatchingId")).build();
            }
            if ((schedule.getPlacements() == null || schedule.getPlacements().isEmpty())
                    || (schedule.getCreatives() == null || schedule.getCreatives().isEmpty())) {
                CreativeInsertionProxy proxyCI = new CreativeInsertionProxy(headers);
                proxyCI.path(id.toString());
                proxyCI.path("byCreativeGroupId");
                proxyCI.delete();
                log.debug("Schedule successfully updated for Group {}", id);
                return APIResponse.ok().build();
            } else {
                if (!hasValidClickthroughs(schedule.getCreatives())) {
                    return APIResponse.bad("A valid clickthrough is required for schedule to be updated. It should start with either http:// or https://.").build();
                }
                //DE872: Add validations for clickthrough not have spaces
                /*if (!hasClickthroughsWithoutSpaces(schedule.getCreatives())) {
                 return APIResponse.bad("A valid clickthrough is required to update Creative. It should not have spaces.").build();
                 }*/
                boolean validCreative = repeatedCreativesValidation(schedule.getCreatives());
                boolean validPlacement = repeatedPlacementValidation(schedule.getPlacements());
                if (validCreative || validPlacement) {
                    return APIResponse.conflict("Only one scheduled association for each Placement/Creative is allowed inside a Creative Group.");
                }
            }
            schedule = proxy.updateSchedule(id, schedule);

            log.debug("Schedule successfully updated for Group {}", id);
            return APIResponse.ok(schedule).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    private boolean hasValidClickthroughs(List<ScheduledCreative> creatives) {
        boolean verifiedClick = true;
        for (ScheduledCreative schedule : creatives) {
            if (schedule.getClickthroughs() == null || schedule.getClickthroughs().isEmpty()) {
                verifiedClick = false;
                return verifiedClick;
            } else {
                List<Clickthrough> clicks = schedule.getClickthroughs();
                for (Clickthrough click : clicks) {
                    if (click == null || click.getUrl() == null) {
                        verifiedClick = false;
                        return verifiedClick;
                    }
                }
            }
        }
        return verifiedClick;
    }

    private boolean repeatedCreativesValidation(List<ScheduledCreative> creatives) throws Exception {
        boolean verifiedCreatives = false;
        Map<String, Integer> countMap = new HashMap();
        for (ScheduledCreative creative : creatives) {
            if (creative.getCreativeId() == null) {
                throw new Exception("Invalid creative id, it cannot be empty.");
            }
            Integer count = countMap.get(creative.getCreativeId().toString());
            if (count == null) {
                count = 0;
            }
            countMap.put(creative.getCreativeId().toString(), (count.intValue() + 1));
        }
        for (Entry<String, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > 1) {
                verifiedCreatives = true;
            }
        }
        return verifiedCreatives;
    }

    private boolean repeatedPlacementValidation(List<ScheduledPlacement> placements) throws Exception {
        boolean verifiedCreatives = false;
        Map<String, Integer> countMap = new HashMap();
        for (ScheduledPlacement placement : placements) {
            if (placement.getPlacementId() == null) {
                throw new Exception("Invalid placement id, it cannot be empty.");
            }
            Integer count = countMap.get(placement.getPlacementId().toString());
            if (count == null) {
                count = 0;
            }
            countMap.put(placement.getPlacementId().toString(), (count.intValue() + 1));
        }
        for (Entry<String, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > 1) {
                verifiedCreatives = true;
            }
        }
        return verifiedCreatives;
    }

    /**
     * Returns the <code>Schedules</code> based on the CreativeGroupId
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *         Roles Allowed: <code>ROLE_APP_ADMIN, ROLE_CLIENT_ADMIN, ROLE_API_FULL_ACCESS</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>-</code>
     *     </li>
     * </ul>
     *
     * <h4>References</h4>
     * <ul>
     *     <li>This service path consumes the following API public layer endpoint:
     *     <code>GET /Schedules/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer to the
     *     public API documentation</li>
     *     <li>Status codes are the same as in public layer.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Schedule</code> that matches the given <code>id</code> exists
     *
     * @param id Id of the CreativeGroup
     * @return the ScheduleSet of the CreativeGroupId
     */
    /**
     * Gets <code>Schedules</code> for given <code>CreativeGroup</code>id.
     *
     * @param id The <code>CreativeGroup</code> id.
     * @return The <code>Schedule</code> for given <code>CreativeGroup</code>id.
     */
    @GET
    @Path("/{id}/schedule")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSchedule(@PathParam("id") Long id) {
        CreativeGroupScheduleProxy proxy = new CreativeGroupScheduleProxy(headers);
        try {
            proxy.path(id.toString());
            Schedule records = proxy.getSchedule(id);
            return APIResponse.ok(records).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: " , e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn("Unexpected exception: ", e);
            return APIResponse.error(e.getMessage());
        }
    }
}
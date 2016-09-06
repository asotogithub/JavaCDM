package trueffect.truconnect.api.tpasapi.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.PUT;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.tpasapi.model.Site;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.proxy.SiteProxy;

/**
 * Set of REST Services related to <code>Site</code> management.
 * 
 * @author Gustavo Claure
 */
@Path("/Sites")
public class SiteService extends GenericService {

    public SiteService() {
    }

    public SiteService(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    /**
     * Returns a <code>Site</code> given its <code>id</code>
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
     *     <code>GET /Sites/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Site</code> that matches the given <code>id</code> exists
     *
     * @param id The <code>Site</code> id.
     * @return a <code>Site</code> of the id.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response get(@PathParam("id") Long id) {
        SiteProxy proxy = new SiteProxy(headers);
        try {
            proxy.path(id.toString());
            Site site = proxy.getSite();
            return APIResponse.ok(site).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: ", e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Returns a <code>RecordSet</code> of one or multiple <code>Site</code>
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
     *     <code>GET /Sites</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>RecordSet</code> with <code>Site</code> is retrieved successfully.
     * Note. It is possible to get an empty list when no records are found for the given Search Criteria
     *
     * @param searchCriteria {@link SearchCriteria} that provides the filtering criteria
     * @return a {@link RecordSet<Site>} with the Sites found
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSites(@InjectParam SearchCriteria searchCriteria) {
        SiteProxy proxy = new SiteProxy(headers);
        try {
            RecordSet<trueffect.truconnect.api.tpasapi.model.Site> sites;
            sites = proxy.getSites(searchCriteria);
            return APIResponse.ok(sites).build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: " , e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Creates a new <code>Site</code> with the given <code>Publisher</code>.
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
     *     <code>POST /Sites</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation.</li>
     * </ul>
     *
     * @HTTP 201 CREATED - When the <code>Site</code> was created successfully.
     *
     * @param site <code>Site</code> to create
     * @return A new <code>Site</code> created given the provided parameters
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response save(trueffect.truconnect.api.tpasapi.model.Site site) {
        SiteProxy proxy = new SiteProxy(headers);
        try {
            site = proxy.saveSite(site);
            Response.ResponseBuilder response = APIResponse.created(site, uriInfo, "Sites");
            proxy.copyHeaders(response);
            log.info("Saved "+site);
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
     * Updates an existing <code>Site</code>.
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
     *     <code>PUT /Sites/{id}</code></li>
     *     <li>In order to see the specific security and business validations please refer
     *     to the public API documentation.</li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Site</code> was updated successfully.
     *
     * @param id The <code>Site</code> id
     * @param site <code>Site</code> file
     * @return A <code>Site</code> updated given the provided parameters
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response update(@PathParam("id") Long id, Site site) {
        SiteProxy proxy = new SiteProxy(headers);
        try {
            proxy.path(id.toString());
            Site result = proxy.putSite(site);
            Response.ResponseBuilder response = APIResponse.ok(result);
            proxy.copyHeaders(response);
            log.info("Updated "+ id);
            return response.build();
        } catch (ProxyException e) {
            log.warn("Proxy Exception: " , e);
            return e.getErrorResponse();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }
}

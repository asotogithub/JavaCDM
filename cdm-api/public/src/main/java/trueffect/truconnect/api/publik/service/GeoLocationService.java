package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.GeoLocationDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.GeoLocationDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.GeoLocationManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Service for interacting with possible geo location targets.
 */
@Path("/GeoLocations")
public class GeoLocationService extends GenericService {
    private GeoLocationManager geoLocationManager;

    public GeoLocationService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        GeoLocationDao geoLocationDao = new GeoLocationDaoImpl(context);
        geoLocationManager = new GeoLocationManager(geoLocationDao, accessControl);
    }

    @GET
    @Path("/countries")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getCountries() {
    	RecordSet<GeoLocation> records = geoLocationManager.getCountries();
        return getGeo(records);
    }
    
    @GET
    @Path("/states")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getStates() {
    	RecordSet<GeoLocation> records = geoLocationManager.getStates();
        return getGeo(records);
    }
    
    @GET
    @Path("/dmas")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getDMAs() {
    	RecordSet<GeoLocation> records = geoLocationManager.getDMAs();
        return getGeo(records);
    }
    
    @GET
    @Path("/zips")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getZipCodes() {
        RecordSet<GeoLocation> records = geoLocationManager.getZipCodes();
        return getGeo(records);
    }
    
    private Response getGeo(RecordSet<GeoLocation> records){
    	try {
            checkAccess("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            return APIResponse.ok(records).build();
        } catch(SystemException e) {
            // Necessary hack to prevent Jersey 1.x bug JERSEY-920
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }
}

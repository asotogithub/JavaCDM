package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.model.ScheduleSet;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupCreativeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ExtendedPropertiesDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.CreativeGroupManager;
import trueffect.truconnect.api.crud.service.ScheduleManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Rambert Rioja
 */
@Path("/Schedules")
public class ScheduleService extends GenericService {

    private ScheduleManager scheduleManager;
    private CreativeGroupManager creativeGroupManager;

    public ScheduleService() {
        PersistenceContext context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        
        CreativeInsertionDao creativeInsertionDao = new CreativeInsertionDaoImpl(context);
        PlacementDao placementDao = new PlacementDaoImpl(context);
        CreativeDao creativeDao = new CreativeDaoImpl(context);
        ExtendedPropertiesDao extendedPropertiesDao = new ExtendedPropertiesDaoImpl(context);
        CreativeGroupDao creativeGroupDao = new CreativeGroupDaoImpl(context);
        CreativeGroupCreativeDao creativeGroupCreativeDao = new CreativeGroupCreativeDaoImpl(context);
        UserDao userDao = new UserDaoImpl(context, accessControl);
        CampaignDao campaignDao = new CampaignDaoImpl(context);
        
        this.scheduleManager = new ScheduleManager(creativeInsertionDao, placementDao, 
                creativeDao, extendedPropertiesDao, creativeGroupDao, creativeGroupCreativeDao, 
                campaignDao, userDao, accessControl);
        
        this.creativeGroupManager =  new CreativeGroupManager(creativeGroupDao,
                                                              creativeGroupCreativeDao,
                                                              creativeInsertionDao,
                                                              creativeDao,
                                                              new CampaignDaoImpl(context),
                                                              new UserDaoImpl(context, accessControl),
                                                              extendedPropertiesDao,  
                                                              accessControl);
    }

    public ScheduleService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Updates ScheduleSet Details
     *
     * @param scheduleSet The Schedule Object
     * @return The Schedule updated.
     */
    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateSchedules(ScheduleSet scheduleSet) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            ScheduleSet result = scheduleManager.updateSchedules(scheduleSet, oauthKey());
            log.info("Data not found for %s : %s", oauthKey().toString(), scheduleSet);
            log.info(oauthKey().toString() + " Updated "+scheduleSet);
            return APIResponse.ok(result).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn(String.format("Data not found for %s : %s", oauthKey().toString(), scheduleSet), e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if(e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getSchedules(@PathParam("id") Long id) {
        try {
            rolesAllowed("ROLE_APP_ADMIN,ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS");
            ScheduleSet scheduleSet = creativeGroupManager.getSchedule(id, oauthKey());
            return APIResponse.ok(scheduleSet).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (NotFoundException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+id, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }
}

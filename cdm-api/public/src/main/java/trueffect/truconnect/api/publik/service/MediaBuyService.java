package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.APIResponse;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.MediaBuyCampaign;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.MediaBuyDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.MediaBuyDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.MediaBuyManager;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Richard Jaldin
 */
@Path("/MediaBuys")
public class MediaBuyService extends GenericService {

    protected MediaBuyManager manager;

    public MediaBuyService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        MediaBuyDao mediaBuyDao = new MediaBuyDaoImpl(context);
        manager = new MediaBuyManager(mediaBuyDao, accessControl);
    }

    public MediaBuyService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Gets a MediaBuy by its campaign id.
     *
     * @param campaignId The ID of Campaign
     * @return The MediaBuy.
     */
    @GET
    @Path("/byCampaign/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getByCampaignId(@PathParam("id") Long campaignId) {
        log.info("Getting MediaBuy by Campaign: " + campaignId);
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            MediaBuy mediaBuy = manager.getByCampaignId(campaignId, oauthKey());
            return APIResponse.ok(mediaBuy).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: " , e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ campaignId, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (NotFoundException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ campaignId, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Saves a a MediaBuy.
     *
     * @param mediaBuy The Media Buy object to create.
     * @return The Media Buy already saved on the database.
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response create(MediaBuy mediaBuy) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            mediaBuy = manager.create(mediaBuy, oauthKey());
            log.info("Media Buy={} was successfully created by user={}", mediaBuy, oauthKey().getUserId());
            return APIResponse.created(mediaBuy, uriInfo, "MediaBuys").build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: ", e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for user= {} and mediaBuy={}", oauthKey().getUserId(), mediaBuy);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }

    /**
     * Saves a a MediaBuy.
     *
     * @param mediaBuyCampaign The Media Buy object to create.
     * @return The Media Buy already saved on the database.
     */
    @POST
    @Path("campaign")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response saveMediaBuyCampaign(MediaBuyCampaign mediaBuyCampaign) {
        try {
            rolesAllowed("ROLE_API_FULL_ACCESS");
            mediaBuyCampaign = manager.createMediaBuyCampaign(mediaBuyCampaign, oauthKey());
            return APIResponse.created(mediaBuyCampaign).build();
        } catch (AccessDeniedException e) {
            log.warn("Access Denied: " + e);
            return APIResponse.forbidden(e.getMessage());
        } catch (DataNotFoundForUserException e) {
            log.warn("Data not found for "+oauthKey().toString()+":"+ mediaBuyCampaign, e);
            return APIResponse.notFound(CrudApiExceptionHandler.getMessage(e)).build();
        } catch (ValidationException e) {
            log.warn("Validation Error: ", e);
            if (e.getErrors() != null) {
                return APIResponse.bad(e.getErrors()).build();
            }
            return APIResponse.bad(e.getMessage()).build();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return APIResponse.error(e.getMessage());
        }
    }
}

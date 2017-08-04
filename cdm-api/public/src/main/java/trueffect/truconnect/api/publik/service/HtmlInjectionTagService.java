package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.HtmlInjectionTagsDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.CampaignDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CostDetailDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.ExtendedPropertiesDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.HtmlInjectionTagsDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.InsertionOrderStatusDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackageDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PackagePlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.PlacementStatusDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SiteSectionDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.SizeDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.HtmlInjectionTagsManager;
import trueffect.truconnect.api.crud.mybatis.dim.DimPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.PlacementManager;
import trueffect.truconnect.api.external.proxy.TruQProxy;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author Saulo Lopez
 */
@Path("/HtmlInjectionTags")
public class HtmlInjectionTagService extends GenericService {

    private HtmlInjectionTagsManager htmlInjectionTagsManager;
    private PlacementManager placementManager;

    public HtmlInjectionTagService() {
        PersistenceContextMyBatis context = new PersistenceContextMyBatis();
        PersistenceContextMyBatis dimContext = new DimPersistenceContextMyBatis();

        AccessControl accessControl = new AccessControlImpl(context);
        SiteSectionDao siteSectionDao = new SiteSectionDaoImpl(context);
        HtmlInjectionTagsDao HtmlInjectionTagsDao = new HtmlInjectionTagsDaoImpl(context);
        PlacementDao placementDao = new PlacementDaoImpl(context);
        CampaignDao campaignDao = new CampaignDaoImpl(context);
        UserDao userDao = new UserDaoImpl(context);
        CreativeInsertionDao creativeInsertionDao = new CreativeInsertionDaoImpl(context);
        htmlInjectionTagsManager = new HtmlInjectionTagsManager(HtmlInjectionTagsDao, placementDao,
                siteSectionDao, campaignDao, userDao, accessControl, new TruQProxy(headers));

        CostDetailDaoExtended placementCostDetailDao =
                new CostDetailDaoImpl(CostDetailType.PLACEMENT, context);
        SizeDao sizeDao = new SizeDaoImpl(context);
        PlacementStatusDao placementStatusDao = new PlacementStatusDaoImpl(context);
        ExtendedPropertiesDao extendedPropertiesDao = new ExtendedPropertiesDaoImpl(context);
        InsertionOrderDao ioDao = new InsertionOrderDaoImpl(context);
        InsertionOrderStatusDao ioStatusDao = new InsertionOrderStatusDaoImpl(context);
        PackageDaoExtended packageDao = new PackageDaoImpl(context);
        PackagePlacementDaoExtended packagePlacementDao = new PackagePlacementDaoImpl(context);
        CostDetailDaoBase dimCostDetailDao =
                new CostDetailDaoImpl(CostDetailType.PLACEMENT, dimContext);

        placementManager = new PlacementManager(placementDao, placementCostDetailDao, campaignDao,
                siteSectionDao, sizeDao, placementStatusDao, userDao, extendedPropertiesDao, ioDao,
                ioStatusDao, packageDao, packagePlacementDao, dimCostDetailDao,
                creativeInsertionDao,
                accessControl);
    }

    public HtmlInjectionTagService(UriInfo uriInfo) {
        this();
        this.uriInfo = uriInfo;
    }

    /**
     * Gets one <code>Html Injection Tag</code> bases on its id.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_HTML_INJECTION_TAG_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 When the <code>Html Injection Tag</code> were got successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>id</code>: Not empty</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the query parameters or payload data</li>
     * </ul>
     *
     * @param id the ID of the HtmlInjection.
     * @return success message.
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getHtmlInjectionTag(@PathParam("id") Long id) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_HTML_INJECTION_TAG_LIST);
            Either<Error, HtmlInjectionTags> result =
                    htmlInjectionTagsManager.getHtmlInjectionTag(id, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Updates one <code>Html Injection Tag</code>.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>UPDATE_HTML_INJECTION_TAG</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 Updated - When the <code>Html Injection Tag</code> was updated successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>id</code>: Not matches the path id</li>
     *     <li><code>name</code>: Not only alphanumeric and spaces</li>
     *     <li><code>name</code>: Not more than 25 characters</li>
     *     <li><code>name</code>: Not null</li>
     *     <li><code>name</code>: Not empty</li>
     *     <li><code>htmlContent</code>: Not more than 1000 characters</li>
     *     <li><code>htmlContent</code>: Does not contains at least one valid tag</li>
     *     <li><code>htmlContent</code>: Not empty if secureHtmlContent is empty</li>
     *     <li><code>secureHtmlContent</code>: Not more than 1000 characters</li>
     *     <li><code>secureHtmlContent</code>: Does not contains at least one valid tag</li>
     *     <li><code>secureHtmlContent</code>: Not empty if htmlContent is empty</li>
     *     <li><code>isEnabled</code>: Not 1 or 0</li>
     *     <li><code>isVisible</code>: Not 1 or 0</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the query parameters or payload data</li>
     * </ul>
     *
     * @param id the ID of the HtmlInjection.
     * @param htmlInjectionTag the data to update
     * @return success message.
     */
    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response updateHtmlInjectionTag(@PathParam("id") Long id,
                                           HtmlInjectionTags htmlInjectionTag) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.UPDATE_HTML_INJECTION_TAG);

            Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager
                    .updateHtmlInjectionTag(id, htmlInjectionTag, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Gets all the associated <code>Placements</code> to a Html Injection Tag.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_HTML_INJECTION_TAG_PLACEMENT_ASSOCIATED_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 When the <code>Placements</code> were got successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>startIndex</code>: Not less than 0</li>
     *     <li><code>pageSize</code>: Not more than 1000</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the query parameters or payload data</li>
     * </ul>
     *
     * @param tagId the ID of the HtmlInjectionTag.
     * @param startIndex the starIndex from where to process
     * @param pageSize the page size
     * @return success message.
     */
    @GET
    @Path("/{id}/placementAssociated")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getPlacementByTagId(@PathParam("id") Long tagId,
                                        @QueryParam("startIndex") Long startIndex,
                                        @QueryParam("pageSize") Long pageSize) {
        log.debug("Get Placements by tagId {}", tagId);
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_HTML_INJECTION_TAG_PLACEMENT_ASSOCIATED_LIST);
            Either<Errors, RecordSet<PlacementView>> result =
                    placementManager.getPlacementsAssociatedInjectionTag(tagId, startIndex,
                            pageSize, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Returns a list if <code>Placements</code> associated to a Html Injection tag, that matches a given String <code>pattern</code>
     * and search options <code>soCampaing</code>, <code>soSite</code> and <code>soPlacement</code>  that can have a Boolean true or false.
     *
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>VIEW_HTML_INJECTION_TAG_PLACEMENT_ASSOCIATED_LIST</code>
     *     </li>
     * </ul>
     *
     * @HTTP 201 When the <code>Placements</code> were got successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li><code>id</code>: Not empty</li>
     *     <li><code>pattern</code>: Not empty</li>
     *     <li><code>pattern</code>: Supports characters up to 250</li>
     *     <li><code>soSection</code>: Cannot be Boolean true</li>
     *     <li><code>startIndex</code>: Greater than 0</li>
     *     <li><code>pageSize</code>: A positive number between 0 and 1000</li>
     * </ul>
     *
     * @HTTP 401 Unauthorized - When an Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token Expired</li>
     * </ul>
     * 
     * @HTTP 404 Not Found - When Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the requested data</li>
     * </ul>
     *
     * @param tagId the ID of the HtmlInjectionTag.
     * @param pattern
     * @param searchOptions
     * @param startIndex the starIndex from where to process
     * @param pageSize the page size
     * @return success message.
     */
    @GET
    @Path("/{id}/searchPlacementsAssociated")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response searchPlacementByPattern(@PathParam("id") Long tagId,
                                             @QueryParam("pattern") String pattern,
                                             @InjectParam PlacementSearchOptions searchOptions,
                                             @QueryParam("startIndex") Long startIndex,
                                             @QueryParam("pageSize") Long pageSize) {
        log.debug("Get Placements by tagId {}", tagId);
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.VIEW_HTML_INJECTION_TAG_PLACEMENT_ASSOCIATED_LIST);
            Either<Errors, RecordSet<PlacementView>> result =
                    placementManager.searchPlacementsAssociatedInjectionTagByPattern(
                            tagId, pattern, searchOptions, startIndex, pageSize, oauthKey());
            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(result.success()).build();
            }

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

    /**
     * Deletes one or more <code>Html Injection Tag</code> in bulk.
     * <h4>Security</h4>
     * <ul>
     *     <li>
     *          Roles Allowed:  <code>-</code>
     *     </li>
     *     <li>
     *         Permissions Allowed:  <code>DELETE_HTML_INJECTION_TAG</code>
     *     </li>
     * </ul>
     *
     * @HTTP 200 OK - When the <code>Html Injection Tag </code> was deleted successfully.
     *
     * @HTTP 400 Bad Request - When a Model validation occurs. Validations rules are:
     * <ul>
     *     <li>Payload not empty.</li>
     *     <li><code>Long</code>: Not empty</li>
     * </ul>
     *
     * @HTTP 403 Forbidden - When a Security or Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Role Not Allowed</li>
     *     <li>Access Token Expired</li>
     * </ul>
     *
     * @HTTP 404 Not Found - When Access Error occurs. Possible scenarios are:
     * <ul>
     *     <li>Access Token belongs to a User that does not have access to the query parameters or payload data</li>
     * </ul>
     *
     * @param bulkDelete A RecordSet of <code>Long</code> with HtmlInjectionTag IDs to process.
     * @return success message.
     */
    @PUT
    @Path("/bulkDelete")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response htmlInjectionTagsBulkDelete(RecordSet<Long> bulkDelete) {
        try {
            checkValidityOfToken();
            checkPermissions(AccessPermission.DELETE_HTML_INJECTION_TAG);
            Either<Errors, String> result =
                    htmlInjectionTagsManager.deleteBulk(bulkDelete, oauthKey());

            if (result.isError()) {
                return handleErrorCodes(result.error());
            } else {
                return Response.ok(new SuccessResponse(result.success())).build();
            }

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new WebApplicationSystemException(e);
        }
    }

}

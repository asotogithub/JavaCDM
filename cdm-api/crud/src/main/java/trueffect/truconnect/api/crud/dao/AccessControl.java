package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;

/**
 *
 * @author marleny.patsi
 */
public interface AccessControl {

    public static final String ACCESS_CONTROL_IS_ADMIN = "DataAccessControlPkg.isAdminUser";
    public static final String ACCESS_CONTROL_FOR_AD_SIZE = "DataAccessControlPkg.getAdSizesByUser";
    public static final String ACCESS_CONTROL_FOR_ADVERTISER = "DataAccessControlPkg.getAdvertisersByUser";
    public static final String ACCESS_CONTROL_FOR_AGENCY = "DataAccessControlPkg.getAgenciesByUser";
    public static final String ACCESS_CONTROL_FOR_BRAND = "DataAccessControlPkg.getBrandsByUser";
    public static final String ACCESS_CONTROL_FOR_CAMPAIGN = "DataAccessControlPkg.getCampaignsByUser";
    public static final String ACCESS_CONTROL_FOR_CREATIVE = "DataAccessControlPkg.getCreativesByUser";
    public static final String ACCESS_CONTROL_FOR_CREATIVE_INSERTION = "DataAccessControlPkg.getCreativeInsertionsByUser";
    public static final String ACCESS_CONTROL_FOR_CREATIVE_GROUP = "DataAccessControlPkg.getCreativeGroupsByUser";
    public static final String ACCESS_CONTROL_FOR_CONTACT = "DataAccessControlPkg.getContactsByUser";
    public static final String ACCESS_CONTROL_FOR_COOKIE_DOMAIN = "DataAccessControlPkg.getCookieDomainsByUser";
    public static final String ACCESS_CONTROL_FOR_INSERTION_ORDER = "DataAccessControlPkg.getInsertionOrdersByUser";
    public static final String ACCESS_CONTROL_FOR_MEDIA_BUY = "DataAccessControlPkg.getMediaBuysByUser";
    public static final String ACCESS_CONTROL_FOR_PLACEMENT = "DataAccessControlPkg.getPlacementsByUser";
    public static final String ACCESS_CONTROL_FOR_PUBLISHER = "DataAccessControlPkg.getPublishersByUser";
    public static final String ACCESS_CONTROL_FOR_SITE = "DataAccessControlPkg.getSitesByUser";
    public static final String ACCESS_CONTROL_FOR_SITE_MEASUREMENT = "DataAccessControlPkg.getSiteMeasurementsByUser";
    public static final String ACCESS_CONTROL_FOR_SITE_MEASUREMENT_EVENT = "DataAccessControlPkg.getSiteMeasurementEventsByUser";
    public static final String ACCESS_CONTROL_FOR_SITE_MEASUREMENT_GROUP = "DataAccessControlPkg.getSiteMeasurementGroupsByUser";
    public static final String ACCESS_CONTROL_FOR_SITE_SECTION = "DataAccessControlPkg.getSiteSectionsByUser";

    /**
     * Same as {@link trueffect.truconnect.api.crud.dao.AccessControl#isAdminUser(String, org.apache.ibatis.session.SqlSession)}, but this one
     * doesn't throw any checked exception
     * @param userId The User ID which we want to check
     * @param session The SqlSession where this query is going to be executed
     * @return true, if the {@code userId} is an Admin. false otherwise.
     */
    boolean isAdmin(String userId, SqlSession session);

    /**
     * Checks if the given {@code userId} can access to the given context of {@code ids} using
     * a specific {@code statement}
     * @param statement The Access Statement to use which references to the query to use for this
     *                  check
     * @param ids The context of IDs to check
     * @param userId The User ID which we want to check
     * @param session The SqlSession where this query is going to be executed
     * @return true if the {@code userId} has access to <b>all</b> of the {@code ids} provided.
     * false otherwise
     */
    boolean isUserValidFor(AccessStatement statement, Collection<Long> ids, String userId, SqlSession session);
}

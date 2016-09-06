package trueffect.truconnect.api.crud.dao;

/**
 * All available Access Statement keys.
 * <p>
 * These are the names for the access control queries found in {@code DataAccessControlPkg.xml}
 *
 */
public enum AccessStatement {
    IS_ADMIN("DataAccessControlPkg.isAdminUser"),
    AD_SIZE("DataAccessControlPkg.getAdSizesByUser"),
    ADVERTISER("DataAccessControlPkg.getAdvertisersByUser"),
    ADVERTISERS("DataAccessControlPkg.controlAdvertisersByUser"),
    AGENCY("DataAccessControlPkg.getAgenciesByUser"),
    BRAND("DataAccessControlPkg.getBrandsByUser"),
    CAMPAIGN("DataAccessControlPkg.getCampaignsByUser"),
    CREATIVE("DataAccessControlPkg.getCreativesByUser"),
    CREATIVE_INSERTION("DataAccessControlPkg.getCreativeInsertionsByUser"),
    CREATIVE_GROUP("DataAccessControlPkg.getCreativeGroupsByUser"),
    CONTACT("DataAccessControlPkg.getContactsByUser"),
    COOKIE_DOMAIN("DataAccessControlPkg.getCookieDomainsByUser"),
    COOKIE_DOMAIN_BY_LIMIT_DOMAINS("DataAccessControlPkg.ifUserHasAccessToAllDomains"),
    HTML_INJECTION("DataAccessControlPkg.ifUserHasAccessToHtmlInjection"),
    INSERTION_ORDER("DataAccessControlPkg.getInsertionOrdersByUser"),
    MEDIA_BUY("DataAccessControlPkg.getMediaBuysByUser"),
    PLACEMENT("DataAccessControlPkg.getPlacementsByUser"),
    PLACEMENT_COST_DETAIL("DataAccessControlPkg.getPlacementCostDetailsByUser"),
    PACKAGE("DataAccessControlPkg.getPackagesByUser"),
    PACKAGE_COST_DETAIL("DataAccessControlPkg.getPackageCostDetailsByUser"),
    PUBLISHER("DataAccessControlPkg.getPublishersByUser"),
    SITE("DataAccessControlPkg.getSitesByUser"),
    SITE_MEASUREMENT("DataAccessControlPkg.getSiteMeasurementsByUser"),
    SITE_MEASUREMENT_EVENT("DataAccessControlPkg.getSiteMeasurementEventsByUser"),
    SITE_MEASUREMENT_GROUP("DataAccessControlPkg.getSiteMeasurementGroupsByUser"),
    SITE_SECTION("DataAccessControlPkg.getSiteSectionsByUser"),
    SITE_MEASUREMENT_EVENT_PING("DataAccessControlPkg.getSiteMeasurementPingsByUser");

    private final String key;

    private AccessStatement(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static AccessStatement fromValue(String key) {
        for(AccessStatement v : values()) {
            if(key.equals(v.getKey())) {
                return v;
            }
        }
        throw new IllegalArgumentException("Provided value " + key + "is not supported");
    }
}

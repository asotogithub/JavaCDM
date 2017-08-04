package trueffect.truconnect.api.commons;

import trueffect.truconnect.api.commons.model.enums.SiteMeasurementAttrSettingsMethodology;
import trueffect.truconnect.api.resources.ResourceUtil;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Application constants Created by richard.jaldin on 5/26/2015.
 */
public class Constants {

    // Creative Group default values
    public static final String SYSTEM_DEFAULT_CREATIVE_GROUP_NAME = "Default";
    public static final String DEFAULT_ROTATION_TYPE = "Weighted";
    public static final Long DEFAULT_IMPRESSION_CAP = 0L;
    public static final Long DEFAULT_CLICKTHROUGH_CAP = 0L;
    public static final Long DEFAULT_WEIGHT = 100L;
    public static final Long DEFAULT_IS_RELEASED = 0L;
    public static final Long DEFAULT_IS_DEFAULT = 1L;
    public static final Long DEFAULT_DO_OPTIMIZATION = 0L;
    public static final String DEFAULT_OPTIMIZATION_TYPE = "";
    public static final String DEFAULT_OPTIMIZATION_SPEED = "";
    public static final Long DEFAULT_MIN_OPTIMIZATION_WEIGHT = 1L;
    public static final Long DEFAULT_ENABLE_GROUP_WEIGHT = 0L;
    public static final Long DEFAULT_DO_COOKIE_TARGETING = 0L;
    public static final Long DEFAULT_DO_DAYPART_TARGETING = 0L;
    public static final Long DEFAULT_DO_GEO_TARGETING = 0L;
    public static final Long DEFAULT_PRIORITY = 0L;
    public static final Long PRIORITY_MIN = 0L;
    public static final Long PRIORITY_MAX = 100L;
    public static final Long DEFAULT_ENABLE_FREQUENCY_CAP = 0L;
    public static final Long DEFAULT_FREQUENCY_CAP = 1L;
    public static final Long DEFAULT_FREQUENCY_CAP_WINDOW = 24L;
    public static final String ROLE_KEY_CAMPAIGN_MANAGER = "CM_USER";
    public static final String DEFAULT_CLICKTHROUGH_CREATIVE = "http://www.trueffect.com";
    public static final Long DEFAULT_SWF_CLICKTHROUGH_COUNT_CREATIVE = 1L;
    public static final Long DEFAULT_HEIGHT_TXT_CREATIVE = 1L;
    public static final Long DEFAULT_WIDTH_TXT_CREATIVE = 1L;

    // XML Files defaults
    public static final Long DEFAULT_HEIGHT_XML_CREATIVE = 2L;
    public static final Long DEFAULT_WIDTH_XML_CREATIVE = 2L;

    public static final Long DEFAULT_HEIGHT_TRD_CREATIVE = 3L;
    public static final Long DEFAULT_WIDTH_TRD_CREATIVE = 3L;
    public static final Long UNSET_WIDTH_OR_HEIGHT = 0L;

    public static final String NO_FLAG = "N";
    public static final String YES_FLAG = "Y";
    public static final String DASH_SPACED = " - ";

    public static final String GRAIN_AGENCY = "agency_id";
    public static final String GRAIN_BRAND = "brand_id";
    public static final String GRAIN_CAMPAIGN = "campaign_id";
    public static final String GRAIN_ADVERTISER = "advertiser_id";
    public static final long ENABLED = 1L;
    public static final long DISABLED = 0L;
    public static final int SHORT_CHARS_LENGTH = 128;
    public static final int DEFAULT_CHARS_LENGTH = 256;
    public static final int MEDIUM_CHARS_LENGTH = 200;
    public static final int LARGE_CHARS_LENGTH = 500;
    public static final int X_LARGE_CHARS_LENGTH = 2000;
    public static final long DEFAULT_CREATIVE_INITIAL_VERSION = 1L;

    //SUPPORTED FILE TYPES
    public static final String TXT_FILE_TYPE = "TXT";
    public static final String HTML_FILE_TYPE = "HTML";
    public static final String XLSX_FILE_TYPE = "XLSX";
    public static final String XLSX_1_X_1_FILE_TYPE = "1X1_XLSX";
    public static final String PDF_FILE_TYPE = "PDF";

    /**
     * Publisher
     */

    public static final String DEFAULT_ZIP_CODE = "00000";

    /**
     * Html Injection
     */
    public static final long HTML_INJECTION_MAX_NAME_LENGTH = 25L;
    public static final long HTML_INJECTION_MAX_OPT_OUT_URL_LENGHT = 653;
    public static final int HTML_INJECTION_HTML_CONTENT_LENGTH = 1000;
    public static final String HTML_INJECTION_ADCHOICES_HTML_CONTENT = "<div style=\"position: " +
            "relative; left: 0; top: 0;\"><a title=\"Proudly Supporting Consumer Ad Choices\" " +
            "target=\"_blank\" href=\"%1s\"><img width=\"15\" height=\"15\" style=\"position: " +
            "absolute; z-index: 100;border: none; top: 3px; padding: 0px;background:transparent;" +
            " left: 4px;\" src=\"http://ad.adlegend.com/cdn/trueffect/adchoices/15x15.png\"></a>" +
            "</div>";
    public static final String HTML_INJECTION_ADCHOICES_SECURE_HTML_CONTENT = "<div style=\"" +
            "position: relative; left: 0; top: 0;\"><a title=\"Proudly Supporting Consumer Ad " +
            "Choices\" target=\"_blank\" href=\"%1s\"><img width=\"15\" height=\"15\" style=\"" +
            "position: absolute; z-index: 100;border: none; top: 3px; padding: 0px;background:" +
            "transparent; left: 4px;\" src=\"https://ad.adlegend.com/cdn/trueffect/adchoices/" +
            "15x15.png\"></a></div>";
    public static final String HTML_INJECTION_FACEBOOK_HTML_CONTENT =
            "<IMG SRC=\"http://ad.adlegend.com/ping?spacedesc=@@spacedesc@@&db_afcr=123&group=" +
                    "TEFP&event=FPILINK&x_fpid=@@PREFID@@&x_fpdomain=%1$d\" WIDTH=1 HEIGHT=1 ALT=\" \" " +
                    "BORDER=0>";
    public static final String HTML_INJECTION_FACEBOOK_SECURE_HTML_CONTENT =
            "<IMG SRC=\"https://ad.adlegend.com/ping?spacedesc=@@spacedesc@@&db_afcr=123&group=" +
                    "TEFP&event=FPILINK&x_fpid=@@PREFID@@&x_fpdomain=%1$d\" WIDTH=1 HEIGHT=1 ALT=\" \" " +
                    "BORDER=0>";

    /**
     * Insertion Order
     */
    public static final int MAX_INSERTION_ORDER_NAME_LENGHT = SHORT_CHARS_LENGTH;
    public static final int INSERTION_ORDER_NUMBER_MIN_VALUE = 0;
    public static final int INSERTION_ORDER_NUMBER_MAX_VALUE = Integer.MAX_VALUE;
    public static final int MAX_INSERTION_ORDER_NOTES_LENGHT = X_LARGE_CHARS_LENGTH;
    /**
     * Site Section
     */
    public static final int MAX_SITE_SECTION_NAME_LENGTH = MEDIUM_CHARS_LENGTH;
    public static final int MAX_SITE_SECTION_AGENCY_NOTES_LENGTH = DEFAULT_CHARS_LENGTH;
    public static final int MAX_SITE_SECTION_PUBLISHER_NOTES_LENGHT = DEFAULT_CHARS_LENGTH;

    /**
     * Size
     */
    public static final int MIN_SIZE_WIDTH = 0;
    public static final int MIN_SIZE_HEIGHT = 0;
    public static final int MAX_SIZE_WIDTH = 9999;
    public static final int MAX_SIZE_HEIGHT = MAX_SIZE_WIDTH;

    public static final int MAXIMUM_LOOKBACK =
            Integer.parseInt(ResourceUtil.get("api.metrics.maximumLookback", "30"));

    /**
     * Packages
     */
    public static final long PACKAGE_PLACEMENT_DEFAULT_INVENTORY = 1L;
    public static final double PACKAGE_PLACEMENT_DEFAULT_AD_SPEND = 0.0D;
    public static final int PACKAGE_NAME_MAX_LENGTH = 100;
    public static final int PACKAGE_EXTERNAL_PACKAGE_ID_MAX_LENGTH = 50;

    /**
     * Placements
     */

    public static final int PLACEMENT_NAME_MAX_LENGTH = DEFAULT_CHARS_LENGTH;
    public static final long PLACEMENT_MIN_FILE_SIZE = 0L;
    public static final long PLACEMENT_MAX_FILE_SIZE = 1L;
    public static final double PLACEMENT_DEFAULT_RATE = 0.0;


    /**
     * Cost Details
     */
    public static final double COST_DETAIL_MARGIN_MAX = 100.0D;
    public static final double COST_DETAIL_MARGIN_MIN = 0.0D;
    public static final double COST_DETAIL_DEFAULT_MARGIN = COST_DETAIL_MARGIN_MAX;
    public static final long MAX_SAFE_INTEGER = (long) (Math.pow(2, 53) - 1);

    public static final long COOKIE_TARGET_TEMPLATE_MAX_SIZE_NAME = 20L;

    public static final long CREATIVE_INSERTION_MAX_WEIGHT = 10000L;
    public static final long CREATIVE_INSERTION_DEFAULT_WEIGHT = 100L;
    public static final long CREATIVE_INSERTION_MIN_WEIGHT = 0L;

    public static final int CREATIVE_GROUP_MAX_NAME_LENGTH = 128;
    public static final long CREATIVE_GROUP_MAX_WEIGHT = 100L;
    public static final long CREATIVE_GROUP_MIN_WEIGHT = CREATIVE_INSERTION_MIN_WEIGHT;

    public static final double COST_DETAIL_GROSS_RATE_MIN_VALUE = 0.0D;
    public static final double COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE = 0.0D;
    public static final double COST_DETAIL_NET_RATE_MIN_VALUE = 0.0D;
    public static final double COST_DETAIL_NET_AD_SPEND_MIN_VALUE = 0.0D;
    public static final int COST_DETAIL_DEFAULT_NUMBER_OF_DAYS_AFTER_START_DATE = 30;

    /**
     * Export/Import Schedules to Excel constants
     */
    public static final String TEMPLATES_PATH = "template";
    public static final String DEFAULT_TIMEZONE = "MST";
    public static final int MAX_NUMBER_VALUES_IN_CLAUSE = 1000;
    public static final int NUMBER_COLUMNS_HEADER = 10;
    public static final String BULK_CREATIVE_INSERTIONS_BASE_PATH = "CreativeInsertions";
    public static final String BULK_CREATIVE_INSERTIONS_EXPORT_PATH =
            BULK_CREATIVE_INSERTIONS_BASE_PATH + File.separator + "export";
    public static final String BULK_CREATIVE_INSERTIONS_IMPORT_PATH =
            BULK_CREATIVE_INSERTIONS_BASE_PATH + File.separator + "import";

    /**
     * Export/Import Schedules to Excel constants
     */
    public static final String BULK_MEDIA_BASE_PATH = "Media";
    public static final String BULK_MEDIA_EXPORT_PATH =
            BULK_MEDIA_BASE_PATH + File.separator + "export";
    public static final String BULK_MEDIA_IMPORT_PATH =
            BULK_MEDIA_BASE_PATH + File.separator + "import";

    /**
     * Site Measurements
     */
    public static final int SITE_MEASUREMENT_NAME_MAX_LENGTH = 128;
    public static final int SITE_MEASUREMENT_NOTES_MAX_LENGTH = 200; //DB supports up to 2000
    public static final int SITE_MEASUREMENT_ASSOCIATION_METHOD_MAX_LENGTH = 5;
    public static final int SITE_MEASUREMENT_EVENT_NAME_MAX_LENGTH = 20;
    public static final int SITE_MEASUREMENT_GROUP_NAME_MAX_LENGTH = 20;
    public static final Long DEFAULT_STATE_NEW_SITEMEASUREMENT = 1L;
    public static final Long STATE_TRAFFICKED_SITEMEASUREMENT = 3L;
    public static final Long DEFAULT_SITE_MEASUREMENT_TRU_TAG_VERSION = 2L;
    public static final String DEFAULT_SITE_MEASUREMENT_METHODOLOGY =
            SiteMeasurementAttrSettingsMethodology.CLICK.name();
    public static final Long DEFAULT_SITE_MEASUREMENT_CLICK_WINDOW = 30L;
    public static final Long MAX_CLICK_WINDOW_VALUE = 30L;
    public static final Long MIN_CLICK_WINDOW_VALUE = 1L;
    public static final Long DEFAULT_SITE_MEASUREMENT_VIEW_WINDOW = 7L;
    public static final Long MAX_VIEW_WINDOW_VALUE = 30L;
    public static final Long MIN_VIEW_WINDOW_VALUE = 1L;

    /**
     * Query utils
     */

    public static final String VALUE_SEPARATOR = "_@<-*->@_";
    /**
     * Resources paths
     */
    public static final String IMAGES_PATH = "/media/images";
    public static final String DEFAULT_PREVIEW_IMAGE = "default-preview.jpg";

    /**
     * Resources paths
     */
    public static final int SEARCH_PATTERN_MAX_LENGTH = 250;

    /**
     * Global dates
     */
    //01/01/0000
    public static final Date MIN_DATE;
    //12/31/9999
    public static final Date MAX_DATE;

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        MIN_DATE = cal.getTime();

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 9999);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        MAX_DATE = cal.getTime();
    }
}

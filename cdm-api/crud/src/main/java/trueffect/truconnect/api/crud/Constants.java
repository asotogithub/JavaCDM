package trueffect.truconnect.api.crud;

/**
 * Constant values used throughout the application.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public final class Constants {

    /**
     * Base URL for tests
     */
    public final static String BASE_URI = "http://localhost:8099/internal/";
    /**
     * The name of the ResourceBundle used in this application
     */
    public static final String BUNDLE_KEY = "ApplicationResources";
    /**
     * File separator from System properties
     */
    public static final String FILE_SEP = System.getProperty("file.separator");
    /**
     * User home from System properties
     */
    public static final String USER_HOME = System.getProperty("user.home") + FILE_SEP;
    /**
     * The name of the configuration hashmap stored in application scope.
     */
    public static final String CONFIG = "appConfig";
    /**
     * Session scope attribute that holds the locale set by the user. By setting
     * this key to the same one that Struts uses, we get synchronization in
     * Struts w/o having to do extra work or have two session-level variables.
     */
    public static final String PREFERRED_LOCALE_KEY = "org.apache.struts2.action.LOCALE";
    /**
     * The request scope attribute under which an editable user form is stored
     */
    public static final String USER_KEY = "userForm";
    /**
     * The request scope attribute that holds the user list
     */
    public static final String USER_LIST = "userList";
    /**
     * The request scope attribute for indicating a newly-registered user
     */
    public static final String REGISTERED = "registered";
    /**
     * The name of the Administrator role, as specified in web.xml
     */
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    /**
     * The name of the User role, as specified in web.xml
     */
    public static final String USER_ROLE = "ROLE_USER";
    /**
     * The name of the user's role list, a request-scoped attribute when
     * adding/editing a user.
     */
    public static final String USER_ROLES = "userRoles";
    /**
     * The name of the available roles list, a request-scoped attribute when
     * adding/editing a user.
     */
    public static final String AVAILABLE_ROLES = "availableRoles";
    /**
     * The name of the CSS Theme setting.
     *
     * @deprecated No longer used to set themes.
     */
    public static final String CSS_THEME = "csstheme";

    /**
     * Configuration for connection to the Metrics database
     */
    public static final String METRICS_MYBATIS_PATH = "MetricsConfigMybatis.xml";
    public static final String METRICS_DATA_STORE_USERNAME_RESOURCE = "metricsDataStore.username";
    public static final String METRICS_DATA_STORE_URL_RESOURCE = "metricsDataStore.url";
    public static final String METRICS_DATA_STORE_PASSWORD_RESOURCE = "metricsDataStore.password";
    public static final String METRICS_DATA_STORE_DRIVER_RESOURCE = "metricsDataStore.driver";

    /**
     * Configuration for connecting to the CM database
     */
    public static final String CM_MYBATIS_PATH = "ConfigMybatis.xml";
    public static final String CM_DATA_STORE_URL_RESOURCE = "dataStore.url";
    public static final String CM_DATA_STORE_USERNAME_RESOURCE = "dataStore.username";
    public static final String CM_DATA_STORE_PASSWORD_RESOURCE = "dataStore.password";
    public static final String CM_DATA_STORE_DRIVER_RESOURCE = "dataStore.driver";
    public static final String APPLICATION_VERSION_MESSAGE = "application.version";
    
    /**
     * Configuration for connection to the dim database
     */
    public static final String DIM_MYBATIS_PATH = "DimConfigMybatis.xml";
    public static final String DIM_DATA_STORE_USERNAME_RESOURCE = "dim.dataStore.username";
    public static final String DIM_DATA_STORE_URL_RESOURCE = "dim.dataStore.url";
    public static final String DIM_DATA_STORE_PASSWORD_RESOURCE = "dim.dataStore.password";
    public static final String DIM_DATA_STORE_DRIVER_RESOURCE = "dim.dataStore.driver";

    /**
     * Configuration for connection with AWS
     */
    public static final String AWS_ACCESS_KEY = "aws.access_key";
    public static final String AWS_SECRET_KEY = "aws.secret_key";
    public static final String ADM_DATA_STORE_TABLE_NAME = "adm.dataStore.tableName";
    public static final String ADM_DATA_STORE_S3_PATH_INDEX_NAME = "adm.dataStore.s3PathIndexName";
    public static final String ADM_DATA_STORE_AGENCY_INDEX_NAME = "adm.dataStore.agencyIndexName";
    public static final String ADM_DATA_STORE_DOMAIN_INDEX_NAME = "adm.dataStore.domainIndexName";
    public static final String ADM_DATA_STORE_REGION = "adm.dataStore.region";

    /**
     * Configuration for the ADM data store
     */

    /**
     * Configuration for the ADM transaction store
     */
    public static final String ADM_REDSHIFT_URL = "adm.redshift.url";
    public static final String ADM_REDSHIFT_USER = "adm.redshift.user";
    public static final String ADM_REDSHIFT_PASSWORD = "adm.redshift.password";
    // jfrylings:
    // The last argument of RedshiftTransactionStore is the day aggregate boundary offset.
    // For example as of 20160407 the agg boundary is 7 days for the read case.
    // In pipeline hertz the day at which things are stored into the agg table is set to 5 days.
    // This is set up this way so there is always an overlap between
    // when data is stored in the agg table and when the code reads from it.
    public static final String ADM_REDSHIFT_RETENTION_INTERVAL = "adm.redshift.retentionInterval";

    /**
     * Configuration for Cassandra
     */
    public static final String CASSANDRA_NODE_ADDRESSES = "cassandra.nodeAddresses";
    public static final String CASSANDRA_SPECULATIVE_DELAY = "cassandra.speculativeDelay";
    public static final String CASSANDRA_SPECULATIVE_MAX_ATTEMPTS = "cassandra.speculativeMaxAttempts";
    public static final String CASSANDRA_ENV = "cassandra.env";


}

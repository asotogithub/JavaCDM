package trueffect.truconnect.api.resources;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Utility class for getting resources from a configuration file or environment variable.
 */
public class ResourceUtil {
    private static final String KEY_PREFIX = "services.truadvertiser.api";

    private static final Config rootConfig;

    static {
        rootConfig = ConfigFactory.load();
    }

    private ResourceUtil() {}

    public static String get(String resource, String key, Object... parameters) {
        return MessageFormat.format(ResourceBundle.getBundle(resource).getString(key), parameters);
    }

    public static String getFromEnv(String key, String defaultValue) {
        String result = System.getenv(key);
        if(result == null || "".equals(result)) {
            result = defaultValue;
        }
        return result;
    }

    public static String getFromConfig(String key) {
        if(!key.startsWith("services.")) {
            key = String.format("%s.%s", KEY_PREFIX, key);
        }

        return rootConfig.getString(key);
    }

    public static String getFromConfig(String key, String defaultValue) {
        String result;
        try {
            result = getFromConfig(key);
        } catch(Exception e) {
            result = defaultValue;
        }

        return result;
    }

    public static String get(String key) {
        if(!key.startsWith("services.")) {
            key = String.format("%s.%s", KEY_PREFIX, key);
        }

        return rootConfig.getString(key);
    }

    public static String get(String key, String defaultValue) {
        String result;
        try {
            result = get(key);
        } catch(Exception e) {
            result = defaultValue;
        }

        return result;
    }
}

package trueffect.truconnect.api.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility to get Resource messages from {@code messages_en_US.properties} file in this module
 * Created by marcelo.heredia on 11/20/2015.
 * @author Marcelo Heredia
 */
public final class ResourceBundleUtil {
    private static final Logger logger = LoggerFactory.getLogger(ResourceBundleUtil.class);

    private final static String RESOURCE_BUNDLE_FILENAME = "messages";
    private final static Locale DEFAULT_LOCALE = Locale.US;
    private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(RESOURCE_BUNDLE_FILENAME, DEFAULT_LOCALE);

    public static String getString(String key, Object... parameters) {
        String pattern;
        try {
            pattern = RESOURCE_BUNDLE.getString(key);
            return MessageFormat.format(pattern, parameters);
        } catch (MissingResourceException e) {
            logger.warn("Can't find resource bundle for {}", key);
            return key;
        }
    }

}

package trueffect.truconnect.api.commons.validation;

import java.util.regex.Pattern;

/**
 * Validation-related constants
 * Created by Marcelo Heredia on 11/25/2014.
 * @author Marcelo Heredia
 */
public class ValidationConstants {
    public static final String KEY_VALIDATION_RESULT = "VALIDATION_RESULT";
    public static final String KEY_BUSINESS_ERROR = "BUSINESS_ERROR";
    public static final String KEY_SECURITY_ERROR = "SECURITY_ERROR";
    public static final String KEY_FILE_UPLOAD_ERROR = "FILE_UPLOAD_ERROR";
    public static final String KEY_WWW_AUTHENTICATE = "WWW_AUTHENTICATE";
    // Regular expressions
    public static final String REGEXP_ALPHANUMERIC_HYPHEN_SPACE_APOSTROPHE = "^[ 0-9A-Za-z'-]+$";
    public static final String REGEXP_CREATIVE_GROUP_NAME = "[\\w- \\.~\\/?%&=\\'\\!\\(\\)\\+\\-\\*\\$\\#\\@\\:\\;\\^\\{\\}\\<\\>\\,\\]\\[]+$";
    public static final String REGEXP_DOMAIN_NAME = "^(?!://|www\\.)([a-zA-Z0-9]+\\.)?[a-zA-Z0-9][a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}?$";
    // Based on https://gist.github.com/dperini/729294
    // MH 07/14/2016 TA-4310. Adding support for Macro specific chars in Hostname and Domain parts
    // Macro characters are:
    // '!', '@', '#', '$', '%', '^', '&', '*', '=', '+',
    // '{', '}', '[', ']', '|', '?', '~', '-', '_', ';', and ':'
    public static final String REGEXP_CLICKTHROUGH_URL = "^" +
            // protocol identifier
            "(?:(?:https?)://)" +
            // user:pass authentication
            "(?:[^,\f\n\r\t\\v\\u00a0\\u1680\\u180e\\u2000-\\u200a\\u2028\\u2029\\u202f\\u205f\\u3000\\ufeff]+(?::[^,\f\n\r\t\\v\\u00a0\\u1680\\u180e\\u2000-\\u200a\\u2028\\u2029\\u202f\\u205f\\u3000\\ufeff]*)?@)?" +
            "(?:" +
            // IP address exclusion
            // private & local networks
            "(?!(?:10|127)(?:\\.\\d{1,3}){3})" +
            "(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})" +
            "(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})" +
            // IP address dotted notation octets
            // excludes loopback network 0.0.0.0
            // excludes reserved space >= 224.0.0.0
            // excludes network & broacast addresses
            // (first & last IP address of each class)
            "(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])" +
            "(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}" +
            "(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))" +
            "|" +
            // host name
            "(?:[a-z\\u00a1-\\uffff0-9!@#$%^&*=+{}\\[\\]\\|?~_;:-]+)" +
            // domain name
            "(?:\\.[a-z\\u00a1-\\uffff0-9!@#$%^&*=+{}\\[\\]\\|?~_;:-]+)*" +
            // TLD identifier
            "(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))" +
            // TLD may end with dot
            "\\.?" +
            ")" +
            // port number
            "(?::\\d{2,5})?" +
            // resource path
            "(?:[/?#][^ ,\f\n\r\t\\v\\u00a0\\u1680\\u180e\\u2000-\\u200a\\u2028\\u2029\\u202f\\u205f\\u3000\\ufeff]*)?" +
            "$";

    public static final String REGEXP_URL = "^" +
            // protocol identifier
            "(?:(?:https?)://)" +
            // user:pass authentication
            "(?:\\S+(?::\\S*)?@)?" +
            "(?:" +
            // IP address exclusion
            // private & local networks
            "(?!(?:10|127)(?:\\.\\d{1,3}){3})" +
            "(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})" +
            "(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})" +
            // IP address dotted notation octets
            // excludes loopback network 0.0.0.0
            // excludes reserved space >= 224.0.0.0
            // excludes network & broacast addresses
            // (first & last IP address of each class)
            "(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])" +
            "(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}" +
            "(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))" +
            "|" +
            // host name
            "(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)" +
            // domain name
            "(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*" +
            // TLD identifier
            "(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))" +
            // TLD may end with dot
            "\\.?" +
            ")" +
            // port number
            "(?::\\d{2,5})?" +
            // resource path
            "(?:[/?#]\\S*)?" +
            "$";

    public static final Pattern PATTERN_CLICKTHROUGH_URL = Pattern.compile(REGEXP_CLICKTHROUGH_URL, Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTERN_URL = Pattern.compile(REGEXP_URL, Pattern.CASE_INSENSITIVE);
    public static final String REGEXP_EMAIL_ADDRESS = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
    public static final String REGEXP_IS_VALID_DOMAIN =  "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
    public static final String REGEXP_ALPHANUMERIC = "[^a-zA-Z0-9]";
    public static final String REGEXP_HTML_INJECTION_AD_CHOICES_OPT_OUT_URL = "^(https?|ftp|file)://[-a-zA-Z0-9&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public static final String REGEXP_HTML_INJECTION_CONTENT = "<(?<endTag>/)?(?<tagname>\\w+)((\\s+(?<attName>\\w+)" +
            "(\\s*=\\s*(?:\"(?<attVal1>[^\"]*)\"|'(?<attVal2>[^']*)'|" +
            "(?<attVal3>[^'\">\\s]+)))?)+\\s*|\\s*)" +
            "(?<completeTag>/)?>";
    public static final String HTML_INJECTION_SPACE_REMOVER = " {2,}";
    public static final String REGEXP_ALPHANUMERIC_AND_SPACES = "[a-zA-Z0-9 ]+";
    public static final String REGEXP_FILENAME_UNALLOWED_CHARACTERS = "[\\/:*?\"<>|]";
    public static final String XLS_DATE_VALUE_REGEXP = "[0-9]*\\.?[0-9]+";
    public static final String REGEXP_ALPHANUMERIC_AND_SPECIAL_CHARACTERS = "^[\\sa-zA-Z0-9\\.\\~\\/\\?\\%\\&\\=\\\\\\'\\!\\+\\-\\*\\$\\#\\@\\:\\;\\^\\{\\}\\,\\[\\]\\(\\)\\<\\>\\_]*$";
    public static final String REGEXP_ALPHANUMERIC_WITH_UNDERSCORE = "[^a-zA-Z0-9\\_]";
    public static final Pattern PATTERN_HTML_CONTENT = Pattern.compile(REGEXP_HTML_INJECTION_CONTENT, Pattern.CASE_INSENSITIVE);
}

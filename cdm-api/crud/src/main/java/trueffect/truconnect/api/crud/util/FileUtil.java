package trueffect.truconnect.api.crud.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Gustavo Claure
 * @deprecated Use {@link trueffect.truconnect.api.commons.AdminFile class instead}
 */
@Deprecated
public class FileUtil {

    public static String getName(String filename) {
        return filename.substring(0, (filename.length() - 4));
    }

    public static boolean isJpg(String filename) {
        return filename.substring((filename.length() - 3), (filename.length())).equals("jpg");
    }

    public static boolean isGif(String filename) {
        return filename.substring((filename.length() - 3), (filename.length())).equals("gif");
    }

    public static boolean isZip(String filename) {
        return filename.substring((filename.length() - 3), (filename.length())).equals("zip");
    }

    public static boolean isSwf(String filename) {
        return filename.substring((filename.length() - 3), (filename.length())).equals("swf");
    }

    /**
     * Gets the file name from the content-disposition header value using
     * regular expressions
     *
     * @param contentDisposition content-disposition header value
     * @return String The file name
     */
    public static String getFileName(String contentDisposition) {
        Pattern p = Pattern.compile(".*filename=\"(.+)\".*");
        Matcher matcher = p.matcher(contentDisposition);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }
    public static String getSubDomain(String domain) {
        String PATTERN = "^media\\.|^mediamgt\\.|^webmedia\\.|^ad\\.";
        String resul = null;
        Pattern r = Pattern.compile(PATTERN);
        Matcher m = r.matcher(domain);
        if (m.find(0)) {
            resul = domain.substring(m.group(0).length() - 1, domain.length());
        } else {
            resul = domain;
        }
        return resul;
    }
}

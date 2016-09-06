package trueffect.truconnect.api.commons.util;

import trueffect.truconnect.api.resources.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Utility methods for Temporary files management
 * Created by marcelo.heredia on 7/17/2015.
 * @author Marcelo Heredia
 */
public class TempFileUtil {
    private static Logger log = LoggerFactory.getLogger(TempFileUtil.class);
    private static final String TRUEADVERTISER_TMP_DIRECTORY_PATH = File.separator + "trueadvertiser" + File.separator +
                                                                    "files" + File.separator ;
    public static final String OS_TMP_PATH;
    static {
        String defaultPath, tmpOsPath = ResourceUtil.get("os.tmp.path");
        // Check if etcd path exist. If not, default to java.io.tmpdir
        File f = new File(tmpOsPath);
        if(!f.exists() || !f.isDirectory() || !f.canWrite()){
            defaultPath = System.getProperty("java.io.tmpdir") + TRUEADVERTISER_TMP_DIRECTORY_PATH;
            log.warn(String.format("Defined 'os.tmp.path' = '%s' either doesn't exist, "
                                      + "it is not a directory, or it is not writable. "
                                      + "Using default path: %s", tmpOsPath, defaultPath));
            OS_TMP_PATH = defaultPath;
        } else {
            OS_TMP_PATH = tmpOsPath;
        }
    }
}

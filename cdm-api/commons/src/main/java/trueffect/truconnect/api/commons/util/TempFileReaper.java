package trueffect.truconnect.api.commons.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * Performs a Temporary File cleanup
 * Created by marcelo.heredia on 7/17/2015.
 * @author Marcelo Heredia
 */
public class TempFileReaper implements Runnable{

    private static Logger log = LoggerFactory.getLogger(TempFileReaper.class);
    private long tempFileIdle;
    private String path;

    public TempFileReaper(long tempFileIdle, String path) {
        this.tempFileIdle = tempFileIdle;
        if(StringUtils.isEmpty(path)){
            throw new IllegalArgumentException("Path cannot be null");
        }
        this.path =  path;
    }

    /**
     * Runs the reaper runnable
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        deleteIdleFiles();
    }

    /**
     * Deletes all files located in {@code os.tmp.path}
     */
    public void deleteAll() {
        log.info("Delete all files in '{}': Start", path);
        File f = new File(path);
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteFile(c);
            log.info("Delete all files in '{}': Done", path);
        } else {
            log.info("Delete all files in '{}': Error, not a directory or does not exist.",
                     path);
        }
    }

    /**
     * Deletes all files located in {@code os.tmp.path} which has a creation time longer than
     * {@code reaper.tmp.file.idle} compared against the platform current time.
     */
    public void deleteIdleFiles() {
        log.info("Delete idle files in '{}': Start", path);
        File f = new File(path);
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteIdleFile(c);
            log.info("Delete idle files in '{}': Done", path);
        } else {
            log.info("Delete idle files in '{}': Error, not a directory or does not exist.",
                     path);
        }
    }

    /**
     * Deletes a file or directory recursively
     * @param f The {@code File} to delete
     */
    private void deleteFile(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteFile(c);
        }
        if (!f.delete())
            log.warn("Failed to delete file '{}'", f);
    }

    /**
     * Deletes a file or directory recursively only if the creation time is older than (current OS time - delta time)
     *
     * @param f The {@code File} to delete
     */
    private void deleteIdleFile(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteIdleFile(c);
        }
        Path path = Paths.get(f.toURI());
        // Read creative file attributes
        BasicFileAttributes attr;
        try {
            attr = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime creationTime = attr.creationTime();
            FileTime delta = FileTime.fromMillis(System.currentTimeMillis() - tempFileIdle * 1000);
            // If File creation time is before than delta; then, it means file can be deleted
            if(creationTime.compareTo(delta) < 0) {
                if (!f.delete()){
                    log.warn("Could not delete idle file '{}'", f);
                } else {
                    log.info("Idle file deleted: '{}'", f);
                }
            }
        } catch (IOException e) {
            log.warn("Could not read file attributes for '{}'", f, e);
        }
    }
}

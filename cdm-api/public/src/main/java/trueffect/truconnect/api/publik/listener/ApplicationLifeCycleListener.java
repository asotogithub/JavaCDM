package trueffect.truconnect.api.publik.listener;

import trueffect.truconnect.api.commons.util.TempFileReaper;
import trueffect.truconnect.api.commons.util.TempFileUtil;
import trueffect.truconnect.api.resources.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application Life Cycle Listener.
 * <p>
 * This class is in charge of receiving notification events about ServletContext lifecycle changes.
 * Created by Marcelo Heredia on 7/17/2015.
 * @author Marcelo Heredia
 */
public class ApplicationLifeCycleListener implements ServletContextListener{

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private ScheduledExecutorService scheduler;
    // By default the TempFileReaper gets its configuration from etcd and TempFileUtil
    private TempFileReaper fileReaper;
    private long checkTime;

    /**
     * Constructor that receives a {@code TempFileReaper} for testing purposes
     * @param fileReaper The {@code TempFileReaper} that performs the cleanup
     * @param checkTime How often the check for files should be done.
     */
    public ApplicationLifeCycleListener(long checkTime, TempFileReaper fileReaper) {
        if(checkTime <= 0){
            throw new IllegalArgumentException("Check time cannot be lower than or equals to 0");
        }
        this.checkTime = checkTime;
        this.fileReaper = fileReaper;
    }
    /**
     * Default constructor that doesn't receives any parameter.
     * <p>
     * By default the check time is obtained from etcd's {@code reaper.check.time} and the path
     * is obtained from etcd's {@code os.tmp.path}
     */
    public ApplicationLifeCycleListener() {
        this.fileReaper = new TempFileReaper(
            Long.valueOf(ResourceUtil.get("reaper.temp.file.idle")),
            TempFileUtil.OS_TMP_PATH);
        this.checkTime = Long.valueOf(ResourceUtil.get("reaper.check.time"));
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("Application is being initialized");
        // Delete all files in temp path as the app is starting up.
        fileReaper.deleteAll();
        // Start a new Scheduled Executor that cleans up the
        // idle files only every REAPER_CHECK_TIME seconds
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(fileReaper, checkTime, checkTime, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.info("Application has been destroyed");
        // Make sure the reaper is stopped
        scheduler.shutdownNow();
    }

}

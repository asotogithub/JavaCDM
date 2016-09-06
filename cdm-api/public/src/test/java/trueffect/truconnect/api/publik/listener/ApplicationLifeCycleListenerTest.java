package trueffect.truconnect.api.publik.listener;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import trueffect.truconnect.api.commons.util.TempFileReaper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class ApplicationLifeCycleListenerTest {

    private ApplicationLifeCycleListener listener;
    private ServletContext servletContext;
    private String path;
    private static final int NUMBER_OF_TEST_FILES = 5;
    private TempFileReaper fileReaper;

    @Before
    public void init(){
        servletContext = mock(ServletContext.class);
        long checkTime = 2; // 2 seconds
        long idleTime = 86400; // 1 day
        path = System.getProperty("java.io.tmpdir") + File.separator + "reaper" + File.separator;
        // Creating a few files for the initial test
        try {
            createAllTestFiles();
        } catch (IOException e) {
            fail("Could not create test files: " + e.getMessage());
        }
        fileReaper = new TempFileReaper(idleTime, path);
        listener = new ApplicationLifeCycleListener(checkTime,
                                                    fileReaper);
        listener.contextInitialized(
            new ServletContextEvent(servletContext));
    }

    @After
    public void stop(){
        fileReaper.deleteAll();
        File f = new File(path);
        if(!f.delete()){
            fail("Could not clean up test directory");
        }
        listener.contextDestroyed(
            new ServletContextEvent(servletContext));

    }
    @Test
    public void testInitialCleanup() throws Exception{
        // Check that we have N files
        File f = new File(path);
        assertTrue(f.exists());
        assertTrue(f.isDirectory());
        File[] files = f.listFiles();
        // Expect all files were deleted
        assertTrue(files.length == 0);
    }

    @Test
    public void testIdleFilesCleanup() throws Exception{
        createAllTestFiles();
        modifyCreationDateForSingleFile();
        // Wait for 3 seconds that the reaper cleans up the "old" file
        Thread.sleep(3000);
        File f = new File(path);
        assertTrue(f.exists());
        assertTrue(f.isDirectory());
        File[] files = f.listFiles();
        assertTrue(files.length == (NUMBER_OF_TEST_FILES - 1));
    }


    private void createAllTestFiles() throws IOException {
        // Create test directory
        File baseFile = new File(path);
        if(!baseFile.exists()){
            if(!baseFile.mkdir()){
                fail("Could not create path " + path);
            }
        }

        // Create 5 test files
        for(int i = 0; i < NUMBER_OF_TEST_FILES; i++){
            File f = new File(path, "file_" + i + ".txt");
            f.createNewFile();
        }
    }

    private void modifyCreationDateForSingleFile() throws IOException {
        // Read creative file attributes
        int n = (int) (Math.random() * NUMBER_OF_TEST_FILES);
        File f = new File(path, "file_" + n + ".txt");
        Path path = Paths.get(f.toURI());
        BasicFileAttributes attr;
        attr = Files.readAttributes(path, BasicFileAttributes.class);
        FileTime creationTime = attr.creationTime();
        Calendar c = new GregorianCalendar();
        c.setTime(new Date(creationTime.toMillis()));
        c.add(Calendar.DAY_OF_YEAR, -10);
        BasicFileAttributeView attributes = Files.getFileAttributeView(path, BasicFileAttributeView.class);
        FileTime time = FileTime.fromMillis(c.getTimeInMillis());
        attributes.setTimes(time, time, time);
    }
}
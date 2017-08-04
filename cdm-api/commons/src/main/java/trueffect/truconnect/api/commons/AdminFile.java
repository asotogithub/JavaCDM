package trueffect.truconnect.api.commons;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *
 * @author Abel Soto
 */
public class AdminFile {

    public static final int STD_BUFFER_IMAGE_SIZE = 1024;
    public static final int MINIMUM_IMAGE_SIZE = 0;
    public static final Long MAX_EXCEL_FILE_SIZE_KB = 61440L; 
    final static int BUFFER = 2048;
    public static final String REGEXP_IS_HTML_HTML = "(?i)\\bhtml.html\\b";
    public static final String REGEXP_IS_FRAME_HTML = "(?i)\\bframe.html\\b";
    public static final String REGEXP_IS_SWIFFY_HTML = "(?i)\\bswiffy.html\\b";
    public static final String REGEXP_ENDS_WITH_EDGE_JS = ".*(?i)_edge.js$";
    public static final String REGEXP_MATCH_FILENAME_AND_EXTENSION = "(\\b%s\\b(\\.(?i)(%s))$)";
    public static final String PROPERTY_BACKUP_IMAGE_WIDTH = "backup.image.width";
    public static final String PROPERTY_BACKUP_IMAGE_HEIGHT = "backup.image.height";
    private static final Logger LOG = LoggerFactory.getLogger(AdminFile.class);

    public enum FileType {

        JPG("jpg"), GIF("gif"), PNG("png"), JPEG("jpeg"), ZIP("zip"), SWF("swf"),
        FLV("flv"), HTML("html"), EXE("exe"), BAT("bat"), COM("com"), TXT("txt"),
        SO("so"), SH("sh"), O("o"), OBJ("obj"), JAR("jar"), WAR("war"), JS("js"), 
        XLSX("xlsx"), XML("xml"), TRD("3rd"), VAST("vast"), VMAP("vmap"), UNKNOWN("?");
        private String type;

        private FileType(String s) {
            type = s;
        }

        public String getFileType() {
            return type;
        }

        public static FileType typeOf(String type) {
            if(type == null){
                throw new IllegalArgumentException("Type cannot be null");
            }
            for(FileType ft : values()){
                if(ft.getFileType().equalsIgnoreCase(type)){
                    return ft;
                }
            }
            return null;
        }
    }

    /**
     * Possible validation results for ZIP files
     */
    public enum ZipCheckResult{
        VALID_ZIP_TYPE,
        VALID_HTML5_TYPE,
        INVALID_CONTAINS_ZIP,
        INVALID_CONTAINS_HARMFUL_FILE,
        INVALID_MISSING_BACKUP_IMAGE,
        INVALID_MISSING_HTML_EDGE_OR_SWF_FILE,
    }

    /**
     * Possible validation results for XLSX file
     */
    public enum FileCheckXLSXResult{
        VALID_FILE_TYPE,
        MAX_NUM_ROWS,
        INVALID_FILE_TYPE,
    }

    public static String fileType(String fileName) {
        String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|gif|png|jpeg|zip|swf|flv|html|exe|bat|com|bmp|so|sh|o|jar|war|js|txt|xml|vast|vmap|3rd))$)";
        String type = null;
        Pattern p = Pattern.compile(IMAGE_PATTERN);
        Matcher matcher = p.matcher(fileName);
        if (matcher.matches()) {
            type = matcher.group(3);
        }
        return type;
    }

    public ByteArrayOutputStream toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        byte[] bytes = new byte[STD_BUFFER_IMAGE_SIZE];
        while ((read = is.read(bytes)) != -1) {
            baos.write(bytes, 0, read);
        }
        baos.flush();
        return baos;
    }

    public boolean filenameValidation(String filePath, String filename) {
        File file = new File(filePath, filename);
        String filenameAux = null;
        boolean result = true;
        if (!file.exists()) {
            String type = fileType(filename);
            FileType fileTypeVerify = FileType.typeOf(fileType(filename));
            List<FileType> fileTypes = new ArrayList<>();
            fileTypes.add(FileType.GIF);
            fileTypes.add(FileType.JPG);
            fileTypes.add(FileType.JPEG);
            
            for (FileType fileType : fileTypes) {
                if (fileTypeVerify != fileType) {
                    filenameAux = filename.substring(0, filename.lastIndexOf(type)) + fileType.getFileType();
                    file = new File(filePath, filenameAux);
                    if (file.exists()) {
                        result = false;
                        break;
                    } else {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List getListZipFileNames(String fileZipLocation) throws Exception {
        List lisZip = new ArrayList();
        ZipInputStream zis = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(fileZipLocation);
            zis = new ZipInputStream(in);
            ZipEntry entry;
            while (null != (entry = zis.getNextEntry())) {
                lisZip.add(entry.getName());
            }
            return lisZip;
        } catch (Exception e) {
            throw e;
        } finally {
            if(zis != null) {
                zis.close();
            }
            if(in != null) {
                in.close();
            }
        }
    }

    /*Returns the file type of real file
     *
     */
    @SuppressWarnings("rawtypes")
    public static String getFileType(List fileZipList, String type) {
        String resultType="";
        int i = 0;
        while (i < fileZipList.size()) {
            if (type.equalsIgnoreCase(fileType(fileZipList.get(i).toString()))) {
                resultType = fileType(fileZipList.get(i).toString());
                break;
            }
            i++;
        }
        return resultType;
    }

    public static String getName(String filename) {
        return filename.substring(0, (filename.length() - 4));
    }

    /**
     * Evaluates a ZIP file according specific rules.
     * <p>
     * Rules are applied in specific order:
     * <ol>
     *     <li>IF zip contains a harmful file inside (*.exe, *.bat, *.so, *.sh, *.o, *.jar, or *.war); THEN, result is
     *     {@link trueffect.truconnect.api.commons.AdminFile.ZipCheckResult#INVALID_CONTAINS_HARMFUL_FILE}</li>
     *     <li>IF zip contains another zip file inside; THEN, result is
     *     {@link trueffect.truconnect.api.commons.AdminFile.ZipCheckResult#INVALID_CONTAINS_ZIP}</li>
     *     <li>IF zip does not contain backup image (GIF filename without extension should match zip's filename); THEN, result is
     *     {@link trueffect.truconnect.api.commons.AdminFile.ZipCheckResult#INVALID_MISSING_BACKUP_IMAGE}</li>
     *     <li>IF zip contains a file with name 'swiffy.html' OR 'frame.html' OR
     *     a file which filename ends with '_edje.js'; THEN, result is
     *     {@link trueffect.truconnect.api.commons.AdminFile.ZipCheckResult#VALID_HTML5_TYPE}</li>
     *     <li>IF zip contains SWF file (SWF filename without extension should match zip's filename); THEN, result is
     *     {@link trueffect.truconnect.api.commons.AdminFile.ZipCheckResult#VALID_ZIP_TYPE}</li>
     *     <li>IF zip contains a file with name 'html.html'; THEN, result is
     *     {@link trueffect.truconnect.api.commons.AdminFile.ZipCheckResult#VALID_ZIP_TYPE}</li>
     *     <li>OTHERWISE, result is
     *     {@link trueffect.truconnect.api.commons.AdminFile.ZipCheckResult#INVALID_MISSING_HTML_EDGE_OR_SWF_FILE}</li>
     * </ol>
     * @param path The base path directory where this zip file is located
     * @param zipFilename The zip filename
     * @return a {@code ZipCheckResult} according specific rules above.
     * @throws IOException if the zip file cannot be read
     */
    public ZipCheckResult checkZipFile(String path, String zipFilename) throws IOException {
        if(path == null){
            throw new IllegalArgumentException("Zip directory base path cannot be null");
        }
        if(StringUtils.isEmpty(path)){
            throw new IllegalArgumentException("Zip filename cannot be null or empty");
        }
        ZipCheckResult result;
        ZipFile zipFile = new ZipFile(new File(path, zipFilename));
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        boolean containsValidGif = false;
        boolean containsValidSwf = false;
        boolean containsValidHtml_Html = false;
        boolean containsValidSwiffy_Html = false;
        boolean containsValidFrame_Html = false;
        boolean containsValid_Edge_Js = false;
        boolean containsZip = false;
        boolean containsHarmfulFileInside = false;
        String zipFilenameNoExtension = getFilenameWithoutExtension(zipFilename);
        outerLoop:
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String filenameWithExtension = entry.getName();

            if (filenameWithExtension.contains("/")) {
                continue;
            }
            String extension = getExtension(filenameWithExtension);
            FileType fileType = FileType.typeOf(extension);
            // If FileType is null, it means one of the files is unknown to this enumeration; just continue
            if (fileType == null) {
                continue;
            }
            switch (fileType){
                case GIF:
                    if(containsValidGif) {
                       continue;
                    }
                    String matchGifPattern = String.format(REGEXP_MATCH_FILENAME_AND_EXTENSION,
                                                    zipFilenameNoExtension,
                                                    AdminFile.FileType.GIF.getFileType());
                    containsValidGif = filenameWithExtension.matches(matchGifPattern);
                    break;
                case HTML:
                    if(!containsValidHtml_Html) {
                        containsValidHtml_Html = filenameWithExtension.matches(REGEXP_IS_HTML_HTML);
                    }
                    if (!containsValidSwiffy_Html) {
                        containsValidSwiffy_Html = filenameWithExtension.matches(REGEXP_IS_SWIFFY_HTML);
                    }
                    if (!containsValidFrame_Html) {
                        containsValidFrame_Html = filenameWithExtension.matches(REGEXP_IS_FRAME_HTML);
                    }
                    break;
                case JS:
                    if(containsValid_Edge_Js) {
                        continue;
                    }
                    containsValid_Edge_Js = filenameWithExtension.matches(REGEXP_ENDS_WITH_EDGE_JS);
                    break;
                case SWF:
                    if(containsValidSwf) {
                        continue;
                    }
                    String matchSwfPattern = String.format(REGEXP_MATCH_FILENAME_AND_EXTENSION,
                                                            zipFilenameNoExtension,
                                                           AdminFile.FileType.SWF.getFileType());
                    containsValidSwf = filenameWithExtension.matches(matchSwfPattern);
                    break;
                // Contains a zip inside. Break the outer loop and stop checking
                case ZIP:
                    containsZip = true;
                    break outerLoop;
                // Check for harmful files
                case EXE:
                case BAT:
                case SO:
                case SH:
                case O:
                case JAR:
                case WAR:
                    containsHarmfulFileInside = true;
                    break outerLoop;
            }
        }
        zipFile.close();
        if(containsHarmfulFileInside){
            result = ZipCheckResult.INVALID_CONTAINS_HARMFUL_FILE;
        } else if(containsZip){
            result = ZipCheckResult.INVALID_CONTAINS_ZIP;
        } else if(!containsValidGif){
            result = ZipCheckResult.INVALID_MISSING_BACKUP_IMAGE;
        } else if(containsValidSwiffy_Html ||
                  containsValidFrame_Html ||
                  containsValid_Edge_Js ){
            result = ZipCheckResult.VALID_HTML5_TYPE;
        } else if(containsValidSwf){
            result = ZipCheckResult.VALID_ZIP_TYPE;
        } else if(containsValidHtml_Html){
            result = ZipCheckResult.VALID_ZIP_TYPE;
        } else {
            result = ZipCheckResult.INVALID_MISSING_HTML_EDGE_OR_SWF_FILE;
        }
        return result;
    }

    /**
     * Saves an InputStream into a file with name {@code filename} in a given {@code savePath}
     * @param inputStream The {@code InputStream} where to build the file to save
     * @param path The path where the {@code filename}  will be saved
     * @param filename The filename to use for the newly saved file
     * @throws IOException when the provided file cannot be written in the given path, or the
     * provided {@code InputStream} is unable to be read
     * @return {@code 1} if the file could be saved successfully. {@code 0} if the size of the input stream is 0 (Empty file)
     */
    public int saveFile(InputStream inputStream, String path, String filename) throws IOException {
        int result = 1;
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        if (path == null) {
            throw new IllegalArgumentException("Directory where to save cannot be null");
        }

        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("Filename cannot be null nor empty");
        }
        ByteArrayOutputStream baos = toByteArray(inputStream);
        int fileSize = baos.size();
        String fileExtension = getExtension(filename);
        FileType fileType = AdminFile.FileType.typeOf(fileExtension);
        if (fileSize == AdminFile.MINIMUM_IMAGE_SIZE && !fileType.equals(FileType.TXT)) {
            result = 0;
        } else {
            OutputStream out = new FileOutputStream(new File(path, filename));
            out.write(baos.toByteArray());
            out.flush();
            out.close();
        }
        // Save actual file on disk
        baos.flush();
        baos.close();
        return result;
    }
    
    public static FileType getXmlPattern(InputStream inputStream) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            String nodes = doc.getChildNodes().item(0).getNodeName();
            if (nodes.equalsIgnoreCase("VMAP") || nodes.equalsIgnoreCase("vmap:VMAP")) {
                return AdminFile.FileType.VMAP;
            } else if (nodes.equalsIgnoreCase("VAST")) {
                return AdminFile.FileType.VAST;
            } else {
                return AdminFile.FileType.XML;
            }
        } catch (Exception ex) {
            LOG.warn("Exception:", ex);
            return AdminFile.FileType.UNKNOWN;
        }
    }
    
    /**
     * Gets the Creative Properties taken from the {@code zipFilename}
     * @param path The path where the {@code zipFilename}  is located
     * @param zipFilename The {@code zipFilename}
     * @return a {@code Properties} object that contains the Creative properties:
     * <ul>
     *     <li>{@code backup.image.width}: The width of the backup image</li>
     *     <li>{@code backup.image.hight}: The height of the backup image</li>
     * </ul>
     * @throws IOException when the provided file cannot be read
     */
    public Properties getZipProperties(String path, String zipFilename) {
        Properties properties = new Properties();
        if(path == null){
            throw new IllegalArgumentException("Zip directory base path cannot be null");
        }
        if(StringUtils.isEmpty(path)){
            throw new IllegalArgumentException("Zip filename cannot be null or empty");
        }
        boolean backupImageFound = false;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(new File(path, zipFilename));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            String zipFileNameNoExtension = getFilenameWithoutExtension(zipFilename);
            String matchGifPattern = String.format(REGEXP_MATCH_FILENAME_AND_EXTENSION,
                    zipFileNameNoExtension,
                    AdminFile.FileType.GIF.getFileType());
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // Backup image file found
                boolean containsValidGif = entry.getName().matches(matchGifPattern);
                if(containsValidGif){
                    backupImageFound = true;
                    InputStream is = null;
                    try {
                        is = zipFile.getInputStream(entry);
                        BufferedImage image = ImageIO.read(is);
                        properties.setProperty(PROPERTY_BACKUP_IMAGE_WIDTH, "" + image.getWidth());
                        properties.setProperty(PROPERTY_BACKUP_IMAGE_HEIGHT, "" + image.getHeight());
                        break;
                    } catch (Exception e) {
                        LOG.warn("Exception while obtaining backup image", e);
                    }
                    finally {
                        if(is != null) {
                            is.close();
                        }
                    }

                }
            }
        } catch (Exception e) {
            LOG.warn("Exception while reading zip file", e);
        }
        finally {
            if(zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    LOG.warn("Exception while closing zip file", e);
                }
            }
        }
        if(!backupImageFound){
            throw new IllegalStateException("Zip file without backup image!");
        }
        return properties;
    }

    /**
     * Extracts the Backup image for a zip file. It leaves the GIF file in the same path as the zip file is.
     * @param path The base path directory where this zip file is located
     * @param zipFilename The zip filename
     * @throws IOException when the provided file cannot be read or the backup image cannot be written
     */
    public void extractBackupImage(String path, String zipFilename) throws IOException {
        if(path == null){
            throw new IllegalArgumentException("Zip directory base path cannot be null");
        }
        if(StringUtils.isEmpty(zipFilename)){
            throw new IllegalArgumentException("Zip filename cannot be null or empty");
        }
        String zipFileNameNoExtension = getFilenameWithoutExtension(zipFilename);
        String matchGifPattern = String.format(REGEXP_MATCH_FILENAME_AND_EXTENSION,
                zipFileNameNoExtension,
                AdminFile.FileType.GIF.getFileType());

        ZipFile zipFile = new ZipFile(new File(path, zipFilename));
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            // Backup image file found
            boolean backupImageFound = entry.getName().matches(matchGifPattern);
            if(backupImageFound){
                InputStream is = null;
                BufferedOutputStream bos = null;
                try {
                    is = zipFile.getInputStream(entry);
                    bos = new BufferedOutputStream(new FileOutputStream(new File(path, entry.getName())), BUFFER);
                    byte[] bytesIn = new byte[BUFFER];
                    int read = 0;
                    while ((read = is.read(bytesIn, 0, BUFFER)) != -1) {
                        bos.write(bytesIn, 0, read);
                    }
                    bos.close();
                    is.close();
                } catch (Exception e) {
                    LOG.warn("Exception while reading zip file contents", e);
                }
                finally {
                    if(is != null) {
                        is.close();
                    }
                    if(bos != null) {
                        bos.close();
                    }
                }
                break;
            }
        }
        zipFile.close();
    }

    /**
     * Verifies if a specific matching pattern is found in a list of Strings
     * @param regex The matching pattern
     * @param zipFilesList The list of Strings where to perform the match
     * @return true if the pattern is found in any of the elements in the list. false otherwise.
     */
    public static boolean contains(String regex, List<String> zipFilesList) {
        if(zipFilesList == null){
            throw new IllegalArgumentException("List of files within zip should not be null");
        }
        if(regex == null){
            throw new IllegalArgumentException("Matching pattern should not be null");
        }
        for(String filename : zipFilesList){
            if(filename.matches(regex)){
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts the filename extension (anything that is located after the last dot ".")
     * @param filename The filename to process
     * @return The file extension
     */
    public static String getExtension(String filename){
        if(StringUtils.isEmpty(filename)){
            throw new IllegalArgumentException("Filename must not be null");
        }
        String result = "";
        String[] tokens = filename.split("\\.(?=[^\\.]+$)");
        if(tokens == null || tokens.length == 0 || tokens.length > 2){
            return result;
        }
        return tokens[1];
    }

    /**
     * Removes the file extension and returns the filename only (all what is in front of the last ".")
     * @param filename The filename to process
     * @return the filename without extension
     */
    public static String getFilenameWithoutExtension(String filename){
        return filename.replaceFirst("[.][^.]+$", "");
    }

    /**
     * Check if filename contains unsupported characters Supported characters
     * are: ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~.
     * First character can't be: .~
     *
     * @param filename The filename to check
     * @return true if filename contains any illegal character. false if doesn't
     * contain any illegal character.
     */
    public static boolean containsUnallowedCharactersOrWrongStart(String filename) {
        if(StringUtils.isBlank(filename)){
            throw new IllegalArgumentException("Filename cannot be null or blank.");
        }
        // Check if it starts with '.' or '~'
        if (filename.matches("^[~|\\.](.*)")) {
            return true;
        } // Check if contains special characters
        else {
            return !filename.matches("^[-_.~A-Za-z0-9]*");
        }
    }

    public static int getHeight(String path, String fileName) throws IOException {
        int height = 0;
        try {
            File f = new File(path, fileName);
            if (f.exists()) {
                BufferedImage image = ImageIO.read(f);
                height = image.getHeight();
                return height;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }

    public static int getWidth(String path, String fileName) throws IOException {
        int width = 0;
        try {
            File f = new File(path, fileName);
            if (f.exists()) {
                BufferedImage image = ImageIO.read(f);
                width = image.getWidth();
                return width;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return width;
    }

    public static long getLength(String path, String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("File Name cannot be null");
        }
        long length = 0;
        File f = new File(path, fileName);
        if (f.exists()) {
            length = f.length();
        }
        return length;
    }

    @Deprecated
    public static HashMap getValuesFileZip(String path, String fileZip) throws Exception {
        HashMap result = new HashMap();
        if (unZip(path, fileZip)) {
            List list = getListZipFileNames(path + fileZip);
            String fileExtension = getFileType(list, AdminFile.FileType.GIF.getFileType());
            
            int height = getHeight(path + getName(fileZip) + "/", getName(fileZip) + "." + fileExtension);
            result.put("height", height);
            int width = getWidth(path + getName(fileZip) +"/", getName(fileZip) + "." + fileExtension);
            result.put("width", width);
            result.put("filename", getName(fileZip) + "." + fileExtension);
        }
        deleteUnZip(path + "/"+getName(fileZip)+"/");

        return result;

    }

    @Deprecated
    public static boolean unZip(String path, String fileZip) throws IOException {
        String dir = path + fileZip;
        String dirUnzip = path + "/"+getName(fileZip)+"/";
        BufferedOutputStream dest;
        FileInputStream fis = null;
        ZipInputStream zis = null;
        try {
            fis = new FileInputStream(dir);
            zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            File folder = new File(dirUnzip);
            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(dirUnzip, entry.getName());
                if(entry.isDirectory()){
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    int count;
                    byte data[] = new byte[BUFFER];
                    // write the files to the disk
                    FileOutputStream fos = new FileOutputStream(file);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
            return true;
        } catch (Exception e) {
            LOG.warn("Exception while unzipping file", e);
            return false;
        } finally {
            if(fis != null) {
                fis.close();
            }
            if(zis != null) {
                zis.close();
            }
        }
    }

    @Deprecated
    public static boolean deleteUnZip(String path) throws IOException {
        File folder = new File(path);
        if (folder.isDirectory()) {
            folder.delete();
        }
        deleteChildren(folder);
        folder.delete();
        return true;
    }

    public static boolean deleteChildren(File dir) {
        File[] children = dir.listFiles();
        boolean childrenDeleted = true;
        for (int i = 0; children != null && i < children.length; i++) {
            File child = children[i];
            if (child.isDirectory()) {
                // Check if directory has files
                if(child.list().length > 0) {
                    childrenDeleted = deleteChildren(child) && childrenDeleted;
                } else {
                    childrenDeleted = child.delete();
                }
            }
            if (child.exists()) {
                childrenDeleted = child.delete() && childrenDeleted;
            }
        }
        return childrenDeleted;
    }

    public static boolean deleteFile(String path, String file) {
        String pathFileName = path + File.separator + file;
        return deleteFile(pathFileName);
    }

    public static boolean deleteFile(String pathFileName) {
        boolean childrenDeleted = false;
        File fileT = new File(pathFileName);
          if (fileT.exists()){
             fileT.delete();
             childrenDeleted = true;
          }  
        return childrenDeleted;
    }

    /**
     * Deletes a directory recursively. If the parameters {@code path} is a file, it will be deleted.
     * @param path The path to delete recursively
     */
    public static void deleteRecursively(String path) {
        if(path == null){
            throw new IllegalArgumentException("path should not be null");
        }
        File f = new File(path);
        if (f.exists()) {
            if (f.isDirectory()) {
                for (File c : f.listFiles()) {
                    deleteFile(c);
                }
            }

            boolean deleted = f.delete();
            if (!deleted) {
                LOG.warn("Could not delete file: {} ", f.getAbsolutePath());
            }
        }
    }

    /**
     * Deletes a file recursively
     * @param f The file that contains the path to delete
     */
    private static void deleteFile(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteFile(c);
            }
        }
        if (!f.delete()) {
            LOG.warn("Could not delete file: {} ", f.getAbsolutePath());
        }
    }
    
    public static boolean deleteAllFilesSameName(String path, String fileZipName) {
        boolean fileDeleted = false;
        File f = new File(path);
        String nameFile = getName(fileZipName);
        if (f.exists()) {
            //list of files
            File[] files = f.listFiles();
            for (File file : files) {
                String nameT = getName(file.getName());
                if (nameT.equals(nameFile)) {
                    File fileT = new File(path + "/" + file.getName());
                    if (fileT.exists()) {
                        fileT.delete();
                        fileDeleted = true;
                    }
                }
            }
        }
        return fileDeleted;
    }

    public static FileCheckXLSXResult validateFileXLSX(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        FileCheckXLSXResult result = FileCheckXLSXResult.INVALID_FILE_TYPE;
        OPCPackage pkg = null;
        InputStream inputStream = null;
        try {
            if (file.exists()) {
                pkg = OPCPackage.open(file, PackageAccess.READ);
                XSSFReader reader = new XSSFReader(pkg);
                inputStream = reader.getSheetsData().next();
                result = FileCheckXLSXResult.VALID_FILE_TYPE;
            }
        } catch (Exception e) {
            LOG.warn("Could not read XLS file. Reason: {}", e.getMessage());
        } finally {
            if(pkg != null) {
                pkg.revert();
            }
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.warn("Could not close InputStream.", e);
                }
            }
        }
        return result;
    }
}

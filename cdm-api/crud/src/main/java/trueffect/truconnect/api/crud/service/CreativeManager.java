package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.AdminFile.FileType;
import trueffect.truconnect.api.commons.AdminFile.ZipCheckResult;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.APIException;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotAcceptableException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.FileUploadCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.CreativeVersion;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.CreativeAssociationsDTO;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.util.TempFileUtil;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.util.FileUtil;
import trueffect.truconnect.api.crud.util.GenericUtils;
import trueffect.truconnect.api.crud.validation.CreativeValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.resources.ResourceUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 *
 * @author Gustavo Claure, Richad Jaldin, Marleny Patsi, Marcelo Heredia
 */
public class CreativeManager extends AbstractGenericManager {

    private static Logger log = LoggerFactory.getLogger(CreativeManager.class);
    public static final int MAX_FILENAME_LENGTH = 122;

    /**
     * Old Error messages from checked exceptions thrown
     */
    public static final String ERR_MSG_CAMPAIGN_NOT_FOUND = "Campaign Id not found.";
    public static final String LEGACY_MSG_IMAGE_FILE_CANNOT_BE_EMPTY =
            "Image file cannot be empty.";
    public static final String CREATIVE_KEY_FILE_TYPE = "CREATIVE_KEY_FILE_TYPE";
    public static final String CREATIVE_KEY_OBJECT = "CREATIVE_KEY_OBJECT";

    protected CreativeDao creativeDao;
    protected CreativeGroupDao creativeGroupDao;
    protected CreativeGroupCreativeDao creativeGroupCreativeDao;
    protected CreativeInsertionDao creativeInsertionDao;
    protected CampaignDao campaignDao;
    protected UserDao userDao;
    protected ExtendedPropertiesDao extendedPropertiesDao;
    private CreativeValidator validator;
    private UtilityWrapper utilityWrapper;

    /**
     * Private class only used for a return of the setWidthHeightAndCreativeType function.
     */
    private class CreativeTypeWidthHeight{
        String creativeType;
        Long width;
        Long height;
    }

    public CreativeManager(CreativeDao creativeDao,
                           CreativeGroupDao creativeGroupDao,
                           CreativeGroupCreativeDao creativeGroupCreativeDao,
                           CreativeInsertionDao creativeInsertionDao,
                           CampaignDao campaignDao,
                           UserDao userDao,
                           ExtendedPropertiesDao extendedPropertiesDao,
                           AccessControl accessControl) {
        super(accessControl);
        this.creativeDao = creativeDao;
        this.creativeGroupCreativeDao = creativeGroupCreativeDao;
        this.creativeInsertionDao = creativeInsertionDao;
        this.campaignDao = campaignDao;
        this.userDao = userDao;
        this.creativeGroupDao = creativeGroupDao;
        this.extendedPropertiesDao = extendedPropertiesDao;
        validator = new CreativeValidator();
        utilityWrapper = new UtilityWrapperImpl();
    }

    public void setCreativeDao(trueffect.truconnect.api.crud.dao.CreativeDao creativeDao) {
        this.creativeDao = creativeDao;
    }

    public void setCreativeGroupCreativeDao(trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao creativeGroupCreativeDao) {
        this.creativeGroupCreativeDao = creativeGroupCreativeDao;
    }

    public enum CreativeType {

        ZIP("zip"),
        HTML5("html5"),
        JPG("jpg"),
        JPEG("jpeg"),
        GIF("gif"),
        TXT("txt"),
        XML("xml"),
        VMAP("vmap"), //TODO: Once the data gets corrected,
        VAST("vast"), //TODO: we should remove this VMAP and VAST types.
        TRD("3rd");

        private String type;

        CreativeType(String s) {
            type = s;
        }

        public String getCreativeType() {
            return type;
        }

        public static CreativeType typeOf(String type) {
            if(type == null){
                throw new IllegalArgumentException("Type cannot be null");
            }
            for(CreativeType ct : values()){
                if(ct.getCreativeType().equalsIgnoreCase(type)){
                    return ct;
                }
            }
            return null;
        }
    }

    /**
     * Global clasification for Creatives according
     * the XML type
     */
    public enum CreativeGlobalClassification {
        XML,
        NON_XML
    }
    
    /**
     * Get the Creative file for the Creative.
     *
     * @param id The id of the Creative.
     * @param key The OAuthKey that contains the user id executing this method
     * @return The Creative File that belongs to the provided Creative Id
     * @throws Exception when the user doesn't have access to the provided Creative Id or any other
     * Exception occurs.
     */
    public File getFile(Long id, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        //session
        SqlSession session = creativeDao.openSession();
        Creative creative = null;
        try {
            //Check access control
            if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException("Data not found for User: " + key.getUserId());
            }
            creative = this.creativeDao.get(id, session);
            // At this point, creative should never be null
            return getCreativeFile(creative, false, session);
        } finally {
            creativeDao.close(session);
        }
    }

    /**
     *
     * @param id The Creative ID
     * @param key The OAuthKey that contains the user id executing this method
     * @return a {@code java.util.Map} that includes two things:
     * <ol>
     *     <li>Element with key {@link trueffect.truconnect.api.crud.service.CreativeManager#CREATIVE_KEY_FILE_TYPE}: Which is the type of Creative</li>
     *     <li>Element with key {@link trueffect.truconnect.api.crud.service.CreativeManager#CREATIVE_KEY_OBJECT}: Which is the Creative File</li>
     * </ol>
     * Note. For ZIP creatives, the backup image will be returned instead of the actual ZIP file.
     */
    public Map<String, Object> getCreativePreviewAndType(Long id, OauthKey key) {
        // Validations
        if (id == null) {
            throw new IllegalArgumentException("Creative ID cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        Map<String, Object> result = new HashMap<>();
        SqlSession session = creativeDao.openSession();
        Creative creative = getCreativePropertiesById(id, key, session);
        // Not checking for null creative as this should not be null thanks to above line
        CreativeType creativeType = CreativeType.typeOf(creative.getCreativeType());
        result.put(CREATIVE_KEY_FILE_TYPE, creative.getCreativeType());
        Object creativeObject;
        if(creativeType == CreativeType.XML ||
                creativeType == CreativeType.VMAP ||
                creativeType == CreativeType.VAST ||
                creativeType == CreativeType.TRD) {
            InputStream inputStream = null;
            try {
                inputStream = getClass().getResourceAsStream(Constants.IMAGES_PATH + "/" + Constants.DEFAULT_PREVIEW_IMAGE);
                BufferedImage image = ImageIO.read(inputStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                creativeObject = baos;
            } catch (IOException e) {
                log.warn("Error while retrieving preview image resource", e);
                creativeObject = null;
            } finally {
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.warn("Error while closing stream", e);
                    }
                }
            }
        } else {
            creativeObject = getCreativeFile(creative, true, session);
        }
        result.put(CREATIVE_KEY_OBJECT, creativeObject);
        return result;
    }

    private Creative getCreativePropertiesById(Long id, OauthKey key, SqlSession session) {
        //Check access control
        if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(id), key.getUserId(), session)) {
            throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
        }
        return creativeDao.get(id, session);
    }

    private File getCreativeFile(Creative creative, boolean backupImageInsteadOfZip, SqlSession session){
        if(creative == null){
            throw new IllegalArgumentException("Creative cannot be null");
        }
        String filename = creative.getFilename();
        Long campaignId = creative.getCampaignId();
        // Legacy Campaigns treatment. All campaigns having RESOURCE_PATH_ID != null should use it to build
        // the Creative file path. Otherwise, it is a 'new' campaign and it should use the CAMPAIGN_ID as usual
        long pathId;
        Campaign campaign = campaignDao.get(creative.getCampaignId(), session);
        if(campaign.getResourcePathId() != null) {
            pathId = campaign.getResourcePathId();
        } else {
            pathId = campaignId;
        }
        String path = ResourceUtil.get("image.path");
        // Check if ZIP OR HTML5 type, return backup image instead. For the rest of the cases, return the
        // actual Creative image
        CreativeType creativeType = CreativeType.typeOf(creative.getCreativeType());
        if (creativeType == CreativeType.HTML5 ||
            creativeType == CreativeType.ZIP){
                // Retrieve backup image instead of the actual Creative
                // file for Zip Creatives
                if(backupImageInsteadOfZip){
                    filename = AdminFile.getFilenameWithoutExtension(creative.getFilename()) + "." +
                               AdminFile.FileType.GIF.getFileType();
                }
        }
        return  getCreativeFile(path, pathId, filename);
    }

    /**
     * Verifies the content of the zip files
     *
     * @param id id of the creative
     * @param key
     * @return the verification of the zip file.
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public boolean verification(Long id, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        //session
        SqlSession session = creativeDao.openSession();
        try {
            //Check access control
            if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeId", Long.toString(id), key.getUserId()));
            }
            Creative creative = this.creativeDao.get(id, session);
            if (creative.getId() == null) {
                throw new ValidationException("Cannot verify th Creative because it does not exist.");
            }
            ZipFile zipFile = new ZipFile(ResourceUtil.get("image.path") + id + "/" + creative.getFilename());
            List fileHeaderList = zipFile.getFileHeaders();
            //TODO: review this methods. To be covered in US5196
            if (!swfTypeVerification(fileHeaderList)) {
                throw new ValidationException("swf file not found");
            }
            if (!gifTypeVerification(fileHeaderList)) {
                throw new ValidationException("gif file not found");
            }
            if (!swfNameVerification(fileHeaderList, creative.getFilename())) {
                throw new ValidationException("swf file name does not match zip file name");
            }
            if (!gifNameVerification(fileHeaderList, creative.getFilename())) {
                throw new ValidationException("gif file name does not match zip file name");
            }
        } finally {
            creativeDao.close(session);
        }
        return true;
    }

    /**
     * Returns the Creative based on the id
     *
     * @param id ID of the Creative to return
     * @param key
     * @return the Creative of the id
     * @throws Exception
     */
    public Creative getCreative(Long id, OauthKey key) throws Exception {
        //null validations
        if (id == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        //session
        SqlSession session = creativeDao.openSession();
        Creative result = null;
        try {
            if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(id),
                    key.getUserId(), session)) {
                throw new DataNotFoundForUserException(
                        "CreativeId: " + id + " Not found for User: " + key.getUserId());
            }
            result = creativeDao.get(id, session);
        } finally {
            creativeDao.close(session);
        }
        return result;
    }

    /**
     * Get records by a Search Criteria
     *
     * @param searchCriteria The search criteria to filter.
     * @param key
     * @return
     * @throws Exception
     */
    public RecordSet<Creative> getCreatives(SearchCriteria searchCriteria, OauthKey key) throws Exception {
        //validations
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        //session
        SqlSession session = creativeDao.openSession();
        RecordSet<Creative> result = null;
        try {
            //call dao
            result = creativeDao.getCreatives(searchCriteria, key, session);
        } finally {
            creativeDao.close(session);
        }
        return result;
    }

    /**
     * Gets associations for a creativeId
     *
     * @param creativeId The Creative Id
     * @param key        The {@code OauthKey} for the requester user
     * @return CreativeRelationsDTO
     */
    public Either<Errors, RecordSet<CreativeAssociationsDTO>> getScheduleAssocPerGroupByCreativeId(
            Long creativeId, OauthKey key) {

        //null validations
        if (creativeId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Creative Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        //Obtain session
        SqlSession session = creativeDao.openSession();
        List<CreativeAssociationsDTO> result;
        Errors errors = new Errors();
        try {
            //check access control
            if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(creativeId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            result = creativeDao.getScheduleAssocPerGroupByCreativeId(creativeId, session);
        } finally {
            creativeDao.close(session);
        }
        return Either.success(new RecordSet<>(result));
    }

    /**
     * Saves a Creative
     *
     * @param creative Creative Object to create
     * @param key
     * @return the new Creative
     * @throws Exception
     */
    public Creative saveCreative(Creative creative, OauthKey key) throws Exception {
        //session
        SqlSession session = creativeDao.openSession();
        Creative result = null;
        try {
            result = this.saveCreative(creative, key, session);
            creativeDao.commit(session);
        } catch (Exception e) {
            session.rollback();
            throw e;
        } finally {
            creativeDao.close(session);
        }
        return result;
    }

    /**
     * Saves a Creative
     *
     * @param creative Creative Object to create
     * @param key
     * @return the new Creative
     * @throws Exception
     */
    private Creative saveCreative(Creative creative, OauthKey key, SqlSession session) throws Exception {
        String className = creative.getClass().getSimpleName();
        BeanPropertyBindingResult bpResult = new BeanPropertyBindingResult(creative, className);

        //validations
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        validator.validate(creative, bpResult);
        if (bpResult.hasErrors()) {
            throw new ValidationException(bpResult.toString());
        }

        Creative result = null;
        //check access control
        if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(creative.getCampaignId()), key.getUserId(), session)) {
            throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CampaignId", Long.toString(creative.getCampaignId()), key.getUserId()));
        }
        if (!userValidFor(AccessStatement.AGENCY, creative.getAgencyId(), key.getUserId(), session)) {
            throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AgencyId", Long.toString(creative.getAgencyId()), key.getUserId()));
        }

        //PK of creative
        Long id = creativeDao.getNextId(session);
        creative.setId(id);

        Long richMediaId = null;
        if (creative.getFilename().endsWith("zip")) {
            richMediaId = creativeDao.getNextId(session);
            creative.setRichMediaId(richMediaId);
        }

        //set clickThrough by default
        //" " to match XLS behavior
        creative.setClickthrough(creative.getCreativeType().equalsIgnoreCase(AdminFile.FileType.TRD.getFileType()) ? " " : Constants.DEFAULT_CLICKTHROUGH_CREATIVE);
        creative.setSwfClickCount(Constants.DEFAULT_SWF_CLICKTHROUGH_COUNT_CREATIVE);

        //call dao + retrieve recently created creative
        result = this.creativeDao.create(creative, key, session);
        return result;
    }

    /**
     * Uploads file, method used by Campaign service. Method for use into
     * Services
     *
     * @param inputStream File upload
     * @param fileName File name
     * @param campaignId Campaign Id
     * @param key OauthKey
     * @param isExpandable Boolean indicating if creative is expandable
     * @param height
     * @param width
     * @return Response status
     * @throws java.lang.Exception
     */
    @SuppressWarnings({"rawtypes", "unused"})
    public Creative saveCreativeFile(InputStream inputStream, String fileName, Long campaignId,
                                     OauthKey key, boolean isExpandable, Long height, Long width)
            throws Exception {
        //session
        SqlSession session = creativeDao.openSession();
        Creative result = null;
        try {
            Creative creativeWithOnlyFileName = new Creative();
            creativeWithOnlyFileName.setFilename(fileName);
            result = this.saveCreativeFile(inputStream, creativeWithOnlyFileName, campaignId,
                    height != null ? height : Constants.UNSET_WIDTH_OR_HEIGHT,
                    width != null ? width : Constants.UNSET_WIDTH_OR_HEIGHT, isExpandable,
                    CreativeGroupManager.DISALLOW_VERSIONING, key, session);
            creativeDao.commit(session);
        } catch (IOException e) {
            creativeDao.rollback(session);
            log.warn("Could not create creative due to an IO exception.", e);
            // This groups whatever kind of IOException we are not supporting directly
            throw new APIException(ResourceBundleUtil.getString("FileUploadCode.INTERNAL_ERROR"));
        } catch (SystemException e) {
            creativeDao.rollback(session);
            throw e;
        } catch (Exception e) {
            creativeDao.rollback(session);
            throw e;
        } finally {
            String baseTmpPath = utilityWrapper.getPath("tmp.path") + File.separator + "0" + File.separator + "creatives";
            AdminFile.deleteFile(baseTmpPath, fileName);
            creativeDao.close(session);
        }
        return result;
    }

    private void nullValidationsForInputParams(InputStream inputStream, Long campaignId, OauthKey key) throws ValidationException, IllegalArgumentException{

        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign id cannot be null");
        }
    }

    private Campaign getCampaignAndValidateFilename(Long campaignId, OauthKey key, SqlSession session,
                                                    String filename, String filenameNoExtension,
                                                    AdminFile.FileType fileType, boolean allowVersioning)
            throws ValidationException, NotAcceptableException {
        //validations
        if (AdminFile.containsUnallowedCharactersOrWrongStart(filenameNoExtension)) {
            // File name contains unsupported characters! Supported characters are: 
            // ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~. First character can't be: .~
            throw new ValidationException(
                    ResourceBundleUtil.getString("FileUploadCode.FILENAME_INVALID"));
        }
        if (fileType == null) {
            throw new NotAcceptableException(
                    ResourceBundleUtil.getString("FileUploadCode.NOT_ALLOWED_CREATIVE_FILE_TYPE"));
        }

        // Check access control
        if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId),
                key.getUserId(), session)) {
            throw BusinessValidationUtil.buildAccessSystemException(
                    SecurityCode.ILLEGAL_USER_CONTEXT);
        }
        // get campaign
        Campaign campaign = campaignDao.get(campaignId, session);
        Long creativeId = creativeDao.getCreativeIdByCampaignIdAndFileName(campaignId, filename, session);
        if (!allowVersioning && creativeId != null) {
            throw BusinessValidationUtil
                    .buildFileUploadSystemException(FileUploadCode.FILE_DUPLICATE, filename);
        }
        return campaign;
    }

    private CreativeTypeWidthHeight setWidthHeightAndCreativeType(final AdminFile.FileType fileType,  final String propertiesPath, final String filename, long widthArg, long heightArg) throws IOException{
        String creativeType = "";
        long width = 0L;
        long height = 0L;
        switch(fileType){
            case ZIP:
                //DO NOTHING FOR ZIP - it is handled by calling code that verifies specific zip types.
                break;
            case GIF:
            case JPG:
            case JPEG:
                width = AdminFile.getWidth(propertiesPath, filename);
                height = AdminFile.getHeight(propertiesPath, filename);
                creativeType = fileType.getFileType();
                break;
            case TXT:
                width = Constants.DEFAULT_WIDTH_TXT_CREATIVE;
                height = Constants.DEFAULT_HEIGHT_TXT_CREATIVE;
                creativeType = fileType.getFileType();
                break;
            case XML:
                width = Constants.DEFAULT_WIDTH_XML_CREATIVE;
                height = Constants.DEFAULT_HEIGHT_XML_CREATIVE;
                creativeType = CreativeType.XML.getCreativeType();
                break;
            case TRD:
                if ((heightArg > 0L) && (widthArg > 0L)) {
                    width = widthArg;
                    height = heightArg;
                } else {
                    width = Constants.DEFAULT_WIDTH_TRD_CREATIVE;
                    height = Constants.DEFAULT_HEIGHT_TRD_CREATIVE;
                }
                creativeType = fileType.getFileType();
                break;
        }

        CreativeTypeWidthHeight result = new CreativeTypeWidthHeight();
        result.height = height;
        result.width = width;
        result.creativeType = creativeType;

        return result;
    }

    private Creative handleFileTypesForSaveCreative(final InputStream inputStream, final String filename, final Long campaignId, final SqlSession session, final boolean isExpandable, AdminFile adminFile, final FileType fileType, final String baseTmpPath, final String finalTmpPath, StringBuilder finalPath, final String filenameNoExtension, final Campaign campaign, OauthKey key, final long widthArg, final long heightArg) throws ConflictException, ValidationException, NotAcceptableException, IOException{
        Creative creative = new Creative();

        // Legacy Campaigns treatment. All campaigns having RESOURCE_PATH_ID != null should use it to build
        // the Creative file path. Otherwise, it is a 'new' campaign and it should use the CAMPAIGN_ID as usual
        long pathId;
        if(campaign.getResourcePathId() != null) {
            pathId = campaign.getResourcePathId();
        } else {
            pathId = campaignId;
        }

        finalPath.append(utilityWrapper.getPath("image.path") + File.separator + pathId + File.separator + "creatives");

        CreativeTypeWidthHeight creativeTypeWidthHeight = null;
        //Using a StringBuilder so functions that are called with this can modify the variable if needed.
        StringBuilder creativeType = new StringBuilder();

        switch (fileType) {
            case ZIP:
                ZipCheckResult zipCheckResult = this.uploadZip(0L, inputStream, filename, baseTmpPath);
                creativeTypeWidthHeight = setWidthHeightAndCreativeType(fileType,finalTmpPath,filename, widthArg, heightArg);

                switch (zipCheckResult) {
                    case VALID_ZIP_TYPE:
                    case VALID_HTML5_TYPE:
                        Properties properties = adminFile.getZipProperties(finalTmpPath,
                                filename);
                        creativeTypeWidthHeight.width = Long.valueOf(properties.getProperty(AdminFile.PROPERTY_BACKUP_IMAGE_WIDTH));
                        creativeTypeWidthHeight.height = Long.valueOf(properties.getProperty(AdminFile.PROPERTY_BACKUP_IMAGE_HEIGHT));
                        if (zipCheckResult.equals(ZipCheckResult.VALID_HTML5_TYPE)) {
                            creativeType.append(CreativeType.HTML5.getCreativeType());
                        } else if (zipCheckResult.equals(ZipCheckResult.VALID_ZIP_TYPE)) {
                            creativeType.append(CreativeType.ZIP.getCreativeType());
                        }
                        break;
                    case INVALID_CONTAINS_HARMFUL_FILE:
                        throw new ValidationException(
                                ResourceBundleUtil.getString("FileUploadCode.ZIP_WITH_HARMFUL_FILE"));
                    case INVALID_CONTAINS_ZIP:
                        throw new ValidationException(
                                ResourceBundleUtil.getString("FileUploadCode.ZIP_WITH_ZIP_FILE"));
                    case INVALID_MISSING_BACKUP_IMAGE:
                        throw new ValidationException(
                                ResourceBundleUtil.getString("FileUploadCode.ZIP_WITH_MISSING_BACKUP_FILE", filename));
                    case INVALID_MISSING_HTML_EDGE_OR_SWF_FILE:
                        throw new ValidationException(
                                ResourceBundleUtil.getString("FileUploadCode.ZIP_WITH_MISSING_SECONDARY_FILE", filename));
                }
                break;
            case GIF:
            case JPG:
            case JPEG:
                this.uploadFile(0L, inputStream, filename, baseTmpPath);
                creativeTypeWidthHeight = setWidthHeightAndCreativeType(fileType,finalTmpPath,filename, widthArg, heightArg);
                creativeType.append(creativeTypeWidthHeight.creativeType);
                break;
            case TXT:
                this.uploadFile(0L, inputStream, filename, baseTmpPath);
                creativeTypeWidthHeight = setWidthHeightAndCreativeType(fileType,finalTmpPath,filename, widthArg, heightArg);
                creativeType.append(creativeTypeWidthHeight.creativeType);
                break;
            case XML:
                FileType xmlFileType;
                byte[] bytes = adminFile.toByteArray(inputStream).toByteArray();
                try (InputStream inputStream2 = new ByteArrayInputStream(bytes)) {
                    xmlFileType = AdminFile.getXmlPattern(inputStream2);
                }
                if (xmlFileType.equals(AdminFile.FileType.UNKNOWN)) {
                    throw new ValidationException(
                            ResourceBundleUtil.getString("global.error.fileFormat"));
                }
                try (InputStream inputStream2 = new ByteArrayInputStream(bytes)) {
                    this.uploadFile(0L, inputStream2, filename, baseTmpPath);
                    creativeTypeWidthHeight = setWidthHeightAndCreativeType(fileType,finalTmpPath,filename, widthArg, heightArg);
                    creativeType.append(creativeTypeWidthHeight.creativeType);
                }
                break;
            case TRD:
                bytes = adminFile.toByteArray(inputStream).toByteArray();
                try (InputStream inputStream2 = new ByteArrayInputStream(bytes)) {
                    this.uploadFile(0L, inputStream2, filename, baseTmpPath);
                    creativeTypeWidthHeight = setWidthHeightAndCreativeType(fileType,finalTmpPath,filename, widthArg, heightArg);
                    creativeType.append(creativeTypeWidthHeight.creativeType);
                }
                creative.setIsExpandable(isExpandable ? 1L : 0L);
                break;
            default:
                throw new NotAcceptableException(
                        ResourceBundleUtil.getString("FileUploadCode.NOT_ALLOWED_CREATIVE_FILE_TYPE"));
        }

        creative.setAgencyId(campaign.getAgencyId());
        creative.setCampaignId(campaignId);
        creative.setFilename(filename);
        String alias = StringUtils.isBlank(creative.getAlias()) ? filenameNoExtension : creative.getAlias();
        creative.setAlias(alias);
        creative.setOwnerCampaignId(campaignId);
        creative.setPurpose(null);
        creative.setScheduled(0L);
        creative.setReleased(0L);
        creative.setCreatedTpwsKey(key.getTpws());
        creative.setWidth(creativeTypeWidthHeight.width);
        creative.setHeight(creativeTypeWidthHeight.height);
        creative.setCreativeType(creativeType.toString());

        return creative;
    }

    /**
     * Uploads file, method used by Campaign service. Method for use into
     * Managers
     *
     * @param inputStream File upload
     * @param creativeToProcess
     * @param campaignId Campaign Id
     * @param key OauthKey
     * @param session SqlSession
     * @param heightArg
     * @param isExpandable Boolean indicating if creative is expandable
     * @param widthArg
     * @param allowVersioning
     * @return Response status
     * @throws java.lang.Exception
     */
    public Creative saveCreativeFile(InputStream inputStream, Creative creativeToProcess,
                                     final Long campaignId, final Long heightArg,
                                     final Long widthArg, final boolean isExpandable,
                                     final boolean allowVersioning, final OauthKey key,
                                     final SqlSession session) throws Exception {
        // Nullability checks for mandatory parameters
        nullValidationsForInputParams(inputStream, campaignId, key);
        if (creativeToProcess == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Creative"));
        }
        if (session == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "SQL Session"));
        }
        //can't add this to the validateForSaveCreativeFile above because saveCreativeFile and saveCreativeFileInTmp 
        //throw different exceptions for tpasapi and public api usage.
        String filename = creativeToProcess.getFilename();
        if (StringUtils.isBlank(filename)) {
            throw new ValidationException(
                    ResourceBundleUtil.getString("FileUploadCode.FILENAME_REQUIRED"));
        }

        String baseTmpPath = utilityWrapper.getPath("tmp.path");
        String finalTmpPath = baseTmpPath + File.separator + "0" + File.separator + "creatives";
        StringBuilder finalPath = new StringBuilder();
        String filenameNoExtension = AdminFile.getFilenameWithoutExtension(filename);
        // Check filename MAX length
        if (filenameNoExtension.length() > MAX_FILENAME_LENGTH) {
            throw new ValidationException(
                    ResourceBundleUtil.getString("FileUploadCode.FILENAME_LENGTH"));
        }

        AdminFile adminFile = new AdminFile();
        String fileExtension = AdminFile.getExtension(filename);
        AdminFile.FileType fileType = AdminFile.FileType.typeOf(fileExtension);
        // Check filename allowed characters
        Campaign campaign =
                getCampaignAndValidateFilename(campaignId, key, session, filename,
                        filenameNoExtension,
                        fileType, allowVersioning);

        //Validate file name with a regex and return extension file
        //This function modifies some of the variables passed in.
        //This deals with file type specific concerns and uploads the file to the correct location before saving to the DB
        Creative creative =
                handleFileTypesForSaveCreative(inputStream, filename, campaignId, session,
                        isExpandable, adminFile, fileType, baseTmpPath, finalTmpPath, finalPath,
                        filenameNoExtension, campaign, key, widthArg, heightArg);

        //copies only the populated fields from the passed in creative object to the existign creative object before saving to the DB
        GenericUtils.copyOnlyPopulatedFields(creative, creativeToProcess);

        //Saves the creative to the DB
        Creative result = saveCreative(creative, key, session);
        // Move temporary file into final path (no matter what format it is)
        Path sourcePath = Paths.get(new File(finalTmpPath, filename).toURI());
        File folder = new File(finalPath.toString());
        // Create create directory if needed
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Path finalMovePath = Paths.get(new File(finalPath.toString(), filename).toURI());
        Files.move(sourcePath, finalMovePath, StandardCopyOption.REPLACE_EXISTING);

        // Extract backup image only if type is zip
        if (CreativeType.typeOf(fileExtension) == CreativeType.ZIP) {
            adminFile.extractBackupImage(finalPath.toString(), filename);
        }
        return result;
    }

    /**
     *
     * @param inputStream
     * @param filename
     * @param campaignId
     * @param key
     * @param heightArg
     * @param widthArg
     * @param isExpandable
     * @return
     * @throws trueffect.truconnect.api.commons.exceptions.ValidationException
     */
    public Creative saveCreativeFileInTmp(InputStream inputStream, String filename, Long campaignId,
                                          boolean isExpandable, long heightArg,
                                          long widthArg, OauthKey key) throws ValidationException {

        //validations
        nullValidationsForInputParams(inputStream, campaignId, key);

        //can't add this to the validateForSaveCreativeFile above because saveCreativeFile 
        // and saveCreativeFileInTmp throw different exceptions for tpasapi and public api usage.
        if (StringUtils.isBlank(filename)) {
            throw BusinessValidationUtil.buildFileUploadSystemException(
                    FileUploadCode.FILENAME_REQUIRED, "filename");
        }

        String filenameNoExtension = AdminFile.getFilenameWithoutExtension(filename);

        // Check filename MAX length
        if (filenameNoExtension.length() > MAX_FILENAME_LENGTH) {
            throw BusinessValidationUtil
                    .buildFileUploadSystemException(FileUploadCode.FILENAME_LENGTH, filename);
        }

        // Check filename allowed characters
        // File name contains unsupported characters! Supported characters are: 
        // ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~. First character can't be: .~
        AdminFile adminFile = new AdminFile();
        if (adminFile.containsUnallowedCharactersOrWrongStart(filenameNoExtension)) {
            throw BusinessValidationUtil
                    .buildFileUploadSystemException(FileUploadCode.FILENAME_INVALID, filename);
        }

        AdminFile.FileType fileType = AdminFile.FileType.typeOf(AdminFile.getExtension(filename));
        if (fileType == null) {
            throw BusinessValidationUtil.buildFileUploadSystemException(
                    FileUploadCode.NOT_ALLOWED_CREATIVE_FILE_TYPE, filename);
        }

        // Create SqlSession
        SqlSession session = creativeDao.openSession();
        Creative creative = new Creative();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId),
                    key.getUserId(), session)) {
                throw BusinessValidationUtil
                        .buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            // Check if Campaign exists
            Campaign campaign = campaignDao.get(campaignId, session);
            if (campaign == null) {
                throw BusinessValidationUtil.buildBusinessSystemException(
                        BusinessCode.NOT_FOUND, "campaign");
            }

            String tempPathByCampaign = getTempPathByCampaign(campaignId);
            log.debug("Saving to temporal file: " + tempPathByCampaign);
            File folder = new File(tempPathByCampaign);
            // Create create directory if needed
            if (!folder.exists()) {
                folder.mkdirs();
            }

            CreativeTypeWidthHeight creativeTypeWidthHeight = null;
            byte[] bytes = null;
            switch (fileType) {
                case ZIP:
                    if (adminFile.saveFile(inputStream, tempPathByCampaign, filename) == 0) {
                        throw BusinessValidationUtil
                                .buildFileUploadSystemException(FileUploadCode.EMPTY_CREATIVE_FILE,
                                        filename);
                    }
                    creativeTypeWidthHeight =
                            setWidthHeightAndCreativeType(fileType, tempPathByCampaign, filename,
                                    widthArg, heightArg);
                    ZipCheckResult zipCheckResult =
                            adminFile.checkZipFile(tempPathByCampaign, filename);
                    switch (zipCheckResult) {
                        case VALID_ZIP_TYPE:
                        case VALID_HTML5_TYPE:
                            if (zipCheckResult.equals(ZipCheckResult.VALID_HTML5_TYPE)) {
                                creativeTypeWidthHeight.creativeType = CreativeType.HTML5.type;
                            } else if (zipCheckResult.equals(ZipCheckResult.VALID_ZIP_TYPE)) {
                                creativeTypeWidthHeight.creativeType = CreativeType.ZIP.type;
                            }
                            break;
                        case INVALID_CONTAINS_HARMFUL_FILE:
                            throw BusinessValidationUtil.buildFileUploadSystemException(
                                    FileUploadCode.ZIP_WITH_HARMFUL_FILE, filename);
                        case INVALID_CONTAINS_ZIP:
                            throw BusinessValidationUtil.buildFileUploadSystemException(
                                    FileUploadCode.ZIP_WITH_ZIP_FILE, filename);
                        case INVALID_MISSING_BACKUP_IMAGE:
                            throw BusinessValidationUtil.buildFileUploadSystemException(
                                    FileUploadCode.ZIP_WITH_MISSING_BACKUP_FILE, filename);
                        case INVALID_MISSING_HTML_EDGE_OR_SWF_FILE:
                            throw BusinessValidationUtil.buildFileUploadSystemException(
                                    FileUploadCode.ZIP_WITH_MISSING_SECONDARY_FILE, filename);
                    }
                    break;
                case GIF:
                case JPG:
                case JPEG:
                    if (adminFile.saveFile(inputStream, tempPathByCampaign, filename) == 0) {
                        throw BusinessValidationUtil
                                .buildFileUploadSystemException(FileUploadCode.EMPTY_CREATIVE_FILE,
                                        filename);
                    }
                    creativeTypeWidthHeight =
                            setWidthHeightAndCreativeType(fileType, tempPathByCampaign, filename,
                                    widthArg, heightArg);
                    break;
                case TXT:
                    adminFile.saveFile(inputStream, tempPathByCampaign, filename);
                    creativeTypeWidthHeight =
                            setWidthHeightAndCreativeType(fileType, tempPathByCampaign, filename,
                                    widthArg, heightArg);
                    break;
                case XML:
                    bytes = adminFile.toByteArray(inputStream).toByteArray();
                    FileType filetype = null;
                    try (InputStream inputStream2 = new ByteArrayInputStream(bytes)) {
                        filetype = AdminFile.getXmlPattern(inputStream2);
                    }
                    if (filetype.equals(AdminFile.FileType.UNKNOWN)) {
                        throw BusinessValidationUtil
                                .buildFileUploadSystemException(FileUploadCode.FILE_INVALID_FORMAT,
                                        filename);
                    }
                    try (InputStream inputStream2 = new ByteArrayInputStream(bytes)) {
                        adminFile.saveFile(inputStream2, tempPathByCampaign, filename);
                        creativeTypeWidthHeight =
                                setWidthHeightAndCreativeType(fileType, tempPathByCampaign,
                                        filename, widthArg, heightArg);
                    }
                    break;
                case TRD:
                    bytes = adminFile.toByteArray(inputStream).toByteArray();
                    try (InputStream inputStream2 = new ByteArrayInputStream(bytes)) {
                        adminFile.saveFile(inputStream2, tempPathByCampaign, filename);
                        creativeTypeWidthHeight =
                                setWidthHeightAndCreativeType(fileType, tempPathByCampaign,
                                        filename, widthArg, heightArg);
                    }
                    creative.setIsExpandable(isExpandable ? 1L : 0L);
                    break;
                default:
                    throw BusinessValidationUtil.buildFileUploadSystemException(
                            FileUploadCode.NOT_ALLOWED_CREATIVE_FILE_TYPE, filename);
            }

            // set parameters into result
            creative.setAgencyId(campaign.getAgencyId());
            creative.setCampaignId(campaignId);
            creative.setFilename(filename);
            creative.setAlias(filenameNoExtension);
            creative.setOwnerCampaignId(campaignId);
            creative.setPurpose(null);
            creative.setScheduled(0L);
            creative.setReleased(0L);
            creative.setCreatedTpwsKey(key.getTpws());
            creative.setCreativeType(creativeTypeWidthHeight.creativeType);
            creative.setWidth(creativeTypeWidthHeight.width);
            creative.setHeight(creativeTypeWidthHeight.height);

            //check if is a new creative:
            Creative existingCreative = creativeDao.getCreativeByCampaignIdAndFileName(campaignId, filename, session);
            if (existingCreative != null) {
                creative.setId(existingCreative.getId());
                creative.setCreativeVersion(existingCreative.getCreativeVersion() + 1);
                creative.setVersions(existingCreative.getVersions());
            }
        }
        catch (IllegalArgumentException e){
            throw BusinessValidationUtil.buildFileUploadSystemException(
                    FileUploadCode.EMPTY_CREATIVE_FILE, filename);
        }
        catch (IOException e) {
            // This groups whatever kind of IOException we are not supporting directly
            throw BusinessValidationUtil
                    .buildFileUploadSystemException(FileUploadCode.INTERNAL_ERROR, filename, e);
        } finally {
            creativeDao.close(session);
        }
        log.debug("Creative {} saved ", creative);
        return creative;
    }

    /**
     * Updates a Creative
     *
     * @param id Creative ID number and primary key.
     * @param inputStream
     * @param filename
     * @param key
     * @throws Exception
     */
    public void replaceImage(Long id, InputStream inputStream, String filename, OauthKey key) throws Exception {

        //null validations
        if (id == null) {
            throw new ValidationException("Creative Id cannot be empty.");
        }

        if (inputStream == null) {
            throw new ValidationException(LEGACY_MSG_IMAGE_FILE_CANNOT_BE_EMPTY);
        }

        if (filename == null) {
            throw new ValidationException("file name cannot be empty.");
        }

        if (key == null) {
            throw new ValidationException("Key cannot be empty.");
        }

        String[] filenameParts = filename.split("\\.");
        if (filenameParts.length > 1) {
            filenameParts[filenameParts.length - 1] = filenameParts[filenameParts.length - 1].toLowerCase();
            filename = StringUtils.join(Arrays.asList(filenameParts), ".");
        }

        String FILE_TMP_PATH = ResourceUtil.get("tmp.path");
        String ADD_PATH = "0/creatives/";
        String dirImage = FILE_TMP_PATH + ADD_PATH;
        String fileType = AdminFile.fileType(filename);

        //session
        SqlSession session = creativeDao.openSession();
        try {
            //check access control

            //Response response = (Response) getCreative(id); review sessions
            //TODO: review sessions. To be covered in US5196
            Creative creative = this.getCreative(id, key);

            User user = userDao.get(key.getUserId(), key.getUserId(), session);

            if (user.getAgencyId().longValue() != creative.getAgencyId().longValue()) {
                throw new AccessDeniedException("User not allowed.");
            }

            if (creative == null) {
                throw new DataNotFoundForUserException("Creative ID not found.");
            }

            if (!filename.equals(creative.getFilename())) {
                throw new NotAcceptableException("A creative's image name cannot be changed.");
            }
            Campaign campaign = campaignDao.get(creative.getCampaignId(), session);
            // Legacy Campaigns treatment. All campaigns having RESOURCE_PATH_ID != null should use it to build
            // the Creative file path. Otherwise, it is a 'new' campaign and it should use the CAMPAIGN_ID as usual
            long pathId;
            if(campaign.getResourcePathId() != null) {
                pathId = campaign.getResourcePathId();
            } else {
                pathId = campaign.getId();
            }
            if (fileType.equals(AdminFile.FileType.ZIP.getFileType())) {
                ZipCheckResult saveZip = this.uploadZip(0L, inputStream, filename, FILE_TMP_PATH);
                if (saveZip == ZipCheckResult.VALID_ZIP_TYPE || saveZip == ZipCheckResult.VALID_HTML5_TYPE) {
                    String dirFileZip = FILE_TMP_PATH + "0/creatives/";
                    HashMap dataZip = AdminFile.getValuesFileZip(dirFileZip, filename);
                    if (!(new Long(dataZip.get("height").toString()).equals(creative.getHeight()))) {
                        throw new NotAcceptableException("The zip's image's Height doesn't match the creative's height.");
                    }
                    if (!(new Long(dataZip.get("width").toString()).equals(creative.getWidth()))) {
                        throw new NotAcceptableException("The zip's image's Width doesn't match the creative's width.");
                    }
                    InputStream zipSave = new FileInputStream(dirFileZip + filename);
                    String zipPath = ResourceUtil.get("image.path");
                    this.uploadZip(pathId, zipSave, filename, zipPath);
                    AdminFile.deleteAllFilesSameName(dirFileZip, filename);
                } else {
                    throw new ConflictException("File not uploaded.");
                }
            } else if (fileType.equals(AdminFile.FileType.GIF.getFileType())
                    || fileType.equals(AdminFile.FileType.JPG.getFileType())
                    || fileType.equals(AdminFile.FileType.JPEG.getFileType())) {
                this.uploadFile(0L, inputStream, filename, FILE_TMP_PATH);
                if (!(new Long(AdminFile.getHeight(dirImage, filename)).equals(creative.getHeight()))) {
                    throw new NotAcceptableException("The image's Height doesn't match the creative's Height.");
                }
                if (!(new Long(AdminFile.getWidth(dirImage, filename)).equals(creative.getWidth()))) {
                    throw new NotAcceptableException("The image's Height doesn't match the creative's Height.");
                }

                String imagePath = ResourceUtil.get("image.path");
                try (InputStream imageSave = new FileInputStream(dirImage + filename)) {
                    this.uploadFile(pathId, imageSave, filename, imagePath);
                }
                AdminFile.deleteFile(dirImage, filename);
            } else {
                throw new NotAcceptableException("The uploaded file's type doesn't match the creative's filetype.");
            }
            creativeDao.commit(session);
        } catch (Exception e) {
            creativeDao.rollback(session);
            throw e;
        } finally {
            creativeDao.close(session);
        }
    }

    /**
     * Updates a Creative
     *
     * @param id Creative ID number and primary key.
     * @param creative Creative object to update.
     * @param key
     * @return
     * @throws Exception
     */
    public Either<Errors, Creative> updateCreative(Long id, Creative creative, OauthKey key) {
        String className = creative.getClass().getSimpleName();
        BeanPropertyBindingResult bpResult = new BeanPropertyBindingResult(creative, className);
        //validations
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        //session
        SqlSession session = creativeDao.openSession();
        Creative result = null;
        Errors errors = new Errors();
        try {
            //Check access control
            String userId = key.getUserId();
            if (!userValidFor(AccessStatement.CREATIVE, id, userId, session)) {
                String message = ResourceBundleUtil.getString("dataAccessControl.notFoundForUserSingleElement",
                        "CreativeId", String.valueOf(id), userId);
                Error error = new BusinessError(
                        message,
                        SecurityCode.ILLEGAL_USER_CONTEXT,
                        "id");
                errors.addError(error);
                return Either.error(errors);
            }
            result = creativeDao.get(id, session);
            creative.setCreativeType(result.getCreativeType());
            creative.setCampaignId(result.getCampaignId());
            validator.validateForUpdate(creative, id, bpResult);
            if (bpResult.hasErrors()) {
                errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(bpResult));
                return Either.error(errors);
            } //review more validations in service

            result.setAlias(creative.getAlias() != null ? creative.getAlias() : result.getAlias());

            result.setClickthrough(CreativeType.typeOf(result.getCreativeType()) == CreativeType.TRD
                    ? result.getClickthrough()
                    : StringUtils.isBlank(creative.getClickthrough())
                    ? result.getClickthrough()
                    : creative.getClickthrough());
            result.setScheduled(creative.getScheduled() != null ? creative.getScheduled() : result
                    .getScheduled());
            Long clickCount = (creative.getClickthroughs() != null ? creative.getClickthroughs().size() : 0L) +
                    (creative.getClickthrough() != null ? Constants.DEFAULT_SWF_CLICKTHROUGH_COUNT_CREATIVE.longValue() : 0L);
            clickCount = AdminFile.FileType.ZIP.getFileType().equalsIgnoreCase(result.getCreativeType()) ||
                    CreativeType.HTML5.getCreativeType().equalsIgnoreCase(result.getCreativeType()) ?
                    clickCount : Constants.DEFAULT_SWF_CLICKTHROUGH_COUNT_CREATIVE;
            result.setSwfClickCount(clickCount);
            result.setPurpose(creative.getPurpose());
            result.setExtProp1(creative.getExtProp1());
            result.setExtProp2(creative.getExtProp2());
            result.setExtProp3(creative.getExtProp3());
            result.setExtProp4(creative.getExtProp4());
            result.setExtProp5(creative.getExtProp5());

            if (AdminFile.FileType.TRD.getFileType().equalsIgnoreCase(result.getCreativeType())) {
                result.setIsExpandable(creative.getIsExpandable());
            }

            // Update creativeGroup associations
            Either<Error, Void> e = updateCreativeGroupsByCreativeId(creative, key, session);
            if(e.isError()) {
                errors.getErrors().add(e.error());
                return Either.error(errors);
            }
            // Update Creative versions
            updateLastVersion(creative, result, session);
            // Update Creative properties
            creativeDao.update(result, key, session);
            // Retrieve recently updated Creative
            result = creativeDao.get(id, session);

            String externalId = extendedPropertiesDao.updateExternalId("Creative", "MediaID", id,
                                                                       creative.getExternalId(),
                                                                       session);
            result.setExternalId(externalId);

            // Regenerate Clickthroughs
            creativeDao.removeCreativeClickThrough(id, key, session);
            creative.setCreatedTpwsKey(key.getTpws());

            if (AdminFile.FileType.ZIP.getFileType().equalsIgnoreCase(result.getCreativeType()) ||
                    CreativeType.HTML5.getCreativeType().equalsIgnoreCase(result.getCreativeType())) {
                creativeDao.saveCreativeClickThrough(creative, key, session);
                result.setClickthroughs(creative.getClickthroughs());
            }
            creativeDao.commit(session);
        } catch (Exception e) {
            creativeDao.rollback(session);
            Error error = new BusinessError(
                    e.getMessage(),
                    BusinessCode.INTERNAL_ERROR,
                    null);
            errors.addError(error);
            return Either.error(errors);
        } finally {
            creativeDao.close(session);
        }
        return Either.success(result);
    }

    private void updateLastVersion(Creative input, Creative onDb, SqlSession session) {
        // If provided, 'alias' should be taken from Creative entity and
        // should update to the last CreativeVersion in DB
        CreativeVersion lastVersionInDb = onDb.getVersions().get(0);
        CreativeVersion lastVersion = input.getVersionByNumber(lastVersionInDb.getVersionNumber());
        if(lastVersion != null) {
            lastVersion.setAlias(input.getAlias());
        } else {
            input.getVersions().add(new CreativeVersion(
                    input.getId(),
                    lastVersionInDb.getVersionNumber(),
                    input.getAlias(),
                    lastVersionInDb.getStartDate(),
                    lastVersionInDb.getIsDateSet(),
                    lastVersionInDb.getCampaignId()));
        }
        creativeDao.updateVersions(input.getVersions(), session);
    }

    /**
     * Removes a Creative based on the ID
     *
     * @param creativeId creative ID that needs to be removed
     * @param key The OAuthKey that contains the user id executing this method
     * @param recursiveDelete Defines whether this operation should remove or
     * not all Scheduled Associations and Creative Groups Associations OR it
     * should only delete Creative Group Associations
     * @throws trueffect.truconnect.api.commons.exceptions.SearchApiException
     * When internally a Search Query could not be built.
     * @throws trueffect.truconnect.api.commons.exceptions.ValidationException
     * When {@code recursiveDelete} was {@code false} or not provided and the
     * {@code creativeId} has existing Scheduled Associations
     * @throws java.lang.IllegalArgumentException When any required parameter is
     * null
     *
     */
    public void removeCreative(Long creativeId, OauthKey key, Boolean recursiveDelete) throws Exception {
        //null validations
        if (creativeId == null) {
            throw new IllegalArgumentException("Creative ID cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        //session
        SqlSession session = creativeDao.openSession();
        try {
            //Check access Control
            if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(creativeId),
                    key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil
                        .getString("dataAccessControl.notFoundForUser", "CreativeId",
                                Long.toString(creativeId), key.getUserId()));
            }

            if (recursiveDelete != null && recursiveDelete) {
                //Delete Creative, CreativeGroupCreative and CreativeInsertions
                log.debug("Deleting recursively");
                removeSingleCreative(creativeId, key, recursiveDelete, session);
            } else {
                //Delete Creative and CreativeGroupCreative only if there are no CreativeInsertions, otherwise send error message
                log.debug("Deleting non recursively");
                SearchCriteria criteria = new SearchCriteria();
                criteria.setQuery("creativeId equals to " + Long.toString(creativeId));
                Long totalRecords = creativeInsertionDao.getScheduleCountByCreativeIds(criteria, key.getUserId(), session);
                if (totalRecords > 0L) {
                    throw new ValidationException("Creative " + creativeId + " could not be deleted because it is already scheduled.");
                } else {
                    removeSingleCreative(creativeId, key, false, session);
                }
            }
            creativeDao.commit(session);
        } catch (Exception e) {
            creativeDao.rollback(session);
            throw e;
        } finally {
            creativeDao.close(session);
        }
        log.info(key.toString() + " Deleted " + creativeId);
    }

    public Either<Errors, SuccessResponse> removeCreative(Long creativeId, RecordSet<Long> groupIds,
                                                          OauthKey key) {
        //null validations
        if (creativeId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Creative ID"));
        }

        if (groupIds == null || groupIds.getRecords().isEmpty()) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.emptyList", "groupIds"));
        }

        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        Set<Long> setGroupIds = new HashSet<>();
        for (Long groupId : groupIds.getRecords()) {
            if (groupId != null) {
                setGroupIds.add(groupId);
            }
        }
        Errors errors = new Errors();
        if (setGroupIds.isEmpty()) {
            ValidationError error =
                    new ValidationError(ResourceBundleUtil.getString("global.error.emptyList",
                            "GroupIds List"), ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }

        //session
        SqlSession session = creativeDao.openSession();
        SuccessResponse result = null;
        try {
            //Check access Control
            if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(creativeId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            if (!userValidFor(AccessStatement.CREATIVE_GROUP, setGroupIds,
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            //1. Check if it has schedules for the groups we want to deassociate
            //  Yes: throw error
            Long totalRecords = creativeInsertionDao
                    .getCountSchedulesByrCreativeAndGroupIds(creativeId, setGroupIds,
                            key.getUserId(), session);
            if (totalRecords > 0L) {
                BusinessError error = new BusinessError(ResourceBundleUtil
                        .getString("creative.error.alreadyScheduled", creativeId.toString()),
                        BusinessCode.INVALID, "creativeId");
                errors.addError(error);
                return Either.error(errors);
            }

            //2. Get all group associations
            List<CreativeGroupCreative> cgcRecords =
                    creativeGroupCreativeDao.getByCreative(creativeId, session);

            //3. foreach groupId in provided groups
            for (CreativeGroupCreative cgc : cgcRecords) {
                // de-associate group and creative only if group id is provided
                if (groupIds.getRecords().contains(cgc.getCreativeGroupId())) {
                    creativeGroupCreativeDao.remove(cgc.getCreativeGroupId(), cgc.getCreativeId(),
                            session);
                }
            }

            //4. Check if groups provided are all or not
            if (groupIds.getRecords().size() >= cgcRecords.size()) {
                this.creativeDao.remove(creativeId, key, session);
                result = new SuccessResponse(ResourceBundleUtil
                        .getString("creative.info.successfullyDeleted", creativeId.toString()));
            } else {
                result = new SuccessResponse(ResourceBundleUtil
                        .getString("creative.info.successfullyDeAssociated", creativeId.toString(),
                                groupIds.getRecords().toString()));
            }

            creativeDao.commit(session);
        } catch (Exception e) {
            creativeDao.rollback(session);
            Error error = new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR);
            errors.addError(error);
            return Either.error(errors);
        } finally {
            creativeDao.close(session);
        }
        log.debug("Creative = {} was successfully deleted by the User = {} .", creativeId,
                key.toString());

        //5. return SuccessResponse
        return Either.success(result);
    }

    /**
     * Removes several Creatives
     *
     * @param creatives creatives to be removed
     * @param key
     * @return status OK if the Creative has been deleted successfully
     */
    public SuccessResponse removeCreatives(RecordSet<Creative> creatives, OauthKey key) {

        //validations
        if (creatives.getRecords() == null || creatives.getRecords().size() <= 0) {
            throw new IllegalArgumentException("Creatives records cannot be empty");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        //session
        SqlSession session = creativeDao.openSession();
        try {
            List<Long> creativeIds = new ArrayList<>();
            for (Creative creative : creatives.getRecords()) {
                creativeIds.add(creative.getId());
            }
            // Check access control
            if (!userValidFor(AccessStatement.CREATIVE, creativeIds, key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            SearchCriteria criteria = new SearchCriteria();
            criteria.setQuery("creativeId in " + creativeIds.toString());

            // check if there are no Schedules
            if (creativeInsertionDao.getScheduleCountByCreativeIds(criteria, key.getUserId(), session) > 0L) {
                throw BusinessValidationUtil.buildBusinessSystemException(
                        new ValidationException("Creatives could not be deleted because they are already scheduled."),
                        BusinessCode.INVALID, "creatives"
                );
            } else {
                for (Long creativeId : creativeIds) {
                    removeSingleCreative(creativeId, key, false, session);
                }
            }
            creativeDao.commit(session);
        } catch (Exception e) {
            creativeDao.rollback(session);
            throw BusinessValidationUtil
                    .buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);
        } finally {
            creativeDao.close(session);
        }
        return new SuccessResponse("Creatives successfully deleted");
    }

    public Either<Error, RecordSet<Creative>> getCreativesWithNoGroupAssociation(Long campaignId, Long groupId, Long startIndex, Long pageSize, OauthKey key) {
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        if (campaignId == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.null", "Campaign Id"),
                    ValidationCode.INVALID));
        }
        if (groupId == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.null", "Group Id"),
                    ValidationCode.INVALID));
        }

        // Validate paginator
        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex",startIndex);
        paginator.put("pageSize", pageSize);
        Either<Error, HashMap<String, Long>> validPaginator = validatePaginator(paginator);
        if(validPaginator.isError()) {
            return Either.error(validPaginator.error());
        } else {
            paginator = validPaginator.success();
        }

        RecordSet<Creative> result = null;
        SqlSession session = creativeDao.openSession();
        try {
            //  Check DAC
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.PERMISSION_DENIED));
            }

            if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(groupId), key.getUserId(), session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.PERMISSION_DENIED));
            }

            // Business Logic
            List<Creative> creatives = creativeDao.getCreativesWithNoGroupAssociation(
                    campaignId, groupId, paginator.get("startIndex"), paginator.get("pageSize"),
                    session);
            Long counterResult = creativeDao.getCountForCreativesWithNoGroupAssociation(campaignId,
                    groupId,
                    paginator.get("startIndex"), paginator.get("pageSize"), session);

            result = new RecordSet<>(
                    paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(),
                    counterResult.intValue(),
                    creatives);
        } finally {
            creativeDao.close(session);
        }
        return Either.success(result);
    }

    public Either<Error,RecordSet<Creative>> getCreativesByCampaign(Long campaignId, Long startIndex, Long pageSize, OauthKey key) {
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        if (campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Campaign Id"));
        }

        // Validate paginator
        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex",startIndex);
        paginator.put("pageSize", pageSize);
        Either<Error, HashMap<String, Long>> validPaginator = validatePaginator(paginator);
        if(validPaginator.isError()) {
            return Either.error(validPaginator.error());
        } else {
            paginator = validPaginator.success();
        }

        RecordSet<Creative> result = null;
        SqlSession session = creativeDao.openSession();
        try {
            //  Check DAC
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }

            // Business Logic
            List<Creative> creatives = creativeDao.getCreativesByCampaign(
                    campaignId, paginator.get("startIndex"), paginator.get("pageSize"), session);
            Long counterResult = creativeDao.getCountForCreativesByCampaign(campaignId,
                    paginator.get("startIndex"), paginator.get("pageSize"), session);

            result = new RecordSet<>(
                    paginator.get("startIndex").intValue(),
                    paginator.get("pageSize").intValue(),
                    counterResult.intValue(),
                    creatives);
        } finally {
            creativeDao.close(session);
        }
        return Either.success(result);
    }

    private void removeSingleCreative(Long id, OauthKey key, Boolean recursiveDelete, SqlSession session) throws Exception {
        if (recursiveDelete != null && recursiveDelete) {
            // Removing CreativeInsertions
            SearchCriteria criteria = new SearchCriteria();
            criteria.setQuery("creativeId equals to " + id);
            RecordSet<CreativeInsertion> ciRecords = creativeInsertionDao.get(criteria, key, session);
            for (CreativeInsertion record : ciRecords.getRecords()) {
                creativeInsertionDao.delete(record.getId(), key, session);
            }
        }
        // Removing CreativeGroupCreatives
        List<CreativeGroupCreative> cgcRecords = creativeGroupCreativeDao.getByCreative(id, session);
        for (CreativeGroupCreative record : cgcRecords) {
            record.setLogicalDelete(Constants.YES_FLAG);
            creativeGroupCreativeDao.update(record, key, session);
        }

        // Removing Creatives
        this.creativeDao.remove(id, key, session);
    }

    /**
     * Upload Image (JPEG/gif)
     *
     * @param pathId Campaign ID or Resourc Path ID
     * @param inputStream File Image
     * @param filename File Image Detail
     * @param path Path File
     * @return Response status
     * @throws Exception
     */
    private void uploadFile(final Long pathId, final InputStream inputStream, final String filename, final String path) throws ValidationException, ConflictException, IOException {
        String pathIdLocation = path + File.separator + pathId;
        String pathIdLocationCreatives = pathIdLocation + File.separator + "creatives";
        AdminFile file = new AdminFile();
        File folder = new File(pathIdLocationCreatives);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        if (file.filenameValidation(pathIdLocationCreatives + "/", filename)) {
            if(file.saveFile(inputStream, pathIdLocationCreatives, filename) == 0) {
                throw new ValidationException(
                                        ResourceBundleUtil.getString("FileUploadCode.EMPTY_CREATIVE_FILE"));
            }
        } else {
            throw new ConflictException(
                                    ResourceBundleUtil.getString("FileUploadCode.FILENAME_DUPLICATE"));
        }
    }

    /**
     * Uploads zip file
     *
     * @param campaignId Campaign ID or Resourc Path ID
     * @param inputStream File zip
     * @param filenameZip File zip name
     * @param path Path File
     * @return Response status
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    private ZipCheckResult uploadZip(Long campaignId, InputStream inputStream, String filenameZip, String path) throws IOException, ValidationException {
        String pathIdLocationCreatives =  path + File.separator + campaignId + File.separator + "creatives";

        AdminFile file = new AdminFile();

        File folder = new File(pathIdLocationCreatives);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        int saveFileResult = file.saveFile(inputStream, pathIdLocationCreatives, filenameZip);
        if(saveFileResult == 0) {
            throw new ValidationException(
                                    ResourceBundleUtil.getString("FileUploadCode.EMPTY_CREATIVE_FILE"));
        }
        return file.checkZipFile(pathIdLocationCreatives, filenameZip);
    }

    /**
     * Update associations CreativeGroup - Creatives. Method for use into
     * Managers
     *
     * @param creative
     * @param key
     * @param session
     * @throws Exception
     */
    private Either<Error, Void> updateCreativeGroupsByCreativeId(Creative creative, OauthKey key, SqlSession session) {

        if(creative.getCreativeGroups() == null) {
            return Either.success(null);
        }

        List<Long> groupsToSave = new ArrayList<>();
        for (CreativeGroup group : creative.getCreativeGroups()) {
            groupsToSave.add(group.getId());
        }

        //Remove duplicates from creativeGroupsIds
        HashSet hs = new HashSet();
        hs.addAll(groupsToSave);
        groupsToSave = new ArrayList<>();
        groupsToSave.addAll(hs);

        //check Data access control
        if (!userValidFor(AccessStatement.CREATIVE_GROUP, groupsToSave, key.getUserId(), session)) {
            Error error = new BusinessError(
                    ResourceBundleUtil.getString("SecurityCode.ILLEGAL_USER_CONTEXT"),
                    SecurityCode.ILLEGAL_USER_CONTEXT,
                    "id");
            return Either.error(error);
        }

            //validate the creativeGroups belongs to campaignId
            Long groups = creativeGroupDao.getCountCreativeGroupsByCampaignId(creative.getCampaignId(), groupsToSave, session);
            if (groups != groupsToSave.size()) {
                Error error = new ValidationError(
                        ResourceBundleUtil.getString("creative.error.groupDoesNotBelongToCampaign"),
                        ValidationCode.INVALID);
                return Either.error(error);
            }

        List<CreativeGroupCreative> existentGroupsByCreative = creativeGroupCreativeDao.getByCreative(creative.getId(), session);
        List<Long> existentGroups = new ArrayList<>();
        for (CreativeGroupCreative group : existentGroupsByCreative) {
            existentGroups.add(group.getCreativeGroupId());
        }

        List<Long> newAssociatons = new ArrayList<>();
        newAssociatons.addAll(groupsToSave);
        newAssociatons.removeAll(existentGroups);

        List<Long> removeAssociations = new ArrayList<>();
        removeAssociations.addAll(existentGroups);
        removeAssociations.removeAll(groupsToSave);

        if (newAssociatons.size() > 0) {
            for (Long groupId : newAssociatons) {
                CreativeGroupCreative cg = new CreativeGroupCreative();
                cg.setCreativeGroupId(groupId);
                cg.setCreativeId(creative.getId());
                cg.setDisplayOrder(0L);
                cg.setDisplayQuantity(0L);
                cg.setCreatedTpwsKey(key.getTpws());
                creativeGroupCreativeDao.save(cg, session);
            }
        }

        if (removeAssociations.size() > 0) {
            for (Long groupId : removeAssociations) {
                creativeGroupCreativeDao.remove(groupId, creative.getId(), session);
            }
        }
        return Either.success(null);
    }

    private File getCreativeFile(String path, Long pathId, String filename) {
        if(path == null){
            throw new IllegalArgumentException("Path cannot be null");
        }
        if(pathId == null){
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "pathId"));
        }
        if(StringUtils.isEmpty(filename)){
            throw new IllegalArgumentException("Filename cannot be null nor empty");
        }
        String creativePath = path + pathId + File.separator + "creatives" + File.separator + filename;

        File file = utilityWrapper.createFile(creativePath);
        if (!file.exists()) {
            file = null;
        }
        return file;
    }
    
    @SuppressWarnings("rawtypes")
    private boolean swfTypeVerification(List fileHeaderList) {
        int i = 0;
        while (i < fileHeaderList.size()) {
            FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
            if (FileUtil.isSwf(fileHeader.getFileName())) {
                return true;
            }
            i++;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private boolean gifTypeVerification(List fileHeaderList) {
        int i = 0;
        while (i < fileHeaderList.size()) {
            FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
            if (FileUtil.isGif(fileHeader.getFileName())) {
                return true;
            }
            i++;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private boolean swfNameVerification(List fileHeaderList, String filename) {
        int i = 0;
        while (i < fileHeaderList.size()) {
            FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
            if (FileUtil.isSwf(fileHeader.getFileName())) {
                if (FileUtil.getName(fileHeader.getFileName()).equals(FileUtil.getName(filename))) {
                    return true;
                }
            }
            i++;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private boolean gifNameVerification(List fileHeaderList, String filename) {
        int i = 0;
        while (i < fileHeaderList.size()) {
            FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
            if (FileUtil.isGif(fileHeader.getFileName())) {
                if (FileUtil.getName(fileHeader.getFileName()).equals(FileUtil.getName(filename))) {
                    return true;
                }
            }
            i++;
        }
        return false;
    }

    /**
     * Builds the temporary path of a filename given a {@code campaignId} and
     * {@code filename}
     *
     * @param campaignId The Campaign Id to use in the Temporary Creative Path
     * @param filename The filename to use in the Temporary Creative Path
     * @return The Path where the temporary Creative has been saved to
     */
    public static String buildTempPathFor(Long campaignId, String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        return getTempPathByCampaign(campaignId) + filename;
    }

    /**
     * Builds the temporary path of a filename given a {@code campaignId} and
     * {@code filename}
     *
     * @param campaignId The Campaign Id to use in the Temporary Creative Path
     * @return The Path where the temporary Creative has been saved to
     */
    public static String getTempPathByCampaign(Long campaignId) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        return TempFileUtil.OS_TMP_PATH + File.separator + "CreativeFiles" + File.separator + campaignId + File.separator + "creatives" + File.separator;
    }

    public void setUtilityWrapper(UtilityWrapper utilityWrapper) {
        this.utilityWrapper = utilityWrapper;
    }

    /**
     * Interface to make this class testable
     * */
    interface UtilityWrapper {
        File createFile(String path);

        String getPath(String path);
    }

    /**
     * Default implementation for {@code FileWrapper}
     */
    class UtilityWrapperImpl implements UtilityWrapper {
        @Override
        public File createFile(String path) {
            return new File(path);
        }

        @Override
        public String getPath(String path) {
            return ResourceUtil.get(path);
        }
    }
}

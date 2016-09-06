package trueffect.truconnect.api.publik.service.mapper;

import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException;
import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.FileUploadCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.validation.ValidationConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Jersey Exception Mapper that allows mapping {@link trueffect.truconnect.api.commons.exceptions.WebApplicationSystemException} to
 * a variable HTTP Error Status
 * Created by Marcelo Heredia on 11/12/2014.
 *
 * TODO Once moving to Jersey 2.x, this mapper can catch exception sof type {@link trueffect.truconnect.api.commons.exceptions.SystemException}
 */
@Provider
public class SystemExceptionMapper implements ExceptionMapper<WebApplicationSystemException> {

    private static final Log log = LogFactory.getLog(SystemExceptionMapper.class);

    private static final Map<ErrorCode, Response.Status> ERROR_CODE_STATUS_MAP;
    // Mapping of Error Codes to HTTP Response status
    static{
        // Validation related
        ERROR_CODE_STATUS_MAP = new HashMap<>();
        ERROR_CODE_STATUS_MAP.put(ValidationCode.GREATER_THAN, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(ValidationCode.INVALID, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(ValidationCode.LOWER_THAN, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(ValidationCode.NONCONSECUTIVE_DATES, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(ValidationCode.REQUIRED, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(ValidationCode.TOO_LONG, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(ValidationCode.TOO_SHORT, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(ValidationCode.UNSUPPORTED, Response.Status.BAD_REQUEST);
        // Business related
        ERROR_CODE_STATUS_MAP.put(BusinessCode.INVALID, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(BusinessCode.ENTITY_ID_MISMATCH, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(BusinessCode.DUPLICATE, Response.Status.CONFLICT);
        ERROR_CODE_STATUS_MAP.put(BusinessCode.NOT_FOUND, Response.Status.NOT_FOUND);
        ERROR_CODE_STATUS_MAP.put(BusinessCode.INTERNAL_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
        ERROR_CODE_STATUS_MAP.put(BusinessCode.UNKNOWN, Response.Status.INTERNAL_SERVER_ERROR);
        // For now, any error related to External Components will be considered as "503 Service Unavailable"
        ERROR_CODE_STATUS_MAP.put(BusinessCode.EXTERNAL_COMPONENT_ERROR, Response.Status.SERVICE_UNAVAILABLE);
        // Security related
        ERROR_CODE_STATUS_MAP.put(SecurityCode.UNAUTHORIZED, Response.Status.UNAUTHORIZED);
        ERROR_CODE_STATUS_MAP.put(SecurityCode.ILLEGAL_USER_CONTEXT, Response.Status.NOT_FOUND);
        ERROR_CODE_STATUS_MAP.put(SecurityCode.INVALID_REQUEST, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(SecurityCode.INVALID_TOKEN, Response.Status.UNAUTHORIZED);
        ERROR_CODE_STATUS_MAP.put(SecurityCode.ERROR_GETTING_PERMISSIONS, Response.Status.INTERNAL_SERVER_ERROR);
        ERROR_CODE_STATUS_MAP.put(SecurityCode.PERMISSION_DENIED, Response.Status.FORBIDDEN);
        ERROR_CODE_STATUS_MAP.put(SecurityCode.NOT_FOUND_FOR_USER, Response.Status.NOT_FOUND);
        // File Upload Related
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.FILE_DUPLICATE, Response.Status.CONFLICT);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.FILENAME_DUPLICATE, Response.Status.CONFLICT);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.FILENAME_INVALID, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.FILENAME_LENGTH, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.FILENAME_REQUIRED, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.NOT_ALLOWED_CREATIVE_FILE_TYPE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.EMPTY_CREATIVE_FILE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.MAX_FILE_SIZE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.FILE_INVALID_FORMAT, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.NOT_ALLOWED_IMPORT_FILE_TYPE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.NOT_ALLOWED_FILE_TYPE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.FILE_XLSX_MAX_ROWS, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.EMPTY_FILE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.ZIP_WITH_HARMFUL_FILE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.ZIP_WITH_ZIP_FILE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.ZIP_WITH_MISSING_BACKUP_FILE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.ZIP_WITH_MISSING_SECONDARY_FILE, Response.Status.BAD_REQUEST);
        ERROR_CODE_STATUS_MAP.put(FileUploadCode.INTERNAL_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
    }

    @Context
    private HttpHeaders headers;

    public SystemExceptionMapper() {
        log.info("Creating Exception Mapper Provider: " + this.getClass().getCanonicalName());
    }

    public Response toResponse(WebApplicationSystemException wase) {
        // TODO: do we really want to always log an error here?
        log.error("ExceptionMapper: " + wase.getMessage(), wase);
        // Get nested exception to process
        SystemException ex = (SystemException) wase.getCause();
        Error error;
        Errors errors = new Errors();
        Response.Status status = null;
        String wwwAuthenticate = null;
        // Check for individual errors
        if(ex.getErrorCode() instanceof ValidationCode && ex.getProperties().containsKey(ValidationConstants.KEY_VALIDATION_RESULT)){
            Object obj = ex.getProperties().get(ValidationConstants.KEY_VALIDATION_RESULT);
            if(obj instanceof BeanPropertyBindingResult) {
                BeanPropertyBindingResult validationResult = (BeanPropertyBindingResult) obj;
                List<ObjectError> objectErrors = validationResult.getAllErrors();
                for(ObjectError objectError : objectErrors){
                    ValidationError validationError = new ValidationError();
                    validationError.setCode(ValidationCode.INVALID);
                    validationError.setMessage(objectError.getDefaultMessage());
                    validationError.setMessageCode(objectError.getCode());
                    if(objectError instanceof FieldError){
                        FieldError fieldError = (FieldError) objectError;
                        validationError.setField(fieldError.getField());
                        validationError.setObjectName(fieldError.getObjectName());
                        if(fieldError.getRejectedValue() != null){
                            validationError.setRejectedValue(fieldError.getRejectedValue().toString());
                        }
                    }
                    errors.addError(validationError);
                }
            } if(obj instanceof Errors) {
                errors = (Errors)obj;
            } else if(obj instanceof ValidationError) {
                ValidationError validationError = (ValidationError) obj;
                errors.addError(validationError);
            }
        }
        else if(ex.getErrorCode() instanceof BusinessCode){

            // In case there are custom BusinessError
            if(ex.getProperties().containsKey(ValidationConstants.KEY_BUSINESS_ERROR)){
                error = (Error) ex.getProperties().get(ValidationConstants.KEY_BUSINESS_ERROR);
            }
            else{
                BusinessCode code = (BusinessCode) ex.getErrorCode();
                // Check if External Exception type. If so, HTTP response status is controlled by the nested exception
                if(code == BusinessCode.EXTERNAL_COMPONENT_ERROR){
                    error = new Error(String.format(ex.getMessage(), ex.getCause().getMessage()), code);
                    if(ex.getCause() instanceof WebApplicationException){
                        WebApplicationException cause = (WebApplicationException) ex.getCause();
                        status = Response.Status.fromStatusCode(cause.getResponse().getStatus());
                    }
                    else{
                        status = Response.Status.SERVICE_UNAVAILABLE;
                    }
                    // In case the exception is not a WebApplicationException, it will be considered as 503 Service Unavailable
                }else{
                    error = new Error(ex.getClass().getCanonicalName() + ":" + ex.getMessage(), code);
                }
            }
            errors.addError(error);
        }
        else if(ex.getErrorCode() instanceof SecurityCode){
            // Obtain Error message associated to exception if it exists
            if(ex.getProperties().containsKey(ValidationConstants.KEY_SECURITY_ERROR)){
                error = (Error) ex.getProperties().get(ValidationConstants.KEY_SECURITY_ERROR);
                errors.addError(error);
            }

            // Obtain "WWW-Authenticate" header if it exists
            if(ex.getProperties().containsKey(ValidationConstants.KEY_WWW_AUTHENTICATE)){
                wwwAuthenticate = (String) ex.getProperties().get(ValidationConstants.KEY_WWW_AUTHENTICATE);
            }
        }
        else if(ex.getErrorCode() instanceof FileUploadCode){
            // Obtain Error message associated to exception if it exists
            if(ex.getProperties().containsKey(ValidationConstants.KEY_FILE_UPLOAD_ERROR)){
                error = (Error) ex.getProperties().get(ValidationConstants.KEY_FILE_UPLOAD_ERROR);
                errors.addError(error);
            }
        }
        else{
            error = new Error(ex.getMessage(), BusinessCode.UNKNOWN);
            errors.addError(error);
        }
        if(status == null){
            status = ERROR_CODE_STATUS_MAP.get(ex.getErrorCode());
        }
        Response.ResponseBuilder response = Response.status(status);
        // Adding extra headers if apply
        if(wwwAuthenticate != null){
            response.header("WWW-Authenticate", wwwAuthenticate);
        }
        // Get the first acceptable media type. Default to application/json if no type exists or it is */* only
        MediaType mediaType;
        if(headers.getAcceptableMediaTypes().size() > 0){
            mediaType = headers.getAcceptableMediaTypes().iterator().next();
            if("*/*".equalsIgnoreCase(mediaType.toString())){
                mediaType = MediaType.APPLICATION_JSON_TYPE;
            }
        }else{
            mediaType = MediaType.APPLICATION_JSON_TYPE;
        }
        return response.
                entity(errors).
                type(mediaType).
                build();
    }
}

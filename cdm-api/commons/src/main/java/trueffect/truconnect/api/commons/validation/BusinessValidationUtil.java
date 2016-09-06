package trueffect.truconnect.api.commons.validation;

import trueffect.truconnect.api.commons.exceptions.APIException;
import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;
import trueffect.truconnect.api.commons.exceptions.business.FileUploadError;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.FileUploadCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;

import org.springframework.validation.Errors;

/**
 * Business Validation Utilities
 * <p>
 * This class uses new Exception and Error hierarchy.
 * Created by marcelo.heredia on 5/29/2015.
 * @author Marcelo Heredia
 */
public class BusinessValidationUtil {
    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_BUSINESS_ERROR} of type {@code BusinessError}
     * @param code The {@code ErrorCode} used to build this {@code SystemException}
     * @param field The {@code field} in error
     * @return systemException
     */
    public static SystemException buildBusinessSystemException(ErrorCode code, String field){
        // Not using JSR-303 validation as this is an internal utility method, not a business method. Therefore,
        // errors in parameters should be uncommon and not propagated to SystemExceptionMapper class
        if(code == null){
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(code);
        BusinessError businessError = new BusinessError(systemException.getMessage(), code, field);
        systemException.set(ValidationConstants.KEY_BUSINESS_ERROR, businessError);
        return systemException;
    }

    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_BUSINESS_ERROR} of type {@code BusinessError}
     * @param cause The {@code Throwable} used to build this {@code SystemException}
     * @param code The {@code ErrorCode} used to build this {@code SystemException}
     * @param field The {@code field} in error
     * @return systemException
     */
    public static SystemException buildBusinessSystemException(Throwable cause, ErrorCode code, String field){
        // Not using JSR-303 validation as this is an internal utility method, not a business method. Therefore,
        // errors in parameters should be uncommon and not propagated to SystemExceptionMapper class
        if(code == null){
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(cause, code);
        BusinessError businessError = new BusinessError(systemException.getMessage(), code, field);
        systemException.set(ValidationConstants.KEY_BUSINESS_ERROR, businessError);
        return systemException;
    }
    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_BUSINESS_ERROR} of type {@link Errors}
     * @param code The {@code ErrorCode} used to build this {@code SystemException}
     * @param errors The {@code Errors} object that contains the Spring Validation errors
     * @return systemException
     */
    public static SystemException buildSpringValidationSystemException(ErrorCode code, Errors errors){
        if(code == null){
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(code);
        systemException.set(ValidationConstants.KEY_VALIDATION_RESULT, errors);
        return systemException;
    }

    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_VALIDATION_RESULT} of type {@code ValidationError}
     * @param code The {@code ErrorCode} used to build this {@code SystemException}
     * @param field The {@code field} in error
     * @return systemException
     */
    public static SystemException buildValidationSystemException(ValidationCode code,
                                                                 String field,
                                                                 String objectName,
                                                                 String rejectedValue,
                                                                 Integer minLength,
                                                                 Integer maxLength,
                                                                 String minValue,
                                                                 String maxValue){
        if (code == null) {
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(code);
        ValidationError validationError = new ValidationError(systemException.getMessage(),
                systemException.getErrorCode(),
                field,
                objectName,
                rejectedValue,
                null,
                minLength,
                maxLength,
                minValue,
                maxValue);
        systemException.set(ValidationConstants.KEY_VALIDATION_RESULT, validationError);
        return systemException;
    }

    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_VALIDATION_RESULT} of
     * type {@code ValidationError} using a {@code code} and multiple {@code messageParams}
     * @param error The {@code error} object
     * @param code The {@code ValidationCode code} used to build this {@code SystemException}
     * @param messageParams The {@code params} used to the localized message associated to {@code ValidationCode code}.
     *                      See {@code exception.properties} to know the mapping of i18n messages against {@code code}
     * @return systemException
     *
     */
    public static SystemException buildSystemException(ValidationError error,
                                                       ValidationCode code,
                                                       Object... messageParams){
        if (code == null) {
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(code, messageParams);
        systemException.set(ValidationConstants.KEY_VALIDATION_RESULT, error);
        error.setMessage(systemException.getMessage());
        error.setCode(systemException.getErrorCode());
        return systemException;
    }

    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_VALIDATION_RESULT} of
     * type {@code ValidationError} using a {@code code} and multiple {@code messageParams}
     * @param error The {@code error} object
     * @param code The {@code ValidationCode code} used to build this {@code SystemException}
     * @param customMessage A custom error message defined by the invoker of this method.
     * @return systemException
     *
     */
    public static SystemException buildSystemException(ValidationError error,
                                                       ValidationCode code,
                                                       String customMessage){
        if (code == null) {
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(customMessage, code);
        systemException.set(ValidationConstants.KEY_VALIDATION_RESULT, error);
        error.setCode(systemException.getErrorCode());
        return systemException;
    }

    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_SECURITY_ERROR} of type {@code AccessError}
     * @param code The {@code ErrorCode} used to build this {@code SystemException}
     * @param oauthErrorCode The kind of OAuth error code. See http://tools.ietf.org/html/rfc6750#section-3.1 for possible values
     * @return systemException
     */
    public static SystemException buildAccessSystemException(ErrorCode code, String oauthErrorCode, String wwwAuthenticate){
        // Not using JSR-303 validation as this is an internal utility method, not a business method. Therefore,
        // errors in parameters should be uncommon and not propagated to SystemExceptionMapper class
        if(code == null){
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(code);
        AccessError accessError = new AccessError(systemException.getMessage(), code);
        accessError.setOauthErrorCode(oauthErrorCode);
        systemException.set(ValidationConstants.KEY_SECURITY_ERROR, accessError);
        systemException.set(ValidationConstants.KEY_WWW_AUTHENTICATE, wwwAuthenticate);
        return systemException;
    }

    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_SECURITY_ERROR} of type {@code AccessError}
     * @param code The {@code ErrorCode} used to build this {@code SystemException}
     * @return systemException A newly build instance of {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_SECURITY_ERROR} of type {@code AccessError}
     * property
     */    
    public static SystemException buildAccessSystemException(ErrorCode code){
        // Not using JSR-303 validation as this is an internal utility method, not a business method. Therefore,
        // errors in parameters should be uncommon and not propagated to SystemExceptionMapper class
        if(code == null){
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(code);
        AccessError accessError = new AccessError(systemException.getMessage(), code);
        systemException.set(ValidationConstants.KEY_SECURITY_ERROR, accessError);
        return systemException;
    }

    public static SystemException buildAccessSystemException(Throwable cause, ErrorCode code, String user){
        if(code == null){
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(cause, code);
        AccessError accessError = new AccessError(systemException.getMessage(), code, user);
        systemException.set(ValidationConstants.KEY_SECURITY_ERROR, accessError);
        return systemException;
    }

    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_FILE_UPLOAD_ERROR} of type {@code FileUploadError}
     * @param code The {@code ErrorCode} used to build this {@code SystemException}
     * @param filename The {@code filename} that generates this exception
     * @return systemException A newly build instance of {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_FILE_UPLOAD_ERROR} of type {@code FileUploadError}
     * property
     */
    public static SystemException buildFileUploadSystemException(FileUploadCode code, String filename){
        return buildFileUploadSystemException(code, filename, filename);
    }

    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_FILE_UPLOAD_ERROR} of type {@code FileUploadError}
     * @param code The {@code ErrorCode} used to build this {@code SystemException}
     * @param filename The {@code filename} that generates this exception
     * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. See {@link String#format(String, Object...)}
     * @return systemException A newly build instance of {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_FILE_UPLOAD_ERROR} of type {@code FileUploadError}
     * property
     */
    public static SystemException buildFileUploadSystemException(FileUploadCode code, String filename, Object... args){
        if(code == null){
            throw new IllegalArgumentException("Error code must not be null");
        }
        SystemException systemException = new SystemException(code, args);
        FileUploadError error = new FileUploadError(systemException.getMessage(), code);
        error.setFilename(filename);

        systemException.set(ValidationConstants.KEY_FILE_UPLOAD_ERROR, error);
        return systemException;
    }

    /**
     * Builds up a {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_FILE_UPLOAD_ERROR} of type {@code FileUploadError}
     * @param code The {@code ErrorCode} used to build this {@code SystemException}
     * @param filename The {@code filename} that generates this exception
     * @param cause The {@code cause} that generates this exception
     * @return systemException A newly build instance of {@code SystemException} that contains a property of type
     * {@link trueffect.truconnect.api.commons.validation.ValidationConstants#KEY_FILE_UPLOAD_ERROR} of type {@code FileUploadError}
     * property
     */
    public static SystemException buildFileUploadSystemException(FileUploadCode code, String filename, Throwable cause){
        if(code == null){
            throw new IllegalArgumentException("Error code must not be null");
        }
        if(cause == null){
            throw new IllegalArgumentException("Throwable cause must not be null");
        }
        SystemException systemException = new SystemException(cause, code);
        FileUploadError error = new FileUploadError(cause.getMessage(), code);
        error.setFilename(filename);
        systemException.set(ValidationConstants.KEY_FILE_UPLOAD_ERROR, error);
        return systemException;
    }


    /**
     * Parses a SystemException to a corresponding Exception of these possible types:
     * <ul>
     *     <li>{@link trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException}:
     *     When the {@code SystemException} is any type of {@code SecurityCode}</li>
     *     <li>{@link trueffect.truconnect.api.commons.exceptions.ValidationException}
     *     When the {@code SystemException} is any type of {@code ValidationCode}</li>
     *     <li>{@link trueffect.truconnect.api.commons.exceptions.ConflictException}
     *     When the {@code SystemException} is of type {@code BusinessCode.DUPLICATE}</li>
     *     <li>{@link trueffect.truconnect.api.commons.exceptions.APIException}
     *     in any other case</li>
     * </ul>
     * <p>
     * The Exception message is obtained by {@code se.toSTring()}
     * @param se The {@code SystemException} to parse
     * @return an Exception built as ber above rules.
     * @throws IllegalArgumentException when the provided {@code SystemException} is null
     */
    public static Exception parseToLegacyException(SystemException se) {
        if(se == null){
            throw new IllegalArgumentException("SystemException to parse cannot be null");
        }
        Exception e;
        ErrorCode errorCode = se.getErrorCode();
        String message = se.getMessage();
        if(errorCode instanceof SecurityCode) {
            if(errorCode.getNumber() == SecurityCode.INVALID_TOKEN.getNumber()
                    || errorCode.getNumber() == SecurityCode.INVALID_REQUEST.getNumber()) {
                String error = se.get(ValidationConstants.KEY_WWW_AUTHENTICATE) != null ?
                        (String)se.get(ValidationConstants.KEY_WWW_AUTHENTICATE) : null;
                e = new AccessDeniedException(error);
            } else {
                e = new DataNotFoundForUserException(message);
            }
        } else if(errorCode instanceof ValidationCode) {
            e = new ValidationException(message);
        } else if(errorCode instanceof BusinessCode) {
            BusinessCode bc = (BusinessCode) errorCode;
            if(bc == BusinessCode.DUPLICATE) {
                e = new ConflictException(message);
            } else {
                e = new APIException(message);
            }
        } else {
            e = new APIException(message);
        }
        return e;
    }

}

package trueffect.truconnect.api.commons.validation;

import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Rambert Rioja
 */
public class ApiValidationUtils {

    public static final String MSG_ERROR_NOT_NULL = "Errors object must not be null.";
    public static final String MSG_ERROR_NOT_NULL_FIELD = "%s must not be null.";
    public static String TYPE_DUPLICATE = "Duplicate";
    public static String TYPE_INVALID = "Invalid";
    public static String TYPE_LENGTH = "Length";
    public static String TYPE_INTERNAL_ERROR = "InternalError";
    public static String TYPE_REQUIRED = "Required";
    public static String TYPE_ACCESS_DENIED = "AccessDenied";
    public static String TYPE_NOT_FOUND = "NotFound";
    public static String TYPE_NOT_ACCEPTABLE = "NotAcceptable";
    public static String TYPE_IMPORT_WARNING = "import.warning";
    public static String SERVICE_UNAVAILABLE = "ServiceUnavailable";
    public static String EXTERNAL_SERVICE_ERROR = "ExternalServiceError";
    private static final Pattern EXTRACT_PATTERN = Pattern.compile("\\[(.*?)\\]");

    /**
     * Reject the given field with the given error code if the value is empty or
     * just contains whitespace.
     * <p>
     * An 'empty' value in this context means either {@code null}, the empty
     * string "", or consisting wholly of whitespace.
     * <p>
     * The object whose field is being validated does not need to be passed in
     * because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     *
     * @param errors the {@code Errors} instance to register errors on
     * @param field the field name to check
     * @param errorCode the error code, interpretable as message key
     */
    public static void rejectIfBlank(Errors errors, String field, String errorCode) {
        rejectIfBlank(errors, field, errorCode, ResourceBundleUtil.getString("global.error.empty", field));
    }

    public static void rejectIfBlank(Errors errors, String field) {
        rejectIfBlank(errors, field, TYPE_REQUIRED);
    }

    /**
     * Reject the given field with the given error code, error arguments and
     * default message if the value is empty or just contains whitespace.
     * <p>
     * An 'empty' value in this context means either {@code null}, the empty
     * string "", or consisting wholly of whitespace.
     * <p>
     * The object whose field is being validated does not need to be passed in
     * because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     *
     * @param errors the {@code Errors} instance to register errors on
     * @param field the field name to check
     * @param errorCode the error code, interpretable as message key
     * @param customMessage fallback default message
     */
    public static void rejectIfBlank(Errors errors, String field, String errorCode, String customMessage) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        rejectIfBlank(errors, field, errorCode, customMessage, value);
    }

    public static void rejectIfBlank(Errors errors, String field, String errorCode, String customMessage, Object value) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        String val = value != null ? String.valueOf(value) : null;
        if (StringUtils.isBlank(val)) {
            errors.rejectValue(field, errorCode,customMessage);
        }
    }

    public static void rejectIfBlankOrCharactersUpTo(
            Errors errors, String field, int size) {
        Object value = errors.getFieldValue(field);
        rejectIfBlank(errors, field);
        String val = value != null ? String.valueOf(value) : null;
        if (StringUtils.isNotBlank(val) && val.length() > size) {
            String message = ResourceBundleUtil.getString("global.error.invalidStringLength", field, String.valueOf(size));
            errors.rejectValue(field, TYPE_LENGTH, message);
        }
    }
    public static void rejectIfBlankOrCharactersUpTo(
            Errors errors, String field, int size, Object value) {
        rejectIfBlank(errors, field, TYPE_REQUIRED, ResourceBundleUtil.getString("global.error.empty", field), value);
        String val = value != null ? String.valueOf(value) : null;
        if (StringUtils.isNotBlank(val) && val.length() > size) {
            String message = ResourceBundleUtil.getString("global.error.invalidStringLength", field, String.valueOf(size));
            errors.rejectValue(field, TYPE_LENGTH, message);
        }
    }

    public static void rejectIfCharactersUpTo(
            Errors errors, String field, int size) {
        Object value = errors.getFieldValue(field);
        rejectIfCharactersUpTo(errors, field, size, value);
    }

    public static void rejectIfCharactersUpTo(
            Errors errors, String field, int size, Object value) {
        String message = ResourceBundleUtil.getString("global.error.invalidStringLength", field,
                String.valueOf(size));
        rejectIfCharactersUpTo(errors, field, size, value, message);
    }

    public static void rejectIfCharactersUpTo(
            Errors errors, String field, int size, Object value, String customMessage) {
        if (value != null && value.toString().length() > size) {
            if(customMessage == null) {
                customMessage = ResourceBundleUtil.getString("global.error.invalidStringLength", field, String.valueOf(size));
            }
            errors.rejectValue(field, TYPE_LENGTH, customMessage);
        }
    }

    public static void rejectIfNegativeNumber(Errors errors, String field) {
        Assert.notNull(errors, "Errors object must not be null.");
        Number number = parseNumber(errors, field);
        if (number != null) {
            if ((number instanceof Long && ((Long)number).compareTo(0L) < 0)
                    || (number instanceof Double && ((Double)number).compareTo(0.0) < 0)) {
                String message = "Invalid " + field + " [" + number + "], it must be a positive number.";
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }

    /**
     * Rejects a number {@code number} if it is greater than to a given Number {@code superiorLimit}
     * @param errors The errors object where to put the validation message
     * @param field The field to associate the rejection message
     * @param number The number under test
     * @param superiorLimit The superior limit that {@code number} should not exceed
     * @param clazz This determines how the given value of {@code field} is treated. Supported values are:
     *  {@code java.lang.Double}, {@code java.lang.Long}, and {@code java.lang.Integer}
     * @throws IllegalArgumentException in case the {@code superiorLimit} is not the same class as {@code clazz} or
     * if the given {@code clazz} is not any of the supported types.
     */
    public static void rejectIfNumberGreaterThan(Errors errors, String field, Number number, Number superiorLimit, Class clazz, String customMessage) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Assert.notNull(clazz, String.format(MSG_ERROR_NOT_NULL_FIELD, "clazz"));
        Assert.notNull(superiorLimit, String.format(MSG_ERROR_NOT_NULL_FIELD, "inferiorLimit"));
        if(!clazz.isInstance(superiorLimit)) {
            throw new IllegalArgumentException("Superior Limit should be of type " + clazz);
        }
        if (number != null && clazz.isInstance(number) ) {
            if(customMessage == null) {
                customMessage = ResourceBundleUtil.getString("global.error.importInvalidNumber",
                        field, "0", String.valueOf(superiorLimit));
            }
            if(number instanceof Integer) {
                if (((Integer)number).compareTo((Integer)superiorLimit) > 0) {
                    errors.rejectValue(field, TYPE_INVALID, customMessage);
                }
            } else if(number instanceof Long) {
                if (((Long)number).compareTo((Long)superiorLimit) > 0) {
                    errors.rejectValue(field, TYPE_INVALID, customMessage);
                }
            } else if(number instanceof Double) {
                if (((Double)number).compareTo((Double)superiorLimit) > 0) {
                    errors.rejectValue(field, TYPE_INVALID, customMessage);
                }
            } else {
                throw new UnsupportedOperationException("The class of 'value' under test is not supported. Integer, Long, and Double are supported");
            }
        }
    }

    public static void rejectIfLessThanNumber(Errors errors, String field, Number n) {
        Assert.notNull(errors, "Errors object must not be null.");
        Number number = parseNumber(errors, field);
        if (number != null) {
            if ((number instanceof Long && ((Long)number).compareTo((Long)n) < 0)
                    || (number instanceof Double && ((Double)number).compareTo((Double)n) < 0)) {
                String message = "Invalid " + field + ", it must be greater than or equals to " + n + ".";
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }

    /**
     * Compound validator for numbers with double precision. It validates that a value:
     * <ol>
     *     <li>is a Double</li>
     *     <li>is a Double greater than or equals to {@code inferiorLimit}</li>
     * </ol>
     * @param errors The errors where to fill in the validation messages
     * @param field the field where to get the value from
     */
    public static void rejectIfNotDoubleOrLessThan(Errors errors, String field, Double inferiorLimit) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Double number = parseDouble(errors, field);
        if (number != null) {
            if (number < inferiorLimit) {
                String message = ResourceBundleUtil.getString("global.error.greaterThanOrEquals", field, String.valueOf(inferiorLimit));
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        } else { // Not a Double, rejecting with different error message
            String message = ResourceBundleUtil.getString("global.error.format", field);
            errors.rejectValue(field, TYPE_INVALID, message);
        }
    }
    /**
     * Tries to parse a String value as a Date. In case it can't, it adds an error in {@code Errors}
     * @param errors The errors where to fill in the validation messages
     * @param field the field where to get the value from
     * @param customMessage The custom message to use
     */
    public static Date parseDate(Errors errors, String field, String customMessage) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value == null) {
            return null;
        }
        Date result;
        try {
            return DateConverter.parseXlsStringToDate((String) value);
        } catch (ParseException e) {
            // could not parse the date
            if(customMessage == null) {
                customMessage = ResourceBundleUtil.getString("global.error.dateFormat", field);
            }
            errors.rejectValue(field, TYPE_INVALID, customMessage);
            return null;
        }
    }

    /**
     * Rejects the Number associated to {@code field} if it is greater than {@code n}
     * @param errors The object to add errors
     * @param field The field where to extract the number to compare
     * @param n The number to compare against to
     */
    public static void rejectIfNumberGreaterThan(Errors errors, String field, Number n) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Number number = parseNumber(errors, field);
        if (number != null) {
            if ((number instanceof Long && ((Long)number).compareTo((Long)n) > 0)
                    || (number instanceof Double && ((Double)number).compareTo((Double)n) > 0)) {
                // TODO change
                String message = "Invalid " + field + ", it must be lower than or equals to " + n + ".";
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }

    /**
     * Rejects the Date object associated to {@code field} if it is after {@code date}
     * Note. To reject the values, the Dates to be compared should be non-null. Otherwise,
     * the field is accepted
     * @param errors The object to add errors
     * @param field The field where to extract the Date to compare
     * @param date The Date to compare against to
     */
    public static void rejectIfAfter(Errors errors, String field, Date date) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            Date otherDate = (Date) value;
            if (date.compareTo(otherDate) > 0) {
                String message = String.format("%1$tF %1$tT is after %2$tF %2$tT", date, otherDate);
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }
    /**
     * Rejects a Date object {@code startDate} if it is after {@code endDate}
     * @param errors The object to add errors
     * @param field The field associated to {@code startDate}
     * @param startDate The Start Date to compare against to
     * @param endDate The End Date to compare against to
     */
    public static void rejectIfAfter(Errors errors, String field, Date startDate, Date endDate) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        if (startDate != null && endDate != null) {
            if (startDate.compareTo(endDate) > 0) {
                String message = ResourceBundleUtil.getString("global.error.startAfterEndDate",
                        endDate,
                        startDate);
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }

    /**
     * Rejects the String object associated to {@code field} if it is after {@code date}
     * Note. To reject the values, the Dates to be compared should be non-null. Otherwise,
     * the field is accepted
     * @param errors The object to add errors
     * @param field The field where to extract the Date to compare
     * @param date The Date to compare against to
     */
    public static void rejectIfAfter(Errors errors, String field, String date, String message) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            String startDateString = (String) value;
            // We assume valid two empty strings being compared
            // This looks wrong but when using this validation method, method 'rejectIfInvalidDateFormat'
            // should be run for Start and End date to avoid having this inconsistent result
            // TODO: Make this method to validate format of Dates at the same time of validating if one
            // is after the other
            if("".equalsIgnoreCase(startDateString) || "".equalsIgnoreCase(date)) {
                return;
            }
            try {
                Date startDate = DateConverter.parseForImport(startDateString);
                Date endDate = DateConverter.parseForImport(date);
                if (startDate.compareTo(endDate) > 0) {
                    if(message == null) {
                        message = String.format("%1$tF %1$tT is after %2$tF %2$tT", date, startDateString);
                    }
                    errors.rejectValue(field, TYPE_INVALID, message);
                }
            } catch (ParseException e) {
                errors.rejectValue(field, TYPE_INVALID, "One of the provided dates fields is not a valid date");
            }
        }
    }

    /**
     * Rejects the String value associated to {@code field} if it has a wrong Date format
     * <p>
     * {@code dateFormat} defines the format to expect
     *
     * @param errors The object to add errors
     * @param field The field where to extract the String value to validate
     * @param message A custom error message
     */
    public static void rejectIfInvalidDateFormat(Errors errors, String field, String message) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null && !"".equalsIgnoreCase((String)value)) {
            try {
                DateConverter.parseForImport((String) value);
            } catch (ParseException e) {
                if(message == null) {
                    message = String.format("Invalid %s. It has an unsupported format", field);
                }
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }

    public static Number parseNumberAs(Errors errors, String field, Class clazz, String customMessage) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            try {
                if (clazz == Integer.class) {
                    return Integer.parseInt(value.toString());
                } else if (clazz == Long.class) {
                    return Long.parseLong(value.toString());
                } else if (clazz == Double.class) {
                    return Double.parseDouble(value.toString());
                } else {
                    throw new IllegalArgumentException(String.format(
                            "Unsupported class type: %s while parsing %s. Supported types are Integer, Long, and Double", clazz, value));
                }
            } catch (NumberFormatException e) {
                if(customMessage == null) {
                    customMessage = "Invalid number " + field + " [" + value + "].";
                }
                errors.rejectValue(field, TYPE_INVALID, customMessage);
                return null;
            }
        }
        return null;
    }



    public static Number parseNumber(Errors errors, String field) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                try {
                    return Double.parseDouble(value.toString());
                } catch (NumberFormatException ex) {
                    String message = "Invalid number " + field + " [" + value + "].";
                    errors.rejectValue(field, TYPE_INVALID, message);
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Rejects the String value associated to {@code field} if it is not a valid positive Long number
     * @param errors The object to add errors
     * @param field The field where to extract the String value to validate
     * @param message A custom error message
     */
    public static void rejectIfNotPositiveLong(Errors errors, String field, String message) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            if(!isPositiveLong(value.toString())) {
                if(message == null) {
                    message = "Invalid number " + field + " [" + value + "].";
                }
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }

    /**
     * Rejects the String value associated to {@code field} if it is not a valid positive Integer number
     * @param errors The object to add errors
     * @param field The field where to extract the String value to validate
     * @param message A custom error message
     */
    public static void rejectIfNotPositiveInt(Errors errors, String field, String message) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            if(!isPositiveInt((value.toString()))) {
                if(message == null) {
                    message = "Invalid number " + field + " [" + value + "].";
                }
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }

    /**
     * Validates is the String passed by is a
     * Positive number
     * @param value The String to evaluate
     * @return true if the passed parameter is a positive long. false otherwise.
     */
    public static boolean isPositiveLong(String value) {
        try {
            long l = Long.parseLong(value.trim());
            return l >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates is the String passed by is a
     * Positive Integer number
     * @param value The String to evaluate
     * @return true if the passed parameter is a positive integer. false otherwise.
     */
    public static boolean isPositiveInt(String value) {
        try {
            int number = Integer.parseInt(value.trim());
            return number >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Rejects the String value associated to {@code field} if it is not a valid Long number
     * @param errors The object to add errors
     * @param field The field where to extract the String value to validate
     * @param message A custom error message
     */
    public static void rejectIfNotLong(Errors errors, String field, String message) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            try {
                Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                if(message == null) {
                    message = "Invalid number " + field + " [" + value + "].";
                }
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }


    /**
     * Rejects the String value associated to {@code field} if it is not match a give {@code Pattern}
     * @param errors The object to add errors
     * @param field The field where to extract the String value to validate
     * @param message A custom error message
     */
    public static void rejectIfDoesNotMatchPattern(Errors errors, String field, String message, Pattern pattern) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Assert.notNull(pattern, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            String url = (String)value;
            Matcher matcher = pattern.matcher(url.trim());
            if(!matcher.matches()) {
                if(message == null) {
                    message = "Invalid number " + field + " [" + value + "].";
                }
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }

    /**
     * Rejects the String value associated to {@code field} if no region of it can find a match vigen
     * the provided {@code Pattern}
     * @param errors The object to add errors
     * @param field The field where to extract the String value to validate
     * @param message A custom error message
     * @param pattern The pattern to use to find at least a match
     */
    public static void rejectIfCannotFindUsingPattern(Errors errors, String field, String message,
                                                      Pattern pattern) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Assert.notNull(pattern, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            String valueString = (String)value;
            Matcher matcher = pattern.matcher(valueString);
            if (!matcher.find()) {
                if(message == null) {
                    message = "Invalid value " + field + " [" + value + "].";
                }
                errors.rejectValue(field, TYPE_NOT_ACCEPTABLE, message);
            }
        }
    }

    public static void rejectIfNull(Errors errors, String field) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value == null) {
            String message = ResourceBundleUtil.getString("global.error.empty", field);
            errors.rejectValue(field, TYPE_REQUIRED, message);
        }
    }

    /**
     * Rejects
     * @param errors
     * @param field
     */
    public static void rejectAsNull(Errors errors, String field) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        String message = ResourceBundleUtil.getString("global.error.empty", field);
        errors.rejectValue(field, TYPE_REQUIRED, message);
    }

    public static void rejectIfNotNull(Errors errors, String field) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (!StringUtils.isBlank((String) value)) {
            String message = ResourceBundleUtil.getString("creativeInsertion.error.clickthroughsNotAllowed");
            errors.rejectValue(field, TYPE_INVALID, message);
        }
    }

    /**
     * Rejects the String value associated to {@code field} if it is not a valid Long number
     * @param errors The object to add errors
     * @param field The field where to extract the String value to validate
     * @param customMessage A custom error message
     */
    public static void rejectIfOutOfRangeOrNotNumber(Errors errors, String field, Long min, Long max, String customMessage) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
            if (value != null) {
            try {
                long number = Long.parseLong(value.toString());
                if (number < min || number > max) {
                    if(customMessage == null) {
                        customMessage  = ResourceBundleUtil.getString("global.error.outOfRange",
                                field,
                                String.valueOf(min),
                                String.valueOf(max));
                    }
                    errors.rejectValue(field, TYPE_INVALID, customMessage);
                }
            } catch (NumberFormatException e) {
                customMessage = ResourceBundleUtil.getString("global.error.invalidNumber", field, value);
                errors.rejectValue(field, TYPE_INVALID, customMessage);
            }
        }
    }

    public static void rejectIfOutOfRangeOrNotNumber(Errors errors, String field, Double min, Double max) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Double number = parseDouble(errors, field);
        if (number != null) {
            if (number < min || number > max) {
                String message  = ResourceBundleUtil.getString("global.error.outOfRange", field,
                        String.valueOf(min),
                        String.valueOf(max));
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }

    public static void rejectIfDoesNotContain(Errors errors, String field, List lis) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        rejectIfBlank(errors, field);
        Object value = errors.getFieldValue(field);
        String val = value != null ? String.valueOf(value) : null;
        if (StringUtils.isNotBlank(val) && !lis.contains(val)) {
            String message = "Invalid " + field + ", it should be one of " + lis + ".";
            errors.rejectValue(field, TYPE_INVALID, message);
        }
    }

    /**
     * Rejects the value associated to {@code field} if it is not an entry of Enum {@code enumType}.
     * @param errors The errors where to put the validation messages
     * @param field The field where to extract the value from
     * @param enumType The type of Enum to get the values to compare to
     * @param exactMatch If true, then the value under validation needs to match the Enum entry exactly. Otherwise, the
     *                   value under validation is capitalized prior comparing it against the Enum entries. Thus, in the
     *                   second case (false) values in mixed, lower, or upper case may end up being valid. Examples:
     *                   <pre>
     *                     enum Bugs{ ANT, BEE} ,<br>
     *                       exactMatch = true:<br>
     *                         values under test:<br>
     *                           "ant" => {@code field} error.<br>
     *                           "ANt" => {@code field} error.<br>
     *                           "ANT" => {@code field} success.<br>
     *                       exactMatch = false:<br>
     *                         values under test:<br>
     *                           "ant" => {@code field} success.<br>
     *                           "ANt" => {@code field} success.<br>
     *                           "ANT" => {@code field} success.<br>
     *                   </pre>
     * @param customMessage A custom message in case the value is rejected
     */
    public static void rejectIfNotInEnum(Errors errors,
                                         final String field,
                                         final Class<? extends Enum> enumType,
                                         boolean exactMatch,
                                         String customMessage) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        String val = value != null ? String.valueOf(value).trim() : null;
        if(StringUtils.isBlank(val)) {
            return;
        }
        try {
            Enum<?> e = Enum.valueOf(enumType, exactMatch ? val : val.toUpperCase());
        } catch (Exception e) {
            Enum[] enumConstants = enumType.getEnumConstants();
            String[] stringEnumConstants = Arrays.toString(enumConstants).replaceAll("^.|.$", "").split(", ");
            if(customMessage == null) {
                customMessage = ResourceBundleUtil.getString("global.error.invalidIfDoesNotContain",
                        field, String.format("[%s]", StringUtils.join(stringEnumConstants, ", ")));
            }
            errors.rejectValue(field, TYPE_INVALID, customMessage);
        }
    }


    public static void rejectIfEmpty(Errors errors, String field) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if(value instanceof Collection) {
            Collection c = (Collection) value;
            if(c.isEmpty()) {
                String message = "Invalid " + field + ", collection should not be empty.";
                errors.rejectValue(field, TYPE_INVALID, message);
            }
        }
    }


    public static Long parseLong(Errors errors, String field) {
        Assert.notNull(errors, "Errors object must not be null.");
        Object value = errors.getFieldValue(field);
        if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                String message = "Invalid number " + field + " [" + value + "].";
                errors.rejectValue(field, TYPE_INVALID, message);
                return null;
            }
        } else {
            rejectIfNull(errors, field);
            return null;
        }
    }

    public static Double parseDouble(Errors errors, String field) {
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                String message = "Invalid number " + field + " [" + value + "].";
                errors.rejectValue(field, TYPE_INVALID, message);
                return null;
            }
        }
        return null;
    }



    public static void rejectIfIdDoesntMatch(Errors errors, String field, Long id) {
        Assert.notNull(errors, "Errors object must not be null.");
        Long objId = parseLong(errors, field);
        if (objId != null && id != null && !id.equals(objId)) {
            String message = "Entity in request body does not match the requested id.";
            errors.rejectValue(field, TYPE_INVALID, message);
        }
    }

    public static void rejectIfPostWithId(Errors errors, String field, String objectName) {
        Assert.notNull(errors, "Errors object must not be null.");
        Long objId = (Long)errors.getFieldValue(field);
        if (objId != null) {
            String message = ResourceBundleUtil.getString("global.error.postWithId", objectName);
            errors.rejectValue(field, TYPE_INVALID, message);
        }
    }

    public static void rejectIfPutWithoutId(Errors errors, String field, String objectName) {
        Assert.notNull(errors, "Errors object must not be null.");
        Long objId = (Long)errors.getFieldValue(field);
        if (objId == null) {
            String message = objectName + " should have the id field. If you are trying to create a new " + objectName + " use POST service.";
            errors.rejectValue(field, TYPE_INVALID, message);
        }
    }
    
    public static void rejectIfInvalidCharacters(Errors errors, String field, String pattern){
        Assert.notNull(errors, MSG_ERROR_NOT_NULL);
        Object value = errors.getFieldValue(field);
        if (value != null && !value.toString().matches(pattern)){
            String message = "Invalid " + field + ", it contains illegal characters.";
            errors.rejectValue(field, TYPE_INVALID, message);
        }
    }

    public static String asCSVString(BeanPropertyBindingResult validationResult) {
        if(validationResult == null || !validationResult.hasErrors()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        List<FieldError> errors = validationResult.getFieldErrors();
        String separator = "";
        int pos = 0;
        if(errors.size() > 1){
            pos = 1;
            separator = ", ";
            FieldError first = errors.get(0);
            result.append(first.getDefaultMessage());
        }
        for(int i = pos; i < errors.size(); i++) {
            result.append(separator).append(errors.get(i).getDefaultMessage());
        }
        return result.toString();
    }

    public static List<ValidationError> parseToValidationError(BeanPropertyBindingResult validationResult) {
        if(validationResult == null || !validationResult.hasErrors()) {
            return null;
        }
        List<ValidationError> validationErrors = new ArrayList<>();
        List<FieldError> errors = validationResult.getFieldErrors();

        ValidationCode errorCode;
        for (FieldError fieldError : errors) {
            String defaultMessage = fieldError.getDefaultMessage();
            String rejectedValue = String.valueOf(fieldError.getRejectedValue());
            if (fieldError.getCode().equals(ApiValidationUtils.TYPE_DUPLICATE)){
                errorCode = ValidationCode.DUPLICATE;
                Matcher matcher = EXTRACT_PATTERN.matcher(defaultMessage);
                if (matcher.find())
                {
                    rejectedValue = matcher.group(1);
                }
            }
            else {
                errorCode = ValidationCode.INVALID;
            }

            ValidationError validationError = new ValidationError(defaultMessage,
                    errorCode,
                    fieldError.getField(),
                    fieldError.getObjectName(),
                    rejectedValue,
                    null,
                    null,
                    null,
                    null,
                    null
                    );
            validationErrors.add(validationError);
        }

        return validationErrors;
    }
    /**
     * Parses a {@link trueffect.truconnect.api.commons.exceptions.business.Errors} (most recent Error structure) instance into a
     * {@link trueffect.truconnect.api.commons.model.Errors} )(Legacy error structure)
     * @param newErrors The error input to parse (latest structure)
     * @return The parsed Error structure as a legacy model structure
     */
    public static trueffect.truconnect.api.commons.model.Errors parseToLegacyErrors(
            trueffect.truconnect.api.commons.exceptions.business.Errors newErrors) {
        if(newErrors == null){
            return null;
        }

        trueffect.truconnect.api.commons.model.Errors errors = new trueffect.truconnect.api.commons.model.Errors();
        trueffect.truconnect.api.commons.model.Error error = null;
        List<trueffect.truconnect.api.commons.model.Error> errorList = new ArrayList<>();
        for(Error nerror : newErrors.getErrors()) {
            ErrorCode errorCode = nerror.getCode();
            if (errorCode instanceof SecurityCode) {
                SecurityCode code = (SecurityCode) errorCode;
                String type;
                if(code == SecurityCode.NOT_FOUND_FOR_USER) {
                    type = ApiValidationUtils.TYPE_NOT_FOUND;
                } else {
                    type = ApiValidationUtils.TYPE_INVALID;
                }
                error = new trueffect.truconnect.api.commons.model.Error(
                        nerror.getMessage(), type);
            } else if (errorCode instanceof ValidationCode) {
                error = new trueffect.truconnect.api.commons.model.Error(
                        nerror.getMessage(), ApiValidationUtils.TYPE_INVALID);
            } else if (errorCode instanceof BusinessCode) {
                BusinessCode code = (BusinessCode) errorCode;
                String type;
                if(code == BusinessCode.NOT_FOUND) {
                    type = ApiValidationUtils.TYPE_NOT_FOUND;
                } else {
                    type = ApiValidationUtils.TYPE_INVALID;
                }
                error = new trueffect.truconnect.api.commons.model.Error(
                        nerror.getMessage(), type);
            } else {
                error = new trueffect.truconnect.api.commons.model.Error(
                        nerror.getMessage(), ApiValidationUtils.TYPE_INVALID);
            }
            errorList.add(error);
        }
        errors.setErrors(errorList);
        return errors;
    }
}

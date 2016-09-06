package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.htmlinjection.AdChoicesHtmlInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.CustomInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.FacebookCustomTrackingInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.HtmlInjectionType;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates operations with Html Injection
 * Created by Saulo Lopez
 * @author Saulo Lopez
 */
public class HtmlInjectionTagValidator implements Validator {

    @SuppressWarnings("rawtypes")
    @Override
    public boolean supports(Class type) {
        return type == HtmlInjectionTags.class;
    }

    @Override
    public void validate(Object o, org.springframework.validation.Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "HtmlInjectionTag");
        validateCommons(o, errors);
    }

    private void validateCommons(Object o, org.springframework.validation.Errors errors) {
        rejectIfInvalidName(errors);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "htmlContent", Constants.HTML_INJECTION_HTML_CONTENT_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "secureHtmlContent", Constants.HTML_INJECTION_HTML_CONTENT_LENGTH);
        rejectIfNoHtmlContent(errors);
        Pattern pattern = Pattern.compile(ValidationConstants.REGEXP_HTML_INJECTION_CONTENT, Pattern.CASE_INSENSITIVE);
        Object content = errors.getFieldValue("htmlContent");
        if (StringUtils.isNotBlank((String) content)) {
            ApiValidationUtils.rejectIfCannotFindUsingPattern(errors, "htmlContent",
                    ResourceBundleUtil.getString("htmlInjectionTag.error.invalidTagContent"),
                    pattern);
        }
        Object secureHtmlContent = errors.getFieldValue("secureHtmlContent");
        if (StringUtils.isNotBlank((String) secureHtmlContent)) {
            ApiValidationUtils.rejectIfCannotFindUsingPattern(errors, "secureHtmlContent",
                    ResourceBundleUtil.getString("htmlInjectionTag.error.invalidTagContent"),
                    pattern);
        }
        List<String> allowedValues = Arrays.asList(Long.toString(Constants.ENABLED), Long.toString(Constants.DISABLED));
        ApiValidationUtils.rejectIfDoesNotContain(errors, "isEnabled", allowedValues);
        ApiValidationUtils.rejectIfDoesNotContain(errors, "isVisible", allowedValues);
    }

    private void rejectIfNoHtmlContent(org.springframework.validation.Errors errors) {
        Object content = errors.getFieldValue("htmlContent");
        Object secureContent = errors.getFieldValue("secureHtmlContent");
        if (StringUtils.isBlank((String) content) && StringUtils.isBlank((String) secureContent)) {
            String whichOne = content == null ? "htmlContent" : "secureHtmlContent";
            errors.rejectValue(whichOne, ApiValidationUtils.TYPE_REQUIRED, ResourceBundleUtil.getString("htmlInjectionTag.error.emptyTagContent"));
        }
    }

    private void rejectIfInvalidName(org.springframework.validation.Errors errors) {
        Object objectValue = errors.getFieldValue("name");
        Error errorInName = validateName((String) objectValue);

        if (errorInName != null) {
            errors.rejectValue("name", ApiValidationUtils.TYPE_INVALID, errorInName.getMessage());
        }
    }

    public static Errors validateHtmlInjectionTag(HtmlInjectionType htmlInjectionType) {

        Errors result = new Errors();

        if (htmlInjectionType == null || htmlInjectionType.getType() == null) {
            ValidationError error = new ValidationError();
            error.setMessage(ResourceBundleUtil.getString("BusinessCode.INVALID"));
            error.setCode(ValidationCode.INVALID);
            result.addError(error);
            return result;
        }

        switch (htmlInjectionType.getType()) {
            case AD_CHOICES:
                result = validateAdChoicesTag((AdChoicesHtmlInjectionType) htmlInjectionType);
                break;
            case FACEBOOK_CUSTOM_TRACKING:
                result = validateFacebookTag(
                        (FacebookCustomTrackingInjectionType) htmlInjectionType);
                break;
            case CUSTOM_TAG:
                result = validateCustomTag((CustomInjectionType) htmlInjectionType);
                break;
            default:
                break;
        }

        return result;
    }

    private static Errors validateFacebookTag(
            FacebookCustomTrackingInjectionType facebookCustomTrackingInjectionType) {
        Errors result = new Errors();

        Error nameValidationError = validateName(facebookCustomTrackingInjectionType.getName());
        if (nameValidationError != null) {
            result.addError(nameValidationError);
        }

        Error firstPartyDomainIdValidationError = validateFacebookCustomTrackingFirstPartyDomainId(
                facebookCustomTrackingInjectionType.getFirstPartyDomainId());
        if (firstPartyDomainIdValidationError != null) {
            result.addError(firstPartyDomainIdValidationError);
        }
        return result;
    }

    private static Errors validateAdChoicesTag(
            AdChoicesHtmlInjectionType adChoicesHtmlInjectionType) {
        Errors result = new Errors();

        Error nameValidationError = validateName(adChoicesHtmlInjectionType.getName());
        if (nameValidationError != null) {
            result.addError(nameValidationError);
        }

        Error optOutUrlValidationError =
                validateAdChoicesOptOutUrl(adChoicesHtmlInjectionType.getOptOutUrl());
        if (optOutUrlValidationError != null) {
            result.addError(optOutUrlValidationError);
        }
        return result;
    }

    private static Errors validateCustomTag(CustomInjectionType customInjectionType) {
        Errors result = new Errors();

        String tagContentField = "tagContent";
        String secureTagContentField = "secureTagContent";

        Error nameValidationError = validateName(customInjectionType.getName());
        if (nameValidationError != null) {
            result.addError(nameValidationError);
        }

        Pattern pattern = Pattern.compile(ValidationConstants.REGEXP_HTML_INJECTION_CONTENT,
                Pattern.CASE_INSENSITIVE);
        if (StringUtils.isNotBlank(customInjectionType.getTagContent())) {
            Matcher matcher = pattern.matcher(customInjectionType.getTagContent());

            // Check length of the tag content
            if (customInjectionType.getTagContent()
                                   .length() > Constants.HTML_INJECTION_HTML_CONTENT_LENGTH) {
                ValidationError error = new ValidationError();
                error.setMessage(ResourceBundleUtil
                        .getString("global.error.invalidStringLength", tagContentField,
                                Constants.HTML_INJECTION_HTML_CONTENT_LENGTH));
                error.setCode(ValidationCode.INVALID);
                result.addError(error);
            } else if (!matcher.find()) {  // Check if tag content is well-formed
                ValidationError error = new ValidationError();
                error.setMessage(
                        ResourceBundleUtil.getString("htmlInjectionTag.error.invalidTagContent"));
                error.setCode(ValidationCode.INVALID);
                result.addError(error);
            }
        }

        if (StringUtils.isNotBlank(customInjectionType.getSecureTagContent())) {
            Matcher matcher = pattern.matcher(customInjectionType.getSecureTagContent());

            // Check length of the secure tag content
            if (customInjectionType.getSecureTagContent()
                                   .length() > Constants.HTML_INJECTION_HTML_CONTENT_LENGTH) {
                ValidationError error = new ValidationError();
                error.setMessage(ResourceBundleUtil
                        .getString("global.error.invalidStringLength", secureTagContentField,
                                Constants.HTML_INJECTION_HTML_CONTENT_LENGTH));
                error.setCode(ValidationCode.INVALID);
                result.addError(error);
            } else if (!matcher.find()) { // Check if secure tag content is well-formed
                ValidationError error = new ValidationError();
                error.setMessage(
                        ResourceBundleUtil.getString("htmlInjectionTag.error.invalidTagContent"));
                error.setCode(ValidationCode.INVALID);
                result.addError(error);
            }
        }

        // Check the presence of either tag content or secure tag content
        if (StringUtils.isBlank(customInjectionType.getTagContent()) && StringUtils.isBlank(
                customInjectionType.getSecureTagContent())) {
            ValidationError error = new ValidationError();
            error.setMessage(
                    ResourceBundleUtil.getString("htmlInjectionTag.error.emptyTagContent"));
            error.setCode(ValidationCode.REQUIRED);
            error.setField(customInjectionType
                    .getTagContent() == null ? tagContentField : secureTagContentField);
            result.addError(error);
        }

        return result;
    }

    private static Error validateFacebookCustomTrackingFirstPartyDomainId(Long firstPartyDomainId) {
        Error result = null;
        if (firstPartyDomainId == null) {
            ValidationError error = new ValidationError();
            error.setMessage(
                    ResourceBundleUtil.getString("global.error.null", "firstPartyDomainId"));
            error.setCode(ValidationCode.INVALID);
            return error;
        }
        return result;
    }

    private static Error validateAdChoicesOptOutUrl(String optOutUrl) {
        Error result = null;
        if (optOutUrl == null) {
            ValidationError error = new ValidationError();
            error.setMessage(ResourceBundleUtil.getString("global.error.null", "optOutUrl"));
            error.setCode(ValidationCode.INVALID);
            return error;
        }

        if (optOutUrl.length() > Constants.HTML_INJECTION_MAX_OPT_OUT_URL_LENGHT) {
            ValidationError error = new ValidationError();
            error.setMessage(ResourceBundleUtil
                    .getString("global.error.invalidStringLength", "optOutUrl",
                            Constants.HTML_INJECTION_MAX_OPT_OUT_URL_LENGHT));
            error.setCode(ValidationCode.INVALID);
            return error;
        }

        if (!optOutUrl.matches(ValidationConstants.REGEXP_HTML_INJECTION_AD_CHOICES_OPT_OUT_URL)) {
            ValidationError error = new ValidationError();
            error.setMessage(ResourceBundleUtil.getString("global.error.format", "optOutUrl"));
            error.setCode(ValidationCode.INVALID);
            return error;
        }

        return result;
    }

    private static Error validateName(String name) {
        Error result = null;
        if (name == null) {
            ValidationError error = new ValidationError();
            error.setMessage(ResourceBundleUtil
                    .getString(ResourceBundleUtil.getString("global.error.null", "name")));
            error.setCode(ValidationCode.INVALID);
            return error;
        }

        String updatedName = name.trim();
        updatedName = updatedName.replaceAll(ValidationConstants.HTML_INJECTION_SPACE_REMOVER, " ");

        if (updatedName.length() == 0) {
            ValidationError error = new ValidationError();
            error.setMessage(ResourceBundleUtil.getString("global.error.empty", "name"));
            error.setCode(ValidationCode.INVALID);
            return error;
        }

        if (updatedName.length() > Constants.HTML_INJECTION_MAX_NAME_LENGTH) {
            ValidationError error = new ValidationError();
            error.setMessage(ResourceBundleUtil
                    .getString("global.error.invalidStringLength", "name",
                            Constants.HTML_INJECTION_MAX_NAME_LENGTH));
            error.setCode(ValidationCode.INVALID);
            return error;
        }

        if (!updatedName.matches(ValidationConstants.REGEXP_ALPHANUMERIC_AND_SPACES)) {
            ValidationError error = new ValidationError();
            error.setMessage(ResourceBundleUtil.getString("global.error.format", "name"));
            error.setCode(ValidationCode.INVALID);
            return error;
        }

        return result;
    }
}
package trueffect.truconnect.api.crud.validation;

import com.trueffect.delivery.formats.adm.CookieDefault$;
import com.trueffect.delivery.formats.adm.KeyDefault$;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.dto.adm.Cookie;
import trueffect.truconnect.api.commons.model.dto.adm.CookieList;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.dto.adm.FailThroughDefaults;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Validates {@code DatasetConfigView} instances
 * Created by marcelo.heredia on 3/29/2016.
 * @author Marcelo Heredia
 */
public class DatasetConfigViewValidator implements Validator {

    public static final List<String> SUPPORTED_FAIL_THROUGH_TYPES = Arrays.asList(
            CookieDefault$.MODULE$.productPrefix(),
            KeyDefault$.MODULE$.productPrefix());
    public static final List<String> SUPPORTED_CONTENT_CHANNELS = Arrays.asList(
            "Display", "Site", "Email");
    private static final int ALIAS_MAX_LENGTH = Constants.DEFAULT_CHARS_LENGTH;
    private static final int MIN_COOKIE_EXPIRATION_DAYS = 1;
    private static final int MAX_COOKIE_EXPIRATION_DAYS = 292;

    @Override
    public boolean supports(Class<?> type) {
        return type == DatasetConfigView.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        throw new UnsupportedOperationException("Not supported yet. Use method validateOnUpdate instead");
    }

    public void validateOnUpdate(Object o, String uuid, Errors errors) {
        if(errors == null){
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "errors"));
        }
        // Dataset ID validation
        DatasetConfigView dataset = (DatasetConfigView) o;
        try {
            if(StringUtils.isBlank(uuid)) {
                errors.rejectValue("datasetId",
                        ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.empty", "datasetId"));
            } else {
                UUID datasetId = UUID.fromString(uuid);
                if (dataset.getDatasetId() == null) {
                    errors.rejectValue("datasetId",
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.empty", "datasetId"));
                } else {
                    if (!dataset.getDatasetId().equals(datasetId)) {
                        errors.rejectValue("datasetId",
                                ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil.getString("adm.error.datasetIdMismatch"));
                    }
                }
            }
        } catch(IllegalArgumentException e) {
            errors.rejectValue("datasetId",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("adm.error.invalidDatasetId"));
        }

        // ttlExpirationSeconds and cookieExpirationDays checks
        // We need these to be validated to be not null as DatasetConfig works with
        // primitive type 'long'. If any of these values are left empty, this will generate a
        // java.lang.NullPointerException: cannot unbox null value

        ApiValidationUtils.rejectIfNull(errors, "ttlExpirationSeconds");
        ApiValidationUtils.rejectIfNotPositiveLong(errors, "ttlExpirationSeconds",
                ResourceBundleUtil.getString("global.error.positiveNumber", "ttlExpirationSeconds"));
        ApiValidationUtils.rejectIfNull(errors, "cookieExpirationDays");
        ApiValidationUtils.rejectIfNotPositiveLong(errors, "cookieExpirationDays",
                ResourceBundleUtil.getString("global.error.positiveNumber", "cookieExpirationDays"));

        // Validate https://trueffect.atlassian.net/browse/DM-58 required fields
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "alias", ALIAS_MAX_LENGTH);
        ApiValidationUtils.rejectIfBlank(errors, "agencyId");
        ApiValidationUtils.rejectIfBlank(errors, "domain");
        ApiValidationUtils.rejectIfBlank(errors, "fileNamePrefix");

        // Cookies lists validations
        // cookiesToCapture
        CookieList cookiesToCapture = dataset.getCookiesToCapture();
        validateCookieList("cookiesToCapture", cookiesToCapture, errors);
        // durableCookies
        CookieList durableCookies = dataset.getDurableCookies();
        validateCookieList("durableCookies", durableCookies, errors);

        // cookieExpirationDays Max Number
        Integer cookieExpirationDays = dataset.getCookieExpirationDays();
        if (cookieExpirationDays != null) {
            if (cookieExpirationDays.intValue() > MAX_COOKIE_EXPIRATION_DAYS) {
                errors.rejectValue("cookieExpirationDays",
                        ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.outOfRanges",
                                "cookieExpirationDays",
                                MIN_COOKIE_EXPIRATION_DAYS,
                                MAX_COOKIE_EXPIRATION_DAYS));
            }
        }

        // Fail-through Defaults validation
        FailThroughDefaults ftd = dataset.getFailThroughDefaults();
        validateFailThoughDefaults(errors, ftd);

        // Content Channels validations
        if(dataset.getContentChannels() != null && !dataset.getContentChannels().isEmpty()) {

            List<String> cleanContentChannels = new ArrayList<>(dataset.getContentChannels().size());
            for(String cc : dataset.getContentChannels()) {
                cleanContentChannels.add(cc != null ? cc.trim() : null);
            }
            dataset.setContentChannels(cleanContentChannels);
            if (!SUPPORTED_CONTENT_CHANNELS.containsAll(dataset.getContentChannels())) {
                errors.rejectValue("contentChannels",
                        ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.unsupportedValues",
                                SUPPORTED_CONTENT_CHANNELS.toString()));
            }
        }
    }

    private void validateFailThoughDefaults(Errors errors, FailThroughDefaults ftd) {
        if(ftd == null) {
            errors.rejectValue("failThroughDefaults",
                    ApiValidationUtils.TYPE_REQUIRED,
                    ResourceBundleUtil.getString("global.error.requiredField", "failThroughDefaults"));
            return;

        }

        if(StringUtils.isBlank(ftd.getDefaultType())) {
            errors.rejectValue("failThroughDefaults.defaultType",
                    ApiValidationUtils.TYPE_REQUIRED,
                    ResourceBundleUtil.getString("global.error.requiredField", "failThroughDefaults.defaultType"));
        } else if (!SUPPORTED_FAIL_THROUGH_TYPES.contains(ftd.getDefaultType().trim())) {
            errors.rejectValue("failThroughDefaults.defaultType",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.invalidIfDoesNotContain",
                            "failThroughDefaults.defaultType" ,
                            SUPPORTED_FAIL_THROUGH_TYPES.toString()));
        } else {
            ftd.setDefaultType(ftd.getDefaultType().trim());

            if(ftd.getDefaultCookieList() == null || ftd.getDefaultCookieList().isEmpty()) {
                // If type == "CookieDefault", then list of cookies should be required
                if(ftd.isEnabled() && CookieDefault$.MODULE$.productPrefix().equals(ftd.getDefaultType())) {
                    errors.rejectValue("failThroughDefaults.defaultCookieList",
                            ApiValidationUtils.TYPE_REQUIRED,
                            ResourceBundleUtil.getString("global.error.requiredField", "failThroughDefaults.defaultCookieList"));
                }
            } else {
                boolean ignoreBlanks = !ftd.isEnabled() || !CookieDefault$.MODULE$.productPrefix().equals(ftd.getDefaultType());
                ftd.setDefaultCookieList(
                        validateCookies("failThroughDefaults.defaultCookieList",
                                ftd.getDefaultCookieList(),
                                errors, ignoreBlanks));
            }

            // If type == "KeyDefault", then defaultKey should be required
            if(ftd.isEnabled() && KeyDefault$.MODULE$.productPrefix().equals(ftd.getDefaultType())) {
                if(StringUtils.isBlank(ftd.getDefaultKey())) {
                    errors.rejectValue("failThroughDefaults.defaultKey",
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.empty",
                                    "failThroughDefaults.defaultKey"));
                } else {
                    ftd.setDefaultKey(ftd.getDefaultKey().trim());
                }
            }
            // Make sure to null up defaultKey when empty. This avoids Amazon to complain:
            // One or more parameter values were invalid: An AttributeValue may not contain
            // an empty string (Service: AmazonDynamoDBv2; Status Code: 400; Error Code: ValidationException
            ftd.setDefaultKey(StringUtils.isBlank(ftd.getDefaultKey()) ? null : ftd.getDefaultKey().trim());
        }
    }

    private void validateCookieList(String fieldName, CookieList cookieList, Errors errors) {
        if(cookieList != null) {
            if( cookieList.getCookies() == null || cookieList.getCookies().isEmpty()) {
                if (cookieList.isEnabled()) {
                    errors.rejectValue(fieldName + ".cookies",
                            ApiValidationUtils.TYPE_REQUIRED,
                            ResourceBundleUtil.getString("adm.error.cookiesRequiredIfSectionEnabled"));
                }
            } else {
                // Perform more validations for blank cookies and trim valid ones
                cookieList.setCookies(
                        validateCookieNames(fieldName + ".cookies", cookieList.getCookies(), errors, !cookieList.isEnabled()));
            }
        } else {
            errors.rejectValue(fieldName,
                    ApiValidationUtils.TYPE_REQUIRED,
                    ResourceBundleUtil.getString("global.error.requiredField", fieldName));
        }
    }

    private List<String> validateCookieNames(String fieldName, List<String> cookies, Errors errors, boolean ignoreBlanks) {
        List<String> validatedCookies = new ArrayList<>(cookies.size());
        int i = 0;
        String newCookieName;
        Set<String> cookieNames = new HashSet<>();
        Set<String> dupCookieNames = new LinkedHashSet<>();
        if(ignoreBlanks) {
            for (String cookieName : cookies) {
                if (StringUtils.isNotBlank(cookieName)) {
                    newCookieName = cookieName.trim();
                    validatedCookies.add(newCookieName);
                    // validation for duplicate cookie names
                    if (!cookieNames.add(newCookieName)) {
                        dupCookieNames.add(newCookieName);
                    }
                }
            }
        } else {
            for (String cookieName : cookies) {
                if (StringUtils.isBlank(cookieName)) {
                    errors.rejectValue(fieldName + "[" + i + "]",
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.empty", "cookie"));
                    newCookieName = cookieName;
                } else {
                    newCookieName = cookieName.trim();
                    // validation for duplicate cookie names
                    if (!cookieNames.add(newCookieName)) {
                        dupCookieNames.add(newCookieName);
                    }
                }

                validatedCookies.add(newCookieName);
                i++;
            }
        }

        if (!dupCookieNames.isEmpty()) {
            errors.rejectValue(fieldName,
                    ApiValidationUtils.TYPE_DUPLICATE,
                    ResourceBundleUtil.getString("global.error.duplicate",
                            ResourceBundleUtil.getString("adm.label.cookieName"),
                            StringUtils.join(dupCookieNames, ',')));
        }
        return validatedCookies;
    }

    private List<Cookie> validateCookies(String fieldName, List<Cookie> cookies, Errors errors, boolean ignoreBlanks) {
        int i = 0;
        Set<String> cookieNames = new HashSet<>();
        Set<String> dupCookieNames = new LinkedHashSet<>();
        List<Cookie> validatedCookies = new ArrayList<>(cookies.size());
        if(ignoreBlanks) {
            for(Cookie cookie : cookies) {
                if(StringUtils.isNotBlank(cookie.getName()) &&
                        StringUtils.isNotBlank(cookie.getValue())) {
                    cookie.setName(cookie.getName().trim());
                    cookie.setValue(cookie.getValue().trim());
                    if (!cookieNames.add(cookie.getName())) {
                        dupCookieNames.add(cookie.getName());
                    }
                    validatedCookies.add(cookie);
                }
            }
        } else {
            for(Cookie cookie : cookies) {
                // Cookie Name
                if(StringUtils.isBlank(cookie.getName())) {
                    errors.rejectValue(fieldName + "[" + i + "].name",
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.empty", "cookie.name"));
                } else {
                    cookie.setName(cookie.getName().trim());
                    // validation for duplicate cookie names
                    if (!cookieNames.add(cookie.getName())) {
                        dupCookieNames.add(cookie.getName());
                    }
                }
                // Cookie Value
                if(StringUtils.isBlank(cookie.getValue())) {
                    errors.rejectValue(fieldName + "[" + i + "].value",
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.empty", "cookie.value"));
                } else {
                    cookie.setValue(cookie.getValue().trim());
                }
                i++;
            }
            validatedCookies.addAll(cookies);
        }

        if (!dupCookieNames.isEmpty()) {
            errors.rejectValue(fieldName,
                    ApiValidationUtils.TYPE_DUPLICATE,
                    ResourceBundleUtil.getString("global.error.duplicate",
                            ResourceBundleUtil.getString("adm.label.cookieName"),
                            StringUtils.join(dupCookieNames, ',')));
        }
        return validatedCookies;
    }
}

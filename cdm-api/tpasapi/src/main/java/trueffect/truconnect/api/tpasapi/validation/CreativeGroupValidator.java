package trueffect.truconnect.api.tpasapi.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Gustavo Claure
 */
public class CreativeGroupValidator implements Validator {

    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == CreativeGroup.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CreativeGroup cg = (CreativeGroup) o;

        if (cg.getIsDefault() != null) {
            if (cg.getIsDefault().equals(1L)) {
                if ((cg.getGeoTargets() != null && !cg.getGeoTargets().isEmpty())
                        || (cg.getDoCookieTargeting() == 1L || !StringUtils.isBlank(cg.getCookieTarget()))
                        || (cg.getDoDaypartTargeting() == 1L || !StringUtils.isBlank(cg.getDaypartTarget()))) {
                    String message = "The creative group cannot have any targeting if default is enabled.";
                    errors.rejectValue("isDefault", ApiValidationUtils.TYPE_INVALID, message);
                }
            }
        } else {
            ApiValidationUtils.rejectIfNull(errors, "isDefault");
        }

        if (cg.getDoCookieTargeting() == null) {
            cg.setDoCookieTargeting(Constants.DEFAULT_DO_COOKIE_TARGETING);
        }
        if (cg.getDoCookieTargeting() == 0L) {
            if (!StringUtils.isBlank(cg.getCookieTarget())) {
                errors.rejectValue("cookieTarget", ApiValidationUtils.TYPE_INVALID, "A cookie target must not be provided if cookie targeting is not enabled.");
            }
            cg.setCookieTarget(null);
        } else if (StringUtils.isBlank(cg.getCookieTarget())) {
            errors.rejectValue("cookieTarget", ApiValidationUtils.TYPE_INVALID, "A cookie target must be provided if cookie targeting is enabled.");
        }
        if (cg.getDoDaypartTargeting() == null) {
            cg.setDoDaypartTargeting(Constants.DEFAULT_DO_DAYPART_TARGETING);
        }
        if (cg.getDoDaypartTargeting() == 0L) {
            if (!StringUtils.isBlank(cg.getDaypartTarget())) {
                errors.rejectValue("daypartTarget", ApiValidationUtils.TYPE_INVALID, "A daypart target must not be provided if daypart targeting is not enabled.");
            }
            cg.setDaypartTarget(null);
        } else if (StringUtils.isBlank(cg.getDaypartTarget())) {
            errors.rejectValue("daypartTarget", ApiValidationUtils.TYPE_INVALID, "A daypart target must be provided if daypart targeting is enabled.");
        }
        if (cg.getDoGeoTargeting() == null) {
            cg.setDoGeoTargeting(Constants.DEFAULT_DO_GEO_TARGETING);
        }
        if (cg.getDoGeoTargeting() == 0L) {
            if (cg.getGeoTargets() != null && !cg.getGeoTargets().isEmpty()) {
                errors.rejectValue("geoTargets", ApiValidationUtils.TYPE_INVALID, "A geo target must not be provided if geo targeting is not enabled.");
            }
            cg.setGeoTargets(null);
        } else if (cg.getGeoTargets() == null || cg.getGeoTargets().isEmpty()) {
            errors.rejectValue("geoTargets", ApiValidationUtils.TYPE_INVALID, "A geo target must be provided if geo targeting is enabled.");
        }
    }
}

package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.resources.ResourceUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;

/**
 *
 * @author Rambert Rioja
 * @edited Richard Jaldin
 */
public class CreativeGroupValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return CreativeGroup.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "CreativeGroup");
        ApiValidationUtils.rejectIfNull(errors, "campaignId");
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "name",
                Constants.CREATIVE_GROUP_MAX_NAME_LENGTH);
        validateCommons(o, errors);
    }

    public void validatePUT(Object o, Long id, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "CreativeGroup");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "name",
                Constants.CREATIVE_GROUP_MAX_NAME_LENGTH);
        validateCommons(o, errors);
    }

    public void validateUpdateWeight(Object o, Errors errors) {
        CreativeGroup group = (CreativeGroup) o;
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "CreativeGroup");
        long min = Constants.CREATIVE_GROUP_MIN_WEIGHT;
        long max = Constants.CREATIVE_GROUP_MAX_WEIGHT;
        if (group.getWeight() != null && 
                (group.getWeight() < min || group.getWeight() > max)) {
            errors.rejectValue("weight",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.outOfRanges",
                            ResourceBundleUtil.getString("creativeInsertion.label.creativeGroupWeight"),
                            min,
                            max));
        }    
    }    

    private void validateCommons(Object o, Errors errors) {
        ApiValidationUtils.rejectIfNull(errors, "clickthroughCap");
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "cookieTarget", 2000);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "daypartTarget", 2000);
        ApiValidationUtils.rejectIfNegativeNumber(errors, "impressionCap");
        ApiValidationUtils.rejectIfDoesNotContain(errors, "rotationType", Arrays.asList("Weighted", "Cursor"));
        
        CreativeGroup cg = (CreativeGroup) o;
        
        ApiValidationUtils.rejectIfInvalidCharacters(errors, "name", ValidationConstants.REGEXP_CREATIVE_GROUP_NAME);

        if ("Cursor".equals(cg.getRotationType())) {
            cg.setDoStoryboarding(Constants.ENABLED);
        } else {
            cg.setDoStoryboarding(Constants.DISABLED);
        }

        if (cg.getEnableGroupWeight() == null) {
            cg.setEnableGroupWeight(Constants.DEFAULT_ENABLE_GROUP_WEIGHT);
        }
        if (cg.getEnableGroupWeight().compareTo(Constants.DISABLED) != 0) {
            ApiValidationUtils.rejectIfOutOfRangeOrNotNumber(errors, "weight",
                    Constants.CREATIVE_GROUP_MIN_WEIGHT,
                    Constants.CREATIVE_GROUP_MAX_WEIGHT,
                    null);
        }

        if (cg.getIsDefault() != null && cg.getIsDefault().equals(1L)) {
            if(cg.getDoDaypartTargeting() == Constants.ENABLED
                    || cg.getDoCookieTargeting() == Constants.ENABLED
                    || cg.getDoGeoTargeting() == Constants.ENABLED
                    || (cg.getGeoTargets() != null && cg.getGeoTargets().isEmpty())) {
                String message = "The creative group cannot have any targeting if default is enabled.";
                errors.rejectValue("isDefault", ApiValidationUtils.TYPE_INVALID, message);
            }
        } else {
            ApiValidationUtils.rejectIfNull(errors, "isDefault");
        }

        if (cg.getDoCookieTargeting() == null) {
            cg.setDoCookieTargeting(Constants.DEFAULT_DO_COOKIE_TARGETING);
        }
        if (cg.getDoCookieTargeting() != Constants.DISABLED && StringUtils.isBlank(cg.getCookieTarget())) {
            errors.rejectValue("cookieTarget", ApiValidationUtils.TYPE_INVALID, "A cookie target must be provided if cookie targeting is enabled.");
        }
        if (cg.getDoDaypartTargeting() == null) {
            cg.setDoDaypartTargeting(Constants.DEFAULT_DO_DAYPART_TARGETING);
        }
        if (cg.getDoDaypartTargeting() != Constants.DISABLED && StringUtils.isBlank(cg.getDaypartTarget())) {
            errors.rejectValue("daypartTarget", ApiValidationUtils.TYPE_INVALID, "A daypart target must be provided if daypart targeting is enabled.");
        }
        if (cg.getDoGeoTargeting() == null) {
            cg.setDoGeoTargeting(Constants.DEFAULT_DO_GEO_TARGETING);
        }
        if (cg.getDoGeoTargeting() != Constants.DISABLED && (cg.getGeoTargets() == null || cg.getGeoTargets().isEmpty())) {
            errors.rejectValue("geoTargets", ApiValidationUtils.TYPE_INVALID, "A geo target must be provided if geo targeting is enabled.");
        }

        if (cg.getPriority() == null) {
            cg.setPriority(Constants.DEFAULT_PRIORITY);
        } else {
            ApiValidationUtils.rejectIfOutOfRangeOrNotNumber(errors, "priority",
                    Constants.PRIORITY_MIN,
                    Constants.PRIORITY_MAX,
                    null);
        }
        if (cg.getEnableFrequencyCap() == null) {
            cg.setEnableFrequencyCap(Constants.DEFAULT_ENABLE_FREQUENCY_CAP);
        }
        if (cg.getFrequencyCap() == null) {
            cg.setFrequencyCap(Constants.DEFAULT_FREQUENCY_CAP);
        }
        if (cg.getFrequencyCapWindow() == null) {
            cg.setFrequencyCapWindow(Constants.DEFAULT_FREQUENCY_CAP_WINDOW);
        }
        if (cg.getEnableFrequencyCap() != Constants.DISABLED) {
            String strFrecuencyCapWindow = ResourceUtil.get("public.frequency.cap.window.max");
            Long frecuencyCapWindow = new Long(strFrecuencyCapWindow);
            if (cg.getFrequencyCap() == null || cg.getFrequencyCap() <= 0L) {
                errors.rejectValue("frequencyCap", ApiValidationUtils.TYPE_INVALID, "The frequency cap should be more than zero.");
            }
            if (cg.getFrequencyCapWindow() == null || cg.getFrequencyCapWindow() <= 0L || cg.getFrequencyCapWindow() > frecuencyCapWindow) {
                errors.rejectValue("frequencyCapWindow", ApiValidationUtils.TYPE_INVALID,
                        "The frequency cap window should be more than 0 and less than " + frecuencyCapWindow + ".");
            }
        }
    }
}

package trueffect.truconnect.api.crud.validation;

import static trueffect.truconnect.api.commons.validation.ApiValidationUtils.TYPE_INVALID;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.service.CreativeManager;
import trueffect.truconnect.api.crud.service.CreativeManager.CreativeType;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Date;

/**
 * Creative Insertion Spring-based Validator.
 * Supports validation for Creative Insertion Creation and Modification
 * @author Marcelo Heredia
 */
public class CreativeInsertionSpringValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CreativeInsertion.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (errors == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "errors"));
        }
        CreativeInsertion ci = (CreativeInsertion) o;
        Date startDate = ci.getStartDate();
        Date endDate = ci.getEndDate();
        if (startDate != null && endDate != null) {
            if(startDate.compareTo(endDate) > 0) {
                errors.rejectValue("startDate",
                        ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.dateAfter", startDate, endDate));
            }
        } else {
            // In this case, we make sure to null out the start and end dates as they
            // should be provided both at the same time
            ci.setStartDate(null);
            ci.setEndDate(null);
        }

        long min = Constants.CREATIVE_INSERTION_MIN_WEIGHT;
        long max = Constants.CREATIVE_INSERTION_MAX_WEIGHT;
        if (ci.getWeight() != null && (ci.getWeight() < min || ci.getWeight() > max)) {
            errors.rejectValue("weight",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.outOfRanges",
                            ResourceBundleUtil.getString("creativeInsertion.label.creativeWeight"),
                            min,
                            max));
        }
    }
    
    /**
     * Validates only the associated Clickthrough and Clickthroughs (for multi-click creatives)
     * @param ci The Creative Insertion to validate
     * @param errors The Errors object where to put errors into
     */
    public void validateClickthroughUpdate(CreativeInsertion ci, Errors errors) {
        if (errors == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "errors"));
        }
        if(ci == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Creative Insertion"));
        }
        if (CreativeManager.CreativeType.typeOf(ci.getCreativeType()) == CreativeManager.CreativeType.TRD) {
            ApiValidationUtils.rejectIfNotNull(errors, "clickthrough");
        } else {
            ApiValidationUtils.rejectIfDoesNotMatchPattern(
                    errors,
                    "clickthrough",
                    ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthrough", ci.getClickthrough()),
                    ValidationConstants.PATTERN_CLICKTHROUGH_URL);
            // Check only if 'clickthrough' field has no errors
            if (ci.getClickthrough() != null && !errors.hasFieldErrors("clickthrough")) {
                ci.setClickthrough(ci.getClickthrough().trim());
                if (ci.getClickthroughs() != null) {
                    validateClickthroughs(ci, errors);
                }
            }
        }
    }
  
    /**
     * Validates only the associated Clickthrough and Clickthroughs (for
     * multi-click creatives)
     *
     * @param ci The Creative Insertion to validate
     * @param creative The Creative
     * @param errors The Errors object where to put errors into
     */
    public void validateClickthroughCreate(CreativeInsertion ci, Creative creative, Errors errors) {
        if (errors == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "errors"));
        }
        if (creative == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Creative"));
        }
        if (ci == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Creative Insertion"));
        }

        CreativeType creativeType = CreativeManager.CreativeType.typeOf(ci.getCreativeType());
        switch (creativeType) {
            case TRD:
                ApiValidationUtils.rejectIfNotNull(errors, "clickthrough");
                if (ci.getClickthroughs() != null && !ci.getClickthroughs().isEmpty()) {
                    //Error when ci has additional clickthroughs
                    errors.rejectValue("clickthroughs", TYPE_INVALID, ResourceBundleUtil.getString("creativeInsertion.error.clickthroughsNotAllowed"));
                }
                break;
            case ZIP:
            case HTML5:
                if (ci.getClickthrough() == null
                        && (ci.getClickthroughs() == null || ci.getClickthroughs().isEmpty())) {
                    ci.setClickthrough(creative.getClickthrough());
                    ci.setClickthroughs(creative.getClickthroughs());
                } else {
                    if (ci.getClickthroughs() != null && creative.getClickthroughs() != null) {
                        if (ci.getClickthrough() == null) {
                            //Error when ci has no clickthrough
                            errors.rejectValue("clickthroughs", TYPE_INVALID, ResourceBundleUtil.getString("creativeInsertion.error.clickthroughRequiredCreate"));
                        } else if (ci.getClickthroughs().size() != creative.getClickthroughs().size()) {
                            //Error when number of ci's clickthroughs is different than creative's clickthroughs
                            errors.rejectValue("clickthroughs", TYPE_INVALID, ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthroughsCount"));
                        } else {
                            ApiValidationUtils.rejectIfDoesNotMatchPattern(
                                    errors,
                                    "clickthrough",
                                    ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthrough", ci.getClickthrough()),
                                    ValidationConstants.PATTERN_CLICKTHROUGH_URL);
                            ci.setClickthrough(ci.getClickthrough().trim());
                            if (!ci.getClickthroughs().isEmpty()) {
                                validateClickthroughs(ci, errors);
                            }
                        }
                    } else {
                        if ((ci.getClickthroughs() == null && (creative.getClickthroughs() != null && !creative.getClickthroughs().isEmpty()))
                                || (ci.getClickthroughs() != null && !ci.getClickthroughs().isEmpty()) && (creative.getClickthroughs() == null || creative.getClickthroughs().isEmpty())) {
                            //Error when number of ci's clickthroughs is different than creative's clickthroughs
                            errors.rejectValue("clickthroughs", TYPE_INVALID, ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthroughsCount"));
                        } else if (creative.getClickthroughs() != null && !creative.getClickthroughs().isEmpty()) {
                            //Error when number of ci's clickthroughs is different than creative's clickthroughs
                            errors.rejectValue("clickthroughs", TYPE_INVALID, ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthroughsCount"));
                        } else {
                            ApiValidationUtils.rejectIfDoesNotMatchPattern(
                                    errors,
                                    "clickthrough",
                                    ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthrough", ci.getClickthrough()),
                                    ValidationConstants.PATTERN_CLICKTHROUGH_URL);
                            ci.setClickthrough(ci.getClickthrough().trim());
                        }
                    }
                }
                break;
            default:
                if (!(ci.getClickthroughs() == null || ci.getClickthroughs().isEmpty())) {
                    errors.rejectValue("clickthroughs", TYPE_INVALID, ResourceBundleUtil.getString("creativeInsertion.error.additionalClickthroughsNotAllowed"));
                }
                if (ci.getClickthrough() == null) {
                    ci.setClickthrough(creative.getClickthrough());
                } else {
                    ApiValidationUtils.rejectIfDoesNotMatchPattern(
                            errors,
                            "clickthrough",
                            ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthrough", ci.getClickthrough()),
                            ValidationConstants.PATTERN_CLICKTHROUGH_URL);
                    // Check only if 'clickthrough' field has no errors
                    if (ci.getClickthrough() != null && !errors.hasFieldErrors("clickthrough")) {
                        ci.setClickthrough(ci.getClickthrough().trim());
                    }
                }
        }
    }

    private void validateClickthroughs(CreativeInsertion ci, Errors errors) {
        int i = 0;
        for (Clickthrough ct : ci.getClickthroughs()) {
            String url = ct.getUrl();
            if (StringUtils.isNotBlank(url)) {
                ct.setUrl(url.trim());
                String field = "clickthroughs[" + (i++) + "].url";
                ApiValidationUtils.rejectIfDoesNotMatchPattern(
                        errors,
                        field,
                        ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthrough", url),
                        ValidationConstants.PATTERN_CLICKTHROUGH_URL);
            }
        }
    }
}

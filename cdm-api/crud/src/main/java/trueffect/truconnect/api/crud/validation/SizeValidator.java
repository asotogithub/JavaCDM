package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Abel Soto
 */
public class SizeValidator implements Validator {

    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == Size.class;
    }

    public void validate(Object o, Errors errors) {
        Size size = (Size) o;
        ApiValidationUtils.rejectIfNull(errors, "agencyId");
        validateDimension(errors, "width", Constants.MIN_SIZE_WIDTH, Constants.MAX_SIZE_WIDTH,
                ResourceBundleUtil.getString("global.label.adWidth"));
        validateDimension(errors, "height", Constants.MIN_SIZE_HEIGHT, Constants.MAX_SIZE_HEIGHT,
                ResourceBundleUtil.getString("global.label.adHeight"));

        applyDefaults(size);

        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "label",
                Constants.DEFAULT_CHARS_LENGTH);
    }

    public Size applyDefaults(Size size) {
        // Always set the label based on its width and height
        size.setLabel(size.getWidth() + "x" + size.getHeight());
        return size;
    }

    /**
     * Validates all import fields related to a {@code Size}
     * @param media The {@code MediaRawDataView} to validate
     * @param errors The {@code Errors} object where all validation errors are collected
     */
    public void validateFieldsForImport(MediaRawDataView media, Errors errors) {
        if(media.getFieldsWithFormulaError().contains("adWidth")) { //contains error in XLS formulas
            errors.rejectValue("adWidth", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.adWidth")));
        } else {
            validateDimension(errors, "adWidth", Constants.MIN_SIZE_WIDTH, Constants.MAX_SIZE_WIDTH,
                    ResourceBundleUtil.getString("global.label.adWidth"));
        }

        if(media.getFieldsWithFormulaError().contains("adHeight")) { //contains error in XLS formulas
            errors.rejectValue("adHeight", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.adHeight")));
        } else {
            validateDimension(errors, "adHeight", Constants.MIN_SIZE_HEIGHT,
                    Constants.MAX_SIZE_HEIGHT,
                    ResourceBundleUtil.getString("global.label.adHeight"));
        }
    }

    private void validateDimension(Errors errors, String field, int lowerLimit, int upperLimit,
                                   String fieldName) {
        ApiValidationUtils.rejectIfBlank(errors,
                field,
                ApiValidationUtils.TYPE_INVALID,
                ResourceBundleUtil.getString("global.error.importRequiredField", fieldName));

        Number number;
        if(!errors.hasFieldErrors(field)) {
            number = ApiValidationUtils.parseNumberAs(errors, field, Integer.class,
                    ResourceBundleUtil.getString("global.error.importInvalidNumber",
                            fieldName,
                            String.valueOf(lowerLimit),
                            String.valueOf(upperLimit)));

            if(number != null) {
                // Check if negative
                if(number.intValue() <= lowerLimit) {
                    errors.rejectValue(field, ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.importInvalidNegativeNumber", fieldName));
                } else {
                    String customMessage = ResourceBundleUtil.getString("global.error.importInvalidNumber", fieldName,
                            String.valueOf(lowerLimit), String.valueOf(upperLimit));
                    ApiValidationUtils.rejectIfNumberGreaterThan(errors, field, number, upperLimit, Integer.class, customMessage);
                }
            }
        }
    }
}

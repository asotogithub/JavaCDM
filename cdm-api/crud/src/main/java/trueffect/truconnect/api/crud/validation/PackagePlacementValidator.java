package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Validate Placements Created below a Package
 * @author Marcelo Heredia
 */
public class PackagePlacementValidator extends PlacementValidator implements Validator {

    public boolean supports(Class type) {
        return type == Placement.class;
    }

    /**
     * Validates a Placement to Create below a Package
     * @param o The Placement to validate
     * @param errors The {@code Errors} object to attach the validation results
     */
    public void validate(Object o, Errors errors) {
        if(errors == null){
            throw new IllegalArgumentException("Errors cannot be null");
        }
        Placement placement = (Placement) o;
        if (placement.getId() != null) {
            errors.rejectValue("id",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("ValidationCode.UNSUPPORTED", "id"));
        }
        if(placement.getCostDetails() != null){
            errors.rejectValue("costDetails",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("ValidationCode.UNSUPPORTED", "costDetails"));
        }
        ApiValidationUtils.rejectIfBlank(errors, "campaignId");
        ApiValidationUtils.rejectIfBlank(errors, "siteId");

        validateCommons(placement, errors);
    }

    /**
     * Validates a List of {@code Placement}s to be created below a Package
     * @param placements The List of {@code Placement}s to be validated
     **/
    public void validatePlacementsToCreateBelowExistingPackage(List<Placement> placements) {

        if (placements == null || placements.isEmpty()) {
            ValidationError error = new ValidationError();
            error.setField("placements");
            throw BusinessValidationUtil.buildSystemException(error, ValidationCode.REQUIRED);
        }
        // TODO make this validation consistent with others
        for (Placement placement : placements) {
            BeanPropertyBindingResult plErrors = new BeanPropertyBindingResult(placement, "placement");
            validatePlacementToCreateBelowExistingPackage(placement, plErrors);
            if(plErrors.hasErrors()) {
                throw BusinessValidationUtil.buildSpringValidationSystemException(ValidationCode.INVALID, plErrors);
            }
        }
    }

    public void validatePlacementToCreateBelowExistingPackage(Placement placement, Errors errors) {
        if(errors == null){
            throw new IllegalArgumentException("Errors cannot be null");
        }
        if (placement.getId() != null) {
            errors.rejectValue("id",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("ValidationCode.UNSUPPORTED", "id"));
        }
        if (placement.getCostDetails() != null) {
            errors.rejectValue("costDetails",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("ValidationCode.UNSUPPORTED", "costDetails"));
        }
        ApiValidationUtils.rejectIfBlank(errors, "campaignId");
        ApiValidationUtils.rejectIfBlank(errors, "siteId");
        validateCommons(placement, errors);
    }

    public void validatePlacementsToAssociateBelowExistingPackage(List<Placement> placements, Errors errors) {
        if (placements == null || placements.isEmpty()) {
            return;
        }
        for (Placement placement : placements) {
            validatePlacementToAssociateBelowExistingPackage(placement, errors);
        }
    }

    private void validatePlacementToAssociateBelowExistingPackage(Placement placement, Errors errors) {
        if (placement.getId() == null) {
            errors.rejectValue("id",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("ValidationCode.REQUIRED"));
        }
    }
}

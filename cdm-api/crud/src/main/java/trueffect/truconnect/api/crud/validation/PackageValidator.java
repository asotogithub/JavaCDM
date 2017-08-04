package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates operations with Packages
 * Created by marcelo.heredia on 10/13/2015.
 * @author Marcelo Heredia
 */
public class PackageValidator implements Validator{

    private CostDetailValidator costDetailValidator;
    private PackagePlacementValidator placementValidator;

    public PackageValidator() {
        costDetailValidator = new CostDetailValidator();
        placementValidator = new PackagePlacementValidator();
    }

    @Override
    public boolean supports(Class aClass) {
        return Package.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

    }

    public void validatePackageForCreate(Package pkg, Errors errors) {
        if(errors == null){
            throw new IllegalArgumentException("Errors cannot be null");
        }
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Package");
        validatePackageCommons(pkg, errors);
        errors.pushNestedPath("costDetails[0]");
        pkg.setCostDetails(costDetailValidator.cleanCostDetails(pkg.getCostDetails()));
        costDetailValidator.validateForCreate(pkg.getCostDetails().iterator().next(), errors);
        errors.popNestedPath();
        ApiValidationUtils.rejectIfNull(errors, "placements");
        // Check if Placements to associate below this Package are correct

        if (pkg.getPlacements() == null || pkg.getPlacements().isEmpty()) {
            errors.rejectValue("placements",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("ValidationCode.REQUIRED"));
            return;
        }
        int i = 0;
        for (Placement placement : pkg.getPlacements()) {
            errors.pushNestedPath("placements[" + i + "]");
            ValidationUtils.invokeValidator(placementValidator, placement, errors);
            errors.popNestedPath();
            i++;
        }
    }

    private void validatePackageCommons(Package pkg, Errors errors) {
        if (pkg == null) {
            ApiValidationUtils.rejectIfNull(errors, "package");
            return;
        }
        ApiValidationUtils.rejectIfBlank(errors, "name");
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "name", Constants.PACKAGE_NAME_MAX_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "description",
                Constants.PACKAGE_NAME_MAX_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "externalId",
                Constants.PACKAGE_EXTERNAL_PACKAGE_ID_MAX_LENGTH);
    }

    /**
     * Validates a Package for Update
     * @param pkg The Package to update
     * @param errors The errors object to contain the validation result
     */
    public void validatePackageForUpdate(Package pkg, Errors errors) {
        if(errors == null){
            throw new IllegalArgumentException("Errors cannot be null");
        }
        validatePackageCommons(pkg, errors);
        ApiValidationUtils.rejectIfNull(errors, "id");
        // Checking if update request has at least a placement
        ApiValidationUtils.rejectIfNull(errors, "placements");
        ApiValidationUtils.rejectIfEmpty(errors, "placements");
        // Check Package Cost Details
        costDetailValidator.validateForUpdate(pkg.getCostDetails(), errors);
        // Check if Placements to associate below this Package are correct
        placementValidator.validatePlacementsToAssociateBelowExistingPackage(pkg.getPlacements(),
                errors);
    }

    public void validateFieldsForImport(MediaRawDataView media, Errors errors) {
        // Package Id
        if(media.getFieldsWithFormulaError().contains("mediaPackageId")) { //contains error in XLS formulas
            errors.rejectValue("mediaPackageId", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.mediaPackageId")));
        } else {
            if (StringUtils.isNotBlank(media.getMediaPackageId())) {
                try {
                    long id = Long.parseLong(media.getMediaPackageId().trim());
                    if (id < 0) {
                        errors.rejectValue("mediaPackageId", ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil.getString("global.error.importInvalidNumber",
                                        ResourceBundleUtil.getString("global.label.mediaPackageId"),
                                        0, 9));
                    } else {
                        media.setMediaPackageId(String.valueOf(id));
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("mediaPackageId", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.importInvalidNumber",
                                    ResourceBundleUtil.getString("global.label.mediaPackageId"),
                                    0, 9));
                }
            }
        }

        if (media.getFieldsWithFormulaError().contains("mediaPackageName")) { //contains error in XLS formulas
            errors.rejectValue("mediaPackageName", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString(
                            "global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.mediaPackageName")));
        } else {
            String packageName = media.getMediaPackageName();
            if (StringUtils.isNotBlank(packageName)) {
                String message = ResourceBundleUtil.getString("global.error.importFieldLength",
                        ResourceBundleUtil.getString("global.label.mediaPackageName"),
                        Constants.PACKAGE_NAME_MAX_LENGTH);
                ApiValidationUtils.rejectIfCharactersUpTo(errors, "mediaPackageName",
                        Constants.PACKAGE_NAME_MAX_LENGTH, packageName, message);
                if (!errors.hasFieldErrors("mediaPackageName")) {
                    media.setMediaPackageName(packageName.trim());
                }
            }
        }
    }

    public void validatePlacementId(Long placementId,
                                    trueffect.truconnect.api.commons.exceptions.business.Errors errors) {
        // Check required
        if(placementId == null) {
            errors.addError(new ValidationError(
                    ResourceBundleUtil.getString("global.error.importRequiredField", "placementId"),
                    ValidationCode.REQUIRED));
        } else {
            // Check if not 0 not negative
            if(placementId <= 0) {
                errors.addError(new ValidationError(
                        ResourceBundleUtil.getString("global.error.positiveNumber", "placementId"),
                        ValidationCode.INVALID
                ));
            }
        }
    }

    public void validatePlacementCanBeDisassociated(Placement placement,
                                                    trueffect.truconnect.api.commons.exceptions.business.Errors errors) {
        if(placement.getPackageId() == null) {
            String message = ResourceBundleUtil.getString(
                    "packagePlacement.error.disassociatePlacementWithNoPackage", String.valueOf(placement.getId()));
            errors.addError(new ValidationError(message, ValidationCode.INVALID));
        }
    }

    public void validatePackageHasPlacementsToDisassociate(Package pkg,
                                                           Long placementId,
                                                           trueffect.truconnect.api.commons.exceptions.business.Errors errors) {
        // If only one placement; then, don't allow to disassociate
        if(pkg.getPlacements().size() == 1 && pkg.getPlacements().iterator().next().getId().equals(placementId)) {
            String message = ResourceBundleUtil.getString(
                    "packagePlacement.error.noPlacementsToDisassociate",
                    String.valueOf(placementId),
                    String.valueOf(pkg.getId()));
            errors.addError(new ValidationError(message, ValidationCode.INVALID));
        }
    }
}

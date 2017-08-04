package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Rambert Rioja
 * modified by Richard Jaldin
 */
public class PlacementValidator implements Validator {

    private CostDetailValidator costDetailValidator;

    public PlacementValidator() {
        this.costDetailValidator = new CostDetailValidator();
    }

    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == Placement.class;
    }

    public void validate(Object o, Errors errors) {

    }

    public void validateStandalonePlacementForCreate(Placement placement, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Placement");
        ApiValidationUtils.rejectIfBlank(errors, "siteId");
        errors.pushNestedPath("costDetails[0]");
        placement.setCostDetails(costDetailValidator.cleanCostDetails(placement.getCostDetails()));
        costDetailValidator.validateForCreate(placement.getCostDetails().iterator().next(), errors);
        errors.popNestedPath();

        applyDefaults(placement);

        validateCommons(placement, errors);
        CostDetail costDetail = placement.getCostDetails().iterator().next();
        placement.setStartDate(costDetail.getStartDate());
        placement.setEndDate(costDetail.getEndDate());
        placement.setRateType(RateTypeEnum.typeOf(costDetail.getRateType()).toString());
        placement.setRate(costDetail.getPlannedGrossRate());
    }

    /**
     * Validates if a Standalone Placement can be updated
     *
     * @param placementId The Placement ID
     * @param placement   The Placement object to validate
     * @param errors      The {@code Errors} object containing the validation results
     */
    public void validatePlacementForUpdate(Long placementId, Placement placement, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "Placement");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", placementId);

        applyDefaults(placement);

        validateCommons(placement, errors);
        // Validate Placement according its Package Id
        if (placement.getPackageId() == null) {
            costDetailValidator.validateForUpdate(placement.getCostDetails(), errors);
        } else {
            if (placement.getCostDetails() != null && !placement.getCostDetails().isEmpty()) {
                // Return another error if Placement is non-standalone and Cost Details are given
                String message = ResourceBundleUtil.getString(
                        "packagePlacement.error.placementCannotUpdatePackageAssociation",
                        String.valueOf(placement.getId()));
                errors.rejectValue(message, ApiValidationUtils.TYPE_INVALID, "packageId");
            }
        }
    }

    protected void validateCommons(Object o, Errors errors) {
        ApiValidationUtils
                .rejectIfBlankOrCharactersUpTo(errors, "name", Constants.PLACEMENT_NAME_MAX_LENGTH);
        Placement placement = (Placement) o;
        if (placement.getStartDate() != null && placement.getEndDate() != null) {
            if (placement.getStartDate().compareTo(placement.getEndDate()) > 0) {
                String message = "End Date is less than Start Date; Start Date:" + placement
                        .getStartDate() + ", End Date:" + placement.getEndDate();
                errors.rejectValue("endDate", ApiValidationUtils.TYPE_INVALID, message);
            }
        }
        // maxFileSize is a positive integer
        if (placement.getMaxFileSize() != null) {
            ApiValidationUtils.rejectIfLessThanNumber(errors, "maxFileSize",
                    Constants.PLACEMENT_MIN_FILE_SIZE);
        }
        //rate is floating point number >= 0
        if (placement.getRate() != null) {
            ApiValidationUtils.rejectIfLessThanNumber(errors, "rate", 0.0);
            validateNumber(placement.getRate(), "rate", errors);
        }
        // rateType can be "CPM", "CPC", "CPA", "CPL", "FLT"
        List<String> rateTypes =
                Arrays.asList(RateTypeEnum.CPM.toString(), RateTypeEnum.CPC.toString(),
                        RateTypeEnum.CPA.toString(), RateTypeEnum.CPL.toString(),
                        RateTypeEnum.FLT.toString());
        if (StringUtils.isNotBlank(placement.getRateType())) {
            ApiValidationUtils.rejectIfDoesNotContain(errors, "rateType", rateTypes);
        }
        // status can have three values: New, Accepted, Rejected
        //     This is stored in the "placement_status" table
        //     Values are: New=10, Accepted=16, Rejected=17
        // externalPlacementId is stored as an entry in "Extended_Properties_Values"
        List<String> statuses = Arrays.asList(InsertionOrderStatusEnum.NEW.getName(),
                InsertionOrderStatusEnum.ACCEPTED.getName(),
                InsertionOrderStatusEnum.REJECTED.getName());
        if (StringUtils.isNotBlank(placement.getStatus())) {
            ApiValidationUtils.rejectIfDoesNotContain(errors, "status", statuses);
        }

        //Validation to width and ad height have positive values - cannot be blank
        if (placement.getHeight() == null || placement.getHeight() <= 0) {
            String message =
                    "The Placement " + (placement.getId() != null ? placement.getId() : " ")
                            + ", is missing or requires corrections to be made to the Height. "
                            + "Height: " + placement.getHeight()
                            + " . Please correct the details for the information provided.";
            errors.rejectValue("height", ApiValidationUtils.TYPE_INVALID, message);

        }

        if (placement.getWidth() == null || placement.getWidth() <= 0) {
            String message =
                    "The Placement " + (placement.getId() != null ? placement.getId() : " ")
                            + ", is missing or requires corrections to be made to the Width. "
                            + "Width: " + placement.getWidth()
                            + " . Please correct the details for the information provided.";
            errors.rejectValue("width", ApiValidationUtils.TYPE_INVALID, message);
        }

        Pattern pattern =
                Pattern.compile(ValidationConstants.REGEXP_ALPHANUMERIC_AND_SPECIAL_CHARACTERS,
                        Pattern.CASE_INSENSITIVE);
        ApiValidationUtils.rejectIfDoesNotMatchPattern(errors, "name",
                ResourceBundleUtil.getString("global.error.name",
                        ResourceBundleUtil.getString("global.label.placement")), pattern);
    }

    public Placement applyDefaults(Placement placement) {
        if (placement.getMaxFileSize() == null) {
            placement.setMaxFileSize(Constants.PLACEMENT_MAX_FILE_SIZE);
        }
        if (placement.getRate() == null) {
            placement.setRate(Constants.PLACEMENT_DEFAULT_RATE);
        }

        if (StringUtils.isBlank(placement.getRateType())) {
            placement.setRateType(RateTypeEnum.CPM.toString());
        }

        if (StringUtils.isBlank(placement.getStatus())) {
            placement.setStatus(InsertionOrderStatusEnum.NEW.getName());
        }

        if (placement.getIsSecure() == null) {
            placement.setIsSecure(0L);
        }

        if (placement.getUtcOffset() == null) {
            placement.setUtcOffset(0L);
        }

        if (placement.getSmEventId() == null) {
            placement.setSmEventId(-1L);
        }

        if (placement.getCountryCurrencyId() == null) {
            placement.setCountryCurrencyId(1L);
        }

        return placement;
    }

    public void validateChangeStatus(Object newPlacement, Object oldPlacement, Errors errors) {
        Placement placementNew = (Placement) newPlacement;
        Placement placementOld = (Placement) oldPlacement;

        boolean isError = false;
        String rules = "";

        if (placementOld.getStatus().equals("New")) {
            if (placementNew.getStatus().equals("New")) {
                isError = true;
                rules = "Accepted or Rejected";
            }
        }else if(placementOld.getStatus().equals("Accepted")){
            if (!placementNew.getStatus().equals("Rejected")) {
                isError = true;
                rules = "Rejected";
            }
        }else if(placementOld.getStatus().equals("Rejected")){
            if (!placementNew.getStatus().equals("Accepted")) {
                isError = true;
                rules = "Accepted";
            }
        }

        if (isError) {
            String message = "The Placement's status cannot be changed to " + placementNew.getStatus()
                    + " as it is currently in status " + placementOld.getStatus() +"."
                    + "You can change this status to: " + rules + ". Please correct the status.";
            errors.rejectValue("status", ApiValidationUtils.TYPE_INVALID, message);
        }
    }

    /**
     * Checks validity of numbers like adSpend, rate and inventory
     *
     * @param number only can be Long or Double values
     * @param field  the field int the POJO that is being validated
     * @param errors List of all errors to place in case there are any.
     */
    public void validateNumber(Number number, String field, Errors errors) {
        Long currentValue;
        if (number instanceof Double) {
            currentValue = (long) Math.floor(number.doubleValue());
        } else {
            currentValue = (Long) number;
        }
        if (currentValue > Constants.MAX_SAFE_INTEGER) {
            String message =
                    "Invalid " + field + ", it must be less than or equals to " + Constants.MAX_SAFE_INTEGER + ".";
            errors.rejectValue(field, ApiValidationUtils.TYPE_INVALID, message);
        }
    }

    /**
     * Validates all import fields related to a {@code Site}
     *
     * @param media  The {@code MediaRawDataView} to validate
     * @param errors The {@code Errors} object where all validation errors are collected
     */
    public void validateFieldsForImport(MediaRawDataView media, Errors errors) {
        // Placement Id
        if (media.getFieldsWithFormulaError()
                 .contains("placementId")) { //contains error in XLS formulas
            errors.rejectValue("placementId", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.placementId")));
        } else {
            if (StringUtils.isNotBlank(media.getPlacementId())) {
                try {
                    long id = Long.parseLong(media.getPlacementId().trim());
                    if (id < 0) {
                        errors.rejectValue("placementId", ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil.getString("global.error.importInvalidNumber",
                                        ResourceBundleUtil.getString("global.label.placementId"), 0,
                                        9));
                    } else {
                        media.setPlacementId(id + "");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("placementId", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.importInvalidNumber",
                                    ResourceBundleUtil.getString("global.label.placementId"), 0,
                                    9));
                }
            }
        }

        if (media.getFieldsWithFormulaError()
                 .contains("placementName")) { //contains error in XLS formulas
            errors.rejectValue("placementName", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.placementName")));
        } else {
            String placementName = media.getPlacementName();
            if (StringUtils.isNotBlank(placementName)) {
                String message = ResourceBundleUtil.getString("global.error.importFieldLength",
                        ResourceBundleUtil.getString("global.label.placementName"),
                        Constants.PLACEMENT_NAME_MAX_LENGTH);
                ApiValidationUtils
                        .rejectIfCharactersUpTo(errors, "placementName",
                                Constants.PLACEMENT_NAME_MAX_LENGTH, media.getPlacementName(),
                                message);
                if (!errors.hasFieldErrors("placementName")) {
                    media.setPlacementName(media.getPlacementName().trim());
                    media.setPlacementNameAutoGenerated(media.getPlacementName());
                }
            } else {
                // Add warning on autogenerated name to update placement
                if (media.getPlacementId() != null) {
                    errors.rejectValue("placementName", ApiValidationUtils.TYPE_IMPORT_WARNING,
                            ResourceBundleUtil.getString(
                                    "packagePlacementImport.warning.placementNameOnUpdate"));
                } else {
                    placementName = getAutogeneratedPlacementName(media);
                    media.setPlacementNameAutoGenerated(placementName);
                    if (media.getPlacementNameAutoGenerated()
                             .length() > Constants.PLACEMENT_NAME_MAX_LENGTH) {
                        media.setPlacementNameAutoGenerated(
                                media.getPlacementNameAutoGenerated().substring(0,
                                        Constants.PLACEMENT_NAME_MAX_LENGTH));
                        errors.rejectValue("placementName", ApiValidationUtils.TYPE_IMPORT_WARNING,
                                ResourceBundleUtil
                                        .getString(
                                                "packagePlacementImport.warning.autogeneratedName",
                                                Constants.PLACEMENT_NAME_MAX_LENGTH));
                    }
                }
            }
        }

        String message;
        if (media.getFieldsWithFormulaError()
                 .contains("extPlacementId")) { //contains error in XLS formulas
            errors.rejectValue("extPlacementId", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.extPlacementId")));
        } else {
            String extPlacementId = media.getExtPlacementId();
            message = ResourceBundleUtil.getString("global.error.importFieldLength",
                    ResourceBundleUtil.getString("global.label.extPlacementId"),
                    Constants.LARGE_CHARS_LENGTH);
            ApiValidationUtils
                    .rejectIfCharactersUpTo(errors, "extPlacementId", Constants.LARGE_CHARS_LENGTH,
                            extPlacementId, message);
            if (errors.getFieldError("extPlacementId") == null && StringUtils
                    .isNotEmpty(extPlacementId)) {
                media.setExtPlacementId(extPlacementId.trim());
            }
        }

        // ExtProps
        int i = 1;
        String placementProp = "placementProp";
        String placementPropLabel = ResourceBundleUtil.getString("global.label.placementProp");

        List<String> extProps = Arrays.asList(media.getPlacementProp1(), media.getPlacementProp2(),
                media.getPlacementProp3(), media.getPlacementProp4(), media.getPlacementProp5());
        for (; i <= 5; i++) {
            if (media.getFieldsWithFormulaError()
                     .contains(placementProp + i)) { //contains error in XLS formulas
                errors.rejectValue(placementProp + i, ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil
                                .getString("global.error.formulaError", placementPropLabel + i));
            } else {
                String placementPropValue = extProps.get(i - 1);
                message = ResourceBundleUtil
                        .getString("global.error.importFieldLength", placementPropLabel + i,
                                Constants.DEFAULT_CHARS_LENGTH);
                ApiValidationUtils.rejectIfCharactersUpTo(errors, placementProp + i,
                        Constants.DEFAULT_CHARS_LENGTH, placementPropValue, message);
                if (errors.getFieldError(placementProp + i) == null && StringUtils.isNotEmpty(
                        placementPropValue)) {
                    extProps.set(i - 1, placementPropValue.trim());
                }
            }
        }
        i = 0;
        media.setPlacementProp1(extProps.get(i++));
        media.setPlacementProp2(extProps.get(i++));
        media.setPlacementProp3(extProps.get(i++));
        media.setPlacementProp4(extProps.get(i++));
        media.setPlacementProp5(extProps.get(i));
    }

    private String getAutogeneratedPlacementName(MediaRawDataView view) {
        String _ = Constants.DASH_SPACED;
        String siteName = StringUtils.isNotEmpty(view.getSite()) ? view.getSite() : "";
        String sectionName = StringUtils.isNotEmpty(view.getSection()) ? view.getSection() : "";
        String adWidth = StringUtils.isNotEmpty(view.getAdWidth()) ? view.getAdWidth() : "";
        String adHeight = StringUtils.isNotEmpty(view.getAdHeight()) ? view.getAdHeight() : "";
        return new StringBuilder().append(siteName).append(_).append(sectionName).append(_)
                                  .append(adWidth).append('x').
                                          append(adHeight).toString();
    }
}

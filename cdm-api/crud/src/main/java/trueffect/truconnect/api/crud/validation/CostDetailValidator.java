package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.util.CostDetailComparator;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Validates that a give List of Cost Details for Packags and Placements have no errors.
 * Created by richard.jaldin on 10/8/2015.
 *
 * @author Richard Jaldin, Marcelo Heredia
 */
public class CostDetailValidator implements Validator{

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private CostDetailComparator costDetailComparator;

    public CostDetailValidator() {
        this.costDetailComparator = new CostDetailComparator();
    }

    public static Double getSafeMargin(Double margin){
        if (!(margin != null && margin <= Constants.COST_DETAIL_MARGIN_MAX
                && margin >= Constants.COST_DETAIL_MARGIN_MIN)) {
            return Constants.COST_DETAIL_MARGIN_MAX;
        }
        return margin;
    }

    public static Double getSafePlannedGrossAdSpend(Double plannedGrossAdSpend){
        if (plannedGrossAdSpend == null || plannedGrossAdSpend < Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE) {
            return Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE;
        }
        return plannedGrossAdSpend;
    }

    public static Double getSafePlannedGrossRate(Double plannedGrossRate){
        if (plannedGrossRate == null || plannedGrossRate < Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE) {
            return Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE;
        }
        return plannedGrossRate;
    }

    /**
     * Utility method to render a List of {@code CostDetail}
     * @param costDetails The List of {@code CostDetail} to render
     * @return A String representation of the List
     */
    public static String renderList(List<CostDetail> costDetails){
        if(costDetails == null || costDetails.isEmpty()) {
            return "";
        }
        String out = "";
        for(CostDetail costDetail: costDetails){
            out+=String.format("[%10s, %2$tF %2$tT, %3$tF %3$tT]\n",
                    costDetail.getId(),
                    costDetail.getStartDate(),
                    costDetail.getEndDate());
        }
        return out;
    }


    /**
     * Validates a {@code CostDetail} for update
     * @param o The Placement Object to validate
     * @param errors The Errors object to include the validation results
     */
    public void validate(Object o, Errors errors) {
        if(errors == null){
            throw new IllegalArgumentException("Errors cannot be null");
        }
        CostDetail costDetail = (CostDetail) o;
        ApiValidationUtils.rejectIfNull(errors, "startDate");
        ApiValidationUtils.rejectIfNull(errors, "endDate");
        validateCommon(errors, costDetail);
    }

    @Override
    public boolean supports(Class<?> type) {
        return type == CostDetail.class;
    }

    /**
     * Validates if a List of {@code CostDetail}s is valid
     * @param costDetails The List of {@code CostDetail}s to validate
     * @param errors The {@code Errors} object to attach the validation results
     */
    public void validateForUpdate(List<CostDetail> costDetails, Errors errors) {
        if(errors == null){
            throw new IllegalArgumentException("Errors cannot be null");
        }
        ApiValidationUtils.rejectIfNull(errors, "costDetails");
        ApiValidationUtils.rejectIfEmpty(errors, "costDetails");
        // Validates if the list of cost detail has been provided
        if (errors.hasErrors()) {
            return;
        }
        costDetails = fixHourMinuteSecond(costDetails);
        // Sort collection of CostsDetails
        Collections.sort(costDetails, costDetailComparator);

        log.debug("Validating List of CostDetails. Ordered collection: \n{}", renderList(costDetails));
        CostDetail first = costDetails.iterator().next();
        Calendar calendar = Calendar.getInstance();
        // Create an initial End Date to compare the first Cost Detail's Start Date
        Date previousEndDate;
        if(first != null && first.getStartDate() != null) {
            calendar.setTime(first.getStartDate());
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            previousEndDate = DateConverter.endDate(calendar.getTime());
        } else {
            // Throw error in case first Cost Detail, Start Date is null
            ApiValidationUtils.rejectIfNull(errors, "startDate");
            return;
        }

        // Get Last Cost Detail, if last end date is null,
        // and has an Id, temporary set a valid End Date
        CostDetail last = costDetails.get(costDetails.size() - 1);
        boolean removeLastEndDate = false;
        if(last.getEndDate() == null) {
            calendar.setTime(last.getStartDate());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            last.setEndDate(calendar.getTime());
            removeLastEndDate = true;
        }
        // Iterate over all Cost Details and validate individually
        int i = 0;
        for(CostDetail current : costDetails) {
            errors.pushNestedPath("costDetails[" + i + "]");
            ValidationUtils.invokeValidator(this, current, errors);
            errors.popNestedPath();
            i++;
        }
        // Stop validation as we need all this with no errors for the next validation round
        if (errors.hasErrors()) {
            return;
        }
        // Iterate over all Cost Details again to find any non-consecutive dates
        i = 0;
        for(CostDetail current : costDetails) {
            if (!DateConverter.datesConsecutive(current.getStartDate(), previousEndDate)) {
                errors.rejectValue("costDetails[" + i + "].startDate",
                        String.format("%1$tF %1$tT", current.getStartDate()),
                        ResourceBundleUtil.getString("ValidationCode.NONCONSECUTIVE_DATES",
                                current.getStartDate(), previousEndDate));
                return;
            }
            previousEndDate = current.getEndDate();
            i++;
        }
        // Revert back last date to null if needed
        if(removeLastEndDate) {
            last.setEndDate(null);
        }
    }

    /**
     * Cleans up a list of {@code List<CostDetail>} by adding HH:MM:SS section and sorting the list
     * of costs
     * @param costDetails
     * @return
     */
    public List<CostDetail> cleanCostDetails(List<CostDetail> costDetails){
        CostDetail costDetail;
        if(costDetails == null || costDetails.isEmpty()) {
            costDetail = new CostDetail();
            costDetails = new ArrayList<>();
        } else {
            costDetails = fixHourMinuteSecond(costDetails);
            // Sort collection of CostsDetails
            Collections.sort(costDetails, costDetailComparator);

            costDetail = costDetails.iterator().next();
            costDetails.clear();
        }
        costDetails.add(costDetail);
        return costDetails;
    }

    /**
     * Validates a {@code CostDetail} for Creation
     * @param costDetail The CostDetail to validate
     * @param errors The {@code Errors} object to attach the validation results
     */
    public void validateForCreate(CostDetail costDetail, Errors errors) {
        Date startDate, endDate;
        Calendar calendar =  Calendar.getInstance();

        // If no Start Date provided, set today as default
        if (costDetail.getStartDate() == null ) {
            startDate = new Date();
        } else {
            startDate = costDetail.getStartDate();
        }
        // If no End Date provided, set 30 days after Start Date as default
        if (costDetail.getEndDate() == null ) {
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_YEAR, Constants.COST_DETAIL_DEFAULT_NUMBER_OF_DAYS_AFTER_START_DATE);
            endDate = calendar.getTime();
        } else {
            endDate = costDetail.getEndDate();
        }

        costDetail.setStartDate(DateConverter.startDate(startDate));
        costDetail.setEndDate(DateConverter.endDate(endDate));

        validateCommon(errors, costDetail);
    }

    private void validateCommon(Errors errors, CostDetail costDetail) {
        ApiValidationUtils.rejectIfAfter(errors, "endDate", costDetail.getStartDate());
        // Check Margin
        if (costDetail.getMargin() == null) {
            costDetail.setMargin(Constants.COST_DETAIL_DEFAULT_MARGIN);
        }
        ApiValidationUtils.rejectIfLessThanNumber(errors, "margin", Constants.COST_DETAIL_MARGIN_MIN);
        ApiValidationUtils.rejectIfNumberGreaterThan(errors, "margin", Constants.COST_DETAIL_MARGIN_MAX);
        DecimalFormat decimalFormat = new DecimalFormat("#.##############");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        if(!errors.hasFieldErrors("margin")){
            costDetail.setMargin(Double.parseDouble(decimalFormat.format(costDetail.getMargin())));
        }

        // Check GrossAdSpend
        if (costDetail.getPlannedGrossAdSpend() == null) {
            costDetail.setPlannedGrossAdSpend(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE);
        }
        ApiValidationUtils.rejectIfLessThanNumber(errors, "plannedGrossAdSpend", Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE);
        // Check if plannedGrossAdSpend values doesn't have errors; if not, round it up
        if(!errors.hasFieldErrors("plannedGrossAdSpend")){
            costDetail.setPlannedGrossAdSpend(Double.parseDouble(decimalFormat.format(costDetail.getPlannedGrossAdSpend())));
        }
        // Check GrossRate is floating point number >= 0
        if (costDetail.getPlannedGrossRate() == null) {
            costDetail.setPlannedGrossRate(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE);
        }
        ApiValidationUtils.rejectIfLessThanNumber(errors, "plannedGrossRate", Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE);
        // Check if plannedGrossRate values doesn't have errors; if not, round it up
        if(!errors.hasFieldErrors("plannedGrossRate")){
            costDetail.setPlannedGrossRate(Double.parseDouble(decimalFormat.format(costDetail.getPlannedGrossRate())));
        }
        // Check Rate Type
        if (costDetail.getRateType() == null) {
            costDetail.setRateType(RateTypeEnum.CPM.getCode());
        } else {
            if (RateTypeEnum.typeOf(costDetail.getRateType()) == null) {
                errors.rejectValue("rateType",
                        ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("ValidationCode.UNSUPPORTED",
                                                    costDetail.getRateType()));
            }
        }
    }

    private List<CostDetail> fixHourMinuteSecond(List<CostDetail> costDetails) {
        // Reset hh:mm:ss portion of all start and end dates
        for (CostDetail costDetail : costDetails) {
            if (costDetail.getStartDate() != null) {
                costDetail.setStartDate(DateConverter.startDate(costDetail.getStartDate()));
            }
            if (costDetail.getEndDate() != null) {
                costDetail.setEndDate(DateConverter.endDate(costDetail.getEndDate()));
            }
        }
        return costDetails;
    }

    /**
     * Validates all import fields related to a {@code CostDetail}
     * @param media The {@code MediaRawDataView} to validate
     * @param errors The {@code Errors} object where all validation errors are collected
     */
    public void validateFieldsForImport(MediaRawDataView media, Errors errors) {

        Number plannedAdSpend = null;
        String label = ResourceBundleUtil.getString("global.label.plannedAdSpend");
        if(media.getFieldsWithFormulaError().contains("plannedAdSpend")) { //contains error in XLS formulas
            errors.rejectValue("plannedAdSpend", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError", label));
        } else {
            ApiValidationUtils
                    .rejectIfBlank(errors, "plannedAdSpend", ApiValidationUtils.TYPE_IMPORT_WARNING,
                            ResourceBundleUtil.getString("importExport.warning.requiredField",
                                    label, ResourceBundleUtil.getString("importExport.label.valueForYou")));
            if (!errors.hasFieldErrors("plannedAdSpend")) {
                String customMessage = ResourceBundleUtil
                        .getString("global.error.importInvalidDecimalFormat", label);
                plannedAdSpend = ApiValidationUtils
                        .parseNumberAs(errors, "plannedAdSpend", Double.class, customMessage);
                if (plannedAdSpend != null) {
                    if (plannedAdSpend.doubleValue() < 0.0) {
                        errors.rejectValue("plannedAdSpend", ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil
                                        .getString("global.error.importInvalidNegativeDecimal",
                                                label));
                    } else {
                        media.setPlannedAdSpend(media.getPlannedAdSpend().trim());
                    }
                }
            }
        }

        Number rate = null;
        label = ResourceBundleUtil.getString("global.label.rate");
        if (media.getFieldsWithFormulaError().contains("rate")) { //contains error in XLS formulas
            errors.rejectValue("rate", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.formulaError", label));
        } else {
            ApiValidationUtils.rejectIfBlank(errors, "rate", ApiValidationUtils.TYPE_IMPORT_WARNING,
                    ResourceBundleUtil.getString("importExport.warning.requiredField",
                            label, ResourceBundleUtil.getString("importExport.label.valueForYou")));
            if (!errors.hasFieldErrors("rate")) {
                String customMessage = ResourceBundleUtil
                        .getString("global.error.importInvalidDecimalFormat", label);
                rate = ApiValidationUtils
                        .parseNumberAs(errors, "rate", Double.class, customMessage);
                if (rate != null) {
                    if (rate.doubleValue() < 0.0) {
                        errors.rejectValue("rate", ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil
                                        .getString("global.error.importInvalidNegativeDecimal",
                                                label));
                    } else {
                        media.setRate(media.getRate().trim());
                    }
                }
            }
        }

        label = ResourceBundleUtil.getString("global.label.rateType");
        if(media.getFieldsWithFormulaError().contains("rateType")) { //contains error in XLS formulas
            errors.rejectValue("rateType", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.formulaError", label));
        } else {
            ApiValidationUtils.rejectIfBlank(errors, "rateType", ApiValidationUtils.TYPE_IMPORT_WARNING,
                    ResourceBundleUtil.getString("importExport.warning.requiredField",
                            label, ResourceBundleUtil.getString("importExport.label.typeForYou")));
            ApiValidationUtils.rejectIfNotInEnum(errors, "rateType", RateTypeEnum.class, true,
                    ResourceBundleUtil.getString("global.error.unsupportedValueForField", label,
                            media.getRateType()));
            if (!errors.hasFieldErrors("rateType")) {
                media.setRateType(media.getRateType().trim());
            }
        }

        applyDefaultsForImport(media);
        label = ResourceBundleUtil.getString("global.label.startDate");
        String message;
        Date startDate = null;
        if(media.getFieldsWithFormulaError().contains("startDate")) { //contains error in XLS formulas
            errors.rejectValue("startDate", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.formulaError", label));
        } else {
            ApiValidationUtils.rejectIfBlank(errors, "startDate", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.importRequiredField",
                            label));
            message = ResourceBundleUtil.getString("global.error.dateFormat", label);
            startDate = ApiValidationUtils.parseDate(errors, "startDate", message);
            if (startDate != null) {
                startDate = DateConverter.startDate(startDate);
                media.setStartDate(DateConverter.importExportFormat(startDate));
                if (startDate.before(Constants.MIN_DATE) || startDate.after(Constants.MAX_DATE)) {
                    errors.rejectValue("startDate",
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidDate",
                                    label,
                                    ResourceBundleUtil.getString("global.minDate"),
                                    ResourceBundleUtil.getString("global.maxDate")));
                    startDate = null;
                }
            }
        }

        label = ResourceBundleUtil.getString("global.label.endDate");
        if(media.getFieldsWithFormulaError().contains("endDate")) { //contains error in XLS formulas
            errors.rejectValue("endDate", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.formulaError", label));
        } else {
            ApiValidationUtils.rejectIfBlank(errors, "endDate", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.importRequiredField", label));
            message = ResourceBundleUtil.getString("global.error.dateFormat", label);
            Date endDate = ApiValidationUtils.parseDate(errors, "endDate", message);
            if (endDate != null) {
                endDate = DateConverter.endDate(endDate);
                media.setEndDate(DateConverter.importExportFormat(endDate));
                if (endDate.before(Constants.MIN_DATE) || endDate.after(Constants.MAX_DATE)) {
                    errors.rejectValue("endDate",
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidDate",
                                    label,
                                    ResourceBundleUtil.getString("global.minDate"),
                                    ResourceBundleUtil.getString("global.maxDate")));
                }
            }
            if (startDate != null && endDate != null) {
                ApiValidationUtils.rejectIfAfter(errors, "startDate", startDate, endDate);
            }
        }


        // Check inventory
        if (plannedAdSpend != null && rate != null
                && !errors.hasFieldErrors("rateType")) {
            try {
                PackagePlacementUtil.calculateInventory(rate.doubleValue(), plannedAdSpend.doubleValue(), media.getRateType());
            } catch (SystemException e) {
                errors.rejectValue("inventory", ApiValidationUtils.TYPE_INVALID, e.getMessage());
            }
        }
    }

    private void applyDefaultsForImport(MediaRawDataView media) {
        if (StringUtils.isBlank(media.getPlannedAdSpend())) {
            media.setPlannedAdSpend(String.valueOf(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE));
        }
        if (StringUtils.isBlank(media.getRate())) {
            media.setRate(String.valueOf(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE));
        }
        if (StringUtils.isBlank(media.getRateType())) {
            media.setRateType(RateTypeEnum.CPM.toString());
        }
    }
}

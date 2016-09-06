package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.CostDetailRawDataView;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Validates that the costs for a given {@code MediaRawDataView} are valid.
 *
 * @author Marcelo Heredia
 */
public class CostDetailImportValidator implements Validator {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CostDetailImportValidator() {
    }

    @Override
    public boolean supports(Class<?> type) {
        return type == MediaRawDataView.class;
    }

    /**
     * Validates a {@code CostDetailRawDataView} for create
     * @param costDetail The CostDetailRawDataView object to validate
     * @param errors The Errors object to include the validation results
     */
    private void validateSingleCostDetail(CostDetailRawDataView costDetail, Errors errors) {
        Number plannedAdSpend = null;
        String label = ResourceBundleUtil.getString("global.label.plannedAdSpend");
        if(costDetail.getFieldsWithFormulaError().contains("plannedAdSpend")) { //contains error in XLS formulas
            errors.rejectValue("plannedAdSpend", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError", label));
        } else {
            ApiValidationUtils
                    .rejectIfBlank(errors, "plannedAdSpend", ApiValidationUtils.TYPE_IMPORT_WARNING,
                            ResourceBundleUtil.getString("importExport.warning.requiredField",
                                    label,
                                    ResourceBundleUtil.getString("importExport.label.valueForYou")));
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
                        costDetail.setPlannedAdSpend(costDetail.getPlannedAdSpend().trim());
                    }
                }
            }
        }

        Number rate = null;
        label = ResourceBundleUtil.getString("global.label.rate");
        if (costDetail.getFieldsWithFormulaError().contains("rate")) { //contains error in XLS formulas
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
                        costDetail.setRate(costDetail.getRate().trim());
                    }
                }
            }
        }

        label = ResourceBundleUtil.getString("global.label.rateType");
        if(costDetail.getFieldsWithFormulaError().contains("rateType")) { //contains error in XLS formulas
            errors.rejectValue("rateType", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.formulaError", label));
        } else {
            ApiValidationUtils.rejectIfBlank(errors, "rateType", ApiValidationUtils.TYPE_IMPORT_WARNING,
                    ResourceBundleUtil.getString("importExport.warning.requiredField",
                            label, ResourceBundleUtil.getString("importExport.label.typeForYou")));
            ApiValidationUtils.rejectIfNotInEnum(errors, "rateType", RateTypeEnum.class, true,
                    ResourceBundleUtil.getString("global.error.unsupportedValueForField", label,
                            costDetail.getRateType()));
            if (!errors.hasFieldErrors("rateType")) {
                costDetail.setRateType(costDetail.getRateType().trim());
            }
        }

        applyDefaultsForImport(costDetail);

        label = ResourceBundleUtil.getString("global.label.startDate");
        String message;
        Date startDate = null;
        if(costDetail.getFieldsWithFormulaError().contains("startDate")) { //contains error in XLS formulas
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
        if(costDetail.getFieldsWithFormulaError().contains("endDate")) { //contains error in XLS formulas
            errors.rejectValue("endDate", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.formulaError", label));
        } else {
            ApiValidationUtils.rejectIfBlank(errors, "endDate", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.importRequiredField", label));
            message = ResourceBundleUtil.getString("global.error.dateFormat", label);
            Date endDate = ApiValidationUtils.parseDate(errors, "endDate", message);
            if (endDate != null) {
                endDate = DateConverter.endDate(endDate);
                if (endDate.before(Constants.MIN_DATE) || endDate.after(Constants.MAX_DATE)) {
                    errors.rejectValue("endDate",
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidDate",
                                    label,
                                    ResourceBundleUtil.getString("global.minDate"),
                                    ResourceBundleUtil.getString("global.maxDate")));
                    endDate = null;
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
                PackagePlacementUtil.calculateInventory(rate.doubleValue(), plannedAdSpend.doubleValue(), costDetail.getRateType());
            } catch (SystemException e) {
                errors.rejectValue("inventory", ApiValidationUtils.TYPE_INVALID, e.getMessage());
            }
        }
    }

    private void applyDefaultsForImport(CostDetailRawDataView costDetail) {
        if (StringUtils.isBlank(costDetail.getPlannedAdSpend())) {
            costDetail.setPlannedAdSpendAutoGenerated(String.valueOf(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE));
        }
        if (StringUtils.isBlank(costDetail.getRate())) {
            costDetail.setRateAutoGenerated(String.valueOf(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE));
        }
        if (StringUtils.isBlank(costDetail.getRateType())) {
            costDetail.setRateTypeAutoGenerated(RateTypeEnum.CPM.toString());
        }
    }

    /**
     * Validates that all {@code CostDetailRawDataView} from a given {@code MediaRawDataView}
     * @param o The {@code MediaRawDataView} that contains the costs to validate
     */
    @Override
    public void validate(Object o, Errors errors) {
        if(errors == null){
            throw new IllegalArgumentException("Errors cannot be null");
        }
        if(o == null){
            throw new IllegalArgumentException("MediaRawDataView cannot be null");
        }
        MediaRawDataView row = (MediaRawDataView) o;
        List<CostDetailRawDataView> costs = row.getCostDetails();
        if(costs.isEmpty()) {
            // TODO put error here.
            return;
        }
        boolean rowHasCostDetailsInError = false;
        CostDetailRawDataView first = costs.iterator().next();
        Calendar calendar = Calendar.getInstance();
        Date previousEndDate = null;
        boolean performConsecutiveDatesCheck = true;
        // Create an initial End Date to compare the first Cost Detail's Start Date
        if(first != null && first.getStartDateDt() != null) {
            calendar.setTime(first.getStartDateDt());
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            previousEndDate = DateConverter.endDate(calendar.getTime());
        } else {
            performConsecutiveDatesCheck = false;
        }

        // Get Last Cost Detail, if last end date is null,
        // and has an Id, temporary set a valid End Date
        CostDetailRawDataView last = costs.get(costs.size() - 1);
        boolean removeLastEndDate = false;
        if(StringUtils.isBlank(last.getEndDate())) {
            if(last.getStartDateDt() != null) {
                calendar.setTime(last.getStartDateDt());
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                last.setEndDate(DateConverter.importExportFormat(calendar.getTime()));
            } else {
                last.setEndDate(DateConverter.importExportFormat(new Date()));
            }
            removeLastEndDate = true;
        }

        BeanPropertyBindingResult cErrors;
        for(CostDetailRawDataView costDetail : costs) {
            cErrors = new BeanPropertyBindingResult(costDetail, CostDetailRawDataView.class.getSimpleName());
            validateSingleCostDetail(costDetail, cErrors);
            MediaRawDataViewValidator.copyErrors(costDetail, cErrors);
            rowHasCostDetailsInError |= !costDetail.getErrors().isEmpty();
        }

        // Iterate over all Cost Details again to find any non-consecutive dates
        int i = 0;
        if(performConsecutiveDatesCheck) {
            for(CostDetailRawDataView current : costs) {
                if (!DateConverter.datesConsecutive(current.getStartDateDt(), previousEndDate)) {
                    cErrors = new BeanPropertyBindingResult(current, CostDetailRawDataView.class.getSimpleName());
                    cErrors.rejectValue("startDate",
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("ValidationCode.NONCONSECUTIVE_DATES",
                                    current.getStartDateDt(), previousEndDate));
                    MediaRawDataViewValidator.copyErrors(current, cErrors);
                    rowHasCostDetailsInError |= true;
                    break;
                }
                previousEndDate = current.getEndDateDt();
                i++;
            }
        }
        // Revert back last date to null if needed
        if(removeLastEndDate) {
            last.setEndDate(null);
        }
        // If at least one Cost Detail in error:
        if(rowHasCostDetailsInError) {
            errors.rejectValue("costDetails",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("packagePlacementImport.error.costDetailsInError"));
        }
    }
}

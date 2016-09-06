package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates an Insertion Order.
 *
 * The default validation is done for IO creation Created by marcelo.heredia on
 * 4/25/2016.
 *
 * @author Marcelo Heredia
 */
public class InsertionOrderValidator implements Validator {

    @Override
    public boolean supports(Class<?> type) {
        return type == InsertionOrder.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", ResourceBundleUtil.getString("insertionOrder.label.io"));
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "notes", Constants.MAX_INSERTION_ORDER_NOTES_LENGHT);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "name", Constants.MAX_INSERTION_ORDER_NAME_LENGHT);
        ApiValidationUtils.rejectIfOutOfRangeOrNotNumber(errors, "ioNumber",
                (long) Constants.INSERTION_ORDER_NUMBER_MIN_VALUE,
                (long) Constants.INSERTION_ORDER_NUMBER_MAX_VALUE, null);
    }

    /**
     * Validates import fields related to a {@code InsertionOrder}
     * <p>
     * Use this in bulk import process
     * </p>
     *
     * @param media The {@code MediaRawDataView} to validate
     * @param errors The {@code Errors} object where all validation errors are
     * collected
     */
    public void validateFieldsForImport(MediaRawDataView media, Errors errors) {
        if(media.getFieldsWithFormulaError().contains("orderName")) { //contains error in XLS formulas
            errors.rejectValue("orderName", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.orderName")));
        } else {
            String orderName = media.getOrderName();
            ApiValidationUtils.rejectIfBlank(errors, "orderName", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.importRequiredField",
                            ResourceBundleUtil.getString("global.label.orderName")));
            String message = ResourceBundleUtil.getString("global.error.importFieldLength",
                    ResourceBundleUtil.getString("global.label.orderName"),
                    Constants.MAX_INSERTION_ORDER_NAME_LENGHT);
            ApiValidationUtils.rejectIfCharactersUpTo(errors, "orderName",
                    Constants.MAX_INSERTION_ORDER_NAME_LENGHT, orderName, message);
            if (!errors.hasFieldErrors("orderName")) {
                media.setOrderName(orderName.trim());
            }
        }

        if(media.getFieldsWithFormulaError().contains("orderNumber")) { //contains error in XLS formulas
            errors.rejectValue("orderNumber", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.orderNumber")));
        } else {
            String orderNumber = media.getOrderNumber();
            String orderNumberLabel = ResourceBundleUtil.getString("global.label.orderNumber");
            ApiValidationUtils.rejectIfBlank(errors, "orderNumber", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil
                            .getString("global.error.importRequiredField", orderNumberLabel));
            int min = Constants.INSERTION_ORDER_NUMBER_MIN_VALUE;
            int max = Constants.INSERTION_ORDER_NUMBER_MAX_VALUE;
            String customMessage = ResourceBundleUtil
                    .getString("global.error.importInvalidNumber", orderNumberLabel, 0,
                            String.valueOf(max));
            Number number = ApiValidationUtils.parseNumberAs(errors, "orderNumber", Long.class,
                    customMessage);
            if (number != null) {
                // Check if negative
                if (number.longValue() <= min) {
                    errors.rejectValue("orderNumber", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.importInvalidNegativeNumber",
                                    orderNumberLabel));
                } else {
                    ApiValidationUtils
                            .rejectIfNumberGreaterThan(errors, "orderNumber", number, (long) max,
                                    Long.class, customMessage);
                }
            }
            if (!errors.hasFieldErrors("orderNumber")) {
                media.setOrderNumber(orderNumber.trim());
            }
        }
    }
}

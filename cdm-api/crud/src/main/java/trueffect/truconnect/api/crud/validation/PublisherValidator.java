package trueffect.truconnect.api.crud.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.util.AdminPhone;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

/**
 *
 * @author Gustavo Claure
 */
public class PublisherValidator implements Validator{

    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == Publisher.class;
    }

    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Publisher");
        validateCommons(o, errors);
    }

    public void validate(Object o, Long id, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "Publisher");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        validateCommons(o, errors);
    }

    private void validateCommons(Object o, Errors errors) {
        Publisher publisher = (Publisher) o;
        applyDefaults(publisher);
        ApiValidationUtils.rejectIfBlank(errors, "agencyId");
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "name", Constants.MEDIUM_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "address1", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "address2", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "url", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "city", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "state", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "zipCode", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "country", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "phoneNumber",
                Constants.DEFAULT_CHARS_LENGTH);
        if (!StringUtils.isBlank(publisher.getPhoneNumber()) && !AdminPhone.validatePhone(publisher.getPhoneNumber())) {
            errors.rejectValue("phoneNumber", ApiValidationUtils.TYPE_INVALID,
                    "Invalid Phone Number: " + publisher.getPhoneNumber());
        }
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "agencyNotes",
                Constants.X_LARGE_CHARS_LENGTH);
    }

    /**
     * Validates import fields related to a {@code Publisher}
     * <p>
     *     Use this in bulk import process
     * </p>
     * @param media The {@code MediaRawDataView} to validate
     * @param errors The {@code Errors} object where all validation errors are collected
     */
    public void validateFieldsForImport(MediaRawDataView media, Errors errors){
        if(media.getFieldsWithFormulaError().contains("publisher")) { //contains error in XLS formulas
            errors.rejectValue("publisher", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.publisher")));
        } else {
            String publisher = media.getPublisher();
            ApiValidationUtils.rejectIfBlank(errors, "publisher", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.importRequiredField",
                            ResourceBundleUtil.getString("global.label.publisher")));
            String message = ResourceBundleUtil.getString("global.error.importFieldLength",
                    ResourceBundleUtil.getString("global.label.publisherName"),
                    Constants.MEDIUM_CHARS_LENGTH);
            ApiValidationUtils
                    .rejectIfCharactersUpTo(errors, "publisher", Constants.MEDIUM_CHARS_LENGTH,
                            media.getPublisher(), message);
            if (!errors.hasFieldErrors("publisher")) {
                media.setPublisher(publisher.trim());
            }
        }
    }

    public Publisher applyDefaults(Publisher publisher) {
        if(StringUtils.isBlank(publisher.getZipCode())) {
            publisher.setZipCode(Constants.DEFAULT_ZIP_CODE);
        }
        return publisher;
    }
}

package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;

/**
 *
 * @author Rambert Rioja
 */
public class AdvertiserValidator implements Validator {

    @Override
    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == Advertiser.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Advertiser");
        ApiValidationUtils.rejectIfBlank(errors, "agencyId");
        validateCommons(o, errors);
    }

    public void validate(Long id, Object o, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "Advertiser");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        ApiValidationUtils.rejectIfDoesNotContain(errors, "isHidden", Arrays.asList("Y", "N"));
        validateCommons(o, errors);
    }

    private void validateCommons(Object o, Errors errors) {
        ApiValidationUtils.rejectIfBlank(errors, "enableHtmlTag");
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "name", 100);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "address1", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "address2", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "city", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "state", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "zipCode", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "country", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "phoneNumber", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "faxNumber", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "url", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "contactDefault", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "notes", 2000);
    }
}

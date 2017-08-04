package trueffect.truconnect.api.crud.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;

/**
 *
 * @author Gustavo Claure
 */
public class AgencyValidator implements Validator {

    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == Agency.class;
    }

    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Agency");
        validateCommons(o, errors);
    }

    public void validate(Object o, Long id, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "Agency");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        validateCommons(o, errors);
    }

    private void validateCommons(Object o, Errors errors) {
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "name", 200);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "address1", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "address2", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "url", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "city", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "state", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "zipCode", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "country", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "phoneNumber", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "faxNumber", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "notes", 2000);
    }
}

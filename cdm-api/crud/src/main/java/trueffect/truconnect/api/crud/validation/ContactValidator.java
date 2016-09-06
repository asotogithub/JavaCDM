package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Abel Soto
 */
public class ContactValidator implements Validator {

    @Override
    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == Contact.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Contact");
        validateCommons(o, errors);
    }

    public void validateToUpdate(Long id, Object o, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "Contact");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        validateCommons(o, errors);
    }

    private void validateCommons(Object o, Errors errors) {
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "lastName", 200);
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "firstName", 200);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "notes", 2000);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "email", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "phone", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "fax", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "address1", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "address2", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "city", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "state", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "zip", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "country", 256);
    }
}

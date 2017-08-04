package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;


/**
 *
 * @author Abel Soto
 */
public class BrandValidator implements Validator {

    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == Brand.class;
    }
    
    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Brand");
        validateCommons(o, errors);
    }

    public void validate(Object o, Long id, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "Brand");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        ApiValidationUtils.rejectIfDoesNotContain(errors, "isHidden", Arrays.asList("Y", "N"));
        validateCommons(o, errors);
    }

    private void validateCommons(Object o, Errors errors) {
        ApiValidationUtils.rejectIfNull(errors, "advertiserId");
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "name", 100);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "description", 256);
    }
}

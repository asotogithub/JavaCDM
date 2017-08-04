package trueffect.truconnect.api.commons.exceptions;

import org.springframework.validation.BeanPropertyBindingResult;

/**
 *
 * @author Rambert Rioja
 */
public class ValidationException extends Exception {

    private BeanPropertyBindingResult errors;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(BeanPropertyBindingResult errors) {
        super(errors.getAllErrors().toString());
        this.errors = errors;
    }
    
    public BeanPropertyBindingResult getErrors() {
        return errors;
    }
}

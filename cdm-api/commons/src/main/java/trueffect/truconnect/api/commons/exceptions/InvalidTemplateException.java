package trueffect.truconnect.api.commons.exceptions;

/**
 * Checked Exception to be thrown when a Template doesn't have the expected structure
 * Created by marcelo.heredia on 12/18/2015.
 * @author Marcelo Heredia
 */
public class InvalidTemplateException extends Exception{

    public InvalidTemplateException(String message) {
        super(message);
    }

    public InvalidTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}

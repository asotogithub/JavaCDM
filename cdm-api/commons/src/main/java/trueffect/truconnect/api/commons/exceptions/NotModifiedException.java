package trueffect.truconnect.api.commons.exceptions;

/**
 *
 * @author Richard Jaldin
 */
@SuppressWarnings("serial")
public class NotModifiedException extends ProxyException {
    
    public NotModifiedException(String message) {
        super(message);
    }
}

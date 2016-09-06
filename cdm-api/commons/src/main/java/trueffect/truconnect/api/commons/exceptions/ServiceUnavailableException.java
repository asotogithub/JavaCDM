package trueffect.truconnect.api.commons.exceptions;

/**
 *
 * @author Richard Jaldin
 */
@SuppressWarnings("serial")
public class ServiceUnavailableException extends ProxyException {
    
    public ServiceUnavailableException(String message) {
        super(message);
    }
}

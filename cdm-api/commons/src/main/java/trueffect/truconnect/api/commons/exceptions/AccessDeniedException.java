package trueffect.truconnect.api.commons.exceptions;

/**
 *
 * @author Rambert Rioja
 */
@SuppressWarnings("serial")
public class AccessDeniedException extends ProxyException {

    private static String message = "Access to the requested data is denied. ";

    public AccessDeniedException() {
        super(AccessDeniedException.message);
    }

    public AccessDeniedException(String message) {
        super(AccessDeniedException.message + message);
    }
}

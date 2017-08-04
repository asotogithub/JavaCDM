package trueffect.truconnect.api.tpasapi.client.exceptions;

/**
 *
 * @author Rambert Rioja
 */
@SuppressWarnings("serial")
public class AccessDeniedException extends Exception {

    public AccessDeniedException(String message) {
        super(message);
    }
}

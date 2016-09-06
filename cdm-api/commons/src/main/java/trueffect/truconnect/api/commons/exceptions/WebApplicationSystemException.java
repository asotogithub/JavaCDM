package trueffect.truconnect.api.commons.exceptions;

import javax.ws.rs.WebApplicationException;

/**
 * Subclass of {@code WebApplicationException} used to wrap a {@link trueffect.truconnect.api.commons.exceptions.SystemException}
 * Created by marcelo.heredia on 6/1/2015.
 * <p>
 * Once migrating to Jersey 2.x, there should be no need to wrap a {@code SystemException} using this class. This is necessary now due
 * to Jersey Bug: https://java.net/jira/browse/JERSEY-920
 * <p>
 *
 * More details why I'm using this solution: https://java.net/projects/jersey/lists/users/archive/2012-01/message/147
 * @author Marcelo Heredia
 */
public class WebApplicationSystemException extends WebApplicationException {
    /**
     * Builds a {@code WebApplicationSystemException} given a {@code Throwable} cause
     * @param cause The {@code Throwable} cause to be wrapped
     */
    public WebApplicationSystemException(Throwable cause) {
        super(cause);
    }
}

package trueffect.truconnect.api.crud;

import java.net.URI;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import trueffect.truconnect.api.commons.APIResponse;

/**
 *
 * @author Rambert Rioja
 */
public class CRUDResponse {

    public static URI getLocation(UriInfo uriInfo, String path) {
        return uriInfo.getBaseUriBuilder().path(path).build("");
    }

    public static Response ok() {
        return APIResponse.ok().build();
    }

    public static Response ok(Object object) {
        return APIResponse.ok(object).build();
    }

    public static Response created(Object object, UriInfo uriInfo, String path) {
        return APIResponse.created(object, uriInfo, path).build();
    }

    public static Response created(Object object) {
        return APIResponse.created(object).build();
    }

    public static Response bad(String message) {
        return APIResponse.bad(message).build();
    }

    public static Response notFound(String message) {
        return APIResponse.notFound(message).build();
    }

    public static Response conflict(String message) {
        return APIResponse.conflict(message);
    }

    public static Response conflict(String message, UriInfo uriInfo, String path) {
        return APIResponse.conflict(message, uriInfo, path).build();
    }

    public static Response forbidden(String message) {
        return APIResponse.forbidden(message);
    }

    public static Response error(String message) {
        return APIResponse.error(message);
    }

    public static Response notAcceptable(String message) {
        return APIResponse.notAcceptable(message);
    }
}
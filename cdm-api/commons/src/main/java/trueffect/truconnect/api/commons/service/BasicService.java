package trueffect.truconnect.api.commons.service;

import java.net.URI;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import com.sun.jersey.api.client.ClientResponse;

/**
 *
 * @author Rambert Rioja
 */
public abstract class BasicService {

    @Context
    protected UriInfo uriInfo;
    @Context
    protected HttpHeaders headers;
    protected ClientResponse response;

    public BasicService() {    }

    protected URI getLocation(String path) {
        return uriInfo.getBaseUriBuilder().path(path).build("");
    }
}

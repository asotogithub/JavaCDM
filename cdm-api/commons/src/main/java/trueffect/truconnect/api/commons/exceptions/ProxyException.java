package trueffect.truconnect.api.commons.exceptions;

import java.util.List;
import java.util.Map.Entry;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import com.sun.jersey.api.client.ClientResponse;
import trueffect.truconnect.api.commons.model.Errors;

/**
 *
 * @author Rambert Rioja
 */
@SuppressWarnings("serial")
public class ProxyException extends Exception {

    private ClientResponse response;
    private Errors errors;

    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, ClientResponse response) {
        super(message);
        this.response = response;
    }

    public ProxyException(Errors errors, ClientResponse response) {
        super(errors.toString());
        this.errors = errors;
        this.response = response;
    }

    public void copyHeaders(ResponseBuilder builder) {
        try {
            for (Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
                for (String value : entry.getValue()) {
                    if (!"Content-Length".equalsIgnoreCase(entry.getKey())) {
                        builder.header(entry.getKey(), value);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public int getStatus() {
        return response.getStatus();
    }


    public void setHeader(String header, List<String> values) {
        response.getHeaders().put(header, values);
    }

    public Response getErrorResponse() {
        ResponseBuilder builder = Response.status(response.getStatus());
        copyHeaders(builder);
        builder.entity(errors);
        return builder.build();
    }
}

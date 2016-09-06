package trueffect.truconnect.api.tpasapi.proxy;

import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import trueffect.truconnect.api.tpasapi.model.Errors;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Trafficking;

/**
 *
 * @author Abel Soto
 */
public class TraffickingConsumerProxy extends BaseProxy<Campaign> {

    public TraffickingConsumerProxy(HttpHeaders headers) {
        super(Campaign.class, headers);
        path("Trafficking");
    }

    public Response saveTrafficking(Trafficking trafficking) throws ProxyException {
        ClientResponse response = header().header("statusId", "1")
                .post(ClientResponse.class, trafficking);
        Response.ResponseBuilder builder = Response.status(response.getStatus());
        copyHeaders(builder);
        this.setLastResponse(response);
        log.info("Response status code: " + response.getStatus());
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                builder.entity(response.getEntity(Errors.class));
            } else {
                handleErrors(response);
            }
        }
        return builder.build();
    }
}

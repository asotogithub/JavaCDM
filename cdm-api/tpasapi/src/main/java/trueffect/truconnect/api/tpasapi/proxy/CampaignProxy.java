package trueffect.truconnect.api.tpasapi.proxy;

import java.io.InputStream;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.factory.CreativeFactory;
import trueffect.truconnect.api.commons.model.Campaign;

/**
 *
 * @author Abel Soto
 */
public class CampaignProxy extends BaseProxy<Campaign> {

    public CampaignProxy(HttpHeaders headers) {
        super(Campaign.class, headers);
        path("Campaigns");
    }

    public Creative saveCreativeImage(InputStream fileInStream, String filename) throws Exception {
        String disposition = "attachment; filename=\"" + filename + "\"";
        WebResource.Builder request = header().header("statusId", "1")
                .header("Content-Disposition", disposition).type(MediaType.APPLICATION_OCTET_STREAM)
                .accept(getContentType());
        ClientResponse response = request.post(ClientResponse.class, fileInStream);

        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            handleErrors(response);
        }
        trueffect.truconnect.api.commons.model.Creative creative;
        creative = getCreativeEntity(response);
        return CreativeFactory.createTpasapiObject(creative);
    }

    private trueffect.truconnect.api.commons.model.Creative getCreativeEntity(ClientResponse response) throws Exception {
        if (getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, trueffect.truconnect.api.commons.model.Creative.class);
        }
        return response.getEntity(trueffect.truconnect.api.commons.model.Creative.class);
    }
}

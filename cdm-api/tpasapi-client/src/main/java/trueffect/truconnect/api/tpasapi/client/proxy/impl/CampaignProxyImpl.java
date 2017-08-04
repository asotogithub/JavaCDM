package trueffect.truconnect.api.tpasapi.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.tpasapi.model.Campaign;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.client.base.ContentType;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.tpasapi.client.proxy.AuthenticationTpasapiServiceProxy;

import java.io.InputStream;
import javax.ws.rs.core.MediaType;

public class CampaignProxyImpl extends ServiceProxyImpl<Campaign> {

    public CampaignProxyImpl(Class<Campaign> type, String baseUrl, String servicePath, AuthenticationTpasapiServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public Creative saveCreativeFile(Long id, InputStream inputStream, String filename) throws Exception {
        path(id.toString());
        path("Creative");
        query("filename", filename);
        String disposition = "attachment; filename=\"" + filename + "\"";
        ClientResponse response = header().header("statusId", "1").accept(getContentType().getType()).type(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", disposition)
                .post(ClientResponse.class, inputStream);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    Creative result = saveCreativeFile(id, inputStream, filename);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getCreativeEntity(response);
    }

    private Creative getCreativeEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, Creative.class);
        }
        return response.getEntity(Creative.class);
    }
}

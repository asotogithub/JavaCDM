package trueffect.truconnect.api.tpasapi.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.commons.model.Trafficking;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.tpasapi.client.proxy.AuthenticationTpasapiServiceProxy;

public class TraffickingProxyImpl extends ServiceProxyImpl<Trafficking> {

    public TraffickingProxyImpl(Class<Trafficking> type, String baseUrl, String servicePath, AuthenticationTpasapiServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public boolean trafficCampaignAdjustTimeZone(Trafficking traffickInput) throws Exception {
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, getPostData(traffickInput));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    boolean result = trafficCampaignAdjustTimeZone(traffickInput);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return true;
    }
}

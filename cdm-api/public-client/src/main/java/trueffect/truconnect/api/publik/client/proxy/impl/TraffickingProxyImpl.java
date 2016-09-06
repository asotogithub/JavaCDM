package trueffect.truconnect.api.publik.client.proxy.impl;

import trueffect.truconnect.api.commons.model.Trafficking;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;

public class TraffickingProxyImpl extends ServiceProxyImpl<Trafficking> {

    public TraffickingProxyImpl(Class<Trafficking> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
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
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
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

    public boolean trafficCampaignMock(Trafficking traffickInput) throws Exception {
        path("test");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, getPostData(traffickInput));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
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

    public boolean validate(Long campaignId) throws Exception {
        path("validate");
        query("campaignId", String.valueOf(campaignId));
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    boolean result = validate(campaignId);
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

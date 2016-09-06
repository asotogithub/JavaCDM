package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.MediaBuyCampaign;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

public class MediaBuyProxyImpl extends ServiceProxyImpl<MediaBuy> {

    public MediaBuyProxyImpl(Class<MediaBuy> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public MediaBuy getByCampaign(Long campaignId) throws Exception {
        path("byCampaign");
        path(campaignId.toString());
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
                    MediaBuy result = getByCampaign(campaignId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntity(response);
    }

    public MediaBuyCampaign saveMediaBuyCampaign(MediaBuyCampaign mediaBuyCampaign) throws Exception {
        path("campaign");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, getPostData(mediaBuyCampaign));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    MediaBuyCampaign result = saveMediaBuyCampaign(mediaBuyCampaign);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getMediaBuyCampaignEntity(response);
    }

    private MediaBuyCampaign getMediaBuyCampaignEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, MediaBuyCampaign.class);
        }
        return response.getEntity(MediaBuyCampaign.class);
    }
}

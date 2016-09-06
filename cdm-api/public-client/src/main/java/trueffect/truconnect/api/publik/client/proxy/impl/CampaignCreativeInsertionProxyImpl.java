package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.dto.BulkCreativeInsertion;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

public class CampaignCreativeInsertionProxyImpl extends ServiceProxyImpl<CreativeInsertion> {

    public CampaignCreativeInsertionProxyImpl(Class<CreativeInsertion> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public BulkCreativeInsertion createWithCiBulk(Long campaignId, Object input) throws Exception {
        path(campaignId.toString());
        path("bulkCreativeInsertion");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, getPostData(input));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    BulkCreativeInsertion result = createWithCiBulk(campaignId,input);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getBulkCreativeInsertionEntity(response);
    }

    private BulkCreativeInsertion getBulkCreativeInsertionEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, BulkCreativeInsertion.class);
        }
        return response.getEntity(BulkCreativeInsertion.class);
    }
}

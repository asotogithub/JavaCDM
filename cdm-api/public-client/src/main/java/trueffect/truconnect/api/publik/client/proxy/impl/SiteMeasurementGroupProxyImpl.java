package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;
import trueffect.truconnect.api.commons.model.BooleanResponse;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

/**
 * Created by thomas.barjou on 6/5/2015.
 */
public class SiteMeasurementGroupProxyImpl extends ServiceProxyImpl<SmGroup> {

    public SiteMeasurementGroupProxyImpl(Class<SmGroup> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public BooleanResponse verifyGroupNameExist(String smId, String name) throws Exception {
        if (smId != null) {
            query("smId", smId);
        }
        if (name != null) {
            query("name", name);
        }
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
                    BooleanResponse result = verifyGroupNameExist(smId, name);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return response.getEntity(BooleanResponse.class);
    }

    @Override
     public void handleErrors(ClientResponse response) throws Exception {
        super.handleNewErrors(response);
    }
}

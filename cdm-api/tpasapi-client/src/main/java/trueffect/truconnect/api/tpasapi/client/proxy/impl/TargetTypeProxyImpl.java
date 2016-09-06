package trueffect.truconnect.api.tpasapi.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.TargetType;
import trueffect.truconnect.api.tpasapi.model.TargetValue;
import trueffect.truconnect.api.tpasapi.client.base.ContentType;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.tpasapi.client.proxy.AuthenticationTpasapiServiceProxy;

public class TargetTypeProxyImpl extends ServiceProxyImpl<TargetType> {

    public TargetTypeProxyImpl(Class<TargetType> type, String baseUrl, String servicePath, AuthenticationTpasapiServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public RecordSet<TargetType> getTargetTypes() throws Exception {
        return find(null, null, null);
    }

    public RecordSet<TargetValue> getTargetValues(String code) throws Exception {
        path(code);
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<TargetValue> result = getTargetValues(code);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getTargetValueEntities(response);
    }

    @SuppressWarnings("unchecked")
    private RecordSet<TargetValue> getTargetValueEntities(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }
}

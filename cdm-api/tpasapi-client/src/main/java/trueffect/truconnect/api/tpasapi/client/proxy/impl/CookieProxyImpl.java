package trueffect.truconnect.api.tpasapi.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.tpasapi.model.Cookie;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.client.base.ContentType;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.tpasapi.client.proxy.AuthenticationTpasapiServiceProxy;

public class CookieProxyImpl extends ServiceProxyImpl<Cookie> {

    public CookieProxyImpl(Class<Cookie> type, String baseUrl, String servicePath, AuthenticationTpasapiServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public RecordSet<Cookie> getByDomainId(Long id) throws Exception {
        path(id.toString());
        path("cookies");
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
                    RecordSet<Cookie> result = getByDomainId(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getResponseEntity(response);
    }

    private RecordSet<Cookie> getResponseEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }
}

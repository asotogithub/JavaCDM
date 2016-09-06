package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.commons.model.CookieTargetTemplate;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

public class CookieTargetTemplateProxyImpl extends ServiceProxyImpl<CookieTargetTemplate> {

    public CookieTargetTemplateProxyImpl(Class<CookieTargetTemplate> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }
    
    public CookieTargetTemplate saveCookieTargetTemplate(Long cookieDomainId, CookieTargetTemplate cookie) throws Exception {
        path(cookieDomainId.toString());
        path("cookie");
        return this.save(cookie);
    }
    
    public RecordSet<CookieTargetTemplate> getByDomainId(Long id) throws Exception {
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
                    RecordSet<CookieTargetTemplate> result = getByDomainId(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getCookieTargetTemplateEntity(response);
    }
    
    private RecordSet<CookieTargetTemplate> getCookieTargetTemplateEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }
}

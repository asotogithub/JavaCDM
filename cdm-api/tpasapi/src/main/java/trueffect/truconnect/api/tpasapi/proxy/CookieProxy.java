package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.tpasapi.factory.CookieFactory;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.tpasapi.model.Cookie;
import trueffect.truconnect.api.tpasapi.model.RecordSet;

/**
 *
 * @author Richard Jaldin
 */
public class CookieProxy extends BaseProxy<trueffect.truconnect.api.commons.model.CookieTargetTemplate> {

    public CookieProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.CookieTargetTemplate.class, headers);
        path("CookieDomains");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public RecordSet<Cookie> getCookiesByDomain(Long cookieDomainId) throws ProxyException {
        path(cookieDomainId.toString());
        path("cookies");
        ClientResponse response = header().get(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }

        trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.CookieTargetTemplate> records
                = (trueffect.truconnect.api.commons.model.RecordSet) response.getEntity(trueffect.truconnect.api.commons.model.RecordSet.class);

        RecordSet<Cookie> result = CookieFactory.createTpasapiObjects(records);
        return result;
    }
}

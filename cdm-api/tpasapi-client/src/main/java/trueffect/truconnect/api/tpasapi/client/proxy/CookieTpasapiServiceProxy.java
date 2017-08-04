package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.tpasapi.model.Cookie;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.client.proxy.impl.CookieProxyImpl;

public class CookieTpasapiServiceProxy extends GenericTpasapiServiceProxy<Cookie> {

    public CookieTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Domains", contentType, userName, password);
    }

    public RecordSet<Cookie> getByDomainId(Long domainId) throws Exception {
        CookieProxyImpl proxy = getProxy();
        return proxy.getByDomainId(domainId);
    }
    
    protected CookieProxyImpl getProxy() {
        CookieProxyImpl proxy = new CookieProxyImpl(Cookie.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

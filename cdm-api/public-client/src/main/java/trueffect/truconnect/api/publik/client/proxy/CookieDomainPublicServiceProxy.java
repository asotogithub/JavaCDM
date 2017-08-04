package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;

/**
 *
 * @author michelle.bowman
 */
public class CookieDomainPublicServiceProxy extends GenericPublicServiceProxy<CookieDomain> {

    public CookieDomainPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "CookieDomains", contentType, userName, password);
    }

    public CookieDomain getById(Long id) throws Exception {
        ServiceProxyImpl<CookieDomain> proxy = getProxy();
        return proxy.getById(id);
    }

    public CookieDomain create(CookieDomain input) throws Exception {
        ServiceProxyImpl<CookieDomain> proxy = getProxy();
        return proxy.save(input);
    }

    public SuccessResponse delete(CookieDomain input) throws Exception {
        ServiceProxyImpl<CookieDomain> proxy = getProxy();
        return proxy.delete(input);
    }

    public SuccessResponse deleteById(Long id) throws Exception {
        ServiceProxyImpl<CookieDomain> proxy = getProxy();
        return proxy.delete(id);
    }
    
    protected ServiceProxyImpl<CookieDomain> getProxy() {
        ServiceProxyImpl<CookieDomain> proxy = new ServiceProxyImpl<CookieDomain>(CookieDomain.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

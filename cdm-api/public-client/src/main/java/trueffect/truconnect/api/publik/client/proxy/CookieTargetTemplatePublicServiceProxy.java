package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.CookieTargetTemplate;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.publik.client.proxy.impl.CookieTargetTemplateProxyImpl;

/**
 *
 * @author michelle.bowman
 */
public class CookieTargetTemplatePublicServiceProxy extends GenericPublicServiceProxy<CookieTargetTemplate> {

    public CookieTargetTemplatePublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "CookieDomains", contentType, userName, password);
    }

    public CookieTargetTemplate saveCookieTargetTemplate(Long id, CookieTargetTemplate cookie) throws Exception {
        CookieTargetTemplateProxyImpl proxy = getProxy();
        return proxy.saveCookieTargetTemplate(id, cookie);
    }
    
    public RecordSet<CookieTargetTemplate> getByDomainId(Long id) throws Exception {
        CookieTargetTemplateProxyImpl proxy = getProxy();
        return proxy.getByDomainId(id);
    }

    protected CookieTargetTemplateProxyImpl getProxy() {
        CookieTargetTemplateProxyImpl proxy = new CookieTargetTemplateProxyImpl(CookieTargetTemplate.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.delivery.TagType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;

/**
 * @author Thomas Barjou
 */
public class AdvertiserTagTypePublicServiceProxy extends GenericPublicServiceProxy<TagType> {

    public AdvertiserTagTypePublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Advertisers", contentType, userName, password);
    }

    public RecordSet<TagType> get(Long advertiserId, Long siteId) throws Exception {
        ServiceProxyImpl<TagType> proxy = getProxy();
        proxy.path(Long.toString(advertiserId));
        proxy.path("site");
        proxy.path(Long.toString(siteId));
        proxy.path("tagTypes");
        return proxy.find(null, null, null);
    }

    protected ServiceProxyImpl<TagType> getProxy() {
        ServiceProxyImpl<TagType> proxy = new ServiceProxyImpl<TagType>(TagType.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.tpasapi.model.TagType;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;

/**
 *
 * @author Rambert Rioja
 */
public class AdvertiserTagTypeTpasapiServiceProxy extends GenericTpasapiServiceProxy<TagType> {

    public AdvertiserTagTypeTpasapiServiceProxy(String baseUrl, String authenticationUrl,
            String contentType, String userName, String password) throws Exception {
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

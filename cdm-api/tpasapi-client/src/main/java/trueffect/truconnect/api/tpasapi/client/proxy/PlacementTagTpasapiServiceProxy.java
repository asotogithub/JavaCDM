package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.tpasapi.model.Tag;

/**
 *
 * @author Abel Soto
 */
public class PlacementTagTpasapiServiceProxy extends GenericTpasapiServiceProxy<Tag>{

    public PlacementTagTpasapiServiceProxy(String baseUrl, String authenticationUrl,
            String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Placements", contentType, userName, password);
    }
    
    public Tag get(Long placementId, Long tagId) throws Exception {
        ServiceProxyImpl<Tag> proxy = getProxy();
        proxy.path(Long.toString(placementId));
        proxy.path("Tag");
        proxy.path(Long.toString(tagId));
        return proxy.get();
    }

    protected ServiceProxyImpl<Tag> getProxy() {
        ServiceProxyImpl<Tag> proxy = new ServiceProxyImpl<Tag>(Tag.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

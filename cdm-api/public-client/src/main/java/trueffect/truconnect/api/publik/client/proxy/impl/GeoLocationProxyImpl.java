package trueffect.truconnect.api.publik.client.proxy.impl;

import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;

public class GeoLocationProxyImpl extends ServiceProxyImpl<GeoLocation> {

    public GeoLocationProxyImpl(Class<GeoLocation> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public RecordSet<GeoLocation> getLocations(String path) throws Exception {
        path(path);
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        return getEntities(response);
    }
}

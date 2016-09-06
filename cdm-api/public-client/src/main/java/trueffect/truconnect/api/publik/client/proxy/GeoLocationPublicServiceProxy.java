package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.publik.client.proxy.impl.GeoLocationProxyImpl;

public class GeoLocationPublicServiceProxy extends GenericPublicServiceProxy<GeoLocation> {

    public GeoLocationPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "GeoLocations", contentType, userName, password);
    }

    public RecordSet<GeoLocation> getCountries() throws Exception {
        return getProxy().getLocations("countries");
    }

    public RecordSet<GeoLocation> getStates() throws Exception {
        return getProxy().getLocations("states");
    }

    public RecordSet<GeoLocation> getDmas() throws Exception {
        return getProxy().getLocations("dmas");
    }

    public RecordSet<GeoLocation> getZips() throws Exception {
        return getProxy().getLocations("zips");
    }

    @Override
    protected GeoLocationProxyImpl getProxy() {
        GeoLocationProxyImpl proxy = new GeoLocationProxyImpl(GeoLocation.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

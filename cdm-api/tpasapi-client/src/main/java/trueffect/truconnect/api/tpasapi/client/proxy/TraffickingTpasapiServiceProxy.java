package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.commons.model.Trafficking;
import trueffect.truconnect.api.tpasapi.client.proxy.impl.TraffickingProxyImpl;

public class TraffickingTpasapiServiceProxy extends GenericTpasapiServiceProxy<Trafficking> {

    public TraffickingTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Trafficking", contentType, userName, password);
    }

    public boolean trafficCampaignAdjustTimeZone(Trafficking input) throws Exception {
        TraffickingProxyImpl proxy = getProxy();
        return proxy.trafficCampaignAdjustTimeZone(input);
    }

    protected TraffickingProxyImpl getProxy() {
        TraffickingProxyImpl proxy = new TraffickingProxyImpl(Trafficking.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

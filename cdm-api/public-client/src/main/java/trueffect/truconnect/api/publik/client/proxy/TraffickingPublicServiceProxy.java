package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.Trafficking;
import trueffect.truconnect.api.publik.client.proxy.impl.TraffickingProxyImpl;

public class TraffickingPublicServiceProxy extends GenericPublicServiceProxy<Trafficking> {

    public TraffickingPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Trafficking", contentType, userName, password);
    }

    public boolean trafficCampaignAdjustTimeZone(Trafficking input) throws Exception {
        TraffickingProxyImpl proxy = getProxy();
        return proxy.trafficCampaignAdjustTimeZone(input);
    }

    public boolean trafficCampaignMock(Trafficking input) throws Exception {
        TraffickingProxyImpl proxy = getProxy();
        return proxy.trafficCampaignMock(input);
    }

    public boolean validateCampaign(Long campaignId) throws Exception {
        TraffickingProxyImpl proxy = getProxy();
        return proxy.validate(campaignId);
    }

    protected TraffickingProxyImpl getProxy() {
        TraffickingProxyImpl proxy = new TraffickingProxyImpl(Trafficking.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

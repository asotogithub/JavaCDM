package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.MediaBuyCampaign;
import trueffect.truconnect.api.publik.client.proxy.impl.MediaBuyProxyImpl;

public class MediaBuyPublicServiceProxy extends GenericPublicServiceProxy<MediaBuy> {

    public MediaBuyPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "MediaBuys", contentType, userName, password);
    }

    public MediaBuy getByCampaign(Long campaignId) throws Exception {
        MediaBuyProxyImpl proxy = getProxy();
        return ((MediaBuyProxyImpl)proxy).getByCampaign(campaignId);
    }

    public MediaBuy create(MediaBuy input) throws Exception {
        MediaBuyProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public MediaBuyCampaign saveMediaBuyCampaign(MediaBuyCampaign input) throws Exception {
        MediaBuyProxyImpl proxy = getProxy();
        return proxy.saveMediaBuyCampaign(input);
    }

    protected MediaBuyProxyImpl getProxy() {
        MediaBuyProxyImpl proxy = new MediaBuyProxyImpl(MediaBuy.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

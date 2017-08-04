package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.dto.BulkCreativeInsertion;
import trueffect.truconnect.api.publik.client.proxy.impl.CampaignCreativeInsertionProxyImpl;

public class CampaignCreativeInsertionPublicServiceProxy extends GenericPublicServiceProxy<CreativeInsertion> {

    public CampaignCreativeInsertionPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Campaigns", contentType, userName, password);
    }

    public BulkCreativeInsertion createWithCiBulk(Long id, BulkCreativeInsertion input) throws Exception {
        CampaignCreativeInsertionProxyImpl proxy = getProxy();
        return proxy.createWithCiBulk(id, input);
    }

    protected CampaignCreativeInsertionProxyImpl getProxy() {
        CampaignCreativeInsertionProxyImpl proxy = new CampaignCreativeInsertionProxyImpl(CreativeInsertion.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

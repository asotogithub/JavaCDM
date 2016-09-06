package trueffect.truconnect.api.tpasapi.client.proxy;

import java.io.File;
import java.io.FileInputStream;

import trueffect.truconnect.api.tpasapi.model.Campaign;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.client.proxy.impl.CampaignProxyImpl;

public class CampaignTpasapiServiceProxy extends GenericTpasapiServiceProxy<Campaign> {

    public CampaignTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Campaigns", contentType, userName, password);
    }

    public Creative saveCreativeFile(Long id, String imageFolderBasePath, String filename) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        File file = new File(imageFolderBasePath + filename);
        // TODO. MH 02/05/2016 Someday, we need to fix this to use try-with-resources blocks. I am not doing that as
        // we shouldn't "touch" tpasapi client
        return proxy.saveCreativeFile(id, new FileInputStream(file), filename);
    }

    protected CampaignProxyImpl getProxy() {
        CampaignProxyImpl proxy = new CampaignProxyImpl(Campaign.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

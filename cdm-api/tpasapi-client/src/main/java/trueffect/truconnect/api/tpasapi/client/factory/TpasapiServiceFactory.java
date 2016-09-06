package trueffect.truconnect.api.tpasapi.client.factory;

import trueffect.truconnect.api.tpasapi.client.proxy.AdvertiserTagTypeTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.CampaignTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.CookieTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.CreativeGroupTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.CreativeTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.ImageTpasapiProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.PhysicalDeleteTpasapiProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.PlacementTagTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.PlacementTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.PublisherTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.SiteTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.SizeTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.TargetTypeTpasapiServiceProxy;
import trueffect.truconnect.api.tpasapi.client.proxy.TraffickingTpasapiServiceProxy;

public class TpasapiServiceFactory {

    private String baseUrl;
    private String authenticationUrl;
    private String userName;
    private String password;
    private String contentType;

    public TpasapiServiceFactory(String baseUrl, String authenticationUrl, String contentType, String userName, String password) {
        this.baseUrl = baseUrl;
        this.authenticationUrl = authenticationUrl; 
        this.userName = userName;
        this.password = password;
        this.contentType = contentType; 
    }

    public SizeTpasapiServiceProxy getAdSizeProxy() throws Exception {
        return new SizeTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CreativeTpasapiServiceProxy getCreativeProxy() throws Exception {
        return new CreativeTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CreativeGroupTpasapiServiceProxy getCreativeGroupProxy() throws Exception {
        return new CreativeGroupTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CampaignTpasapiServiceProxy getCampaignProxy() throws Exception {
        return new CampaignTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public PlacementTpasapiServiceProxy getPlacementProxy() throws Exception {
        return new PlacementTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public PublisherTpasapiServiceProxy getPublisherProxy() throws Exception {
        return new PublisherTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public SiteTpasapiServiceProxy getSiteProxy() throws Exception {
        return new SiteTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public TargetTypeTpasapiServiceProxy getTargetTypeProxy() throws Exception {
        return new TargetTypeTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public TraffickingTpasapiServiceProxy getTraffickingProxy() throws Exception {
        return new TraffickingTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }
    
    public AdvertiserTagTypeTpasapiServiceProxy getTagTypeProxy() throws Exception {
        return new AdvertiserTagTypeTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public PlacementTagTpasapiServiceProxy getTagProxy() throws Exception {
        return new PlacementTagTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }
    
    public CookieTpasapiServiceProxy getCookieProxy() throws Exception {
        return new CookieTpasapiServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public <T> ImageTpasapiProxy<T> getImageProxy(Class<T> type, String servicePath) throws Exception{
        return new ImageTpasapiProxy<T>(type, baseUrl, authenticationUrl, servicePath, contentType, userName, password);
    }

    public <T> PhysicalDeleteTpasapiProxy<T> getPhysicalDeleteProxy(Class<T> type, String servicePath) throws Exception{
        return new PhysicalDeleteTpasapiProxy<T>(type, baseUrl, authenticationUrl, servicePath, contentType, userName, password);
    }
}

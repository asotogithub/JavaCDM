package trueffect.truconnect.api.publik.client.factory;

import trueffect.truconnect.api.publik.client.proxy.AdvertiserPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.AdvertiserTagTypePublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.AgencyPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.BrandPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.CampaignCreativeInsertionPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.CampaignPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.ContactPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.CookieDomainPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.CookieTargetTemplatePublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.CreativeGroupPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.CreativeInsertionPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.CreativePublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.DatasetConfigPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.GeoLocationPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.HtmlInjectionTagsPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.ImagePublicProxy;
import trueffect.truconnect.api.publik.client.proxy.InsertionOrderPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.MediaBuyPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.PackagePublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.PhysicalDeletePublicProxy;
import trueffect.truconnect.api.publik.client.proxy.PlacementPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.PublisherPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.SchedulePublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.SiteMeasurementEventPingPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.SiteMeasurementEventPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.SiteMeasurementGroupPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.SiteMeasurementsPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.SitePublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.SiteSectionPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.SizePublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.TraffickingPublicServiceProxy;
import trueffect.truconnect.api.publik.client.proxy.UserPublicServiceProxy;

public class PublicServiceFactory {

    private String baseUrl;
    private String authenticationUrl;
    private String userName;
    private String password;
    private String contentType;

    public PublicServiceFactory(String baseUrl, String authenticationUrl, String contentType, String userName, String password) {
        this.baseUrl = baseUrl;
        this.authenticationUrl = authenticationUrl;
        this.userName = userName;
        this.password = password;
        this.contentType = contentType;
    }

    public SizePublicServiceProxy getSizeProxy() throws Exception {
        return new SizePublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public AdvertiserPublicServiceProxy getAdvertiserProxy() throws Exception {
        return new AdvertiserPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public AdvertiserTagTypePublicServiceProxy getAdvertiserTagTypeProxy() throws Exception {
        return new AdvertiserTagTypePublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public AgencyPublicServiceProxy getAgencyProxy() throws Exception {
        return new AgencyPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public BrandPublicServiceProxy getBrandProxy() throws Exception {
        return new BrandPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CampaignPublicServiceProxy getCampaignProxy() throws Exception {
        return new CampaignPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public ContactPublicServiceProxy getContactProxy() throws Exception {
        return new ContactPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CookieDomainPublicServiceProxy getCookieDomainProxy() throws Exception {
        return new CookieDomainPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CreativePublicServiceProxy getCreativeProxy() throws Exception {
        return new CreativePublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CreativeGroupPublicServiceProxy getCreativeGroupProxy() throws Exception {
        return new CreativeGroupPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CreativeInsertionPublicServiceProxy getCreativeInsertionProxy() throws Exception {
        return new CreativeInsertionPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CampaignCreativeInsertionPublicServiceProxy getCampaignCreativeInsertionProxy() throws Exception {
        return new CampaignCreativeInsertionPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public InsertionOrderPublicServiceProxy getInsertionOrderProxy() throws Exception {
        return new InsertionOrderPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public MediaBuyPublicServiceProxy getMediaBuyProxy() throws Exception {
        return new MediaBuyPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public PlacementPublicServiceProxy getPlacementProxy() throws Exception {
        return new PlacementPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public PackagePublicServiceProxy getPackageProxy() throws Exception {
        return new PackagePublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public PublisherPublicServiceProxy getPublisherProxy() throws Exception {
        return new PublisherPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public SchedulePublicServiceProxy getScheduleProxy() throws Exception {
        return new SchedulePublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public SiteSectionPublicServiceProxy getSiteSectionProxy() throws Exception {
        return new SiteSectionPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public SitePublicServiceProxy getSiteProxy() throws Exception {
        return new SitePublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public TraffickingPublicServiceProxy getTraffickingProxy() throws Exception {
        return new TraffickingPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public UserPublicServiceProxy getUserProxy() throws Exception {
        return new UserPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public CookieTargetTemplatePublicServiceProxy getCookieTargetTemplateProxy() throws Exception {
        return new CookieTargetTemplatePublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public SiteMeasurementsPublicServiceProxy getSiteMeasurementProxy() throws Exception {
        return new SiteMeasurementsPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public SiteMeasurementGroupPublicServiceProxy getSiteMeasurementGroupProxy() throws Exception {
        return new SiteMeasurementGroupPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public SiteMeasurementEventPublicServiceProxy getSiteMeasurementEventProxy() throws Exception {
        return new SiteMeasurementEventPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }
    
    public SiteMeasurementEventPingPublicServiceProxy getSiteMeasurementEventPingProxy() throws Exception {
        return new SiteMeasurementEventPingPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }
    
    public <T> ImagePublicProxy<T> getImageProxy(Class<T> type, String servicePath) throws Exception {
        return new ImagePublicProxy<T>(type, baseUrl, authenticationUrl, servicePath, contentType, userName, password);
    }

    public <T> PhysicalDeletePublicProxy<T> getPhysicalDeleteProxy(Class<T> type, String servicePath) throws Exception {
        return new PhysicalDeletePublicProxy<T>(type, baseUrl, authenticationUrl, servicePath, contentType, userName, password);
    }

    public GeoLocationPublicServiceProxy getGeoLocationProxy() throws Exception {
        return new GeoLocationPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public DatasetConfigPublicServiceProxy getDatasetConfigProxy() throws Exception {
        return new DatasetConfigPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }

    public HtmlInjectionTagsPublicServiceProxy getHtmlInjectionTagsProxy() throws Exception {
        return new HtmlInjectionTagsPublicServiceProxy(baseUrl, authenticationUrl, contentType, userName, password);
    }
}

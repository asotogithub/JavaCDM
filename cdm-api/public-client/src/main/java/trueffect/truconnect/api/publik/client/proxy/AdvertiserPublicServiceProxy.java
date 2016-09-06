package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.publik.client.proxy.impl.AdvertiserProxyImpl;

/**
 *
 * @author michelle.bowman
 * @edited Richard Jaldin
 */
public class AdvertiserPublicServiceProxy extends GenericPublicServiceProxy<Advertiser> {

    public AdvertiserPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Advertisers", contentType, userName, password);
    }

    public Advertiser getById(Long id) throws Exception {
        AdvertiserProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public Advertiser create(Advertiser input) throws Exception {
        AdvertiserProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public Advertiser update(Object id, Advertiser input) throws Exception {
        AdvertiserProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public RecordSet<Advertiser> find(String query, Long startIndex, Long pageSize) throws Exception{
        AdvertiserProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Advertiser> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<Advertiser> find() throws Exception{
        return this.find(null);
    }

    public RecordSet<Brand> getBrands(Long id) throws Exception{
        AdvertiserProxyImpl proxy = getProxy();
        return proxy.getBrands(id);
    }

    public void physicalDelete(Long id) throws Exception {
        AdvertiserProxyImpl proxy = getProxy();
        proxy.physicalDelete(id);
    }

    protected AdvertiserProxyImpl getProxy() {
        AdvertiserProxyImpl proxy = new AdvertiserProxyImpl(Advertiser.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

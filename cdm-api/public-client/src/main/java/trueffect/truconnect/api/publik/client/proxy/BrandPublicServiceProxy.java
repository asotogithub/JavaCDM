package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.proxy.impl.BrandProxyImpl;

/**
 *
 * @author michelle.bowman
 * @edited Thomas Barjou
 */
public class BrandPublicServiceProxy extends GenericPublicServiceProxy<Brand> {

    public BrandPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Brands", contentType, userName, password);
    }

    public Brand getById(Long id) throws Exception {
        BrandProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public Brand create(Brand input) throws Exception {
        BrandProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public Brand update(Object id, Brand input) throws Exception {
        BrandProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public RecordSet<Brand> find(String query, Long startIndex, Long pageSize) throws Exception{
        BrandProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Brand> find(String query) throws Exception{
        BrandProxyImpl proxy = getProxy();
        return proxy.find(query, null, null);
    }

    public RecordSet<Brand> find() throws Exception{
        return this.find(null);
    }

    public SuccessResponse delete(Brand input) throws Exception {
        BrandProxyImpl proxy = getProxy();
        return proxy.delete(input);
    }

    public RecordSet<PlacementView> getPlacementsByBrandId(Long id) throws Exception{
        BrandProxyImpl proxy = getProxy();
        return proxy.getPlacementsByBrandId(id);
    }

    protected BrandProxyImpl getProxy() {
        BrandProxyImpl proxy = new BrandProxyImpl(Brand.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

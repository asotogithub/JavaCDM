package trueffect.truconnect.api.publik.client.proxy;


import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;

public class SiteSectionPublicServiceProxy extends GenericPublicServiceProxy<SiteSection> {

    public SiteSectionPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "SiteSections", contentType, userName, password);
    }

    public SiteSection getById(Long id) throws Exception {
        ServiceProxyImpl<SiteSection> proxy = getProxy();
        return proxy.getById(id);
    }

    public SiteSection create(SiteSection input) throws Exception {
        ServiceProxyImpl<SiteSection> proxy = getProxy();
        return proxy.save(input);
    }

    public SiteSection update(Object id, SiteSection input) throws Exception {
        ServiceProxyImpl<SiteSection> proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(SiteSection input) throws Exception {
        ServiceProxyImpl<SiteSection> proxy = getProxy();
        return proxy.delete(input);
    }

    public RecordSet<SiteSection> find(String query, Long startIndex, Long pageSize) throws Exception{
        ServiceProxyImpl<SiteSection> proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<SiteSection> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<SiteSection> find() throws Exception{
        return this.find(null);
    }

    protected ServiceProxyImpl<SiteSection> getProxy() {
        ServiceProxyImpl<SiteSection> proxy = new ServiceProxyImpl<SiteSection>(SiteSection.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

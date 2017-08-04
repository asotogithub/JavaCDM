package trueffect.truconnect.api.publik.client.proxy;


import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;

public class SitePublicServiceProxy extends GenericPublicServiceProxy<Site> {

    public SitePublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Sites", contentType, userName, password);
    }

    public Site getById(Long id) throws Exception {
        ServiceProxyImpl<Site> proxy = getProxy();
        return proxy.getById(id);
    }

    public Site create(Site input) throws Exception {
        ServiceProxyImpl<Site> proxy = getProxy();
        return proxy.save(input);
    }

    public Site update(Object id, Site input) throws Exception {
        ServiceProxyImpl<Site> proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(Site input) throws Exception {
        ServiceProxyImpl<Site> proxy = getProxy();
        return proxy.delete(input);
    }

    public RecordSet<Site> find(String query, Long startIndex, Long pageSize) throws Exception{
        ServiceProxyImpl<Site> proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Site> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<Site> find() throws Exception{
        return this.find(null);
    }

    protected ServiceProxyImpl<Site> getProxy() {
        ServiceProxyImpl<Site> proxy = new ServiceProxyImpl<Site>(Site.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

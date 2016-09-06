package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Site;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;

public class SiteTpasapiServiceProxy extends GenericTpasapiServiceProxy<Site> {

    public SiteTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Sites", contentType, userName, password);
    }

    public Site getById(int id) throws Exception {
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

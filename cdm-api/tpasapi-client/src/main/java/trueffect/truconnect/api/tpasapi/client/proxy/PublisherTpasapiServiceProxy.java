package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.tpasapi.model.Publisher;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;

public class PublisherTpasapiServiceProxy extends GenericTpasapiServiceProxy<Publisher> {

    public PublisherTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Publishers", contentType, userName, password);
    }

    public Publisher getById(int id) throws Exception {
        ServiceProxyImpl<Publisher> proxy = getProxy();
        return proxy.getById(id);
    }

    public Publisher create(Publisher input) throws Exception {
        ServiceProxyImpl<Publisher> proxy = getProxy();
        return proxy.save(input);
    }

    public Publisher update(Object id, Publisher input) throws Exception {
        ServiceProxyImpl<Publisher> proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public RecordSet<Publisher> find(String query, Long startIndex, Long pageSize) throws Exception{
        ServiceProxyImpl<Publisher> proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Publisher> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<Publisher> find() throws Exception{
        return this.find(null);
    }
    
    protected ServiceProxyImpl<Publisher> getProxy() {
        ServiceProxyImpl<Publisher> proxy = new ServiceProxyImpl<Publisher>(Publisher.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

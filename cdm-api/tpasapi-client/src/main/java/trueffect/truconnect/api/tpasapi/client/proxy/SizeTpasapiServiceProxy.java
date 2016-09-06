package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Size;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;

public class SizeTpasapiServiceProxy extends GenericTpasapiServiceProxy<Size> {

    public SizeTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Sizes", contentType, userName, password);
    }

    public Size getById(int id) throws Exception {
        ServiceProxyImpl<Size> proxy = getProxy();
        return proxy.getById(id);
    }

    public Size create(Size input) throws Exception {
        ServiceProxyImpl<Size> proxy = getProxy();
        return proxy.save(input);
    }

    public Size update(Size input) throws Exception {
        ServiceProxyImpl<Size> proxy = getProxy();
        return proxy.update(input);
    }

    public SuccessResponse delete(Object id) throws Exception {
        ServiceProxyImpl<Size> proxy = getProxy();
        return proxy.delete(id);
    }

    public RecordSet<Size> find(String query, Long startIndex, Long pageSize) throws Exception{
        ServiceProxyImpl<Size> proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Size> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<Size> find() throws Exception{
        return this.find(null);
    }
    
    protected ServiceProxyImpl<Size> getProxy() {
        ServiceProxyImpl<Size> proxy = new ServiceProxyImpl<Size>(Size.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

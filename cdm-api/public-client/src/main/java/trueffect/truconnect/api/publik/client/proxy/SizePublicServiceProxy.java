package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;

public class SizePublicServiceProxy extends GenericPublicServiceProxy<Size> {

    public SizePublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Sizes", contentType, userName, password);
    }

    public Size getById(Long id) throws Exception {
        ServiceProxyImpl<Size> proxy = getProxy();
        return proxy.getById(id);
    }

    public Size create(Size input) throws Exception {
        ServiceProxyImpl<Size> proxy = getProxy();
        return proxy.save(input);
    }

    public Size update(Object id, Size input) throws Exception {
        ServiceProxyImpl<Size> proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(Size input) throws Exception {
        ServiceProxyImpl<Size> proxy = getProxy();
        return proxy.delete(input);
    }

    public RecordSet<Size> find(String query, Long startIndex, Long pageSize) throws Exception {
        ServiceProxyImpl<Size> proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Size> find(String query) throws Exception {
        return this.find(query, null, null);
    }

    public RecordSet<Size> find() throws Exception {
        return this.find(null);
    }

    protected ServiceProxyImpl<Size> getProxy() {
        ServiceProxyImpl<Size> proxy = new ServiceProxyImpl<Size>(Size.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

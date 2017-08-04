package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.publik.client.base.PhysicalDeleteProxyImpl;

public class PhysicalDeletePublicProxy<T> extends GenericPublicServiceProxy<T> {

    private Class<T> type;

    public PhysicalDeletePublicProxy(Class<T> type, String baseUrl, String authenticationUrl, String servicePath, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, servicePath, contentType, userName, password);
        this.type = type;
    }

    public boolean physicalDelete(Object id) throws Exception {
        PhysicalDeleteProxyImpl<T> proxy = getProxy();
        return proxy.physicalDelete(id);
    }
    
    protected PhysicalDeleteProxyImpl<T> getProxy() {
        PhysicalDeleteProxyImpl<T> proxy = new PhysicalDeleteProxyImpl<T>(type, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

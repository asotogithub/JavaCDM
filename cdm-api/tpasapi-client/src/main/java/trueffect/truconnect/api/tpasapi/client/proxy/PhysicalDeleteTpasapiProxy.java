package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.tpasapi.client.base.PhysicalDeleteProxyImpl;

public class PhysicalDeleteTpasapiProxy<T> extends GenericTpasapiServiceProxy<T> {

    private Class<T> type;

    public PhysicalDeleteTpasapiProxy(Class<T> type, String baseUrl, String authenticationUrl, String servicePath, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, servicePath, contentType, userName, password);
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

package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.TargetType;
import trueffect.truconnect.api.tpasapi.model.TargetValue;
import trueffect.truconnect.api.tpasapi.client.proxy.impl.TargetTypeProxyImpl;

public class TargetTypeTpasapiServiceProxy extends GenericTpasapiServiceProxy<TargetType> {

    public TargetTypeTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "TargetTypes", contentType, userName, password);
    }

    public RecordSet<TargetType> getTargetTypes() throws Exception {
        TargetTypeProxyImpl proxy = getProxy();
        return proxy.getTargetTypes();
    }

    public RecordSet<TargetValue> getTargetValues(String code) throws Exception {
        TargetTypeProxyImpl proxy = getProxy();
        return proxy.getTargetValues(code);
    }

    protected TargetTypeProxyImpl getProxy() {
        TargetTypeProxyImpl proxy = new TargetTypeProxyImpl(TargetType.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

package trueffect.truconnect.api.tpasapi.client.proxy;


import java.io.File;
import java.io.InputStream;

import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.tpasapi.client.proxy.impl.CreativeProxyImpl;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.model.RecordSet;

public class CreativeTpasapiServiceProxy extends GenericTpasapiServiceProxy<Creative> {

    public CreativeTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
	super(baseUrl, authenticationUrl, "Creatives", contentType, userName, password);
    }

    public Creative getById(int id) throws Exception {
        CreativeProxyImpl proxy = getProxy();
	return proxy.getById(id);
    }

    public RecordSet<Creative> find(String query, Long startIndex, Long pageSize) throws Exception{
        CreativeProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Creative> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<Creative> find() throws Exception{
        return this.find(null);
    }

    public File getImage(Object id) throws Exception{
        CreativeProxyImpl proxy = getProxy();
        return proxy.getImage(id);
    }

    public boolean replaceImage(Object id, InputStream inputStream, String filename) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.replaceImage(id, inputStream, filename);
    }

    public SuccessResponse delete(Object id, boolean recursiveDelete) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.delete(id, recursiveDelete);
    }
    
    public Creative update(Object id, Creative input) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }
    
    protected CreativeProxyImpl getProxy() {
        CreativeProxyImpl proxy = new CreativeProxyImpl(Creative.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

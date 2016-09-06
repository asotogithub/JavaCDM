package trueffect.truconnect.api.tpasapi.client.proxy;

import java.io.File;

import trueffect.truconnect.api.tpasapi.client.base.ImageProxyImpl;

public class ImageTpasapiProxy<T> extends GenericTpasapiServiceProxy<T> {

    private Class<T> type;

    public ImageTpasapiProxy(Class<T> type, String baseUrl, String authenticationUrl, String servicePath, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, servicePath, contentType, userName, password);
    }

    public File getImagen() throws Exception {
        ImageProxyImpl<T> proxy = getProxy();
        return proxy.getImagen();
    }

    public T uploadImagen(File file) throws Exception {
        ImageProxyImpl<T> proxy = getProxy();
        return proxy.uploadImagen(file);
    }

    protected ImageProxyImpl<T> getProxy() {
        ImageProxyImpl<T> proxy = new ImageProxyImpl<T>(type, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

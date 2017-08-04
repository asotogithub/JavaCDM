package trueffect.truconnect.api.publik.client.proxy;

import java.io.File;

import trueffect.truconnect.api.publik.client.base.ImageProxyImpl;

public class ImagePublicProxy<T> extends GenericPublicServiceProxy<T> {

    private Class<T> type;

    public ImagePublicProxy(Class<T> type, String baseUrl, String authenticationUrl, String servicePath, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, servicePath, contentType, userName, password);
        this.type = type;
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

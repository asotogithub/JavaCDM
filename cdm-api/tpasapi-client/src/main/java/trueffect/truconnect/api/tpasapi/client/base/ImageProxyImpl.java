package trueffect.truconnect.api.tpasapi.client.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;

import trueffect.truconnect.api.tpasapi.client.proxy.AuthenticationTpasapiServiceProxy;

import com.sun.jersey.api.client.ClientResponse;
/**
 *
 * @author michelle.bowman
 */
public class ImageProxyImpl<T> extends ServiceProxyImpl<T> {
    
    public ImageProxyImpl(Class<T> type, String baseUrl, AuthenticationTpasapiServiceProxy authenticator) {
        super(type, baseUrl, "", authenticator);
    }

    public ImageProxyImpl(Class<T> type, String baseUrl, String servicePath, AuthenticationTpasapiServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public File getImagen() throws Exception {
        ClientResponse response = webRsc.accept("image/jpg")
                .get(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return response.getEntity(File.class);
    }

    public T uploadImagen(File file) throws Exception {
        // TODO. MH 02/05/2016 Someday, we need to fix this to use try-with-resources blocks. I am not doing that as
        // we shouldn't "touch" tpasapi client
        InputStream fileInStream = new FileInputStream(file);
        String disposition = "attachment; filename=\"" + file.getName() + "\"";
        ClientResponse response = webRsc.type(MediaType.APPLICATION_OCTET_STREAM)
                .accept(getContentType().getType())
                .header("Content-Disposition", disposition)
                .post(ClientResponse.class, fileInStream);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return getEntity(response);
    }
    
}

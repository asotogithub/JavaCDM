package trueffect.truconnect.api.publik.client.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;

import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;
/**
 *
 * @author michelle.bowman
 */
public class ImageProxyImpl<T> extends ServiceProxyImpl<T> {

    public ImageProxyImpl(Class<T> type, String baseUrl, AuthenticationPublicServiceProxy authenticator) {
		super(type, baseUrl, "", authenticator);
    }

    public ImageProxyImpl(Class<T> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
		super(type, baseUrl, servicePath, authenticator);
    }

    public File getImagen() throws Exception {
		ClientResponse response = webRsc.accept("image/jpg").get(ClientResponse.class);
		this.setLastResponse(response);
		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			handleErrors(response);
		}
		return response.getEntity(File.class);
    }

    public T uploadImagen(File file) throws Exception {
		ClientResponse response;
		try (InputStream fileInStream = new FileInputStream(file)) {
			String disposition = "attachment; filename=\"" + file.getName() + "\"";
			response = webRsc.type(MediaType.APPLICATION_OCTET_STREAM)
					.accept(getContentType().getType())
					.header("Content-Disposition", disposition)
					.post(ClientResponse.class, fileInStream);
		}
		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			handleErrors(response);
		}
		return getEntity(response);
    }

}

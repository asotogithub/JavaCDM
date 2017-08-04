package trueffect.truconnect.api.tpasapi.client.proxy.impl;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.tpasapi.client.proxy.AuthenticationTpasapiServiceProxy;

public class CreativeProxyImpl extends ServiceProxyImpl<Creative> {

    public CreativeProxyImpl(Class<Creative> type, String baseUrl, String servicePath, AuthenticationTpasapiServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public File getImage(Object id) throws Exception {
        path(id.toString());
        path("image");
        WebResource.Builder builder = header().accept("text/xml");
        ClientResponse response = builder.get(ClientResponse.class);
        this.setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    File result = getImage(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return response.getEntity(File.class);
    }
    
    public boolean replaceImage(Object id, InputStream inputStream, String filename) throws Exception {
        path(id.toString());
        path("image");
        query("filename", filename);
        String disposition = "attachment; filename=\"" + filename + "\"";
        ClientResponse response = header().header("statusId", "1").accept(getContentType().getType()).type(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", disposition)
                .put(ClientResponse.class, inputStream);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    boolean result = replaceImage(id, inputStream, filename);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return true;
    }
    
    public SuccessResponse delete(Object id, Boolean recursiveDelete) throws Exception {
        path(id.toString());
        query("recursiveDelete", recursiveDelete.toString());
        return this.delete();
    }
}

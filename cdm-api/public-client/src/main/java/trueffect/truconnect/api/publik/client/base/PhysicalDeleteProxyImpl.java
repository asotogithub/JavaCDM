package trueffect.truconnect.api.publik.client.base;

import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;

/**
 *
 * @author michelle.bowman
 */
public class PhysicalDeleteProxyImpl<T> extends ServiceProxyImpl<T> {

    public PhysicalDeleteProxyImpl(Class<T> type, String baseUrl, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, "", authenticator);
    }
    
    public PhysicalDeleteProxyImpl(Class<T> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public boolean physicalDelete(Object id) throws Exception {
        ClientResponse response = webRsc.path(id.toString() + "/physical")
                .accept(getContentType().getType()).delete(ClientResponse.class);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return true;
    }
}

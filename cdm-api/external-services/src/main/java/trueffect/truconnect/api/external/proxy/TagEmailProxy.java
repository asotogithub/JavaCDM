package trueffect.truconnect.api.external.proxy;

import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.delivery.TagEmailWrapper;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;


/**
 *
 * @author rodrigo.alarcon
 */
public class TagEmailProxy extends BaseDeliveryProxy<TagEmailWrapper>{
    
    private UtilityWrapper utilityWrapper;
    
    public TagEmailProxy(HttpHeaders headers) {
        super(TagEmailWrapper.class, headers);
        path("tag");
        path("email");
        setContentType("application/json");
        
        utilityWrapper = new UtilityWrapperImpl();
    }
    
    public TagEmailProxy(HttpHeaders headers, TagEmailProxy.UtilityWrapper utilityWrapper) {
        super(TagEmailWrapper.class, headers);
        path("tag");
        path("email");
        setContentType("application/json");
        
        this.utilityWrapper = utilityWrapper;
    }
    
    @Override
    public TagEmailWrapper post(TagEmailWrapper t) throws ProxyException {
        ClientResponse response = utilityWrapper.getWebResourceBuilder()
                .post(ClientResponse.class, getPostData(t));
        
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return getEntity(response);
    }
    
    private Object getPostData(Object target) throws ProxyException {
        if (getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
            try {
                return getJsonMapper().writeValueAsString(target).replace("\"Key\":", "").replace(",\"Value\"", "");
            } catch (Exception e) {
                throw new ProxyException(e.getMessage());
            }
        }
        return target;
    }
    
    /**
     * Interface to make this class testable
     * */
    interface UtilityWrapper {
        WebResource.Builder getWebResourceBuilder();
    }

    /**
     * Default implementation for {@code UtilityWrapper}
     */
    class UtilityWrapperImpl implements UtilityWrapper {
        
        @Override
        public WebResource.Builder getWebResourceBuilder() {
            return header().type(getContentType()).accept(getContentType());
        }
    }
}
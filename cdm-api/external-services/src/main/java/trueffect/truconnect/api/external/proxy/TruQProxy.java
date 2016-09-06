package trueffect.truconnect.api.external.proxy;

import trueffect.truconnect.api.commons.service.proxy.ServiceProxyImpl;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.delivery.TruQTagList;
import trueffect.truconnect.api.resources.ResourceUtil;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author rodrigo.alarcon
 */
public class TruQProxy extends ServiceProxyImpl<TruQTagList> {

    private TruQProxy.UtilityWrapper utilityWrapper;
    
    public TruQProxy(HttpHeaders headers) {
        super(TruQTagList.class, headers);
        
        setContentType("application/json");
        
        this.utilityWrapper = new TruQProxy.UtilityWrapperImpl();
    }
    
    public TruQProxy(HttpHeaders headers, TruQProxy.UtilityWrapper utilityWrapper) {
        super(TruQTagList.class, headers);
        
        setContentType("application/json");
        
        this.utilityWrapper = utilityWrapper;
    }

    @Override
    public String getBaseUrl() {
        return ResourceUtil.get("public.truq.url");
    }
    
    @Override
    public TruQTagList post(TruQTagList t) throws ProxyException {
        ClientResponse response = utilityWrapper.getWebResourceBuilder()
                .post(ClientResponse.class, getPostData(t.getTagMessages()));
        
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return getEntity(response);
    }
    
    private Object getPostData(Object target) throws ProxyException {
        if (getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
            try {
                return getJsonMapper().writeValueAsString(target);
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


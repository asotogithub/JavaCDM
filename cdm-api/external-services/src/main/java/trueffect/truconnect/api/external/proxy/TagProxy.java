package trueffect.truconnect.api.external.proxy;

import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import trueffect.truconnect.api.commons.exceptions.ProxyException;

/**
 *
 * @author Abel Soto
 */
public class TagProxy extends BaseDeliveryProxy<String> {

    public TagProxy(HttpHeaders headers) {
        super(String.class, headers);
        path("tag");
    }
    
    public String getTagString() throws ProxyException{
       setContentType("application/json");
       String aux = post("");
       return aux;
    } 
    
    public String getTagString(int tagTypeId, int placementId) throws ProxyException{
        reset();
        
        path("tag");
        path("GetTag");
        query("tagId", Integer.toString(tagTypeId));
        query("placementId", Integer.toString(placementId));
        
        setContentType("application/json");
        String aux = post("");
        
        return aux;
    }
   
    @Override
    public String post(String t) throws ProxyException {
        ClientResponse response = header().type(getContentType()).accept(getContentType())
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
                return getJsonMapper().writeValueAsString(target);
            } catch (Exception e) {
                throw new ProxyException(e.getMessage());
            }
        }
        return target;
    }
}

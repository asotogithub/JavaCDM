package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

/**
 *
 * @author Thomas Barjou
 */
public class BrandProxyImpl extends ServiceProxyImpl<Brand> {

    public BrandProxyImpl(Class<Brand> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public RecordSet<PlacementView> getPlacementsByBrandId(Long id) throws Exception {
        path(id.toString());
        path("placements");
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<PlacementView> result = getPlacementsByBrandId(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getPlacementViewEntity(response);
    }
    
    @SuppressWarnings("unchecked")
    private RecordSet<PlacementView> getPlacementViewEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }
}

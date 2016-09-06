package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeDTO;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

public class CreativeGroupProxyImpl extends ServiceProxyImpl<CreativeGroup> {

    public CreativeGroupProxyImpl(Class<CreativeGroup> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public void delete(Long id, Boolean recursiveDelete) throws Exception {
        path(id.toString());
        query("recursiveDelete", recursiveDelete.toString());
        this.delete();
    }

    public RecordSet<CreativeGroupCreative> getCreativeGroupCreatives(Object id) throws Exception {
        path(id.toString());
        path("creatives");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CreativeGroupCreative> result = getCreativeGroupCreatives(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getCreativeGroupCreativesEntity(response);
    }

    @SuppressWarnings("unchecked")
    private RecordSet<CreativeGroupCreative> getCreativeGroupCreativesEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }

    public CreativeGroupCreative updateCreativeGroupCreativesList(Long id, CreativeGroupCreative cgc) throws Exception {
        path(id.toString());
        path("creatives");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, getPostData(cgc));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    CreativeGroupCreative result = updateCreativeGroupCreativesList(id, cgc);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getCreativeGroupCreativeEntity(response);
    }

    private CreativeGroupCreative getCreativeGroupCreativeEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, CreativeGroupCreative.class);
        }
        return response.getEntity(CreativeGroupCreative.class);
    }

    public SuccessResponse createAssociations(CreativeGroupCreativeDTO associations)
            throws Exception {
        path("createAssociations");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class,
                                                                       getPostData(associations));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = createAssociations(associations);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getSuccessEntity(response);
    }
}

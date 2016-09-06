package trueffect.truconnect.api.publik.client.proxy.impl;

import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.BulkCreativeInsertion;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionBulkUpdate;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Thomas barjou
 */
public class CreativeInsertionProxyImpl extends ServiceProxyImpl<CreativeInsertion> {

    public CreativeInsertionProxyImpl(Class<CreativeInsertion> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public RecordSet<CreativeInsertion> getByCreativeGroup(Long creativeGroupId) throws Exception {
        path(creativeGroupId.toString());
        path("byCreativeGroupId");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CreativeInsertion> result = getByCreativeGroup(creativeGroupId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getCreativeGroupsCreativeInsertionsEntity(response);
    }

    @SuppressWarnings("unchecked")
    private RecordSet<CreativeInsertion> getCreativeGroupsCreativeInsertionsEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }

    public BulkCreativeInsertion createWithCiBulk(Object input) throws Exception {
        path("bulkCreate");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, getPostData(input));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    BulkCreativeInsertion result = createWithCiBulk(input);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getBulkCreativeInsertionEntity(response);
    }

    private BulkCreativeInsertion getBulkCreativeInsertionEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, BulkCreativeInsertion.class);
        }
        return response.getEntity(BulkCreativeInsertion.class);
    }

    public CreativeInsertionBulkUpdate updateWithCiBulk(Object input) throws Exception {
        path("bulkUpdate");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, getPostData(input));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    CreativeInsertionBulkUpdate result = updateWithCiBulk(input);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getCreativeInsertionBulkUpdateEntity(response);
    }

    private CreativeInsertionBulkUpdate getCreativeInsertionBulkUpdateEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, CreativeInsertionBulkUpdate.class);
        }
        return response.getEntity(CreativeInsertionBulkUpdate.class);
    }
}

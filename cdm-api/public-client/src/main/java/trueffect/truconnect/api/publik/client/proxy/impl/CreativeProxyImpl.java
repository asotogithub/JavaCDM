package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;
import trueffect.truconnect.api.commons.model.BooleanResponse;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CreativeAssociationsDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import java.io.File;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;

public class CreativeProxyImpl extends ServiceProxyImpl<Creative> {

    public CreativeProxyImpl(Class<Creative> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public File getFile(Object id) throws Exception {
        path(id.toString());
        path("file");
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
                    File result = getFile(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getFileEntity(response);
    }

    public RecordSet<Creative> getUnassociated(String campaignId, String groupId) throws Exception {
        path("unassociated");
        if (campaignId != null) {
            query("campaignId", campaignId);
        }
        if (groupId != null) {
            query("groupId", groupId);
        }
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<Creative> result = getUnassociated(campaignId, groupId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }

    public String getPreview(Object id) throws Exception {
        path(id.toString());
        path("preview");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept("text/plain").get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    String result = getPreview(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getPreviewString(response);
    }

    private String getPreviewString(ClientResponse response) {
        return response.getEntity(String.class);
    }

    private File getFileEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, File.class);
        }
        return response.getEntity(File.class);
    }

    public Boolean verify(Object id) throws Exception {
        path("verify");
        path(id.toString());
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
                    Boolean result = verify(id);
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

    public SuccessResponse removeCreative(Long creativeId, RecordSet<Long> groupIds) throws Exception {
        path(creativeId.toString());
        path("remove");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, groupIds);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = removeCreative(creativeId, groupIds);
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

    public RecordSet<CreativeAssociationsDTO> creativeAssociationCount(Long creativeId) throws Exception {
        path(creativeId.toString());
        path("creativeAssociationsCount");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CreativeAssociationsDTO> result = creativeAssociationCount(creativeId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }

    public RecordSet<CreativeInsertionView> creativeSchedules(String creativeId) throws Exception {
        path(creativeId);
        path("schedules");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CreativeInsertionView> result = creativeSchedules(creativeId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
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
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
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

    private BooleanResponse getCreativeBooleanResponse(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, BooleanResponse.class);
        }
        return response.getEntity(BooleanResponse.class);
    }
}

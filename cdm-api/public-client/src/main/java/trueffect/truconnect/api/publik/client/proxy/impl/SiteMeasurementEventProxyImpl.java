package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;
import trueffect.truconnect.api.commons.model.BooleanResponse;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

/**
 * Created by Siamak Marjouei
 */
public class SiteMeasurementEventProxyImpl extends ServiceProxyImpl<SmEvent> {

    public SiteMeasurementEventProxyImpl(Class<SmEvent> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    @Override
    public void handleErrors(ClientResponse response) throws Exception {
        super.handleNewErrors(response);
    }

    public RecordSet<SmPingEventDTO> getPingEventsById(Long id) throws Exception {
        path(id.toString());
        path("pingEvents");
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
                    RecordSet<SmPingEventDTO> result = getPingEventsById(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getPingEventsEntity(response);
    }

    public RecordSet<SmPingEventDTO> createPingEventsById(Long id, RecordSet<SmPingEventDTO> records) throws Exception {
        path(id.toString());
        path("pingEvents");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, records);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<SmPingEventDTO> result = createPingEventsById(id, records);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getPingEventsEntity(response);
    }

    @SuppressWarnings("unchecked")
    private RecordSet<SmPingEventDTO> getPingEventsEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }

    public BooleanResponse verifyEventNameExist(String groupId, String name) throws Exception {
        if (groupId != null) {
            query("groupId", groupId);
        }
        if (name != null) {
            query("name", name);
        }
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
                    BooleanResponse result = verifyEventNameExist(groupId, name);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return response.getEntity(BooleanResponse.class);
    }
}

package trueffect.truconnect.api.publik.client.proxy.impl;

import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.dto.SmEventDTO;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementCampaignDTO;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;

/**
 * Created by richard.jaldin on 6/3/2015. 
 * Modified by thomas.barjou on 6/8/2015.
 */
public class SiteMeasurementProxyImpl extends ServiceProxyImpl<SiteMeasurementDTO> {

    public SiteMeasurementProxyImpl(Class<SiteMeasurementDTO> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public RecordSet<SiteMeasurementCampaignDTO> getSiteMeasurementCampaigns(Long id, String associate) throws Exception {
        path(id.toString());
        path("campaigns");
        if (associate != null) {
            query("type", associate);
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
                    RecordSet<SiteMeasurementCampaignDTO> result = getSiteMeasurementCampaigns(id, associate);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getSiteMeasurementCampaignsEntity(response);
    }

    public RecordSet<SiteMeasurementCampaignDTO> updateSiteMeasurementCampaignsList(Long id, RecordSet<SiteMeasurementCampaignDTO> smcs) throws Exception {
        path(id.toString());
        path("campaigns");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, getPostData(smcs));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<SiteMeasurementCampaignDTO> result = updateSiteMeasurementCampaignsList(id, smcs);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getSiteMeasurementCampaignsEntity(response);
    }

    @SuppressWarnings("unchecked")
    private RecordSet<SiteMeasurementCampaignDTO> getSiteMeasurementCampaignsEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }

    @Override
    public void handleErrors(ClientResponse response) throws Exception {
        super.handleNewErrors(response);
    }

    public RecordSet<SmEventDTO> getSiteMeasurementEvents(Long id) throws Exception {
        path(id.toString());
        path("events");
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
                    RecordSet<SmEventDTO> result = getSiteMeasurementEvents(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getSiteMeasurementEventsEntity(response);
    }

    @SuppressWarnings("unchecked")
    private RecordSet<SmEventDTO> getSiteMeasurementEventsEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }

    public RecordSet<SmGroup> getSiteMeasurementGroups(Long id) throws Exception {
        path(id.toString());
        path("groups");
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
                    RecordSet<SmGroup> result = getSiteMeasurementGroups(id);
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
}

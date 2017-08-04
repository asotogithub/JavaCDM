package trueffect.truconnect.api.publik.client.proxy.impl;

import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.dto.DatasetMetrics;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;

public class DatasetConfigProxyImpl extends ServiceProxyImpl<DatasetConfigView> {

    public DatasetConfigProxyImpl(Class<DatasetConfigView> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public DatasetMetrics getDatasetMetricsById(Object id, String startDate, String endDate) throws Exception {
        path(id.toString());
        path("metrics");
        if (startDate != null) {
            query("startDate", startDate);
            query("endDate", endDate);
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
                    DatasetMetrics result = getDatasetMetricsById(id, startDate, endDate);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getDatasetMetricsEntity(response);
    }

    private DatasetMetrics getDatasetMetricsEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, DatasetMetrics.class);
        }
        return response.getEntity(DatasetMetrics.class);
    }
}

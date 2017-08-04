package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.dto.DatasetMetrics;
import trueffect.truconnect.api.publik.client.proxy.impl.DatasetConfigProxyImpl;

import java.util.UUID;

public class DatasetConfigPublicServiceProxy extends GenericPublicServiceProxy<DatasetConfigView> {
    public DatasetConfigPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Datasets", contentType, userName, password);
    }

    public DatasetConfigView getById(UUID id) throws Exception {
        return getProxy().getByIdThrowNewError(id);
    }

    public DatasetMetrics getDatasetMetricsById(UUID id, String startDate, String endDate) throws Exception {
        return getProxy().getDatasetMetricsById(id, startDate, endDate);
    }

    public DatasetConfigView create(DatasetConfigView in) throws Exception {
        return getProxy().saveThrowNewError(in);
    }

    public DatasetConfigView update(UUID id, DatasetConfigView in) throws Exception {
        DatasetConfigProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.updateThrowNewError(in);
    }

    @Override
    protected DatasetConfigProxyImpl getProxy() {
        DatasetConfigProxyImpl proxy = new DatasetConfigProxyImpl(DatasetConfigView.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

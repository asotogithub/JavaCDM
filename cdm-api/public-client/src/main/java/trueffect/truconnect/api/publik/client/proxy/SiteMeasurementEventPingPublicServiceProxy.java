package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.proxy.impl.SiteMeasurementEventPingProxyImpl;

/**
 * Created by Thomas Barjou on 08/12/2016
 */
public class SiteMeasurementEventPingPublicServiceProxy extends GenericPublicServiceProxy<SmPingEventDTO> {

    public SiteMeasurementEventPingPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "SiteMeasurementEventPings", contentType, userName, password);
    }

    public RecordSet<SmPingEventDTO> createPingEvents(RecordSet<SmPingEventDTO> records) throws Exception {
        SiteMeasurementEventPingProxyImpl proxy = getProxy();
        return proxy.createPingEvents(records);
    }

    public RecordSet<SmPingEventDTO> updatePingEvents(RecordSet<SmPingEventDTO> records) throws Exception {
        SiteMeasurementEventPingProxyImpl proxy = getProxy();
        return proxy.updatePingEvents(records);
    }

    public SuccessResponse deletePingEvents(RecordSet<Long> smPingIds) throws Exception {
        SiteMeasurementEventPingProxyImpl proxy = getProxy();
        return proxy.deletePingEvents(smPingIds);
    }

    protected SiteMeasurementEventPingProxyImpl getProxy() {
        SiteMeasurementEventPingProxyImpl proxy = new SiteMeasurementEventPingProxyImpl(SmPingEventDTO.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

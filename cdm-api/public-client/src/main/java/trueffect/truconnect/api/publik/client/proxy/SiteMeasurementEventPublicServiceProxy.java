package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.BooleanResponse;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.proxy.impl.SiteMeasurementEventProxyImpl;

/**
 * Created by Siamak Marjouei
 */
public class SiteMeasurementEventPublicServiceProxy extends GenericPublicServiceProxy<SmEvent> {

    public SiteMeasurementEventPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "SiteMeasurementEvents", contentType, userName, password);
    }

    public SmEvent getById(Long id) throws Exception {
        SiteMeasurementEventProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public RecordSet<SmPingEventDTO> getPingEventsById(Long id) throws Exception {
        SiteMeasurementEventProxyImpl proxy = getProxy();
        return proxy.getPingEventsById(id);
    }

    public RecordSet<SmPingEventDTO> createPingEventsById(Long id, RecordSet<SmPingEventDTO> records) throws Exception {
        SiteMeasurementEventProxyImpl proxy = getProxy();
        return proxy.createPingEventsById(id, records);
    }

    public SmEvent create(SmEvent input) throws Exception {
        SiteMeasurementEventProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public SmEvent update(Object id, SmEvent input) throws Exception {
        SiteMeasurementEventProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(Long input) throws Exception {
        SiteMeasurementEventProxyImpl proxy = getProxy();
        return proxy.delete(input);
    }

    public RecordSet<SmEvent> find(String query, Long startIndex, Long pageSize) throws Exception {
        SiteMeasurementEventProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<SmEvent> find(String query) throws Exception {
        return this.find(query, null, null);
    }

    public RecordSet<SmEvent> find() throws Exception {
        return this.find(null);
    }

    public BooleanResponse verifyEventNameExist(String groupId, String name) throws Exception {
        SiteMeasurementEventProxyImpl proxy = getProxy();
        return proxy.verifyEventNameExist(groupId, name);
    }

    protected SiteMeasurementEventProxyImpl getProxy() {
        SiteMeasurementEventProxyImpl proxy = new SiteMeasurementEventProxyImpl(SmEvent.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

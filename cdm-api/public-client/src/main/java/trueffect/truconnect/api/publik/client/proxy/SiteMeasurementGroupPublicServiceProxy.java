package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.BooleanResponse;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.proxy.impl.SiteMeasurementGroupProxyImpl;

/**
 * Created by thomas.barjou on 6/5/2015.
 */
public class SiteMeasurementGroupPublicServiceProxy extends GenericPublicServiceProxy<SmGroup> {

    public SiteMeasurementGroupPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "SiteMeasurementGroups", contentType, userName, password);
    }

    public SmGroup getById(Long id) throws Exception {
        SiteMeasurementGroupProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public SmGroup create(SmGroup input) throws Exception {
        SiteMeasurementGroupProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public SmGroup update(Object id, SmGroup input) throws Exception {
        SiteMeasurementGroupProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(Long input) throws Exception {
        SiteMeasurementGroupProxyImpl proxy = getProxy();
        return proxy.delete(input);
    }

    public RecordSet<SmGroup> find(String query, Long startIndex, Long pageSize) throws Exception {
        SiteMeasurementGroupProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<SmGroup> find(String query) throws Exception {
        return this.find(query, null, null);
    }

    public RecordSet<SmGroup> find() throws Exception {
        return this.find(null);
    }

    public BooleanResponse verifyGroupNameExist(String smId, String name) throws Exception {
        SiteMeasurementGroupProxyImpl proxy = getProxy();
        return proxy.verifyGroupNameExist(smId, name);
    }

    protected SiteMeasurementGroupProxyImpl getProxy() {
        SiteMeasurementGroupProxyImpl proxy = new SiteMeasurementGroupProxyImpl(SmGroup.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

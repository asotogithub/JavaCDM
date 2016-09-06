package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementCampaignDTO;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.dto.SmEventDTO;
import trueffect.truconnect.api.publik.client.proxy.impl.SiteMeasurementProxyImpl;

/**
 * Created by richard.jaldin on 6/3/2015.
 * Modified by thomas.barjou on 6/8/2015.
 */
public class SiteMeasurementsPublicServiceProxy extends GenericPublicServiceProxy<SiteMeasurementDTO> {

    public SiteMeasurementsPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "SiteMeasurements", contentType, userName, password);
    }

    public SiteMeasurementDTO getById(Long id) throws Exception {
        SiteMeasurementProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public SiteMeasurementDTO create(SiteMeasurementDTO input) throws Exception {
        SiteMeasurementProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public SiteMeasurementDTO update(Long id, SiteMeasurementDTO input) throws Exception {
        SiteMeasurementProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(Long input) throws Exception {
        SiteMeasurementProxyImpl proxy = getProxy();
        return proxy.delete(input);
    }

    public RecordSet<SmEventDTO> getSiteMeasurementEvents(Long id) throws Exception {
        SiteMeasurementProxyImpl proxy = getProxy();
        return proxy.getSiteMeasurementEvents(id);
    }

    public RecordSet<SiteMeasurementDTO> find(String query, Long startIndex, Long pageSize) throws Exception {
        SiteMeasurementProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<SiteMeasurementDTO> find(String query) throws Exception {
        return this.find(query, null, null);
    }

    public RecordSet<SiteMeasurementDTO> find() throws Exception {
        return this.find(null);
    }

    public RecordSet<SiteMeasurementCampaignDTO> getSiteMeasurementCampaigns(Long id, String associate) throws Exception{
        SiteMeasurementProxyImpl proxy = getProxy();
        return proxy.getSiteMeasurementCampaigns(id, associate);
    }
    
    public RecordSet<SiteMeasurementCampaignDTO> updateSiteMeasurementCampaignsList(Long id, RecordSet<SiteMeasurementCampaignDTO> smcs) throws Exception{
        SiteMeasurementProxyImpl proxy = getProxy();
        return proxy.updateSiteMeasurementCampaignsList(id, smcs);
    }

    public RecordSet<SmGroup> getSiteMeasurementGroups(Long id) throws Exception {
        SiteMeasurementProxyImpl proxy = getProxy();
        return proxy.getSiteMeasurementGroups(id);
    }

    protected SiteMeasurementProxyImpl getProxy() {
        SiteMeasurementProxyImpl proxy = new SiteMeasurementProxyImpl(SiteMeasurementDTO.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

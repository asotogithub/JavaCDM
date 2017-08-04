package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;

import trueffect.truconnect.api.commons.model.dto.CreativeAssociationsDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.publik.client.proxy.impl.CreativeProxyImpl;

import java.io.File;
import java.io.InputStream;

public class CreativePublicServiceProxy extends GenericPublicServiceProxy<Creative> {

    public CreativePublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Creatives", contentType, userName, password);
    }

    public Creative getById(int id) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public String previewById(int id) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.getPreview(id);
    }

    public File getFile(Object id) throws Exception{
        CreativeProxyImpl proxy = getProxy();
        return ((CreativeProxyImpl)proxy).getFile(id);
    }
    
    public boolean verify(Object id) throws Exception{
        CreativeProxyImpl proxy = getProxy();
        return ((CreativeProxyImpl)proxy).verify(id);
    }

    public RecordSet<CreativeAssociationsDTO> getCreativeAssociationCount(Long creativeId) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.creativeAssociationCount(creativeId);
    }

    public SuccessResponse removeCreativeFromGroups(Long creativeId, RecordSet<Long> groupIds) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.removeCreative(creativeId, groupIds);
    }

    public RecordSet<CreativeInsertionView> creativeSchedules(String creativeId) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.creativeSchedules(creativeId);
    }

    public RecordSet<Creative> find(String query, Long startIndex, Long pageSize) throws Exception{
        CreativeProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Creative> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<Creative> find() throws Exception{
        return this.find(null);
    }

    public RecordSet<Creative> findUnassociated(String campaignId, String groupId)throws Exception{
        return getProxy().getUnassociated(campaignId,groupId);
    }
    
    public Creative create(Creative input) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.save(input);
    }
    
    public boolean replaceImage(Object id, InputStream inputStream, String filename) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.replaceImage(id, inputStream, filename);
    }
    
    public SuccessResponse delete(Long creativeId, boolean recursiveDelete) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        return proxy.delete(creativeId, recursiveDelete);
    }
    
    public Creative update(Object id, Creative input) throws Exception {
        CreativeProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    protected CreativeProxyImpl getProxy() {
        CreativeProxyImpl proxy = new CreativeProxyImpl(Creative.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

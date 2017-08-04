package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.BulkCreativeInsertion;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionBulkUpdate;
import trueffect.truconnect.api.publik.client.proxy.impl.CreativeInsertionProxyImpl;

public class CreativeInsertionPublicServiceProxy extends GenericPublicServiceProxy<CreativeInsertion> {

    public CreativeInsertionPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "CreativeInsertions", contentType, userName, password);
    }

    public CreativeInsertion getById(int id) throws Exception {
        CreativeInsertionProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public CreativeInsertion create(CreativeInsertion input) throws Exception {
        CreativeInsertionProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public CreativeInsertion update(Object id, CreativeInsertion input) throws Exception {
        CreativeInsertionProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public BulkCreativeInsertion createWithCiBulk(BulkCreativeInsertion input) throws Exception {
        CreativeInsertionProxyImpl proxy = getProxy();
        return proxy.createWithCiBulk(input);
    }

    public CreativeInsertionBulkUpdate updateWithCiBulk(CreativeInsertionBulkUpdate input) throws Exception {
        CreativeInsertionProxyImpl proxy = getProxy();
        return proxy.updateWithCiBulk(input);
    }

    public SuccessResponse delete(Long creativeGroupId) throws Exception {
        CreativeInsertionProxyImpl proxy = getProxy();
        return proxy.delete(creativeGroupId);
    }

    public RecordSet<CreativeInsertion> find(String query, Long startIndex, Long pageSize) throws Exception{
        CreativeInsertionProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<CreativeInsertion> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<CreativeInsertion> find() throws Exception{
        return this.find(null);
    }

    public RecordSet<CreativeInsertion> getByCreativeGroup(Long creativeGroupId) throws Exception {
        CreativeInsertionProxyImpl proxy = getProxy();
        return proxy.getByCreativeGroup(creativeGroupId);
    }
    
    protected CreativeInsertionProxyImpl getProxy() {
        CreativeInsertionProxyImpl proxy = new CreativeInsertionProxyImpl(CreativeInsertion.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

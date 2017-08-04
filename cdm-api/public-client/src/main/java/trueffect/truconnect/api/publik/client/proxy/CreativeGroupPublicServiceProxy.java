package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeDTO;
import trueffect.truconnect.api.publik.client.proxy.impl.CreativeGroupProxyImpl;

public class CreativeGroupPublicServiceProxy extends GenericPublicServiceProxy<CreativeGroup> {

    public CreativeGroupPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "CreativeGroups", contentType, userName, password);
    }

    public CreativeGroup getById(int id) throws Exception {
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public CreativeGroup create(CreativeGroup input) throws Exception {
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public CreativeGroup update(Long id, CreativeGroup input) throws Exception {
        CreativeGroupProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public RecordSet<CreativeGroup> find(String query, Long startIndex, Long pageSize) throws Exception{
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<CreativeGroup> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<CreativeGroup> find() throws Exception{
        return this.find(null);
    }

    public void delete(Long id, boolean recursiveDelete) throws Exception {
        CreativeGroupProxyImpl proxy = getProxy();
        proxy.delete(id, recursiveDelete);
    }

    public RecordSet<CreativeGroupCreative> getCreativeGroupCreatives(Object id) throws Exception{
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.getCreativeGroupCreatives(id);
    }
    
    public CreativeGroupCreative updateCreativeGroupCreativesList(Long id, CreativeGroupCreative cgc) throws Exception{
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.updateCreativeGroupCreativesList(id, cgc);
    }

    public SuccessResponse createAssociations(CreativeGroupCreativeDTO associations) throws Exception {
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.createAssociations(associations);
    }
    
    protected CreativeGroupProxyImpl getProxy() {
        CreativeGroupProxyImpl proxy = new CreativeGroupProxyImpl(CreativeGroup.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

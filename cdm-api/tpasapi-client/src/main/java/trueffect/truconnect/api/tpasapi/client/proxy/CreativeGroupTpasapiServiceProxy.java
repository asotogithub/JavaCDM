package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.tpasapi.client.proxy.impl.CreativeGroupProxyImpl;
import trueffect.truconnect.api.tpasapi.model.CreativeGroup;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Schedule;

public class CreativeGroupTpasapiServiceProxy extends GenericTpasapiServiceProxy<CreativeGroup> {

    public CreativeGroupTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
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

    public CreativeGroup update(Object id, CreativeGroup input) throws Exception {
        CreativeGroupProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(Object id, boolean recursiveDelete) throws Exception {
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.delete(id, recursiveDelete);
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

    public CreativeGroupCreatives getCreativeGroupCreatives(Object id) throws Exception{
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.getCreativeGroupCreatives(id);
    }
    
    public CreativeGroupCreatives updateCreativeGroupCreatives(Object id, CreativeGroupCreatives cgc) throws Exception{
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.updateCreativeGroupCreatives(id, cgc);
    }

    public Schedule getSchedule(Object id) throws Exception {
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.getSchedule(id);
    }
    
    public Schedule updateSchedule(Object id, Schedule schedule) throws Exception{
        CreativeGroupProxyImpl proxy = getProxy();
        return proxy.updateSchedule(id, schedule);
    }
    
    protected CreativeGroupProxyImpl getProxy() {
        CreativeGroupProxyImpl proxy = new CreativeGroupProxyImpl(CreativeGroup.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

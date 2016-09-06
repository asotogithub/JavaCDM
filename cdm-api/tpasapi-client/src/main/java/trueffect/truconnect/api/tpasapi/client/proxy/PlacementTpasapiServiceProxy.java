package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.tpasapi.model.Placement;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;

public class PlacementTpasapiServiceProxy extends GenericTpasapiServiceProxy<Placement> {

    public PlacementTpasapiServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Placements", contentType, userName, password);
    }

    public Placement getById(int id) throws Exception {
        ServiceProxyImpl<Placement> proxy = getProxy();
        return proxy.getById(id);
    }

    public Placement create(Placement input) throws Exception {
        ServiceProxyImpl<Placement> proxy = getProxy();
        return proxy.save(input);
    }

    public Placement update(Object id, Placement input) throws Exception {
        ServiceProxyImpl<Placement> proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public RecordSet<Placement> find(String query, Long startIndex, Long pageSize) throws Exception{
        ServiceProxyImpl<Placement> proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Placement> find(String query) throws Exception{
        return this.find(query, null, null);
    }

    public RecordSet<Placement> find() throws Exception{
        return this.find(null);
    }
    
    protected ServiceProxyImpl<Placement> getProxy() {
        ServiceProxyImpl<Placement> proxy = new ServiceProxyImpl<Placement>(Placement.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

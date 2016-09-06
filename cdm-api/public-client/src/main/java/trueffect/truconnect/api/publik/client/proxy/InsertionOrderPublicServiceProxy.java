package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.BulkPackagePlacement;
import trueffect.truconnect.api.commons.model.dto.PackagePlacementView;
import trueffect.truconnect.api.publik.client.proxy.impl.InsertionOrderProxyImpl;

public class InsertionOrderPublicServiceProxy extends GenericPublicServiceProxy<InsertionOrder> {

    public InsertionOrderPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "InsertionOrders", contentType, userName, password);
    }

    public InsertionOrder getById(Long id) throws Exception {
        InsertionOrderProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public InsertionOrder create(InsertionOrder input) throws Exception {
        InsertionOrderProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public InsertionOrder update(Object id, InsertionOrder input) throws Exception {
        InsertionOrderProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(InsertionOrder input) throws Exception {
        InsertionOrderProxyImpl proxy = getProxy();
        return proxy.delete(input);
    }

    public RecordSet<InsertionOrder> find(String query, Long startIndex, Long pageSize) throws Exception {
        InsertionOrderProxyImpl proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<PackagePlacementView> findPackPlacementView(Long ioId) throws Exception {
        InsertionOrderProxyImpl proxy = getProxy();
        return proxy.findPackPlacementView(ioId);
    }

    public RecordSet<InsertionOrder> find(String query) throws Exception {
        return this.find(query, null, null);
    }

    public RecordSet<InsertionOrder> find() throws Exception {
        return this.find(null);
    }

    public InsertionOrder getByMediaBuy(Long mediaBuyId) throws Exception {
        InsertionOrderProxyImpl proxy = getProxy();
        return proxy.getByMediaBuy(mediaBuyId);
    }

    public BulkPackagePlacement createBulkPackagePlacements(Long id, BulkPackagePlacement bulk) throws Exception {
        InsertionOrderProxyImpl proxy = getProxy();
        return proxy.createBulkPackagePlacements(id, bulk);
    }

    protected InsertionOrderProxyImpl getProxy() {
        InsertionOrderProxyImpl proxy = new InsertionOrderProxyImpl(InsertionOrder.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

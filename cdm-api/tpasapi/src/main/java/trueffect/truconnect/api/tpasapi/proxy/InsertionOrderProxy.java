package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;

import trueffect.truconnect.api.commons.exceptions.ProxyException;

/**
 *
 * @author Richard Jaldin
 */
public class InsertionOrderProxy extends BaseProxy<trueffect.truconnect.api.commons.model.InsertionOrder> {

    public InsertionOrderProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.InsertionOrder.class, headers);
        path("InsertionOrders");
    }

    public trueffect.truconnect.api.commons.model.InsertionOrder getByMediaBuy(Long mediaBuyId) throws ProxyException {
        path("byMediaBuy");
        path(mediaBuyId.toString());
        return this.get();
    }

    public trueffect.truconnect.api.commons.model.InsertionOrder createDefault(trueffect.truconnect.api.commons.model.MediaBuy mediaBuy) throws ProxyException {
        trueffect.truconnect.api.commons.model.InsertionOrder io = new trueffect.truconnect.api.commons.model.InsertionOrder();
        io.setIoNumber(999999);
        io.setMediaBuyId(mediaBuy.getId());
        io.setName("Default");
        io.setStatus("Accepted");
        return this.post(io);
    }
}

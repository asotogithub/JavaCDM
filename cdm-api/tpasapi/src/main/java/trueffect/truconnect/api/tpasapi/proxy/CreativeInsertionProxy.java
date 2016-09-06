package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author Abel Soto
 */
public class CreativeInsertionProxy extends BaseProxy<trueffect.truconnect.api.commons.model.Creative> {

    public CreativeInsertionProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.Creative.class, headers);
        path("CreativeInsertions");
    }
}

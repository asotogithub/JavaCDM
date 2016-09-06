package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.tpasapi.factory.TagFactory;
import trueffect.truconnect.api.tpasapi.model.Tag;

/**
 *
 * @author Abel Soto
 */
public class TagProxy extends BaseProxy<trueffect.truconnect.api.commons.model.delivery.Tag> {

    public TagProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.delivery.Tag.class, headers);
        path("Placements");
    }

    public Tag getTagByTypeId() throws ProxyException {
        return TagFactory.toTpasapiObject(get());
    }
}

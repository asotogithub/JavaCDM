package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;
import trueffect.truconnect.api.tpasapi.factory.CreativeGroupCreativeFactory;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives;
import trueffect.truconnect.api.commons.exceptions.ProxyException;

/**
 *
 * @author Gustavo Claure
 */
public class CreativeGroupCreativeProxy extends BaseProxy<trueffect.truconnect.api.commons.model.CreativeGroupCreative> {

    public CreativeGroupCreativeProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.CreativeGroupCreative.class, headers);
        path("CreativeGroups");
    }

    public CreativeGroupCreatives updateCreativeGroup(Long id, trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives creativeGroup) throws ProxyException {
        trueffect.truconnect.api.commons.model.CreativeGroupCreative commonsCGC = CreativeGroupCreativeFactory.createPublicObject(creativeGroup);
        commonsCGC = put(commonsCGC);
        creativeGroup = CreativeGroupCreativeFactory.createTpasapiObject(commonsCGC);
        return creativeGroup;
    }
}

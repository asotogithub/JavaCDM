package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author Richard Jaldin
 */
public class MediaBuyCampaignProxy extends BaseProxy<trueffect.truconnect.api.commons.model.MediaBuyCampaign> {

    public MediaBuyCampaignProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.MediaBuyCampaign.class, headers);
        path("MediaBuys");
    }
}

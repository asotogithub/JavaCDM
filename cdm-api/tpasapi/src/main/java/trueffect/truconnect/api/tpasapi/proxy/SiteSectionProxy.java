package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author Richard Jaldin
 */
public class SiteSectionProxy extends BaseProxy<trueffect.truconnect.api.commons.model.SiteSection> {

    public SiteSectionProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.SiteSection.class, headers);
        path("SiteSections");
    }
}

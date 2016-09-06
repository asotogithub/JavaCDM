package trueffect.truconnect.api.tpasapi.proxy;

import java.util.List;
import java.util.ArrayList;
import javax.ws.rs.core.HttpHeaders;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.tpasapi.model.Site;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.factory.SiteFactory;

/**
 *
 * @author Gustavo Claure
 */
public class SiteProxy extends BaseProxy<trueffect.truconnect.api.commons.model.Site> {

    public SiteProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.Site.class, headers);
        path("Sites");
    }

    public Site getSite() throws ProxyException {
        trueffect.truconnect.api.commons.model.Site site = super.get();
        return SiteFactory.createTpasapiObject(site);
    }

    public Site putSite(Site input) throws ProxyException {
        trueffect.truconnect.api.commons.model.Site site = SiteFactory.createPublicObject(input);
        return SiteFactory.createTpasapiObject(this.put(site));
    }
    
    public Site saveSite(Site input) throws ProxyException {
        trueffect.truconnect.api.commons.model.Site site = SiteFactory.createPublicObject(input);
        return SiteFactory.createTpasapiObject(this.post(site));
    }

    public RecordSet<Site> getSites(SearchCriteria searchCriteria) throws ProxyException {
        trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.Site> records = this.get(searchCriteria);
        RecordSet<Site> result = new RecordSet<Site>();
        result.setPageSize(records.getPageSize());
        result.setStartIndex(records.getStartIndex());
        result.setTotalNumberOfRecords(records.getTotalNumberOfRecords());
        List<Site> aux = new ArrayList<Site>();
        if(records.getRecords() != null) {
            for (trueffect.truconnect.api.commons.model.Site record : records.getRecords()) {
                Site item = SiteFactory.createTpasapiObject(record);
                aux.add(item);
            }
        }
        result.setRecords(aux);
        return result;
    }
}

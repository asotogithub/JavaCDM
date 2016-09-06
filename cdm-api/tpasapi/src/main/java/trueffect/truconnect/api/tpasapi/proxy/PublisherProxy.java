package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;
import trueffect.truconnect.api.tpasapi.factory.PublisherFactory;
import trueffect.truconnect.api.tpasapi.model.Publisher;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.SearchCriteria;

/**
 *
 * @author Abel Soto
 */
public class PublisherProxy extends BaseProxy<trueffect.truconnect.api.commons.model.Publisher> {

    public PublisherProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.Publisher.class, headers);
        path("Publishers");
    }

    public Publisher getPublisher() throws ProxyException {
        trueffect.truconnect.api.commons.model.Publisher publisher = super.get();
        return PublisherFactory.publisherTpasapiObject(publisher);
    }

    public Publisher putPublisher(Publisher input) throws ProxyException {
        trueffect.truconnect.api.commons.model.Publisher publisher = PublisherFactory.createPublicObject(input);
        return PublisherFactory.publisherTpasapiObject(this.put(publisher));
    }

    public Publisher savePublisher(Publisher input) throws ProxyException {
        trueffect.truconnect.api.commons.model.Publisher publisher = PublisherFactory.createPublicObject(input);
        return PublisherFactory.publisherTpasapiObject(this.post(publisher));
    }

    public RecordSet<Publisher> getPublishers(SearchCriteria searchCriteria) throws ProxyException {
        trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.Publisher> records;
        records = get(searchCriteria);
        RecordSet<Publisher> publishers = PublisherFactory.createTpasapiObjects(records);
        return publishers;
    }
}

package trueffect.truconnect.api.tpasapi.factory;

import java.util.ArrayList;
import java.util.List;
import trueffect.truconnect.api.tpasapi.model.Publisher;
import trueffect.truconnect.api.tpasapi.model.RecordSet;

/**
 *
 * @author Abel Soto
 */
public class PublisherFactory {

    private Publisher toTpasapiObject(trueffect.truconnect.api.commons.model.Publisher publisher) {
        Publisher result = new Publisher();
        result.setAddress1(publisher.getAddress1());
        result.setAddress2(publisher.getAddress2());
        result.setAgencyId(publisher.getAgencyId());
        result.setAgencyNotes(publisher.getAgencyNotes());
        result.setCity(publisher.getCity());
        result.setCountry(publisher.getCountry());
        result.setCreatedDate(publisher.getCreatedDate());
        result.setModifiedDate(publisher.getModifiedDate());
        result.setId(publisher.getId());
        result.setName(publisher.getName());
        result.setPhoneNumber(publisher.getPhoneNumber());
        result.setState(publisher.getState());
        result.setUrl(publisher.getUrl());
        result.setZipCode(publisher.getZipCode());
        return result;
    }

    private trueffect.truconnect.api.commons.model.Publisher toPublicObject(Publisher publisher) {
        trueffect.truconnect.api.commons.model.Publisher result = new trueffect.truconnect.api.commons.model.Publisher();
        result.setAddress1(publisher.getAddress1());
        result.setAddress2(publisher.getAddress2());
        result.setAgencyId(publisher.getAgencyId());
        result.setAgencyNotes(publisher.getAgencyNotes());
        result.setCity(publisher.getCity());
        result.setCountry(publisher.getCountry());
        result.setCreatedDate(publisher.getCreatedDate());
        result.setModifiedDate(publisher.getModifiedDate());
        result.setId(publisher.getId());
        result.setName(publisher.getName());
        result.setPhoneNumber(publisher.getPhoneNumber());
        result.setState(publisher.getState());
        result.setUrl(publisher.getUrl());
        result.setZipCode(publisher.getZipCode());
        return result;
    }

    public static Publisher publisherTpasapiObject(trueffect.truconnect.api.commons.model.Publisher publisher) {
        PublisherFactory factory = new PublisherFactory();
        return factory.toTpasapiObject(publisher);
    }

    public static trueffect.truconnect.api.commons.model.Publisher createPublicObject(Publisher publisher) {
        PublisherFactory factory = new PublisherFactory();
        return factory.toPublicObject(publisher);
    }

    public static RecordSet<Publisher> createTpasapiObjects(trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.Publisher> publishers) {
        RecordSet<Publisher> records = new RecordSet<Publisher>();
        records.setPageSize(publishers.getPageSize());
        records.setStartIndex(publishers.getStartIndex());
        records.setTotalNumberOfRecords(publishers.getTotalNumberOfRecords());
        List<Publisher> aux = new ArrayList<Publisher>();
        Publisher publisher;
        PublisherFactory factory = new PublisherFactory();
        if(publishers.getRecords() != null) {
            for (trueffect.truconnect.api.commons.model.Publisher record : publishers.getRecords()) {
                publisher = factory.toTpasapiObject(record);
                aux.add(publisher);
            }
        }
        records.setRecords(aux);
        return records;
    }
}


package trueffect.truconnect.consumer.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfint;


@XmlRegistry
public class ObjectFactory {

    private final static QName _TrafficCampaignSelectedAgencyContacts_QNAME = new QName("http://tempuri.org/", "SelectedAgencyContacts");
    private final static QName _TrafficCampaignSelectedSiteContacts_QNAME = new QName("http://tempuri.org/", "SelectedSiteContacts");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tempuri
     * 
     */
    public ObjectFactory() {
    }

    public TrafficSiteMeasurement createTrafficSiteMeasurement() {
        return new TrafficSiteMeasurement();
    }

    
    public TrafficCampaign createTrafficCampaign() {
        return new TrafficCampaign();
    }

   
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "SelectedAgencyContacts", scope = TrafficCampaign.class)
    public JAXBElement<ArrayOfint> createTrafficCampaignSelectedAgencyContacts(ArrayOfint value) {
        return new JAXBElement<ArrayOfint>(_TrafficCampaignSelectedAgencyContacts_QNAME, ArrayOfint.class, TrafficCampaign.class, value);
    }

    
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "SelectedSiteContacts", scope = TrafficCampaign.class)
    public JAXBElement<ArrayOfint> createTrafficCampaignSelectedSiteContacts(ArrayOfint value) {
        return new JAXBElement<ArrayOfint>(_TrafficCampaignSelectedSiteContacts_QNAME, ArrayOfint.class, TrafficCampaign.class, value);
    }

}

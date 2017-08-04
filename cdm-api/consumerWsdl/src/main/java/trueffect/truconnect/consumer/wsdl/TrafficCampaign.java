
package trueffect.truconnect.consumer.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfint;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "entityId",
    "priorStatusId",
    "selectedAgencyContacts",
    "selectedSiteContacts",
    "campaignOwner",
    "traffickingUserId"
})
@XmlRootElement(name = "TrafficCampaign")
public class TrafficCampaign {

    @XmlElement(name = "EntityId")
    protected Integer entityId;
    @XmlElement(name = "PriorStatusId")
    protected Integer priorStatusId;
    @XmlElementRef(name = "SelectedAgencyContacts", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfint> selectedAgencyContacts;
    @XmlElementRef(name = "SelectedSiteContacts", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfint> selectedSiteContacts;
    @XmlElement(name = "CampaignOwner")
    protected Integer campaignOwner;
    @XmlElement(name = "TraffickingUserId")
    protected Integer traffickingUserId;


    public Integer getEntityId() {
        return entityId;
    }


    public void setEntityId(Integer value) {
        this.entityId = value;
    }


    public Integer getPriorStatusId() {
        return priorStatusId;
    }


    public void setPriorStatusId(Integer value) {
        this.priorStatusId = value;
    }


    public JAXBElement<ArrayOfint> getSelectedAgencyContacts() {
        return selectedAgencyContacts;
    }


    public void setSelectedAgencyContacts(JAXBElement<ArrayOfint> value) {
        this.selectedAgencyContacts = value;
    }


    public JAXBElement<ArrayOfint> getSelectedSiteContacts() {
        return selectedSiteContacts;
    }


    public void setSelectedSiteContacts(JAXBElement<ArrayOfint> value) {
        this.selectedSiteContacts = value;
    }

    public Integer getCampaignOwner() {
        return campaignOwner;
    }


    public void setCampaignOwner(Integer value) {
        this.campaignOwner = value;
    }


    public Integer getTraffickingUserId() {
        return traffickingUserId;
    }

    public void setTraffickingUserId(Integer value) {
        this.traffickingUserId = value;
    }

}

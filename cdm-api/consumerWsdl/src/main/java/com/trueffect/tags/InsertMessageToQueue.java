
package com.trueffect.tags;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "campaignId",
    "agencyId",
    "sessionKey"
})
@XmlRootElement(name = "InsertMessageToQueue")
public class InsertMessageToQueue {

    @XmlElement(name = "CampaignId")
    protected Integer campaignId;
    @XmlElement(name = "AgencyId")
    protected Integer agencyId;
    @XmlElementRef(name = "SessionKey", namespace = "http://tags.trueffect.com/", type = JAXBElement.class)
    protected JAXBElement<String> sessionKey;

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer value) {
        this.campaignId = value;
    }

    public Integer getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Integer value) {
        this.agencyId = value;
    }

    public JAXBElement<String> getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(JAXBElement<String> value) {
        this.sessionKey = value;
    }

}

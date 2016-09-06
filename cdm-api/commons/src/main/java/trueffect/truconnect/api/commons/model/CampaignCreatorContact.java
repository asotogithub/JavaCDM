package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author abel.soto
 */
@XmlRootElement(name="CampaignCreatorConctact")
public class CampaignCreatorContact{
    
    @TableFieldMapping(table = "CONTACT", field = "CONTACT_ID")
    private Long contactId;
    //TODO, check stored procedure
    @TableFieldMapping(table = "CONTACT", field = "FNAME")
    private String contactName;
    @TableFieldMapping(table = "CONTACT", field = "EMAIL")
    private String contactEmail;

    public CampaignCreatorContact() {
    }

    public CampaignCreatorContact(Long contactId, String contactName, String contactEmail) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Override
    public String toString() {
        return "CampaignCreatorContact{" + "contactId=" + contactId + ", contactName=" + contactName + ", contactEmail=" + contactEmail + '}';
    }
    
}

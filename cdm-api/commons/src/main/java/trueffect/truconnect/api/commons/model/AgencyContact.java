package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

@XmlRootElement(name = "Agencycontact")
public class AgencyContact {

    @TableFieldMapping(table = "AGENCY_CONTACT", field = "AGENCY_ID")
    private Long agencyId;
    @TableFieldMapping(table = "AGENCY_CONTACT", field = "CONTACT_ID")
    private Long contactId;
    @TableFieldMapping(table = "AGENCY_CONTACT", field = "TYPE_ID")
    private Long typeId;
    @TableFieldMapping(table = "AGENCY_CONTACT", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "AGENCY_CONTACT", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "AGENCY_CONTACT", field = "LOGICAL_DELETE")
    private String logicalDelete;

    public AgencyContact() {
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getCreatedTpwsKey() {
        return createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

    public String getModifiedTpwsKey() {
        return modifiedTpwsKey;
    }

    public void setModifiedTpwsKey(String modifiedTpwsKey) {
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    @Override
    public String toString() {
        return "AgencyContact [agencyId=" + agencyId + ", contactId="
                + contactId + ", typeId=" + typeId + ", createdTpwsKey="
                + createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
                + ", logicalDelete=" + logicalDelete + "]";
    }
}

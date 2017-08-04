package trueffect.truconnect.api.commons.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

@XmlRootElement(name = "PlacementStatus")
public class PlacementStatus {

	@TableFieldMapping(table = "PLACEMENT_STATUS", field = "PLACEMENT_ID")
    private Long placementId;
    @TableFieldMapping(table = "PLACEMENT_STATUS", field = "STATUS_ID")
    private Long statusId;
    @TableFieldMapping(table = "PLACEMENT_STATUS", field = "STATUS_DATE")
    private Date statusDate;
    @TableFieldMapping(table = "PLACEMENT_STATUS", field = "CONTACT_ID")
    private Long contactId;
    @TableFieldMapping(table = "PLACEMENT_STATUS", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "PLACEMENT_STATUS", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "PLACEMENT_STATUS", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "PLACEMENT_STATUS", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "PLACEMENT_STATUS", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "STATUS_TYPES", field = "STATUS_NAME")
    private String statusName;

    public PlacementStatus() {    }

    public PlacementStatus(Long placementId, Long statusId, Date statusDate,
			Long contactId, String statusName) {
		this.placementId = placementId;
		this.statusId = statusId;
		this.statusDate = statusDate;
		this.contactId = contactId;
		this.statusName = statusName;
	}
    
    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
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

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

	@Override
	public String toString() {
		return "PlacementStatus [placementId=" + placementId + ", statusId="
				+ statusId + ", statusDate=" + statusDate + ", contactId="
				+ contactId + ", logicalDelete=" + logicalDelete
				+ ", createdTpwsKey=" + createdTpwsKey + ", modifiedTpwsKey="
				+ modifiedTpwsKey + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + ", statusName="
				+ statusName + "]";
	}
}

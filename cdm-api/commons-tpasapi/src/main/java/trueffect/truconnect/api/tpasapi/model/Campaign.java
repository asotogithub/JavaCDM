package trueffect.truconnect.api.tpasapi.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author anibal.zapata
 */
@XmlRootElement(name = "Campaign")
public class Campaign {

    private Long id;
    private Long agencyId;
    private Long advertiserId;
    private Long brandId;
    private String name;
    private String dupName;
    private Long statusId;
    private Date startDate;
    private Date endDate;
    private String objective;
    private String description;
    private String agencyNotes;
    private Long contactId;
    private Long trafficToOwner;
    private Long cookieDomainId;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;
    private Long resourcePathId;
    private String isHidden;
    
    private Long mediaBuyId;

    public Campaign() {
    }

    public Campaign(Long id, Long statusId,
            Long agencyId, Long brandId, Long advertiserId,
            String name, String createdTpwsKey, String modifiedTpwsKey) {
        this.id = id;
        this.statusId = statusId;
        this.agencyId = agencyId;
        this.brandId = brandId;
        this.advertiserId = advertiserId;
        this.name = name;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public Campaign(Long id, Long cookieDomainId,
            Long statusId, Long agencyId, Long brandId,
            Long contactId, Long advertiserId, String name,
            String dupName, Date startDate, Date endDate,
            String objective, String description, String agencyNotes,
            Long trafficToOwner, String logicalDelete,
            String createdTpwsKey, String modifiedTpwsKey, Date createdDate,
            Date modifiedDate, Long resourcePathId) {
        this.id = id;
        this.cookieDomainId = cookieDomainId;
        this.statusId = statusId;
        this.agencyId = agencyId;
        this.brandId = brandId;
        this.contactId = contactId;
        this.advertiserId = advertiserId;
        this.name = name;
        this.dupName = dupName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.objective = objective;
        this.description = description;
        this.agencyNotes = agencyNotes;
        this.trafficToOwner = trafficToOwner;
        this.logicalDelete = logicalDelete;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.resourcePathId = resourcePathId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCookieDomainId() {
        return this.cookieDomainId;
    }

    public void setCookieDomainId(Long cookieDomainId) {
        this.cookieDomainId = cookieDomainId;
    }

    public Long getStatusId() {
        return this.statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getAgencyId() {
        return this.agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getBrandId() {
        return this.brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getContactId() {
        return this.contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public Long getAdvertiserId() {
        return this.advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDupName() {
        return this.dupName;
    }

    public void setDupName(String dupName) {
        this.dupName = dupName;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getObjective() {
        return this.objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAgencyNotes() {
        return this.agencyNotes;
    }

    public void setAgencyNotes(String agencyNotes) {
        this.agencyNotes = agencyNotes;
    }

    public Long getTrafficToOwner() {
        return this.trafficToOwner;
    }

    public void setTrafficToOwner(Long trafficToOwner) {
        this.trafficToOwner = trafficToOwner;
    }

    public String getLogicalDelete() {
        return this.logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    public String getCreatedTpwsKey() {
        return this.createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

    public String getModifiedTpwsKey() {
        return this.modifiedTpwsKey;
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

    public Long getResourcePathId() {
        return this.resourcePathId;
    }

    public void setResourcePathId(Long resourcePathId) {
        this.resourcePathId = resourcePathId;
    }

    public Long getMediaBuyId() {
        return mediaBuyId;
    }

    public void setMediaBuyId(Long mediaBuyId) {
        this.mediaBuyId = mediaBuyId;
    }

    public String getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
    }

	@Override
	public String toString() {
		return "Campaign [id=" + id + ", agencyId=" + agencyId
				+ ", advertiserId=" + advertiserId + ", brandId=" + brandId
				+ ", name=" + name + ", dupName=" + dupName + ", statusId="
				+ statusId + ", startDate=" + startDate + ", endDate="
				+ endDate + ", objective=" + objective + ", description="
				+ description + ", agencyNotes=" + agencyNotes + ", contactId="
				+ contactId + ", trafficToOwner=" + trafficToOwner
				+ ", cookieDomainId=" + cookieDomainId + ", logicalDelete="
				+ logicalDelete + ", createdTpwsKey=" + createdTpwsKey
				+ ", modifiedTpwsKey=" + modifiedTpwsKey + ", createdDate="
				+ createdDate + ", modifiedDate=" + modifiedDate
				+ ", resourcePathId=" + resourcePathId + ", isHidden="
				+ isHidden + ", mediaBuyId=" + mediaBuyId + "]";
	}
}
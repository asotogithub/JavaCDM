package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Campaign")
public class Campaign {

    @TableFieldMapping(table = "CAMPAIGN", field = "CAMPAIGN_ID")
    private Long id;
    @TableFieldMapping(table = "CAMPAIGN", field = "AGENCY_ID")
    private Long agencyId;
    @TableFieldMapping(table = "CAMPAIGN", field = "ADVERTISER_ID")
    private Long advertiserId;
    @TableFieldMapping(table = "CAMPAIGN", field = "BRAND_ID")
    private Long brandId;
    @TableFieldMapping(table = "CAMPAIGN", field = "CAMPAIGN_NAME")
    private String name;
    @TableFieldMapping(table = "CAMPAIGN", field = "DUP_CAMPAIGN_NAME")
    private String dupName;
    @TableFieldMapping(table = "CAMPAIGN", field = "CAMPAIGN_STATUS_ID")
    private Long statusId;
    @TableFieldMapping(table = "CAMPAIGN", field = "START_DATE")
    private Date startDate;
    @TableFieldMapping(table = "CAMPAIGN", field = "END_DATE")
    private Date endDate;
    @TableFieldMapping(table = "CAMPAIGN", field = "OBJECTIVE")
    private String objective;
    @TableFieldMapping(table = "CAMPAIGN", field = "DESCRIPTION")
    private String description;
    @TableFieldMapping(table = "CAMPAIGN", field = "AGENCY_NOTES")
    private String agencyNotes;
    @TableFieldMapping(table = "CAMPAIGN", field = "CONTACT_ID")
    private Long contactId;
    @TableFieldMapping(table = "CAMPAIGN", field = "TRAFFIC_TO_OWNER")
    private Long trafficToOwner;
    @TableFieldMapping(table = "CAMPAIGN", field = "COOKIE_DOMAIN_ID")
    private Long cookieDomainId;
    @TableFieldMapping(table = "CAMPAIGN", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "CAMPAIGN", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "CAMPAIGN", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "CAMPAIGN", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "CAMPAIGN", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "CAMPAIGN", field = "RESOURCE_PATH_ID")
    private Long resourcePathId;
    @TableFieldMapping(table = "CAMPAIGN", field = "IS_HIDDEN")
    private String isHidden;
    @TableFieldMapping(table = "CAMPAIGN", field = "IS_ACTIVE")
    private String isActive;
    @TableFieldMapping(table = "MEDIA_BUY", field = "OVERALL_BUDGET")
    private Double overallBudget;
    
    private Long mediaBuyId;
    private String userId;

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

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public Double getOverallBudget() {
        return overallBudget;
    }

    public void setOverallBudget(Double overallBudget) {
        this.overallBudget = overallBudget;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

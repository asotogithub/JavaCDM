package trueffect.truconnect.api.standalone.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Campaign")
@XmlType(propOrder = {"id", "name", "statusId", "startDate", "endDate", 
        "objective", "description", "agencyNotes", "contactId", 
        "trafficToOwner", "cookieDomain", "logicalDelete", "createdTpwsKey",
        "modifiedTpwsKey", "createdDate", "modifiedDate", "resourcePathId",
        "isHidden", "mediaBuy", "creatives", "creativeGroups", 
        "creativeGroupCreatives", "placements"})
@XmlSeeAlso({CookieDomain.class, MediaBuy.class, Creative.class, 
    CreativeGroup.class, CreativeGroupCreative.class, Placement.class})
public class Campaign {

    private Long id;
    private String name;
    private Long statusId;
    private Date startDate;
    private Date endDate;
    private String objective;
    private String description;
    private String agencyNotes;
    private Long contactId;
    private Long trafficToOwner;
    private CookieDomain cookieDomain;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;
    private Long resourcePathId;
    private String isHidden;
    private MediaBuy mediaBuy;
    private List<Creative> creatives;
    private List<CreativeGroup> creativeGroups;
    private List<CreativeGroupCreative> creativeGroupCreatives;
    private List<Placement> placements;

    public Campaign() {
    }

    public Campaign(Long id, Long statusId,
            String name, String createdTpwsKey, String modifiedTpwsKey) {
        this.id = id;
        this.statusId = statusId;
        this.name = name;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public Campaign(Long id, CookieDomain cookieDomain,
            Long statusId, Long contactId, String name,
            String dupName, Date startDate, Date endDate,
            String objective, String description, String agencyNotes,
            Long trafficToOwner, String logicalDelete,
            String createdTpwsKey, String modifiedTpwsKey, Date createdDate,
            Date modifiedDate, Long resourcePathId) {
        this.id = id;
        this.cookieDomain = cookieDomain;
        this.statusId = statusId;
        this.contactId = contactId;
        this.name = name;
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

    public CookieDomain getCookieDomain() {
        return this.cookieDomain;
    }

    public void setCookieDomain(CookieDomain cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public Long getStatusId() {
        return this.statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getContactId() {
        return this.contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public MediaBuy getMediaBuy() {
        return mediaBuy;
    }

    public void setMediaBuy(MediaBuy mediaBuy) {
        this.mediaBuy = mediaBuy;
    }

    public String getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
    }

    @XmlElementWrapper(name = "creatives")
    @XmlAnyElement(lax = true)
    public List<Creative> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<Creative> creatives) {
        this.creatives = creatives;
    }

    @XmlElementWrapper(name = "creativeGroups")
    @XmlAnyElement(lax = true)
    public List<CreativeGroup> getCreativeGroups() {
        return creativeGroups;
    }

    public void setCreativeGroups(List<CreativeGroup> creativeGroups) {
        this.creativeGroups = creativeGroups;
    }

    @XmlElementWrapper(name = "creativeGroupCreatives")
    @XmlAnyElement(lax = true)
    public List<CreativeGroupCreative> getCreativeGroupCreatives() {
        return creativeGroupCreatives;
    }

    public void setCreativeGroupCreatives(
            List<CreativeGroupCreative> creativeGroupCreatives) {
        this.creativeGroupCreatives = creativeGroupCreatives;
    }

    @XmlElementWrapper(name = "placements")
    @XmlAnyElement(lax = true)
    public List<Placement> getPlacements() {
        return placements;
    }

    public void setPlacements(List<Placement> placements) {
        this.placements = placements;
    }

    @Override
    public String toString() {
        return "Campaign [id=" + id + ", name=" + name + ", statusId="
                + statusId + ", startDate=" + startDate + ", endDate="
                + endDate + ", objective=" + objective + ", description="
                + description + ", agencyNotes=" + agencyNotes + ", contactId="
                + contactId + ", trafficToOwner=" + trafficToOwner
                + ", cookieDomain=" + cookieDomain + ", logicalDelete="
                + logicalDelete + ", createdTpwsKey=" + createdTpwsKey
                + ", modifiedTpwsKey=" + modifiedTpwsKey + ", createdDate="
                + createdDate + ", modifiedDate=" + modifiedDate
                + ", resourcePathId=" + resourcePathId + ", isHidden="
                + isHidden + ", mediaBuy=" + mediaBuy + ", creatives="
                + creatives + ", creativeGroups=" + creativeGroups
                + ", creativeGroupCreatives=" + creativeGroupCreatives
                + ", placements=" + placements + "]";
    }
}

package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.Clickthrough;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * View object to simplify data transfer to the UI.
 */
@XmlRootElement(name = "CreativeInsertionView")
public class CreativeInsertionView {
    // Campaign fields
    private Long campaignId;

    // Creative Insertion fields
    private Long id;
    private Long creativeInsertionRootId;
    private Date createdDate;
    private String createdTpwsKey;
    private Date endDate;
    private String logicalDelete;
    private Date modifiedDate;
    private String modifiedTpwsKey;
    private Long released;
    private Long sequence;
    private Date startDate;
    private String timeZone;
    private Long weight;
    private String primaryClickthrough;
    private List<Clickthrough> additionalClickthroughs;

    // Creative fields
    private Long creativeId;
    private String filename;
    private String creativeAlias;
    private String creativeType;
    private String creativeSize;

    // Creative Group fields
    private Long creativeGroupId;
    private String creativeGroupName;
    private Long creativeGroupWeight;
    private Long creativeGroupWeightEnabled;
    private String creativeGroupRotationType;
    private Long creativeGroupDoGeoTargeting;
    private Long creativeGroupDoCookieTargeting;
    private Long creativeGroupFrequencyCap;
    private Long creativeGroupFrequencyCapWindow;
    private Long creativeGroupPriority;
    private String creativeGroupDaypartTarget;
    private Long creativeGroupDoDaypartTarget;

    // Placement fields
    private Long placementId;
    private Date placementEndDate;
    private String placementName;
    private Date placementStartDate;
    private String placementStatus;

    // Site fields
    private Long siteId;
    private String siteName;

    // Site Section fields
    private Long siteSectionId;
    private String siteSectionName;

    //Size fields
    private String sizeName;

    //Association fields
    private Long placementAssociationsWithCreativeGroups;
    private Long creativeGroupAssociationsWithPlacements;
    private Long creativeGroupAssociationsWithCreatives;
    private Long creativeAssociationsWithPlacements;
    private Long creativeAssociationsWithCreativeGroups;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreativeInsertionRootId() {
        return creativeInsertionRootId;
    }

    public void setCreativeInsertionRootId(Long creativeInsertionRootId) {
        this.creativeInsertionRootId = creativeInsertionRootId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedTpwsKey() {
        return createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedTpwsKey() {
        return modifiedTpwsKey;
    }

    public void setModifiedTpwsKey(String modifiedTpwsKey) {
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public Long getReleased() {
        return released;
    }

    public void setReleased(Long released) {
        this.released = released;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public String getCreativeAlias() {
        return creativeAlias;
    }

    public void setCreativeAlias(String creativeAlias) {
        this.creativeAlias = creativeAlias;
    }

    public String getCreativeType() {
        return creativeType;
    }

    public void setCreativeType(String creativeType) {
        this.creativeType = creativeType;
    }

    public String getCreativeSize() {
        return creativeSize;
    }

    public void setCreativeSize(String creativeSize) {
        this.creativeSize = creativeSize;
    }

    public String getPrimaryClickthrough() {
        return primaryClickthrough;
    }

    public void setPrimaryClickthrough(String primaryClickthrough) {
        this.primaryClickthrough = primaryClickthrough;
    }

    public String getCreativeGroupName() {
        return creativeGroupName;
    }

    public void setCreativeGroupName(String creativeGroupName) {
        this.creativeGroupName = creativeGroupName;
    }

    public Long getCreativeGroupId() {
        return creativeGroupId;
    }

    public void setCreativeGroupId(Long creativeGroupId) {
        this.creativeGroupId = creativeGroupId;
    }

    public Long getCreativeGroupWeight() {
        return creativeGroupWeight;
    }

    public void setCreativeGroupWeight(Long creativeGroupWeight) {
        this.creativeGroupWeight = creativeGroupWeight;
    }

    public Long getCreativeGroupWeightEnabled() {
        return creativeGroupWeightEnabled;
    }

    public void setCreativeGroupWeightEnabled(Long creativeGroupWeightEnabled) {
        this.creativeGroupWeightEnabled = creativeGroupWeightEnabled;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    public Date getPlacementEndDate() {
        return placementEndDate;
    }

    public void setPlacementEndDate(Date placementEndDate) {
        this.placementEndDate = placementEndDate;
    }

    public String getPlacementName() {
        return placementName;
    }

    public void setPlacementName(String placementName) {
        this.placementName = placementName;
    }

    public Date getPlacementStartDate() {
        return placementStartDate;
    }

    public void setPlacementStartDate(Date placementStartDate) {
        this.placementStartDate = placementStartDate;
    }

    public String getPlacementStatus() {
        return placementStatus;
    }

    public void setPlacementStatus(String placementStatus) {
        this.placementStatus = placementStatus;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getSiteSectionId() {
        return siteSectionId;
    }

    public void setSiteSectionId(Long siteSectionId) {
        this.siteSectionId = siteSectionId;
    }

    public String getSiteSectionName() {
        return siteSectionName;
    }

    public void setSiteSectionName(String siteSectionName) {
        this.siteSectionName = siteSectionName;
    }

    public List<Clickthrough> getAdditionalClickthroughs() {
        return additionalClickthroughs;
    }

    public void setAdditionalClickthroughs(List<Clickthrough> additionalClickthroughs) {
        this.additionalClickthroughs = additionalClickthroughs;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCreativeGroupRotationType() {
        return creativeGroupRotationType;
    }

    public void setCreativeGroupRotationType(String creativeGroupRotationType) {
        this.creativeGroupRotationType = creativeGroupRotationType;
    }

    public Long getCreativeGroupDoGeoTargeting() {
        return creativeGroupDoGeoTargeting;
    }

    public void setCreativeGroupDoGeoTargeting(Long creativeGroupDoGeoTargeting) {
        this.creativeGroupDoGeoTargeting = creativeGroupDoGeoTargeting;
    }

    public Long getCreativeGroupDoCookieTargeting() {
        return creativeGroupDoCookieTargeting;
    }

    public void setCreativeGroupDoCookieTargeting(Long creativeGroupDoCookieTargeting) {
        this.creativeGroupDoCookieTargeting = creativeGroupDoCookieTargeting;
    }

    public Long getCreativeGroupFrequencyCap() {
        return creativeGroupFrequencyCap;
    }

    public void setCreativeGroupFrequencyCap(Long creativeGroupFrequencyCap) {
        this.creativeGroupFrequencyCap = creativeGroupFrequencyCap;
    }

    public Long getCreativeGroupFrequencyCapWindow() {
        return creativeGroupFrequencyCapWindow;
    }

    public void setCreativeGroupFrequencyCapWindow(Long creativeGroupFrequencyCapWindow) {
        this.creativeGroupFrequencyCapWindow = creativeGroupFrequencyCapWindow;
    }

    public Long getCreativeGroupPriority() {
        return creativeGroupPriority;
    }

    public void setCreativeGroupPriority(Long creativeGroupPriority) {
        this.creativeGroupPriority = creativeGroupPriority;
    }

    public String getCreativeGroupDaypartTarget() {
        return creativeGroupDaypartTarget;
    }

    public void setCreativeGroupDaypartTarget(String creativeGroupDaypartTarget) {
        this.creativeGroupDaypartTarget = creativeGroupDaypartTarget;
    }
    
    public Long getCreativeGroupDoDaypartTarget() {
        return creativeGroupDoDaypartTarget;
    }

    public void setCreativeGroupDoDaypartTarget(Long creativeGroupDoDaypartTarget) {
        this.creativeGroupDoDaypartTarget = creativeGroupDoDaypartTarget;
    }

    public Long getPlacementAssociationsWithCreativeGroups() {
        return placementAssociationsWithCreativeGroups;
    }

    public void setPlacementAssociationsWithCreativeGroups(Long placementAssociationsWithCreativeGroups) {
        this.placementAssociationsWithCreativeGroups = placementAssociationsWithCreativeGroups;
    }

    public Long getCreativeGroupAssociationsWithPlacements() {
        return creativeGroupAssociationsWithPlacements;
    }

    public void setCreativeGroupAssociationsWithPlacements(Long creativeGroupAssociationsWithPlacements) {
        this.creativeGroupAssociationsWithPlacements = creativeGroupAssociationsWithPlacements;
    }

    public Long getCreativeGroupAssociationsWithCreatives() {
        return creativeGroupAssociationsWithCreatives;
    }

    public void setCreativeGroupAssociationsWithCreatives(Long creativeGroupAssociationsWithCreatives) {
        this.creativeGroupAssociationsWithCreatives = creativeGroupAssociationsWithCreatives;
    }

    public Long getCreativeAssociationsWithPlacements() {
        return creativeAssociationsWithPlacements;
    }

    public void setCreativeAssociationsWithPlacements(Long creativeAssociationsWithPlacements) {
        this.creativeAssociationsWithPlacements = creativeAssociationsWithPlacements;
    }

    public Long getCreativeAssociationsWithCreativeGroups() {
        return creativeAssociationsWithCreativeGroups;
    }

    public void setCreativeAssociationsWithCreativeGroups(Long creativeAssociationsWithCreativeGroups) {
        this.creativeAssociationsWithCreativeGroups = creativeAssociationsWithCreativeGroups;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CreativeInsertionView) {
            CreativeInsertionView ci = (CreativeInsertionView) obj;
            return getCampaignId().longValue() == ci.getCampaignId().longValue() &&
                    getCreativeId().longValue() == ci.getCreativeId().longValue() &&
                    getCreativeGroupId().longValue() == ci.getCreativeGroupId().longValue() &&
                    getPlacementId().longValue() == ci.getPlacementId().longValue();
        }
        return super.equals(obj);
    }
}

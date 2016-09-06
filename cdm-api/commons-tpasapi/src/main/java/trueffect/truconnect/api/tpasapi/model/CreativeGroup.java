package trueffect.truconnect.api.tpasapi.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "CreativeGroup")
@XmlType(propOrder = {"id", "campaignId", "name", "impressionCap", "isDefault",
    "doCookieTargeting", "cookieTarget", "doDaypartTargeting", "daypartTarget",
    "rotationType", "weight", "doGeoTargeting", "targetValueIds", "isReleased", 
    "enableGroupWeight", "priority", "enableFrequencyCap", "frequencyCap", 
    "frequencyCapWindow", "externalId", "createdDate", "modifiedDate"})

public class CreativeGroup {

    private Long id;
    private Long campaignId;
    private String name;
    private Long impressionCap;
    private Boolean isDefault;
    private Boolean doCookieTargeting;
    private String cookieTarget;
    private Boolean doDaypartTargeting;
    private String daypartTarget;
    private String rotationType;
    private Long weight;
    private Boolean doGeoTargeting;
    private CreativeGroupTarget targetValueIds;
    private Boolean isReleased;
    private Boolean enableGroupWeight;
    private Long priority;
    private Boolean enableFrequencyCap;
    private Long frequencyCap;
    private Long frequencyCapWindow;
    private String externalId;
    private Date createdDate;
    private Date modifiedDate;

    public CreativeGroup() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getImpressionCap() {
        return impressionCap;
    }

    public void setImpressionCap(Long impressionCap) {
        this.impressionCap = impressionCap;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getDoGeoTargeting() {
        return doGeoTargeting;
    }

    public void setDoGeoTargeting(Boolean doGeoTargeting) {
        this.doGeoTargeting = doGeoTargeting;
    }

    public Boolean getIsReleased() {
        return isReleased;
    }

    public void setIsReleased(Boolean isReleased) {
        this.isReleased = isReleased;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @XmlElement(name = "geoTarget")
    public CreativeGroupTarget getTargetValueIds() {
        return targetValueIds;
    }

    public void setTargetValueIds(CreativeGroupTarget targetValueIds) {
        this.targetValueIds = targetValueIds;
    }

    public String getRotationType() {
        return rotationType;
    }

    public void setRotationType(String rotationType) {
        this.rotationType = rotationType;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Boolean getDoCookieTargeting() {
        return doCookieTargeting;
    }

    public void setDoCookieTargeting(Boolean doCookieTargeting) {
        this.doCookieTargeting = doCookieTargeting;
    }

    public String getCookieTarget() {
        return cookieTarget;
    }

    public void setCookieTarget(String cookieTarget) {
        this.cookieTarget = cookieTarget;
    }

    public Boolean getDoDaypartTargeting() {
        return doDaypartTargeting;
    }

    public void setDoDaypartTargeting(Boolean doDaypartTargeting) {
        this.doDaypartTargeting = doDaypartTargeting;
    }

    public String getDaypartTarget() {
        return daypartTarget;
    }

    public void setDaypartTarget(String daypartTarget) {
        this.daypartTarget = daypartTarget;
    }

    public Boolean getEnableGroupWeight() {
        return enableGroupWeight;
    }

    public void setEnableGroupWeight(Boolean enableGroupWeight) {
        this.enableGroupWeight = enableGroupWeight;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Boolean getEnableFrequencyCap() {
        return enableFrequencyCap;
    }

    public void setEnableFrequencyCap(Boolean enableFrequencyCap) {
        this.enableFrequencyCap = enableFrequencyCap;
    }

    public Long getFrequencyCap() {
        return frequencyCap;
    }

    public void setFrequencyCap(Long frequencyCap) {
        this.frequencyCap = frequencyCap;
    }

    public Long getFrequencyCapWindow() {
        return frequencyCapWindow;
    }

    public void setFrequencyCapWindow(Long frequencyCapWindow) {
        this.frequencyCapWindow = frequencyCapWindow;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return "CreativeGroup [id=" + id + ", campaignId=" + campaignId
                + ", name=" + name + ", impressionCap=" + impressionCap
                + ", isDefault=" + isDefault + ", doCookieTargeting="
                + doCookieTargeting + ", cookieTarget=" + cookieTarget
                + ", doDaypartTargeting=" + doDaypartTargeting
                + ", daypartTarget=" + daypartTarget + ", rotationType="
                + rotationType + ", weight=" + weight + ", doGeoTargeting="
                + doGeoTargeting + ", targetValueIds=" + targetValueIds
                + ", isReleased=" + isReleased + ", enableGroupWeight="
                + enableGroupWeight + ", priority=" + priority
                + ", enableFrequencyCap=" + enableFrequencyCap
                + ", frequencyCap=" + frequencyCap + ", frequencyCapWindow="
                + frequencyCapWindow + ", externalId=" + externalId
                + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + "]";
    }
}

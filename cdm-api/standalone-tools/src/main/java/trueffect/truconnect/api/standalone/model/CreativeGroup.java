package trueffect.truconnect.api.standalone.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "CreativeGroup")
@XmlType(propOrder = {"id", "name", "rotationType", "impressionCap", "clickthroughCap", 
        "weight", "isReleased", "isDefault", "cookieTarget", "doOptimization", 
        "optimizationType", "optimizationSpeed", "minOptimizationWeight", "doGeoTargeting",
        "doCookieTargeting", "doStoryboarding", "logicalDelete", "createdTpwsKey", 
        "modifiedTpwsKey", "createdDate", "modifiedDate", "daypartTarget", "doDaypartTargeting",
        "creativeGroupTargets", "enableGroupWeight", "priority", "enableFrequencyCap", 
        "frequencyCap", "frequencyCapWindow", "scheduleSet"})
@XmlSeeAlso({CreativeGroupTarget.class, ScheduleSet.class})
public class CreativeGroup {

    private Long id;
    private String name;
    private String rotationType;
    private Long impressionCap;
    private Long clickthroughCap;
    private Long weight;
    private Long isReleased;
    private Long isDefault;
    private String cookieTarget;
    private Long doOptimization;
    private String optimizationType;
    private String optimizationSpeed;
    private Long minOptimizationWeight;
    private Long doGeoTargeting;
    private Long doCookieTargeting;
    private Long doStoryboarding;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;
    private String daypartTarget;
    private Long doDaypartTargeting;
    private CreativeGroupTarget creativeGroupTargets;
    private Long enableGroupWeight;
    private Long priority;
    private Long enableFrequencyCap;
    private Long frequencyCap;
    private Long frequencyCapWindow;
    private ScheduleSet scheduleSet;

    public CreativeGroup() {
    }

    public CreativeGroup(Long id, String name, String rotationType, Long impressionCap,
            Long clickthroughCap, Long weight, Long isReleased,
            Long doOptimization, Long minOptimizationWeight,
            Long doGeoTargeting, Long doCookieTarget,
            Long doStoryboarding, String createdTpwsKey,
            String modifiedTpwsKey) {
        this.id = id;
        this.name = name;
        this.rotationType = rotationType;
        this.impressionCap = impressionCap;
        this.clickthroughCap = clickthroughCap;
        this.weight = weight;
        this.isReleased = isReleased;
        this.doOptimization = doOptimization;
        this.minOptimizationWeight = minOptimizationWeight;
        this.doGeoTargeting = doGeoTargeting;
        this.doCookieTargeting = doCookieTarget;
        this.doStoryboarding = doStoryboarding;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public CreativeGroup(Long id,
            String name, String rotationType, Long impressionCap,
            Long clickthroughCap, Long weight, Long isReleased,
            Long isDefault, String cookieTarget,
            Long doOptimization, String optimizationType,
            String optimizationSpeed, Long minOptimizationWeight,
            Long doGeoTargeting, Long doCookieTargeting,
            Long doStoryboarding, String logicalDelete,
            String createdTpwsKey, String modifiedTpwsKey,
            String daypartTarget, Long doDaypartTargeting) {
        this.id = id;
        this.name = name;
        this.rotationType = rotationType;
        this.impressionCap = impressionCap;
        this.clickthroughCap = clickthroughCap;
        this.weight = weight;
        this.isReleased = isReleased;
        this.isDefault = isDefault;
        this.cookieTarget = cookieTarget;
        this.doOptimization = doOptimization;
        this.optimizationType = optimizationType;
        this.optimizationSpeed = optimizationSpeed;
        this.minOptimizationWeight = minOptimizationWeight;
        this.doGeoTargeting = doGeoTargeting;
        this.doCookieTargeting = doCookieTargeting;
        this.doStoryboarding = doStoryboarding;
        this.logicalDelete = logicalDelete;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
        this.daypartTarget = daypartTarget;
        this.doDaypartTargeting = doDaypartTargeting;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRotationType() {
        return this.rotationType;
    }

    public void setRotationType(String rotationType) {
        this.rotationType = rotationType;
    }

    public Long getImpressionCap() {
        return this.impressionCap;
    }

    public void setImpressionCap(Long impressionCap) {
        this.impressionCap = impressionCap;
    }

    public Long getClickthroughCap() {
        return this.clickthroughCap;
    }

    public void setClickthroughCap(Long clickthroughCap) {
        this.clickthroughCap = clickthroughCap;
    }

    public Long getWeight() {
        return this.weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Long getIsReleased() {
        return this.isReleased;
    }

    public void setIsReleased(Long isReleased) {
        this.isReleased = isReleased;
    }

    public Long getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(Long isDefault) {
        this.isDefault = isDefault;
    }

    public String getCookieTarget() {
        return this.cookieTarget;
    }

    public void setCookieTarget(String cookieTarget) {
        this.cookieTarget = cookieTarget;
    }

    public Long getDoOptimization() {
        return this.doOptimization;
    }

    public void setDoOptimization(Long doOptimization) {
        this.doOptimization = doOptimization;
    }

    public String getOptimizationType() {
        return this.optimizationType;
    }

    public void setOptimizationType(String optimizationType) {
        this.optimizationType = optimizationType;
    }

    public String getOptimizationSpeed() {
        return this.optimizationSpeed;
    }

    public void setOptimizationSpeed(String optimizationSpeed) {
        this.optimizationSpeed = optimizationSpeed;
    }

    public Long getMinOptimizationWeight() {
        return this.minOptimizationWeight;
    }

    public void setMinOptimizationWeight(Long minOptimizationWeight) {
        this.minOptimizationWeight = minOptimizationWeight;
    }

    public Long getDoGeoTargeting() {
        return this.doGeoTargeting;
    }

    public void setDoGeoTargeting(Long doGeoTargeting) {
        this.doGeoTargeting = doGeoTargeting;
    }

    public Long getDoCookieTargeting() {
        return this.doCookieTargeting;
    }

    public void setDoCookieTargeting(Long doCookieTargeting) {
        this.doCookieTargeting = doCookieTargeting;
    }

    public Long getDoStoryboarding() {
        return this.doStoryboarding;
    }

    public void setDoStoryboarding(Long doStoryboarding) {
        this.doStoryboarding = doStoryboarding;
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

    public String getDaypartTarget() {
        return this.daypartTarget;
    }

    public void setDaypartTarget(String daypartTarget) {
        this.daypartTarget = daypartTarget;
    }

    public Long getDoDaypartTargeting() {
        return this.doDaypartTargeting;
    }

    public void setDoDaypartTargeting(Long doDaypartTargeting) {
        this.doDaypartTargeting = doDaypartTargeting;
    }

    @XmlElement(name = "geoTarget")
    public CreativeGroupTarget getCreativeGroupTargets() {
        return creativeGroupTargets;
    }

    public void setCreativeGroupTargets(CreativeGroupTarget creativeGroupTargets) {
        this.creativeGroupTargets = creativeGroupTargets;
    }

    public Long getEnableGroupWeight() {
        return enableGroupWeight;
    }

    public void setEnableGroupWeight(Long enableGroupWeight) {
        this.enableGroupWeight = enableGroupWeight;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Long getEnableFrequencyCap() {
        return enableFrequencyCap;
    }

    public void setEnableFrequencyCap(Long enableFrequencyCap) {
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

    public ScheduleSet getScheduleSet() {
        return scheduleSet;
    }

    public void setScheduleSet(ScheduleSet scheduleSet) {
        this.scheduleSet = scheduleSet;
    }

    @Override
    public String toString() {
        return "CreativeGroup [id=" + id + ", name=" + name + ", rotationType="
                + rotationType + ", impressionCap=" + impressionCap
                + ", clickthroughCap=" + clickthroughCap + ", weight=" + weight
                + ", isReleased=" + isReleased + ", isDefault=" + isDefault
                + ", cookieTarget=" + cookieTarget + ", doOptimization="
                + doOptimization + ", optimizationType=" + optimizationType
                + ", optimizationSpeed=" + optimizationSpeed
                + ", minOptimizationWeight=" + minOptimizationWeight
                + ", doGeoTargeting=" + doGeoTargeting + ", doCookieTargeting="
                + doCookieTargeting + ", doStoryboarding=" + doStoryboarding
                + ", logicalDelete=" + logicalDelete + ", createdTpwsKey="
                + createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
                + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + ", daypartTarget=" + daypartTarget
                + ", doDaypartTargeting=" + doDaypartTargeting
                + ", creativeGroupTargets=" + creativeGroupTargets
                + ", enableGroupWeight=" + enableGroupWeight + ", priority="
                + priority + ", enableFrequencyCap=" + enableFrequencyCap
                + ", frequencyCap=" + frequencyCap + ", frequencyCapWindow="
                + frequencyCapWindow + ", scheduleSet=" + scheduleSet + "]";
    }
}

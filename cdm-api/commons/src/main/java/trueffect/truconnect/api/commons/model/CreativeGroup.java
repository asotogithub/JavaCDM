package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name = "CreativeGroup")
@XmlSeeAlso({CreativeGroupTarget.class})
public class CreativeGroup {

    @TableFieldMapping(table = "CREATIVE_GROUP", field = "CREATIVE_GROUP_ID")
    private Long id;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "CAMPAIGN_ID")
    private Long campaignId;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "GROUP_NAME")
    private String name;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "ROTATION_TYPE")
    private String rotationType;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "IMPRESSION_CAP")
    private Long impressionCap;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "CLICKTHROUGH_CAP")
    private Long clickthroughCap;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "WEIGHT")
    private Long weight;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "RELEASED", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long isReleased;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "IS_DEFAULT", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long isDefault;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "COOKIE_TARGET")
    private String cookieTarget;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "DO_OPTIMIZATION", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long doOptimization;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "OPTIMIZATION_TYPE")
    private String optimizationType;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "OPTIMIZATION_SPEED")
    private String optimizationSpeed;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "MIN_OPTIMIZATION_WEIGHT")
    private Long minOptimizationWeight;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "DO_GEO_TARGET", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long doGeoTargeting;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "DO_COOKIE_TARGET", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long doCookieTargeting;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "DO_STORYBOARDING", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long doStoryboarding;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "DAYPART_TARGET")
    private String daypartTarget;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "DO_DAYPART_TARGET", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long doDaypartTargeting;
    private List<GeoTarget> geoTargets;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "ENABLE_GROUP_WEIGHT", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long enableGroupWeight;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "PRIORITY")
    private Long priority;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "ENABLE_FREQUENCY_CAP", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long enableFrequencyCap;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "FREQUENCY_CAP")
    private Long frequencyCap;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "FREQUENCY_CAP_WINDOW")
    private Long frequencyCapWindow;
    @TableFieldMapping(table = "EXT", field = "EXTERNALID")
    private String externalId;

    public CreativeGroup() {
    }

    public CreativeGroup(Long id, Long campaignId,
            String name, String rotationType, Long impressionCap,
            Long clickthroughCap, Long weight, Long isReleased,
            Long doOptimization, Long minOptimizationWeight,
            Long doGeoTargeting, Long doCookieTarget,
            Long doStoryboarding, String createdTpwsKey,
            String modifiedTpwsKey) {
        this.id = id;
        this.campaignId = campaignId;
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

    public CreativeGroup(Long id, Long campaignId,
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
        this.campaignId = campaignId;
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

    public Long getCampaignId() {
        return this.campaignId;
    }

    public void setCampaignId(Long campaingId) {
        this.campaignId = campaingId;
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @XmlElementWrapper(name = "geoTargets")
    @XmlElement(name = "geoTarget", type = GeoTarget.class)
    public List<GeoTarget> getGeoTargets() {
        return geoTargets;
    }

    public void setGeoTargets(List<GeoTarget> geoTargets) {
        this.geoTargets = geoTargets;
    }

    public void addGeoTarget(GeoTarget target) {
        if (this.geoTargets == null) { this.geoTargets = new ArrayList<>(); }

        this.geoTargets.add(target);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

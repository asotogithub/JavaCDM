package trueffect.truconnect.api.commons.model.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO currently used for display in CDM UI creative group tab.
 * @author jfryer on 6/1/15.
 */
@XmlRootElement(name = "CreativeGroupDtoForCampaigns")
public class CreativeGroupDtoForCampaigns {

    private Long campaignId;
    private Long doDaypartTargeting;
    private Long doCookieTargeting;
    private Long doGeoTargeting;
    private Long id;
    private String name;
    private int numberOfCreativesInGroup;
    private Long priority;
    private Long isDefault;
    private Long enableGroupWeight;

    //ctors
    public CreativeGroupDtoForCampaigns(){}

    //getters and setters
    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getDoDaypartTargeting() {
        return doDaypartTargeting;
    }

    public void setDoDaypartTargeting(Long doDaypartTargeting) {
        this.doDaypartTargeting = doDaypartTargeting;
    }

    public Long getDoGeoTargeting() {
        return doGeoTargeting;
    }

    public void setDoGeoTargeting(Long doGeoTargeting) {
        this.doGeoTargeting = doGeoTargeting;
    }

    public Long getDoCookieTargeting() {
        return doCookieTargeting;
    }

    public void setDoCookieTargeting(Long doCookieTargeting) {
        this.doCookieTargeting = doCookieTargeting;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfCreativesInGroup() {
        return numberOfCreativesInGroup;
    }

    public void setNumberOfCreativesInGroup(int numberOfCreativesInGroup) {
        this.numberOfCreativesInGroup = numberOfCreativesInGroup;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Long getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Long isDefault) {
        this.isDefault = isDefault;
    }

    public Long getEnableGroupWeight() {
        return enableGroupWeight;
    }

    public void setEnableGroupWeight(Long enableGroupWeight) {
        this.enableGroupWeight = enableGroupWeight;
    }
}

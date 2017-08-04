package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.Clickthrough;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data object for getting the list of Creative Group Creatives to use is scheduling.
 */
@XmlRootElement(name = "CreativeGroupCreativeView")
public class CreativeGroupCreativeView {
    private Long campaignId;
    private Long creativeId;
    private String creativeFileName;
    private String creativeAlias;
    private Long creativeGroupId;
    private String creativeGroupName;
    private Long creativeWidth;
    private Long creativeHeight;
    private List<Clickthrough> creativeClickthroughs;
    private String creativeDefaultClickthrough;
    private Long creativeGroupWeight;
    private Long creativeGroupWeightEnabled;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public String getCreativeFileName() {
        return creativeFileName;
    }

    public void setCreativeFileName(String creativeFileName) {
        this.creativeFileName = creativeFileName;
    }

    public String getCreativeAlias() {
        return creativeAlias;
    }

    public void setCreativeAlias(String creativeAlias) {
        this.creativeAlias = creativeAlias;
    }

    public Long getCreativeGroupId() {
        return creativeGroupId;
    }

    public void setCreativeGroupId(Long creativeGroupId) {
        this.creativeGroupId = creativeGroupId;
    }

    public String getCreativeGroupName() {
        return creativeGroupName;
    }

    public void setCreativeGroupName(String creativeGroupName) {
        this.creativeGroupName = creativeGroupName;
    }

    public Long getCreativeWidth() {
        return creativeWidth;
    }

    public void setCreativeWidth(Long creativeWidth) {
        this.creativeWidth = creativeWidth;
    }

    public Long getCreativeHeight() {
        return creativeHeight;
    }

    public void setCreativeHeight(Long creativeHeight) {
        this.creativeHeight = creativeHeight;
    }

    public String getCreativeDefaultClickthrough() {
        return creativeDefaultClickthrough;
    }

    public void setCreativeDefaultClickthrough(String creativeDefaultClickthrough) {
        this.creativeDefaultClickthrough = creativeDefaultClickthrough;
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

    public List<Clickthrough> getCreativeClickthroughs() {
        return creativeClickthroughs;
    }

    public void setCreativeClickthroughs(List<Clickthrough> creativeClickthroughs) {
        this.creativeClickthroughs = creativeClickthroughs;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

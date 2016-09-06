package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.Creative;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author marleny.patsi
 */
@XmlRootElement(name = "CreativeGroupCreativeDTO")
@ApiModel(value="CreativeGroupCreativeDTO")
public class CreativeGroupCreativeDTO {

    @ApiModelProperty(required=true, value="ID of Campaign that all Creatives are part of")
    private Long campaignId;

    @ApiModelProperty(required=true, value="Set of Creatives to be associated with Creative Groups")
    private List<Creative> creatives;

    @ApiModelProperty(required=true, value="Set of Creative Groups that each Creative will be associated with")
    private List<Long> creativeGroupIds;

    public List<Creative> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<Creative> creatives) {
        this.creatives = creatives;
    }

    public List<Long> getCreativeGroupIds() {
        return creativeGroupIds;
    }

    public void setCreativeGroupIds(List<Long> creativeGroupIds) {
        this.creativeGroupIds = creativeGroupIds;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "MediaBuyCampaign")
public class MediaBuyCampaign {

    @TableFieldMapping(table = "MEDIA_BUY_CAMPAIGN", field = "MEDIA_BUY_ID")
    private Long mediaBuyId;
    @TableFieldMapping(table = "MEDIA_BUY_CAMPAIGN", field = "CAMPAIGN_ID")
    private Long campaignId;
    @TableFieldMapping(table = "MEDIA_BUY_CAMPAIGN", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "MEDIA_BUY_CAMPAIGN", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "MEDIA_BUY_CAMPAIGN", field = "MODIFIED")
    private Date modifiedDate;
    private String createdTpwsKey;
    private String modifiedTpwsKey;

    public Long getMediaBuyId() {
        return mediaBuyId;
    }

    public void setMediaBuyId(Long mediaBuyId) {
        this.mediaBuyId = mediaBuyId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
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

    public String getCreatedTpwsKey() {
        return createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

    public String getModifiedTpwsKey() {
        return modifiedTpwsKey;
    }

    public void setModifiedTpwsKey(String modifiedTpwsKey) {
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

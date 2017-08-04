package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "SiteMeasurementCampaign")
public class SiteMeasurementCampaign {

    @TableFieldMapping(table = "SITE_MEASUREMENT_CAMPAIGN", field = "CAMPAIGN_ID")
    private Long campaignId;
    @TableFieldMapping(table = "SITE_MEASUREMENT_CAMPAIGN", field = "MEASUREMENT_ID")
    private Long measurementId;
    @TableFieldMapping(table = "SITE_MEASUREMENT_CAMPAIGN", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "SITE_MEASUREMENT_CAMPAIGN", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "SITE_MEASUREMENT_CAMPAIGN", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "SITE_MEASUREMENT_CAMPAIGN", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "SITE_MEASUREMENT_CAMPAIGN", field = "MODIFIED")
    private Date modifiedDate;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(Long measurementId) {
        this.measurementId = measurementId;
    }

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

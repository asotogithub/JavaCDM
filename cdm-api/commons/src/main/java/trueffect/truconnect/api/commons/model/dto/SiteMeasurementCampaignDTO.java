package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author marleny.patsi
 */
@XmlRootElement(name = "SiteMeasurementCampaignDTO")
public class SiteMeasurementCampaignDTO implements Serializable {

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
    
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "MEASUREMENT_NAME")
    private String measurementName;
    
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERTISER_ID")
    private Long advertiserId;

    @TableFieldMapping(table = "ADVERTISER", field = "ADVERTISER_NAME")
    private String advertiserName;

    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "BRAND_ID")
    private Long brandId;

    @TableFieldMapping(table = "BRAND", field = "BRAND_NAME")
    private String brandName;
    
    @TableFieldMapping(table = "CAMPAIGN", field = "CAMPAIGN_NAME")
    private String campaignName;

    private String campaignStatus;
    
    public SiteMeasurementCampaignDTO() {
    }
    
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

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public String getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(String campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SiteMeasurementCampaignDTO that = (SiteMeasurementCampaignDTO) o;

        return new EqualsBuilder()
                .append(campaignId, that.campaignId)
                .append(measurementId, that.measurementId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(campaignId)
                .append(measurementId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }    
}

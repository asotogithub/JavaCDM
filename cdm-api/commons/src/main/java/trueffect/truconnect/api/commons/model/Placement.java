package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "Placement")
public class Placement {

    @TableFieldMapping(table = "PLACEMENT", field = "PLACEMENT_ID")
    private Long id;
    @TableFieldMapping(table = "PLACEMENT", field = "SITE_ID")
    private Long siteId;
    @TableFieldMapping(table = "PLACEMENT", field = "SECTION_ID")
    private Long siteSectionId;
    @TableFieldMapping(table = "PLACEMENT", field = "IO_ID")
    private Long ioId;
    @TableFieldMapping(table = "PLACEMENT", field = "AD_SIZE_ID")
    private Long sizeId;
    @TableFieldMapping(table = "PLACEMENT", field = "START_DATE")
    private Date startDate;
    @TableFieldMapping(table = "PLACEMENT", field = "END_DATE")
    private Date endDate;
    @TableFieldMapping(table = "PLACEMENT", field = "INVENTORY")
    private Long inventory;
    @TableFieldMapping(table = "PLACEMENT", field = "RATE")
    private Double rate;
    @TableFieldMapping(table = "PLACEMENT", field = "RATE_TYPE")
    private String rateType;
    @TableFieldMapping(table = "PLACEMENT", field = "MAX_FILE_SIZE")
    private Long maxFileSize;
    @TableFieldMapping(table = "PLACEMENT", field = "IS_SECURE", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long isSecure;
    @TableFieldMapping(table = "PLACEMENT", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "PLACEMENT", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "PLACEMENT", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "PLACEMENT", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "PLACEMENT", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "PLACEMENT", field = "IS_TRAFFICKED", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long isTrafficked;
    @TableFieldMapping(table = "PLACEMENT", field = "RESEND_TAGS", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Long resendTags;
    @TableFieldMapping(table = "PLACEMENT", field = "UTC_OFFSET")
    private Long utcOffset;
    @TableFieldMapping(table = "PLACEMENT", field = "SM_EVENT_ID")
    private Long smEventId;
    @TableFieldMapping(table = "PLACEMENT", field = "COUNTRY_CURRENCY_ID")
    private Long countryCurrencyId;
    @TableFieldMapping(table = "PLACEMENT", field = "PLACEMENT_NAME")
    private String name;
    @TableFieldMapping(table = "PLACEMENT", field = "EXT_PROP1")
    private String extProp1;
    @TableFieldMapping(table = "PLACEMENT", field = "EXT_PROP2")
    private String extProp2;
    @TableFieldMapping(table = "PLACEMENT", field = "EXT_PROP3")
    private String extProp3;
    @TableFieldMapping(table = "PLACEMENT", field = "EXT_PROP4")
    private String extProp4;
    @TableFieldMapping(table = "PLACEMENT", field = "EXT_PROP5")
    private String extProp5;
    @TableFieldMapping(table = "AD_SIZE", field = "WIDTH")
    private Long width;
    @TableFieldMapping(table = "AD_SIZE", field = "HEIGHT")
    private Long height;
    @TableFieldMapping(table = "CAMPAIGN", field = "CAMPAIGN_ID")
    private Long campaignId;
    @TableFieldMapping(table = "STATUS_TYPES", field = "STATUS_NAME", valueMappings = {
        @FieldValueMapping(input = "\"New\"", output = "\"IO_NEW\""),
        @FieldValueMapping(input = "\"Accepted\"", output = "\"IO_ACPT\""),
        @FieldValueMapping(input = "\"Rejected\"", output = "\"IO_RJCT\"")
    })
    private String status;
    @TableFieldMapping(table = "EXT", field = "EXTERNALID")
    private String externalId;
    @TableFieldMapping(table = "PACKAGE", field = "PACKAGE_ID")
    private Long packageId;

    private String siteName;
    private String sizeName;

    List<CostDetail> costDetails;

    private Double adSpend;
    private String isScheduled;
    private String sectionName;

    public Placement() {
    }

    public Placement(Long id, Long siteId, Long sectionId, Long ioId,
            Long adSizeId, Date startDate, Date endDate, Long inventory,
            Double rate, String rateType, Long maxFileSize, Long isSecure,
            String createdTpwsKey, Long isTrafficked, Long resendTags,
            Long utcOffset, Long smEventId, Long countryCurrencyId, String name) {
        this.id = id;
        this.siteId = siteId;
        this.siteSectionId = sectionId;
        this.ioId = ioId;
        this.sizeId = adSizeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.inventory = inventory;
        this.rate = rate;
        this.rateType = rateType;
        this.maxFileSize = maxFileSize;
        this.isSecure = isSecure;
        this.createdTpwsKey = createdTpwsKey;
        this.isTrafficked = isTrafficked;
        this.resendTags = resendTags;
        this.utcOffset = utcOffset;
        this.smEventId = smEventId;
        this.countryCurrencyId = countryCurrencyId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getSiteSectionId() {
        return siteSectionId;
    }

    public void setSiteSectionId(Long siteSectionId) {
        this.siteSectionId = siteSectionId;
    }

    public Long getIoId() {
        return ioId;
    }

    public void setIoId(Long ioId) {
        this.ioId = ioId;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getInventory() {
        return inventory;
    }

    public void setInventory(Long inventory) {
        this.inventory = inventory;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Long getIsSecure() {
        return isSecure;
    }

    public void setIsSecure(Long isSecure) {
        this.isSecure = isSecure;
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

    public Long getIsTrafficked() {
        return isTrafficked;
    }

    public void setIsTrafficked(Long isTrafficked) {
        this.isTrafficked = isTrafficked;
    }

    public Long getResendTags() {
        return resendTags;
    }

    public void setResendTags(Long resendTags) {
        this.resendTags = resendTags;
    }

    public Long getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(Long utcOffset) {
        this.utcOffset = utcOffset;
    }

    public Long getSmEventId() {
        return smEventId;
    }

    public void setSmEventId(Long smEventId) {
        this.smEventId = smEventId;
    }

    public Long getCountryCurrencyId() {
        return countryCurrencyId;
    }

    public void setCountryCurrencyId(Long countryCurrencyId) {
        this.countryCurrencyId = countryCurrencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExtProp1() {
        return extProp1;
    }

    public void setExtProp1(String extProp1) {
        this.extProp1 = extProp1;
    }

    public String getExtProp2() {
        return extProp2;
    }

    public void setExtProp2(String extProp2) {
        this.extProp2 = extProp2;
    }

    public String getExtProp3() {
        return extProp3;
    }

    public void setExtProp3(String extProp3) {
        this.extProp3 = extProp3;
    }

    public String getExtProp4() {
        return extProp4;
    }

    public void setExtProp4(String extProp4) {
        this.extProp4 = extProp4;
    }

    public String getExtProp5() {
        return extProp5;
    }

    public void setExtProp5(String extProp5) {
        this.extProp5 = extProp5;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
    
    public Double getAdSpend() {
        return adSpend;
    }

    public void setAdSpend(Double adSpend) {
        this.adSpend = adSpend;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public String getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(String isScheduled) {
        this.isScheduled = isScheduled;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<CostDetail> getCostDetails() {
        return costDetails;
    }

    public void setCostDetails(List<CostDetail> costDetails) {
        this.costDetails = costDetails;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

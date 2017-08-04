package trueffect.truconnect.api.standalone.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "Placement")
@XmlType(propOrder = {"id", "siteId", "siteSectionId", "ioId", "sizeId", "startDate",
        "endDate", "inventory", "rate", "rateType", "maxFileSize", "isSecure", 
        "logicalDelete", "createdTpwsKey", "modifiedTpwsKey", "createdDate", 
        "modifiedDate", "isTrafficked", "resendTags", "utcOffset", "smEventId", 
        "countryCurrencyId", "name", "extProp1", "extProp2", "extProp3", "extProp4", 
        "extProp5", "status", "externalId"})
public class Placement {

    private Long id;
    private Long siteId;
    private Long siteSectionId;
    private Long ioId;
    private Long sizeId;
    private Date startDate;
    private Date endDate;
    private Long inventory;
    private Double rate;
    private String rateType;
    private Long maxFileSize;
    private Long isSecure;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;
    private Long isTrafficked;
    private Long resendTags;
    private Long utcOffset;
    private Long smEventId;
    private Long countryCurrencyId;
    private String name;
    private String extProp1;
    private String extProp2;
    private String extProp3;
    private String extProp4;
    private String extProp5;
    private String status;
    private String externalId;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Placement [id=" + id + ", siteId=" + siteId
                + ", siteSectionId=" + siteSectionId + ", ioId=" + ioId
                + ", sizeId=" + sizeId + ", startDate=" + startDate
                + ", endDate=" + endDate + ", inventory=" + inventory
                + ", rate=" + rate + ", rateType=" + rateType
                + ", maxFileSize=" + maxFileSize + ", isSecure=" + isSecure
                + ", logicalDelete=" + logicalDelete + ", createdTpwsKey="
                + createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
                + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + ", isTrafficked=" + isTrafficked
                + ", resendTags=" + resendTags + ", utcOffset=" + utcOffset
                + ", smEventId=" + smEventId + ", countryCurrencyId="
                + countryCurrencyId + ", name=" + name + ", extProp1="
                + extProp1 + ", extProp2=" + extProp2 + ", extProp3="
                + extProp3 + ", extProp4=" + extProp4 + ", extProp5="
                + extProp5 + ", status=" + status + ", externalId="
                + externalId + "]";
    }
}

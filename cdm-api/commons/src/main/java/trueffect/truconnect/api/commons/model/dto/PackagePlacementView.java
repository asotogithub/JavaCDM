package trueffect.truconnect.api.commons.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PackagePlacementView")
public class PackagePlacementView implements Comparable {

    //io
    private Long ioId;
    private String ioName;
    private Integer ioNumber;
    private Long placementCounter;

    //package
    private Long packageId;
    private String packageName;
    private Double placementAdSpend;

    //placement
    private Long placementId;
    private String placementName;
    private String status;
    private String isScheduled;

    //site
    private Long siteId;
    private String siteName;

    //size
    private Long sizeId;
    private String sizeName;

    //cost detail
    private Double adSpend;
    private Date startDate;
    private Date endDate;
    private Long inventory;
    private Double rate;
    private String rateType;

    //campaign
    private Long campaignId;

    public Long getIoId() {
        return ioId;
    }

    public void setIoId(Long ioId) {
        this.ioId = ioId;
    }

    public String getIoName() {
        return ioName;
    }

    public void setIoName(String ioName) {
        this.ioName = ioName;
    }

    public Integer getIoNumber() {
        return ioNumber;
    }

    public void setIoNumber(Integer ioNumber) {
        this.ioNumber = ioNumber;
    }

    public Long getPlacementCounter() {
        return placementCounter;
    }

    public void setPlacementCounter(Long placementCounter) {
        this.placementCounter = placementCounter;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    public String getPlacementName() {
        return placementName;
    }

    public void setPlacementName(String placementName) {
        this.placementName = placementName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(String isScheduled) {
        this.isScheduled = isScheduled;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
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

    public Double getPlacementAdSpend() {
        return placementAdSpend;
    }

    public void setPlacementAdSpend(Double placementAdSpend) {
        this.placementAdSpend = placementAdSpend;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public int compareTo(Object o) {
        int compareage = ((PackagePlacementView) o).getPackageId() != null ? ((PackagePlacementView) o).getPackageId().intValue() : 0;
        int packageIdInt = packageId != null ? packageId.intValue() : 0;
        return packageIdInt - compareage;
    }
}

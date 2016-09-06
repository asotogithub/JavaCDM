package trueffect.truconnect.api.commons.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PlacementView")
public class PlacementView {
    // placement fields
    private Long id;
    private Date startDate;
    private Date endDate;
    private String name;
    private String status;
    private Double adSpend;
    private String placementAlias;
    private Long isTrafficked;
    private Long placementsTotal;

    // campaign fields
    private Long campaignId;
    private String campaignName;
    private Long campaignsTotal;

    // InsertionOrder fields
    private Long ioId;

    // Site fields
    private Long siteId;
    private String siteName;
    private Long sitesTotal;

    // Section fields
    private Long siteSectionId;
    private String siteSectionName;
    private Long sectionsTotal;

    // Size fields
    private Long sizeId;
    private String sizeName;
    private Long height;
    private Long width;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
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

    public Long getSiteSectionId() {
        return siteSectionId;
    }

    public void setSiteSectionId(Long siteSectionId) {
        this.siteSectionId = siteSectionId;
    }

    public String getSiteSectionName() {
        return siteSectionName;
    }

    public void setSiteSectionName(String siteSectionName) {
        this.siteSectionName = siteSectionName;
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

    public Long getIoId() {
        return ioId;
    }

    public void setIoId(Long ioId) {
        this.ioId = ioId;
    }

    public String getPlacementAlias() {
        return placementAlias;
    }

    public void setPlacementAlias(String placementAlias) {
        this.placementAlias = placementAlias;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public Long getIsTrafficked() {
        return isTrafficked;
    }

    public void setIsTrafficked(Long isTrafficked) {
        this.isTrafficked = isTrafficked;
    }

    public Long getPlacementsTotal() {
        return placementsTotal;
    }

    public void setPlacementsTotal(Long placementsTotal) {
        this.placementsTotal = placementsTotal;
    }

    public Long getCampaignsTotal() {
        return campaignsTotal;
    }

    public void setCampaignsTotal(Long campaignsTotal) {
        this.campaignsTotal = campaignsTotal;
    }

    public Long getSitesTotal() {
        return sitesTotal;
    }

    public void setSitesTotal(Long sitesTotal) {
        this.sitesTotal = sitesTotal;
    }

    public Long getSectionsTotal() {
        return sectionsTotal;
    }

    public void setSectionsTotal(Long sectionsTotal) {
        this.sectionsTotal = sectionsTotal;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

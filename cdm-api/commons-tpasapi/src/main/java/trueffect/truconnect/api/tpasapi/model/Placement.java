package trueffect.truconnect.api.tpasapi.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Placement")
@XmlType(propOrder = {"id", "campaignId", "name", "siteId", "width", "height",
    "status", "isSecure", "maxFileSize", "externalId", "createdDate",
    "modifiedDate"})
public class Placement {

    private Long id;
    private Long campaignId;
    private String name;
    private Long siteId;
    private Long width;
    private Long height;
    private String status;
    private Boolean isSecure;
    private Long maxFileSize;
    private String externalId;
    private Date createdDate;
    private Date modifiedDate;

    public Placement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsSecure() {
        return isSecure;
    }

    public void setIsSecure(Boolean isSecure) {
        this.isSecure = isSecure;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String toString() {
        return "Placement{" + "id=" + id + ", campaignId=" + campaignId
                + ", name=" + name + ", siteId=" + siteId + ", width=" + width
                + ", height=" + height + ", status=" + status + ", isSecure="
                + isSecure + ", maxFileSize=" + maxFileSize
                + ", externalId=" + externalId + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + '}';
    }
}

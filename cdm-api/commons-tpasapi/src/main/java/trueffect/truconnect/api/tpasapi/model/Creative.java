package trueffect.truconnect.api.tpasapi.model;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "Creative")
@XmlType(propOrder = {"id", "campaignId", "type", "filename", "width", "height",
    "alias", "clickthroughs", "isExpandable", "purpose", "extendedProperty1",
    "extendedProperty2", "extendedProperty3", "extendedProperty4",
    "extendedProperty5", "externalId", "createdDate", "modifiedDate"})
public class Creative {

    private Long id;
    private Long campaignId;
    private String type;
    private String filename;
    private Long width;
    private Long height;
    private String alias;
    private List<Clickthrough> clickthroughs;
    private Boolean isExpandable;
    private String purpose;
    private String extendedProperty1;
    private String extendedProperty2;
    private String extendedProperty3;
    private String extendedProperty4;
    private String extendedProperty5;
    private String externalId;
    private Date createdDate;
    private Date modifiedDate;

    public Creative() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @XmlElementWrapper(name = "clickthroughs")
    @XmlElement(name = "Clickthrough")
    public List<Clickthrough> getClickthroughs() {
        return clickthroughs;
    }

    public void setClickthroughs(List<Clickthrough> clickthroughs) {
        this.clickthroughs = clickthroughs;
    }

    public Boolean getIsExpandable() {
        return isExpandable;
    }

    public void setIsExpandable(Boolean isExpandable) {
        this.isExpandable = isExpandable;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getExtendedProperty1() {
        return extendedProperty1;
    }

    public void setExtendedProperty1(String extendedProperty1) {
        this.extendedProperty1 = extendedProperty1;
    }

    public String getExtendedProperty2() {
        return extendedProperty2;
    }

    public void setExtendedProperty2(String extendedProperty2) {
        this.extendedProperty2 = extendedProperty2;
    }

    public String getExtendedProperty3() {
        return extendedProperty3;
    }

    public void setExtendedProperty3(String extendedProperty3) {
        this.extendedProperty3 = extendedProperty3;
    }

    public String getExtendedProperty4() {
        return extendedProperty4;
    }

    public void setExtendedProperty4(String extendedProperty4) {
        this.extendedProperty4 = extendedProperty4;
    }

    public String getExtendedProperty5() {
        return extendedProperty5;
    }

    public void setExtendedProperty5(String extendedProperty5) {
        this.extendedProperty5 = extendedProperty5;
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
        return "Creative{" + "id=" + id + ", campaignId=" + campaignId
                + ", type=" + type + ", filename=" + filename + ", width="
                + width + ", height=" + height + ", alias=" + alias
                + ", clickthroughs=" + clickthroughs + ", isExpandable="
                + isExpandable + ", purpose=" + purpose + ", extendedProperty1="
                + extendedProperty1 + ", extendedProperty2=" + extendedProperty2
                + ", extendedProperty3=" + extendedProperty3
                + ", extendedProperty4=" + extendedProperty4
                + ", extendedProperty5=" + extendedProperty5 + ", externalId="
                + externalId + ", createdDate=" + createdDate
                + ", modifiedDate=" + modifiedDate + '}';
    }
}

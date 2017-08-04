package trueffect.truconnect.api.standalone.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "Creative")
@XmlType(propOrder = {"id", "ownerCampaignId", "filename", "alias", 
        "creativeType", "purpose", "width", "height", "clickthrough",
        "scheduled", "released", "logicalDelete", "createdTpwsKey", 
        "modifiedTpwsKey", "createdDate", "modifiedDate", "setCookieString", 
        "extProp1", "extProp2", "extProp3", "extProp4", "extProp5",
        "richMediaId", "fileSize", "swfClickCount", "isExpandable", 
        "externalId", "clickthroughs"})
@XmlSeeAlso({Clickthrough.class})
public class Creative {

    private Long id;
    private Long ownerCampaignId;
    private String filename;
    private String alias;
    private String creativeType;
    private String purpose;
    private Long width;
    private Long height;
    private String clickthrough;
    private Long scheduled;
    private Long released;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;
    private String setCookieString;
    private String extProp1;
    private String extProp2;
    private String extProp3;
    private String extProp4;
    private String extProp5;
    private Long richMediaId;
    private Long fileSize;
    private Long swfClickCount;
    private Long isExpandable;
    private String externalId;
    private List<Clickthrough> clickthroughs;

    public Creative() {
    }

    public Creative(Long id, Long ownerCampaignId, String filename, String alias,
            String creativeType, String purpose, Long width, Long height,
            String clickthrough, Long scheduled, Long released,
            String extProp1, String extProp2, String extProp3,
            String extProp4, String extProp5, String createdTpwsKey,
            String modifiedTpwsKey, Long richMediaId, Long swfClickCount,
            Long isExpandable) {
        super();
        this.id = id;
        this.ownerCampaignId = ownerCampaignId;
        this.filename = filename;
        this.alias = alias;
        this.creativeType = creativeType;
        this.purpose = purpose;
        this.width = width;
        this.height = height;
        this.clickthrough = clickthrough;
        this.scheduled = scheduled;
        this.released = released;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
        this.extProp1 = extProp1;
        this.extProp2 = extProp2;
        this.extProp3 = extProp3;
        this.extProp4 = extProp4;
        this.extProp5 = extProp5;
        this.richMediaId = richMediaId;
        this.swfClickCount = swfClickCount;
        this.isExpandable = isExpandable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerCampaignId() {
        return ownerCampaignId;
    }

    public void setOwnerCampaignId(Long ownerCampaignId) {
        this.ownerCampaignId = ownerCampaignId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCreativeType() {
        return creativeType;
    }

    public void setCreativeType(String creativeType) {
        this.creativeType = creativeType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public String getClickthrough() {
        return clickthrough;
    }

    public void setClickthrough(String clickthrough) {
        this.clickthrough = clickthrough;
    }

    public Long getScheduled() {
        return scheduled;
    }

    public void setScheduled(Long scheduled) {
        this.scheduled = scheduled;
    }

    public Long getReleased() {
        return released;
    }

    public void setReleased(Long released) {
        this.released = released;
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

    public String getSetCookieString() {
        return setCookieString;
    }

    public void setSetCookieString(String setCookieString) {
        this.setCookieString = setCookieString;
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

    public Long getRichMediaId() {
        return richMediaId;
    }

    public void setRichMediaId(Long richMediaId) {
        this.richMediaId = richMediaId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getSwfClickCount() {
        return swfClickCount;
    }

    public void setSwfClickCount(Long swfClickCount) {
        this.swfClickCount = swfClickCount;
    }

    public Long getIsExpandable() {
        return isExpandable;
    }

    public void setIsExpandable(Long isExpandable) {
        this.isExpandable = isExpandable;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @XmlElementWrapper(name = "clickthroughs")
    @XmlAnyElement(lax = true)
    public List<Clickthrough> getClickthroughs() {
        return clickthroughs;
    }

    public void setClickthroughs(List<Clickthrough> clickthroughs) {
        this.clickthroughs = clickthroughs;
    }

    @Override
    public String toString() {
        return "Creative [id=" + id + ", ownerCampaignId=" + ownerCampaignId
                + ", filename=" + filename + ", alias=" + alias
                + ", creativeType=" + creativeType + ", purpose=" + purpose
                + ", width=" + width + ", height=" + height + ", clickthrough="
                + clickthrough + ", scheduled=" + scheduled + ", released="
                + released + ", logicalDelete=" + logicalDelete
                + ", createdTpwsKey=" + createdTpwsKey + ", modifiedTpwsKey="
                + modifiedTpwsKey + ", createdDate=" + createdDate
                + ", modifiedDate=" + modifiedDate + ", setCookieString="
                + setCookieString + ", extProp1=" + extProp1 + ", extProp2="
                + extProp2 + ", extProp3=" + extProp3 + ", extProp4="
                + extProp4 + ", extProp5=" + extProp5 + ", richMediaId="
                + richMediaId + ", fileSize=" + fileSize + ", swfClickCount="
                + swfClickCount + ", isExpandable=" + isExpandable
                + ", externalId=" + externalId + ", clickthroughs="
                + clickthroughs + "]";
    }
}

package trueffect.truconnect.api.standalone.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "CreativeGroupCreative")
@XmlType(propOrder = {"creativeGroupId", "creativeId", "displayOrder", "displayQuantity", 
        "logicalDelete", "createdTpwsKey", "modifiedTpwsKey", "createdDate", "modifiedDate"})
public class CreativeGroupCreative {

    private Long creativeGroupId;
    private Long creativeId;
    private Long displayOrder;
    private Long displayQuantity;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;

    public CreativeGroupCreative() {
    }

    public CreativeGroupCreative(Long creativeGroupId, Long creativeId,
            String createdTpwsKey, String modifiedTpwsKey) {
        this.creativeGroupId = creativeGroupId;
        this.creativeId = creativeId;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public CreativeGroupCreative(Long creativeGroupId, Long creativeId,
            Long displayOrder, Long displayQuantity,
            String createdTpwsKey, String modifiedTpwsKey) {
        this.creativeGroupId = creativeGroupId;
        this.creativeId = creativeId;
        this.displayOrder = displayOrder;
        this.displayQuantity = displayQuantity;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public Long getCreativeGroupId() {
        return this.creativeGroupId;
    }

    public void setCreativeGroupId(Long creativeGroupId) {
        this.creativeGroupId = creativeGroupId;
    }

    public Long getCreativeId() {
        return this.creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public Long getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getDisplayQuantity() {
        return this.displayQuantity;
    }

    public void setDisplayQuantity(Long displayQuantity) {
        this.displayQuantity = displayQuantity;
    }

    public String getLogicalDelete() {
        return this.logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    public String getCreatedTpwsKey() {
        return this.createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

    public String getModifiedTpwsKey() {
        return this.modifiedTpwsKey;
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
        return "CreativeGroupCreative [creativeGroupId=" + creativeGroupId
                + ", creativeId=" + creativeId + ", displayOrder="
                + displayOrder + ", displayQuantity=" + displayQuantity
                + ", logicalDelete=" + logicalDelete + ", createdTpwsKey="
                + createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
                + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + "]";
    }
}

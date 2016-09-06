package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name = "CreativeGroupCreative")
@XmlSeeAlso({Creative.class})
public class CreativeGroupCreative {

    @TableFieldMapping(table = "CREATIVE_GROUP_CREATIVE", field = "CREATIVE_GROUP_ID")
    private Long creativeGroupId;
    @TableFieldMapping(table = "CREATIVE_GROUP_CREATIVE", field = "CREATIVE_ID")
    private Long creativeId;
    @TableFieldMapping(table = "CREATIVE_GROUP_CREATIVE", field = "DISPLAY_ORDER")
    private Long displayOrder;
    @TableFieldMapping(table = "CREATIVE_GROUP_CREATIVE", field = "DISPLAY_QUANTITY")
    private Long displayQuantity;
    @TableFieldMapping(table = "CREATIVE_GROUP_CREATIVE", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "CREATIVE_GROUP_CREATIVE", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "CREATIVE_GROUP_CREATIVE", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "CREATIVE_GROUP_CREATIVE", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "CREATIVE_GROUP_CREATIVE", field = "MODIFIED")
    private Date modifiedDate;
    // Foreign Columns (3 TABLES)
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "GROUP_NAME")
    private String creativeGroupName;
    @TableFieldMapping(table = "CREATIVE", field = "ALIAS")
    private String creativeAlias;

    private Creative creative;
    private List<Creative> creatives;

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

    public String getCreativeGroupName() {
        return creativeGroupName;
    }

    public void setCreativeGroupName(String creativeGroupName) {
        this.creativeGroupName = creativeGroupName;
    }

    public String getCreativeAlias() {
        return creativeAlias;
    }

    public void setCreativeAlias(String creativeAlias) {
        this.creativeAlias = creativeAlias;
    }

    public Creative getCreative() {
        return creative;
    }

    public void setCreative(Creative creative) {
        this.creative = creative;
    }

    public List<Creative> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<Creative> creatives) {
        this.creatives = creatives;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

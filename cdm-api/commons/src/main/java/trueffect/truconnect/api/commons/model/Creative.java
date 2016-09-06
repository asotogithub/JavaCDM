package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author Gustavo Claure
 */
@ApiModel(value="Creative")
@XmlRootElement(name = "Creative")
@XmlSeeAlso({Clickthrough.class})
public class Creative {

    @TableFieldMapping(table = "CREATIVE", field = "CREATIVE_ID")
    private Long id;

    @ApiModelProperty(required=true, value="ID of Agency that \"owns\" this Creative")
    @TableFieldMapping(table = "CREATIVE", field = "AGENCY_ID")
    private Long agencyId;

    @ApiModelProperty(required=true, value="ID of Campaign this Creative is associated with")
    @TableFieldMapping(table = "CREATIVE", field = "CAMPAIGN_ID")
    private Long campaignId;

    @TableFieldMapping(table = "CREATIVE", field = "OWNER_CAMPAIGN_ID")
    private Long ownerCampaignId;

    @ApiModelProperty(required=true, value="Name of image file for the Creative")
    @TableFieldMapping(table = "CREATIVE", field = "FILENAME")
    private String filename;

    @ApiModelProperty(value="Alternative name for the image file of the Creative")
    @TableFieldMapping(table = "CREATIVE", field = "ALIAS")
    private String alias;

    @ApiModelProperty(required=true, allowableValues = "3rd, gif, html, html5, jpeg, jpg, json, txt, vast. vmap, mxl, zip")
    @TableFieldMapping(table = "CREATIVE", field = "CREATIVE_TYPE")
    private String creativeType;

    @TableFieldMapping(table = "CREATIVE", field = "PURPOSE")
    private String purpose;

    @ApiModelProperty(value="of Creative; will default, based on examination of Creative's image file, or creativeTtype, if not explicitly set")
    @TableFieldMapping(table = "CREATIVE", field = "WIDTH")
    private Long width;

    @ApiModelProperty(value="of Creative; will default, based on examination of Creative's image file, or creativeTtype, if not explicitly set")
    @TableFieldMapping(table = "CREATIVE", field = "HEIGHT")
    private Long height;

    @TableFieldMapping(table = "CREATIVE", field = "CLICKTHROUGH")
    private String clickthrough;

    @TableFieldMapping(table = "CREATIVE", field = "SCHEDULED", valueMappings = {
            @FieldValueMapping(input = "true", output = "1"),
            @FieldValueMapping(input = "false", output = "0")
    })
    private Long scheduled;

    @TableFieldMapping(table = "CREATIVE", field = "RELEASED", valueMappings = {
            @FieldValueMapping(input = "true", output = "1"),
            @FieldValueMapping(input = "false", output = "0")
    })
    private Long released;

    @TableFieldMapping(table = "CREATIVE", field = "LOGICAL_DELETE")
    private String logicalDelete;

    @TableFieldMapping(table = "CREATIVE", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;

    @TableFieldMapping(table = "CREATIVE", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;

    @TableFieldMapping(table = "CREATIVE", field = "CREATED")
    private Date createdDate;

    @TableFieldMapping(table = "CREATIVE", field = "MODIFIED")
    private Date modifiedDate;

    @TableFieldMapping(table = "CREATIVE", field = "SET_COOKIE_STRING")
    private String setCookieString;

    @TableFieldMapping(table = "CREATIVE", field = "EXT_PROP1")
    private String extProp1;

    @TableFieldMapping(table = "CREATIVE", field = "EXT_PROP2")
    private String extProp2;

    @TableFieldMapping(table = "CREATIVE", field = "EXT_PROP3")
    private String extProp3;

    @TableFieldMapping(table = "CREATIVE", field = "EXT_PROP4")
    private String extProp4;

    @TableFieldMapping(table = "CREATIVE", field = "EXT_PROP5")
    private String extProp5;

    @TableFieldMapping(table = "CREATIVE", field = "RICH_MEDIA_ID")
    private Long richMediaId;

    @TableFieldMapping(table = "CREATIVE", field = "FILE_SIZE")
    private Long fileSize;

    @TableFieldMapping(table = "CREATIVE", field = "SWF_CLICK_COUNT")
    private Long swfClickCount;

    @TableFieldMapping(table = "CREATIVE", field = "IS_EXPANDABLE", valueMappings = {
            @FieldValueMapping(input = "true", output = "1"),
            @FieldValueMapping(input = "false", output = "0")
    })
    private Long isExpandable;

    @TableFieldMapping(table = "EXT", field = "EXTERNALID")
    private String externalId;

    private List<Clickthrough> clickthroughs;
    private Long groupsCount;
    private List<CreativeGroup> creativeGroups;

    @TableFieldMapping(table = "CREATIVE_VERSION", field = "VERSION_NUMBER")
    private Long creativeVersion = Constants.DEFAULT_CREATIVE_INITIAL_VERSION;

    private List<CreativeVersion> versions;

    public Creative() {
    }

    public Creative(Long id, Long agencyId, Long campaignId,
                    Long ownerCampaignId, String filename, String alias,
                    String creativeType, String purpose, Long width, Long height,
                    String clickthrough, Long scheduled, Long released,
                    String extProp1, String extProp2, String extProp3,
                    String extProp4, String extProp5, String createdTpwsKey,
                    String modifiedTpwsKey, Long richMediaId, Long swfClickCount,
                    Long isExpandable, Long creativeVersion) {
        super();
        this.id = id;
        this.agencyId = agencyId;
        this.campaignId = campaignId;
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
        this.creativeVersion = creativeVersion;
    }

    /**
     * @return the creativeId
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the creativeId to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the agencyId
     */
    public Long getAgencyId() {
        return agencyId;
    }

    /**
     * @param agencyId the agencyId to set
     */
    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    /**
     * @return the campaignId
     */
    public Long getCampaignId() {
        return campaignId;
    }

    /**
     * @param campaignId the campaignId to set
     */
    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    /**
     * @return the ownerCampaignId
     */
    public Long getOwnerCampaignId() {
        return ownerCampaignId;
    }

    /**
     * @param ownerCampaignId the ownerCampaignId to set
     */
    public void setOwnerCampaignId(Long ownerCampaignId) {
        this.ownerCampaignId = ownerCampaignId;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the creativeType
     */
    public String getCreativeType() {
        return creativeType;
    }

    /**
     * @param creativeType the creativeType to set
     */
    public void setCreativeType(String creativeType) {
        this.creativeType = creativeType;
    }

    /**
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the width
     */
    public Long getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Long width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public Long getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(Long height) {
        this.height = height;
    }

    /**
     * @return the clickthrough
     */
    public String getClickthrough() {
        return clickthrough;
    }

    /**
     * @param clickthrough the clickthrough to set
     */
    public void setClickthrough(String clickthrough) {
        this.clickthrough = clickthrough;
    }

    /**
     * @return the scheduled
     */
    public Long getScheduled() {
        return scheduled;
    }

    /**
     * @param scheduled the scheduled to set
     */
    public void setScheduled(Long scheduled) {
        this.scheduled = scheduled;
    }

    /**
     * @return the released
     */
    public Long getReleased() {
        return released;
    }

    /**
     * @param released the released to set
     */
    public void setReleased(Long released) {
        this.released = released;
    }

    /**
     * @return the logicalDelete
     */
    public String getLogicalDelete() {
        return logicalDelete;
    }

    /**
     * @param logicalDelete the logicalDelete to set
     */
    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    /**
     * @return the createdTpwsKey
     */
    public String getCreatedTpwsKey() {
        return createdTpwsKey;
    }

    /**
     * @param createdTpwsKey the createdTpwsKey to set
     */
    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

    /**
     * @return the modifiedTpwsKey
     */
    public String getModifiedTpwsKey() {
        return modifiedTpwsKey;
    }

    /**
     * @param modifiedTpwsKey the modifiedTpwsKey to set
     */
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

    /**
     * @return the setCookieString
     */
    public String getSetCookieString() {
        return setCookieString;
    }

    /**
     * @param setCookieString the setCookieString to set
     */
    public void setSetCookieString(String setCookieString) {
        this.setCookieString = setCookieString;
    }

    /**
     * @return the extProp1
     */
    public String getExtProp1() {
        return extProp1;
    }

    /**
     * @param extProp1 the extProp1 to set
     */
    public void setExtProp1(String extProp1) {
        this.extProp1 = extProp1;
    }

    /**
     * @return the extProp2
     */
    public String getExtProp2() {
        return extProp2;
    }

    /**
     * @param extProp2 the extProp2 to set
     */
    public void setExtProp2(String extProp2) {
        this.extProp2 = extProp2;
    }

    /**
     * @return the extProp3
     */
    public String getExtProp3() {
        return extProp3;
    }

    /**
     * @param extProp3 the extProp3 to set
     */
    public void setExtProp3(String extProp3) {
        this.extProp3 = extProp3;
    }

    /**
     * @return the extProp4
     */
    public String getExtProp4() {
        return extProp4;
    }

    /**
     * @param extProp4 the extProp4 to set
     */
    public void setExtProp4(String extProp4) {
        this.extProp4 = extProp4;
    }

    /**
     * @return the extProp5
     */
    public String getExtProp5() {
        return extProp5;
    }

    /**
     * @param extProp5 the extProp5 to set
     */
    public void setExtProp5(String extProp5) {
        this.extProp5 = extProp5;
    }

    /**
     * @return the richMediaId
     */
    public Long getRichMediaId() {
        return richMediaId;
    }

    /**
     * @param richMediaId the richMediaId to set
     */
    public void setRichMediaId(Long richMediaId) {
        this.richMediaId = richMediaId;
    }

    /**
     * @return the fileSize
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return the swfClickCount
     */
    public Long getSwfClickCount() {
        return swfClickCount;
    }

    /**
     * @param swfClickCount the swfClickCount to set
     */
    public void setSwfClickCount(Long swfClickCount) {
        this.swfClickCount = swfClickCount;
    }

    /**
     * @return the isExpandable
     */
    public Long getIsExpandable() {
        return isExpandable;
    }

    /**
     * @param isExpandable the isExpandable to set
     */
    public void setIsExpandable(Long isExpandable) {
        this.isExpandable = isExpandable;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<Clickthrough> getClickthroughs() {
        return clickthroughs;
    }

    public void addClickthrough(Clickthrough click) {
        if (this.clickthroughs == null) {
            this.clickthroughs = new ArrayList<>();
        }

        this.clickthroughs.add(click);
    }

    public void setClickthroughs(List<Clickthrough> clickthroughs) {
        this.clickthroughs = clickthroughs;
    }

    /**
     * Get creativeVersion
     * @return
     */
    public Long getCreativeVersion() {
        return creativeVersion;
    }

    /**
     * Set creativeVersion.
     * @param creativeVersion
     */
    public void setCreativeVersion(Long creativeVersion) {
        this.creativeVersion = creativeVersion;
    }

    /**
     * @return the groupsCount
     */
    public Long getGroupsCount() {
        return this.groupsCount;
    }

    /**
     * @param groupsCount the groupsCount to set
     */
    public void setGroupsCount(Long groupsCount) {
        this.groupsCount = groupsCount;
    }

    /**
     * @return the creativeGroups
     */
    @XmlElementWrapper(name = "creativeGroups")
    public List<CreativeGroup> getCreativeGroups() {
        return creativeGroups;
    }

    /**
     * @param creativeGroups the creativeGroups to set
     */
    public void setCreativeGroups(List<CreativeGroup> creativeGroups) {
        this.creativeGroups = creativeGroups;
    }

    public List<CreativeVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<CreativeVersion> versions) {
        this.versions = versions;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void addCreativeGroup(CreativeGroup group) {
        if (this.creativeGroups == null) {
            this.creativeGroups = new ArrayList<CreativeGroup>();
        }

        this.creativeGroups.add(group);
    }

    /**
     * Gets a given {@code CreativeVersion} given a {@code versionNumber}
     * @param versionNumber The Version number to find
     * @return A {@code CreativeVersion} that matches the given {@code versionNumber}
     */
    public CreativeVersion getVersionByNumber(Long versionNumber) {
        if(versions == null) {
            return null;
        }
        for(CreativeVersion v : versions ) {
            if(v.getVersionNumber() != null && v.getVersionNumber().equals(versionNumber)){
                return v;
            }
        }
        return null;
    }
}

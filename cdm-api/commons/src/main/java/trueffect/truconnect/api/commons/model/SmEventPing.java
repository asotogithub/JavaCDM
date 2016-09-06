package trueffect.truconnect.api.commons.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "SmEventPing")
public class SmEventPing {

    @TableFieldMapping(table = "SM_EVENT_PING", field = "SM_EVENT_PING_ID")
    private Long smEventPingId;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "SM_EVENT_ID")
    private Long smEventId;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "PING_CONTENT")
    private String pingContent;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "DESCRIPTION")
    private String description;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "SITE_ID")
    private Long siteId;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "PING_TYPE")
    private Long pingType;
    @TableFieldMapping(table = "SM_EVENT_PING", field = "TAG_TYPE")
    private Long tagType;

    public SmEventPing() {
    }

    public Long getSmEventPingId() {
        return smEventPingId;
    }

    public void setSmEventPingId(Long smEventPingId) {
        this.smEventPingId = smEventPingId;
    }

    public Long getSmEventId() {
        return smEventId;
    }

    public void setSmEventId(Long smEventId) {
        this.smEventId = smEventId;
    }

    public String getPingContent() {
        return pingContent;
    }

    public void setPingContent(String pingContent) {
        this.pingContent = pingContent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
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

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    public Long getPingType() {
        return pingType;
    }

    public void setPingType(Long pingType) {
        this.pingType = pingType;
    }

    public Long getTagType() {
        return tagType;
    }

    public void setTagType(Long tagType) {
        this.tagType = tagType;
    }

	@Override
	public String toString() {
		return "SmEventPing [smEventPingId=" + smEventPingId + ", smEventId="
				+ smEventId + ", pingContent=" + pingContent + ", description="
				+ description + ", siteId=" + siteId + ", createdTpwsKey="
				+ createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
				+ ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + ", logicalDelete=" + logicalDelete
				+ ", pingType=" + pingType + ", tagType=" + tagType + "]";
	}
}

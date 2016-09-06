package trueffect.truconnect.api.commons.model;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "CreativeInsertion")
public class CreativeInsertion {

    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "CREATIVE_INSERTION_ID")
    private Long id;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "CAMPAIGN_ID")
    private Long campaignId;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "CREATIVE_GROUP_ID")
    private Long creativeGroupId;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "CREATIVE_ID")
    private Long creativeId;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "PLACEMENT_ID")
    private Long placementId;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "START_DATE")
    private Date startDate;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "END_DATE")
    private Date endDate;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "TIME_ZONE")
    private String timeZone;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "WEIGHT")
    private Long weight;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "SEQUENCE")
    private Long sequence;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "CLICKTHROUGH")
    private String clickthrough;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "RELEASED", valueMappings= {
    		@FieldValueMapping(input="true", output="1"),
    		@FieldValueMapping(input="false", output="0")
    })
    private Long released;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "SET_COOKIE_STRING")
    private String setCookieString;
    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "CREATIVE_INSERTION_ROOT_ID")
    private Long creativeInsertionRootId;
    private String creativeType;
    private List<Clickthrough> clickthroughs;

    public CreativeInsertion() {
    }

    public CreativeInsertion(Long id, Long campaignId, Long creativeGroupId,
            Long creativeId, Long placementId, Date startDate, Date endDate,
            String timeZone, Long weight, String clickthrough, Long released,
            Long sequence, String createdTpwsKey, String modifiedTpwsKey) {
        this.id = id;
        this.campaignId = campaignId;
        this.creativeGroupId = creativeGroupId;
        this.creativeId = creativeId;
        this.placementId = placementId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeZone = timeZone;
        this.weight = weight;
        this.clickthrough = clickthrough;
        this.released = released;
        this.sequence = sequence;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
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

    public Long getCreativeGroupId() {
        return creativeGroupId;
    }

    public void setCreativeGroupId(Long creativeGroupId) {
        this.creativeGroupId = creativeGroupId;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
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

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getClickthrough() {
        return clickthrough;
    }

    public void setClickthrough(String clickthrough) {
        this.clickthrough = clickthrough;
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

    public Long getCreativeInsertionRootId() {
        return creativeInsertionRootId;
    }

    public void setCreativeInsertionRootId(Long creativeInsertionRootId) {
        this.creativeInsertionRootId = creativeInsertionRootId;
    }

    public String getCreativeType() {
        return creativeType;
    }

    public void setCreativeType(String creativeType) {
        this.creativeType = creativeType;
    }

    public List<Clickthrough> getClickthroughs() {
        return clickthroughs;
    }

    public void setClickthroughs(List<Clickthrough> clickthroughs) {
        this.clickthroughs = clickthroughs;
    }

	@Override
	public String toString() {
		return "CreativeInsertion [id=" + id + ", campaignId=" + campaignId
				+ ", creativeGroupId=" + creativeGroupId + ", creativeId="
				+ creativeId + ", placementId=" + placementId + ", startDate="
				+ startDate + ", endDate=" + endDate + ", timeZone=" + timeZone
				+ ", weight=" + weight + ", sequence=" + sequence
				+ ", clickthrough=" + clickthrough + ", released=" + released
				+ ", logicalDelete=" + logicalDelete + ", createdTpwsKey="
				+ createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
				+ ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + ", setCookieString=" + setCookieString
				+ ", creativeInsertionRootId=" + creativeInsertionRootId
				+ ", clickthroughs=" + clickthroughs + "]";
	}
}

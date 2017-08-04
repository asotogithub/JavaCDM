package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "InsertionOrder")
public class InsertionOrder {

    @TableFieldMapping(table = "INSERTION_ORDER", field = "IO_ID")
    private Long id;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "MEDIA_BUY_ID")
    private Long mediaBuyId;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "PUBLISHER_ID")
    private Long publisherId;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "IO_NUMBER")
    private Integer ioNumber;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "IO_NAME")
    private String name;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "NOTES")
    private String notes;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "INSERTION_ORDER", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "STATUS_TYPES", field = "STATUS_NAME", valueMappings = {
        @FieldValueMapping(input = "\"New\"", output = "\"IO_NEW\""),
        @FieldValueMapping(input = "\"Accepted\"", output = "\"IO_ACPT\""),
        @FieldValueMapping(input = "\"Rejected\"", output = "\"IO_RJCT\"")
    })
    private String status;
    @TableFieldMapping(table = "MEDIA_BUY_CAMPAIGN", field = "CAMPAIGN_ID")
    private Long campaignId;

    private Long placementsCount;
    private Double totalAdSpend;
    private Date lastUpdated;
    private String lastUpdatedAuthor;
    private Long activePlacementCounter;

    public InsertionOrder() {
    }

    public InsertionOrder(Long id, Long mediaBuyId, Long publisherId,
            Integer ioNumber, String name, String notes,
            String createdTpwsKey) {
        this.id = id;
        this.mediaBuyId = mediaBuyId;
        this.publisherId = publisherId;
        this.ioNumber = ioNumber;
        this.name = name;
        this.notes = notes;
        this.createdTpwsKey = createdTpwsKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMediaBuyId() {
        return mediaBuyId;
    }

    public void setMediaBuyId(Long mediaBuyId) {
        this.mediaBuyId = mediaBuyId;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public Integer getIoNumber() {
        return ioNumber;
    }

    public void setIoNumber(Integer ioNumber) {
        this.ioNumber = ioNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getPlacementsCount() {
        return placementsCount;
    }

    public void setPlacementsCount(Long placementsCount) {
        this.placementsCount = placementsCount;
    }

    public Double getTotalAdSpend() {
        return totalAdSpend;
    }

    public void setTotalAdSpend(Double totalAdSpend) {
        this.totalAdSpend = totalAdSpend;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdatedAuthor() {
        return lastUpdatedAuthor;
    }

    public void setLastUpdatedAuthor(String lastUpdatedAuthor) {
        this.lastUpdatedAuthor = lastUpdatedAuthor;
    }

    public Long getActivePlacementCounter() {
        return activePlacementCounter;
    }

    public void setActivePlacementCounter(Long activePlacementCounter) {
        this.activePlacementCounter = activePlacementCounter;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "SmEvent")
public class SmEvent {

    @TableFieldMapping(table = "SM_EVENT", field = "SM_EVENT_ID")
    private Long id;
    @TableFieldMapping(table = "SM_EVENT", field = "SM_GROUP_ID")
    private Long smGroupId;
    @TableFieldMapping(table = "SM_EVENT", field = "EVENT_NAME")
    private String eventName;
    @TableFieldMapping(table = "SM_EVENT", field = "LOCATION")
    private String location;
    @TableFieldMapping(table = "SM_EVENT", field = "EVENT_TYPE")
    private Long eventType;
    @TableFieldMapping(table = "SM_EVENT", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "SM_EVENT", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "SM_EVENT", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "SM_EVENT", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "SM_EVENT", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "SM_EVENT", field = "IS_TRAFFICKED")
    private Long isTrafficked;
    @TableFieldMapping(table = "SM_EVENT", field = "SM_EVENT_TYPE")
    private Long smEventType;


    private Long measurementState;
    private List<SmPingEventDTO> pingEvents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSmGroupId() {
        return smGroupId;
    }

    public void setSmGroupId(Long smGroupId) {
        this.smGroupId = smGroupId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getEventType() {
        return eventType;
    }

    public void setEventType(Long eventType) {
        this.eventType = eventType;
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

    public Long getIsTrafficked() {
        return isTrafficked;
    }

    public void setIsTrafficked(Long isTrafficked) {
        this.isTrafficked = isTrafficked;
    }

    public Long getSmEventType() {
        return smEventType;
    }

    public void setSmEventType(Long smEventType) {
        this.smEventType = smEventType;
    }

    public List<SmPingEventDTO> getPingEvents() {
        return pingEvents;
    }

    public void setPingEvents(List<SmPingEventDTO> pingEvents) {
        this.pingEvents = pingEvents;
    }

    public Long getMeasurementState() {
        return measurementState;
    }

    public void setMeasurementState(Long measurementState) {
        this.measurementState = measurementState;
    }

	@Override
    public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

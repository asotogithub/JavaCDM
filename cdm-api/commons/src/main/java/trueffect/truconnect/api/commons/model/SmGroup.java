package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "SmGroup")
public class SmGroup {

    @TableFieldMapping(table = "SM_GROUP", field = "SM_GROUP_ID")
    private Long id;
    @TableFieldMapping(table = "SM_GROUP", field = "MEASUREMENT_ID")
    private Long measurementId;
    @TableFieldMapping(table = "SM_GROUP", field = "GROUP_NAME")
    private String groupName;
    @TableFieldMapping(table = "SM_GROUP", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "SM_GROUP", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "SM_GROUP", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "SM_GROUP", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "SM_GROUP", field = "LOGICAL_DELETE")
    private String logicalDelete;

    private SmEvent smEvent;

    public SmGroup() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(Long measurementId) {
        this.measurementId = measurementId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public SmEvent getSmEvent() {
        return smEvent;
    }

    public void setSmEvent(SmEvent smEvent) {
        this.smEvent = smEvent;
    }

    @Override
	public String toString() {
        return ToStringBuilder.reflectionToString(this);
	}
}

package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "geoTarget")
public class CreativeGroupTarget {

    private Long creativeGroupId;
    private Long valueId;
    private String typeCode;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private String logicalDelete;
    private Date createdDate;
    private Date modifiedDate;
    private Long antiTarget;
    private String targetLabel;

    public Long getCreativeGroupId() {
        return creativeGroupId;
    }

    public void setCreativeGroupId(Long creativeGroupId) {
        this.creativeGroupId = creativeGroupId;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
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

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
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

    public Long getAntiTarget() {
        return antiTarget;
    }

    public void setAntiTarget(Long antiTarget) {
        this.antiTarget = antiTarget;
    }

    public String getTargetLabel() {
        return targetLabel;
    }

    public void setTargetLabel(String targetLabel) {
        this.targetLabel = targetLabel;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreativeGroupTarget that = (CreativeGroupTarget) o;

        return new EqualsBuilder()
                .append(this.antiTarget, that.antiTarget)
                .append(this.createdDate, that.createdDate)
                .append(this.createdTpwsKey, that.createdTpwsKey)
                .append(this.logicalDelete, that.logicalDelete)
                .append(this.modifiedDate, that.modifiedDate)
                .append(this.modifiedTpwsKey, that.modifiedTpwsKey)
                .append(this.targetLabel, that.targetLabel)
                .append(this.typeCode, that.typeCode)
                .append(this.valueId, that.valueId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.antiTarget)
                .append(this.createdDate)
                .append(this.createdTpwsKey)
                .append(this.logicalDelete)
                .append(this.modifiedDate)
                .append(this.modifiedTpwsKey)
                .append(this.targetLabel)
                .append(this.typeCode)
                .append(this.valueId)
                .toHashCode();
    }
}

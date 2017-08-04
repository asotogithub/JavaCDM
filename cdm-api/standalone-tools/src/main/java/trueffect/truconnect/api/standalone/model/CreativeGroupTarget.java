package trueffect.truconnect.api.standalone.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "geoTarget")
@XmlType(propOrder = {"targetValueId", "valueId", "typeCode", "logicalDelete",
        "createdTpwsKey", "modifiedTpwsKey", "createdDate", "modifiedDate"})
public class CreativeGroupTarget {

    private List<Long> targetValueId;
    private Long valueId;
    private String typeCode;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private String logicalDelete;
    private Date createdDate;
    private Date modifiedDate;

    @XmlElementWrapper(name = "values")
    @XmlElement(name = "value")
    public List<Long> getTargetValueId() {
        return targetValueId;
    }

    public void setTargetValueId(List<Long> targetValueId) {
        this.targetValueId = targetValueId;
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

    @Override
    public String toString() {
        return "CreativeGroupTarget [targetValueId=" + targetValueId
                + ", valueId=" + valueId + ", typeCode=" + typeCode
                + ", createdTpwsKey=" + createdTpwsKey + ", modifiedTpwsKey="
                + modifiedTpwsKey + ", logicalDelete=" + logicalDelete
                + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + "]";
    }
}

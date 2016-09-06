package trueffect.truconnect.api.tpasapi.model;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "geoTarget")
@XmlType(propOrder = {"typeCode", "targetValuesIds"})
public class CreativeGroupTarget implements Serializable {

    private String typeCode;
    private List<Long> targetValuesIds;

    public CreativeGroupTarget() {
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @XmlElementWrapper(name = "values")
    @XmlElement(name = "value")
    public List<Long> getTargetValuesIds() {
        return targetValuesIds;
    }

    public void setTargetValuesIds(List<Long> targetValuesIds) {
        this.targetValuesIds = targetValuesIds;
    }

    @Override
    public String toString() {
        return "CreativeGroupTarget{" + "typeCode=" + typeCode + ", targetValuesIds=" + targetValuesIds + '}';
    }
}

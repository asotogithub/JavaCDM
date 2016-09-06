package trueffect.truconnect.api.tpasapi.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "TargetValue")
@XmlType(propOrder = {"id", "typeCode", "code", "label"})
public class TargetValue implements Serializable {

    private Long id;
    private String typeCode;
    private String code;
    private String label;

    public TargetValue() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "TargetValue{" + "id=" + id + ", typeCode=" + typeCode
                + ", code=" + code + ", label=" + label + '}';
    }
}

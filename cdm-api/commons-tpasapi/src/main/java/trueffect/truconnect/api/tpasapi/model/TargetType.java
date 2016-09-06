package trueffect.truconnect.api.tpasapi.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "TargetType")
@XmlType(propOrder = {"id", "code", "label"})
public class TargetType implements Serializable {

    private Long id;
    private String code;
    private String label;

    public TargetType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "TargetType{" + "id=" + id + ", code=" + code
                + ", label=" + label + '}';
    }
}

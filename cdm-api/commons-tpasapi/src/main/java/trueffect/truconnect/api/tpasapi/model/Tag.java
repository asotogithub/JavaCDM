package trueffect.truconnect.api.tpasapi.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "Tag")
@XmlType(propOrder = {"typeId", "text"})
public class Tag {
    private Long typeId;
    private String text;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Tag{" + "typeId=" + typeId + ", text=" + text + '}';
    }
    
    
}

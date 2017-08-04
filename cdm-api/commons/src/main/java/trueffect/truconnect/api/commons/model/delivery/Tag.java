package trueffect.truconnect.api.commons.model.delivery;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "Tag")
public class Tag {

    private Long typeId;
    private String text;

    public Tag() {
    }

    public Tag(Long typeId, String text) {
        this.typeId = typeId;
        this.text = text;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long tagId) {
        this.typeId = tagId;
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

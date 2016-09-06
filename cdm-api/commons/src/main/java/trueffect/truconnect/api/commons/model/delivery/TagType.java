package trueffect.truconnect.api.commons.model.delivery;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Tag")
public class TagType {

    private List<TagAttribute> attributes;
    private String description;
    private String name;
    private Long tagId;

    @JsonProperty("Attributes")
    public List<TagAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<TagAttribute> attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("TagId")
    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
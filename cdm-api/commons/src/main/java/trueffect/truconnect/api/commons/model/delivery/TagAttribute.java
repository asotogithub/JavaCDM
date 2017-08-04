package trueffect.truconnect.api.commons.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "TagAttribute")
public class TagAttribute {

    private Long defaultValue;
    private String description;
    private String macro;
    private String name;
    private Long tagAttributeId;
    private Long tagId;
    private String validationRegEx;

    @JsonProperty("DefaultValue")
    public Long getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Long defaultValue) {
        this.defaultValue = defaultValue;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("Macro")
    public String getMacro() {
        return macro;
    }

    public void setMacro(String macro) {
        this.macro = macro;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("TagAttributeId")
    public Long getTagAttributeId() {
        return tagAttributeId;
    }

    public void setTagAttributeId(Long tagAttributeId) {
        this.tagAttributeId = tagAttributeId;
    }

    @JsonProperty("TagId")
    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @JsonProperty("ValidationRegEx")
    public String getValidationRegEx() {
        return validationRegEx;
    }

    public void setValidationRegEx(String validationRegEx) {
        this.validationRegEx = validationRegEx;
    }
}

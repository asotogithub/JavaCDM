package trueffect.truconnect.api.commons.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author marleny.patsi
 */
@XmlRootElement(name = "PlacementActionTagAssocParam")
public class PlacementActionTagAssocParam extends PlacementFilterParam {

    public enum PlacementAction { C, D }

    private Long htmlInjectionId;
    private String action;

    @XmlElement(required = true, nillable = false)
    public Long getHtmlInjectionId() {
        return htmlInjectionId;
    }

    public void setHtmlInjectionId(Long htmlInjectionId) {
        this.htmlInjectionId = htmlInjectionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

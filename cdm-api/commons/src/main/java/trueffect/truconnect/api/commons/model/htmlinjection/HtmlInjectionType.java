package trueffect.truconnect.api.commons.model.htmlinjection;

import trueffect.truconnect.api.commons.model.enums.HtmlInjectionTypeEnum;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author saulo.lopez
 */
@XmlRootElement(name = "HtmlInjectionType")
public class HtmlInjectionType {
    private String name;
    private HtmlInjectionTypeEnum type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HtmlInjectionTypeEnum getType() {
        return type;
    }

    protected void setType(HtmlInjectionTypeEnum type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

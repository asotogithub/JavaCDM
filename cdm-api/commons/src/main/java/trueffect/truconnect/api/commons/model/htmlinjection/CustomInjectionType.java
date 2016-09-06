package trueffect.truconnect.api.commons.model.htmlinjection;

import trueffect.truconnect.api.commons.model.enums.HtmlInjectionTypeEnum;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 7/14/2016.
 */
@XmlRootElement(name = "CustomInjectionType")
public class CustomInjectionType extends HtmlInjectionType {

    private String tagContent;
    private String secureTagContent;

    public CustomInjectionType() {
        setType(HtmlInjectionTypeEnum.CUSTOM_TAG);
    }

    public String getTagContent() {
        return tagContent;
    }

    public void setTagContent(String tagContent) {
        this.tagContent = tagContent;
    }

    public String getSecureTagContent() {
        return secureTagContent;
    }

    public void setSecureTagContent(String secureTagContent) {
        this.secureTagContent = secureTagContent;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

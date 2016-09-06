package trueffect.truconnect.api.commons.model.htmlinjection;

import trueffect.truconnect.api.commons.model.enums.HtmlInjectionTypeEnum;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author saulo.lopez
 */
@XmlRootElement(name = "AdChoicesHtmlInjectionType")
public class AdChoicesHtmlInjectionType extends HtmlInjectionType {

    private String optOutUrl;

    public AdChoicesHtmlInjectionType() {
        setType(HtmlInjectionTypeEnum.AD_CHOICES);
    }

    public String getOptOutUrl() {
        return optOutUrl;
    }

    public void setOptOutUrl(String optOutUrl) {
        this.optOutUrl = optOutUrl;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

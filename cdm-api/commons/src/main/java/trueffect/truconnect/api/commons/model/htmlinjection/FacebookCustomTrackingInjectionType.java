package trueffect.truconnect.api.commons.model.htmlinjection;

import trueffect.truconnect.api.commons.model.enums.HtmlInjectionTypeEnum;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author saulo.lopez
 */
@XmlRootElement(name = "FacebookCustomTrackingInjectionType")
public class FacebookCustomTrackingInjectionType extends HtmlInjectionType {

    private Long firstPartyDomainId;

    public FacebookCustomTrackingInjectionType() {
        setType(HtmlInjectionTypeEnum.FACEBOOK_CUSTOM_TRACKING);
    }

    public Long getFirstPartyDomainId() {
        return firstPartyDomainId;
    }

    public void setFirstPartyDomainId(Long firstPartyDomainId) {
        this.firstPartyDomainId = firstPartyDomainId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

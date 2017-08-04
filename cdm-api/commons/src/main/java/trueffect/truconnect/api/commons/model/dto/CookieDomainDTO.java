package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.CookieDomain;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transfer object to add some additional fields to the cookie domain object.
 */
@XmlRootElement(name = "CookieDomainDTO")
public class CookieDomainDTO extends CookieDomain {
    private String isThirdParty = "N";

    public String getIsThirdParty() {
        return isThirdParty;
    }

    public void setIsThirdParty(String isThirdParty) {
        this.isThirdParty = isThirdParty;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

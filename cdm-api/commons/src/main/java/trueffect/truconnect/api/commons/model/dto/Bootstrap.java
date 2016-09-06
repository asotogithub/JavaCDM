package trueffect.truconnect.api.commons.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Bootstrap")
public class Bootstrap {
    private Long agencyId;
    private String userId;
    private String userPassword;
    private Long advertiserId;
    private Long brandId;
    private Long cookieDomainId;

    public Bootstrap() {}

    public Bootstrap(Long agencyId, String userId, String userPassword) {
        this.agencyId = agencyId;
        this.userId = userId;
        this.userPassword = userPassword;
    }

    public Bootstrap(Long agencyId, Long advertiserId, Long brandId, Long cookieDomainId) {
        this.agencyId = agencyId;
        this.advertiserId = advertiserId;
        this.brandId = brandId;
        this.cookieDomainId = cookieDomainId;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCookieDomainId() {
        return cookieDomainId;
    }

    public void setCookieDomainId(Long cookieDomainId) {
        this.cookieDomainId = cookieDomainId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("agencyId", agencyId)
                .append("userId", userId)
                .append("userPassword", userPassword)
                .append("advertiserId", advertiserId)
                .append("brandId", brandId)
                .append("cookieDomainId", cookieDomainId)
                .toString();
    }
}

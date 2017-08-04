package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 6/18/2015.
 */
@XmlRootElement(name = "UserDomain")
public class UserDomain {

    private String userId;
    private Long domainId;

    public UserDomain() {
    }

    public UserDomain(String userId, Long domainId) {
        this.userId = userId;
        this.domainId = domainId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author abel.soto
 */
@XmlRootElement(name = "UserAdvertiser")
@XmlType(propOrder = {"userId", "advertiserId"})
public class UserAdvertiser {

    private String userId;
    private Long advertiserId;

    public UserAdvertiser() {
    }

    public UserAdvertiser(String userId, Long advertiserId) {
        this.userId = userId;
        this.advertiserId = advertiserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

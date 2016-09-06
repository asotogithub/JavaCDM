package trueffect.truconnect.api.oauth.model;

import java.util.List;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement
public class User {

    private String userId;
    private String password;
    private String isAppAdmin;
    private String isClientAdmin;
    private String isDisabled;
    private String isSysAdmin;
    private Long contactId;
    private List<String> roles;

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsAppAdmin() {
        return isAppAdmin;
    }

    public void setIsAppAdmin(String isAppAdmin) {
        this.isAppAdmin = isAppAdmin;
    }

    public String getIsClientAdmin() {
        return isClientAdmin;
    }

    public void setIsClientAdmin(String isClientAdmin) {
        this.isClientAdmin = isClientAdmin;
    }

    public String getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(String isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getIsSysAdmin() {
        return isSysAdmin;
    }

    public void setIsSysAdmin(String isSysAdmin) {
        this.isSysAdmin = isSysAdmin;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public void setRoles(List<String> roles) {
        if (roles == null) {
            this.roles = new ArrayList<String>();
        } else {
            this.roles = roles;
        }
        if (isAppAdmin.equalsIgnoreCase("Y")) {
            this.roles.add("ROLE_APP_ADMIN");
        }
        if (isClientAdmin.equalsIgnoreCase("Y")) {
            this.roles.add("ROLE_CLIENT_ADMIN");
        }
        if (isSysAdmin.equalsIgnoreCase("Y")) {
            this.roles.add("ROLE_SYS_ADMIN");
        }
    }

    public List<String> getRoles() {
        return roles;
    }
}

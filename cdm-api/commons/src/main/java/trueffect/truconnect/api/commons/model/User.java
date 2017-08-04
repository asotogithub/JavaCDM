package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Users")
@XmlType(propOrder = {"id", "agencyId", "contactId", "limitDomains",
    "limitAdvertisers", "isTraffickingContact", "userName", "password", "roles", "permissions",
    "createdDate", "modifiedDate"})
public class User {

    @TableFieldMapping(table = "USERS", field = "USER_ID")
    private String userName;
    @TableFieldMapping(table = "USERS", field = "PASSWORD")
    private String password;
    @TableFieldMapping(table = "USERS", field = "AGENCY_ID")
    private Long agencyId;
    @TableFieldMapping(table = "USERS", field = "CONTACT_ID")
    private Long contactId;
    @TableFieldMapping(table = "USERS", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "USERS", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "USERS", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "USERS", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "USERS", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "USERS", field = "IS_APP_ADMIN")
    private String isAppAdmin;
    @TableFieldMapping(table = "USERS", field = "IS_CLIENT_ADMIN")
    private String isClientAdmin;
    @TableFieldMapping(table = "USERS", field = "LIMIT_DOMAINS")
    private String limitDomains;
    @TableFieldMapping(table = "USERS", field = "LIMIT_ADVERTISERS")
    private String limitAdvertisers;
    @TableFieldMapping(table = "USERS", field = "ID")
    private Long id;
    @TableFieldMapping(table = "USERS", field = "IS_SYS_ADMIN")
    private String isSysAdmin;
    @TableFieldMapping(table = "USERS", field = "IS_DISABLED")
    private String isDisabled;
    @TableFieldMapping(table = "USERS", field = "IS_TRAFFICKING_CONTACT")
    private String isTraffickingContact;
    private List<String> role;
    /**
     * The possible Permissions for the User's assigned Roles
      */
    private List<String> permissions;

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlTransient
    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    @XmlTransient
    public String getCreatedTpwsKey() {
        return createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

    @XmlTransient
    public String getModifiedTpwsKey() {
        return modifiedTpwsKey;
    }

    public void setModifiedTpwsKey(String modifiedTpwsKey) {
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @XmlTransient
    public String getIsAppAdmin() {
        return isAppAdmin;
    }

    public void setIsAppAdmin(String isAppAdmin) {
        this.isAppAdmin = isAppAdmin;
    }

    @XmlTransient
    public String getIsClientAdmin() {
        return isClientAdmin;
    }

    public void setIsClientAdmin(String isClientAdmin) {
        this.isClientAdmin = isClientAdmin;
    }

    public String getLimitDomains() {
        return limitDomains;
    }

    public void setLimitDomains(String limitDomains) {
        this.limitDomains = limitDomains;
    }

    public String getLimitAdvertisers() {
        return limitAdvertisers;
    }

    public void setLimitAdvertisers(String limitAdvertisers) {
        this.limitAdvertisers = limitAdvertisers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    public String getIsSysAdmin() {
        return isSysAdmin;
    }

    public void setIsSysAdmin(String isSysAdmin) {
        this.isSysAdmin = isSysAdmin;
    }

    @XmlTransient
    public String getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(String isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getIsTraffickingContact() {
        return isTraffickingContact;
    }

    public void setIsTraffickingContact(String isTraffickingContact) {
        this.isTraffickingContact = isTraffickingContact;
    }

    public void setRoles(List<String> roles) {
        if (roles == null) {
            this.role = new ArrayList<String>();
        } else {
            this.role = roles;
        }
        if (isAppAdmin != null && isAppAdmin.equalsIgnoreCase("Y")) {
            this.role.add("ROLE_APP_ADMIN");
        }
        if (isClientAdmin != null && isClientAdmin.equalsIgnoreCase("Y")) {
            this.role.add("ROLE_CLIENT_ADMIN");
        }
        if (isSysAdmin != null && isSysAdmin.equalsIgnoreCase("Y")) {
            this.role.add("ROLE_SYS_ADMIN");
        }

    }

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    public List<String> getRoles() {
        return role;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

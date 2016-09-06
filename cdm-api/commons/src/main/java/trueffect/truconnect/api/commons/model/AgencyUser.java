package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "AgencyUser")
public class AgencyUser {

    @TableFieldMapping(table = "USERS", field = "USER_ID")
    private String userId;
    @TableFieldMapping(table = "USERS", field = "PASSWORD")
    private String password;
    @TableFieldMapping(table = "USERS", field = "AGENCY_ID")
    private Long agencyId;
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
    @TableFieldMapping(table = "USERS", field = "IS_TRAFFICKING_CONTACT")
    private String isTraffickingContact;
    @TableFieldMapping(table = "USERS", field = "IS_DISABLED")
    private String isDisabled;
    @TableFieldMapping(table = "CONTACT", field = "LNAME")
    private String lname;
    @TableFieldMapping(table = "CONTACT", field = "FNAME")
    private String fname;
    @TableFieldMapping(table = "CONTACT", field = "EMAIL")
    private String email;
    @TableFieldMapping(table = "CONTACT", field = "FAX")
    private String fax;
    @TableFieldMapping(table = "CONTACT", field = "NOTES")
    private String notes;
    @TableFieldMapping(table = "CONTACT", field = "PHONE")
    private String phone;
    @TableFieldMapping(table = "AGENCY", field = "AGENCY_NAME")
    private String agencyName;
    @TableFieldMapping(table = "AGENCY", field = "AG_URL")
    private String agUrl;
    @TableFieldMapping(table = "CONTACT", field = "CONTACT_ID")
    private Long contactId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
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

    public String getIsTraffickingContact() {
        return isTraffickingContact;
    }

    public void setIsTraffickingContact(String isTraffickingContact) {
        this.isTraffickingContact = isTraffickingContact;
    }

    public String getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(String isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgUrl() {
        return agUrl;
    }

    public void setAgUrl(String agUrl) {
        this.agUrl = agUrl;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    @Override
    public String toString() {
        return "AgencyUser{" + "userId=" + userId + ", password="
                + password + ", agencyId=" + agencyId + ", isAppAdmin="
                + isAppAdmin + ", isClientAdmin=" + isClientAdmin
                + ", limitDomains=" + limitDomains + ", limitAdvertisers="
                + limitAdvertisers + ", id=" + id + ", isTraffickingContact="
                + isTraffickingContact + ", isDisabled=" + isDisabled
                + ", lname=" + lname + ", fname=" + fname + ", email="
                + email + ", fax=" + fax + ", notes=" + notes + ", phone="
                + phone + ", agencyName=" + agencyName + ", agUrl=" + agUrl
                + ", contactId=" + contactId + '}';
    }
}

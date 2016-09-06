package trueffect.truconnect.api.commons.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Abel Soto
 * @edited Richard Jaldin
 */
@XmlRootElement(name = "Contact")
public class Contact {

    @TableFieldMapping(table = "CONTACT", field = "CONTACT_ID")
    private Long id;
    @TableFieldMapping(table = "CONTACT", field = "LNAME")
    private String lastName;
    @TableFieldMapping(table = "CONTACT", field = "FNAME")
    private String firstName;
    @TableFieldMapping(table = "CONTACT", field = "EMAIL")
    private String email;
    @TableFieldMapping(table = "CONTACT", field = "PHONE")
    private String phone;
    @TableFieldMapping(table = "CONTACT", field = "FAX")
    private String fax;
    @TableFieldMapping(table = "CONTACT", field = "NOTES")
    private String notes;
    @TableFieldMapping(table = "CONTACT", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "CONTACT", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "CONTACT", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "CONTACT", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "CONTACT", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "CONTACT", field = "ADDRESS1")
    private String address1;
    @TableFieldMapping(table = "CONTACT", field = "ADDRESS2")
    private String address2;
    @TableFieldMapping(table = "CONTACT", field = "CITY")
    private String city;
    @TableFieldMapping(table = "CONTACT", field = "STATE")
    private String state;
    @TableFieldMapping(table = "CONTACT", field = "ZIP")
    private String zip;
    @TableFieldMapping(table = "CONTACT", field = "COUNTRY")
    private String country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    public String getCreatedTpwsKey() {
        return createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

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

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Contact [contactId=" + id + ", lastName=" + lastName
                + ", firstName=" + firstName + ", email=" + email + ", phone="
                + phone + ", fax=" + fax + ", notes=" + notes
                + ", logicalDelete=" + logicalDelete + ", createdTpwsKey="
                + createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
                + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate
                + ", address1=" + address1 + ", address2=" + address2
                + ", city=" + city + ", state=" + state + ", zip=" + zip
                + ", country=" + country + "]";
    }
}

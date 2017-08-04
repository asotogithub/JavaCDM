package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Agency")
public class Agency {

    @TableFieldMapping(table = "AGENCY", field = "AGENCY_ID")
    private Long id;
    @TableFieldMapping(table = "AGENCY", field = "AGENCY_NAME")
    private String name;
    @TableFieldMapping(table = "AGENCY", field = "DOMAIN_ID")
    private Long domainId;
    @TableFieldMapping(table = "AGENCY", field = "AG_ADDRESS1")
    private String address1;
    @TableFieldMapping(table = "AGENCY", field = "AG_ADDRESS2")
    private String address2;
    @TableFieldMapping(table = "AGENCY", field = "AG_CITY")
    private String city;
    @TableFieldMapping(table = "AGENCY", field = "AG_STATE")
    private String state;
    @TableFieldMapping(table = "AGENCY", field = "AG_ZIP")
    private String zipCode;
    @TableFieldMapping(table = "AGENCY", field = "AG_COUNTRY")
    private String country;
    @TableFieldMapping(table = "AGENCY", field = "AG_PHONE")
    private String phoneNumber;
    @TableFieldMapping(table = "AGENCY", field = "AG_FAX")
    private String faxNumber;
    @TableFieldMapping(table = "AGENCY", field = "AG_URL")
    private String url;
    @TableFieldMapping(table = "AGENCY", field = "HTML_INJECTION_ENABLE", valueMappings = {
        @FieldValueMapping(input = "true", output = "1"),
        @FieldValueMapping(input = "false", output = "0")
    })
    private Boolean enableHtmlInjection;
    @TableFieldMapping(table = "AGENCY", field = "AG_NOTES")
    private String notes;
    @TableFieldMapping(table = "AGENCY", field = "IS_ACTIVE", valueMappings = {
        @FieldValueMapping(input = "true", output = "Y"),
        @FieldValueMapping(input = "false", output = "N")
    })
    private Boolean isActive;
    private String isActiveStr;
    @TableFieldMapping(table = "AGENCY", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "AGENCY", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "AGENCY", field = "AG_CONTACT_DEFAULT")
    private String contactDefault;
    @TableFieldMapping(table = "AGENCY", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "AGENCY", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;

    public Agency() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getEnableHtmlInjection() {
        return enableHtmlInjection;
    }

    public void setEnableHtmlInjection(Boolean enableHtmlInjection) {
        this.enableHtmlInjection = enableHtmlInjection;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getContactDefault() {
        return contactDefault;
    }

    public void setContactDefault(String contactDefault) {
        this.contactDefault = contactDefault;
    }

    public String getIsActiveStr() {
        return isActiveStr;
    }

    public void setIsActiveStr(String isActiveStr) {
        this.isActive = isActiveStr != null && isActiveStr.equals("Y") ? Boolean.TRUE : Boolean.FALSE;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Advertiser")
public class Advertiser {

    @TableFieldMapping(table = "ADVERTISER", field = "ADVERTISER_ID")
    private Long id;
    @TableFieldMapping(table = "ADVERTISER", field = "AGENCY_ID")
    private Long agencyId;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERTISER_NAME")
    private String name;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_ADDRESS1")
    private String address1;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_ADDRESS2")
    private String address2;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_CITY")
    private String city;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_STATE")
    private String state;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_ZIP")
    private String zipCode;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_COUNTRY")
    private String country;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_PHONE")
    private String phoneNumber;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_FAX")
    private String faxNumber;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_URL")
    private String url;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_CONTACT_DEFAULT")
    private String contactDefault;
    @TableFieldMapping(table = "ADVERTISER", field = "ENABLE_HTML_TAG", valueMappings= {
        @FieldValueMapping(input="true", output="1"),
        @FieldValueMapping(input="false", output="0")
    })
    private Long enableHtmlTag;
    @TableFieldMapping(table = "ADVERTISER", field = "ADVERT_NOTES")
    private String notes;
    @TableFieldMapping(table = "ADVERTISER", field = "IS_HIDDEN")
    private String isHidden;
    @TableFieldMapping(table = "ADVERTISER", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "ADVERTISER", field = "MODIFIED")
    private Date modifiedDate;
    
    @TableFieldMapping(table = "ADVERTISER", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "ADVERTISER", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;

    public Advertiser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getContactDefault() {
        return contactDefault;
    }

    public void setContactDefault(String contactDefault) {
        this.contactDefault = contactDefault;
    }

    public Long getEnableHtmlTag() {
        return enableHtmlTag;
    }

    public void setEnableHtmlTag(Long enableHtmlTag) {
        this.enableHtmlTag = enableHtmlTag;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
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

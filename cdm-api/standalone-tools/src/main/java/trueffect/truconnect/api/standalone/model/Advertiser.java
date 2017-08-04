package trueffect.truconnect.api.standalone.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Advertiser")
@XmlType(propOrder = {"id", "name", "address1", "address2", "city", 
        "state", "zipCode", "country", "phoneNumber", "faxNumber", 
        "url", "contactDefault", "enableHtmlTag", "notes", "isHidden", 
        "createdDate", "modifiedDate", "brand"})
@XmlSeeAlso({Brand.class})
public class Advertiser {

    private Long id;
    private String name;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
    private String faxNumber;
    private String url;
    private String contactDefault;
    private Long enableHtmlTag;
    private String notes;
    private String isHidden;
    private Date createdDate;
    private Date modifiedDate;
    private Brand brand;

    public Advertiser() {
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

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "Advertiser [id=" + id + ", name=" + name + ", address1="
                + address1 + ", address2=" + address2 + ", city=" + city
                + ", state=" + state + ", zipCode=" + zipCode + ", country="
                + country + ", phoneNumber=" + phoneNumber + ", faxNumber="
                + faxNumber + ", url=" + url + ", contactDefault="
                + contactDefault + ", enableHtmlTag=" + enableHtmlTag
                + ", notes=" + notes + ", isHidden=" + isHidden
                + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + ", brand=" + brand + "]";
    }
}

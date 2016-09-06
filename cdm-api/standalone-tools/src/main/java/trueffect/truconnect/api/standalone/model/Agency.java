package trueffect.truconnect.api.standalone.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Agency")
@XmlType(propOrder = {"id", "name", "domainId", "address1", "address2",
        "city", "state", "zipCode", "country", "phoneNumber", "faxNumber",
        "url", "enableHtmlInjection", "notes", "isActive", "isActiveStr",
        "createdDate", "modifiedDate", "contactDefault", "advertiser",
        "publishers", "sizes"})
@XmlSeeAlso({Advertiser.class, Publisher.class, Size.class})
public class Agency {

    private Long id;
    private String name;
    private Long domainId;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
    private String faxNumber;
    private String url;
    private Boolean enableHtmlInjection;
    private String notes;
    private Boolean isActive;
    private String isActiveStr;
    private Date createdDate;
    private Date modifiedDate;
    private String contactDefault;
    private Advertiser advertiser;
    private List<Publisher> publishers;
    private List<Size> sizes;

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

    public Advertiser getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(Advertiser advertiser) {
        this.advertiser = advertiser;
    }

    @XmlElementWrapper(name = "publishers")
    @XmlAnyElement(lax = true)
    public List<Publisher> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<Publisher> publishers) {
        this.publishers = publishers;
    }

    @XmlElementWrapper(name = "sizes")
    @XmlAnyElement(lax = true)
    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    @Override
    public String toString() {
        return "Agency [id=" + id + ", name=" + name + ", domainId=" + domainId
                + ", address1=" + address1 + ", address2=" + address2
                + ", city=" + city + ", state=" + state + ", zipCode="
                + zipCode + ", country=" + country + ", phoneNumber="
                + phoneNumber + ", faxNumber=" + faxNumber + ", url=" + url
                + ", enableHtmlInjection=" + enableHtmlInjection + ", notes="
                + notes + ", isActive=" + isActive + ", isActiveStr="
                + isActiveStr + ", createdDate=" + createdDate
                + ", modifiedDate=" + modifiedDate + ", contactDefault="
                + contactDefault + ", advertiser=" + advertiser
                + ", publishers=" + publishers + ", sizes=" + sizes + "]";
    }
}

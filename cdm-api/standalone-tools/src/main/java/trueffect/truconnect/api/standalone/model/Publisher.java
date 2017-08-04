package trueffect.truconnect.api.standalone.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "Publisher")
@XmlType(propOrder = {"id", "name", "address1", "address2", "city", 
        "state", "zipCode", "country", "phoneNumber", "url", 
        "agencyNotes", "logicalDelete", "createdTpwsKey", 
        "modifiedTpwsKey", "createdDate", "modifiedDate", "sites"})
@XmlSeeAlso({Site.class})
public class Publisher {

    private Long id;
    private String name;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
    private String url;
    private String agencyNotes;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;
    private List<Site> sites;

    public Publisher() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAgencyNotes() {
        return agencyNotes;
    }

    public void setAgencyNotes(String agencyNotes) {
        this.agencyNotes = agencyNotes;
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

    @XmlElementWrapper(name = "sites")
    @XmlAnyElement(lax = true)
    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    @Override
    public String toString() {
        return "Publisher [id=" + id + ", name=" + name + ", address1="
                + address1 + ", address2=" + address2 + ", city=" + city
                + ", state=" + state + ", zipCode=" + zipCode + ", country="
                + country + ", phoneNumber=" + phoneNumber + ", url=" + url
                + ", agencyNotes=" + agencyNotes + ", logicalDelete="
                + logicalDelete + ", createdTpwsKey=" + createdTpwsKey
                + ", modifiedTpwsKey=" + modifiedTpwsKey + ", createdDate="
                + createdDate + ", modifiedDate=" + modifiedDate + ", sites="
                + sites + "]";
    }
}

package trueffect.truconnect.api.commons.model;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Publisher")
public class Publisher implements Serializable{

    @TableFieldMapping(table = "PUBLISHER", field = "PUBLISHER_ID")
    private Long id;
    @TableFieldMapping(table = "PUBLISHER", field = "PUBLISHER_NAME")
    private String name;
    @TableFieldMapping(table = "PUBLISHER", field = "DUP_PUB_NAME")
    private String dupName;
    @TableFieldMapping(table = "PUBLISHER", field = "PUB_ADDRESS1")
    private String address1;
    @TableFieldMapping(table = "PUBLISHER", field = "PUB_ADDRESS2")
    private String address2;
    @TableFieldMapping(table = "PUBLISHER", field = "PUB_CITY")
    private String city;
    @TableFieldMapping(table = "PUBLISHER", field = "PUB_STATE")
    private String state;
    @TableFieldMapping(table = "PUBLISHER", field = "PUB_ZIP")
    private String zipCode;
    @TableFieldMapping(table = "PUBLISHER", field = "PUB_COUNTRY")
    private String country;
    @TableFieldMapping(table = "PUBLISHER", field = "PUB_PHONE")
    private String phoneNumber;
    @TableFieldMapping(table = "PUBLISHER", field = "PUB_URL")
    private String url;
    @TableFieldMapping(table = "PUBLISHER", field = "AGENCY_NOTES")
    private String agencyNotes;
    @TableFieldMapping(table = "PUBLISHER", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "PUBLISHER", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "PUBLISHER", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "PUBLISHER", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "PUBLISHER", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "AGENCY_PUBLISHER", field = "AGENCY_ID")
    private Long agencyId;

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

    public String getDupName() {
        return dupName;
    }

    public void setDupName(String dupName) {
        this.dupName = dupName;
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

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    @Override
    public String toString() {
        return "agencyId:" + agencyId + ", id:" + id
                + ", name:" + name + ", dupName:" + dupName + ", address1:"
                + address1 + ", address2:" + address2 + ", city:" + city
                + ", state:" + state + ", zipCode:" + zipCode + ", country:"
                + country + ", phoneNumber:" + phoneNumber + ", url:" + url
                + ", agencyNotes:" + agencyNotes + ", logicalDelete:"
                + logicalDelete + ", createdTpwsKey:" + createdTpwsKey
                + ", modifiedTpwsKey:" + modifiedTpwsKey + ", createdDate:"
                + createdDate + ", modifiedDate:" + modifiedDate;
    }
}

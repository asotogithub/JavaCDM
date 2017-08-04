package trueffect.truconnect.api.commons.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

@XmlRootElement(name = "Organization")
public class Organization {

    @TableFieldMapping(table = "ORGANIZATION", field = "ORGANIZATION_ID")
    private Long id;
    @TableFieldMapping(table = "ORGANIZATION", field = "ORGANIZATION_NAME")
    private String name;
    @TableFieldMapping(table = "ORGANIZATION", field = "DUP_ORGANIZATION_NAME")
    private String dupName;
    @TableFieldMapping(table = "ORGANIZATION", field = "ORG_ADDRESS1")
    private String address1;
    @TableFieldMapping(table = "ORGANIZATION", field = "ORG_ADDRESS2")
    private String address2;
    @TableFieldMapping(table = "ORGANIZATION", field = "ORG_CITY")
    private String city;
    @TableFieldMapping(table = "ORGANIZATION", field = "ORG_STATE")
    private String state;
    @TableFieldMapping(table = "ORGANIZATION", field = "ORG_ZIP")
    private String zip;
    @TableFieldMapping(table = "ORGANIZATION", field = "ORG_COUNTRY")
    private String country;
    @TableFieldMapping(table = "ORGANIZATION", field = "ORG_PHONE")
    private String phone;
    @TableFieldMapping(table = "ORGANIZATION", field = "ORG_URL")
    private String url;
    @TableFieldMapping(table = "ORGANIZATION", field = "IMAGE_PATH")
    private String imagePath;
    @TableFieldMapping(table = "ORGANIZATION", field = "FORE_COLOR")
    private String foreColor;
    @TableFieldMapping(table = "ORGANIZATION", field = "BACK_COLOR")
    private String backColor;
    @TableFieldMapping(table = "ORGANIZATION", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "ORGANIZATION", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "ORGANIZATION", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "ORGANIZATION", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "ORGANIZATION", field = "LOGICAL_DELETE")
    private String logicalDelete;

    public Organization() {
    }

    public Organization(Long organizationId, String organizationName,
            String createdTpwsKey, String modifiedTpwsKey) {
        this.id = organizationId;
        this.name = organizationName;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public Organization(Long id, String name,
            String dupName, String address1, String address2,
            String city, String state, String zip, String country,
            String phone, String url, String imagePath, String foreColor,
            String backColor, String createdTpwsKey, String modifiedTpwsKey,
            Date created, Date modified, String logicalDelete) {
        this.id = id;
        this.name = name;
        this.dupName = dupName;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.phone = phone;
        this.url = url;
        this.imagePath = imagePath;
        this.foreColor = foreColor;
        this.backColor = backColor;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
        this.createdDate = created;
        this.modifiedDate = modified;
        this.logicalDelete = logicalDelete;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getForeColor() {
        return foreColor;
    }

    public void setForeColor(String foreColor) {
        this.foreColor = foreColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
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

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

	@Override
	public String toString() {
		return "Organization [id=" + id + ", name=" + name + ", dupName="
				+ dupName + ", address1=" + address1 + ", address2=" + address2
				+ ", city=" + city + ", state=" + state + ", zip=" + zip
				+ ", country=" + country + ", phone=" + phone + ", url=" + url
				+ ", imagePath=" + imagePath + ", foreColor=" + foreColor
				+ ", backColor=" + backColor + ", createdTpwsKey="
				+ createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
				+ ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + ", logicalDelete=" + logicalDelete + "]";
	}
}

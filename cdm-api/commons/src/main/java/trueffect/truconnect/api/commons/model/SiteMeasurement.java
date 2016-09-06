package trueffect.truconnect.api.commons.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "SiteMeasurement")
public class SiteMeasurement {

    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "MEASUREMENT_ID")
    private Long id;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "BRAND_ID")
    private Long brandId;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "SITE_ID")
    private Long siteId;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "MEASUREMENT_NAME")
    private String name;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "DUP_MEASURE_NAME")
    private String dupName;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "MEASUREMENT_STATE")
    private Long state;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "GENERAL_NOTES")
    private String notes;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "COOKIE_DOMAIN_ID")
    private Long cookieDomainId;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "EXPIRATION_DATE")
    private Date expirationDate;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "TT_VER")
    private Long ttVer;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "RESOURCE_PATH_ID")
    private Long resourcePathId;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "CL_WINDOW")
    private Long clWindow;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "VT_WINDOW")
    private Long vtWindow;
    @TableFieldMapping(table = "SITE_MEASUREMENT", field = "ASSOC_METHOD")
    private String assocMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
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

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
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

    public Long getCookieDomainId() {
        return cookieDomainId;
    }

    public void setCookieDomainId(Long cookieDomainId) {
        this.cookieDomainId = cookieDomainId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getTtVer() {
        return ttVer;
    }

    public void setTtVer(Long ttVer) {
        this.ttVer = ttVer;
    }

    public Long getResourcePathId() {
        return resourcePathId;
    }

    public void setResourcePathId(Long resourcePathId) {
        this.resourcePathId = resourcePathId;
    }

    public Long getClWindow() {
        return clWindow;
    }

    public void setClWindow(Long clWindow) {
        this.clWindow = clWindow;
    }

    public Long getVtWindow() {
        return vtWindow;
    }

    public void setVtWindow(Long vtWindow) {
        this.vtWindow = vtWindow;
    }

    public String getAssocMethod() {
        return assocMethod;
    }

    public void setAssocMethod(String assocMethod) {
        this.assocMethod = assocMethod;
    }

	@Override
	public String toString() {
		return "SiteMeasurement [id=" + id + ", brandId=" + brandId
				+ ", siteId=" + siteId + ", name=" + name + ", dupName="
				+ dupName + ", state=" + state + ", notes=" + notes
				+ ", logicalDelete=" + logicalDelete + ", createdTpwsKey="
				+ createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
				+ ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + ", cookieDomainId=" + cookieDomainId
				+ ", expirationDate=" + expirationDate + ", ttVer=" + ttVer
				+ ", resourcePathId=" + resourcePathId + ", clWindow="
				+ clWindow + ", vtWindow=" + vtWindow + ", assocMethod="
				+ assocMethod + "]";
	}
}

package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;
import trueffect.truconnect.api.commons.model.SiteMeasurement;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "SiteMeasurementDTO")
public class SiteMeasurementDTO implements Serializable {

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

    @TableFieldMapping(table = "ADVERTISER", field = "ADVERTISER_ID")
    private Long advertiserId;

    @TableFieldMapping(table = "ADVERTISER", field = "ADVERTISER_NAME")
    private String advertiserName;

    @TableFieldMapping(table = "BRAND", field = "BRAND_NAME")
    private String brandName;

    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "DOMAIN")
    private String domain;

    @TableFieldMapping(table = "PPP", field = "NUMBER_OF_EVENTS")
    private Long numberOfEvents;

    public SiteMeasurementDTO() {
    }

    public SiteMeasurementDTO(SiteMeasurement measurement) {
        this.setId(measurement.getId());
        this.setBrandId(measurement.getBrandId());
        this.setSiteId(measurement.getSiteId());
        this.setName(measurement.getName());
        this.setDupName(measurement.getDupName());
        this.setState(measurement.getState());
        this.setNotes(measurement.getNotes());
        this.setLogicalDelete(measurement.getLogicalDelete());
        this.setCreatedTpwsKey(measurement.getCreatedTpwsKey());
        this.setModifiedTpwsKey(measurement.getModifiedTpwsKey());
        this.setCreatedDate(measurement.getCreatedDate());
        this.setModifiedDate(measurement.getModifiedDate());
        this.setCookieDomainId(measurement.getCookieDomainId());
        this.setExpirationDate(measurement.getExpirationDate());
        this.setTtVer(measurement.getTtVer());
        this.setResourcePathId(measurement.getResourcePathId());
        this.setClWindow(measurement.getClWindow());
        this.setVtWindow(measurement.getVtWindow());
        this.setAssocMethod(measurement.getAssocMethod());
    }

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
   
    /**
     * @return the advertiserName
     */
    public String getAdvertiserName() {
        return advertiserName;
    }

    /**
     * @param advertiserName the advertiserName to set
     */
    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    /**
     * @return the brandName
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * @param brandName the brandName to set
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return the advertiserId
     */
    public Long getAdvertiserId() {
        return advertiserId;
    }

    /**
     * @param advertiserId the advertiserId to set
     */
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getNumberOfEvents() {
        return numberOfEvents;
    }

    public void setNumberOfEvents(Long numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "SiteSection")
public class SiteSection {

    @TableFieldMapping(table = "SITE_SECTION", field = "SECTION_ID")
    private Long id;
    @TableFieldMapping(table = "SITE_SECTION", field = "SITE_ID")
    private Long siteId;
    @TableFieldMapping(table = "SITE_SECTION", field = "SECTION_NAME")
    private String name;
    @TableFieldMapping(table = "SITE_SECTION", field = "AGENCY_NOTES")
    private String agencyNotes;
    @TableFieldMapping(table = "SITE_SECTION", field = "PUBLISHER_NOTES")
    private String publisherNotes;
    @TableFieldMapping(table = "SITE_SECTION", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "SITE_SECTION", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "SITE_SECTION", field = "EXT_PROP1")
    private String extProp1;
    @TableFieldMapping(table = "SITE_SECTION", field = "EXT_PROP2")
    private String extProp2;
    @TableFieldMapping(table = "SITE_SECTION", field = "EXT_PROP3")
    private String extProp3;
    @TableFieldMapping(table = "SITE_SECTION", field = "EXT_PROP4")
    private String extProp4;
    @TableFieldMapping(table = "SITE_SECTION", field = "EXT_PROP5")
    private String extProp5;
    @TableFieldMapping(table = "SITE_SECTION", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "SITE_SECTION", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAgencyNotes() {
        return agencyNotes;
    }

    public void setAgencyNotes(String agencyNotes) {
        this.agencyNotes = agencyNotes;
    }

    public String getPublisherNotes() {
        return publisherNotes;
    }

    public void setPublisherNotes(String publisherNotes) {
        this.publisherNotes = publisherNotes;
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

    public String getExtProp1() {
        return extProp1;
    }

    public void setExtProp1(String extProp1) {
        this.extProp1 = extProp1;
    }

    public String getExtProp2() {
        return extProp2;
    }

    public void setExtProp2(String extProp2) {
        this.extProp2 = extProp2;
    }

    public String getExtProp3() {
        return extProp3;
    }

    public void setExtProp3(String extProp3) {
        this.extProp3 = extProp3;
    }

    public String getExtProp4() {
        return extProp4;
    }

    public void setExtProp4(String extProp4) {
        this.extProp4 = extProp4;
    }

    public String getExtProp5() {
        return extProp5;
    }

    public void setExtProp5(String extProp5) {
        this.extProp5 = extProp5;
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

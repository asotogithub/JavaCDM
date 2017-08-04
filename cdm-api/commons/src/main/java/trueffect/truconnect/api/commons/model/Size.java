package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object that represents a <code>Size</code>
 * @author richard Jaldin
 */
@XmlRootElement(name = "Size")
public class Size {
    /**
     * id for this Size
     */
    @TableFieldMapping(table = "AD_SIZE", field = "AD_SIZE_ID")
    private Long id;
    /**
     * Agency id this Size belongs to
     */
    @TableFieldMapping(table = "AD_SIZE", field = "AGENCY_ID")
    private Long agencyId;

    @TableFieldMapping(table = "AD_SIZE", field = "WIDTH")
    private Long width;
    /**
     * The <code>Size</code> height
     */
    @TableFieldMapping(table = "AD_SIZE", field = "HEIGHT")
    private Long height;
    @TableFieldMapping(table = "AD_SIZE", field = "SIZE_NAME")
    private String label;
    @TableFieldMapping(table = "AD_SIZE", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "AD_SIZE", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "AD_SIZE", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "AD_SIZE", field = "MODIFIED")
    private Date modifiedDate;

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

    /**
     * Returns the <code>Size</code> width
     * @return the <code>Size</code> width
     */
    @XmlElement(required = true, nillable = false)
    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    @XmlElement(required = true, nillable = false)
    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

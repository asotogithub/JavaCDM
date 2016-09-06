package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Brand")
public class Brand {

    @TableFieldMapping(table = "BRAND", field = "BRAND_ID")
    private Long id;
    @TableFieldMapping(table = "BRAND", field = "ADVERTISER_ID")
    private Long advertiserId;
    @TableFieldMapping(table = "BRAND", field = "BRAND_NAME")
    private String name;
    @TableFieldMapping(table = "BRAND", field = "DESCRIPTION")
    private String description;
    @TableFieldMapping(table = "BRAND", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "BRAND", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "BRAND", field = "IS_HIDDEN")
    private String isHidden;
    @TableFieldMapping(table = "BRAND", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "BRAND", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    public Brand() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
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

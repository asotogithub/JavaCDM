package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author marleny.patsi
 */
@XmlRootElement(name = "HtmlInjectionTagAssociation")
public class HtmlInjectionTagAssociation implements Serializable {

    private Long id;
    private Long campaignId;
    private Long htmlInjectionId;
    private Long entityType;
    private Long entityId;
    private Long isEnabled;
    private Long isDeleted;
    private Date createdDate;
    private Date modifiedDate;
    private String createdTpwsKey;
    private String modifiedTpwsKey;

    private String htmlInjectionName;
    private Long isInherited;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getHtmlInjectionId() {
        return htmlInjectionId;
    }

    public void setHtmlInjectionId(Long htmlInjectionId) {
        this.htmlInjectionId = htmlInjectionId;
    }

    public Long getEntityType() {
        return entityType;
    }

    public void setEntityType(Long entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Long isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Long getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Long isDeleted) {
        this.isDeleted = isDeleted;
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

    public String getHtmlInjectionName() {
        return htmlInjectionName;
    }

    public void setHtmlInjectionName(String htmlInjectionName) {
        this.htmlInjectionName = htmlInjectionName;
    }

    public Long getIsInherited() {
        return isInherited;
    }

    public void setIsInherited(Long isInherited) {
        this.isInherited = isInherited;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

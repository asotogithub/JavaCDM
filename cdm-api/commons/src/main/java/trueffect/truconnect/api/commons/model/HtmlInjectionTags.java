package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Saulo Lopez
 */
@XmlRootElement(name = "HtmlInjectionTags")
public class HtmlInjectionTags {
    private Long id;
    private String name;
    private String htmlContent;
    private String secureHtmlContent;
    private Long isEnabled;
    private Long agencyId;
    private Long isVisible;
    private Date createdDate;
    private Date modifiedDate;
    private String createdTpwsKey;
    private String modifiedTpwsKey;

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

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getSecureHtmlContent() {
        return secureHtmlContent;
    }

    public void setSecureHtmlContent(String secureHtmlContent) {
        this.secureHtmlContent = secureHtmlContent;
    }

    public Long getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Long isEnabled) {
        this.isEnabled = isEnabled;
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

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Long isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package trueffect.truconnect.api.commons.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "CookieTargetTemplate")
public class CookieTargetTemplate {

    @TableFieldMapping(table = "COOKIE_TARGET_TEMPLATE", field = "COOKIE_TARGET_TEMPLATE_ID")
    private Long cookieTargetTemplateId;
    @TableFieldMapping(table = "COOKIE_TARGET_TEMPLATE", field = "COOKIE_DOMAIN_ID")
    private Long cookieDomainId;
    @TableFieldMapping(table = "COOKIE_TARGET_TEMPLATE", field = "COOKIE_NAME")
    private String cookieName;
    @TableFieldMapping(table = "COOKIE_TARGET_TEMPLATE", field = "COOKIE_CONTENT_TYPE")
    private Byte cookieContentType;
    @TableFieldMapping(table = "COOKIE_TARGET_TEMPLATE", field = "CONTENT_POSSIBLE_VALUES")
    private String contentPossibleValues;
    @TableFieldMapping(table = "COOKIE_TARGET_TEMPLATE", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "COOKIE_TARGET_TEMPLATE", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "COOKIE_TARGET_TEMPLATE", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "COOKIE_TARGET_TEMPLATE", field = "TRACKING_COOKIE")
    private Character trackingCookie;

    public CookieTargetTemplate() {
    }

    public Long getCookieTargetTemplateId() {
        return cookieTargetTemplateId;
    }

    public void setCookieTargetTemplateId(Long cookieTargetTemplateId) {
        this.cookieTargetTemplateId = cookieTargetTemplateId;
    }

    public Long getCookieDomainId() {
        return cookieDomainId;
    }

    public void setCookieDomainId(Long cookieDomainId) {
        this.cookieDomainId = cookieDomainId;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public Byte getCookieContentType() {
        return cookieContentType;
    }

    public void setCookieContentType(Byte cookieContentType) {
        this.cookieContentType = cookieContentType;
    }

    public String getContentPossibleValues() {
        return contentPossibleValues;
    }

    public void setContentPossibleValues(String contentPossibleValues) {
        this.contentPossibleValues = contentPossibleValues;
    }

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
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

    public Character getTrackingCookie() {
        return trackingCookie;
    }

    public void setTrackingCookie(Character trackingCookie) {
        this.trackingCookie = trackingCookie;
    }

	@Override
	public String toString() {
		return "CookieTargetTemplate [cookieTargetTemplateId="
				+ cookieTargetTemplateId + ", cookieDomainId=" + cookieDomainId
				+ ", cookieName=" + cookieName + ", cookieContentType="
				+ cookieContentType + ", contentPossibleValues="
				+ contentPossibleValues + ", logicalDelete=" + logicalDelete
				+ ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + ", trackingCookie=" + trackingCookie + "]";
	}
}

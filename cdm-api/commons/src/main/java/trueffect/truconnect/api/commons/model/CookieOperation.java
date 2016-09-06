package trueffect.truconnect.api.commons.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "CookieOperation")
public class CookieOperation {

    @TableFieldMapping(table = "COOKIE_OPERATION", field = "COOKIE_OPERATION_ID")
    private Long cookieOperationId;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "COOKIE_NAME")
    private String cookieName;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "COOKIE_DOMAIN_ID")
    private Long cookieDomainId;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "EXPIRATION_DAYS")
    private Long expirationDays;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "COOKIE_OVERWRITE_BEHAVE")
    private Long cookieOverwriteBehave;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "TRAFFIC_STATUS_FLAG", valueMappings= {
    		@FieldValueMapping(input="true", output="1"),
    		@FieldValueMapping(input="false", output="0")
    })
    private Long trafficStatusFlag;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "COOKIE_OPERATION", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;

    public Long getCookieOperationId() {
        return cookieOperationId;
    }

    public void setCookieOperationId(Long cookieOperationId) {
        this.cookieOperationId = cookieOperationId;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public Long getCookieDomainId() {
        return cookieDomainId;
    }

    public void setCookieDomainId(Long cookieDomainId) {
        this.cookieDomainId = cookieDomainId;
    }

    public Long getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(Long expirationDays) {
        this.expirationDays = expirationDays;
    }

    public Long getCookieOverwriteBehave() {
        return cookieOverwriteBehave;
    }

    public void setCookieOverwriteBehave(Long cookieOverwriteBehave) {
        this.cookieOverwriteBehave = cookieOverwriteBehave;
    }

    public Long getTrafficStatusFlag() {
        return trafficStatusFlag;
    }

    public void setTrafficStatusFlag(Long trafficStatusFlag) {
        this.trafficStatusFlag = trafficStatusFlag;
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
		return "CookieOperation [cookieOperationId=" + cookieOperationId
				+ ", cookieName=" + cookieName + ", cookieDomainId="
				+ cookieDomainId + ", expirationDays=" + expirationDays
				+ ", cookieOverwriteBehave=" + cookieOverwriteBehave
				+ ", trafficStatusFlag=" + trafficStatusFlag
				+ ", logicalDelete=" + logicalDelete + ", createdDate="
				+ createdDate + ", modifiedDate=" + modifiedDate
				+ ", createdTpwsKey=" + createdTpwsKey + ", modifiedTpwsKey="
				+ modifiedTpwsKey + "]";
	}
}

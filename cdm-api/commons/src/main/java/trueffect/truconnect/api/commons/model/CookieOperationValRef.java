package trueffect.truconnect.api.commons.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "CookieOperationValRef")
public class CookieOperationValRef {

    @TableFieldMapping(table = "COOKIE_OPERATION_VAL_REF", field = "COOKIE_OPERATION_VALUE_ID")
    private Long cookieOperationValueId;
    @TableFieldMapping(table = "COOKIE_OPERATION_VAL_REF", field = "COOKIE_REF_ENTITY_TYPE")
    private Long cookieRefEntityType;
    @TableFieldMapping(table = "COOKIE_OPERATION_VAL_REF", field = "COOKIE_REF_ENTITY_ID")
    private Long cookieRefEntityId;
    @TableFieldMapping(table = "COOKIE_OPERATION_VAL_REF", field = "CAMPAIGN_ID")
    private Long campaignId;
    @TableFieldMapping(table = "COOKIE_OPERATION_VAL_REF", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "COOKIE_OPERATION_VAL_REF", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "COOKIE_OPERATION_VAL_REF", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "COOKIE_OPERATION_VAL_REF", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "COOKIE_OPERATION_VAL_REF", field = "MODIFIED")
    private Date modifiedDate;

    public Long getCookieOperationValueId() {
        return cookieOperationValueId;
    }

    public void setCookieOperationValueId(Long cookieOperationValueId) {
        this.cookieOperationValueId = cookieOperationValueId;
    }

    public Long getCookieRefEntityType() {
        return cookieRefEntityType;
    }

    public void setCookieRefEntityType(Long cookieRefEntityType) {
        this.cookieRefEntityType = cookieRefEntityType;
    }

    public Long getCookieRefEntityId() {
        return cookieRefEntityId;
    }

    public void setCookieRefEntityId(Long cookieRefEntityId) {
        this.cookieRefEntityId = cookieRefEntityId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
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

	@Override
	public String toString() {
		return "CookieOperationValRef [cookieOperationValueId="
				+ cookieOperationValueId + ", cookieRefEntityType="
				+ cookieRefEntityType + ", cookieRefEntityId="
				+ cookieRefEntityId + ", campaignId=" + campaignId
				+ ", logicalDelete=" + logicalDelete + ", createdTpwsKey="
				+ createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
				+ ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + "]";
	}
}

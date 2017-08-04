package trueffect.truconnect.api.commons.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "CookieOperationValue")
public class CookieOperationValue {

    @TableFieldMapping(table = "COOKIE_OPERATION_VALUE", field = "COOKIE_OPERATION_VALUE_ID")
    private Long cookieOperationValueId;
    @TableFieldMapping(table = "COOKIE_OPERATION_VALUE", field = "COOKIE_OPERATION_ID")
    private Long cookieOperationId;
    @TableFieldMapping(table = "COOKIE_OPERATION_VALUE", field = "COOKIE_VALUE")
    private String cookieValue;
    @TableFieldMapping(table = "COOKIE_OPERATION_VALUE", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "COOKIE_OPERATION_VALUE", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "COOKIE_OPERATION_VALUE", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "COOKIE_OPERATION_VALUE", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "COOKIE_OPERATION_VALUE", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;

    public Long getCookieOperationValueId() {
        return cookieOperationValueId;
    }

    public void setCookieOperationValueId(Long cookieOperationValueId) {
        this.cookieOperationValueId = cookieOperationValueId;
    }

    public Long getCookieOperationId() {
        return cookieOperationId;
    }

    public void setCookieOperationId(Long cookieOperationId) {
        this.cookieOperationId = cookieOperationId;
    }

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
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

	@Override
	public String toString() {
		return "CookieOperationValue [cookieOperationValueId="
				+ cookieOperationValueId + ", cookieOperationId="
				+ cookieOperationId + ", cookieValue=" + cookieValue
				+ ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + ", logicalDelete=" + logicalDelete
				+ ", createdTpwsKey=" + createdTpwsKey + ", modifiedTpwsKey="
				+ modifiedTpwsKey + "]";
	}
}

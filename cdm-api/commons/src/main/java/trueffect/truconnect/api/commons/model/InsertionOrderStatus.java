package trueffect.truconnect.api.commons.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "InsertionOrderStatus")
public class InsertionOrderStatus {

    @TableFieldMapping(table = "INSERTION_ORDER_STATUS", field = "IO_ID")
    private Long ioId;
    @TableFieldMapping(table = "INSERTION_ORDER_STATUS", field = "STATUS_ID")
    private Long statusId;
    @TableFieldMapping(table = "INSERTION_ORDER_STATUS", field = "STATUS_DATE")
    private Date statusDate;
    @TableFieldMapping(table = "INSERTION_ORDER_STATUS", field = "CONTACT_ID")
    private Long contactId;
    @TableFieldMapping(table = "INSERTION_ORDER_STATUS", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "INSERTION_ORDER_STATUS", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "INSERTION_ORDER_STATUS", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "INSERTION_ORDER_STATUS", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "INSERTION_ORDER_STATUS", field = "MODIFIED")
    private Date modifiedDate;

    public Long getIoId() {
        return ioId;
    }

    public void setIoId(Long ioId) {
        this.ioId = ioId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
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
		return "InsertionOrderStatus [ioId=" + ioId + ", statusId=" + statusId
				+ ", statusDate=" + statusDate + ", contactId=" + contactId
				+ ", logicalDelete=" + logicalDelete + ", createdTpwsKey="
				+ createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
				+ ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + "]";
	}
}

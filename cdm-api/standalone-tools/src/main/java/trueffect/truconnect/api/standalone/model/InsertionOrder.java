package trueffect.truconnect.api.standalone.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "InsertionOrder")
@XmlType(propOrder = {"id", "publisherId", "ioNumber", "name", "notes",
        "logicalDelete", "createdTpwsKey", "modifiedTpwsKey", "createdDate",
        "modifiedDate", "status"})
public class InsertionOrder {

    private Long id;
    private Long publisherId;
    private Integer ioNumber;
    private String name;
    private String notes;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;
    private String status;

    public InsertionOrder() {
    }

    public InsertionOrder(Long id, Long publisherId,
            Integer ioNumber, String name, String notes,
            String createdTpwsKey) {
        this.id = id;
        this.publisherId = publisherId;
        this.ioNumber = ioNumber;
        this.name = name;
        this.notes = notes;
        this.createdTpwsKey = createdTpwsKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public Integer getIoNumber() {
        return ioNumber;
    }

    public void setIoNumber(Integer ioNumber) {
        this.ioNumber = ioNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "InsertionOrder [id=" + id + ", publisherId=" + publisherId
                + ", ioNumber=" + ioNumber + ", name=" + name + ", notes="
                + notes + ", logicalDelete=" + logicalDelete
                + ", createdTpwsKey=" + createdTpwsKey + ", modifiedTpwsKey="
                + modifiedTpwsKey + ", createdDate=" + createdDate
                + ", modifiedDate=" + modifiedDate + ", status=" + status + "]";
    }

}

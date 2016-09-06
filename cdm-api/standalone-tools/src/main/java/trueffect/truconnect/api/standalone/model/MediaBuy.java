package trueffect.truconnect.api.standalone.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "MediaBuy")
@XmlType(propOrder = {"id", "name", "state", "overallBudget", 
        "agencyNotes", "createdDate", "modifiedDate", "insertionOrders"})
@XmlSeeAlso({InsertionOrder.class})
public class MediaBuy {

    private Long id;
    private String name;
    private String state;
    private Long overallBudget;
    private String agencyNotes;
    private Date createdDate;
    private Date modifiedDate;
    private List<InsertionOrder> insertionOrders;

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
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public Long getOverallBudget() {
        return overallBudget;
    }
    public void setOverallBudget(Long overallBudget) {
        this.overallBudget = overallBudget;
    }
    public String getAgencyNotes() {
        return agencyNotes;
    }
    public void setAgencyNotes(String agencyNotes) {
        this.agencyNotes = agencyNotes;
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

    @XmlElementWrapper(name = "insertionOrders")
    @XmlAnyElement(lax = true)
    public List<InsertionOrder> getInsertionOrders() {
        return insertionOrders;
    }
    public void setInsertionOrders(List<InsertionOrder> insertionOrders) {
        this.insertionOrders = insertionOrders;
    }
    @Override
    public String toString() {
        return "MediaBuy [id=" + id + ", name=" + name + ", state=" + state
                + ", overallBudget=" + overallBudget + ", agencyNotes="
                + agencyNotes + ", createdDate=" + createdDate
                + ", modifiedDate=" + modifiedDate + ", insertionOrders="
                + insertionOrders + "]";
    }
}

package trueffect.truconnect.api.commons.model;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "ScheduleEntry")
public class ScheduleEntry {

    private Long id;
    private Date startDate;
    private Date endDate;
    private String timeZone;
    private Long weight;
    private Long sequence;
    private List<Clickthrough> clickthroughs;
    private Boolean isReleased;
    private Date createdDate;
    private Date modifiedDate;

    public ScheduleEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    @XmlElementWrapper(name = "clickthroughs")
    @XmlAnyElement(lax = true)
    public List<Clickthrough> getClickthroughs() {
        return clickthroughs;
    }

    public void setClickthroughs(List<Clickthrough> clickthroughs) {
        this.clickthroughs = clickthroughs;
    }

    public Boolean getIsReleased() {
        return isReleased;
    }

    public void setIsReleased(Boolean isReleased) {
        this.isReleased = isReleased;
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

    @Override
    public String toString() {
        return "ScheduleEntry{" + "id=" + id + ", startDate=" + startDate
                + ", endDate=" + endDate + ", timeZone=" + timeZone
                + ", weight=" + weight + ", sequence=" + sequence
                + ", clickthroughs=" + clickthroughs + ", isReleased="
                + isReleased + ", createdDate=" + createdDate
                + ", modifiedDate=" + modifiedDate + '}';
    }
}

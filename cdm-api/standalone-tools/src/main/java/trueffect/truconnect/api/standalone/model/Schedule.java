package trueffect.truconnect.api.standalone.model;

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
@XmlRootElement(name = "Schedule")
@XmlType(propOrder = {"id", "placementId", "creativeGroupId", "creativeId", "entries"})
@XmlSeeAlso({ScheduleEntry.class})
public class Schedule {

    private Long id;
    private Long placementId;
    private Long creativeGroupId;
    private Long creativeId;
    private List<ScheduleEntry> entries;

    public Schedule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    public Long getCreativeGroupId() {
        return creativeGroupId;
    }

    public void setCreativeGroupId(Long creativeGroupId) {
        this.creativeGroupId = creativeGroupId;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    @XmlElementWrapper(name = "entries")
    @XmlAnyElement(lax = true)
    public List<ScheduleEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ScheduleEntry> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "Schedule [id=" + id + ", placementId=" + placementId
                + ", creativeGroupId=" + creativeGroupId + ", creativeId="
                + creativeId + ", entries=" + entries + "]";
    }
}
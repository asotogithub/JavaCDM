package trueffect.truconnect.api.commons.model;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "Schedule")
public class Schedule {

    @TableFieldMapping(table = "CREATIVE_INSERTION", field = "CREATIVE_INSERTION_ID")
    private Long id;
    @TableFieldMapping(table = "PLACEMENT", field = "PLACEMENT_ID")
    private Long placementId;
    @TableFieldMapping(table = "CREATIVE_GROUP", field = "CREATIVE_GROUP_ID")
    private Long creativeGroupId;
    @TableFieldMapping(table = "CREATIVE", field = "CREATIVE_ID")
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

    public List<ScheduleEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ScheduleEntry> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "Schedule{" + "id=" + id + ", placementId=" + placementId
                + ", creativeGroupId=" + creativeGroupId + ", creativeId="
                + creativeId + ", entries=" + entries + '}';
    }
}
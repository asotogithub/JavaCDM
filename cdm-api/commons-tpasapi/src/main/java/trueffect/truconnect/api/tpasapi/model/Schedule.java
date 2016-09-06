package trueffect.truconnect.api.tpasapi.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Schedule")
@XmlType(propOrder = {"creativeGroupId", "placements", "creatives",
    "createdDate", "modifiedDate"})
@XmlSeeAlso({ScheduledPlacement.class, ScheduledCreative.class})
public class Schedule implements Serializable {

    private Long creativeGroupId;
    private List<ScheduledPlacement> placements;
    private List<ScheduledCreative> creatives;
    private Date createdDate;
    private Date modifiedDate;

    public Schedule() {
    }

    public Long getCreativeGroupId() {
        return creativeGroupId;
    }

    public void setCreativeGroupId(Long creativeGroupId) {
        this.creativeGroupId = creativeGroupId;
    }

    @XmlElementWrapper(name = "placements")
    @XmlElement(name = "ScheduledPlacement")
    public List<ScheduledPlacement> getPlacements() {
        return placements;
    }

    public void setPlacements(List<ScheduledPlacement> placements) {
        this.placements = placements;
    }

    @XmlElementWrapper(name = "creatives")
    @XmlElement(name = "ScheduledCreative")
    public List<ScheduledCreative> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<ScheduledCreative> creatives) {
        this.creatives = creatives;
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
        return "Schedule{" + "creativeGroupId=" + creativeGroupId
                + ", placements=" + placements + ", creatives="
                + creatives + ", createdDate=" + createdDate
                + ", modifiedDate=" + modifiedDate + '}';
    }
}

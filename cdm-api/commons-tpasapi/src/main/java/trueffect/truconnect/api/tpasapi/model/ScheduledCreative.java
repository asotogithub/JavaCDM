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
@XmlRootElement(name = "ScheduledCreative")
@XmlType(propOrder = {"creativeId", "weight", "sequence", "startDate",
    "endDate", "clickthroughs"})
@XmlSeeAlso({Clickthrough.class})
public class ScheduledCreative implements Serializable {

    private Long creativeId;
    private Long weight;
    private Long sequence;
    private Date startDate;
    private Date endDate;
    private List<Clickthrough> clickthroughs;

    public ScheduledCreative() {
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
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

    @XmlElementWrapper(name = "clickthroughs")
    @XmlElement(name = "Clickthrough")
    public List<Clickthrough> getClickthroughs() {
        return clickthroughs;
    }

    public void setClickthroughs(List<Clickthrough> clickthroughs) {
        this.clickthroughs = clickthroughs;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "ScheduledCreative{" + "creativeId=" + creativeId
                + ", weight=" + weight + ", sequence=" + sequence + ", startDate=" + startDate
                + ", endDate=" + endDate + ", clickthroughs="
                + clickthroughs + '}';
    }
}

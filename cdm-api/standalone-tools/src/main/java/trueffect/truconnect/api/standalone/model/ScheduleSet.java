package trueffect.truconnect.api.standalone.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "ScheduleSet")
@XmlSeeAlso({Schedule.class})
public class ScheduleSet {

    private List<Schedule> schedules;

    public ScheduleSet() {
    }

    @XmlElementWrapper(name = "schedules")
    @XmlAnyElement(lax = true)
    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    @Override
    public String toString() {
        return "ScheduleSet [schedules=" + schedules + "]";
    }
    
}

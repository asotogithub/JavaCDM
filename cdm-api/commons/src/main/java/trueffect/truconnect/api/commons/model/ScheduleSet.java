package trueffect.truconnect.api.commons.model;

import java.util.List;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@SuppressWarnings("serial")
@XmlRootElement
@XmlSeeAlso({Schedule.class, ScheduleEntry.class, Clickthrough.class})
public class ScheduleSet implements Serializable {

    private List<Schedule> schedules;

    public ScheduleSet() {
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    @Override
    public String toString() {
        return "ScheduleSet{" + "schedules=" + schedules + '}';
    }
    
}

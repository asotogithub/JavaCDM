package trueffect.truconnect.api.commons.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jesus.nunez on 6/8/2016.
 */
@XmlRootElement(name = "CreativeAssociationsDTO")
public class CreativeAssociationsDTO {

    private Long groupId;
    private Long schedules;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getSchedules() {
        return schedules;
    }

    public void setSchedules(Long schedules) {
        this.schedules = schedules;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

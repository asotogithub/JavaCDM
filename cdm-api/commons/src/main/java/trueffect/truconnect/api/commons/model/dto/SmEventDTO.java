package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.SmGroup;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Site Measurement Event DTO
 * Created by richard.jaldin on 6/8/2015.
 */
@XmlRootElement(name = "SmEventDTO")
public class SmEventDTO extends SmEvent {

    private String groupName;
    private SmGroup smGroup;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public SmGroup getSmGroup() {
        return smGroup;
    }

    public void setSmGroup(SmGroup smGroup) {
        this.smGroup = smGroup;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package trueffect.truconnect.api.tpasapi.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "ScheduledPlacement")
public class ScheduledPlacement implements Serializable {

    private Long placementId;

    public ScheduledPlacement() {
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    @Override
    public String toString() {
        return "ScheduledPlacement{" + "placementId=" + placementId + '}';
    }
}

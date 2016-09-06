package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "ScheduledPlacement")
public class ScheduledPlacement {

    @TableFieldMapping(table = "PLACEMENT", field = "PLACEMENT_ID")
    private Long placementId;

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

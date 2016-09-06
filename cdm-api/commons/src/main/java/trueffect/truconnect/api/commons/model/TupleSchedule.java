package trueffect.truconnect.api.commons.model;

/**
 *
 * @author Gustavo Claure
 */
public class TupleSchedule {
    private Long creativeId;
    private Long placementId;

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    @Override
    public String toString() {
        return "TupleSchedule{" + "creativeId=" + creativeId + ", placementId=" + placementId + '}';
    }
    
}

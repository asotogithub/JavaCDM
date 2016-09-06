package trueffect.truconnect.api.commons.model.dim;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DimPlacementCost")
public class DimPlacementCost extends DimCostRateType {

    private Long placementCostId;
    private Long placementId;

    public Long getPlacementCostId() {
        return placementCostId;
    }

    public void setPlacementCostId(Long placementCostId) {
        this.placementCostId = placementCostId;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

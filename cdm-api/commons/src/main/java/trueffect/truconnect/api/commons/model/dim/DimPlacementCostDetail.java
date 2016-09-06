package trueffect.truconnect.api.commons.model.dim;

import trueffect.truconnect.api.commons.model.CostDetail;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DimPlacementCostDetail")
public class DimPlacementCostDetail extends CostDetail {

    private Long placementCostId;
    private Long placementId;
    private Long dimPlacementCostDetailRateType;

    public DimPlacementCostDetail() {
    }

    public DimPlacementCostDetail(CostDetail costDetail) {
        super(costDetail);
    }

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

    public Long getDimPlacementCostDetailRateType() {
        return dimPlacementCostDetailRateType;
    }

    public void setDimPlacementCostDetailRateType(Long dimPlacementCostDetailRateType) {
        this.dimPlacementCostDetailRateType = dimPlacementCostDetailRateType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

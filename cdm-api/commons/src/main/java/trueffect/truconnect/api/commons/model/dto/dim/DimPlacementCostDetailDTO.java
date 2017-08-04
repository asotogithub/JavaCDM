package trueffect.truconnect.api.commons.model.dto.dim;

import trueffect.truconnect.api.commons.model.dim.DimPlacementCost;
import trueffect.truconnect.api.commons.model.dim.DimPlacementCostDetail;
import trueffect.truconnect.api.commons.model.dim.DimProductBuyCost;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DimPlacementCostDetailDTO")
public class DimPlacementCostDetailDTO {

    private List<DimPlacementCost> dimPlacementCosts;
    private List<DimPlacementCostDetail> dimPlacementCostDetails;
    private List<DimProductBuyCost> dimProductBuyCosts;

    public List<DimPlacementCost> getDimPlacementCosts() {
        return dimPlacementCosts;
    }

    public void setDimPlacementCosts(List<DimPlacementCost> dimPlacementCosts) {
        this.dimPlacementCosts = dimPlacementCosts;
    }

    public List<DimPlacementCostDetail> getDimPlacementCostDetails() {
        return dimPlacementCostDetails;
    }

    public void setDimPlacementCostDetails(List<DimPlacementCostDetail> dimPlacementCostDetails) {
        this.dimPlacementCostDetails = dimPlacementCostDetails;
    }

    public List<DimProductBuyCost> getDimProductBuyCosts() {
        return dimProductBuyCosts;
    }

    public void setDimProductBuyCosts(List<DimProductBuyCost> dimProductBuyCosts) {
        this.dimProductBuyCosts = dimProductBuyCosts;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

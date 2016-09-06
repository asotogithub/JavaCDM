package trueffect.truconnect.api.commons.model.dim;

import trueffect.truconnect.api.commons.model.CostDetail;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DimProductBuyCost")
public class DimProductBuyCost extends DimCostRateType {

    private Long productBuyCostId;
    private Long productBuyId;

    public Long getProductBuyCostId() {
        return productBuyCostId;
    }

    public void setProductBuyCostId(Long productBuyCostId) {
        this.productBuyCostId = productBuyCostId;
    }

    public Long getProductBuyId() {
        return productBuyId;
    }

    public void setProductBuyId(Long productBuyId) {
        this.productBuyId = productBuyId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

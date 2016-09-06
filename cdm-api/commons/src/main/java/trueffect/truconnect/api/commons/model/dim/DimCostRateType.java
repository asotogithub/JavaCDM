package trueffect.truconnect.api.commons.model.dim;

import trueffect.truconnect.api.commons.model.CostDetail;

/**
 *
 * @author Gustavo Claure
 */
public class DimCostRateType extends CostDetail {

    private String costRateType;
    private Double rate;
    private String costType;

    public String getCostRateType() {
        return costRateType;
    }

    public void setCostRateType(String costRateType) {
        this.costRateType = costRateType;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }
    
}

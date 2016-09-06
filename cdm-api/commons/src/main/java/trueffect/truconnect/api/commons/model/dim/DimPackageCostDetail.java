package trueffect.truconnect.api.commons.model.dim;

import trueffect.truconnect.api.commons.model.CostDetail;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "DimPackageCostDetail")
public class DimPackageCostDetail extends CostDetail {

    private Long packageCostId;
    private Long packageId;

    public DimPackageCostDetail() {
    }

    public DimPackageCostDetail(CostDetail costDetail) {
        super(costDetail);
    }

    public Long getPackageCostId() {
        return packageCostId;
    }

    public void setPackageCostId(Long packageCostId) {
        this.packageCostId = packageCostId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

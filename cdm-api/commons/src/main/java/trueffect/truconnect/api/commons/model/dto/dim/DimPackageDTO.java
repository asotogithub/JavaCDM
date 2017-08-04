package trueffect.truconnect.api.commons.model.dto.dim;

import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.commons.model.dim.DimPackageCostDetail;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "DimPackageDTO")
public class DimPackageDTO extends Package {

    private List<PackagePlacement> dimPackagePlacements;
    private List<DimPackageCostDetail> dimPackageCostDetail;

    public List<PackagePlacement> getDimPackagePlacements() {
        return dimPackagePlacements;
    }

    public void setDimPackagePlacements(List<PackagePlacement> dimPackagePlacements) {
        this.dimPackagePlacements = dimPackagePlacements;
    }

    public List<DimPackageCostDetail> getDimPackageCostDetail() {
        return dimPackageCostDetail;
    }

    public void setDimPackageCostDetail(List<DimPackageCostDetail> dimPackageCostDetail) {
        this.dimPackageCostDetail = dimPackageCostDetail;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

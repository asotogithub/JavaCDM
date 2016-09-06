package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.Placement;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 10/5/2015.
 */
@XmlRootElement(name = "BulkPackagePlacement")
public class BulkPackagePlacement {
    private List<Package> packages;
    private List<Placement> placements;

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public List<Placement> getPlacements() {
        return placements;
    }

    public void setPlacements(List<Placement> placements) {
        this.placements = placements;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

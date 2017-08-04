package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "geoTarget")
public class GeoTarget {
    private String typeCode;
    private Long antiTarget;
    private List<GeoLocation> targets;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Long getAntiTarget() {
        return antiTarget;
    }

    public void setAntiTarget(Long antiTarget) {
        this.antiTarget = antiTarget;
    }

    @XmlElementWrapper(name = "values")
    @XmlElement(name = "value")
    public List<GeoLocation> getTargets() {
        return targets;
    }

    public void setTargets(List<GeoLocation> targets) {
        this.targets = targets;
    }

    public void addTarget(GeoLocation geoLocation) {
        if(targets == null) {
            targets = new ArrayList<>(1);
        }

        if(geoLocation != null) {
            targets.add(geoLocation);
        }
    }

    public List<CreativeGroupTarget> getCreativeGroupTargets() {
        List<CreativeGroupTarget> creativeGroupTargets;
        if(targets == null) {
            creativeGroupTargets = new ArrayList<>(0);
        } else {
            creativeGroupTargets = new ArrayList<>(targets.size());
            for (GeoLocation location : targets) {
                CreativeGroupTarget target = new CreativeGroupTarget();
                target.setAntiTarget(antiTarget);
                target.setTypeCode(typeCode);
                target.setValueId(location.getId());
                target.setTargetLabel(location.getLabel());
                creativeGroupTargets.add(target);
            }
        }
        return creativeGroupTargets;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

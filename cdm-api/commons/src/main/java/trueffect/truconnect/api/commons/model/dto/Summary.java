package trueffect.truconnect.api.commons.model.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mnelson on 12/10/15.
 */

@XmlRootElement(name = "Summary")
public class Summary {

    private Long engagements;
    private Long cached;
    private Long matched;
    private Long updated;
    private Double matchRate;
    
    public Summary() {}

    public Summary(Long engagements, Long cached, Long matched, Long updated, Double matchRate) {
        this.engagements = engagements;
        this.cached = cached;
        this.matched = matched;
        this.updated = updated;
        this.matchRate = matchRate;
    }

    public Long getEngagements() {
        return engagements;
    }

    public Long getCached() {
        return cached;
    }

    public Long getMatched() {
        return matched;
    }

    public Long getUpdated() {
        return updated;
    }

    public Double getMatchRate() {
        return matchRate;
    }

    public void setEngagements(Long engagements) {
        this.engagements = engagements;
    }

    public void setCached(Long cached) {
        this.cached = cached;
    }

    public void setMatched(Long matched) {
        this.matched = matched;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public void setMatchRate(Double matchRate) {
        this.matchRate = matchRate;
    }
}

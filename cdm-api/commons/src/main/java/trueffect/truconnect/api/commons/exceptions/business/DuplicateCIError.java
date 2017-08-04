package trueffect.truconnect.api.commons.exceptions.business;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 1/11/2016.
 */
@XmlRootElement(name = "DuplicateCIError")
public class DuplicateCIError extends Error {
    private Long siteId;
    private Long sectionId;
    private Long placementId;
    private Long groupId;
    private Long creativeId;

    public DuplicateCIError() {    }

    public DuplicateCIError(String message, ErrorCode code, Long siteId, Long sectionId, Long placementId, Long groupId, Long creativeId) {
        super(message, code);
        this.siteId = siteId;
        this.sectionId = sectionId;
        this.placementId = placementId;
        this.groupId = groupId;
        this.creativeId = creativeId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

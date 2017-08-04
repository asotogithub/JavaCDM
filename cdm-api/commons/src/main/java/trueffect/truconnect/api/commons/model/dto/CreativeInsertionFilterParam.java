package trueffect.truconnect.api.commons.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 12/31/2015.
 */
@XmlRootElement(name = "CreativeInsertionFilterParam")
public class CreativeInsertionFilterParam {

    @QueryParam("siteId")
    private Long siteId;

    @QueryParam("sectionId")
    private Long sectionId;

    @QueryParam("placementId")
    private Long placementId;

    @QueryParam("groupId")
    private Long groupId;

    @QueryParam("creativeId")
    private Long creativeId;

    @QueryParam("type")
    private String type;
    
    @QueryParam("pivotType")
    private String pivotType;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getPivotType() {
        return pivotType;
    }

    public void setPivotType(String pivotType) {
        this.pivotType = pivotType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

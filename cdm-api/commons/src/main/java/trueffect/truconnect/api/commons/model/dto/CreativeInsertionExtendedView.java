package trueffect.truconnect.api.commons.model.dto;

import java.util.List;

/**
 * Extended view of {@code CreativeInsertionView}
 * @author marcelo.heredia on 2/26/2016.
 */
public class CreativeInsertionExtendedView extends CreativeInsertionView {

    private String creativeClickthrough;
    private List<String> creativeClickthroughs;
    private Long placementCampaignId;
    private Long creativeCampaignId;
    private Long groupCampaignId;
    
    public CreativeInsertionExtendedView() {
        super();
    }

    public String getCreativeClickthrough() {
        return creativeClickthrough;
    }

    public void setCreativeClickthrough(String creativeClickthrough) {
        this.creativeClickthrough = creativeClickthrough;
    }

    public List<String> getCreativeClickthroughs() {
        return creativeClickthroughs;
    }

    public void setCreativeClickthroughs(List<String> creativeClickthroughs) {
        this.creativeClickthroughs = creativeClickthroughs;
    }

    public Long getPlacementCampaignId() {
        return placementCampaignId;
    }

    public void setPlacementCampaignId(Long placementCampaignId) {
        this.placementCampaignId = placementCampaignId;
    }

    public Long getCreativeCampaignId() {
        return creativeCampaignId;
    }

    public void setCreativeCampaignId(Long creativeCampaignId) {
        this.creativeCampaignId = creativeCampaignId;
    }

    public Long getGroupCampaignId() {
        return groupCampaignId;
    }

    public void setGroupCampaignId(Long groupCampaignId) {
        this.groupCampaignId = groupCampaignId;
    }


}

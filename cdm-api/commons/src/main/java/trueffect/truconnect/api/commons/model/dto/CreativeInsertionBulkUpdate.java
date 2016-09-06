package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeInsertion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for doing bulk updates from the Schedule tab of the UI.
 */
@XmlRootElement(name = "CreativeInsertionBulkUpdate")
public class CreativeInsertionBulkUpdate {

    private List<CreativeInsertion> creativeInsertions;
    private List<CreativeGroup> creativeGroups;

    public List<Long> getCreativeInsertionIds() {
        if (creativeInsertions == null) {
            return new ArrayList<>(0);
        }

        List<Long> ciIds = new ArrayList<>(creativeInsertions.size());
        for (CreativeInsertion ci : creativeInsertions) {
            if (ci.getId() != null) {
                ciIds.add(ci.getId());
            }
        }
        return ciIds;
    }

    public List<Long> getCreativeGroupIds() {
        if (creativeGroups == null) {
            return new ArrayList<>(0);
        }

        List<Long> cgIds = new ArrayList<>(creativeGroups.size());
        for (CreativeGroup cg : creativeGroups) {
            if (cg.getId() != null) {
                cgIds.add(cg.getId());
            }
        }
        return cgIds;
    }

    public List<CreativeInsertion> getCreativeInsertions() {
        return creativeInsertions;
    }

    public void setCreativeInsertions(List<CreativeInsertion> creativeInsertions) {
        this.creativeInsertions = creativeInsertions;
    }

    public List<CreativeGroup> getCreativeGroups() {
        return creativeGroups;
    }

    public void setCreativeGroups(List<CreativeGroup> creativeGroups) {
        this.creativeGroups = creativeGroups;
    }
}

package trueffect.truconnect.api.commons.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author rodrigo.alarcon
 */
public class TruQTagMessage {
    
    @JsonProperty("CampaignId")
    private int campaignId;
    
    @JsonProperty("AgencyId")
    private int agencyId;
    
    @JsonProperty("SessionKey")
    private String sessionKey;
    
    @JsonProperty("ChangedTagIds")
    private String changedTagIds;

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int camapaignId) {
        this.campaignId = camapaignId;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getChangedTagIds() {
        return changedTagIds;
    }

    public void setChangedTagIds(String changedTagIds) {
        this.changedTagIds = changedTagIds;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

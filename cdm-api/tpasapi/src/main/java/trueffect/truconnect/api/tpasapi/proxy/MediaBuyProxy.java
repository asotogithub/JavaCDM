package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.MediaBuyCampaign;

/**
 *
 * @author Richard Jaldin
 */
public class MediaBuyProxy extends BaseProxy<trueffect.truconnect.api.commons.model.MediaBuy> {

    public MediaBuyProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.MediaBuy.class, headers);
        path("MediaBuys");
    }

    public trueffect.truconnect.api.commons.model.MediaBuy getByCampaign(Long campaignId) throws ProxyException {
        path("byCampaign");
        path(campaignId.toString());
        trueffect.truconnect.api.commons.model.MediaBuy media = this.get();
        return media;
    }

    public trueffect.truconnect.api.commons.model.MediaBuy createDefault(Long campaignId) throws ProxyException {
        trueffect.truconnect.api.commons.model.MediaBuy mediaBuy = new trueffect.truconnect.api.commons.model.MediaBuy();

        log.info("Looking for Campaign: " + campaignId);
        // We need the agencyId that the placement belongs to
        CampaignProxy campaignProxy = new CampaignProxy(headers);
        campaignProxy.path(Long.toString(campaignId));
        Campaign campaign = campaignProxy.get();
        log.info("Recovered Campaign: " + campaign);

        // Setting default values
        mediaBuy.setAgencyId(campaign.getAgencyId());
        mediaBuy.setName("Default");
        mediaBuy.setOverallBudget(0L);
        mediaBuy.setState("New");
        log.info("Saving the default MediaBuy: " + mediaBuy);
        mediaBuy = this.post(mediaBuy);

        // Creating the relationship between MediaBuy and Campaign
        log.info("Saving the MediaBuyCampaign relationship: " + mediaBuy + ", " + campaign);
        MediaBuyCampaign mediaBuyCampaign = new MediaBuyCampaign();
        mediaBuyCampaign.setMediaBuyId(mediaBuy.getId());
        mediaBuyCampaign.setCampaignId(campaignId);
        MediaBuyCampaignProxy mediaBuyCampaignProxy = new MediaBuyCampaignProxy(headers);
        mediaBuyCampaignProxy.path("campaign");
        mediaBuyCampaignProxy.post(mediaBuyCampaign);

        return mediaBuy;
    }
}

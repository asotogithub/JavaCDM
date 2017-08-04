package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.CookieDomain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CampaignDTO")
public class CampaignDTO extends Campaign {
    private String advertiserName;
    private String brandName;
    private String domain;

    public CampaignDTO() {}

    public CampaignDTO(Campaign campaign) {
        this.setAdvertiserId(campaign.getAdvertiserId());
        this.setAgencyId(campaign.getAgencyId());
        this.setAgencyNotes(campaign.getAgencyNotes());
        this.setBrandId(campaign.getBrandId());
        this.setContactId(campaign.getContactId());
        this.setCookieDomainId(campaign.getCookieDomainId());
        this.setCreatedDate(campaign.getCreatedDate());
        this.setCreatedTpwsKey(campaign.getCreatedTpwsKey());
        this.setDescription(campaign.getDescription());
        this.setDupName(campaign.getDupName());
        this.setEndDate(campaign.getEndDate());
        this.setId(campaign.getId());
        this.setIsActive(campaign.getIsActive());
        this.setIsHidden(campaign.getIsHidden());
        this.setLogicalDelete(campaign.getLogicalDelete());
        this.setMediaBuyId(campaign.getMediaBuyId());
        this.setModifiedDate(campaign.getModifiedDate());
        this.setModifiedTpwsKey(campaign.getModifiedTpwsKey());
        this.setName(campaign.getName());
        this.setObjective(campaign.getObjective());
        this.setResourcePathId(campaign.getResourcePathId());
        this.setStartDate(campaign.getStartDate());
        this.setStatusId(campaign.getStatusId());
        this.setTrafficToOwner(campaign.getTrafficToOwner());
        this.setOverallBudget(campaign.getOverallBudget());
        this.setUserId(campaign.getUserId());
    }

    public CampaignDTO(Campaign campaign, Advertiser advertiser, Brand brand, CookieDomain domain) {
        this(campaign);
        this.advertiserName = advertiser.getName();
        this.brandName = brand.getName();
        this.domain = domain != null ? domain.getDomain() : null;
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}

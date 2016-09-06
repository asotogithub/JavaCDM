package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Brand;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for combining all of the information needed for the details view into one object.
 */
@XmlRootElement(name = "CampaignDetails")
public class CampaignDetailsDTO implements Serializable {
    private CampaignDTO campaign;
    private Brand brand;
    private Advertiser advertiser;

    public CampaignDetailsDTO() {}

    public CampaignDetailsDTO(CampaignDTO campaign, Brand brand, Advertiser advertiser) {
        this.campaign = campaign;
        this.brand = brand;
        this.advertiser = advertiser;
    }

    public CampaignDTO getCampaign() {
        return campaign;
    }

    public void setCampaign(CampaignDTO campaign) {
        this.campaign = campaign;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Advertiser getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(Advertiser advertiser) {
        this.advertiser = advertiser;
    }
}

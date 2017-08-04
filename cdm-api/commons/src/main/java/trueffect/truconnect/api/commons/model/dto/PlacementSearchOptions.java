package trueffect.truconnect.api.commons.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author marleny.patsi
 */
@XmlRootElement(name = "PlacementSearchOptions")
public class PlacementSearchOptions {
    
    @QueryParam("soCampaign")
    private boolean campaign;

    @QueryParam("soSite")
    private boolean site;

    @QueryParam("soSection")
    private boolean section;

    @QueryParam("soPlacement")
    private boolean placement;

    public boolean isCampaign() {
        return campaign;
    }

    public void setCampaign(boolean campaign) {
        this.campaign = campaign;
    }

    public boolean isSite() {
        return site;
    }

    public void setSite(boolean site) {
        this.site = site;
    }

    public boolean isSection() {
        return section;
    }

    public void setSection(boolean section) {
        this.section = section;
    }

    public boolean isPlacement() {
        return placement;
    }

    public void setPlacement(boolean placement) {
        this.placement = placement;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    } 
}

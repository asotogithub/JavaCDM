package trueffect.truconnect.api.commons.model.dto;

import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 2/16/2016.
 */
@XmlRootElement(name = "CreativeInsertionSearchOptions")
public class CreativeInsertionSearchOptions {

    @QueryParam("soSite")
    private boolean site;

    @QueryParam("soSection")
    private boolean section;

    @QueryParam("soPlacement")
    private boolean placement;

    @QueryParam("soGroup")
    private boolean group;

    @QueryParam("soCreative")
    private boolean creative;

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

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isCreative() {
        return creative;
    }

    public void setCreative(boolean creative) {
        this.creative = creative;
    }
}

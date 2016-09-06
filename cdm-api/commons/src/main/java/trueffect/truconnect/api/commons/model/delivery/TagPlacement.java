package trueffect.truconnect.api.commons.model.delivery;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rodrigo.alarcon
 */
@XmlRootElement(name = "TagPlacement")
public class TagPlacement {
    
    private String ioNumber;
    private String ioDescription;
    private String campaignName;
    private Date startDate;
    private Date endDate;
    private Integer impressions;
    private String fullAdTag;
    private String scriptVersion;
    private String noScriptVersion;
    private String clickRedirect;

    public String getFullAdTag() {
        return fullAdTag;
    }

    public void setFullAdTag(String fullAdTag) {
        this.fullAdTag = fullAdTag;
    }

    public String getScriptVersion() {
        return scriptVersion;
    }

    public void setScriptVersion(String scriptVersion) {
        this.scriptVersion = scriptVersion;
    }

    public String getNoScriptVersion() {
        return noScriptVersion;
    }

    public void setNoScriptVersion(String noScriptVersion) {
        this.noScriptVersion = noScriptVersion;
    }

    public String getClickRedirect() {
        return clickRedirect;
    }

    public void setClickRedirect(String clickRedirect) {
        this.clickRedirect = clickRedirect;
    }

    public String getIoNumber() {
        return ioNumber;
    }

    public void setIoNumber(String ioNumber) {
        this.ioNumber = ioNumber;
    }

    public String getIoDescription() {
        return ioDescription;
    }

    public void setIoDescription(String ioDescription) {
        this.ioDescription = ioDescription;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getImpressions() {
        return impressions;
    }

    public void setImpressions(Integer impressions) {
        this.impressions = impressions;
    }
    
    
}

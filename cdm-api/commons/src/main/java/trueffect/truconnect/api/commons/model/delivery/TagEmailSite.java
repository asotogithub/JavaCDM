package trueffect.truconnect.api.commons.model.delivery;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rodrigo.alarcon
 */
@XmlRootElement(name = "TagEmailSite")
public class TagEmailSite {
    
    private String fileType;
    private Integer siteId;
    private List<Integer> placementIds;
    private List<String> recipients;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public List<Integer> getPlacementIds() {
        return placementIds;
    }

    public void setPlacementIds(List<Integer> placementIds) {
        this.placementIds = placementIds;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
    
    
}

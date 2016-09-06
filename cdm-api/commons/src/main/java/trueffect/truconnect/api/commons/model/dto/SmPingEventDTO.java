package trueffect.truconnect.api.commons.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jesus.nunez on 7/26/2016.
 */

@XmlRootElement(name = "SmPingEventDTO")
public class SmPingEventDTO extends SmEventDTO {

    private Long pingId;
    private String pingContent;
    private String description;
    private Long siteId;
    private String siteName;
    private Long pingType;
    private Long pingTagType;


    public Long getPingId() {
        return pingId;
    }

    public void setPingId(Long pingId) {
        this.pingId = pingId;
    }

    public Long getPingTagType() {
        return pingTagType;
    }

    public void setPingTagType(Long pingTagType) {
        this.pingTagType = pingTagType;
    }

    public Long getPingType() {
        return pingType;
    }

    public void setPingType(Long pingType) {
        this.pingType = pingType;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPingContent() {
        return pingContent;
    }

    public void setPingContent(String pingContent) {
        this.pingContent = pingContent;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

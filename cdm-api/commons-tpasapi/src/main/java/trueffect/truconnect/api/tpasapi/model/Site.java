package trueffect.truconnect.api.tpasapi.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name="Site")
@XmlType(propOrder = {"id", "publisherId", "name", "url", "externalId",
    "preferredTag", "richMedia", "acceptsFlash", "clickTrack", 
    "encode", "targetWin", "agencyNotes", "publisherNotes", 
    "createdDate", "modifiedDate"})
public class Site implements Serializable {

    private Long id;
    private Long publisherId;
    private String name;
    private String url;
    private String preferredTag;
    private String externalId;
    private Boolean richMedia;
    private Boolean acceptsFlash;
    private Boolean clickTrack;
    private Boolean encode;
    private String targetWin;
    private String agencyNotes;
    private String publisherNotes;
    private Date createdDate;
    private Date modifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPreferredTag() {
        return preferredTag;
    }

    public void setPreferredTag(String preferredTag) {
        this.preferredTag = preferredTag;
    }

    public Boolean getRichMedia() {
        return richMedia;
    }

    public void setRichMedia(Boolean richMedia) {
        this.richMedia = richMedia;
    }

    public Boolean getAcceptsFlash() {
        return acceptsFlash;
    }

    public void setAcceptsFlash(Boolean acceptsFlash) {
        this.acceptsFlash = acceptsFlash;
    }

    public Boolean getClickTrack() {
        return clickTrack;
    }

    public void setClickTrack(Boolean clickTrack) {
        this.clickTrack = clickTrack;
    }

    public Boolean getEncode() {
        return encode;
    }

    public void setEncode(Boolean encode) {
        this.encode = encode;
    }

    public String getTargetWin() {
        return targetWin;
    }

    public void setTargetWin(String targetWin) {
        this.targetWin = targetWin;
    }

    public String getAgencyNotes() {
        return agencyNotes;
    }

    public void setAgencyNotes(String agencyNotes) {
        this.agencyNotes = agencyNotes;
    }

    public String getPublisherNotes() {
        return publisherNotes;
    }

    public void setPublisherNotes(String publisherNotes) {
        this.publisherNotes = publisherNotes;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    @Override
    public String toString() {
        return "Site{" + "id=" + id + ", publisherId=" + publisherId
                + ", name=" + name + ", url=" + url + "externalId=" + externalId + ", preferredTag="
                + preferredTag + ", richMedia=" + richMedia + ", acceptsFlash="
                + acceptsFlash + ", clickTrack=" + clickTrack + ", encode="
                + encode + ", targetWin=" + targetWin + ", agencyNotes="
                + agencyNotes + ", publisherNotes=" + publisherNotes
                + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + '}';
    }
}

package trueffect.truconnect.api.standalone.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "Site")
@XmlType(propOrder = {"id", "publisherId", "name", "url", "preferredTag", 
        "richMedia", "acceptsFlash", "clickTrack", "encode", "targetWin", 
        "agencyNotes", "publisherNotes", "logicalDelete", "createdTpwsKey", 
        "modifiedTpwsKey", "createdDate", "modifiedDate", "externalId", 
        "sections"})
@XmlSeeAlso({SiteSection.class})
public class Site {

    private Long id;
    private Long publisherId;
    private String name;
    private String url;
    private String preferredTag;
    private String richMedia;
    private String acceptsFlash;
    private String clickTrack;
    private String encode;
    private String targetWin;
    private String agencyNotes;
    private String publisherNotes;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;
    private String externalId;
    private List<SiteSection> sections;

    public Site() {
    }

    public Site(Long id, String name, String url, String preferredTag,
            String richMedia, String acceptsFlash, String clickTrack, String encode, String targetWin,
            String agencyNotes, String publisherNotes, String logicalDelete, String createdTpwsKey,
            String modifiedTpwsKey, Date created, Date modified) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.preferredTag = preferredTag;
        this.richMedia = richMedia;
        this.acceptsFlash = acceptsFlash;
        this.clickTrack = clickTrack;
        this.encode = encode;
        this.targetWin = targetWin;
        this.agencyNotes = agencyNotes;
        this.publisherNotes = publisherNotes;
        this.logicalDelete = logicalDelete;
        this.createdTpwsKey = createdTpwsKey;
        this.modifiedTpwsKey = modifiedTpwsKey;
        this.createdDate = created;
        this.modifiedDate = modified;
    }

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

    public String getRichMedia() {
        return richMedia;
    }

    public void setRichMedia(String richMedia) {
        this.richMedia = richMedia;
    }

    public String getAcceptsFlash() {
        return acceptsFlash;
    }

    public void setAcceptsFlash(String acceptsFlash) {
        this.acceptsFlash = acceptsFlash;
    }

    public String getClickTrack() {
        return clickTrack;
    }

    public void setClickTrack(String clickTrack) {
        this.clickTrack = clickTrack;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
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

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    public String getCreatedTpwsKey() {
        return createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

    public String getModifiedTpwsKey() {
        return modifiedTpwsKey;
    }

    public void setModifiedTpwsKey(String modifiedTpwsKey) {
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
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

    @XmlElementWrapper(name = "sections")
    @XmlAnyElement(lax = true)
    public List<SiteSection> getSections() {
        return sections;
    }

    public void setSections(List<SiteSection> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "Site [id=" + id + ", publisherId=" + publisherId + ", name="
                + name + ", url=" + url + ", preferredTag=" + preferredTag
                + ", richMedia=" + richMedia + ", acceptsFlash=" + acceptsFlash
                + ", clickTrack=" + clickTrack + ", encode=" + encode
                + ", targetWin=" + targetWin + ", agencyNotes=" + agencyNotes
                + ", publisherNotes=" + publisherNotes + ", logicalDelete="
                + logicalDelete + ", createdTpwsKey=" + createdTpwsKey
                + ", modifiedTpwsKey=" + modifiedTpwsKey + ", createdDate="
                + createdDate + ", modifiedDate=" + modifiedDate
                + ", externalId=" + externalId + ", sections=" + sections + "]";
    }
}

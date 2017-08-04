package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "Site")
public class Site {

    @TableFieldMapping(table = "SITE", field = "SITE_ID")
    private Long id;
    @TableFieldMapping(table = "SITE", field = "PUBLISHER_ID")
    private Long publisherId;
    @TableFieldMapping(table = "SITE", field = "SITE_NAME")
    private String name;
    @TableFieldMapping(table = "SITE", field = "DUP_SITE_NAME")
    private String dupName;
    @TableFieldMapping(table = "SITE", field = "SITE_URL")
    private String url;
    @TableFieldMapping(table = "SITE", field = "PREFERRED_TAG")
    private String preferredTag;
    @TableFieldMapping(table = "SITE", field = "RICH_MEDIA", valueMappings= {
    		@FieldValueMapping(input="true", output="\"Y\""),
    		@FieldValueMapping(input="false", output="\"N\"")
    })
    private String richMedia;
    @TableFieldMapping(table = "SITE", field = "ACCEPT_FLASH", valueMappings= {
    		@FieldValueMapping(input="true", output="\"Y\""),
    		@FieldValueMapping(input="false", output="\"N\"")
    })
    private String acceptsFlash;
    @TableFieldMapping(table = "SITE", field = "CLICK_TRACK", valueMappings= {
    		@FieldValueMapping(input="true", output="\"Y\""),
    		@FieldValueMapping(input="false", output="\"N\"")
    })
    private String clickTrack;
    @TableFieldMapping(table = "SITE", field = "ENCODE", valueMappings= {
    		@FieldValueMapping(input="true", output="\"Y\""),
    		@FieldValueMapping(input="false", output="\"N\"")
    })
    private String encode;
    @TableFieldMapping(table = "SITE", field = "TARGET_WIN")
    private String targetWin;
    @TableFieldMapping(table = "SITE", field = "AGENCY_NOTES")
    private String agencyNotes;
    @TableFieldMapping(table = "SITE", field = "PUBLISHER_NOTES")
    private String publisherNotes;
    @TableFieldMapping(table = "SITE", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "SITE", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "SITE", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "SITE", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "SITE", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "EXT", field = "EXTERNALID")
    private String externalId;
    
    private String publisherName;
    
    public Site() {
    }

    public Site(Long id, Long publisherId, String name, String dupName, String url, String preferredTag,
            String richMedia, String acceptsFlash, String clickTrack, String encode, String targetWin,
            String agencyNotes, String publisherNotes, String logicalDelete, String createdTpwsKey,
            String modifiedTpwsKey, Date created, Date modified) {
        this.id = id;
        this.publisherId = publisherId;
        this.name = name;
        this.dupName = dupName;
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

    public String getDupName() {
        return dupName;
    }

    public void setDupName(String dupName) {
        this.dupName = dupName;
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

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

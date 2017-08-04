package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "MediaBuy")
public class MediaBuy {

    @TableFieldMapping(table = "MEDIA_BUY", field = "MEDIA_BUY_ID")
    private Long id;
    @TableFieldMapping(table = "MEDIA_BUY", field = "AGENCY_ID")
    private Long agencyId;
    @TableFieldMapping(table = "MEDIA_BUY", field = "BUY_NAME")
    private String name;
    @TableFieldMapping(table = "MEDIA_BUY", field = "STATE")
    private String state;
    @TableFieldMapping(table = "MEDIA_BUY", field = "OVERALL_BUDGET")
    private Long overallBudget;
    @TableFieldMapping(table = "MEDIA_BUY", field = "AGENCY_NOTES")
    private String agencyNotes;
    @TableFieldMapping(table = "MEDIA_BUY", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "MEDIA_BUY", field = "MODIFIED")
    private Date modifiedDate;
    private String createdTpwsKey;
    private String modifiedTpwsKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getOverallBudget() {
        return overallBudget;
    }

    public void setOverallBudget(Long overallBudget) {
        this.overallBudget = overallBudget;
    }

    public String getAgencyNotes() {
        return agencyNotes;
    }

    public void setAgencyNotes(String agencyNotes) {
        this.agencyNotes = agencyNotes;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

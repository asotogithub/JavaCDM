package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Jeff Frylings
 */
@XmlRootElement(name = "CreativeVersion")
public class CreativeVersion {

    private Long creativeId;
    private Long versionNumber;
    private String alias;
    private Date startDate;
    private Long isDateSet;
    private Long campaignId;

    public CreativeVersion(){
    }

    public CreativeVersion(Long creativeId, Long versionNumber, String alias, Date startDate, Long isDateSet, Long campaignId) {
        this.creativeId = creativeId;
        this.versionNumber = versionNumber;
        this.alias = alias;
        this.startDate = startDate;
        this.isDateSet = isDateSet;
        this.campaignId = campaignId;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Long getIsDateSet() {
        return isDateSet;
    }

    public void setIsDateSet(Long isDateSet) {
        this.isDateSet = isDateSet;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CreativeVersion that = (CreativeVersion) o;

        return new EqualsBuilder()
                .append(versionNumber, that.versionNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(versionNumber)
                .toHashCode();
    }
}

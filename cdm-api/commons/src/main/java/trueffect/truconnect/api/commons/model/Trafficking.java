package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Trafficking DTO.
 * <pre>
 * Change log:
 * 03/10/2016 MH Marking "domain" as @Deprecated as this attribute does nothing for the business logic. It is going to
 *            remain here to allow tpasapi FitNesse tests to remain green.
 * </pre>
 * @author Abel Soto
 * @author Ramber Rioja
 * @author Richard Jaldin
 * @author Marcelo Heredia
 */
@XmlRootElement(name = "Trafficking")
public class Trafficking {

    private Long campaignId;
    private Long timeZoneOffset;
    @Deprecated
    private String domain;
    private Long cookieDomainId;
    private List<Integer> agencyContacts;
    private List<Integer> siteContacts;
    private Integer currentContactId;

    public Trafficking() {
    }

    public Trafficking(Long campaignId, Long cookieDomainId, Long timeZoneOffset, List<Integer> agencyContacts, List<Integer> siteContacts) {
        this.campaignId = campaignId;
        this.timeZoneOffset = timeZoneOffset;
        this.agencyContacts = agencyContacts;
        this.siteContacts = siteContacts;
        this.cookieDomainId = cookieDomainId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Long timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }
    @Deprecated
    public String getDomain() {
        return domain;
    }
    @Deprecated
    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<Integer> getAgencyContacts() {
        return agencyContacts;
    }

    public void setAgencyContacts(List<Integer> agencyContacts) {
        this.agencyContacts = agencyContacts;
    }

    public List<Integer> getSiteContacts() {
        return siteContacts;
    }

    public void setSiteContacts(List<Integer> siteContacts) {
        this.siteContacts = siteContacts;
    }

    public Integer getCurrentContactId() {
        return currentContactId;
    }

    public void setCurrentContactId(Integer currentContactId) {
        this.currentContactId = currentContactId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Long getCookieDomainId() {
        return cookieDomainId;
    }

    public void setCookieDomainId(Long cookieDomainId) {
        this.cookieDomainId = cookieDomainId;
    }
}

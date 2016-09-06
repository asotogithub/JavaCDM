package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "CookieDomain")
public class CookieDomain {

    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "COOKIE_DOMAIN_ID")
    private Long id;
    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "AGENCY_ID")
    private Long agencyId;
    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "DOMAIN")
    private String domain;
    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "COOKIE_DOMAIN", field = "COOKIE_DOMAIN_ROOT_ID")
    private Long cookieDomainRootId;

    public CookieDomain() {
    }

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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public Long getCookieDomainRootId() {
        return cookieDomainRootId;
    }

    public void setCookieDomainRootId(Long cookieDomainRootId) {
        this.cookieDomainRootId = cookieDomainRootId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CookieDomain that = (CookieDomain) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public String toString() {
        return "CookieDomain [cookieDomainId=" + id + ", agencyId="
                + agencyId + ", domain=" + domain + ", logicalDelete="
                + logicalDelete + ", createdTpwsKey=" + createdTpwsKey
                + ", modifiedTpwsKey=" + modifiedTpwsKey + ", createdDate="
                + createdDate + ", modifiedDate=" + modifiedDate
                + ", cookieDomainRootId=" + cookieDomainRootId + "]";
    }
}

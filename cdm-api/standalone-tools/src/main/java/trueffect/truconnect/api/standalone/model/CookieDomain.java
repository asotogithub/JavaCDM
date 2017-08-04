package trueffect.truconnect.api.standalone.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "CookieDomain")
@XmlType(propOrder = {"id", "domain", "logicalDelete", "createdTpwsKey",
        "modifiedTpwsKey", "createdDate", "modifiedDate", "cookieDomainRootId"})
public class CookieDomain {

    private Long id;
    private String domain;
    private String logicalDelete;
    private String createdTpwsKey;
    private String modifiedTpwsKey;
    private Date createdDate;
    private Date modifiedDate;
    private Long cookieDomainRootId;

    public CookieDomain() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public String toString() {
        return "CookieDomain [id=" + id + ", domain=" + domain
                + ", logicalDelete=" + logicalDelete + ", createdTpwsKey="
                + createdTpwsKey + ", modifiedTpwsKey=" + modifiedTpwsKey
                + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + ", cookieDomainRootId=" + cookieDomainRootId
                + "]";
    }
}

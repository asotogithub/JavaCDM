package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "PackagePlacement")
public class PackagePlacement {

    private Long id;
    private Long packageId;
    private Long placementId;
    private String logicalDelete;
    private Date created;
    private String createdTpwsKey;
    private Date modified;
    private String modifiedTpwsKey;

    public PackagePlacement() {
    }

    public PackagePlacement(Long packageId, Long placementId) {
        this.packageId = packageId;
        this.placementId = placementId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedTpwsKey() {
        return createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(packageId)
                .append(placementId)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PackagePlacement) {
            final PackagePlacement rhs = (PackagePlacement) obj;
            return new EqualsBuilder()
                    .append(id, rhs.id)
                    .append(packageId, rhs.packageId)
                    .append(placementId, rhs.placementId)
                    .isEquals();
        } else {
            return false;
        }
    }
}

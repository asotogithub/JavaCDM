package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "Package")
public class Package {

    @TableFieldMapping(table = "PACKAGE", field = "PACKAGE_ID")
    private Long id;
    @TableFieldMapping(table = "PACKAGE", field = "PACKAGE_NAME")
    private String name;
    @TableFieldMapping(table = "PACKAGE", field = "PACKAGE_DESC")
    private String description;
    @TableFieldMapping(table = "PACKAGE", field = "CAMPAIGN_ID")
    private Long campaignId;
    @TableFieldMapping(table = "PACKAGE", field = "EXT_PACKAGE_ID")
    private String externalId;
    @TableFieldMapping(table = "PACKAGE", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "PACKAGE", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "PACKAGE", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "PACKAGE", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "PACKAGE", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "PACKAGE", field = "COUNTRY_CURRENCY_ID")
    private Long countryCurrencyId;
    @TableFieldMapping(table = "PLACEMENT", field = "IO_ID")
    private Long ioId;
    
    private Long placementCount;
    
    private List<CostDetail> costDetails;
    private List<Placement> placements;

    private boolean changed;

    public Package() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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

    public String getModifiedTpwsKey() {
        return modifiedTpwsKey;
    }

    public void setModifiedTpwsKey(String modifiedTpwsKey) {
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    public Long getCountryCurrencyId() {
        return countryCurrencyId;
    }

    public void setCountryCurrencyId(Long countryCurrencyId) {
        this.countryCurrencyId = countryCurrencyId;
    }

    public Long getIoId() {
        return ioId;
    }

    public void setIoId(Long ioId) {
        this.ioId = ioId;
    }

    public Long getPlacementCount() {
        return placementCount;
    }

    public void setPlacementCount(Long placementCount) {
        this.placementCount = placementCount;
    }

    public List<CostDetail> getCostDetails() {
        return costDetails;
    }

    public void setCostDetails(List<CostDetail> costDetails) {
        this.costDetails = costDetails;
    }

    public List<Placement> getPlacements() {
        return placements;
    }

    public void setPlacements(List<Placement> placements) {
        this.placements = placements;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

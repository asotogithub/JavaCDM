package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gustavo Claure
 * Modified by Richard Jaldin
 */
@XmlRootElement(name = "PlacementCostDetail")
public class PlacementCostDetail {

    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "PLACEMENT_COST_ID")
    private Long id;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "PLACEMENT_ID")
    private Long placementId;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "START_DATE")
    private Date startDate;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "END_DATE")
    private Date endDate;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "INVENTORY")
    private Long inventory;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "RATE_TYPE")
    private Long rateType;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "LOGICAL_DELETE")
    private String logicalDelete;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "CREATED")
    private Date createdDate;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "CREATED_TPWS_KEY")
    private String createdTpwsKey;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "MODIFIED")
    private Date modifiedDate;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "MODIFIED_TPWS_KEY")
    private String modifiedTpwsKey;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "PLANNED_NET_RATE")
    private Double plannedNetRate;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "PLANNED_GROSS_RATE")
    private Double plannedGrossRate;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "PLANNED_NET_AD_SPEND")
    private Double plannedNetAdSpend;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "PLANNED_GROSS_AD_SPEND")
    private Double plannedGrossAdSpend;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "ACTUAL_NET_RATE")
    private Double actualNetRate;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "ACTUAL_GROSS_RATE")
    private Double actualGrossRate;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "ACTUAL_NET_AD_SPEND")
    private Double actualNetAdSpend;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "ACTUAL_GROSS_AD_SPEND")
    private Double actualGrossAdSpend;
    @TableFieldMapping(table = "PLACEMENT_COST_DETAIL", field = "COUNTRY_CURRENCY_ID")
    private Long countryCurrencyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getInventory() {
        return inventory;
    }

    public void setInventory(Long inventory) {
        this.inventory = inventory;
    }

    public Long getRateType() {
        return rateType;
    }

    public void setRateType(Long rateType) {
        this.rateType = rateType;
    }

    public String getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(String logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedTpwsKey() {
        return createdTpwsKey;
    }

    public void setCreatedTpwsKey(String createdTpwsKey) {
        this.createdTpwsKey = createdTpwsKey;
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

    public Double getPlannedNetRate() {
        return plannedNetRate;
    }

    public void setPlannedNetRate(Double plannedNetRate) {
        this.plannedNetRate = plannedNetRate;
    }

    public Double getPlannedGrossRate() {
        return plannedGrossRate;
    }

    public void setPlannedGrossRate(Double plannedGrossRate) {
        this.plannedGrossRate = plannedGrossRate;
    }

    public Double getPlannedNetAdSpend() {
        return plannedNetAdSpend;
    }

    public void setPlannedNetAdSpend(Double plannedNetAdSpend) {
        this.plannedNetAdSpend = plannedNetAdSpend;
    }

    public Double getPlannedGrossAdSpend() {
        return plannedGrossAdSpend;
    }

    public void setPlannedGrossAdSpend(Double plannedGrossAdSpend) {
        this.plannedGrossAdSpend = plannedGrossAdSpend;
    }

    public Double getActualNetRate() {
        return actualNetRate;
    }

    public void setActualNetRate(Double actualNetRate) {
        this.actualNetRate = actualNetRate;
    }

    public Double getActualGrossRate() {
        return actualGrossRate;
    }

    public void setActualGrossRate(Double actualGrossRate) {
        this.actualGrossRate = actualGrossRate;
    }

    public Double getActualNetAdSpend() {
        return actualNetAdSpend;
    }

    public void setActualNetAdSpend(Double actualNetAdSpend) {
        this.actualNetAdSpend = actualNetAdSpend;
    }

    public Double getActualGrossAdSpend() {
        return actualGrossAdSpend;
    }

    public void setActualGrossAdSpend(Double actualGrossAdSpend) {
        this.actualGrossAdSpend = actualGrossAdSpend;
    }

    public Long getCountryCurrencyId() {
        return countryCurrencyId;
    }

    public void setCountryCurrencyId(Long countryCurrencyId) {
        this.countryCurrencyId = countryCurrencyId;
    }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Cost Detail representation
 * Created by richard.jaldin on 10/8/2015.
 * @author Richard Jaldin
 */
@XmlRootElement(name = "CostDetail")
public class CostDetail {
    private Long id;
    private Long foreignId;
    private Date startDate;
    private Date endDate;
    private Long inventory;
    private Long rateType;
    private String logicalDelete;
    private Date createdDate;
    private String createdTpwsKey;
    private Date modifiedDate;
    private String modifiedTpwsKey;
    private Double plannedNetRate;
    private Double plannedGrossRate;
    private Double plannedNetAdSpend;
    private Double plannedGrossAdSpend;
    private Double actualNetRate;
    private Double actualGrossRate;
    private Double actualNetAdSpend;
    private Double actualGrossAdSpend;
    private Long countryCurrencyId;
    private Double margin;

    public CostDetail() {
    }

    public CostDetail(CostDetail costDetail) {
        this.id = costDetail.getId();
        this.foreignId = costDetail.getForeignId();
        this.startDate = costDetail.getStartDate();
        this.endDate = costDetail.getEndDate();
        this.inventory = costDetail.getInventory();
        this.rateType = costDetail.getRateType();
        this.logicalDelete = costDetail.getLogicalDelete();
        this.createdDate = costDetail.getCreatedDate();
        this.createdTpwsKey = costDetail.getCreatedTpwsKey();
        this.modifiedDate = costDetail.getModifiedDate();
        this.modifiedTpwsKey = costDetail.getModifiedTpwsKey();
        this.plannedNetRate = costDetail.getPlannedNetRate();
        this.plannedGrossRate = costDetail.getPlannedGrossRate();
        this.plannedNetAdSpend = costDetail.getPlannedNetAdSpend();
        this.plannedGrossAdSpend = costDetail.getPlannedGrossAdSpend();
        this.actualNetRate = costDetail.getActualNetRate();
        this.actualGrossRate = costDetail.getActualGrossRate();
        this.actualNetAdSpend = costDetail.getActualNetAdSpend();
        this.actualGrossAdSpend = costDetail.getActualGrossAdSpend();
        this.countryCurrencyId = costDetail.getCountryCurrencyId();
        this.margin = costDetail.getMargin();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getForeignId() {
        return foreignId;
    }

    public void setForeignId(Long foreignId) {
        this.foreignId = foreignId;
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
        return createdDate;
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
        return modifiedDate;
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

    public Double getMargin() {
        return margin;
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CostDetail that = (CostDetail) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

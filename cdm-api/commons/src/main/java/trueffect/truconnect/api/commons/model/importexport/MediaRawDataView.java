package trueffect.truconnect.api.commons.model.importexport;

import trueffect.truconnect.api.commons.exceptions.business.ImportExportCellError;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marleny.patsi
 */
public class MediaRawDataView extends RawDataView
        implements Serializable, XLSTemplateDescriptor, XlsErrorTemplateDescriptor {

    private static final MediaRawErrorDataView ERROR_CLASS;
    private final static Map<String, Column> TEMPLATE_MAPPING;

    static {
        ERROR_CLASS = new MediaRawErrorDataView();
        TEMPLATE_MAPPING = new LinkedHashMap<String, Column>() {
            {
                int index = 1;
                put("B", new Column("OrderNumber", index++));
                put("C", new Column("OrderName", index++));
                put("D", new Column("MediaPackageName", index++));
                put("E", new Column("MediaPackageId", index++));
                put("F", new Column("PlacementName", index++));
                put("G", new Column("PlacementId", index++));
                put("H", new Column("ExtPlacementId", index++));
                put("I", new Column("Publisher", index++));
                put("J", new Column("Site", index++));
                put("K", new Column("SiteId", index++));
                put("L", new Column("Section", index++));
                put("M", new Column("AdWidth", index++));
                put("N", new Column("AdHeight", index++));
                put("O", new Column("PlannedAdSpend", index++));
                put("P", new Column("Inventory", index++));
                put("Q", new Column("Rate", index++));
                put("R", new Column("RateType", index++));
                put("S", new Column("StartDate", index++));
                put("T", new Column("EndDate", index++));
                put("U", new Column("PlacementProp1", index++));
                put("V", new Column("PlacementProp2", index++));
                put("W", new Column("PlacementProp3", index++));
                put("X", new Column("PlacementProp4", index++));
                put("Y", new Column("PlacementProp5", index++));
                put("Z", new Column("SectionProp1", index++));
                put("AA", new Column("SectionProp2", index++));
                put("AB", new Column("SectionProp3", index++));
                put("AC", new Column("SectionProp4", index++));
                put("AD", new Column("SectionProp5", index));
            }
        };
    }
 
    private static final int ROW_HEADER = 6;
    private String orderNumber;
    private String orderName;
    private String mediaPackageId;
    private String mediaPackageName;
    private String placementId;
    private String placementName;
    private String extPlacementId;
    private String publisher;
    private String site;
    private String section;
    private String adWidth;
    private String adHeight;
    private String plannedAdSpend;
    private String inventory;
    private String rate;
    private String rateType;
    private String startDate;
    private String endDate;
    private String placementProp1;
    private String placementProp2;
    private String placementProp3;
    private String placementProp4;
    private String placementProp5;
    private String sectionProp1;
    private String sectionProp2;
    private String sectionProp3;
    private String sectionProp4;
    private String sectionProp5;

    private Long ioId;
    private Long publisherId;
    private String siteId;
    private Long sectionId;
    private Long sizeId;
    private Long existingPlacements;
    private String rowError;
    private String reason;

    private List<CostDetailRawDataView> costDetails;
    private boolean packageChanged;
    private boolean placementChanged;
    private boolean costDetailsChanged;

    //Internal fields
    private Long mediaPackageIdDb;
    private Long siteIdDb;
    private String placementNameAutoGenerated;

    public MediaRawDataView() {
        super();
        costDetails = new ArrayList<>();
    }

    @Override
    public int getHeaderRow() {
        return ROW_HEADER;
    }

    @Override
    public Map<String, Column> getTemplateMapping() {
        return TEMPLATE_MAPPING;
    }

    @Override
    public XLSTemplateDescriptor getAlternativeClassType() {
        return ERROR_CLASS;
    }

    public String getRowError() {
        return rowError;
    }

    @Override
    public void setRowError(String rowError) {
        this.rowError = rowError;
    }

    /**
     * @deprecated Do not use since this will be removed once we use
     * {@link MediaRawDataView#getIssues()} all over the places
     * @return A String with all error messages separated by a comma
     */
    @Deprecated
    public String getReason() {
        if (StringUtils.isEmpty(reason)) {
            if (issues != null) {
                List<String> allErrors = new ArrayList<>();
                for (Map.Entry<String, List<ImportExportCellError>> entry : issues.entrySet()) {
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        for (ImportExportCellError error : entry.getValue()) {
                            allErrors.add(error.getMessage());
                        }
                    }
                }
                return StringUtils.join(allErrors, ", ");
            } else {
                return "";
            }
        } else {
            return reason;
        }
    }

    @Deprecated
    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getMediaPackageId() {
        return mediaPackageId;
    }

    public void setMediaPackageId(String mediaPackageId) {
        this.mediaPackageId = mediaPackageId;
    }

    public String getMediaPackageName() {
        return mediaPackageName;
    }

    public void setMediaPackageName(String mediaPackageName) {
        this.mediaPackageName = mediaPackageName;
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public String getPlacementName() {
        return placementName;
    }

    public void setPlacementName(String placementName) {
        this.placementName = placementName;
    }

    public String getExtPlacementId() {
        return extPlacementId;
    }

    public void setExtPlacementId(String extPlacementId) {
        this.extPlacementId = extPlacementId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getAdWidth() {
        return adWidth;
    }

    public void setAdWidth(String adWidth) {
        this.adWidth = adWidth;
    }

    public String getAdHeight() {
        return adHeight;
    }

    public void setAdHeight(String adHeight) {
        this.adHeight = adHeight;
    }

    public String getPlannedAdSpend() {
        return plannedAdSpend;
    }

    public void setPlannedAdSpend(String plannedAdSpend) {
        this.plannedAdSpend = plannedAdSpend;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPlacementProp1() {
        return placementProp1;
    }

    public void setPlacementProp1(String placementProp1) {
        this.placementProp1 = placementProp1;
    }

    public String getPlacementProp2() {
        return placementProp2;
    }

    public void setPlacementProp2(String placementProp2) {
        this.placementProp2 = placementProp2;
    }

    public String getPlacementProp3() {
        return placementProp3;
    }

    public void setPlacementProp3(String placementProp3) {
        this.placementProp3 = placementProp3;
    }

    public String getPlacementProp4() {
        return placementProp4;
    }

    public void setPlacementProp4(String placementProp4) {
        this.placementProp4 = placementProp4;
    }

    public String getPlacementProp5() {
        return placementProp5;
    }

    public void setPlacementProp5(String placementProp5) {
        this.placementProp5 = placementProp5;
    }

    public String getSectionProp1() {
        return sectionProp1;
    }

    public void setSectionProp1(String sectionProp1) {
        this.sectionProp1 = sectionProp1;
    }

    public String getSectionProp2() {
        return sectionProp2;
    }

    public void setSectionProp2(String sectionProp2) {
        this.sectionProp2 = sectionProp2;
    }

    public String getSectionProp3() {
        return sectionProp3;
    }

    public void setSectionProp3(String sectionProp3) {
        this.sectionProp3 = sectionProp3;
    }

    public String getSectionProp4() {
        return sectionProp4;
    }

    public void setSectionProp4(String sectionProp4) {
        this.sectionProp4 = sectionProp4;
    }

    public String getSectionProp5() {
        return sectionProp5;
    }

    public void setSectionProp5(String sectionProp5) {
        this.sectionProp5 = sectionProp5;
    }

    public Long getIoId() {
        return ioId;
    }

    public void setIoId(Long ioId) {
        this.ioId = ioId;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public List<CostDetailRawDataView> getCostDetails() {
        return costDetails;
    }

    public void setCostDetails(
            List<CostDetailRawDataView> costDetails) {
        this.costDetails = costDetails;
    }

    public Long getExistingPlacements() {
        return existingPlacements;
    }

    public void setExistingPlacements(Long existingPlacements) {
        this.existingPlacements = existingPlacements;
    }

    public boolean isPackageChanged() {
        return packageChanged;
    }

    public void setPackageChanged(boolean packageChanged) {
        this.packageChanged = packageChanged;
    }

    public boolean isPlacementChanged() {
        return placementChanged;
    }

    public void setPlacementChanged(boolean placementChanged) {
        this.placementChanged = placementChanged;
    }

    public boolean isCostDetailsChanged() {
        return costDetailsChanged;
    }

    public void setCostDetailsChanged(boolean costDetailsChanged) {
        this.costDetailsChanged = costDetailsChanged;
    }

    public Long getMediaPackageIdDb() {
        return mediaPackageIdDb;
    }

    public void setMediaPackageIdDb(Long mediaPackageIdDb) {
        this.mediaPackageIdDb = mediaPackageIdDb;
    }

    public Long getSiteIdDb() {
        return siteIdDb;
    }

    public void setSiteIdDb(Long siteIdDb) {
        this.siteIdDb = siteIdDb;
    }

    public String getPlacementNameAutoGenerated() {
        return placementNameAutoGenerated;
    }

    public void setPlacementNameAutoGenerated(String placementNameAutoGenerated) {
        this.placementNameAutoGenerated = placementNameAutoGenerated;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    // Helper methods
    public boolean hasValidInsertionOrder() {
        return (this.issues.get("orderNumber") == null || this.issues.get("orderNumber").isEmpty())
                && (this.issues.get("orderName") == null || this.issues.get("orderName").isEmpty());
    }

    public boolean hasValidPublisher() {
        return this.issues.get("publisher") == null || this.issues.get("publisher").isEmpty();
    }

    public boolean hasValidSite() {
        return this.issues.get("site") == null || this.issues.get("site").isEmpty();
    }

    public boolean hasValidSection() {
        return (this.issues.get("section") == null || this.issues.get("section").isEmpty())
                && (this.issues.get("sectionProp1") == null || this.issues.get("sectionProp1").isEmpty())
                && (this.issues.get("sectionProp2") == null || this.issues.get("sectionProp2").isEmpty())
                && (this.issues.get("sectionProp3") == null || this.issues.get("sectionProp3").isEmpty())
                && (this.issues.get("sectionProp4") == null || this.issues.get("sectionProp4").isEmpty())
                && (this.issues.get("sectionProp5") == null || this.issues.get("sectionProp5").isEmpty());
    }

    public boolean hasValidSize() {
        return (this.issues.get("adWidth") == null || this.issues.get("adWidth").isEmpty())
                && (this.issues.get("adHeight") == null || this.issues.get("adHeight").isEmpty());
    }

    public boolean isCostDetailRow() {
        StringBuffer buffer = buildNonCostDetailStringBuffer();
        return  StringUtils.isBlank(buffer.toString());
    }

    private StringBuffer buildNonCostDetailStringBuffer() {
        return new StringBuffer().append(StringUtils.defaultString(orderNumber))
        .append(StringUtils.defaultString(orderName))
        .append(StringUtils.defaultString(mediaPackageId))
        .append(StringUtils.defaultString(mediaPackageName))
        .append(StringUtils.defaultString(placementId))
        .append(StringUtils.defaultString(placementName))
        .append(StringUtils.defaultString(extPlacementId))
        .append(StringUtils.defaultString(publisher))
        .append(StringUtils.defaultString(siteId))
        .append(StringUtils.defaultString(site))
        .append(StringUtils.defaultString(section))
        .append(StringUtils.defaultString(publisher))
        .append(StringUtils.defaultString(adWidth))
        .append(StringUtils.defaultString(adHeight));
    }

    /**
     * <p>
     *    Checks if this {@code MediaRawDataView} has valid Cost Details.
     * </p>
     * <p>
     *    This method checks if any of the issues have a key that starts with "costDetails".
     * </p>
     * @return true, if there is at least one key from {@code issues} that starts with "costDetails";
     * false otherwise.
     */
    public boolean hasValidCostDetails() {
        for(String key : this.issues.keySet()) {
            if(key.startsWith("costDetails")) {
                return false;
            }
        }
        return true;
    }

    public boolean hasValidPackage() {
        boolean result = true;
        for(String key : this.issues.keySet()) {
            if(key.startsWith("mediaPackageName")) {
                result = false;
                break;
            }
        }
        return result;
    }

    public boolean isEmpty() {
        StringBuffer buffer = buildNonCostDetailStringBuffer();
        buffer.append(StringUtils.defaultString(plannedAdSpend)).
        append(StringUtils.defaultString(inventory)).
        append(StringUtils.defaultString(rate)).
        append(StringUtils.defaultString(rateType)).
        append(StringUtils.defaultString(startDate)).
        append(StringUtils.defaultString(endDate)).
        append(StringUtils.defaultString(placementProp1)).
        append(StringUtils.defaultString(placementProp2)).
        append(StringUtils.defaultString(placementProp3)).
        append(StringUtils.defaultString(placementProp4)).
        append(StringUtils.defaultString(placementProp5)).
        append(StringUtils.defaultString(sectionProp1)).
        append(StringUtils.defaultString(sectionProp2)).
        append(StringUtils.defaultString(sectionProp3)).
        append(StringUtils.defaultString(sectionProp4)).
        append(StringUtils.defaultString(sectionProp5));
        return StringUtils.isBlank(buffer.toString());
    }

    /**
     * Validates if the lists of {@code CostDetailRawDataView} for this {@code MediaRawDataView}
     * are the same than the Cost Details from {@code other}
     * @param other The other {@code MediaRawDataView} to get the Cost Details to compare
     * @return true, if Cost Details are the same
     */
    public boolean costDetailsEquals(final MediaRawDataView other) {
        if(other == null) {
            return false;
        }
        if (costDetails.size() != other.getCostDetails().size()) {
            return false;
        }
        for (int i = 0; i < costDetails.size(); i++) {
            if(!costDetails.get(i).equals(other.getCostDetails().get(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean costDetailsHaveWarnings() {
        for(CostDetailRawDataView costDetail : costDetails) {
            if(!costDetail.getWarnings().isEmpty()){
                return true;
            }
        }
        return false;
    }

    public boolean costDetailsHaveIssues() {
        for(CostDetailRawDataView costDetail : costDetails) {
            if(!costDetail.getIssues().isEmpty()){
                return true;
            }
        }
        return false;
    }
    
    public boolean packagePropertiesEqual(final MediaRawDataView pkg) {
        if(pkg == null) {
            return false;
        }
        EqualsBuilder eb = new EqualsBuilder().append(mediaPackageId, pkg.getMediaPackageId());
        if(StringUtils.isNotBlank(mediaPackageName)) {
            eb.append(mediaPackageName, pkg.getMediaPackageName());
        }
        return eb.isEquals();
    }

    public boolean placementPropertiesEqual(final MediaRawDataView plac) {
        if(plac == null) {
            return false;
        }
        EqualsBuilder eb = new EqualsBuilder().append(placementId, plac.getPlacementId());
        if(StringUtils.isNotBlank(placementName)) {
            eb.append(placementName, plac.getPlacementName());
        }
        if(StringUtils.isNotBlank(extPlacementId)) {
            eb.append(extPlacementId, plac.getExtPlacementId());
        }
        if(StringUtils.isNotBlank(placementProp1)) {
            eb.append(placementProp1, plac.getPlacementProp1());
        }
        if(StringUtils.isNotBlank(placementProp2)) {
            eb.append(placementProp2, plac.getPlacementProp2());
        }
        if(StringUtils.isNotBlank(placementProp3)) {
            eb.append(placementProp3, plac.getPlacementProp3());
        }
        if(StringUtils.isNotBlank(placementProp4)) {
            eb.append(placementProp4, plac.getPlacementProp4());
        }
        if(StringUtils.isNotBlank(placementProp5)) {
            eb.append(placementProp5, plac.getPlacementProp5());
        }
        return eb.isEquals();
    }
}

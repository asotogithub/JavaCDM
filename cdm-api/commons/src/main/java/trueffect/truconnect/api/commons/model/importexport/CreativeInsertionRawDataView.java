package trueffect.truconnect.api.commons.model.importexport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author saulo.lopez
 */
@XmlRootElement(name = "CreativeInsertionRawDataView")
public class CreativeInsertionRawDataView
        implements Serializable, XLSTemplateDescriptor, XlsErrorTemplateDescriptor {

    private static final CreativeInsertionRawErrorDataView ERROR_CLASS;
    private static final Map<String, Column> TEMPLATE_MAPPING;
    static {
        ERROR_CLASS = new CreativeInsertionRawErrorDataView();
        TEMPLATE_MAPPING = new LinkedHashMap<String, Column>() {
            {
                int index = 0;
                put("A", new Column("SiteName", index++));
                put("B", new Column("PlacementName", index++));
                put("C", new Column("PlacementId", index++));
                put("D", new Column("CreativeGroupName", index++));
                put("E", new Column("GroupWeight", index++));
                put("F", new Column("PlacementCreativeName", index++));
                put("G", new Column("CreativeWeight", index++));
                put("H", new Column("CreativeStartDate", index++));
                put("I", new Column("CreativeEndDate", index++));
                put("J", new Column("CreativeClickThroughUrl", index++));
                put("K", new Column("CreativeInsertionId", index++));
            }
        };
    }

    protected static final int ROW_HEADER = 5;
    private String siteName;
    private String placementName;
    private String placementId;
    private String creativeGroupName;
    private String groupWeight;
    private String placementCreativeName;
    private String creativeWeight;
    private String creativeStartDate;
    private String creativeEndDate;
    private String creativeClickThroughUrl;
    private String creativeType;
    private String creativeInsertionId;
    private String reason;
    private String rowError;
    private Long creativeGroupId;
    private boolean ciPropsChanged;
    private boolean groupPropsChanged;
    private String modifiedTpwsKey;

    private List<String> fieldsWithFormulaError;

    public CreativeInsertionRawDataView() {
        fieldsWithFormulaError = new ArrayList<>();
    }

    public CreativeInsertionRawDataView(String siteName, String placementName, String placementId,
                                        String creativeGroupName, String groupWeight,
                                        String placementCreativeName, String creativeWeight,
                                        String creativeStartDate, String creativeEndDate,
                                        String creativeClickThroughUrl, String creativeInsertionId,
                                        String reason, Long groupId) {
        this();
        this.siteName = siteName;
        this.placementName = placementName;
        this.placementId = placementId;
        this.creativeGroupName = creativeGroupName;
        this.groupWeight = groupWeight;
        this.placementCreativeName = placementCreativeName;
        this.creativeWeight = creativeWeight;
        this.creativeStartDate = creativeStartDate;
        this.creativeEndDate = creativeEndDate;
        this.creativeClickThroughUrl = creativeClickThroughUrl;
        this.creativeInsertionId = creativeInsertionId;
        this.reason = reason;
        this.creativeGroupId = groupId;
    }
    
    /**
     * @return the siteName
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * @param siteName the siteName to set
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * @return the placementName
     */
    public String getPlacementName() {
        return placementName;
    }

    /**
     * @param placementName the placementName to set
     */
    public void setPlacementName(String placementName) {
        this.placementName = placementName;
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    /**
     * @return the creativeGroupName
     */
    public String getCreativeGroupName() {
        return creativeGroupName;
    }

    /**
     * @param creativeGroupName the creativeGroupName to set
     */
    public void setCreativeGroupName(String creativeGroupName) {
        this.creativeGroupName = creativeGroupName;
    }

    /**
     * @return the groupWeight
     */
    public String getGroupWeight() {
        return groupWeight;
    }

    /**
     * @param groupWeight the groupWeight to set
     */
    public void setGroupWeight(String groupWeight) {
        this.groupWeight = groupWeight;
    }

    /**
     * @return the placementCreativeName
     */
    public String getPlacementCreativeName() {
        return placementCreativeName;
    }

    /**
     * @param placementCreativeName the placementCreativeName to set
     */
    public void setPlacementCreativeName(String placementCreativeName) {
        this.placementCreativeName = placementCreativeName;
    }

    /**
     * @return the creativeWeight
     */
    public String getCreativeWeight() {
        return creativeWeight;
    }

    /**
     * @param creativeWeight the creativeWeight to set
     */
    public void setCreativeWeight(String creativeWeight) {
        this.creativeWeight = creativeWeight;
    }

    /**
     * @return the creativeStartDate
     */
    public String getCreativeStartDate() {
        return creativeStartDate;
    }

    /**
     * @param creativeStartDate the creativeStartDate to set
     */
    public void setCreativeStartDate(String creativeStartDate) {
        this.creativeStartDate = creativeStartDate;
    }

    /**
     * @return the creativeEndDate
     */
    public String getCreativeEndDate() {
        return creativeEndDate;
    }

    /**
     * @param creativeEndDate the creativeEndDate to set
     */
    public void setCreativeEndDate(String creativeEndDate) {
        this.creativeEndDate = creativeEndDate;
    }

    /**
     * @return the creativeClickThroughUrl
     */
    public String getCreativeClickThroughUrl() {
        return creativeClickThroughUrl;
    }

    /**
     * @param creativeClickThroughUrl the creativeClickThroughUrl to set
     */
    public void setCreativeClickThroughUrl(String creativeClickThroughUrl) {
        this.creativeClickThroughUrl = creativeClickThroughUrl;
    }

    public String getCreativeType() {
        return creativeType;
    }

    public void setCreativeType(String creativeType) {
        this.creativeType = creativeType;
    }

    /**
     * @return the creativeInsertionId
     */
    public String getCreativeInsertionId() {
        return creativeInsertionId;
    }

    /**
     * @param creativeInsertionId the creativeInsertionId to set
     */
    public void setCreativeInsertionId(String creativeInsertionId) {
        this.creativeInsertionId = creativeInsertionId;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    /**
     * @return the rowError
     */
    public String getRowError() {
        return rowError;
    }

    /**
     * @param rowError the rowError to set
     */
    public void setRowError(String rowError) {
        this.rowError = rowError;
    }

    public Long getCreativeGroupId() {
        return creativeGroupId;
    }

    public void setCreativeGroupId(Long creativeGroupId) {
        this.creativeGroupId = creativeGroupId;
    }

    public boolean isCiPropsChanged() {
        return ciPropsChanged;
    }

    public void setCiPropsChanged(boolean isChanged) {
        this.ciPropsChanged = isChanged;
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

    public Map<String, Column> getTemplateMapping() {
        return TEMPLATE_MAPPING;
    }

    @Override
    public XLSTemplateDescriptor getAlternativeClassType() {
        return ERROR_CLASS;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(groupWeight)
                .append(creativeWeight)
                .append(creativeStartDate)
                .append(creativeEndDate)
                .append(creativeClickThroughUrl)
                .append(creativeInsertionId)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CreativeInsertionRawDataView) {
            final CreativeInsertionRawDataView ci = (CreativeInsertionRawDataView) obj;
            return creativeInsertionPropsEqual(ci) && groupPropsEqual(ci);
        } else {
            return false;
        }
    }

    public boolean groupPropsEqual(CreativeInsertionRawDataView ci) {
        EqualsBuilder eb = new EqualsBuilder();
        if(StringUtils.isNotBlank(groupWeight)) {
            eb.append(groupWeight, ci.groupWeight);
        }
        return eb.isEquals();
    }

    public boolean creativeInsertionPropsEqual(CreativeInsertionRawDataView ci) {
        EqualsBuilder eb = new EqualsBuilder().append(creativeInsertionId, ci.creativeInsertionId);

        if(StringUtils.isNotBlank(creativeWeight)) {
            eb.append(creativeWeight, ci.creativeWeight);
        }

        if(StringUtils.isNotBlank(creativeStartDate)) {
            eb.append(creativeStartDate, ci.creativeStartDate);
        }

        if(StringUtils.isNotBlank(creativeEndDate)) {
            eb.append(creativeEndDate, ci.creativeEndDate);
        }

        if(StringUtils.isNotBlank(creativeClickThroughUrl)) {
            eb.append(creativeClickThroughUrl, ci.creativeClickThroughUrl);
        }

        return eb.isEquals();
    }


    @Override
    public int getHeaderRow() {
        return ROW_HEADER;
    }

    public boolean isGroupPropsChanged() {
        return groupPropsChanged;
    }

    public void setGroupPropsChanged(boolean groupPropsChanged) {
        this.groupPropsChanged = groupPropsChanged;
    }

    public List<String> getFieldsWithFormulaError() {
        return this.fieldsWithFormulaError;
    }

    @Override
    public void setFieldsWithFormulaError(List<String> fieldsWithFormulaError) {
        this.fieldsWithFormulaError = fieldsWithFormulaError;
    }
}

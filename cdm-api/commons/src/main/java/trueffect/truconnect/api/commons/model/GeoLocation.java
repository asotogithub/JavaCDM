package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;
import trueffect.truconnect.api.commons.model.enums.LocationType;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model object to contain a record for a location used in geo-targeting.
 */
@XmlRootElement(name = "GeoLocation")
public class GeoLocation {
    @TableFieldMapping(table = "TARGET_VALUE", field = "TARGET_VALUE_ID")
    private Long id;
    @TableFieldMapping(table = "TARGET_VALUE", field = "TARGET_LABEL")
    private String label;
    @TableFieldMapping(table = "TARGET_VALUE", field = "TARGET_CODE")
    private String code;
    @TableFieldMapping(table = "TARGET_VALUE", field = "TYPE_ID")
    private Long typeId;
    @TableFieldMapping(table = "TARGET_VALUE", field = "PARENT_CODE")
    private String parentCode;
    private LocationType locationType;
    private String countryLabel;
    private String stateLabel;
    private String dmaLabel;
    private String zipLabel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getCountryLabel() {
        return countryLabel;
    }

    public void setCountryLabel(String countryLabel) {
        this.countryLabel = countryLabel;
    }

    public String getStateLabel() {
        return stateLabel;
    }

    public void setStateLabel(String stateLabel) {
        this.stateLabel = stateLabel;
    }

    public String getDmaLabel() {
        return dmaLabel;
    }

    public void setDmaLabel(String dmaLabel) {
        this.dmaLabel = dmaLabel;
    }

    public String getZipLabel() {
        return zipLabel;
    }

    public void setZipLabel(String zipLabel) {
        this.zipLabel = zipLabel;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "ExtendedProperties")
public class ExtendedProperties {

    @TableFieldMapping(table = "EXTENDED_PROPERTIES", field = "EXTENDED_PROPERTIES_ID")
    private Long id;
    @TableFieldMapping(table = "EXTENDED_PROPERTIES", field = "OBJECT_NAME")
    private String objectName;
    @TableFieldMapping(table = "EXTENDED_PROPERTIES", field = "FIELD_NAME")
    private String fieldName;
    @TableFieldMapping(table = "EXTENDED_PROPERTIES", field = "DATA_TYPE")
    private String dataType;
    @TableFieldMapping(table = "EXTENDED_PROPERTIES", field = "TABLE_NAME")
    private String tableName;
    // Foreign Columns (1 TABLE)
    @TableFieldMapping(table = "EXTENDED_PROPERTIES_VALUE", field = "EXTENDED_PROPVALUE_ID")
    private Long extendPropValueId;
    @TableFieldMapping(table = "EXTENDED_PROPERTIES_VALUE", field = "VALUE")
    private String value;
    @TableFieldMapping(table = "EXTENDED_PROPERTIES_VALUE", field = "OBJECT_ID")
    private Long objectId;

    public ExtendedProperties() {
    }

    public ExtendedProperties(Long id, String fieldName, String value, Long objectId,
            String objectName, Long extendPropValueId, String dataType, String tableName) {
        this.id = id;
        this.fieldName = fieldName;
        this.value = value;
        this.objectId = objectId;
        this.objectName = objectName;
        this.extendPropValueId = extendPropValueId;
        this.dataType = dataType;
        this.tableName = tableName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Long getExtendPropValueId() {
        return extendPropValueId;
    }

    public void setExtendPropValueId(Long extendPropValueId) {
        this.extendPropValueId = extendPropValueId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "ExtendedProperties{" + "id=" + id + ", fieldName=" + fieldName + ", value=" + value
                + ", objectId=" + objectId + ", objectName=" + objectName + ", extendPropValueId="
                + extendPropValueId + ", dataType=" + dataType + ", tableName=" + tableName + '}';
    }
}

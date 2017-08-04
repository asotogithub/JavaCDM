package trueffect.truconnect.api.commons.exceptions.business;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Error subtype for Validation purposes.
 * Created by Marcelo Heredia on 11/25/2014.
 * @author  Marcelo Heredia
 */
@XmlRootElement
public class ValidationError extends Error{

    private String field;
    private String objectName;
    private String rejectedValue;
    private String messageCode;
    private Integer minLength;
    private Integer maxLength;
    private String minValue;
    private String maxValue;

    public ValidationError() {
        super();
    }

    public ValidationError(String message, ErrorCode code) {
        super(message, code);
    }

    public ValidationError(String message,
                           ErrorCode code, String field, String objectName,
                           String rejectedValue, String messageCode, Integer minLength,
                           Integer maxLength, String minValue, String maxValue) {
        super(message, code);
        this.field = field;
        this.objectName = objectName;
        this.rejectedValue = rejectedValue;
        this.messageCode = messageCode;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(String rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

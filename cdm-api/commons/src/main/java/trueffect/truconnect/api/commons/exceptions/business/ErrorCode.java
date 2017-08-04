package trueffect.truconnect.api.commons.exceptions.business;

import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.FileUploadCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Interface that allows representing an error code as multiple types.
 * <p>
 * Error Codes are represented as Enums. Any specific error code needs to be defined in its own Enum.\
 * @author  Marcelo Heredia
 */
@XmlRootElement
@XmlJavaTypeAdapter(AnyTypeAdapter.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BusinessCode.class, name = "businessCode"),
        @JsonSubTypes.Type(value = ValidationCode.class, name = "validationCode"),
        @JsonSubTypes.Type(value = SecurityCode.class, name = "securityCode") ,
        @JsonSubTypes.Type(value = FileUploadCode.class, name = "fileUploadCode") })
public interface ErrorCode extends Serializable {
    @JsonValue
    int getNumber();
}

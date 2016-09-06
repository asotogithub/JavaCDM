package trueffect.truconnect.api.commons.exceptions.business.enums;

import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * High level Security error codes.
 */
@XmlType
@XmlEnum(Integer.class)
public enum SecurityCode implements ErrorCode {
    @XmlEnumValue("300")
    UNAUTHORIZED(300),
    @XmlEnumValue("301")
    ILLEGAL_USER_CONTEXT(301),
    @XmlEnumValue("302")
    INVALID_REQUEST(302),
    @XmlEnumValue("303")
    INVALID_TOKEN(303),

    @XmlEnumValue("304")
    ERROR_GETTING_PERMISSIONS(304),
    @XmlEnumValue("305")
    PERMISSION_DENIED(305),
    @XmlEnumValue("306")
    NOT_FOUND_FOR_USER(306);

    private final int number;

    private SecurityCode(int number) {
            this.number = number;
    }
    @Override
    public int getNumber() {
		return number;
	}

    public static SecurityCode fromValue(int value) {
        if(value < 300 || value > 399) {
            throw new IllegalArgumentException("Illegal value " + value);
        }
        for(SecurityCode v : values()) {
            if(value == v.getNumber()) {
                return v;
            }
        }
        throw new IllegalArgumentException("Provided value " + value + "is not supported");
    }
}

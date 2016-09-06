package trueffect.truconnect.api.commons.exceptions.business.enums;

import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * High level Business Logic error codes.
 */
@XmlType
@XmlEnum(Integer.class)
public enum BusinessCode implements ErrorCode {
    @XmlEnumValue("200")
    INVALID(200),
    @XmlEnumValue("201")
    DUPLICATE(201),
    @XmlEnumValue("202")
    NOT_FOUND(202),
    @XmlEnumValue("203")
    INTERNAL_ERROR(203),
    @XmlEnumValue("204")
    UNKNOWN(204),
    @XmlEnumValue("205")
    ENTITY_ID_MISMATCH(205),
    @XmlEnumValue("206")
    EXTERNAL_COMPONENT_ERROR(206),
    @XmlEnumValue("207")
    ILLEGAL_ACCESS(207);

    private final int number;

    private BusinessCode(int number) {
            this.number = number;
    }
    @Override
    public int getNumber() {
            return number;
    }

    public static BusinessCode fromValue(int value) {
        if(value < 200 || value > 299) {
            throw new IllegalArgumentException("Illegal value " + value);
        }
        for(BusinessCode v : values()) {
            if(value == v.getNumber()) {
                return v;
            }
        }
        throw new IllegalArgumentException("Provided value " + value + "is not supported");
    }
}

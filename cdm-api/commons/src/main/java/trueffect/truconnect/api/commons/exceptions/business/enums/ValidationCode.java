package trueffect.truconnect.api.commons.exceptions.business.enums;

import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * High level Validation error codes.
 */
@XmlType
@XmlEnum(Integer.class)
public enum ValidationCode implements ErrorCode {
    @XmlEnumValue("100")
    REQUIRED(100),
    @XmlEnumValue("101")
    INVALID(101),
    @XmlEnumValue("102")
    TOO_SHORT(102),
    @XmlEnumValue("103")
    TOO_LONG(103),
    @XmlEnumValue("104")
    GREATER_THAN(104),
    @XmlEnumValue("105")
    LOWER_THAN(105),
    @XmlEnumValue("106")
    UNSUPPORTED(106),
    @XmlEnumValue("107")
    NONCONSECUTIVE_DATES(107),
    @XmlEnumValue("108")
    DUPLICATE(108);

	private final int number;

	private ValidationCode(int number) {
		this.number = number;
	}

	@Override
    public int getNumber() {
		return number;
	}

    @JsonCreator
    public static ValidationCode fromValue(int value) {
        if(value < 100 || value > 199) {
            throw new IllegalArgumentException("Illegal value " + value);
        }
        for(ValidationCode v : values()) {
            if(value == v.getNumber()) {
                return v;
            }
        }
        throw new IllegalArgumentException("Provided value " + value + "is not supported");
    }
}

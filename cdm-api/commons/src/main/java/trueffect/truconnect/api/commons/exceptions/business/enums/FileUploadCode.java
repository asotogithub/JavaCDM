package trueffect.truconnect.api.commons.exceptions.business.enums;

import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * High level File Upload error codes.
 */
@XmlType
@XmlEnum(Integer.class)
public enum FileUploadCode implements ErrorCode {
    @XmlEnumValue("400")
    FILENAME_LENGTH(400),
    @XmlEnumValue("401")
    FILENAME_INVALID(401),
    @XmlEnumValue("402")
    FILE_DUPLICATE(402),
    @XmlEnumValue("403")
    NOT_ALLOWED_CREATIVE_FILE_TYPE(403),
    @XmlEnumValue("404")
    INTERNAL_ERROR(404),
    @XmlEnumValue("405")
    FILENAME_REQUIRED(405),
    @XmlEnumValue("406")
    ZIP_WITH_HARMFUL_FILE(406),
    @XmlEnumValue("408")
    ZIP_WITH_ZIP_FILE(408),
    @XmlEnumValue("409")
    ZIP_WITH_MISSING_BACKUP_FILE(409),
    @XmlEnumValue("410")
    ZIP_WITH_MISSING_SECONDARY_FILE(410),
    @XmlEnumValue("411")
    FILENAME_DUPLICATE(411),
    @XmlEnumValue("412")
    EMPTY_CREATIVE_FILE(412),
    @XmlEnumValue("413")
    MAX_FILE_SIZE(413),
    @XmlEnumValue("414")
    FILE_INVALID_FORMAT(414),
    @XmlEnumValue("415")
    NOT_ALLOWED_FILE_TYPE(415),
    @XmlEnumValue("416")
    EMPTY_FILE(416),
    @XmlEnumValue("417")
    FILE_XLSX_MAX_ROWS(417),
    @XmlEnumValue("418")
    NOT_ALLOWED_IMPORT_FILE_TYPE(418);

    private final int number;

    private FileUploadCode(int number) {
            this.number = number;
    }
    @Override
    public int getNumber() {
		return number;
	}

    public static FileUploadCode fromValue(int value) {
        if(value < 400 || value > 499) {
            throw new IllegalArgumentException("Illegal value " + value);
        }
        for(FileUploadCode v : values()) {
            if(value == v.getNumber()) {
                return v;
            }
        }
        throw new IllegalArgumentException("Provided value " + value + "is not supported");
    }
}

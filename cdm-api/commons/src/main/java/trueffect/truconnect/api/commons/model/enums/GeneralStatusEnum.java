package trueffect.truconnect.api.commons.model.enums;

/**
 *
 * @author marleny.patsi
 */
public enum GeneralStatusEnum {
    NEW("New"),
    ACTIVE("Active");

    private String statusCode;

    GeneralStatusEnum(String s) {
        statusCode = s;
    }
    public String getStatusCode() {
        return statusCode;
    }

    public static GeneralStatusEnum fromStateCode(String statusCode) {
        if (statusCode == null) {
            throw new IllegalArgumentException("Status Code cannot be null");
        }
        for (GeneralStatusEnum ct : values()) {
            if (ct.getStatusCode().equalsIgnoreCase(statusCode)) {
                return ct;
            }
        }
        return null;
    }
}

package trueffect.truconnect.api.commons.model.enums;

/**
 *
 * Created by richard.jaldin on 7/20/2015.
 */
public enum InsertionOrderStatusEnum {
    NEW("New", 10L, "IO_NEW"),
    ACCEPTED("Accepted", 16L, "IO_ACPT"),
    REJECTED("Rejected", 17L, "IO_RJCT");

    private String name;
    private String statusName;
    private Long code;

    private InsertionOrderStatusEnum(String name, Long code, String statusName) {
        this.name = name;
        this.code = code;
        this.statusName = statusName;
    }

    public String getName() {
        return name;
    }

    public Long getCode() {
        return code;
    }

    public static InsertionOrderStatusEnum fromName(String name) {
        InsertionOrderStatusEnum result = null;
        for(InsertionOrderStatusEnum v : values()) {
            if(v.getName().equals(name)) {
                result = v;
                break;
            }
        }
        return result;
    }

    public String getStatusName() {
        return statusName;
    }
}

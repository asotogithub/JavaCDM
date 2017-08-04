package trueffect.truconnect.api.commons.model.enums;

import java.util.LinkedList;
import java.util.List;

/**
 * Enum for getting the type codes.
 */
public enum LocationType {
    COUNTRY("geo_country"),
    STATE("geo_state"),
    DMA("geo_dma"),
    ZIP("geo_zip");

    private String code;

    LocationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LocationType typeOf(String code) {
        LocationType result;
        switch(code) {
            case "geo_state":
                result = STATE;
                break;
            case "geo_country":
                result = COUNTRY;
                break;
            case "geo_dma":
                result = DMA;
                break;
            case "geo_zip":
                result = ZIP;
                break;
            default:
                result = null;
        }
        return result;
    }

    public static List<LocationType> getHierarchy(LocationType type) {
        LinkedList<LocationType> hierarchy = new LinkedList<>();
        if(type == null) {
            throw new IllegalArgumentException("'type' cannot be null");
        }

        switch (type) {
            case ZIP:
                hierarchy.addFirst(ZIP);
            case DMA:
                hierarchy.addFirst(DMA);
            case STATE:
                hierarchy.addFirst(STATE);
            case COUNTRY:
                hierarchy.addFirst(COUNTRY);
        }
        return hierarchy;
    }

    public static LocationType getAncestor(LocationType type) {
        LocationType ancestor = null;

        switch (type) {
            case ZIP:
                ancestor = DMA;
                break;
            case DMA:
                ancestor = STATE;
                break;
            case STATE:
                ancestor = COUNTRY;
                break;
            case COUNTRY:
                ancestor = null;
                break;
        }

        return ancestor;
    }
}

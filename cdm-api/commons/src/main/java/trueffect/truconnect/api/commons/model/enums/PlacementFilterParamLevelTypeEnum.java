package trueffect.truconnect.api.commons.model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author marleny.patsi
 */
public enum PlacementFilterParamLevelTypeEnum {

    /**
     * These numbers are the IDs of the entities in the table TE_XLS.ENTITY_TYPE
     * that are used in the TE_XLS.HTML_INJECTION_VAL_REF table to mark/show the
     * relationship between a HTML_INJECTION_TAG and the respective Entity.
     */
    CAMPAIGN(11L),
    SITE(10L),
    SECTION(9L),
    PLACEMENT(5L);

    public static final Map<PlacementFilterParamLevelTypeEnum, ArrayList<PlacementFilterParamLevelTypeEnum>>
            DEFAULT_HIERARCHY_TO_LEVEL = new HashMap<>();
    public static final Map<PlacementFilterParamLevelTypeEnum, ArrayList<PlacementFilterParamLevelTypeEnum>>
            DEFAULT_HIERARCHY_FROM_LEVEL = new HashMap<>();
    private Long code;

    static {
        DEFAULT_HIERARCHY_TO_LEVEL.put(PLACEMENT, new ArrayList<>(
                Arrays.asList(CAMPAIGN, SITE, SECTION, PLACEMENT)));
        DEFAULT_HIERARCHY_TO_LEVEL.put(SECTION, new ArrayList<>(
                Arrays.asList(CAMPAIGN, SITE, SECTION)));
        DEFAULT_HIERARCHY_TO_LEVEL.put(SITE, new ArrayList<>(Arrays.asList(CAMPAIGN, SITE)));
        DEFAULT_HIERARCHY_TO_LEVEL.put(CAMPAIGN, new ArrayList<>(Arrays.asList(CAMPAIGN)));

        DEFAULT_HIERARCHY_FROM_LEVEL.put(CAMPAIGN, new ArrayList<>(
                Arrays.asList(CAMPAIGN, SITE, SECTION, PLACEMENT)));
        DEFAULT_HIERARCHY_FROM_LEVEL.put(SITE, new ArrayList<>(
                Arrays.asList(SITE, SECTION, PLACEMENT)));
        DEFAULT_HIERARCHY_FROM_LEVEL.put(SECTION, new ArrayList<>(Arrays.asList(SECTION, PLACEMENT)));
        DEFAULT_HIERARCHY_FROM_LEVEL.put(PLACEMENT, new ArrayList<>(Arrays.asList(PLACEMENT)));
    }

    private PlacementFilterParamLevelTypeEnum(Long levelTypeCode) {
        this.code = levelTypeCode;
    }

    public Long getCode() {
        return code;
    }

    public static PlacementFilterParamLevelTypeEnum typeOf(Long type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        for (PlacementFilterParamLevelTypeEnum levelType : values()) {
            if (levelType.getCode().equals(type)) {
                return levelType;
            }
        }
        return null;
    }
}

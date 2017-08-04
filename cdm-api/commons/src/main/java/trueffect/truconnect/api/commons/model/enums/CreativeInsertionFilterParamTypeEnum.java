package trueffect.truconnect.api.commons.model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richard.jaldin on 12/29/2015.
 */
public enum CreativeInsertionFilterParamTypeEnum {
    SITE,
    SECTION,
    PLACEMENT,
    GROUP,
    CREATIVE,
    SCHEDULE;

    public static final List<CreativeInsertionFilterParamTypeEnum> PIVOT_TYPES;
    public static final Map<CreativeInsertionFilterParamTypeEnum, ArrayList<CreativeInsertionFilterParamTypeEnum>> DEFAULT_HIERARCHY = new HashMap<>();
    public static final Map<CreativeInsertionFilterParamTypeEnum, Map<CreativeInsertionFilterParamTypeEnum, ArrayList<CreativeInsertionFilterParamTypeEnum>>> HIERARCHIES_BY_PIVOT_TO_LEVEL = new HashMap<>();
    public static final Map<CreativeInsertionFilterParamTypeEnum, Map<CreativeInsertionFilterParamTypeEnum, ArrayList<CreativeInsertionFilterParamTypeEnum>>> HIERARCHIES_BY_PIVOT_FROM_LEVEL = new HashMap<>();
    static {
        PIVOT_TYPES = new ArrayList<>(Arrays.asList(SITE, PLACEMENT, GROUP, CREATIVE));
        DEFAULT_HIERARCHY.put(SCHEDULE, new ArrayList<>(Arrays.asList(SITE, SECTION, PLACEMENT, GROUP, SCHEDULE)));
        DEFAULT_HIERARCHY.put(GROUP, new ArrayList<>(Arrays.asList(SITE, SECTION, PLACEMENT, GROUP)));
        DEFAULT_HIERARCHY.put(PLACEMENT, new ArrayList<>(Arrays.asList(SITE, SECTION, PLACEMENT)));
        DEFAULT_HIERARCHY.put(SECTION, new ArrayList<>(Arrays.asList(SITE, SECTION)));
        DEFAULT_HIERARCHY.put(SITE, new ArrayList<>(Arrays.asList(SITE)));
        
        HIERARCHIES_BY_PIVOT_TO_LEVEL.put(SITE, DEFAULT_HIERARCHY);

        Map<CreativeInsertionFilterParamTypeEnum, ArrayList<CreativeInsertionFilterParamTypeEnum>> levels = new HashMap<>();
        levels.put(PLACEMENT, new ArrayList<>(Arrays.asList(PLACEMENT)));
        levels.put(GROUP, new ArrayList<>(Arrays.asList(PLACEMENT, GROUP)));
        levels.put(SCHEDULE, new ArrayList<>(Arrays.asList(PLACEMENT, GROUP, SCHEDULE)));
        HIERARCHIES_BY_PIVOT_TO_LEVEL.put(PLACEMENT, levels);
        
        levels = new HashMap<>();
        levels.put(GROUP, new ArrayList<>(Arrays.asList(GROUP)));
        levels.put(SITE, new ArrayList<>(Arrays.asList(GROUP, SITE)));
        levels.put(PLACEMENT, new ArrayList<>(Arrays.asList(GROUP, SITE, PLACEMENT)));
        levels.put(SCHEDULE, new ArrayList<>(Arrays.asList(GROUP, SITE, PLACEMENT, SCHEDULE)));
        HIERARCHIES_BY_PIVOT_TO_LEVEL.put(GROUP, levels);

        levels = new HashMap<>();
        levels.put(CREATIVE, new ArrayList<>(Arrays.asList(CREATIVE)));
        levels.put(SITE, new ArrayList<>(Arrays.asList(CREATIVE, SITE)));
        levels.put(PLACEMENT, new ArrayList<>(Arrays.asList(CREATIVE, SITE, PLACEMENT)));
        levels.put(GROUP, new ArrayList<>(Arrays.asList(CREATIVE, SITE, PLACEMENT, GROUP)));
        levels.put(SCHEDULE, new ArrayList<>(Arrays.asList(CREATIVE, SITE, PLACEMENT, GROUP, SCHEDULE)));
        HIERARCHIES_BY_PIVOT_TO_LEVEL.put(CREATIVE, levels);
        
        levels = new HashMap<>();
        levels.put(SITE, new ArrayList<>(Arrays.asList(SITE, SECTION, PLACEMENT, GROUP, SCHEDULE)));
        levels.put(SECTION, new ArrayList<>(Arrays.asList(SECTION, PLACEMENT, GROUP, SCHEDULE)));
        levels.put(PLACEMENT, new ArrayList<>(Arrays.asList(PLACEMENT, GROUP, SCHEDULE)));
        levels.put(GROUP, new ArrayList<>(Arrays.asList(GROUP, SCHEDULE)));
        levels.put(SCHEDULE, new ArrayList<>(Arrays.asList(SCHEDULE)));
        HIERARCHIES_BY_PIVOT_FROM_LEVEL.put(SITE, levels);
        
        levels = new HashMap<>();
        levels.put(PLACEMENT, new ArrayList<>(Arrays.asList(PLACEMENT, GROUP, SCHEDULE)));
        levels.put(GROUP, new ArrayList<>(Arrays.asList(GROUP, SCHEDULE)));
        levels.put(SCHEDULE, new ArrayList<>(Arrays.asList(SCHEDULE)));
        HIERARCHIES_BY_PIVOT_FROM_LEVEL.put(PLACEMENT, levels);
        
        levels = new HashMap<>();
        levels.put(GROUP, new ArrayList<>(Arrays.asList(GROUP, SITE, PLACEMENT, SCHEDULE)));
        levels.put(SITE, new ArrayList<>(Arrays.asList(SITE, PLACEMENT, SCHEDULE)));
        levels.put(PLACEMENT, new ArrayList<>(Arrays.asList(PLACEMENT, SCHEDULE)));
        levels.put(SCHEDULE, new ArrayList<>(Arrays.asList(SCHEDULE)));
        HIERARCHIES_BY_PIVOT_FROM_LEVEL.put(GROUP, levels);

        levels = new HashMap<>();
        levels.put(CREATIVE, new ArrayList<>(Arrays.asList(CREATIVE, SITE, PLACEMENT, GROUP, SCHEDULE)));
        levels.put(SITE, new ArrayList<>(Arrays.asList(SITE, PLACEMENT, GROUP, SCHEDULE)));
        levels.put(PLACEMENT, new ArrayList<>(Arrays.asList(PLACEMENT, GROUP, SCHEDULE)));
        levels.put(GROUP, new ArrayList<>(Arrays.asList(GROUP, SCHEDULE)));
        levels.put(SCHEDULE, new ArrayList<>(Arrays.asList(SCHEDULE)));
        HIERARCHIES_BY_PIVOT_FROM_LEVEL.put(CREATIVE, levels);
    }
}

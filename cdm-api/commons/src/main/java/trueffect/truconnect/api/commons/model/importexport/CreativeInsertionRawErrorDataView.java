package trueffect.truconnect.api.commons.model.importexport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by richard.jaldin on 4/26/2016.
 */
public class CreativeInsertionRawErrorDataView extends CreativeInsertionRawDataView {

    private static final Map<String, Column> TEMPLATE_MAPPING;
    static {
        TEMPLATE_MAPPING = new LinkedHashMap<String, Column>() {
            {
                int index = 0;
                put("A", new Column("RowError", index++));
                put("B", new Column("Reason", index++));
                put("C", new Column("SiteName", index++));
                put("D", new Column("PlacementName", index++));
                put("E", new Column("PlacementId", index++));
                put("F", new Column("CreativeGroupName", index++));
                put("G", new Column("GroupWeight", index++));
                put("H", new Column("PlacementCreativeName", index++));
                put("I", new Column("CreativeWeight", index++));
                put("J", new Column("CreativeStartDate", index++));
                put("K", new Column("CreativeEndDate", index++));
                put("L", new Column("CreativeClickThroughUrl", index++));
                put("M", new Column("CreativeInsertionId", index++));
            }
        };
    }

    @Override
    public Map<String, Column> getTemplateMapping() {
        return TEMPLATE_MAPPING;
    }

    @Override
    public XLSTemplateDescriptor getAlternativeClassType() {
        return null;
    }
}

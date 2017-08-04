package trueffect.truconnect.api.commons.model.importexport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by richard.jaldin on 5/18/2016.
 */
public class MediaRawErrorDataView extends MediaRawDataView {
    private static final Map<String, Column> TEMPLATE_MAPPING;
    static {
        TEMPLATE_MAPPING = new LinkedHashMap<String, Column>() {
            {
                int index = 1;
                put("B", new Column("RowError", index++));
                put("C", new Column("Reason", index++));
                put("D", new Column("OrderNumber", index++));
                put("E", new Column("OrderName", index++));
                put("F", new Column("MediaPackageName", index++));
                put("G", new Column("MediaPackageId", index++));
                put("H", new Column("PlacementName", index++));
                put("I", new Column("PlacementId", index++));
                put("J", new Column("ExtPlacementId", index++));
                put("K", new Column("Publisher", index++));
                put("L", new Column("Site", index++));
                put("M", new Column("SiteId", index++));
                put("N", new Column("Section", index++));
                put("O", new Column("AdWidth", index++));
                put("P", new Column("AdHeight", index++));
                put("Q", new Column("PlannedAdSpend", index++));
                put("R", new Column("Inventory", index++));
                put("S", new Column("Rate", index++));
                put("T", new Column("RateType", index++));
                put("U", new Column("StartDate", index++));
                put("V", new Column("EndDate", index++));
                put("W", new Column("PlacementProp1", index++));
                put("X", new Column("PlacementProp2", index++));
                put("Y", new Column("PlacementProp3", index++));
                put("Z", new Column("PlacementProp4", index++));
                put("AA", new Column("PlacementProp5", index++));
                put("AB", new Column("SectionProp1", index++));
                put("AC", new Column("SectionProp2", index++));
                put("AD", new Column("SectionProp3", index++));
                put("AE", new Column("SectionProp4", index++));
                put("AF", new Column("SectionProp5", index));
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

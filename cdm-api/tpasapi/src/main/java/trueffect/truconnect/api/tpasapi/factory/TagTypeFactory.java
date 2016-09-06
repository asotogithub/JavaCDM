package trueffect.truconnect.api.tpasapi.factory;

import java.util.List;
import java.util.ArrayList;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.TagType;

/**
 *
 * @author Rambert Rioja
 */
public class TagTypeFactory {

    public static RecordSet<TagType> toTpasapiObject(trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.delivery.TagType> entities) {
        RecordSet<TagType> records = new RecordSet<TagType>();
        records.setPageSize(entities.getPageSize());
        records.setStartIndex(entities.getStartIndex());
        records.setTotalNumberOfRecords(entities.getTotalNumberOfRecords());
        List<TagType> aux = new ArrayList<TagType>();
        TagType creativeGroup;
        if (entities.getRecords() != null) {
            for (trueffect.truconnect.api.commons.model.delivery.TagType record : entities.getRecords()) {
                creativeGroup = toTpasapiObject(record);
                aux.add(creativeGroup);
            }
        }
        records.setRecords(aux);
        return records;
    }

    private static TagType toTpasapiObject(trueffect.truconnect.api.commons.model.delivery.TagType tagType) {
        TagType type = new TagType();
        type.setId(tagType.getTagId());
        type.setName(tagType.getName());
        type.setDescription(tagType.getDescription());
        return type;
    }
}

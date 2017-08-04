package trueffect.truconnect.api.tpasapi.factory;

import java.util.List;
import java.util.ArrayList;
import trueffect.truconnect.api.tpasapi.model.Placement;
import trueffect.truconnect.api.tpasapi.model.RecordSet;

/**
 *
 * @author Rambert Rioja
 */
public class PlacementFactory {

    public static Placement createTpasapiObject(trueffect.truconnect.api.commons.model.Placement placement) {
        PlacementFactory factory = new PlacementFactory();
        return factory.toTpasapiObject(placement);
    }

    public static trueffect.truconnect.api.commons.model.Placement createPublicObject(Placement placement) {
        PlacementFactory factory = new PlacementFactory();
        return factory.toPublicObject(placement);
    }

    public static trueffect.truconnect.api.commons.model.Placement copyCostDetails(trueffect.truconnect.api.commons.model.Placement placementSource, 
            trueffect.truconnect.api.commons.model.Placement placementTarget) {
        placementTarget.setCostDetails(placementSource.getCostDetails());
        return placementTarget;
    }

    public static RecordSet<Placement> createTpasapiObjects(trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.Placement> placements) {
        RecordSet<Placement> records = new RecordSet<Placement>();
        records.setPageSize(placements.getPageSize());
        records.setStartIndex(placements.getStartIndex());
        records.setTotalNumberOfRecords(placements.getTotalNumberOfRecords());
        List<Placement> aux = new ArrayList<Placement>();
        Placement creativeGroup;
        PlacementFactory factory = new PlacementFactory();
        if(placements.getRecords() != null) {
            for (trueffect.truconnect.api.commons.model.Placement record : placements.getRecords()) {
                creativeGroup = factory.toTpasapiObject(record);
                aux.add(creativeGroup);
            }
        }
        records.setRecords(aux);
        return records;
    }

    private Placement toTpasapiObject(trueffect.truconnect.api.commons.model.Placement place) {
        Placement placement = new Placement();
        placement.setId(place.getId());
        placement.setCampaignId(place.getCampaignId());
        placement.setName(place.getName());
        placement.setSiteId(place.getSiteId());
        placement.setWidth(place.getWidth());
        placement.setHeight(place.getHeight());
        placement.setStatus(place.getStatus());
        placement.setIsSecure(place.getIsSecure() == 1L);
        placement.setMaxFileSize(place.getMaxFileSize());
        placement.setCreatedDate(place.getCreatedDate());
        placement.setModifiedDate(place.getModifiedDate());
        placement.setExternalId(place.getExternalId());
        return placement;
    }

    private trueffect.truconnect.api.commons.model.Placement toPublicObject(Placement input) {
    	trueffect.truconnect.api.commons.model.Placement placement = new trueffect.truconnect.api.commons.model.Placement();
        placement.setId(input.getId());
        placement.setCampaignId(input.getCampaignId());
        placement.setName(input.getName());
        placement.setSiteId(input.getSiteId());
        placement.setWidth(input.getWidth());
        placement.setHeight(input.getHeight());
        placement.setStatus(input.getStatus());
        placement.setIsSecure(input.getIsSecure() != null && input.getIsSecure() ? 1L : 0L);
        placement.setMaxFileSize(input.getMaxFileSize());
        placement.setCreatedDate(input.getCreatedDate());
        placement.setModifiedDate(input.getModifiedDate());
        placement.setExternalId(input.getExternalId());
        placement.setUtcOffset(0L);
        placement.setCountryCurrencyId(1L);
        return placement;
    }
}

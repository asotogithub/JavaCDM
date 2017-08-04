package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;

import trueffect.truconnect.api.tpasapi.model.Placement;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.factory.PlacementFactory;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.MediaBuy;

/**
 *
 * @author Rambert Rioja
 */
public class PlacementProxy extends BaseProxy<trueffect.truconnect.api.commons.model.Placement> {

    public PlacementProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.Placement.class, headers);
        path("Placements");
    }

    public Placement getPlacement() throws ProxyException {
        return PlacementFactory.createTpasapiObject(get());
    }

    public RecordSet<Placement> getPlacements(SearchCriteria searchCriteria) throws ProxyException {
        trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.Placement> records;
        records = get(searchCriteria);
        RecordSet<Placement> creativeGroups = PlacementFactory.createTpasapiObjects(records);
        return creativeGroups;
    }

    public Placement savePlacement(Placement placement) throws ProxyException {
        trueffect.truconnect.api.commons.model.Placement publicInput = PlacementFactory.createPublicObject(placement);

        // There is expected to be a single media buy and insertion order for the campaign
        // If either of these does not exist, create them using "Default" as the media buy name and "Default" as the Insertion Order Number.
        log.info("Looking for MediaBuy and InsertionOrder");
        MediaBuyProxy mediaBuyProxy = new MediaBuyProxy(headers);
        InsertionOrderProxy ioProxy = new InsertionOrderProxy(headers);
        MediaBuy mediaBuy = null;
        try {
            mediaBuy = mediaBuyProxy.getByCampaign(publicInput.getCampaignId());
        } catch (ProxyException e) {
            if (!e.getMessage().contains("Record not found.")) {
                log.warn("Proxy Exception: " , e);
                throw e;
            }
        }
        InsertionOrder io = null;
        if (mediaBuy != null) {
            try {
                io = ioProxy.getByMediaBuy(mediaBuy.getId());
            } catch (ProxyException e) {
                if (!e.getMessage().contains("Record not found.")) {
                    throw e;
                }
            }
            if (io == null) {
                ioProxy = new InsertionOrderProxy(headers);
                io = ioProxy.createDefault(mediaBuy);

            }
        } else {
            log.info("Creating default MediaBuy and InsertionOrder");
            mediaBuyProxy = new MediaBuyProxy(headers);
            ioProxy = new InsertionOrderProxy(headers);
            mediaBuy = mediaBuyProxy.createDefault(publicInput.getCampaignId());
            io = ioProxy.createDefault(mediaBuy);
        }
        publicInput.setIoId(io.getId());
        trueffect.truconnect.api.commons.model.Placement result = this.post(publicInput);
        log.info("Public Result: " + result + ", Status: " + getLastResponse().getStatus());
        return PlacementFactory.createTpasapiObject(result);
    }

    public Placement updatePlacement(Placement placement) throws ProxyException {
        //obtain costDetails from db
        trueffect.truconnect.api.commons.model.Placement placementFromDB = get();
        trueffect.truconnect.api.commons.model.Placement publicInput = PlacementFactory.createPublicObject(placement);
        publicInput = PlacementFactory.copyCostDetails(placementFromDB, publicInput);
        if (placement.getName() == null || placement.getName().isEmpty()) {
            publicInput.setName(placementFromDB.getName());
        }
        return PlacementFactory.createTpasapiObject(this.put(publicInput));
    }   
}

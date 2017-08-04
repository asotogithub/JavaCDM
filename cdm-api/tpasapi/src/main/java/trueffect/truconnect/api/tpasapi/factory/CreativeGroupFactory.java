package trueffect.truconnect.api.tpasapi.factory;

import java.util.List;
import java.util.ArrayList;

import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.GeoTarget;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.model.CreativeGroup;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupTarget;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;

/**
 *
 * @author Rambert Rioja
 */
public class CreativeGroupFactory {

    public static RecordSet<CreativeGroup> createTpasapiObjects(trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.CreativeGroup> creativeGroups) {
        RecordSet<CreativeGroup> records = new RecordSet<CreativeGroup>();
        records.setPageSize(creativeGroups.getPageSize());
        records.setStartIndex(creativeGroups.getStartIndex());
        records.setTotalNumberOfRecords(creativeGroups.getTotalNumberOfRecords());
        List<CreativeGroup> aux = new ArrayList<CreativeGroup>();
        CreativeGroup creativeGroup;
        CreativeGroupFactory factory = new CreativeGroupFactory();
        if (creativeGroups.getRecords() != null) {
            for (trueffect.truconnect.api.commons.model.CreativeGroup record : creativeGroups.getRecords()) {
                creativeGroup = factory.toTpasapiObject(record);
                aux.add(creativeGroup);
            }
        }
        records.setRecords(aux);
        return records;
    }

    private CreativeGroup toTpasapiObject(trueffect.truconnect.api.commons.model.CreativeGroup cg) {
        CreativeGroup creativeGroup = new CreativeGroup();
        creativeGroup.setId(cg.getId());
        creativeGroup.setCampaignId(cg.getCampaignId());
        creativeGroup.setName(cg.getName());
        creativeGroup.setImpressionCap(cg.getImpressionCap());
        creativeGroup.setIsDefault(cg.getIsDefault() != null && cg.getIsDefault() == 1L);
        creativeGroup.setDoGeoTargeting(cg.getDoGeoTargeting() != null && cg.getDoGeoTargeting() == 1L);
        creativeGroup.setIsReleased(cg.getIsReleased() != null && cg.getIsReleased() == 1L);
        creativeGroup.setCreatedDate(cg.getCreatedDate());
        creativeGroup.setModifiedDate(cg.getModifiedDate());
        creativeGroup.setWeight(cg.getWeight());
        creativeGroup.setRotationType(cg.getRotationType());
        creativeGroup.setTargetValueIds(getTargetValueIds(cg.getGeoTargets()));
        creativeGroup.setDoCookieTargeting(cg.getDoCookieTargeting() != null && cg.getDoCookieTargeting() == 1L);
        creativeGroup.setCookieTarget(cg.getCookieTarget());
        creativeGroup.setDoDaypartTargeting(cg.getDoDaypartTargeting() != null && cg.getDoDaypartTargeting() == 1L);
        creativeGroup.setDaypartTarget(cg.getDaypartTarget());
        creativeGroup.setEnableGroupWeight(cg.getEnableGroupWeight() != null && cg.getEnableGroupWeight() == 1L);
        creativeGroup.setPriority(cg.getPriority());
        creativeGroup.setEnableFrequencyCap(cg.getEnableFrequencyCap() != null && cg.getEnableFrequencyCap() == 1L);
        creativeGroup.setFrequencyCap(cg.getFrequencyCap());
        creativeGroup.setFrequencyCapWindow(cg.getFrequencyCapWindow());
        creativeGroup.setExternalId(cg.getExternalId());
        return creativeGroup;
    }

    private trueffect.truconnect.api.commons.model.CreativeGroup toPublicObject(CreativeGroup cg) {
        trueffect.truconnect.api.commons.model.CreativeGroup creativeGroup = new trueffect.truconnect.api.commons.model.CreativeGroup();
        creativeGroup.setId(cg.getId());
        creativeGroup.setCampaignId(cg.getCampaignId());
        creativeGroup.setName(cg.getName());
        creativeGroup.setImpressionCap(cg.getImpressionCap());
        Long isDefault = cg.getIsDefault() != null && cg.getIsDefault() ? 1L : 0L;
        creativeGroup.setIsDefault(isDefault);
        Long doGeoTarget = cg.getDoGeoTargeting() != null && cg.getDoGeoTargeting() ? 1L : 0L;
        creativeGroup.setDoGeoTargeting(doGeoTarget);
        Long released = cg.getIsReleased() != null && cg.getIsReleased() ? 1L : 0L;
        creativeGroup.setIsReleased(released);
        creativeGroup.setCreatedDate(cg.getCreatedDate());
        creativeGroup.setModifiedDate(cg.getModifiedDate());
        creativeGroup.setGeoTargets(getGeoTargets(cg.getTargetValueIds()));
        creativeGroup.setWeight(cg.getWeight());
        creativeGroup.setRotationType(cg.getRotationType());
        Long doCookieTarget = cg.getDoCookieTargeting() != null && cg.getDoCookieTargeting() ? 1L : 0L;
        creativeGroup.setDoCookieTargeting(doCookieTarget);
        creativeGroup.setCookieTarget(cg.getCookieTarget());
        Long doDayPartTarget = cg.getDoDaypartTargeting() != null && cg.getDoDaypartTargeting() ? 1L : 0L;
        creativeGroup.setDoDaypartTargeting(doDayPartTarget);
        creativeGroup.setDaypartTarget(cg.getDaypartTarget());

        Long enableGroupWeight = cg.getEnableGroupWeight() != null && cg.getEnableGroupWeight() ? 1L : 0L;
        creativeGroup.setEnableGroupWeight(enableGroupWeight);
        creativeGroup.setPriority(cg.getPriority());
        Long enableFrequencyCap = cg.getEnableFrequencyCap() != null && cg.getEnableFrequencyCap() ? 1L : 0L;
        creativeGroup.setEnableFrequencyCap(enableFrequencyCap);
        creativeGroup.setFrequencyCap(cg.getFrequencyCap());
        creativeGroup.setFrequencyCapWindow(cg.getFrequencyCapWindow());
        creativeGroup.setExternalId(cg.getExternalId());

        //Default values
        creativeGroup.setDoStoryboarding(0L);
        creativeGroup.setClickthroughCap(0L);
        creativeGroup.setDoOptimization(0L);
        creativeGroup.setMinOptimizationWeight(0L);
        creativeGroup.setCreatedTpwsKey("000000");
        return creativeGroup;
    }

    private CreativeGroupTarget getTargetValueIds(List<trueffect.truconnect.api.commons.model.GeoTarget> input) {
        CreativeGroupTarget targetIds = null;
        if(input != null && !input.isEmpty()) {
            targetIds = new CreativeGroupTarget();
            List<Long> targetValueIds = new ArrayList<>(0);
            for (trueffect.truconnect.api.commons.model.GeoTarget geoTarget : input) {
                for(trueffect.truconnect.api.commons.model.CreativeGroupTarget creativeGroupTarget : geoTarget.getCreativeGroupTargets()) {
                    targetIds.setTypeCode(creativeGroupTarget.getTypeCode());
                    targetValueIds.add(creativeGroupTarget.getValueId());
                }
            }
            targetIds.setTargetValuesIds(targetValueIds);
        }
        return targetIds;

    }

    private List<trueffect.truconnect.api.commons.model.GeoTarget> getGeoTargets(CreativeGroupTarget targetValueIds) {
        List<trueffect.truconnect.api.commons.model.GeoTarget> geoTargets = null;
        if(targetValueIds != null && targetValueIds.getTargetValuesIds() != null && !targetValueIds.getTargetValuesIds().isEmpty()) {
            geoTargets = new ArrayList<>(targetValueIds.getTargetValuesIds().size());
            trueffect.truconnect.api.commons.model.GeoTarget target = new trueffect.truconnect.api.commons.model.GeoTarget();
            target.setAntiTarget(0L);
            target.setTypeCode(targetValueIds.getTypeCode());
            List<trueffect.truconnect.api.commons.model.GeoLocation> locations = new ArrayList<>(targetValueIds.getTargetValuesIds().size());
            for(Long valueId: targetValueIds.getTargetValuesIds()) {
                trueffect.truconnect.api.commons.model.GeoLocation geoLocation = new trueffect.truconnect.api.commons.model.GeoLocation();
                geoLocation.setId(valueId);
                locations.add(geoLocation);
            }
            target.setTargets(locations);
            geoTargets.add(target);
        }
        return geoTargets;
    }

    public static CreativeGroup createTpasapiObject(trueffect.truconnect.api.commons.model.CreativeGroup creativeGroup) {
        CreativeGroupFactory factory = new CreativeGroupFactory();
        return factory.toTpasapiObject(creativeGroup);
    }

    public static trueffect.truconnect.api.commons.model.CreativeGroup createPublicObject(CreativeGroup creativeGroup) {
        CreativeGroupFactory factory = new CreativeGroupFactory();
        return factory.toPublicObject(creativeGroup);
    }

    public static CreativeGroupCreatives getTpasapiCreativeGroupCreative(Long id, List<CreativeGroupCreative> records) {
        CreativeGroupCreatives result = new CreativeGroupCreatives();
        result.setCreativeGroupId(id);
        List<Creative> creatives = new ArrayList<Creative>();
        for (CreativeGroupCreative record : records) {
            Creative item = new Creative();
            item.setId(record.getCreativeId());
            creatives.add(item);
        }
        result.setCreatives(creatives);
        return result;
    }
}

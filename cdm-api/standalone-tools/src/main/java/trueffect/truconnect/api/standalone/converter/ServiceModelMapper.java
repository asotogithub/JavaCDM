package trueffect.truconnect.api.standalone.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.CreativeGroupTarget;
import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.GeoTarget;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.Schedule;
import trueffect.truconnect.api.commons.model.ScheduleEntry;
import trueffect.truconnect.api.commons.model.ScheduleSet;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.Size;

public class ServiceModelMapper {

    public static Campaign toServiceModel(trueffect.truconnect.api.standalone.model.Campaign input, Long agencyId, Long advertiserId, Long brandId, Long cookieDomainId) {
        Campaign output = new Campaign();

        output.setAgencyId(agencyId);
        output.setAdvertiserId(advertiserId);
        output.setBrandId(brandId);
        output.setCookieDomainId(cookieDomainId);
        output.setName(input.getName());
        output.setStatusId(input.getStatusId());
        output.setStartDate(input.getStartDate());
        output.setEndDate(input.getEndDate());
        output.setObjective(input.getObjective());
        output.setDescription(input.getDescription());
        output.setAgencyNotes(input.getAgencyNotes());
        output.setContactId(input.getContactId());
        output.setTrafficToOwner(input.getTrafficToOwner());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setResourcePathId(input.getResourcePathId());
        output.setIsHidden(input.getIsHidden());

        return output;
    }

    public static CookieDomain toServiceModel(trueffect.truconnect.api.standalone.model.CookieDomain input, Long agencyId) {
        CookieDomain output = new CookieDomain();

        output.setAgencyId(agencyId);
        output.setDomain(input.getDomain());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setCookieDomainRootId(input.getCookieDomainRootId());

        return output;
    }

    public static Advertiser toServiceModel(trueffect.truconnect.api.standalone.model.Advertiser input, Long agencyId) {
        Advertiser output = new Advertiser();

        output.setAgencyId(agencyId);
        output.setName(input.getName());
        output.setAddress1(input.getAddress1());
        output.setAddress2(input.getAddress2());
        output.setCity(input.getCity());
        output.setState(input.getState());
        output.setZipCode(input.getZipCode());
        output.setCountry(input.getCountry());
        output.setPhoneNumber(input.getPhoneNumber());
        output.setFaxNumber(input.getFaxNumber());
        output.setUrl(input.getUrl());
        output.setContactDefault(input.getContactDefault());
        output.setEnableHtmlTag(input.getEnableHtmlTag());
        output.setNotes(input.getNotes());
        output.setIsHidden(input.getIsHidden());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }

    public static Brand toServiceModel(trueffect.truconnect.api.standalone.model.Brand input, Long advertiserId) {
        Brand output = new Brand();

        output.setAdvertiserId(advertiserId);
        output.setName(input.getName());
        output.setDescription(input.getDescription());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setIsHidden(input.getIsHidden());

        return output;
    }

    public static Creative toServiceModel(Creative output, trueffect.truconnect.api.standalone.model.Creative input, Long agencyId, Long campaignId) {

        output.setAgencyId(agencyId);
        output.setCampaignId(campaignId);
        output.setFilename(input.getFilename());
        output.setAlias(input.getAlias());
        output.setCreativeType(input.getCreativeType());
        output.setPurpose(input.getPurpose());
        output.setWidth(input.getWidth());
        output.setHeight(input.getHeight());
        output.setClickthrough(input.getClickthrough());
        output.setScheduled(input.getScheduled());
        output.setReleased(input.getReleased());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setSetCookieString(input.getSetCookieString());
        output.setExtProp1(input.getExtProp1());
        output.setExtProp2(input.getExtProp2());
        output.setExtProp3(input.getExtProp3());
        output.setExtProp4(input.getExtProp4());
        output.setExtProp5(input.getExtProp5());
        output.setRichMediaId(input.getRichMediaId());
        output.setFileSize(input.getFileSize());
        output.setSwfClickCount(input.getSwfClickCount());
        output.setIsExpandable(input.getIsExpandable());
        output.setExternalId(input.getExternalId());
        if(input.getClickthroughs() != null){
            List<Clickthrough> clicks = new ArrayList<Clickthrough>();
            for (trueffect.truconnect.api.standalone.model.Clickthrough clickthrough : input.getClickthroughs()) {
                clicks.add(toServiceModel(clickthrough));
            }
            output.setClickthroughs(clicks);
        }

        return output;
    }

    public static Clickthrough toServiceModel(trueffect.truconnect.api.standalone.model.Clickthrough input) {
        Clickthrough output = new Clickthrough();
        output.setSequence(input.getSequence());
        output.setUrl(input.getUrl());
        return output;
    }

    public static CreativeGroup toServiceModel(trueffect.truconnect.api.standalone.model.CreativeGroup input, Long campaignId) {
        CreativeGroup output = new CreativeGroup();

        output.setCampaignId(campaignId);
        output.setName(input.getName());
        output.setRotationType(input.getRotationType());
        output.setImpressionCap(input.getImpressionCap());
        output.setClickthroughCap(input.getClickthroughCap());
        output.setWeight(input.getWeight());
        output.setIsReleased(input.getIsReleased());
        output.setIsDefault(input.getIsDefault());
        output.setCookieTarget(input.getCookieTarget());
        output.setDoOptimization(input.getDoOptimization());
        output.setOptimizationType(input.getOptimizationType());
        output.setOptimizationSpeed(input.getOptimizationSpeed());
        output.setMinOptimizationWeight(input.getMinOptimizationWeight());
        output.setDoGeoTargeting(input.getDoGeoTargeting());
        output.setDoCookieTargeting(input.getDoCookieTargeting());
        output.setDoStoryboarding(input.getDoStoryboarding());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setDaypartTarget(input.getDaypartTarget());
        output.setDoDaypartTargeting(input.getDoDaypartTargeting());
        output.setEnableGroupWeight(input.getEnableGroupWeight());
        output.setPriority(input.getPriority());
        output.setEnableFrequencyCap(input.getEnableFrequencyCap());
        output.setFrequencyCap(input.getFrequencyCap());
        output.setFrequencyCapWindow(input.getFrequencyCapWindow());
        output.setGeoTargets(toServiceModel(input.getCreativeGroupTargets()));

        return output;
    }

    public static List<GeoTarget> toServiceModel(trueffect.truconnect.api.standalone.model.CreativeGroupTarget input) {
        List<GeoTarget> output = null;
        if(input != null) {
            output = new ArrayList<GeoTarget>(1);
            GeoTarget target = new GeoTarget();
            target.setAntiTarget(0L);
            target.setTypeCode(input.getTypeCode());
            List<GeoLocation> locations = new ArrayList<GeoLocation>(input.getTargetValueId().size());
            for(Long valueId : input.getTargetValueId()) {
                GeoLocation geoLocation = new GeoLocation();
                geoLocation.setId(valueId);
                locations.add(geoLocation);
            }
            target.setTargets(locations);
            output.add(target);
        }
        return output;
    }

    public static CreativeGroupCreative toServiceModel(trueffect.truconnect.api.standalone.model.CreativeGroupCreative input, Long creativeId, Long creativeGroupId) {
        CreativeGroupCreative output = new CreativeGroupCreative();

        output.setCreativeGroupId(creativeGroupId);
        output.setCreativeId(creativeId);
        output.setDisplayOrder(input.getDisplayOrder());
        output.setDisplayQuantity(input.getDisplayQuantity());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }

    public static MediaBuy toServiceModel(trueffect.truconnect.api.standalone.model.MediaBuy input) {
        MediaBuy output = new MediaBuy();

        output.setId(input.getId());
        output.setName(input.getName());
        output.setState(input.getState());
        output.setOverallBudget(input.getOverallBudget());
        output.setAgencyNotes(input.getAgencyNotes());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }

    public static InsertionOrder toServiceModel(trueffect.truconnect.api.standalone.model.InsertionOrder input, Long mediaBuyId, Long publisherId) {
        InsertionOrder output = new InsertionOrder();

        output.setMediaBuyId(mediaBuyId);
        output.setPublisherId(publisherId);
        output.setIoNumber(input.getIoNumber());
        output.setName(input.getName());
        output.setNotes(input.getNotes());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setStatus(input.getStatus());

        return output;
    }

    public static Placement toServiceModel(trueffect.truconnect.api.standalone.model.Placement input, Long siteId, Long ioId, Size size,Long campaignId) {
        Placement output = new Placement();

        output.setSiteId(siteId);
        output.setIoId(ioId);
        output.setSizeId(size.getId());
        output.setHeight(size.getHeight());
        output.setWidth(size.getWidth());
        output.setCampaignId(campaignId);
        output.setStartDate(input.getStartDate());
        output.setEndDate(input.getEndDate());
        output.setInventory(input.getInventory());
        output.setRate(input.getRate());
        output.setRateType(input.getRateType());
        output.setMaxFileSize(input.getMaxFileSize());
        output.setIsSecure(input.getIsSecure());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setIsTrafficked(input.getIsTrafficked());
        output.setResendTags(input.getResendTags());
        output.setUtcOffset(input.getUtcOffset());
        output.setSmEventId(input.getSmEventId());
        output.setCountryCurrencyId(input.getCountryCurrencyId());
        output.setName(input.getName());
        output.setExtProp1(input.getExtProp1());
        output.setExtProp2(input.getExtProp2());
        output.setExtProp3(input.getExtProp3());
        output.setExtProp4(input.getExtProp4());
        output.setExtProp5(input.getExtProp5());
        output.setStatus(input.getStatus());
        output.setExternalId(input.getExternalId());

        return output;
    }

    public static Size toServiceModel(trueffect.truconnect.api.standalone.model.Size input, Long agencyId) {
        Size output = new Size();

        output.setAgencyId(agencyId);
        output.setWidth(input.getWidth());
        output.setHeight(input.getHeight());
        output.setLabel(input.getLabel());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }

    public static Site toServiceModel(trueffect.truconnect.api.standalone.model.Site input, Long publisherId) {
        Site output = new Site();

        output.setPublisherId(publisherId);
        output.setName(input.getName());
        output.setUrl(input.getUrl());
        output.setPreferredTag(input.getPreferredTag());
        output.setRichMedia(input.getRichMedia());
        output.setAcceptsFlash(input.getAcceptsFlash());
        output.setClickTrack(input.getClickTrack());
        output.setEncode(input.getEncode());
        output.setTargetWin(input.getTargetWin());
        output.setAgencyNotes(input.getAgencyNotes());
        output.setPublisherNotes(input.getPublisherNotes());
        output.setExternalId(input.getExternalId());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }

    public static SiteSection toServiceModel(trueffect.truconnect.api.standalone.model.SiteSection input, Long siteId) {
        SiteSection output = new SiteSection();

        output.setSiteId(siteId);
        output.setName(input.getName());
        output.setAgencyNotes(input.getAgencyNotes());
        output.setPublisherNotes(input.getPublisherNotes());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setExtProp1(input.getExtProp1());
        output.setExtProp2(input.getExtProp2());
        output.setExtProp3(input.getExtProp3());
        output.setExtProp4(input.getExtProp4());
        output.setExtProp5(input.getExtProp5());

        return output;
    }

    public static Publisher toServiceModel(trueffect.truconnect.api.standalone.model.Publisher input, Long agencyId) {
        Publisher output = new Publisher();

        output.setAgencyId(agencyId);
        output.setName(input.getName());
        output.setAddress1(input.getAddress1());
        output.setAddress2(input.getAddress2());
        output.setCity(input.getCity());
        output.setState(input.getState());
        output.setZipCode(input.getZipCode());
        output.setCountry(input.getCountry());
        output.setPhoneNumber(input.getPhoneNumber());
        output.setUrl(input.getUrl());
        output.setAgencyNotes(input.getAgencyNotes());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }

    public static ScheduleSet toServiceModel(trueffect.truconnect.api.standalone.model.ScheduleSet input,
            HashMap<Long, Creative> newCreatives, HashMap<Long, CreativeGroup> newCreativeGroups,
            HashMap<Long, Placement> newPlacements) {
        ScheduleSet output = new ScheduleSet();
        List<Schedule> schedules = new ArrayList<Schedule>();
        if(input.getSchedules() != null) {
            for (trueffect.truconnect.api.standalone.model.Schedule schedule : input.getSchedules()) {
                schedules.add(toServiceModel(schedule, newCreatives.get(schedule.getCreativeId()).getId(), 
                        newCreativeGroups.get(schedule.getCreativeGroupId()).getId(),
                        newPlacements.get(schedule.getPlacementId()).getId()));
            }
        }
        output.setSchedules(schedules);
        return output;
    }

    private static Schedule toServiceModel(trueffect.truconnect.api.standalone.model.Schedule input,
            Long creativeId, Long creativeGroupId, Long placementId) {
        Schedule output = new Schedule();

        output.setPlacementId(placementId);
        output.setCreativeGroupId(creativeGroupId);
        output.setCreativeId(creativeId);
        List<ScheduleEntry> entries = new ArrayList<ScheduleEntry>();
        if(input.getEntries() != null) {
            for (trueffect.truconnect.api.standalone.model.ScheduleEntry entry : input.getEntries()) {
                entries.add(toServiceModel(entry));
            }
        }
        output.setEntries(entries);

        return output;
    }

    private static ScheduleEntry toServiceModel(trueffect.truconnect.api.standalone.model.ScheduleEntry input) {
        ScheduleEntry output = new ScheduleEntry();

        output.setStartDate(input.getStartDate());
        output.setEndDate(input.getEndDate());
        output.setTimeZone(input.getTimeZone());
        output.setWeight(input.getWeight());
        output.setSequence(input.getSequence());
        
        List<Clickthrough> clickthroughs = new ArrayList<Clickthrough>();
        if(input.getClickthroughs() != null) {
            for (trueffect.truconnect.api.standalone.model.Clickthrough click : input.getClickthroughs()) {
                clickthroughs.add(toServiceModel(click));
            }
        }
        output.setClickthroughs(clickthroughs);
        
        output.setIsReleased(input.getIsReleased());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }
}

package trueffect.truconnect.api.standalone.converter;

import java.util.ArrayList;
import java.util.List;

import trueffect.truconnect.api.standalone.model.Advertiser;
import trueffect.truconnect.api.standalone.model.Agency;
import trueffect.truconnect.api.standalone.model.Brand;
import trueffect.truconnect.api.standalone.model.Campaign;
import trueffect.truconnect.api.standalone.model.Clickthrough;
import trueffect.truconnect.api.standalone.model.CookieDomain;
import trueffect.truconnect.api.standalone.model.Creative;
import trueffect.truconnect.api.standalone.model.CreativeGroup;
import trueffect.truconnect.api.standalone.model.CreativeGroupCreative;
import trueffect.truconnect.api.standalone.model.CreativeGroupTarget;
import trueffect.truconnect.api.standalone.model.InsertionOrder;
import trueffect.truconnect.api.standalone.model.MediaBuy;
import trueffect.truconnect.api.standalone.model.Placement;
import trueffect.truconnect.api.standalone.model.Publisher;
import trueffect.truconnect.api.standalone.model.Schedule;
import trueffect.truconnect.api.standalone.model.ScheduleEntry;
import trueffect.truconnect.api.standalone.model.ScheduleSet;
import trueffect.truconnect.api.standalone.model.Site;
import trueffect.truconnect.api.standalone.model.SiteSection;
import trueffect.truconnect.api.standalone.model.Size;

public class LocalModelMapper {

    public static Campaign toLocalModel(trueffect.truconnect.api.commons.model.Campaign input) {
        Campaign output = new Campaign();

        output.setId(input.getId());
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

    public static CookieDomain toLocalModel(trueffect.truconnect.api.commons.model.CookieDomain input) {
        CookieDomain output = new CookieDomain();

        output.setId(input.getId());
        output.setDomain(input.getDomain());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setCookieDomainRootId(input.getCookieDomainRootId());

        return output;
    }

    public static Agency toLocalModel(trueffect.truconnect.api.commons.model.Agency input) {
        Agency output = new Agency();

        output.setId(input.getId());
        output.setName(input.getName());
        output.setDomainId(input.getDomainId());
        output.setAddress1(input.getAddress1());
        output.setAddress2(input.getAddress2());
        output.setCity(input.getCity());
        output.setState(input.getState());
        output.setZipCode(input.getZipCode());
        output.setCountry(input.getCountry());
        output.setPhoneNumber(input.getPhoneNumber());
        output.setFaxNumber(input.getFaxNumber());
        output.setUrl(input.getUrl());
        output.setEnableHtmlInjection(input.getEnableHtmlInjection());
        output.setNotes(input.getNotes());
        output.setIsActive(input.getIsActive());
        output.setIsActiveStr(input.getIsActiveStr());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setContactDefault(input.getContactDefault());

        return output;
    }

    public static Advertiser toLocalModel(trueffect.truconnect.api.commons.model.Advertiser input) {
        Advertiser output = new Advertiser();

        output.setId(input.getId());
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

    public static Brand toLocalModel(trueffect.truconnect.api.commons.model.Brand input) {
        Brand output = new Brand();

        output.setId(input.getId());
        output.setName(input.getName());
        output.setDescription(input.getDescription());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());
        output.setIsHidden(input.getIsHidden());

        return output;
    }

    public static Creative toLocalModel(trueffect.truconnect.api.commons.model.Creative input) {
        Creative output = new Creative();

        output.setId(input.getId());
        output.setOwnerCampaignId(input.getOwnerCampaignId());
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
            for (trueffect.truconnect.api.commons.model.Clickthrough clickthrough : input.getClickthroughs()) {
                clicks.add(toLocalModel(clickthrough));
            }
            output.setClickthroughs(clicks);
        }

        return output;
    }

    public static Clickthrough toLocalModel(trueffect.truconnect.api.commons.model.Clickthrough input) {
        Clickthrough output = new Clickthrough();
        output.setSequence(input.getSequence());
        output.setUrl(input.getUrl());
        return output;
    }

    public static CreativeGroup toLocalModel(trueffect.truconnect.api.commons.model.CreativeGroup input) {
        CreativeGroup output = new CreativeGroup();

        output.setId(input.getId());
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
        output.setCreativeGroupTargets(toLocalModel(input.getGeoTargets()));

        return output;
    }

    public static CreativeGroupTarget toLocalModel(List<trueffect.truconnect.api.commons.model.GeoTarget> input) {
        CreativeGroupTarget output = null;
        if(input != null) {
            output = new CreativeGroupTarget();
            List<Long> targetValueIds = new ArrayList<Long>(0);
            for (trueffect.truconnect.api.commons.model.GeoTarget geoTarget : input) {
                for(trueffect.truconnect.api.commons.model.CreativeGroupTarget creativeGroupTarget : geoTarget.getCreativeGroupTargets()) {
                    output.setCreatedDate(creativeGroupTarget.getCreatedDate());
                    output.setCreatedTpwsKey(creativeGroupTarget.getCreatedTpwsKey());
                    output.setLogicalDelete(creativeGroupTarget.getLogicalDelete());
                    output.setModifiedDate(creativeGroupTarget.getModifiedDate());
                    output.setModifiedTpwsKey(creativeGroupTarget.getModifiedTpwsKey());
                    output.setValueId(creativeGroupTarget.getValueId());
                    output.setTypeCode(creativeGroupTarget.getTypeCode());
                    targetValueIds.add(creativeGroupTarget.getValueId());
                }
            }
            output.setTargetValueId(targetValueIds);
        }
        return output;
    }

    public static CreativeGroupCreative toLocalModel(trueffect.truconnect.api.commons.model.CreativeGroupCreative input) {
        CreativeGroupCreative output = new CreativeGroupCreative();

        output.setCreativeGroupId(input.getCreativeGroupId());
        output.setCreativeId(input.getCreativeId());
        output.setDisplayOrder(input.getDisplayOrder());
        output.setDisplayQuantity(input.getDisplayQuantity());
        output.setCreatedTpwsKey(input.getCreatedTpwsKey());
        output.setModifiedTpwsKey(input.getModifiedTpwsKey());
        output.setLogicalDelete(input.getLogicalDelete());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }

    public static MediaBuy toLocalModel(trueffect.truconnect.api.commons.model.MediaBuy input) {
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

    public static InsertionOrder toLocalModel(trueffect.truconnect.api.commons.model.InsertionOrder input) {
        InsertionOrder output = new InsertionOrder();

        output.setId(input.getId());
        output.setPublisherId(input.getPublisherId());
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

    public static Placement toLocalModel(trueffect.truconnect.api.commons.model.Placement input) {
        Placement output = new Placement();

        output.setId(input.getId());
        output.setSiteId(input.getSiteId());
        output.setSiteSectionId(input.getSiteSectionId());
        output.setIoId(input.getIoId());
        output.setSizeId(input.getSizeId());
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

    public static Size toLocalModel(trueffect.truconnect.api.commons.model.Size input) {
        Size output = new Size();

        output.setId(input.getId());
        output.setWidth(input.getWidth());
        output.setHeight(input.getHeight());
        output.setLabel(input.getLabel());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }

    public static Site toLocalModel(trueffect.truconnect.api.commons.model.Site input) {
        Site output = new Site();

        output.setId(input.getId());
        output.setPublisherId(input.getPublisherId());
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

    public static SiteSection toLocalModel(trueffect.truconnect.api.commons.model.SiteSection input) {
        SiteSection output = new SiteSection();

        output.setId(input.getId());
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

    public static Publisher toLocalModel(trueffect.truconnect.api.commons.model.Publisher input) {
        Publisher output = new Publisher();

        output.setId(input.getId());
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

    public static ScheduleSet toLocalModel(trueffect.truconnect.api.commons.model.ScheduleSet input) {
        ScheduleSet output = new ScheduleSet();
        List<Schedule> schedules = new ArrayList<Schedule>();
        if(input.getSchedules() != null) {
            for (trueffect.truconnect.api.commons.model.Schedule schedule : input.getSchedules()) {
                schedules.add(toLocalModel(schedule));
            }
        }
        output.setSchedules(schedules);
        return output;
    }

    private static Schedule toLocalModel(trueffect.truconnect.api.commons.model.Schedule input) {
        Schedule output = new Schedule();

        output.setId(input.getId());
        output.setPlacementId(input.getPlacementId());
        output.setCreativeGroupId(input.getCreativeGroupId());
        output.setCreativeId(input.getCreativeId());
        List<ScheduleEntry> entries = new ArrayList<ScheduleEntry>();
        if(input.getEntries() != null) {
            for (trueffect.truconnect.api.commons.model.ScheduleEntry entry : input.getEntries()) {
                entries.add(toLocalModel(entry));
            }
        }
        output.setEntries(entries);

        return output;
    }

    private static ScheduleEntry toLocalModel(trueffect.truconnect.api.commons.model.ScheduleEntry input) {
        ScheduleEntry output = new ScheduleEntry();

        output.setId(input.getId());
        output.setStartDate(input.getStartDate());
        output.setEndDate(input.getEndDate());
        output.setTimeZone(input.getTimeZone());
        output.setWeight(input.getWeight());
        output.setSequence(input.getSequence());
        
        List<Clickthrough> clickthroughs = new ArrayList<Clickthrough>();
        if(input.getClickthroughs() != null) {
            for (trueffect.truconnect.api.commons.model.Clickthrough click : input.getClickthroughs()) {
                clickthroughs.add(toLocalModel(click));
            }
        }
        output.setClickthroughs(clickthroughs);
        
        output.setIsReleased(input.getIsReleased());
        output.setCreatedDate(input.getCreatedDate());
        output.setModifiedDate(input.getModifiedDate());

        return output;
    }
}

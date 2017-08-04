package trueffect.truconnect.api.tpasapi.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.tpasapi.model.Clickthrough;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.model.CreativeGroup;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupTarget;
import trueffect.truconnect.api.tpasapi.model.Placement;
import trueffect.truconnect.api.tpasapi.model.Publisher;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Schedule;
import trueffect.truconnect.api.tpasapi.model.ScheduledCreative;
import trueffect.truconnect.api.tpasapi.model.ScheduledPlacement;
import trueffect.truconnect.api.tpasapi.model.Site;
import trueffect.truconnect.api.tpasapi.model.Size;
import trueffect.truconnect.api.tpasapi.model.TargetType;
import trueffect.truconnect.api.tpasapi.model.TargetValue;

/**
 *
 * @author Rambert Rioja
 */
public class BeanFactory {

    public static CreativeGroup getCreativeGroup() {
        List<Long> targetValueIds = new ArrayList<Long>();
        targetValueIds.add(3L);
        targetValueIds.add(6L);
        targetValueIds.add(15L);
        targetValueIds.add(45L);
        CreativeGroupTarget target = new CreativeGroupTarget();
        target.setTargetValuesIds(targetValueIds);
        target.setTypeCode("geo_state");
                
        CreativeGroup cg = new CreativeGroup();
        cg.setId(123432L);
        cg.setCampaignId(541234L);
        cg.setName("Group Name 1");
        cg.setImpressionCap(0L);
        cg.setIsDefault(Boolean.FALSE);
        cg.setDoGeoTargeting(Boolean.TRUE);
        cg.setTargetValueIds(target);
        cg.setIsReleased(Boolean.TRUE);
        cg.setCreatedDate(new Date());
        cg.setModifiedDate(new Date());
        return cg;
    }

    public static RecordSet<CreativeGroup> getCreativeGroups() {
        List<CreativeGroup> result = new ArrayList<CreativeGroup>();
        CreativeGroup cg;
        for (int i = 0; i < 5; i++) {
            cg = getCreativeGroup();
            cg.setName(cg.getName() + " - " + i);
            result.add(cg);
        }
        return new RecordSet<CreativeGroup>(1, 20, result);
    }

    public static Creative getCreative() {

        Creative creative = new Creative();

        List<Clickthrough> clickthrough = new ArrayList<Clickthrough>();
        clickthrough.add(new Clickthrough(1L, "http://my.site.com/somewhere"));
        clickthrough.add(new Clickthrough(2L, "http://my.site.com/somewhere"));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2014, 0, 20, 14, 48, 28);
        Date created = cal.getTime();
        cal.set(2014, 2, 16, 12, 27, 54);
        Date modified = cal.getTime();

        creative.setId(123432L);
        creative.setCampaignId(541234L);
        creative.setType("zip");
        creative.setFilename("TestCreative-468x80");
        creative.setWidth(468L);
        creative.setHeight(60L);
        creative.setAlias("TestCreative-468x80");
        creative.setClickthroughs(clickthrough);
        creative.setIsExpandable(Boolean.FALSE);
        creative.setPurpose("This is a test");
        creative.setExtendedProperty1("value1");
        creative.setExtendedProperty2("value2");
        creative.setExtendedProperty3("value3");
        creative.setExtendedProperty4("value4");
        creative.setExtendedProperty5("value5");
        creative.setCreatedDate(created);
        creative.setModifiedDate(modified);

        return creative;
    }

    public static Creative getCreative(Long campaignId) {

        Creative creative = new Creative();

        List<Clickthrough> clickthrough = new ArrayList<Clickthrough>();
        clickthrough.add(new Clickthrough(1L, "http://my.site.com/somewhere"));
        clickthrough.add(new Clickthrough(2L, "http://my.site.com/somewhere"));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2014, 0, 20, 14, 48, 28);
        Date created = cal.getTime();
        cal.set(2014, 2, 16, 12, 27, 54);
        Date modified = cal.getTime();

        creative.setId(123432L);
        creative.setCampaignId(campaignId);
        creative.setType("zip");
        creative.setFilename("TestCreative-468x80");
        creative.setWidth(468L);
        creative.setHeight(60L);
        creative.setAlias("TestCreative-468x80");
        creative.setClickthroughs(clickthrough);
        creative.setIsExpandable(Boolean.FALSE);
        creative.setPurpose("This is a test");
        creative.setExtendedProperty1("value1");
        creative.setExtendedProperty2("value2");
        creative.setExtendedProperty3("value3");
        creative.setExtendedProperty4("value4");
        creative.setExtendedProperty5("value5");
        creative.setCreatedDate(created);
        creative.setModifiedDate(modified);

        return creative;
    }

    public static RecordSet<Creative> getCreatives() {
        List<Creative> result = new ArrayList<Creative>();
        Creative creative;
        for (int i = 0; i < 5; i++) {
            creative = getCreative();
            creative.setAlias(creative.getAlias() + " - " + i);
            result.add(creative);
        }
        return new RecordSet<Creative>(1, 20, result);
    }

    public static Schedule getSchedule() {
        Schedule schedule = new Schedule();
        schedule.setPlacements(getScheduledPlacements());
        schedule.setCreatives(getScheduledCreatives());
        return schedule;
    }

    public static Placement getPlacement() {
        Placement placement = new Placement();
        Long id = (long) (Math.random() * 1000);
        placement.setId(id);
        placement.setCampaignId(541234L);
        placement.setName("Trueffect-Home-468x60");
        placement.setSiteId(541443L);
        placement.setWidth(468L);
        placement.setHeight(60L);
        placement.setStatus("Accepted");
        placement.setIsSecure(Boolean.TRUE);
        placement.setMaxFileSize(2048L);
        placement.setExternalId("XYZ123");
        placement.setCreatedDate(new Date());
        placement.setModifiedDate(new Date());
        return placement;
    }
    public static Publisher getPublisher() {
        Publisher publisher = new Publisher();
        Long id = (long) (Math.random() * 1000);
        publisher.setId(id);
        publisher.setAddress1("Trueffect-Home-1");
        publisher.setAddress2("Trueffect-Home-2");
        publisher.setAgencyId(541234L);
        publisher.setAgencyNotes("This is a note");
        publisher.setCity("City");
        publisher.setCountry("Country");
        publisher.setCreatedDate(new Date());
        publisher.setModifiedDate(new Date());
        publisher.setName("Trueffect-Home");
        publisher.setPhoneNumber("1-222-222-2222 x22222");
        publisher.setState("State");
        publisher.setUrl("http://www.trueffect.com/");
        publisher.setZipCode("Zip Code");
        return publisher;
    }

    public static ScheduledPlacement getScheduledPlacement() {
        Long placementId = (long) (Math.random() * 1000);
        ScheduledPlacement sp = new ScheduledPlacement();
        sp.setPlacementId(placementId);
        return sp;
    }

    public static List<ScheduledPlacement> getScheduledPlacements() {
        List<ScheduledPlacement> sps = new ArrayList<ScheduledPlacement>();
        for (int i = 0; i < 4; i++) {
            sps.add(getScheduledPlacement());
        }
        return sps;
    }

    public static ScheduledCreative getScheduledCreative() {
        Long creativeId = (long) (Math.random() * 1000);
        ScheduledCreative sc = new ScheduledCreative();
        sc.setCreativeId(creativeId);
        sc.setWeight(100L);
        sc.setStartDate(new Date());
        sc.setEndDate(new Date());
        sc.setClickthroughs(getClickthroughs());
        return sc;
    }

    public static List<ScheduledCreative> getScheduledCreatives() {
        List<ScheduledCreative> scs = new ArrayList<ScheduledCreative>();
        for (int i = 0; i < 2; i++) {
            scs.add(getScheduledCreative());
        }
        return scs;
    }

    public static Clickthrough getClickthrough() {
        Clickthrough clickthrough = new Clickthrough();
        clickthrough.setUrl("http://www.trueffect.com/");
        clickthrough.setSequence(1L);
        return clickthrough;
    }

    public static List<Clickthrough> getClickthroughs() {
        List<Clickthrough> clicks = new ArrayList<Clickthrough>();
        for (int i = 0; i < 1; i++) {
            clicks.add(getClickthrough());
        }
        return clicks;
    }

    public static RecordSet<TargetType> getTargetTypes() {
        List<TargetType> result = new ArrayList<TargetType>();
        TargetType tt;
        String[] labels = {"State", "Country", "MetroArea", "ZipCode"};
        for (String label : labels) {
            tt = getTargetType();
            tt.setLabel(label);
            tt.setCode("geo_" + label.toLowerCase());
            result.add(tt);
        }
        return new RecordSet<TargetType>(1, 10, result);
    }

    public static TargetType getTargetType() {
        TargetType tt = new TargetType();
        Long ttId = (long) (Math.random() * 1000);
        tt.setId(ttId);
        return tt;
    }

    public static RecordSet<TargetValue> getTargetValues(String ttId) {
        List<TargetValue> result = new ArrayList<TargetValue>();
        TargetValue tl;
        String[] codes = {"AB", "AC", "AD", "AF"};
        for (String code : codes) {
            tl = getTargetValue();
            tl.setCode(code);
            tl.setLabel("label_" + code.toLowerCase());
            tl.setTypeCode(ttId);
            result.add(tl);
        }
        return new RecordSet<TargetValue>(1, 10, result);
    }

    public static TargetValue getTargetValue() {
        TargetValue tl = new TargetValue();
        Long tlId = (long) (Math.random() * 1000);
        tl.setId(tlId);
        return tl;
    }

    public static RecordSet<Size> getSizes() {
        List<Size> sizes = new ArrayList<Size>();
        Size size;
        for (int i = 0; i < 5; i++) {
            size = getSize();
            size.setId(Long.parseLong("10" + i));
            sizes.add(size);
        }
        return new RecordSet<Size>(1, 10, sizes);
    }

    public static Size getSize() {
        Size size = new Size();
        Long id = 100L;
        size.setId(id);
        size.setAgencyId(541234L);
        size.setWidth(468L);
        size.setHeight(60L);
        size.setLabel("468x60");
        size.setCreatedDate(new Date());
        size.setModifiedDate(new Date());
        return size;
    }

    public static RecordSet<Placement> getPlacements() {
        List<Placement> placements = new ArrayList<Placement>();
        for (int i = 0; i < 4; i++) {
            placements.add(getPlacement());
        }
        return new RecordSet<Placement>(1, 10, placements);
    }
    public static RecordSet<Publisher> getPublishers() {
        List<Publisher> publisher = new ArrayList<Publisher>();
        for (int i = 0; i < 4; i++) {
            publisher.add(getPublisher());
        }
        return new RecordSet<Publisher>(1, 10, publisher);
    }

    public static Site getSite() {
        Site site = new Site();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2014, 0, 20, 14, 48, 28);
        Date createdDate = cal.getTime();
        cal.set(2014, 2, 16, 12, 27, 54);
        Date modifiedDate = cal.getTime();

        site.setId(123432L);
        site.setPublisherId(541234L);
        site.setName("Trueffect");
        site.setUrl("http://www.trueffect.com");
        site.setPreferredTag("");
        site.setRichMedia(Boolean.TRUE);
        site.setAcceptsFlash(Boolean.TRUE);
        site.setClickTrack(Boolean.FALSE);
        site.setEncode(Boolean.TRUE);
        site.setTargetWin("blank");
        site.setAgencyNotes("This is a note");
        site.setPublisherNotes("This is a note");
        site.setCreatedDate(createdDate);
        site.setModifiedDate(modifiedDate);
        return site;
    }

    public static RecordSet<Site> getSites() {
        List<Site> sites = new ArrayList<Site>();
        for (int i = 0; i < 4; i++) {
            sites.add(getSite());
        }
        return new RecordSet<Site>(1, 10, sites);
    }

    public static Campaign getCampaign() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2014, 0, 20, 14, 48, 28);
        Date created = cal.getTime();
        cal.set(2014, 2, 16, 12, 27, 54);
        Date endDate = cal.getTime();
        cal.set(2014, 2, 16, 12, 27, 54);
        Date modified = cal.getTime();
        cal.set(2014, 2, 16, 12, 27, 54);
        Date start = cal.getTime();

        Campaign campaign = new Campaign();
        campaign.setAdvertiserId(Long.MIN_VALUE);
        campaign.setAgencyId(Long.MIN_VALUE);
        campaign.setAgencyNotes(null);
        campaign.setBrandId(Long.MIN_VALUE);
        campaign.setContactId(Long.MIN_VALUE);
        campaign.setCookieDomainId(Long.MIN_VALUE);
        campaign.setCreatedDate(created);
        campaign.setCreatedTpwsKey(null);
        campaign.setDescription(null);
        campaign.setDupName(null);
        campaign.setEndDate(endDate);
        campaign.setId(Long.MIN_VALUE);
        campaign.setIsHidden(null);
        campaign.setLogicalDelete(null);
        campaign.setMediaBuyId(Long.MIN_VALUE);
        campaign.setModifiedDate(modified);
        campaign.setModifiedTpwsKey(null);
        campaign.setName(null);
        campaign.setObjective(null);
        campaign.setResourcePathId(Long.MIN_VALUE);
        campaign.setStartDate(start);
        campaign.setStatusId(Long.MIN_VALUE);
        campaign.setTrafficToOwner(Long.MIN_VALUE);
        return campaign;

    }

    public static CreativeGroupCreatives getCreativeGroupCreatives(Long cgId) {
        CreativeGroupCreatives result = new CreativeGroupCreatives();
        result.setCreativeGroupId(cgId);
        List<Creative> creatives = new ArrayList<Creative>();
        for (int i = 0; i < 3; i++) {
            Creative item = getCreative();
            creatives.add(item);
        }
        result.setCreatives(creatives);

        return result;
    }
}

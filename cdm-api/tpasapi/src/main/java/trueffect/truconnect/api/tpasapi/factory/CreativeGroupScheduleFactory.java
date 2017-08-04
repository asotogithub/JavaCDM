package trueffect.truconnect.api.tpasapi.factory;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import trueffect.truconnect.api.tpasapi.model.Schedule;
import trueffect.truconnect.api.tpasapi.model.ScheduledCreative;
import trueffect.truconnect.api.tpasapi.model.ScheduledPlacement;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.ScheduleEntry;
import trueffect.truconnect.api.commons.model.ScheduleSet;

/**
 *
 * @author Rambert Rioja
 */
public class CreativeGroupScheduleFactory {

    public static ScheduleSet creativePublicObject(Schedule schedule) {
        CreativeGroupScheduleFactory factory = new CreativeGroupScheduleFactory();
        return factory.toPublicObject(schedule);
    }

    public static Schedule creativeTpasapiObject(ScheduleSet scheduleSet) {
        CreativeGroupScheduleFactory factory = new CreativeGroupScheduleFactory();
        return factory.toTpasapiObject(scheduleSet);
    }

    private ScheduleSet toPublicObject(Schedule schedule) {
        List<trueffect.truconnect.api.commons.model.Schedule> list;
        list = new ArrayList<trueffect.truconnect.api.commons.model.Schedule>();
        trueffect.truconnect.api.commons.model.Schedule schedule1;
        for (ScheduledPlacement pl : schedule.getPlacements()) {
            for (ScheduledCreative sc : schedule.getCreatives()) {
                schedule1 = new trueffect.truconnect.api.commons.model.Schedule();
                schedule1.setPlacementId(pl.getPlacementId());
                schedule1.setCreativeGroupId(schedule.getCreativeGroupId());
                schedule1.setCreativeId(sc.getCreativeId());
                schedule1.setEntries(createScheduleEntries(sc));
                list.add(schedule1);
            }
        }
        ScheduleSet scheduleSet = new ScheduleSet();
        scheduleSet.setSchedules(list);
        return scheduleSet;
    }

    private List<ScheduleEntry> createScheduleEntries(ScheduledCreative sc) {
        List<ScheduleEntry> entries = new ArrayList<ScheduleEntry>();
        ScheduleEntry entry = new ScheduleEntry();
        entry.setStartDate(sc.getStartDate());
        entry.setEndDate(sc.getEndDate());
        entry.setWeight(sc.getWeight());
        entry.setClickthroughs(toPublicClickthroughs(sc.getClickthroughs()));
        //Default values
        entry.setTimeZone("MST");
        entry.setSequence(0L);
        entries.add(entry);
        return entries;
    }

    private List<Clickthrough> toPublicClickthroughs(List<trueffect.truconnect.api.tpasapi.model.Clickthrough> clickthroughs) {
        List<Clickthrough> clicks = null;
        if (clickthroughs != null) {
            clicks = new ArrayList<Clickthrough>();
            for (trueffect.truconnect.api.tpasapi.model.Clickthrough click : clickthroughs) {
                Clickthrough click1 = new Clickthrough();
                click1.setSequence(click.getSequence());
                click1.setUrl(click.getUrl());
                clicks.add(click1);
            }
        }
        return clicks;
    }

    private Schedule toTpasapiObject(ScheduleSet scheduleSet) {
        List<ScheduledPlacement> placements = new ArrayList<ScheduledPlacement>();
        List<ScheduledCreative> creatives = new ArrayList<ScheduledCreative>();
        ScheduledPlacement scheduledPlacement;
        Schedule scheduleRes = new Schedule();
        if (scheduleSet.getSchedules() != null && !scheduleSet.getSchedules().isEmpty()) {
            scheduleRes.setCreativeGroupId(scheduleSet.getSchedules().get(0).getCreativeGroupId());
            for (trueffect.truconnect.api.commons.model.Schedule schedule : scheduleSet.getSchedules()) {
                scheduledPlacement = new ScheduledPlacement();
                scheduledPlacement.setPlacementId(schedule.getPlacementId());
                placements.add(scheduledPlacement);
                creatives.addAll(toTpasapiScheduledCreatives(schedule));
            }
        }
        scheduleRes.setPlacements(getUniquePlacemets(placements));
        scheduleRes.setCreatives(getUniqueCreatives(creatives));
        return scheduleRes;
    }
    private List<ScheduledPlacement> getUniquePlacemets(List<ScheduledPlacement> placements) {
        List<ScheduledPlacement> placementsValid = new ArrayList<ScheduledPlacement>();
        if (placements.size() <= 1) {
            placementsValid = placements;
        } else {
            Map<String, Long> countMap = new HashMap();
            for (ScheduledPlacement placement : placements) {
                Long count = countMap.get(placement.getPlacementId().toString());
                if (count == null) {
                    count = 0L;
                }
                countMap.put(placement.getPlacementId().toString(), (count.intValue() + 1L));
            }
            for (Map.Entry<String, Long> entry : countMap.entrySet()) {
                ScheduledPlacement aux = new ScheduledPlacement();
                aux.setPlacementId(new Long(entry.getKey()));
                placementsValid.add(aux);
            }
        }
        return placementsValid;
    }
    
    private List<ScheduledCreative> getUniqueCreatives(List<ScheduledCreative> creatives) {
        List<ScheduledCreative> creativesValid = new ArrayList<ScheduledCreative>();
        if (creatives.size() <= 1) {
            creativesValid = creatives;
        } else {
            for (ScheduledCreative creative : creatives) {
                if (!ifExists(creativesValid, creative)) {
                    creativesValid.add(creative);
                }
            }
        }
        return creativesValid;
    }

    private boolean ifExists(List<ScheduledCreative> creatives, ScheduledCreative cr) {
        boolean exists = false;
        for (ScheduledCreative creative : creatives) {
            if (creative.getCreativeId().equals(cr.getCreativeId())) {
                exists = true;
            }
        }
        return exists;
    }
    
    private List<ScheduledCreative> toTpasapiScheduledCreatives(trueffect.truconnect.api.commons.model.Schedule schedule) {
        List<ScheduledCreative> list = new ArrayList<ScheduledCreative>();
        ScheduledCreative scheduledCreative = new ScheduledCreative();
        for (ScheduleEntry entry : schedule.getEntries()) {
            scheduledCreative.setCreativeId(schedule.getCreativeId());
            scheduledCreative.setWeight(entry.getWeight());
            scheduledCreative.setStartDate(entry.getStartDate());
            scheduledCreative.setEndDate(entry.getEndDate());
            scheduledCreative.setSequence(entry.getSequence());
            scheduledCreative.setClickthroughs(toTpasapiClickthroughs(entry.getClickthroughs()));
            list.add(scheduledCreative);
        }
        return list;
    }

    private List<trueffect.truconnect.api.tpasapi.model.Clickthrough> toTpasapiClickthroughs(List<Clickthrough> clickthroughs) {
        if (clickthroughs != null) {
            List<trueffect.truconnect.api.tpasapi.model.Clickthrough> clicks;
            clicks = new ArrayList<trueffect.truconnect.api.tpasapi.model.Clickthrough>();
            trueffect.truconnect.api.tpasapi.model.Clickthrough click1;
            for (Clickthrough click : clickthroughs) {
                click1 = new trueffect.truconnect.api.tpasapi.model.Clickthrough();
                click1.setSequence(click.getSequence());
                click1.setUrl(click.getUrl());
                clicks.add(click1);
            }
            return clicks;
        }
        return null;
    }
}

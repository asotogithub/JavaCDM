package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;
import trueffect.truconnect.api.tpasapi.factory.CreativeGroupScheduleFactory;
import trueffect.truconnect.api.tpasapi.model.Schedule;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.ScheduleSet;

/**
 *
 * @author Rambert Rioja
 */
public class CreativeGroupScheduleProxy extends BaseProxy<trueffect.truconnect.api.commons.model.ScheduleSet> {

    public CreativeGroupScheduleProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.ScheduleSet.class, headers);
        path("Schedules");
    }

    public Schedule updateSchedule(Long id, Schedule schedule) throws ProxyException {
        trueffect.truconnect.api.commons.model.ScheduleSet scheduleSet;
        scheduleSet = CreativeGroupScheduleFactory.creativePublicObject(schedule);
        scheduleSet = put(scheduleSet);
        schedule = CreativeGroupScheduleFactory.creativeTpasapiObject(scheduleSet);
        return schedule;
    }
    public Schedule getSchedule(Long id) throws ProxyException {
        ScheduleSet scheduleSet = get();
        Schedule result = CreativeGroupScheduleFactory.creativeTpasapiObject(scheduleSet);
        return result;
    }
}

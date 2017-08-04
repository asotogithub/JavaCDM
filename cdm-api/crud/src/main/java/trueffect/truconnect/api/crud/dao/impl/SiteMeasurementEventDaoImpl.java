package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.dto.SmEventDTO;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.search.Searcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by richard.jaldin on 6/9/2015.
 */
public class SiteMeasurementEventDaoImpl extends AbstractGenericDao implements SiteMeasurementEventDao {

    private static final String STATEMENT_GET_SITE_MEASUREMENT_PING_EVENT_LIST = "SmEvent.getSmPingEventsByEvent";
    private static final String STATEMENT_GET_SITE_MEASUREMENT_EVENT = "SmEvent.getSmEvent";
    private static final String STATEMENT_INSERT_SITE_MEASUREMENT_EVENT_PING = "SmEvent.insertSmEventPing";
    private static final String STATEMENT_UPDATE_SITE_MEASUREMENT_EVENT = "SmEvent.updateSmEvent";
    private static final Log LOGGER = LogFactory.getLog(SiteMeasurementEventDaoImpl.class);
    private static final String STATEMENT_IS_SM_EVENT_NAME_EXISTS = "SmEvent.isSmEventNameExists";

    /**
     * {@inheritDoc}
     */
    public SiteMeasurementEventDaoImpl(PersistenceContext persistenceContext, AccessControl accessControl) {
        super(persistenceContext, accessControl);
    }

    @Override
    public SmEvent get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle("SmEvent.getSmEvent", id, session, SmEvent.class);
    }

    @Override
    public void create(SmEvent smEvent, SqlSession session) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("id", String.valueOf(smEvent.getId()));
        parameter.put("smGroupId", String.valueOf(smEvent.getSmGroupId()));
        parameter.put("eventName", smEvent.getEventName());
        parameter.put("location", smEvent.getLocation());
        parameter.put("eventType", String.valueOf(smEvent.getEventType()));
        parameter.put("smEventType", String.valueOf(smEvent.getSmEventType()));
        parameter.put("tpwsKey", String.valueOf(smEvent.getCreatedTpwsKey()));

        getPersistenceContext().execute("SmEvent.insertSmEvent", parameter, session);
    }

    @Override
    public void update(SmEvent smEvent, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", smEvent.getId());
        parameter.put("location", smEvent.getLocation());
        parameter.put("smEventType", smEvent.getSmEventType());
        parameter.put("tpwsKey", smEvent.getCreatedTpwsKey());

        getPersistenceContext().execute(STATEMENT_UPDATE_SITE_MEASUREMENT_EVENT, parameter, session);
    }

    @Override
    public RecordSet<SmEventDTO> getSmEventsBySiteMeasurement(Long siteMeasurementId, SqlSession session) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("siteMeasurementId", String.valueOf(siteMeasurementId));

        List<SmEventDTO> aux = getPersistenceContext().selectMultiple(
                "SmEvent.getSmEventsBySiteMeasurement", parameter, session);

        return new RecordSet<SmEventDTO>(0, aux.size(), aux.size(), aux);
    }

    @Override
    public SmEvent getSmPingsByEvents(Long smEventId, SqlSession session) {
        HashMap<String, Long> parameter = new HashMap<>();
        parameter.put("smEventId", smEventId);

        SmEvent event =  getPersistenceContext().selectSingle(STATEMENT_GET_SITE_MEASUREMENT_EVENT,
                smEventId, session, SmEvent.class);

        List<SmPingEventDTO> aux = getPersistenceContext().selectMultiple(
                STATEMENT_GET_SITE_MEASUREMENT_PING_EVENT_LIST, parameter, session);
        event.setPingEvents(aux);

        return event;
    }

    @Override
    public void createSmPingByEvent(SmPingEventDTO record, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pingId", record.getPingId());
        parameter.put("smEventId", record.getId());
        parameter.put("siteId", record.getSiteId());
        parameter.put("pingContent", record.getPingContent());
        parameter.put("pingDescription", record.getDescription());
        parameter.put("pingType", record.getPingType());
        parameter.put("pingTagType", record.getPingTagType());
        parameter.put("tpwsKey", record.getCreatedTpwsKey());

        getPersistenceContext().execute(STATEMENT_INSERT_SITE_MEASUREMENT_EVENT_PING, parameter, session);
    }



    @Override
    public Boolean isSmEventNameExist(Long groupId,  String name, SqlSession session) {
        Long result =0L;
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("groupId", groupId.toString());
        parameter.put("name", name);
        result = getPersistenceContext().selectSingle(STATEMENT_IS_SM_EVENT_NAME_EXISTS, parameter, session, Long.class);
        return result == 0L ? false : true;
    }
}

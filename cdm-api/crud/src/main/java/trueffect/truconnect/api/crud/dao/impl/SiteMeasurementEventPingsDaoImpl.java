package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventPingsDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.SumAccumulatorImpl;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jesus.nunez on 8/12/2016.
 */
public class SiteMeasurementEventPingsDaoImpl extends AbstractGenericDao implements SiteMeasurementEventPingsDao {

    private static final String STATEMENT_DELETE_PINGS_EVENTS_BY_IDS = "SmPing.deletePings";
    private static final String STATEMENT_INSERT_SITE_MEASUREMENT_EVENT_PING = "SmPing.insertSmEventPing";
    private static final String STATEMENT_SELECT_SITE_MEASUREMENT_EVENT_PING = "SmPing.getSmPingEvent";
    private static final String STATEMENT_UPDATE_SITE_MEASUREMENT_EVENT_PING = "SmPing.updateSmEventPing";

    /**
     * Creates a new {@code SiteMeasurementEventPingsDaoImpl} with a specific {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this DAO
     */
    public SiteMeasurementEventPingsDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public Integer deletePingEvent(List<Long> pingIds, String tpwsKey ,final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tpwsKey", tpwsKey);
        int result = 0;
        Accumulator<Integer> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Integer>("ids", pingIds, resultAccumulator, params) {
            @Override
            public Integer execute(Object parameters) {
                return getPersistenceContext()
                        .update(STATEMENT_DELETE_PINGS_EVENTS_BY_IDS,
                                parameters,
                                session);
            }
        }.getResults();
    }

    @Override
    public void create(SmPingEventDTO dto, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();

        parameter.put("pingId", dto.getPingId());
        parameter.put("smEventId", dto.getId());
        parameter.put("siteId", dto.getSiteId());
        parameter.put("pingContent", dto.getPingContent());
        parameter.put("pingDescription", dto.getDescription());
        parameter.put("pingType", dto.getPingType());
        parameter.put("pingTagType", dto.getPingTagType());
        parameter.put("tpwsKey", dto.getCreatedTpwsKey());

        getPersistenceContext().execute(STATEMENT_INSERT_SITE_MEASUREMENT_EVENT_PING, parameter, session);
    }

    @Override
    public SmPingEventDTO get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(
                STATEMENT_SELECT_SITE_MEASUREMENT_EVENT_PING, id, session, SmPingEventDTO.class);
    }

    @Override
    public void update(SmPingEventDTO dto, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();

        parameter.put("pingId", dto.getPingId());
        parameter.put("pingContent", dto.getPingContent());
        parameter.put("pingDescription", dto.getDescription());
        parameter.put("pingType", dto.getPingType());
        parameter.put("pingTagType", dto.getPingTagType());
        parameter.put("tpwsKey", dto.getModifiedTpwsKey());

        getPersistenceContext().execute(STATEMENT_UPDATE_SITE_MEASUREMENT_EVENT_PING, parameter, session);
    }


}

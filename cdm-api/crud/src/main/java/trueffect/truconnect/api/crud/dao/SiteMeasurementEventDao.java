package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.dto.SmEventDTO;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 * Created by richard.jaldin on 6/9/2015.
 */
public interface SiteMeasurementEventDao extends GenericDao {

    /**
     * Gets the SmEvent by its Id
     * @param id The Id of the {@code SmEvent}
     * @param session The current persistence session
     * @return
     */
    SmEvent get(Long id, SqlSession session);

    /**
     * Creates a given {@code SmEvent}
     * @param smEvent The given {@code SmEvent}
     * @param session The current persistence session
     */
    void create(SmEvent smEvent, SqlSession session);

    /**
     * Updates a given {@code SmEvent}
     * @param smEvent The given {@code SmEvent}
     * @param session The current persistence session
     */
    void update(SmEvent smEvent, SqlSession session);

    /**
     * Gets all {@code SmEventDTO} that belongs to the {@code SiteMeasurement}
     * @param siteMeasurementId Id of de {@code SiteMeasurement}
     * @param session The current persistence session
     * @return A {@code RecordSet} that contains the {@code SmEventDTO}
     */
    RecordSet<SmEventDTO> getSmEventsBySiteMeasurement(Long siteMeasurementId, SqlSession session);

    void createSmPingByEvent(SmPingEventDTO record, SqlSession session);

    SmEvent getSmPingsByEvents(Long smEventId, SqlSession session);

    Boolean isSmEventNameExist(Long groupId,  String name, SqlSession session);
}

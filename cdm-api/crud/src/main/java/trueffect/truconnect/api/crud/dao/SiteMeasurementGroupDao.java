package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 * Created by richard.jaldin on 6/9/2015.
 */
public interface SiteMeasurementGroupDao extends GenericDao {

    /**
     * Gets the SmGroup by its Id
     * @param id The Id of the {@code SmGroup}
     * @param session The current persistence session
     * @return
     */
    SmGroup get(Long id, SqlSession session);

    /**
     * Updates a given {@code SmGroup}
     * @param smGroup The given {@code SmGroup}
     * @param session The current persistence session
     */
    void create(SmGroup smGroup, SqlSession session);

    Boolean isSmGroupNametExist(Long smId, String name, SqlSession session);

    RecordSet<SmGroup> getSmGroupsBySiteMeasurement(Long siteMeasurementId, SqlSession session);
}

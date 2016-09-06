package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 * Method definition for Site Measurement related to persistence
 * @author marleny.patsi
 */
public interface SiteMeasurementDao extends GenericDao {

    SiteMeasurementDTO get(Long id, SqlSession session);

    RecordSet<SiteMeasurementDTO> get(SearchCriteria criteria, OauthKey key, SqlSession session);

    /**
     * Updates a given {@code SiteMeasurement} DTO
     * @param siteMeasurement The given {@code SiteMeasurement} DTO
     * @param session The current persistence session
     */
    SiteMeasurementDTO create(SiteMeasurementDTO siteMeasurement, SqlSession session);

    /**
     * Updates a given {@code SiteMeasurement} DTO
     * @param dto The given {@code SiteMeasurement} DTO
     * @param session The current persistence session
     * @return The number of records affected
     */
    int update(SiteMeasurementDTO dto, SqlSession session);

    //TODO: method definitions created according to what developed, change with the new code
    SiteMeasurementDTO delete(Long id, OauthKey key) throws Exception ;

    boolean doesSiteMeasurementExists(String name, Long smId, Long brandId, SqlSession session);
}

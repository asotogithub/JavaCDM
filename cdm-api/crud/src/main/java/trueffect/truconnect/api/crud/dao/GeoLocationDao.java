package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.enums.LocationType;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Access object for getting lists of geolocations from the database.
 */
public interface GeoLocationDao extends GenericDao {
    /**
     * Get all of the locations for a specific type.
     *
     * @param type - type of the locations to get.
     * @param session - active session to use for connections.
     * @return - List of all locations for the type.
     * @throws Exception
     */
    List<GeoLocation> getLocations(LocationType type, SqlSession session);
}

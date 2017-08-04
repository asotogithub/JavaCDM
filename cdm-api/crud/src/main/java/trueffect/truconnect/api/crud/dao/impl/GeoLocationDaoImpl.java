package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.enums.LocationType;
import trueffect.truconnect.api.crud.dao.GeoLocationDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * MyBatis implementation for GeoLocation.
 */
public class GeoLocationDaoImpl extends AbstractGenericDao implements GeoLocationDao {

    private static final Log LOGGER = LogFactory.getLog(GeoLocationDaoImpl.class);

    public GeoLocationDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GeoLocation> getLocations(LocationType type, SqlSession session) {
        List<GeoLocation> geoLocations;
        try {
            geoLocations = (List<GeoLocation>) getPersistenceContext().selectList("GeoLocation.getLocations", type.getCode(), session);
        } catch (Exception e) {
            throw new SystemException(e, BusinessCode.INTERNAL_ERROR);
        }
        return geoLocations;
    }
}

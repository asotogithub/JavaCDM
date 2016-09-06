package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.enums.LocationType;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.GeoLocationDao;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for getting available geo locations for targeting.
 */
public class GeoLocationManager extends AbstractGenericManager {

	private GeoLocationDao geoLocationDao;

	public GeoLocationManager(GeoLocationDao geoLocationDao,
			AccessControl accessControl) {
		super(accessControl);
		this.geoLocationDao = geoLocationDao;
	}

	public RecordSet<GeoLocation> getCountries() {
		return getLocations(LocationType.COUNTRY);
	}
	
	public RecordSet<GeoLocation> getStates() {
		return getLocations(LocationType.STATE);
	}
	
	public RecordSet<GeoLocation> getDMAs() {
		return getLocations(LocationType.DMA);
	}
	
	public RecordSet<GeoLocation> getZipCodes() {
		return getLocations(LocationType.ZIP);
	}
	
	private RecordSet<GeoLocation> getLocations(LocationType locationType) {
		List<GeoLocation> result = new ArrayList<>(0);

		// obtain session
		SqlSession session = geoLocationDao.openSession();
		try {
            Map<LocationType, Map<String, GeoLocation>> parents = new HashMap<>();
            List<LocationType> hierarchy = LocationType.getHierarchy(locationType);
            for(LocationType type : hierarchy) {
                result = getLocations(type, parents, session);
                Map<String, GeoLocation> codedMap = codeMap(result);
                parents.put(type, codedMap);
            }
		} finally {
			geoLocationDao.close(session);
		}

		return new RecordSet<>(result);
	}

    protected List<GeoLocation> getLocations(LocationType type, Map<LocationType, Map<String, GeoLocation>> parents, SqlSession session) {
        List<GeoLocation> locations = geoLocationDao.getLocations(type, session);
        locations = setLabels(type, locations, parents);
        return locations;
    }

    protected static List<GeoLocation> setLabels(LocationType type, List<GeoLocation> locations, Map<LocationType, Map<String, GeoLocation>> parents) {
        List<GeoLocation> result = new ArrayList<>();
        for(GeoLocation location : locations) {
            location.setLocationType(type);
            setParentFields(type, location, parents);
            switch (type) {
                case COUNTRY:
                    location.setCountryLabel(location.getLabel());
                    break;
                case STATE:
                    location.setStateLabel(location.getLabel());
                    break;
                case DMA:
                    location.setDmaLabel(location.getLabel());
                    break;
                case ZIP:
                    location.setZipLabel(location.getLabel());
                    break;
            }
            result.add(location);
        }
        return result;
    }

    protected static void setParentFields(LocationType type, GeoLocation location, Map<LocationType, Map<String, GeoLocation>> parents) {
        if(parents != null) {
            GeoLocation parent = getParent(type, location, parents);
            if(parent != null) {
                location.setCountryLabel(parent.getCountryLabel());
                location.setStateLabel(parent.getStateLabel());
                location.setDmaLabel(parent.getDmaLabel());
                location.setZipLabel(parent.getZipLabel());
            }
        }
    }

    protected static GeoLocation getParent(LocationType locationType, GeoLocation location, Map<LocationType, Map<String, GeoLocation>> parents) {
        GeoLocation parent = null;

        LocationType ancestor = LocationType.getAncestor(locationType);
        if(ancestor == null) {
            return null;
        }
        Map<String, GeoLocation> ancestorMap = parents.get(ancestor);
        if(ancestorMap != null) {
            parent = ancestorMap.get(parentCode(location));
        }

        if(parent == null) {
            parent = getParent(ancestor, location, parents);
        }

        return parent;
    }

    protected static String parentCode(GeoLocation location) {
        String result = location.getParentCode();

        if(result == null) {
            switch (location.getLocationType()) {
                case ZIP:
                    String[] parts = location.getLabel().split(" - ");
                    result = parts.length < 3 ? null : parts[2];
            }
        }

        return result;
    }

    protected static Map<String, GeoLocation> codeMap(List<GeoLocation> locations) {
        Map<String, GeoLocation> locationsMap = new HashMap<>();
        for(GeoLocation location : locations) {
            locationsMap.put(location.getCode(), location);
        }
        return locationsMap;
    }
}

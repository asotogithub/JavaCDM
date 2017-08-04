package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoExtended;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public class PackagePlacementDaoImpl extends AbstractGenericDao implements PackagePlacementDaoExtended {

    private static final String STATEMENT_GET_PACKAGE_PLACEMENT_ASSOCIATIONS = "Package.getPackagePlacementAsso";
    private static final String STATEMENT_INSERT_PACKAGE_PLACEMENT_ASSOCIATIONS = "Package.insertPackagePlacementAsso";
    private static final String STATEMENT_DELETE_PACKAGE_PLACEMENT_ASSOCIATIONS = "Package.deletePackagePlacementAsso";

    public PackagePlacementDaoImpl(PersistenceContext context) {
        super(context);
    }

    @Override
    public List<PackagePlacement> getPackagePlacements(List<Long> ids, Long packageId, List<Long> placementIds, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("ids", ids);
        parameter.put("packageId", packageId);
        parameter.put("placementIds", placementIds);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PACKAGE_PLACEMENT_ASSOCIATIONS, parameter, session);
    }

    @Override
    public List<PackagePlacement> create(List<PackagePlacement> packagePlacements, OauthKey key, SqlSession session) {
        List<Long> ids = new ArrayList<>();
        Long packageId = null;
        for (PackagePlacement packagePlacement : packagePlacements) {
            Long packagePlacementId = getNextId(session);
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("packagePlacementId", packagePlacementId);
            parameter.put("packageId", packagePlacement.getPackageId());
            parameter.put("placementId", packagePlacement.getPlacementId());
            parameter.put("createdTpwsKey", key.getTpws());
            getPersistenceContext().execute(STATEMENT_INSERT_PACKAGE_PLACEMENT_ASSOCIATIONS, parameter, session);
            ids.add(packagePlacementId);
            packageId = packageId == null ? packagePlacement.getPackageId() : packageId;
        }
        return this.getPackagePlacements(ids, packageId, null, session);
    }

    @Override
    public void delete(List<PackagePlacement> packagePlacements, OauthKey key, SqlSession session) {
        for (PackagePlacement packagePlacement : packagePlacements) {
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("packagePlacementId", packagePlacement.getId());
            parameter.put("packageId", packagePlacement.getPackageId());
            parameter.put("placementId", packagePlacement.getPlacementId());
            parameter.put("createdTpwsKey", key.getTpws());
            getPersistenceContext().execute(STATEMENT_DELETE_PACKAGE_PLACEMENT_ASSOCIATIONS, parameter, session);
        }
    }

}

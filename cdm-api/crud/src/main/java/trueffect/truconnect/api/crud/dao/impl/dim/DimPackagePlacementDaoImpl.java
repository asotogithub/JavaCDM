package trueffect.truconnect.api.crud.dao.impl.dim;

import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.PackagePlacementDaoBase;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public class DimPackagePlacementDaoImpl extends AbstractGenericDao implements PackagePlacementDaoBase {

    private static final String STATEMENT_UPDATE_PACKAGE_PLACEMENT = "Package.packagePlacement";

    public DimPackagePlacementDaoImpl(PersistenceContext context) {
        super(context);
    }

    @Override
    public List<PackagePlacement> create(List<PackagePlacement> packagePlacements, OauthKey key, SqlSession session) {
        for (PackagePlacement packagePlacement : packagePlacements) {
            HashMap<String, Object> parameter = setParameters(packagePlacement, DimCostDetailDaoImpl.DIM_ACTION.INSERT);
            getPersistenceContext().execute(STATEMENT_UPDATE_PACKAGE_PLACEMENT, parameter, session);
            if (!parameter.get("error").equals("SUCCESS")) {
                Exception e = new Exception(parameter.get("error").toString());
                throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);
            }
        }
        return packagePlacements;
    }

    @Override
    public void delete(List<PackagePlacement> packagePlacements, OauthKey key, SqlSession session) {
        for (PackagePlacement packagePlacement : packagePlacements) {
            packagePlacement.setLogicalDelete("Y");
            HashMap<String, Object> parameter = setParameters(packagePlacement, DimCostDetailDaoImpl.DIM_ACTION.UPDATE);
            getPersistenceContext().execute(STATEMENT_UPDATE_PACKAGE_PLACEMENT, parameter, session);
            if (!parameter.get("error").equals("SUCCESS")) {
                Exception e = new Exception(parameter.get("error").toString());
                throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);
            }
        }
    }

    private HashMap<String, Object> setParameters(PackagePlacement packagePlacement, DimCostDetailDaoImpl.DIM_ACTION action) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("action", action.toString());
        parameter.put("packagePlacementId", packagePlacement.getId());
        parameter.put("packageId", packagePlacement.getPackageId());
        parameter.put("placementId", packagePlacement.getPlacementId());
        parameter.put("logicalDelete", packagePlacement.getLogicalDelete());
        parameter.put("created", packagePlacement.getCreated());
        parameter.put("createdTpwsKey", packagePlacement.getCreatedTpwsKey());
        parameter.put("modified", packagePlacement.getModified());
        parameter.put("modifiedTpwsKey", packagePlacement.getModifiedTpwsKey());
        return parameter;
    }
}

package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.dto.dim.DimPlacementCostDetailDTO;
import trueffect.truconnect.api.commons.model.dto.dim.DimPackageDTO;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AgencyDao;
import trueffect.truconnect.api.crud.dao.DimCostDetailDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Gustavo Claure
 */
public class DimCostDetailManager extends AbstractGenericManager {

    private final DimCostDetailDao dimCostDetailDao;

    private final AgencyDao agencyDao;
    private final PackageDaoBase dimPackageDao;

    public DimCostDetailManager(PackageDaoBase dimPackageDao, DimCostDetailDao dimCostDetailDao,
            AgencyDao agencyDao, AccessControl accessControl) {
        super(accessControl);
        this.dimPackageDao = dimPackageDao;

        this.dimCostDetailDao = dimCostDetailDao;
        this.agencyDao = agencyDao;
    }

    public Either<Error, DimPackageDTO> getPackage(Long packageId) {
        if (packageId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Package Id"));
        }

        SqlSession session = dimCostDetailDao.openSession();
        DimPackageDTO result;
        try {
            result = (DimPackageDTO) dimPackageDao.get(packageId, session);
            if (result != null) {
                result.setDimPackagePlacements(dimCostDetailDao.getPackagePlacement(packageId, session));
                result.setDimPackageCostDetail(dimCostDetailDao.getDimPackageCostDetail(packageId, session));
                return Either.success(result);
            } else {
                return Either.error(new Error(ResourceBundleUtil.getString("global.error.recordNotFound", packageId), BusinessCode.NOT_FOUND));
            }
        } finally {
            dimCostDetailDao.close(session);
        }
    }

    public Either<Error, DimPlacementCostDetailDTO> getDimPlacementCostDetails(Long placementId) {
        if (placementId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Placement Id"));
        }

        SqlSession session = dimCostDetailDao.openSession();

        DimPlacementCostDetailDTO result = new DimPlacementCostDetailDTO();
        try {
            result.setDimPlacementCosts(dimCostDetailDao.getDimPlacementCost(placementId, session));
            result.setDimPlacementCostDetails(dimCostDetailDao.getDimPlacementCostDetail(placementId, session));
            result.setDimProductBuyCosts(dimCostDetailDao.getDimProductBuyCost(placementId, session));
        } finally {
            dimCostDetailDao.close(session);
        }
        return Either.success(result);
    }

    public Either<Error, Void> dimHardRemoveCostDetails(Long agencyId, OauthKey key, SqlSession tfaSession) {
        //null validations
        if (agencyId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Agency Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (tfaSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "TFA Session"));
        }

        //obtain session
        SqlSession session = dimCostDetailDao.openSession();
        try {
            if (!userValidFor(AccessStatement.AGENCY, agencyId, key.getUserId(), tfaSession)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
            Set<Long> packageIds = new HashSet<>(agencyDao.getAllPackageIds(agencyId, tfaSession));
            Set<Long> placementsIds = new HashSet<>(agencyDao.getAllPlacementIds(agencyId, tfaSession));
            dimCostDetailDao.dimHardRemoveCostDetails(packageIds, placementsIds, session);
        } catch (Exception e) {
            dimCostDetailDao.rollback(session);
            return Either.error(new Error(ResourceBundleUtil.getString("BusinessCode.INTERNAL_ERROR"),
                    BusinessCode.INTERNAL_ERROR));
        } finally {
            dimCostDetailDao.commit(session);
            dimCostDetailDao.close(session);
        }
        return Either.success(null);
    }
}

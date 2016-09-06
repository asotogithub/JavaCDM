package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.commons.model.dim.DimPackageCostDetail;
import trueffect.truconnect.api.commons.model.dim.DimPlacementCost;
import trueffect.truconnect.api.commons.model.dim.DimPlacementCostDetail;
import trueffect.truconnect.api.commons.model.dim.DimProductBuyCost;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Set;

public interface DimCostDetailDao extends GenericDao {

    List<PackagePlacement> getPackagePlacement(Long packageId, SqlSession session);

    List<DimPackageCostDetail> getDimPackageCostDetail(Long packageId, SqlSession session);

    List<DimPlacementCost> getDimPlacementCost(Long placementId, SqlSession session);

    List<DimPlacementCostDetail> getDimPlacementCostDetail(Long placementId, SqlSession session);

    List<DimProductBuyCost> getDimProductBuyCost(Long productBuyId, SqlSession session);

    void dimHardRemoveCostDetails(Set<Long> packageIds, Set<Long> placementsIds, SqlSession session);

}

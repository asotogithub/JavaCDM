package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.PackagePlacement;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public interface PackagePlacementDaoExtended extends PackagePlacementDaoBase {

    List<PackagePlacement> getPackagePlacements(List<Long> ids, Long packageId, List<Long> placementIds, SqlSession session);
}

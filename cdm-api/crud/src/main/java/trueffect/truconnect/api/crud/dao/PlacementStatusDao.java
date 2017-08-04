package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.PlacementStatus;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author Richard Jaldin
 */
public interface PlacementStatusDao extends GenericDao {

    PlacementStatus create(PlacementStatus status, SqlSession session);

    PlacementStatus get(Long placementId, SqlSession session);
}

package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public interface PackagePlacementDaoBase extends GenericDao {

    List<PackagePlacement> create(List<PackagePlacement> packagePlacements, OauthKey key, SqlSession session);

    void delete(List<PackagePlacement> packagePlacements, OauthKey key, SqlSession session);

}

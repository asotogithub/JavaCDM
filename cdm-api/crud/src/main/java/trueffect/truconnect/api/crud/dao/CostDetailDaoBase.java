package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author marleny.patsi
 */
public interface CostDetailDaoBase extends GenericDao {

    CostDetail create(CostDetail costDetail, SqlSession session);

    CostDetail update(CostDetail costDetail, SqlSession session);

    void remove(CostDetail costDetail, String key, SqlSession session);
}

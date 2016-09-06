package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.InsertionOrderStatus;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author marleny.patsi
 */
public interface InsertionOrderStatusDao extends GenericDao {

    InsertionOrderStatus get(Long ioId, SqlSession session);

    void create(InsertionOrderStatus ioStatus, SqlSession session);
}

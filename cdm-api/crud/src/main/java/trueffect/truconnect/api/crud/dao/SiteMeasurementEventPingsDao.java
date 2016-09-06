package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by jesus.nunez on 8/12/2016.
 */
public interface SiteMeasurementEventPingsDao extends GenericDao {

    Integer deletePingEvent(List<Long> pingIds, String tpwsKey, SqlSession session);
    void create(SmPingEventDTO dto, SqlSession session);
    void update(SmPingEventDTO dto, SqlSession session);
    SmPingEventDTO get(Long id, SqlSession session);
}

package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by richard.jaldin on 10/8/2015.
 */
public interface CostDetailDaoExtended extends CostDetailDaoBase {

    List<CostDetail> getAll(Long foreignId, SqlSession session);

    CostDetail get(Long id, SqlSession session);

    List<MediaRawDataView> getCostsForExport(List<Long> ids, final SqlSession session);
}

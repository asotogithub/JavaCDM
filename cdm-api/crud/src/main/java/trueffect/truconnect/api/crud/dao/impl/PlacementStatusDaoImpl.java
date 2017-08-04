package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.PlacementStatus;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Richard Jaldin
 */
public class PlacementStatusDaoImpl extends AbstractGenericDao implements PlacementStatusDao {

    private static final String STATEMENT_SAVE_PLACEMENT_STATUS = "Placement.savePlacementStatus";
    private static final String STATEMENT_GET_CURRENT_PLACEMENT_STATUS = "Placement.getCurrentPlacementStatus";

    public PlacementStatusDaoImpl(PersistenceContext context) {
        super(context);
    }

    @Override
    public PlacementStatus create(PlacementStatus status, SqlSession session) {
        PlacementStatus result;
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("placementId", status.getPlacementId());
        parameter.put("statusId", status.getStatusId());
        parameter.put("contactId", status.getContactId());
        parameter.put("createdTpwsKey", status.getCreatedTpwsKey());
        getPersistenceContext().execute(STATEMENT_SAVE_PLACEMENT_STATUS, parameter, session);
        result = get(status.getPlacementId(), session);
        return result;
    }

    @Override
    public PlacementStatus get(Long placementId, SqlSession session) {
        PlacementStatus result = null;
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("placementId", placementId);
        getPersistenceContext().execute(STATEMENT_GET_CURRENT_PLACEMENT_STATUS, parameter, session);
        if (parameter.get("refCursor") != null) {
            List<PlacementStatus> aux = (List<PlacementStatus>) parameter.get("refCursor");
            result = aux.get(0);
        }
        return result;
    }
}

package trueffect.truconnect.api.crud.dao.impl;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import trueffect.truconnect.api.crud.service.CreativeManager;

import java.util.HashMap;
import java.util.Map;

/**
 * MyBatis data handler for managing which Cretive Types hold a set of Placement Ids
 * @author  marcelo.heredia.
 */
public class CreativeClassificationByPlacementHandler implements ResultHandler{

    Map<Long,CreativeManager.CreativeGlobalClassification> map =
            new HashMap<>();
    @Override
    public void handleResult(ResultContext resultContext) {
        Map<String,Object> m = (Map<String,Object>)resultContext.getResultObject();
        String creativeType = (String) getFromMap(m, "CLASSIFICATION");
        map.put((Long) getFromMap(m, "PLACEMENT_ID"),
                CreativeManager.CreativeGlobalClassification.valueOf(creativeType));
    }

    private Object getFromMap(Map<String, Object> map, String key) {
        key = key.toLowerCase();
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return map.get(key.toUpperCase());
        }
    }

    public Map<Long, CreativeManager.CreativeGlobalClassification> getMap() {
        return map;
    }
}

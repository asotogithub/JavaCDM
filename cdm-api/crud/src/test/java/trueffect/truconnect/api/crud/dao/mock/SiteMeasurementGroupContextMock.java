package trueffect.truconnect.api.crud.dao.mock;

import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock persistence context for Site Measurement Group
 * Created by marcelo.heredia on 6/10/2015.
 * * @author Marcelo Heredia
 */
public class SiteMeasurementGroupContextMock extends PersistenceContextMock {

    private Map<Long, SmGroup> dataMap;

    public SiteMeasurementGroupContextMock() {
        dataMap = new HashMap<Long, SmGroup>();
    }

    @Override
    public <T> T selectSingle(String statement, Object parameter, SqlSession session,
                              Class<T> type) {
        return type.cast(dataMap.get(parameter));
    }

    @Override
    public void execute(String statement, Object parameter, SqlSession session) {
        Map map = (Map) parameter;
        SmGroup smGroup = new SmGroup();
        smGroup.setId(Long.valueOf((String) map.get("id")));
        smGroup.setMeasurementId(Long.valueOf((String) map.get("measurementId")));
        smGroup.setGroupName((String) map.get("groupName"));
        smGroup.setCreatedTpwsKey((String) map.get("tpwsKey"));
        dataMap.put(smGroup.getId(), smGroup);
    }

    public Map<Long, SmGroup> getDataMap() {
        return dataMap;
    }
}

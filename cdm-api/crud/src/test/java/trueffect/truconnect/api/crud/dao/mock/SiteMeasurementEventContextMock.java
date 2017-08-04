package trueffect.truconnect.api.crud.dao.mock;

import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.dto.SmEventDTO;
import trueffect.truconnect.api.crud.dao.SiteMeasurementContextMock;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock persistence context for Site Measurement Event
 * Created by marcelo.heredia on 6/11/2015.
 * @author Marcelo Heredia
 */
public class SiteMeasurementEventContextMock extends PersistenceContextMock {

    private Map<Long, SmEvent> dataMap;
    private SiteMeasurementGroupContextMock groupContextMock;
    private SiteMeasurementContextMock smContextMock;

    public SiteMeasurementEventContextMock() {
        dataMap = new HashMap<Long, SmEvent>();
    }

    public SiteMeasurementEventContextMock(SiteMeasurementGroupContextMock groupContextMock,
                                           SiteMeasurementContextMock smContextMock) {
        this.groupContextMock = groupContextMock;
        this.smContextMock = smContextMock;
        dataMap = new HashMap<Long, SmEvent>();
    }

    @Override
    public <T> T selectSingle(String statement, Object parameter, SqlSession session,
                              Class<T> type) {
        return type.cast(dataMap.get(parameter));
    }

    @Override
    public void execute(String statement, Object parameter, SqlSession session) {
        Map map = (Map) parameter;
        SmEvent smEvent = new SmEvent();
        smEvent.setId(Long.valueOf((String) map.get("id")));
        smEvent.setSmGroupId(Long.valueOf((String) map.get("smGroupId")));
        smEvent.setEventName((String) map.get("location"));
        smEvent.setEventType(Long.valueOf((String) map.get("eventType")));
        smEvent.setSmEventType(Long.valueOf((String) map.get("smEventType")));
        smEvent.setCreatedTpwsKey((String) map.get("tpwsKey"));
        dataMap.put(smEvent.getId(), smEvent);
    }

    @Override
    public List<SmEventDTO> selectMultiple(String statement, Object parameter, SqlSession session){
        HashMap<String, String> map = (HashMap<String, String>) parameter;
        String siteMeasurementId = map.get("siteMeasurementId");
        List<SmEventDTO> result = new ArrayList<>();
        for(Map.Entry<Long, SmGroup> entry : groupContextMock.getDataMap().entrySet()){
            SmGroup group = entry.getValue();
            if(group.getMeasurementId().equals(Long.valueOf(siteMeasurementId))){
                result.addAll(getEventsForGroup(group));
            }
        }
        return result;
    }

    private List<SmEventDTO> getEventsForGroup(SmGroup group) {
        // To avoid having problems copying properties
        ConvertUtils.register(new DateConverter(null), Date.class);
        List<SmEventDTO> result = new ArrayList<>();
        for(Map.Entry<Long, SmEvent> entry : dataMap.entrySet()){
            SmEvent event = entry.getValue();
            if(event.getSmGroupId().equals(group.getId())){
                SmEventDTO dto = new SmEventDTO();
                try {
                    BeanUtils.copyProperties(dto, event);
                    dto.setGroupName(group.getGroupName());
                } catch (IllegalAccessException e) {
                    dto = null;
                } catch (InvocationTargetException e) {
                    dto = null;
                }
                result.add(dto);
            }
        }
        return result;
    }

    public void setGroupContextMock(SiteMeasurementGroupContextMock groupContextMock) {
        this.groupContextMock = groupContextMock;
    }
}

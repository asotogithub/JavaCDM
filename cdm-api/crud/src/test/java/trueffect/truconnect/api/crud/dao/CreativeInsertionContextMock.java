package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.SqlSession;

import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by richard.jaldin on 7/7/2015.
 */
public class CreativeInsertionContextMock extends PersistenceContextMock {

    private Map<Long, CreativeInsertion> creativeInsertions;

    public CreativeInsertionContextMock(Map<Long, CreativeInsertion> creativeInsertions) {
        this.creativeInsertions = creativeInsertions;
    }

    public Map<Long, CreativeInsertion> getCreativeInsertions() {
        return creativeInsertions;
    }

    @Override
    public List<?> selectList(String xmlMethod, Object parameter, SqlSession session)
            throws Exception {
        if (CreativeInsertionDaoImpl.CREATIVE_INSERTION_GET_CREATIVE_INSERTION_BY_CREATIVE_GROUP_ID.equalsIgnoreCase(xmlMethod)) {
            List<CreativeInsertion> result = new ArrayList<>();
            Long creativeGroupId = (Long) parameter;
            for(Map.Entry<Long, CreativeInsertion> entry : creativeInsertions.entrySet()) {
                if(creativeGroupId.compareTo(entry.getValue().getCreativeGroupId()) == 0){
                    result.add(entry.getValue());
                }
            }
            return result;
        } else {
            return super.selectList(xmlMethod, parameter, session);
        }
    }
}

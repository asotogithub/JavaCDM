package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by richard.jaldin on 7/6/2015.
 */
public class CreativeInsertionPersistenceContextMock extends PersistenceContextMock {

    private static Long globalSequence = 0L;

    private Map<Long, CreativeInsertion> creativeInsertions;

    public CreativeInsertionPersistenceContextMock(Map<Long, CreativeInsertion> creativeInsertions) {
        this.creativeInsertions = creativeInsertions;
    }

    public Map<Long, CreativeInsertion> getCreatives() {
        return creativeInsertions;
    }

    @Override
    public <T> T selectSingle(String xmlMethod, Object parameter, SqlSession session, Class<T> type) {
        if (CreativeInsertionDaoImpl.CREATIVE_INSERTION_GET_CREATIVE_INSERTIONS_NUMBER_OF_RECORDS_BY_CRITERIA.equalsIgnoreCase(xmlMethod)) {
            return type.cast(new Integer(creativeInsertions.size()));
        } else {
            return super.selectSingle(xmlMethod, parameter, session, type);
        }
    }

    @Override
    public List<?> selectList(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) throws Exception {
        List<CreativeInsertion> result = new ArrayList<>();
        for (Map.Entry<Long, CreativeInsertion> entry : creativeInsertions.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }
}

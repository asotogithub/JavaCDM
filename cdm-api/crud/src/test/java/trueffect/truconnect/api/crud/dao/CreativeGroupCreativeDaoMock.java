package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupCreativeDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

/**
 *
 * @author marleny.patsi
 */
public class CreativeGroupCreativeDaoMock extends CreativeGroupCreativeDaoImpl {

    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this
     * DAO
     */
    public CreativeGroupCreativeDaoMock(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public CreativeGroupCreative get(Long creativeGroupId, Long creativeId, SqlSession session) {
        String getKey = creativeGroupId + "," + creativeId;
        return ((CreativeGroupCreativeContextMock) getPersistenceContext()).getCreativeGroupCreatives().get(getKey);
    }
}

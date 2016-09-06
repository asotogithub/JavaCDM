package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.crud.dao.impl.CreativeGroupDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

/**
 *
 * Created by marcelo.heredia on 5/19/2015.
 */
public class CreativeGroupDaoMock extends CreativeGroupDaoImpl {



    /**
     * Creates a new {@code GenericDaoImpl} with a specific {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this DAO
     */
    public CreativeGroupDaoMock(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public CreativeGroup getById(Long creativeGroupId, SqlSession session) {
        return ((CreativeGroupContextMock)getPersistenceContext()).getCreativeGroups().get(creativeGroupId);
    }
}

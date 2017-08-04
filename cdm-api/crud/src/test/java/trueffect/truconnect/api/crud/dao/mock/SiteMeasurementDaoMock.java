package trueffect.truconnect.api.crud.dao.mock;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessControlMock;
import trueffect.truconnect.api.crud.dao.impl.SiteMeasurementDaoImpl;
import trueffect.truconnect.api.crud.dao.SiteMeasurementContextMock;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

/**
 * Mock implementation of {@code SiteMeasurementDaoImpl}
 *
 * @author Marcelo Heredia
 */
public class SiteMeasurementDaoMock extends SiteMeasurementDaoImpl {

    public SiteMeasurementDaoMock() {
        super(new PersistenceContextMock(), new AccessControlMock());
    }

    public SiteMeasurementDaoMock(PersistenceContext persistenceContext, AccessControl accessControl) {
        super(persistenceContext, accessControl);
    }
    
    @Override
    public SiteMeasurementDTO get(Long id, SqlSession session) {
        return ((SiteMeasurementContextMock)getPersistenceContext()).getSiteMeasurements().get(id);
    }    

    @Override
    public int update(SiteMeasurementDTO dto, SqlSession session) {
        return 1;
    }

    @Override
    public SiteMeasurementDTO delete(Long id, OauthKey key) throws Exception {
        throw new UnsupportedOperationException("Not Supported");
    }
}

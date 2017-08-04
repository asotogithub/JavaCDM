package trueffect.truconnect.api.crud.dao;

import org.apache.ibatis.session.RowBounds;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gustavo Claure
 */
public class SiteMeasurementContextMock extends PersistenceContextMock {

    private Map<Long, SiteMeasurementDTO> siteMeasurements;

    public SiteMeasurementContextMock(Map<Long, SiteMeasurementDTO> siteMeasurements) {
        this.siteMeasurements = siteMeasurements;
    }
    
    @Override
    public void execute(String statement, Object parameter, SqlSession session) {
        super.execute(statement, parameter, session);
        if ("SiteMeasurementPkg.insertSiteMeasurement".equalsIgnoreCase(statement)
                || "SiteMeasurementPkg.updateSiteMeasurement".equalsIgnoreCase(statement)) {
            SiteMeasurementDTO sm = (SiteMeasurementDTO) parameter;
            siteMeasurements.put(sm.getId(), sm);
        }
    }
    
    public Map<Long, SiteMeasurementDTO> getSiteMeasurements() {
        return siteMeasurements;
    }
    
    /**
     * @param siteMeasurements the siteMeasurements to set
     */
    public void setSiteMeasurements(Map<Long, SiteMeasurementDTO> siteMeasurements) {
        this.siteMeasurements = siteMeasurements;
    }

    @Override
    public List<?> selectMultiple(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) {
        List<SiteMeasurementDTO> records = new ArrayList<>();
        records.addAll(getSiteMeasurements().values());
        return records;
    }

    @Override
    public <T> T selectSingle(String statement, Object parameter, SqlSession session, Class<T> type) {
        if ("SiteMeasurementPkg.getSiteMeasurementsNumberOfRecordsByCriteria".equalsIgnoreCase(statement)) {
            return type.cast(siteMeasurements.size());
        } else {
            return super.selectSingle(statement, parameter, session, type);
        }
    }
}

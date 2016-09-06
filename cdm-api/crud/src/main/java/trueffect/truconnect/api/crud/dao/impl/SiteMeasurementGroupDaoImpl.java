package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.SiteMeasurementGroupDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by richard.jaldin on 6/9/2015.
 */
public class SiteMeasurementGroupDaoImpl extends AbstractGenericDao implements SiteMeasurementGroupDao {

    private static final Log LOGGER = LogFactory.getLog(SiteMeasurementGroupDaoImpl.class);
    private static final String STATEMENT_IS_SM_GROUP_NAME_EXISTS = "SmGroup.isSmGroupNameExists";
    private static final String STATEMENT_GET_SM_GROUP_LIST = "SmGroup.getSmGroupList";

    /**
     * {@inheritDoc}
     */
    public SiteMeasurementGroupDaoImpl(PersistenceContext persistenceContext, AccessControl accessControl) {
        super(persistenceContext, accessControl);
    }

    @Override
    public SmGroup get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle("SmGroup.getSmGroup", id, session, SmGroup.class);
    }

    @Override
    public void create(SmGroup smGroup, SqlSession session) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("id", String.valueOf(smGroup.getId()));
        parameter.put("measurementId", String.valueOf(smGroup.getMeasurementId()));
        parameter.put("groupName", smGroup.getGroupName());
        parameter.put("tpwsKey", String.valueOf(smGroup.getCreatedTpwsKey()));

        getPersistenceContext().execute("SmGroup.insertSmGroup", parameter, session);
    }

    @Override
    public Boolean isSmGroupNametExist(Long smId, String name, SqlSession session) {
        Long result =0L;
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("smId", smId.toString());
        parameter.put("name", name);
        result = getPersistenceContext().selectSingle(STATEMENT_IS_SM_GROUP_NAME_EXISTS, parameter, session,
                Long.class);

        return result == 0L ? false : true;
    }

    @Override
    public RecordSet<SmGroup> getSmGroupsBySiteMeasurement(Long siteMeasurementId, SqlSession session) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("siteMeasurementId", String.valueOf(siteMeasurementId));

        List<SmGroup> groupsList = getPersistenceContext().selectMultiple(
                STATEMENT_GET_SM_GROUP_LIST, parameter, session);

        return new RecordSet<SmGroup>(0, groupsList.size(), groupsList.size(), groupsList);
    }
}

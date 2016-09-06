package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.ExtendedProperties;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.search.Searcher;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Abel Soto
 */
public class ExtendedPropertiesDaoImpl extends AbstractGenericDao implements ExtendedPropertiesDao {

    private static final String STATEMENT_GET_BY_CRITERIA = "ExtendedPropertiesPkg.getCriteriaExtendedProperties";
    private static final String STATEMENT_GET_COUNT_BY_CRITERIA = "ExtendedPropertiesPkg.getExtPropertiesNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_BY_NAME_FIELD_VALUE = "ExtendedPropertiesPkg.getByObjectIdNameFieldValue";
    private static final String STATEMENT_GET_BY_NAME_FIELD = "ExtendedPropertiesPkg.getExtendedPropertyValue";
    private static final String STATEMENT_CREATE = "ExtendedPropertiesPkg.insertExtendedProperty";
    private static final String STATEMENT_UPDATE = "ExtendedPropertiesPkg.updateExtendedPropertyValue";
    private static final String STATEMENT_DELETE = "ExtendedPropertiesPkg.removeExtendedPropertyValue";

    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this
     * DAO
     */
    public ExtendedPropertiesDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<ExtendedProperties> get(SearchCriteria criteria, SqlSession session) throws Exception {

        if (criteria.getPageSize() > 1000) {
            throw new Exception("The page size allows up to 1000 records.");
        }
        if (criteria.getStartIndex() < 0) {
            throw new Exception("Cannot retrieve records for start index: " + criteria.getStartIndex()
                    + ". The minimum start index is: 0");
        }

        Searcher<ExtendedProperties> searcher = new Searcher<>(criteria.getQuery(),
                ExtendedProperties.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());

        // Getting data only for the page
        List<ExtendedProperties> aux = (List<ExtendedProperties>) 
                getPersistenceContext().selectList(STATEMENT_GET_BY_CRITERIA,
                parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<ExtendedProperties>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = (Integer) 
                getPersistenceContext().selectOne(STATEMENT_GET_COUNT_BY_CRITERIA,
                parameter, session);
        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        RecordSet<ExtendedProperties> result = new RecordSet<>(criteria.getStartIndex(), 
                criteria.getPageSize(), totalRecords, aux);
        return result;
    }

    @Override
    public ExtendedProperties getByIdNameFieldNameValue(ExtendedProperties extendProterties, 
            SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_BY_NAME_FIELD_VALUE,
                extendProterties, session, ExtendedProperties.class);
    }

    @Override
    public ExtendedProperties getExtendedPropertyValue(ExtendedProperties extendProterties, 
            SqlSession session){
        return getPersistenceContext().selectSingle(STATEMENT_GET_BY_NAME_FIELD,
                extendProterties, session, ExtendedProperties.class);
    }

    @Override
    public void save(ExtendedProperties extendProterties, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("objectName", extendProterties.getObjectName());
        parameter.put("fieldName", extendProterties.getFieldName());
        parameter.put("value", extendProterties.getValue());
        parameter.put("objectId", extendProterties.getObjectId());
        getPersistenceContext().execute(STATEMENT_CREATE, parameter, session);
    }

    @Override
    public void update(ExtendedProperties extProp, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("objectName", extProp.getObjectName());
        parameter.put("fieldName", extProp.getFieldName());
        parameter.put("objectId", extProp.getObjectId());
        parameter.put("value", extProp.getValue());
        getPersistenceContext().execute(STATEMENT_UPDATE, parameter, session);
    }

    @Override
    public String updateExternalId(String objectName, String fieldName, Long objectId, 
            String externalId, SqlSession session) {
        ExtendedProperties extProp = new ExtendedProperties();
        extProp.setObjectName(objectName);
        extProp.setFieldName(fieldName);
        extProp.setObjectId(objectId);
        extProp.setValue(externalId);
        if (StringUtils.isNotBlank(externalId)) {
            ExtendedProperties ext = this.getExtendedPropertyValue(extProp, session);
            if (ext == null) {
                this.save(extProp, session);
                extProp = getByIdNameFieldNameValue(extProp, session);
                return extProp.getValue();
            } else {
                this.update(extProp, session);
                return extProp.getValue();
            }
        } else {
            this.remove(extProp, session);
            return null;
        }
    }
    
    @Override
    public void remove(ExtendedProperties extProp, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("objectName", extProp.getObjectName());
        parameter.put("fieldName", extProp.getFieldName());
        parameter.put("objectId", extProp.getObjectId());
        getPersistenceContext().execute(STATEMENT_DELETE, parameter, session);
    }
}

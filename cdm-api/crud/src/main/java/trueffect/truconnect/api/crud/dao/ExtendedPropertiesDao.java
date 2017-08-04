package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.ExtendedProperties;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author marleny.patsi
 */
public interface ExtendedPropertiesDao extends GenericDao {

    @SuppressWarnings("unchecked")
    RecordSet<ExtendedProperties> get(SearchCriteria criteria, SqlSession session) throws Exception ;

    ExtendedProperties getByIdNameFieldNameValue(ExtendedProperties extendProterties, SqlSession session);

    ExtendedProperties getExtendedPropertyValue(ExtendedProperties extendProterties, SqlSession session) ;
    
    void save(ExtendedProperties extendProterties, SqlSession session);

    void update(ExtendedProperties extProp, SqlSession session);
    
    String updateExternalId(String objectName, String fieldName, Long objectId, String externalId, SqlSession session);

    void remove(ExtendedProperties extProp, SqlSession session);
}

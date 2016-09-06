package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 * Method implementation for Site Measurement related to persistence
 *
 * @author Abel Soto
 */
public class SiteMeasurementDaoImpl extends AbstractGenericDao implements SiteMeasurementDao {

    private static final String STATEMENT_INSERT_SITE_MEASUREMENT =
            "SiteMeasurementPkg.insertSiteMeasurement";
    private static final String STATEMENT_GET_SITE_MEASUREMENT =
            "SiteMeasurementPkg.getSiteMeasurement";
    private static final String STATEMENT_GET_SITE_MEASUREMENTS_BY_CRITERIA =
            "SiteMeasurementPkg.getSiteMeasurementsByCriteria";
    private static final String STATEMENT_GET_SITE_MEASUREMENTS_NUMBER_OF_RECORDS_BY_CRITERIA =
            "SiteMeasurementPkg.getSiteMeasurementsNumberOfRecordsByCriteria";
    private static final String STATEMENT_UPDATE_SITE_MEASUREMENT =
            "SiteMeasurementPkg.updateSiteMeasurement";
    private static final String STATEMENT_DELETE_SITE_MEASUREMENT =
            "SiteMeasurementPkg.deleteSiteMeasurement";
    private static final String STATEMENT_DOES_SITE_MEASUREMENT_EXISTS =
            "SiteMeasurementPkg.doesSiteMeasurementExists";

    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this DAO
     * @param accessControl The specific {@code AccessControl} Access control to this DAO
     */
    public SiteMeasurementDaoImpl(PersistenceContext persistenceContext, AccessControl accessControl) {
        super(persistenceContext, accessControl);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<SiteMeasurementDTO> get(SearchCriteria criteria, OauthKey key, SqlSession session) {
        Searcher<SiteMeasurementDTO> searcher;
        try{
            searcher = new Searcher<SiteMeasurementDTO>(criteria.getQuery(), SiteMeasurementDTO.class);
        } catch(SearchApiException e) {
            throw new SystemException(e, BusinessCode.INVALID);
        }
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        List<SiteMeasurementDTO> aux = getPersistenceContext()
                .selectMultiple(STATEMENT_GET_SITE_MEASUREMENTS_BY_CRITERIA, parameter,
                        new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) {
            return new RecordSet<SiteMeasurementDTO>(0, 0, 0, aux);
        }
        int totalRecords = getPersistenceContext()
                .selectSingle(STATEMENT_GET_SITE_MEASUREMENTS_NUMBER_OF_RECORDS_BY_CRITERIA,
                        parameter, session, Integer.class);
        if (criteria.getStartIndex() > totalRecords) {
            throw new SystemException("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords, BusinessCode.INVALID);
        }

        return new RecordSet<SiteMeasurementDTO>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
    }

    @Override
    public SiteMeasurementDTO get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_SITE_MEASUREMENT, id, session, SiteMeasurementDTO.class);
    }

    @Override
    public SiteMeasurementDTO create(SiteMeasurementDTO siteMeasurement, SqlSession session) {
        Long id = getNextId(session);
        siteMeasurement.setId(id);
        getPersistenceContext().execute(STATEMENT_INSERT_SITE_MEASUREMENT, siteMeasurement, session);

        return get(id, session);
    }

    @Override
    public int update(SiteMeasurementDTO dto, SqlSession session) {
       return getPersistenceContext().update(STATEMENT_UPDATE_SITE_MEASUREMENT, dto, session);
    }

    @Override
    public SiteMeasurementDTO delete(Long id, OauthKey key) throws Exception {
        SqlSession session = null;
        SiteMeasurementDTO result;
        try {
            session = MyBatisUtil.beginTransaction();

            //Parsing the input parameters of the process
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("id", id);
            parameter.put("tpwsKey", key.getTpws());
            MyBatisUtil.callProcedurePlSql(STATEMENT_DELETE_SITE_MEASUREMENT, parameter, session);
            result = get(id, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    @Override
    public boolean doesSiteMeasurementExists(String name, Long smId, Long brandId,
                                             SqlSession session) {
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("name", name);
        parameter.put("smId", smId != null ? smId.toString() : null);
        parameter.put("brandId", brandId != null ? brandId.toString() : null);
        Long result = getPersistenceContext()
                .selectSingle(STATEMENT_DOES_SITE_MEASUREMENT_EXISTS, parameter, session,
                        Long.class);

        return result != 0L;
    }
}

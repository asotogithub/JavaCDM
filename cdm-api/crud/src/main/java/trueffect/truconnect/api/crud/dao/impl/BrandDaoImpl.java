package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.dao.BrandDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Rambert Rioja
 * @edited Abel Soto
 */
public class BrandDaoImpl extends AbstractGenericDao implements BrandDao {

    private static final String STATEMENT_GET_BRAND = "Brand.getBrand";
    private static final String STATEMENT_GET_BRANDS_BY_CRITERIA = "Brand.getBrandsByCriteria";
    private static final String STATEMENT_GET_NUMBER_OF_RECORDS_BRANDS_BY_CRITERIA = "Brand.getBrandsNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_BRANDS_BY_ADVERTISER = "Brand.getBrandsByAdvertiserId";
    private static final String STATEMENT_INSERT_BRAND = "Brand.insertBrand";
    private static final String STATEMENT_UPDATE_BRAND = "Brand.updateBrand";
    private static final String STATEMENT_DELETE_BRAND = "Brand.deleteBrand";
    private static final String STATEMENT_BRAND_EXISTS_FOR_SAVE = "Brand.brandExistsForSave";
    private static final String STATEMENT_BRAND_EXISTS_FOR_UPDATE = "Brand.brandExistsForUpdate";
    private static final String STATEMENT_HAS_ACTIVE_CAMPAIGN = "Brand.hasActiveCampaign";
    private static final String STATEMENT_HAS_ACTIVE_SITE_MEASUREMENT = "Brand.hasActiveSiteMeasurement";

    public BrandDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public Brand get(Long id, SqlSession session) throws Exception {
        return getPersistenceContext().selectOne(STATEMENT_GET_BRAND, id, session, Brand.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<Brand> getBrands(SearchCriteria criteria, String userId, SqlSession session) throws Exception {
        RecordSet<Brand> result = null;
        try {

            Searcher<Brand> searcher = new Searcher<>(criteria.getQuery(), Brand.class);
            HashMap<String, String> parameter = new HashMap<>();

            parameter.put("condition", searcher.getMybatisWheresCondition());
            parameter.put("order", searcher.getMybatisOrderBySection());
            parameter.put("userId", userId);

            // Getting data only for the page
            List<Brand> aux = (List<Brand>) getPersistenceContext().selectList(STATEMENT_GET_BRANDS_BY_CRITERIA,
                    parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

            if (aux.isEmpty()) { // empty result.
                return new RecordSet<Brand>(0, 0, 0, aux);
            }

            //Getting the total number of records available
            int totalRecords = (Integer) getPersistenceContext().selectOne(STATEMENT_GET_NUMBER_OF_RECORDS_BRANDS_BY_CRITERIA, parameter, session);
            if (criteria.getStartIndex() > totalRecords) {
                throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                        + " because the starting index should be less than " + totalRecords);
            }
            result = new RecordSet<>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    @Override
    public RecordSet<Brand> getBrandsByAdvertiserId(Long advertiserId, String userId, SqlSession session){
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("advertiserId", advertiserId);
        parameter.put("userId", userId);

        List<Brand> brandsList = getPersistenceContext().selectMultiple(STATEMENT_GET_BRANDS_BY_ADVERTISER,
                parameter, new RowBounds(), session);

        if (brandsList.isEmpty()) {
            return new RecordSet<Brand>(0, 0, 0, brandsList);
        }

        RecordSet<Brand> recordSetResult = new RecordSet<>();
        recordSetResult.setRecords(brandsList);
        recordSetResult.setTotalNumberOfRecords(brandsList.size());
        return recordSetResult;
    }
    
    @Override
    public void create(Brand brand, SqlSession session) throws Exception {
        try {
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("id", brand.getId());
            parameter.put("advertiserId", brand.getAdvertiserId());
            parameter.put("name", brand.getName());
            parameter.put("dupName", brand.getName());
            parameter.put("description", brand.getDescription());
            parameter.put("createdTpwsKey", brand.getCreatedTpwsKey());
            getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_INSERT_BRAND, parameter, session);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void update(Brand brand, SqlSession session) throws Exception {
        try {
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("id", brand.getId());
            parameter.put("advertiserId", brand.getAdvertiserId());
            parameter.put("name", brand.getName());
            parameter.put("dupName", brand.getName());
            parameter.put("description", brand.getDescription());
            parameter.put("modifiedTpwsKey", brand.getModifiedTpwsKey());
            parameter.put("isHidden", brand.getIsHidden());
            getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_UPDATE_BRAND, parameter, session);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void remove(Long id, String userId, SqlSession session) throws Exception {
        try {
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("id", id);
            parameter.put("createdTpwsKey", userId);
            getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_DELETE_BRAND, parameter, session);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void brandExistsForSave(String name, Long advertiserId, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("dupName", name);
        parameter.put("advertiserId", advertiserId);
        Long result = (Long) getPersistenceContext().selectOne(STATEMENT_BRAND_EXISTS_FOR_SAVE, parameter, session);
        if (result.compareTo(0L) > 0) {
            throw new ConflictException("Brand name already exists.");
        }
    }

    @Override
    public Boolean brandExistsForUpdate(Brand brand, SqlSession session) {
        Long result = getPersistenceContext().selectSingle(STATEMENT_BRAND_EXISTS_FOR_UPDATE, brand, session, Long.class);
        return result.compareTo(0L) > 0;
    }
    
    @Override
    public void hasActiveCampaign(Long id, SqlSession session) throws Exception {
        Long result = (Long) getPersistenceContext().selectOne(STATEMENT_HAS_ACTIVE_CAMPAIGN, id, session);
        if (result.compareTo(0L) > 0) {
            throw new ConflictException("Brand has active trafficked campaign(s)");
        }
    }

    @Override
    public void hasActiveSiteMeasurement(Long id, SqlSession session) throws Exception {
        Long result = (Long) getPersistenceContext().selectOne(STATEMENT_HAS_ACTIVE_SITE_MEASUREMENT, id, session);
        if (result.compareTo(0L) > 0) {
            throw new ConflictException("Brand has active trafficked campaign(s)");
        }
    }
}

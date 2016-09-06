package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Richard Jaldin
 */
public class AdvertiserDaoImpl extends AbstractGenericDao implements AdvertiserDao {

    public static final String STATEMENT_GET_ADVERTISER = "Advertiser.getAdvertiser";
    public static final String STATEMENT_GET_ADVERTISERS_BY_CRITERIA =
            "Advertiser.getAdvertisersByCriteria";
    public static final String STATEMENT_NUMBER_ADVERTISERS_BY_CRITERIA =
            "Advertiser.getAdvertisersNumberOfRecordsByCriteria";

    public AdvertiserDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public Advertiser get(Long id, SqlSession session){
        return getPersistenceContext().selectSingle(STATEMENT_GET_ADVERTISER, id, session, Advertiser.class);
    }

    @Override
    public RecordSet<Advertiser> getAdvertisers(SearchCriteria criteria, String userId, SqlSession session)
            throws SearchApiException {

        RecordSet<Advertiser> result = null;

        Searcher<Advertiser> searcher = new Searcher<>(criteria.getQuery(), Advertiser.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", userId);

        // Getting data only for the page
        List<Advertiser> aux = getPersistenceContext().selectMultiple(
                STATEMENT_GET_ADVERTISERS_BY_CRITERIA,
                parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()),
                session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<Advertiser>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = getPersistenceContext().selectSingle(
                STATEMENT_NUMBER_ADVERTISERS_BY_CRITERIA, parameter, session, Integer.class);
        if (criteria.getStartIndex() > totalRecords) {
            throw new SearchApiException("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        result = new RecordSet<>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<Advertiser> getAdvertisersByUserId(String userId, SqlSession session) {
        
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("userId", userId);

        List<Advertiser> advertisersList = getPersistenceContext().selectMultiple("Advertiser.getAdvertisersByUserId",
                parameter, new RowBounds(), session);

        if (advertisersList.isEmpty()) {
            return new RecordSet<Advertiser>(0, 0, 0, advertisersList);
        }

        RecordSet<Advertiser> recordSetResult = new RecordSet<>();
        recordSetResult.setRecords(advertisersList);
        recordSetResult.setTotalNumberOfRecords(advertisersList.size());
        return recordSetResult;        
    }

    @Override
    public void create(Advertiser advertiser, SqlSession session) throws Exception {

        try {
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("id", advertiser.getId());
            parameter.put("agencyId", advertiser.getAgencyId());
            parameter.put("name", advertiser.getName());
            parameter.put("address1", advertiser.getAddress1());
            parameter.put("address2", advertiser.getAddress2());
            parameter.put("city", advertiser.getCity());
            parameter.put("state", advertiser.getState());
            parameter.put("zip", advertiser.getZipCode());
            parameter.put("country", advertiser.getCountry());
            parameter.put("phone", advertiser.getPhoneNumber());
            parameter.put("url", advertiser.getUrl());
            parameter.put("fax", advertiser.getFaxNumber());
            parameter.put("contactDefault", advertiser.getContactDefault());
            parameter.put("notes", advertiser.getNotes());
            parameter.put("createdTpwsKey", advertiser.getCreatedTpwsKey());
            parameter.put("enableHtmlTag", advertiser.getEnableHtmlTag());
            getPersistenceContext().callPlSqlStoredProcedure("Advertiser.saveAdvertiser", parameter, session);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void update(Advertiser advertiser, SqlSession session) throws Exception {

        try {
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("id", advertiser.getId());
            parameter.put("name", advertiser.getName());
            parameter.put("address1", advertiser.getAddress1());
            parameter.put("address2", advertiser.getAddress2());
            parameter.put("city", advertiser.getCity());
            parameter.put("state", advertiser.getState());
            parameter.put("zip", advertiser.getZipCode());
            parameter.put("country", advertiser.getCountry());
            parameter.put("phone", advertiser.getPhoneNumber());
            parameter.put("url", advertiser.getUrl());
            parameter.put("fax", advertiser.getFaxNumber());
            parameter.put("contactDefault", advertiser.getContactDefault());
            parameter.put("notes", advertiser.getNotes());
            parameter.put("modifiedTpwsKey", advertiser.getModifiedTpwsKey());
            parameter.put("enableHtmlTag", advertiser.getEnableHtmlTag());
            parameter.put("isHidden", advertiser.getIsHidden());
            getPersistenceContext().callPlSqlStoredProcedure("Advertiser.updateAdvertiser", parameter, session);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void hardRemove(Long id, SqlSession session) throws Exception {
        try {
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("id", id);
            getPersistenceContext().callPlSqlStoredProcedure("DeletePkg.removeAdvertiserPhysically", parameter, session);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Boolean advertiserNameExists(Advertiser advertiser, SqlSession session) throws Exception {
        Long cant = (Long) getPersistenceContext().selectOne("Advertiser.advertiserNameExists", advertiser, session);
        return cant == 0L ? false : true;
    }
}

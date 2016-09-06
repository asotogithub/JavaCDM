package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by richard.jaldin on 10/21/2015.
 */
public class PublisherDaoImpl extends AbstractGenericDao implements PublisherDao {

    private static final String STATEMENT_GET_PUBLISHER = "PublisherPkg.getPublisher";
    private static final String STATEMENT_GET_PUBLISHER_BY_AGENCY_ID = "PublisherPkg.getPublisherByAgency";
    private static final String STATEMENT_GET_PUBLISHERS_BY_CRITERIA = "PublisherPkg.getPublishersByCriteria";
    private static final String STATEMENT_GET_NUMBER_OF_PUBLISHERS_BY_CRITERIA = "PublisherPkg.getPublishersNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_BY_CAMPAIGNID_NAME = "PublisherPkg.getPublisherByCampaignIdAndName";
    private static final String STATEMENT_PUBLISHER_EXISTS = "PublisherPkg.publisherExists";
    private static final String STATEMENT_INSERT_PUBLISHER = "PublisherPkg.insertPublisher";
    private static final String STATEMENT_UPDATE_PUBLISHER = "PublisherPkg.updatePublisher";
    private static final String STATEMENT_DELETE_PUBLISHER = "PublisherPkg.deletePublisher";

    public PublisherDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public Publisher get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_PUBLISHER, id, session, Publisher.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RecordSet<Publisher> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception {
        RecordSet<Publisher> result = null;
        if (criteria.getPageSize() > 1000) {
            throw new Exception("The page size allows up to 1000 records.");
        }
        if (criteria.getStartIndex() < 0) {
            throw new Exception("Cannot retrieve records for start index: " +
                    criteria.getStartIndex() + ". The minimum start index is: 0");
        }

        Searcher<Publisher> searcher = new Searcher<>(criteria.getQuery(), Publisher.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        // Getting data only for the page
        List<Publisher> aux = (List<Publisher>) getPersistenceContext().selectList(STATEMENT_GET_PUBLISHERS_BY_CRITERIA,
                parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<Publisher>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = (Integer) getPersistenceContext().selectOne(STATEMENT_GET_NUMBER_OF_PUBLISHERS_BY_CRITERIA,
                parameter, session);
        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        result = new RecordSet<Publisher>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
        return result;
    }

    @Override
    public Publisher getByAgencyId(Long id, Long agencyId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("agencyId", agencyId);
        return getPersistenceContext().selectSingle(STATEMENT_GET_PUBLISHER_BY_AGENCY_ID, parameter, session, Publisher.class);
    }

    @Override
    public Long exists(Publisher publisher, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_PUBLISHER_EXISTS, publisher, session, Long.class);
    }

    @Override
    public Publisher create(Publisher publisher, SqlSession session) {
        Long publisherId = getNextId(session);
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("agencyId", publisher.getAgencyId());
        parameter.put("id", publisherId);
        parameter.put("name", publisher.getName());
        parameter.put("address1", publisher.getAddress1());
        parameter.put("address2", publisher.getAddress2());
        parameter.put("city", publisher.getCity());
        parameter.put("state", publisher.getState());
        parameter.put("zip", publisher.getZipCode());
        parameter.put("country", publisher.getCountry());
        parameter.put("phoneNumber", publisher.getPhoneNumber());
        parameter.put("url", publisher.getUrl());
        parameter.put("agencyNotes", publisher.getAgencyNotes());
        parameter.put("createdTpwsKey", publisher.getCreatedTpwsKey());
        getPersistenceContext().execute(STATEMENT_INSERT_PUBLISHER, parameter, session);
        return getByAgencyId(publisherId, publisher.getAgencyId(), session);
    }

    @Override
    public Publisher update(Publisher publisher, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", publisher.getId());
        parameter.put("name", publisher.getName());
        parameter.put("address1", publisher.getAddress1());
        parameter.put("address2", publisher.getAddress2());
        parameter.put("city", publisher.getCity());
        parameter.put("state", publisher.getState());
        parameter.put("zip", publisher.getZipCode());
        parameter.put("country", publisher.getCountry());
        parameter.put("phoneNumber", publisher.getPhoneNumber());
        parameter.put("url", publisher.getUrl());
        parameter.put("agencyNotes", publisher.getAgencyNotes());
        parameter.put("modifiedTpwsKey", publisher.getModifiedTpwsKey());

        getPersistenceContext().execute(STATEMENT_UPDATE_PUBLISHER, parameter, session);
        return get(publisher.getId(), session);
    }

    @Override
    public void remove(Long id, Long agencyId, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("agencyId", agencyId);
        parameter.put("id", id);
        parameter.put("createdTpwsKey", key.getTpws());
        getPersistenceContext().execute(STATEMENT_DELETE_PUBLISHER, parameter, session);
    }

    @Override
    public List<Publisher> getByCampaignIdAndName(Collection<String> names, String userId,
                                                  Long campaignId, final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", names);
        parameters.put("userId", userId);
        parameters.put("campaignId", campaignId);
        List<Publisher> result = new ArrayList<>();
        Accumulator<List<Publisher>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<Publisher>>(
                "names",
                new ArrayList<>(names),
                resultAccumulator,
                parameters) {
            @Override
            public List<Publisher> execute(Object parameters) {
                return getPersistenceContext().selectMultiple(STATEMENT_GET_BY_CAMPAIGNID_NAME,
                        parameters, session);
            }
        }.getResults();
    }
}
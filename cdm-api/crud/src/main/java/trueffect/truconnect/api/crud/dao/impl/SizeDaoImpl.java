package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.crud.dao.SizeDao;
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
 *
 * @author Gustavo Claure
 * Modified by Richard Jaldin
 */
public class SizeDaoImpl extends AbstractGenericDao implements SizeDao {

    private static final String STATEMENT_GET_BY_ID = "Size.getAdSize";
    private static final String STATEMENT_GET_BY_USER_AND_DIMENSIONS = "Size.getAdSizeByDimension";
    private static final String STATEMENT_GET_BY_CRITERIA = "Size.getAdSizesByCriteria";
    private static final String STATEMENT_GET_COUNT_BY_CRITERIA = "Size.getAdSizesNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_NEXT_ID = "GenericQueries.getNextId";
    private static final String STATEMENT_SEQ_SIZE = "SEQ_AD_SIZE";
    private static final String STATEMENT_GET_BY_CAMPAIGNID_WIDTH_HEIGHT = "Size.getSizeByCampaignIdAndWidthHeight";
    private static final String STATEMENT_CREATE = "Size.insertAdSize";

    public SizeDaoImpl(PersistenceContext context) {
        super(context);
    }

    @Override
    public Size getById(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_BY_ID, id, session, Size.class);
    }

    @Override
    public Size getByUserAndDimensions(Long height, Long width, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("height", height);
        parameter.put("width", width);
        parameter.put("userId", key.getUserId());
        return getPersistenceContext().selectSingle(STATEMENT_GET_BY_USER_AND_DIMENSIONS, parameter, session, Size.class);
    }

    @SuppressWarnings("unchecked")
    public RecordSet<Size> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws SearchApiException{
        Searcher<Size> searcher = new Searcher<>(criteria.getQuery(), Size.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        // Getting data only for the page
        List<Size> aux = getPersistenceContext().selectMultiple(STATEMENT_GET_BY_CRITERIA,
            parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<Size>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = getPersistenceContext().selectSingle(
                STATEMENT_GET_COUNT_BY_CRITERIA, parameter, session, Integer.class);
        if (criteria.getStartIndex() > totalRecords) {
            throw new SearchApiException("Cannot retrieve the set of records starting by " + criteria.getStartIndex() +
                " because the starting index should be less than " + totalRecords);
        }
        return new RecordSet<>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
    }

    @Override
    public Size create(Size size, SqlSession session) {
        Long id = getPersistenceContext().selectSingle(STATEMENT_GET_NEXT_ID, STATEMENT_SEQ_SIZE, session, Long.class);
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("agencyId", size.getAgencyId());
        parameter.put("width", size.getWidth());
        parameter.put("height", size.getHeight());
        parameter.put("label", size.getLabel());
        parameter.put("createdTpwsKey", size.getCreatedTpwsKey());
        getPersistenceContext().execute(STATEMENT_CREATE, parameter, session);
        return getById(id, session);
    }

    @Override
    public List<Size> getByCampaignIdAndWidthHeight(Collection<String> names, String userId,
                                                    Long campaignId, final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", names);
        parameters.put("userId", userId);
        parameters.put("campaignId", campaignId);
        List<Size> result = new ArrayList<>();
        Accumulator<List<Size>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<Size>>(
                "names",
                new ArrayList<>(names),
                resultAccumulator,
                parameters) {
            @Override
            public List<Size> execute(Object parameters) {
                return getPersistenceContext()
                        .selectMultiple(STATEMENT_GET_BY_CAMPAIGNID_WIDTH_HEIGHT,
                                parameters, session);
            }
        }.getResults();
    }
}

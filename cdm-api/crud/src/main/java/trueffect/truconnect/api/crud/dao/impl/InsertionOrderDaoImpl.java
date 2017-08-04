package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
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
 * @author richard Jaldin
 */
public class InsertionOrderDaoImpl extends AbstractGenericDao implements InsertionOrderDao {

    private static final String STATEMENT_GET_BY_ID = "IOPkg.getIO";
    private static final String STATEMENT_GET_BY_CRITERIA = "IOPkg.getIOByCriteria";
    private static final String STATEMENT_GET_COUNT_BY_CRITERIA = "IOPkg.getIONumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_FIRST_IO_BY_MEDIA_BUY_ID = "IOPkg.getIOByMediaBuy";
    private static final String STATEMENT_GET_BY_CAMPAIGNID_IONUMBER_NAME = "IOPkg.getIoByCampaignIdAndIoNumberName";
    private static final String STATEMENT_GET_HAS_ACT_CAMPAIGN = "IOPkg.ioHasActCampaings";
    private static final String STATEMENT_CREATE = "IOPkg.insertInsertionOrder";
    private static final String STATEMENT_UPDATE = "IOPkg.updateInsertionOrder";
    private static final String STATEMENT_DELETE = "IOPkg.deleteInsertionOrder";

    public InsertionOrderDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public InsertionOrder get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_BY_ID, id, session, InsertionOrder.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<InsertionOrder> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception {
        if (criteria.getPageSize() > 1000) {
            throw new Exception("The page size allows up to 1000 records.");
        }
        if (criteria.getStartIndex() < 0) {
            throw new Exception(
                    "Cannot retrieve records for start index: " + criteria.getStartIndex()
                    + ". The minimum start index is: 0");
        }

        Searcher<InsertionOrder> searcher
                = new Searcher<>(criteria.getQuery(), InsertionOrder.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        // Getting data only for the page
        List<InsertionOrder> aux = (List<InsertionOrder>) getPersistenceContext().selectList(
                STATEMENT_GET_BY_CRITERIA, parameter,
                new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = (Integer) getPersistenceContext().selectOne(
                STATEMENT_GET_COUNT_BY_CRITERIA, parameter, session);
        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception(
                    "Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        return new RecordSet<>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
    }

    @Override
    public InsertionOrder getFirstIOByMediaBuy(Long mediaBuyId, SqlSession session) {
        List<InsertionOrder> result = getPersistenceContext().selectMultiple(STATEMENT_GET_FIRST_IO_BY_MEDIA_BUY_ID, mediaBuyId, session);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public void create(InsertionOrder insertionOrder, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", insertionOrder.getId());
        parameter.put("mediaBuyId", insertionOrder.getMediaBuyId());
        parameter.put("publisherId", insertionOrder.getPublisherId());
        parameter.put("ioNumber", insertionOrder.getIoNumber());
        parameter.put("name", insertionOrder.getName());
        parameter.put("notes", insertionOrder.getNotes());
        parameter.put("createdTpwsKey", insertionOrder.getCreatedTpwsKey());
        getPersistenceContext().execute(STATEMENT_CREATE, parameter, session);
    }

    @Override
    public void update(InsertionOrder insertionOrder, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", insertionOrder.getId());
        parameter.put("name", insertionOrder.getName());
        parameter.put("notes", insertionOrder.getNotes());
        parameter.put("modifiedTpwsKey", insertionOrder.getModifiedTpwsKey());
        parameter.put("ioNumber", insertionOrder.getIoNumber());
        getPersistenceContext().execute(STATEMENT_UPDATE, parameter, session);
    }

    @Override
    public void remove(Long id, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("modifiedTpwsKey", key.getTpws());
        getPersistenceContext().execute(STATEMENT_DELETE, parameter, session);
    }

    @Override
    public boolean hasActCampaings(Long id, SqlSession session) {
        Long ioId = getPersistenceContext().selectSingle(STATEMENT_GET_HAS_ACT_CAMPAIGN, id, session, Long.class);
        return ioId != null;
    }

    @Override
    public List<InsertionOrder> getIosByCampaignIdAndIoNumberName(Collection<String> names,
                                                                  String userId, Long campaignId,
                                                                  final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", names);
        parameters.put("userId", userId);
        parameters.put("campaignId", campaignId);
        List<InsertionOrder> result = new ArrayList<>();
        Accumulator<List<InsertionOrder>> resultAccumulator =
                new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<InsertionOrder>>(
                "names",
                new ArrayList<>(names),
                resultAccumulator,
                parameters) {
            @Override
            public List<InsertionOrder> execute(Object parameters) {
                return getPersistenceContext()
                        .selectMultiple(STATEMENT_GET_BY_CAMPAIGNID_IONUMBER_NAME,
                                parameters, session);
            }
        }.getResults();
    }
}

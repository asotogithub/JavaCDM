package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
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
 * Created by richard.jaldin on 7/31/2015.
 */
public class SiteSectionDaoImpl extends AbstractGenericDao implements SiteSectionDao {

    private static final String STATEMENT_GET_BY_ID = "SiteSection.getInsertSiteSection";
    private static final String STATEMENT_GET_BY_CRITERIA = "SiteSection.getSiteSectionsByCriteria";
    private static final String STATEMENT_GET_COUNT_BY_CRITERIA = "SiteSection.getSiteSectionsNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_COUNT_BY_NAME_SITE_ID = "SiteSection.siteSectionExists";
    private static final String STATEMENT_GET_BY_CAMPAIGNID_PUBLISHERNAME_SITENAME_SECTIONNAME = "SiteSection.getSectionByCampaignIdAndPublisherSiteSectionNames";
    private static final String STATEMENT_CREATE = "SiteSection.insertSiteSection";
    private static final String STATEMENT_UPDATE = "SiteSection.updateSiteSection";
    private static final String STATEMENT_DELETE = "SiteSection.deleteSiteSection";

    public SiteSectionDaoImpl(PersistenceContext context) {
        super(context);
    }

    @Override
    public SiteSection get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_BY_ID, id, session, SiteSection.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<SiteSection> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception {
        Searcher<SiteSection> searcher = new Searcher<>(criteria.getQuery(), SiteSection.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        // Getting data only for the page
        List<SiteSection> aux = (List<SiteSection>) getPersistenceContext().selectList(STATEMENT_GET_BY_CRITERIA,
            parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<SiteSection>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = (Integer) getPersistenceContext().selectOne(STATEMENT_GET_COUNT_BY_CRITERIA, parameter, session);
        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                + " because the starting index should be less than " + totalRecords);
        }
        return new RecordSet<SiteSection>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
    }

    @Override
    public Long exists(SiteSection siteSection, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_COUNT_BY_NAME_SITE_ID, siteSection, session, Long.class);
    }

    @Override
    public SiteSection create(SiteSection siteSection, SqlSession session) {
        SiteSection result;
        Long id = getNextId(session);
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("siteId", siteSection.getSiteId());
        parameter.put("name", siteSection.getName());
        parameter.put("url", "");
        parameter.put("agencyNotes", siteSection.getAgencyNotes());
        parameter.put("publisherNotes", siteSection.getPublisherNotes());
        parameter.put("extProp1", siteSection.getExtProp1());
        parameter.put("extProp2", siteSection.getExtProp2());
        parameter.put("extProp3", siteSection.getExtProp3());
        parameter.put("extProp4", siteSection.getExtProp4());
        parameter.put("extProp5", siteSection.getExtProp5());
        parameter.put("tpwsKey", siteSection.getCreatedTpwsKey());
        getPersistenceContext().execute(STATEMENT_CREATE, parameter, session);
        result = get(id, session);
        return result;
    }

    @Override
    public SiteSection update(SiteSection siteSection, SqlSession session) {
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", siteSection.getId());
        parameter.put("name", siteSection.getName());
        parameter.put("url", "");
        parameter.put("agencyNotes", siteSection.getAgencyNotes());
        parameter.put("publisherNotes", siteSection.getPublisherNotes());
        parameter.put("extProp1", siteSection.getExtProp1());
        parameter.put("extProp2", siteSection.getExtProp2());
        parameter.put("extProp3", siteSection.getExtProp3());
        parameter.put("extProp4", siteSection.getExtProp4());
        parameter.put("extProp5", siteSection.getExtProp5());
        parameter.put("tpwsKey", siteSection.getModifiedTpwsKey());
        getPersistenceContext().execute(STATEMENT_UPDATE, parameter, session);
        return get(siteSection.getId(), session);
    }

    @Override
    public void remove(Long id, OauthKey key, SqlSession session) {
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("tpwsKey", key.getTpws());
        getPersistenceContext().execute(STATEMENT_DELETE, parameter, session);
    }

    @Override
    public List<SiteSection> getByCampaignIdAndPublisherNameSiteName(Collection<String> names,
                                                                     String userId, Long campaignId,
                                                                     final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", names);
        parameters.put("userId", userId);
        parameters.put("campaignId", campaignId);
        List<SiteSection> result = new ArrayList<>();
        Accumulator<List<SiteSection>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<SiteSection>>(
                "names",
                new ArrayList<>(names),
                resultAccumulator,
                parameters) {
            @Override
            public List<SiteSection> execute(Object parameters) {
                return getPersistenceContext().selectMultiple(
                        STATEMENT_GET_BY_CAMPAIGNID_PUBLISHERNAME_SITENAME_SECTIONNAME,
                        parameters, session);
            }
        }.getResults();
    }
}

package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.dto.SiteContactView;
import trueffect.truconnect.api.crud.dao.SiteDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;
import trueffect.truconnect.api.crud.validation.SiteValidator;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Abel Soto
 */
public class SiteDaoImpl extends AbstractGenericDao implements SiteDao{
    
    private static final String STATEMENT_GET_SITE_BY_ID = "Site.getSiteById";
    private static final String STATEMENT_GET_SITE_BY_CRITERIA = "Site.getSitesByCriteria";
    private static final String STATEMENT_GET_SITE_NUMBER_OF_RECORDS_BY_CRITERIA = "Site.getSiteNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_BY_CAMPAIGNID_PUBLISHERNAME_SITENAME = "Site.getSiteByCampaignIdAndPublisherSiteNames";
    private static final String STATEMENT_CHECK_SITE_EXISTS_BY_NAME = "Site.checkSiteExistsByName";
    private static final String STATEMENT_CHECK_SITE_EXISTS_BY_NAME_AND_PUBLISHER_ID = "Site.siteExists";
    private static final String STATEMENT_INSERT_SITE = "Site.insertSite";
    private static final String STATEMENT_UPDATE_SITE = "Site.updateSite";
    private static final String STATEMENT_DELETE_SITE = "Site.deleteSite";
    private static final String STATEMENT_GET_SITE_CONTACTS_BY_CAMPAIGNID = "Site.getTraffickingContactsByCampaignId";
    
    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this
     * DAO
     */
    public SiteDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
        
    }

    @Override
    public Site get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_SITE_BY_ID, id, session, Site.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<Site> getSites(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception {
        RecordSet<Site> result = null;
        if (criteria.getPageSize() > 3000) {
            throw new Exception("The page size allows up to 3000 records.");
        }
        if (criteria.getStartIndex() < 0) {
            throw new Exception("Cannot retrieve records for start index: " + 
                    criteria.getStartIndex() + ". The minimum start index is: 0");
        }

        Searcher<Site> searcher = new Searcher<>(criteria.getQuery(), Site.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        // Getting data only for the page
        //TODO: Need to change this when new story for pagination and data loading come up
        List<Site> aux = (List<Site>) getPersistenceContext().selectList(STATEMENT_GET_SITE_BY_CRITERIA,
                parameter, new RowBounds(criteria.getStartIndex(), 3000), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<Site>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = (Integer) getPersistenceContext().selectOne(STATEMENT_GET_SITE_NUMBER_OF_RECORDS_BY_CRITERIA, 
                parameter, session);
        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        result = new RecordSet<Site>(criteria.getStartIndex(), 3000, totalRecords, aux);
        return result;
    }

    @Override
    public Long exists(Site site, SqlSession session) throws Exception {
        return (Long) getPersistenceContext().selectOne(STATEMENT_CHECK_SITE_EXISTS_BY_NAME_AND_PUBLISHER_ID, site, session);
    }
    
    @Override
    public List<Site> checkSiteByName(String siteName , String userId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("siteName", siteName);
        parameter.put("userId", userId);
        List<Site> result = getPersistenceContext().selectMultiple(STATEMENT_CHECK_SITE_EXISTS_BY_NAME, parameter,  session);
        return result;
    }
    
    @Override
    public void create(Site site, SqlSession session) {
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", site.getId());
        parameter.put("publisherId", site.getPublisherId());
        parameter.put("name", site.getName());
        parameter.put("url", site.getUrl());
        parameter.put("preferredTag", site.getPreferredTag());
        parameter.put("richMedia", site.getRichMedia());
        parameter.put("acceptsFlash", site.getAcceptsFlash());
        parameter.put("clickTrack", site.getClickTrack());
        parameter.put("encode", site.getEncode());
        parameter.put("targetWin", site.getTargetWin());
        parameter.put("agencyNotes", site.getAgencyNotes());
        parameter.put("publisherNotes", site.getPublisherNotes());
        parameter.put("tpwsKey", site.getCreatedTpwsKey());
        getPersistenceContext().execute(STATEMENT_INSERT_SITE, parameter, session);
    }

    @Override
    public void update(Site site, SqlSession session) {
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", site.getId());
        parameter.put("name", site.getName());
        parameter.put("url", site.getUrl());
        parameter.put("preferredTag", site.getPreferredTag());
        parameter.put("richMedia", site.getRichMedia());
        parameter.put("acceptsFlash", site.getAcceptsFlash());
        parameter.put("clickTrack", site.getClickTrack());
        parameter.put("encode", site.getEncode());
        parameter.put("targetWin", site.getTargetWin());
        parameter.put("agencyNotes", site.getAgencyNotes());
        parameter.put("publisherNotes", site.getPublisherNotes());
        parameter.put("tpwsKey", site.getModifiedTpwsKey());
        getPersistenceContext().execute(STATEMENT_UPDATE_SITE, parameter, session);
    }

    @Override
    public void delete(Long id, OauthKey key, SqlSession session) {
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("tpwsKey", key.getTpws());
        getPersistenceContext().execute(STATEMENT_DELETE_SITE, parameter, session);
    }
    
    @Override
    public List<SiteContactView> getTraffickingSiteContacts(Long campaignId, SqlSession session) {
        return getPersistenceContext().selectMultiple(
                STATEMENT_GET_SITE_CONTACTS_BY_CAMPAIGNID, campaignId, session);
    }

    @Override
    public List<Site> getByCampaignIdAndPublisherNameSiteName(Collection<String> names,
                                                              String userId, Long campaignId,
                                                              final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", names);
        parameters.put("userId", userId);
        parameters.put("campaignId", campaignId);
        List<Site> result = new ArrayList<>();
        Accumulator<List<Site>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<Site>>(
                "names",
                new ArrayList<>(names),
                resultAccumulator,
                parameters) {
            @Override
            public List<Site> execute(Object parameters) {
                return getPersistenceContext()
                        .selectMultiple(STATEMENT_GET_BY_CAMPAIGNID_PUBLISHERNAME_SITENAME,
                                parameters, session);
            }
        }.getResults();
    }
}

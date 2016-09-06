package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeVersion;
import trueffect.truconnect.api.commons.model.ExtendedProperties;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.CreativeAssociationsDTO;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.SumAccumulatorImpl;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public class CreativeDaoImpl extends AbstractGenericDao implements CreativeDao {

    private static final String STATEMENT_GET_BY_ID = "CreativePkg.getCreative";
    private static final String STATEMENT_GET_CREATIVES_BY_IDS = "CreativePkg.getCreativesByIds";
    private static final String STATEMENT_GET_CREATIVE_VERSIONS_BY_ID = "CreativePkg.getCreativeVersionsByCreativeId";
    private static final String STATEMENT_GET_CREATIVE_BY_CAMPAIGN_ID_AND_FILENAME = "CreativePkg.getByCampaignIdAndFileName";
    private static final String STATEMENT_GET_CREATIVE_BY_CAMPAIGN_ID_AND_FILENAMES = "CreativePkg.getByCampaignIdAndFileNames";
    private static final String STATEMENT_GET_BY_CRITERIA = "CreativePkg.getCreativesByCriteria";
    private static final String STATEMENT_GET_NUMBER_RECORDS_BY_CRITERIA = "CreativePkg.getCreativesNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_COUNT_BY_CAMPAIGN_AND_IDS = "CreativePkg.getCountCreativesByCampaignIdAndIds";
    private static final String STATEMENT_CREATE = "CreativePkg.insertCreative";
    private static final String STATEMENT_CREATE_VERSION = "CreativePkg.insertCreativeVersion";
    private static final String STATEMENT_UPDATE_CREATIVE_ALIAS = "CreativePkg.updateCreativeAlias";
    private static final String STATEMENT_UPDATE_CREATIVE_VERSION_IS_DATE_SET_BY_CAMPAIG_ID = "CreativePkg.updateCreativeVersionIsDateSetByCampaignId";
    private static final String STATEMENT_UPDATE_CREATIVE_VERSION_ALIAS = "CreativePkg.updateCreativeVersionAlias";
    private static final String STATEMENT_UPDATE_CREATIVE_VERSION_ALIAS_BY_VERSION = "CreativePkg.updateCreativeAliasByVersion";

    private static final String STATEMENT_UPDATE = "CreativePkg.updateCreative";
    private static final String STATEMENT_DELETE = "CreativePkg.deleteCreative";
    private static final String STATEMENT_DELETE_CREATIVE_CLICKTHROUGHS = "CreativePkg.deleteCreativeClickThrough";
    private static final String STATEMENT_CREATE_CREATIVE_CLICKTHROUGHS = "CreativePkg.insertCreativeClickThrough";
    private static final String STATEMENT_GET_UNASSOCIATED_TO_GROUP = "CreativePkg.getUnassociatedToGroup";
    private static final String STATEMENT_COUNT_GET_UNASSOCIATED_TO_GROUP = "CreativePkg.countGetUnassociatedToGroup";
    private static final String STATEMENT_GET_BY_CAMPAIGN = "CreativePkg.getByCampaign";
    private static final String STATEMENT_COUNT_GET_BY_CAMPAIGN = "CreativePkg.countGetByCampaign";
    private static final String STATEMENT_GET_SCHEDULE_ASSOC_PER_GROUP_BY_CREATIVE_ID = "CreativePkg.getScheduleAssociationsPerGroupByCreativeId";
    private static final String STATEMENT_GET_DUP_VERSION_ALIAS_BY_CAMPAIGNID_CREATIVEID_ALIAS = "CreativePkg.getCreativeVersionAliasWithDupValue";

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private ExtendedPropertiesDao extendedPropertiesDao;

    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this
     * DAO
     */
    public CreativeDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
        extendedPropertiesDao = new ExtendedPropertiesDaoImpl(persistenceContext);
    }

    @Override
    public Creative get(Long id, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("creativeId", id);
        parameter.put("defaultCreativeVersion", Constants.DEFAULT_CREATIVE_INITIAL_VERSION);

        Creative creative = getPersistenceContext().selectSingle(STATEMENT_GET_BY_ID, parameter,
                session, Creative.class);
        creative.setExternalId(getCreativeExternalId(id, session));
        return creative;
    }

    @Override
    public Creative getCreativeByCampaignIdAndFileName(Long campaignId, String filename,
                                                       SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("filename", filename);
        parameter.put("defaultCreativeVersion", Constants.DEFAULT_CREATIVE_INITIAL_VERSION);
        Creative creative = getPersistenceContext()
                .selectSingle(STATEMENT_GET_CREATIVE_BY_CAMPAIGN_ID_AND_FILENAME, parameter,
                        session, Creative.class);
        return creative;
    }

    @Override
    public List<Creative> getByCampaignIdAndFileNames(Long campaignId, Collection<String> names,
                                                      final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("campaignId", campaignId);
        List<Creative> result = new ArrayList<>();
        Accumulator<List<Creative>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<Creative>>(
                "names",
                new ArrayList<>(names),
                resultAccumulator,
                parameters) {
            @Override
            public List<Creative> execute(Object parameters) {
                return getPersistenceContext()
                        .selectMultiple(STATEMENT_GET_CREATIVE_BY_CAMPAIGN_ID_AND_FILENAMES,
                                parameters, session);
            }
        }.getResults();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<Creative> getCreatives(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception {
        log.info("Search Criteria: " + criteria);
        log.info("UserId: " + key.getUserId());
        RecordSet<Creative> result = null;
        if (criteria.getPageSize() > 1000) {
            throw new Exception("The page size allows up to 1000 records.");
        }
        if (criteria.getStartIndex() < 0) {
            throw new Exception("Cannot retrieve records for start index: " + criteria.getStartIndex() + ". The minimum start index is: 0");
        }

        Searcher<Creative> searcher = new Searcher<>(criteria.getQuery(), Creative.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        log.info("Query Condition: " + searcher.getMybatisWheresCondition());
        log.info("Query Ordering: " + searcher.getMybatisOrderBySection());

        // Getting data only for the page
        List<Creative> aux = (List<Creative>) getPersistenceContext().selectList(STATEMENT_GET_BY_CRITERIA,
                parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<Creative>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = getPersistenceContext().selectOne(STATEMENT_GET_NUMBER_RECORDS_BY_CRITERIA, parameter, session, Integer.class);
        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        result = new RecordSet<>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
        return result;
    }

    @Override
    public Long getCountCreativesByCampaignIdAndIds(List<Long> ids, Long campaignId, final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("campaignId", campaignId);
        Long result = 0L;
        Accumulator<Long> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Long>("ids", ids, resultAccumulator, params) {
            @Override
            public Long execute(Object parameters) {
                return getPersistenceContext().selectSingle(STATEMENT_GET_COUNT_BY_CAMPAIGN_AND_IDS, parameters, session, Long.class);
            }
        }.getResults();
    }

    @Override
    public Creative create(Creative creative, OauthKey key, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", creative.getId());
        parameter.put("agencyId", creative.getAgencyId());
        parameter.put("campaignId", creative.getCampaignId());
        parameter.put("ownerCampaignId", creative.getOwnerCampaignId());
        parameter.put("filename", creative.getFilename());
        parameter.put("alias", creative.getAlias());
        parameter.put("creativeType", creative.getCreativeType());
        parameter.put("purpose", creative.getPurpose());
        parameter.put("width", creative.getWidth());
        parameter.put("height", creative.getHeight());
        parameter.put("clickthrough", creative.getClickthrough());
        parameter.put("scheduled", creative.getScheduled());
        parameter.put("released", creative.getReleased());
        parameter.put("extProp1", creative.getExtProp1());
        parameter.put("extProp2", creative.getExtProp2());
        parameter.put("extProp3", creative.getExtProp3());
        parameter.put("extProp4", creative.getExtProp4());
        parameter.put("extProp5", creative.getExtProp5());
        parameter.put("createdTpwsKey", key.getTpws());
        parameter.put("richMediaId", creative.getRichMediaId());
        parameter.put("swfClickCount", creative.getSwfClickCount());
        Long isExpandable = creative.getIsExpandable();
        // account for possibility that getIsExpandable() returns null
        // which is an issue, since the field CREATIVE.IS_EXPANDABLE cannot be null
        parameter.put("isExpandable", isExpandable != null ? isExpandable : 0L);
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_CREATE, parameter, session);

        HashMap<String, Object> creativeVersionParams = new HashMap<>();
        creativeVersionParams.put("campaignId", creative.getCampaignId());
        creativeVersionParams.put("creativeId", creative.getId());
        creativeVersionParams.put("alias", creative.getAlias());
        creativeVersionParams.put("versionNumber", Constants.DEFAULT_CREATIVE_INITIAL_VERSION);
        //For 3rd files which are not hosted by Trueffect we just update the start date immediately.
        setDateParams(creative, creativeVersionParams);
        getPersistenceContext().execute(STATEMENT_CREATE_VERSION, creativeVersionParams, session);

        Creative result = this.get(creative.getId(), session);
        return result;
    }

    private void setDateParams(Creative creative, HashMap<String, Object> creativeVersionParams){
        //For 3rd files which are not hosted by Trueffect we just update the start date immediately.
        if(creative.getCreativeType().equalsIgnoreCase(AdminFile.FileType.TRD.getFileType())){
            creativeVersionParams.put("isDateSet", Constants.ENABLED);
        }
        else { //for non-3rd the date set and start date should be set by trafficking
            creativeVersionParams.put("isDateSet", Constants.DISABLED);
        }
        // START_DATE will be default value as per DB
    }

    @Override
    public void update(Creative creative, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", creative.getId());
        parameter.put("alias", creative.getAlias());
        parameter.put("purpose", creative.getPurpose());
        parameter.put("width", creative.getWidth());
        parameter.put("height", creative.getHeight());
        parameter.put("clickthrough", creative.getClickthrough());
        parameter.put("scheduled", creative.getScheduled());
        parameter.put("released", creative.getReleased());
        parameter.put("modifiedTpwsKey", key.getTpws());
        parameter.put("extProp1", creative.getExtProp1());
        parameter.put("extProp2", creative.getExtProp2());
        parameter.put("extProp3", creative.getExtProp3());
        parameter.put("extProp4", creative.getExtProp4());
        parameter.put("extProp5", creative.getExtProp5());
        parameter.put("swfClickCount", creative.getSwfClickCount());
        parameter.put("isExpandable", creative.getIsExpandable());
        getPersistenceContext().execute(STATEMENT_UPDATE, parameter, session);
    }

    @Override
    public void insertCreativeVersion(Creative creative, SqlSession session) {
        HashMap<String, Object> creativeVersionParams = new HashMap<>();
        creativeVersionParams.put("campaignId", creative.getCampaignId());
        creativeVersionParams.put("creativeId", creative.getId());
        creativeVersionParams.put("alias", creative.getAlias());
        creativeVersionParams.put("versionNumber", creative.getCreativeVersion());

        // "startDate" and "isDateSet" are set by Trafficking
        // For 3rd files which are not hosted by Trueffect we just update the start date immediately.
        setDateParams(creative, creativeVersionParams);
        getPersistenceContext().execute(STATEMENT_CREATE_VERSION, creativeVersionParams, session);

        //update the alias in the creative table
        HashMap<String, Object> creativeAliasParams = new HashMap<>();
        creativeAliasParams.put("creativeId", creative.getId());
        creativeAliasParams.put("alias", creative.getAlias());

        getPersistenceContext().execute(STATEMENT_UPDATE_CREATIVE_ALIAS, creativeAliasParams,
                session);
    }

    @Override
    public void updateCreativeAlias(Long creativeId, String alias, OauthKey key,
                                    SqlSession session) {
        HashMap<String, Object> creativeAliasParams = new HashMap<>();
        creativeAliasParams.put("creativeId", creativeId);
        creativeAliasParams.put("alias", alias);
        creativeAliasParams.put("modifiedTpwsKey", key.getTpws());

        getPersistenceContext().execute(STATEMENT_UPDATE_CREATIVE_ALIAS, creativeAliasParams,
                session);
        getPersistenceContext().execute(STATEMENT_UPDATE_CREATIVE_VERSION_ALIAS,
                creativeAliasParams, session);
    }

    private String getCreativeExternalId(Long creativeId, SqlSession session){
        ExtendedProperties extProp = new ExtendedProperties();
        extProp.setObjectName("Creative");
        extProp.setFieldName("MediaID");
        extProp.setObjectId(creativeId);
        extProp = extendedPropertiesDao.getExtendedPropertyValue(extProp, session);
        if (extProp != null) {
            return extProp.getValue();
        }
        return null;
    }

    @Override
    public void remove(Long id, OauthKey key, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("modifiedTpwsKey", key.getTpws());
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_DELETE, parameter, session);
    }

    @Override
    public void removeCreativeClickThrough(Long creativeId, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("creativeId", creativeId);
        getPersistenceContext().execute(
                STATEMENT_DELETE_CREATIVE_CLICKTHROUGHS, parameter, session);
    }

    @Override
    public Creative saveCreativeClickThrough(Creative creative, OauthKey key, SqlSession session) {
        if(creative.getClickthroughs() != null && !creative.getClickthroughs().isEmpty()) {
            for (Clickthrough click : creative.getClickthroughs()) {
                HashMap<String, Object> parameter = new HashMap<>();
                parameter.put("creativeId", creative.getId());
                parameter.put("clickthrough", click.getUrl());
                parameter.put("sequence", click.getSequence());
                parameter.put("tpwsKey", key.getTpws());
                getPersistenceContext().execute(
                        STATEMENT_CREATE_CREATIVE_CLICKTHROUGHS, parameter, session);
            }
        }
        return creative;
    }

    @Override
    public List<Creative> getCreativesByIds(Collection<Long> ids, final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        List<Creative> result = new ArrayList<>();
        List<Long> idsList = new ArrayList<>(ids);
        Accumulator<List<Creative>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<Creative>>(
                "ids",
                idsList,
                resultAccumulator,
                params) {
            @Override
            public List<Creative> execute(Object parameters) {
                return getPersistenceContext().selectMultiple(STATEMENT_GET_CREATIVES_BY_IDS,
                        parameters, session);
            }

        }.getResults();
        // Do not delete.
        // Below lines should be re-enabled when this information is needed to be part of the result for this method.
        //creative.setExternalId(getCreativeExternalId(id, session));
    }

    @Override
    public List<CreativeVersion> getCreativeVersionsById(Long creativeId, SqlSession session){
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("creativeId", creativeId);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_CREATIVE_VERSIONS_BY_ID, parameter, session);
    }

    @Override
    public Long getCreativeIdByCampaignIdAndFileName(Long campaignId, String filename,
                                                     SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("filename", filename);
        parameter.put("defaultCreativeVersion", Constants.DEFAULT_CREATIVE_INITIAL_VERSION);
        Creative creative = getPersistenceContext()
                .selectSingle(STATEMENT_GET_CREATIVE_BY_CAMPAIGN_ID_AND_FILENAME, parameter,
                        session, Creative.class);
        return creative != null ? creative.getId() : null;
    }

    @Override
    public List<Creative> getCreativesWithNoGroupAssociation(Long campaignId, Long groupId, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("groupId", groupId);
        parameter.put("startIndex", startIndex);
        parameter.put("pageSize", pageSize);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_UNASSOCIATED_TO_GROUP, parameter, session);
    }

    @Override
    public Long getCountForCreativesWithNoGroupAssociation(Long campaignId, Long groupId, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("groupId", groupId);
        parameter.put("startIndex", startIndex);
        parameter.put("pageSize", pageSize);
        return getPersistenceContext().
                selectSingle(STATEMENT_COUNT_GET_UNASSOCIATED_TO_GROUP, parameter, session,
                        Long.class);
    }

    @Override
    public List<Creative> getCreativesByCampaign(Long campaignId, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("startIndex", startIndex);
        parameter.put("pageSize", pageSize);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_BY_CAMPAIGN, parameter, session);
    }

    @Override
    public Long getCountForCreativesByCampaign(Long campaignId, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("startIndex", startIndex);
        parameter.put("pageSize", pageSize);
        return getPersistenceContext().
                selectSingle(STATEMENT_COUNT_GET_BY_CAMPAIGN, parameter, session, Long.class);
    }

    @Override
    public List<CreativeAssociationsDTO> getScheduleAssocPerGroupByCreativeId(Long creativeId,
                                                                              SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("creativeId", creativeId);
        return getPersistenceContext()
                .selectMultiple(STATEMENT_GET_SCHEDULE_ASSOC_PER_GROUP_BY_CREATIVE_ID, parameter,
                        session);
    }

    @Override
    public int updateVersions(List<CreativeVersion> versions, SqlSession session) {
        int affected = 0;
        for (CreativeVersion version : versions) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("creativeId", version.getCreativeId());
            params.put("alias", version.getAlias());
            params.put("versionNumber", version.getVersionNumber());
            affected += getPersistenceContext()
                    .update(STATEMENT_UPDATE_CREATIVE_VERSION_ALIAS_BY_VERSION, params, session);
        }
        return affected;
    }

    @Override
    public List<Creative> getDupVersionAliasByCampaignAndCreativeIdAlias(Long campaignId, Collection<String> alias, final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("campaignId", campaignId);
        List<Creative> result = new ArrayList<>();
        Accumulator<List<Creative>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<Creative>>(
                "names",
                new ArrayList<>(alias),
                resultAccumulator,
                parameters) {
            @Override
            public List<Creative> execute(Object parameters) {
                return getPersistenceContext().selectMultiple(
                        STATEMENT_GET_DUP_VERSION_ALIAS_BY_CAMPAIGNID_CREATIVEID_ALIAS,
                        parameters, session);
            }
        }.getResults();
    }

    @Override
    public int updateCreativeVersionsToTrafficked(Long campaignId, SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("campaignId", campaignId);
        return getPersistenceContext().update(STATEMENT_UPDATE_CREATIVE_VERSION_IS_DATE_SET_BY_CAMPAIG_ID, params,
                session);
    }
}

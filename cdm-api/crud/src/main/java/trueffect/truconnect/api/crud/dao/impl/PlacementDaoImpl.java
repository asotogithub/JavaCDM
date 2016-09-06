package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.PackagePlacementView;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.SumAccumulatorImpl;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Richard Jaldin, Gustavo Claure
 */
public class PlacementDaoImpl extends AbstractGenericDao implements PlacementDao {
    
    private static final String STATEMENT_INSERT_PLACEMENT = "Placement.insertPlacement";
    private static final String STATEMENT_UPDATE_PLACEMENT = "Placement.updatePlacement";
    private static final String STATEMENT_UPDATE_PLACEMENT_ON_IMPORT = "Placement.updatePlacementOnImport";
    private static final String STATEMENT_UPDATE_PLACEMENTS_COSTDETAILS_BY_PACKAGE_ID = "Placement.updatePlacementsCostDetailByPackageId";
    private static final String STATEMENT_DELETE_PLACEMENT = "Placement.deletePlacement";
    private static final String STATEMENT_GET_PLACEMENT = "Placement.getPlacement";
    private static final String STATEMENT_GET_PLACEMENTS_BY_IDS = "Placement.getPlacementsByIds";
    private static final String STATEMENT_GET_PLACEMENTS_BY_CRITERIA = "Placement.getPlacementsByCriteria";
    private static final String STATEMENT_GET_PLACEMENTS_BY_SITE_SECTION_SIZE = "Placement.getPlacementsBySiteSectionSize";
    private static final String STATEMENT_GET_PLACEMENTS_FOR_PACKAGE_PLACEMENTS_VIEW = "Placement.placementPPV";
    private static final String STATEMENT_GET_PLACEMENTS_BY_PACKAGE_ID = "Placement.getPlacementsByPackageId";
    private static final String STATEMENT_GET_PLACEMENTS_BY_IO_ID = "Placement.getPlacementsByIoId";
    private static final String STATEMENT_GET_PACKAGES_FOR_PACKAGE_PLACEMENTS_VIEW = "Package.packagePPV";
    private static final String STATEMENT_GET_NUMBER_RECORDS_BY_CRITERIA = "Placement.getPlacementsNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_COUNT_PLACEMENTS_BY_CAMPAIGN = "Placement.getCountPlacementByCampaign";
    private static final String STATEMENT_CHECK_PLACEMENTS_STANDALONE = "Placement.checkPlacementsStandAlone";
    private static final String STATEMENT_GET_VIEW_BY_CREATIVE_INSERTION_FILTER_PARAM = "Placement.getPlacementsByFilterParam";
    private static final String STATEMENT_GET_VIEW_COUNT_BY_CREATIVE_INSERTION_FILTER_PARAM = "Placement.getCountPlacementsByFilterParam";
    private static final String STATEMENT_GET_VIEW_BY_CAMPAIGN_LEVEL = "Placement.getPlacementsViewCampaignLevel";
    private static final String STATEMENT_GET_VIEW_COUNT_BY_CAMPAIGN_LEVEL = "Placement.getCountPlacementsViewCampaignLevel";
    private static final String STATEMENT_GET_VIEW_BY_SITE_LEVEL = "Placement.getPlacementsViewSiteLevel";
    private static final String STATEMENT_GET_VIEW_COUNT_BY_SITE_LEVEL = "Placement.getCountPlacementsViewSiteLevel";
    private static final String STATEMENT_GET_VIEW_BY_SECTION_LEVEL = "Placement.getPlacementsViewSectionLevel";
    private static final String STATEMENT_GET_VIEW_COUNT_BY_SECTION_LEVEL = "Placement.getCountPlacementsViewSectionLevel";
    private static final String STATEMENT_GET_VIEW_BY_PLACEMENT_LEVEL = "Placement.getPlacementsViewPlacementLevel";
    private static final String STATEMENT_GET_VIEW_COUNT_BY_PLACEMENT_LEVEL = "Placement.getCountPlacementsViewPlacementLevel";
    private static final String STATEMENT_GET_VIEW_PLACEMENT_BY_BRAND = "Placement.getPlacementByAdvertiserAndBrand";
    private static final String STATEMENT_GET_SEARCH_VIEW_BY_PATTERN = "Placement.searchPlacementView";
    private static final String STATEMENT_GET_COUNT_SEARCH_VIEW_BY_PATTERN = "Placement.searchPlacementViewCount";
    private static final String STATEMENT_GET_MEDIA_FOR_EXPORT = "Placement.getMediaForExport";
    private static final String STATEMENT_GET_COUNT_CAMPAIGNSITE_OF_PLACEMENTS_BY_IDS = "Placement.getCountCampaignSiteOfPlacementsByIds";
    private static final String STATEMENT_GET_COUNT_CAMPAIGNSITESECTION_OF_PLACEMENTS_BY_IDS = "Placement.getCountCampaignSiteSectionOfPlacementsByIds";
    private static final String STATEMENT_GET_COUNT_CAMPAIGNSITESECTIONPLACEMENT_OF_PLACEMENTS_BY_IDS = "Placement.getCountCampaignSiteSectionPlacementOfPlacementsByIds";
    private static final String STATEMENT_GET_MEDIA_PLACEMENTS_BY_PKG_PLAC_IDS = "Placement.getMediaPlacementByPkgPlacIds";
    private static final String STATEMENT_GET_PLACEMENT_BY_TAG_ID = "Placement.getPlacementsByTagId";
    private static final String STATEMENT_GET_COUNT_PLACEMENT_BY_TAG_ID = "Placement.getPlacementsByTagIdCount";
    private static final String STATEMENT_SEARCH_PLACEMENT_ASSOCIATED_TAG_BY_PATTERN = "Placement.searchPlacementsRelatedTagByPattern";
    private static final String STATEMENT_GET_COUNT_SEARCH_PLACEMENT_ASSOCIATED_TAG_BY_PATTERN = "Placement.getSearhPlacementsRelatedTagByPatternCount";

    public PlacementDaoImpl(PersistenceContext context) {
        super(context);
    }

    @Override
    public Placement create(Placement placement, SqlSession session) {
        Long id = getNextId(session);
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("siteId", placement.getSiteId());
        parameter.put("sectionId", placement.getSiteSectionId());
        parameter.put("sizeId", placement.getSizeId());
        parameter.put("ioId", placement.getIoId());
        parameter.put("startDate", placement.getStartDate());
        parameter.put("endDate", placement.getEndDate());
        parameter.put("inventory", placement.getInventory());
        parameter.put("rate", placement.getRate());
        parameter.put("rateType", placement.getRateType());
        parameter.put("maxFileSize", placement.getMaxFileSize());
        parameter.put("isSecure", placement.getIsSecure());
        parameter.put("utcOffset", placement.getUtcOffset());
        parameter.put("smEventId", placement.getSmEventId());
        parameter.put("tpwsKey", placement.getCreatedTpwsKey());
        parameter.put("countryCurrencyId", placement.getCountryCurrencyId());
        parameter.put("name", placement.getName());
        parameter.put("extProp1", placement.getExtProp1());
        parameter.put("extProp2", placement.getExtProp2());
        parameter.put("extProp3", placement.getExtProp3());
        parameter.put("extProp4", placement.getExtProp4());
        parameter.put("extProp5", placement.getExtProp5());
        getPersistenceContext().execute(STATEMENT_INSERT_PLACEMENT, parameter, session);

        return get(id, session);
    }

    @Override
    public Placement update(Placement placement, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", placement.getId());
        parameter.put("sizeId", placement.getSizeId());
        parameter.put("startDate", placement.getStartDate());
        parameter.put("endDate", placement.getEndDate());
        parameter.put("inventory", placement.getInventory());
        parameter.put("rate", placement.getRate());
        parameter.put("rateType", placement.getRateType());
        parameter.put("maxFileSize", placement.getMaxFileSize());
        parameter.put("isSecure", placement.getIsSecure());
        parameter.put("utcOffset", placement.getUtcOffset());
        parameter.put("smEventId", placement.getSmEventId());
        parameter.put("modifiedTpwsKey", placement.getModifiedTpwsKey());
        parameter.put("countryCurrencyId", placement.getCountryCurrencyId());
        parameter.put("name", placement.getName());
        parameter.put("extProp1", placement.getExtProp1());
        parameter.put("extProp2", placement.getExtProp2());
        parameter.put("extProp3", placement.getExtProp3());
        parameter.put("extProp4", placement.getExtProp4());
        parameter.put("extProp5", placement.getExtProp5());
        getPersistenceContext().execute(STATEMENT_UPDATE_PLACEMENT, parameter, session);
        return get(placement.getId(), session);
    }

    @Override
    public Placement updateOnImport(Placement placement, SqlSession session) {
        getPersistenceContext().execute(STATEMENT_UPDATE_PLACEMENT_ON_IMPORT, placement, session);
        return get(placement.getId(), session);
    }

    @Override
    public int updateDataCostDetailPlacementsByPackageId(Placement placement, Long packageId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("startDate", placement.getStartDate());
        parameter.put("endDate", placement.getEndDate());
        parameter.put("inventory", placement.getInventory());
        parameter.put("rate", placement.getRate());
        parameter.put("rateType", placement.getRateType());
        parameter.put("modifiedTpwsKey", placement.getModifiedTpwsKey());
        parameter.put("packageId", packageId);
        return getPersistenceContext().update(STATEMENT_UPDATE_PLACEMENTS_COSTDETAILS_BY_PACKAGE_ID, parameter, session);
    }

    @Override
    public void delete(Long id, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("modifiedTpwsKey", key.getTpws());
        getPersistenceContext().execute(STATEMENT_DELETE_PLACEMENT, parameter, session);
    }

    @Override
    public Placement get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_PLACEMENT, id, session, Placement.class);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<Placement> getPlacements(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception {
        Searcher<Placement> searcher = new Searcher<>(criteria.getQuery(), Placement.class);
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        // Getting data only for the page
        List<Placement> aux = getPersistenceContext().selectMultiple(STATEMENT_GET_PLACEMENTS_BY_CRITERIA,
                parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<Placement>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = getPersistenceContext().selectSingle(
                STATEMENT_GET_NUMBER_RECORDS_BY_CRITERIA, parameter, session, Integer.class);
        if (criteria.getStartIndex() > totalRecords) {
            throw new Exception("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                    + " because the starting index should be less than " + totalRecords);
        }
        return new RecordSet<>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
    }

    @Override
    public List<Placement> getPlacementsByPackageId(Long packageId,
                                                         SqlSession session) {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("packageId", packageId);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PLACEMENTS_BY_PACKAGE_ID,
                parameters, session);
    }

    @Override
    public List<Placement> getPlacementsByIoId(Long ioId,
                                               InsertionOrderStatusEnum ioStatus,
                                               SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ioId", ioId);
        if(ioStatus != null) {
            parameters.put("statusName", ioStatus.getStatusName());
        }
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PLACEMENTS_BY_IO_ID,
                parameters, session);
    }

    @Override
    public boolean checkPlacementsBelongsCampaignId(List<Long> ids, Long campaignId, SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", ids);
        parameters.put("campaignId", campaignId);
        // Possible results are 'true' or 'false'
        String result = getPersistenceContext().selectSingle(STATEMENT_GET_COUNT_PLACEMENTS_BY_CAMPAIGN, 
                parameters, session, String.class);
        return Boolean.valueOf(result);        
    }    

    @Override
    public Long checkPlacementsStandAlone(List<Long> placementIds, Long packageId, SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", placementIds);
        parameters.put("packageId", packageId);
        return getPersistenceContext().selectSingle(STATEMENT_CHECK_PLACEMENTS_STANDALONE, parameters, session, Long.class);
    }    
    
    @Override
    public List<PackagePlacementView> getPlacementsForPackagePlacementView(Long id, String paramName, SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(paramName, id);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PLACEMENTS_FOR_PACKAGE_PLACEMENTS_VIEW, parameters, session);
    }
    
    @Override
    public List<PackagePlacementView> getPackagesForPackagePlacementView(Long id, String paramName, SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(paramName, id);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PACKAGES_FOR_PACKAGE_PLACEMENTS_VIEW, parameters, session);
    }

    @Override
    public List<Placement> getPlacementsByIds(Collection<Long> ids, final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        List<Placement> placements = new ArrayList<>();
        Accumulator<List<Placement>> resultAccumulator = new CollectionAccumulatorImpl<>(placements);
        return (new ResultSetAccumulatorImpl<List<Placement>>(
                "ids",
                new ArrayList<>(ids),
                resultAccumulator,
                parameters) {
            @Override
            public List<Placement> execute(Object parameters) {
                return getPersistenceContext().selectMultiple(STATEMENT_GET_PLACEMENTS_BY_IDS, parameters, session);
            }
        }).getResults();
    }

    @Override
    public List<PlacementView> getPlacementsByFilterParam(Long campaignId, CreativeInsertionFilterParam filterParam, String userId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("userId", userId);
        parameter.put("pivotType", filterParam.getPivotType());
        parameter.put("type", filterParam.getType());
        parameter.put("campaignId", campaignId);

        parameter.put("siteId", filterParam.getSiteId());
        parameter.put("sectionId", filterParam.getSectionId());
        parameter.put("placementId", filterParam.getPlacementId());
        parameter.put("groupId", filterParam.getGroupId());
        parameter.put("creativeId", filterParam.getCreativeId());
        List<PlacementView> result = getPersistenceContext().selectMultiple(STATEMENT_GET_VIEW_BY_CREATIVE_INSERTION_FILTER_PARAM, parameter, session);
        return result;
    }

    @Override
    public Long getCountPlacementsByFilterParam(Long campaignId, CreativeInsertionFilterParam filterParam, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("pivotType", filterParam.getPivotType());
        parameter.put("type", filterParam.getType());
        parameter.put("campaignId", campaignId);
        parameter.put("siteId", filterParam.getSiteId());
        parameter.put("sectionId", filterParam.getSectionId());
        parameter.put("placementId", filterParam.getPlacementId());
        parameter.put("groupId", filterParam.getGroupId());
        parameter.put("creativeId", filterParam.getCreativeId());

        return getPersistenceContext().selectSingle(STATEMENT_GET_VIEW_COUNT_BY_CREATIVE_INSERTION_FILTER_PARAM, parameter, session, Long.class);
    }

    @Override
    public List<PlacementView> getPlacementsViewByLevelType(Long agencyId, Long advertiserId,
                                                            Long brandId,
                                                            PlacementFilterParam filterParam,
                                                            PlacementFilterParamLevelTypeEnum levelType,
                                                            Long startIndex, Long pageSize,
                                                            SqlSession session) {

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("agencyId", agencyId);
        parameter.put("advertiserId", advertiserId);
        parameter.put("brandId", brandId);
        parameter.put("campaignId", filterParam.getCampaignId());
        parameter.put("siteId", filterParam.getSiteId());
        parameter.put("sectionId", filterParam.getSectionId());

        String statement = "";
        switch (levelType) {
            case CAMPAIGN:
                statement = STATEMENT_GET_VIEW_BY_CAMPAIGN_LEVEL;
                break;
            case SITE:
                statement = STATEMENT_GET_VIEW_BY_SITE_LEVEL;
                break;
            case SECTION:
                statement = STATEMENT_GET_VIEW_BY_SECTION_LEVEL;
                break;
            case PLACEMENT:
                statement = STATEMENT_GET_VIEW_BY_PLACEMENT_LEVEL;
                break;
        }
        return getPersistenceContext().selectMultiple(statement, parameter,
                new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
    }

    @Override
    public Long getCountPlacementsViewByLevelType(Long agencyId, Long advertiserId, Long brandId,
                                                  PlacementFilterParam filterParam,
                                                  PlacementFilterParamLevelTypeEnum levelType,
                                                  SqlSession session) {

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("agencyId", agencyId);
        parameter.put("advertiserId", advertiserId);
        parameter.put("brandId", brandId);
        parameter.put("campaignId", filterParam.getCampaignId());
        parameter.put("siteId", filterParam.getSiteId());
        parameter.put("sectionId", filterParam.getSectionId());

        String statement = "";
        switch (levelType) {
            case CAMPAIGN:
                statement = STATEMENT_GET_VIEW_COUNT_BY_CAMPAIGN_LEVEL;
                break;
            case SITE:
                statement = STATEMENT_GET_VIEW_COUNT_BY_SITE_LEVEL;
                break;
            case SECTION:
                statement = STATEMENT_GET_VIEW_COUNT_BY_SECTION_LEVEL;
                break;
            case PLACEMENT:
                statement = STATEMENT_GET_VIEW_COUNT_BY_PLACEMENT_LEVEL;
                break;
        }
        return getPersistenceContext().selectSingle(statement, parameter, session, Long.class);
    }

    @Override
    public List<PlacementView> searchPlacementsViewByPattern(Long agencyId, Long advertiserId,
                                                             Long brandId, String pattern,
                                                             PlacementSearchOptions searchOptions,
                                                             Long startIndex, Long pageSize,
                                                             SqlSession session) {

        List<PlacementView> result = null;
        if (searchOptions.isCampaign() || searchOptions.isSite() || searchOptions
                .isSection() || searchOptions.isPlacement()) {

            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("agencyId", agencyId);
            parameter.put("advertiserId", advertiserId);
            parameter.put("brandId", brandId);
            parameter.put("pattern", pattern);
            parameter.put("soCampaign", searchOptions.isCampaign());
            parameter.put("soSite", searchOptions.isSite());
            parameter.put("soSection", searchOptions.isSection());
            parameter.put("soPlacement", searchOptions.isPlacement());

            result = getPersistenceContext()
                    .selectMultiple(STATEMENT_GET_SEARCH_VIEW_BY_PATTERN, parameter,
                            new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
        }
        return result;
    }

    @Override
    public Long searchCountPlacementsViewByPattern(Long agencyId, Long advertiserId, Long brandId,
                                                   String pattern,
                                                   PlacementSearchOptions searchOptions,
                                                   SqlSession session) {

        Long result = 0L;
        if (searchOptions.isCampaign() || searchOptions.isSite() || searchOptions
                .isSection() || searchOptions.isPlacement()) {
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("agencyId", agencyId);
            parameter.put("advertiserId", advertiserId);
            parameter.put("brandId", brandId);
            parameter.put("pattern", pattern);
            parameter.put("soCampaign", searchOptions.isCampaign());
            parameter.put("soSite", searchOptions.isSite());
            parameter.put("soSection", searchOptions.isSection());
            parameter.put("soPlacement", searchOptions.isPlacement());

            result = getPersistenceContext()
                    .selectSingle(STATEMENT_GET_COUNT_SEARCH_VIEW_BY_PATTERN, parameter,
                            session, Long.class);
        }
        return result;
    }

    @Override
    public List<Placement> getBySiteSectionAndSize(Collection<String> names, String userId, Long campaignId, final SqlSession session){
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", names);
        parameters.put("userId", userId);
        parameters.put("campaignId", campaignId);
        List<Placement> result = new ArrayList<>();
        Accumulator<List<Placement>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<Placement>>(
                "names",
                new ArrayList<>(names),
                resultAccumulator,
                parameters) {
                    @Override
                    public List<Placement> execute(Object parameters) {
                        return getPersistenceContext().selectMultiple(STATEMENT_GET_PLACEMENTS_BY_SITE_SECTION_SIZE,
                                parameters, session);
                    }
                }.getResults();
    }

    @Override
    public List<PlacementView> getPlacementsByBrand(Long brandId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("brandId", brandId);
        List<PlacementView> result = getPersistenceContext().selectMultiple(STATEMENT_GET_VIEW_PLACEMENT_BY_BRAND,parameter, session);
        return result;
    }

    @Override
    public List<MediaRawDataView> getMediaForExport(Long campaignId, SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("campaignId", campaignId);
        return getPersistenceContext().selectMultiple(STATEMENT_GET_MEDIA_FOR_EXPORT, parameters,
                session);
    }

    @Override
    public Long getCountCampaignSiteOfPlacementsByIds(List<String> campaignSiteIds,
                                                      final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        Long result = 0L;
        Accumulator<Long> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Long>("ids", campaignSiteIds, resultAccumulator,
                params) {
            @Override
            public Long execute(Object parameters) {
                return getPersistenceContext()
                        .selectSingle(STATEMENT_GET_COUNT_CAMPAIGNSITE_OF_PLACEMENTS_BY_IDS,
                                parameters,
                                session, Long.class);
            }
        }.getResults();
    }

    @Override
    public Long getCountCampaignSiteSectionOfPlacementsByIds(List<String> campaignSiteSectionIds,
                                                             final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        Long result = 0L;
        Accumulator<Long> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Long>("ids", campaignSiteSectionIds, resultAccumulator,
                params) {
            @Override
            public Long execute(Object parameters) {
                return getPersistenceContext()
                        .selectSingle(STATEMENT_GET_COUNT_CAMPAIGNSITESECTION_OF_PLACEMENTS_BY_IDS,
                                parameters,
                                session, Long.class);
            }
        }.getResults();
    }

    @Override
    public Long getCountCampaignSiteSectionPlacementOfPlacementsByIds(
            List<String> campaignSiteSectionPlacIds, final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        Long result = 0L;
        Accumulator<Long> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Long>("ids", campaignSiteSectionPlacIds,
                resultAccumulator, params) {
            @Override
            public Long execute(Object parameters) {
                return getPersistenceContext()
                        .selectSingle(
                                STATEMENT_GET_COUNT_CAMPAIGNSITESECTIONPLACEMENT_OF_PLACEMENTS_BY_IDS,
                                parameters,
                                session, Long.class);
            }
        }.getResults();
    }

    @Override
    public List<MediaRawDataView> getMediaPlacementByPkgPlacIds(List<String> pkgPlacIds,
                                                                Long campaignId,
                                                                final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("campaignId", campaignId);
        parameters.put("timezone", TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
        List<MediaRawDataView> result = new ArrayList<>();
        Accumulator<List<MediaRawDataView>> resultAccumulator =
                new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<MediaRawDataView>>(
                "pkgPlacIds",
                pkgPlacIds,
                resultAccumulator,
                parameters) {
            @Override
            public List<MediaRawDataView> execute(Object parameters) {
                return getPersistenceContext()
                        .selectMultiple(STATEMENT_GET_MEDIA_PLACEMENTS_BY_PKG_PLAC_IDS,
                                parameters, session);
            }
        }.getResults();
    }

    @Override
    public List<MediaRawDataView> getMediaPlacementByUserAndIds(List<Long> ids, Long campaignId,
                                                                String userId,
                                                                final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("campaignId", campaignId);
        parameters.put("timezone", TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
        List<MediaRawDataView> result = new ArrayList<>();
        Accumulator<List<MediaRawDataView>> resultAccumulator =
                new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<MediaRawDataView>>(
                "ids",
                ids,
                resultAccumulator,
                parameters) {
            @Override
            public List<MediaRawDataView> execute(Object parameters) {
                return getPersistenceContext()
                        .selectMultiple(STATEMENT_GET_MEDIA_PLACEMENTS_BY_PKG_PLAC_IDS,
                                parameters, session);
            }
        }.getResults();
    }

    @Override
    public List<PlacementView> getPlacementsByTagId(Long tagId, Long startIndex, Long pageSize,
                                                    SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("tagId", tagId);
        List<PlacementView> result =  getPersistenceContext().selectMultiple(
                STATEMENT_GET_PLACEMENT_BY_TAG_ID, parameter,
                new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
        return result;
    }

    @Override
    public Long getCountPlacementsByTagId(Long tagId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("tagId", tagId);
        return getPersistenceContext()
                .selectSingle(STATEMENT_GET_COUNT_PLACEMENT_BY_TAG_ID, parameter,
                        session, Long.class);
    }

    @Override
    public List<PlacementView> searchPlacementsAssociatedTagByPattern(Long tagId, String pattern,
                                                                      PlacementSearchOptions searchOptions,
                                                                      Long startIndex,
                                                                      Long pageSize,
                                                                      SqlSession session) {

        List<PlacementView> result = null;
        if (searchOptions.isCampaign() || searchOptions.isSite() || searchOptions.isPlacement()) {

            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("tagId", tagId);
            parameter.put("pattern", pattern);
            parameter.put("soCampaign", searchOptions.isCampaign());
            parameter.put("soSite", searchOptions.isSite());
            parameter.put("soPlacement", searchOptions.isPlacement());

            result = getPersistenceContext()
                    .selectMultiple(STATEMENT_SEARCH_PLACEMENT_ASSOCIATED_TAG_BY_PATTERN, parameter,
                            new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
        }
        return result;
    }

    @Override
    public Long getCountSearchPlacementsAssociatedTagByPattern(Long tagId, String pattern,
                                                               PlacementSearchOptions searchOptions,
                                                               SqlSession session) {

        Long result = 0L;
        if (searchOptions.isCampaign() || searchOptions.isSite() || searchOptions
                .isSection() || searchOptions.isPlacement()) {
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("tagId", tagId);
            parameter.put("pattern", pattern);
            parameter.put("soCampaign", searchOptions.isCampaign());
            parameter.put("soSite", searchOptions.isSite());
            parameter.put("soPlacement", searchOptions.isPlacement());

            result = getPersistenceContext()
                    .selectSingle(STATEMENT_GET_COUNT_SEARCH_PLACEMENT_ASSOCIATED_TAG_BY_PATTERN,
                            parameter,
                            session, Long.class);
        }
        return result;
    }
}

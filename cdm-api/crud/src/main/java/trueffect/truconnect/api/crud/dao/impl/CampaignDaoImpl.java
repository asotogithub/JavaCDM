package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupDtoForCampaigns;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.SumAccumulatorImpl;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Rambert Rioja
 * @edited Richard Jaldin
 */
public class CampaignDaoImpl extends AbstractGenericDao implements CampaignDao {

    private static final String CHECK_FOR_DUPLICATE_NAME = "Campaign.checkForDuplicateName";
    private static final String CREATIVE_GROUP_INSERT_CREATIVE_GROUP = "CreativeGroup.insertCreativeGroup";
    private static final String GET_ADVERTISER_ID = "Campaign.getAdvertiserId";
    private static final String GET_ADVERTISER_ID_ELSE = "Campaign.getAdvertiserIdElse";
    private static final String GET_CAMPAIGN_COUNT = "Campaign.getCampaignCount";
    private static final String GET_CREATIVE_GROUPS_FOR_CAMPAIGN = "Campaign.getCreativeGroupsForCampaign";
    private static final String GET_CURRENT_STATUS = "Campaign.getCurrentStatus";
    private static final String GET_LIMIT_ADVERTISERS = "Campaign.getLimitAdvertisers";
    private static final String GET_ONLY_CAMPAIGN = "Campaign.getOnlyCampaign";
    private static final String INSERT_MEDIA_BUY = "Campaign.insertMediaBuy";
    private static final String INSERT_MEDIA_BUY_CAMPAIGN = "Campaign.insertMediaBuyCampaign";
    private static final String SAVE_CAMPAIGN = "Campaign.saveCampaign";
    private static final String SET_STATUS = "Campaign.setStatus";
    private static final String SET_STATUS_IF_TRAFFICKED = "Campaign.setStatusIfTrafficked";
    private static final String UPDATE_CAMPAIGN = "Campaign.updateCampaign";
    private static final String STATEMENT_UPDATE_COOKIE_DOMAIN = "Campaign.updateDomainId";
    private static final String STATEMENT_GET_COUNT_BY_ADVERTISER_BRAND_ID = "Campaign.getCountCampaignsByAdvertiserBrand";
    private static final String STATEMENT_COUNT_CREATIVE_GROUPS_FOR_CAMPAIGN = "Campaign.getCountCreativeGroupsForCampaign";

    public CampaignDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    public Campaign get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(GET_ONLY_CAMPAIGN, id, session, Campaign.class);
    }

    @Override
    public int update(Campaign campaign, SqlSession session) throws Exception {
        return getPersistenceContext().update(UPDATE_CAMPAIGN, campaign, session);
    }

    @Override
    public void updateOverallBudget(Campaign campaign, SqlSession session) throws Exception {
        //Get MediaBuy by Campaign's id
        MediaBuy mediaBuy = (MediaBuy) getPersistenceContext().selectOne(MediaBuyDaoImpl.STATEMENT_GET_BY_CAMPAIGN_ID, campaign.getId());
        //Set Overall Budget and update the MediaBuy
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", mediaBuy.getId());
        parameter.put("name", mediaBuy.getName());
        parameter.put("overallBudget", campaign.getOverallBudget());
        parameter.put("agencyNotes", mediaBuy.getAgencyNotes());
        parameter.put("tpwsKey", campaign.getModifiedTpwsKey());

        getPersistenceContext().callPlSqlStoredProcedure(MediaBuyDaoImpl.STATEMENT_UPDATE, parameter, session);
    }

    @Override
    public Boolean isDuplicate(Campaign campaign, SqlSession session) throws Exception {
        Long result = (Long) getPersistenceContext().selectOne(CHECK_FOR_DUPLICATE_NAME, campaign, session);
        return result.compareTo(0L) > 0;
    }

    @Override
    public String getLimitAdvertisers(String id, SqlSession session) throws Exception {
        return getPersistenceContext().selectOne(GET_LIMIT_ADVERTISERS, id, session).toString();
    }

    @Override
    public Long getAdvertiserId(String userId, Long brandId, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("userId", userId);
        parameter.put("brandId", brandId);
        return getPersistenceContext().selectSingle(GET_ADVERTISER_ID, parameter, session, Long.class);
    }

    @Override
    public Long getAdvertiserIdElse(String userId, Long brandId, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("userId", userId);
        parameter.put("brandId", brandId);
        return getPersistenceContext().selectSingle(GET_ADVERTISER_ID_ELSE, parameter, session, Long.class);
    }

    @Override
    public Long getCampaignCount(String name, Long brandId, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("name", name);
        parameter.put("brandId", brandId);
        return getPersistenceContext().selectSingle(GET_CAMPAIGN_COUNT, parameter, session, Long.class);
    }

    @Override
    public void saveCampaign(Long id, Campaign campaign, Long advertiserId, OauthKey key, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", id);
        parameter.put("cookieDomainId", campaign.getCookieDomainId());
        parameter.put("advertiserId", advertiserId);
        parameter.put("agencyId", campaign.getAgencyId());
        parameter.put("brandId", campaign.getBrandId());
        parameter.put("name", campaign.getName());
        parameter.put("description", campaign.getDescription());
        parameter.put("tpwsKey", key.getTpws());
        parameter.put("startDate", campaign.getStartDate());
        parameter.put("endDate", campaign.getEndDate());
        parameter.put("resourcePathId", campaign.getResourcePathId());

        getPersistenceContext().callPlSqlStoredProcedure(SAVE_CAMPAIGN, parameter, session);
    }

    @Override
    public Long getMediaBuyId(SqlSession session) throws Exception {
        return getPersistenceContext().selectSingle("getNextId", "SEQ_MEDIA_BUY", session, Long.class);
    }

    @Override
    public void saveMediaBuy(Long id, Long mediaBuyId, Campaign campaign, OauthKey key, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", id);
        parameter.put("mediaBuyId", mediaBuyId);
        parameter.put("agencyId", campaign.getAgencyId());
        parameter.put("mbName", "Default Media Buy for Campaign " + campaign.getName().trim() + "(" + id + ")");
        parameter.put("mbState", "New");
        parameter.put("mbBudget", campaign.getOverallBudget() != null ? campaign.getOverallBudget() : 0.0);
        parameter.put("tpwsKey", key.getTpws());
        getPersistenceContext().callPlSqlStoredProcedure(INSERT_MEDIA_BUY, parameter, session);
    }

    @Override
    public void saveMediaBuyCampaign(Long id, Long mediaBuyId, OauthKey key, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", id);
        parameter.put("mediaBuyId", mediaBuyId);
        parameter.put("tpwsKey", key.getTpws());
        getPersistenceContext().callPlSqlStoredProcedure(INSERT_MEDIA_BUY_CAMPAIGN, parameter, session);
    }

    @Override
    public void saveCreativeGroup(Long id, OauthKey key, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("campaignId", id);
        parameter.put("name", "Default");
        parameter.put("rotationType", Constants.DEFAULT_ROTATION_TYPE);
        parameter.put("impressionCap", Constants.DEFAULT_IMPRESSION_CAP);
        parameter.put("ctCap", Constants.DEFAULT_CLICKTHROUGH_CAP);
        parameter.put("weight", Constants.DEFAULT_WEIGHT);
        parameter.put("isReleased", Constants.DEFAULT_IS_RELEASED);
        parameter.put("isDefault", Constants.DEFAULT_IS_DEFAULT);
        parameter.put("doOpt", Constants.DEFAULT_DO_OPTIMIZATION);
        parameter.put("optType", Constants.DEFAULT_OPTIMIZATION_TYPE);
        parameter.put("optSpeed", Constants.DEFAULT_OPTIMIZATION_SPEED);
        parameter.put("minOptWeight", Constants.DEFAULT_MIN_OPTIMIZATION_WEIGHT);
        parameter.put("doGeoTargeting", Constants.DEFAULT_DO_GEO_TARGETING);
        parameter.put("doCookieTarget", Constants.DEFAULT_DO_COOKIE_TARGETING);
        parameter.put("doDaypartTarget", Constants.DEFAULT_DO_DAYPART_TARGETING);
        parameter.put("frequencyCap", Constants.DEFAULT_FREQUENCY_CAP);
        parameter.put("frequencyCapWindow", Constants.DEFAULT_FREQUENCY_CAP_WINDOW);
        parameter.put("enableGroupWeight", Constants.DEFAULT_ENABLE_GROUP_WEIGHT);
        parameter.put("tpwsKey", key.getTpws());
        getPersistenceContext().callPlSqlStoredProcedure(CREATIVE_GROUP_INSERT_CREATIVE_GROUP, parameter, session);
    }

    @Override
    public Long getCampaignStatus(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(GET_CURRENT_STATUS, id, session, Long.class);
    }

    @Override
    public void setCampaignStatus(Long id, Long statusId, SqlSession session){
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", id);
        parameter.put("statusId", statusId);
        getPersistenceContext().execute(SET_STATUS, parameter, session);
    }

    @Override
    public int updateCookieDomainId(Campaign campaign, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaign.getId());
        parameter.put("cookieDomainId", campaign.getCookieDomainId());
        parameter.put("modifiedTpwsKey", campaign.getModifiedTpwsKey());
        return getPersistenceContext().update(STATEMENT_UPDATE_COOKIE_DOMAIN, parameter, session);
    }

    @Override
    public void setCampaignStatusIfTrafficked(Long id, Long statusId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("id", id);
        parameter.put("statusId", statusId);
        getPersistenceContext().execute(SET_STATUS_IF_TRAFFICKED, parameter, session);
    }

    @Override
    public List<CreativeGroupDtoForCampaigns> getCreativeGroupList(Long campaignId, Long startIndex, Long pageSize,
                                                                   SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("superiorLimit", startIndex + pageSize);
        parameter.put("inferiorLimit", startIndex);
        return getPersistenceContext().selectMultiple(GET_CREATIVE_GROUPS_FOR_CAMPAIGN, parameter, session);

    }

    @Override
    public Long getCountCampaignsByAdvertiserAndBrandIds(Long advertiserId, Long brandId,
                                                         List<Long> campaignIds,
                                                         final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("advertiserId", advertiserId);
        params.put("brandId", brandId);
        Long result = 0L;
        Accumulator<Long> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Long>("ids", campaignIds, resultAccumulator, params) {
            @Override
            public Long execute(Object parameters) {
                return getPersistenceContext()
                        .selectSingle(STATEMENT_GET_COUNT_BY_ADVERTISER_BRAND_ID, parameters,
                                session, Long.class);
            }
        }.getResults();
    }

    @Override
    public Long getCountCreativeGroupList (Long campaignId, SqlSession session){
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        return getPersistenceContext()
                .selectSingle(STATEMENT_COUNT_CREATIVE_GROUPS_FOR_CAMPAIGN, parameter,
                        session, Long.class);
    }
}

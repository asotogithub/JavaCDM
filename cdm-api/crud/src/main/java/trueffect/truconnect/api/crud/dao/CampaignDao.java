package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupDtoForCampaigns;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public interface CampaignDao extends GenericDao {

    Campaign get(Long id, SqlSession session);

    int update(Campaign campaign, SqlSession session) throws Exception;

    Boolean isDuplicate(Campaign campaign, SqlSession session) throws Exception;

    String getLimitAdvertisers(String id, SqlSession session) throws Exception;

    Long getAdvertiserId(String userId, Long brandId, SqlSession session) throws Exception;

    Long getAdvertiserIdElse(String userId, Long brandId, SqlSession session) throws Exception;

    Long getCampaignCount(String name, Long brandId, SqlSession session) throws Exception;

    void saveCampaign(Long id, Campaign campaign, Long advertiserId, OauthKey key, SqlSession session) throws Exception;

    Long getMediaBuyId(SqlSession session) throws Exception;

    void saveMediaBuy(Long id, Long mediaBuyId, Campaign campaign, OauthKey key, SqlSession session) throws Exception;

    void saveMediaBuyCampaign(Long id, Long mediaBuyId, OauthKey key, SqlSession session) throws Exception;

    void saveCreativeGroup(Long id, OauthKey key, SqlSession session) throws Exception;

    Long getCampaignStatus(Long id, SqlSession session);

    void setCampaignStatus(Long id, Long statusId, SqlSession session);

    /**
     * Updates only the COOKIE_DOMAIN_ID for the given {@code campaign}. The provided campaign should have set
     * the:
     * <ol>
     *     <li>campaign.id</li>
     *     <li>campaign.modifiedTpwsKey</li>
     *     <li>campaign.cookieDomainId</li>
     * </ol>
     * @param campaign The campaign to update
     * @param session The SqlSession where to execute the update transaction
     */
    int updateCookieDomainId(Campaign campaign, SqlSession session);

    void setCampaignStatusIfTrafficked(Long id, Long statusId, SqlSession session);

    List<CreativeGroupDtoForCampaigns> getCreativeGroupList(Long campaignId, Long startIndex, Long pageSize,
                                                            SqlSession session) ;

    void updateOverallBudget(Campaign campaign, SqlSession session) throws Exception;

    Long getCountCampaignsByAdvertiserAndBrandIds(Long advertiserId, Long brandId,
                                                  List<Long> campaignIds, SqlSession session);

    Long getCountCreativeGroupList (Long campaignId, SqlSession session);
}

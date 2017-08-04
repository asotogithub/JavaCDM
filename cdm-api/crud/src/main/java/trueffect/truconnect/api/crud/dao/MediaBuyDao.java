package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.MediaBuyCampaign;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author Rambert Rioja
 */
public interface MediaBuyDao extends GenericDao {

    public MediaBuy get(Long id, SqlSession session);

    public MediaBuy getByCampaign(Long campaignId, SqlSession session);

    public MediaBuy create(MediaBuy mediaBuy, SqlSession session);

    public MediaBuyCampaign createMediaBuyCampaign(MediaBuyCampaign mediaBuyCampaign, SqlSession session);
}

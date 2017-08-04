package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.MediaBuyCampaign;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.crud.dao.MediaBuyDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;

/**
 *
 * @author Rambert Rioja
 */
public class MediaBuyDaoImpl extends AbstractGenericDao implements MediaBuyDao {

    public static final String STATEMENT_GET_BY_ID = "MediaBuy.getMediaBuy";
    public static final String STATEMENT_GET_BY_CAMPAIGN_ID = "MediaBuy.getMediaBuyByCampaign";
    public static final String STATEMENT_GET_MEDIA_BUY_CAMPAIGN = "MediaBuy.getMediaBuyCampaign";
    public static final String STATEMENT_CREATE = "MediaBuy.insertMediaBuy";
    public static final String STATEMENT_UPDATE = "MediaBuy.updateMediaBuy";
    public static final String STATEMENT_CREATE_MEDIABUY_CAMPAIGN = "MediaBuy.insertMediaBuyCampaign";
    public static final String STATEMENT_DELETE_MEDIABUY_CAMPAIGN = "MediaBuy.deleteMediaBuyCampaign";

    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this
     * DAO
     */
    public MediaBuyDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public MediaBuy get(Long id, SqlSession session) {
        MediaBuy result = getPersistenceContext().selectSingle(STATEMENT_GET_BY_ID, id, session, MediaBuy.class);
        return result;
    }

    @Override
    public MediaBuy getByCampaign(Long campaignId, SqlSession session) {
        MediaBuy result = getPersistenceContext().selectSingle(STATEMENT_GET_BY_CAMPAIGN_ID, campaignId, session, MediaBuy.class);
        return result;
    }

    @Override
    public MediaBuy create(MediaBuy mediaBuy, SqlSession session) {

        MediaBuy result = null;
        Long id = getPersistenceContext().selectSingle("GenericQueries.getNextId", "SEQ_MEDIA_BUY", session, Long.class);
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("agencyId", mediaBuy.getAgencyId());
        parameter.put("name", mediaBuy.getName());
        parameter.put("overallBudget", mediaBuy.getOverallBudget());
        parameter.put("agencyNotes", mediaBuy.getAgencyNotes());
        parameter.put("tpwsKey", mediaBuy.getCreatedTpwsKey());
        getPersistenceContext().execute(STATEMENT_CREATE, parameter, session);
        result = this.get(id, session);
        return result;
    }

    @Override
    public MediaBuyCampaign createMediaBuyCampaign(MediaBuyCampaign mediaBuyCampaign, SqlSession session) {

        // Delete existent record.
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("mediaBuyId", mediaBuyCampaign.getMediaBuyId());
        parameter.put("campaignId", mediaBuyCampaign.getCampaignId());
        parameter.put("tpwsKey", mediaBuyCampaign.getModifiedTpwsKey());
        getPersistenceContext().execute(STATEMENT_DELETE_MEDIABUY_CAMPAIGN, parameter, session);

        // Save the new record.
        getPersistenceContext().execute(STATEMENT_CREATE_MEDIABUY_CAMPAIGN, parameter, session);
        return this.get(mediaBuyCampaign.getMediaBuyId(), mediaBuyCampaign.getCampaignId(), session);
    }

    private MediaBuyCampaign get(Long mediaBuyId, Long campaignId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("mediaBuyId", mediaBuyId);
        parameter.put("campaignId", campaignId);
        return getPersistenceContext().selectSingle(STATEMENT_GET_MEDIA_BUY_CAMPAIGN, parameter, session, MediaBuyCampaign.class);
    }
}

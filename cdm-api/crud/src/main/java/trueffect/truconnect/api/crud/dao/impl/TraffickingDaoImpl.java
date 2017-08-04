package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.CampaignCreatorContact;
import trueffect.truconnect.api.crud.dao.TraffickingDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Abel Soto
 */
public class TraffickingDaoImpl extends AbstractGenericDao implements TraffickingDao {

    private static final String STATEMENT_GET_CAMPAIGN_CREATOR_CONTACT = "TrafficPkg.getCampaignCreatorContact";
    private static final String STATEMENT_GET_CREATIVE_SCHEDULE = "TrafficPkg.getCreativeSchedule";
    private static final String STATEMENT_GET_CREATIVE_PLACEMENT_MATCH_SIZES = "TrafficPkg.getCreativePlacementMatchHeightAndWidth";
    private static final String STATEMENT_GET_SCHEDULES_CLICKTHROUGH_COUNT = "TrafficPkg.getSchedulesClickthroughCount";
    private static final String STATEMENT_GET_CREATIVES_CLICKTHROUGH_COUNT = "TrafficPkg.getCreativesClickthroughCount";
    private static final String STATEMENT_GET_DATES_VALIDATION_COUNT = "TrafficPkg.getDatesValidationCount";
    private static final String STATEMENT_CHECK_CONTACTS_BELONGS_AGENCY_BY_USER = "TrafficPkg.checkContactsBelongsAgencyByUser";
    private static final String STATEMENT_CHECK_SITE_CONTACTS = "TrafficPkg.checkSiteContacts";

    public TraffickingDaoImpl(PersistenceContext context) {
        super(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CampaignCreatorContact getCreativeGroupCreativesByCampaign(Long id, SqlSession session) {

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        getPersistenceContext().execute(STATEMENT_GET_CAMPAIGN_CREATOR_CONTACT, parameter, session);

        CampaignCreatorContact result = null;
        if (parameter.get("refCursor") != null) {
            List<CampaignCreatorContact> resultArray = (List<CampaignCreatorContact>) parameter.get("refCursor");
            result = resultArray.get(0);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long getCreativeSchedule(Long campaignId, SqlSession session){
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        Long totalSchedules = getPersistenceContext().selectSingle(STATEMENT_GET_CREATIVE_SCHEDULE, parameter, session, Long.class);
        return totalSchedules;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long getCreativePlacementMatchHeightAndWidth(Long campaignId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        Long totalDefault = getPersistenceContext().selectSingle(STATEMENT_GET_CREATIVE_PLACEMENT_MATCH_SIZES, parameter, session, Long.class);
        return totalDefault;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Long getSchedulesClickthroughCount(Long campaignId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        Long totalDefault = getPersistenceContext().selectSingle(STATEMENT_GET_SCHEDULES_CLICKTHROUGH_COUNT, parameter, session, Long.class);
        return totalDefault;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long getCreativesClickthroughCount(Long campaignId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        Long totalDefault = getPersistenceContext().selectSingle(STATEMENT_GET_CREATIVES_CLICKTHROUGH_COUNT, parameter, session, Long.class);
        return totalDefault;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Long getDatesValidationCount(Long campaignId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        Long totalDefault = getPersistenceContext().selectSingle(STATEMENT_GET_DATES_VALIDATION_COUNT, parameter, session, Long.class);
        return totalDefault;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean checkContactsBelongsAgencyByUser(Collection<Integer> ids, String userId, SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", ids);
        parameters.put("userId", userId);
        
        // Possible results are 'true' or 'false'
        String result = getPersistenceContext().selectSingle(STATEMENT_CHECK_CONTACTS_BELONGS_AGENCY_BY_USER, parameters, session, String.class);
        return Boolean.valueOf(result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean checkSiteContacts(Collection<Integer> ids, String userId, SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", ids);
        parameters.put("userId", userId);

        // Possible results are 'true' or 'false'
        String result = getPersistenceContext().selectSingle(STATEMENT_CHECK_SITE_CONTACTS, parameters, session, String.class);
        return Boolean.valueOf(result);
    }
}

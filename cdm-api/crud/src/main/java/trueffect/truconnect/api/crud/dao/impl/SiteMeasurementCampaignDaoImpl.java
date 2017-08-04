package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.dto.SiteMeasurementCampaignDTO;
import trueffect.truconnect.api.crud.dao.SiteMeasurementCampaignDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.BooleanAccumulator;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Abel Soto
 */
public class SiteMeasurementCampaignDaoImpl extends AbstractGenericDao implements SiteMeasurementCampaignDao {

    private static final String STATEMENT_GET_SITE_MEASUREMENT_CAMPAIGN =
            "SiteMeasurementPkg.getSiteMeasurementCampaign";
    private static final String STATEMENT_GET_ASSOCIATED_SM_CAMPAIGNS_FOR_SITE_MEASUREMENT_ID =
            "SiteMeasurementPkg.getAssociatedSMCampaignsForSiteMeasurementId";
    private static final String
            STATEMENT_GET_COUNT_ASSOCIATED_SM_CAMPAIGNS_FOR_SITE_MEASUREMENT_ID =
            "SiteMeasurementPkg.getCountAssociatedSMCampaignsForSiteMeasurementId";
    private static final String STATEMENT_GET_UNASSOCIATED_SM_CAMPAIGNS_FOR_SITE_MEASUREMENT_ID =
            "SiteMeasurementPkg.getUnassociatedSMCampaignsForSiteMeasurementId";
    private static final String
            STATEMENT_GET_COUNT_UNASSOCIATED_SM_CAMPAIGNS_FOR_SITE_MEASUREMENT_ID =
            "SiteMeasurementPkg.getCountUnassociatedSMCampaignsForSiteMeasurementId";
    private static final String STATEMENT_INSERT_SITE_MEASUREMENT_CAMPAIGN =
            "SiteMeasurementPkg.addSiteMeasureCampaign";
    private static final String STATEMENT_REMOVE_SITE_MEASUREMENT_CAMPAIGN =
            "SiteMeasurementPkg.removeSiteMeasureCampaign";
    private static final String STATEMENT_CHECK_DOMAIN_CONSISTENCY =
            "SiteMeasurementPkg.checkDomainConsistency";

    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this
     * DAO
     */
    public SiteMeasurementCampaignDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public SiteMeasurementCampaignDTO getSiteMeasurementCampaign(
            SiteMeasurementCampaignDTO siteMeasurementCampaign, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("measurementId", siteMeasurementCampaign.getMeasurementId());
        parameter.put("campaignId", siteMeasurementCampaign.getCampaignId());
        return getPersistenceContext()
                .selectSingle(STATEMENT_GET_SITE_MEASUREMENT_CAMPAIGN, parameter, session,
                        SiteMeasurementCampaignDTO.class);
    }

    @Override
    public List<SiteMeasurementCampaignDTO> getAssociatedCampaignsForSiteMeasurement(
            Long measurementId, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("smId", measurementId);
        parameter.put("superiorLimit", startIndex + pageSize);
        parameter.put("inferiorLimit", startIndex);

        return getPersistenceContext().selectMultiple(
                STATEMENT_GET_ASSOCIATED_SM_CAMPAIGNS_FOR_SITE_MEASUREMENT_ID,
                parameter, session);
    }

    @Override
    public Long getCountAssociatedCampaignsForSiteMeasurement(Long measurementId,
                                                              SqlSession session) {
        return getPersistenceContext()
                .selectSingle(STATEMENT_GET_COUNT_ASSOCIATED_SM_CAMPAIGNS_FOR_SITE_MEASUREMENT_ID,
                        measurementId, session, Long.class);
    }

    @Override
    public List<SiteMeasurementCampaignDTO> getUnassociatedCampaignsForSiteMeasurement(
            Long measurementId, Long startIndex, Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("smId", measurementId);
        parameter.put("superiorLimit", startIndex + pageSize);
        parameter.put("inferiorLimit", startIndex);

        return getPersistenceContext().selectMultiple(
                STATEMENT_GET_UNASSOCIATED_SM_CAMPAIGNS_FOR_SITE_MEASUREMENT_ID,
                parameter, session);
    }

    @Override
    public Long getCountUnassociatedCampaignsForSiteMeasurement(Long measurementId,
                                                                SqlSession session) {
        return getPersistenceContext()
                .selectSingle(STATEMENT_GET_COUNT_UNASSOCIATED_SM_CAMPAIGNS_FOR_SITE_MEASUREMENT_ID,
                        measurementId, session, Long.class);
    }

    @Override
    public SiteMeasurementCampaignDTO create(SiteMeasurementCampaignDTO siteMeasurementCampaign, SqlSession session){
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("measurementId", siteMeasurementCampaign.getMeasurementId());
        parameter.put("campaignId", siteMeasurementCampaign.getCampaignId());
        parameter.put("createdTpwsKey", siteMeasurementCampaign.getCreatedTpwsKey());
        getPersistenceContext().execute(STATEMENT_INSERT_SITE_MEASUREMENT_CAMPAIGN, parameter,
                                        session);
        return this.getSiteMeasurementCampaign(siteMeasurementCampaign, session);
    }

    @Override
    public void remove(SiteMeasurementCampaignDTO siteMeasurementCampaign, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("measurementId", siteMeasurementCampaign.getMeasurementId());
        parameter.put("campaignId", siteMeasurementCampaign.getCampaignId());
        parameter.put("modifiedTpwsKey", siteMeasurementCampaign.getModifiedTpwsKey());
        getPersistenceContext().execute(STATEMENT_REMOVE_SITE_MEASUREMENT_CAMPAIGN, parameter,
                                        session);
    }

    @Override
    public boolean checkDomainConsistency(Long id, Set<Long> campaignIds, final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("smId", id);
        boolean result = true;
        Accumulator<Boolean> resultAccumulator = new BooleanAccumulator(result);
        return new ResultSetAccumulatorImpl<Boolean>(
                "ids",
                new ArrayList<>(campaignIds),
                resultAccumulator,
                parameters) {
            @Override
            public Boolean execute(Object parameters) {
                return Boolean.valueOf(getPersistenceContext().selectSingle(
                        STATEMENT_CHECK_DOMAIN_CONSISTENCY, parameters, session, String.class));
            }
        }.getResults();
    }
}

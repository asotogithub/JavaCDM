package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.MetricsDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MetricsManager extends AbstractGenericManager{

    private final MetricsDao metricsDaoForUserValidation;
    private final MetricsDao metricsDaoEDW;

    /**
     *
     * @param accessControl
     */
    public MetricsManager(MetricsDao metricsDaoForUserValidation, MetricsDao metricsDaoForEDW, AccessControl accessControl){
        super(accessControl);
        this.metricsDaoForUserValidation = metricsDaoForUserValidation;
        this.metricsDaoEDW = metricsDaoForEDW;
    }

    public RecordSet<Metrics> getAgencyMetrics(Long agencyId, Date startDate, Date endDate, OauthKey key, RecordSet<CampaignDTO> campaigns) throws Exception {
        if(agencyId == null){
            throw new IllegalArgumentException("Agency agencyId cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        LocalDate minimumDate = getMinimumDate(startDate);
        LocalDate maximumDate = toLocalDate(endDate);

        SqlSession userValidationSession = metricsDaoForUserValidation.openSession();
        SqlSession session = metricsDaoEDW.openSession();
        RecordSet<Metrics> result;
        try {
            if (!userValidFor(AccessStatement.AGENCY, agencyId, key.getUserId(), userValidationSession)) {
                throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
            }
            result = metricsDaoEDW.getCampaignListMetrics(agencyId, minimumDate, maximumDate, session, campaigns);
        } finally {
            metricsDaoEDW.close(session);
            metricsDaoForUserValidation.close(userValidationSession);
        }
        return result;
    }

    public void insertCampaignMetrics(Long agencyId, List<Metrics> metricsList) {
        if(agencyId == null) {
            throw new IllegalArgumentException("Agency id cannot be null");
        }
        if(metricsList == null || metricsList.size() == 0) {
            throw new IllegalArgumentException("Metrics List is required");
        }

        SqlSession session = metricsDaoEDW.openSession();
        try {
            for(Metrics metrics : metricsList) {
                metricsDaoEDW.insertMetrics(agencyId, metrics, session);
            }
            metricsDaoEDW.commit(session);
        }
        catch(Exception e) {
            log.warn("Rolling back transaction due to error.", e);
            metricsDaoEDW.rollback(session);
            throw e;
        }
        finally {
            metricsDaoEDW.close(session);
        }
    }

    public Either<Error, RecordSet<Metrics>> insertMetrics(Long agencyId, List<Metrics> metricsList, OauthKey key) {
        if (agencyId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Agency Id"));
        }
        if (metricsList == null || metricsList.isEmpty()) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Metrics List"));
        }
        SqlSession session = metricsDaoEDW.openSession(ExecutorType.BATCH);
        SqlSession dacSession = metricsDaoForUserValidation.openSession();
        List<Long> campaignIds = new ArrayList<>();
        try {
            //Data Access Control
            if(!userValidFor(AccessStatement.AGENCY, agencyId, key.getUserId(), dacSession)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
            for (Metrics metrics : metricsList) {
                campaignIds.add(metrics.getId());
            }
            if (!accessControl.isUserValidFor(AccessStatement.CAMPAIGN, new HashSet(campaignIds), key.getUserId(), dacSession)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
            //Insert Metrics
            List<String> ids = new ArrayList<>();
            SimpleDateFormat sdf = DateConverter.DATE_FORMATS.get(0);
            for (Metrics metrics : metricsList) {
                metricsDaoEDW.insertMetrics(agencyId, metrics, session);
                ids.add(metrics.getId() + "_" + sdf.format(metrics.getDay()).toUpperCase());
            }
            RecordSet<Metrics> result = metricsDaoEDW.getMetricsByCampaignIds(ids, session);
            metricsDaoEDW.commit(session);
            return Either.success(result);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            metricsDaoEDW.rollback(session);
            return Either.error(new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR));
        } finally {
            metricsDaoEDW.close(session);
            metricsDaoForUserValidation.close(dacSession);
        }
    }

    public RecordSet<Metrics> getCampaignMetrics(Long campaignId, Date startDate, Date endDate, OauthKey key) {
        if(campaignId == null) {
            throw new IllegalArgumentException("campaignId cannot be null");
        }
        if(key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        LocalDate minimumDate = getMinimumDate(startDate);
        LocalDate maximumDate = toLocalDate(endDate);

        minimumDate = getMinimumDate(startDate);
        maximumDate = toLocalDate(endDate);

        SqlSession userValidationSession = metricsDaoForUserValidation.openSession();
        SqlSession session = metricsDaoEDW.openSession();
        RecordSet<Metrics> result;
        try {
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), userValidationSession)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            result = metricsDaoEDW.getCampaignMetrics(campaignId, minimumDate, maximumDate, session);
        } finally {
            metricsDaoEDW.close(session);
            metricsDaoForUserValidation.close(userValidationSession);
        }
        return result;
    }

    private LocalDate toLocalDate(Date endDate) {
        if(endDate != null) {
            return LocalDate.fromDateFields(endDate);
        }
        return LocalDate.now();
    }

    private LocalDate getMinimumDate(Date startDate) {
        LocalDate minimumDate = LocalDate.now().minusDays(Constants.MAXIMUM_LOOKBACK);
        if (startDate != null && !minimumDate.isAfter(LocalDate.fromDateFields(startDate))) {
            minimumDate = LocalDate.fromDateFields(startDate);
        }
        return minimumDate;
    }

    public RecordSet<Metrics> getCreativeMetricsByCampaign(Long campaignId, OauthKey key){
        if(campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("error.generic.null", "Campaign ID"));
        }
        if(key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("error.oauth.null"));
        }

        SqlSession userValidationSession = metricsDaoForUserValidation.openSession();
        SqlSession session = metricsDaoEDW.openSession();
        RecordSet<Metrics> result;
        try {
            super.userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), userValidationSession);
            result = metricsDaoEDW.getCreativeMetricsByCampaign(campaignId, session);
        } finally {
            metricsDaoEDW.close(session);
            metricsDaoForUserValidation.close(userValidationSession);
        }
        return result;
    }

    public RecordSet<Metrics> getTopTenCreativeMetricsByCampaign(Long campaignId, OauthKey key){
        if(campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("error.generic.null", "Campaign ID"));
        }
        if(key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("error.oauth.null"));
        }

        SqlSession userValidationSession = metricsDaoForUserValidation.openSession();
        SqlSession session = metricsDaoEDW.openSession();
        RecordSet<Metrics> result;
        try {
            super.userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), userValidationSession);
            result = metricsDaoEDW.getTopTenCreativeMetricsByCampaign(campaignId, session);
        } finally {
            metricsDaoEDW.close(session);
            metricsDaoForUserValidation.close(userValidationSession);
        }
        return result;
    }
}

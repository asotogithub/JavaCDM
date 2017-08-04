package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementCampaignDTO;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.enums.DeltaActionEnum;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementCampaignType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteMeasurementCampaignDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Abel Soto
 */
public class SiteMeasurementCampaignManager extends AbstractGenericManager {

    private SiteMeasurementCampaignDao siteMeasurementCampaignDao;


    public SiteMeasurementCampaignManager(SiteMeasurementCampaignDao dao, AccessControl accessControl) {
        super(accessControl);
        this.siteMeasurementCampaignDao = dao;
    }

    /**
     * Gets a Site Measurement Campaign based on its ID
     *
     * @param measurementId the ID of the Site Measurement.
     * @param campaignId the ID of the Campaign.
     * @param key
     * @return the Site Measurement
     */
    public SiteMeasurementCampaignDTO get(Long measurementId, Long campaignId, OauthKey key) {
        if (measurementId == null) {
            throw new IllegalArgumentException("Site Measurement's id cannot be null");
        }

        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign's id cannot be null");
        }

        SqlSession session = siteMeasurementCampaignDao.openSession();
        SiteMeasurementCampaignDTO result;
        try {
            // Check access control
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT, Collections.singletonList(measurementId), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            SiteMeasurementCampaignDTO smc = new SiteMeasurementCampaignDTO();
            smc.setMeasurementId(measurementId);
            smc.setCampaignId(campaignId);
            result = this.getSiteMeasurementCampaignDTO(smc, session);
        } finally {
            siteMeasurementCampaignDao.close(session);
        }
        return result;
    }

    /**
     * Gets a Campaign by Site Measurement based ID
     *
     * @param siteMeasurementId the ID of the Site Measurement.
     * @param key
     * @return the Site Measurement
     */
    public Either<Error, RecordSet<SiteMeasurementCampaignDTO>> getCampaignsForSiteMeasurement(
            Long siteMeasurementId, SiteMeasurementCampaignType type, Long startIndex, Long pageSize, OauthKey key) {

        //validations
        //nullability checks
        if (siteMeasurementId == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Site Measurement's id"));
        }

        if (type == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Campaign type"));
        }

        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuthKey"));
        }

        SqlSession session = siteMeasurementCampaignDao.openSession();
        List<SiteMeasurementCampaignDTO> records;
        Long totalRecords = 0L;

        HashMap<String, Long> paginator = new HashMap<>();
        paginator.put("startIndex",startIndex);
        paginator.put("pageSize",pageSize);
        validatePaginator(paginator);

        try {
            // Check access control
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT,
                    Collections.singletonList(siteMeasurementId), key.getUserId(), session)) {
                return Either.error(new Error(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
            }

            switch (type) {
                case UNASSOCIATED:
                    records = siteMeasurementCampaignDao.getUnassociatedCampaignsForSiteMeasurement(
                            siteMeasurementId, paginator.get("startIndex"), paginator.get("pageSize"), session);
                    totalRecords = siteMeasurementCampaignDao.getCountUnassociatedCampaignsForSiteMeasurement(
                            siteMeasurementId, session);
                    break;
                default:
                    records = siteMeasurementCampaignDao.getAssociatedCampaignsForSiteMeasurement(
                            siteMeasurementId, paginator.get("startIndex"), paginator.get("pageSize"), session);
                    totalRecords = siteMeasurementCampaignDao.getCountAssociatedCampaignsForSiteMeasurement(
                            siteMeasurementId, session);
            }
        } finally {
            siteMeasurementCampaignDao.close(session);
        }
        RecordSet<SiteMeasurementCampaignDTO> result = new RecordSet<>(
                paginator.get("startIndex").intValue(),
                paginator.get("pageSize").intValue(),
                totalRecords.intValue(),
                records);
        return Either.success(result);
    }

    /**
     * Saves the relationships between <code>SiteMeasurement</code> and <code>Campaign</code>
     * It also removes whatever relationship that is no longer provided during the update
     *
     * @param id The ID of the Site Measurement
     * @param smCampaigns All Relationships of SiteMeasurement and Campaign
     * @param key Session ID of the user who updates the Site Measurement.
     * @return Relationships updated.
     */
    public Either<Error, RecordSet<SiteMeasurementCampaignDTO>> save(Long id,
                                                                      RecordSet<SiteMeasurementCampaignDTO> smCampaigns,
                                                                      OauthKey key) {

        //validations
        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Site Measurement's id"));
        }

        if (smCampaigns == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil
                            .getString("global.error.null", "Site Measurement Campaigns"));
        }

        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuthKey"));
        }

        List<SiteMeasurementCampaignDTO> newSmCampaigns = smCampaigns.getRecords();
        Error error = null;
        if(newSmCampaigns != null) {
            for (SiteMeasurementCampaignDTO smc : newSmCampaigns) {
                // Check that id matches DTO id.
                if (smc.getMeasurementId() != null && id.longValue() != smc.getMeasurementId()
                                                                           .longValue()) {
                    error = new ValidationError(
                            ResourceBundleUtil.getString("global.error.mismatchingId"),
                            ValidationCode.INVALID);
                    return Either.error(error);
                }
                if (smc.getCampaignId() == null) {
                    error = new ValidationError(
                            ResourceBundleUtil.getString("global.error.null", "CampaignId"),
                            ValidationCode.REQUIRED);
                    return Either.error(error);
                }
            }
        }

        List<SiteMeasurementCampaignDTO> records = new ArrayList<>();
        SqlSession session = siteMeasurementCampaignDao.openSession();

        try {
            // 1. Get Existing records from DB
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT, Collections.singletonList(id),
                    key.getUserId(), session)) {
                error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER);
                return Either.error(error);
            }
            List<SiteMeasurementCampaignDTO> existing =
                    siteMeasurementCampaignDao.getAssociatedCampaignsForSiteMeasurement(id, 0L,
                            siteMeasurementCampaignDao
                                    .getCountAssociatedCampaignsForSiteMeasurement(id, session),
                            session);

            // 2. Delta processing
            // 2.1. To delete (existing - news)
            final List<SiteMeasurementCampaignDTO> toDelete = new ArrayList<>();
            toDelete.addAll(existing);
            toDelete.removeAll(newSmCampaigns);

            // 2.2. To Add (news - existing)
            final List<SiteMeasurementCampaignDTO> toAdd = new ArrayList<>();
            toAdd.addAll(newSmCampaigns);
            toAdd.removeAll(existing);

            // 2.3 keep all that doesn't get updated as part of the result
            records.addAll(newSmCampaigns);
            records.removeAll(toAdd);

            Map<DeltaActionEnum, List<SiteMeasurementCampaignDTO>> groups =
                    new HashMap<DeltaActionEnum, List<SiteMeasurementCampaignDTO>>() {{
                        put(DeltaActionEnum.DELETE, toDelete);
                        put(DeltaActionEnum.ADD, toAdd);
                    }};

            // 3. Perform operations DELETE, ADD
            // 3.1 Check access control
            error = checkAccessControl(groups, key, session);
            if (error != null) {
                return Either.error(error);
            }

            // 3.2 Check consistency between Campaign's domain and SM's domain
            error = checkDomainConsistency(id, toAdd, session);
            if (error != null) {
                return Either.error(error);
            }

            // 3.3 Persist
            for (Map.Entry<DeltaActionEnum, List<SiteMeasurementCampaignDTO>> group : groups
                    .entrySet()) {
                for (SiteMeasurementCampaignDTO smCampaign : group.getValue()) {
                    DeltaActionEnum actionType = group.getKey();
                    switch (actionType) {
                        case DELETE: {
                            smCampaign.setModifiedTpwsKey(key.getTpws());
                            siteMeasurementCampaignDao.remove(smCampaign, session);
                            break;
                        }
                        case ADD: {
                            smCampaign.setCreatedTpwsKey(key.getTpws());
                            smCampaign = siteMeasurementCampaignDao.create(smCampaign, session);
                            records.add(smCampaign);
                            break;
                        }
                    }
                }
            }

            siteMeasurementCampaignDao.commit(session);
        } catch (Exception e) {
            siteMeasurementCampaignDao.rollback(session);
            return Either.error(new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR));
        } finally {
            siteMeasurementCampaignDao.close(session);
        }
        return Either.success(new RecordSet<>(records));
    }

    private Error checkAccessControl(Map<DeltaActionEnum, List<SiteMeasurementCampaignDTO>> groups,
                                     OauthKey key, SqlSession session) {
        Set<Long> smIds = new HashSet<>();
        Set<Long> campaignIds = new HashSet<>();

        for (Map.Entry<DeltaActionEnum, List<SiteMeasurementCampaignDTO>> entry : groups
                .entrySet()) {
            for (SiteMeasurementCampaignDTO smc : entry.getValue()) {
                if (smc.getMeasurementId() != null) {
                    smIds.add(smc.getMeasurementId());
                }
                if (smc.getCampaignId() != null) {
                    campaignIds.add(smc.getCampaignId());
                }
            }
        }

        if (!smIds.isEmpty()) {
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT, smIds, key.getUserId(), session)) {
                return new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER);
            }
        }
        if (!campaignIds.isEmpty()) {
            if (!userValidFor(AccessStatement.CAMPAIGN, campaignIds, key.getUserId(), session)) {
                return new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER);
            }
        }
        return null;
    }

    private Error checkDomainConsistency(Long id,
                                         List<SiteMeasurementCampaignDTO> smCampaigns,
                                         SqlSession session) {
        Set<Long> campaignIds = new HashSet<>();

        for (SiteMeasurementCampaignDTO smc : smCampaigns) {
            if (smc.getCampaignId() != null) {
                campaignIds.add(smc.getCampaignId());
            }
        }

        if (!campaignIds.isEmpty()) {
            if(!siteMeasurementCampaignDao.checkDomainConsistency(id, campaignIds, session)) {
                return new ValidationError(
                        ResourceBundleUtil.getString("sm.error.domainDoNotMatchInCampaign"),
                        ValidationCode.INVALID);
            }
        }
        return null;
    }

    private SiteMeasurementCampaignDTO getSiteMeasurementCampaignDTO(SiteMeasurementCampaignDTO dto, SqlSession session) {
        SiteMeasurementCampaignDTO result = siteMeasurementCampaignDao.getSiteMeasurementCampaign(
                dto, session);
        if (result == null) {
            throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.NOT_FOUND, "campaignId");
        }
        return result;
    }
}

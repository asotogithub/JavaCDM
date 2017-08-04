package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventPingsDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.validation.SiteMeasurementEventPingValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jesus.nunez on 8/12/2016.
 */
public class SiteMeasurementEventPingsManager extends AbstractGenericManager {

    private UserDao userDao;
    private final SiteMeasurementEventPingsDao pingDao;
    private SiteMeasurementEventPingValidator validator;

    public SiteMeasurementEventPingsManager(UserDao userDao, SiteMeasurementEventPingsDao pingDao, AccessControl accessControl) {
        super(accessControl);
        this.userDao = userDao;
        this.pingDao = pingDao;

        this.validator = new SiteMeasurementEventPingValidator();
    }

    public Either<Errors, String> deletePingEvent(RecordSet<Long> smPingIds, OauthKey key) {
        // Nullability checks
        if (smPingIds == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Pings Ids"));
        }
        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        // Validate payload
        List<Long> toDelete = new ArrayList<>();
        for (Long id : smPingIds.getRecords()) {
            if (id != null) {
                toDelete.add(id);
            }
        }

        Errors errors = new Errors();

        if (toDelete.isEmpty()) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.empty", "smPingIds List"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }

        SqlSession session = pingDao.openSession();
        try {
            // Check DAC
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT_EVENT_PING, toDelete,
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }
            Integer affected = pingDao.deletePingEvent(toDelete, key.getTpws(), session);
            if (affected <= 0) {
                pingDao.rollback(session);
                BusinessError error = new BusinessError(
                        ResourceBundleUtil.getString("global.error.empty", "id"), BusinessCode.NOT_FOUND, "ids");
                errors.addError(error);
                return Either.error(errors);
            }
            pingDao.commit(session);

        } catch (Exception e) {
            pingDao.rollback(session);
            BusinessError error = new BusinessError(e.getMessage(), BusinessCode.INTERNAL_ERROR, null);
            errors.addError(error);
            return Either.error(errors);
        } finally {
            pingDao.close(session);
        }

        String message = ResourceBundleUtil.getString("global.info.bulkOperationSuccess",
                ResourceBundleUtil.getString("global.label.delete"),
                ResourceBundleUtil.getString("global.label.smEventPing"));
        return Either.success(message);
    }


    public Either<Errors, RecordSet<SmPingEventDTO>> createPingEvents(RecordSet<SmPingEventDTO> records, OauthKey key) {
        // Nullability checks
        if(records == null){
            throw new IllegalArgumentException("Event Ping records cannot be null");
        }

        if(key == null){
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        Errors errors = new Errors();

        // model validation
        String className = SmPingEventDTO.class.getSimpleName();
        for (SmPingEventDTO dto:records.getRecords()) {
            BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(dto, className);
            validator.validateSiteMeasurementEventForCreate(dto, validationResult);

            if (validationResult.hasErrors()) {
                errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(validationResult));
                return Either.error(errors);
            }
        }

        List<SmPingEventDTO> result = new ArrayList<>();
        SqlSession session = pingDao.openSession();

        try {
            // Check access control
            Set<Long> eventIds = new HashSet<>();
            Set<Long> siteIds = new HashSet<>();

            for (SmPingEventDTO dto:records.getRecords()) {
                eventIds.add(dto.getId());
                siteIds.add(dto.getSiteId());
            }

            if (!userValidFor(AccessStatement.SITE_MEASUREMENT_EVENT, eventIds, key.getUserId(), session)) {
                AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            if (!userValidFor(AccessStatement.SITE, siteIds, key.getUserId(), session)) {
                AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // Update SM Event Pings
            for (SmPingEventDTO dto : records.getRecords()) {
                Long id = pingDao.getNextId(session);
                dto.setPingId(id);
                dto.setCreatedTpwsKey(key.getTpws());
                pingDao.create(dto, session);
                result.add(dto);
            }

            pingDao.commit(session);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            pingDao.rollback(session);

            Error error = new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR);
            errors.addError(error);

            return Either.error(errors);
        } finally {
            pingDao.close(session);
        }

        return Either.success(new RecordSet<SmPingEventDTO>(result));
    }

    public Either<Errors, RecordSet<SmPingEventDTO>> updatePingEvents(RecordSet<SmPingEventDTO> records, OauthKey key) {
        // Nullability checks
        if(records == null){
            throw new IllegalArgumentException("Event Ping records cannot be null");
        }

        if(key == null){
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        Errors errors = new Errors();

        // model validation
        String className = SmPingEventDTO.class.getSimpleName();
        for (SmPingEventDTO dto:records.getRecords()) {
            BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(dto, className);
            validator.validateSiteMeasurementEventForUpdate(dto, validationResult);

            if (validationResult.hasErrors()) {
                errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(validationResult));
                return Either.error(errors);
            }
        }

        List<SmPingEventDTO> result = new ArrayList<>();
        SqlSession session = pingDao.openSession();

        try {
            // Check access control
            Set<Long> eventIds = new HashSet<>();
            Set<Long> siteIds = new HashSet<>();
            Set<Long> toUpdate = new HashSet<>();

            for (SmPingEventDTO dto:records.getRecords()) {
                eventIds.add(dto.getId());
                siteIds.add(dto.getSiteId());
                toUpdate.add(dto.getPingId());
            }

            if (!userValidFor(AccessStatement.SITE_MEASUREMENT_EVENT, eventIds, key.getUserId(), session)) {
                AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            if (!userValidFor(AccessStatement.SITE_MEASUREMENT_EVENT_PING, toUpdate, key.getUserId(), session)) {
                AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            if (!userValidFor(AccessStatement.SITE, siteIds, key.getUserId(), session)) {
                AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // Create SM Event Pings
            for (SmPingEventDTO dto : records.getRecords()) {
                dto.setModifiedTpwsKey(key.getTpws());
                pingDao.update(dto, session);
                result.add(dto);
            }

            pingDao.commit(session);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            pingDao.rollback(session);

            Error error = new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR);
            errors.addError(error);

            return Either.error(errors);
        } finally {
            pingDao.close(session);
        }

        return Either.success(new RecordSet<SmPingEventDTO>(result));
    }
}

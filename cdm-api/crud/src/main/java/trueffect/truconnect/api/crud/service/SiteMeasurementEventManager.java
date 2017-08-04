package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
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
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.dto.SmEventDTO;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementGroupDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.validation.SiteMeasurementEventValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by richard.jaldin on 6/9/2015.
 */
public class SiteMeasurementEventManager extends AbstractGenericManager{

    private SiteMeasurementEventDao smEventDao;
    private UserDao userDao;
    private SiteMeasurementEventValidator validator;
    private SiteMeasurementDao smDao;
    private SiteMeasurementGroupDao smGroupDao;

    public SiteMeasurementEventManager(SiteMeasurementEventDao dao, UserDao userDao, AccessControl accessControl,
                                       SiteMeasurementDao smDao, SiteMeasurementGroupDao smGroupDao) {
        super(accessControl);
        this.smEventDao = dao;
        this.userDao = userDao;
        this.smDao = smDao;
        this.smGroupDao = smGroupDao;
        this.validator = new SiteMeasurementEventValidator();
    }

    /**
     * Create a new {@code SmEvent} with the provided parameters.
     * Catch an PersistenceException error and throw an buildBusinessSystemException with the error description.
     *
     * @param smEvent The {@code SmEvent} that contains created data
     * @param key The Oauth key to track who is executing this operation
     * @return an created {@code SmEvent}
     */
    public Either<trueffect.truconnect.api.commons.exceptions.business.Error,SmEvent> create(SmEvent smEvent,  OauthKey key) {

        SmEvent result = null;
        SqlSession session = smEventDao.openSession();

        trueffect.truconnect.api.commons.exceptions.business.Error validationError = checkValidations(smEvent, key);
        if (validationError != null) {
            return Either.error(validationError);
        }

        validationError = nameValidation(smEvent, session);
        if (validationError != null) {
            return Either.error(validationError);
        }

        try {
            validationError = checkDAC(smEvent, session, key);

            if (validationError != null) {
                return Either.error(validationError);
            }

            //PK of SiteMeasurementEvent
            Long id = smEventDao.getNextId(session);
            smEvent.setId(id);
            smEvent.setCreatedTpwsKey(key.getTpws());
            smEventDao.create(smEvent, session);
            log.info(key.toString()+ " Saved "+ smEvent);
            smEventDao.commit(session);
            // Retrieve recently created Site Measurement
            result = getSmEvent(id, session);
        } catch (PersistenceException e){
            log.warn(e.getMessage(),e);
            smEventDao.rollback(session);
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.DUPLICATE, "eventName");
        }  finally{
            smEventDao.close(session);
        }
        return Either.success(result);
    }

    private SmEvent getSmEvent(Long id, SqlSession session) {
        SmEvent result;
        result = smEventDao.get(id, session);
        if(result == null) {
            throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.NOT_FOUND, "id");
        }
        return result;
    }

    /**
     * Gets a Site Measurement Events based on Site Measurement's Id
     *
     * @param siteMeasurementId the Id of the Site Measurement.
     * @param key
     * @return the RecordSet of Site Measurement Events
     */
    public RecordSet<SmEventDTO> getSmEventsBySiteMeasurement(Long siteMeasurementId, OauthKey key) {
        if(siteMeasurementId == null){
            throw new IllegalArgumentException("Site Measurement DTO's id cannot be null");
        }

        SqlSession session = smEventDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT, Collections.singletonList(siteMeasurementId), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            return smEventDao.getSmEventsBySiteMeasurement(siteMeasurementId, session);
        }finally{
            smEventDao.close(session);
        }
    }

    /**
     * Gets a Site Measurement Ping Events based on Site Measurement Event Id
     *
     * @param smEventId the Id of the Site Measurement.
     * @param key
     * @return the RecordSet of Site Measurement Events
     */
    public Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmEvent> getSmPingsByEvents(Long smEventId, OauthKey key) {
        if(smEventId == null){
            return Either.error(new Error(ResourceBundleUtil.getString("error.generic.empty", "event ID"), ValidationCode.REQUIRED));
        }

        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        SqlSession session = smEventDao.openSession();
        SmEvent records = new SmEvent();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT_EVENT, Collections.singletonList(smEventId), key.getUserId(), session)) {
                return Either.error((Error)new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId()));
            }

            records = smEventDao.getSmPingsByEvents(smEventId, session);


        } catch(Exception e) {
            Either.error(new Error(ResourceBundleUtil.getString("sm.error.cannotGetPing", smEventId), BusinessCode.INTERNAL_ERROR));

        } finally {
            smEventDao.close(session);
        }
        return Either.success(records);
    }

    public Either<Error, RecordSet<SmPingEventDTO>> createSmPingsByEvents(Long eventId, RecordSet<SmPingEventDTO> records, OauthKey oauthKey) {

        // TODO: This method will be modified in one of the next sprints to complete the properly validations and other stuff.

        SqlSession session = smEventDao.openSession();
        try {
            for (SmPingEventDTO dto : records.getRecords()) {
                Long id = smEventDao.getNextId(session);
                dto.setPingId(id);
                dto.setCreatedTpwsKey(oauthKey.getTpws());
                dto.setId(eventId);
                smEventDao.createSmPingByEvent(dto, session);
            }

            smEventDao.commit(session);

            return Either.success(new RecordSet<SmPingEventDTO>(smEventDao.getSmPingsByEvents(eventId, session).getPingEvents()));
        } catch (Exception e){
            smEventDao.rollback(session);

            log.warn(e.getMessage(), e);
            return Either.error(new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR));
        } finally {
            smEventDao.close(session);
        }
    }

    public Either<Errors, SmEvent> update(Long id, SmEvent sme, OauthKey key) {
        // Nullability checks
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        if(sme == null){
            throw new IllegalArgumentException("SmEvent cannot be null");
        }

        if(key == null){
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        Errors errors = new Errors();

        // model validation
        String className = sme.getClass().getSimpleName();
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(sme, className);
        validator.validateSiteMeasurementEventForUpdate(id, sme, validationResult);

        if (validationResult.hasErrors()) {
            errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(validationResult));
            return Either.error(errors);
        }

        SmEvent result = null;
        SqlSession session = smEventDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT_EVENT, Collections.singletonList(sme.getId()), key.getUserId(), session)) {
                AccessError error = new AccessError(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            // Validate fields according of site measurement's state
            SmEvent existingSmEvent = getSmEvent(id, session);
            SmGroup existingSmGroup = smGroupDao.get(existingSmEvent.getSmGroupId(), session);
            SiteMeasurementDTO existingSiteMeasurement = smDao.get(existingSmGroup.getMeasurementId(), session);

            validationResult = new BeanPropertyBindingResult(sme, className);
            validator.validateSiteMeasurementEventStateForUpdate(sme, existingSmEvent, existingSiteMeasurement, validationResult);

            if (validationResult.hasErrors()) {
                errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(validationResult));
                return Either.error(errors);
            }

            // Update site measurement event
            sme.setModifiedTpwsKey(key.getTpws());
            smEventDao.update(sme, session);
            result = getSmEvent(id, session);

            smEventDao.commit(session);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            smEventDao.rollback(session);

            BusinessError error = new BusinessError(e.getMessage(), BusinessCode.INTERNAL_ERROR, "");
            errors.addError(error);
            return Either.error(errors);
        } finally {
            smEventDao.close(session);
        }

        return Either.success(result);
    }

    public Either<Errors, Boolean> isEventNameExist(Long groupId,  String name, OauthKey key) {
        Errors errors = new Errors();
        if (name == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.empty", "Sm Event Name"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);

        }

        if(key == null){
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        if (groupId == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.empty", "Sm Group Id"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }

        name = name.trim();
        Pattern pattern = Pattern.compile(ValidationConstants.REGEXP_ALPHANUMERIC_WITH_UNDERSCORE);
        Matcher matcher = pattern.matcher(name);

        if (matcher.find()) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.name", name),
                    ValidationCode.INVALID);
            errors.addError(error);
            return Either.error(errors);
        }

        SqlSession session = smEventDao.openSession();
        Boolean result = false;
        try {
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT_GROUP, Collections.singletonList(groupId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            result = smEventDao.isSmEventNameExist(groupId, name, session);
        } finally {
            smEventDao.close(session);
        }
        return Either.success(result);
    }

    public Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmEvent> createSmEvent(SmEvent smEvent,
                                                                                                     SqlSession session,
                                                                                                     OauthKey key) {
        trueffect.truconnect.api.commons.exceptions.business.Error validationError = checkValidations(smEvent, key);
        if (validationError != null) {
            return Either.error(validationError);
        }

        validationError = checkDAC(smEvent, session,key);

        if (validationError != null) {
            return Either.error(validationError);
        }

        validationError = nameValidation(smEvent, session);
        if (validationError != null) {
            return Either.error(validationError);
        }

        SmEvent result = null;

        Long id = smEventDao.getNextId(session);
        smEvent.setId(id);
        smEvent.setCreatedTpwsKey(key.getTpws());

        smEventDao.create(smEvent, session);
        log.debug("Creating SmEvent {}", smEvent);
        result = getSmEvent(id, session);
        return Either.success(result);
    }

    private trueffect.truconnect.api.commons.exceptions.business.Error checkValidations(SmEvent smEvent,
                                                                                       OauthKey key) {
        if(smEvent == null){
            throw new IllegalArgumentException("SmEvent cannot be null");
        }

        if(key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        if (smEvent.getSmGroupId() == null) {
            return new Error(ResourceBundleUtil.getString("global.error.null", "smGroup Id"),
                    ValidationCode.REQUIRED);
        }

        //business logic
        if (StringUtils.isBlank(smEvent.getEventName())) {
            return new Error(ResourceBundleUtil.getString("global.error.empty", "eventName"),
                    ValidationCode.REQUIRED);
        }

        if (smEvent.getEventName().length() > Constants.SITE_MEASUREMENT_EVENT_NAME_MAX_LENGTH) {
            return new Error(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "eventName", Constants.SITE_MEASUREMENT_EVENT_NAME_MAX_LENGTH),
                    ValidationCode.TOO_LONG);
        }

        if (StringUtils.contains(smEvent.getEventName(), " ")) {
            return new Error(ResourceBundleUtil.getString("global.error.name", "eventName"),
                    ValidationCode.INVALID);
        }

        if (smEvent.getEventType() != null && smEvent.getEventName().length() > 20) {
            throw BusinessValidationUtil.buildBusinessSystemException(ValidationCode.TOO_LONG, "eventType");
        }
        return null;
    }

    private trueffect.truconnect.api.commons.exceptions.business.Error checkDAC(SmEvent smEvent, SqlSession session,
                                                                               OauthKey key) {
        if (!userValidFor(AccessStatement.SITE_MEASUREMENT_GROUP, Collections.singletonList(smEvent.getSmGroupId()),
                key.getUserId(), session)) {
            return (Error)new AccessError(
                    ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                    SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
        }
        return null;
    }

    private trueffect.truconnect.api.commons.exceptions.business.Error nameValidation(SmEvent smEvent,
                                                                                     SqlSession session) {
        smEvent.setEventName(smEvent.getEventName().trim());
        Pattern pattern = Pattern.compile(ValidationConstants.REGEXP_ALPHANUMERIC_WITH_UNDERSCORE);
        Matcher matcher = pattern.matcher(smEvent.getEventName());

        if (matcher.find()) {
            return new Error(ResourceBundleUtil.getString("global.error.name", "SmEvent Name"),
                    ValidationCode.REQUIRED);
        }

        Boolean isNameExist = smEventDao.isSmEventNameExist(smEvent.getSmGroupId(), smEvent.getEventName(), session);
        if (isNameExist) {
            return new Error(ResourceBundleUtil.getString("global.error.nameAlreadyExist", "SmEvent"),
                    ValidationCode.DUPLICATE);
        }
        return null;
    }
}

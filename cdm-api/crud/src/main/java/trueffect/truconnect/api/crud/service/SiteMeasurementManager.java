package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.validation.SiteMeasurementValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.util.Collections;

/**
 * Business logic methods for Site Measurement
 * @author Abel Soto
 */
public class SiteMeasurementManager extends AbstractGenericManager {

    private SiteMeasurementDao siteMeasurementDao;
    private UserDao userDao;

    private SiteMeasurementValidator validator;

    public SiteMeasurementManager(SiteMeasurementDao dao, UserDao userDao,
                                  AccessControl accessControl) {
        super(accessControl);
        this.siteMeasurementDao = dao;
        this.userDao = userDao;
        this.validator = new SiteMeasurementValidator();
    }

    /**
     * Gets a Site Measurement based on its ID
     *
     * @param id the ID of the Site Measurement.
     * @param key
     * @return the Site Measurement
     */
    public SiteMeasurementDTO get(Long id, OauthKey key) {
        if (id == null) {
            throw new IllegalArgumentException("Site Measurement DTO's id cannot be null");
        }

        SqlSession session = siteMeasurementDao.openSession();
        SiteMeasurementDTO result;
        try {
            // Check access control
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT, Collections.singletonList(id), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            result = getSiteMeasurementDTO(id, session);
        } finally {
            siteMeasurementDao.close(session);
        }
        return result;
    }

    /**
     * Gets a list of all Site Measurements
     *
     * @param criteria
     * @param key
     * @return The list of all Site Measurements.
     */
    public RecordSet<SiteMeasurementDTO> get(SearchCriteria criteria, OauthKey key) {
        if (criteria.getPageSize() > 1000) {
            throw new SystemException("The page size allows up to 1000 records.", BusinessCode.INVALID);
        }
        if (criteria.getStartIndex() < 0) {
            throw new SystemException("Cannot retrieve records for start index: " + criteria.getStartIndex() + ". The minimum start index is: 0", BusinessCode.INVALID);
        }

        SqlSession session = siteMeasurementDao.openSession();
        RecordSet<SiteMeasurementDTO> result;
        try {
            result = siteMeasurementDao.get(criteria, key, session);
        } finally {
            siteMeasurementDao.close(session);
        }
        return result;
    }

    /**
     * Create a new {@code SiteMeasurement} with the provided parameters. Catch
     * an PersistenceException error and throw an buildBusinessSystemException
     * with the error description.
     *
     * @param siteMeasurement The {@code SiteMeasurement} DTO that contains
     * created data
     * @param key The Oauth key to track who is executing this operation
     * @return an created {@code SiteMeasurement} DTO
     * @throws SystemException
     */
    public Either<Errors, SiteMeasurementDTO> create(SiteMeasurementDTO siteMeasurement,
                                                     OauthKey key) {
        //validations
        // Nullability checks
        if (siteMeasurement == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Site Measurement DTO"));
        }

        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuthKey"));
        }

        Errors errors = new Errors();
        BeanPropertyBindingResult smErrors =
                new BeanPropertyBindingResult(siteMeasurement, "SiteMeasurementDTO");
        ValidationUtils.invokeValidator(validator, siteMeasurement, smErrors);
        if (smErrors.hasErrors()) {
            errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(smErrors));
            return Either.error(errors);
        }

        SiteMeasurementDTO result = null;
        SqlSession session = siteMeasurementDao.openSession();

        try {
            // Check access control
            if (!userValidFor(AccessStatement.ADVERTISER,
                    Collections.singletonList(siteMeasurement.getAdvertiserId()), key.getUserId(),
                    session)) {
                errors.addError(new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
                return Either.error(errors);
            }

            if (!userValidFor(AccessStatement.BRAND,
                    Collections.singletonList(siteMeasurement.getBrandId()), key.getUserId(),
                    session)) {
                errors.addError(new Error(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
                return Either.error(errors);
            }

            // Check SM's name for duplicity
            if (siteMeasurementDao.doesSiteMeasurementExists(siteMeasurement.getName(),
                    null, siteMeasurement.getBrandId(), session)) {
                errors.addError(new BusinessError(ResourceBundleUtil.getString("sm.error.duplicated",
                        siteMeasurement.getName(), String.valueOf(siteMeasurement.getBrandId())),
                        BusinessCode.DUPLICATE, "name"));
                return Either.error(errors);
            }

            siteMeasurement.setCreatedTpwsKey(key.getTpws());
            siteMeasurementDao.create(siteMeasurement, session);
            siteMeasurementDao.commit(session);
            log.debug("Site Measurement {} saved", result);
        } catch (Exception e) { // Check for validation exceptions part of Package
            siteMeasurementDao.rollback(session);
            errors.addError(new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR));
            return Either.error(errors);
        } finally {
            siteMeasurementDao.close(session);
        }
        return Either.success(siteMeasurement);
    }

    /**
     * Updates a Site Measurement Catch an PersistenceException error and throw
     * an buildBusinessSystemException with the error description.
     *
     * @param id The {@code SiteMeasurement} ID
     * @param dto The {@code SiteMeasurement} DTO that contains updated data
     * @param key The Oauth key to track who is executing this operation
     * @return an updated {@code SiteMeasurement} DTO
     * @throws SystemException
     */
    public Either<Errors, SiteMeasurementDTO> update(Long id, SiteMeasurementDTO dto, OauthKey key) {
        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Site Measurement Id"));
        }

        if (dto == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Site Measurement DTO"));
        }

        if (key == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "OAuthKey"));
        }

        Errors errors = new Errors();
        BeanPropertyBindingResult smErrors =
                new BeanPropertyBindingResult(dto, "SiteMeasurementDTO");
        validator.validateForUpdate(id, dto, smErrors);
        if (smErrors.hasErrors()) {
            errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(smErrors));
            return Either.error(errors);
        }

        SiteMeasurementDTO result = null;
        SqlSession session = siteMeasurementDao.openSession();

        try {
            // Check access control
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT, Collections.singletonList(id),
                    key.getUserId(), session)) {
                errors.addError(new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER));
                return Either.error(errors);
            }

            //validate value state field --> 3 --> isTrafficked, it does not permit changes
            SiteMeasurementDTO sm = getSiteMeasurementDTO(dto.getId(), session);

            if (sm.getState() != null && sm.getState().equals(Constants.STATE_TRAFFICKED_SITEMEASUREMENT)) {
                //It is because in db to assure a unique name for each brand a trigger saves name value with uppercase
                if (!sm.getDupName().equals(dto.getName().toUpperCase())) {
                    errors.addError(new BusinessError(
                            ResourceBundleUtil.getString("sm.error.unableToChangeName"),
                            BusinessCode.INVALID, "name"));
                    return Either.error(errors);
                }
            } else {  // Check SM's name for duplicity
                if (siteMeasurementDao
                        .doesSiteMeasurementExists(dto.getName(), dto.getId(), dto.getBrandId(), session)) {
                    errors.addError(new BusinessError(ResourceBundleUtil
                            .getString("sm.error.duplicated", dto.getName(),
                                    String.valueOf(dto.getBrandId())), BusinessCode.DUPLICATE,
                            "name"));
                    return Either.error(errors);
                }
            }

            dto.setModifiedTpwsKey(key.getTpws());
            int affected = siteMeasurementDao.update(dto, session);
            if (affected > 0) {
                siteMeasurementDao.commit(session);
            } else {
                siteMeasurementDao.rollback(session);
                errors.addError(new BusinessError(
                        ResourceBundleUtil.getString("global.error.recordNotFound", id),
                        BusinessCode.NOT_FOUND, "id"));
                return Either.error(errors);
            }
            // Retrieve recently updated Site Measurement
            result = getSiteMeasurementDTO(dto.getId(), session);
        } catch (Exception e) {
            siteMeasurementDao.rollback(session);
            errors.addError(new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR));
            return Either.error(errors);
        } finally {
            siteMeasurementDao.close(session);
        }
        return Either.success(result);
    }

    private SiteMeasurementDTO getSiteMeasurementDTO(Long id, SqlSession session) {
        SiteMeasurementDTO result;
        result = siteMeasurementDao.get(id, session);
        if (result == null) {
            throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.NOT_FOUND, "id");
        }
        return result;
    }

    /**
     * Mark a Site Measurement as deleted, Note that is a logical deletion.
     *
     * @param id The ID of the Site Measurement
     * @param key Session ID of the user who updates the Site Measurement.
     * @return the Site Measurement marked as deleted.
     * @throws java.lang.Exception
     */
    public SiteMeasurementDTO removeSiteMeasurement(Long id, OauthKey key) throws Exception {
        log.info(key.toString()+ " Deleted "+ id);
        return this.siteMeasurementDao.delete(id, key);
    }
}

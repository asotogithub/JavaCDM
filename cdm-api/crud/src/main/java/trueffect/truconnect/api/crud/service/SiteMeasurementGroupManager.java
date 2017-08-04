package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
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
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementGroupDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by richard.jaldin on 6/9/2015.
 */
public class SiteMeasurementGroupManager extends AbstractGenericManager{

    private SiteMeasurementGroupDao smGroupDao;
    private UserDao userDao;
    private SiteMeasurementEventDao smEventDao;
    private SiteMeasurementDao smDao;
    private SiteMeasurementEventManager smEventManager;

    public SiteMeasurementGroupManager(SiteMeasurementGroupDao dao, UserDao userDao, SiteMeasurementEventDao smEventDao, SiteMeasurementDao smDao, AccessControl accessControl) {
        super(accessControl);
        this.smGroupDao = dao;
        this.userDao = userDao;
        this.smEventDao = smEventDao;
        this.smDao = smDao;
        this.smEventManager = new SiteMeasurementEventManager(smEventDao,userDao,accessControl,smDao,dao);
    }

    private SmGroup getSmGroup(Long id, SqlSession session) {
        SmGroup result;
        result = smGroupDao.get(id, session);
        if(result == null) {
            throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.NOT_FOUND, "id");
        }
        return result;
    }

    public Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> isSmGroupNameExist(Long smId,  String name, OauthKey key) {
        Errors errors = new Errors();
        if (name == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.empty", "SmGroup Name"),
                    ValidationCode.REQUIRED);
            errors.addError(error);
            return Either.error(errors);
        }

        if (smId == null) {
            ValidationError error = new ValidationError(
                    ResourceBundleUtil.getString("global.error.empty", "Sm Id"),
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

        SqlSession session = smGroupDao.openSession();
        Boolean result= false;
        try {
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT, Collections.singletonList(smId),
                    key.getUserId(), session)) {
                AccessError error = new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            result = smGroupDao.isSmGroupNametExist(smId, name, session);
        } finally {
            smGroupDao.close(session);
        }
        return Either.success(result);
    }

    public Either<trueffect.truconnect.api.commons.exceptions.business.Error, RecordSet<SmGroup>> getSiteMeasurementGroups(Long siteMeasurementId, OauthKey key) {
        if(siteMeasurementId == null){
            return Either.error(new Error(ResourceBundleUtil.getString("error.generic.empty", "site measurement Id"),
                    ValidationCode.REQUIRED));
        }

        SqlSession session = smGroupDao.openSession();
        RecordSet<SmGroup> result ;
        try {
            // Check access control
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT, Collections.singletonList(siteMeasurementId), key.getUserId(), session)) {
                return Either.error((Error)new AccessError(
                        ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER"),
                        SecurityCode.NOT_FOUND_FOR_USER, key.getUserId()));
            }
            result = smGroupDao.getSmGroupsBySiteMeasurement(siteMeasurementId, session);
        } catch(Exception e) {
            String message = String.format("sm.error.unableToReceiveGroups");
            log.warn(message, e);
            return Either.error(new Error(message, BusinessCode.INTERNAL_ERROR));
        } finally {
            smGroupDao.close(session);
        }
        return Either.success(result);
    }

    public Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmGroup> createSmGroup(SmGroup smGroup,
                                                                                              OauthKey key) {
        //validations
        // Nullability checks
        if(smGroup == null){
            throw new IllegalArgumentException("SmGroup cannot be null");
        }

        if(key == null){
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        if (smGroup.getMeasurementId() == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.null", "measurement Id"),
                    ValidationCode.REQUIRED));
        }

        //business logic
        if (StringUtils.isBlank(smGroup.getGroupName())) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.empty", "groupName"),
                    ValidationCode.REQUIRED));
        }

        if (smGroup.getGroupName().length() > Constants.SITE_MEASUREMENT_GROUP_NAME_MAX_LENGTH) {
            return Either.error(new Error(ResourceBundleUtil
                    .getString("global.error.invalidStringLength", "groupName",
                            Constants.SITE_MEASUREMENT_GROUP_NAME_MAX_LENGTH),
                    ValidationCode.TOO_LONG));
        }

        if (StringUtils.contains(smGroup.getGroupName(), " ")) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.name", "groupName"),
                    ValidationCode.INVALID));
        }

        SmGroup result = null;
        SqlSession session = smGroupDao.openSession();
        try {
                        
            // Check access control
            if (!userValidFor(AccessStatement.SITE_MEASUREMENT, Collections.singletonList(smGroup.getMeasurementId()), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }

            Boolean isNameExist = smGroupDao.isSmGroupNametExist(smGroup.getMeasurementId(), smGroup.getGroupName(), session);

            if (isNameExist) {
                return Either.error(new Error(ResourceBundleUtil.getString("global.error.nameAlreadyExist", "SmGroup"),
                        ValidationCode.REQUIRED));
           }
            
            //PK of SiteMeasurementGroup
            Long id = smGroupDao.getNextId(session);
            smGroup.setId(id);
            smGroup.setCreatedTpwsKey(key.getTpws());
            smGroupDao.create(smGroup, session);

            // Retrieve recently created Site Measurement Group
            result = getSmGroup(id, session);
            if (smGroup.getSmEvent() != null && result != null ) {
                SmEvent smEvent = smGroup.getSmEvent();
                smEvent.setSmGroupId(id);
                Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmEvent> res =
                        smEventManager.createSmEvent(smEvent, session, key);
                if (res.isError()) {
                    return Either.error(res.error());
                }
                else {
                    result.setSmEvent(res.success());
                }
            }

            smGroupDao.commit(session);
        } catch (Exception e) {
            smGroupDao.rollback(session);

            return Either.error(new Error(ResourceBundleUtil.getString(e.getMessage()),
                    BusinessCode.INTERNAL_ERROR));
        } finally{
            smGroupDao.close(session);

        }
        return Either.success(result);
    }
}

package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.validation.AdvertiserValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Collections;

/**
 *
 * @author marleny.patsi
 */
public class AdvertiserManager extends AbstractGenericManager {

    private AdvertiserDao advertiserDao;
    private UserDao userDao;
    private AdvertiserValidator validator;

    public AdvertiserManager(AdvertiserDao dao, UserDao userDao, AccessControl accessControl) {
        super(accessControl);
        this.advertiserDao = dao;
        this.userDao = userDao;
        validator = new AdvertiserValidator();
    }

    /**
     * Gets an Advertiser based on its id
     *
     * @param id the ID of the Advertiser.
     * @param key The OAuth object that contains the User id
     * @return the Advertiser
     * @throws java.lang.Exception
     */
    public Advertiser get(Long id, OauthKey key) throws Exception {

        Advertiser result = null;
        SqlSession session = advertiserDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.ADVERTISER, id, key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AdvertiserId", Long.toString(id), key.getUserId()));
            }
            result = advertiserDao.get(id, session);
        } catch (Exception e) {
            advertiserDao.rollback(session);
            throw e;
        } finally {
            advertiserDao.close(session);
        }
        return result;
    }

    /**
     * Gets a RecordSet of Advertisers
     *
     * @param criteria
     * @param key
     * @return a RecordSet of Advertisers
     * @throws java.lang.Exception
     */
    public RecordSet<Advertiser> getAdvertisers(SearchCriteria criteria, OauthKey key) throws SearchApiException {

        RecordSet<Advertiser> result = null;
        SqlSession session = advertiserDao.openSession();
        try {
            if (criteria.getPageSize() > 1000) {
                throw new SearchApiException("The page size allows up to 1000 records.");
            }
            if (criteria.getStartIndex() < 0) {
                throw new SearchApiException("Cannot retrieve records for start index: " + criteria.getStartIndex() + ". The minimum start index is: 0");
            }

            //call dao
            result = advertiserDao.getAdvertisers(criteria, key.getUserId(), session);
        } finally {
            advertiserDao.close(session);
        }
        return result;
    }

    /**
     * Gets a set of Advertisers by UserId
     *
     * @param userId
     * @param key
     * @return a RecordSet of Advertisers
     */
    public RecordSet<Advertiser> getAdvertisersByUserId(String userId, OauthKey key) {

        //validations
        //nullability checks
        if (userId == null) {
            throw new IllegalArgumentException("User's id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        // Check that id matches DTO id.
        if (key.getUserId() != null && !userId.equalsIgnoreCase(key.getUserId())) {
            throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.ENTITY_ID_MISMATCH, null);
        }

        RecordSet<Advertiser> result;
        SqlSession session = advertiserDao.openSession();
        try {
            User user = userDao.get(userId, session);
            // Check access control
            if (!userValidFor(AccessStatement.AGENCY, user.getAgencyId(), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            //call Dao
            result = advertiserDao.getAdvertisersByUserId(userId, session);
        } finally {
            advertiserDao.close(session);
        }
        return result;
    }

    /**
     * Create a new Advertiser after complete validations
     *
     * @param advertiser Advertiser to create
     * @param key
     * @return the Advertiser created
     * @throws java.lang.Exception
     */
    public Advertiser create(Advertiser advertiser, OauthKey key) throws Exception {
        String className = advertiser.getClass().getSimpleName();
        BeanPropertyBindingResult pbResult = new BeanPropertyBindingResult(advertiser, className);
        //validations
        // Nullability checks
        if (advertiser.getId() != null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.postWithId", "Advertiser"));
        }

        validator.validate(advertiser, pbResult);
        if (pbResult.hasErrors()) {
            throw new ValidationException(pbResult);
        }

        Advertiser result = null;
        SqlSession session = advertiserDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.AGENCY, advertiser.getAgencyId(), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AgencyId", Long.toString(advertiser.getAgencyId()), key.getUserId()));
            }
            if (advertiserDao.advertiserNameExists(advertiser, session)) {
                throw new ValidationException("Advertiser name already exists.");
            }
            //PK of Advertiser
            Long id = advertiserDao.getNextId(session);
            advertiser.setId(id);
            advertiser.setCreatedTpwsKey(key.getTpws());
            //call dao
            advertiserDao.create(advertiser, session);
            advertiserDao.commit(session);
            // Retrieve recently created Advertiser
            result = advertiserDao.get(id, session);
            log.info(key.toString() + " Saved " + result);
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            advertiserDao.rollback(session);
            throw e;
        } finally {
            advertiserDao.close(session);
        }
        return result;
    }

    /**
     * Updates data of an Advertiser after complete validations
     *
     * @param id
     * @param advertiser Advertiser to updated
     * @param key
     * @return the Advertiser updated
     * @throws java.lang.Exception
     */
    public Advertiser update(Long id, Advertiser advertiser, OauthKey key) throws Exception {
        String className = advertiser.getClass().getSimpleName();
        BeanPropertyBindingResult pbResult = new BeanPropertyBindingResult(advertiser, className);

        //validations
        // Nullability checks
        if (advertiser.getId() == null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.putWithoutId", "Advertiser"));
        }

        Advertiser result = null;
        SqlSession session = advertiserDao.openSession();
        try {
            Advertiser existentAdvertiser = advertiserDao.get(advertiser.getId(), session);
            if (existentAdvertiser == null) {
                throw new ValidationException("Can't update record if it doesn't exist");
            }

            // Check access control
            if (!userValidFor(AccessStatement.ADVERTISER, advertiser.getId(), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AdvertiserId", Long.toString(advertiser.getId()), key.getUserId()));
            }

            //set values
            if (StringUtils.isBlank(advertiser.getIsHidden())) {
                advertiser.setIsHidden(existentAdvertiser.getIsHidden());
            }

            validator.validate(id, advertiser, pbResult);
            if (pbResult.hasErrors()) {
                throw new ValidationException(pbResult);
            }

            if (StringUtils.isEmpty(advertiser.getName())) {
                advertiser.setName(existentAdvertiser.getName());
            }

            if (advertiserDao.advertiserNameExists(advertiser, session)) {
                throw new ValidationException("Advertiser name already exists.");
            }

            advertiser.setAgencyId(existentAdvertiser.getAgencyId());
            advertiser.setModifiedTpwsKey(key.getTpws());

            //call dao
            advertiserDao.update(advertiser, session);
            advertiserDao.commit(session);
            // Retrieve recently updated Advertiser
            result = advertiserDao.get(advertiser.getId(), session);
            log.info(key.toString()+" Updated "+id);
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            advertiserDao.rollback(session);
            throw e;
        } finally {
            advertiserDao.close(session);
        }

        return result;
    }

    public SuccessResponse hardRemove(Long id, OauthKey key) throws Exception {

        //validations
        //nullability checks
        if (id == null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.empty", "Advertiser Id"));
        }

        SqlSession session = advertiserDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.ADVERTISER, id, key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AdvertiserId", Long.toString(id), key.getUserId()));
            }

            //call dao
            advertiserDao.hardRemove(id, session);
            advertiserDao.commit(session);
            log.info(key.toString()+ " Deleted "+ id);
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            advertiserDao.rollback(session);
            throw e;
        } finally {
            advertiserDao.close(session);
        }
        return new SuccessResponse("Advertiser " + id + " successfully deleted physically.");
    }

    //TODO: This method is being used by AdvertiserService.getTagTypes method.
    //When that method will refactored this method can be deleted.
    public void isValidForAgency(Long id, Long siteId, OauthKey key) throws Exception {
        SqlSession session = null;
        try {
            session = advertiserDao.openSession();
            if (!userValidFor(AccessStatement.ADVERTISER, id, key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AdvertiserId", Long.toString(id), key.getUserId()));
            }
            if (!userValidFor(AccessStatement.SITE, Collections.singletonList(siteId), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteId", Long.toString(siteId), key.getUserId()));
            }
        } catch (Exception e) {
            advertiserDao.rollback(session);
            throw e;
        } finally {
            advertiserDao.close(session);
        }
    }
}

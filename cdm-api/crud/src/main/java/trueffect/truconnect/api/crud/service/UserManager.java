package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.AgencyUser;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.UserView;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Rambert Rioja
 */
public class UserManager extends AbstractGenericManager{

    // Regular expression for username validation (email), taken from XLS.

    public static final int USERNAME_MAX_LENGTH = 64;
    public static final int PASSWORD_MAX_LENGTH = 256;
    private UserDao userDao;

    public UserManager(UserDao userDao, AccessControl accessControl) {
        super(accessControl);
        this.userDao = userDao;
    }

    /**
     * Returns the User based on the email
     *
     * @param userId email of the User to return
     * @param requesterUserId The User id that executes this method
     * @return the User of the id
     * @throws java.lang.Exception
     */
    public User get(String userId, String requesterUserId) throws Exception {

        // Get a session
        SqlSession session = userDao.openSession();
        User user = null;
        try {
            //Validations
            if (!accessControl.isAdmin(requesterUserId, session)) {
                Long agencyId = userDao.getAgencyIdByUser(requesterUserId, session);
                if (!userValidFor(AccessStatement.AGENCY, agencyId, userId, session)) {
                    throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + requesterUserId);
                }
            }
            // Get basic information for a User
            user = this.userDao.get(userId, requesterUserId, session);
            // Get Permissions for the user
            if (user != null) {
                List<String> permissions = userDao.getPermissionsByUser(userId, session);
                user.setPermissions(permissions);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            userDao.close(session);
        }
        return user;
    }

    /**
     * Saves a User
     *
     * @param user User to be saved.
     * @param key
     * @return the new User
     * @throws java.lang.Exception
     */
    public User save(User user, OauthKey key) throws Exception {
        if (StringUtils.isBlank(user.getUserName())) {
            throw new ValidationException("User ID (user name) cannot be null.");
        }
        else if (user.getUserName().length() > USERNAME_MAX_LENGTH) {
            throw new ValidationException("Invalid User ID, it supports "
                    + "characters up to " + USERNAME_MAX_LENGTH);
        }
        else if (!user.getUserName().matches(ValidationConstants.REGEXP_EMAIL_ADDRESS)) {
            throw new ValidationException("Invalid Username format");
        }
        if (user.getPassword() != null && user.getPassword().length() > PASSWORD_MAX_LENGTH) {
            throw new ValidationException("Invalid User password, it supports "
                    + "characters up to " + PASSWORD_MAX_LENGTH);
        }
        if (user.getAgencyId() == null) {
            throw new ValidationException("Invalid Agency Id, it cannot be null.");
        }
        this.userDao.isValidUserToBeSaved(user.getUserName(), user.getAgencyId(), key.getUserId());
        user.setPassword(EncryptUtil.sha512(user.getPassword()));
        log.info(key.toString()+ " Saved "+ user);
        return this.userDao.save(user, key);
    }

    /**
     * Updates a User
     *
     * @param id User email and primary key
     * @param user User to be updated.
     * @param key
     * @return the User updated
     * @throws java.lang.Exception
     */
    public User update(String id, User user, OauthKey key) throws Exception {
        if (id == null || user.getUserName() == null) {
            throw new ValidationException("User ID (user name) cannot be null.");
        } else if (id.length() > 64) {
            throw new ValidationException("Invalid User ID, it supports "
                    + "characters up to 64");
        }
        String userName = user.getUserName();
        if (userName != null && !id.equals(userName)) {
            throw new ValidationException("Entity in request body does not match the requested id.");
        }
        if (user.getPassword() != null && user.getPassword().length() > 256) {
            throw new ValidationException("Invalid User password, it supports "
                    + "characters up to 256");
        }
        if (user.getContactId() == null) {
            throw new ValidationException("Invalid contactId, it cannot be null.");
        }
        User existentUser = userDao.get(id, key.getUserId());
        if (existentUser == null) {
            throw new ValidationException("Can't update record if it doesn't exist");
        }
        user.setPassword(EncryptUtil.sha512(user.getPassword()));
        log.info(key.toString()+ " Updated "+ user);
        return this.userDao.update(user, key);
    }

    /**
     * Removes an User based on the email
     *
     * @param id email of the User to be removed
     * @param key
     * @return
     * @throws java.lang.Exception
     */
    public SuccessResponse remove(String id, OauthKey key) throws Exception {
        userDao.remove(id, key);
        log.info(key.toString()+ " Saved "+ id);
        return new SuccessResponse("User " + id + " successfully deleted.");
    }

    public AgencyUser getAgencyUserByTPWSKey(String tpws) throws Exception {
        //validations
        // Nullability checks
        if (tpws == null) {
            throw new IllegalArgumentException("Tpws cannot be null");
        }
        
        //Session
        SqlSession session = userDao.openSession();
        AgencyUser result;
        try {
            result = userDao.getByTPWSKey(tpws, session);
        } finally {
            userDao.close(session);
        }
        return result;
    }

    public List<String> getPermissionsByUser(String userId, OauthKey key) throws Exception {
        SqlSession session = userDao.openSession();
        List<String> permissions = null;
        try {
            //Validations
            if (!accessControl.isAdmin(key.getUserId(), session)) {
                Long agencyId = userDao.getAgencyIdByUser(key.getUserId(), session);
                if (!userValidFor(AccessStatement.AGENCY, agencyId, userId, session)) {
                    throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
                }
            }
            // Get basic information for a User
            permissions = this.userDao.getPermissionsByUser(userId, session);
        } catch (Exception e) {
            throw e;
        } finally {
            userDao.close(session);
        }
        return permissions;
    }
    
    public RecordSet<UserView> getUserView(Long agencyId, OauthKey key) {
        if (agencyId == null) {
            throw new IllegalArgumentException("Agency ID cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }
        //obtain session
        SqlSession session = userDao.openSession();
        RecordSet<UserView> result = new RecordSet<>();
        List<UserView> userView = new ArrayList<>();

        try {
            //check access control
            if (!userValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyId), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            userView = userDao.getUserView(agencyId, session);
        } finally {
            userDao.close(session);
        }
        result.setRecords(userView);
        result.setTotalNumberOfRecords(userView.size());
        return result;
    }
    
    /**
     * Set User's limit domains
     *
     * @param id  The User's Id 
     * @param user The User to set limit domains
     * @param key     The Oauth key to track who is executing this operation (AKA the admin)
     * @return The User with the limit domains updated
     */
    public Either<Error, Boolean> setUserLimits(String id, User user, OauthKey key) {
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "UserId"));
        }
        if (user == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "User"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuthKey"));
        }

        boolean notLimitedDomains = user.getLimitDomains() == null || (!user.getLimitDomains().equals("Y") && !user.getLimitDomains().equals("N"));
        boolean notLimitedAdvertisers = user.getLimitAdvertisers() == null || (!user.getLimitAdvertisers().equals("Y") && !user.getLimitAdvertisers().equals("N"));
        if (notLimitedDomains && notLimitedAdvertisers){
            return Either.error(new Error(ResourceBundleUtil.getString("BusinessCode.INVALID"), ValidationCode.INVALID));
        }
        SqlSession session = userDao.openSession();
        try {
            if (!accessControl.isAdmin(key.getUserId(), session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "UserId", id, key.getUserId())));
            }
            accessControl.isAdmin(key.getUserId(), session);
            userDao.setUserLimits(user, key, session);
            userDao.commit(session);
        } catch (Exception e) {
            userDao.rollback(session);
            return Either.error(new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR));
        } finally {
            userDao.close(session);
        }
        return Either.success(true);
    }

}

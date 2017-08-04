package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.AgencyUser;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.UserView;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Rambert Rioja
 */
public class UserDaoImpl extends AbstractGenericDao implements UserDao {

    private static final String STATEMENT_DELETE_USER = "UserPkg.deleteUser";
    private static final String STATEMENT_DELETE_USER_ROLE = "UserPkg.deleteUserRole";
    private static final String STATEMENT_GET_AGENCY_ID_BY_USER = "UserPkg.getAgencyIdByUser";
    private static final String STATEMENT_GET_AGENCY_USER_BY_TPWS = "UserPkg.getAgencyUserByTpws";
    private static final String STATEMENT_GET_CONTACT = "Contact.getContact";
    private static final String STATEMENT_GET_PERMISSIONS_BY_USER = "UserPkg.getPermissionsByUser";
    private static final String STATEMENT_GET_ROLES = "UserPkg.getRoles";
    private static final String STATEMENT_GET_ROLE_ID = "UserPkg.getRoleId";
    private static final String STATEMENT_GET_USER = "UserPkg.getUser";
    private static final String STATEMENT_GET_USER_CONTACT = "UserPkg.getUserContact";
    private static final String STATEMENT_GET_USERS_VIEW = "UserPkg.getUserView";
    private static final String STATEMENT_GET_TRAFFICKING_CONTACTS = "UserPkg.getTraffickingContacts";
    private static final String STATEMENT_INSERT_USER = "UserPkg.insertUser";
    private static final String STATEMENT_INSERT_USER_ROLE = "UserPkg.insertUserRole";
    private static final String STATEMENT_UPDATE_USER = "UserPkg.updateUser";
    private static final String STATEMENT_SET_USER_LIMITS = "UserPkg.setUserLimits";


    @Deprecated
    public UserDaoImpl(PersistenceContext persistenceContext, AccessControl accessControl) {
        super(persistenceContext, accessControl);
    }

    public UserDaoImpl (PersistenceContext context){
        super(context);
    }

    @Override
    public User get(String userIdTobeRetreived, String sessionUserId) throws Exception {
        SqlSession session = null;
        User user = null;
        try {
            session = getPersistenceContext().beginTransaction();
            if (!getAccessControl().isAdmin(sessionUserId, session)) {
                Long agencyId = getAgencyIdByUser(sessionUserId, session);
                if (!getAccessControl().isUserValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyId), userIdTobeRetreived, session)) {
                    throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + sessionUserId);
                }
            }
            user = get(userIdTobeRetreived, sessionUserId, session);
            getPersistenceContext().endTransaction(session);
        } catch (Exception e) {
            getPersistenceContext().rollbackTransaction(session);
            throw e;
        }
        return user;
    }

    @Override
    public boolean isValidUserToBeSaved(String userIdTobeRetreived, Long agencyId, String sessionUserId) throws Exception {
        SqlSession session = null;
        try {
            session = getPersistenceContext().beginTransaction();
            if (!getAccessControl().isAdmin(sessionUserId, session)) {
                Long currentAgencyId = getAgencyIdByUser(sessionUserId, session);
                if (agencyId == null) {
                    throw new ValidationException("Invalid Agency Id, it cannot be null.");
                } else if (!agencyId.equals(currentAgencyId)) {
                    throw new ValidationException("Access to the requested data is denied.");
                }
            }
            User user;
            try {
                user = get(userIdTobeRetreived, sessionUserId, session);
                if (user != null) {
                    throw new ConflictException("User name already exists!");
                }
            } catch (NotFoundException e) {
            }
            getPersistenceContext().endTransaction(session);
            return true;
        } catch (Exception e) {
            getPersistenceContext().rollbackTransaction(session);
            throw e;
        }
    }

    @Override
    public Long getAgencyIdByUser(String userId, SqlSession session) throws Exception {
        return (Long) getPersistenceContext().selectOne(STATEMENT_GET_AGENCY_ID_BY_USER, userId, session);
    }

    @Override
    public User get(String id, String userId, SqlSession session) throws Exception {
        User user = (User) getPersistenceContext().selectOne(STATEMENT_GET_USER, id, session);
        if (user != null) {
            List<String> roles = (List<String>) getPersistenceContext().selectList(STATEMENT_GET_ROLES, id, session);
            user.setRoles(roles);
            return user;
        }
        throw new NotFoundException("User not found: " + id);
    }

    @Override
    public User get(String id, SqlSession session) {
        User user = getPersistenceContext().selectSingle(STATEMENT_GET_USER, id, session, User.class);
        if (user != null) {
            List<String> roles = getPersistenceContext().selectMultiple(STATEMENT_GET_ROLES, id, session);
            user.setRoles(roles);
            return user;
        }
        return null;
    }

    @Override
    public User save(User user, OauthKey key) throws Exception {
        SqlSession session = null;
        User result = null;
        try {
            session = getPersistenceContext().beginTransaction();
            if (!getAccessControl().isAdmin(key.getUserId(), session)) {
                if (user.getRoles().contains("ROLE_APP_ADMIN")) {
                    throw new ValidationException("You are not allowed to create an admin user!");
                }
                Long agencyId = getAgencyIdByUser(key.getUserId(), session);
                if (user.getAgencyId() == agencyId) {
                    throw new ValidationException("You are not allowed to create user out of your Agency!");
                }
            }
            validateContact(user, user.getContactId(), session);
            setStaticUserRoles(user);
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("userId", user.getUserName());
            parameter.put("password", user.getPassword());
            parameter.put("agencyId", user.getAgencyId());
            parameter.put("contactId", user.getContactId());
            parameter.put("isAppAdmin", user.getIsAppAdmin());
            parameter.put("isClientAdmin", user.getIsClientAdmin());
            parameter.put("limitDomains", Constants.NO_FLAG);
            parameter.put("limitAdvertisers", user.getLimitAdvertisers());
            parameter.put("createdTpwsKey", key.getTpws());
            getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_INSERT_USER, parameter, session);
            result = get(user.getUserName(), key.getUserId(), session);
            createUserRole(result.getId(), user.getRoles(), session);
            result = get(user.getUserName(), key.getUserId(), session);
            getPersistenceContext().commitTransaction(session);
        } catch (Exception e) {
            e.printStackTrace();
            getPersistenceContext().rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    @Override
    public void createUserRole(Long userId, List<String> roles, SqlSession session) throws Exception {
        if (roles != null) {
            for (String role : roles) {
                Long id = (Long) getPersistenceContext().selectOne("getNextId", "SEQ_GBL_DIM", session);
                HashMap<String, Object> parameter = new HashMap<String, Object>();
                Long roleId = getRoleId(role, session);
                if (roleId != null) {
                    parameter.put("userRoleId", id);
                    parameter.put("userId", userId);
                    parameter.put("roleId", roleId);
                    getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_INSERT_USER_ROLE, parameter, session);
                } else {
                    throw new ValidationException("Invalid role name: " + role);
                }
            }
        }
    }

    private void deleteUserRoles(String id, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("userId", id);
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_DELETE_USER_ROLE, parameter, session);
    }

    @Override
    public Long getRoleId(String role, SqlSession session) throws Exception {
        Long id = (Long) getPersistenceContext().selectOne(STATEMENT_GET_ROLE_ID, role, session);
        return id;
    }

    @Override
    public User update(User user, OauthKey key) throws Exception {
        SqlSession session = null;
        User result = null;
        try {
            session = getPersistenceContext().beginTransaction();
            result = update(user, key, session);
            getPersistenceContext().commitTransaction(session);
        } catch (Exception e) {
            getPersistenceContext().rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    @Override
    public User update(User user, OauthKey key, SqlSession session) throws Exception {
        User result = null;
        if (!getAccessControl().isAdmin(key.getUserId(), session)) {
            if (user.getRoles().contains("ROLE_APP_ADMIN")) {
                throw new ValidationException("You are not allowed to create an admin user!");
            }
            Long agencyId = getAgencyIdByUser(key.getUserId(), session);
            if (user.getAgencyId() == agencyId) {
                throw new ValidationException(
                        "You are not allowed to create user out of your Agency!");
            }
        }
        deleteUserRoles(user.getUserName(), session);
        setStaticUserRoles(user);
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("userId", user.getUserName());
        parameter.put("password", user.getPassword());
        parameter.put("isAppAdmin", user.getIsAppAdmin());
        parameter.put("isClientAdmin", user.getIsClientAdmin());
        parameter.put("limitDomains", user.getLimitDomains());
        parameter.put("limitAdvertisers", user.getLimitAdvertisers());
        parameter.put("isDisabled", user.getIsDisabled() == null ? "N" : user.getIsDisabled());
        parameter.put("isTraffickingContact", user.getIsTraffickingContact());
        parameter.put("createdTpwsKey", key.getTpws());
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_UPDATE_USER, parameter, session);
        result = get(user.getUserName(), key.getUserId(), session);
        createUserRole(result.getId(), user.getRoles(), session);
        result = get(user.getUserName(), key.getUserId(), session);
        return result;
    }
    
    @Override
    public void setUserLimits(User user, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("userId", user.getUserName());
        parameter.put("limitDomains", user.getLimitDomains());
        parameter.put("limitAdvertisers", user.getLimitAdvertisers());
        getPersistenceContext().execute(STATEMENT_SET_USER_LIMITS, parameter, session);
    }

    @Override
    public void remove(String id, OauthKey key) throws Exception {
        SqlSession session = null;
        try {
            session = getPersistenceContext().beginTransaction();
            if (!getAccessControl().isAdmin(key.getUserId(), session)) {
                if (getAccessControl().isAdmin(id, session)) {
                    throw new ValidationException("You are not allowed to delete an admin user!");
                }
                Long agencyId = getAgencyIdByUser(key.getUserId(), session);
                if (!getAccessControl().isUserValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyId), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AgencyId", Long.toString(agencyId), key.getUserId()));
                }
            }
            deleteUserRoles(id, session);
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("userId", id);
            parameter.put("modifiedTpwsKey", key.getTpws());
            getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_DELETE_USER, parameter, session);
            getPersistenceContext().commitTransaction(session);
        } catch (Exception e) {
            getPersistenceContext().rollbackTransaction(session);
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public AgencyUser getByTPWSKey(String tpws, SqlSession session) {
        AgencyUser result = null;
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("tpws", tpws);
        getPersistenceContext().selectSingle(STATEMENT_GET_AGENCY_USER_BY_TPWS, parameter, session,
                AgencyUser.class);
        if (parameter.get("refCursor") != null) {
            result = ((List<AgencyUser>) parameter.get("refCursor")).get(0);
        }
        return result;
    }

    /**
     * Returns all the Permissions assigned to the User's assigned Roles
     *
     * @param userId  The User Id to obtain the Permissions from
     * @param session The MyBatis {@code SqlSession} to use
     * @return The {@code List} of {@code RolePermission} for the provided {@code userId}
     */
    @Override
    public List<String> getPermissionsByUser(String userId, SqlSession session) throws Exception {
        return (List<String>) getPersistenceContext().selectList(STATEMENT_GET_PERMISSIONS_BY_USER, userId, session);
    }

    private void setStaticUserRoles(User user) {
        List<String> roles = user.getRoles();
        boolean role = roles.remove("ROLE_APP_ADMIN");
        if (role) {
            user.setIsAppAdmin("Y");
        } else {
            user.setIsAppAdmin("N");
        }
        role = roles.remove("ROLE_CLIENT_ADMIN");
        if (role) {
            user.setIsClientAdmin("Y");
        } else {
            user.setIsClientAdmin("N");
        }
        role = roles.remove("ROLE_SYS_ADMIN");
        if (role) {
            user.setIsSysAdmin("Y");
        } else {
            user.setIsSysAdmin("N");

        }
    }

    private void validateContact(User user, Long contactId, SqlSession session) throws Exception {

        if (contactId == null) {
            throw new ValidationException("Invalid contactId, it cannot be null.");
        }
        Long contactCounter = (Long) getPersistenceContext().selectOne(STATEMENT_GET_USER_CONTACT, contactId, session);
        if (contactCounter != null && contactCounter.compareTo(0L) > 0) {
            throw new ValidationException("Invalid contactId, this contact is already used by another user");
        }
        Contact contact = (Contact) getPersistenceContext().selectOne(STATEMENT_GET_CONTACT, contactId, session);
        if (contact != null && !user.getUserName().equals(contact.getEmail())) {
            throw new ValidationException("Invalid Username, it must be the same as Contact email address.");
        }
    }   
    
    @Override
    public List<UserView> getUserView(Long agencyId, SqlSession session){
        return getPersistenceContext().selectMultiple(STATEMENT_GET_USERS_VIEW, agencyId, session);
    }
    
    @Override
    public List<Integer> getTraffickingContacts(List<Integer> agencyContacts, final SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        List<Integer> result = new ArrayList<>();
        Accumulator<List<Integer>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<Integer>>(
                "agencyContacts",
                agencyContacts,
                resultAccumulator,
                parameter) {
                    @Override
                    public List<Integer> execute(Object parameters) {
                        return getPersistenceContext().selectMultiple(STATEMENT_GET_TRAFFICKING_CONTACTS,
                                parameters, session);
                    }
                }.getResults();
    }
}

package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.CookieDomainDTO;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CookieDomainDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.util.CrudApiExceptionHandler;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.Collections;
import java.util.List;

/**
 * Created by richard.jaldin on 6/18/2015.
 */
public class CookieDomainManager extends AbstractGenericManager {

    private static final Log log = LogFactory.getLog(CookieDomainManager.class);

    private CookieDomainDao cookieDomainDao;
    private UserDao userDao;

    public CookieDomainManager(CookieDomainDao cookieDomainDao, UserDao userDao,
                               AccessControl accessControl) {
        super(accessControl);
        this.cookieDomainDao = cookieDomainDao;
        this.userDao = userDao;
    }

    public CookieDomain create(CookieDomain domain, OauthKey key) throws Exception {
        //validations
        // Nullability checks
        checkNullabilityOfInputs(domain, "CookieDomain cannot be null", key);
        if (domain.getDomain() == null) {
            throw new IllegalArgumentException("Domain cannot be null");
        }
        if (domain.getAgencyId()== null) {
            throw new IllegalArgumentException("Agency Id cannot be null");
        }

        CookieDomain result = new CookieDomain();
        SqlSession session = cookieDomainDao.openSession();
        try {
            if (!userValidFor(AccessStatement.AGENCY, domain.getAgencyId(), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
            }
            if (cookieDomainDao.existCookieDomain(domain, session)) {
                throw new ValidationException("Domain name already exists.");
            }

            //for the nextval of cookie domain
            Long id = cookieDomainDao.getNextId(session);
            domain.setId(id);
            domain.setCreatedTpwsKey(key.getTpws());
            cookieDomainDao.create(domain, session);
            result = getCookieDomain(id, session);
            log.info(key.toString()+" Saved "+result);
            cookieDomainDao.commit(session);
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            cookieDomainDao.rollback(session);
            throw e;
        } finally {
            cookieDomainDao.close(session);
        }
        return result;
    }

    public CookieDomain get(Long id, OauthKey key) throws Exception {
        //validations
        // Nullability checks
        checkNullabilityOfInputs(id, "CookieDomain's id cannot be null", key);

        CookieDomain result = new CookieDomain();
        SqlSession session = cookieDomainDao.openSession();
        try {
            if (!userValidFor(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
            }
               
            result = getCookieDomain(id, session);
            cookieDomainDao.commit(session);
        } finally {
            cookieDomainDao.close(session);
        }
        return result;
    }

    public SuccessResponse delete(Long id, OauthKey key) throws Exception {
        //validations
        // Nullability checks
        checkNullabilityOfInputs(id, "CookieDomain's id cannot be null", key);

        SqlSession session = cookieDomainDao.openSession();
        try {
            if (!userValidFor(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CookieDomainId", Long.toString(id), key.getUserId()));
            }
            CookieDomain domain = getCookieDomain(id, session);
            domain.setModifiedTpwsKey(key.getTpws());
            //Parsing the input parameters of the process
            cookieDomainDao.delete(domain, session);
            cookieDomainDao.commit(session);
            log.info(key.toString()+" Deleted "+ id);
            return new SuccessResponse("CookieDomain " + id + " successfully deleted");
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            cookieDomainDao.rollback(session);
            throw new ValidationException(CrudApiExceptionHandler.getMessage(e));
        } finally {
            cookieDomainDao.close(session);
        }
    }

    public RecordSet<CookieDomainDTO> getDomains(String userId, OauthKey key) throws Exception {
        //validations
        // Nullability checks
        checkNullabilityOfInputs(userId, "The user's id cannot be null", key);

        RecordSet<CookieDomainDTO> result;
        SqlSession session = cookieDomainDao.openSession();
        try {
            User user = userDao.get(userId, key.getUserId(), session);
            if (!userValidFor(AccessStatement.AGENCY, user.getAgencyId(), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
            }
            List<CookieDomainDTO> domains = cookieDomainDao.getDomains(userId, session);
            result = new RecordSet<>(0, domains.size(), domains.size(), domains);
            cookieDomainDao.commit(session);
        } catch (Exception e) {
            cookieDomainDao.rollback(session);
            throw new ValidationException(CrudApiExceptionHandler.getMessage(e));
        } finally {
            cookieDomainDao.close(session);
        }
        return result;
    }

    private CookieDomain getCookieDomain(Long id, SqlSession session) throws Exception {
        CookieDomain result;
        result = cookieDomainDao.get(id, session);
        if (result == null) {
            throw new NotFoundException("Cookie Domain not found");
        }
        return result;
    }

    private void checkNullabilityOfInputs(Object input, String errorMessage, OauthKey key) {
        if(input == null){
            throw new IllegalArgumentException(errorMessage);
        }
        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }
    }
}

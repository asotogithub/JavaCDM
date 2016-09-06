package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.CookieTargetTemplate;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CookieTargetTemplateDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Rambert Rioja
 */
public class CookieTargetTemplateManager extends AbstractGenericManager {

    private CookieTargetTemplateDao cookieTargetTemplateDao;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CookieTargetTemplateManager(CookieTargetTemplateDao cookieTargetTemplateDao, AccessControl accessControl) {
        super(accessControl);
        this.cookieTargetTemplateDao = cookieTargetTemplateDao;
    }

    public RecordSet<CookieTargetTemplate> getByDomain(Long cookieDomainId, OauthKey key) throws Exception {
        //null validations
        if (cookieDomainId == null) {
            throw new IllegalArgumentException("CookieDomain ID cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("Oauth key cannot be null");
        }

        //obtain session
        SqlSession session = cookieTargetTemplateDao.openSession();
        RecordSet<CookieTargetTemplate> result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS, Collections.singletonList(cookieDomainId), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CookieDomainId", Long.toString(cookieDomainId), key.getUserId()));
            }
            //call dao
            List<CookieTargetTemplate> cookieTargets = cookieTargetTemplateDao.getByCookieDomainId(cookieDomainId, session);
            if (cookieTargets.isEmpty()) { // empty result.
                result = new RecordSet<>(0, 0, 0, cookieTargets);
            }
            result = new RecordSet<>(0, cookieTargets.size(), cookieTargets.size(), cookieTargets);
        } finally {
            cookieTargetTemplateDao.close(session);
        }
        return result;
    }

    /**
     * Saves a CookieTargetTemplate
     *
     * @param cookieDomainId CookieDomain ID
     * @param cookie
     * @param key
     * @return the new CookieTargetTemplate
     * @throws java.lang.Exception
     */
    public CookieTargetTemplate saveCookieTargetTemplate(Long cookieDomainId, CookieTargetTemplate cookie, OauthKey key) throws Exception {
        //null validations
        if (cookieDomainId == null) {
            throw new IllegalArgumentException("CookieDomain ID cannot be null");
        }
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("Oauth key cannot be null");
        }

        //obtain session
        SqlSession session = cookieTargetTemplateDao.openSession();
        CookieTargetTemplate result;
        try {
            //check access control
            if (!userValidFor(AccessStatement.COOKIE_DOMAIN_BY_LIMIT_DOMAINS, Collections.singletonList(cookie.getCookieDomainId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CookieDomainId", Long.toString(cookie.getCookieDomainId()), key.getUserId()));
            }
            if (StringUtils.isBlank(cookie.getCookieName())) {
                throw new ValidationException(ResourceBundleUtil.getString("global.error.empty",
                        "Cookie Target Template name"));
            } else if (cookie.getCookieName().length() > Constants.COOKIE_TARGET_TEMPLATE_MAX_SIZE_NAME) {
                throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                        "Cookie Target Template name", Constants.COOKIE_TARGET_TEMPLATE_MAX_SIZE_NAME));
            }
            if (cookieTargetTemplateDao.exists(cookieDomainId, cookie.getCookieName(), session)) {
                throw new ConflictException("CookieTargetTamplate name"
                        + " already exists.");
            }
            //call dao
            result = cookieTargetTemplateDao.save(cookie, session);
            cookieTargetTemplateDao.commit(session);
        } catch (Exception e) {
            cookieTargetTemplateDao.rollback(session);
            throw e;
        } finally {
            cookieTargetTemplateDao.close(session);
        }
        log.info("Cookie Domain Id = {} was saved for key = {}", cookieDomainId, key.toString());
        return result;
    }
}

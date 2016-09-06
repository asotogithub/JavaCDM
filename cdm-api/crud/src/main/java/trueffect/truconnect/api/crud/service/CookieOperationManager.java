package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.CookieOperation;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.crud.dao.CookieOperationDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Abel Soto
 * @edited Richard Jaldin
 */
public class CookieOperationManager {

    private CookieOperationDao dao;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CookieOperationManager() {
        dao = new CookieOperationDao();
    }

    /**
     * Get all cookie operations by its cookie domain id
     *
     * @param cookieDomainId the ID of the CookieDomain
     * @return List of Cookie Operations.
     */
    public List<CookieOperation> getCookieOperationsByCookieDomain(Long cookieDomainId) throws Exception {
        List<CookieOperation> list = new ArrayList<CookieOperation>(
                this.dao.getByCookieDomain(cookieDomainId));
        return list;
    }

    /**
     * Get all cookie operations by its agency id
     *
     * @param agencyId the ID of the Agency
     * @return List of Cookie Operations.
     */
    public List<CookieOperation> getCookieOperationsByAgency(Long agencyId) throws Exception {
        List<CookieOperation> list = new ArrayList<CookieOperation>(
                this.dao.getByAgency(agencyId));
        return list;
    }

    /**
     * Get all cookie operations by its campaign id
     *
     * @param campaignId the ID of the Campaign
     * @return List of Cookie Operations.
     */
    public List<CookieOperation> getCookieOperationsByCampaign(Long campaignId) throws Exception {
        List<CookieOperation> list = new ArrayList<CookieOperation>(
                this.dao.getCookieOperationtByCampaign(campaignId));
        return list;
    }

    /**
     * Saves a CookieOperation
     *
     * @param name Name of a cookie to place
     * @param cookieDomainId Pointer to domain this cookie is served under
     * @param expirationDays Number of days before cookie expires
     * @param cookieOverwriteBehave The behavior for placing the cookie
     * @param key Session ID of the user who creates the CookieOperation
     * @return the new CookieOperation
     */
    public CookieOperation saveCookieOperation(String name, Long cookieDomainId,
            Long expirationDays, Long cookieOverwriteBehave, OauthKey key) throws Exception {
        if (StringUtils.isBlank(name)) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.empty",
                    "Cookie name"));
        } else if (name.length() > 20) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Cookie name", "20"));
        }

        CookieOperation cookie = dao.findByName(name);
        if (cookie != null) {
            throw new ValidationException("Cookie Operation name already exists.");
        }
        log.info(key.toString() + " Saved " + name);
        return this.dao.save(name, cookieDomainId,
                expirationDays, cookieOverwriteBehave, key);
    }

    /**
     * Updates a CookieOperation
     *
     * @param id The ID of the CookieOperation
     * @param name Name of a cookie to place
     * @param cookieDomainId Pointer to domain this cookie is served under
     * @param expirationDays Number of days before cookie expires
     * @param cookieOverwriteBehave The behavior for placing the cookie
     * @param key Session ID of the user who updates the CookieOperation
     * @return the new CookieOperation
     */
    public CookieOperation updateCookieOperation(Long id, String name,
            Long cookieDomainId, Long expirationDays, Long cookieOverwriteBehave,
            OauthKey key) throws Exception {
        if (name != null && name.length() > 20) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Cookie name", "20"));
        }

        CookieOperation cookie = dao.get(id);
        if (cookie == null) {
            throw new ValidationException("Can't update record if it doesn't exist");
        }
        if (StringUtils.isEmpty(name)) {
            name = cookie.getCookieName();
        }

        if (!StringUtils.equals(cookie.getCookieName(), name)) {
            cookie = dao.findByName(name);
            if (cookie != null) {
                throw new ConflictException("Cookie Operation name already exists.");
            }
        }
        log.info(key.toString() + " Updated "+ id);
        return this.dao.update(id, name, cookieDomainId,
                expirationDays, cookieOverwriteBehave, key);
    }

    /**
     * Removes an CookieOperation based on the identifier
     *
     * @param id ID of the CookieOperation to be removed
     * @param key Session ID of the user who deletes the CookieOperation
     * @return CookieOperation that has been deleted
     */
    public void removeCookieDomain(Long id, OauthKey key) throws Exception {
        log.info(key.toString()+" Deleted "+ id);
        this.dao.delete(id, key);
    }
}

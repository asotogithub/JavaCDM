package trueffect.truconnect.api.crud.service;

import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.CookieOperationValue;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.crud.dao.CookieOperationValueDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

/**
 *
 * @author Gustavo Claure
 */
public class CookieOperationValueManager {

    private CookieOperationValueDao cookieOperationValueDao;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CookieOperationValueManager() {
        this.cookieOperationValueDao = new CookieOperationValueDao();
    }

    /**
     * Returns the Cookie Operation Value based on the id
     *
     * @param cookieOperationValueId ID of the Cookie Operation Value to return
     * @return the Cookie Operation Value of the id
     */
    public CookieOperationValue getCookieOperationValue(Long cookieOperationValueId) throws Exception {
        if (cookieOperationValueDao.get(cookieOperationValueId) != null) {
            return this.cookieOperationValueDao.get(cookieOperationValueId);
        } else {
            throw new NotFoundException("Cookie Operation Value not found");
        }
    }

    /**
     * Gets a list Cookie Operation Values by Object
     *
     * @param cookieOperationId Agency that owns this size record.
     * @return the list of all Ad Sizes by Object.
     */
    public ArrayList<CookieOperationValue> getCookieOperationValuesByOperation(Long cookieOperationId) throws Exception {
        return new ArrayList<CookieOperationValue>(this.cookieOperationValueDao
                .getCookieOperationValuesByOperation(cookieOperationId));
    }

    /**
     * Gets a list Cookie Operation Values by Object
     *
     * @param campaignId Agency that owns this size record.
     * @return the list of all CookieOperationValRef by Campaign.
     */
    public ArrayList<CookieOperationValue> getCookieOperationValuesByCampaign(Long campaignId) throws Exception {
        return new ArrayList<CookieOperationValue>(this.cookieOperationValueDao
                .getCookieOperationValuesByCampaign(campaignId));
    }

    /**
     * Gets a list Cookie Operation Values by Object and by Campaign
     *
     * @param cookieOperationValueId Id of the Cookie Operation Value
     * @param campaignId Id of the Agency.
     * @return the list of all CookieOperationValRef by Cookie Operation Value
     * and Campaign.
     */
    public ArrayList<CookieOperationValue> getCookieOperationValuesByCOVCampaign(Long cookieOperationValueId,
            Long campaignId) throws Exception {
        return new ArrayList<CookieOperationValue>(this.cookieOperationValueDao
                .getCookieOperationValuesByOperationCampaign(
                        cookieOperationValueId, campaignId));
    }

    /**
     * Saves a CookieOperationValue
     *
     * @param cookieOperationId Id of a cookie operation
     * @param cookieValue Value of a Cookie
     * @param key Session ID of the user who creates the
     * CookieOperationValue
     * @return the new CookieOperationValue
     */
    public CookieOperationValue saveCookieOperationValue(Long cookieOperationId,
            String cookieValue, OauthKey key) throws Exception {
        if (!StringUtils.isBlank(cookieValue) && cookieValue.length() > 256) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Cookie Value", "256"));
        }
        log.info(key.toString()+ " Saved "+ cookieOperationId);
        return this.cookieOperationValueDao.save(cookieOperationId,
                cookieValue, key);
    }

    /**
     * Removes an CookieOperationValue based on the identifier
     *
     * @param id ID of the CookieOperationValue to be removed
     * @param key Session ID of the user who deletes the
     * CookieOperationValue
     * @return CookieOperationValue that has been deleted
     */
    public void removeCookieOperationValue(Long id,
            OauthKey key) throws Exception {
        log.info(key.toString()+ " Deleted " + id);
        this.cookieOperationValueDao.delete(id, key);
    }
}

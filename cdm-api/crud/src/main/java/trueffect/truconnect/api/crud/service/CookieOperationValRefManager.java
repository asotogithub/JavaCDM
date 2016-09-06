package trueffect.truconnect.api.crud.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.commons.model.CookieOperationValRef;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.crud.dao.CookieOperationValRefDao;

/**
 *
 * @author Gustavo Claure
 */
public class CookieOperationValRefManager {

    private CookieOperationValRefDao cookieOperationValRefDao;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CookieOperationValRefManager() {
        this.cookieOperationValRefDao = new CookieOperationValRefDao();
    }

    /**
     * Gets a list Cookie Operation Values by Object
     *
     * @param CookieOperationValueId Agency that owns this size record.
     * @return the list of all CookieOperationValRef by Object.
     */
    public List<CookieOperationValRef> getCookieOperationValRefByOperationValue(Long cookieOperationValueId) throws Exception {
        return new ArrayList<CookieOperationValRef>(
                this.cookieOperationValRefDao
                .getCookieOperationValRefsByOperationValue(cookieOperationValueId));
    }

    /**
     * Gets a list Cookie Operation Values by Object
     *
     * @param campaignId Agency that owns this size record.
     * @return the list of all CookieOperationValRef by Campaign.
     */
    public ArrayList<CookieOperationValRef> getCookieOperationValRefByCampaign(Long campaignId) throws Exception {
        return new ArrayList<CookieOperationValRef>(this.cookieOperationValRefDao.getCookieOperationValRefsByCampaign(campaignId));
    }

    /**
     * Gets a list Cookie Operation Values by Object and by Campaign
     *
     * @param cookieOperationValueId Id of the Cookie Operation Value
     * @param campaignId Id of the Agency.
     * @return the list of all CookieOperationValRef by Cookie Operation Value
     * and Campaign.
     */
    public ArrayList<CookieOperationValRef> getCookieOperationValRefByCOVCampaign(Long cookieOperationValueId, Long campaignId) throws Exception {
        return new ArrayList<CookieOperationValRef>(this.cookieOperationValRefDao
                .getCookieOperationValRefsByOperationCampaign(
                cookieOperationValueId, campaignId));
    }

    /**
     * Saves a CookieOperationValRef
     *
     * @param cookieOperationValueId Id of the Cookie Operation Value
     * @param cookieRefEntityType Type of Entity
     * @param cookieRefEntityId Id of the Entity
     * @param campaignId Id of the Campaign
     * @param createdTpwsKey Id of the session
     * @return the new CookieOperationValue
     */
    public CookieOperationValRef saveCookieOperationValRef(Long cookieOperationValueId,
            Long cookieRefEntityType, Long cookieRefEntityId,
            Long campaignId, OauthKey key) throws Exception {
        log.info(key.toString() + " Saved "+ cookieOperationValueId);
        return this.cookieOperationValRefDao.save(cookieOperationValueId,
                cookieRefEntityType, cookieRefEntityId, campaignId,
                key);
    }

    /**
     * Removes an Cookie Operation Val Ref based on the ID
     *
     * @param cookieOperationValueId creative ID number and primary key
     * @return true if this Creative has been deleted successfully
     */
    public void removeCookieOperationValRef(Long cookieOperationValueId,
            Long cookieRefEntityType, Long cookieRefEntityId,
            Long campaignId, OauthKey key) throws Exception {
        log.info(key.toString()+" Deleted "+ cookieOperationValueId );
        this.cookieOperationValRefDao.remove(cookieOperationValueId,
                cookieRefEntityType, cookieRefEntityId, campaignId,
                key);
    }
}

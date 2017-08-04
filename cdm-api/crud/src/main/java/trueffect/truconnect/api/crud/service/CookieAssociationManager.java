package trueffect.truconnect.api.crud.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.CookieOperationValue;

/**
 *
 * @author Gustavo Claure
 */
public class CookieAssociationManager {

    private CookieOperationValueManager cookieOperationValue;
    private CookieOperationValRefManager cookieOperationValRef;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CookieAssociationManager() {
        this.cookieOperationValue = new CookieOperationValueManager();
        this.cookieOperationValRef = new CookieOperationValRefManager();
    }

    /**
     * Saves a CookieOperationValue and its respective CookieOperationValRef
     *
     * @param cookieOperationId Id of a cookie operation
     * @param cookieValue Value of a Cookie
     * @param cookieRefEntityType Type of Entity
     * @param cookieRefEntityId Id of the Entity
     * @param campaignId Id of the Campaign
     * @param createdTpwsKey Id of the session
     * @return the new CookieOperationValue
     */
    public CookieOperationValue saveCookie(Long cookieOperationId, String cookieValue,
            Long cookieRefEntityType, Long cookieRefEntityId,
            Long campaignId, OauthKey key) throws Exception {

        CookieOperationValue cookie = (CookieOperationValue) cookieOperationValue
                .saveCookieOperationValue(cookieOperationId,
                cookieValue, key);
        cookieOperationValRef.saveCookieOperationValRef(cookie.getCookieOperationValueId(),
                cookieRefEntityType, cookieRefEntityId, campaignId, key);
        log.info(key.toString() + " Saved "+ cookie);
        return cookie;

    }
}

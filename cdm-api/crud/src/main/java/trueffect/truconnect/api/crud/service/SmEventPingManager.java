package trueffect.truconnect.api.crud.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.SmEventPing;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.crud.dao.SmEventPingDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

/**
 *
 * @author Rambert Rioja
 */
public class SmEventPingManager {

    private SmEventPingDao eventPingDao;
    private Logger log = LoggerFactory.getLogger(this.getClass());


    public SmEventPingManager() {
        this.eventPingDao = new SmEventPingDao();
    }

    /**
     * Returns the SmEventPing based on the ID
     *
     * @param id ID of the SmEventPing to return
     * @return the SmEventPing of the id
     */
    public SmEventPing get(Long id) throws Exception {
        return eventPingDao.get(id);
    }

    /**
     * Saves a SmEventPing
     *
     * @param smEventId SmEvent ID number and primary key
     * @param siteId Site ID number and primary key
     * @param pingContent the Content of the ping
     * @param description Ping description
     * @param pingType the Type of the ping
     * @param tagType Tag type (IMG, IFRAME, SCRIPT)
     *
     * @return SmEventPing saved
     */
    public SmEventPing save(Long smEventId, Long siteId, String pingContent,
            String description, Long pingType, Long tagType, OauthKey key) throws Exception {
        if (!StringUtils.isBlank(pingContent) && pingContent.length() > 4000) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Site Measurement Event Ping content", "4000"));
        }
        if (!StringUtils.isBlank(description) && description.length() > 1024) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Site Measurement Event Ping description", "1024"));
        }
        if (StringUtils.contains(pingContent, " ")) {
            throw new ValidationException("Event Ping name cannot "
                    + "contain spaces: " + pingContent);
        }
        SmEventPing ping = eventPingDao.save(smEventId, siteId,
                pingContent, description, pingType, tagType, key);
        log.info(key.toString()+ " Saved "+ smEventId);
        return ping;
    }

    /**
     * Updates a SmEventPing
     *
     * @param id SmEventPing ID number and primary key
     * @param pingContent the Content of the ping
     * @param description Ping description
     * @param pingType the Type of the ping
     * @param tagType Tag type (IMG, IFRAME, SCRIPT)
     *
     * @return SmEventPing updated
     */
    public SmEventPing update(Long id, String pingContent,
            String description, Long pingType, Long tagType, OauthKey key) throws Exception {
        if (!StringUtils.isBlank(pingContent) && pingContent.length() > 4000) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Site Measurement Event Ping", "4000"));
        }
        if (!StringUtils.isBlank(description) && description.length() > 1024) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Site Measurement Event Ping description", "1024"));
        }
        if (StringUtils.contains(pingContent, " ")) {
            throw new ValidationException("Event Ping name cannot "
                    + "contain spaces: " + pingContent);
        }
        SmEventPing ping = eventPingDao.update(id,
                pingContent, description, pingType, tagType, key);
        log.info(key.toString()+ " Updated "+ id);
        return ping;
    }

    /**
     * Removes logically a SmEventPing
     *
     * @param id SmEventPing ID number and primary key
     *
     * @return SmEventPing removed
     */
    public void remove(Long id, OauthKey key) throws Exception {
        log.info(key.toString()+ " Deleted "+ id);
        eventPingDao.remove(id, key);
    }
}

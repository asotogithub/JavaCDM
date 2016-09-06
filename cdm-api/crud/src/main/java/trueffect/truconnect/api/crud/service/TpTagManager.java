package trueffect.truconnect.api.crud.service;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.commons.model.TpTag;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.crud.dao.TpTagDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

/**
 *
 * @author Abel Soto
 */
public class TpTagManager {

    private TpTagDao dao;
    private Logger log = LoggerFactory.getLogger(this.getClass());


    public TpTagManager() {
        dao = new TpTagDao();
    }

    /**
     * Returns all TpTags
     *
     * @return all TpTags
     */
    public List<TpTag> getTpTags() throws Exception {
        return new ArrayList<TpTag>(this.dao.getTpTagAll());
    }

    /**
     * Returns the TpTag based on the ID
     *
     * @param id TpTag ID number and primary key
     * @return the TpTag of the id
     */
    public TpTag getTpTag(Long id) throws Exception {
        return dao.get(id);
    }

    /**
     * Saves a TpTag
     *
     * @param tpVendorId TgVendor ID number and primary key
     * @param name the Name of the tag
     * @param description Tag description
     * @param matchExpression Ping description
     * @param heightExpression Height expression
     * @param widthExpression Width expression
     *
     * @return TpTag saved
     */
    public TpTag saveTpTag(Long tpVendorId, String name, String description,
            String matchExpression, String heightExpression, String widthExpression) throws Exception {
        if (StringUtils.isBlank(name)) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.empty",
                    "Tag name"));
        } else if (name.length() > 255) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Tag name", "255"));
        }
        if (!StringUtils.isBlank(description) && description.length() > 4000) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Tag description", "4000"));
        }
        log.info("Saved "+ tpVendorId);
        return this.dao.save(tpVendorId, name, description,
                matchExpression, heightExpression, widthExpression);
    }

    /**
     * Updates a TpTag
     *
     * @param id TgTag ID number and primary key
     * @param tpVendorId TgVendor ID number and primary key
     * @param name the Name of the tag
     * @param description Tag description
     * @param matchExpression Ping description
     * @param heightExpression Height expression
     * @param widthExpression Width expression
     *
     * @return TpTag updated
     */
    public TpTag updateTpTag(Long id, Long tpVendorId, String name,
            String description, String matchExpression, String heightExpression,
            String widthExpression) throws Exception {

        //******Validation Input Parameters  ****************
        if (StringUtils.isBlank(name)) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.empty",
                    "Tag name"));
        } else if (name.length() > 255) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Tag name", "255"));
        }
        if (!StringUtils.isBlank(description) && description.length() > 4000) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Tag description", "4000"));
        }

        TpTag existentTpTag = dao.get(id);
        if (existentTpTag == null) {
            throw new ValidationException("Can't update record if it doesn't exist");
        }
        //******End  Validation Input Parameters  ****************
        //*******  Begin Business Logic  ************
        TpTag bean = this.dao.update(id, tpVendorId, name, description,
                matchExpression, heightExpression, widthExpression);
        log.info("Updated "+ id);
        return bean;
        //*******  End Business Logic  ************
    }

    /**
     * Removes a TpTag
     *
     * @param id TgTag ID number and primary key
     *
     * @return TpTag removed
     */
    public void removeTpTag(Long id) throws Exception {
        log.info("Deleted "+ id);
        this.dao.delete(id);
    }
}

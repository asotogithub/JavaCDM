package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 *
 * @author Abel Soto
 */
public class ClickthroughManager extends AbstractGenericManager{

    private CreativeDao dao;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ClickthroughManager(CreativeDao creativeDao, AccessControl accessControl) {
        super(accessControl);
        this.dao = creativeDao;
    }

    /**
     * Saves a Clickthrough
     *
     * @param creative Creative Object to create
     * @return the new Creative
     * @throws java.lang.Exception
     */
    public Creative saveCreativeClickthrough(Creative creative, OauthKey key) throws Exception {
        //validations
        
        //session
        SqlSession session = dao.openSession();
        Creative result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(creative.getId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeId", Long.toString(creative.getId()), key.getUserId()));
            }
            //call dao
            result = this.dao.saveCreativeClickThrough(creative, key, session);
            
            dao.commit(session);
        } catch (Exception e) {
            dao.rollback(session);
            throw e;
        } finally {
            dao.close(session);
        }
        log.info(key.toString() + " Saved "+ creative);
        return result;
    }

    /**
     * Delete a Clickthrough
     *
     * @param id Creative Id
     * @param key 
     * @throws java.lang.Exception 
     */
    public void removeCreativeClickthrough(Long id, OauthKey key) throws Exception {
        SqlSession session = dao.openSession();
        try {
            //check access control
            if (!userValidFor(AccessStatement.CREATIVE, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "CreativeId", Long.toString(id), key.getUserId()));
            }
            //call dao
            this.dao.removeCreativeClickThrough(id, key, session);
            dao.commit(session);
        } catch (Exception e) {
            dao.rollback(session);
            throw e;
        } finally {
            dao.close(session);
        }
        log.info(key.toString() + " Deleted "+ id);
    }
}

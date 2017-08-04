package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.SiteContactView;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.SiteDao;
import trueffect.truconnect.api.crud.validation.SiteValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Abel Soto
 */
public class SiteManager extends AbstractGenericManager {

    private SiteDao siteDao;
    private ExtendedPropertiesDao extendedPropertiesDao;
    private SiteValidator validator;
    
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public SiteManager(SiteDao siteDao, ExtendedPropertiesDao extendedPropertiesDao, AccessControl accessControl) {
        super(accessControl);
        this.siteDao = siteDao;
        this.extendedPropertiesDao = extendedPropertiesDao;
        validator = new SiteValidator();
    }

    /**
     * Returns the Site based on the id
     *
     * @param id ID of the Site to return
     * @param key
     * @return the Site of the id
     * @throws java.lang.Exception
     */
    public Site getByid(Long id, OauthKey key) throws Exception {
        //Validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        
        //Session
        SqlSession session = siteDao.openSession();
        Site result = null;
        try {
            // Check access control
            if (!userValidFor(AccessStatement.SITE, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteId", Long.toString(id), key.getUserId()));
            }
            result = this.siteDao.get(id, session);
        } finally {
            siteDao.close(session);
        }
        return result;
    }
    
    /**
     * Returns List Site of the Search Criteria
     *
     * @param searchCriteria
     * @param key
     * @return all Sites
     * @throws java.lang.Exception
     */
    public RecordSet<Site> getSites(SearchCriteria searchCriteria, OauthKey key) throws Exception {
        //Validations
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        
        //Session
        SqlSession session = siteDao.openSession();
        RecordSet<Site> result;
        try {
            result = siteDao.getSites(searchCriteria, key, session);
        } finally {
            siteDao.close(session);
        }
        return result;
    }


    /**
     * Receives a Site Object and saves it
     *
     * @param site Site it's Object
     * @param key
     * @return the new Site
     * @throws java.lang.Exception
     */
    public Site create(Site site, OauthKey key) throws Exception {
        //Session
        SqlSession session = siteDao.openSession();
        Site result = null;
        try {
            result = create(site, true, key, session);
            siteDao.commit(session);
        } catch (Exception e) {
            siteDao.rollback(session);
            throw e;
        } finally {
            siteDao.close(session);
        }

        log.info(key.toString()+ " Saved "+ site);
        return result;
    }

    /**
     * Check data and creates a new Site.
     *
     * @param site Site object to be created
     * @param ignoreDupSite
     * @param key
     * @param session
     * @return the new InsertionOrder saved on the data base
     * @throws java.lang.Exception
     */
    public Site create(Site site, Boolean ignoreDupSite, OauthKey key, SqlSession session) throws Exception {
        //validations
        // Nullability checks
        if (site == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.site")));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (ignoreDupSite == null) {
            ignoreDupSite = false;
        }

        // model validation
        String className = site.getClass().getSimpleName();
        BeanPropertyBindingResult pbResult = new BeanPropertyBindingResult(site, className);
        validator.validate(site, pbResult);
        if (pbResult.hasErrors()) {
            throw new ValidationException(pbResult);
        }

        //check access control
        if (!userValidFor(AccessStatement.PUBLISHER, Collections.singletonList(site.getPublisherId()), key.getUserId(), session)) {
            throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "PublisherId", 
                    Long.toString(site.getPublisherId()), key.getUserId()));
        }

        //check size name exist for some publisher
        if (!ignoreDupSite) {
            List<Site> sites = siteDao.checkSiteByName(site.getName(), key.getUserId(), session);
            if (sites.size() > 0) {
                //Look for Publishers names.
                HashSet<String> publisherNames = new HashSet<>();
                for (Site siteAux : sites) {
                    publisherNames.add(siteAux.getPublisherName());
                }
                throw new ValidationException("The site name you are adding exists under different publisher " + publisherNames.toString());
            }
        }
        Long existsId = this.siteDao.exists(site, session);
        if (existsId != null) {
            throw new ConflictException("Site name already exists.");
        }
        // set default values and persist a new Site
        return create(site, key, session);
    }

    /**
     * Creates a new Site with already validated data.
     *
     * @param site Site object to be created
     * @param key
     * @param session
     * @return the new InsertionOrder saved on the data base
     */
    public Site create(Site site, OauthKey key, SqlSession session) {
        //validations
        // Nullability checks
        if (site == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.site")));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }

        // Set Values
        if (site.getPreferredTag() == null || site.getPreferredTag().isEmpty()) {
            site.setPreferredTag("IFRAME");
        }
        Long siteId = siteDao.getNextId(session);
        site.setId(siteId);
        site.setCreatedTpwsKey(key.getTpws());
        //call dao
        this.siteDao.create(site, session);
        // Saving the ExternalSiteId
        extendedPropertiesDao.updateExternalId("Site", "MediaID", site.getId(),
                site.getExternalId(), session);

        //retrieve recently created site
        return siteDao.get(siteId, session);
    }

    /**
     * Receives a Site Object and saves it
     *
     * @param id identifier
     * @param site Site it's Object
     * @param key
     * @return the new Site
     * @throws java.lang.Exception
     */
    public Site update(Long id, Site site, OauthKey key) throws Exception {
        String className = site.getClass().getSimpleName();
        BeanPropertyBindingResult pbResult = new BeanPropertyBindingResult(site, className);
        
        //validations
        // Nullability checks   
        validator.validatePUT(site, id, pbResult);
        if (pbResult.hasErrors()) {
            throw new ValidationException(pbResult);
        }
        
        //Session
        SqlSession session = siteDao.openSession();
        Site result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.SITE, Collections.singletonList(site.getId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteId", Long.toString(site.getId()), key.getUserId()));
            }
            
            Site toUpdate = siteDao.get(site.getId(), session);
            if (toUpdate == null) {
                throw new NotFoundException("Can't update record if it "
                        + "doesn't exist");
            }
            if (!toUpdate.getName().equals(site.getName())) {
                site.setPublisherId(toUpdate.getPublisherId());
                Long existsId = this.siteDao.exists(site, session);
                if (existsId != null) {
                    throw new ConflictException("Site name already exists.");
                }
            }
            //set values
            site.setModifiedTpwsKey(key.getTpws());
            //call dao
            this.siteDao.update(site, session);

            //Updating the ExternalSiteId
            extendedPropertiesDao.updateExternalId("Site", "MediaID", 
                    site.getId(), site.getExternalId(), session);
            //retrieve recently created site
            result = siteDao.get(site.getId(), session);
            siteDao.commit(session);            
        } catch (Exception e) {
            siteDao.rollback(session);
            throw e;
        } finally {
            siteDao.close(session);
        }
        log.debug("The Site = {} was Updated successfully by user = {} " , id , key.getUserId());
        return result;
    }

    /**
     * Removes an Site based on the ID
     *
     * @param id Site ID number and primary key
     * @param key
     * @return Site that has been deleted
     * @throws java.lang.Exception
     */
    public SuccessResponse remove(Long id, OauthKey key) throws Exception {
        //validations
        // Nullability checks   
        if (id == null) {
            throw new NotFoundException(ResourceBundleUtil.getString("global.error.empty", "Site Identifier"));
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        
        //Session
        SqlSession session = siteDao.openSession();
        try {
            //check access control
            if (!userValidFor(AccessStatement.SITE, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteId", Long.toString(id), key.getUserId()));
            }
            //call dao
            this.siteDao.delete(id, key, session);
        } catch (Exception e) {
            siteDao.rollback(session);
            throw e;
        } finally {
            siteDao.close(session);
        }
        SuccessResponse result = new SuccessResponse("Site " + id + " successfully deleted");
        log.debug("The Site = {} was Deleted  by user = {} " , id , key.getUserId());
        return result;
    }
    
    public Either<Error, RecordSet<SiteContactView>> getTraffickingSiteContacts(Long campaignId, OauthKey key){
        // Nullability checks
        if (campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Campaign ID"));
        }
        
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        
        // Get Session
        SqlSession session = siteDao.openSession();
        List<SiteContactView> result;
        try {
            // Data Access Control
            if(!accessControl.isUserValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER",
                        ResourceBundleUtil.getString("global.label.campaign"), campaignId.toString(), key.getUserId()), 
                        SecurityCode.NOT_FOUND_FOR_USER));
            }
            // Business Logic
            result = siteDao.getTraffickingSiteContacts(campaignId, session);
        } finally {
            // Session close
            siteDao.close(session);
        }
        return Either.success(new RecordSet<>(result));
    }
}

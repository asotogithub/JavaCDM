package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.validation.SiteSectionValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.util.Collections;

/**
 *
 * @author Ricahrd Jaldin
 */
public class SiteSectionManager extends AbstractGenericManager {

    protected SiteSectionDao siteSectionDao;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private SiteSectionValidator validator;


    public SiteSectionManager(SiteSectionDao siteSectionDao, AccessControl accessControl) {
        super(accessControl);
        this.siteSectionDao = siteSectionDao;
        this.validator = new SiteSectionValidator();
    }

    /**
     * Returns all SiteSections
     *
     * @return all SiteSections
     */
    public RecordSet<SiteSection> getSiteSections(SearchCriteria searchCriteria, OauthKey key) throws Exception {
        //validations
        if (searchCriteria == null) {
            throw new IllegalArgumentException("Search Criteria cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        if (searchCriteria.getPageSize() > 1000) {
            throw new Exception("The page size allows up to 1000 records.");
        }
        if (searchCriteria.getStartIndex() < 0) {
            throw new Exception("Cannot retrieve records for start index: " + searchCriteria.getStartIndex() + ". The minimum start index is: 0");
        }

        SqlSession session = siteSectionDao.openSession();
        RecordSet<SiteSection> result;
        try {
            result = siteSectionDao.get(searchCriteria, key, session);
        } finally {
            siteSectionDao.close(session);
        }
        return result;
    }

    /**
     * Returns the SiteSection based on the id
     *
     * @param id ID of the Site to return
     * @return the SiteSection of the id
     */
    public SiteSection get(Long id, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        SqlSession session = siteSectionDao.openSession();
        SiteSection result;
        try {
            if (!userValidFor(AccessStatement.SITE_SECTION, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteSectionId", Long.toString(id), key.getUserId()));
            }
            result = siteSectionDao.get(id, session);
            if (result == null) {
                throw new NotFoundException("SiteSection not found. Id: " + id);
            }
        } finally {
            siteSectionDao.close(session);
        }
        return result;
    }

    /**
     * Saves a SiteSection
     *
     * @param siteSection SiteSection Object
     * @param key
     * @return SiteSection object saved
     * @throws java.lang.Exception
     */
    public SiteSection create(SiteSection siteSection, OauthKey key) throws Exception {
        SiteSection result = null;
        SqlSession session = siteSectionDao.openSession();
        try {
            result = create(siteSection, key, session);
            siteSectionDao.commit(session);
        } catch (Exception e) {
            siteSectionDao.rollback(session);
            throw e;
        } finally {
            siteSectionDao.close(session);
        }
        log.debug(key.toString() + " Saved " + siteSection);
        return result;
    }

    /**
     * Check data and creates a new SiteSection.
     *
     * @param siteSection SiteSection object to be created
     * @param session
     * @param key
     * @return the new InsertionOrder saved on the data base
     * @throws java.lang.Exception
     */
    public SiteSection create(SiteSection siteSection, OauthKey key, SqlSession session) throws Exception {
        SiteSection result;//validations
        if (siteSection == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.siteSection")));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        // model validations
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(siteSection,
                siteSection.getClass().getSimpleName());
        ValidationUtils.invokeValidator(validator, siteSection, errors);
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        // check access control
        if (!userValidFor(AccessStatement.SITE, Collections.singletonList(siteSection.getSiteId()), key.getUserId(), session)) {
            throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteId", Long.toString(siteSection.getSiteId()), key.getUserId()));
        }

        Long existingId = siteSectionDao.exists(siteSection, session);
        if (existingId > 0) {
            throw new ConflictException("Site Section name already exists.");
        }
        // set default values and persist Publisher
        result = createSection(siteSection, key, session);
        return result;
    }

    /**
     * Set Default values and creates a new SiteSection with already validated data.
     *
     * @param siteSection SiteSection object to be created
     * @param session
     * @param key
     * @return the new InsertionOrder saved on the data base
     */
    public SiteSection createSection(SiteSection siteSection, OauthKey key, SqlSession session) {
        SiteSection result;//validations
        if (siteSection == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.siteSection")));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        // Set values
        siteSection.setCreatedTpwsKey(key.getTpws());
        // call dao
        result = this.siteSectionDao.create(siteSection, session);
        return result;
    }

    /**
     * Updates a SiteSection
     *
     * @param siteSection SiteSection Object
     * @return SiteSection object updated
     */
    public SiteSection update(Long id, SiteSection siteSection, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (siteSection == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        if (siteSection.getId() == null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.putWithoutId", "Site Section"));
        }
        if (id == null || id.compareTo(siteSection.getId()) != 0) {
            throw new ValidationException("Identifier in URL does not match resource in request body.");
        }
        if (siteSection.getSiteId() == null) {
            throw new ValidationException("Site id is required.");
        }
        if (!StringUtils.isBlank(siteSection.getName()) && siteSection.getName().length() > Constants.MAX_SITE_SECTION_NAME_LENGTH) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength", "Site Section name", String.valueOf(Constants.MAX_SITE_SECTION_NAME_LENGTH)));
        }
        if (!StringUtils.isBlank(siteSection.getAgencyNotes()) && siteSection.getAgencyNotes().length() > Constants.MAX_SITE_SECTION_AGENCY_NOTES_LENGTH) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength", "Agency notes", String.valueOf(Constants.MAX_SITE_SECTION_AGENCY_NOTES_LENGTH)));
        }
        if (!StringUtils.isBlank(siteSection.getPublisherNotes()) && siteSection.getPublisherNotes().length() > Constants.MAX_SITE_SECTION_PUBLISHER_NOTES_LENGHT) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength", "Publisher notes", String.valueOf(Constants.MAX_SITE_SECTION_PUBLISHER_NOTES_LENGHT)));
        }

        SqlSession session = siteSectionDao.openSession();
        SiteSection result;
        try {
            if (!userValidFor(AccessStatement.SITE_SECTION, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteSectionId", Long.toString(id), key.getUserId()));
            }
            SiteSection existentSiteSection = siteSectionDao.get(id, session);
            if (existentSiteSection == null) {
                throw new NotFoundException("Can't update record if it doesn't exist");
            }

            if (!StringUtils.equals(existentSiteSection.getName(), siteSection.getName())) {
                Long existingId = siteSectionDao.exists(siteSection, session);
                if (existingId > 0) {
                    throw new ConflictException("Site Section name already exists.");
                }
            }
            siteSection.setModifiedTpwsKey(key.getTpws());
            result = siteSectionDao.update(siteSection, session);
            siteSectionDao.commit(session);
        } catch (Exception e) {
            siteSectionDao.rollback(session);
            throw e;
        } finally {
            siteSectionDao.close(session);
        }
        log.debug(key.toString() + " Updated " + id);
        return result;
    }

    /**
     * Removes a SiteSection based on the ID
     *
     * @param id Site_Section ID number and primary key
     * @return SiteSection that has been deleted
     */
    public SuccessResponse remove(Long id, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        if (id == null) {
            throw new NotFoundException(ResourceBundleUtil.getString("global.error.empty", "SiteSection Id"));
        }

        SqlSession session = siteSectionDao.openSession();
        try {
            if (!userValidFor(AccessStatement.SITE_SECTION, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteSectionId", Long.toString(id), key.getUserId()));
            }
            siteSectionDao.remove(id, key, session);
            siteSectionDao.commit(session);
        } catch (PersistenceException e) {
            siteSectionDao.rollback(session);
            throw e;
        } finally {
            siteSectionDao.close(session);
        }
        log.debug(key.toString() + " Deleted " + id);
        return new SuccessResponse("Site Section " + id + " successfully deleted");
    }
}

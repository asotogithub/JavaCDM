package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.validation.PublisherValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.ValidationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Collections;

/**
 *
 * @author Rambert Rioja, Richard Jaldin
 */
public class PublisherManager extends AbstractGenericManager {

    private final PublisherDao publisherDao;
    private PublisherValidator validator;
    private Logger log = LoggerFactory.getLogger(this.getClass());


    public PublisherManager(PublisherDao publisherDao,
                            AccessControl accessControl) {
        super(accessControl);
        this.publisherDao = publisherDao;
        validator = new PublisherValidator();
    }

    /**
     * Returns a List of Publishers
     *
     * @param searchCriteria
     * @param key
     * @return a List of Publishers
     * @throws java.lang.Exception
     */
    public RecordSet<Publisher> getPublishers(SearchCriteria searchCriteria, OauthKey key) throws Exception {
        //Validations
        if (searchCriteria == null) {
            throw new IllegalArgumentException("Search Criteria cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        //Session
        SqlSession session = publisherDao.openSession();
        RecordSet<Publisher> result;
        try {
            result = publisherDao.get(searchCriteria, key, session);
        } finally {
            publisherDao.close(session);
        }
        return result;
    }

    /**
     * Returns the Publisher based on the ID
     *
     * @param id ID of the Publisher to return
     * @param key
     * @return the Publisher of the id
     * @throws java.lang.Exception
     */
    public Publisher get(Long id, OauthKey key) throws Exception {
        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }


        Publisher result = null;
        SqlSession session = publisherDao.openSession();
        try {
            if (!userValidFor(AccessStatement.PUBLISHER, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "PublisherId", Long.toString(id), key.getUserId()));
            }
            result = publisherDao.get(id, session);
        } finally {
            publisherDao.close(session);
        }
        return result;
    }

    /**
     * Create a Publisher
     *
     * @param publisher the publisher is being created
     * @param key
     * @return the new Publisher
     * @throws java.lang.Exception
     */
    public Publisher create(Publisher publisher, OauthKey key) throws Exception {
        //Session
        SqlSession session = publisherDao.openSession();
        Publisher result = null;
        try {
            result = create(publisher, key, session);
            publisherDao.commit(session);
        } catch (Exception e) {
            publisherDao.rollback(session);
            throw e;
        } finally {
            publisherDao.close(session);
        }
        return result;
    }

    /**
     * Check data and creates a new Publisher.
     *
     * @param publisher Publisher object to be created
     * @param key
     * @param session
     * @return the new Publisher created on the data base
     * @throws java.lang.Exception
     */
    public Publisher create(Publisher publisher, OauthKey key, SqlSession session) throws Exception {
        //validations
        // Nullability checks
        if (publisher == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.publisher")));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        // Model validations
        String className = publisher.getClass().getSimpleName();
        BeanPropertyBindingResult pbResult = new BeanPropertyBindingResult(publisher, className);
        ValidationUtils.invokeValidator(validator, publisher, pbResult);
        if (pbResult.hasErrors()) {
            throw new ValidationException(pbResult);
        }

        // check access control
        if (!userValidFor(AccessStatement.AGENCY, publisher.getAgencyId(), key.getUserId(), session)) {
            throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", 
                    "AgencyId", Long.toString(publisher.getAgencyId()), key.getUserId()));
        }
        Long existingId = publisherDao.exists(publisher, session);
        if (existingId != null) {
            throw new ConflictException("Publisher name already exists.");
        }

        // set default values and persist Publisher
        Publisher result = createPublisher(publisher, key, session);
        log.info(key.toString()+ " Saved "+ publisher);
        return result;
    }

    /**
     * Set Default values and creates a new Publisher with already validated data.
     *
     * @param publisher Publisher object to be created
     * @param key
     * @param session
     * @return the new Publisher created on the data base
     */
    public Publisher createPublisher(Publisher publisher, OauthKey key, SqlSession session) {
        //validations
        // Nullability checks
        if (publisher == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.publisher")));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        // Set values
        if (publisher.getZipCode() == null || publisher.getZipCode().isEmpty()) {
            publisher.setZipCode("00000");
        }
        publisher.setCreatedTpwsKey(key.getTpws());

        // call dao
        Publisher result = this.publisherDao.create(publisher, session);
        return result;
    }

    /**
     * Updates a Publisher
     *
     * @param id
     * @param publisher is the Publisher Object is going to be updated
     * @param key
     * @return the updated Publisher
     * @throws java.lang.Exception
     */
    public Publisher update(Long id, Publisher publisher, OauthKey key) throws Exception {
        //validations
        // Nullability checks
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (publisher == null) {
            throw new ValidationException("Publisher cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        String className = publisher.getClass().getSimpleName();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(publisher, className);
        validator.validate(publisher, id, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        //Session
        SqlSession session = publisherDao.openSession();
        Publisher result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.PUBLISHER, Collections.singletonList(publisher.getId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "PublisherId", Long.toString(publisher.getId()), key.getUserId()));
            }

            Publisher existentPublisher = publisherDao.get(publisher.getId(), session);
            if (existentPublisher == null) {
                throw new NotFoundException("Can't update record if it doesn't exist");
            }
            if (!StringUtils.equals(existentPublisher.getName(), publisher.getName())) {
                publisher.setAgencyId(existentPublisher.getAgencyId());
                Long existingId = publisherDao.exists(publisher, session);
                if (existingId != null) {
                    throw new ConflictException("Publisher name already exists.");
                }
            }
            //set values
            publisher.setModifiedTpwsKey(key.getTpws());
            //call dao
            this.publisherDao.update(publisher, session);
            result = publisherDao.get(publisher.getId(), session);
            publisherDao.commit(session);
        } catch (Exception e) {
            publisherDao.rollback(session);
            throw e;
        } finally {
            publisherDao.close(session);
        }
        log.info(key.toString()+ " Updated "+ id);
        return result;
    }

    /**
     * Removes a Publisher
     *
     * @param id Publisher ID number and primary key
     * @param agencyId Agency ID number and primary key
     * @param key
     * @return Publisher that has been deleted
     * @throws java.lang.Exception
     */
    public SuccessResponse remove(Long id, Long agencyId, OauthKey key) throws Exception {
        //validations
        // Nullability checks
        if (id == null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.empty", "Publisher Id"));
        }
        if (agencyId == null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.empty", "Agency Id"));
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        //Session
        SqlSession session = publisherDao.openSession();
        try {
            //check access control
            if (!userValidFor(AccessStatement.SITE, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "SiteId", Long.toString(id), key.getUserId()));
            }
            if (!userValidFor(AccessStatement.AGENCY, agencyId, key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AgencyId", Long.toString(agencyId), key.getUserId()));
            }

            //call dao
            this.publisherDao.remove(id, agencyId, key, session);
            publisherDao.commit(session);
        } catch (Exception e) {
            publisherDao.rollback(session);
            throw e;
        } finally {
            publisherDao.close(session);
        }
        SuccessResponse result = new SuccessResponse("Publisher " + id + " successfully deleted");
        log.info(key.toString()+ " Deleted " + id);
        return result;
    }
}

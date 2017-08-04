package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.validation.SizeValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Collections;

/**
 *
 * @author Gustavo Claure
 */
public class SizeManager extends AbstractGenericManager {

    protected SizeDao sizeDao;
    private SizeValidator validator;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public SizeManager(SizeDao sizeDao, AccessControl accessControl) {
        super(accessControl);
        this.sizeDao = sizeDao;
        this.validator = new SizeValidator();
    }

    /**
     * Gets a RecordSet of Sizes
     *
     * @param searchCriteria search criteria
     * @param key
     * @return a RecordSet of Sizes
     * @throws java.lang.Exception
     */
    public RecordSet<Size> get(SearchCriteria searchCriteria, OauthKey key) throws SearchApiException {
        //validations
        if (searchCriteria == null) {
            throw new IllegalArgumentException("Search Criteria cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        if (searchCriteria.getPageSize() > 1000) {
            throw new SearchApiException("The page size allows up to 1000 records.");
        }
        if (searchCriteria.getStartIndex() < 0) {
            throw new SearchApiException("Cannot retrieve records for start index: " + searchCriteria.getStartIndex() + ". The minimum start index is: 0");
        }

        SqlSession session = sizeDao.openSession();
        RecordSet<Size> result;
        try {
            result = sizeDao.get(searchCriteria, key, session);
        } finally {
            sizeDao.close(session);
        }
        return result;
    }

    /**
     * Returns the Size based on the id
     *
     * @param id ID of the Size to return
     * @param key
     * @return the Size of the id
     * @throws java.lang.Exception
     */
    public Size get(Long id, OauthKey key) throws DataNotFoundForUserException, NotFoundException{
        //validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        SqlSession session = sizeDao.openSession();
        Size result;
        try {
            if (!userValidFor(AccessStatement.AD_SIZE, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", 
                        "SizeId", Long.toString(id), key.getUserId()));
            }
            result = sizeDao.getById(id, session);
        } finally {
            sizeDao.close(session);
        }
        if (result == null) {
            throw new NotFoundException("Size not found. Id: " + id);
        }
        return result;
    }

    /**
     * Returns the Size based on its dimensions
     *
     * @param height the height of the size
     * @param width the width of the size
     * @param key
     * @return the Size of the dimensions
     * @throws java.lang.Exception
     */
    public Size get(Long height, Long width, OauthKey key) throws Exception {
        //validations
        if (height == null) {
            throw new IllegalArgumentException("Height cannot be null");
        }

        if (width == null) {
            throw new IllegalArgumentException("Width cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        SqlSession session = sizeDao.openSession();
        Size result;
        try {
            result = this.sizeDao.getByUserAndDimensions(height, width, key, session);
        } finally {
            sizeDao.close(session);
        }
        return result;
    }

    /**
     * Saves Size
     *
     * @param size Size to save
     * @param key
     * @return Size saved
     * @throws java.lang.Exception
     */
    public Size create(Size size, OauthKey key) throws Exception {
        Size result = null;
        SqlSession session = sizeDao.openSession();
        try {
            result = create(size, key, session);
            sizeDao.commit(session);
        } catch (Exception e) {
            sizeDao.rollback(session);
            throw e;
        } finally {
            sizeDao.close(session);
        }
        log.debug(key.toString()+ " Saved "+size);
        return result;
    }

    /*
     * Check data and creates a new Size.
     *
     * @param size Size object to be created
     * @param key
     * @param session
     * @return
     * @throws ValidationException
     * @throws DataNotFoundForUserException
     * @throws ConflictException
     */
    public Size create(Size size, OauthKey key, SqlSession session)
            throws ValidationException, DataNotFoundForUserException, ConflictException {
        // Nullability checks
        if (size == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.size")));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        Size result;

        String className = size.getClass().getSimpleName();
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(size, className);
        validator.validate(size, validationResult);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        if (!userValidFor(AccessStatement.AGENCY, size.getAgencyId(), key.getUserId(), session)) {
            throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", 
                    "AgencyId", Long.toString(size.getAgencyId()), key.getUserId()));
        }
        Size existingSize = sizeDao.getByUserAndDimensions(size.getHeight(), size.getWidth(), key,
                session);
        if (existingSize != null) {
            throw new ConflictException("Size " + size.getLabel() + " already exists.");
        }

        size.setCreatedTpwsKey(key.getTpws());
        result = sizeDao.create(size, session);
        return result;
    }

    /**
     * Set Default values and creates a new Size with already validated data.
     *
     * @param size Size object to be created
     * @param key
     * @param session
     * @return the new InsertionOrder saved on the data base
     */
    public Size createSize(Size size, OauthKey key, SqlSession session) {
        // Nullability checks
        if (size == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.size")));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        Size result;
        size.setCreatedTpwsKey(key.getTpws());
        result = sizeDao.create(size, session);
        return result;
    }
}

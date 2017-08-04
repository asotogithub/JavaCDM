package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.BrandDao;
import trueffect.truconnect.api.crud.validation.BrandValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Collections;

/**
 *
 * @author marleny.patsi
 */
public class BrandManager extends AbstractGenericManager{

    private BrandDao brandDao;
    private BrandValidator validator;

    public BrandManager(BrandDao dao, AccessControl accessControl) {
        super(accessControl);
        this.brandDao = dao;
        validator = new BrandValidator();
    }

    /**
     * Gets a Brand based on its ID
     *
     * @param id the ID of the Brand.
     * @param key
     * @return the Brand
     * @throws java.lang.Exception
     */
    public Brand get(Long id, OauthKey key) throws Exception {
        Brand result = null;
        SqlSession session = brandDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "BrandId", Long.toString(id), key.getUserId()));
            }
            //call dao
            result = brandDao.get(id, session);
        } catch (Exception e) {
            brandDao.rollback(session);
            throw e;
        } finally {
            brandDao.close(session);
        }

        return result;
    }

    /**
     * Gets a RecordSet of Brands
     *
     * @param criteria
     * @param key
     * @return a RecordSet of Brands
     * @throws java.lang.Exception
     */
    public RecordSet<Brand> getBrands(SearchCriteria criteria, OauthKey key) throws Exception {
        RecordSet<Brand> result = null;
        SqlSession session = brandDao.openSession();
        try {
            if (criteria.getPageSize() > 1000) {
                throw new Exception("The page size allows up to 1000 records.");
            }
            if (criteria.getStartIndex() < 0) {
                throw new Exception("Cannot retrieve records for start index: " + criteria.getStartIndex() + ". The minimum start index is: 0");
            }

            //call dao
            result = brandDao.getBrands(criteria, key.getUserId(), session);
        } catch (Exception e) {
            brandDao.rollback(session);
            throw e;
        } finally {
            brandDao.close(session);
        }
        return result;
    }

    /**
     * Gets a set of Brands by AdvertiserId
     *
     * @param advertiserId
     * @param key
     * @return a RecordSet of Advertisers
     */
    public RecordSet<Brand> getBrandsByAdvertiserId(Long advertiserId, OauthKey key) {

        //validations
        //nullability checks
        if (advertiserId == null) {
            throw new IllegalArgumentException("Advertiser's id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OAuthKey cannot be null");
        }

        RecordSet<Brand> result;
        SqlSession session = brandDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.ADVERTISERS, Collections.singletonList(advertiserId), key.getUserId(), session)) {
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.ILLEGAL_USER_CONTEXT);
            }
            //call Dao
            result = brandDao.getBrandsByAdvertiserId(advertiserId, key.getUserId(), session);
        } finally {
            brandDao.close(session);
        }
        return result;
    }

    /**
     * Create a new Brand after complete validations
     *
     * @param brand  Brand to create
     * @param key
     * @return the Brand created
     * @throws java.lang.Exception
     */
    public Brand create(Brand brand, OauthKey key) throws Exception {
        String className = brand.getClass().getSimpleName();
        BeanPropertyBindingResult pbResult = new BeanPropertyBindingResult(brand, className);
        //validations
        // Nullability checks
        validator.validate(brand, pbResult);
        if (pbResult.hasErrors()) {
            throw new ValidationException(pbResult);
        }

        Brand result = null;
        SqlSession session = brandDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.ADVERTISER, brand.getAdvertiserId(), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AdvertiserId", Long.toString(brand.getAdvertiserId()), key.getUserId()));
            }

            brandDao.brandExistsForSave(brand.getName(), brand.getAdvertiserId(), session);
            //PK of Brand
            Long id = brandDao.getNextId(session);
            brand.setId(id);
            brand.setCreatedTpwsKey(key.getTpws());
            //call dao
            brandDao.create(brand, session);
            brandDao.commit(session);
            // Retrieve recently created Brand
            result = brandDao.get(id, session);
            log.info(key.toString()+" Saved "+result);
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            brandDao.rollback(session);
            throw e;
        } finally {
            brandDao.close(session);
        }

        return result;
    }

    /**
     * Updates data of a Brand after complete validations
     *
     * @param id
     * @param brand Brand to updated
     * @param key
     * @return the Brand updated
     * @throws java.lang.Exception
     */
    public Brand update(Long id, Brand brand, OauthKey key) throws Exception {
        String className = brand.getClass().getSimpleName();
        BeanPropertyBindingResult bpResult = new BeanPropertyBindingResult(brand, className);

        //validations
        // Nullability checks
        if (brand.getId() == null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.putWithoutId", "Brand"));
        }

        Brand result = null;
        SqlSession session = brandDao.openSession();
        try {
            Brand existentBrand = brandDao.get(brand.getId(), session);
            if (existentBrand == null) {
                throw new NotFoundException("Can't update record if it doesn't exist");
            }

            //set values
            if (StringUtils.isBlank(brand.getIsHidden())) {
                brand.setIsHidden(existentBrand.getIsHidden());
            }

            validator.validate(brand, id, bpResult);
            if (bpResult.hasErrors()) {
                throw new ValidationException(bpResult);
            }

            // Check access control
            if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(brand.getId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "BrandId", Long.toString(brand.getId()), key.getUserId()));
            }
            if (StringUtils.isEmpty(brand.getName())) {
                brand.setName(existentBrand.getName());
            }

            if (!StringUtils.equals(existentBrand.getName(), brand.getName())) {
                Boolean exists = brandDao.brandExistsForUpdate(brand, session);
                if (exists) {
                    throw new ConflictException("Brand name already exists");
                }
            }

            brand.setModifiedTpwsKey(key.getTpws());
            //call dao
            brandDao.update(brand, session);
            brandDao.commit(session);
            // Retrieve recently updated Brand
            result = brandDao.get(brand.getId(), session);
            log.info(key.toString()+" Updated "+id);
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            brandDao.rollback(session);
            throw e;
        } finally {
            brandDao.close(session);
        }
        return result;
    }

    public SuccessResponse remove(Long id, OauthKey key) throws Exception {

        //validations
        //nullability checks

        SqlSession session = brandDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.BRAND, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "BrandId", Long.toString(id), key.getUserId()));
            }
            brandDao.hasActiveCampaign(id, session);
            brandDao.hasActiveSiteMeasurement(id, session);

            //call dao
            brandDao.remove(id, key.getTpws(), session);
            brandDao.commit(session);
            log.info(key.toString()+" Deleted "+id);
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            brandDao.rollback(session);
            throw e;
        } finally {
            brandDao.close(session);
        }
        return new SuccessResponse("Brand " + id + " successfully deleted.");
    }
}

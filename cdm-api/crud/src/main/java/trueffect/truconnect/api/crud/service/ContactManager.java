package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.SiteContact;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.ContactDao;
import trueffect.truconnect.api.crud.validation.ContactValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.commons.exceptions.business.Error;

import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.util.Collections;

/**
 *
 * @author marleny.patsi
 */
public class ContactManager extends AbstractGenericManager {
    
    private ContactDao contactDao;
    private  ContactValidator validator;

    public ContactManager(ContactDao contactDao, AccessControl accessControl) {
        super(accessControl);
        this.contactDao = contactDao;
        this.validator = new ContactValidator();
    }
    
    public Contact getContact(Long id, OauthKey key) throws Exception {
        //validations
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "id"));
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        
        //Obtain session
        SqlSession session = this.contactDao.openSession();
        Contact result = null;
        try {
            // check access control
            if (!isAdminUser(key.getUserId(), session)) {
                if (!userValidFor(AccessStatement.CONTACT, Collections.singletonList(id), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                            "dataAccessControl.dataNotFoundForUser", key.getUserId()));
                }
            }
            result = contactDao.getById(id, key.getUserId(), session);
        } finally {
            contactDao.close(session);
        }
        return result;
    } 
    
    public Contact getContactByUser(OauthKey key) throws Exception {
        //validations
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }     
        
        //Obtain session
        SqlSession session = this.contactDao.openSession();
        Contact result = null;
        try {
            // check access control
            if (!isAdminUser(key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                        "dataAccessControl.dataNotFoundForUser", key.getUserId()));
            }            

            result = contactDao.getContactByUser(key.getUserId(), session);
        } finally {
            contactDao.close(session);
        }
        return result;
    }
    
    public Contact create(Contact contact, OauthKey key) throws Exception {
        //validations
        if (contact == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Contact"));
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(contact, "contact");
        ValidationUtils.invokeValidator(validator, contact, validationResult);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        
        //Obtain session
        SqlSession session = this.contactDao.openSession();
        Contact result;
        try {
            contact.setCreatedTpwsKey(key.getTpws());
            result = contactDao.create(contact, key, session);
            contactDao.commit(session);
            return result;
        } catch (Exception e) {
            contactDao.rollback(session);
            throw e;
        } finally {
            contactDao.close(session);
        }
    } 
    
    public Contact update(Long id, Contact contact, OauthKey key) throws Exception{
        //validations
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "id"));
        }        
        if (contact == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Contact"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(contact, "contact");
        validator.validateToUpdate(id,contact, validationResult);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        
        //Obtain session        
        SqlSession session = this.contactDao.openSession();
        Contact result = new Contact();
        try {
            // check access control
            if (!isAdminUser(key.getUserId(), session)) {
                if (!userValidFor(AccessStatement.CONTACT, Collections.singletonList(id), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                            "dataAccessControl.dataNotFoundForUser", key.getUserId()));
                }
            }            
            // existence validation
            Contact existentContact = contactDao.getById(contact.getId(), key.getUserId(), session);
            if (existentContact == null) {
                throw new ValidationException(ResourceBundleUtil.getString("global.error.putDataNotFound"));
            }
            
            contact.setModifiedTpwsKey(key.getTpws());
            result = contactDao.update(contact, key, session);
            contactDao.commit(session);
        } catch (Exception e) {
            contactDao.rollback(session);
            throw e;
        } finally {
            contactDao.close(session);
        }
        return result;
    }    
    
    public SuccessResponse delete(Long id, OauthKey key) throws Exception { 
        //validations
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Contact ID"));
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        
        //Obtain session        
        SqlSession session = contactDao.openSession();
        try {
            // check access control
            if (!isAdminUser(key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                        "dataAccessControl.dataNotFoundForUser", key.getUserId()));
            }
            
            if (contactDao.userContactCount(id, session) !=  0L) {
                throw new ValidationException(ResourceBundleUtil.getString(
                        "contact.error.deleteContactWithActiveUser"));
            }
            
            contactDao.delete(id, key, session);
            contactDao.commit(session);
        } catch (Exception e) {
            contactDao.rollback(session);
            throw e;
        } finally {
            contactDao.close(session);
        }
        return new SuccessResponse(ResourceBundleUtil.getString("contact.info.deleteContactSuccess", id.toString()));
    }

    public AgencyContact addAgencyContactRef(AgencyContact agencyContact, OauthKey key) throws Exception {
        //validations
        if (agencyContact == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Agency Contact"));
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        
        //Obtain session        
        SqlSession session = contactDao.openSession();        
        AgencyContact result = null;
        try {
            //check access control
            if (!isAdminUser(key.getUserId(), session)) {
                if (!userValidFor(AccessStatement.AGENCY, Collections.singletonList(agencyContact.getAgencyId()), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                            "dataAccessControl.dataNotFoundForUser", key.getUserId()));
                }
            }            
            agencyContact.setCreatedTpwsKey(key.getTpws());
            
            result = contactDao.addAgencyContactRef(agencyContact, key, session);
            contactDao.commit(session);
        } catch (Exception e) {
            contactDao.rollback(session);
            throw e;
        } finally {
            contactDao.close(session);
        }
        return result;
    }
    
    public SuccessResponse removeAgencyContactRefs(Long contactId, Long agencyId, OauthKey key) throws Exception {
        //validations
        if (contactId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Contact ID"));
        }
        if (agencyId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Agency ID"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        
        //Obtain session        
        SqlSession session = contactDao.openSession(); 
        try {
            // check access control
            if (!isAdminUser(key.getUserId(), session)) {
                if (!userValidFor(AccessStatement.CONTACT, Collections.singletonList(contactId), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                            "dataAccessControl.dataNotFoundForUser", key.getUserId()));
                }
            }         
            contactDao.removeAgencyContactRefs(contactId, agencyId, key, session);
            contactDao.commit(session);
        } catch (Exception e) {
            contactDao.rollback(session);
            throw e;
        } finally {
            contactDao.close(session);
        }
        return new SuccessResponse(ResourceBundleUtil.getString("contact.info.deleteAgencyContactRefSuccess", 
                contactId.toString(), agencyId.toString()));
    }

    public Either<Error, SiteContact> addSiteContactRef(SiteContact siteContact, OauthKey key) {
        //validations
        if (siteContact == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Site Contact"));
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        
        //Obtain session
        SqlSession session = contactDao.openSession();        
        SiteContact result = null;
        try {
            //check access control
            if (!isAdminUser(key.getUserId(), session)) {
                if (!userValidFor(AccessStatement.SITE, Collections.singletonList(siteContact.getSiteId()), key.getUserId(), session)) {
                    return Either.error(new Error(ResourceBundleUtil.getString("SecurityCode.NOT_FOUND_FOR_USER",
                        ResourceBundleUtil.getString("global.label.site"), siteContact.getSiteId().toString(), key.getUserId()), 
                        SecurityCode.PERMISSION_DENIED));
                }
            }
            siteContact.setCreatedTpwsKey(key.getTpws());
            //call dao
            result = contactDao.addSiteContactRef(siteContact, key, session);
            contactDao.commit(session);
        } finally {
            contactDao.close(session);
        }
        return Either.success(result);
    }
}

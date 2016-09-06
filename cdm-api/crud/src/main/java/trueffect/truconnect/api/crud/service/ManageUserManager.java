package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.impl.ContactDaoImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rambert Rioja
 */
public class ManageUserManager {

    private UserManager userManager;
    private ContactManager contactManager;
    private Logger log = LoggerFactory.getLogger(this.getClass());


    public ManageUserManager(PersistenceContext context, AccessControl accessControl) {
        this.userManager = new UserManager(new UserDaoImpl(context, accessControl), accessControl);
        this.contactManager = new ContactManager(new ContactDaoImpl(context), accessControl);
    }

    /**
     * Saves a User Contact
     *
     * @param email E-mail contact for person, lowercased
     * @param lastName Last name of the contact
     * @param firstName First name of the contact
     * @param password Password for the users login
     * @param agencyId Agency the user belongs to
     * @return the new Contact
     */
    public Contact saveAccount(String email, String lastName,
            String firstName, String password, Long agencyId,
            OauthKey key) throws Exception {
        User users = userManager.get(email, email);
        if (users instanceof User) {
            throw new ConflictException("User already exists!");
        }

        Contact contact = null;
        Contact contactToSave = new Contact();
        contactToSave.setLastName(lastName);
        contactToSave.setFirstName(firstName);
        contactToSave.setEmail(email);
        contactToSave.setPhone("");
        contactToSave.setFax("");
        contactToSave.setNotes("");
        contactToSave.setAddress1("");
        contactToSave.setAddress2("");
        contactToSave.setCity("");
        contactToSave.setState("");
        contactToSave.setZip("");
        contactToSave.setCountry("");
        try {
            contact = contactManager.create(contactToSave, key);
            log.debug("Contact = {} was successfully saved for User = {}", contact.toString(), key.getUserId());
        } catch (Exception e) {
            throw e;
        }
        User user = new User();
        user.setUserName(email);
        user.setPassword(password);
        user.setAgencyId(agencyId);
        user.setContactId(contact.getId());
        user.setIsAppAdmin("N");
        user.setIsClientAdmin("Y");
        user.setLimitDomains("N");
        user.setLimitAdvertisers("N");
        userManager.save(user, key);

        return contact;
    }
}

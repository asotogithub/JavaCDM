package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.Error;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.Bootstrap;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.dao.AccessControl;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager class for bootstrapping data for tests.  There is a lot of copied code from the FitNesse
 * tests, but I don't want to link the two code bases.
 */
public class BootstrapManager extends AbstractGenericManager {

    private static Faker faker = new Faker();
    private AgencyManager agencyManager;
    private ContactManager contactManager;
    private UserManager userManager;
    private AdvertiserManager advertiserManager;
    private BrandManager brandManager;
    private CookieDomainManager cookieDomainManager;

    public BootstrapManager(AgencyManager agencyManager, ContactManager contactManager, 
            UserManager userManager, AdvertiserManager advertiserManager, BrandManager brandManager, 
            CookieDomainManager cookieDomainManager, AccessControl accessControl) {
        super(accessControl);
        this.agencyManager = agencyManager;
        this.contactManager = contactManager;
        this.userManager = userManager;
        this.advertiserManager = advertiserManager;
        this.brandManager = brandManager;
        this.cookieDomainManager = cookieDomainManager;
    }

    public Either<Errors, Bootstrap> bootstrap(OauthKey key) {
        // create agency
        Agency agency = agency();
        try {
            agency = agencyManager.save(agency, key);
        } catch (Exception e) {
            return Either.error(new Errors(new Error(e.getMessage(), ApiValidationUtils.TYPE_INVALID)));
        }

        return bootstrap(agency.getId(), key);
    }

    public Either<Errors, Bootstrap> bootstrap(Long agencyId, OauthKey key) {
        // check that agency exists
        try {
            Agency existing = agencyManager.get(agencyId, key);
            if(existing == null) {
                return Either.error(new Errors(new Error(String.format("Agency %d does not exist", agencyId), ApiValidationUtils.TYPE_NOT_FOUND)));
            }
        } catch (Exception e) {
            return Either.error(new Errors(new Error(e.getMessage(), ApiValidationUtils.TYPE_NOT_FOUND)));
        }

        // create fake user name
        String fakeUserName = fakeUserName();
        // create fake password
        String fakePassword = faker.bothify("????####????####????");
        // create contact
        Contact contact = contact(fakeUserName);
        try {
            contact = contactManager.create(contact, key);
        } catch (Exception e) {
            return Either.error(new Errors(new Error(e.getMessage(), ApiValidationUtils.TYPE_INVALID)));
        }
        // create user (set roles on the user, strings)
        User user = user(fakeUserName, fakePassword, contact.getId(), agencyId);
        try {
            user = userManager.save(user, key);
        } catch (Exception e) {
            return Either.error(new Errors(new Error(e.getMessage(), ApiValidationUtils.TYPE_INVALID)));
        }
        // link contact and agency (agencycontact)
        AgencyContact agencyContact = agencyContact(contact.getId(), agencyId);
        try {
            contactManager.addAgencyContactRef(agencyContact, key);
        } catch (Exception e) {
            return Either.error(new Errors(new Error(e.getMessage(), ApiValidationUtils.TYPE_INVALID)));
        }

        return Either.success(new Bootstrap(agencyId, fakeUserName, fakePassword));

    }

    public Either<Errors, Bootstrap> basicSetup(Long agencyId, OauthKey key) {
        // check that agency exists
        try {
            Agency existing = agencyManager.get(agencyId, key);
            if(existing == null) {
                return Either.error(new Errors(new Error(String.format("Agency %d does not exist", agencyId), ApiValidationUtils.TYPE_NOT_FOUND)));
            }
        } catch (Exception e) {
            return Either.error(new Errors(new Error(e.getMessage(), ApiValidationUtils.TYPE_NOT_FOUND)));
        }

        // create advertiser
        Advertiser advertiser = advertiser(agencyId);
        try {
            advertiser = advertiserManager.create(advertiser, key);
        } catch (Exception e) {
            return Either.error(new Errors(new Error(e.getMessage(), ApiValidationUtils.TYPE_INVALID)));
        }

        // create brand
        Brand brand = brand(advertiser.getId());
        try {
            brand = brandManager.create(brand, key);
        } catch (Exception e) {
            return Either.error(new Errors(new Error(e.getMessage(), ApiValidationUtils.TYPE_INVALID)));
        }

        // create domain
        CookieDomain cookieDomain = cookieDomain(agencyId);
        try {
            cookieDomain = cookieDomainManager.create(cookieDomain, key);
        } catch (Exception e) {
            return Either.error(new Errors(new Error(e.getMessage(), ApiValidationUtils.TYPE_INVALID)));
        }

        return Either.success(new Bootstrap(agencyId, advertiser.getId(), brand.getId(), cookieDomain.getId()));
    }

    private static String fakeUserName() {
        return faker.letterify("????????????????????") + "_autouser@trueffect.com";
    }

    public static String fakeUrl() {
        return ("www." + faker.letterify("????????????????????") + ".com");
    }

    private static Agency agency() {
        Agency agency = new Agency();
        agency.setName(faker.lorem().sentence(4));
        agency.setEnableHtmlInjection(true);
        agency.setAddress1(faker.address().streetAddress(false));
        agency.setAddress2(faker.address().secondaryAddress());
        agency.setCity(faker.address().citySuffix());
        agency.setState(faker.address().stateAbbr());
        agency.setZipCode(faker.address().zipCode());
        agency.setCountry(faker.address().country());
        agency.setPhoneNumber(faker.phoneNumber().phoneNumber());
        agency.setUrl(faker.internet().url());
        agency.setFaxNumber(faker.phoneNumber().phoneNumber());
        agency.setContactDefault(faker.internet().emailAddress());
        agency.setNotes(faker.lorem().sentence(3));
        return agency;
    }

    private static Contact contact(String userName) {
        Contact contact = new Contact();
        contact.setFirstName(faker.name().firstName());
        contact.setLastName(faker.name().lastName());
        contact.setEmail(userName);
        contact.setAddress1(faker.address().streetAddress(false));
        contact.setAddress2(faker.address().secondaryAddress());
        contact.setCity(faker.address().citySuffix());
        contact.setState(faker.address().stateAbbr());
        contact.setZip(faker.address().zipCode());
        contact.setCountry(faker.address().country());
        contact.setPhone(faker.phoneNumber().phoneNumber());
        contact.setFax(faker.phoneNumber().phoneNumber());
        contact.setNotes(faker.lorem().sentence(2));
        contact.setCreatedTpwsKey("000000");
        contact.setModifiedTpwsKey("000000");
        return contact;
    }

    private static User user(String userName, String password, Long contactId, Long agencyId) {
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        user.setCreatedTpwsKey("000000");
        user.setModifiedTpwsKey("000000");
        user.setLimitDomains("N");
        user.setLimitAdvertisers("N");
        user.setIsTraffickingContact("Y");
        user.setContactId(contactId);
        user.setAgencyId(agencyId);

        // Set up roles
        List<String> roles = new ArrayList<String>();
        roles.add("ROLE_API_FULL_ACCESS");
        roles.add("ROLE_API_TEST_ACCESS");
        roles.add("TE_ADMIN");
        roles.add("ROLE_APP_ADMIN");
        roles.add("TEST_SETUP");
        roles.add("CM_USER");
        user.setRoles(roles);
        return user;
    }

    private static AgencyContact agencyContact(Long contactId, Long agencyId) {
        AgencyContact agencyContact = new AgencyContact();
        agencyContact.setCreatedTpwsKey("000000");
        agencyContact.setModifiedTpwsKey("000000");
        agencyContact.setContactId(contactId);
        agencyContact.setAgencyId(agencyId);
        agencyContact.setTypeId(1L);
        return agencyContact;
    }

    private static Advertiser advertiser(Long agencyId) {
        Advertiser advertiser = new Advertiser();
        advertiser.setName("Automation Advertiser" + faker.lorem().sentence(5));
        advertiser.setAddress1(faker.address().streetAddress(false));
        advertiser.setAddress2(faker.address().secondaryAddress());
        advertiser.setCity(faker.address().citySuffix());
        advertiser.setState(faker.address().stateAbbr());
        advertiser.setZipCode(faker.address().zipCode());
        advertiser.setCountry(faker.address().country());
        advertiser.setPhoneNumber(faker.phoneNumber().phoneNumber());
        advertiser.setUrl(faker.internet().url());
        advertiser.setFaxNumber(faker.phoneNumber().phoneNumber());
        advertiser.setContactDefault(faker.internet().emailAddress());
        advertiser.setNotes(faker.lorem().sentence(3));
        advertiser.setIsHidden("N");
        advertiser.setEnableHtmlTag(0L);
        advertiser.setAgencyId(agencyId);
        return advertiser;
    }

    private static Brand brand(Long advertiserId) {
        Brand brand = new Brand();
        brand.setName(faker.lorem().sentence(2));
        brand.setDescription(faker.lorem().sentence(2));
        brand.setIsHidden("N");
        brand.setAdvertiserId(advertiserId);
        return brand;
    }

    private static CookieDomain cookieDomain(Long agencyId) {
        CookieDomain domain = new CookieDomain();
        domain.setDomain(fakeUrl());
        domain.setCreatedTpwsKey("000000");
        domain.setModifiedTpwsKey("000000");
        domain.setAgencyId(agencyId);
        return domain;
    }

}

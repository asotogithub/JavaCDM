package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trueffect.truconnect.api.crud.EntityFactory.createAdvertiser;
import static trueffect.truconnect.api.crud.EntityFactory.createAgency;
import static trueffect.truconnect.api.crud.EntityFactory.createAgencyContact;
import static trueffect.truconnect.api.crud.EntityFactory.createBrand;
import static trueffect.truconnect.api.crud.EntityFactory.createContact;
import static trueffect.truconnect.api.crud.EntityFactory.createCookieDomain;
import static trueffect.truconnect.api.crud.EntityFactory.createUser;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.Bootstrap;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.ContactDao;

import org.junit.Before;
import org.junit.Test;

public class BootstrapManagerTest extends AbstractManagerTest {

    private BootstrapManager bootstrapManager;
    private AgencyManager agencyManagerMock;
    private ContactManager contactManagerMock;
    private UserManager userManagerMock;
    private AdvertiserManager advertiserManagerMock;
    private BrandManager brandManagerMock;
    private CookieDomainManager cookieDomainManagerMock;

    @Before
    public void init() throws Exception {
        agencyManagerMock = mock(AgencyManager.class);
        contactManagerMock = mock(ContactManager.class);
        userManagerMock = mock(UserManager.class);
        advertiserManagerMock = mock(AdvertiserManager.class);
        brandManagerMock = mock(BrandManager.class);
        cookieDomainManagerMock = mock(CookieDomainManager.class);
        bootstrapManager = new BootstrapManager(agencyManagerMock, contactManagerMock, 
                userManagerMock, advertiserManagerMock, brandManagerMock, cookieDomainManagerMock, 
                accessControlMockito);

        when(agencyManagerMock.save(any(Agency.class), eq(key))).thenReturn(createAgency());
        when(agencyManagerMock.get(anyLong(), eq(key))).thenReturn(createAgency());
        when(contactManagerMock.create(any(Contact.class), eq(key))).thenReturn(createContact());
        when(userManagerMock.save(any(User.class), eq(key))).thenReturn(createUser());
        when(contactManagerMock.addAgencyContactRef(any(AgencyContact.class), eq(key))).thenReturn(createAgencyContact());
        when(advertiserManagerMock.create(any(Advertiser.class), eq(key))).thenReturn(createAdvertiser());
        when(brandManagerMock.create(any(Brand.class), eq(key))).thenReturn(createBrand());
        when(cookieDomainManagerMock.create(any(CookieDomain.class), eq(key))).thenReturn(createCookieDomain());
    }

    @Test
    public void testErrorOnCreatingAgency() throws Exception {
        String expectedMessage = "Error saving agency";

        when(agencyManagerMock.save(any(Agency.class), eq(key))).thenThrow(new Exception(expectedMessage));

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.bootstrap(key);

        assertThat(bootstrap.error().getErrors().get(0).getMessage(), is(equalTo(expectedMessage)));
    }

    @Test
    public void testErrorOnCreatingContact() throws Exception {
        String expectedMessage = "Error saving contact";

        when(contactManagerMock.create(any(Contact.class), eq(key))).thenThrow(new Exception(expectedMessage));

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.bootstrap(key);

        assertThat(bootstrap.error().getErrors().get(0).getMessage(), is(equalTo(expectedMessage)));
    }

    @Test
    public void testErrorOnCreatingUser() throws Exception {
        String expectedMessage = "Error saving user";

        when(userManagerMock.save(any(User.class), eq(key))).thenThrow(new Exception(expectedMessage));

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.bootstrap(key);

        assertThat(bootstrap.error().getErrors().get(0).getMessage(), is(equalTo(expectedMessage)));
    }

    @Test
    public void testErrorOnCreatingAgencyContact() throws Exception {
        String expectedMessage = "Error saving agency contact";

        when(contactManagerMock.addAgencyContactRef(any(AgencyContact.class), eq(key))).thenThrow(new Exception(expectedMessage));

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.bootstrap(key);

        assertThat(bootstrap.error().getErrors().get(0).getMessage(), is(equalTo(expectedMessage)));
    }

    @Test
    public void testWhenUsingExistingAgencyAndItDoesNotExist() throws Exception {
        when(agencyManagerMock.get(anyLong(), eq(key))).thenReturn(null);

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.bootstrap(123L, key);

        assertThat(bootstrap.isError(), is(true));
    }

    @Test
    public void testWhenUsingExistingAgencyAndItDoesExist() throws Exception {
        Agency agency = createAgency();
        Contact contact = createContact();
        User user = createUser();

        when(agencyManagerMock.get(anyLong(), eq(key))).thenReturn(agency);
        when(contactManagerMock.create(any(Contact.class), eq(key))).thenReturn(contact);
        when(userManagerMock.save(any(User.class), eq(key))).thenReturn(user);
        when(contactManagerMock.addAgencyContactRef(any(AgencyContact.class), eq(key))).thenReturn(createAgencyContact());

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.bootstrap(agency.getId(), key);
        Bootstrap actual = bootstrap.success();

        assertThat(actual.getAgencyId(), is(equalTo(agency.getId())));
        // not checking user information because there is not a way to inject the username or password into the function.
    }

    @Test
    public void testEverythingSavedSuccessfully() throws Exception {
        Agency agency = createAgency();
        Contact contact = createContact();
        User user = createUser();

        when(agencyManagerMock.save(any(Agency.class), eq(key))).thenReturn(agency);
        when(contactManagerMock.create(any(Contact.class), eq(key))).thenReturn(contact);
        when(userManagerMock.save(any(User.class), eq(key))).thenReturn(user);
        when(contactManagerMock.addAgencyContactRef(any(AgencyContact.class), eq(key))).thenReturn(createAgencyContact());

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.bootstrap(key);
        Bootstrap actual = bootstrap.success();

        assertThat(actual.getAgencyId(), is(equalTo(agency.getId())));
        // not checking user information because there is not a way to inject the username or password into the function.
    }

    @Test
    public void testBasicSetupWithNonExistingAgency() throws Exception {
        when(agencyManagerMock.get(anyLong(), eq(key))).thenReturn(null);

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.basicSetup(123L, key);

        assertThat(bootstrap.isError(), is(true));
    }

    @Test
    public void testBasicSetupWhenCreatingAdvertiserFails() throws Exception {
        String expectedMessage = "Error saving advertiser";

        when(advertiserManagerMock.create(any(Advertiser.class), eq(key))).thenThrow(new Exception(expectedMessage));

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.basicSetup(123L, key);

        assertThat(bootstrap.error().getErrors().get(0).getMessage(), is(equalTo(expectedMessage)));
    }

    @Test
    public void testBasicSetupWhenCreatingBrandFails() throws Exception {
        String expectedMessage = "Error saving brand";

        when(brandManagerMock.create(any(Brand.class), eq(key))).thenThrow(new Exception(expectedMessage));

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.basicSetup(123L, key);

        assertThat(bootstrap.error().getErrors().get(0).getMessage(), is(equalTo(expectedMessage)));
    }

    @Test
    public void testBasicSetupWhenCreatingCookieDomainFails() throws Exception {
        String expectedMessage = "Error saving cookie domain";

        when(cookieDomainManagerMock.create(any(CookieDomain.class), eq(key))).thenThrow(new Exception(expectedMessage));

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.basicSetup(123L, key);

        assertThat(bootstrap.error().getErrors().get(0).getMessage(), is(equalTo(expectedMessage)));
    }

    @Test
    public void testBasicSetup() throws Exception {
        Agency agency = createAgency();
        Advertiser advertiser = createAdvertiser();
        Brand brand = createBrand();
        CookieDomain cookieDomain = createCookieDomain();

        when(agencyManagerMock.get(anyLong(), eq(key))).thenReturn(agency);
        when(advertiserManagerMock.create(any(Advertiser.class), eq(key))).thenReturn(advertiser);
        when(brandManagerMock.create(any(Brand.class), eq(key))).thenReturn(brand);
        when(cookieDomainManagerMock.create(any(CookieDomain.class), eq(key))).thenReturn(cookieDomain);

        Either<Errors, Bootstrap> bootstrap = bootstrapManager.basicSetup(agency.getId(), key);
        Bootstrap actual = bootstrap.success();

        assertThat(actual.getAgencyId(), is(equalTo(agency.getId())));
        assertThat(actual.getAdvertiserId(), is(equalTo(advertiser.getId())));
        assertThat(actual.getBrandId(), is(equalTo(brand.getId())));
        assertThat(actual.getCookieDomainId(), is(equalTo(cookieDomain.getId())));
    }
}

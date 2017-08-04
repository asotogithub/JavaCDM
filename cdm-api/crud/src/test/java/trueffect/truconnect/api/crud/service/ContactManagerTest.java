package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.SiteContact;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.ContactDao;

import org.junit.Test;
import org.junit.Before;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

/**
 *
 * @author Abel Soto
 * @edited Richard Jaldin
 */
public class ContactManagerTest extends AbstractManagerTest {

    private ContactManager contactManager;
    private ContactDao contactDaoMock;
    
    private Contact contact;
    private AgencyContact agencyContact;
    private SiteContact siteContact;

    @Before
    public void init() throws Exception {
        contactDaoMock = mock(ContactDao.class);
        contactManager = new ContactManager(contactDaoMock, accessControlMockito);
        
        contact = EntityFactory.createContact();
        agencyContact = EntityFactory.createAgencyContact();
        siteContact = EntityFactory.createSiteContact();

        //mock sessions
        when(contactDaoMock.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(contactDaoMock).close(sqlSessionMock);
        doNothing().when(contactDaoMock).commit(sqlSessionMock);

        //mock access control
        when(accessControlMockito.isAdmin(eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CONTACT),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        
        //mock behaviors
        when(contactDaoMock.getById(anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenReturn(contact);
        when(contactDaoMock.create(any(Contact.class), eq(key),
                eq(sqlSessionMock))).thenAnswer(createContact());
    }
    
    @Test
    public void testCreateContactOk() throws Exception {
        // set data
        contact.setId(null);
        // Initialize mocks
        
        // perform test
        Contact result = contactManager.create(contact, key);
        assertNotNull(result);
        assertThat(result.getId(), is(notNullValue()));        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateContactWithoutContact() throws Exception {
        // set data
        contact = null;
        // Initialize mocks
        
        // perform test
        contactManager.create(contact, key);
    }

    @Test(expected = ValidationException.class)
    public void testCreateContactWithWrongPayload() throws Exception {
        // set data
        contact.setId(null);
        contact.setLastName(null);
        contact.setEmail(null);
        // Initialize mocks
        
        // perform test
        contactManager.create(contact, key);
    }

    @Test
    public void testGetContactAdminUserOk() throws Exception {
        
        // Initialize mocks
        when(contactDaoMock.getById(anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenReturn(contact);
        
        // perform test
        Contact result = contactManager.getContact(contact.getId(), key);
        assertNotNull(result);
        assertThat(result.getId(), is(notNullValue()));        
    }
    
    @Test
    public void testGetContactAppUserOk() throws Exception {

        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(false);
        when(contactDaoMock.getById(anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenReturn(contact);
        
        // perform test
        Contact result = contactManager.getContact(contact.getId(), key);
        assertNotNull(result);
        assertThat(result.getId(), is(notNullValue()));        
    }
    
    @Test(expected = DataNotFoundForUserException.class)
    public void testGetContactAppUserWithoutAccess() throws Exception {
       
        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CONTACT),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);
        
        // perform test
        contactManager.getContact(contact.getId(), key);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetContactWithNullParameters() throws Exception {
        // set data
        contact.setId(null);
        
        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CONTACT),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);
        
        // perform test
        contactManager.getContact(contact.getId(), key);
    }

    @Test
    public void testGetContactByUserAdminUserOk() throws Exception {
        
        // Initialize mocks
        when(contactDaoMock.getContactByUser(eq(key.getUserId()),
                eq(sqlSessionMock))).thenReturn(contact);
        
        // perform test
        Contact result = contactManager.getContactByUser(key);
        assertNotNull(result);
        assertThat(result.getId(), is(notNullValue()));        
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void testGetContactByUserAdminUserWithAppAdmin() throws Exception {
        
        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(false);
        
        // perform test
        contactManager.getContactByUser(key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetContactByUserAdminUserWithNullParameters() throws Exception {
        // set data
        key = null;
        
        // perform test
        contactManager.getContactByUser(key);
    }
    
    @Test
    public void testUpdateContactOk() throws Exception {

        // Initialize mocks
        when(contactDaoMock.update(any(Contact.class), eq(key),
                eq(sqlSessionMock))).thenAnswer(createContact());
        
        // perform test
        Contact result = contactManager.update(contact.getId(), contact, key);
        assertNotNull(result);
        assertThat(result.getId(), is(notNullValue()));        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateContactWithNullParameters() throws Exception {
        // set data
        contact = null;
        
        // perform test
        contactManager.update(null, contact, key);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateContactWithWrongPayload() throws Exception {
        // set data
        Long id = contact.getId() + 1;
        contact.setFirstName(null);
        contact.setEmail(null);
        
        // perform test
        contactManager.update(id, contact, key);
    }
    
    @Test(expected = DataNotFoundForUserException.class)
    public void testUpdateContactWithWrongAccess() throws Exception {

        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CONTACT),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);
        
        // perform test
        contactManager.update(contact.getId(), contact, key);
    }
    
    @Test(expected = ValidationException.class)
    public void testUpdateContactWithWrongContact() throws Exception {

        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(true);
        when(contactDaoMock.getById(anyLong(), eq(key.getUserId()),
                eq(sqlSessionMock))).thenReturn(null);
        
        // perform test
        contactManager.update(contact.getId(), contact, key);
    }

    @Test
    public void testDeleteContactOk() throws Exception {

        // Initialize mocks
        when(contactDaoMock.userContactCount(anyLong(), eq(sqlSessionMock))).thenReturn(0L);
        when(contactDaoMock.delete(anyLong(), eq(key), eq(sqlSessionMock))).thenReturn(1);
        
        // perform test
        SuccessResponse result = contactManager.delete(contact.getId(), key);
        assertNotNull(result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteContactWithNullParameters() throws Exception {
        // set data
        contact.setId(null);
        key = null;
        
        // perform test
        contactManager.delete(contact.getId(), key);
    }
    
    @Test(expected = DataNotFoundForUserException.class)
    public void testDeleteContactWithWrongAccess() throws Exception {

        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(false);        

        // perform test
        contactManager.delete(contact.getId(), key);
    }
    
    @Test(expected = ValidationException.class)
    public void testDeleteContactWithActiveUsers() throws Exception {

        // Initialize mocks
        when(contactDaoMock.userContactCount(anyLong(), eq(sqlSessionMock))).thenReturn(1L);
        
        // perform test
        contactManager.delete(contact.getId(), key);
    }

    @Test
    public void testAddAgencyContactOk() throws Exception {
        
        // Initialize mocks
        when(contactDaoMock.userContactCount(anyLong(), eq(sqlSessionMock))).thenReturn(0L);
        when(contactDaoMock.addAgencyContactRef(any(AgencyContact.class), eq(key), 
                eq(sqlSessionMock))).thenAnswer(createAgencyContact());
        
        // perform test
        AgencyContact result = contactManager.addAgencyContactRef(agencyContact, key);
        assertNotNull(result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddAgencyContactWithNullParameters() throws Exception {
        // set data
        agencyContact = null;
        key = null;
        
        // perform test
        contactManager.addAgencyContactRef(agencyContact, key);
    }
    
    @Test(expected = DataNotFoundForUserException.class)
    public void testAddAgencyContactWithWrongAccess() throws Exception {

        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        contactManager.addAgencyContactRef(agencyContact, key);
    }

    @Test
    public void testRemoveAgencyContactOk() throws Exception {
        
        // Initialize mocks
        doNothing().when(contactDaoMock).removeAgencyContactRefs(anyLong(), anyLong(), 
                eq(key), eq(sqlSessionMock));
        
        // perform test
        SuccessResponse result = contactManager.removeAgencyContactRefs(agencyContact.getContactId(), agencyContact.getAgencyId(), key);
        assertNotNull(result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveAgencyContactWithNullParameters() throws Exception {
        // set data
        agencyContact.setContactId(null);
        agencyContact.setAgencyId(null);
        key = null;
        
        // perform test
        contactManager.removeAgencyContactRefs(agencyContact.getContactId(), agencyContact.getAgencyId(), key);
    }
    
    @Test(expected = DataNotFoundForUserException.class)
    public void testRemoveAgencyContactWithWrongAccess() throws Exception {

        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CONTACT),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        contactManager.removeAgencyContactRefs(agencyContact.getContactId(), agencyContact.getAgencyId(), key);
    }
    
    @Test
    public void testAddSiteContactOk() {
        
        // Initialize mocks
        when(contactDaoMock.addSiteContactRef(any(SiteContact.class), eq(key), 
                eq(sqlSessionMock))).thenAnswer(createSiteContact());
        
        // perform test
        Either<Error, SiteContact> result = contactManager.addSiteContactRef(siteContact, key);
        assertNotNull(result);
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddSiteContactWithNullParameters() {
        // set data
        agencyContact = null;
        key = null;
        
        // perform test
        contactManager.addSiteContactRef(siteContact, key);
    }
    
    @Test
    public void testAddSiteContactWithWrongAccess() {

        // Initialize mocks
        when(accessControlMockito.isAdmin(eq(key.getUserId()), 
                eq(sqlSessionMock))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Error, SiteContact> result = contactManager.addSiteContactRef(siteContact, key);
        assertNotNull(result);
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));        
    }    

    private static Answer<Contact> createContact() {
        return new Answer<Contact>() {
            @Override
            public Contact answer(InvocationOnMock invocation) throws Throwable {
                Contact result = (Contact) invocation.getArguments()[0];

                result.setId(Math.abs(EntityFactory.random.nextLong()));
                result.setCreatedDate(new Date());
                return result;
            }

        };
    }
    
    private static Answer<AgencyContact> createAgencyContact() {
        return new Answer<AgencyContact>() {
            @Override
            public AgencyContact answer(InvocationOnMock invocation) throws Throwable {
                AgencyContact result = (AgencyContact) invocation.getArguments()[0];
                result.setLogicalDelete("N");
                return result;
            }

        };
    }

    private static Answer<SiteContact> createSiteContact() {
        return new Answer<SiteContact>() {
            @Override
            public SiteContact answer(InvocationOnMock invocation) throws Throwable {
                SiteContact result = (SiteContact) invocation.getArguments()[0];
                result.setLogicalDelete("N");
                return result;
            }

        };
    }
}

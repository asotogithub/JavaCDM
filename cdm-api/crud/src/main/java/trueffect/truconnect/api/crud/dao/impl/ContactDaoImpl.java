package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.SiteContact;
import trueffect.truconnect.api.crud.dao.ContactDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;

/**
 *
 * @author Abel Soto
 */
public class ContactDaoImpl extends AbstractGenericDao implements ContactDao {
    
    private static final String STATEMENT_GET_BY_ID = "Contact.getContact";
    private static final String STATEMENT_GET_BY_USER = "Contact.getContactByUser";
    private static final String STATEMENT_GET_COUNT_USERS_RELATED = "Contact.userContactCount";
    private static final String STATEMENT_GET_NEXT_ID = "GenericQueries.getNextId";
    private static final String STATEMENT_SEQ_CONTACT = "SEQ_CONTACT";
    private static final String STATEMENT_INSERT = "Contact.insertContact";
    private static final String STATEMENT_UPDATE = "Contact.updateContact";
    private static final String STATEMENT_DELETE = "Contact.deleteContact";
    private static final String STATEMENT_UPDATE_AGENCY_CONTACT = "Contact.updateAgencyContactRef";
    private static final String STATEMENT_INSERT_AGENCY_CONTACT = "Contact.addAgencyContactRef";
    private static final String STATEMENT_DELETE_AGENCY_CONTACT = "Contact.clearAgencyContactRef";
    private static final String STATEMENT_GET_COUNT_AGENCY_CONTACT = "Contact.existsAgencyContactRef";
    private static final String STATEMENT_GET_AGENCY_CONTACT = "Contact.getAgencyContact";
    private static final String STATEMENT_INSERT_SITE_CONTACT = "Contact.AddSiteContactRef";
    private static final String STATEMENT_GET_SITE_CONTACT = "Contact.getSiteContact";    

    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this
     * DAO
     */
    public ContactDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Contact getById(Long id,  String userId, SqlSession session) {
        Contact result = getPersistenceContext().selectSingle(STATEMENT_GET_BY_ID, id, session, Contact.class);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Contact getContactByUser(String userId, SqlSession session) {
        Contact result = getPersistenceContext().selectSingle(STATEMENT_GET_BY_USER, userId, session, Contact.class);
        return result;
    }    
    
    @Override
    public Long userContactCount(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_COUNT_USERS_RELATED, id, session, Long.class);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Contact create(Contact contact, OauthKey key, SqlSession session) {
        Contact result;

        //the next id for contact
        Long id = getPersistenceContext().selectSingle(STATEMENT_GET_NEXT_ID, STATEMENT_SEQ_CONTACT, session, Long.class);
        //Parsing the input parameters of the process
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("lname", contact.getLastName());
        parameter.put("fname", contact.getFirstName());
        parameter.put("email", contact.getEmail());
        parameter.put("phone", contact.getPhone());
        parameter.put("fax", contact.getFax());
        parameter.put("notes", contact.getNotes());
        parameter.put("address1", contact.getAddress1());
        parameter.put("address2", contact.getAddress2());
        parameter.put("city", contact.getCity());
        parameter.put("state", contact.getState());
        parameter.put("zip", contact.getZip());
        parameter.put("country", contact.getCountry());
        parameter.put("tpwsKey", contact.getCreatedTpwsKey());

        getPersistenceContext().execute(STATEMENT_INSERT, parameter, session);
        result = this.getById(id, key.getUserId(), session);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Contact update(Contact contact, OauthKey key, SqlSession session) {
        Contact result;
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", contact.getId());
        parameter.put("lname", contact.getLastName());
        parameter.put("fname", contact.getFirstName());
        parameter.put("email", contact.getEmail());
        parameter.put("phone", contact.getPhone());
        parameter.put("fax", contact.getFax());
        parameter.put("notes", contact.getNotes());
        parameter.put("address1", contact.getAddress1());
        parameter.put("address2", contact.getAddress2());
        parameter.put("city", contact.getCity());
        parameter.put("state", contact.getState());
        parameter.put("zip", contact.getZip());
        parameter.put("country", contact.getCountry());
        parameter.put("tpwsKey", contact.getModifiedTpwsKey());
        getPersistenceContext().execute(STATEMENT_UPDATE, parameter, session);
        result = this.getById(contact.getId(), key.getUserId(), session);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int delete(Long id, OauthKey key, SqlSession session) { 
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("tpwsKey", key.getTpws());
        return getPersistenceContext().update(STATEMENT_DELETE, parameter, session);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AgencyContact addAgencyContactRef(AgencyContact agencyContact, OauthKey key, SqlSession session) {
        AgencyContact result = null;
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("contactId", agencyContact.getContactId());
        parameter.put("agencyId", agencyContact.getAgencyId());
        parameter.put("typeId", agencyContact.getTypeId());
        parameter.put("createdTpwsKey", agencyContact.getCreatedTpwsKey());

        if (existsAgencyContactRef(parameter, session) >= 1L) {
            getPersistenceContext().execute(STATEMENT_UPDATE_AGENCY_CONTACT, parameter, session);
        } else {
            getPersistenceContext().execute(STATEMENT_INSERT_AGENCY_CONTACT, parameter, session);
        }
        result = getAgencyContact(agencyContact, session);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeAgencyContactRefs(Long contactId, Long agencyId, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("contactId", contactId);
        parameter.put("agencyId", agencyId);
        parameter.put("modifiedTpwsKey", key.getTpws());
        getPersistenceContext().execute(STATEMENT_DELETE_AGENCY_CONTACT, parameter, session);
    }

    private Long existsAgencyContactRef(HashMap<String, Object> parameter, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_COUNT_AGENCY_CONTACT, parameter, session, Long.class);
    }

    private AgencyContact getAgencyContact(AgencyContact agencyContact, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("contactId", agencyContact.getContactId());
        parameter.put("agencyId", agencyContact.getAgencyId());
        parameter.put("typeId", agencyContact.getTypeId());
        return getPersistenceContext().executeSelectOne(STATEMENT_GET_AGENCY_CONTACT, parameter, session, AgencyContact.class);
    }

    @Override
    public SiteContact addSiteContactRef(SiteContact siteContact, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("siteId", siteContact.getSiteId());
        parameter.put("contactId", siteContact.getContactId());
        parameter.put("typeId", siteContact.getTypeId());
        parameter.put("createdTpwsKey", siteContact.getCreatedTpwsKey());
        
        getPersistenceContext().execute(STATEMENT_INSERT_SITE_CONTACT, parameter, session);
        SiteContact  result = getSiteContact(siteContact, session);
        return result;
    }

    private SiteContact getSiteContact(SiteContact siteContact, SqlSession session) {
        return getPersistenceContext().executeSelectOne(STATEMENT_GET_SITE_CONTACT, siteContact, session, SiteContact.class);
    }
}

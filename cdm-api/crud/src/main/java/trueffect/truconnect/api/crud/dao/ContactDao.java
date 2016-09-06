package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.SiteContact;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author Abel Soto
 */
public interface ContactDao extends GenericDao {

    /**
     * Returns the Contact based on the identifier
     *
     * @param id ID of the Contact to return
     * @param userId
     * @param session
     * @return the Contact of the ID identifier
     */
    Contact getById(Long id, String userId, SqlSession session);
    
    /**
     * Returns the Contact based on its User
     *
     * @param userId
     * @param session
     * @return the Contact of the ID identifier
     */
    Contact getContactByUser(String userId, SqlSession session);
    
    Long userContactCount(Long id, SqlSession session) ;

    /**
     * Saves a Contact
     *
     * @param contact Contact to be saved.
     * @param key
     * @param session
     * @return the new Contact
     */
    Contact create(Contact contact, OauthKey key, SqlSession session);

    /**
     * Updates an existing Contact
     *
     * @param contact Contact to be updated.
     * @param key
     * @param session
     * @return Contact updated
     */
    Contact update(Contact contact, OauthKey key, SqlSession session);

    /**
     * Removes an Contact based on the identifier
     *
     * @param id ID of the Contact to be removed
     * @param key
     * @param session
     */
    int delete(Long id, OauthKey key, SqlSession session);

    /**
     * Add the association between Agency and Contact based their identifiers
     *
     * @param agencyContact Agency Contact to be associated
     * @param key
     * @param session
     *
     * @return Contact that has associated
     */
    AgencyContact addAgencyContactRef(AgencyContact agencyContact, OauthKey key, SqlSession session);

    /**
     * Removes the association between Agency and Contact based their identifiers
     *
     * @param contactId ID of the Contact to be removed
     * @param agencyId ID of the Agency to be removed
     * @param key User session that created the record
     * @param session
     */
    void removeAgencyContactRefs(Long contactId, Long agencyId, OauthKey key, SqlSession session);

    /**
     * Add the association between Agency and Contact based their identifiers
     *
     * @param siteContact Site Contact to be associated
     * @param key
     * @param session
     *
     * @return Contact that has associated
     */
    SiteContact addSiteContactRef(SiteContact siteContact, OauthKey key, SqlSession session);    
}

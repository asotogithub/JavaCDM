package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author Abel Soto
 */
public interface AdvertiserDao extends GenericDao{

    /**
     * Gets an Advertiser based on its ID
     *
     * @param id the ID of the Advertiser.
     * @param session
     * @return the Advertiser
     * @throws java.lang.Exception
     */
    Advertiser get(Long id, SqlSession session);

    /**
     * Gets a RecordSet of Advertisers
     *
     * @param criteria
     * @param userId
     * @param session
     * @return a RecordSet of Advertisers
     */
    RecordSet<Advertiser> getAdvertisers(SearchCriteria criteria, String userId, SqlSession session)
            throws SearchApiException;

    /**
     * Gets a RecordSet of Advertisers by an UserId
     *
     * @param userId
     * @param session
     * @return a RecordSet of AdSizes
     */
    RecordSet<Advertiser> getAdvertisersByUserId(String userId, SqlSession session);

    /**
     * Saves the Advertiser object.
     *
     * @param advertiser
     * @param session
     * @throws java.lang.Exception
     */
    void create(Advertiser advertiser, SqlSession session) throws Exception;

    /**
     * Updates data of an Advertiser
     *
     * @param advertiser the Advertiser.
     * @param session
     * @throws java.lang.Exception
     */
    void update(Advertiser advertiser, SqlSession session) throws Exception;

    /**
     * Removes an Advertiser physically from the Database.
     *
     * @param id The ID of the Advertiser
     * @param session
     * @throws java.lang.Exception
     */
    void hardRemove(Long id, SqlSession session) throws Exception;
    
    Boolean advertiserNameExists(Advertiser advertiser, SqlSession session) throws Exception;

}

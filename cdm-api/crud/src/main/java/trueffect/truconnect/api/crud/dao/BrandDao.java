package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author Rambert Rioja
 */
public interface BrandDao extends GenericDao{

    /**
     * Gets a Brand based on its ID
     *
     * @param id the ID of the Brand.
     * @param session
     * @return the Brand
     * @throws java.lang.Exception
     */
    Brand get(Long id, SqlSession session) throws Exception;

    /**
     * Gets a RecordSet of Brands
     *
     * @param criteria
     * @param userId
     * @param session
     * @return a RecordSet of Brands
     * @throws java.lang.Exception
     */
    RecordSet<Brand> getBrands(SearchCriteria criteria, String userId, SqlSession session) throws Exception;

    /**
     * Gets a RecordSet of Brands by Advertiser ID
     *
     * @param advertiserId
     * @param userId
     * @param session
     * @return a RecordSet of Brands
     */
    RecordSet<Brand> getBrandsByAdvertiserId(Long advertiserId, String userId, SqlSession session);

    /**
     * Create a new Brand after complete validations
     *
     * @param brand  Brand to create
     * @param session 
     * @throws java.lang.Exception
     */  
    void create(Brand brand, SqlSession session) throws Exception;

    /**
     * Updates data of a Brand
     *
     * @param brand the Advertiser.
     * @param session
     * @throws java.lang.Exception
     */
    void update(Brand brand, SqlSession session) throws Exception;

    /**
     * Removes a Brand physically from the Database.
     *
     * @param id The ID of the Brand
     * @param userId
     * @param session
     * @throws java.lang.Exception
     */
    void remove(Long id, String userId, SqlSession session) throws Exception;

    void brandExistsForSave(String name, Long advertiserId, SqlSession session) throws Exception;
    
    Boolean brandExistsForUpdate(Brand brand, SqlSession session);
    
    void hasActiveCampaign(Long id, SqlSession session) throws Exception;
    
    void hasActiveSiteMeasurement(Long id, SqlSession session) throws Exception;
}

package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.Organization;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 *
 * @author Rambert Rioja
 * @edited Richard Jaldin
 * @edited Abel Soto
 */
public interface AgencyDao extends GenericDao{

    /**
     * Gets an agency based on its ID
     *
     * @param id the ID of the agency.
     * @param session
     * @return the Agency
     * @throws java.lang.Exception
     */
    Agency get(Long id, SqlSession session) throws Exception;

    /**
     * Saves the Agency.
     *
     * @param agency The Agency to be saved.
     * @param idOrganization
     * @param session
     * @return The Agency saved on the database.
     * @throws java.lang.Exception
     */
    Agency save(Agency agency, Long idOrganization, SqlSession session) throws Exception;
    
    /**
     * Updates an Agency.
     *
     * @param agency The Agency to be updated.
     * @param session
     *
     * @return The Agency updated on the database.
     * @throws java.lang.Exception
     */
    Agency update(Agency agency, SqlSession session) throws Exception;

    /**
     * Mark an agency as deleted, Note that is a logical deletion.
     *
     * @param id The ID of the agency
     * @param session
     * @throws java.lang.Exception
     */
    void hardRemove(Long id, SqlSession session) throws Exception;
    
    /**
     * Gets the list of all campaigns under an Agency.  This will
     * aggregate all of the campaigns from the Advertiser and Brands
     * under the Agency.
     *
     * @param id the ID of the Agency.
     * @param session
     * @return list of campaigns.
     *
     * @throws Exception
     */
    RecordSet<CampaignDTO> getCampaigns(Long id, String userId, SqlSession session) throws Exception;

    Long saveOrganization(Organization organization, SqlSession session)throws Exception;

    boolean getExistsAgency(String name, SqlSession session) throws Exception;

    List<Long> getAllPackageIds(Long agencyId, SqlSession session);
    
    List<Long> getAllPlacementIds(Long agencyId, SqlSession session);
}

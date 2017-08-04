package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Persistence methods definition for Package
 * @author Gustavo Claure, Marcelo Heredia
 */
public interface PackageDaoExtended extends PackageDaoBase {

    /**
     * Gets a RecordSet of Packages based on criteria
     * @param criteria The criteria to make the search operations
     * @param key The current user logged on.
     * @param session Session for the current user
     * @return  A RecordSet of Packages
     */
    RecordSet<Package> get(SearchCriteria criteria, OauthKey key, SqlSession session);

    /**
     * Gets a List of StandAlone Packages for a CampaignId and packages related placements for an ioId
     * @param campaignId
     * @param ioId
     * @param startIndex
     * @param pageSize
     * @param session Session for the current user
     * @return  A RecordSet of Packages
     */
    List<Package> getPackageByCampaignAndIoId(Long campaignId, Long ioId, Long startIndex,
                                              Long pageSize, SqlSession session);

    Long getCountPackageByCampaignAndIoId(Long campaignId, Long ioId, SqlSession session);

    /**
     *
     * @param id
     * @param session
     * @return
     */
    Package getExistActiveCampaign(Long id, SqlSession session);

    /**
     * Checks is a Package name exists below the current Campaign ({@code pkg#campaignId}
     * @param pkg The Package that contains the name and campaign to check
     * @param session The SqlSession where to execute the persistence query
     * @return true if the Package name exists. false otherwise.
     */
    boolean packageNameExists(Package pkg, SqlSession session);

    List<MediaRawDataView> getMediaPackageByPackageNames(List<String> names, Long campaignId,
                                                         SqlSession session);

    List<MediaRawDataView> getMediaPackagesByUserAndIds(List<Long> ids, Long campaignId,
                                                        String userId, SqlSession session);

    Package updateOnImport(Package pkg, SqlSession session);
}

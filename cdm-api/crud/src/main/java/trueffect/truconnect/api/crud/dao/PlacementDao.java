package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.PackagePlacementView;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Richard Jaldin, Gustavo Claure
 */
public interface PlacementDao extends GenericDao {

    Placement create(Placement placement, SqlSession session);

    Placement update(Placement placement, SqlSession session);

    Placement updateOnImport(Placement placement, SqlSession session);

    int updateDataCostDetailPlacementsByPackageId(Placement placement, Long packageId, SqlSession session);

    void delete(Long id, OauthKey key, SqlSession session);

    /**
     *
     * @param id
     * @param session
     * @return
     */
    Placement get(Long id, SqlSession session);

    /**
     *
     * @param criteria
     * @param key
     * @param session
     * @return
     * @throws java.lang.Exception
     */
    @Deprecated
    RecordSet<Placement> getPlacements(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception;

    /**
     * Retrieves all Placements associated to a given {@code packageId}
     * @param packageId The Package Id to use to get Placements
     * @param session The SQL session where to run this query
     * @return All Placements associates to given {@code packageId}
     */
    List<Placement> getPlacementsByPackageId(Long packageId, SqlSession session);

    /**
     * Retrieves all Placements associated to a given {@code ioId}
     * @param ioId The IO id to use to get Placements for
     * @param ioStatus The IO status (if any) to be used as a filter. If null is provided, then all
     *                 status will be considered in the results
     * @param session The SQL session where to run this query
     * @return All Placements associates to given {@code ioId}
     */
    List<Placement> getPlacementsByIoId(Long ioId, InsertionOrderStatusEnum ioStatus, SqlSession session);

    boolean checkPlacementsBelongsCampaignId(List<Long> ids, Long campaignId, SqlSession session);

    Long checkPlacementsStandAlone(List<Long> placementIds, Long packageId, SqlSession session);

    List<PackagePlacementView> getPlacementsForPackagePlacementView(Long id, String paramName, SqlSession session);

    List<PackagePlacementView> getPackagesForPackagePlacementView(Long id, String paramName, SqlSession session);

    List<Placement> getPlacementsByIds(Collection<Long> ids, SqlSession session);

    List<PlacementView> getPlacementsByFilterParam(Long campaignId,
                                                   CreativeInsertionFilterParam filterParam,
                                                   String userId, SqlSession session);

    Long getCountPlacementsByFilterParam(Long campaignId, CreativeInsertionFilterParam filterParam,
                                         SqlSession session);

    List<PlacementView> searchPlacementsViewByPattern(Long agencyId, Long advertiserId,
                                                      Long brandId, String pattern,
                                                      PlacementSearchOptions searchOptions,
                                                      Long startIndex, Long pageSize,
                                                      SqlSession session);

    Long searchCountPlacementsViewByPattern(Long agencyId, Long advertiserId, Long brandId,
                                            String pattern, PlacementSearchOptions searchOptions,
                                            SqlSession session);

    List<PlacementView> getPlacementsViewByLevelType(Long agencyId, Long advertiserId, Long brandId,
                                                     PlacementFilterParam filterParam,
                                                     PlacementFilterParamLevelTypeEnum levelType,
                                                     Long startIndex, Long pageSize,
                                                     SqlSession session);

    Long getCountPlacementsViewByLevelType(Long agencyId, Long advertiserId, Long brandId,
                                           PlacementFilterParam filterParam,
                                           PlacementFilterParamLevelTypeEnum levelType,
                                           SqlSession session);
    
    List<Placement> getBySiteSectionAndSize(Collection<String> names, String userId, Long campaignId, final SqlSession session);

    List<PlacementView> getPlacementsByBrand(Long brandId, SqlSession session);

    List<MediaRawDataView> getMediaForExport(Long campaignId, SqlSession session);

    Long getCountCampaignSiteOfPlacementsByIds(List<String> campaignSiteIds, SqlSession session);

    Long getCountCampaignSiteSectionOfPlacementsByIds(List<String> campaignSiteSectionIds,
                                                      SqlSession session);

    Long getCountCampaignSiteSectionPlacementOfPlacementsByIds(List<String> campaignSiteSectionPlacIds,
                                                               SqlSession session);

    List<MediaRawDataView> getMediaPlacementByPkgPlacIds(List<String> pkgPlacIds, Long campaignId,
                                                         SqlSession session);

    List<MediaRawDataView> getMediaPlacementByUserAndIds(List<Long> ids, Long campaignId,
                                                         String userId, SqlSession session);

    List<PlacementView> getPlacementsByTagId(Long tagId, Long startIndex, Long pageSize,
                                             SqlSession session);

    Long getCountPlacementsByTagId(Long tagId, SqlSession session);

    List<PlacementView> searchPlacementsAssociatedTagByPattern(Long tagId, String pattern,
                                                               PlacementSearchOptions searchOptions,
                                                               Long startIndex, Long pageSize,
                                                               SqlSession session);

    Long getCountSearchPlacementsAssociatedTagByPattern(Long tagId, String pattern,
                                                        PlacementSearchOptions searchOptions,
                                                        SqlSession session);
}

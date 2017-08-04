package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.HtmlInjectionTagAssociation;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @author Saulo Lopez
 */
public interface HtmlInjectionTagsDao extends GenericDao {

    HtmlInjectionTags getHtmlInjectionById(Long id, SqlSession session);

    RecordSet<HtmlInjectionTags> getHtmlInjectionTagsForAgency(Long agencyId, OauthKey key,
                                                               SqlSession session);

    List<HtmlInjectionTags> getHtmlInjectionTagsByPlacementId(Long campaignId, Long placementId,
                                                              Long startIndex, Long pageSize,
                                                              SqlSession session);

    Long getCountHtmlInjectionTagsByPlacementId(Long campaignId, Long placementId,
                                                SqlSession session);

    List<HtmlInjectionTagAssociation> getAssociations(Long agencyId,
                                                      PlacementFilterParam filterParam,
                                                      Long entityTypeId, Long entityId,
                                                      Long startIndex, Long pageSize,
                                                      SqlSession session);

    Long getCountAssociations(Long agencyId, PlacementFilterParam filterParam, Long entityTypeId,
                              Long entityId, SqlSession session);

    HtmlInjectionTags insertHtmlInjection(HtmlInjectionTags htmlInjectionTags, SqlSession session);

    HtmlInjectionTags updateHtmlInjection(HtmlInjectionTags htmlInjectionTags, SqlSession session);

    void createTagAssociations(Long agencyId, Long advertiserId, Long brandId,
                               PlacementActionTagAssocParam createParam, String tpwsKey,
                               SqlSession session);

    Integer bulkDeleteByIds(Long agencyId, List<Long> ids, String tpwsKey, SqlSession session);

    int deleteTagAssociations(Long agencyId, Long advertiserId, Long brandId,
                              PlacementActionTagAssocParam createParam,
                              String tpwsKey, SqlSession session);
}

package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.HtmlInjectionTagAssociation;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.crud.dao.HtmlInjectionTagsDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.SumAccumulatorImpl;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 * @author Saulo Lopez
 */
public class HtmlInjectionTagsDaoImpl extends AbstractGenericDao implements HtmlInjectionTagsDao {

    private static final String STATEMENT_GET_FOR_AGENCY = "HtmlInjectionTags.getHtmlInjectionTagsForAgency";
    private static final String STATEMENT_GET_HTML_INJECTION_BY_ID = "HtmlInjectionTags.getHtmlInjectionById";
    private static final String STATEMENT_GET_HTML_INJECTION_BY_PLACEMENT_ID = "HtmlInjectionTags.getTagsByPlacementId";
    private static final String STATEMENT_GET_COUNT_HTML_INJECTION_BY_PLACEMENT_ID = "HtmlInjectionTags.getTagsByPlacementIdCount";
    private static final String STATEMENT_INSERT_HTML_INJECTION = "HtmlInjectionTags.insertHtmlInjection";
    private static final String STATEMENT_UPDATE_HTML_INJECTION = "HtmlInjectionTags.updateHtmlInjection";
    private static final String STATEMENT_GET_ASSOCIATIONS_BY_PLACEMENT_FILTER_PARAM = "HtmlInjectionTags.getAssociationsByFilterParam";
    private static final String STATEMENT_GET_COUNT_ASSOCIATIONS_BY_PLACEMENT_FILTER_PARAM = "HtmlInjectionTags.getCountAssociationsByFilterParam";
    private static final String STATEMENT_CREATE_TAG_ASSOCIATIONS_BY_PARAMS = "HtmlInjectionTags.createNewTagAssociations";
    private static final String STATEMENT_BULK_DELETE_BY_IDS = "HtmlInjectionTags.bulkDeleteByIds";
    private static final String SEQUENCE_HTML_INJECTION = "SEQ_HTML_INJECTION";
    private static final String STATEMENT_DELETE_TAG_ASSOCIATIONS_BY_PARAMS = "HtmlInjectionTags.deleteTagAssociationsData";

    /**
     * Creates a new {@code GenericDaoImpl} with a specific {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this DAO
     */
    public HtmlInjectionTagsDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public HtmlInjectionTags getHtmlInjectionById(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_HTML_INJECTION_BY_ID, id, session,
                HtmlInjectionTags.class);
    }

    @Override
    public RecordSet<HtmlInjectionTags> getHtmlInjectionTagsForAgency(Long agencyId, OauthKey key,
                                                                      SqlSession session) {
        List<HtmlInjectionTags> result = getPersistenceContext()
                .selectMultiple(STATEMENT_GET_FOR_AGENCY, agencyId, session);
        return new RecordSet<>(result);
    }

    @Override
    public List<HtmlInjectionTags> getHtmlInjectionTagsByPlacementId(Long campaignId,
                                                                     Long placementId,
                                                                     Long startIndex, Long pageSize,
                                                                     SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("placementId", placementId);

        List<HtmlInjectionTags> result = getPersistenceContext()
                .selectMultiple(STATEMENT_GET_HTML_INJECTION_BY_PLACEMENT_ID, parameter,
                        new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
        return result;
    }

    @Override
    public Long getCountHtmlInjectionTagsByPlacementId(Long campaignId, Long placementId,
                                                       SqlSession session) {

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("placementId", placementId);

        return getPersistenceContext()
                .selectSingle(STATEMENT_GET_COUNT_HTML_INJECTION_BY_PLACEMENT_ID, parameter,
                        session, Long.class);
    }

    @Override
    public List<HtmlInjectionTagAssociation> getAssociations(Long agencyId,
                                                             PlacementFilterParam filterParam,
                                                             Long entityTypeId, Long entityId,
                                                             Long startIndex, Long pageSize,
                                                             SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("agencyId", agencyId);
        parameter.put("levelType", filterParam.getLevelType());
        parameter.put("campaignId", filterParam.getCampaignId());
        parameter.put("siteId", filterParam.getSiteId());
        parameter.put("sectionId", filterParam.getSectionId());
        parameter.put("placementId", filterParam.getPlacementId());
        parameter.put("entityTypeId", entityTypeId);
        parameter.put("entityId", entityId);

        List<HtmlInjectionTagAssociation> result = getPersistenceContext()
                .selectMultiple(STATEMENT_GET_ASSOCIATIONS_BY_PLACEMENT_FILTER_PARAM, parameter,
                        new RowBounds(startIndex.intValue(), pageSize.intValue()), session);
        return result;
    }

    @Override
    public Long getCountAssociations(Long agencyId, PlacementFilterParam filterParam,
                                     Long entityTypeId, Long entityId, SqlSession session) {

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("agencyId", agencyId);
        parameter.put("levelType", filterParam.getLevelType());
        parameter.put("campaignId", filterParam.getCampaignId());
        parameter.put("siteId", filterParam.getSiteId());
        parameter.put("sectionId", filterParam.getSectionId());
        parameter.put("placementId", filterParam.getPlacementId());
        parameter.put("entityTypeId", entityTypeId);
        parameter.put("entityId", entityId);

        return getPersistenceContext()
                .selectSingle(STATEMENT_GET_COUNT_ASSOCIATIONS_BY_PLACEMENT_FILTER_PARAM, parameter,
                        session, Long.class);
    }

    @Override
    public HtmlInjectionTags insertHtmlInjection(HtmlInjectionTags htmlInjectionTag, SqlSession session) {
        Long id = getNextId(SEQUENCE_HTML_INJECTION, session);
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("name", htmlInjectionTag.getName());
        parameter.put("htmlContent", htmlInjectionTag.getHtmlContent());
        parameter.put("isEnabled", htmlInjectionTag.getIsEnabled());
        parameter.put("createdDate", htmlInjectionTag.getCreatedDate());
        parameter.put("modifiedDate", htmlInjectionTag.getModifiedDate());
        parameter.put("createdTpwsKey", htmlInjectionTag.getCreatedTpwsKey());
        parameter.put("modifiedTpwsKey", htmlInjectionTag.getModifiedTpwsKey());
        parameter.put("agencyId", htmlInjectionTag.getAgencyId());
        parameter.put("isVisible", htmlInjectionTag.getIsVisible());
        parameter.put("secureHtmlContent", htmlInjectionTag.getSecureHtmlContent());

        getPersistenceContext().execute(STATEMENT_INSERT_HTML_INJECTION, parameter, session);
        HtmlInjectionTags result = getHtmlInjectionById(id, session);
        return result;
    }

    @Override
    public HtmlInjectionTags updateHtmlInjection(HtmlInjectionTags htmlInjectionTag,
                                                 SqlSession session) {
        HtmlInjectionTags result = null;
        HashMap<String, Object> parameter = new HashMap<>();

        parameter.put("id", htmlInjectionTag.getId());
        parameter.put("name", htmlInjectionTag.getName());
        parameter.put("htmlContent", htmlInjectionTag.getHtmlContent());
        parameter.put("modifiedTpwsKey", htmlInjectionTag.getModifiedTpwsKey());
        parameter.put("isVisible", htmlInjectionTag.getIsVisible());
        parameter.put("secureHtmlContent", htmlInjectionTag.getSecureHtmlContent());

        getPersistenceContext().execute(STATEMENT_UPDATE_HTML_INJECTION, parameter, session);
        return getHtmlInjectionById(htmlInjectionTag.getId(), session);
    }

    @Override
    public void createTagAssociations(Long agencyId, Long advertiserId, Long brandId,
                                      PlacementActionTagAssocParam createParam, String tpwsKey,
                                      SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("agencyId", agencyId);
        parameter.put("advertiserId", advertiserId);
        parameter.put("brandId", brandId);
        parameter.put("levelType", createParam.getLevelType());
        parameter.put("campaignId", createParam.getCampaignId());
        parameter.put("siteId", createParam.getSiteId());
        parameter.put("sectionId", createParam.getSectionId());
        parameter.put("placementId", createParam.getPlacementId());
        parameter.put("htmlInjectionId", createParam.getHtmlInjectionId());
        parameter.put("tpwsKey", tpwsKey);

        getPersistenceContext()
                .execute(STATEMENT_CREATE_TAG_ASSOCIATIONS_BY_PARAMS, parameter, session);
    }

    @Override
    public Integer bulkDeleteByIds(Long agencyId, List<Long> ids, String tpwsKey,
                                   final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tpwsKey", tpwsKey);
        params.put("agencyId", agencyId);
        int result = 0;
        Accumulator<Integer> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Integer>("ids", ids, resultAccumulator, params) {
            @Override
            public Integer execute(Object parameters) {
                return getPersistenceContext()
                        .update(STATEMENT_BULK_DELETE_BY_IDS,
                                parameters,
                                session);
            }
        }.getResults();
    }

    @Override
    public int deleteTagAssociations(Long agencyId, Long advertiserId, Long brandId,
                                     PlacementActionTagAssocParam createParam,
                                     String tpwsKey, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("agencyId", agencyId);
        parameter.put("advertiserId", advertiserId);
        parameter.put("brandId", brandId);
        parameter.put("levelType", createParam.getLevelType());
        parameter.put("campaignId", createParam.getCampaignId());
        parameter.put("siteId", createParam.getSiteId());
        parameter.put("sectionId", createParam.getSectionId());
        parameter.put("placementId", createParam.getPlacementId());
        parameter.put("htmlInjectionId", createParam.getHtmlInjectionId());
        parameter.put("tpwsKey", tpwsKey);
        return getPersistenceContext()
                .update(STATEMENT_DELETE_TAG_ASSOCIATIONS_BY_PARAMS, parameter, session);
    }
}

package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Richard Jaldin
 */
public interface SiteSectionDao extends GenericDao {

    SiteSection get(Long id, SqlSession session);

    RecordSet<SiteSection> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception;

    Long exists(SiteSection siteSection, SqlSession session);

    SiteSection create(SiteSection siteSection, SqlSession session);

    SiteSection update(SiteSection siteSection, SqlSession session);

    void remove(Long id, OauthKey key, SqlSession session);

    List<SiteSection> getByCampaignIdAndPublisherNameSiteName(Collection<String> names,
                                                              String userId, Long campaignId,
                                                              final SqlSession session);
}

package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.dto.SiteContactView;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public interface SiteDao extends GenericDao {

    Site get(Long id, SqlSession session);

    RecordSet<Site> getSites(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception ;

    Long exists(Site site, SqlSession session) throws Exception ;
    
    List<Site> checkSiteByName(String siteName , String userId, SqlSession session);
    
    void create(Site site, SqlSession session);

    void update(Site site, SqlSession session);
    
    void delete(Long id, OauthKey key, SqlSession session);
    
    List<SiteContactView> getTraffickingSiteContacts(Long campaignId, SqlSession session);

    List<Site> getByCampaignIdAndPublisherNameSiteName(Collection<String> names, String userId,
                                                       Long campaignId, final SqlSession session);
}

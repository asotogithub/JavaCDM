package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Rambert Rioja, Richard Jaldin
 */
public interface PublisherDao extends GenericDao {

    Publisher get(Long id, SqlSession session);

    RecordSet<Publisher> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception;

    Publisher getByAgencyId(Long id, Long agencyId, SqlSession session);

    Long exists(Publisher publisher, SqlSession session);

    Publisher create(Publisher publisher, SqlSession session);

    Publisher update(Publisher publisher, SqlSession session);

    void remove(Long id, Long agencyId, OauthKey key, SqlSession session);

    List<Publisher> getByCampaignIdAndName(Collection<String> names, String userId, Long campaignId,
                                           final SqlSession session);
}

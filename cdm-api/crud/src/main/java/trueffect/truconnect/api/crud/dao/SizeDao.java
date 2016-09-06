package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Richard Jaldin
 */
public interface SizeDao extends GenericDao {

    Size getById(Long id, SqlSession session);

    Size getByUserAndDimensions(Long height, Long width, OauthKey key, SqlSession session);

    RecordSet<Size> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws SearchApiException;

    Size create(Size size, SqlSession session);

    List<Size> getByCampaignIdAndWidthHeight(Collection<String> names, String userId,
                                             Long campaignId, final SqlSession session);
}

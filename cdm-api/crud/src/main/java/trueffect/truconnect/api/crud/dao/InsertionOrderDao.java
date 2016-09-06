package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.crud.mybatis.GenericDao;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 * Created by richard.jaldin on 7/15/2015.
 */
public interface InsertionOrderDao  extends GenericDao {

    InsertionOrder get(Long id, SqlSession session);

    RecordSet<InsertionOrder> get(SearchCriteria criteria, OauthKey key, SqlSession session) throws Exception;

    InsertionOrder getFirstIOByMediaBuy(Long mediaBuyId, SqlSession session);

    void create(InsertionOrder insertionOrder, SqlSession session);

    void update(InsertionOrder insertionOrder, SqlSession session);

    void remove(Long id, OauthKey key, SqlSession session);
    
    boolean hasActCampaings(Long id, SqlSession session);

    List<InsertionOrder> getIosByCampaignIdAndIoNumberName(Collection<String> names, String userId,
                                                           Long campaignId, SqlSession session);
}

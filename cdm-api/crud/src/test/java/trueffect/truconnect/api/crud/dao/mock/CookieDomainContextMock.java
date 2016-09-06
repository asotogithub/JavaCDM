package trueffect.truconnect.api.crud.dao.mock;

import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
/**
 *
 * Created by marcelo.heredia on 6/22/2015.
 */
public class CookieDomainContextMock  extends PersistenceContextMock {

    private Map<Long, CookieDomain> dataMap;

    public CookieDomainContextMock() {
        dataMap = new HashMap<Long, CookieDomain>();
    }

    @Override
    public Object selectOne(String xmlMethod, Object parameter, SqlSession session) throws Exception{
        if("CookieDomain.cookieDomainExists".equals(xmlMethod)){
            return findByName((String) parameter);
        }
        return dataMap.get(parameter);

    }

    @Override
    public void execute(String statement, Object parameter, SqlSession session) {
        Map map = (Map) parameter;
        if("CookieDomain.deleteCookieDomain".equalsIgnoreCase(statement)){
            dataMap.remove((Long)map.get("id"));
        }
        else{
            CookieDomain domain = new CookieDomain();
            domain.setId((Long) map.get("id"));
            domain.setAgencyId((Long) map.get("agencyId"));
            domain.setDomain((String) map.get("domain"));
            domain.setCreatedTpwsKey((String) map.get("tpwsKey"));
            dataMap.put(domain.getId(), domain);
        }
    }


    private CookieDomain findByName(String name){
        for(Map.Entry<Long, CookieDomain> entry : dataMap.entrySet()){
            if(entry.getValue().getDomain().equalsIgnoreCase(name)){
                return entry.getValue();
            }
        }
        return null;
    }

}

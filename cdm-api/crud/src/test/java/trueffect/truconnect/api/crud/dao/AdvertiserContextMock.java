package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMock;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marleny.patsi
 */
public class AdvertiserContextMock extends PersistenceContextMock{
    
    private Map<Long, Advertiser> advertisersMap;
    private Map<String, User> users;

    public AdvertiserContextMock() {
        this.users = new HashMap<>();
        this.advertisersMap = new HashMap<>();
    }
    
    @Override
    public void callPlSqlStoredProcedure(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception {
        HashMap<String,Object> param = parameterMap;
        Advertiser advertiser = new Advertiser();
        advertiser.setId((Long) param.get("id"));              
        advertiser.setAgencyId((Long) param.get("agencyId"));
        advertiser.setName((String) param.get("name"));
        advertiser.setAddress1((String) param.get("address1"));
        advertiser.setAddress2((String) param.get("address2"));
        advertiser.setCity((String) param.get("city"));
        advertiser.setState((String) param.get("state"));
        advertiser.setZipCode((String) param.get("zip"));
        advertiser.setCountry((String) param.get("country"));
        advertiser.setPhoneNumber((String) param.get("phone"));
        advertiser.setUrl((String) param.get("url"));
        advertiser.setFaxNumber((String) param.get("fax"));
        advertiser.setContactDefault((String) param.get("contactDefault"));
        advertiser.setNotes((String) param.get("notes"));
        advertiser.setEnableHtmlTag((Long) param.get("enableHtmlTag"));

        if ("Advertiser.saveAdvertiser".equalsIgnoreCase(xmlMethod)) {
            advertiser.setCreatedTpwsKey((String) param.get("createdTpwsKey"));
            advertisersMap.put(advertiser.getId(), advertiser);
        }else if("Advertiser.updateAdvertiser".equalsIgnoreCase(xmlMethod)){
            advertiser.setModifiedTpwsKey((String) param.get("modifiedTpwsKey"));
            advertisersMap.put(advertiser.getId(), advertiser);
        }else{
            super.callPlSqlStoredProcedure(xmlMethod, parameterMap, session);
        }        
    }

    @Override
    public <T> T selectOne(String xmlMethod, Object parameter, SqlSession session, Class<T> type) throws Exception {

        if ("Advertiser.getAdvertiser".equalsIgnoreCase(xmlMethod)) {
            Long id = (Long) parameter;
            Advertiser adv = advertisersMap.get(id);
            return type.cast(adv);
        } else if("Advertiser.advertiserNameExists".equalsIgnoreCase(xmlMethod)) {
            return type.cast(0L);
        }else { 
            return type.cast(session.selectOne(xmlMethod, parameter));
        }        
    }    

    @Override
    public Object selectOne(String xmlMethod, Object parameter, SqlSession session) throws Exception {
        if("Advertiser.advertiserNameExists".equalsIgnoreCase(xmlMethod)) {
            return 0L;
        }else{
            return session.selectOne(xmlMethod, parameter);
        }
    }
    
    @Override
    public List<?> selectMultiple(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) {
        List<Advertiser> advList = new ArrayList<>();
        advList.addAll(getAdvertisersMap().values()); 
        if ("Advertiser.getAdvertisersByUserId".equalsIgnoreCase(xmlMethod)) {
            HashMap<String, Object> param = (HashMap<String, Object>) parameter;
            String userId = (String) param.get("userId");
            
            List<Advertiser> listResult = new ArrayList<>();
            User us = this.users.get(userId);
            for (Advertiser adv : advList) {
                if (adv.getAgencyId().equals(us.getAgencyId()) ) {
                    listResult.add(adv);
                }
            }
            return (listResult);
        } else {
            return session.selectList(xmlMethod, parameter, limits);
        }
    }

    public void setUser(User user){
        this.users.put(user.getUserName(), user);
        
    }
    
    public User getUser(String userName){
        return this.users.get(userName);
    }
    /**
     * @return the advertisers
     */
    public Map<Long, Advertiser> getAdvertisersMap() {
        return advertisersMap;
    }

    /**
     * @param advertisersMap the advertisers to set
     */
    public void setAdvertisersMap(Map<Long, Advertiser> advertisersMap) {
        this.advertisersMap = advertisersMap;
    }

    /**
     * @return the users
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(Map<String, User> users) {
        this.users = users;
    }
    
}

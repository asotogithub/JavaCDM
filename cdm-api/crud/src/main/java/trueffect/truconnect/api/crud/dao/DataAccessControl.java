package trueffect.truconnect.api.crud.dao;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;

import org.apache.ibatis.session.SqlSession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Rambert Rioja
 */
public class DataAccessControl {

    public static boolean isAdminUser(String userId, SqlSession session) throws Exception {
        String roleAdmin = (String) MyBatisUtil.selectOne(
                "DataAccessControlPkg.isAdminUser", userId, session);
        return roleAdmin != null && roleAdmin.equals("Y");
        
    }

    @SuppressWarnings("unchecked")
    public static boolean isUserValidForCreativeGroup(List<Long> ids, String userId, SqlSession session) throws Exception {
        List<Long> agencyIds = (List<Long>) MyBatisUtil.selectList(
                "DataAccessControlPkg.getCreativeGroupsByUser",
                parameters(ids, userId), session);
        if (agencyIds.isEmpty()) {
            throw new DataNotFoundForUserException("CreativeGroupId: " + ids.toString() + DataNotFoundForUserException.NOT_FOUND_MESSAGE + userId);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static boolean isUserValidForCampaign(List<Long> ids, String userId, SqlSession session) throws Exception {
        String agencyIds = (String) MyBatisUtil.selectOne(
                "DataAccessControlPkg.getCampaignsByUser",
                parameters(ids, userId), session);
        if (Boolean.valueOf(agencyIds) == false) {
            throw new DataNotFoundForUserException("CampaignId: " +ids.toString() + DataNotFoundForUserException.NOT_FOUND_MESSAGE + userId);
        }
        return true;
    }

    private static HashMap<String, Object> parameters(List<Long> ids, String userId) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ids", ids);
        parameters.put("userId", userId);
        return parameters;
    }

    @SuppressWarnings("unchecked")
    public static boolean isUserValidForAgency(List<Long> ids, String userId, SqlSession session) throws Exception {
        List<Long> agencyIds = (List<Long>) MyBatisUtil.selectList(
                "DataAccessControlPkg.getAgenciesByUser",
                parameters(ids, userId), session);
        if (agencyIds.isEmpty()) {
            throw new DataNotFoundForUserException("AgencyId: "+ ids.toString() + DataNotFoundForUserException.NOT_FOUND_MESSAGE + userId);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static boolean isUserValidForMediaBuy(List<Long> ids, String userId,
            SqlSession session) throws Exception {
        List<Long> agencyIds = (List<Long>) MyBatisUtil.selectList(
                "DataAccessControlPkg.getMediaBuysByUser",
                parameters(ids, userId), session);
        if (agencyIds.isEmpty()) {
            throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + userId);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static boolean isUserValidForCreativeInsertion(List<Long> ids, String userId, SqlSession session) throws Exception {
        List<Long> agencyIds = (List<Long>) MyBatisUtil.selectList(
                "DataAccessControlPkg.getCreativeInsertionsByUser",
                parameters(ids, userId), session);
        if (agencyIds.isEmpty()) {
            throw new DataNotFoundForUserException("CreativeInsertionId: " + ids.toString() + DataNotFoundForUserException.NOT_FOUND_MESSAGE + userId);
        }
        return true;
    }

}

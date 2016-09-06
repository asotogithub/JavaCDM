package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.UserAdvertiser;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.UserAdvertiserDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author abel.soto
 */
public class UserAdvertiserManager extends AbstractGenericManager{
    
    private UserAdvertiserDao userAdvertiserDao;


    public UserAdvertiserManager(UserAdvertiserDao userAdvertiserDao, AccessControl accessControl) {
        super(accessControl);
        this.userAdvertiserDao = userAdvertiserDao;
    }

    /**
     * Updates the relationship between a {@code User} and an {@code Advertiser}.
     * @param userId The user id to relate to the provided {@advertiserId}s
     * @param advertisers A {@code RecodSet} that includes the list of {@codeUserAdvertiser} elements
     * @param key The {@code OAuth} key
     * @return The list of updated {@codeUserAdvertiser} elements
     */
    public Either<Error, RecordSet<UserAdvertiser>> updateUserAdvertisers(String userId,
                                                                          RecordSet<UserAdvertiser> advertisers,
                                                                          OauthKey key) {
        if (userId == null) {
            return Either.error(
                    new trueffect.truconnect.api.commons.exceptions.business.Error(
                            ResourceBundleUtil.getString("global.error.null", "User ID"),
                            ValidationCode.REQUIRED));
        }
        if (advertisers == null || advertisers.getRecords() == null || advertisers.getRecords().isEmpty()) {
            return Either.error(
                    new trueffect.truconnect.api.commons.exceptions.business.Error(
                            ResourceBundleUtil.getString("global.error.null", "Advertiser list"),
                            ValidationCode.REQUIRED));
        }
        if (key == null) {
            return Either.error(
                    new trueffect.truconnect.api.commons.exceptions.business.Error(
                            ResourceBundleUtil.getString("error.oauth.null"),
                            ValidationCode.REQUIRED));
        }

        List<Long> ids = new ArrayList<>();
        for (UserAdvertiser userAdvertiser : advertisers.getRecords()) {
            ids.add(userAdvertiser.getAdvertiserId());
            // check if userId matches with domain.userId
            if (StringUtils.isNotEmpty(userId) && StringUtils
                    .isNotEmpty(userAdvertiser.getUserId()) && !userId
                    .equals(userAdvertiser.getUserId())) {
                return Either.error(
                        new Error(ResourceBundleUtil.getString("BusinessCode.ENTITY_ID_MISMATCH"),
                                BusinessCode.ENTITY_ID_MISMATCH));
            }
        }

        SqlSession session = userAdvertiserDao.openSession();
        try {
            // Check access control
            super.isAdminUser(key.getUserId(), session);
            if (!userValidFor(AccessStatement.ADVERTISER, ids, key.getUserId(), session)) {
                return Either.error(
                        new Error(ResourceBundleUtil.getString("SecurityCode.ILLEGAL_USER_CONTEXT"),
                                SecurityCode.ILLEGAL_USER_CONTEXT));
            }

            userAdvertiserDao.cleanUserAdvReference(userId, session);
            //Set data
            for (UserAdvertiser userAdv : advertisers.getRecords()) {
                userAdvertiserDao.addUserAdvReference(userId, userAdv.getAdvertiserId(), session);
            }
            userAdvertiserDao.commit(session);
        } catch (Exception e) {
            userAdvertiserDao.rollback(session);
            return Either.error(new Error(e.getMessage(), BusinessCode.INTERNAL_ERROR));
        } finally {
            userAdvertiserDao.close(session);
        }
        return Either.success(advertisers);
    }
}

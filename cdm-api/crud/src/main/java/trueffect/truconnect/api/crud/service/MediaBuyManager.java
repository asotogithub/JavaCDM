package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.MediaBuyCampaign;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.enums.GeneralStatusEnum;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.MediaBuyDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.Collections;

/**
 *
 * @author marleny.patsi
 */
public class MediaBuyManager extends AbstractGenericManager {

    private MediaBuyDao mediaBuyDao;

    public MediaBuyManager(MediaBuyDao mediaBuyDao, AccessControl accessControl) {
        super(accessControl);
        this.mediaBuyDao = mediaBuyDao;
    }

    public MediaBuy get(Long id, OauthKey key) throws Exception {
        //null validations
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Media Buy Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        // Obtain session
        SqlSession session = mediaBuyDao.openSession();
        MediaBuy result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.MEDIA_BUY, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                        "dataAccessControl.dataNotFoundForUser", key.getUserId()));
            }
            // call dao
            result = mediaBuyDao.get(id, session);
        } finally {
            mediaBuyDao.close(session);
        }
        return result;
    }

    public MediaBuy getByCampaignId(Long campaignId, OauthKey key) throws Exception {
        //null validations
        if (campaignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Campaign Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        // Obtain session
        SqlSession session = mediaBuyDao.openSession();
        MediaBuy result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                        "dataAccessControl.notFoundForUser", "CampaignId", Long.toString(campaignId), key.getUserId()));
                //throw new DataNotFoundForUserException("CampaignId: " +ids.toString() + DataNotFoundForUserException.NOT_FOUND_MESSAGE + userId);
            }
            result = mediaBuyDao.getByCampaign(campaignId, session);
            if (result == null) {
                throw new NotFoundException("Record not found."); //TODO: Review this error message
            }
        } finally {
            mediaBuyDao.close(session);
        }
        return result;
    }

    public MediaBuy create(MediaBuy mediaBuy, OauthKey key) throws Exception {

        // null validations
        if (mediaBuy == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "MediaBuy"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        // Perform Model Validations
        if (mediaBuy.getId() != null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.postWithId", "Media Buy"));
        }
        if (StringUtils.isBlank(mediaBuy.getName())) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.empty", "Media Buy Name"));
        } else if (mediaBuy.getName().length() > Constants.DEFAULT_CHARS_LENGTH) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength", 
                    "Media Buy Name", Constants.DEFAULT_CHARS_LENGTH));
        }
        if (mediaBuy.getState() == null) {
            mediaBuy.setState(GeneralStatusEnum.NEW.getStatusCode());
        } else if (GeneralStatusEnum.fromStateCode(mediaBuy.getState()) == null) {
            throw new ValidationException(ResourceBundleUtil.getString("mediaBuy.error.state"));
        }
        // Obtain session
        SqlSession session = mediaBuyDao.openSession();
        MediaBuy result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.AGENCY, Collections.singletonList(mediaBuy.getAgencyId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                        "dataAccessControl.notFoundForUser", "AgencyId", Long.toString(mediaBuy.getAgencyId()), key.getUserId()));
                //throw new DataNotFoundForUserException("AgencyId: "+ ids.toString() + DataNotFoundForUserException.NOT_FOUND_MESSAGE + userId);
            }

            // set values
            mediaBuy.setCreatedTpwsKey(key.getTpws());
            result = mediaBuyDao.create(mediaBuy, session);
        } catch (Exception e) {
            log.warn("Could not create new Media Buy", e);
            mediaBuyDao.rollback(session);
            throw e;
        } finally {
            mediaBuyDao.close(session);
        }
        return result;
    }

    public MediaBuyCampaign createMediaBuyCampaign(MediaBuyCampaign mediaBuyCampaign, OauthKey key) throws Exception {
        // null validations
        if (mediaBuyCampaign == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "MediaBuyCampaign"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }

        // Perform Model Validations
        if (mediaBuyCampaign.getMediaBuyId() == null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.empty", "Media Buy Id"));
        }
        if (mediaBuyCampaign.getCampaignId() == null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.empty", "Campaign Id"));
        }

        // Obtain session
        SqlSession session = mediaBuyDao.openSession();
        MediaBuyCampaign result = null;
        try {
            //check access control
            if (!userValidFor(AccessStatement.MEDIA_BUY, Collections.singletonList(mediaBuyCampaign.getMediaBuyId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                        "dataAccessControl.dataNotFoundForUser", key.getUserId()));
            }
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(mediaBuyCampaign.getCampaignId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString(
                        "dataAccessControl.notFoundForUser", "CampaignId", Long.toString(mediaBuyCampaign.getCampaignId()), key.getUserId()));
                //throw new DataNotFoundForUserException("CampaignId: " +ids.toString() + DataNotFoundForUserException.NOT_FOUND_MESSAGE + userId);
            }

            // set values
            mediaBuyCampaign.setModifiedTpwsKey(key.getTpws());
            mediaBuyCampaign.setCreatedTpwsKey(key.getTpws());

            result = mediaBuyDao.createMediaBuyCampaign(mediaBuyCampaign, session);
        } catch (Exception e) {
            log.warn("Could not create new Media Buy", e);
            mediaBuyDao.rollback(session);
            throw e;
        } finally {
            mediaBuyDao.close(session);
        }
        return result;
    }
}

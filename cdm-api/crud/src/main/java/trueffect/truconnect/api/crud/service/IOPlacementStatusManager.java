package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.InsertionOrderStatus;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.PlacementStatus;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.validation.PlacementValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Date;

/**
 *
 * @author marleny.patsi
 */
public class IOPlacementStatusManager extends AbstractGenericManager {

    private PlacementStatusDao placementStatusDao;
    private InsertionOrderStatusDao insertionOrderStatusDao;
    private UserDao userDao;
    private PlacementValidator validator;
    private CreativeInsertionDao creativeInsertionDao;

    public IOPlacementStatusManager(PlacementStatusDao placementStatusDao,
                                    InsertionOrderStatusDao insertionOrderStatusDao,
                                    UserDao userDao,
                                    CreativeInsertionDao creativeInsertionDao,
                                    AccessControl accessControl) {
        super(accessControl);
        this.placementStatusDao = placementStatusDao;
        this.insertionOrderStatusDao = insertionOrderStatusDao;
        this.userDao = userDao;
        this.creativeInsertionDao = creativeInsertionDao;
        this.validator = new PlacementValidator();
    }

    public void createIOStatus(Long ioId, String status, OauthKey key, SqlSession session) {
        InsertionOrderStatusEnum statusEnum = InsertionOrderStatusEnum.fromName(status);
        if (statusEnum == null) {
            statusEnum = InsertionOrderStatusEnum.NEW;
        }
        User user = userDao.get(key.getUserId(), session);
        this.createNewIOStatus(ioId, statusEnum.getCode(), key, user.getContactId(), session);
    }

    /**
     *
     * @param ioId
     * @param newStatus
     * @param session
     * @param oldStatus
     * @param key
     */
    public void updateIOStatus(Long ioId, String newStatus, String oldStatus, OauthKey key, SqlSession session) {
        //validates only if the status is changed
        if (newStatus != null && !oldStatus.equals(newStatus)) {
            if (newStatus.equals(InsertionOrderStatusEnum.NEW.getName())) {
                throw BusinessValidationUtil.buildBusinessSystemException(BusinessCode.INVALID, "status");
            }
            User user = userDao.get(key.getUserId(), session);
            Long statusId = InsertionOrderStatusEnum.fromName(newStatus).getCode();
            this.createNewIOStatus(ioId, statusId, key, user.getContactId(), session);
        }
    }

    public void createPlacementStatus(Long placementId, String status, Long userContactId, OauthKey key, SqlSession session) {
        // Nullability checks
        if (placementId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Placement Id"));
        }
        if (status == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Status"));
        }
        if (userContactId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "User Contact Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }

        InsertionOrderStatusEnum statusEnum = InsertionOrderStatusEnum.fromName(status);
        if (statusEnum == null) {
            statusEnum = InsertionOrderStatusEnum.NEW;
        }
        this.createNewPlacementStatus(placementId, statusEnum.getCode(), userContactId, key, session);
    }

    /**
     *
     * @param campaignId
     * @param placementId
     * @param newStatus
     * @param oldStatus
     * @param userContactId
     * @param session
     * @param key
     */
    public void updatePlacementStatus(Long campaignId, Long placementId, String newStatus, String oldStatus, Long userContactId, OauthKey key, SqlSession session) {

        //validates only if the status is changed
        if (newStatus != null && !oldStatus.equals(newStatus)) {
            Placement existentPlacement = new Placement();
            existentPlacement.setStatus(oldStatus);
            Placement placement = new Placement();
            placement.setStatus(newStatus);

            String className = placement.getClass().getSimpleName();
            BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(placement, className);
            validator.validateChangeStatus(placement, existentPlacement, validationResult);
            if (validationResult.hasErrors()) {
                throw BusinessValidationUtil.buildSpringValidationSystemException(ValidationCode.INVALID, validationResult);
            }
            // If Placement status is being set to "Rejected(Disabled)"; then, delete all Schedules associated
            if(InsertionOrderStatusEnum.fromName(newStatus) == InsertionOrderStatusEnum.REJECTED) {
                CreativeInsertionFilterParam filter = new CreativeInsertionFilterParam();
                filter.setPlacementId(placementId);
                creativeInsertionDao.bulkDeleteByFilterParam(campaignId, filter, key.getTpws(), session);
            }
            Long statusId = InsertionOrderStatusEnum.fromName(placement.getStatus()).getCode();
            this.createNewPlacementStatus(placementId, statusId, userContactId, key, session);
        }
    }

    /**
     *
     * @param placementId
     * @param key
     * @param statusId
     * @param session
     */
    private void createNewPlacementStatus(Long placementId, Long statusId, Long userContactId, OauthKey key, SqlSession session) {
        PlacementStatus placementStatus = new PlacementStatus();
        placementStatus.setPlacementId(placementId);
        placementStatus.setCreatedTpwsKey(key.getTpws());
        placementStatus.setStatusDate(new Date());
        placementStatus.setStatusId(statusId);
        placementStatus.setContactId(userContactId);
        log.debug("Placement's status: {}", placementStatus);
        placementStatusDao.create(placementStatus, session);
    }

    private void createNewIOStatus(Long ioId, Long statusId, OauthKey key, Long contactId, SqlSession session) {
        InsertionOrderStatus ioStatus = new InsertionOrderStatus();
        ioStatus.setIoId(ioId);
        ioStatus.setCreatedTpwsKey(key.getTpws());
        ioStatus.setStatusDate(new Date());
        ioStatus.setStatusId(statusId);
        ioStatus.setContactId(contactId);
        log.debug("Insertion Order's status: {}", ioStatus);
        insertionOrderStatusDao.create(ioStatus, session);
    }
}

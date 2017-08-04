package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.validation.InsertionOrderValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Richard Jaldin
 */
public class InsertionOrderManager extends AbstractGenericManager {

    public static final String REGEX_MATCH_LEADING_ZEROES = "^0+(?!$)";
    private InsertionOrderDao insertionOrderDao;
    private PlacementDao placementDao;
    private UserDao userDao;
    private IOPlacementStatusManager ioPlacementStatusManager;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private InsertionOrderValidator validator;

    private static final SimpleDateFormat CREATION_DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss '('z')'");

    public InsertionOrderManager(InsertionOrderDao insertionOrderDao, InsertionOrderStatusDao insertionOrderStatusDao, 
            UserDao userDao, PlacementDao placementDao, PlacementStatusDao placementStatusDao,
                                 CreativeInsertionDao creativeInsertionDao, AccessControl accessControl) {
        super(accessControl);
        this.insertionOrderDao = insertionOrderDao;
        this.placementDao = placementDao;
        this.userDao = userDao;
        this.ioPlacementStatusManager = new IOPlacementStatusManager(placementStatusDao, insertionOrderStatusDao, userDao,
                creativeInsertionDao, accessControl);
        this.validator = new InsertionOrderValidator();
    }

    /**
     * Returns the InsertionOrder based on the ID
     *
     * @param id Insertion order ID number and primary key
     * @param key
     * @return the InsertionOrder of the id
     * @throws Exception
     */
    public InsertionOrder get(Long id, OauthKey key) throws Exception {
        //validations
        // Nullability checks   
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Id"));
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        //Session
        SqlSession session = insertionOrderDao.openSession();
        InsertionOrder result;
        try {
            // Check access control
            if (!userValidFor(AccessStatement.INSERTION_ORDER, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.dataNotFoundForUser", key.getUserId()));
            }
            result = insertionOrderDao.get(id, session);
        } finally {
            insertionOrderDao.close(session);
        }
        return result;
    }

    /**
     * Returns the InsertionOrders that matches with search criteria
     *
     * @param searchCriteria search criteria object
     * @param key
     * @return RecordSet of InsertionOrders
     * @throws Exception
     */
    public RecordSet<InsertionOrder> get(SearchCriteria searchCriteria, OauthKey key) throws Exception {
        //validations
        // Nullability checks   
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        //Session        
        SqlSession session = insertionOrderDao.openSession();
        RecordSet<InsertionOrder> result;
        try {
            result = insertionOrderDao.get(searchCriteria, key, session);
        } finally {
            insertionOrderDao.close(session);
        }
        return result;
    }

    /**
     * Returns the InsertionOrder based on the MediaBuy ID
     *
     * @param mediaBuyId ID number of the MediaBuy
     * @param key
     * @return the InsertionOrder based on the ID
     * @throws Exception
     */
    public InsertionOrder getByMediaBuy(Long mediaBuyId, OauthKey key) throws Exception {
        //validations
        // Nullability checks 
        if (mediaBuyId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Media Buy Id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        SqlSession session = insertionOrderDao.openSession();
        InsertionOrder result;
        try {
            //Check access control
            if (!userValidFor(AccessStatement.MEDIA_BUY, Collections.singletonList(mediaBuyId), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser",
                        "MediaBuyId", Long.toString(mediaBuyId), key.getUserId()));
            }
            result = this.insertionOrderDao.getFirstIOByMediaBuy(mediaBuyId, session);
            if (result == null) {
                throw new NotFoundException("Record not found.");
            }
        } finally {
            insertionOrderDao.close(session);
        }
        return result;
    }
    
    /**
     * Creates an Insertion Order
     *
     * @param insertionOrder Insertion Order object to be saved
     * @param key
     * @return the new InsertionOrder saved on the data base
     * @throws Exception
     */
    public InsertionOrder create(InsertionOrder insertionOrder, OauthKey key) throws Exception {
        //validations
        // Nullability checks
        if (insertionOrder == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.insertionOrder")));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(insertionOrder,
                insertionOrder.getClass().getSimpleName());
        ValidationUtils.invokeValidator(validator, insertionOrder, errors);
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        //Session
        SqlSession session = insertionOrderDao.openSession();
        InsertionOrder result;
        try {
            //check access control
            if (!userValidFor(AccessStatement.MEDIA_BUY, Collections.singletonList(insertionOrder.getMediaBuyId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.dataNotFoundForUser", key.getUserId()));
            }
            if (insertionOrder.getPublisherId() != null) {
                if (!userValidFor(AccessStatement.PUBLISHER, Collections.singletonList(insertionOrder.getPublisherId()), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.dataNotFoundForUser", key.getUserId()));
                }
            }

            // set default values and persist IO:
            result = create(insertionOrder, session, key);

            insertionOrderDao.commit(session);
        } catch (Exception e) {
            insertionOrderDao.rollback(session);
            throw e;
        } finally {
            insertionOrderDao.close(session);
        }
        log.info(key.toString() + " Saved " + insertionOrder);
        return result;
    }

    /**
     * Creates a new Insertion Order with already validated data.
     *
     * @param insertionOrder Insertion Order object to be created
     * @param session
     * @param key
     * @return the new InsertionOrder saved on the data base
     */
    public InsertionOrder create(InsertionOrder insertionOrder, SqlSession session, OauthKey key) {
        //validations
        // Nullability checks
        if (insertionOrder == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null",
                    ResourceBundleUtil.getString("global.label.insertionOrder")));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Session"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        //set values
        Long ioId = insertionOrderDao.getNextId(session);
        insertionOrder.setId(ioId);
        insertionOrder.setCreatedTpwsKey(key.getTpws());

        Integer ioNumber = insertionOrder.getIoNumber() != null? insertionOrder.getIoNumber() : insertionOrder.getId().intValue();
        insertionOrder.setIoNumber(ioNumber);

        if (insertionOrder.getNotes() == null || insertionOrder.getName().isEmpty()) {
            insertionOrder.setNotes(ResourceBundleUtil.getString("insertionOrder.info.notes", key.getUserId(), CREATION_DATE_FORMATTER.format(new Date())));
        }
        //call dao to create IO
        this.insertionOrderDao.create(insertionOrder, session);

        //saving insertion order status
        this.ioPlacementStatusManager.createIOStatus(insertionOrder.getId(), insertionOrder.getStatus(), key, session);

        return this.insertionOrderDao.get(ioId, session);
    }

    /**
     * Updates an Insertion Order
     *
     * @param id Insertion order ID number and primary key
     * @param insertionOrder Insertion Order object to be saved
     * @param key
     *
     * @return InsertionOrder updated
     * @throws Exception
     */
    public InsertionOrder update(Long id, InsertionOrder insertionOrder, OauthKey key) throws Exception {
        //validations
        // Nullability checks 
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        
        if (insertionOrder.getId() == null) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.putWithoutId", "Insertion Order"));
        }
        if (id.compareTo(insertionOrder.getId()) != 0) {
            throw new ValidationException(
                    "Identifier in URL does not match resource in request body.");
        }
        if (!StringUtils.isBlank(insertionOrder.getName())
                && insertionOrder.getName().length() > Constants.MAX_INSERTION_ORDER_NAME_LENGHT) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Insertion Order name", String.valueOf(
                            Constants.MAX_INSERTION_ORDER_NAME_LENGHT)));
        }
        
        if (insertionOrder.getIoNumber() != null
                && insertionOrder.getIoNumber() < Constants.INSERTION_ORDER_NUMBER_MIN_VALUE) {
            throw new ValidationException(
                    ResourceBundleUtil.getString("global.error.outOfRange",
                    ResourceBundleUtil.getString("insertionOrder.label.ioNumber"),
                    String.valueOf(Constants.INSERTION_ORDER_NUMBER_MIN_VALUE),
                    String.valueOf(Constants.INSERTION_ORDER_NUMBER_MAX_VALUE)));
        }
        
        if (!StringUtils.isBlank(insertionOrder.getNotes())
                && insertionOrder.getNotes().length() > Constants.MAX_INSERTION_ORDER_NOTES_LENGHT) {
            throw new ValidationException(ResourceBundleUtil.getString("global.error.invalidStringLength",
                    "Insertion Order notes", String.valueOf(
                            Constants.MAX_INSERTION_ORDER_NOTES_LENGHT)));
        }

        //Session
        SqlSession session = insertionOrderDao.openSession();
        InsertionOrder result;
        try {
            //check access control
            if (!userValidFor(AccessStatement.INSERTION_ORDER, Collections.singletonList(insertionOrder.getId()), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
            }
            if (insertionOrder.getPublisherId() != null) {
                if (!userValidFor(AccessStatement.PUBLISHER, Collections.singletonList(insertionOrder.getPublisherId()), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
                }
            }
            //validations
            InsertionOrder existentIO = insertionOrderDao.get(insertionOrder.getId(), session);
            if (existentIO == null) {
                throw new ValidationException("Can't update record if it doesn't exist");
            }
            
            //validates only if the status is changed
            if (insertionOrder.getStatus() != null && !existentIO.getStatus().equals(insertionOrder.getStatus())) {
                if (insertionOrder.getStatus().equals(InsertionOrderStatusEnum.NEW.getName())) {
                    String message = "The Insertion Order's status cannot be changed to " + insertionOrder.getStatus()
                        + " as it is currently in status " + existentIO.getStatus() ;
                    throw new ValidationException(message);
                }
            }

            if (StringUtils.isBlank(insertionOrder.getName())) {
                insertionOrder.setName(existentIO.getName());
            }
            
            //set values
            Integer ioNumber = (insertionOrder.getIoNumber() != null)
                ? insertionOrder.getIoNumber() : insertionOrder.getId().intValue();
            insertionOrder.setIoNumber(ioNumber);
            insertionOrder.setModifiedTpwsKey(key.getTpws());
            
            //call dao to update IO
            this.insertionOrderDao.update(insertionOrder, session);
            
            //updating IO status
            if (insertionOrder.getStatus() != null) {
                this.ioPlacementStatusManager.updateIOStatus(insertionOrder.getId(), insertionOrder.getStatus(), existentIO.getStatus(),
                        key, session);
                //update all placements status
                SearchCriteria searchCriteria = new SearchCriteria();
                searchCriteria.setQuery("ioId equals to " + insertionOrder.getId() + " and status not equals to \""+insertionOrder.getStatus()+"\"");
                RecordSet<Placement> placementsResult = placementDao.getPlacements(searchCriteria, key, session);
                List<Placement> placementsList = placementsResult.getRecords();
                
                // get Contact User
                User user = userDao.get(key.getUserId(), session);
                for (Placement placementToUpdate : placementsList) {
                    this.ioPlacementStatusManager.updatePlacementStatus(existentIO.getCampaignId(),
                            placementToUpdate.getId(),
                            insertionOrder.getStatus(),
                            placementToUpdate.getStatus(),
                            user.getContactId(),
                            key,
                            session);
                }
            }
            
            //retrieve recently created IO
            result = this.insertionOrderDao.get(insertionOrder.getId(), session);            
            insertionOrderDao.commit(session);
        } catch (Exception e) {
            insertionOrderDao.rollback(session);
            throw e;
        } finally {
            insertionOrderDao.close(session);
        }
        log.info(key.toString() + " Updated " + id);
        return result;
    }

    /**
     * Removes a InsertionOrder
     *
     * @param id Insertion order ID number and primary key
     * @param key Session ID of the user who updates the Placement.
     * @return InsertionOrder that has been deleted
     * @throws Exception
     */
    public SuccessResponse remove(Long id, OauthKey key) throws Exception {
        //validations
        // Nullability checks 
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        
        //Session
        SqlSession session = insertionOrderDao.openSession();
        try {
            // Check access control
            if (!userValidFor(AccessStatement.INSERTION_ORDER, Collections.singletonList(id), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "InsertionOrderId", Long.toString(id), key.getUserId()));
            }
            if (insertionOrderDao.hasActCampaings(id, session)) {
                throw new SQLException("Unable to delete InsertionOrder with id: "
                        + id + ", InsertionOrder has active Campaigns");
            }
            //call dao
            insertionOrderDao.remove(id, key, session);
            insertionOrderDao.commit(session);
        } catch (PersistenceException e) {
            insertionOrderDao.rollback(session);
            throw e;
        } finally {
            insertionOrderDao.close(session);
        }
        log.info(key.toString() + " Deleted " + id);
        return new SuccessResponse("Insertion Order " + id + " successfully deleted");
    }
}
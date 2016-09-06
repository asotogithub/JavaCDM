package trueffect.truconnect.api.crud.service;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.InsertionOrderStatus;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.PlacementStatusDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.CreativeInsertionDaoImpl;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Collections;

/**
 *
 * Created by richard.jaldin on 7/16/2015.
 */
public class InsertionOrderManagerTest extends AbstractManagerTest {

    private InsertionOrderManager ioManager;
    private InsertionOrderDao insertionOrderDao;
    private UserDao userDao;
    private InsertionOrderStatusDao insertionOrderStatusDao;
    private PlacementDao placementDao;
    private PlacementStatusDao placementStatusDao;
    private InsertionOrder io;

    @Before
    public void setUp() {
        // mock daos
        insertionOrderDao = mock(InsertionOrderDao.class);
        userDao = mock(UserDao.class);
        insertionOrderStatusDao = mock(InsertionOrderStatusDao.class);
        placementDao = mock(PlacementDao.class);
        placementStatusDao = mock(PlacementStatusDao.class);
        CreativeInsertionDao creativeInsertionDao = mock(CreativeInsertionDaoImpl.class);

        ioManager = new InsertionOrderManager(insertionOrderDao, insertionOrderStatusDao,
                userDao, placementDao, placementStatusDao, creativeInsertionDao, accessControlMockito);

        //variables
        User user = EntityFactory.createUser();
        user.setUserName(key.getUserId());
        io = EntityFactory.createInsertionOrder();
        io.setId(Math.abs(EntityFactory.random.nextLong()));

        // Mock Session
        when(insertionOrderDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(insertionOrderDao).close(sqlSessionMock);

        // Mocks common behavior
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.MEDIA_BUY, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.PUBLISHER, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.INSERTION_ORDER, sqlSessionMock);

        when(insertionOrderDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(io);

        when(insertionOrderDao.getNextId(eq(sqlSessionMock))).thenReturn(
                Math.abs((long) EntityFactory.random.nextInt()));
        doAnswer(create(io)).when(insertionOrderDao).create(eq(io), eq(sqlSessionMock));
        when(userDao.get(anyString(), eq(sqlSessionMock))).thenReturn(user);
        doNothing().when(insertionOrderStatusDao).create(any(InsertionOrderStatus.class), eq(sqlSessionMock));
    }

    @Test
    public void getByIdOk() throws Exception {
        //Perform test
        InsertionOrder result = ioManager.get(io.getId(), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(equalTo(io.getId())));
    }

    @Test
    public void getByIdWithNullParameters() throws Exception {
        // prepare data
        io.setId(null);

        //Perform test
        try {
            ioManager.get(io.getId(), key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("Id cannot be null."));
        }
    }

    @Test
    public void getByIdWithWrongAccess() throws Exception {

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.INSERTION_ORDER, Collections.singletonList(io.getId()),
                sqlSessionMock);

        //Perform test
        try {
            ioManager.get(io.getId(), key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(),
                    is("Data not found for User: " + key.getUserId()));
        }
    }

    @Test
    public void getByMediaBuyOk() throws Exception {
        //customize mock's behavior
        when(insertionOrderDao.getFirstIOByMediaBuy(eq(io.getMediaBuyId()), eq(sqlSessionMock))).thenReturn(io);

        //Perform test
        InsertionOrder result = ioManager.getByMediaBuy(io.getMediaBuyId(), key);

        assertThat(result, is(notNullValue()));
    }

    @Test
    public void getByMediaBuyWithNullParameters() throws Exception {
        // prepare data
        io.setMediaBuyId(null);

        //Perform test
        try {
            ioManager.getByMediaBuy(io.getMediaBuyId(), key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("Media Buy Id cannot be null."));
        }
    }

    @Test
    public void getByMediaBuyWithWrongAccess() throws Exception {

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.MEDIA_BUY, Collections.singletonList(io.getMediaBuyId()),
                sqlSessionMock);

        //Perform test
        try {
            ioManager.getByMediaBuy(io.getMediaBuyId(), key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(), is(equalTo(
                    String.format("%s: [%s]Not found for User: %s", "MediaBuyId",
                            String.valueOf(io.getMediaBuyId()),
                            key.getUserId()))));
        }
    }

    @Test
    public void createWithIoNumberAndNotesOk() throws Exception {
        // prepare data
        io.setId(null);

        //customize mock's behavior
        when(insertionOrderDao.getFirstIOByMediaBuy(eq(io.getMediaBuyId()), eq(sqlSessionMock))).thenReturn(io);

        //Perform test
        InsertionOrder result = ioManager.create(io, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getIoNumber(), is(equalTo(io.getIoNumber())));
        assertThat(result.getNotes(), is(equalTo(io.getNotes())));
    }

    @Test
    public void createWithoutIoNumberAndNotesOk() throws Exception {
        // prepare data
        io.setId(null);
        io.setIoNumber(null);
        io.setNotes(null);

        //customize mock's behavior
        when(insertionOrderDao.getFirstIOByMediaBuy(eq(io.getMediaBuyId()), eq(sqlSessionMock))).thenReturn(io);

        //Perform test
        InsertionOrder result = ioManager.create(io, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getIoNumber(), is(equalTo(result.getId().intValue())));
        assertThat(result.getNotes(), containsString(key.getUserId()));
    }

    @Test
    public void createWithNullParameters() throws Exception {
        // prepare data
        io = null;

        //Perform test
        try {
            ioManager.create(io, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("Insertion Order cannot be null."));
        }
    }

    @Test
    public void createWithWrongPayload() throws Exception {
        // prepare data
        io.setId(Math.abs(EntityFactory.random.nextLong()));

        //Perform test
        try {
            ioManager.create(io, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getErrors().getAllErrors().size(), is(1));
            FieldError fieldError = (FieldError) e.getErrors().getAllErrors().get(0);
            assertThat(fieldError, is(notNullValue()));
            assertThat(fieldError.getField(), is(equalTo("id")));
            assertThat((Long) fieldError.getRejectedValue(), is(equalTo(io.getId())));
            assertThat(fieldError.getDefaultMessage(), is(equalTo(
                    String.format("%s should not have the id field. If you are trying to update an existing %s use PUT service.",
                            ResourceBundleUtil.getString("global.label.insertionOrder"),
                            ResourceBundleUtil.getString("global.label.insertionOrder")))));
        }
    }

    @Test
    public void createWithWrongIoNumber() throws Exception {
        // prepare data
        io.setId(null);
        io.setIoNumber(io.getIoNumber() * (-1));

        // Perform test
        try {
            ioManager.create(io, key);
            fail("This test should throw an ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getErrors().getAllErrors().size(), is(1));
            FieldError fieldError = (FieldError) e.getErrors().getAllErrors().get(0);
            assertThat(fieldError, is(notNullValue()));
            assertThat(fieldError.getField(), is(equalTo("ioNumber")));
            assertThat((int) fieldError.getRejectedValue(), is(equalTo(io.getIoNumber())));
            assertThat(fieldError.getDefaultMessage(), is(equalTo(
                    String.format("Invalid %s, it supports values between %s and %s.", "ioNumber",
                            String.valueOf(Constants.INSERTION_ORDER_NUMBER_MIN_VALUE),
                            String.valueOf(Constants.INSERTION_ORDER_NUMBER_MAX_VALUE)))));
        }
    }

    @Test
    public void createWithWrongAccess() throws Exception {
        // prepare data
        io.setId(null);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.MEDIA_BUY, Collections.singletonList(io.getMediaBuyId()),
                sqlSessionMock);

        //Perform test
        try {
            ioManager.create(io, key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(), is(equalTo(
                    String.format("Data not found for User: %s", key.getUserId()))));
        }
    }

    @Test
    public void createIOWithIoNumberAndNotesOk() {
        // prepare data
        io.setId(null);

        //customize mock's behavior
        when(insertionOrderDao.getFirstIOByMediaBuy(eq(io.getMediaBuyId()), eq(sqlSessionMock))).thenReturn(io);

        //Perform test
        InsertionOrder result = ioManager.create(io, sqlSessionMock, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getIoNumber(), is(equalTo(io.getIoNumber())));
        assertThat(result.getNotes(), is(equalTo(io.getNotes())));
    }

    @Test
    public void createIOWithoutIoNumberAndNotesOk() {
        // prepare data
        io.setId(null);
        io.setIoNumber(null);
        io.setNotes(null);

        //customize mock's behavior
        when(insertionOrderDao.getFirstIOByMediaBuy(eq(io.getMediaBuyId()), eq(sqlSessionMock))).thenReturn(io);

        //Perform test
        InsertionOrder result = ioManager.create(io, sqlSessionMock, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getIoNumber(), is(equalTo(result.getId().intValue())));
        assertThat(result.getNotes(), containsString(key.getUserId()));
    }

    @Test
    public void createIOWithNullParameters() {
        // prepare data
        io.setId(null);
        sqlSessionMock = null;

        //Perform test
        try {
            ioManager.create(io, sqlSessionMock, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("Session cannot be null."));
        }
    }

    @Test
    public void testUpdate() throws Exception {
        // prepare data
        io.setId(null);
        String name = io.getName();
        Integer number = io.getIoNumber();

        io = ioManager.create(io, key);
        assertThat(io, is(notNullValue()));
        assertThat(number, is(equalTo(io.getIoNumber())));
        assertThat(name, is(equalTo(io.getName())));

        String newName = "IO updated name";
        String newStatus = "Accepted";
        io.setName(newName);
        io.setStatus(newStatus);

        //customize mock's behavior
        when(placementDao.getPlacements(any(SearchCriteria.class), eq(key), eq(sqlSessionMock))).
                thenReturn(new RecordSet<>(EntityFactory.createPlacements(5)));

        //Perform test
        InsertionOrder updatedIO = ioManager.update(io.getId(), io, key);
        assertThat(updatedIO, is(notNullValue()));
        assertThat(io.getIoNumber(), is(equalTo(updatedIO.getIoNumber())));
        assertThat(newName, is(equalTo(updatedIO.getName())));
        assertThat(newStatus, is(equalTo(updatedIO.getStatus())));
    }

    @Test
    public void testUpdateStatusBackToNew() throws Exception {
        // preapare data
        io.setId(null);
        String name = io.getName();
        Integer number = io.getIoNumber();

        io = ioManager.create(io, key);

        assertThat(io, is(notNullValue()));
        assertThat(number, is(equalTo(io.getIoNumber())));
        assertThat(name, is(equalTo(io.getName())));

        String newName = "IO updated name";
        String newStatus = "Accepted";
        io.setName(newName);
        io.setStatus(newStatus);

        //customize mock's behavior
        when(placementDao.getPlacements(any(SearchCriteria.class), eq(key), eq(sqlSessionMock))).
                thenReturn(new RecordSet<>(EntityFactory.createPlacements(5)));

        //Perform test
        InsertionOrder updatedIO = ioManager.update(io.getId(), io, key);
        assertThat(updatedIO, is(notNullValue()));
        assertThat(io.getIoNumber(), is(equalTo(updatedIO.getIoNumber())));
        assertThat(newName, is(equalTo(updatedIO.getName())));
        assertThat(newStatus, is(equalTo(updatedIO.getStatus())));

        try {
            updatedIO.setStatus("New");
            ioManager.update(updatedIO.getId(), updatedIO, key);
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    containsString("The Insertion Order's status cannot be changed to New as it is currently in status Accepted"));
        }
    }

    @Test
    public void updateInsertionOrderNullIONumberOk() throws Exception {
        // prepare data
        Long insertionOrderId = (long) Math.abs(Math.random() * Integer.MAX_VALUE);
        InsertionOrder insertionOrder = EntityFactory.createInsertionOrder();
        insertionOrder.setId(insertionOrderId);
        insertionOrder.setIoNumber(null);
        InsertionOrder ioResult = new InsertionOrder(insertionOrderId,
                insertionOrder.getMediaBuyId(), insertionOrder.getPublisherId(),
                insertionOrderId.intValue(), insertionOrder.getName(), insertionOrder.getNotes(),
                insertionOrder.getCreatedTpwsKey());
        ioResult.setStatus("New");

        //customize mock's behavior
        when(insertionOrderDao.getNextId(any(SqlSession.class))).thenReturn(insertionOrderId);
        when(insertionOrderDao.get(anyLong(), any(SqlSession.class))).thenReturn(ioResult);
        when(placementDao.getPlacements(any(SearchCriteria.class), eq(key), eq(sqlSessionMock))).
                thenReturn(new RecordSet<>(EntityFactory.createPlacements(5)));

        //Perform test
        insertionOrder = ioManager.update(insertionOrderId, insertionOrder, key);
        assertThat(insertionOrder.getId(), is(ioResult.getId()));
        assertThat(insertionOrder.getIoNumber(), is(ioResult.getIoNumber()));
        assertThat(insertionOrder.getName(), is(ioResult.getName()));
    }

    @Test
    public void updateInsertionOrderValidIONumberOk() throws Exception {
        // prepare data
        Long insertionOrderId = (long) Math.abs(Math.random() * Integer.MAX_VALUE);
        InsertionOrder insertionOrder = EntityFactory.createInsertionOrder();
        insertionOrder.setId(insertionOrderId);
        insertionOrder.setIoNumber((int) Math.abs(Math.random() * Integer.MAX_VALUE));
        InsertionOrder ioResult = new InsertionOrder(insertionOrderId,
                insertionOrder.getMediaBuyId(), insertionOrder.getPublisherId(),
                insertionOrder.getIoNumber(), insertionOrder.getName(), insertionOrder.getNotes(),
                insertionOrder.getCreatedTpwsKey());
        ioResult.setStatus(InsertionOrderStatusEnum.NEW.getName());

        //customize mock's behavior
        when(insertionOrderDao.openSession()).thenReturn(sqlSessionMock);
        when(insertionOrderDao.getNextId(any(SqlSession.class))).thenReturn(insertionOrderId);
        when(insertionOrderDao.get(anyLong(), any(SqlSession.class))).thenReturn(ioResult);
        when(placementDao.getPlacements(any(SearchCriteria.class), eq(key), eq(sqlSessionMock))).
                thenReturn(new RecordSet<>(EntityFactory.createPlacements(5)));

        //Perform test
        insertionOrder = ioManager.update(insertionOrderId, insertionOrder, key);
        assertThat(insertionOrder.getId(), is(ioResult.getId()));
        assertThat(insertionOrder.getIoNumber(), is(ioResult.getIoNumber()));
        assertThat(insertionOrder.getName(), is(ioResult.getName()));
    }

    @Test
    public void updateInsertionOrderInvalidIONumberFail() throws Exception {
        // prepare data
        Long insertionOrderId = (long) Math.abs(Math.random() * Integer.MAX_VALUE);
        InsertionOrder insertionOrder = EntityFactory.createInsertionOrder();
        insertionOrder.setId(insertionOrderId);
        insertionOrder.setIoNumber(Integer.MAX_VALUE + 1);
        InsertionOrder ioResult = new InsertionOrder(insertionOrderId,
                insertionOrder.getMediaBuyId(), insertionOrder.getPublisherId(),
                insertionOrder.getIoNumber(), insertionOrder.getName(), insertionOrder.getNotes(),
                insertionOrder.getCreatedTpwsKey());
        ioResult.setStatus(InsertionOrderStatusEnum.NEW.getName());

        //customize mock's behavior
        when(insertionOrderDao.openSession()).thenReturn(sqlSessionMock);
        when(insertionOrderDao.getNextId(any(SqlSession.class))).thenReturn(insertionOrderId);

        //Perform test
        try {
            ioManager.update(insertionOrderId, insertionOrder, key);
            fail("Invalid ioNumber");
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    is("Invalid Insertion Order number, it supports values between 0 and 2147483647."));
        }
    }

    @Test
    public void createInsertionOrderNullIONumberOk() throws Exception {
        // prepare data
        InsertionOrder insertionOrder = EntityFactory.createInsertionOrder();
        insertionOrder.setIoNumber(null);
        Long insertionOrderId = (long) Math.abs(Math.random() * Integer.MAX_VALUE);
        InsertionOrder ioResult = new InsertionOrder(insertionOrderId,
                insertionOrder.getMediaBuyId(), insertionOrder.getPublisherId(),
                insertionOrderId.intValue(), insertionOrder.getName(), insertionOrder.getNotes(),
                insertionOrder.getCreatedTpwsKey());

        //customize mock's behavior
        when(insertionOrderDao.getNextId(any(SqlSession.class))).thenReturn(insertionOrderId);
        when(insertionOrderDao.get(anyLong(), any(SqlSession.class))).thenReturn(ioResult);

        //Perform test
        insertionOrder = ioManager.create(insertionOrder, key);
        assertThat(insertionOrder.getId(), is(ioResult.getId()));
        assertThat(insertionOrder.getIoNumber(), is(ioResult.getIoNumber()));
        assertThat(insertionOrder.getName(), is(ioResult.getName()));
    }

    @Test
    public void createInsertionOrderValidIONumberOk() throws Exception {
        // prepare data
        InsertionOrder insertionOrder = EntityFactory.createInsertionOrder();
        insertionOrder.setIoNumber((int) Math.abs(Math.random() * Integer.MAX_VALUE));
        Long insertionOrderId = (long) Math.abs(Math.random() * Integer.MAX_VALUE);
        InsertionOrder ioResult = new InsertionOrder(insertionOrderId,
                insertionOrder.getMediaBuyId(), insertionOrder.getPublisherId(),
                insertionOrder.getIoNumber(), insertionOrder.getName(), insertionOrder.getNotes(),
                insertionOrder.getCreatedTpwsKey());

        //customize mock's behavior
        when(insertionOrderDao.getNextId(any(SqlSession.class))).thenReturn(insertionOrderId);
        when(insertionOrderDao.get(anyLong(), any(SqlSession.class))).thenReturn(ioResult);

        //Perform test
        insertionOrder = ioManager.create(insertionOrder, key);
        assertThat(insertionOrder.getId(), is(ioResult.getId()));
        assertThat(insertionOrder.getIoNumber(), is(ioResult.getIoNumber()));
        assertThat(insertionOrder.getName(), is(ioResult.getName()));
    }

    @Test
    public void createInsertionOrderInvalidIONumberFail() throws Exception {
        // prepare data
        InsertionOrder insertionOrder = EntityFactory.createInsertionOrder();
        insertionOrder.setIoNumber(Integer.MAX_VALUE + 1);
        Long insertionOrderId = (long) Math.abs(Math.random() * Integer.MAX_VALUE);

        //customize mock's behavior
        when(insertionOrderDao.getNextId(any(SqlSession.class))).thenReturn(insertionOrderId);

        //Perform test
        try {
            ioManager.create(insertionOrder, key);
            fail("Invalid ioNumber");
        } catch (ValidationException e) {
            ObjectError error = e.getErrors().getAllErrors().get(0);
            assertThat(error.getDefaultMessage(),
                    is("Invalid ioNumber, it supports values between 0 and 2147483647."));
        }
    }

    private static Answer<Void> create(final InsertionOrder insertionOrder) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                InsertionOrder result = (InsertionOrder) invocation.getArguments()[0];
                insertionOrder.setId(result.getId());
                insertionOrder.setIoNumber(result.getIoNumber());
                insertionOrder.setNotes(result.getNotes());
                insertionOrder.setCreatedTpwsKey(result.getCreatedTpwsKey());
                return null;
            }
        };
    }
}

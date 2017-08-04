package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trueffect.truconnect.api.crud.EntityFactory.faker;

import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventTagType;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementGroupDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.mock.SiteMeasurementEventContextMock;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class SiteMeasurementEventManagerTest extends AbstractManagerTest{

    private SiteMeasurementEventContextMock mockContext;
    private SiteMeasurementEventManager manager;
    private SiteMeasurementEventDao smEventDao;
    private OauthKey key;
    private SmEvent expectedSmEvent;
    private User user;
    private UserDao userDao;
    private SiteMeasurementGroupDao smGroupDao;
    private SiteMeasurementDao smDao;

    @Before
    public void init() throws Exception {
        smEventDao = mock(SiteMeasurementEventDao.class);
        smDao = mock(SiteMeasurementDao.class);
        smGroupDao = mock(SiteMeasurementGroupDao.class);

        manager = new SiteMeasurementEventManager(smEventDao, userDao, accessControlMockito, smDao, smGroupDao);
        key = new OauthKey(EncryptUtil.encryptAES("dummy@user"), "00000");
        expectedSmEvent = EntityFactory.createSmEvent();

        // mock sessions
        when(smEventDao.openSession()).thenReturn(sqlSessionMock);

        // Mocks access control
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.SITE_MEASUREMENT_EVENT,
                sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.SITE_MEASUREMENT_GROUP,
                sqlSessionMock);
    }

    @Test
    public void testCreateSmEvent() throws Exception {
        Long nextId = EntityFactory.random.nextLong();

        when(smEventDao.getNextId(sqlSessionMock)).thenReturn(nextId);
        when(smEventDao.get(nextId, sqlSessionMock)).thenReturn(expectedSmEvent);

        expectedSmEvent.setSmGroupId(EntityFactory.random.nextLong());
        Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmEvent>
         actualSmEvent = manager.create(expectedSmEvent, key);
        assertThat(actualSmEvent.success(), is(notNullValue()));
    }

    @Test
    public void testUpdateSmEventSuccessful() {

        SmEvent smEvent = EntityFactory.createSmEvent();
        SmGroup smGroup = EntityFactory.createSmGroup();
        SiteMeasurementDTO smDTO = EntityFactory.createSiteMeasurement();

        smEvent.setEventType((long)SiteMeasurementEventTagType.TRU_TAG.ordinal());
        smEvent.setSmEventType((long)SiteMeasurementEventType.OTHER.ordinal());

        // Mock bussiness logic
        when(smEventDao.get(smEvent.getId(), sqlSessionMock)).thenReturn(smEvent);
        when(smGroupDao.get(smEvent.getSmGroupId(), sqlSessionMock)).thenReturn(smGroup);
        when(smDao.get(smGroup.getMeasurementId(), sqlSessionMock)).thenReturn(smDTO);

        Either<Errors, SmEvent> result = manager.update(smEvent.getId(), smEvent, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
    }

    @Test
    public void testUpdateSmEventTryChangeName() {

        SmEvent existingSmEvent = EntityFactory.createSmEvent();
        SmEvent modifiedSmEvent = EntityFactory.createSmEvent();

        SmGroup smGroup = EntityFactory.createSmGroup();
        SiteMeasurementDTO smDTO = EntityFactory.createSiteMeasurement();

        existingSmEvent.setEventType((long)SiteMeasurementEventTagType.TRU_TAG.ordinal());
        existingSmEvent.setSmEventType((long)SiteMeasurementEventType.OTHER.ordinal());

        modifiedSmEvent.setId(existingSmEvent.getId());
        modifiedSmEvent.setLocation(existingSmEvent.getLocation());
        modifiedSmEvent.setEventType(existingSmEvent.getEventType());
        modifiedSmEvent.setSmEventType(existingSmEvent.getSmEventType());
        modifiedSmEvent.setEventName("AnotherName");

        // Mock bussiness logic
        when(smEventDao.get(modifiedSmEvent.getId(), sqlSessionMock)).thenReturn(existingSmEvent);
        when(smGroupDao.get(modifiedSmEvent.getSmGroupId(), sqlSessionMock)).thenReturn(smGroup);
        when(smDao.get(smGroup.getMeasurementId(), sqlSessionMock)).thenReturn(smDTO);

        Either<Errors, SmEvent> result = manager.update(modifiedSmEvent.getId(), modifiedSmEvent, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));

        Errors errors = result.error();
        assertThat(errors.getErrors(), is(notNullValue()));
        assertThat(errors.getErrors().size(), is(1));
        assertThat(errors.getErrors().get(0).getMessage(), is("Event Name cannot be changed."));
    }

    @Test
    public void testIsSmGroupNameDoesNotExist() throws Exception {
        when(smEventDao.isSmEventNameExist(anyLong() ,Matchers.any(String.class), eq(sqlSessionMock))).thenReturn(false);
        String name = "Group_name";
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> result = manager.isEventNameExist(123L, name, key);
        assertThat(result.success(), Is.is(false));
    }

    @Test
    public void testIsSmGroupNameAlreadyExist() throws Exception {
        when(smEventDao.isSmEventNameExist(anyLong() ,Matchers.any(String.class), eq(sqlSessionMock))).thenReturn(true);
        String name ="Group_name";
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> result = manager.isEventNameExist(12345L, name, key);
        assertThat(result.success(), Is.is(true));
    }

    @Test
    public void testIsSmGroupNameAlreadyExistInvalidGroupId() throws Exception {
        when(smEventDao.isSmEventNameExist(anyLong() ,Matchers.any(String.class), eq(sqlSessionMock))).thenReturn(true);
        String name ="Group_name";
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> result = manager.isEventNameExist(null, name, key);
        MatcherAssert.assertThat(result.isError(), Is.is(true));
        MatcherAssert.assertThat(result.error().getErrors().get(0).getMessage(), Is.is("Invalid Sm Group Id, it cannot be empty."));
    }

    @Test
    public void testInvalidEventName()throws Exception {
        SmEvent smEvent = EntityFactory.createSmEvent();
        String invalidName = "Group_Name_Test_Invalid_lenght";
        smEvent.setEventName(invalidName);
        smEvent.setSmGroupId(132123L);
        Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmEvent> result = manager.create(smEvent, key);
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getMessage(), is("Invalid eventName, it supports characters up to 20."));
    }

    @Test
    public void testInvalidSmEventGroup()throws Exception {
        SmEvent smEvent = EntityFactory.createSmEvent();
        Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmEvent> result = manager.create(smEvent, key);
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getMessage(), is("smGroup Id cannot be null."));
    }

    @Test
    public void testInvalidBlankEventName()throws Exception {
        SmEvent smEvent = EntityFactory.createSmEvent();
        smEvent.setEventName(" ");
        smEvent.setSmGroupId(132123L);
        Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmEvent> result = manager.create(smEvent, key);
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getMessage(), is("Invalid eventName, it cannot be empty."));
    }
}
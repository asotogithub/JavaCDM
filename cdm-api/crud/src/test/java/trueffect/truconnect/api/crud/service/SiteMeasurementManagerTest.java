package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.SiteMeasurementDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventDao;
import trueffect.truconnect.api.crud.dao.SiteMeasurementGroupDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Richard Jaldin
 */

public class SiteMeasurementManagerTest extends AbstractManagerTest {

    private SiteMeasurementManager siteMeasurementManager;
    private SiteMeasurementGroupManager groupManager;
    private SiteMeasurementEventManager eventManager;
    private User user;

    //DAOs
    private SiteMeasurementEventDao siteMeasurementEventDao;
    private SiteMeasurementDao siteMeasurementDao;
    private SiteMeasurementGroupDao siteMeasurementGroupDao;
    private UserDao userDao;

    private SiteMeasurementDTO siteMeasurement;

    @Before
    public void setUp() throws Exception {
        // Mock daos
        accessControlMockito = mock(AccessControl.class);
        siteMeasurementEventDao = mock(SiteMeasurementEventDao.class);
        siteMeasurementDao = mock(SiteMeasurementDao.class);
        siteMeasurementGroupDao = mock(SiteMeasurementGroupDao.class);
        userDao = mock(UserDao.class);

        // Variables
        user = EntityFactory.createUser();
        user.setUserName(key.getUserId());
        siteMeasurement = EntityFactory.createSiteMeasurement();

        eventManager = new SiteMeasurementEventManager(siteMeasurementEventDao, userDao,
                accessControlMockito, siteMeasurementDao, siteMeasurementGroupDao);
        siteMeasurementManager =
                new SiteMeasurementManager(siteMeasurementDao, userDao, accessControlMockito);

        // mock sessions
        when(siteMeasurementEventDao.openSession()).thenReturn(sqlSessionMock);
        when(siteMeasurementDao.openSession()).thenReturn(sqlSessionMock);

        // Mocks access control
        MockAccessControlUtil.allowAllForUser(accessControlMockito, sqlSessionMock);

        // Mocks common behavior
        when(siteMeasurementDao.create(any(SiteMeasurementDTO.class), eq(sqlSessionMock)))
                .thenAnswer(new Answer<SiteMeasurementDTO>() {
                    @Override
                    public SiteMeasurementDTO answer(InvocationOnMock invocationOnMock)
                            throws Throwable {
                        siteMeasurement = (SiteMeasurementDTO) invocationOnMock.getArguments()[0];
                        siteMeasurement.setId(siteMeasurement.getId() == null ? Math
                                .abs(EntityFactory.random.nextLong()) : siteMeasurement.getId());
                        siteMeasurement.setCreatedTpwsKey(key.getTpws());
                        siteMeasurement.setModifiedTpwsKey(key.getTpws());
                        siteMeasurement.setCreatedDate(new Date());
                        siteMeasurement.setModifiedDate(new Date());
                        return siteMeasurement;
                    }
                });
    }

    @Test
    public void testSaveSiteMeasurement() {
        Long siteMeasurentId = EntityFactory.random.nextLong();
        when(siteMeasurementDao.getNextId(sqlSessionMock)).thenReturn(siteMeasurentId);
        when(siteMeasurementDao.get(siteMeasurentId, sqlSessionMock)).thenReturn(siteMeasurement);

        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, SiteMeasurementDTO>
                result = siteMeasurementManager.create(siteMeasurement, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getId(), is(notNullValue()));
        assertThat(result.success().getName(), is(equalTo(siteMeasurement.getName())));
    }

    @Test
    public void testSaveSiteMeasurementWithDuplicatedName() {
        Long siteMeasurementId = EntityFactory.random.nextLong();
        when(siteMeasurementDao.getNextId(sqlSessionMock)).thenReturn(siteMeasurementId);
        when(siteMeasurementDao.get(siteMeasurementId, sqlSessionMock)).thenReturn(siteMeasurement);
        when(siteMeasurementDao
                .doesSiteMeasurementExists(anyString(), anyLong(), anyLong(), eq(sqlSessionMock)))
                .thenReturn(true);

        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, SiteMeasurementDTO>
                result = siteMeasurementManager.create(siteMeasurement, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().get(0).getCode().toString(), is(equalTo(
                BusinessCode.DUPLICATE.toString())));
        assertThat(result.error().getErrors().get(0).
                getMessage(), is(equalTo("The provided name " + siteMeasurement
                .getName() + " already exists for brand " + siteMeasurement.getBrandId() + ".")));
    }

    @Test
    public void testGetSiteMeasurementById() throws Exception {
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, SiteMeasurementDTO>
                result = siteMeasurementManager.create(siteMeasurement, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getId(), is(notNullValue()));

        when(siteMeasurementDao.get(eq(siteMeasurement.getId()), eq(sqlSessionMock))).thenReturn(
                siteMeasurement);
        SiteMeasurementDTO record = siteMeasurementManager.get(result.success().getId(), key);
        assertThat(record, is(notNullValue()));
        assertThat(record.getName(), is(notNullValue()));
        assertThat(record.getName(), is(equalTo(result.success().getName())));
    }

    @Test
    public void testGetSiteMeasurements() throws Exception {
        RecordSet<SiteMeasurementDTO> records = new RecordSet<>();
        Long siteMeasurementId;

        List<SiteMeasurementDTO> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            SiteMeasurementDTO siteMeasurementDTO = EntityFactory.createSiteMeasurement();
            siteMeasurementId = EntityFactory.random.nextLong();
            when(siteMeasurementDao.getNextId(sqlSessionMock)).thenReturn(siteMeasurementId);
            when(siteMeasurementDao.get(siteMeasurementId, sqlSessionMock)).thenReturn(
                    siteMeasurementDTO);
            siteMeasurementManager.create(siteMeasurementDTO, key);
            list.add(siteMeasurementDTO);
        }

        records.setRecords(list);

        SearchCriteria searchCriteria = new SearchCriteria();

        when(siteMeasurementDao.get(any(SearchCriteria.class), eq(key), eq(sqlSessionMock)))
                .thenReturn(
                        records);

        RecordSet<SiteMeasurementDTO> recordSet = siteMeasurementManager.get(searchCriteria, key);
        assertThat(recordSet, is(notNullValue()));
        assertThat(recordSet.getRecords(), is(notNullValue()));
        assertThat(recordSet.getRecords().size(), is(equalTo(5)));
    }

    @Test
    public void testUpdateSiteMeasurement() throws Exception {
        when(siteMeasurementDao.update(siteMeasurement, sqlSessionMock)).thenReturn(1);

        siteMeasurement.setState(Constants.DEFAULT_STATE_NEW_SITEMEASUREMENT);
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, SiteMeasurementDTO> sm =
                siteMeasurementManager.create(siteMeasurement, key);
        assertThat(sm, is(notNullValue()));
        assertThat(sm.success(), is(notNullValue()));
        assertThat(sm.success().getId(), is(notNullValue()));
        assertThat(sm.success().getName(), is(equalTo(siteMeasurement.getName())));

        when(siteMeasurementDao.get(eq(sm.success().getId()), eq(sqlSessionMock))).thenReturn(
                siteMeasurement);

        String newName = "Site Measurement update " + (new Date().getTime());
        sm.success().setName(newName);
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, SiteMeasurementDTO> result =
                siteMeasurementManager.update(sm.success().getId(), sm.success(), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getId(), is(notNullValue()));
        assertThat(result.success().getId(), is(equalTo(sm.success().getId())));
        assertThat(result.success().getName(), is(equalTo(siteMeasurement.getName())));
    }

    @Test
    public void testUpdateSiteMeasurementWithDuplicatedName() throws Exception {
        Long siteMeasurementId = EntityFactory.random.nextLong();
        siteMeasurement.setId(siteMeasurementId);
        siteMeasurement.setState(Constants.DEFAULT_STATE_NEW_SITEMEASUREMENT);

        when(siteMeasurementDao.update(siteMeasurement, sqlSessionMock)).thenReturn(1);
        when(siteMeasurementDao.get(eq(siteMeasurement.getId()), eq(sqlSessionMock))).thenReturn(
                siteMeasurement);
        when(siteMeasurementDao
                .doesSiteMeasurementExists(anyString(), anyLong(), anyLong(), eq(sqlSessionMock)))
                .thenReturn(true);

        String newName = "Site Measurement update " + (new Date().getTime());
        siteMeasurement.setName(newName);
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, SiteMeasurementDTO> result =
                siteMeasurementManager.update(siteMeasurement.getId(), siteMeasurement, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().get(0).getCode().toString(), is(equalTo(
                BusinessCode.DUPLICATE.toString())));
        assertThat(result.error().getErrors().get(0).
                getMessage(), is(equalTo("The provided name " + siteMeasurement
                .getName() + " already exists for brand " + siteMeasurement.getBrandId() + ".")));
    }

    @Test
    public void testUpdateSiteMeasurementWithDuplicatedNameTraffickedState() throws Exception {
        Long siteMeasurementId = EntityFactory.random.nextLong();
        siteMeasurement.setId(siteMeasurementId);
        siteMeasurement.setState(Constants.STATE_TRAFFICKED_SITEMEASUREMENT);

        when(siteMeasurementDao.update(siteMeasurement, sqlSessionMock)).thenReturn(1);
        when(siteMeasurementDao.get(eq(siteMeasurement.getId()), eq(sqlSessionMock))).thenReturn(
                siteMeasurement);

        String newName = "Site Measurement update " + (new Date().getTime());
        siteMeasurement.setName(newName);
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, SiteMeasurementDTO>
                result = siteMeasurementManager.update(siteMeasurement.getId(), siteMeasurement, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().get(0).getCode().toString(), is(equalTo(
                BusinessCode.INVALID.toString())));
        assertThat(result.error().getErrors().get(0).getMessage(),
                is(equalTo("Once Site Measurement gets trafficked you cannot change the name.")));
    }

    @Test
    public void testGetPingEventByEvent() {
        RecordSet<SmEvent> records = new RecordSet<>();
        List<SmPingEventDTO> list = new ArrayList<>();
        Either<trueffect.truconnect.api.commons.exceptions.business.Error, SmEvent> res;
        Long eventId = EntityFactory.random.nextLong();
        SmEvent event = new SmEvent();
        event.setId(eventId);
        int numberOfPingsEvents = 5;
        for (int i = 0; i < numberOfPingsEvents; i++) {
            SmPingEventDTO ping = EntityFactory.createSmPingEvent();
            ping.setId(eventId);
            list.add(ping);
        }

        event.setPingEvents(list);

        when(siteMeasurementEventDao.getSmPingsByEvents(anyLong(), eq(sqlSessionMock))).thenReturn(
                event);

        res = eventManager.getSmPingsByEvents(eventId, key);
        assertThat(res.isSuccess(), is(true));
        assertThat(res.success().getPingEvents().size(), is(numberOfPingsEvents));
    }
}


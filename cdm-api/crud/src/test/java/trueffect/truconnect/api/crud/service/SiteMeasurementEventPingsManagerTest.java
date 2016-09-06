package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.fail;

import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventPingTagType;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventPingType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SiteMeasurementEventPingsDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * Created by jesus.nunez on 8/12/2016.
 */
public class SiteMeasurementEventPingsManagerTest extends AbstractManagerTest {

    private SiteMeasurementEventPingsManager eventPingManager;
    private SmPingEventDTO pingDTO;
    private User user;


    //DAOs
    private SiteMeasurementEventPingsDao smEventPingsDao;
    private UserDao userDao;

    @Before
    public void setUp() throws Exception {
        // Mock daos
        accessControlMockito = mock(AccessControl.class);
        smEventPingsDao = mock(SiteMeasurementEventPingsDao.class);
        userDao = mock(UserDao.class);

        // Variables
        user = EntityFactory.createUser();
        user.setUserName(key.getUserId());
        pingDTO = EntityFactory.createSmPingEvent();

        when(smEventPingsDao.openSession()).thenReturn(sqlSessionMock);
        when(smEventPingsDao.getNextId(sqlSessionMock)).thenReturn(EntityFactory.random.nextLong());
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.SITE_MEASUREMENT_EVENT_PING,
                sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.SITE_MEASUREMENT_EVENT,
                sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.SITE,
                sqlSessionMock);



        eventPingManager = new SiteMeasurementEventPingsManager(userDao, smEventPingsDao, accessControlMockito);
    }

    @Test
    public void deleteEventPingsPass() {
        int totalRecords = 100;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);
        User user = EntityFactory.createUser();

        when(smEventPingsDao.deletePingEvent(anyList(),
                eq(key.getTpws()), eq(sqlSessionMock))).thenReturn(ids.size());

        Either<Errors, String> result = eventPingManager.deletePingEvent(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(),
                is("Bulk delete for SiteMeasurementEventPings completed successfully."));
    }

    @Test
    public void deleteEventPingsFailedDueNullPayload() {
        RecordSet<Long> toDelete = null;

        try {
            eventPingManager.deletePingEvent(toDelete, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(IsNull.notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Pings Ids"))));
        }
    }

    @Test
    public void deleteEventPingsFailedDueEmptyPayload() {
        // prepare data
        int totalRecords = 100;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        ids.set(EntityFactory.random.nextInt(totalRecords), null);
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // Perform test
        Either<Errors, String> result = eventPingManager.deletePingEvent(toDelete, key);

        assertThat(result, is(IsNull.notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(IsNull.notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        assertThat(result.error().getErrors().get(0).getMessage(), is(equalTo(
                String.format("Invalid %s, it cannot be empty.", "id"))));
    }

    @Test
    public void testCreateSiteMeasurementEventPingPass() {
        Either<Errors, RecordSet<SmPingEventDTO>> result =
                eventPingManager.createPingEvents(new RecordSet<>(Collections.singletonList(pingDTO)), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
    }

    @Test
    public void testCreateSiteMeasurementEventPingSelectiveFail() {
        pingDTO.setPingType(Long.valueOf(SiteMeasurementEventPingType.SELECTIVE.value()));
        pingDTO.setPingTagType(Long.valueOf(SiteMeasurementEventPingTagType.IFRAME.ordinal()));
        pingDTO.setPingContent(EntityFactory.createFakeURL());

        Either<Errors, RecordSet<SmPingEventDTO>> result =
                eventPingManager.createPingEvents(new RecordSet<>(Collections.singletonList(pingDTO)), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));

        Errors errors = result.error();
        assertThat(errors.getErrors(), is(notNullValue()));
        assertThat(errors.getErrors().size(), is(1));
        assertThat(errors.getErrors().get(0).getMessage(), is("Invalid pingTagType, it should be one of [0]."));
    }

    @Test
    public void testCreateSiteMeasurementEventPingSelectiveUrlFail() {
        pingDTO.setPingType(Long.valueOf(SiteMeasurementEventPingType.SELECTIVE.value()));
        pingDTO.setPingTagType(Long.valueOf(SiteMeasurementEventPingTagType.IMG.ordinal()));
        pingDTO.setPingContent(EntityFactory.faker.letterify("??????????"));

        Either<Errors, RecordSet<SmPingEventDTO>> result =
                eventPingManager.createPingEvents(new RecordSet<>(Collections.singletonList(pingDTO)), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));

        Errors errors = result.error();
        assertThat(errors.getErrors(), is(notNullValue()));
        assertThat(errors.getErrors().size(), is(1));
        assertThat(errors.getErrors().get(0).getMessage(), is("Invalid URL."));
    }

    @Test
    public void testCreateSiteMeasurementEventPingHtmlFail() {
        pingDTO.setPingType(Long.valueOf(SiteMeasurementEventPingType.BROADCAST.value()));
        pingDTO.setPingTagType(Long.valueOf(SiteMeasurementEventPingTagType.TAG.ordinal()));
        pingDTO.setPingContent(EntityFactory.faker.letterify("??????????"));

        Either<Errors, RecordSet<SmPingEventDTO>> result =
                eventPingManager.createPingEvents(new RecordSet<>(Collections.singletonList(pingDTO)), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));

        Errors errors = result.error();
        assertThat(errors.getErrors(), is(notNullValue()));
        assertThat(errors.getErrors().size(), is(1));
        assertThat(errors.getErrors().get(0).getMessage(), is("Invalid HTML content."));
    }

    @Test
    public void testCreateSiteMeasurementEventPingHtmlPass() {
        pingDTO.setPingType(Long.valueOf(SiteMeasurementEventPingType.BROADCAST.value()));
        pingDTO.setPingTagType(Long.valueOf(SiteMeasurementEventPingTagType.TAG.ordinal()));
        pingDTO.setPingContent("<div> content here </div>");

        Either<Errors, RecordSet<SmPingEventDTO>> result =
                eventPingManager.createPingEvents(new RecordSet<>(Collections.singletonList(pingDTO)), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
    }

    @Test
    public void testUpdateSiteMeasurementEventPingPass(){
        pingDTO.setPingType(Long.valueOf(SiteMeasurementEventPingType.BROADCAST.value()));
        pingDTO.setPingTagType(Long.valueOf(SiteMeasurementEventPingTagType.IMG.ordinal()));
        pingDTO.setPingContent(EntityFactory.createFakeURL());

        Either<Errors, RecordSet<SmPingEventDTO>> result =
                eventPingManager.updatePingEvents(new RecordSet<>(Collections.singletonList(pingDTO)), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
    }

    @Test
    public void testUpdateSiteMeasurementEventPingNullIdFail(){
        pingDTO.setPingType(Long.valueOf(SiteMeasurementEventPingType.BROADCAST.value()));
        pingDTO.setPingTagType(Long.valueOf(SiteMeasurementEventPingTagType.IMG.ordinal()));
        pingDTO.setPingContent(EntityFactory.createFakeURL());
        pingDTO.setPingId(null);

        Either<Errors, RecordSet<SmPingEventDTO>> result =
                eventPingManager.updatePingEvents(new RecordSet<>(Collections.singletonList(pingDTO)), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));

        Errors errors = result.error();
        assertThat(errors.getErrors(), is(notNullValue()));
        assertThat(errors.getErrors().size(), is(1));
        assertThat(errors.getErrors().get(0).getMessage(), is("Ping Id cannot be null."));
    }
}

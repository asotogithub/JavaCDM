package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.model.SystemStatus;
import trueffect.truconnect.api.crud.Constants;
import trueffect.truconnect.api.crud.dao.StatusDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.resources.ResourceUtil;

import org.junit.Before;
import org.junit.Test;

public class StatusManagerTest extends AbstractManagerTest {
    private StatusDao statusDaoMock;
    private StatusManager statusManager;

    @Before
    public void init() {
        statusDaoMock = mock(StatusDao.class);
        statusManager = new StatusManager(statusDaoMock, statusDaoMock, accessControlMockito);

        when(statusDaoMock.openSession()).thenReturn(sqlSessionMock);
    }

    @Test
    public void testAnErrorWhenCMConnectionCheckThrowsException() {
        String message = "No connection available";
        when(statusDaoMock.checkConnection(sqlSessionMock)).thenThrow(new RuntimeException(message));

        SystemStatus systemStatus = statusManager.checkStatus();
        assertThat(systemStatus.getCmDbConnectionValid(), is(false));
        assertThat(systemStatus.getCmDbStatusMessage(), is(equalTo(message)));
    }

    @Test
    public void testAnErrorWhenCMConnectionReturnsUnexpectedValue() throws Exception {
        when(statusDaoMock.checkConnection(sqlSessionMock)).thenReturn(0L);

        SystemStatus systemStatus = statusManager.checkStatus();
        assertThat(systemStatus.getCmDbConnectionValid(), is(false));
        assertThat(systemStatus.getCmDbStatusMessage(), is(equalTo(ResourceBundleUtil.getString(StatusManager.ERROR_STATUS_NO_CONNECTION))));
    }

    @Test
    public void testAnErrorWhenCMConnectionIsAvailableAndWrongSchema() {
        when(statusDaoMock.checkConnection(sqlSessionMock)).thenReturn(1L);
        when(statusDaoMock.currentSchema(sqlSessionMock)).thenReturn("notMySchema");

        SystemStatus systemStatus = statusManager.checkStatus();
        assertThat(systemStatus.getCmDbConnectionValid(), is(false));
        assertThat(systemStatus.getCmDbStatusMessage(), is(equalTo(ResourceBundleUtil.getString(StatusManager.ERROR_STATUS_WRONG_SCHEMA))));
    }

    @Test
    public void testWhenCMCheckIsSuccessful() {
        when(statusDaoMock.checkConnection(sqlSessionMock)).thenReturn(1L);
        when(statusDaoMock.currentSchema(sqlSessionMock)).thenReturn(ResourceUtil.get(Constants.CM_DATA_STORE_USERNAME_RESOURCE));

        SystemStatus systemStatus = statusManager.checkStatus();
        assertThat(systemStatus.getCmDbConnectionValid(), is(true));
    }

    @Test
    public void testAnErrorWhenMetricsConnectionCheckThrowsException() {
        String message = "No connection available";
        when(statusDaoMock.checkConnection(sqlSessionMock)).thenThrow(new RuntimeException(message));

        SystemStatus systemStatus = statusManager.checkStatus();
        assertThat(systemStatus.getMetricsDbConnectionValid(), is(false));
        assertThat(systemStatus.getMetricsDbStatusMessage(), is(equalTo(message)));
    }

    @Test
    public void testAnErrorWhenMetricsConnectionReturnsUnexpectedValue() throws Exception {
        when(statusDaoMock.checkConnection(sqlSessionMock)).thenReturn(0L);

        SystemStatus systemStatus = statusManager.checkStatus();
        assertThat(systemStatus.getMetricsDbConnectionValid(), is(false));
        assertThat(systemStatus.getMetricsDbStatusMessage(), is(equalTo(ResourceBundleUtil.getString(StatusManager.ERROR_STATUS_NO_CONNECTION))));
    }

    @Test
    public void testAnErrorWhenMetricsConnectionIsAvailableAndWrongSchema() {
        when(statusDaoMock.checkConnection(sqlSessionMock)).thenReturn(1L);
        when(statusDaoMock.currentSchema(sqlSessionMock)).thenReturn("notMySchema");

        SystemStatus systemStatus = statusManager.checkStatus();
        assertThat(systemStatus.getMetricsDbConnectionValid(), is(false));
        assertThat(systemStatus.getMetricsDbStatusMessage(), is(equalTo(ResourceBundleUtil.getString(StatusManager.ERROR_STATUS_WRONG_SCHEMA))));
    }

    @Test
    public void testWhenMetricsCheckIsSuccessful() {
        when(statusDaoMock.checkConnection(sqlSessionMock)).thenReturn(1L);
        when(statusDaoMock.currentSchema(sqlSessionMock)).thenReturn(ResourceUtil.get(Constants.METRICS_DATA_STORE_USERNAME_RESOURCE));

        SystemStatus systemStatus = statusManager.checkStatus();
        assertThat(systemStatus.getMetricsDbConnectionValid(), is(true));
    }
}

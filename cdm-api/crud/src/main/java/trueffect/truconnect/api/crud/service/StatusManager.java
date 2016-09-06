package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.model.SystemStatus;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.Constants;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.StatusDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.resources.ResourceUtil;

import org.apache.ibatis.session.SqlSession;

/**
 * Manager for getting the status of the application.
 */
public class StatusManager extends AbstractGenericManager {

    public static final String ERROR_STATUS_NO_CONNECTION = "global.error.noDBConnection";
    public static final String ERROR_STATUS_WRONG_SCHEMA = "global.error.wrongSchema";

    private final StatusDao cmStatusDao;
    private final StatusDao metricsStatusDao;

    public StatusManager(StatusDao cmStatusDao, StatusDao metricsStatusDao, AccessControl accessControl) {
        super(accessControl);
        this.cmStatusDao = cmStatusDao;
        this.metricsStatusDao = metricsStatusDao;
    }

    public SystemStatus checkStatus() {
        Either<String, Boolean> metricsConnection = checkConnection(ResourceUtil.get(Constants.METRICS_DATA_STORE_USERNAME_RESOURCE), metricsStatusDao);
        Either<String, Boolean> cmConnection = checkConnection(ResourceUtil.get(Constants.CM_DATA_STORE_USERNAME_RESOURCE), cmStatusDao);
        SystemStatus systemStatus = new SystemStatus(cmConnection, metricsConnection);
        systemStatus.setVersion(ResourceBundleUtil.getString(Constants.APPLICATION_VERSION_MESSAGE));
        return systemStatus;
    }

    private Either<String, Boolean> checkConnection(String expectedSchema, StatusDao statusDao) {
        SqlSession session = statusDao.openSession();
        try {
            // Validate we can get a connection
            Long checkConnection = statusDao.checkConnection(session);
            if (checkConnection != 1L) {
                return Either.error(ResourceBundleUtil.getString(ERROR_STATUS_NO_CONNECTION));
            }

            // Validate we are connected to the correct schema
            String schema = statusDao.currentSchema(session);
            if (!expectedSchema.equals(schema)) {
                return Either.error(ResourceBundleUtil.getString(ERROR_STATUS_WRONG_SCHEMA));
            }
        } catch (Exception e) {
            return Either.error(e.getMessage());
        } finally {
            statusDao.close(session);
        }
        return Either.success(true);
    }
}

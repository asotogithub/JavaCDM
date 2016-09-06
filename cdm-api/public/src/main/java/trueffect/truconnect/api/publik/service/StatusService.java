package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.model.SystemStatus;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.StatusDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.StatusDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.mybatis.edw.MetricsPersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.StatusManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Service for getting the status of the API.
 */
@Path("/Status")
public class StatusService extends GenericService {

    private final StatusManager statusManager;

    public StatusService() {
        PersistenceContext cmContext = new PersistenceContextMyBatis();
        PersistenceContext metricsContext = new MetricsPersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(cmContext);
        StatusDao cmStatusDao = new StatusDaoImpl(cmContext);
        StatusDao metricsStatusDao = new StatusDaoImpl(metricsContext);
        statusManager = new StatusManager(cmStatusDao, metricsStatusDao, accessControl);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Response getStatus() {
        SystemStatus systemStatus = statusManager.checkStatus();
        if(systemStatus.getCmDbConnectionValid() && systemStatus.getMetricsDbConnectionValid()) {
            return Response.ok(systemStatus).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(systemStatus).build();
        }
    }
}

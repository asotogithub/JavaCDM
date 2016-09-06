package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.ScheduleSet;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;

/**
 *
 * @author Richard Jaldin
 */
public class SchedulePublicServiceProxy extends GenericPublicServiceProxy<ScheduleSet> {

    public SchedulePublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Schedules", contentType, userName, password);
    }

    public ScheduleSet getById(Long id) throws Exception {
        ServiceProxyImpl<ScheduleSet> proxy = getProxy();
        return proxy.getById(id);
    }

    public ScheduleSet update(ScheduleSet input) throws Exception {
        ServiceProxyImpl<ScheduleSet> proxy = getProxy();
        return proxy.update(input);
    }

    protected ServiceProxyImpl<ScheduleSet> getProxy() {
        ServiceProxyImpl<ScheduleSet> proxy = new ServiceProxyImpl<ScheduleSet>(ScheduleSet.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}

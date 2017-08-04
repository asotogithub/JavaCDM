package trueffect.truconnect.api.tpasapi.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.commons.service.proxy.ServiceProxyImpl;
import trueffect.truconnect.api.resources.ResourceUtil;

import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author Rambert Rioja
 */
public class BaseProxy<T> extends ServiceProxyImpl<T> {

    protected Logger log;
    
    public BaseProxy(Class<T> type, HttpHeaders headers) {
        super(type, headers);
        log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public String getBaseUrl() {
        return ResourceUtil.get("public.url");
    }
}

package trueffect.truconnect.api.external.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.service.proxy.ServiceProxyImpl;
import trueffect.truconnect.api.resources.ResourceUtil;
import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author Rambert Rioja
 */
public class BaseDeliveryProxy<T> extends ServiceProxyImpl<T> {
    
    protected Logger log;

    public BaseDeliveryProxy(Class<T> type, HttpHeaders headers) {
        super(type, headers);
        log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public String getBaseUrl() {
        return ResourceUtil.get("public.delivery.url");
    }

    @Override
    protected void handleErrors(ClientResponse response) throws ProxyException {
        String message = response.getEntity(String.class);
        log.warn("================ Error Output ================");
        log.warn("Failed : HTTP error code : " + response.getStatus());
        log.warn("Failed : HTTP error message : " + message);
        log.warn("==============================================");
        throw new ProxyException(message, response);
    }
}

package trueffect.truconnect.api.tpasapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trueffect.truconnect.api.commons.service.BasicService;

/**
 *
 * @author Rambert Rioja
 */
public class GenericService extends BasicService {

    protected Logger log;
    
    public GenericService() {
        super();
        log = LoggerFactory.getLogger(this.getClass());
    }
}

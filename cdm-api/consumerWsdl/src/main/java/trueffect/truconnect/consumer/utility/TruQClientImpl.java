package trueffect.truconnect.consumer.utility;

import com.trueffect.tags.IMSMQService;
import com.trueffect.tags.MSMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rodrigo.alarcon
 */
public class TruQClientImpl implements TruQClient{

    protected Logger log;
    
    public TruQClientImpl() {
        log = LoggerFactory.getLogger(this.getClass());
    }
    
    @Override
    public Boolean pushHtmlTagToTruQ(Integer campaignId, Integer agencyId, String sessionKey) {
        Boolean result = false;
        try {
            MSMQService service = new MSMQService();
            IMSMQService port = service.getBasicHttpBindingIMSMQService();
            result = port.insertMessageToQueue(campaignId, agencyId, sessionKey);
        } catch (Exception e) {
            log.warn("TruQ service error: ", e);
            throw e;
        }
        return result;
    }
    
}
